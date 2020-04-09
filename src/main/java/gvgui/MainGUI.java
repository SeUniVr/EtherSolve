package gvgui;

import SolidityInfo.SolidityVersionUnknownException;
import analyzer.ABI.Abi;
import analyzer.ABI.AbiExtractor;
import parseTree.Contract;

public class MainGUI {

    public static void main(String[] args){
        String ottOtt = "608060405260aa60005534801561001557600080fd5b50610184806100256000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c80634b3df200146100515780635bfd987d1461006f578063c2985578146100b1578063febb0f7e146100cf575b600080fd5b6100596100ed565b6040518082815260200191505060405180910390f35b61009b6004803603602081101561008557600080fd5b810190808035906020019092919050505061011d565b6040518082815260200191505060405180910390f35b6100b961012a565b6040518082815260200191505060405180910390f35b6100d761013c565b6040518082815260200191505060405180910390f35b6000806000815480929190600101919050555060aa600054141561011557600054905061011a565b600080fd5b90565b6000816088019050919050565b600061013761cccc61011d565b905090565b600061014961dddd61011d565b90509056fea2646970667358221220e619b234c1887f9b10b567ee21364dbf523a19001c8c47a33049907c0398563164736f6c63430006040033";
        String pippo = "608060405234801561001057600080fd5b5061020c806100206000396000f3fe608060405234801561001057600080fd5b506004361061005b5760003560e01c80632a1bbc3414610077578063b54ac1d614610095578063d96073cf146100c3578063e7a721cf14610116578063ee7563111461015f5761005c565b5b60006188889050619999811015610074576001810190505b50005b61007f6101a1565b6040518082815260200191505060405180910390f35b6100c1600480360360208110156100ab57600080fd5b81019080803590602001909291905050506101ab565b005b6100f9600480360360408110156100d957600080fd5b8101908080359060200190929190803590602001909291905050506101ae565b604051808381526020018281526020019250505060405180910390f35b6101426004803603602081101561012c57600080fd5b81019080803590602001909291905050506101be565b604051808381526020018281526020019250505060405180910390f35b61018b6004803603602081101561017557600080fd5b81019080803590602001909291905050506101cc565b6040518082815260200191505060405180910390f35b600061cccc905090565b50565b6000808284915091509250929050565b600080828391509150915091565b600081905091905056fea2646970667358221220ba20c110ddf955d3e57a83651b5ab836206afdcc43777d1cfb86dff69eafa8aa64736f6c63430006010033";
        String ballot = "608060405234801561001057600080fd5b50604051610e27380380610e278339818101604052602081101561003357600080fd5b810190808051604051939291908464010000000082111561005357600080fd5b8382019150602082018581111561006957600080fd5b825186602082028301116401000000008211171561008657600080fd5b8083526020830192505050908051906020019060200280838360005b838110156100bd5780820151818401526020810190506100a2565b50505050905001604052505050336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060018060008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000018190555060008090505b81518110156101f8576002604051806040016040528084848151811061019a57fe5b6020026020010151815260200160008152509080600181540180825580915050600190039060005260206000209060020201600090919091909150600082015181600001556020820151816001015550508080600101915050610178565b5050610c1e806102096000396000f3fe608060405234801561001057600080fd5b50600436106100885760003560e01c8063609ff1bd1161005b578063609ff1bd146101925780639e7b8d61146101b0578063a3ec138d146101f4578063e2ba53f01461029157610088565b80630121b93f1461008d578063013cf08b146100bb5780632e4176cf146101045780635c19a95c1461014e575b600080fd5b6100b9600480360360208110156100a357600080fd5b81019080803590602001909291905050506102af565b005b6100e7600480360360208110156100d157600080fd5b810190808035906020019092919050505061044c565b604051808381526020018281526020019250505060405180910390f35b61010c61047d565b604051808273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200191505060405180910390f35b6101906004803603602081101561016457600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506104a2565b005b61019a6108be565b6040518082815260200191505060405180910390f35b6101f2600480360360208110156101c657600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610935565b005b6102366004803603602081101561020a57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610b36565b60405180858152602001841515151581526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200182815260200194505050505060405180910390f35b610299610b93565b6040518082815260200191505060405180910390f35b6000600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020905060008160000154141561036d576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260148152602001807f486173206e6f20726967687420746f20766f746500000000000000000000000081525060200191505060405180910390fd5b8060010160009054906101000a900460ff16156103f2576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252600e8152602001807f416c726561647920766f7465642e00000000000000000000000000000000000081525060200191505060405180910390fd5b60018160010160006101000a81548160ff02191690831515021790555081816002018190555080600001546002838154811061042a57fe5b9060005260206000209060020201600101600082825401925050819055505050565b6002818154811061045957fe5b90600052602060002090600202016000915090508060000154908060010154905082565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b6000600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002090508060010160009054906101000a900460ff161561056a576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260128152602001807f596f7520616c726561647920766f7465642e000000000000000000000000000081525060200191505060405180910390fd5b3373ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff16141561060c576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601e8152602001807f53656c662d64656c65676174696f6e20697320646973616c6c6f7765642e000081525060200191505060405180910390fd5b5b600073ffffffffffffffffffffffffffffffffffffffff16600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060010160019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16146107af57600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060010160019054906101000a900473ffffffffffffffffffffffffffffffffffffffff1691503373ffffffffffffffffffffffffffffffffffffffff168273ffffffffffffffffffffffffffffffffffffffff1614156107aa576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260198152602001807f466f756e64206c6f6f7020696e2064656c65676174696f6e2e0000000000000081525060200191505060405180910390fd5b61060d565b60018160010160006101000a81548160ff021916908315150217905550818160010160016101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600160008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002090508060010160009054906101000a900460ff16156108a2578160000154600282600201548154811061087f57fe5b9060005260206000209060020201600101600082825401925050819055506108b9565b816000015481600001600082825401925050819055505b505050565b6000806000905060008090505b6002805490508110156109305781600282815481106108e657fe5b9060005260206000209060020201600101541115610923576002818154811061090b57fe5b90600052602060002090600202016001015491508092505b80806001019150506108cb565b505090565b6000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16146109da576040517f08c379a0000000000000000000000000000000000000000000000000000000008152600401808060200182810382526028815260200180610bc16028913960400191505060405180910390fd5b600160008273ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060010160009054906101000a900460ff1615610a9d576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260188152602001807f54686520766f74657220616c726561647920766f7465642e000000000000000081525060200191505060405180910390fd5b6000600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000015414610aec57600080fd5b60018060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000018190555050565b60016020528060005260406000206000915090508060000154908060010160009054906101000a900460ff16908060010160019054906101000a900473ffffffffffffffffffffffffffffffffffffffff16908060020154905084565b60006002610b9f6108be565b81548110610ba957fe5b90600052602060002090600202016000015490509056fe4f6e6c79206368616972706572736f6e2063616e206769766520726967687420746f20766f74652ea264697066735822122058766ff2967b30f7c3cda5eaff3f0ee81b91e91abde708241920de181187750864736f6c63430006010033";
        String testString = "608060405234801561001057600080fd5b50610132806100206000396000f3fe6080604052348015600f57600080fd5b506004361060325760003560e01c80630423a132146037578063a7630622146076575b600080fd5b606060048036036020811015604b57600080fd5b810190808035906020019092919050505060b5565b6040518082815260200191505060405180910390f35b609f60048036036020811015608a57600080fd5b810190808035906020019092919050505060c5565b6040518082815260200191505060405180910390f35b600060be8260d5565b9050919050565b600060ce8260b5565b9050919050565b600080600090505b8281101560f657600183019150808060010191505060dd565b5091905056fea2646970667358221220ff8ce6e9ddd9170382e965f1d6b949d9ba02c3ed9491abb3d46aa20aea66fe1764736f6c63430006010033";
        String ballotSimple = "608060405234801561001057600080fd5b50610180806100206000396000f3fe608060405234801561001057600080fd5b50600436106100415760003560e01c8063013cf08b14610046578063609ff1bd1461008f578063e2ba53f0146100ad575b600080fd5b6100726004803603602081101561005c57600080fd5b81019080803590602001909291905050506100b7565b604051808381526020018281526020019250505060405180910390f35b6100976100e8565b6040518082815260200191505060405180910390f35b6100b561013f565b005b600081815481106100c457fe5b90600052602060002090600202016000915090508060000154908060010154905082565b6000806000905080600080815481106100fd57fe5b906000526020600020906002020160010154111561013b576000808154811061012257fe5b9060005260206000209060020201600101549050600091505b5090565b6101476100e8565b5056fea26469706673582212208a437aac18cadcb8a18fd12b32ed9ab645e042c61184e723ca80cca3a44a7efd64736f6c63430006010033";
        String empty = "6080604052348015600f57600080fd5b50603f80601d6000396000f3fe6080604052600080fdfea26469706673582212208960952bc3a2554d91c419447abb95d474a8d5d3273e5fb769dded7df97878d464736f6c63430006040033";
        String publicFields = "60806040526188886000557fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff777860015534801561003a57600080fd5b5060b1806100496000396000f3fe6080604052348015600f57600080fd5b506004361060325760003560e01c80633df4ddf41460375780635a8ac02d146053575b600080fd5b603d606f565b6040518082815260200191505060405180910390f35b60596075565b6040518082815260200191505060405180910390f35b60005481565b6001548156fea26469706673582212207b2646cf1766be8c4d97e1485cac5be4671899a64e79807b45ea166e835d9ffe64736f6c63430006040033";
        String contractBytecode = "608060405234801561001057600080fd5b506102d3806100206000396000f3fe608060405234801561001057600080fd5b50600436106100505760003560e01c80630ce68a19146100e75780633858335814610115578063c2bc2efc14610133578063dfeb4ccc1461018b57610051565b5b60003390508073ffffffffffffffffffffffffffffffffffffffff166108fc6100786101e3565b6000808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054019081150290604051600060405180830381858888f193505050501580156100e3573d6000803e3d6000fd5b5050005b610113600480360360208110156100fd57600080fd5b81019080803590602001909291905050506101ee565b005b61011d6101e3565b6040518082815260200191505060405180910390f35b6101756004803603602081101561014957600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061023d565b6040518082815260200191505060405180910390f35b6101cd600480360360208110156101a157600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610285565b6040518082815260200191505060405180910390f35b600062088888905090565b806000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254019250508190555050565b60008060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b6000602052806000526040600020600091509050548156fea2646970667358221220ee980f3a83bce481c1bf53e79d1ba45d2162c4c03681da626d85ac2347668c4364736f6c63430006040033";
        String testFunctionsNew = "608060405234801561001057600080fd5b506103e9806100206000396000f3fe60806040526004361061007f5760003560e01c80639c34a15f1161004e5780639c34a15f14610203578063baf7160914610248578063bf06dbf11461030d578063cd580ff31461035c57610080565b806305a0581e146100825780630dbe671f146100bf57806376662a59146100d657806382e1cea11461013157610080565b5b005b34801561008e57600080fd5b506100bd600480360360208110156100a557600080fd5b81019080803515159060200190929190505050610397565b005b3480156100cb57600080fd5b506100d461039a565b005b3480156100e257600080fd5b5061012f600480360360408110156100f957600080fd5b8101908080359060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061039c565b005b34801561013d57600080fd5b506102016004803603604081101561015457600080fd5b81019080803590602001909291908035906020019064010000000081111561017b57600080fd5b82018360208201111561018d57600080fd5b803590602001918460018302840111640100000000831117156101af57600080fd5b91908080601f016020809104026020016040519081016040528093929190818152602001838380828437600081840152601f19601f8201169050808301925050505050505091929192905050506103a0565b005b34801561020f57600080fd5b506102466004803603604081101561022657600080fd5b8101908080359060200190929190803590602001909291905050506103a4565b005b34801561025457600080fd5b5061030b6004803603602081101561026b57600080fd5b810190808035906020019064010000000081111561028857600080fd5b82018360208201111561029a57600080fd5b803590602001918460208302840111640100000000831117156102bc57600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600081840152601f19601f8201169050808301925050505050505091929192905050506103a8565b005b34801561031957600080fd5b5061035a6004803603606081101561033057600080fd5b810190808035906020019092919080359060200190929190803590602001909291905050506103ab565b005b34801561036857600080fd5b506103956004803603602081101561037f57600080fd5b81019080803590602001909291905050506103b0565b005b50565b565b5050565b5050565b5050565b50565b505050565b5056fea2646970667358221220a5b442f7c7a8e4e3bcd1aa0c26a50d8d89b65729f5377ca5ba4867dae2e66dd364736f6c63430006050033";
        String testFunctionsOld = "6060604052341561000c57fe5b5b61028d8061001c6000396000f3006060604052361561008c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806305a0581e146100955780630dbe671f146100b757806376662a59146100c957806382e1cea1146101085780639c34a15f1461016b578063baf7160914610194578063bf06dbf1146101eb578063cd580ff31461021d575b6100935b5b565b005b341561009d57fe5b6100b56004808035151590602001909190505061023d565b005b34156100bf57fe5b6100c7610241565b005b34156100d157fe5b610106600480803590602001909190803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610244565b005b341561011057fe5b610169600480803590602001909190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091905050610249565b005b341561017357fe5b610192600480803590602001909190803590602001909190505061024e565b005b341561019c57fe5b6101e9600480803590602001908201803590602001908080602002602001604051908101604052809392919081815260200183836020028082843782019150505050505091905050610253565b005b34156101f357fe5b61021b6004808035906020019091908035906020019091908035906020019091905050610257565b005b341561022557fe5b61023b600480803590602001909190505061025d565b005b5b50565b5b565b5b5050565b5b5050565b5b5050565b5b50565b5b505050565b5b505600a165627a7a72305820822dfa4b0c809705a931f8a0a68e337844fb2b0e8edd653e72caacb78b5d31140029";
        String dao = "608060405262278d006008553480156200001857600080fd5b5060405162001dff38038062001dff8339810180604052810190808051820192919060200180519060200190929190805182019291905050506000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555060006003819055508260009080519060200190620000b6929190620000f4565b508060019080519060200190620000cf929190620000f4565b5081600260006101000a81548160ff021916908360ff160217905550505050620001a3565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200013757805160ff191683800117855562000168565b8280016001018555821562000168579182015b82811115620001675782518255916020019190600101906200014a565b5b5090506200017791906200017b565b5090565b620001a091905b808211156200019c57600081600090555060010162000182565b5090565b90565b611c4c80620001b36000396000f300608060405260043610610112576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806306fdde0314610272578063095ea7b31461030257806318160ddd1461036757806323b872dd1461039257806327b380f314610417578063313ce56714610475578063378dc3dc146104a6578063441d6a61146104d15780634c9f66c7146104fc57806354fd4d501461052757806370a08231146105b757806372a2d90c1461060e5780638dd7e44b1461063057806395d89b411461065f57806399a5d747146106ef578063a9059cbb14610730578063cae9ca511461077d578063d2d7231f14610828578063dcc6762c14610869578063dd62ed3e14610880575b600080600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000154111561016357600080fd5b349050600081141561017457600080fd5b80600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282540192505081905550806003600082825401925050819055503373ffffffffffffffffffffffffffffffffffffffff1660007fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040518082815260200191505060405180910390a33373ffffffffffffffffffffffffffffffffffffffff167f2da466a7b24304f47e87fa2e1e5a81b9831ce54fec19055ce277ca2f39ba42c4826040518082815260200191505060405180910390a250005b34801561027e57600080fd5b506102876108f7565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156102c75780820151818401526020810190506102ac565b50505050905090810190601f1680156102f45780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b34801561030e57600080fd5b5061034d600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610995565b604051808215151515815260200191505060405180910390f35b34801561037357600080fd5b5061037c610b6e565b6040518082815260200191505060405180910390f35b34801561039e57600080fd5b506103fd600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610b74565b604051808215151515815260200191505060405180910390f35b34801561042357600080fd5b50610458600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610f0b565b604051808381526020018281526020019250505060405180910390f35b34801561048157600080fd5b5061048a610f2f565b604051808260ff1660ff16815260200191505060405180910390f35b3480156104b257600080fd5b506104bb610f42565b6040518082815260200191505060405180910390f35b3480156104dd57600080fd5b506104e6610f47565b6040518082815260200191505060405180910390f35b34801561050857600080fd5b50610511610f4d565b6040518082815260200191505060405180910390f35b34801561053357600080fd5b5061053c610f53565b6040518080602001828103825283818151815260200191508051906020019080838360005b8381101561057c578082015181840152602081019050610561565b50505050905090810190601f1680156105a95780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156105c357600080fd5b506105f8600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610f8c565b6040518082815260200191505060405180910390f35b610616610fa4565b604051808215151515815260200191505060405180910390f35b34801561063c57600080fd5b50610645611129565b604051808215151515815260200191505060405180910390f35b34801561066b57600080fd5b50610674611377565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156106b4578082015181840152602081019050610699565b50505050905090810190601f1680156106e15780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b3480156106fb57600080fd5b5061071a60048036038101908080359060200190929190505050611415565b6040518082815260200191505060405180910390f35b34801561073c57600080fd5b5061077b600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050611430565b005b34801561078957600080fd5b5061080e600480360381019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091929192905050506116ac565b604051808215151515815260200191505060405180910390f35b34801561083457600080fd5b50610853600480360381019080803590602001909291905050506118cc565b6040518082815260200191505060405180910390f35b34801561087557600080fd5b5061087e6118fc565b005b34801561088c57600080fd5b506108e1600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611a85565b6040518082815260200191505060405180910390f35b60008054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561098d5780601f106109625761010080835404028352916020019161098d565b820191906000526020600020905b81548152906001019060200180831161097057829003601f168201915b505050505081565b600080600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000015411156109e657600080fd5b60008214158015610a7457506000600560003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205414155b15610a7e57600080fd5b81600560003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040518082815260200191505060405180910390a36001905092915050565b60035481565b600080600660008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600001541115610bc557600080fd5b6000600660008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600001541115610c1557600080fd5b81600460008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020541015610c6157600080fd5b600460008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205482600460008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054011015610cee57600080fd5b600560008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054821115610d7757600080fd5b81600460008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555081600460008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254019250508190555081600560008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825403925050819055508273ffffffffffffffffffffffffffffffffffffffff168473ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a3600190509392505050565b60066020528060005260406000206000915090508060000154908060010154905082565b600260009054906101000a900460ff1681565b600081565b60085481565b60075481565b6040805190810160405280600881526020017f4844414f20302e3700000000000000000000000000000000000000000000000081525081565b60046020528060005260406000206000915090505481565b600080600080600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600001541115610ff857600080fd5b600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549150600082141561104857600080fd5b61105182611415565b905080341415156110af573373ffffffffffffffffffffffffffffffffffffffff167f4b02e32836ab61e09520c2fa7a744654ae1105fbc64fd963db54ccaeedcb26a4826040518082815260200191505060405180910390a2600080fd5b346007600082825401925050819055506110c96000611aaa565b3373ffffffffffffffffffffffffffffffffffffffff167f05de6288c7d47933a7195ba55a4ebbbdeb6c7ddbc12c83e70d2842254db165c2836000604051808381526020018281526020019250505060405180910390a260019250505090565b600080600080600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020925060008360000154141561118257600080fd5b4260085484600001540111156111f3573373ffffffffffffffffffffffffffffffffffffffff167f17a2aaa48e27a928dad797a90a80e37151e1d04dcffaa02d3d8ce8eba4342fa542600854866000015401036040518082815260200191505060405180910390a260009350611371565b600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060010154915061124583600101546118cc565b90506000600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600001819055506000600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060010181905550600081111561130d57600754816007540311156112fb57600060078190555061130c565b806007600082825403925050819055505b5b61131681611aaa565b3373ffffffffffffffffffffffffffffffffffffffff167f05de6288c7d47933a7195ba55a4ebbbdeb6c7ddbc12c83e70d2842254db165c28383604051808381526020018281526020019250505060405180910390a2600193505b50505090565b60018054600181600116156101000203166002900480601f01602080910402602001604051908101604052809291908181526020018280546001816001161561010002031660029004801561140d5780601f106113e25761010080835404028352916020019161140d565b820191906000526020600020905b8154815290600101906020018083116113f057829003601f168201915b505050505081565b60008060648381151561142457fe5b04905080915050919050565b6000600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000154111561148057600080fd5b80600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410156114cc57600080fd5b600460008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205481600460008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205401101561155957600080fd5b6000600660008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000015411156115a957600080fd5b80600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555080600460008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055508173ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040518082815260200191505060405180910390a35050565b600080600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000015411156116fd57600080fd5b6117078484610995565b151561171657600090506118c5565b8373ffffffffffffffffffffffffffffffffffffffff1660405180807f72656365697665417070726f76616c28616464726573732c75696e743235362c81526020017f616464726573732c627974657329000000000000000000000000000000000000815250602e01905060405180910390207c01000000000000000000000000000000000000000000000000000000009004338530866040518563ffffffff167c0100000000000000000000000000000000000000000000000000000000028152600401808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018481526020018373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001828051906020019080838360005b8381101561186f578082015181840152602081019050611854565b50505050905090810190601f16801561189c5780820380516001836020036101000a031916815260200191505b509450505050506000604051808303816000875af19250505015156118c057600080fd5b600190505b9392505050565b60008060009050600060075411156118f35760035483600754028115156118ef57fe5b0490505b80915050919050565b6000600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060000154111561194c57600080fd5b3373ffffffffffffffffffffffffffffffffffffffff167f731bed8bd2f1bca152ccc18462478d1d39325ffb89617c598d1b54fa34570fb0600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020546040518082815260200191505060405180910390a26040805190810160405280428152602001600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054815250600660003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000820151816000015560208201518160010155905050565b6005602052816000526040600020602052806000526040600020600091509150505481565b6000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205490506000811415611afc57600080fd5b3073ffffffffffffffffffffffffffffffffffffffff16318282011115611b2257600080fd5b6000600460003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000208190555080600354036003541015611b7a57600080fd5b8060036000828254039250508190555060003373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef836040518082815260200191505060405180910390a33373ffffffffffffffffffffffffffffffffffffffff166108fc8383019081150290604051600060405180830381858888f193505050501515611c1c57600080fd5b50505600a165627a7a72305820afd0500fddd54817810e66d9573ce3f295349d53a037f89ebbd866d858cfdcfd0029";
        String complexInput = "6060604052341561000f57600080fd5b61028c8061001e6000396000f300606060405260043610610041576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680630dbe671f14610046575b600080fd5b341561005157600080fd5b61005961012e565b60405180806020018581526020018060200184151515158152602001838103835287818151815260200191508051906020019080838360005b838110156100ad578082015181840152602081019050610092565b50505050905090810190601f1680156100da5780820380516001836020036101000a031916815260200191505b50838103825285818151815260200191508051906020019060200280838360005b838110156101165780820151818401526020810190506100fb565b50505050905001965050505050505060405180910390f35b610136610238565b600061014061024c565b600061014a61024c565b60036040518059106101595750595b90808252806020026020018201604052509050600181600081518110151561017d57fe5b9060200190602002018181525050600281600181518110151561019c57fe5b906020019060200201818152505060038160028151811015156101bb57fe5b90602001906020020181815250507ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff88160016040805190810160405280600481526020017f6369616f0000000000000000000000000000000000000000000000000000000081525092919082925094509450945094505090919293565b602060405190810160405280600081525090565b6020604051908101604052806000815250905600a165627a7a72305820bd1c17fe12a8032c4939b4a4740a57e2abd571d80c527d8af16ed33a98af3b230029";

        Contract contract = new Contract("OttOtt", complexInput);

        try {
            System.out.println("Solidity version: " + contract.getExactSolidityVersion());
        } catch (SolidityVersionUnknownException e) {
            e.printStackTrace();
        }

        System.out.println("Data pre cfg: \"" + contract.getRuntime().getRemainingData() + '"');

        long pre = System.currentTimeMillis();
        CFGPrinter.show(contract.getRuntimeCfg());
        long post = System.currentTimeMillis();
        System.out.println("ELAPSED TIME: " + (post-pre));

        System.out.println("Data post cfg: \"" + contract.getRuntime().getRemainingData() + '"');

        Abi contractAbi = AbiExtractor.getInstance().extractAbi(contract);
        System.out.println(contractAbi.toJson());
    }

}
