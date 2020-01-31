package opcodes.systemOpcodes;

import opcodes.SystemOpcode;

public class RevertOpcode extends SystemOpcode {

    public RevertOpcode(long offset) {
        this.name = "REVERT";
        this.opcode = (byte) 0xFD;
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
