package opcodes.stackOpcodes;

import opcodes.OpcodeID;
import opcodes.StackOpcode;

public class MLoadOpcode extends StackOpcode {

    public MLoadOpcode(long offset) {
        super(OpcodeID.MLOAD);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 1;
    }
}
