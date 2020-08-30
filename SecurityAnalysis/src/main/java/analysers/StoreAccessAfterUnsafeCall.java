package analysers;

import main.SecurityAnalysisReport;
import main.SecurityDetection;
import main.SecurityVulnerability;
import opcodes.Opcode;
import opcodes.stackOpcodes.SLoadOpcode;
import opcodes.stackOpcodes.SStoreOpcode;
import opcodes.systemOpcodes.CallOpcode;
import parseTree.Contract;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.Cfg;

import java.util.HashSet;
import java.util.Stack;

public class StoreAccessAfterUnsafeCall {
    private static final String SLOAD_MESSAGE = "Store access in write mode after an unsafe call";
    private static final String SSTORE_MESSAGE = "Store access in write mode after an unsafe call; this could bring to a re-entrancy vulnerability";


    public static void analyse(Contract contract, SecurityAnalysisReport report) {
        // Get the CFG
        Cfg runtimeCfg = contract.getRuntimeCfg();
        // Gather the blocks containing the CALL opcode
        for (BasicBlock bb : runtimeCfg){
            if (bb.checkPattern(new CallOpcode(0))){
                // Check if it is an unsafe call
                if (isUnsafeCall(bb)){
                    // Check if it can reach an SSTORE or an SLOAD opcode through DFS
                    BasicBlock currentBlock;
                    Stack<BasicBlock> queue = new Stack<>();
                    queue.add(bb);
                    HashSet<BasicBlock> visited = new HashSet<>();
                    while (! queue.isEmpty()){
                        currentBlock = queue.pop();
                        visited.add(currentBlock);

                        if (currentBlock.checkPattern(new SLoadOpcode(0))){
                            Opcode sLoadOpcode = null;
                            for (Opcode o : currentBlock)
                                if (o instanceof SLoadOpcode)
                                    sLoadOpcode = o;
                            report.addDetection(new SecurityDetection(SecurityVulnerability.STORE_READ_AFTER_UNSAFE_CALL, sLoadOpcode, SLOAD_MESSAGE));
                        }
                        if (currentBlock.checkPattern(new SStoreOpcode(0))){
                            Opcode sStoreOpcode = null;
                            for (Opcode o : currentBlock)
                                if (o instanceof SStoreOpcode)
                                    sStoreOpcode = o;
                            report.addDetection(new SecurityDetection(SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL, sStoreOpcode, SSTORE_MESSAGE));
                        }

                        for (BasicBlock successor : currentBlock.getSuccessors())
                            if (! visited.contains(successor))
                                queue.add(successor);
                    }
                }

            }

        }
    }

    private static boolean isUnsafeCall(BasicBlock bb) {
        // TODO implement this with symbolic stack execution
        return true;
    }
}
