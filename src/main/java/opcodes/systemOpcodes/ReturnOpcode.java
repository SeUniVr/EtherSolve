package opcodes.systemOpcodes;

import opcodes.SystemOpcode;

public class ReturnOpcode extends SystemOpcode {

    public ReturnOpcode(long offset) {
        this.name = "RETURN";
        this.opcode = (byte) 0xF3;
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
