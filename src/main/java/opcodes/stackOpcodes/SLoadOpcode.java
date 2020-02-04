package opcodes.stackOpcodes;

import opcodes.OpcodeID;
import opcodes.StackOpcode;

public class SLoadOpcode extends StackOpcode {

    public SLoadOpcode(long offset) {
        super(OpcodeID.SLOAD);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 1;
    }
}
