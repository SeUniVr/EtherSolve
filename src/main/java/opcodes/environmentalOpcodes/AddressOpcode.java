package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class AddressOpcode extends EnvironmentalOpcode {

    public AddressOpcode(long offset) {
        super(OpcodeID.ADDRESS);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
