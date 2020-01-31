package opcodes.blockOpcodes;

import opcodes.BlockOpcode;

public class NumberOpcode extends BlockOpcode {

    public NumberOpcode(long offset) {
        this.name = "NUMBER";
        this.opcode = 0x43;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
