package opcodes.blockOpcodes;

import opcodes.BlockOpcode;
import opcodes.OpcodeID;

public class BlockHashOpcode extends BlockOpcode {

    public BlockHashOpcode(long offset) {
        super(OpcodeID.BLOCKHASH);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 1;
    }
}
