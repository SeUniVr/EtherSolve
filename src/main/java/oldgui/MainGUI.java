package oldgui;

import parseTree.Contract;

public class MainGUI {

    public static void main(String[] args){
        String input1 = "6080604052348015600f57600080fd5b5060aa60008190555060538060256000396000f3fe608060405260008054600e6014565b01905050005b6000608890509056fea2646970667358221220e73604337ca6440964bb7aaae76c90999a4ba3d1746f922f9b1534366a8051c86473";//6f6c63430006020033";
        String input2 = "6080604052348015600f57600080fd5b5060aa60008190555060538060256000396000f3fe608060405260008054600e6014565b01905050005b6000608890509056fea2646970667358221220e73604337ca6440964bb7aaae76c90999a4ba3d1746f922f9b1534366a8051c864736f6c63430006020033";
        String input3 = "6080604052348015600f57600080fd5b5060aa60008190555060538060256000396000f3fe608060405260008054600e6014565b01905050005b6000608890509056fea264697066735822122084caa862448229c72e485dc6650fe6a76f351622432eb39a9a2e2c9fb30dd5de64736f6c63430006020033";
        String input4 = "60538060256000396000f3fe608060405260008054600e6014565b01905050005b6000608890509056fe";//a2646970667358221220e73";
        String input5 = "6080604052348015600f57600080fd5b5060aa60008190555060718060256000396000f3fe608060405260008054905060008090505b600a811215602e57601e6032565b8201915080806001019150506010565b5050005b6000608890509056fea26469706673582212209fc244405a9148300a1ae46482b0c38c00dcddc76d7be18a6f94aa31ba4e03d664736f6c63430006010033";
        String input6 = "608060405234801561001057600080fd5b5060aa60008190555060c1806100276000396000f3fe6080604052348015600f57600080fd5b506004361060285760003560e01c8063dffeadd014602d575b600080fd5b60336049565b6040518082815260200191505060405180910390f35b60006051605a565b60005401905090565b6000806000905060008090505b6088811215608357818060010192505080806001019150506067565b50809150509056fea26469706673582212200aa6410c850aa23b7ea5ac1fdb6fc4b730cfb04eeb371c06c43696cf3c918cb764736f6c63430006010033";
        String input7 = "608060405234801561001057600080fd5b5060aa60008190555060bc806100276000396000f3fe6080604052348015600f57600080fd5b506004361060285760003560e01c8063dffeadd014602d575b600080fd5b60336049565b6040518082815260200191505060405180910390f35b600080600054905060008090505b600a8112156075576065607d565b8201915080806001019150506057565b508091505090565b6000608890509056fea2646970667358221220da5bb52dd0834e53ee31d8bca24934aa0bedd5c4ea4dfbab3a6fb6ef00a8ed7964736f6c63430006010033";
        String input8 = "608060405234801561001057600080fd5b506101a6806100206000396000f3fe6080604052600436106100385760003560e01c80630dbe671f146100a4578063c3da42b8146100cf578063cd580ff3146100fa57610039565b5b600060dd905060ff8110156100535760018101905061005a565b6002810190505b3373ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f193505050501580156100a0573d6000803e3d6000fd5b5050005b3480156100b057600080fd5b506100b9610149565b6040518082815260200191505060405180910390f35b3480156100db57600080fd5b506100e4610152565b6040518082815260200191505060405180910390f35b34801561010657600080fd5b506101336004803603602081101561011d57600080fd5b8101908080359060200190929190505050610163565b6040518082815260200191505060405180910390f35b600060aa905090565b600061015e60cc610163565b905090565b60008160bb01905091905056fea2646970667358221220346d035846a3247c236a1504219b172281d3577cf1ebdc035d798fef9c2143ba64736f6c63430006010033";
        String input9 = "6080604052348015600f57600080fd5b5060908061001e6000396000f3fe608060405260043610601f5760003560e01c80630dbe671f146029576020565b5b60006088905050005b348015603457600080fd5b50603b6051565b6040518082815260200191505060405180910390f35b600060aa90509056fea26469706673582212200cd488de0539e096a3da5717c5cfafa354c0945dea75af62eb88140f046f5d5e64736f6c63430006010033";
        String input10 = "6080604052348015600f57600080fd5b5060908061001e6000396000f3fe608060405260043610601f5760003560e01c80630dbe671f146029576020565b5b60006088905050005b348015603457600080fd5b50603b6051565b6040518082815260200191505060405180910390f35b600060aa90509056fea26469706673582212200cd488de0539e096a3da5717c5cfafa354c0945dea75af62eb88140f046f5d5e64736f6c63430006010033";


        Contract contract = new Contract("c8", input8);

        CFGPrinter.show(contract.getBodyCfg());
    }

}
