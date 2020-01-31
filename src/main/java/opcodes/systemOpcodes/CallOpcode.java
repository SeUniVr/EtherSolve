package opcodes.systemOpcodes;

import opcodes.SystemOpcode;

public class CallOpcode extends SystemOpcode {

    public CallOpcode(long offset) {
        this.name = "CALL";
        this.opcode = (byte) 0xF1;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 7;
    }
}
