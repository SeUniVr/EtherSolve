package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class SelfBalanceOpcode extends EnvironmentalOpcode {

    public SelfBalanceOpcode(long offset) {
        super(OpcodeID.SELFBALANCE);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
