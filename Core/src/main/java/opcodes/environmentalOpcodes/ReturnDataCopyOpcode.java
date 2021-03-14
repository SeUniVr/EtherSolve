package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class ReturnDataCopyOpcode extends EnvironmentalOpcode {

    public ReturnDataCopyOpcode(long offset) {
        super(OpcodeID.RETURNDATACOPY);
        this.offset = offset;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public int getStackConsumed() {
        return 3;
    }
}
