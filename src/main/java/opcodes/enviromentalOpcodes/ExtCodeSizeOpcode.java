package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class ExtCodeSizeOpcode extends EnviromentalOpcode {

    public ExtCodeSizeOpcode(long offset) {
        this.name = "EXTCODESIZE";
        this.opcode = 0x3B;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 1;
    }
}
