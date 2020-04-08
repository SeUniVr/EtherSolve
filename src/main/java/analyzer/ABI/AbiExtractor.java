package analyzer.ABI;

import analyzer.ABI.fields.FunctionType;
import analyzer.ABI.fields.IOElement;
import analyzer.ABI.fields.SolidityType;
import opcodes.Opcode;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.AndOpcode;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.EQOpcode;
import opcodes.arithmeticOpcodes.unaryArithmeticOpcodes.IsZeroOpcode;
import opcodes.controlFlowOpcodes.JumpIOpcode;
import opcodes.environmentalOpcodes.CallDataCopyOpcode;
import opcodes.environmentalOpcodes.CallDataLoadOpcode;
import opcodes.stackOpcodes.DupOpcode;
import opcodes.stackOpcodes.PushOpcode;
import parseTree.BasicBlock;
import parseTree.BasicBlockType;
import parseTree.Contract;

import java.util.Stack;

public class AbiExtractor {
    private static AbiExtractor ilSoloEUnico = new AbiExtractor();

    private AbiExtractor() {
    }

    public static AbiExtractor getInstance() {
        return ilSoloEUnico;
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
        AbiFunction abiFunction = new AbiFunction(name, FunctionType.FUNCTION);

        parseInputs(basicBlock, abiFunction);

        abi.addFunction(abiFunction);
    }

    private void parseInputs(BasicBlock basicBlock, AbiFunction abiFunction) {
        // Perform a DFS on dispatcher blocks child of basicBlock
        // Count CallDataLoad and CallDataCopy
        int argumentCount = 0;
        Stack<BasicBlock> queue = new Stack<>();
        // The first block is the one with higher offset
        long maxOffset = 0;
        BasicBlock firstArgumentBlock = null;
        for (BasicBlock candidate : basicBlock.getChildren()){
            if (candidate.getOffset() > maxOffset){
                maxOffset = candidate.getOffset();
                firstArgumentBlock = candidate;
            }
        }
        queue.add(firstArgumentBlock);
        while (! queue.isEmpty()) {
            BasicBlock current = queue.pop();
            // Count
            for (int i = 0; i < current.getOpcodes().size(); i++){
                Opcode opcode = current.getOpcodes().get(i);
                if (opcode instanceof CallDataLoadOpcode){
                    SolidityType inputType = SolidityType.UINT256;
                    // Calculates the type
                    // Case 1: after the load there is a push20 0xffff...; AND
                    if (current.getOpcodes().get(i+1) instanceof PushOpcode && current.getOpcodes().get(i+2) instanceof AndOpcode)
                        inputType = SolidityType.ADDRESS;
                    else if (current.getOpcodes().get(i+1) instanceof IsZeroOpcode)
                        inputType = SolidityType.BOOL;

                    abiFunction.addInput(new IOElement("arg_"+argumentCount, inputType));
                    argumentCount++;
                }
                else if (opcode instanceof CallDataCopyOpcode){
                    abiFunction.popInput();
                    abiFunction.popInput();
                    argumentCount-=2;
                    abiFunction.addInput(new IOElement("arg_"+argumentCount, SolidityType.BYTES));
                    argumentCount++;
                }
            }

            // Add children
            current.getChildren().forEach(child -> {
                if (child.getType() == BasicBlockType.DISPATCHER)
                    queue.push(child);
            });
        }

    }

}
