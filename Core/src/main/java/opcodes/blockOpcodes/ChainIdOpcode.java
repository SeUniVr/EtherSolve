package opcodes.blockOpcodes;

import opcodes.BlockOpcode;
import opcodes.OpcodeID;

public class ChainIdOpcode extends BlockOpcode {
    public ChainIdOpcode(long offset){
        super(OpcodeID.CHAINID);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 0;
    }
}
