package opcodes.stackOpcodes;

import opcodes.StackOpcode;

public class PCOpcode extends StackOpcode {

    public PCOpcode(long offset) {
        this.name = "PC";
        this.opcode = 0x58;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
