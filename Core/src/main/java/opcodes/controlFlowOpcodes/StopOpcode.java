package opcodes.controlFlowOpcodes;

import opcodes.ControlFlowOpcode;
import opcodes.OpcodeID;

public class StopOpcode extends ControlFlowOpcode {

    public StopOpcode(long offset) {
        super(OpcodeID.STOP);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }
}
