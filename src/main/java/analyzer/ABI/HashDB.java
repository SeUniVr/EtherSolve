package analyzer.ABI;

public class HashDB {
    private static HashDB ilSooEUnico = new HashDB();

    private HashDB() {
    }

    public static HashDB getInstance() {
        return ilSooEUnico;
    }

    public String getSignatureFromHash(String hash){
        return "";
    }

    public AbiFunction getAbiFromHash(String hash){
        return null;
    }
}
