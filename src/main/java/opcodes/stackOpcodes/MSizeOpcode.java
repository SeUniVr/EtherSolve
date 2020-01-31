package opcodes.stackOpcodes;

import opcodes.StackOpcode;

public class MSizeOpcode extends StackOpcode {

    public MSizeOpcode(long offset) {
        this.name = "MSIZE";
        this.opcode = 0x59;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
