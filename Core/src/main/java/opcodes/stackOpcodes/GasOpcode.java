package opcodes.stackOpcodes;

import opcodes.OpcodeID;
import opcodes.StackOpcode;

public class GasOpcode extends StackOpcode {

    public GasOpcode(long offset) {
        super(OpcodeID.GAS);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
