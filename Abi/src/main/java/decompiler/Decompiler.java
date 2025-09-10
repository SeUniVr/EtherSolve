package decompiler;

import SolidityInfo.SolidityVersion;
import opcodes.Opcode;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.Cfg;
import rebuiltabi.RebuiltAbi;
import rebuiltabi.RebuiltAbiFunction;
import rebuiltabi.fields.RebuiltIOElement;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Solidity decompiler which reconstructs Solidity code from
 * CFG abi functions.
 */
public class Decompiler {

    private final Map<String, String> convertVarNames = new HashMap<>();
    private final Map<String, String> typeInferences = new HashMap<>();

    /**
     * Core function of Decompiler.
     * It takes cfg and abi to analyze the functions, their signatures and their body.
     * - Step 1: generate all possible paths in order to visit all possible executions.
     * - Step 2: simulate each path with the memory structure, in order to obtain a
     *           middle-level code decompilation.
     * - Step 3: construct code logic and create a .sol file where it will be written
     *           the output.
     * @param cfg -> Control Flow graph of program.
     * @param abi -> Abi of program.
     * @param version -> Version of Solidity used for compilation.
     * @param contractName -> Name of contract
     * @throws IOException
     * @throws InterruptedException
     */
    public void decompile (Cfg cfg, RebuiltAbi abi, SolidityVersion version, String contractName, String outputDir) throws IOException, InterruptedException {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("// Decompiled from bytecode - Solidity ^").append(version).append("\n");
        stringBuilder.append("pragma solidity ").append(version.getVersionString()).append(";\n\n");
        stringBuilder.append("contract ").append(contractName.split(" ")[0]).append(" {\n\n");

        // Analyze all functions which name is not "no_name" (duplicates)
        List<RebuiltAbiFunction> functions = new ArrayList<>();
        abi.getFunctions().forEach(functions::add);

        for (RebuiltAbiFunction func : functions) {
            if (!func.getResolvedSignature().equals("no_name")) {
                // Get all possible paths of execution (Branch simulation)
                long functionEntryOffset = func.getEntryPointOffset();
                StringBuilder functionBuilder = new StringBuilder();
                if (functionEntryOffset > 0) {
                    PathsExtractor pathsExtractor = new PathsExtractor();
                    List<List<BasicBlock>> paths = pathsExtractor.findAllPaths(cfg, functionEntryOffset);
                    InstructionResolver instructionResolver = new InstructionResolver();
                    List<BlockTracing> instructionResolved = instructionResolver.resolve(paths, typeInferences);
                    // Rewriting code extracted following Solidity Logical Order
                    functionBuilder = guessIfFunctionIsVar(aggregateInstructions(instructionResolved, cfg), func.getResolvedSignature());
                }

                if (functions.get(functions.size() - 1) == func) {
                    // Global variable definition;
                    int funcIndex = stringBuilder.indexOf("function");
                    if (funcIndex != -1) {
                        String variablesResolution = "";
                        for (Map.Entry<String, String> entry : typeInferences.entrySet()) {
                            if (!entry.getKey().equals("msg.sender") && !entry.getKey().equals("msg.value") &&
                                    !entry.getKey().startsWith("_arg") && !entry.getKey().contains("[") &&
                                    !entry.getKey().contains("\"") && !entry.getKey().contains("_localVar")) {
                                variablesResolution += typeInferences.get(entry.getKey()) + " " + entry.getKey() + ";\n";
                            }
                        }
                        variablesResolution += "\n";
                        stringBuilder.insert(funcIndex, variablesResolution);
                    }
                }

                if (!functionBuilder.toString().startsWith("var")) {
                    stringBuilder.append("function ").append(func.getResolvedSignature()).append("(");
                    // Add arguments
                    List<String> idx = new ArrayList<>();
                    Map<String, String> argsInferences = typeInferences.entrySet().stream().filter(entry -> entry.getKey().startsWith("_arg")).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    List<Map.Entry<String, String>> entries = new ArrayList<>(argsInferences.entrySet());
                    for (int a = 0; a < entries.size(); a++) {
                        Map.Entry<String, String> entry = entries.get(a);
                        idx.add(entry.getKey());
                        stringBuilder.append(entry.getValue()).append(" ").append(entry.getKey());
                        if (a < entries.size() - 1) stringBuilder.append(", ");
                    }
                    for (String id : idx) {
                        typeInferences.remove(id);
                    }
                    stringBuilder.append(") public ");
                    if (functionBuilder.toString().contains("return")) {
                        int index = functionBuilder.indexOf("return");
                        int lineNumber = functionBuilder.substring(0, index).split("\n").length;
                        String line = functionBuilder.toString().split("\n")[lineNumber];
                        String value;
                        if (line.contains("\""))
                            value = "\"" + line.split("\"")[1] + "\"";
                        else
                            value = line.split(" ")[1].substring(0, line.split(" ")[1].length() - 1);
                        stringBuilder.append("returns (").append(typeInferences.get(value)).append(") ");
                    }
                    stringBuilder.append("{\n");
                    stringBuilder.append(functionBuilder);

                    stringBuilder.append("  }\n\n");
                }
            }
        }
        stringBuilder.append("}");

        // Convert all variables name
        for (Map.Entry<String, String> entry : convertVarNames.entrySet()) {
            replaceAll(stringBuilder, entry.getKey(), entry.getValue());
        }

        // Create the solidity File
        createSolidityFile(stringBuilder, outputDir);

        // Just for debugging
        //System.out.println(stringBuilder);
    }

