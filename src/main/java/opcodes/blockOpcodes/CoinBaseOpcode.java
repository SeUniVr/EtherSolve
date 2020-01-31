package opcodes.blockOpcodes;

import opcodes.BlockOpcode;

public class CoinBaseOpcode extends BlockOpcode {

    public CoinBaseOpcode(long offset) {
        this.name = "COINBASE";
        this.opcode = 0x41;
        this.offset = offset;
    }

    @Override
    public int getStackInput() {
        return 0;
    }
}
