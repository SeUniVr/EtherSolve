package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class OriginOpcode extends EnvironmentalOpcode {

    public OriginOpcode(long offset) {
        super(OpcodeID.ORIGIN);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
