package rebuiltabi;

import abi.fields.FunctionType;
import comparation.AbiComparator;
import opcodes.*;
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
import parseTree.Cfg;
import parseTree.Contract;
import rebuiltabi.fields.RebuiltIOElement;
import rebuiltabi.fields.RebuiltSolidityType;
import rebuiltabi.fields.RebuiltSolidityTypeID;

import java.util.Stack;

/**
 * We consider only functions with their simplified types and the fallback
 */
public class AbiExtractor {
    public static RebuiltAbi getAbiFromContract(Contract src){
        RebuiltAbi rebuiltAbi = new RebuiltAbi();

        src.getRuntimeCfg().forEach(basicBlock -> {
            if (basicBlock.checkPattern(new DupOpcode(0, 1), new PushOpcode(0, 4),
                    new EQOpcode(0), null, new JumpIOpcode(0)))
                rebuiltAbi.addFunction(parseFunction(src.getRuntimeCfg(), basicBlock));
            else if (basicBlock.getType() == BasicBlockType.FALLBACK)
                rebuiltAbi.addFunction(parseFallback(src.getRuntimeCfg(), basicBlock));
        });

        return rebuiltAbi;
    }

    private static RebuiltAbiFunction parseFunction(Cfg cfg, BasicBlock root){
        // Get hash and initialize
        String hash = "0x" + root.getOpcodes().get(root.getOpcodes().size() - 4).getBytes().substring(2);
        RebuiltAbiFunction rebuiltAbiFunction = new RebuiltAbiFunction(hash, FunctionType.FUNCTION);

        // The first block is the successor with higher offset
        long maxOffset = 0;
        BasicBlock firstArgumentBlock = null;
        for (BasicBlock candidate : root.getSuccessors()){
            if (candidate.getOffset() > maxOffset){
                maxOffset = candidate.getOffset();
                firstArgumentBlock = candidate;
            }
        }

        // DFS
        int argumentCount = 0;
        Stack<BasicBlock> queue = new Stack<>();
        queue.add(firstArgumentBlock);

        while (! queue.isEmpty()) {
            BasicBlock current = queue.pop();
            // Count
            for (int i = 0; i < current.getOpcodes().size(); i++){
                Opcode opcode = current.getOpcodes().get(i);
                // Simple type
                if (opcode instanceof CallDataLoadOpcode){
                    int length = 256;
                    // Calculates the length
                    // Case 1: after the load there is a "PUSH-n 0xffff...; AND"
                    if (current.getOpcodes().get(i+1) instanceof PushOpcode && current.getOpcodes().get(i+2) instanceof AndOpcode){
                        PushOpcode pushOpcode = (PushOpcode) current.getOpcodes().get(i+1);
                        length = pushOpcode.getParameterLength() * 8;
                    }
                    // Case 2: after the load there is an IsZero
                    else if (current.getOpcodes().get(i+1) instanceof IsZeroOpcode)
                        length = 1;

                    RebuiltSolidityType inputType = new RebuiltSolidityType(RebuiltSolidityTypeID.SIMPLE, length);
                    rebuiltAbiFunction.addInput(new RebuiltIOElement(argumentCount, inputType));
                    argumentCount++;
                }
                // Complex type
                else if (opcode instanceof CallDataCopyOpcode){
                    rebuiltAbiFunction.popInput();
                    rebuiltAbiFunction.popInput();
                    argumentCount-=2;
                    rebuiltAbiFunction.addInput(new RebuiltIOElement(argumentCount, new RebuiltSolidityType(RebuiltSolidityTypeID.COMPLEX)));
                    argumentCount++;
                }
            }

            // Add children
            current.getSuccessors().forEach(child -> {
                if (child.getType() == BasicBlockType.DISPATCHER)
                    queue.push(child);
            });
        }

        return rebuiltAbiFunction;
    }

    private static RebuiltAbiFunction parseFallback(Cfg cfg, BasicBlock root){
        return new RebuiltAbiFunction("", FunctionType.FALLBACK);
    }


}
