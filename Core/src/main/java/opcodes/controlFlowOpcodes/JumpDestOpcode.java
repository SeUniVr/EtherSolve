package opcodes.controlFlowOpcodes;

import opcodes.ControlFlowOpcode;
import opcodes.OpcodeID;

public class JumpDestOpcode extends ControlFlowOpcode {

    public JumpDestOpcode(long offset) {
        super(OpcodeID.JUMPDEST);
        this.offset = offset;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
