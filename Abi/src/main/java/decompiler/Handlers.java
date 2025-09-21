package decompiler;

import opcodes.Opcode;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.BasicBlockType;

import java.math.BigInteger;
import java.util.*;

public class Handlers {}

class PushHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        String arg = context.getResolver().getPUSHArg(context.getOpcode());
        context.getStack().add(arg);
    }
}

class PopHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        if (!context.getStack().isEmpty()) {
            context.getStack().remove(context.getStack().size() - 1);
        } else  {
            System.err.println("Warning: Attempted to POP from an empty stack: " + context.getOpcode());
        }
    }
}

class DupHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        int position = context.getResolver().getInstructionIndex(context.getOpcode(), 3);
        ArrayList<String> stack = context.getStack();
        if (stack.size() - position >= 0) {
            String valueToDup = stack.get(stack.size() - position);
            stack.add(valueToDup);
        } else {
            System.err.println("Warning: Attempted to DUP from an invalid position: " + context.getOpcode());
        }
    }
}

class SwapHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        int position = context.getResolver().getInstructionIndex(context.getOpcode(), 4);
        int lastIndex = context.getStack().size() - 1;
        int targetIndex = lastIndex - position;

        ArrayList<String> stack = context.getStack();
        if (stack.size() - (position + 1) >= 0) {
            Collections.swap(stack, lastIndex, targetIndex);
        } else {
            System.err.println("Warning: Attempted to SWAP from an invalid position: " + context.getOpcode());
        }
    }
}

class SloadHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        String value = context.getStack().remove(context.getStack().size() - 1);
        String rootArray = context.getResolver().isArrayElement(value);

        if (rootArray == null) {
            // No array, but mapping
            if (!value.contains("[") && context.getResolver().doesNotContainAbstractValue(value)) {
                if (context.getStorage().containsKey(value)) {
                    context.getStack().add(context.getStorage().get(value));
                } else {
                    context.getStack().add("_storage" + context.getResolver().convertToInt(value));
                }
            } else {
                if (context.getStorage().containsKey(value)) {
                    context.getStack().add(context.getStorage().get(value));
                } else {
                    context.getStack().add("_storage" + value);
                }
            }
        }
    }
}

class SstoreHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        String offset = context.getStack().remove(context.getStack().size() - 1);
        String value = context.getStack().remove(context.getStack().size() - 1);

        context.getStorage().put(offset, value);

        // check Struct pattern
        offset = context.getResolver().checkStructPattern("_storage" + offset).substring(8);
        value = context.getResolver().checkStructPattern(value);

        if (!context.getResolver().doesNotContainAbstractValue(offset)) {
            // Manage old occurrences of var in stack when the value changes.
            for (int a = 0; a < context.getStack().size(); a++) {
                String stackValue = context.getStack().get(a);
                if (stackValue.equals("_storage" + offset)) {
                    context.getStack().set(a, "_localVar" + a);
                    context.getBlockTracing().appendCode("_localVar" + a + " = _storage" + offset + ";\n");
                }
            }

            if (!context.getResolver().doesNotContainAbstractValue(value)) {
                if (context.getBlock().getType() == BasicBlockType.CODE && context.getVistiBlocksCounter().get(context.getBlock().getOffset()) == 1) {
                    String rootArray = context.getResolver().isArrayElement(offset);
                    if (rootArray == null) {
                        context.getBlockTracing().appendCode("_storage" + offset + " = " + value + ";\n");
                    } else {
                        context.getBlockTracing().appendCode("_storage" + context.getResolver().convertToInt(rootArray) + "[" + offset + "] = " + value + ";\n");
                    }
                }
            } else {
                if (context.getBlock().getType() == BasicBlockType.CODE && context.getVistiBlocksCounter().get(context.getBlock().getOffset()) == 1) {
                    String rootArray = context.getResolver().isArrayElement(offset);
                    if (rootArray == null) {
                        context.getBlockTracing().appendCode("_storage" + offset + " = " + context.getResolver().convertToInt(value) + ";\n");
                    } else {
                        context.getBlockTracing().appendCode("_storage" + context.getResolver().convertToInt(rootArray) + "[" + offset + "] = " + context.getResolver().convertToInt(value) + ";\n");
                    }
                }
            }
        } else {
            if (!context.getResolver().doesNotContainAbstractValue(value)) {
                if (context.getTypeInferences().get(value) != null)
                    context.getTypeInferences().put("_storage" +context.getResolver().convertToInt(offset), context.getTypeInferences().get(value));

                if (context.getBlock().getType() == BasicBlockType.CODE && context.getVistiBlocksCounter().get(context.getBlock().getOffset()) == 1) {
                    String rootArray = context.getResolver().isArrayElement(offset);
                    if (rootArray == null) {
                        try {
                            context.getBlockTracing().appendCode("_storage" + context.getResolver().convertToInt(offset) + " = " + value + ";\n");
                        } catch (NumberFormatException e) {
                            context.getBlockTracing().appendCode("_storage" + offset + " = " + value + ";\n");
                        }

                    } else {
                        context.getBlockTracing().appendCode("_storage" + context.getResolver().convertToInt(rootArray) + "[" + context.getResolver().convertToInt(offset) + "] = " + value + ";\n");
                    }
                }
            } else {
                if (context.getBlock().getType() == BasicBlockType.CODE && context.getVistiBlocksCounter().get(context.getBlock().getOffset()) == 1) {
                    String rootArray = context.getResolver().isArrayElement(offset);
                    if (rootArray == null) {
                        try {
                            if (context.getTypeInferences().get("_storage" + context.getResolver().convertToInt(offset)) != null && context.getTypeInferences().get("_storage" + context.getResolver().convertToInt(offset)).equals("bool")) {
                                if (context.getResolver().convertToInt(value).equals(BigInteger.valueOf(0))) {
                                    context.getBlockTracing().appendCode("_storage" + context.getResolver().convertToInt(offset) + " = false;\n");
                                } else {
                                    context.getBlockTracing().appendCode("_storage" + context.getResolver().convertToInt(offset) + " = true;\n");
                                }
                            } else {
                                context.getBlockTracing().appendCode("_storage" + context.getResolver().convertToInt(offset) + " = " + context.getResolver().convertToInt(value) + ";\n");
                            }
                        } catch (NumberFormatException e) {
                            context.getBlockTracing().appendCode("_storage" + offset + " = " + context.getResolver().convertToInt(value) + ";\n");
                        }

                    } else {
                        context.getBlockTracing().appendCode("_storage" + context.getResolver().convertToInt(rootArray) + "[" + context.getResolver().convertToInt(offset) + "] = " + context.getResolver().convertToInt(value) + ";\n");
                    }
                }
            }
        }
    }
}

class MloadHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        String address = context.getStack().remove(context.getStack().size() - 1);
        if (context.getResolver().doesNotContainAbstractValue(address)) {
            BigInteger startAddress = context.getResolver().convertToInt(address);
            Optional<String> valueInMemory = context.getMemory().loadValueFromMemory(startAddress);

            if (valueInMemory.isPresent()) {
                context.getStack().add(valueInMemory.get());
            } else {
                context.getMemory().addMemoryStructure(startAddress, startAddress.add(BigInteger.valueOf(31)), "0x00");
                context.getStack().add("0x00");
            }
        } else {
            context.getStack().add("mem(" + address + ")");
        }
    }
}

class MstoreHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        if (context.getStack().size() >= 2) {
            String address = context.getStack().remove((context.getStack().size() -1));
            String valueToMemorize = context.getStack().remove(context.getStack().size() - 1);
            BigInteger startAddress, endAddress;

            if (context.getResolver().doesNotContainAbstractValue(address)) {
                startAddress = context.getResolver().convertToInt(address);
                endAddress = startAddress.add(BigInteger.valueOf(31));
                context.getMemory().addMemoryStructure(startAddress, endAddress, valueToMemorize);
            }
        } else {
            System.err.println("Warning: skipped MSTORE: " + context.getOpcode());
        }
    }
}

class JumpHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        if (!context.getStack().isEmpty()) {
            context.getStack().remove(context.getStack().size() - 1);
        } else {
            System.err.println("Warning: skipped JUMP instruction: " + context.getOpcode());
        }
    }
}

class JumpiHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        context.getStack().remove(context.getStack().size() - 1);
        context.getStack().remove(context.getStack().size() - 1);
    }
}

class RevertHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        String offset = context.getStack().remove(context.getStack().size() - 1);
        String size = context.getStack().remove(context.getStack().size() - 1);

        System.err.println(context.getMemory());
        System.err.println("OFFSET: " + offset);
        System.err.println("SIZE: " + size);


        if (size.equals("0x00")) {
            context.getBlockTracing().appendCode("revert();\n");
        } else {
            // Avoid check error msg patterns (Solidity 0.8.x)
            for (MemoryStructure ms : context.getMemory().getMemoryStructures()) {
                for (String revertPattern : context.getResolver().AVOID_REVERT_VALUES) {
                    if (ms.getValueStored().equals(revertPattern))
                        return;
                }
            }
            String revertMsg;
            if (context.getMemory().getLastMemoryStructure().isPresent()) {
                revertMsg = context.getMemory().getLastMemoryStructure().get();
                if (!revertMsg.isEmpty()) {
                    String strRevertMsg = context.getResolver().hexToString(revertMsg);
                    if (!strRevertMsg.trim().isEmpty()) {
                        context.getBlockTracing().appendCode("revert(\"" + strRevertMsg + "\");\n");
                    } else {
                        context.getBlockTracing().appendCode("revert();\n");
                    }
                } else {
                    context.getBlockTracing().appendCode("revert();\n");
                }
            } else {
                context.getBlockTracing().appendCode("revert();\n");
            }
        }

    }
}

class OperationHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        String sign = context.getResolver().isOperationInstruction(context.getOpcode());
        String firstOperand = context.getStack().remove(context.getStack().size() - 1);
        String secondOperand = context.getStack().remove(context.getStack().size() - 1);

        String neutralOperand = context.getResolver().isNeutralOperation(firstOperand, secondOperand, sign);
        if (neutralOperand != null) {
            context.getStack().add(neutralOperand);
        } else if (!context.getResolver().doesNotContainAbstractValue(firstOperand)) {
            if (!context.getResolver().doesNotContainAbstractValue(secondOperand)) {
                if (!context.getTypeInferences().containsKey(firstOperand)) {
                    if (!firstOperand.contains("[")) {
                        context.getTypeInferences().put(firstOperand, "uint");
                    } else if (context.getTypeInferences().containsKey(firstOperand.split("\\[")[0])) {
                        if (context.getTypeInferences().get(firstOperand.split("\\[")[0]).startsWith("mapping")) {
                            context.getTypeInferences().put(firstOperand.split("\\[")[0], context.getTypeInferences().get(firstOperand.split("\\[")[0]).split(" => ")[0] + " => uint)");
                        }
                    }
                } else if (firstOperand.contains("[")) {
                    String typeArray = context.getTypeInferences().get(firstOperand.split("\\[")[0]);
                    typeArray = typeArray.replaceAll("var", "uint");
                    context.getTypeInferences().put(firstOperand.split("\\[")[0], typeArray);
                    context.getResolver().updateArrayTypeElements(firstOperand.split("\\[")[0], "uint");
                }

                if (!context.getTypeInferences().containsKey(secondOperand)) {
                    if (!secondOperand.contains("[")) {
                        context.getTypeInferences().put(secondOperand, "uint");
                    } else if (context.getTypeInferences().containsKey(secondOperand.split("\\[")[0])) {
                        if (context.getTypeInferences().get(secondOperand.split("\\[")[0]).startsWith("mapping")) {
                            context.getTypeInferences().put(secondOperand.split("\\[")[0], context.getTypeInferences().get(secondOperand.split("\\[")[0]).split(" => ")[0] + " => uint)");
                        }
                    }
                } else if (secondOperand.contains("[")) {
                    String typeArray = context.getTypeInferences().get(secondOperand.split("\\[")[0]);
                    typeArray = typeArray.replaceAll("var", "uint");
                    context.getTypeInferences().put(secondOperand.split("\\[")[0], typeArray);
                    context.getResolver().updateArrayTypeElements(secondOperand.split("\\[")[0], "uint");
                }

                context.getStack().add(firstOperand + " " + sign + " " + secondOperand);
            } else {
                if (!context.getTypeInferences().containsKey(firstOperand)) {
                    if (!firstOperand.contains("[")) {
                        context.getTypeInferences().put(firstOperand, "uint");
                    } else if (context.getTypeInferences().containsKey(firstOperand.split("\\[")[0])) {
                        if (context.getTypeInferences().get(firstOperand.split("\\[")[0]).startsWith("mapping")) {
                            context.getTypeInferences().put(firstOperand.split("\\[")[0], context.getTypeInferences().get(firstOperand.split("\\[")[0]).split(" => ")[0] + " => uint)");
                        }
                    }
                } else if (firstOperand.contains("[")) {
                    String typeArray = context.getTypeInferences().get(firstOperand.split("\\[")[0]);
                    typeArray = typeArray.replaceAll("var", "uint");
                    context.getTypeInferences().put(firstOperand.split("\\[")[0], typeArray);
                    context.getResolver().updateArrayTypeElements(firstOperand.split("\\[")[0], "uint");
                }

                context.getStack().add(firstOperand + " " + sign +  " " + context.getResolver().convertToInt(secondOperand));
            }
        } else if (!context.getResolver().doesNotContainAbstractValue(secondOperand)) {
            if (!context.getTypeInferences().containsKey(secondOperand)) {
                if (!secondOperand.contains("[")) {
                    context.getTypeInferences().put(secondOperand, "uint");
                } else if (context.getTypeInferences().containsKey(secondOperand.split("\\[")[0])) {
                    if (context.getTypeInferences().get(secondOperand.split("\\[")[0]).startsWith("mapping")) {
                        context.getTypeInferences().put(secondOperand.split("\\[")[0], context.getTypeInferences().get(secondOperand.split("\\[")[0]).split(" => ")[0] + " => uint)");
                    }
                }
            } else if (secondOperand.contains("[")) {
                String typeArray = context.getTypeInferences().get(secondOperand.split("\\[")[0]);
                typeArray = typeArray.replaceAll("var", "uint");
                context.getTypeInferences().put(secondOperand.split("\\[")[0], typeArray);
                context.getResolver().updateArrayTypeElements(secondOperand.split("\\[")[0], "uint");
            }

            context.getStack().add(context.getResolver().convertToInt(firstOperand) + " " + sign + " " + secondOperand);
        } else {
            BigInteger firstOperandInt = context.getResolver().convertToInt(firstOperand);
            BigInteger secondOperandInt = context.getResolver().convertToInt(secondOperand);
            BigInteger unsignedRes = context.getResolver().calculate(sign, firstOperandInt, secondOperandInt);

            context.getStack().add("0x" + unsignedRes.toString(16));

        }
    }
}

class CompareHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        String sign = context.getResolver().isCompareInstruction(context.getOpcode());
        String firstOperand = context.getStack().remove(context.getStack().size() - 1);
        String secondOperand = context.getStack().remove(context.getStack().size() - 1);

        // ISZERO after compareCode ==> inverse operand
        int isZeroBackwardCounter = 0;
        int counter = 1;
        while (context.getResolver().containsInstruction(context.getOpcodeList().get(context.getOpcodeIndex() + counter), "ISZERO")) {
            counter++;
            isZeroBackwardCounter++;
        }

        if (isZeroBackwardCounter % 2 != 0) {
            switch (sign) {
                case ">": sign = "<="; break;
                case "<": sign = ">="; break;
                case "==": sign = "!="; break;
            }
        }

        // Array pattern: they check if index < length
        if (context.getOpcode().toString().contains("LT")) {
            // If index >= length --> INVALID opcode
            for (BasicBlock successor : context.getBlock().getSuccessors()) {
                for (Opcode opcode : successor.getOpcodes()) {
                    if (opcode.toString().split(" ")[1].equals("INVALID")) {
                        String rootArray = context.getStack().get(context.getStack().size() - 2);
                        List<String> indexes = new ArrayList<>();
                        if (context.getResolver().doesNotContainAbstractValue(secondOperand)) {
                            for (int i = context.getResolver().convertToInt(rootArray).intValue(); i < context.getResolver().convertToInt(secondOperand).intValue(); i++) {
                                indexes.add("0x" + i);
                                if (!context.getTypeInferences().containsKey("_storage" + context.getResolver().convertToInt(rootArray) + "[" + context.getResolver().convertToInt("0x" + i) + "]")) {
                                    context.getTypeInferences().put("_storage" + context.getResolver().convertToInt(rootArray) + "[" + context.getResolver().convertToInt("0x" + i) + "]" , "var");
                                }
                            }
                            context.getArrayLocations().put(rootArray, indexes);
                            if (!context.getTypeInferences().containsKey("_storage" + context.getResolver().convertToInt(rootArray))) {
                                context.getTypeInferences().put("_storage" + context.getResolver().convertToInt(rootArray), "var[" + context.getResolver().convertToInt(secondOperand).intValue() + "]");
                            }
                            break;
                        }
                    }
                }
            }
        }

        String resultOfCompare;
        if (!context.getResolver().doesNotContainAbstractValue(firstOperand)) {
            if (!context.getResolver().doesNotContainAbstractValue(secondOperand)) {
                String typeInference = context.getResolver().getTypeFromOperation(context.getOpcode());
                context.getTypeInferences().put(firstOperand, typeInference);
                context.getTypeInferences().put(secondOperand, typeInference);
                resultOfCompare = firstOperand + " " + sign + " " + secondOperand;
            } else {
                String typeInference = context.getResolver().getTypeFromOperation(context.getOpcode());
                context.getTypeInferences().put(firstOperand, typeInference);
                try {
                    resultOfCompare = firstOperand + " " + sign + " " + context.getResolver().convertToInt(secondOperand);
                } catch (NumberFormatException e) {
                    resultOfCompare = "0x00";
                }
            }
        } else if (!context.getResolver().doesNotContainAbstractValue(secondOperand)) {
            String typeInference = context.getResolver().getTypeFromOperation(context.getOpcode());
            context.getTypeInferences().put(secondOperand, typeInference);
            resultOfCompare = context.getResolver().convertToInt(firstOperand) + " " + sign + secondOperand;
        } else {
            BigInteger firstOperantInt = context.getResolver().convertToInt(firstOperand);
            BigInteger secondOperantInt = context.getResolver().convertToInt(secondOperand);
            resultOfCompare = firstOperantInt.compareTo(secondOperantInt) > 0 ? "0x01" : "0x00";
        }

        context.getStack().add(resultOfCompare);

        // If there is no opcode ISZERO after compare, flag that as "need to manage it"
        if (context.getOpcodeList().get(context.getOpcodeIndex() + 1).toString().contains("PUSH") &&
                context.getOpcodeList().get(context.getOpcodeIndex() + 2).toString().contains("JUMPI")) {
            context.setNeedToManageIf(true);
        }
    }
}

class ExpHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        String baseStr = context.getStack().remove(context.getStack().size() - 1);
        String powStr = context.getStack().remove(context.getStack().size() - 1);
        if (context.getResolver().doesNotContainAbstractValue(baseStr) && context.getResolver().doesNotContainAbstractValue(powStr)) {
            BigInteger base = context.getResolver().convertToInt(baseStr);
            BigInteger pow = context.getResolver().convertToInt(powStr);
            BigInteger mod = BigInteger.valueOf(1).shiftLeft(256);
            context.getStack().add("0x" + base.modPow(pow, mod).toString(16));
        } else {
            context.getStack().add(baseStr + "^" + powStr);
        }
    }
}

class NotHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        String valueStr = context.getStack().remove(context.getStack().size() - 1);
        if (context.getResolver().doesNotContainAbstractValue(valueStr)) {
            BigInteger value = context.getResolver().convertToInt(valueStr);
            BigInteger mask = BigInteger.valueOf(2).pow(256).subtract(BigInteger.valueOf(1));
            context.getStack().add("0x" + value.not().and(mask).toString(16));
        } else {
            context.getStack().add("!" + valueStr);
        }
    }
}

class LogicHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        String firstOperand = context.getStack().remove(context.getStack().size() - 1);
        String secondOperand = context.getStack().remove(context.getStack().size() - 1);

        if (firstOperand.equals(context.getResolver().ADDRESS_MASK) && secondOperand.contains("1 // _storage")) {
            context.getStack().add(secondOperand.substring(5));
        } else if (firstOperand.startsWith("0xf")) {
            if (secondOperand.startsWith("_storage0")) {
                context.getTypeInferences().put(secondOperand, "address");
            }
            context.getStack().add(secondOperand);
        } else if (!context.getResolver().doesNotContainAbstractValue(firstOperand) && context.getResolver().doesNotContainAbstractValue(secondOperand)) {
            context.getStack().add(firstOperand);
        }
        else if (context.getResolver().doesNotContainAbstractValue(firstOperand) && !context.getResolver().doesNotContainAbstractValue(secondOperand)) {
            context.getStack().add(secondOperand);
        }
        else if (!context.getResolver().doesNotContainAbstractValue(firstOperand) && !context.getResolver().doesNotContainAbstractValue(secondOperand)) {
           context.getStack().add(firstOperand);
        }
        else {
            BigInteger firstOperandInt = context.getResolver().convertToInt(firstOperand);
            BigInteger secondOperandInt = context.getResolver().convertToInt(secondOperand);
            BigInteger mask = BigInteger.ONE.shiftLeft(256).subtract(BigInteger.ONE);

            switch (context.getResolver().isLogicInstruction(context.getOpcode())) {
                case "&&": context.getStack().add("0x" + firstOperandInt.and(secondOperandInt).and(mask).toString(16)); break;
                case "||": context.getStack().add("0x" + firstOperandInt.or(secondOperandInt).and(mask).toString(16)); break;
            }
        }
    }
}

class ReturnHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        if (context.getStack().size() >= 2) {
            String size = context.getStack().remove(context.getStack().size() - 1);
            context.getStack().remove(context.getStack().size() - 1);

            if (context.getResolver().doesNotContainAbstractValue(size)) {
                Optional<String> returnValue = context.getMemory().loadValueFromMemory(context.getResolver().convertToInt(size));
                if (returnValue.isPresent() && context.getResolver().hasAReturnType(returnValue.get())) {
                    context.getBlockTracing().appendCode("return " + returnValue.get() + ";\n");

                    for (Map.Entry<String, String> entry : context.getTypeInferences().entrySet()) {
                        if (returnValue.get().contains(entry.getKey())) {
                            context.getTypeInferences().put(returnValue.get(), entry.getValue());
                            break;
                        }
                    }

                } else {
                    context.getBlockTracing().appendCode("return " + size + ";\n");
                }
            } else {
                context.getBlockTracing().appendCode("return " + size + ";\n");
            }
        } else {
            System.err.println("Warning: Skipped RETURN opcode: " + context.getOpcode());
        }
    }
}

class ReturnDataSizeHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        context.getStack().add("0x01");
    }
}

class ReturnDataCopyHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        String destOffset = context.getStack().remove(context.getStack().size() - 1);
        String offset = context.getStack().remove(context.getStack().size() - 1);
        String size = context.getStack().remove(context.getStack().size() - 1);
    }
}

class GasHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        context.getStack().add("0x01");
    }
}

class CallHandler implements OpcodeHandler {
    @Override
    public void handle(ExecutionContext context) {
        String gas = context.getStack().remove(context.getStack().size() - 1);
        String to = context.getStack().remove(context.getStack().size() - 1);
        String value = context.getStack().remove(context.getStack().size() - 1);
        String argsOffset = context.getStack().remove(context.getStack().size() - 1);
        String argsLength = context.getStack().remove(context.getStack().size() - 1);
        String retOffset = context.getStack().remove(context.getStack().size() - 1);
        String retLength = context.getStack().remove(context.getStack().size() - 1);

        context.getStack().add("0x01");

        boolean containsCheckOpcodes = false;
        int counter = 1;
        while ((context.getOpcodeIndex() + counter) < context.getOpcodeList().size()) {
            String opcodeTemp = context.getOpcodeList().get(context.getOpcodeIndex() + counter).toString();
            if (opcodeTemp.contains("ISZERO")) {
                containsCheckOpcodes = true;
                context.setCheckOfCallMethod(true);
                break;
            }
            counter++;
        }

        if (context.getVistiBlocksCounter().get(context.getBlock().getOffset()) == 1) {
            if (gas.contains("2300")) {
                if (containsCheckOpcodes) {
                    context.getBlockTracing().appendCode(to + ".transfer(" + value + ");\n");
                } else {
                    context.getBlockTracing().appendCode(to + ".send(" + value + ");\n");
                }
            } else {
                context.getBlockTracing().appendCode(to + ".call(" + value + ");\n");
            }
        }
    }
}

class CallDataLoadHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        context.getStack().remove(context.getStack().size() - 1);
        context.getFunctionArguments().add(context.getFunctionArguments().size());
        context.getStack().add("_arg" + context.getFunctionArguments().size());
        context.getTypeInferences().put("_arg" + context.getFunctionArguments().size(), "var");
    }
}

class CallDataSizeHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        context.getStack().add("0x01");
    }
}

class CallDataCopyHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        String memOffset = context.getStack().remove(context.getStack().size() - 1);
        String dataOffset = context.getStack().remove(context.getStack().size() - 1);
        String length = context.getStack().remove(context.getStack().size() - 1);
    }
}

class CallValueHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        context.getStack().add("msg.value");
    }
}

class CallerHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        context.getTypeInferences().put("msg.sender", "address");
        context.getStack().add("msg.sender");
    }
}

