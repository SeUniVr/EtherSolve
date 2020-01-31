package opcodes.controlFlowOpcodes;

import opcodes.ControlFlowOpcode;

public class JumpDestOpcode extends ControlFlowOpcode {

    public JumpDestOpcode(long offset) {
        this.name = "JUMPDEST";
        this.opcode = 0x5B;
        this.offset = offset;
    }

    @Override
    public int getStackOutput() {
        return 0;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
