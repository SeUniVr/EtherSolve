package opcodes.stackOpcodes;

import opcodes.OpcodeID;
import opcodes.StackOpcode;

public class SStoreOpcode extends StackOpcode {

    public SStoreOpcode(long offset) {
        super(OpcodeID.SSTORE);
        this.offset = offset;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public int getStackConsumed() {
        return 2;
    }
}
