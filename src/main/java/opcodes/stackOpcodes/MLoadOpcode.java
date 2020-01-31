package opcodes.stackOpcodes;

import opcodes.StackOpcode;

public class MLoadOpcode extends StackOpcode {

    public MLoadOpcode(long offset) {
        this.name = "MLOAD";
        this.opcode = 0x51;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 1;
    }
}
