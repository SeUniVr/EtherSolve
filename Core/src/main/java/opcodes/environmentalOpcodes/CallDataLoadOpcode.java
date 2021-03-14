package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class CallDataLoadOpcode extends EnvironmentalOpcode {

    public CallDataLoadOpcode(long offset) {
        super(OpcodeID.CALLDATALOAD);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 1;
    }
}
