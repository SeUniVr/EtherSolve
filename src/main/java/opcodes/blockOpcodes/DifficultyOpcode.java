package opcodes.blockOpcodes;

import opcodes.BlockOpcode;
import opcodes.OpcodeID;

public class DifficultyOpcode extends BlockOpcode {

    public DifficultyOpcode(long offset) {
        super(OpcodeID.DIFFICULTY);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
