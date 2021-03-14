package opcodes.systemOpcodes;

import opcodes.OpcodeID;
import opcodes.SystemOpcode;

public class CreateOpcode extends SystemOpcode {

    public CreateOpcode(long offset) {
        super(OpcodeID.CREATE);
        this.offset = offset;
    }

    @Override
    public int getStackConsumed() {
        return 3;
    }
}
