package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class OriginOpcode extends EnviromentalOpcode {

    public OriginOpcode(long offset) {
        this.name = "ORIGIN";
        this.opcode = 0x32;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
