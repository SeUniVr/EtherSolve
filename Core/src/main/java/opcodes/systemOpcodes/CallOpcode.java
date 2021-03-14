package opcodes.systemOpcodes;

import opcodes.OpcodeID;
import opcodes.SystemOpcode;

public class CallOpcode extends SystemOpcode {

    public CallOpcode(long offset) {
        super(OpcodeID.CALL);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 7;
    }
}
