package opcodes.systemOpcodes;

import opcodes.SystemOpcode;

public class DelegateCallOpcode extends SystemOpcode {

    public DelegateCallOpcode(long offset) {
        this.name = "DELEGATECALL";
        this.opcode = (byte) 0xF4;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 6;
    }
}
