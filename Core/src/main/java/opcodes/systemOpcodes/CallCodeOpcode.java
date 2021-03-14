package opcodes.systemOpcodes;

import opcodes.OpcodeID;
import opcodes.SystemOpcode;

public class CallCodeOpcode extends SystemOpcode {

    public CallCodeOpcode(long offset) {
        super(OpcodeID.CALLCODE);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 7;
    }
}
