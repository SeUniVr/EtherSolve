package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class CodeCopyOpcode extends EnvironmentalOpcode {

    public CodeCopyOpcode(long offset) {
        super(OpcodeID.CODECOPY);
        this.offset = offset;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public int getStackConsumed() {
        return 3;
    }
}
