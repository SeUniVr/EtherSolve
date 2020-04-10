package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class ReturnDataSizeOpcode extends EnvironmentalOpcode {

    public ReturnDataSizeOpcode(long offset) {
        super(OpcodeID.RETURNDATASIZE);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
