package opcodes.stackOpcodes;

import opcodes.StackOpcode;

public class GasOpcode extends StackOpcode {

    public GasOpcode(long offset) {
        this.name = "GAS";
        this.opcode = 0x5A;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
