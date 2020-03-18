package gvgui;

import parseTree.Contract;

public class MainGUI {

    public static void main(String[] args){
        String contractBytecode = "6060604052602d600060005055600a8060186000396000f360606040526008565b00";

        Contract contract = new Contract("Contract", contractBytecode);

        CFGPrinter.show(contract.getBodyCfg());
    }

}