    private StringBuilder aggregateInstructions(List<BlockTracing> blockTracings, Cfg cfg) {
        List<Long> elseOffsetVisited = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean currentTraceWasBranch = false;

        for (int i = 0; i < blockTracings.size(); i++) {
            BlockTracing blockTracing = blockTracings.get(i);
            BasicBlock block = cfg.getBasicBlock(blockTracing.getCurrentOffset());
            if (blockTracing.getCode().length() != 0) {
                sb.append("// BLOCK: ");
                for (Opcode op : block.getOpcodes()) {
                    if (op.equals(block.getOpcodes().get(block.getOpcodes().size() - 1)))
                        sb.append(op.getOffset()).append("\n");
                    else
                        sb.append(op.getOffset()).append(", ");
                }
                // Manage require pattern
                if (checkRequirePattern(blockTracings, i)) {
                    String[] snippets = blockTracing.getCode().toString().split("=>");
                    String output = "require" + snippets[0].substring(2).trim() + ";\n";
                    output = output.replaceAll(">=", "<");
                    output = output.replaceAll("<=", ">");
                    sb.append(output);
                    i += 1;
                }

                else {
                    // Check if we are visiting an else branch child
                    if (!elseOffsetVisited.isEmpty()) {
                        if (elseOffsetVisited.get(elseOffsetVisited.size() - 1).equals(blockTracing.getCurrentOffset())) {
                            if (block.getSuccessors().stream().distinct().count() == 1)
                                sb.append(" else {").append("\n");
                            else
                                sb.append(" else ").append("\n");

                            elseOffsetVisited.remove(elseOffsetVisited.size() - 1);
                            currentTraceWasBranch = true;
                        }
                    }
                    // If child
                    if (blockTracing.getCode().toString().contains("if")) {
                        String[] snippets = blockTracing.getCode().toString().split("=>");
                        Pattern pattern = Pattern.compile("@Block:\\s*(\\d+)\\s*ELSE\\s*@Block:\\s*(\\d+)");
                        Matcher matcher = pattern.matcher(snippets[1]);

                        if (matcher.find()) {
                            boolean toAdd = true;
                            // Check if successors of successor contains the same child as father
                            for (BasicBlock successor : block.getSuccessors()) {
                                for (BasicBlock successorOfSuccessor : successor.getSuccessors()) {
                                    if (block.getSuccessors().stream().distinct().count() == 2 && block.getSuccessors().contains(successorOfSuccessor)) {
                                        toAdd = false;
                                    }
                                }
                                // Ignore branch condition with REVERT
                                for (Opcode opcode : successor.getOpcodes()) {
                                    if (opcode.toString().contains("REVERT"))
                                        toAdd = false;
                                }
                            }
                            if (toAdd)
                                elseOffsetVisited.add(Long.parseLong(matcher.group(1)));
                        }
                        sb.append(snippets[0]).append("{").append("\n");
                        currentTraceWasBranch = true;
                    }
                    else if (blockTracing.getCode().toString().contains("return")) {
                        // Check if returned value is a String, then convert it
                        if (blockTracing.getCode().toString().split(" ")[1].substring(2, 4).matches("[0-9a-fA-F]+")) {
                            String ascii = hexToString(blockTracing.getCode().toString().split(" ")[1]);
                            sb.append("return \"").append(ascii).append("\"").append(";\n");
                        } else {
                            sb.append(blockTracing.getCode()).append("\n");
                        }

                        if (currentTraceWasBranch) {
                            currentTraceWasBranch = false;
                            sb.append("}\n");
                        }
                    }
                    else {
                        sb.append(blockTracing.getCode());

                        if (blockTracing.getCode().toString().startsWith("_localVar")) {
                            int idx =  sb.indexOf("_localVar");
                            int lineNumber = sb.substring(0, idx).split("\n").length;
                            String line = sb.toString().split("\n")[lineNumber];
                            String nameLocalVar = line.split(" = ")[0];
                            sb.replace(idx, idx + 10, typeInferences.get(nameLocalVar) + " " + nameLocalVar);
                        }

                        if (currentTraceWasBranch) {
                            currentTraceWasBranch = false;
                            sb.append("}\n");
                        }
                    }
                }
            }
        }
        return sb;
    }

