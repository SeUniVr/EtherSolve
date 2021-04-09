package graphviz;

import SolidityInfo.SolidityVersionUnknownException;
import parseTree.Contract;
import parseTree.NotSolidityContractException;
import parseTree.cfg.Cfg;

public class MainGV {

    public static void main(String[] args){
        String ottOtt = "608060405260aa60005534801561001557600080fd5b50610184806100256000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80634b3df200146100515780635bfd987d1461006f578063c2985578146100b1578063febb0f7e146100cf575b600080fd5b6100596100ed565b6040518082815260200191505060405180910390f35b61009b6004803603602081101561008557600080fd5b810190808035906020019092919050505061011d565b6040518082815260200191505060405180910390f35b6100b961012a565b6040518082815260200191505060405180910390f35b6100d761013c565b6040518082815260200191505060405180910390f35b6000806000815480929190600101919050555060aa600054141561011557600054905061011a565b600080fd5b90565b6000816088019050919050565b600061013761cccc61011d565b905090565b600061014961dddd61011d565b90509056fea2646970667358221220e619b234c1887f9b10b567ee21364dbf523a19001c8c47a33049907c0398563164736f6c63430006040033";

        System.out.println("START");
        long pre = System.currentTimeMillis();
        Contract contract = null;
        try {
            contract = new Contract("OttOtt", ottOtt, false);
        } catch (NotSolidityContractException e) {
            e.printStackTrace();
            return;
        }
        Cfg generated_cfg = contract.getRuntimeCfg();
        long post = System.currentTimeMillis();
        System.out.println("ELAPSED TIME: " + (post-pre));

        String solidity_version;
        try {
            solidity_version = contract.getExactSolidityVersion();
            System.out.println("Solidity version: " + solidity_version);
        } catch (SolidityVersionUnknownException e) {
            solidity_version = "unknown or before 0.5.9";
        }

        System.out.println("Constructor CFG remaining data: \"" + contract.getConstructorCfg().getRemainingData() + '"');
        System.out.println("Runtime CFG remaining data: \"" + contract.getRuntimeCfg().getRemainingData() + '"');
        System.out.println("Constructor remaining data: \"" + contract.getConstructorRemainingData() + '"');
        System.out.println("Runtime CFG build report:\n\t" + contract.getRuntimeCfg().getBuildReport().toString().replace("\n", "\n\t"));

        String svgPath = CFGPrinter.renderAndSave(generated_cfg);
        String reportPath = CFGPrinter.createReport(svgPath, solidity_version, post-pre, generated_cfg.getRemainingData(), generated_cfg.getBuildReport());
        CFGPrinter.openHtmlReport(reportPath);
    }

}
