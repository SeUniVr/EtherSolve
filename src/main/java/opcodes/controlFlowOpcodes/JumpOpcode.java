package opcodes.controlFlowOpcodes;

import opcodes.ControlFlowOpcode;
import opcodes.OpcodeID;

public class JumpOpcode extends ControlFlowOpcode {

    public JumpOpcode(long offset) {
        super(OpcodeID.JUMP);
        this.offset = offset;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public int getStackConsumed() {
        return 1;
    }
}
