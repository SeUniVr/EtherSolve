package parseTree;

import parseTree.SymbolicExecution.SymbolicExecutionStack;
import utils.Message;

public class CfgBuildReport {

    private int directJumpTargetErrors;
    private int orphanJumpTargetNullErrors;
    private int orphanJumpTargetUnknownErrors;
    private int multipleRootNodesError;

    public CfgBuildReport(){
        directJumpTargetErrors = 0;
        orphanJumpTargetNullErrors = 0;
        orphanJumpTargetUnknownErrors = 0;
        multipleRootNodesError = 0;
    }

    public void addDirectJumpError(long sourceOffset, long destinationOffset) {
        directJumpTargetErrors++;
        Message.printError(String.format("Direct jump unresolvable at %d, block %d does not exists", sourceOffset, destinationOffset));
    }

    public void addOrphanJumpTargetNullError(long offset, long nextOffset) {
        orphanJumpTargetNullErrors++;
        Message.printError(String.format("Orphan jump unresolvable at %d, block %d does not exists", offset, nextOffset));
    }

    public void addOrphanJumpTargetUnknownError(long offset, SymbolicExecutionStack stack) {
        orphanJumpTargetUnknownErrors++;
        Message.printError(String.format("Orphan jump unresolvable at %d, symbolic execution found an unknown value\n%s", offset, stack));
    }

    public void addMultipleRootNodesError(int trees) {
        multipleRootNodesError++;
        Message.printWarning(String.format("Warning: the CFG has %d root nodes (a.k.a. nodes without predecessors)", trees));
    }

    @Override
    public String toString() {
        return  "Direct jump target errors: " + directJumpTargetErrors +
                "\nOrphan jump target null errors: " + orphanJumpTargetNullErrors +
                "\nOrphan jump target unknown errors: " + orphanJumpTargetUnknownErrors +
                "\nMultiple root nodes error: " + multipleRootNodesError;
    }
}
