package opcodes.controlFlowOpcodes;

import opcodes.ControlFlowOpcode;

public class JumpOpcode extends ControlFlowOpcode {

    public JumpOpcode(long offset) {
        this.name = "JUMP";
        this.opcode = 0x56;
        this.offset = offset;
    }

    @Override
    public int getStackOutput() {
        return 0;
    }

    @Override
    public int getStackInput() {
        return 1;
    }
}
