package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class CallDataCopyOpcode extends EnvironmentalOpcode {

    public CallDataCopyOpcode(long offset) {
        super(OpcodeID.CALLDATACOPY);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 3;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }
}
