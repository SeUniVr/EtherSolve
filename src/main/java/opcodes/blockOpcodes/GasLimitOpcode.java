package opcodes.blockOpcodes;

import opcodes.BlockOpcode;

public class GasLimitOpcode extends BlockOpcode {

    public GasLimitOpcode(long offset) {
        this.name = "GASLIMIT";
        this.opcode = 0x45;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
