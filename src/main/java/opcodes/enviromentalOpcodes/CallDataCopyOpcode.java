package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class CallDataCopyOpcode extends EnviromentalOpcode {

    public CallDataCopyOpcode(long offset) {
        this.name = "CALLDATACOPY";
        this.opcode = 0x37;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 3;
    }

    @Override
    public int getStackOutput() {
        return 0;
    }
}
