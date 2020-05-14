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
import parseTree.Contract;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.BasicBlockType;
import parseTree.cfg.Cfg;
import rebuiltabi.fields.RebuiltIOElement;
import rebuiltabi.fields.RebuiltSolidityType;
import rebuiltabi.fields.RebuiltSolidityTypeID;
import utils.Message;

import java.util.HashSet;
import java.util.Stack;

/**
 * We consider only functions with their simplified types and the fallback
 */
public class AbiExtractor {
    public static RebuiltAbi getAbiFromContract(Contract src){
        RebuiltAbi rebuiltAbi = new RebuiltAbi();

        src.getRuntimeCfg().forEach(basicBlock -> {
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
                // Pattern 1: DUP1, PUSH3, EQ, *, JUMPI
                // If the hash starts with 00 then solc uses a PUSH3 instead of a PUSH4
                if (basicBlock.checkPattern(new DupOpcode(0, 1), new PushOpcode(0, 3),
                        new EQOpcode(0), null, new JumpIOpcode(0))) {
                    String hash = "0x00" + basicBlock.getOpcodes().get(basicBlock.getOpcodes().size() - 4).getBytes().substring(2);
                    rebuiltAbi.addFunction(parseFunction(basicBlock, hash));
                }
            } else if (basicBlock.getType() == BasicBlockType.FALLBACK)
                rebuiltAbi.addFunction(parseFallback(src.getRuntimeCfg(), basicBlock));
        });

        return rebuiltAbi;
    }

    private static RebuiltAbiFunction parseFunction(BasicBlock root, String hash){
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
        HashSet<BasicBlock> visited = new HashSet<>();
        queue.add(firstArgumentBlock);

        while (! queue.isEmpty()) {
            BasicBlock current = queue.pop();
            visited.add(current);
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
                    // TODO consider fixed size arrays e.g. address[3]
                    try {
                        rebuiltAbiFunction.popInput();
                        rebuiltAbiFunction.popInput();
                    } catch (IndexOutOfBoundsException e){
                        Message.printWarning("Popping nonexistent input; probably there is a fixed size array");
                    }
                    argumentCount-=2;
                    rebuiltAbiFunction.addInput(new RebuiltIOElement(argumentCount, new RebuiltSolidityType(RebuiltSolidityTypeID.COMPLEX)));
                    argumentCount++;
                }
            }

            // Add children
            current.getSuccessors().forEach(successor -> {
                if (successor.getType() == BasicBlockType.DISPATCHER)
                    if (visited.contains(successor))
                    queue.push(successor);
            });
        }

        return rebuiltAbiFunction;
    }

    private static RebuiltAbiFunction parseFallback(Cfg cfg, BasicBlock root){
        return new RebuiltAbiFunction("", FunctionType.FALLBACK);
    }


}
