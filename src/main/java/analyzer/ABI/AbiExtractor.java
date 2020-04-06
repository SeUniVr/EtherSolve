package analyzer.ABI;

import parseTree.Contract;

public class AbiExtractor {
    private static AbiExtractor ilSooEUnico = new AbiExtractor();

    private AbiExtractor() {}

    public static AbiExtractor getInstance() {return ilSooEUnico;}

    public Abi extractAbi(Contract contract) {
        Abi result = new Abi();

        return result;
    }


}
