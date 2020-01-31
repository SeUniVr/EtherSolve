package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class ExtCodeCopyOpcode extends EnviromentalOpcode {

    public ExtCodeCopyOpcode(long offset) {
        this.name = "EXTCODECOPY";
        this.opcode = 0x3C;
        this.offset = offset;
    }

    @Override
    public int getStackOutput() {
        return 0;
    }

    @Override
    public int getStackInput() {
        return 3;
    }
}
