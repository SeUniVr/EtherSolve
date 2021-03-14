package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class ExtCodeSizeOpcode extends EnvironmentalOpcode {

    public ExtCodeSizeOpcode(long offset) {
        super(OpcodeID.EXTCODESIZE);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 1;
    }
}
