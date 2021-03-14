package opcodes.stackOpcodes;

import opcodes.OpcodeID;
import opcodes.StackOpcode;

public class PCOpcode extends StackOpcode {

    public PCOpcode(long offset) {
        super(OpcodeID.PC);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
