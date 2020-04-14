package comparation;

import abi.Abi;
import abi.AbiFunction;
import org.bouncycastle.jcajce.provider.digest.Keccak;

public class AbiComparator {
    public static AbiComparison compare(Abi rebuiltAbi, Abi downloadedAbi){
        AbiComparison result = new AbiComparison();
        for (AbiFunction function : downloadedAbi.getFunctions()){
            String hash = keccak256(function.getName());
            double score = compare(rebuiltAbi.getFunction(hash), function);
            result.addScore(hash, score);
        }
        return result;
    }

    private static double compare(AbiFunction rebuiltFunction, AbiFunction downloadedFunction){
        // TODO
        return 0;
    }

    private static String keccak256(String input) {
        Keccak.Digest256 keccak = new Keccak.Digest256();
        StringBuilder sb = new StringBuilder();
        for (byte b : keccak.digest("a()".getBytes()))
            sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
