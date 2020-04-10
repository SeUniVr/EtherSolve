package opcodes.stackOpcodes;

import opcodes.OpcodeID;
import opcodes.StackOpcode;

public class MSizeOpcode extends StackOpcode {

    public MSizeOpcode(long offset) {
        super(OpcodeID.MSIZE);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
