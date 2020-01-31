package opcodes.enviromentalOpcodes;

import opcodes.EnviromentalOpcode;

public class CodeSizeOpcode extends EnviromentalOpcode {

    public CodeSizeOpcode(long offset) {
        this.name = "CODESIZE";
        this.opcode = 0x38;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
