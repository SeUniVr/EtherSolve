package opcodes.blockOpcodes;

import opcodes.BlockOpcode;

public class TimeStampOpcode extends BlockOpcode {

    public TimeStampOpcode(long offset) {
        this.name = "TIMESTAMP";
        this.opcode = 0x42;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
