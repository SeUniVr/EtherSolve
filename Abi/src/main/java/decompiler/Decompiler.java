package decompiler;

import SolidityInfo.SolidityVersion;
import opcodes.Opcode;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.Cfg;
import rebuiltabi.RebuiltAbi;
import rebuiltabi.RebuiltAbiFunction;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Decompiler {

    private final Map<String, String> convertVarNames = new HashMap<>();
    private final Map<String, String> typeInferences = new HashMap<>();
    private final List<String> eventDeclarations = new ArrayList<>();

    public void decompile(Cfg cfg, RebuiltAbi abi, SolidityVersion version, String contractName, String outputDir) throws IOException, InterruptedException {
        StringBuilder contractBuilder = buildContractHeader(version, contractName);
        List<RebuiltAbiFunction> functions = new ArrayList<>();
        for (RebuiltAbiFunction func : abi.getFunctions()) {
            if (!"no_name".equals(func.getResolvedSignature())) {
                functions.add(func);
            }
        }

        for (RebuiltAbiFunction func : functions) {
            processFunction(func, cfg, contractBuilder);
        }

        contractBuilder.append("}\n");

        insertStateDeclarations(contractBuilder);
        replaceAll(contractBuilder, convertVarNames);

        createSolidityFile(contractBuilder, outputDir);
    }

    private void processFunction(RebuiltAbiFunction func, Cfg cfg, StringBuilder contractBuilder) {
        if (func.getEntryPointOffset() <= 0) {
            return;
        }

        PathsExtractor pathsExtractor = new PathsExtractor();
        List<List<BasicBlock>> paths = pathsExtractor.findAllPaths(cfg, func.getEntryPointOffset());

        InstructionResolver instructionResolver = new InstructionResolver();
        List<BlockTracing> instructionResolved = instructionResolver.resolve(paths, typeInferences, eventDeclarations);
        List<BlockTracing> trimmedInstructions = trimInstructionResolved(instructionResolved);

        StringBuilder functionBody = aggregateInstructions(trimmedInstructions, cfg);

        if (isPublicVariableGetter(functionBody, func.getResolvedSignature())) {
            return;
        }

        String signature = buildFunctionSignature(func, functionBody);
        contractBuilder.append(signature)
                .append("{\n")
                .append(functionBody)
                .append("  }\n\n");
    }

    private StringBuilder buildContractHeader(SolidityVersion version, String contractName) {
        StringBuilder sb = new StringBuilder();
        sb.append("// Decompiled from bytecode - Solidity ^").append(version).append("\n");
        sb.append("pragma solidity ").append(version.getVersionString()).append(";\n\n");
        sb.append("contract ").append(contractName.split(" ")[0]).append(" {\n\n");
        return sb;
    }

    private void insertStateDeclarations(StringBuilder contractBuilder) {
        int firstFunctionIndex = contractBuilder.indexOf("function");
        if (firstFunctionIndex == -1) {
            return;
        }

        StringBuilder declarations = new StringBuilder();
        for (Map.Entry<String, String> entry : typeInferences.entrySet()) {
            if (isStateVariable(entry.getKey())) {
                declarations.append(entry.getValue()).append(" ").append(entry.getKey()).append(";\n");
            }
        }
        declarations.append("\n");

        for (String event : eventDeclarations) {
            declarations.append(event).append("\n");
        }
        declarations.append("\n");

        contractBuilder.insert(firstFunctionIndex, declarations);
    }

    private boolean isStateVariable(String expr) {
        expr = expr.trim();

        if (expr.matches("^\\d+$")) {
            return false;
        }

        if (expr.matches(".*[+\\-*/].*")) {
            return false;
        }

        if (expr.equals("msg.sender") || expr.equals("msg.value")) return false;
        if (expr.startsWith("_arg")) return false;
        if (expr.contains("[")) return false;
        if (expr.contains("\"")) return false;
        if (expr.contains("_localVar")) return false;
        if (expr.contains("this")) return false;

        return true;
    }

    private String buildFunctionSignature(RebuiltAbiFunction func, StringBuilder functionBody) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append("function ").append(func.getResolvedSignature()).append("(");

        Map<String, String> argsInferences = typeInferences.entrySet().stream()
                .filter(entry -> entry.getKey().matches("^_arg\\d+$"))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        List<String> processedArgs = new ArrayList<>();
        List<Map.Entry<String, String>> entries = new ArrayList<>(argsInferences.entrySet());
        for (int i = 0; i < entries.size(); i++) {
            Map.Entry<String, String> entry = entries.get(i);
            signatureBuilder.append(entry.getValue()).append(" ").append(entry.getKey());
            if (i < entries.size() - 1) {
                signatureBuilder.append(", ");
            }
            processedArgs.add(entry.getKey());
        }

        processedArgs.forEach(typeInferences::remove);
        signatureBuilder.append(") public ");

        if (functionBody.toString().contains("return")) {
            String returnLine = Arrays.stream(functionBody.toString().split("\n"))
                    .filter(line -> line.trim().startsWith("return"))
                    .findFirst().orElse("");

            if (!returnLine.isEmpty()) {
                String value = returnLine.split("return")[1].replace(";", "");

                String returnType = typeInferences.getOrDefault(value.trim(), "var");
                signatureBuilder.append("returns (").append(returnType).append(") ");
            }
        }
        return signatureBuilder.toString();
    }

    private List<BlockTracing> trimInstructionResolved(List<BlockTracing> instructionResolved) {
        return instructionResolved.stream()
                .filter(blockTracing -> !blockTracing.getCode().toString().isEmpty())
                .collect(Collectors.toList());
    }

    private StringBuilder aggregateInstructions(List<BlockTracing> blockTracings, Cfg cfg) {
        List<Long> elseOffsetVisited = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean currentTraceWasBranch = false;

        for (int i = 0; i < blockTracings.size(); i++) {
            BlockTracing blockTracing = blockTracings.get(i);
            BasicBlock block = cfg.getBasicBlock(blockTracing.getCurrentOffset());

            if (blockTracing.getCode().length() == 0) continue;

            sb.append("// BLOCK: ").append(block.getOpcodes().stream().map(Opcode::getOffset).map(String::valueOf).collect(Collectors.joining(", "))).append("\n");

            if (checkRequirePattern(blockTracings, i)) {
                sb.append(buildRequireStatement(blockTracings, i));
                i++;
            } else {
                if (!elseOffsetVisited.isEmpty() && elseOffsetVisited.get(elseOffsetVisited.size() - 1).equals(blockTracing.getCurrentOffset())) {
                    sb.append(block.getSuccessors().stream().distinct().count() == 1 ? " else {\n" : " else \n");
                    elseOffsetVisited.remove(elseOffsetVisited.size() - 1);
                    currentTraceWasBranch = true;
                }

                String currentCode = blockTracing.getCode().toString();
                if (currentCode.contains("if")) {
                    String[] snippets = currentCode.split("=>");
                    Pattern pattern = Pattern.compile("@Block:\\s*(\\d+)\\s*ELSE\\s*@Block:\\s*(\\d+)");
                    Matcher matcher = pattern.matcher(snippets[1]);
                    if (matcher.find() && !hasComplexRedirectPattern(block)) {
                        elseOffsetVisited.add(Long.parseLong(matcher.group(1)));
                    } else if (hasComplexRedirectPattern(block)) {
                        snippets[0] = replaceCompareSigns(snippets[0]);
                    }
                    sb.append(snippets[0]).append("{\n");
                    currentTraceWasBranch = true;
                } else if (currentCode.contains("return")) {
                    sb.append(handleReturnStatement(currentCode));
                    if (currentTraceWasBranch) {
                        currentTraceWasBranch = false;
                        sb.append("}\n");
                    }
                } else if (currentCode.contains("revert(")) {
                } else {
                    sb.append(handleRegularStatement(currentCode));
                    if (currentTraceWasBranch) {
                        currentTraceWasBranch = false;
                        sb.append("}\n");
                    }
                }
            }
        }
        return sb;
    }

    private String buildRequireStatement(List<BlockTracing> blockTracings, int i) {
        String[] snippets = blockTracings.get(i).getCode().toString().split("=>");
        String condition = snippets[0].substring(2).trim();
        String output;

        if (blockTracings.get(i + 1).getCode().toString().startsWith("revert(")) {
            output = "require" + condition;
            String msg = blockTracings.get(i + 1).getCode().toString();
            String strMsg = msg.substring(7, msg.length() - 3).trim();
            output = strMsg.isEmpty() ? output.substring(0, output.length() - 1) + ");\n"
                    : output.substring(0, output.length() - 1) + ", " + strMsg + ");\n";
        } else {
            output = "require" + condition + ";\n";
        }

        return output;
    }

    private String handleReturnStatement(String code) {
        String value = code.split(" ")[1];

        try {
            if (value.substring(2, 4).matches("[0-9a-fA-F]+")) {
                String ascii = hexToString(value);
                try {
                    BigInteger asciiInteger = new BigInteger(ascii, 16);
                    return "return " + asciiInteger + ";\n";
                } catch (NumberFormatException e) {
                    return "return \"" + ascii + "\";\n";
                }
            }
        } catch (StringIndexOutOfBoundsException e) {
            return code + "\n";
        }

        return code + "\n";
    }

    private String handleRegularStatement(String code) {
        if (code.startsWith("_localVar")) {
            String[] parts = code.split(" = ");
            String varName = parts[0];
            return typeInferences.getOrDefault(varName, "var") + " " + code;
        }
        return code;
    }

    private boolean hasComplexRedirectPattern(BasicBlock block) {
        List<BasicBlock> successors = block.getSuccessors();
        if (successors.stream().distinct().count() < 2) return false;

        for (BasicBlock target : successors) {
            for (BasicBlock start : successors) {
                if (!start.equals(target) && pathExists(start, target, new HashSet<>())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean pathExists(BasicBlock current, BasicBlock target, Set<BasicBlock> visited) {
        if (current.equals(target)) return true;
        if (visited.contains(current) || current.getOpcodes().stream().anyMatch(op -> op.toString().contains("REVERT"))) return false;

        visited.add(current);
        for (BasicBlock nextNode : current.getSuccessors()) {
            if (pathExists(nextNode, target, visited)) return true;
        }
        return false;
    }

    private boolean isPublicVariableGetter(StringBuilder functionBuilder, String functionName) {
        String[] blockLines = functionBuilder.toString().split("\n");
        if (Arrays.stream(blockLines).filter(s -> !s.startsWith("//")).count() != 2) {
            return false;
        }

        String line1 = Arrays.stream(blockLines).filter(s -> !s.startsWith("//")).collect(Collectors.toList()).get(0).trim();
        String line2 = Arrays.stream(blockLines).filter(s -> !s.startsWith("//")).collect(Collectors.toList()).get(1).trim().toLowerCase();

        if (line1.startsWith("revert") && line2.startsWith("return")) {
            String abstractVarName = line2.split(" ")[1].replace(";", "");
            convertVarNames.put(abstractVarName, functionName);
            return true;
        }
        return false;
    }

    private boolean checkRequirePattern(List<BlockTracing> blockTracings, int idx) {
        if (blockTracings.size() <= idx + 1) return false;

        String regex = "if \\((.+?)\\) => @Block: (\\d+) ELSE @Block: (\\d+)(.*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(blockTracings.get(idx).getCode().toString().trim().replace("\n", ""));

        return matcher.matches() && matcher.group(4).isEmpty() && blockTracings.get(idx + 1).getCode().toString().contains("revert(");
    }

    private String hexToString(String hex) {
        hex = hex.substring(0, hex.length() - 2).trim();
        String cleanHex = hex.startsWith("0x") ? hex.substring(2) : hex;
        StringBuilder sb = new StringBuilder();

        try {
            BigInteger hexInt = new BigInteger(cleanHex, 16);
            typeInferences.put(hexInt.toString(), "uint");
            return hexInt.toString();
        } catch (NumberFormatException e) {}

        for (int i = 0; i < cleanHex.length(); i += 2) {
            String byteHex = cleanHex.substring(i, i + 2);
            int byteValue = Integer.parseInt(byteHex, 16);
            if (byteValue == 0) break;
            sb.append((char) byteValue);
        }
        typeInferences.put("\"" + sb + "\"", "string");
        return sb.toString();
    }

    private void replaceAll(StringBuilder sb, Map<String, String> replacements) {
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String target = entry.getKey();
            String replacement = entry.getValue();
            int index = sb.indexOf(target);
            while (index != -1) {
                sb.replace(index, index + target.length(), replacement);
                index = sb.indexOf(target, index + replacement.length());
            }
        }
    }

    private void createSolidityFile(StringBuilder sb, String outputDir) throws IOException, InterruptedException {
        Path dirPath = Paths.get(outputDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String name = "Analysis_" + dtf.format(LocalDateTime.now());
        File solidityFile = dirPath.resolve(name + "_decompiled.sol").toFile();

        String formattedResult = formatSolidityCode(sb.toString());
        System.out.println("Solidity file formatted successfully!");

        try (Writer writer = new BufferedWriter(new FileWriter(solidityFile))) {
            writer.write(formattedResult);
        }
        System.out.println("Successfully wrote solidity file: " + solidityFile.getAbsolutePath());
    }

    private String formatSolidityCode(String rawCode) {
        StringBuilder formattedCode = new StringBuilder();
        int indentLevel = 0;
        final String indentString = "    ";
        String[] lines = rawCode.split("\\R");

        for (int i = 0; i < lines.length; i++) {
            String trimmedLine = lines[i].trim();
            if (trimmedLine.isEmpty()) {
                formattedCode.append("\n");
                continue;
            }

            if (trimmedLine.equals("else") && i + 1 < lines.length && lines[i + 1].trim().startsWith("if")) {
                trimmedLine += " " + lines[i + 1].trim();
                i++;
            }

            if (trimmedLine.startsWith("}")) {
                indentLevel = Math.max(0, indentLevel - 1);
            }

            for (int j = 0; j < indentLevel; j++) {
                formattedCode.append(indentString);
            }
            formattedCode.append(trimmedLine).append("\n");

            if (trimmedLine.endsWith("{")) {
                indentLevel++;
            }
        }
        return formattedCode.toString();
    }

    private String replaceCompareSigns(String code) {
        if (code.trim().equals(">"))
            return code.replaceAll(">", "<=");
        else if (code.trim().equals("<"))
            return code.replaceAll("<", ">=");
        else if (code.contains("=="))
            return code.replaceAll("==", "!=");
        else if (code.contains("!="))
            return code.replaceAll("!=", "==");
        else if (code.contains(">="))
            return code.replaceAll(">=", "<");
        else if (code.contains("<="))
            return code.replaceAll("<=", ">");

        return "";
    }

}