    private StringBuilder guessIfFunctionIsVar(StringBuilder functionBuilder, String functionName) {
        String[] blockLines = functionBuilder.toString().split("\n");
        if (blockLines.length != 4)
            return functionBuilder;

        String line1 = blockLines[1].trim();
        String line2 = blockLines[3].trim().toLowerCase();

        if (line1.startsWith("revert") && line2.startsWith("return")) {
            String abstractVarName = line2.split(" ")[1].substring(0, line2.split(" ")[1].length() - 1);
            convertVarNames.put(abstractVarName, abstractVarName);
            return new StringBuilder("var public ").append(functionName).append(";\n\n");
        } else {
            return functionBuilder;
        }

    }

    private boolean checkRequirePattern(List<BlockTracing> blockTracings, int idx) {
        if (blockTracings.size() <= idx) return false;
        else return blockTracings.get(idx).getCode().toString().contains("if") && blockTracings.get(idx + 1).getCode().toString().contains("revert();");
    }

    private String hexToString(String hex) {
        if (hex.startsWith("0x") || hex.startsWith("0X")) {
            hex = hex.substring(2);
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String byteHex = hex.substring(i, i + 2);
            int byteValue = Integer.parseInt(byteHex, 16);
            if (byteValue == 0) break; // stop al primo zero (padding)
            sb.append((char) byteValue);
        }

        typeInferences.put("\"" + sb + "\"", "string");
        return sb.toString();
    }

    public static void replaceAll(StringBuilder sb, String target, String replacement) {
        int index = sb.indexOf(target);
        while (index != -1) {
            sb.replace(index, index + target.length(), replacement);
            index = sb.indexOf(target, index + replacement.length());
        }
    }

    private void createSolidityFile(StringBuilder sb, String outputDir) throws IOException, InterruptedException {
        // Path exists
        Path dirPath = Paths.get(outputDir);
        if (!Files.exists(dirPath)) {
            Files.createDirectories(dirPath);
        }

        // Create a new .sol file
        DateTimeFormatter datetime_format = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String name = "Analysis_" + datetime_format.format(LocalDateTime.now());
        File solidityFile = dirPath.resolve(name + "_decompiled.sol").toFile();

        String formattedResult = formatSolidityCode(sb.toString());
        System.out.println("Solidity file formatted successfully!");

        // Write content
        try (Writer writer = new BufferedWriter(new FileWriter(solidityFile))) {
            writer.write(formattedResult);
        }
        System.out.println("Successfully wrote solidity file: " + solidityFile.getAbsolutePath());

    }

    /**
     * Formats a block of Solidity code with improved logic to handle 'else if' statements.
     * @param rawCode The unformatted source code.
     * @return The correctly indented source code.
     */
    public String formatSolidityCode(String rawCode) {
        StringBuilder formattedCode = new StringBuilder();
        int indentLevel = 0;
        final String indentString = "    "; // 4 spaces per indent level

        String[] lines = rawCode.split("\\R");

        // We use an index-based loop to allow "peeking" at the next line.
        for (int i = 0; i < lines.length; i++) {
            String trimmedLine = lines[i].trim();

            // handling "else if" on separate lines
            if (trimmedLine.equals("else") && i + 1 < lines.length) {
                String nextTrimmedLine = lines[i + 1].trim();
                if (nextTrimmedLine.startsWith("if")) {
                    // Merge "else" and "if" lines into one
                    trimmedLine += " " + nextTrimmedLine;
                    i++; // skip the next line since we've already processed it.
                }
            }

            if (trimmedLine.isEmpty()) {
                formattedCode.append("\n");
                continue;
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
}
