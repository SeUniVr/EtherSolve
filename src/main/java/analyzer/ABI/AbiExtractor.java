package analyzer.ABI;

import analyzer.ABI.fields.FunctionType;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.EQOpcode;
import opcodes.controlFlowOpcodes.JumpIOpcode;
import opcodes.stackOpcodes.DupOpcode;
import opcodes.stackOpcodes.PushOpcode;
import parseTree.BasicBlock;
import parseTree.Contract;

public class AbiExtractor {
    private static AbiExtractor ilSooEUnico = new AbiExtractor();

    private AbiExtractor() {
    }

    public static AbiExtractor getInstance() {
        return ilSooEUnico;
    }

    public Abi extractAbi(Contract contract) {
        Abi abi = new Abi();

        contract.getRuntimeCfg().forEach(basicBlock -> {
            if (basicBlock.checkPattern(new DupOpcode(0, 1), new PushOpcode(0, 4),
                                        new EQOpcode(0), null, new JumpIOpcode(0)))
                parseFunction(basicBlock, abi);
        });

        return abi;
    }

    private void parseFunction(BasicBlock basicBlock, Abi abi) {
        String name = "0x" + ((PushOpcode) basicBlock.getOpcodes().get(basicBlock.getOpcodes().size() - 4)).getParameter().toString(16);
        AbiFunction function = new AbiFunction(name, FunctionType.FUNCTION);

        abi.addFunction(function);
    }

}
