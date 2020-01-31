package opcodes.systemOpcodes;

import opcodes.SystemOpcode;

public class SelfDestructOpcode extends SystemOpcode {

    public SelfDestructOpcode(long offset) {
        this.name = "SELFDESTRUCT";
        this.opcode = (byte) 0xFF;
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
