import abi.Abi;
import abi.AbiExtractor;
import etherscan.AbiDownloader;
import gson.GsonAbi;
import parseTree.Contract;

import java.io.IOException;

public class MainAbi {
    public static void main(String[] args) {
        String pippo = "608060405234801561001057600080fd5b5061020c806100206000396000f3fe608060405234801561001057600080fd5b506004361061005b5760003560e01c80632a1bbc3414610077578063b54ac1d614610095578063d96073cf146100c3578063e7a721cf14610116578063ee7563111461015f5761005c565b5b60006188889050619999811015610074576001810190505b50005b61007f6101a1565b6040518082815260200191505060405180910390f35b6100c1600480360360208110156100ab57600080fd5b81019080803590602001909291905050506101ab565b005b6100f9600480360360408110156100d957600080fd5b8101908080359060200190929190803590602001909291905050506101ae565b604051808381526020018281526020019250505060405180910390f35b6101426004803603602081101561012c57600080fd5b81019080803590602001909291905050506101be565b604051808381526020018281526020019250505060405180910390f35b61018b6004803603602081101561017557600080fd5b81019080803590602001909291905050506101cc565b6040518082815260200191505060405180910390f35b600061cccc905090565b50565b6000808284915091509250929050565b600080828391509150915091565b600081905091905056fea2646970667358221220ba20c110ddf955d3e57a83651b5ab836206afdcc43777d1cfb86dff69eafa8aa64736f6c63430006010033";
        String huge = "60606040526040805190810160405280600381526020017f312e3000000000000000000000000000000000000000000000000000000000008152506003908051906020019062000051929190620001c1565b5060006009556000600a55610271600b5534156200006e57600080fd5b60405160408062001e90833981016040528080519060200190919080519060200190919050505b81600460006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600560146101000a81548160ff021916908315150217905550600060068190555060006007819055506200012081620001b06401000000000262001bb4176401000000009004565b60088190555062000149632498e580620001b06401000000000262001bb4176401000000009004565b600081905550600054600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055506000546008541115620001a757600080fd5b5b505062000270565b60006012600a0a820290505b919050565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200020457805160ff191683800117855562000235565b8280016001018555821562000235579182015b828111156200023457825182559160200191906001019062000217565b5b50905062000244919062000248565b5090565b6200026d91905b80821115620002695760008160009055506001016200024f565b5090565b90565b611c1080620002806000396000f30060606040523615610173576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806306fdde0314610294578063095ea7b3146103235780630a9ffdb71461037d57806313b53153146103bf57806318160ddd146103ec57806323b872dd1461041557806326a215751461048e578063313ce567146104b757806332513ce5146104e05780634172d080146105355780634477c5da1461055e5780634a36df251461057357806354fd4d50146105ac5780636fe3a5671461063b57806370a0823114610664578063771282f6146106b1578063775c46cd146106da5780638fd3ab801461070657806395d89b411461071b57806398e52f9a146107aa578063a6f9dae1146107cd578063a81c3bdf14610806578063a9059cbb1461085b578063b921e163146108b5578063cb7b8673146108d8578063d648a647146108fb578063dd62ed3e14610924578063e28d717b14610990578063ff29507d146109a5575b6102925b6000600560149054906101000a900460ff16151561019457600080fd5b60003414156101a257600080fd5b6006544310156101b157600080fd5b6007544311156101c057600080fd5b6101cc34600b546109ce565b9050600854600954820111156101e157600080fd5b6101ed60095482610a02565b60098190555080600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055503373ffffffffffffffffffffffffffffffffffffffff167f7ba26a0f068612fb882b3272004674d21fed286c2c8c795cf653044690b32db4826040518082815260200191505060405180910390a25b50565b005b341561029f57600080fd5b6102a7610a2d565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156102e85780820151818401525b6020810190506102cc565b50505050905090810190601f1680156103155780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561032e57600080fd5b610363600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050610a66565b604051808215151515815260200191505060405180910390f35b341561038857600080fd5b6103bd600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050610b59565b005b34156103ca57600080fd5b6103d2610cdc565b604051808215151515815260200191505060405180910390f35b34156103f757600080fd5b6103ff610cef565b6040518082815260200191505060405180910390f35b341561042057600080fd5b610474600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050610cf5565b604051808215151515815260200191505060405180910390f35b341561049957600080fd5b6104a1610f76565b6040518082815260200191505060405180910390f35b34156104c257600080fd5b6104ca610f7c565b6040518082815260200191505060405180910390f35b34156104eb57600080fd5b6104f3610f81565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b341561054057600080fd5b610548610fa7565b6040518082815260200191505060405180910390f35b341561056957600080fd5b610571610fad565b005b341561057e57600080fd5b6105aa600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050611043565b005b34156105b757600080fd5b6105bf611140565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156106005780820151818401525b6020810190506105e4565b50505050905090810190601f16801561062d5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b341561064657600080fd5b61064e6111de565b6040518082815260200191505060405180910390f35b341561066f57600080fd5b61069b600480803573ffffffffffffffffffffffffffffffffffffffff169060200190919050506111e4565b6040518082815260200191505060405180910390f35b34156106bc57600080fd5b6106c461122e565b6040518082815260200191505060405180910390f35b34156106e557600080fd5b6107046004808035906020019091908035906020019091905050611234565b005b341561071157600080fd5b6107196112f5565b005b341561072657600080fd5b61072e61155e565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561076f5780820151818401525b602081019050610753565b50505050905090810190601f16801561079c5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34156107b557600080fd5b6107cb6004808035906020019091905050611597565b005b34156107d857600080fd5b610804600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050611662565b005b341561081157600080fd5b61081961173e565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b341561086657600080fd5b61089b600480803573ffffffffffffffffffffffffffffffffffffffff16906020019091908035906020019091905050611764565b604051808215151515815260200191505060405180910390f35b34156108c057600080fd5b6108d660048080359060200190919050506118d2565b005b34156108e357600080fd5b6108f9600480803590602001909190505061199d565b005b341561090657600080fd5b61090e611a22565b6040518082815260200191505060405180910390f35b341561092f57600080fd5b61097a600480803573ffffffffffffffffffffffffffffffffffffffff1690602001909190803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050611a28565b6040518082815260200191505060405180910390f35b341561099b57600080fd5b6109a3611ab0565b005b34156109b057600080fd5b6109b8611bae565b6040518082815260200191505060405180910390f35b600080828402905060008414806109ef57508284828115156109ec57fe5b04145b15156109f757fe5b8091505b5092915050565b6000808284019050838110158015610a1a5750828110155b1515610a2257fe5b8091505b5092915050565b6040805190810160405280600381526020017f57594a000000000000000000000000000000000000000000000000000000000081525081565b600081600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040518082815260200191505060405180910390a3600190505b92915050565b6000600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610bb757600080fd5b6000821415610bc557600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168373ffffffffffffffffffffffffffffffffffffffff161415610bff57600080fd5b610c13610c0b83611bb4565b600b546109ce565b905060085460095482011115610c2857600080fd5b610c3460095482610a02565b60098190555080600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055508273ffffffffffffffffffffffffffffffffffffffff167f1aee3ddc9eba03c98b273cd914e999b78162e1ddd1c022045394f635a469e105826040518082815260200191505060405180910390a25b5b505050565b600560149054906101000a900460ff1681565b60005481565b600081600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410158015610dc2575081600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410155b8015610dce5750600082115b15610f655781600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254019250508190555081600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555081600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825403925050819055508273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a360019050610f6f565b60009050610f6f565b5b9392505050565b60095481565b601281565b600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600b5481565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561100957600080fd5b600560149054906101000a900460ff16151561102457600080fd5b6000600560146101000a81548160ff0219169083151502179055505b5b565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561109f57600080fd5b600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614156110fa57600080fd5b80600560006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b5b50565b60038054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156111d65780601f106111ab576101008083540402835291602001916111d6565b820191906000526020600020905b8154815290600101906020018083116111b957829003601f168201915b505050505081565b600a5481565b6000600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205490505b919050565b60085481565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561129057600080fd5b600560149054906101000a900460ff16156112aa57600080fd5b80821015156112b857600080fd5b81431015156112c657600080fd5b81600681905550806007819055506001600560146101000a81548160ff0219169083151502179055505b5b5050565b600080600560149054906101000a900460ff161561131257600080fd5b600073ffffffffffffffffffffffffffffffffffffffff16600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16141561136e57600080fd5b600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054915060008214156113be57600080fd5b6000600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555061140f600a5483610a02565b600a81905550600560009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1690508073ffffffffffffffffffffffffffffffffffffffff1663ad68ebf733846000604051602001526040518363ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200192505050602060405180830381600087803b15156114e557600080fd5b6102c65a03f115156114f657600080fd5b50505060405180519050151561150b57600080fd5b3373ffffffffffffffffffffffffffffffffffffffff167fa59785389b00cbd19745afbe8d59b28e3161395c6b1e3525861a2b0dede0b90d836040518082815260200191505060405180910390a25b5050565b6040805190810160405280600381526020017f57594a000000000000000000000000000000000000000000000000000000000081525081565b6000600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156115f557600080fd5b6115fe82611bb4565b90506008546009548201111561161357600080fd5b61161f60085482611bc5565b6008819055507f9ecdebfa921d6ab8cecf7259ef30327664ad0d45d32fa3641089b00b533f2eee816040518082815260200191505060405180910390a15b5b5050565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156116be57600080fd5b600073ffffffffffffffffffffffffffffffffffffffff168173ffffffffffffffffffffffffffffffffffffffff1614156116f857600080fd5b80600460006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b5b50565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b600081600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054101580156117b55750600082115b156118c25781600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555081600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a3600190506118cc565b600090506118cc565b5b92915050565b6000600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561193057600080fd5b61193982611bb4565b90506000546008548201111561194e57600080fd5b61195a60085482610a02565b6008819055507ffaabf704b783af9e21c676de8e3e6e0c9c2260dce2ee299437ec9b70151ddaeb816040518082815260200191505060405180910390a15b5b5050565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156119f957600080fd5b6000811415611a0757600080fd5b600b54811415611a1657600080fd5b80600b819055505b5b50565b60065481565b6000600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205490505b92915050565b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515611b0c57600080fd5b60003073ffffffffffffffffffffffffffffffffffffffff16311415611b3157600080fd5b600460009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff166108fc3073ffffffffffffffffffffffffffffffffffffffff16319081150290604051600060405180830381858888f193505050501515611baa57600080fd5b5b5b565b60075481565b60006012600a0a820290505b919050565b600080828410151515611bd457fe5b82840390508091505b50929150505600a165627a7a72305820a98f2a914b14b8c7570fda4c2f11aadfe007ebebaf061e64b49e29556c7b783b0029";

        String address = "0xfc5Ed02BD8Ed12CC3ED03cEf45B5A1b8A000c959";

        Contract contract = new Contract("Pippo", pippo);
        Abi abi1 = AbiExtractor.getInstance().extractAbi(contract);

        Abi abi2 = null;
        try {
            abi2 = AbiDownloader.getInstance().getContractAbi(address);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(new GsonAbi().toJson(abi1));
        System.out.println(new GsonAbi().toJson(abi2));
    }
}
