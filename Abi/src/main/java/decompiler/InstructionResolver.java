package decompiler;

import abi.HashDB;
import abi.fields.FunctionType;
import opcodes.Opcode;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.BasicBlockType;

import java.math.BigInteger;
import java.util.*;
import java.util.List;

public class InstructionResolver {
    private EVMemoryStructure memory;                        // EVM Memory Structure simulator
    private Map<String, String> storage = new HashMap<>();   // EVM Storage Structure simulator
    private ArrayList<String> stack = new ArrayList<>();     // EVM Stack Structure simulator


    Map<Long, Integer> visitBlocksCounter;
    boolean checkingLocalVariableInStack;
    private Map<String, List<String>> arrayLocations = new HashMap<>();

    Map<String, String> typeInferences = new HashMap<>();
    List<Integer> functionArguments;
    boolean needToManageIf = false;

    private static final List<BasicBlockType> READABLE_BLOCKS = Arrays.asList(BasicBlockType.CODE, BasicBlockType.DISPATCHER, BasicBlockType.FALLBACK);
    private static final List<BasicBlockType> ANALYZABLE_BLOCKS = Arrays.asList(BasicBlockType.CODE, BasicBlockType.FALLBACK);
    private static final List<String> ABSTRACT_VALUES = Arrays.asList("storage", "msg", "arg", "localVar");
    private static final List<String> COMPARE_SIGNS = Arrays.asList(">", "<", "==", "!=", ">=", "<=");
    private static final String ADDRESS_MASK = "0xffffffffffffffffffffffffffffffffffffffff";

    public InstructionResolver() {
        visitBlocksCounter = new HashMap<>();
    }

    /**
     * Resolve all possible paths and gets instructions from opcodes found.
     * @param paths
     * @return List of Block Trace
     */
    public List<BlockTracing> resolve(List<List<BasicBlock>> paths, Map<String, String> inferences) {
        List<BlockTracing> relations = new ArrayList<>();
        typeInferences.putAll(inferences);
        for (List<BasicBlock> path : paths) {
            functionArguments = new ArrayList<>();
            resolvePath(path, relations);
        }

        for (Map.Entry<String, String> entry : typeInferences.entrySet()) {
            if (inferences.containsKey(entry.getKey())) {
                if (inferences.get(entry.getKey()).startsWith("mapping"))
                    if (inferences.get(entry.getKey()).split(" => ")[1].contains("z")) {
                        inferences.put(entry.getKey(), entry.getValue());
                    }

            } else
                inferences.put(entry.getKey(), entry.getValue());
        }

        return relations;
    }

    private void resolvePath(List<BasicBlock> path, List<BlockTracing> relations) {
        memory = new EVMemoryStructure();
        storage.clear();

        for (BasicBlock block : path) {
            checkingLocalVariableInStack = false;
            // Read only
            if (READABLE_BLOCKS.contains(block.getType())) {
                BlockTracing blockTracing = new BlockTracing(block.getOffset(), null, new StringBuilder());
                // Analyze instructions
                if (ANALYZABLE_BLOCKS.contains(block.getType())) {
                    visitBlocksCounter.put(block.getOffset(), visitBlocksCounter.getOrDefault(block.getOffset(), 0) + 1);
                }
                // Get opcode instructions
                List<Opcode> opcodes = block.getOpcodes();
                // If block has only one successor, add it to Block trace
                if (block.getSuccessors().stream().distinct().count() == 1)
                    blockTracing.setSuccessorOffset(block.getSuccessors().get(0).getOffset());

                for (int i = 0; i < opcodes.size(); i++) {
                    Opcode opcode = opcodes.get(i);
                    resolveOpcode(opcode, opcodes, i, block, blockTracing);
                }
                relations.add(blockTracing);
            }
        }
    }

