import parseTree.Contract;
import parseTree.NotSolidityContractException;
import utils.JsonExporter;
import utils.Message;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String bytecode = "608060405260aa60005534801561001557600080fd5b50610184806100256000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80634b3df200146100515780635bfd987d1461006f578063c2985578146100b1578063febb0f7e146100cf575b600080fd5b6100596100ed565b6040518082815260200191505060405180910390f35b61009b6004803603602081101561008557600080fd5b810190808035906020019092919050505061011d565b6040518082815260200191505060405180910390f35b6100b961012a565b6040518082815260200191505060405180910390f35b6100d761013c565b6040518082815260200191505060405180910390f35b6000806000815480929190600101919050555060aa600054141561011557600054905061011a565b600080fd5b90565b6000816088019050919050565b600061013761cccc61011d565b905090565b600061014961dddd61011d565b90509056fea2646970667358221220e619b234c1887f9b10b567ee21364dbf523a19001c8c47a33049907c0398563164736f6c63430006040033";
        try {
            Contract contract = new Contract("Sample", bytecode);
            String json = new JsonExporter().toJson(contract);
            File file = new File("../outputs/json/" + contract.getContractHash() + ".json");
            if (file.getParentFile().mkdirs())
                Message.printDebug("Output folder will be created");
            try (BufferedWriter out = new BufferedWriter(new FileWriter(file))) {
                out.write(json);
                System.out.println("Contract analysis exported in " + file.getCanonicalPath());
            } catch (IOException e) {
                System.err.format("Error writing file %s: %s%n", contract.getContractHash(), e);
                e.printStackTrace();
            }
        } catch (NotSolidityContractException e) {
            e.printStackTrace();
        }
    }
}