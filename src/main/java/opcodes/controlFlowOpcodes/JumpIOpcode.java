package opcodes.controlFlowOpcodes;

import opcodes.ControlFlowOpcode;

public class JumpIOpcode extends ControlFlowOpcode {

    public JumpIOpcode(long offset) {
        this.name = "JUMPI";
        this.opcode = 0x57;
        this.offset = offset;
    }

    @Override
    public int getStackOutput() {
        return 0;
    }

    @Override
    public int getStackInput() {
        return 2;
    }
}
