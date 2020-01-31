package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class BalanceOpcode extends EnviromentalOpcode {

    public BalanceOpcode(long offset) {
        this.name = "BALANCE";
        this.opcode = 0x31;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 1;
    }
}