    private void resolveOpcode(Opcode opcode, List<Opcode> opcodeList, int opcodeIndex, BasicBlock block, BlockTracing blockTracing) {

        if (containsInstruction(opcode, "PUSH")) {
            stack.add(getPUSHArg(opcode));
        }
        if (containsInstruction(opcode, "POP")) {
            if (!stack.isEmpty())
                stack.remove(stack.size() - 1);
            else
                System.err.println("Warning: skipped POP opcode because stack was empty.");
        }
        if (containsInstruction(opcode, "DUP")) {
            int idx = getInstructionIndex(opcode, 3);
            stack.add(stack.get(stack.size() - idx));
        }
        if (containsInstruction(opcode, "SWAP")) {
            int idx = getInstructionIndex(opcode, 4);
            int lastIndex = stack.size() - 1;
            int targetIndex = lastIndex - idx;
            Collections.swap(stack, lastIndex, targetIndex);

        }
        if (containsInstruction(opcode, "SLOAD")) {
            String value = stack.remove(stack.size() - 1);
            String rootArray = isArrayElement(value);

            if (rootArray == null) {
                // No array, mapping...
                if (!value.contains("[") && doesNotContainAbstractValue(value)) {
                    if (storage.containsKey(value))
                        stack.add(storage.get(value));
                    else
                        stack.add("_storage" + convertToInt(value));
                } else {
                    if (storage.containsKey(value))
                        stack.add(storage.get(value));
                    else
                        stack.add("_storage" + value);
                }
            } else {
                if  (storage.containsKey("_storage" + convertToInt(rootArray) + "[" +  convertToInt(value) + "]"))
                     stack.add(storage.get("_storage" + convertToInt(rootArray) + "[" +  convertToInt(value) + "]"));
                else
                    stack.add("_storage" + convertToInt(rootArray) + "[" +  convertToInt(value) + "]");
            }
        }
        if (containsInstruction(opcode, "SSTORE")) {
            String offset = stack.remove(stack.size() - 1);
            String value = stack.remove(stack.size() - 1);

            storage.put(offset, value);


            if (!doesNotContainAbstractValue(offset)) {
                // Looking for old occurrences of offset
                for (int a = 0; a < stack.size(); a++) {
                    String element =  stack.get(a);
                    if (element.equals("_storage" + offset)) {
                        stack.set(a, "_localVar" + a);
                        blockTracing.appendCode("_localVar" + a + " = " + "_storage" + offset + ";\n");
                    }

                }

                if (!doesNotContainAbstractValue(value)) {
                    if (block.getType() == BasicBlockType.CODE && visitBlocksCounter.get(block.getOffset()) == 1) {
                        String rootArray = isArrayElement(offset);
                        if (rootArray == null)
                            blockTracing.appendCode("_storage" + offset + " = " + value + ";\n");
                        else
                            blockTracing.appendCode("_storage" + convertToInt(rootArray) + "[" + offset + "] = " + value + ";\n");
                    }
                } else {
                    if (block.getType() == BasicBlockType.CODE && visitBlocksCounter.get(block.getOffset()) == 1) {
                        String rootArray = isArrayElement(offset);
                        if (rootArray == null)
                            blockTracing.appendCode("_storage" + offset + " = " + convertToInt(value) + ";\n");
                        else
                            blockTracing.appendCode("_storage" + convertToInt(rootArray) + "[" + offset + "] = " + convertToInt(value) + ";\n");
                    }
                }
            } else {
                if (!doesNotContainAbstractValue(value)) {
                    if (block.getType() == BasicBlockType.CODE && visitBlocksCounter.get(block.getOffset()) == 1) {
                        String rootArray = isArrayElement(offset);
                        if (rootArray == null)
                            blockTracing.appendCode("_storage" + convertToInt(offset) + " = " + value + ";\n");
                        else
                            blockTracing.appendCode("_storage" + convertToInt(rootArray) + "[" + convertToInt(offset) + "] = " + value + ";\n");
                    }
                } else {
                    String rootArray = isArrayElement(offset);
                    if (rootArray == null)
                        blockTracing.appendCode("_storage" + convertToInt(offset) + " = " + convertToInt(value) + ";\n");
                    else
                        blockTracing.appendCode("_storage" + convertToInt(rootArray) + "[" + convertToInt(offset) + "] = " + convertToInt(value) + ";\n");
                }
            }
            storage.put(offset, value);
        }
        if (containsInstruction(opcode, "MLOAD")) {
            String address = stack.remove(stack.size() - 1);
            if (doesNotContainAbstractValue(address)) {
                BigInteger startAddress = convertToInt(address);
                Optional<String> valueInMemory = memory.loadValueFromMemory(startAddress);

                if (valueInMemory.isPresent()) {
                    stack.add(valueInMemory.get());
                } else {
                    memory.addMemoryStructure(startAddress, startAddress.add(BigInteger.valueOf(31)), "0x00");
                    stack.add("0x00");
                }
            } else {
                stack.add("mem(" + address + ")");
            }

        }
        if (containsInstruction(opcode, "MSTORE")) {
            String address = stack.remove(stack.size() - 1);
            String valueToMemorize =  stack.remove(stack.size() - 1);
            BigInteger startAddress;
            BigInteger endAddress;
            if (doesNotContainAbstractValue(address)) {
                startAddress = convertToInt(address);
                endAddress = startAddress.add(BigInteger.valueOf(31));
                memory.addMemoryStructure(startAddress, endAddress, valueToMemorize);
            }
        }
        if (containsInstruction(opcode, "JUMPI")) {
            stack.remove(stack.size() - 1);
            stack.remove(stack.size() - 1);
        }
        if (isInstruction(opcode, "JUMP")) {
            if (!stack.isEmpty()) {
                stack.remove(stack.size() - 1);
            }
        }
        if (containsInstruction(opcode, "REVERT")) {
            stack.remove(stack.size() - 1);
            stack.remove(stack.size() - 1);
            blockTracing.appendCode("revert();\n");
        }
        if (!isOperationInstruction(opcode).equals("??")) {
            String sign = isOperationInstruction(opcode);
            String firstOperand =  stack.remove(stack.size() - 1);
            String secondOperand =  stack.remove(stack.size() - 1);

            String neutralOperand = isNeutralOperation(firstOperand, secondOperand, sign);
            if (neutralOperand != null) {
                stack.add(neutralOperand);
            }
            else if (!doesNotContainAbstractValue(firstOperand)) {
                if (!doesNotContainAbstractValue(secondOperand)) {
                    if (!typeInferences.containsKey(firstOperand)) {
                        if (!firstOperand.contains("[")) {
                            typeInferences.put(firstOperand, "uint");
                        } else if (typeInferences.containsKey(firstOperand.split("\\[")[0])) {
                            if (typeInferences.get(firstOperand.split("\\[")[0]).startsWith("mapping")) {
                                typeInferences.put(firstOperand.split("\\[")[0], typeInferences.get(firstOperand.split("\\[")[0]).split(" => ")[0] + " => uint)");
                            }
                        }
                    } else if (firstOperand.contains("[")) {
                        String typeArray = typeInferences.get(firstOperand.split("\\[")[0]);
                        typeArray = typeArray.replaceAll("var", "uint");
                        typeInferences.put(firstOperand.split("\\[")[0], typeArray);
                        updateArrayTypeElements(firstOperand.split("\\[")[0], "uint");
                    }

                    if (!typeInferences.containsKey(secondOperand)) {
                        if (!firstOperand.contains("[")) {
                            typeInferences.put(secondOperand, "uint");
                        } else if (typeInferences.containsKey(secondOperand.split("\\[")[0])) {
                            if (typeInferences.get(secondOperand.split("\\[")[0]).startsWith("mapping")) {
                                typeInferences.put(secondOperand.split("\\[")[0], typeInferences.get(secondOperand.split("\\[")[0]).split(" => ")[0] + " => uint)");
                            }
                        }
                    } else if (secondOperand.contains("[")) {
                        String typeArray = typeInferences.get(secondOperand.split("\\[")[0]);
                        typeArray = typeArray.replaceAll("var", "uint");
                        typeInferences.put(secondOperand.split("\\[")[0], typeArray);
                        updateArrayTypeElements(firstOperand.split("\\[")[0], "uint");
                    }

                    stack.add(firstOperand + " " + sign + " " + secondOperand);
                } else {
                    if (!typeInferences.containsKey(firstOperand)) {
                        if (!firstOperand.contains("[")) {
                            typeInferences.put(firstOperand, "uint");
                        } else if (typeInferences.containsKey(firstOperand.split("\\[")[0])) {
                            if (typeInferences.get(firstOperand.split("\\[")[0]).startsWith("mapping")) {
                                typeInferences.put(firstOperand.split("\\[")[0], typeInferences.get(firstOperand.split("\\[")[0]).split(" => ")[0] + " => uint)");
                            }

                        }
                    } else if (firstOperand.contains("[")) {
                        String typeArray = typeInferences.get(firstOperand.split("\\[")[0]);
                        typeArray = typeArray.replaceAll("var", "uint");
                        typeInferences.put(firstOperand.split("\\[")[0], typeArray);
                        updateArrayTypeElements(firstOperand.split("\\[")[0], "uint");
                    }
                    stack.add(firstOperand + " " + sign + " " + convertToInt(secondOperand));
                }
            } else if (!doesNotContainAbstractValue(secondOperand)) {
                if (!typeInferences.containsKey(secondOperand)) {
                    if (!secondOperand.contains("[")) {
                        typeInferences.put(secondOperand, "uint");
                    } else if (typeInferences.containsKey(secondOperand.split("\\[")[0])) {
                        if (typeInferences.get(secondOperand.split("\\[")[0]).startsWith("mapping")) {
                            typeInferences.put(secondOperand.split("\\[")[0], typeInferences.get(secondOperand.split("\\[")[0]).split(" => ")[0] + " => uint)");
                        }
                    }
                }  else if (secondOperand.contains("[")) {
                    String typeArray = typeInferences.get(secondOperand.split("\\[")[0]);
                    typeArray = typeArray.replaceAll("var", "uint");
                    typeInferences.put(secondOperand.split("\\[")[0], typeArray);
                    updateArrayTypeElements(firstOperand.split("\\[")[0], "uint");
                }

                stack.add(convertToInt(firstOperand) + " " + sign + " " + secondOperand);
            } else {
                BigInteger firstOperandInt = convertToInt(firstOperand);
                BigInteger secondOperandInt = convertToInt(secondOperand);
                BigInteger unsignedRes = calculate(sign, firstOperandInt, secondOperandInt);
                stack.add("0x" + unsignedRes.toString(16));
            }
        }
        if (!isCompareInstruction(opcode).equals("??")) {
            String sign = isCompareInstruction(opcode);
            String firstOperand = stack.remove(stack.size() - 1);
            String secondOperand = stack.remove(stack.size() - 1);

            // ISZERO, ISZERO => revert sign
            if (containsInstruction(opcodeList.get(opcodeIndex+1), "ISZERO") && containsInstruction(opcodeList.get(opcodeIndex+2), "ISZERO")) {
                if (sign.equals(">")) sign = "<=";
                if (sign.equals("<")) sign = ">=";
                if (sign.equals("==")) sign = "!=";
            }

            // Heuristic for arrays: check index < length
            if (opcode.toString().contains("LT")) {
                // if index > length, there is an invalid block
                for (BasicBlock successor : block.getSuccessors()) {
                    for (Opcode op : successor.getOpcodes()) {
                        if (op.toString().split(" ")[1].equals("INVALID")) {
                            String rootArray = stack.get(stack.size() - 2);
                            List<String> indexes = new ArrayList<>();
                            if (doesNotContainAbstractValue(secondOperand)) {
                                for (int i = convertToInt(rootArray).intValue() ; i < convertToInt(secondOperand).intValue(); i++) {
                                    indexes.add("0x" + i);
                                    if (!typeInferences.containsKey("_storage" + convertToInt(rootArray) + "[" + convertToInt("0x" + i) + "]"))
                                        typeInferences.put("_storage" + convertToInt(rootArray) + "[" + convertToInt("0x" + i) + "]", "var");
                                }
                                arrayLocations.put(rootArray, indexes);
                                if (!typeInferences.containsKey("_storage" + convertToInt(rootArray)))
                                    typeInferences.put("_storage" + convertToInt(rootArray), "var[" + convertToInt(secondOperand).intValue() + "]");
                                break;
                            }

                        }
                    }
                }
            }

            String resultOfCompare;
            if (!doesNotContainAbstractValue(firstOperand)) {
                if (!doesNotContainAbstractValue(secondOperand)) {
                    String typeInference = getTypeFromOperation(opcode);
                    typeInferences.put(firstOperand, typeInference);
                    typeInferences.put(secondOperand, typeInference);
                    resultOfCompare = firstOperand + " " + sign + " " + secondOperand;
                } else {
                    String typeInference = getTypeFromOperation(opcode);
                    typeInferences.put(firstOperand, typeInference);
                    resultOfCompare = firstOperand + " " + sign + " " + convertToInt(secondOperand);
                }
            } else if (!doesNotContainAbstractValue(secondOperand)) {
                String typeInference = getTypeFromOperation(opcode);
                typeInferences.put(secondOperand, typeInference);
                resultOfCompare = convertToInt(firstOperand) + " " + sign + " " + secondOperand;
            } else {
                BigInteger firstOperandInt = convertToInt(firstOperand);
                BigInteger secondOperandInt = convertToInt(secondOperand);
                resultOfCompare = firstOperandInt.compareTo(secondOperandInt) > 0 ? "0x01" : "0x00";
            }
            stack.add(resultOfCompare);


            // After this op there is PUSH + JUMP and not ISZERO: map this code fragment
            if (opcodeList.get(opcodeIndex+1).toString().contains("PUSH") && opcodeList.get(opcodeIndex+2).toString().contains("JUMPI")) {
                needToManageIf = true;
            }
        }
        if (containsInstruction(opcode, "EXP")) {
            String baseStr = stack.remove(stack.size() - 1);
            String powStr =  stack.remove(stack.size() - 1);
            if (doesNotContainAbstractValue(baseStr) && doesNotContainAbstractValue(powStr)) {
                BigInteger base = convertToInt(baseStr);
                BigInteger pow = convertToInt(powStr);
                BigInteger mod = BigInteger.valueOf(1).shiftLeft(256);
                stack.add("0x" + base.modPow(pow, mod).toString(16));
            } else {
                stack.add(baseStr + "^" + powStr);
            }
        }
        if (containsInstruction(opcode, "NOT")) {
            String valueStr =  stack.remove(stack.size() - 1);
            if (doesNotContainAbstractValue(valueStr)) {
                BigInteger value = convertToInt(valueStr);
                BigInteger mask = BigInteger.valueOf(2).pow(256).subtract(BigInteger.valueOf(1));
                stack.add("0x" + value.not().and(mask).toString(16));
            } else {
                stack.add("!" + valueStr);
                stack.add("!" + valueStr);
            }
        }
        if (containsInstruction(opcode, "ISZERO") || needToManageIf) {
            if (needToManageIf)
                needToManageIf = false;

            // Check the only or the second opcode ISZERO
            if (!containsInstruction(opcodeList.get(opcodeIndex+1), "ISZERO")) {
                String value = stack.get(stack.size() - 1);
                if (doesNotContainAbstractValue(value)) {
                    BigInteger valueInt = convertToInt(stack.remove(stack.size() - 1));
                    if (valueInt.equals(BigInteger.valueOf(0)))
                        stack.add("0x00");
                    else
                        stack.add("0x01");
                } else {
                    if (ANALYZABLE_BLOCKS.contains(block.getType()) && visitBlocksCounter.get(block.getOffset()) == 1) {
                        if (block.getSuccessors().stream().distinct().count() == 2) {
                            blockTracing.appendCode("if (" + value + ")");
                            // type inference
                            if (doesNotContainCompareSigns(value))
                                typeInferences.put(value, "bool");

                            if (!containsInstruction(opcodeList.get(opcodeIndex-1), "ISZERO")) {
                                blockTracing.appendCode(" => @Block: " + convertToInt(getPUSHArg(opcodeList.get(opcodeList.size()-2))));
                                boolean found = false;
                                for (BasicBlock b : block.getSuccessors()) {
                                    if (b.getOffset() != convertToInt(getPUSHArg(opcodeList.get(opcodeList.size()-2))).intValue()) {
                                        blockTracing.appendCode(" ELSE @Block: " + b.getOffset() + "\n");
                                        found = true;
                                    }
                                }
                                if (!found) blockTracing.appendCode("\n");
                            } else {
                                for (BasicBlock b : block.getSuccessors()) {
                                    if (b.getOffset() != convertToInt(getPUSHArg(opcodeList.get(opcodeList.size() - 2))).intValue()) {
                                        blockTracing.appendCode(" => @Block: " + b.getOffset());
                                    }
                                }
                                blockTracing.appendCode(" ELSE @Block: " +  convertToInt(getPUSHArg(opcodeList.get(opcodeList.size() - 2))) + "\n");
                            }
                        }
                    }
                }
            }
        }
        if (!isLogicInstruction(opcode).equals("??")) {
            String firstOperand =  stack.remove(stack.size() - 1);
            String secondOperand =  stack.remove(stack.size() - 1);

            // EURISITICA 1: se viene applicato un operatore logico tra:
            // - maschera d'indirizzo 0xffffff...
            // - divisione tra 1 e una variabile presa dallo storage
            // Si tiene come risultato la variabile presa dallo storage
            if (firstOperand.equals(ADDRESS_MASK) && secondOperand.contains("1 // _storage")) {
                stack.add(secondOperand.substring(5));
            }
            // EURISTICA 2: se viene applicato un operatore logico tra:
            // - maschera d'indirizzo 0xffffff...
            // - qualsiasi altro operando
            // Si tiene come risultato il secondo operando
            else if (firstOperand.startsWith("0xf")) {
                if (secondOperand.startsWith("_storage0"))
                    typeInferences.put(secondOperand, "address");
                stack.add(secondOperand);
            }
            else if (!doesNotContainAbstractValue(firstOperand) || !doesNotContainAbstractValue(secondOperand)) {
                stack.add(firstOperand);
            }
            else {
                BigInteger firstOperandInt = convertToInt(firstOperand);
                BigInteger secondOperandInt = convertToInt(secondOperand);
                BigInteger mask = BigInteger.ONE.shiftLeft(256).subtract(BigInteger.ONE);

                switch (isLogicInstruction(opcode)) {
                    case "&&" : stack.add("0x" + firstOperandInt.and(secondOperandInt).and(mask).toString(16)); break;
                    case "||" : stack.add("0x" + firstOperandInt.or(secondOperandInt).and(mask).toString(16)); break;
                }
            }
        }
        if (containsInstruction(opcode, "RETURN")) {
            String size = stack.remove(stack.size() - 1);
            stack.remove(stack.size() - 1);

            if (doesNotContainAbstractValue(size)) {
                Optional<String> returnValue = memory.loadValueFromMemory(convertToInt(size));

                if (returnValue.isPresent() && returnValue.get().contains("storage")) {
                    blockTracing.appendCode("return " + returnValue.get() + ";\n");
                } else {
                    blockTracing.appendCode("return " + size + ";\n");
                }
            }
        }
        if (containsInstruction(opcode, "CALLDATALOAD")) {
            stack.remove(stack.size() - 1);
            functionArguments.add(functionArguments.size());
            stack.add("_arg" + functionArguments.size());
        }
        if (containsInstruction(opcode, "CALLDATASIZE")) {
            stack.add("0x1");
        }
        if (containsInstruction(opcode, "CALLDATACOPY")) {
            String memOffset = stack.remove(stack.size() - 1);
            String dataOffset =  stack.remove(stack.size() - 1);
            String length = stack.remove(stack.size() - 1);
        }
        if (containsInstruction(opcode, "CALLVALUE")) {
            stack.add("msg.value");
        }
        if (containsInstruction(opcode, "CALLER")) {
            typeInferences.put("msg.sender", "address");
            stack.add("msg.sender");
        }
        if (containsInstruction(opcode, "SHA3")) {
            BigInteger firstValue =  convertToInt(stack.remove(stack.size() - 1));
            BigInteger secondValue = convertToInt(stack.remove(stack.size() - 1));
            List<String> values = new ArrayList<>();

            while (firstValue.compareTo(secondValue) < 0) {
                Optional<String> valueMemorized = memory.loadValueFromMemory(firstValue);
                valueMemorized.ifPresent(values::add);
                firstValue = firstValue.add(BigInteger.valueOf(32));
            }
            if (values.size() == 2) {
                typeInferences.put("_storage" + convertToInt(values.get(1)), "mapping(" + typeInferences.get(values.get(0)) +  " => z)");
                stack.add(convertToInt(values.get(1)) + "[" + values.get(0) + "]");
            } else if (values.size() == 1) {
                stack.add(values.get(0));
            }
        }
        if (containsInstruction(opcode, "SHR")) {
            String shift = stack.remove(stack.size() - 1);
            if (doesNotContainAbstractValue(shift)) {
                String value = stack.remove(stack.size() - 1);
                if (doesNotContainAbstractValue(value)) {
                    BigInteger shiftInt = convertToInt(shift);
                    BigInteger valueInt = convertToInt(value);

                    if (shiftInt.compareTo(BigInteger.valueOf(256)) > 0)
                        stack.add("0x00");
                    else {
                        BigInteger result = valueInt.shiftRight(shiftInt.intValue());
                        stack.add((result.toString()));
                    }
                } else {
                    stack.add(value);
                }
            }
        }
        if (equalsInstruction(opcode, "CALL")) {
            String gas =  stack.remove(stack.size() - 1);
            String to =  stack.remove(stack.size() - 1);
            String value = stack.remove(stack.size() - 1);
            String argOffset = stack.remove(stack.size() - 1);
            String argsLength = stack.remove(stack.size() - 1);
            String retOffset = stack.remove(stack.size() - 1);
            String retLength = stack.remove(stack.size() - 1);

            stack.add("0x01");

            // send() or transfer()
            if (gas.contains("2300")) {
                // transfer()
                if (opcodeList.get(opcodeIndex+1).toString().contains("ISZERO"))
                    blockTracing.appendCode(to +  ".transfer(" + value + ");\n");
                else
                    blockTracing.appendCode(to +  ".send(" + value + ");\n");
            } else {
                blockTracing.appendCode(to +  ".call(" + value + ");\n");
            }
        }
        if (containsInstruction(opcode, "LOG")) {
            String mem_start = stack.remove(stack.size() - 1);
            String mem_size = stack.remove(stack.size() - 1);
            String eventHash = stack.remove(stack.size() - 1);
            int topics = Integer.parseInt(opcode.toString().split(" ")[1].substring(3));
            List<String> values = new ArrayList<>();

            for (int k = 1; k < topics; k++) {
                String topic =  stack.remove(stack.size() - 1);
                values.add(topic);
            }
            String code = "event_" + eventHash + "(";
            for (int a = 0; a < values.size(); a++) {
                String value = values.get(a);
                if (a < values.size() - 1) {
                    code += value + ", ";
                } else {
                    code += value + ");\n";
                }
            }
            blockTracing.appendCode(code);
        }
    }

