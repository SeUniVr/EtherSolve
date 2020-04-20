package opcodes;

public enum OpcodeID {

    STOP ((byte) 0x00,"STOP"),
    ADD ((byte) 0x01,"ADD"),
    MUL ((byte) 0x02,"MUL"),
    SUB ((byte) 0x03,"SUB"),
    DIV ((byte) 0x04,"DIV"),
    SDIV ((byte) 0x05,"SDIV"),
    MOD ((byte) 0x06,"MOD"),
    SMOD ((byte) 0x07,"SMOD"),
    ADDMOD ((byte) 0x08,"ADDMOD"),
    MULMOD ((byte) 0x09,"MULMOD"),
    EXP ((byte) 0x0a,"EXP"),
    SIGNEXTEND ((byte) 0x0b,"SIGNEXTEND"),
    LT ((byte) 0x10,"LT"),
    GT ((byte) 0x11,"GT"),
    SLT ((byte) 0x12,"SLT"),
    SGT ((byte) 0x13,"SGT"),
    EQ ((byte) 0x14,"EQ"),
    ISZERO ((byte) 0x15,"ISZERO"),
    AND ((byte) 0x16,"AND"),
    OR ((byte) 0x17,"OR"),
    XOR ((byte) 0x18,"XOR"),
    NOT ((byte) 0x19,"NOT"),
    BYTE ((byte) 0x1a,"BYTE"),
    SHL ((byte) 0x1b, "SHL"),
    SHR ((byte) 0x1c, "SHR"),
    SAR ((byte) 0x1d, "SAR"),
    SHA3 ((byte) 0x20,"SHA3"),
    ADDRESS ((byte) 0x30,"ADDRESS"),
    BALANCE ((byte) 0x31,"BALANCE"),
    ORIGIN ((byte) 0x32,"ORIGIN"),
    CALLER ((byte) 0x33,"CALLER"),
    CALLVALUE ((byte) 0x34,"CALLVALUE"),
    CALLDATALOAD ((byte) 0x35,"CALLDATALOAD"),
    CALLDATASIZE ((byte) 0x36,"CALLDATASIZE"),
    CALLDATACOPY ((byte) 0x37,"CALLDATACOPY"),
    CODESIZE ((byte) 0x38,"CODESIZE"),
    CODECOPY ((byte) 0x39,"CODECOPY"),
    GASPRICE ((byte) 0x3a,"GASPRICE"),
    EXTCODESIZE ((byte) 0x3b,"EXTCODESIZE"),
    EXTCODECOPY ((byte) 0x3c,"EXTCODECOPY"),
    RETURNDATASIZE ((byte) 0x3d,"RETURNDATASIZE"),
    RETURNDATACOPY ((byte) 0x3e,"RETURNDATACOPY"),
    EXTCODEHASH ((byte) 0x3f, "EXTCODEHASH"),
    BLOCKHASH ((byte) 0x40,"BLOCKHASH"),
    COINBASE ((byte) 0x41,"COINBASE"),
    TIMESTAMP ((byte) 0x42,"TIMESTAMP"),
    NUMBER ((byte) 0x43,"NUMBER"),
    DIFFICULTY ((byte) 0x44,"DIFFICULTY"),
    GASLIMIT ((byte) 0x45,"GASLIMIT"),
    CHAINID ((byte) 0x46, "CHAINID"),
    SELFBALANCE ((byte) 0x47, "SELFBALANCE"),
    POP ((byte) 0x50,"POP"),
    MLOAD ((byte) 0x51,"MLOAD"),
    MSTORE ((byte) 0x52,"MSTORE"),
    MSTORE8 ((byte) 0x53,"MSTORE8"),
    SLOAD ((byte) 0x54,"SLOAD"),
    SSTORE ((byte) 0x55,"SSTORE"),
    JUMP ((byte) 0x56,"JUMP"),
    JUMPI ((byte) 0x57,"JUMPI"),
    PC ((byte) 0x58,"PC"),
    MSIZE ((byte) 0x59,"MSIZE"),
    GAS ((byte) 0x5a,"GAS"),
    JUMPDEST ((byte) 0x5b,"JUMPDEST"),
    PUSH ((byte) 0x60,"PUSH"),
    DUP ((byte) 0x80,"DUP"),
    SWAP ((byte) 0x90,"SWAP"),
    LOG ((byte) 0xa0,"LOG"),
    CREATE ((byte) 0xf0,"CREATE"),
    CALL ((byte) 0xf1,"CALL"),
    CALLCODE ((byte) 0xf2,"CALLCODE"),
    RETURN ((byte) 0xf3,"RETURN"),
    DELEGATECALL ((byte) 0xf4,"DELEGATECALL"),
    STATICCALL ((byte) 0xfa,"STATICCALL"),
    REVERT ((byte) 0xfd,"REVERT"),
    INVALID ((byte) 0xfe,"INVALID"),
    SELFDESTRUCT ((byte) 0xff,"SELFDESTRUCT");

    private final byte opcode;
    private final String name;

    OpcodeID(byte opcode, String name) {
        this.opcode = opcode;
        this.name = name;
    }

    public byte getOpcode() {
        return opcode;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
