package abi;

import abi.fields.FunctionType;
import abi.fields.IOElement;
import abi.fields.SolidityType;
import abi.fields.SolidityTypeID;
import com.google.gson.*;
import gson.GsonAbi;
import opcodes.Opcode;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.AndOpcode;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.EQOpcode;
import opcodes.arithmeticOpcodes.unaryArithmeticOpcodes.IsZeroOpcode;
import opcodes.controlFlowOpcodes.JumpIOpcode;
import opcodes.environmentalOpcodes.CallDataCopyOpcode;
import opcodes.environmentalOpcodes.CallDataLoadOpcode;
import opcodes.stackOpcodes.DupOpcode;
import opcodes.stackOpcodes.MStoreOpcode;
import opcodes.stackOpcodes.PushOpcode;
import parseTree.BasicBlock;
import parseTree.BasicBlockType;
import parseTree.Cfg;
import parseTree.Contract;

import java.util.Stack;

public class AbiExtractor {
    private static AbiExtractor ilSoloEUnico = new AbiExtractor();

    private AbiExtractor() {
    }

    public static AbiExtractor getInstance() {
        return ilSoloEUnico;
    }

    /**
     * Extract the Abi from the Json String
     * @param abiString Json string representing the Abi
     * @return instance of class Abi
     */
    public Abi extractAbi(String abiString){
        return new GsonAbi().fromJson(abiString, Abi.class);
    }

    /**
     * Extract an Abi from a contract
     * @param contract source
     * @return Abi of the contract
     */
    public Abi extractAbi(Contract contract) {
        // TODO consider constructor
        Abi abi = new Abi();

        contract.getRuntimeCfg().forEach(basicBlock -> {
            if (basicBlock.checkPattern(new DupOpcode(0, 1), new PushOpcode(0, 4),
                                        new EQOpcode(0), null, new JumpIOpcode(0)))
                parseFunction(contract.getRuntimeCfg(), basicBlock, abi);
            else if (basicBlock.getType() == BasicBlockType.FALLBACK)
                parseFallback(contract.getRuntimeCfg(), basicBlock, abi);
        });

        return abi;
    }

    private void parseFunction(Cfg cfg, BasicBlock basicBlock, Abi abi) {
        String name = "0x" + basicBlock.getOpcodes().get(basicBlock.getOpcodes().size() - 4).getBytes().substring(2);
        AbiFunction abiFunction = new AbiFunction(name, FunctionType.FUNCTION);

        long returnBlockOffset = parseInputsAndReturnLastOffset(basicBlock, abiFunction);
        BasicBlock closureBlock = cfg.getNextBasicBlock(returnBlockOffset);
        parseOutput(closureBlock, abiFunction, cfg);

        abi.addFunction(abiFunction);
    }

    private long parseInputsAndReturnLastOffset(BasicBlock basicBlock, AbiFunction abiFunction) {
        // Perform a DFS on dispatcher blocks child of basicBlock
        // Count CallDataLoad and CallDataCopy
        int argumentCount = 0;
        long lastOffset = 0;
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

        // Real DFS
        while (! queue.isEmpty()) {
            BasicBlock current = queue.pop();
            // Check if it's the last block of the dispatcher
            if (current.getOffset() > lastOffset)
                lastOffset = current.getOffset();

            // Count
            for (int i = 0; i < current.getOpcodes().size(); i++){
                Opcode opcode = current.getOpcodes().get(i);
                if (opcode instanceof CallDataLoadOpcode){
                    SolidityType inputType = new SolidityType(SolidityTypeID.INT, 256);
                    // Calculates the type
                    // Case 1: after the load there is a push20 0xffff...; AND
                    if (current.getOpcodes().get(i+1) instanceof PushOpcode && current.getOpcodes().get(i+2) instanceof AndOpcode){
                        PushOpcode pushOpcode = (PushOpcode) current.getOpcodes().get(i+1);
                        if (pushOpcode.getParameterLength() == 20)
                            inputType = new SolidityType(SolidityTypeID.ADDRESS);
                        else
                            inputType = new SolidityType(SolidityTypeID.INT, pushOpcode.getParameterLength() * 8);
                    }
                    else if (current.getOpcodes().get(i+1) instanceof IsZeroOpcode)
                        inputType = new SolidityType(SolidityTypeID.BOOL);

                    abiFunction.addInput(new IOElement("arg_"+argumentCount, inputType));
                    argumentCount++;
                }
                else if (opcode instanceof CallDataCopyOpcode){
                    abiFunction.popInput();
                    abiFunction.popInput();
                    argumentCount-=2;
                    abiFunction.addInput(new IOElement("arg_"+argumentCount, new SolidityType(SolidityTypeID.BYTES)));
                    argumentCount++;
                }
            }

            // Add children
            current.getChildren().forEach(child -> {
                if (child.getType() == BasicBlockType.DISPATCHER)
                    queue.push(child);
            });
        }

        return lastOffset;
    }

    private void parseOutput(BasicBlock basicBlock, AbiFunction abiFunction, Cfg cfg) {
        int argumentCount = 0;
        Stack<BasicBlock> queue = new Stack<>();
        queue.push(basicBlock);

        while (! queue.isEmpty()) {
            BasicBlock current = queue.pop();
            boolean addChildren = true;

            for (int i = 0; i < current.getOpcodes().size(); i++) {
                Opcode opcode = current.getOpcodes().get(i);

                if (opcode instanceof MStoreOpcode){
                    SolidityType outputType = new SolidityType(SolidityTypeID.INT, 256);

                    if (current.getOpcodes().get(i-2) instanceof AndOpcode && current.getOpcodes().get(i-3) instanceof PushOpcode){
                        PushOpcode pushOpcode = (PushOpcode) current.getOpcodes().get(i-3);
                        if (pushOpcode.getParameterLength() == 20)
                            outputType = new SolidityType(SolidityTypeID.ADDRESS);
                        else
                            outputType = new SolidityType(SolidityTypeID.INT, pushOpcode.getParameterLength() * 8);
                    }
                    else if (current.getOpcodes().get(i-2) instanceof IsZeroOpcode)
                        outputType = new SolidityType(SolidityTypeID.BOOL);

                    abiFunction.addOutput(new IOElement("arg_"+argumentCount, outputType));
                    argumentCount++;

                } else if (opcode instanceof JumpIOpcode){
                    // Change the type to BYTES and skip the next 4 blocks
                    abiFunction.popOutput();
                    abiFunction.addOutput(new IOElement("arg_" + (argumentCount-1), new SolidityType(SolidityTypeID.BYTES)));

                    // Skip 4 blocks
                    current = cfg.getNextBasicBlock(current.getOffset());
                    current = cfg.getNextBasicBlock(current.getOffset());
                    current = cfg.getNextBasicBlock(current.getOffset());
                    current = cfg.getNextBasicBlock(current.getOffset());
                    queue.push(current);
                    addChildren = false;
                }
            }

            // Add children
            if (addChildren)
                current.getChildren().forEach(child -> {
                    if (child.getType() == BasicBlockType.DISPATCHER)
                        queue.push(child);
                });
        }
    }

    private void parseFallback(Cfg runtimeCfg, BasicBlock basicBlock, Abi abi){
        abi.addFunction(new AbiFunction("", FunctionType.FALLBACK));
    }
}
