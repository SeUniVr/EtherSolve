package opcodes.controlFlowOpcodes;

import opcodes.ControlFlowOpcode;

public class StopOpcode extends ControlFlowOpcode {

    public StopOpcode(long offset) {
        this.name = "STOP";
        this.opcode = 0x00;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
