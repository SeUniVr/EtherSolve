package rebuiltabi;

import abi.Abi;
import abi.AbiFunction;
import abi.HashDB;
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

import java.util.*;

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
                    System.err.println(basicBlock.getFunctions());
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

                if (basicBlock.getFunctions().isEmpty()) {
                    rebuiltAbi.addFunction(parseUnknownDispatcher(src, basicBlock));
                }

            } else if (basicBlock.getType() == BasicBlockType.FALLBACK) {
                rebuiltAbi.addFunction(parseFallback(src.getRuntimeCfg(), basicBlock));
            }

        });

        return rebuiltAbi;
    }

    private static RebuiltAbiFunction parseFunction(BasicBlock root, String hash){
        // Get hash and initialize
        RebuiltAbiFunction rebuiltAbiFunction = new RebuiltAbiFunction(hash, FunctionType.FUNCTION);

        // Resolution of Solidity method name by using 4byte.directory
        try {
            String resolvedSignature = HashDB.getInstance().getSignatureFromHash(hash);
            String methodName = "";
            if (resolvedSignature != null)
                methodName = resolvedSignature.split("\\(")[0];
            else {
                methodName = "unknown_function_" + hash;
            }
            rebuiltAbiFunction.setResolvedSignature(methodName);

        } catch (Exception e) {
            System.err.println("Error during the hash resolution " + hash + ": " + e.getMessage());
        }

        // The first block is the successor with higher offset
        long maxOffset = 0;
        BasicBlock firstArgumentBlock = null;
        for (BasicBlock candidate : root.getSuccessors()){
            if (candidate.getOffset() > maxOffset){
                maxOffset = candidate.getOffset();
                firstArgumentBlock = candidate;
            }
        }

        // Set offset entry point
        if (firstArgumentBlock != null) {
            rebuiltAbiFunction.setEntryPointOffset(firstArgumentBlock.getOffset());
        }

        // DFS
        int argumentCount = 0;
        Stack<BasicBlock> queue = new Stack<>();
        HashSet<BasicBlock> visited = new HashSet<>();
        queue.add(firstArgumentBlock);

        int distance = 0;
        HashMap<BasicBlock, Integer> distances = new HashMap<>();
        distances.put(firstArgumentBlock, 0);

        while (! queue.isEmpty()) {
            BasicBlock current = queue.pop();
            visited.add(current);

            current.addFunction(rebuiltAbiFunction.getResolvedSignature());
            current.setDistanceForFunction(rebuiltAbiFunction.getResolvedSignature(), distances.get(current));

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
                // LOOP CONDITION!
                /**
                if (successor.getType() == BasicBlockType.DISPATCHER)
                    if (visited.contains(successor))
                        queue.push(successor);
                **/
                if (!visited.contains(successor)) {
                    queue.push(successor);
                    distances.put(successor, distances.get(current) + 1);
                }
            });
        }

        return rebuiltAbiFunction;
    }

    private static RebuiltAbiFunction parseUnknownDispatcher(Contract src, BasicBlock unknownBlock) {
      BasicBlock entryProgramPoint = src.getRuntimeCfg().getEntryPoint();

      Map<BasicBlock, Integer> distances = new HashMap<>();
      Queue<BasicBlock> queue = new LinkedList<>();
      queue.add(entryProgramPoint);
      distances.put(entryProgramPoint, 0);

      while (! queue.isEmpty()) {
          BasicBlock current = queue.poll();
          int currentDistance =  distances.get(current);

          for (BasicBlock succ : current.getSuccessors()) {
              if (!distances.containsKey(succ)) {
                  distances.put(succ, currentDistance+ 1);
                  queue.add(succ);
              }
          }
      }

      String functionName = "no_name";
      int distance = distances.getOrDefault(unknownBlock, -1);

      unknownBlock.addFunction(functionName);
      unknownBlock.setDistanceForFunction(functionName, distance);

      RebuiltAbiFunction func = new RebuiltAbiFunction("", FunctionType.FUNCTION);
      func.setResolvedSignature(functionName);
      func.setEntryPointOffset(unknownBlock.getOffset());

      return func;
    }

    private static RebuiltAbiFunction parseFallback(Cfg cfg, BasicBlock fallbackBlock) {
        RebuiltAbiFunction func = new RebuiltAbiFunction("", FunctionType.FALLBACK);
        func.setResolvedSignature("fallback_" + fallbackBlock.getOffset());
        func.setEntryPointOffset(fallbackBlock.getOffset());

        Stack<BasicBlock> stack = new Stack<>();
        Set<BasicBlock> visited = new HashSet<>();
        stack.push(fallbackBlock);
        Map<BasicBlock, Integer> distances = new HashMap<>();
        distances.put(fallbackBlock, 0);

        while (!stack.isEmpty()) {
            BasicBlock current = stack.pop();
            if (visited.contains(current)) continue;

            visited.add(current);
            current.addFunction(func.getResolvedSignature());
            current.setDistanceForFunction(func.getResolvedSignature(), distances.get(current));

            for (BasicBlock succ : current.getSuccessors()) {
                if (!visited.contains(succ)) {
                    stack.push(succ);
                    distances.put(succ, distances.get(current) + 1);
                }
            }
        }
        return func;
    }

}
