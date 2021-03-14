package opcodes.systemOpcodes;

import opcodes.OpcodeID;
import opcodes.SystemOpcode;

public class SelfDestructOpcode extends SystemOpcode {

    public SelfDestructOpcode(long offset) {
        super(OpcodeID.SELFDESTRUCT);
        this.offset = offset;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public int getStackConsumed() {
        return 1;
    }
}
