package parseTree.cfg;

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
    private long buildTimeMillis;

    /**
     * Default constructor which sets all values to 0
     */
    public CfgBuildReport(){
        buildTimeMillis = System.currentTimeMillis();
        directJumpTargetErrors = 0;
        orphanJumpTargetNullErrors = 0;
        orphanJumpTargetUnknownErrors = 0;
        loopDepthExceededErrors = 0;
        multipleRootNodesErrors = 0;
        stackExceededErrors = 0;
        criticalErrors = 0;
        errorLog = new StringBuilder();
    }

    /**
     * Adds a direct jump error. They are caused by a jump to an offset which is not a basic block
     * @param sourceOffset offset of the opcode that generated the error
     * @param destinationOffset destination offset for the jump
     */
    public void addDirectJumpError(long sourceOffset, long destinationOffset) {
        directJumpTargetErrors++;
        //Message.printError(String.format("Direct jump unresolvable at %d, block %d does not exists", sourceOffset, destinationOffset));
        errorLog.append(String.format("Direct jump unresolvable at %d, block %d does not exists\n", sourceOffset, destinationOffset));
    }

    /**
     * Adds an orphan jump target null error. They are caused by a jump to an offset which is not a basic block
     * @param sourceOffset offset of the opcode that generated the error
     * @param destinationOffset destination offset for the jump
     */
    public void addOrphanJumpTargetNullError(long sourceOffset, long destinationOffset) {
        orphanJumpTargetNullErrors++;
        //Message.printError(String.format("Orphan jump unresolvable at %d, block %d does not exists", offset, nextOffset));
        errorLog.append(String.format("Orphan jump unresolvable at %d, block %d does not exists\n", sourceOffset, destinationOffset));
    }

    /**
     * Adds an orphan jump target unknown error. They are caused by a jump with an unknown value on the symbolic execution stack
     * @param sourceOffset offset of the opcode that generated the error
     * @param stack current state of the symbolic execution stack
     */
    public void addOrphanJumpTargetUnknownError(long sourceOffset, SymbolicExecutionStack stack) {
        orphanJumpTargetUnknownErrors++;
        //Message.printError(String.format("Orphan jump unresolvable at %d, symbolic execution found an unknown value\n%s", offset, stack));
        errorLog.append(String.format("Orphan jump unresolvable at %d, symbolic execution found an unknown value\nStack: %s\n", sourceOffset, stack));
    }

    /**
     * Adds a loop depth exceeded error. They are caused by the DFS if it becomes too deep
     * @param offset offset of the opcode that generated the error
     */
    public void addLoopDepthExceededError(long offset) {
        loopDepthExceededErrors++;
        //Message.printWarning(String.format("Loop depth exceeded at %d", offset));
        errorLog.append(String.format("Loop depth exceeded at %d\n", offset));
    }

    /**
     * Adds a multiple root nodes error. They are caused by the cfg validator if there are more root nodes (a.k.a. nodes without predecessors)
     * @param trees the number of roots in the cfg
     */
    public void addMultipleRootNodesError(int trees) {
        multipleRootNodesErrors++;
        //Message.printWarning(String.format("Warning: the CFG has %d root nodes (a.k.a. nodes without predecessors)", trees));
        errorLog.append(String.format("Warning: the CFG has %d root nodes (a.k.a. nodes without predecessors)\n", trees));
    }

    /**
     * Adds a stack exceeded error. They are caused if the symbolic execution stack exceeded the 1024 limit
     * @param offset offset of the opcode that generated the error
     */
    public void addStackExceededError(long offset) {
        stackExceededErrors++;
        //Message.printWarning(String.format("Symbolic stack exceeded the limit at %d", offset));
        errorLog.append(String.format("Symbolic stack exceeded the limit at %d\n", offset));
    }

    /**
     * Adds a critical error. They are caused by any exception
     * @param exception the critical error
     */
    public void addCriticalError(Exception exception) {
        criticalErrors++;
        errorLog.append("Critical error: ");
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        exception.printStackTrace(pw);
        errorLog.append(sw.toString());
    }

    /**
     * Stops the timer and updates the build time
     */
    public void stopTimer(){
        buildTimeMillis = System.currentTimeMillis() - buildTimeMillis;
    }

    /**
     * Prints all the errors
     * @return errors count for each one
     */
    @Override
    public String toString() {
        return  "Direct jump target errors: " + directJumpTargetErrors +
                "\nOrphan jump target null errors: " + orphanJumpTargetNullErrors +
                "\nOrphan jump target unknown errors: " + orphanJumpTargetUnknownErrors +
                "\nMultiple root nodes errors: " + multipleRootNodesErrors +
                "\nSymbolic execution stack limit exceeded errors: " + stackExceededErrors +
                "\nCritical errors: " + criticalErrors;
    }

    /**
     * gets the sum of the jump-related error
     * @return the sum of the errors
     */
    public int getTotalJumpError(){
        return directJumpTargetErrors + orphanJumpTargetUnknownErrors + orphanJumpTargetNullErrors + loopDepthExceededErrors;
    }

    /**
     * gets the multiple root nodes errors counter
     * @return the errors count
     */
    public int getMultipleRootNodesErrors() {
        return multipleRootNodesErrors;
    }

    /**
     * Gets the complete error log
     * @return complete error log
     */
    public String getLog(){
        return errorLog.toString();
    }
}
