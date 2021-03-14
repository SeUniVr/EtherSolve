package opcodes.stackOpcodes;

import opcodes.OpcodeID;
import opcodes.StackOpcode;

public class MStoreOpcode extends StackOpcode {

    public MStoreOpcode(long offset) {
        super(OpcodeID.MSTORE);
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
