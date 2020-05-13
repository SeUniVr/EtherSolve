import parseTree.Contract;
import parseTree.NotSolidityContractException;
import utils.JsonExporter;

public class Main {
    public static void main(String[] args) {
        String bytecode = "6060608052";
        Contract contract = null;
        try {
            contract = new Contract("Pippo", bytecode);
            System.out.println(new JsonExporter().toJson(contract));
        } catch (NotSolidityContractException e) {
            e.printStackTrace();
        }
    }
}