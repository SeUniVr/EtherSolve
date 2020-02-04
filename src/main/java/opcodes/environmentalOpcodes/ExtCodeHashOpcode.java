package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class ExtCodeHashOpcode extends EnvironmentalOpcode {

    public ExtCodeHashOpcode(long offset) {
        super(OpcodeID.EXTCODEHASH);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 1;
    }
}
