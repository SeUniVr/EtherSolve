package decompiler;

import SolidityInfo.SolidityVersion;
import opcodes.Opcode;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.Cfg;
import rebuiltabi.RebuiltAbi;
import rebuiltabi.RebuiltAbiFunction;
import rebuiltabi.fields.RebuiltIOElement;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Solidity decompiler which reconstructs Solidity code from
 * CFG abi functions.
 */
public class Decompiler {

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
    public void decompile (Cfg cfg, RebuiltAbi abi, SolidityVersion version, String contractName) throws IOException, InterruptedException {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("// Decompiled from bytecode - Solidity ^").append(version).append("\n");
        stringBuilder.append("pragma solidity ").append(version.getVersionString()).append(";\n\n");
        stringBuilder.append("contract ").append(contractName).append(" {\n\n");

        // Analyze all functions which name is not "no_name" (duplicates)
        for (RebuiltAbiFunction func : abi.getFunctions()) {
            if (!func.getResolvedSignature().equals("no_name")) {
                stringBuilder.append("    function ").append(func.getResolvedSignature()).append("(");
                // Add arguments
                List<RebuiltIOElement> arguments = func.getInputs();
                for (int i = 0; i < arguments.size(); i++) {
                    RebuiltIOElement argument = arguments.get(i);
                    stringBuilder.append(argument.getType()).append(" arg").append(argument.getIndex());
                    if (i < arguments.size() - 1)
                        stringBuilder.append(", ");
                }
                stringBuilder.append(") public {\n");
                // Get all possible paths of execution (Branch simulation)
                long functionEntryOffset = func.getEntryPointOffset();
                if (functionEntryOffset > 0) {
                    PathsExtractor pathsExtractor = new PathsExtractor();
                    List<List<BasicBlock>> paths = pathsExtractor.findAllPaths(cfg, functionEntryOffset);
                    InstructionResolver instructionResolver = new InstructionResolver();
                    List<BlockTracing> instructionResolved = instructionResolver.resolve(paths);
                    // Rewriting code extracted following Solidity Logical Order
                    stringBuilder.append(aggregateInstructions(instructionResolved, cfg));
                }
                stringBuilder.append("  }\n\n");
            }
        }
        stringBuilder.append("}");

        // Create the solidity File
        createSolidityFile(stringBuilder, contractName);

    }

    private StringBuilder aggregateInstructions(List<BlockTracing> blockTracings, Cfg cfg) {
        List<Long> elseOffsetVisited = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean currentTraceWasBranch = false;

        for (BlockTracing blockTracing : blockTracings) {
            BasicBlock block = cfg.getBasicBlock(blockTracing.getCurrentOffset());
            if (blockTracing.getCode().length() != 0) {
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
                    if (currentTraceWasBranch) {
                        currentTraceWasBranch = false;
                        sb.append("}\n");
                    }
                }

            }
        }
        return sb;
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
        return sb.toString();
    }

    private void createSolidityFile(StringBuilder sb, String name) throws IOException, InterruptedException {
        // Create a new .sol file
        File solidityFile = new File(name + "_decompiled.sol");

        // Write content
        try (FileWriter writer = new FileWriter(solidityFile)) {
            writer.write(sb.toString());
        } catch (IOException e) {
            System.err.println("Error writing solidity file: " + e.getMessage());
        }

        // Print the file path
        System.out.println("Successfully wrote solidity file: " + solidityFile.getAbsolutePath());

        // PRETTY PRINT: use prettier to format the code written
        ProcessBuilder processBuilder = new ProcessBuilder(
                "npx",
                "prettier",
                "--plugin=prettier-plugin-solidity",
                "--write",
                solidityFile.getAbsolutePath()
        );

        processBuilder.directory(new File(System.getProperty("user.dir")));
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        if (exitCode == 0)
            System.out.println("Solidity file formatted!");
        else
            System.out.println("Solidity file format failed, exit code: " + exitCode);
    }
}
