package parseTree;

import opcodes.InvalidOpcode;
import opcodes.LogOpcode;
import opcodes.Opcode;
import opcodes.OpcodeID;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.*;
import opcodes.arithmeticOpcodes.ternaryArithmeticOpcodes.AddModOpcode;
import opcodes.arithmeticOpcodes.ternaryArithmeticOpcodes.MulModOpcode;
import opcodes.arithmeticOpcodes.unaryArithmeticOpcodes.IsZeroOpcode;
import opcodes.arithmeticOpcodes.unaryArithmeticOpcodes.NotOpcode;
import opcodes.blockOpcodes.*;
import opcodes.controlFlowOpcodes.JumpDestOpcode;
import opcodes.controlFlowOpcodes.JumpIOpcode;
import opcodes.controlFlowOpcodes.JumpOpcode;
import opcodes.controlFlowOpcodes.StopOpcode;
import opcodes.environmentalOpcodes.*;
import opcodes.stackOpcodes.*;
import opcodes.systemOpcodes.*;
import utils.Pair;

import java.math.BigInteger;

public class BytecodeParser {

    /**
     * Parse a string with the hexadecimal representation of the bytecode and generates both bytecode and remaining data
     *
     * If there is a PUSH-n but not enough bytes to build the argument then they become remaining data
     * @param binary the hexadecimal code
     * @return A pair with the bytecode and the remaining data
     */
    public static Pair<Bytecode, String> parse(String binary){
        Bytecode bytecode = new Bytecode();
        String remainingData = "";
        final byte PUSH_OPCODE = OpcodeID.PUSH.getOpcode();

        // Read two bytes for each opcode
        for (int i = 0; i < binary.length(); i+=2){
            // Cast the 2 hex char to one byte
            byte byteOpcode = (byte) Character.digit(binary.charAt(i), 16);
            byteOpcode <<= 4;
            byteOpcode += (byte) Character.digit(binary.charAt(i + 1), 16);

            // Parse all the opcodes except for the PUSH
            if (byteOpcode < PUSH_OPCODE || byteOpcode > PUSH_OPCODE + 32){
                // the offset is the half of the string index
                bytecode.addOpcode(parseOpcode(byteOpcode, i / 2));
            } else {
                // Treat the PUSH in different ways
                int argumentNumber = byteOpcode - PUSH_OPCODE + 1;
                // get the arguments of the PUSH
                if (i + 2 + argumentNumber * 2 <= binary.length()){
                    String strArgument = binary.substring(i + 2, i + 2 + argumentNumber*2);
                    BigInteger argument = new BigInteger(strArgument, 16);
                    bytecode.addOpcode(new PushOpcode(i / 2, argumentNumber, argument));
                    i += argumentNumber * 2;
                } else {
                    // The push has not enough data, then all the remaining bytes become remainingData
                    remainingData = binary.substring(i);
                    // breaks the loop
                    i = binary.length();
                }
            }
        }

        return new Pair<>(bytecode, remainingData);
    }

