package decompiler;

import opcodes.Opcode;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.BasicBlockType;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InstructionResolver {
    private EVMemoryStructure memory;                        // EVM Memory Structure simulator
    private Map<String, String> storage = new HashMap<>();   // EVM Storage Structure simulator
    private ArrayList<String> stack = new ArrayList<>();     // EVM Stack Structure simulator
    Map<Long, Integer> visitBlocksCounter = new HashMap<>();
    boolean checkingLocalVariableInStack;
    public Map<String, List<String>> arrayLocations = new HashMap<>();

    Map<String, String> typeInferences = new HashMap<>();
    List<String> events = new ArrayList<>();
    List<Integer> functionArguments;

    boolean needToManageIf = false;
    boolean checkOfCallMethod = false;

    public final List<BasicBlockType> READABLE_BLOCKS = Arrays.asList(BasicBlockType.CODE, BasicBlockType.DISPATCHER, BasicBlockType.FALLBACK, BasicBlockType.ENTRY);
    public final List<BasicBlockType> ANALYZABLE_BLOCKS = Arrays.asList(BasicBlockType.CODE, BasicBlockType.FALLBACK);
    public final List<String> ABSTRACT_VALUES = Arrays.asList("_storage", "msg", "_arg", "_localVar", "this");
    public final List<String> COMPARE_SIGNS = Arrays.asList(">", "<", "==", "!=", ">=", "<=");
    public final String ADDRESS_MASK = "0xffffffffffffffffffffffffffffffffffffffff";

    // Values to avoid in Solidity 0.8.x because they are security checks
    public final List<String> AVOID_REVERT_VALUES = Arrays.asList(
            "0x4e487b7100000000000000000000000000000000000000000000000000000000"
    );

    private final Map<String, OpcodeHandler> handlers = new HashMap<>();

    public InstructionResolver() {
        initializeHandlers();
    }

    private void initializeHandlers() {
        handlers.put("POP", new PopHandler());
        handlers.put("SLOAD", new SloadHandler());
        handlers.put("SSTORE", new SstoreHandler());
        handlers.put("MLOAD", new MloadHandler());
        handlers.put("MSTORE", new MstoreHandler());
        handlers.put("JUMP", new JumpHandler());
        handlers.put("JUMPI", new JumpiHandler());
        handlers.put("REVERT", new RevertHandler());
        handlers.put("ADD", new OperationHandler());
        handlers.put("SUB", new OperationHandler());
        handlers.put("MUL", new OperationHandler());
        handlers.put("DIV", new OperationHandler());
        handlers.put("SDIV", new OperationHandler());
        handlers.put("MOD", new OperationHandler());
        handlers.put("SMOD", new OperationHandler());
        handlers.put("AND", new LogicHandler());
        handlers.put("OR", new LogicHandler());
        handlers.put("XOR", new LogicHandler());
        handlers.put("NOT", new NotHandler());
        handlers.put("LT", new CompareHandler());
        handlers.put("GT", new CompareHandler());
        handlers.put("SLT", new CompareHandler());
        handlers.put("SGT", new CompareHandler());
        handlers.put("EQ", new CompareHandler());
        handlers.put("NEQ", new CompareHandler());
        handlers.put("EXP", new ExpHandler());
        handlers.put("RETURN", new ReturnHandler());
        handlers.put("RETURNDATASIZE", new ReturnDataSizeHandler());
        handlers.put("RETURNDATACOPY", new ReturnDataCopyHandler());
        handlers.put("GAS", new GasHandler());
        handlers.put("CALL", new CallHandler());
        handlers.put("CALLDATALOAD", new CallDataLoadHandler());
        handlers.put("CALLDATASIZE", new CallDataSizeHandler());
        handlers.put("CALLDATACOPY", new CallDataCopyHandler());
        handlers.put("CALLVALUE", new CallValueHandler());
        handlers.put("CALLER", new CallerHandler());
        handlers.put("SHA3", new Sha3Handler());
        handlers.put("SHR", new ShrHandler());
        handlers.put("SHL", new ShlHandler());
        handlers.put("ADDRESS", new AddressHandler());
        handlers.put("BALANCE", new BalanceHandler());
        handlers.put("JUMPDEST", new JumpDestHandler());
        handlers.put("STOP", new StopHandler());
        handlers.put("INVALID", new InvalidHandler());
        handlers.put("ISZERO", new IsZeroHandler());
        handlers.put("CODECOPY", new CodeCopyHandler());

        for (int i = 1; i <= 32; i++) {
            handlers.put("PUSH" + i, new PushHandler());
        }

        for (int i = 1; i <= 16; i++) {
            handlers.put("DUP" + i, new DupHandler());
            handlers.put("SWAP" + i, new SwapHandler());
        }

        for (int i = 0; i <= 4; i++) {
            handlers.put("LOG" + i, new LogHandler());
        }
    }

    /**
     * Resolve all possible paths and gets instructions from opcodes found.
     * @param paths
     * @return List of Block Trace
     */
    public List<BlockTracing> resolve(List<List<BasicBlock>> paths, Map<String, String> inferences, List<String> eventDeclarations) {
        List<BlockTracing> relations = new ArrayList<>();
        typeInferences.putAll(inferences);
        // Hash of Known Events
        KnownHashEvent.init();

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

        for (int a = 0; a < events.size(); a++) {
            if (!eventDeclarations.contains(events.get(a))) {
                eventDeclarations.add(events.get(a));
            }
        }
        events.clear();

        return relations;
    }

    private void resolvePath(List<BasicBlock> path, List<BlockTracing> relations) {
        memory = new EVMemoryStructure();
        storage.clear();
        stack.clear();

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
        String opcodeName = opcode.toString().split(" ")[1];
        OpcodeHandler handler = handlers.get(opcodeName);

        if (handler != null) {
            ExecutionContext context = new ExecutionContext(
                    opcode, opcodeList, opcodeIndex, block, blockTracing,
                    this.stack, this.storage, this.memory, this.typeInferences,
                    this.visitBlocksCounter, this.functionArguments, this.events,
                    this.arrayLocations, this
            );

            handler.handle(context);

            this.needToManageIf = context.isNeedToManageIf();
            this.checkOfCallMethod = context.isCheckOfCallMethod();
        } else {
            System.err.println("Warning: handler not found for opcode: " + opcodeName);
        }
    }

    public boolean containsInstruction(Opcode opcode, String instruction) {
        return opcode.toString().contains(instruction);
    }

    public String isOperationInstruction(Opcode opcode) {
        String opStr = opcode.toString().split(" ")[1];
        switch (opStr) {
            case "ADD": return "+";
            case "SUB": return "-";
            case "MUL": return "*";
            case "DIV": return "/";
            default: return "??";
        }
    }

    public String isCompareInstruction(Opcode opcode) {
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

    public String getTypeFromOperation(Opcode opcode) {
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

    public String isLogicInstruction(Opcode opcode) {
        String opStr = opcode.toString().split(" ")[1];
        switch (opStr) {
            case "AND": return "&&";
            case "OR": return "||";
            default: return "??";
        }
    }

    public String isArrayElement(String value) {
        for (Map.Entry<String, List<String>> entry : arrayLocations.entrySet()) {
            for (String en : entry.getValue()) {
                if (doesNotContainAbstractValue(value) && convertToInt(value).equals(convertToInt(en)))
                    return entry.getKey();
            }
        }
        return null;
    }

    public void updateArrayTypeElements(String key, String type) {
        for (Map.Entry<String, String> entry : typeInferences.entrySet()) {
            if (entry.getKey().startsWith(key + "[")) {
                typeInferences.put(entry.getKey(), type);
            }
        }
    }

    public boolean doesNotContainAbstractValue(String value) {
        for (String abstractValue : ABSTRACT_VALUES) {
            if (value.contains(abstractValue)) { return false; }
        }
        return true;
    }

    public String checkStructPattern(String value) {
        final Pattern pattern = Pattern.compile("([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\+\\s*(\\d+)\\s*\\*\\s*([a-zA-Z_][a-zA-Z0-9_]*)\\s*\\+\\s*(\\d+)");
        Matcher matcher = pattern.matcher(value);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String storageName = matcher.group(1);
            String indexName = matcher.group(3);
            String offset = matcher.group(4);
            String replacement = String.format("%s[%s].el%s", storageName, indexName, offset);
            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    public boolean doesNotContainCompareSigns(String value) {
        for (String compareSign : COMPARE_SIGNS) {
            if (value.contains(compareSign)) { return false; }
        }
        return true;
    }

    // Control if one of the operands is a neutral element (var + 0, var * 1, ...)
    public String isNeutralOperation(String first, String second, String sign) {
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

    public BigInteger calculate(String sign, BigInteger firstOperandInt, BigInteger secondOperandInt) {
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

    public String getPUSHArg(Opcode opcode) {
        return opcode.toString().split(" ")[2];
    }

    public String hexToString(String hex) {
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

        //typeInferences.put("\"" + sb + "\"", "string");
        return sb.toString();
    }

    public Integer getInstructionIndex(Opcode opcode, int offset) {
        return Integer.parseInt(opcode.toString().split(" ")[1].substring(offset));
    }

    public BigInteger convertToInt(String value) {
        return new BigInteger(value.substring(2), 16);
    }

    public boolean hasAReturnType(String value) {
        if (value.contains("storage") || value.contains("this") || value.contains("_arg"))
            return true;

        else return doesNotContainAbstractValue(value);
    }

}
