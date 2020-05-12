package parseTree;

import parseTree.SymbolicExecution.SymbolicExecutionStack;

import java.io.PrintWriter;
import java.io.StringWriter;

public class CfgBuildReport {

    private int directJumpTargetErrors;
    private int orphanJumpTargetNullErrors;
    private int orphanJumpTargetUnknownErrors;
    private int loopDepthExceededErrors;
    private int multipleRootNodesErrors;
    private int stackExceededErrors;
    private int criticalErrors;
    private final StringBuilder errorLog;

    public CfgBuildReport(){
        directJumpTargetErrors = 0;
        orphanJumpTargetNullErrors = 0;
        orphanJumpTargetUnknownErrors = 0;
        loopDepthExceededErrors = 0;
        multipleRootNodesErrors = 0;
        stackExceededErrors = 0;
        criticalErrors = 0;
        errorLog = new StringBuilder();
    }

    public void addDirectJumpError(long sourceOffset, long destinationOffset) {
        directJumpTargetErrors++;
        //Message.printError(String.format("Direct jump unresolvable at %d, block %d does not exists", sourceOffset, destinationOffset));
        errorLog.append(String.format("Direct jump unresolvable at %d, block %d does not exists\n", sourceOffset, destinationOffset));
    }

    public void addOrphanJumpTargetNullError(long offset, long nextOffset) {
        orphanJumpTargetNullErrors++;
        //Message.printError(String.format("Orphan jump unresolvable at %d, block %d does not exists", offset, nextOffset));
        errorLog.append(String.format("Orphan jump unresolvable at %d, block %d does not exists\n", offset, nextOffset));
    }

    public void addOrphanJumpTargetUnknownError(long offset, SymbolicExecutionStack stack) {
        orphanJumpTargetUnknownErrors++;
        //Message.printError(String.format("Orphan jump unresolvable at %d, symbolic execution found an unknown value\n%s", offset, stack));
        errorLog.append(String.format("Orphan jump unresolvable at %d, symbolic execution found an unknown value\nStack: %s\n", offset, stack));
    }

    public void addLoopDepthExceededError(long offset) {
        loopDepthExceededErrors++;
        //Message.printWarning(String.format("Loop depth exceeded at %d", offset));
        errorLog.append(String.format("Loop depth exceeded at %d\n", offset));
    }

    public void addMultipleRootNodesError(int trees) {
        multipleRootNodesErrors++;
        //Message.printWarning(String.format("Warning: the CFG has %d root nodes (a.k.a. nodes without predecessors)", trees));
        errorLog.append(String.format("Warning: the CFG has %d root nodes (a.k.a. nodes without predecessors)\n", trees));
    }

    public void addStackExceededError(long offset) {
        stackExceededErrors++;
        //Message.printWarning(String.format("Symbolic stack exceeded the limit at %d", offset));
        errorLog.append(String.format("Symbolic stack exceeded the limit at %d\n", offset));
    }

    public void addCriticalError(Exception exception) {
        criticalErrors++;
        errorLog.append("Critical error: ");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        errorLog.append(sw.toString());
    }

    @Override
    public String toString() {
        return  "Direct jump target errors: " + directJumpTargetErrors +
                "\nOrphan jump target null errors: " + orphanJumpTargetNullErrors +
                "\nOrphan jump target unknown errors: " + orphanJumpTargetUnknownErrors +
                "\nMultiple root nodes errors: " + multipleRootNodesErrors +
                "\nSymbolic execution stack limit exceeded errors: " + stackExceededErrors +
                "\nCritical errors: " + criticalErrors;
    }

    public int getTotalJumpError(){
        return directJumpTargetErrors + orphanJumpTargetUnknownErrors + orphanJumpTargetNullErrors + loopDepthExceededErrors;
    }

    public int getMultipleRootNodesErrors() {
        return multipleRootNodesErrors;
    }

    public String getLog(){
        return errorLog.toString();
    }
}
