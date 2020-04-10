package opcodes.systemOpcodes;

import opcodes.OpcodeID;
import opcodes.SystemOpcode;

public class RevertOpcode extends SystemOpcode {

    public RevertOpcode(long offset) {
        super(OpcodeID.REVERT);
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
