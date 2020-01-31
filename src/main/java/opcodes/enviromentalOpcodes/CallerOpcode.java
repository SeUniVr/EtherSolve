package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class CallerOpcode extends EnviromentalOpcode {

    public CallerOpcode(long offset) {
        this.name = "CALLER";
        this.opcode = 0x33;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
