package opcodes.stackOpcodes;

import opcodes.OpcodeID;
import opcodes.StackOpcode;

public class MStore8Opcode extends StackOpcode {

    public MStore8Opcode(long offset) {
        super(OpcodeID.MSTORE8);
        this.offset = offset;
    }

    @Override
    public int getStackGenerated() {
        return 0;
    }

    @Override
    public int getStackConsumed() {
        return 2;
    }
}
