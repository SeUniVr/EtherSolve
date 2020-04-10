package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class ExtCodeCopyOpcode extends EnvironmentalOpcode {

    public ExtCodeCopyOpcode(long offset) {
        super(OpcodeID.EXTCODECOPY);
        this.offset = offset;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public int getStackConsumed() {
        return 4;
    }
}
