package opcodes.blockOpcodes;

import opcodes.BlockOpcode;
import opcodes.OpcodeID;

public class TimeStampOpcode extends BlockOpcode {

    public TimeStampOpcode(long offset) {
        super(OpcodeID.TIMESTAMP);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
