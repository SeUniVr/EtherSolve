package opcodes.blockOpcodes;

import opcodes.BlockOpcode;
import opcodes.OpcodeID;

public class CoinBaseOpcode extends BlockOpcode {

    public CoinBaseOpcode(long offset) {
        super(OpcodeID.COINBASE);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
