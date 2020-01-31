package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class CallValueOpcode extends EnviromentalOpcode {

    public CallValueOpcode(long offset) {
        this.name = "CALLVALUE";
        this.opcode = 0x34;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
