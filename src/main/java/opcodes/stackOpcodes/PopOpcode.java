package opcodes.stackOpcodes;

import opcodes.OpcodeID;
import opcodes.StackOpcode;

public class PopOpcode extends StackOpcode {

    public PopOpcode(long offset) {
        super(OpcodeID.POP);
        this.offset = offset;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public int getStackConsumed() {
        return 1;
    }
}
