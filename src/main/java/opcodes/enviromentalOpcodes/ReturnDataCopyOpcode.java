package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class ReturnDataCopyOpcode extends EnviromentalOpcode {

    public ReturnDataCopyOpcode(long offset) {
        this.name = "RETURNDATACOPY";
        this.opcode = 0x3E;
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
