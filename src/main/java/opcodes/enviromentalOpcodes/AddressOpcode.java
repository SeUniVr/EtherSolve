package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class AddressOpcode extends EnviromentalOpcode {

    public AddressOpcode(long offset) {
        this.name = "ADDRESS";
        this.opcode = 0x30;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
