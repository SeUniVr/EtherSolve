package opcodes.controlFlowOpcodes;

import opcodes.ControlFlowOpcode;
import opcodes.OpcodeID;

public class JumpIOpcode extends ControlFlowOpcode {

    public JumpIOpcode(long offset) {
        super(OpcodeID.JUMPI);
        this.offset = offset;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public int getStackConsumed() {
        return 2;
    }
}
