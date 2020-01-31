package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class CallDataLoadOpcode extends EnviromentalOpcode {

    public CallDataLoadOpcode(long offset) {
        this.name = "CALLDATALOAD";
        this.opcode = 0x35;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 1;
    }
}
