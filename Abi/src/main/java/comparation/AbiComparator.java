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

import java.util.List;
import java.util.stream.Collectors;

public class AbiComparator {

    private static final double COMPLETE_MATCH = 1;
    private static final double PARTIAL_MATCH = 0.5;
    private static final double NO_MATCH = 0;

    public static AbiComparison compare(RebuiltAbi rebuiltAbi, Abi downloadedAbi){
        AbiComparison result = new AbiComparison();
        for (AbiFunction function : downloadedAbi.getFunctions()){
            String hash = "0x" + keccak256(getSignature(function)).substring(0,8);
            RebuiltAbiFunction candidate = rebuiltAbi.getFunction(hash);
            if (function.getType() == FunctionType.FALLBACK)
                candidate = rebuiltAbi.getFunction("");
            double score = 0;
            if (candidate != null)
                score = compare(candidate, function);
            result.addScore(hash, score);
        }
        return result;
    }

    private static double compare(RebuiltAbiFunction rebuiltAbiFunction, AbiFunction downloadedFunction){
        // TODO implement comparison
        return 0;
    }

    private static double compare(AbiFunction rebuiltFunction, AbiFunction downloadedFunction, boolean compareOutput){
        int comparisons = 1;
        double score = 0;
        // Compare type
        if (rebuiltFunction.getType() == downloadedFunction.getType())
            score++;
        // Compare inputs
        int argMin = Math.min(rebuiltFunction.getInputs().size(), downloadedFunction.getInputs().size());
        int argMax = Math.max(rebuiltFunction.getInputs().size(), downloadedFunction.getInputs().size());
        for (int i = 0; i < argMin; i++){
            score += compareIOElement(rebuiltFunction.getInputs().get(i), downloadedFunction.getInputs().get(i));
        }
        comparisons += argMax;
        // Compare outputs
        if (compareOutput) {
            argMin = Math.min(rebuiltFunction.getOutputs().size(), downloadedFunction.getOutputs().size());
            argMax = Math.max(rebuiltFunction.getOutputs().size(), downloadedFunction.getOutputs().size());
            for (int i = 0; i < argMin; i++) {
                score += compareIOElement(rebuiltFunction.getOutputs().get(i), downloadedFunction.getOutputs().get(i));
            }
            comparisons += argMax;
        }
        return score / comparisons;
    }

    private static double compareIOElement(IOElement rebuiltIO, IOElement downloadedIO) {
        // If the type is the same then 1
        if (rebuiltIO.getType().equals(downloadedIO.getType()))
            return COMPLETE_MATCH;
        // If the difference is byte-string or int-uint then 0.5
        if (rebuiltIO.getType().getSolidityTypeID() == SolidityTypeID.UINT && downloadedIO.getType().getSolidityTypeID() == SolidityTypeID.INT)
            if (rebuiltIO.getType().getN() == downloadedIO.getType().getN())
                return PARTIAL_MATCH;
        if (rebuiltIO.getType().getSolidityTypeID() == SolidityTypeID.INT && downloadedIO.getType().getSolidityTypeID() == SolidityTypeID.UINT)
            if (rebuiltIO.getType().getN() == downloadedIO.getType().getN())
                return PARTIAL_MATCH;
        if (rebuiltIO.getType().getSolidityTypeID() == SolidityTypeID.BYTES && downloadedIO.getType().getSolidityTypeID() == SolidityTypeID.STRING)
            return PARTIAL_MATCH;
        if (rebuiltIO.getType().getSolidityTypeID() == SolidityTypeID.STRING && downloadedIO.getType().getSolidityTypeID() == SolidityTypeID.BYTES)
            return PARTIAL_MATCH;
        // Else no match
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

    private static String keccak256(String input) {
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
