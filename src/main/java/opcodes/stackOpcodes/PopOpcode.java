package opcodes.stackOpcodes;

import opcodes.StackOpcode;

public class PopOpcode extends StackOpcode {

    public PopOpcode(long offset) {
        this.name = "POP";
        this.opcode = 0x50;
        this.offset = offset;
    }

    @Override
    public int getStackOutput() {
        return 0;
    }

    @Override
    public int getStackInput() {
        return 1;
    }
}
