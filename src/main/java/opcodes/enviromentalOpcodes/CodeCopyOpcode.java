package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class CodeCopyOpcode extends EnviromentalOpcode {

    public CodeCopyOpcode(long offset) {
        this.name = "CODECOPY";
        this.opcode = 0x39;
        this.offset = offset;
    }

    @Override
    public int getStackOutput() {
        return 0;
    }

    @Override
    public int getStackInput() {
        return 3;
    }
}
