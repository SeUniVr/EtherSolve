package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class GasPriceOpcode extends EnviromentalOpcode {

    public GasPriceOpcode(long offset) {
        this.name = "GASPRICE";
        this.opcode = 0x3A;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
