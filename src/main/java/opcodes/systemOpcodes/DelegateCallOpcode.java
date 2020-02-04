package opcodes.systemOpcodes;

import opcodes.OpcodeID;
import opcodes.SystemOpcode;

public class DelegateCallOpcode extends SystemOpcode {

    public DelegateCallOpcode(long offset) {
        super(OpcodeID.DELEGATECALL);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 6;
    }
}