class Sha3Handler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        String first = context.getStack().remove(context.getStack().size() - 1);
        String second = context.getStack().remove(context.getStack().size() - 1);

        if (context.getResolver().doesNotContainAbstractValue(first) && context.getResolver().doesNotContainAbstractValue(second)) {
            BigInteger firstValue = context.getResolver().convertToInt(first);
            BigInteger secondValue = context.getResolver().convertToInt(second);
            List<String> values = new ArrayList<>();

            while (firstValue.compareTo(secondValue) < 0) {
                Optional<String> valueMemorized = context.getMemory().loadValueFromMemory(firstValue);
                valueMemorized.ifPresent(values::add);
                firstValue = firstValue.add(BigInteger.valueOf(32));
            }
            if (values.size() == 2) {
                try {
                    if (context.getTypeInferences().get("_storage" + context.getResolver().convertToInt(values.get(1))) == null)
                        context.getTypeInferences().put("_storage" + context.getResolver().convertToInt(values.get(1)), "mapping(" + context.getTypeInferences().get(values.get(0)) + " => z)");

                    context.getStack().add(context.getResolver().convertToInt(values.get(1)) + "[" + values.get(0) + "]");
                } catch (NumberFormatException e) {
                    if (context.getTypeInferences().get("_storage" + values.get(1)) == null)
                        context.getTypeInferences().put("_storage" + values.get(1), "mapping(" + context.getTypeInferences().get(values.get(0)) + " => z)");

                    context.getStack().add(values.get(1) + "[" + values.get(0) + "]");
                }
            } else if (values.size() == 1) {
                context.getStack().add(values.get(0));
            } else {
                System.err.println("Warning: SHA3 skipped: " + context.getOpcode());
            }
        }
    }
}

class ShrHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        String shift = context.getStack().remove(context.getStack().size() - 1);
        if (context.getResolver().doesNotContainAbstractValue(shift)) {
            String value = context.getStack().remove(context.getStack().size() - 1);
            if (context.getResolver().doesNotContainAbstractValue(value)) {
                BigInteger shiftInt = context.getResolver().convertToInt(shift);
                BigInteger valueInt = context.getResolver().convertToInt(value);

                if (shiftInt.compareTo(BigInteger.valueOf(256)) > 0) {
                    context.getStack().add("0x00");
                } else {
                    BigInteger result = valueInt.shiftRight(shiftInt.intValue());
                    context.getStack().add(result.toString());
                }
            } else {
                context.getStack().add(value);
            }
        }
    }
}

class ShlHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        String shift = context.getStack().remove(context.getStack().size() - 1);
        if (context.getResolver().doesNotContainAbstractValue(shift)) {
            String value = context.getStack().remove(context.getStack().size() - 1);
            if (context.getResolver().doesNotContainAbstractValue(value)) {
                BigInteger shiftInt = context.getResolver().convertToInt(shift);
                BigInteger valueInt = context.getResolver().convertToInt(value);

                if (shiftInt.compareTo(BigInteger.valueOf(256)) > 0) {
                    context.getStack().add("0x00");
                } else {
                    BigInteger result = valueInt.shiftLeft(shiftInt.intValue());
                    context.getStack().add(result.toString());
                }
            } else {
                context.getStack().add(value);
            }
        }
    }
}

class LogHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        int topics = Integer.parseInt(context.getOpcode().toString().split(" ")[1].substring(3));
        String mem_start = context.getStack().remove(context.getStack().size() - 1);
        String mem_size = context.getStack().remove(context.getStack().size() - 1);
        String eventHash = context.getStack().remove(context.getStack().size() - 1);

        String eventName = "_event" + eventHash;
        KnownHashEvent event = null;
        for (KnownHashEvent e : KnownHashEvent.KNOWN_HASH_EVENTS) {
            if (e.getHash().equals(eventHash)) {
                event = e;
                eventName = e.getSignature();
                break;
            }
        }

        List<String> indexedValues = new ArrayList<>();
        for (int i = 1; i < topics; i++) {
            indexedValues.add(context.getStack().remove(context.getStack().size() - 1));
        }


        String noIndexed;
        if (context.getResolver().doesNotContainAbstractValue(mem_start)) {
            Optional<String> temp = context.getMemory().loadValueFromMemory(context.getResolver().convertToInt(mem_start));
            if (temp.isPresent() && (indexedValues.size() < Objects.requireNonNull(event).getIndexCount())) {
                noIndexed = temp.get();
            } else {
                noIndexed = "";
            }
        } else {
            noIndexed = "mem_" + mem_start;
        }

        String code = eventName + "(";
        code += String.join(", ", indexedValues);
        if (!noIndexed.isEmpty()) {
            if (!indexedValues.isEmpty()) {
                code += ", ";
            }
            code += noIndexed;
        }
        code += ");\n";

        // Mapping event definition
        String eventDefinition = "event " + eventName + "(";
        if (event != null) {
            for (int a = 0; a < event.getTypeArgs().size(); a++) {
                eventDefinition += event.getTypeArgs().get(a) + " " + event.getNameArgs().get(a);
                if (a == event.getTypeArgs().size() - 1) {
                    eventDefinition += ");";
                } else {
                    eventDefinition += ", ";
                }
            }
        }

        context.getEvents().add(eventDefinition);
        context.getBlockTracing().appendCode(code);
    }
}

class AddressHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        context.getStack().add("this");
    }
}

class BalanceHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        String address = context.getStack().remove(context.getStack().size() - 1);
        context.getStack().add(address + ".balance");
        context.getTypeInferences().put("this.balance", "uint");
    }
}

class IsZeroHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        int indexOfNextInstructions = 1;

        if (context.isNeedToManageIf()) {
            context.setNeedToManageIf(false);
            indexOfNextInstructions--;
        }

        if (!context.getResolver().containsInstruction(context.getOpcodeList().get(context.getOpcodeIndex() + 1), "ISZERO")) {
            if (context.isCheckOfCallMethod()) {
                context.setCheckOfCallMethod(false);
            } else {
                String value = context.getStack().get(context.getStack().size() - 1);
                if (context.getResolver().doesNotContainAbstractValue(value)) {
                    BigInteger valueInt = context.getResolver().convertToInt(context.getStack().remove(context.getStack().size() - 1));
                    if (valueInt.equals(BigInteger.valueOf(0))) {
                        context.getStack().add("0x00");
                    } else {
                        context.getStack().add("0x01");
                    }
                } else {
                    if (context.getResolver().ANALYZABLE_BLOCKS.contains(context.getBlock().getType()) &&
                            context.getVistiBlocksCounter().get(context.getBlock().getOffset()) == 1 &&
                            context.getOpcodeList().get(context.getOpcodeIndex() + indexOfNextInstructions).toString().contains("PUSH") &&
                            context.getOpcodeList().get(context.getOpcodeIndex() + indexOfNextInstructions + 1).toString().contains("JUMP")) {

                        if (context.getBlock().getSuccessors().stream().distinct().count() == 2) {
                            int isZeroBackwardCounter = 0;
                            int counter = 1;
                            while (context.getResolver().containsInstruction(context.getOpcodeList().get(context.getOpcodeIndex() - counter), "ISZERO")) {
                                counter++;
                                isZeroBackwardCounter++;
                            }
                            String negateBooleanCondition = "";
                            if (context.getResolver().doesNotContainCompareSigns(value)) {
                                context.getTypeInferences().put(value, "bool");
                                if (isZeroBackwardCounter % 2 == 0) {
                                    negateBooleanCondition = "!";
                                }
                            }
                            context.getBlockTracing().appendCode("if (" + negateBooleanCondition + value + ")");

                            if (isZeroBackwardCounter % 2 == 0) {
                                context.getBlockTracing().appendCode(" => @Block: " + context.getResolver().convertToInt(context.getResolver().getPUSHArg(context.getOpcodeList().get(context.getOpcodeList().size() - 2))));
                                boolean found = false;
                                for (BasicBlock b : context.getBlock().getSuccessors()) {
                                    if (b.getOffset() != context.getResolver().convertToInt(context.getResolver().getPUSHArg(context.getOpcodeList().get(context.getOpcodeList().size() - 2))).intValue()) {
                                        context.getBlockTracing().appendCode(" ELSE @Block: " + b.getOffset() + "\n");
                                        found = true;
                                    }
                                }
                                if (!found) {
                                    context.getBlockTracing().appendCode("\n");
                                }
                            } else {
                                for (BasicBlock b : context.getBlock().getSuccessors()) {
                                    if (b.getOffset() != context.getResolver().convertToInt(context.getResolver().getPUSHArg(context.getOpcodeList().get(context.getOpcodeList().size() - 2))).intValue()) {
                                        context.getBlockTracing().appendCode(" => @Block: " + b.getOffset());
                                    }
                                }
                                context.getBlockTracing().appendCode(" ELSE @Block: " + context.getResolver().convertToInt(context.getResolver().getPUSHArg(context.getOpcodeList().get(context.getOpcodeList().size() - 2))) + "\n");
                            }
                        }
                    }
                }
            }
        }
    }
}

class StopHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {}
}

class JumpDestHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {}
}

class InvalidHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {}
}

class CodeCopyHandler implements OpcodeHandler {
    @Override
    public void handle (ExecutionContext context) {
        String destOffset = context.getStack().remove(context.getStack().size() - 1);
        String offset =  context.getStack().remove(context.getStack().size() - 1);
        String size =  context.getStack().remove(context.getStack().size() - 1);
    }
}