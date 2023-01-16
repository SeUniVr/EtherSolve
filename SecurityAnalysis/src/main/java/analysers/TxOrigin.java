package analysers;

import main.SecurityAnalysisReport;
import main.SecurityDetection;
import main.SecurityVulnerability;
import opcodes.Opcode;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.AndOpcode;
import opcodes.arithmeticOpcodes.binaryArithmeticOpcodes.EQOpcode;
import opcodes.arithmeticOpcodes.unaryArithmeticOpcodes.IsZeroOpcode;
import opcodes.controlFlowOpcodes.JumpIOpcode;
import opcodes.environmentalOpcodes.OriginOpcode;
import opcodes.stackOpcodes.PushOpcode;
import parseTree.Contract;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.BasicBlockType;
import parseTree.cfg.Cfg;

public class TxOrigin {
    private static final String TX_ORIGIN_MESSAGE = "tx.origin used as authentication check";
    private static final Opcode origin = new OriginOpcode(0);
    private static final Opcode push20 = new PushOpcode(0, 20);
    private static final Opcode and = new AndOpcode(0);
    private static final Opcode eq = new EQOpcode(0);
    private static final Opcode iszero = new IsZeroOpcode(0);
    private static final Opcode push2 = new PushOpcode(0, 2);
    private static final Opcode jumpi = new JumpIOpcode(0);
    private static final Opcode[] pattern1 = {origin, push20, and, eq, push2, jumpi};
    private static final Opcode[] pattern2 = {origin, push20, and, eq, iszero, iszero, push2, jumpi};


    public static void analyse(Contract contract, SecurityAnalysisReport report) {
        // Get the CFG
        Cfg runtimeCfg = contract.getRuntimeCfg();
        // Gather the blocks containing the ORIGIN opcode
        for (BasicBlock bb : runtimeCfg) {
            if (bb.checkPattern(new OriginOpcode(0)) && bb.getType() == BasicBlockType.COMMON) {
                // Searching for patterns: ORIGIN, PUSH20 0XFFF..., AND, EQ, (ISZERO, ISZERO,)fffffe PUSH2 ..., JUMPI
                if ((bb.checkPattern(pattern1) || bb.checkPattern(pattern2)) && bb.getType() == BasicBlockType.COMMON) {
                    report.addDetection(new SecurityDetection(SecurityVulnerability.TX_ORIGIN_AS_AUTHENTICATION, getOrigin(bb), TX_ORIGIN_MESSAGE));
                } else if (taintCheck(bb)) {
                    // Taint analysis returns TRUE
                    report.addDetection(new SecurityDetection(SecurityVulnerability.TX_ORIGIN_AS_AUTHENTICATION, getOrigin(bb), TX_ORIGIN_MESSAGE));
                }
            }
        }
    }

    private static boolean taintCheck(BasicBlock bb) {
        int pos = -1;
        for (Opcode o : bb) {
            if (o instanceof  OriginOpcode && pos < 0) {
                // ORIGIN opcode found
                pos = 0;
            } else {
                // tracking
                int add = o.getStackGenerated();
                int remove = o.getStackConsumed();
                if (add > 0 && remove == 0) {
                    // OP adds only
                    pos += add;
                } else if (add == 0 && remove > 0) {
                    // OP removes only
                    pos -= remove;
                    if (pos < 0) {
                        // check for JUMPI
                        return o instanceof JumpIOpcode;
                    }
                } else if (add > 0 && remove > 0) {
                    pos -= remove;
                    if (pos < 0) {
                        pos = 0;
                    } else {
                        pos += add;
                    }
                }
            }
        }
        return false;
    }

    private static Opcode getOrigin(BasicBlock bb) {
        for (Opcode o : bb) {
            if (o instanceof OriginOpcode) {
                return o;
            }
        }

        return null;
    }
}
