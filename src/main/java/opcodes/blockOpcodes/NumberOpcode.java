package opcodes.blockOpcodes;

import opcodes.BlockOpcode;
import opcodes.OpcodeID;

public class NumberOpcode extends BlockOpcode {

    public NumberOpcode(long offset) {
        super(OpcodeID.NUMBER);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
