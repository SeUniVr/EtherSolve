package opcodes.stackOpcodes;

import opcodes.StackOpcode;

public class SStoreOpcode extends StackOpcode {

    public SStoreOpcode(long offset) {
        this.name = "SSTORE";
        this.opcode = 0x55;
        this.offset = offset;
    }

    @Override
    public int getStackOutput() {
        return 0;
    }

    @Override
    public int getStackInput() {
        return 2;
    }
}
