package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class ExtCodeHashOpcode extends EnviromentalOpcode {

    public ExtCodeHashOpcode(long offset) {
        this.name = "EXTCODEHASH";
        this.opcode = 0x3F;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 1;
    }
}
