package comparation;

import abi.Abi;
import abi.AbiFunction;
import abi.fields.FunctionType;
import abi.fields.IOElement;
import abi.fields.SolidityType;
import abi.fields.SolidityTypeID;
import org.bouncycastle.jcajce.provider.digest.Keccak;
import rebuiltabi.RebuiltAbi;
import rebuiltabi.RebuiltAbiFunction;
import rebuiltabi.fields.RebuiltSolidityType;
import rebuiltabi.fields.RebuiltSolidityTypeID;
import utils.Message;

import java.util.List;
import java.util.stream.Collectors;

public class AbiComparator {

    public static final double COMPLETE_MATCH = 1;
    public static final double PARTIAL_MATCH = 0.5;
    public static final double NO_MATCH = 0;

    public static AbiComparison compare(RebuiltAbi rebuiltAbi, Abi downloadedAbi){
        AbiComparison result = new AbiComparison();
        for (AbiFunction function : downloadedAbi.getFunctions()){
            // Compare only functions and fallback
            if (function.getType() == FunctionType.FUNCTION || function.getType() == FunctionType.FALLBACK){
                String hash = "0x" + keccak256(getSignature(function)).substring(0,8);
                RebuiltAbiFunction candidate = rebuiltAbi.getFunction(hash);
                if (function.getType() == FunctionType.FALLBACK)
                    candidate = rebuiltAbi.getFunction("");
                double[] score = null;
                if (candidate != null){
                    result.addMatch();
                    score = compare(candidate, function);
                } else {
                    result.addMismatch();
                }
                result.addScore(hash, score);
            }
        }
        result.addMismatchedRebuiltFunctions(rebuiltAbi.getLength() - result.getMatch());
        return result;
    }

    private static double[] compare(RebuiltAbiFunction rebuiltAbiFunction, AbiFunction downloadedFunction){
        int argMin = Math.min(rebuiltAbiFunction.getInputs().size(), downloadedFunction.getInputs().size());
        int argMax = Math.max(rebuiltAbiFunction.getInputs().size(), downloadedFunction.getInputs().size());
        // If there are no inputs then it's a complete match;
        if (argMax == 0)
            return new double[0];
        // Else checks for the inputs
        double[] score = new double[argMax];
        for (int i = 0; i < argMin; i++){
            score[i] = compareIOTypes(rebuiltAbiFunction.getInputs().get(i).getType(), downloadedFunction.getInputs().get(i).getType());
        }
        for (int i = argMin; i < argMax; i++){
            score[i] = NO_MATCH;
        }
        return score;
    }

    private static double compareIOTypes(RebuiltSolidityType candidateType, SolidityType referenceType) {
        if (candidateType.getTypeID() == RebuiltSolidityTypeID.SIMPLE){
            switch (referenceType.getSolidityTypeID()){
                case UINT:
                case INT:
                    if (candidateType.getN() == referenceType.getN())
                        return COMPLETE_MATCH;
                    else
                        return PARTIAL_MATCH;
                case ADDRESS:
                    // Address is 160 bits
                    return candidateType.getN() == 160 ? COMPLETE_MATCH : PARTIAL_MATCH;
                case BOOL:
                    // Bool is 1 bit
                    return candidateType.getN() == 1 ? COMPLETE_MATCH : PARTIAL_MATCH;
                default:
                    return NO_MATCH;
            }

        } else if (candidateType.getTypeID() == RebuiltSolidityTypeID.COMPLEX){
            if (referenceType.isArray() || referenceType.getSolidityTypeID() == SolidityTypeID.STRING)
                return COMPLETE_MATCH;
            else
                return NO_MATCH;
        } else
            return NO_MATCH;
    }

    private static String getSignature(AbiFunction function) {
        StringBuilder signature = new StringBuilder();
        signature.append(function.getName());
        signature.append("(");
        List<String> parameters = function.getInputs().stream().map(input -> input.getType().toString()).collect(Collectors.toList());
        signature.append(String.join(",", parameters));
        signature.append(")");
        return signature.toString();
    }

    public static String keccak256(String input) {
        Keccak.Digest256 keccak = new Keccak.Digest256();
        StringBuilder sb = new StringBuilder();
        for (byte b : keccak.digest(input.getBytes()))
            sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static void main(String[] args) {
        AbiFunction function = new AbiFunction("a", FunctionType.FUNCTION);
        function.addInput(new IOElement("arg", new SolidityType(SolidityTypeID.UINT, 256)));
        function.addInput(new IOElement("arg1", new SolidityType(SolidityTypeID.INT, 128)));
        System.out.println(getSignature(function));
        System.out.println(keccak256(getSignature(function)).substring(0, 8));
    }
}
