package main;

import analysers.StoreAccessAfterUnsafeCall;
import parseTree.Contract;
import parseTree.NotSolidityContractException;

public class SecurityAnalyser {

    public static SecurityAnalysisReport analyse(Contract contract){
        SecurityAnalysisReport report = new SecurityAnalysisReport(contract);
        StoreAccessAfterUnsafeCall.analyse(contract, report);
        report.stopTimer();
        return report;
    }

    public static void main(String[] args) {
        String bytecode = "608060405234801561001057600080fd5b50610396806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c8063102ccd9214610051578063271625f71461007f578063cc06463214610089578063cd482e7714610093575b600080fd5b61007d6004803603602081101561006757600080fd5b810190808035906020019092919050505061009d565b005b61008761019d565b005b61009161023f565b005b61009b6102c7565b005b806000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410156100e857600080fd5b3373ffffffffffffffffffffffffffffffffffffffff168160405180600001905060006040518083038185875af1925050503d8060008114610146576040519150601f19603f3d011682016040523d82523d6000602084013e61014b565b606091505b505050806000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555050565b600073888888888888888888888888888888888888888890508073ffffffffffffffffffffffffffffffffffffffff166108fc61aaaa9081150290604051600060405180830381858888f193505050505061aaaa6000808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555050565b3373ffffffffffffffffffffffffffffffffffffffff166108fc6188889081150290604051600060405180830381858888f19350505050506188886000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282540392505081905550565b3373ffffffffffffffffffffffffffffffffffffffff166108fc6199999081150290604051600060405180830381858888f1935050505015801561030f573d6000803e3d6000fd5b506199996000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555056fea2646970667358221220df8e9a0b8321357ef3ec613ee7864e94116500d68b9bb1dba6f439f8c9dc76bc64736f6c634300060c0033";
        String etherScanTest = "60806040526006805460a060020a62ffffff02191690553480156200002357600080fd5b5060068054600160a060020a031916331790556200004964010000000062000077810204565b6200007173c4ca16b889564729324447e369a32602d012d191640100000000620000f6810204565b62000197565b600654760100000000000000000000000000000000000000000000900460ff1615620000a257600080fd5b6006805460b060020a60ff0219167601000000000000000000000000000000000000000000001790556040517f5daa87a0e9463431830481fd4b6e3403442dfb9a12b9c07597e9f61d50b633c890600090a1565b600654600160a060020a031633146200010e57600080fd5b620001228164010000000062000125810204565b50565b600160a060020a03811615156200013b57600080fd5b600654604051600160a060020a038084169216907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a360068054600160a060020a031916600160a060020a0392909216919091179055565b611a4780620001a76000396000f3006080604052600436106101d65763ffffffff7c01000000000000000000000000000000000000000000000000000000006000350416623fd35a81146101db57806302d6f7301461020457806305d2035b1461024c57806306fdde0314610261578063095ea7b3146102eb5780630bb2cd6b1461030f578063158ef93e1461034057806317a950ac1461035557806318160ddd14610388578063188214001461039d57806323b872dd146103b25780632a905318146103dc578063313ce567146103f15780633be1e9521461041c5780633f4ba83a1461044f57806340c10f191461046457806342966c681461048857806356780085146104a05780635b7f415c146104b55780635be7fde8146104ca5780635c975abb146104df57806366188463146104f457806366a92cda1461051857806370a082311461052d578063715018a61461054e578063726a431a146105635780637d64bcb4146105945780638456cb59146105a95780638da5cb5b146105be57806395d89b41146105d3578063a9059cbb146105e8578063a9aad58c1461060c578063ca63b5b814610621578063cf3b196714610642578063d73dd62314610657578063d8aeedf51461067b578063dd62ed3e1461069c578063f2fde38b146106c3575b600080fd5b3480156101e757600080fd5b506101f06106e4565b604080519115158252519081900360200190f35b34801561021057600080fd5b50610228600160a060020a03600435166024356106e9565b6040805167ffffffffffffffff909316835260208301919091528051918290030190f35b34801561025857600080fd5b506101f0610776565b34801561026d57600080fd5b50610276610786565b6040805160208082528351818301528351919283929083019185019080838360005b838110156102b0578181015183820152602001610298565b50505050905090810190601f1680156102dd5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156102f757600080fd5b506101f0600160a060020a03600435166024356107bd565b34801561031b57600080fd5b506101f0600160a060020a036004351660243567ffffffffffffffff60443516610823565b34801561034c57600080fd5b506101f06109c1565b34801561036157600080fd5b50610376600160a060020a03600435166109e4565b60408051918252519081900360200190f35b34801561039457600080fd5b506103766109f5565b3480156103a957600080fd5b506102766109fb565b3480156103be57600080fd5b506101f0600160a060020a0360043581169060243516604435610a32565b3480156103e857600080fd5b50610276610a5f565b3480156103fd57600080fd5b50610406610a96565b6040805160ff9092168252519081900360200190f35b34801561042857600080fd5b5061044d600160a060020a036004351660243567ffffffffffffffff60443516610a9b565b005b34801561045b57600080fd5b5061044d610c0f565b34801561047057600080fd5b506101f0600160a060020a0360043516602435610c88565b34801561049457600080fd5b5061044d600435610d80565b3480156104ac57600080fd5b50610376610d8d565b3480156104c157600080fd5b50610376610d99565b3480156104d657600080fd5b50610376610d9e565b3480156104eb57600080fd5b506101f0610e03565b34801561050057600080fd5b506101f0600160a060020a0360043516602435610e13565b34801561052457600080fd5b5061044d610f03565b34801561053957600080fd5b50610376600160a060020a03600435166110a6565b34801561055a57600080fd5b5061044d6110cf565b34801561056f57600080fd5b5061057861113d565b60408051600160a060020a039092168252519081900360200190f35b3480156105a057600080fd5b506101f0611155565b3480156105b557600080fd5b5061044d6111d9565b3480156105ca57600080fd5b50610578611257565b3480156105df57600080fd5b50610276611266565b3480156105f457600080fd5b506101f0600160a060020a036004351660243561129d565b34801561061857600080fd5b506101f06112c8565b34801561062d57600080fd5b50610376600160a060020a03600435166112cd565b34801561064e57600080fd5b50610406610d99565b34801561066357600080fd5b506101f0600160a060020a0360043516602435611353565b34801561068757600080fd5b50610376600160a060020a03600435166113ec565b3480156106a857600080fd5b50610376600160a060020a0360043581169060243516611407565b3480156106cf57600080fd5b5061044d600160a060020a0360043516611432565b600181565b600080805b836001018110156107425760036000610711878667ffffffffffffffff16611452565b815260208101919091526040016000205467ffffffffffffffff16925082151561073a5761076e565b6001016106ee565b6004600061075a878667ffffffffffffffff16611452565b815260208101919091526040016000205491505b509250929050565b60065460a060020a900460ff1681565b60408051808201909152600f81527f556e636f6d6d6f6e6e20546f6b656e0000000000000000000000000000000000602082015290565b336000818152600260209081526040808320600160a060020a038716808552908352818420869055815186815291519394909390927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925928290030190a350600192915050565b6006546000908190600160a060020a0316331461083f57600080fd5b60065460a060020a900460ff161561085657600080fd5b600154610869908563ffffffff61148616565b6001556108808567ffffffffffffffff8516611452565b6000818152600460205260409020549091506108a2908563ffffffff61148616565b600082815260046020908152604080832093909355600160a060020a03881682526005905220546108d9908563ffffffff61148616565b600160a060020a0386166000908152600560205260409020556108fc8584611493565b604080518581529051600160a060020a038716917f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d4121396885919081900360200190a26040805167ffffffffffffffff85168152602081018690528151600160a060020a038816927f2ecd071e4d10ed2221b04636ed0724cce66a873aa98c1a31b4bb0e6846d3aab4928290030190a2604080518581529051600160a060020a0387169133916000805160206119fc8339815191529181900360200190a3506001949350505050565b600654760100000000000000000000000000000000000000000000900460ff1681565b60006109ef8261162d565b92915050565b60015490565b60408051808201909152600f81527f556e636f6d6d6f6e6e20546f6b656e0000000000000000000000000000000000602082015281565b60065460009060a860020a900460ff1615610a4c57600080fd5b610a57848484611648565b949350505050565b60408051808201909152600381527f5543520000000000000000000000000000000000000000000000000000000000602082015281565b601290565b6000600160a060020a0384161515610ab257600080fd5b33600090815260208190526040902054831115610ace57600080fd5b33600090815260208190526040902054610aee908463ffffffff6117ad16565b33600090815260208190526040902055610b128467ffffffffffffffff8416611452565b600081815260046020526040902054909150610b34908463ffffffff61148616565b600082815260046020908152604080832093909355600160a060020a0387168252600590522054610b6b908463ffffffff61148616565b600160a060020a038516600090815260056020526040902055610b8e8483611493565b604080518481529051600160a060020a0386169133916000805160206119fc8339815191529181900360200190a36040805167ffffffffffffffff84168152602081018590528151600160a060020a038716927f2ecd071e4d10ed2221b04636ed0724cce66a873aa98c1a31b4bb0e6846d3aab4928290030190a250505050565b600654600160a060020a03163314610c2657600080fd5b60065460a860020a900460ff161515610c3e57600080fd5b6006805475ff000000000000000000000000000000000000000000191690556040517f7805862f689e2f13df9f062ff482ad3ad112aca9e0847911ed832e158c525b3390600090a1565b600654600090600160a060020a03163314610ca257600080fd5b60065460a060020a900460ff1615610cb957600080fd5b600154610ccc908363ffffffff61148616565b600155600160a060020a038316600090815260208190526040902054610cf8908363ffffffff61148616565b600160a060020a03841660008181526020818152604091829020939093558051858152905191927f0f6798a560793a54c3bcfe86a93cde1e73087d944c0ea20544137d412139688592918290030190a2604080518381529051600160a060020a038516916000916000805160206119fc8339815191529181900360200190a350600192915050565b610d8a33826117bf565b50565b670de0b6b3a764000081565b601281565b6000806000610dae3360006106e9565b67ffffffffffffffff909116925090505b8115801590610dcd57508142115b15610dfe57610dda610f03565b91820191610de93360006106e9565b67ffffffffffffffff90911692509050610dbf565b505090565b60065460a860020a900460ff1681565b336000908152600260209081526040808320600160a060020a038616845290915281205480831115610e6857336000908152600260209081526040808320600160a060020a0388168452909152812055610e9d565b610e78818463ffffffff6117ad16565b336000908152600260209081526040808320600160a060020a03891684529091529020555b336000818152600260209081526040808320600160a060020a0389168085529083529281902054815190815290519293927f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925929181900390910190a35060019392505050565b6000806000806000610f16336000611452565b60008181526003602052604090205490955067ffffffffffffffff169350831515610f4057600080fd5b8367ffffffffffffffff164267ffffffffffffffff16111515610f6257600080fd5b610f76338567ffffffffffffffff16611452565b600081815260036020908152604080832054600483528184208054908590553385529284905292205492955067ffffffffffffffff90911693509150610fc2908263ffffffff61148616565b3360009081526020818152604080832093909355600590522054610fec908263ffffffff6117ad16565b3360009081526005602052604090205567ffffffffffffffff8216151561102f576000858152600360205260409020805467ffffffffffffffff19169055611069565b600085815260036020526040808220805467ffffffffffffffff861667ffffffffffffffff19918216179091558583529120805490911690555b60408051828152905133917fb21fb52d5749b80f3182f8c6992236b5e5576681880914484d7f4c9b062e619e919081900360200190a25050505050565b600160a060020a0381166000908152600560205260408120546110c88361162d565b0192915050565b600654600160a060020a031633146110e657600080fd5b600654604051600160a060020a03909116907ff8df31144d9c2f0f6b59d69b8b98abd5459d07f2742c4df920b25aae33c6482090600090a26006805473ffffffffffffffffffffffffffffffffffffffff19169055565b73c4ca16b889564729324447e369a32602d012d19181565b600654600090600160a060020a0316331461116f57600080fd5b60065460a060020a900460ff161561118657600080fd5b6006805474ff0000000000000000000000000000000000000000191660a060020a1790556040517fae5184fba832cb2b1f702aca6117b8d265eaf03ad33eb133f19dde0f5920fa0890600090a150600190565b600654600160a060020a031633146111f057600080fd5b60065460a860020a900460ff161561120757600080fd5b6006805475ff000000000000000000000000000000000000000000191660a860020a1790556040517f6985a02210a168e66602d3235cb6db0e70f92b3ba4d376a33c0f3d9434bff62590600090a1565b600654600160a060020a031681565b60408051808201909152600381527f5543520000000000000000000000000000000000000000000000000000000000602082015290565b60065460009060a860020a900460ff16156112b757600080fd5b6112c183836118ae565b9392505050565b600081565b600080600360006112df856000611452565b815260208101919091526040016000205467ffffffffffffffff1690505b67ffffffffffffffff81161561134d576001909101906003600061132b8567ffffffffffffffff8516611452565b815260208101919091526040016000205467ffffffffffffffff1690506112fd565b50919050565b336000908152600260209081526040808320600160a060020a0386168452909152812054611387908363ffffffff61148616565b336000818152600260209081526040808320600160a060020a0389168085529083529281902085905580519485525191937f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925929081900390910190a350600192915050565b600160a060020a031660009081526005602052604090205490565b600160a060020a03918216600090815260026020908152604080832093909416825291909152205490565b600654600160a060020a0316331461144957600080fd5b610d8a8161197d565b6801000000000000000091909102177f57495348000000000000000000000000000000000000000000000000000000001790565b818101828110156109ef57fe5b6000808080804267ffffffffffffffff8716116114af57600080fd5b6114c3878767ffffffffffffffff16611452565b94506114d0876000611452565b60008181526003602052604090205490945067ffffffffffffffff169250821515611523576000848152600360205260409020805467ffffffffffffffff191667ffffffffffffffff8816179055611624565b611537878467ffffffffffffffff16611452565b91505b67ffffffffffffffff83161580159061156657508267ffffffffffffffff168667ffffffffffffffff16115b1561159f575060008181526003602052604090205490925067ffffffffffffffff908116918391166115988784611452565b915061153a565b8267ffffffffffffffff168667ffffffffffffffff1614156115c057611624565b67ffffffffffffffff8316156115fa576000858152600360205260409020805467ffffffffffffffff191667ffffffffffffffff85161790555b6000848152600360205260409020805467ffffffffffffffff191667ffffffffffffffff88161790555b50505050505050565b600160a060020a031660009081526020819052604090205490565b6000600160a060020a038316151561165f57600080fd5b600160a060020a03841660009081526020819052604090205482111561168457600080fd5b600160a060020a03841660009081526002602090815260408083203384529091529020548211156116b457600080fd5b600160a060020a0384166000908152602081905260409020546116dd908363ffffffff6117ad16565b600160a060020a038086166000908152602081905260408082209390935590851681522054611712908363ffffffff61148616565b600160a060020a03808516600090815260208181526040808320949094559187168152600282528281203382529091522054611754908363ffffffff6117ad16565b600160a060020a03808616600081815260026020908152604080832033845282529182902094909455805186815290519287169391926000805160206119fc833981519152929181900390910190a35060019392505050565b6000828211156117b957fe5b50900390565b600160a060020a0382166000908152602081905260409020548111156117e457600080fd5b600160a060020a03821660009081526020819052604090205461180d908263ffffffff6117ad16565b600160a060020a038316600090815260208190526040902055600154611839908263ffffffff6117ad16565b600155604080518281529051600160a060020a038416917fcc16f5dbb4873280815c1ee09dbd06736cffcc184412cf7a71a0fdb75d397ca5919081900360200190a2604080518281529051600091600160a060020a038516916000805160206119fc8339815191529181900360200190a35050565b6000600160a060020a03831615156118c557600080fd5b336000908152602081905260409020548211156118e157600080fd5b33600090815260208190526040902054611901908363ffffffff6117ad16565b3360009081526020819052604080822092909255600160a060020a03851681522054611933908363ffffffff61148616565b600160a060020a038416600081815260208181526040918290209390935580518581529051919233926000805160206119fc8339815191529281900390910190a350600192915050565b600160a060020a038116151561199257600080fd5b600654604051600160a060020a038084169216907f8be0079c531659141344cd1fd0a4f28419497f9722a3daafe3b4186f6b6457e090600090a36006805473ffffffffffffffffffffffffffffffffffffffff1916600160a060020a03929092169190911790555600ddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3efa165627a7a723058205890fc256d0a1324996dafe5f9cf1774481eafdc0015b9fdc9de63c6f8b062c00029";
        try {
            Contract contract = new Contract("OttOtt", etherScanTest, false);
            SecurityAnalysisReport report = analyse(contract);
            System.out.println(report);
        } catch (NotSolidityContractException e) {
            e.printStackTrace();
        }
    }

}
