package opcodes.systemOpcodes;

import opcodes.OpcodeID;
import opcodes.SystemOpcode;

public class StaticCallOpcode extends SystemOpcode {

    public StaticCallOpcode(long offset) {
        super(OpcodeID.STATICCALL);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 6;
    }
}
