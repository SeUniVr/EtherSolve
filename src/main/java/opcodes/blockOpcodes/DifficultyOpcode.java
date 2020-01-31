package opcodes.blockOpcodes;

import opcodes.BlockOpcode;

public class DifficultyOpcode extends BlockOpcode {

    public DifficultyOpcode(long offset) {
        this.name = "DIFFICULTY";
        this.opcode = 0x44;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
