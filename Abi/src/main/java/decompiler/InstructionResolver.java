package decompiler;

import opcodes.Opcode;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.BasicBlockType;

import java.math.BigInteger;
import java.util.*;

public class InstructionResolver {
    private EVMemoryStructure memory;                        // EVM Memory Structure simulator
    private Map<String, String> storage = new HashMap<>();   // EVM Storage Structure simulator
    private ArrayList<String> stack = new ArrayList<>();     // EVM Stack Structure simulator

    Map<Long, Integer> visitBlocksCounter;
    boolean checkingLocalVariableInStack;

    private static final List<BasicBlockType> READABLE_BLOCKS = Arrays.asList(BasicBlockType.CODE, BasicBlockType.DISPATCHER, BasicBlockType.FALLBACK);
    private static final List<BasicBlockType> ANALYZABLE_BLOCKS = Arrays.asList(BasicBlockType.CODE, BasicBlockType.FALLBACK);
    private static final List<String> ABSTRACT_VALUES = Arrays.asList("storage", "msg", "arg");
    private static final List<String> COMPARE_SIGNS = Arrays.asList(">", "<", "==", "!=");
    private static final String ADDRESS_MASK = "0xffffffffffffffffffffffffffffffffffffffff";

    public InstructionResolver() {
        visitBlocksCounter = new HashMap<>();
    }

