import opcodes.Opcode;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.AddOpcode;
import opcodes.stackOpcodes.pushOpcodes.Push1;

import java.math.BigInteger;

public class Main {
    public static void main(String[] args) {
        Opcode o1 = new Push1(0, new BigInteger("60", 16));
        Opcode o2 = new AddOpcode(4);
        System.out.println(o1 + "\n" + o2);
    }
}
