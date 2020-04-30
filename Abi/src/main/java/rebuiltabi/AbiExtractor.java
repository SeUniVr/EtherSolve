package rebuiltabi;

import abi.fields.FunctionType;
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
            if (basicBlock.getType() == BasicBlockType.FALLBACK)
                System.out.println(basicBlock.getOffset() + " <- FALLBACK");
            if (basicBlock.getType() == BasicBlockType.DISPATCHER) {
                // Pattern 1: DUP1, PUSH4, EQ, *, JUMPI
                if (basicBlock.checkPattern(new DupOpcode(0, 1), new PushOpcode(0, 4),
                        new EQOpcode(0), null, new JumpIOpcode(0))) {
                    String hash = "0x" + basicBlock.getOpcodes().get(basicBlock.getOpcodes().size() - 4).getBytes().substring(2);
                    rebuiltAbi.addFunction(parseFunction(basicBlock, hash));
                }
                // Pattern 2: PUSH4, DUP2, EQ, *, JUMPI
                else if (basicBlock.checkPattern(new PushOpcode(0, 4), new DupOpcode(0, 2),
                        new EQOpcode(0), null, new JumpIOpcode(0))) {
                    String hash = "0x" + basicBlock.getOpcodes().get(basicBlock.getOpcodes().size() - 5).getBytes().substring(2);
                    rebuiltAbi.addFunction(parseFunction(basicBlock, hash));
                }
            } else if (basicBlock.getType() == BasicBlockType.FALLBACK)
                rebuiltAbi.addFunction(parseFallback(src.getRuntimeCfg(), basicBlock));
        });

        return rebuiltAbi;
    }

    private static RebuiltAbiFunction parseFunction(BasicBlock root, String hash){
        System.out.println("Parsing " + hash);
        // Get hash and initialize
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
