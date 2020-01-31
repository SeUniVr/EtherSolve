package opcodes.systemOpcodes;

import opcodes.SystemOpcode;

public class StaticCallOpcode extends SystemOpcode {

    public StaticCallOpcode(long offset) {
        this.name = "STATICCALL";
        this.opcode = (byte) 0xFA;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 6;
    }
}
