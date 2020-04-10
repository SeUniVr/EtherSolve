package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class GasPriceOpcode extends EnvironmentalOpcode {

    public GasPriceOpcode(long offset) {
        super(OpcodeID.GASPRICE);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
