package opcodes.environmentalOpcodes;

import opcodes.EnvironmentalOpcode;
import opcodes.OpcodeID;

public class CodeSizeOpcode extends EnvironmentalOpcode {

    public CodeSizeOpcode(long offset) {
        super(OpcodeID.CODESIZE);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
