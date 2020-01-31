package opcodes.stackOpcodes;

import opcodes.StackOpcode;

public class SLoadOpcode extends StackOpcode {

    public SLoadOpcode(long offset) {
        this.name = "SLOAD";
        this.opcode = 0x54;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 1;
    }
}
