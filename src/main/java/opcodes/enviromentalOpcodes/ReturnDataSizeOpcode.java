package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class ReturnDataSizeOpcode extends EnviromentalOpcode {

    public ReturnDataSizeOpcode(long offset) {
        this.name = "RETURNDATASIZE";
        this.opcode = 0x3D;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
