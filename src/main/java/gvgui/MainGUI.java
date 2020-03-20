package gvgui;

import parseTree.Contract;
import parseTree.SolidityVersionUnknownException;

public class MainGUI {

    public static void main(String[] args){
        String contractBytecode = "608060405234801561001057600080fd5b5061021b806100206000396000f3fe608060405234801561001057600080fd5b506004361061003a5760003560e01c80630ce68a19146100c8578063c2bc2efc146100f65761003b565b5b60003390508073ffffffffffffffffffffffffffffffffffffffff166108fc6000808473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549081150290604051600060405180830381858888f193505050501580156100c4573d6000803e3d6000fd5b5050005b6100f4600480360360208110156100de57600080fd5b810190808035906020019092919050505061014e565b005b6101386004803603602081101561010c57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061019d565b6040518082815260200191505060405180910390f35b806000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254019250508190555050565b60008060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905091905056fea2646970667358221220e271c2fc99c4a09fb9ebfd4c2a150d48b6613cae9c895cebf3ca6f06bb4dfdf064736f6c63430006040033";

        Contract contract = new Contract("Contract", contractBytecode);

        try {
            System.out.println("Solidity version: " + contract.getExactSolidityVersion());
        } catch (SolidityVersionUnknownException e) {
            e.printStackTrace();
        }
        CFGPrinter.show(contract.getBodyCfg());
    }

}
