package opcodes.blockOpcodes;

import opcodes.BlockOpcode;

public class BlockHashOpcode extends BlockOpcode {

    public BlockHashOpcode(long offset) {
        this.name = "BLOCKHASH";
        this.opcode = 0x40;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 1;
    }
}
