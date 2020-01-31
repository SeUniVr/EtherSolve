package opcodes.systemOpcodes;

import opcodes.SystemOpcode;

public class CallCodeOpcode extends SystemOpcode {

    public CallCodeOpcode(long offset) {
        this.name = "CALLCODE";
        this.opcode = (byte) 0xF2;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 7;
    }
}