    private static Opcode parseOpcode(byte byteOpcode, int offset) {
        // System.out.println(String.format("[%2d] 0x%x", offset, byteOpcode));
        // DUP
        if (byteOpcode >= OpcodeID.DUP.getOpcode() && byteOpcode <= OpcodeID.DUP.getOpcode() + 15){
            int value = byteOpcode - OpcodeID.DUP.getOpcode() + 1;
            //System.out.println(String.format("%d, [0x%x - 0x%x]", value, OpcodeID.DUP.getOpcode(), OpcodeID.DUP.getOpcode() + 16));
            return new DupOpcode(offset, value);
        }

        // SWAP
        if (byteOpcode >= OpcodeID.SWAP.getOpcode() && byteOpcode <= OpcodeID.SWAP.getOpcode() + 15){
            int value = byteOpcode - OpcodeID.SWAP.getOpcode() + 1;
            return new SwapOpcode(offset, value);
        }

        // LOG
        if (byteOpcode >= OpcodeID.LOG.getOpcode() && byteOpcode <= OpcodeID.LOG.getOpcode() + 4){
            int topicNumber = byteOpcode - OpcodeID.LOG.getOpcode();
            return new LogOpcode(offset, topicNumber);
        }

        switch (byteOpcode){
            case 0x00:
                return new StopOpcode(offset);
            case 0x01:
                return new AddOpcode(offset);
            case 0x02:
                return new MulOpcode(offset);
            case 0x03:
                return new SubOpcode(offset);
            case 0x04:
                return new DivOpcode(offset);
            case 0x05:
                return new SDivOpcode(offset);
            case 0x06:
                return new ModOpcode(offset);
            case 0x07:
                return new SModOpcode(offset);
            case 0x08:
                return new AddModOpcode(offset);
            case 0x09:
                return new MulModOpcode(offset);
            case 0x0A:
                return new ExpOpcode(offset);
            case 0x0B:
                return new SignExtendOpcode(offset);
            case 0x10:
                return new LTOpcode(offset);
            case 0x11:
                return new GTOpcode(offset);
            case 0x12:
                return new SLTOpcode(offset);
            case 0x13:
                return new SGTOpcode(offset);
            case 0x14:
                return new EQOpcode(offset);
            case 0x15:
                return new IsZeroOpcode(offset);
            case 0x16:
                return new AndOpcode(offset);
            case 0x17:
                return new OrOpcode(offset);
            case 0x18:
                return new XorOpcode(offset);
            case 0x19:
                return new NotOpcode(offset);
            case 0x1A:
                return new ByteOpcode(offset);
            case 0x1B:
                return new SHLOpcode(offset);
            case 0x1C:
                return new SHROpcode(offset);
            case 0x1D:
                return new SAROpcode(offset);
            case 0x20:
                return new SHA3Opcode(offset);
            case 0x30:
                return new AddressOpcode(offset);
            case 0x31:
                return new BalanceOpcode(offset);
            case 0x32:
                return new OriginOpcode(offset);
            case 0x33:
                return new CallerOpcode(offset);
            case 0x34:
                return new CallValueOpcode(offset);
            case 0x35:
                return new CallDataLoadOpcode(offset);
            case 0x36:
                return new CallDataSizeOpcode(offset);
            case 0x37:
                return new CallDataCopyOpcode(offset);
            case 0x38:
                return new CodeSizeOpcode(offset);
            case 0x39:
                return new CodeCopyOpcode(offset);
            case 0x3A:
                return new GasPriceOpcode(offset);
            case 0x3B:
                return new ExtCodeSizeOpcode(offset);
            case 0x3C:
                return new ExtCodeCopyOpcode(offset);
            case 0x3D:
                return new ReturnDataSizeOpcode(offset);
            case 0x3E:
                return new ReturnDataCopyOpcode(offset);
            case 0x3F:
                return new ExtCodeHashOpcode(offset);
            case 0x40:
                return new BlockHashOpcode(offset);
            case 0x41:
                return new CoinBaseOpcode(offset);
            case 0x42:
                return new TimeStampOpcode(offset);
            case 0x43:
                return new NumberOpcode(offset);
            case 0x44:
                return new DifficultyOpcode(offset);
            case 0x45:
                return new GasLimitOpcode(offset);
            case 0x46:
                return new ChainIdOpcode(offset);
            case 0x47:
                return new SelfBalanceOpcode(offset);
            case 0x50:
                return new PopOpcode(offset);
            case 0x51:
                return new MLoadOpcode(offset);
            case 0x52:
                return new MStoreOpcode(offset);
            case 0x53:
                return new MStore8Opcode(offset);
            case 0x54:
                return new SLoadOpcode(offset);
            case 0x55:
                return new SStoreOpcode(offset);
            case 0x56:
                return new JumpOpcode(offset);
            case 0x57:
                return new JumpIOpcode(offset);
            case 0x58:
                return new PCOpcode(offset);
            case 0x59:
                return new MSizeOpcode(offset);
            case 0x5A:
                return new GasOpcode(offset);
            case 0x5B:
                return new JumpDestOpcode(offset);
            case (byte) 0xF0:
                return new CreateOpcode(offset);
            case (byte) 0xF1:
                return new CallOpcode(offset);
            case (byte) 0xF2:
                return new CallCodeOpcode(offset);
            case (byte) 0xF3:
                return new ReturnOpcode(offset);
            case (byte) 0xF4:
                return new DelegateCallOpcode(offset);
            case (byte) 0xFA:
                return new StaticCallOpcode(offset);
            case (byte) 0xFD:
                return new RevertOpcode(offset);
            case (byte) 0xFF:
                return new SelfDestructOpcode(offset);
            default:
                return new InvalidOpcode(offset, byteOpcode);
        }
    }

}
