package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class CallerOpcode extends EnvironmentalOpcode {

    public CallerOpcode(long offset) {
        super(OpcodeID.CALLER);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
