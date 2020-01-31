package opcodes.stackOpcodes;

import opcodes.StackOpcode;

public class MStore8Opcode extends StackOpcode {

    public MStore8Opcode(long offset) {
        this.name = "MSTORE8";
        this.opcode = 0x53;
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