    private boolean containsInstruction(Opcode opcode, String instruction) {
        return opcode.toString().contains(instruction);
    }

    private boolean equalsInstruction(Opcode opcode, String instruction) {
        return opcode.toString().split(" ")[1].equals(instruction);
    }

    private boolean isInstruction(Opcode opcode, String instruction) {
        return opcode.toString().split(" ")[1].equals(instruction);
    }

    private String isOperationInstruction(Opcode opcode) {
        String opStr = opcode.toString().split(" ")[1];
        switch (opStr) {
            case "ADD": return "+";
            case "SUB": return "-";
            case "MUL": return "*";
            case "DIV": return "/";
            default: return "??";
        }
    }

    private String isCompareInstruction(Opcode opcode) {
        String opStr =  opcode.toString().split(" ")[1];
        switch (opStr) {
            case "GT":
            case "SGT": return ">";
            case "LT":
            case "SLT": return "<";
            case "EQ": return "==";
            default: return "??";
        }
    }

    private String getTypeFromOperation(Opcode opcode) {
        String opStr =  opcode.toString().split(" ")[1];
        switch (opStr) {
            case "GT":
            case "EQ":
            case "LT":
                return "uint";
            case "SGT":
            case "SLT":
                return "int";
            default: return "??";
        }
    }

