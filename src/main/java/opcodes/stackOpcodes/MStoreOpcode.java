package opcodes.stackOpcodes;

import opcodes.StackOpcode;

public class MStoreOpcode extends StackOpcode {

    public MStoreOpcode(long offset) {
        this.name = "MSTORE";
        this.opcode = 0x52;
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
