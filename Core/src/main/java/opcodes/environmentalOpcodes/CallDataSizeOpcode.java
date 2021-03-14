package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class CallDataSizeOpcode extends EnvironmentalOpcode {

    public CallDataSizeOpcode(long offset) {
        super(OpcodeID.CALLDATASIZE);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
