package opcodes.blockOpcodes;

import opcodes.BlockOpcode;
import opcodes.OpcodeID;

public class GasLimitOpcode extends BlockOpcode {

    public GasLimitOpcode(long offset) {
        super(OpcodeID.GASLIMIT);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