    /**
     * Resolve all possible paths and gets instructions from opcodes found.
     * @param paths
     * @return List of Block Trace
     */
    public List<BlockTracing> resolve(List<List<BasicBlock>> paths) {
        List<BlockTracing> relations = new ArrayList<>();
        for (List<BasicBlock> path : paths) {
            resolvePath(path, relations);
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
        else if (containsInstruction(opcode, "POP")) {
            stack.remove(stack.size() - 1);
        }
        else if (containsInstruction(opcode, "DUP")) {
            int idx = getInstructionIndex(opcode, 3);
            stack.add(stack.get(stack.size() - idx));
        }
        else if (containsInstruction(opcode, "SWAP")) {
            int idx = getInstructionIndex(opcode, 4);
            int lastIndex = stack.size() - 1;
            int targetIndex = lastIndex - idx;
            Collections.swap(stack, lastIndex, targetIndex);

        }
        else if (containsInstruction(opcode, "SLOAD")) {
            String value = stack.remove(stack.size() - 1);
            // No array, mapping...
            if (!value.contains("[")) {
                stack.add("_storage" + convertToInt(value));
            } else {
                stack.add("_storage" + value);
            }
        }
        else if (containsInstruction(opcode, "SSTORE")) {
            String offset = stack.remove(stack.size() - 1);
            String value = stack.remove(stack.size() - 1);

            if (!doesNotContainAbstractValue(offset)) {
                if (!doesNotContainAbstractValue(value)) {
                    if (block.getType() == BasicBlockType.CODE && visitBlocksCounter.get(block.getOffset()) == 1) {
                        blockTracing.appendCode("_storage" + offset + " = " + value + ";\n");
                    }
                } else {
                    if (block.getType() == BasicBlockType.CODE && visitBlocksCounter.get(block.getOffset()) == 1) {
                        blockTracing.appendCode("_storage" + offset + " = " + convertToInt(value) + ";\n");
                    }
                }
            } else {
                if (!doesNotContainAbstractValue(value)) {
                    if (block.getType() == BasicBlockType.CODE && visitBlocksCounter.get(block.getOffset()) == 1) {
                        blockTracing.appendCode("_storage" + convertToInt(offset) + " = " + value + ";\n");
                    }
                } else {
                    blockTracing.appendCode("_storage" + convertToInt(offset) + " = " + convertToInt(value) + ";\n");
                }
            }
            storage.put(offset, value);
        }
        else if (containsInstruction(opcode, "MLOAD")) {
            String address = stack.remove(stack.size() - 1);
            BigInteger startAddress = convertToInt(address);
            Optional<String> valueInMemory = memory.loadValueFromMemory(startAddress);

            if (valueInMemory.isPresent()) {
                stack.add(valueInMemory.get());
            } else {
                memory.addMemoryStructure(startAddress, startAddress.add(BigInteger.valueOf(31)), "0x00");
                stack.add("0x00");
            }
        }
        else if (containsInstruction(opcode, "MSTORE")) {
            String address = stack.remove(stack.size() - 1);
            String valueToMemorize =  stack.remove(stack.size() - 1);
            BigInteger startAddress = convertToInt(address);
            BigInteger endAddress = startAddress.add(BigInteger.valueOf(31));
            memory.addMemoryStructure(startAddress, endAddress, valueToMemorize);
        }
        else if (containsInstruction(opcode, "JUMPI")) {
            stack.remove(stack.size() - 1);
            stack.remove(stack.size() - 1);
        }
        else if (isInstruction(opcode, "JUMP")) {
            if (!stack.isEmpty()) {
                stack.remove(stack.size() - 1);
            }
        }
        else if (containsInstruction(opcode, "REVERT")) {
            stack.remove(stack.size() - 1);
            stack.remove(stack.size() - 1);
            blockTracing.appendCode("revert();\n");
        }
        else if (!isOperationInstruction(opcode).equals("??")) {
            String sign = isOperationInstruction(opcode);
            String firstOperand =  stack.remove(stack.size() - 1);
            String secondOperand =  stack.remove(stack.size() - 1);

            if (isNeutralOperation(firstOperand, secondOperand, sign)) {
                stack.add(firstOperand);
            }
            else if (!doesNotContainAbstractValue(firstOperand)) {
                if (!doesNotContainAbstractValue(secondOperand)) {
                    stack.add(firstOperand + " " + sign + " " + secondOperand);
                } else {
                    stack.add(firstOperand + " " + sign + " " + convertToInt(secondOperand));
                }
            } else if (!doesNotContainAbstractValue(secondOperand)) {
                stack.add(convertToInt(firstOperand) + " " + sign + " " + secondOperand);
            } else {
                BigInteger firstOperandInt = convertToInt(firstOperand);
                BigInteger secondOperandInt = convertToInt(secondOperand);
                BigInteger unsignedRes = calculate(sign, firstOperandInt, secondOperandInt);
                stack.add("0x" + unsignedRes.toString(16));
            }
        }
        else if (!isCompareInstruction(opcode).equals("??")) {
            String sign = isCompareInstruction(opcode);
            String firstOperand = stack.remove(stack.size() - 1);
            String secondOperand = stack.remove(stack.size() - 1);

            // ISZERO, ISZERO => revert sign
            if (containsInstruction(opcodeList.get(opcodeIndex+1), "ISZERO") && containsInstruction(opcodeList.get(opcodeIndex+2), "ISZERO")) {
                if (sign.equals(">")) sign = "<=";
                if (sign.equals("<")) sign = ">=";
                if (sign.equals("==")) sign = "!=";
            }

            if (!doesNotContainAbstractValue(firstOperand)) {
                if (!doesNotContainAbstractValue(secondOperand)) {
                    stack.add(firstOperand + " " + sign + " " + secondOperand);
                } else {
                    stack.add(firstOperand + " " + sign + " " + convertToInt(secondOperand));
                }
            } else if (!doesNotContainAbstractValue(secondOperand)) {
                stack.add(convertToInt(firstOperand) + " " + sign + " " + secondOperand);
            } else {
                BigInteger firstOperandInt = convertToInt(firstOperand);
                BigInteger secondOperandInt = convertToInt(secondOperand);
                stack.add(firstOperandInt.compareTo(secondOperandInt) > 0 ? "0x01" : "0x00");
            }
        }
        else if (containsInstruction(opcode, "EXP")) {
            BigInteger base = convertToInt(stack.remove(stack.size() - 1));
            BigInteger pow = convertToInt(stack.remove(stack.size() - 1));
            BigInteger mod = BigInteger.valueOf(1).shiftLeft(256);

            stack.add("0x" + base.modPow(pow, mod).toString(16));
        }
        else if (containsInstruction(opcode, "NOT")) {
            BigInteger value = convertToInt(stack.remove(stack.size() - 1));
            BigInteger mask = BigInteger.valueOf(2).pow(256).subtract(BigInteger.valueOf(1));
            stack.add("0x" + value.not().and(mask).toString(16));
        }
        else if (containsInstruction(opcode, "ISZERO")) {
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
        else if (!isLogicInstruction(opcode).equals("??")) {
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
            else if (firstOperand.startsWith("0xf") && firstOperand.length() >= 30) {
                stack.add(secondOperand);
            }
            else if (firstOperand.contains("storage") && secondOperand.startsWith("msg.sender")) {
                stack.add(firstOperand);
            } else if (firstOperand.startsWith("msg.sender") && secondOperand.contains("storage")) {
                stack.add(firstOperand);
            }
            else {
                BigInteger firstOperandInt = convertToInt(firstOperand);
                BigInteger secondOperandInt = convertToInt(secondOperand);
                BigInteger mask =  BigInteger.valueOf(1).shiftLeft(256).subtract(BigInteger.valueOf(1));

                switch (isLogicInstruction(opcode)) {
                    case "&&" : stack.add("0x" + firstOperandInt.and(secondOperandInt).and(mask).toString(16)); break;
                    case "||" : stack.add("0x" + firstOperandInt.or(secondOperandInt).and(mask).toString(16)); break;
                }
            }
        }
        else if (containsInstruction(opcode, "RETURN")) {
            String size = stack.remove(stack.size() - 1);
            stack.remove(stack.size() - 1);
            Optional<String> returnValue = memory.loadValueFromMemory(convertToInt(size));

            if (returnValue.isPresent() && returnValue.get().contains("storage")) {
                blockTracing.appendCode("return " + returnValue.get() + ";\n");
            } else {
                blockTracing.appendCode("return " + size + ";\n");
            }
        }
        else if (containsInstruction(opcode, "CALLDATALOAD")) {
            BigInteger idx = convertToInt(stack.remove(stack.size() - 1));
            stack.add("_arg" + idx);
        }
        else if (containsInstruction(opcode, "CALLVALUE")) {
            stack.add("msg.value");
        }
        else if (containsInstruction(opcode, "CALLER")) {
            // EURISTICA:
            // Non potendo vedere l'assegnamento a variabili locali perchè viene solo caricato
            // il valore sullo stack durante l'utilizzo, se quando letto CALLER, si verifica se
            // sullo stack sono presenti valori anomali (?).
            if (!checkingLocalVariableInStack) {
                for (String instruction : stack) {
                    if (instruction.startsWith("_storage") && instruction.contains("[")) {
                        if (block.getType() == BasicBlockType.CODE && visitBlocksCounter.get(block.getOffset()) == 1) {
                            blockTracing.appendCode("_localVar" + stack.indexOf(instruction) + " = " + instruction + ";\n");
                            checkingLocalVariableInStack = true;
                        }
                    }
                }
            }
            stack.add("msg.sender");
        }
        else if (containsInstruction(opcode, "SHA3")) {
            BigInteger firstValue =  convertToInt(stack.remove(stack.size() - 1));
            BigInteger secondValue = convertToInt(stack.remove(stack.size() - 1));
            List<String> values = new ArrayList<>();

            while (firstValue.compareTo(secondValue) < 0) {
                Optional<String> valueMemorized = memory.loadValueFromMemory(firstValue);
                valueMemorized.ifPresent(values::add);
                firstValue = firstValue.add(BigInteger.valueOf(32));
            }
            if (values.size() == 2) {
                stack.add(convertToInt(values.get(1)) + "[" + values.get(0) + "]");
            }
        }
        else if (containsInstruction(opcode, "CALL")) {
            String gas =  stack.remove(stack.size() - 1);
            String to =  stack.remove(stack.size() - 1);
            String value = stack.remove(stack.size() - 1);
            String argOffset = stack.remove(stack.size() - 1);
            String argsLength = stack.remove(stack.size() - 1);
            String retOffset = stack.remove(stack.size() - 1);
            String retLength = stack.remove(stack.size() - 1);

            stack.add("success");

            blockTracing.appendCode(to +  ".call();\n");
        }
    }

    private boolean containsInstruction(Opcode opcode, String instruction) {
        return opcode.toString().contains(instruction);
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
            case "DIV": return "//";
            default: return "??";
        }
    }

    private String isCompareInstruction(Opcode opcode) {
        String opStr =  opcode.toString().split(" ")[1];
        switch (opStr) {
            case "GT": return ">";
            case "EQ": return "==";
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

    private boolean doesNotContainAbstractValue(String value) {
        for (String abstractValue : ABSTRACT_VALUES) {
            if (value.contains(abstractValue)) { return false; }
        }
        return true;
    }

    private boolean isNeutralOperation(String first, String second, String sign) {
        if (!doesNotContainAbstractValue(first) && !doesNotContainAbstractValue(second))
            return false;
        if (convertToInt(second).equals(BigInteger.valueOf(0)) && (sign.equals("+") || sign.equals("-")))
            return true;
        else if (convertToInt(second).equals(BigInteger.valueOf(1)) && (sign.equals("*") || sign.equals("//")))
            return true;
        else
            return false;
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
