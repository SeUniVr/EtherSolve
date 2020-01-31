package opcodes.systemOpcodes;

import opcodes.SystemOpcode;

public class CreateOpcode extends SystemOpcode {

    public CreateOpcode(long offset) {
        this.name = "CREATE";
        this.opcode = (byte) 0xF0;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 3;
    }
}