    private String isLogicInstruction(Opcode opcode) {
        String opStr = opcode.toString().split(" ")[1];
        switch (opStr) {
            case "AND": return "&&";
            case "OR": return "||";
            default: return "??";
        }
    }

    private String isArrayElement(String value) {
        for (Map.Entry<String, List<String>> entry : arrayLocations.entrySet()) {
            for (String en : entry.getValue()) {
                if (convertToInt(value).equals(convertToInt(en)))
                    return entry.getKey();
            }
        }
        return null;
    }

    private void updateArrayTypeElements(String key, String type) {
        for (Map.Entry<String, String> entry : typeInferences.entrySet()) {
            if (entry.getKey().startsWith(key + "[")) {
                typeInferences.put(entry.getKey(), type);
            }
        }
    }

    private boolean doesNotContainAbstractValue(String value) {
        for (String abstractValue : ABSTRACT_VALUES) {
            if (value.contains(abstractValue)) { return false; }
        }
        return true;
    }

    private boolean doesNotContainCompareSigns(String value) {
        for (String compareSign : COMPARE_SIGNS) {
            if (value.contains(compareSign)) { return false; }
        }
        return true;
    }

    // Control if one of the operands is a neutral element (var + 0, var * 1, ...)
    private String isNeutralOperation(String first, String second, String sign) {
        if (!doesNotContainAbstractValue(first) && !doesNotContainAbstractValue(second))
            return null;
        if ((doesNotContainAbstractValue(second) && convertToInt(second).equals(BigInteger.valueOf(0))) && (sign.equals("+") || sign.equals("-")))
            return first;
        else if ((doesNotContainAbstractValue(first) && convertToInt(first).equals(BigInteger.valueOf(0))) && (sign.equals("+") || sign.equals("-")))
            return second;
        else if ((doesNotContainAbstractValue(second) && convertToInt(second).equals(BigInteger.valueOf(1))) && (sign.equals("*") || sign.equals("/")))
            return first;
        else if ((doesNotContainAbstractValue(first) && convertToInt(first).equals(BigInteger.valueOf(1))) &&  (sign.equals("*") || sign.equals("/")))
            return second;

        return null;
    }

    private BigInteger calculate(String sign, BigInteger firstOperandInt, BigInteger secondOperandInt) {
        BigInteger res;
        switch (sign) {
            case "+": res = firstOperandInt.add(secondOperandInt); break;
            case "-": res = firstOperandInt.subtract(secondOperandInt); break;
            case "*": res = firstOperandInt.multiply(secondOperandInt); break;
            case "//": res = firstOperandInt.divide(secondOperandInt); break;
            default: res = BigInteger.valueOf(-1);
        }
        return res.mod(BigInteger.valueOf(1).shiftLeft(256));
    }

    private String getPUSHArg(Opcode opcode) {
        return opcode.toString().split(" ")[2];
    }

    private Integer getInstructionIndex(Opcode opcode, int offset) {
        int index = Integer.parseInt(opcode.toString().split(" ")[1].substring(offset));

        if (index < 1 || index > stack.size())
            System.out.println(opcode + " : invalid index => " + index);

        return index;
    }

    private BigInteger convertToInt(String value) {
        return new BigInteger(value.substring(2), 16);
    }
}
