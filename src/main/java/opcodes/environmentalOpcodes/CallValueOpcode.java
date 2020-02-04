package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class CallValueOpcode extends EnvironmentalOpcode {

    public CallValueOpcode(long offset) {
        super(OpcodeID.CALLVALUE);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
