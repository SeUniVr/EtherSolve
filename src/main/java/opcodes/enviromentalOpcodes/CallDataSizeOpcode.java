package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class CallDataSizeOpcode extends EnviromentalOpcode {

    public CallDataSizeOpcode(long offset) {
        this.name = "CALLDATASIZE";
        this.opcode = 0x36;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
