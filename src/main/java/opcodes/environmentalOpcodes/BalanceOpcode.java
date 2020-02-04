package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class BalanceOpcode extends EnvironmentalOpcode {

    public BalanceOpcode(long offset) {
        super(OpcodeID.BALANCE);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 1;
    }
}
