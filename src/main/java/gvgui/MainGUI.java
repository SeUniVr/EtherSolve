package gvgui;

import parseTree.Contract;
import parseTree.SolidityVersionUnknownException;

public class MainGUI {

    public static void main(String[] args){
        String contractBytecode = "608060405234801561001057600080fd5b5060aa60008190555060cf806100276000396000f3fe6080604052348015600f57600080fd5b506004361060285760003560e01c8063dffeadd014602d575b600080fd5b60336049565b6040518082815260200191505060405180910390f35b600080600054905060008090505b600a811015607657606681608c565b8201915080806001019150506057565b5060006082600054608c565b9050819250505090565b600081608801905091905056fea264697066735822122035c3363ddc33c6c86105bdc5ba120f12e8a8be4c0968c663b01609ce5c5c5c2e64736f6c63430006010033";

        Contract contract = new Contract("Contract", contractBytecode);

        try {
            System.out.println("Solidity version: " + contract.getExactSolidityVersion());
        } catch (SolidityVersionUnknownException e) {
            e.printStackTrace();
        }
        CFGPrinter.show(contract.getBodyCfg());
    }

}
