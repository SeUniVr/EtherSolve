package main;

import analysers.StoreAccessAfterUnsafeCall;
import analysers.TxOrigin;
import parseTree.Contract;
import parseTree.NotSolidityContractException;

public class SecurityAnalyser {

    public static SecurityAnalysisReport analyse(Contract contract){
        SecurityAnalysisReport report = new SecurityAnalysisReport(contract);
        StoreAccessAfterUnsafeCall.analyse(contract, report);
        TxOrigin.analyse(contract, report);
        report.stopTimer();
        return report;
    }

    public static void main(String[] args) {
        String reentrancy = "608060405234801561001057600080fd5b50610396806100206000396000f3fe608060405234801561001057600080fd5b506004361061004c5760003560e01c8063102ccd9214610051578063271625f71461007f578063cc06463214610089578063cd482e7714610093575b600080fd5b61007d6004803603602081101561006757600080fd5b810190808035906020019092919050505061009d565b005b61008761019d565b005b61009161023f565b005b61009b6102c7565b005b806000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410156100e857600080fd5b3373ffffffffffffffffffffffffffffffffffffffff168160405180600001905060006040518083038185875af1925050503d8060008114610146576040519150601f19603f3d011682016040523d82523d6000602084013e61014b565b606091505b505050806000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555050565b600073888888888888888888888888888888888888888890508073ffffffffffffffffffffffffffffffffffffffff166108fc61aaaa9081150290604051600060405180830381858888f193505050505061aaaa6000808373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555050565b3373ffffffffffffffffffffffffffffffffffffffff166108fc6188889081150290604051600060405180830381858888f19350505050506188886000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008282540392505081905550565b3373ffffffffffffffffffffffffffffffffffffffff166108fc6199999081150290604051600060405180830381858888f1935050505015801561030f573d6000803e3d6000fd5b506199996000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555056fea2646970667358221220df8e9a0b8321357ef3ec613ee7864e94116500d68b9bb1dba6f439f8c9dc76bc64736f6c634300060c0033";
        String txorigin = "60806040523480156200001157600080fd5b506b60ef6b1aba6f0723300000006000819055506040518060400160405280601081526020017f486f74446f6c6c61727320546f6b656e00000000000000000000000000000000815250600390805190602001906200007292919062000129565b506012600460006101000a81548160ff021916908360ff1602179055506040518060400160405280600381526020017f484453000000000000000000000000000000000000000000000000000000000081525060059080519060200190620000dc92919062000129565b50600054600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002081905550620001d8565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106200016c57805160ff19168380011785556200019d565b828001600101855582156200019d579182015b828111156200019c5782518255916020019190600101906200017f565b5b509050620001ac9190620001b0565b5090565b620001d591905b80821115620001d1576000816000905550600101620001b7565b5090565b90565b611b6b80620001e86000396000f3fe608060405234801561001057600080fd5b50600436106101cf5760003560e01c80635c65816511610104578063b8594fd3116100a2578063f6292d5911610071578063f6292d5914610b18578063f7ae6cde14610b86578063fb44fdd114610bf4578063fe3d3a9414610c62576101cf565b8063b8594fd3146109d4578063b9e3125814610a18578063cccf4b3114610a5c578063dd62ed3e14610aa0576101cf565b806370e309ef116100de57806370e309ef146108195780638376964d1461087d57806395d89b41146108eb578063a9059cbb1461096e576101cf565b80635c658165146106db57806368c6116f1461075357806370a08231146107c1576101cf565b806318160ddd1161017157806327e235e31161014b57806327e235e3146105b7578063313ce5671461060f5780633261a0e0146106335780633f1540b614610677576101cf565b806318160ddd146104af5780631e65db3c146104cd57806323b872dd14610531576101cf565b8063095ea7b3116101ad578063095ea7b3146103095780630a20e74e1461036f5780630ccb9a12146103dd578063174afdd41461044b576101cf565b80630405a8a7146101d457806306b3ad661461024257806306fdde0314610286575b600080fd5b610240600480360360608110156101ea57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610cc6565b005b6102846004803603602081101561025857600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610d03565b005b61028e610d3e565b6040518080602001828103825283818151815260200191508051906020019080838360005b838110156102ce5780820151818401526020810190506102b3565b50505050905090810190601f1680156102fb5780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6103556004803603604081101561031f57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050610ddc565b604051808215151515815260200191505060405180910390f35b6103db6004803603606081101561038557600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610ece565b005b610449600480360360608110156103f357600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610f0b565b005b6104ad6004803603604081101561046157600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610f48565b005b6104b7610fe2565b6040518082815260200191505060405180910390f35b61052f600480360360408110156104e357600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610fe8565b005b61059d6004803603606081101561054757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050611082565b604051808215151515815260200191505060405180910390f35b6105f9600480360360208110156105cd57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061131a565b6040518082815260200191505060405180910390f35b610617611332565b604051808260ff1660ff16815260200191505060405180910390f35b6106756004803603602081101561064957600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611345565b005b6106d96004803603604081101561068d57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611380565b005b61073d600480360360408110156106f157600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061141a565b6040518082815260200191505060405180910390f35b6107bf6004803603606081101561076957600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061143f565b005b610803600480360360208110156107d757600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506114c3565b6040518082815260200191505060405180910390f35b61087b6004803603604081101561082f57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061150c565b005b6108e96004803603606081101561089357600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506115a6565b005b6108f36115e3565b6040518080602001828103825283818151815260200191508051906020019080838360005b83811015610933578082015181840152602081019050610918565b50505050905090810190601f1680156109605780820380516001836020036101000a031916815260200191505b509250505060405180910390f35b6109ba6004803603604081101561098457600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190505050611681565b604051808215151515815260200191505060405180910390f35b610a16600480360360208110156109ea57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506117d8565b005b610a5a60048036036020811015610a2e57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611813565b005b610a9e60048036036020811015610a7257600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919050505061184e565b005b610b0260048036036040811015610ab657600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611889565b6040518082815260200191505060405180910390f35b610b8460048036036060811015610b2e57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611910565b005b610bf260048036036060811015610b9c57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611994565b005b610c6060048036036060811015610c0a57600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff16906020019092919080359060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611a18565b005b610cc460048036036040811015610c7857600080fd5b81019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050611a9c565b005b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff1614610cfe57600080fd5b505050565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff1614610d3b57600080fd5b50565b60038054600181600116156101000203166002900480601f016020809104026020016040519081016040528092919081815260200182805460018160011615610100020316600290048015610dd45780601f10610da957610100808354040283529160200191610dd4565b820191906000526020600020905b815481529060010190602001808311610db757829003601f168201915b505050505081565b600081600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167f8c5be1e5ebec7d5bd14f71427d1e84f3dd0314c0f7b2291e5b200ac8c7c3b925846040518082815260200191505060405180910390a36001905092915050565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff1614610f0657600080fd5b505050565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff1614610f4357600080fd5b505050565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff1614610f8057600080fd5b8173ffffffffffffffffffffffffffffffffffffffff166108fc3073ffffffffffffffffffffffffffffffffffffffff16319081150290604051600060405180830381858888f19350505050158015610fdd573d6000803e3d6000fd5b505050565b60005481565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff161461102057600080fd5b8173ffffffffffffffffffffffffffffffffffffffff166108fc3073ffffffffffffffffffffffffffffffffffffffff16319081150290604051600060405180830381858888f1935050505015801561107d573d6000803e3d6000fd5b505050565b600080600260008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905082600160008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054101580156111535750828110155b61115c57600080fd5b82600160008673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254019250508190555082600160008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825403925050819055507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8110156112a95782600260008773ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825403925050819055505b8373ffffffffffffffffffffffffffffffffffffffff168573ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef856040518082815260200191505060405180910390a360019150509392505050565b60016020528060005260406000206000915090505481565b600460009054906101000a900460ff1681565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff161461137d57600080fd5b50565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff16146113b857600080fd5b8173ffffffffffffffffffffffffffffffffffffffff166108fc3073ffffffffffffffffffffffffffffffffffffffff16319081150290604051600060405180830381858888f19350505050158015611415573d6000803e3d6000fd5b505050565b6002602052816000526040600020602052806000526040600020600091509150505481565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff161461147757600080fd5b8273ffffffffffffffffffffffffffffffffffffffff166108fc839081150290604051600060405180830381858888f193505050501580156114bd573d6000803e3d6000fd5b50505050565b6000600160008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020549050919050565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff161461154457600080fd5b8173ffffffffffffffffffffffffffffffffffffffff166108fc3073ffffffffffffffffffffffffffffffffffffffff16319081150290604051600060405180830381858888f193505050501580156115a1573d6000803e3d6000fd5b505050565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff16146115de57600080fd5b505050565b60058054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156116795780601f1061164e57610100808354040283529160200191611679565b820191906000526020600020905b81548152906001019060200180831161165c57829003601f168201915b505050505081565b600081600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410156116cf57600080fd5b81600160003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555081600160008573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055508273ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff167fddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef846040518082815260200191505060405180910390a36001905092915050565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff161461181057600080fd5b50565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff161461184b57600080fd5b50565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff161461188657600080fd5b50565b6000600260008473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002060008373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905092915050565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff161461194857600080fd5b8273ffffffffffffffffffffffffffffffffffffffff166108fc839081150290604051600060405180830381858888f1935050505015801561198e573d6000803e3d6000fd5b50505050565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff16146119cc57600080fd5b8273ffffffffffffffffffffffffffffffffffffffff166108fc839081150290604051600060405180830381858888f19350505050158015611a12573d6000803e3d6000fd5b50505050565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff1614611a5057600080fd5b8273ffffffffffffffffffffffffffffffffffffffff166108fc839081150290604051600060405180830381858888f19350505050158015611a96573d6000803e3d6000fd5b50505050565b8073ffffffffffffffffffffffffffffffffffffffff163273ffffffffffffffffffffffffffffffffffffffff1614611ad457600080fd5b8173ffffffffffffffffffffffffffffffffffffffff166108fc3073ffffffffffffffffffffffffffffffffffffffff16319081150290604051600060405180830381858888f19350505050158015611b31573d6000803e3d6000fd5b50505056fea265627a7a72315820a4c8e5c10d6b805129f834ba71f7d89afcf84e9555e8943d4a347b7bc22fd02f64736f6c634300050c0032";

        // Test for re-entrancy
        try {
            Contract contract = new Contract("OttOtt", reentrancy, false);
            SecurityAnalysisReport report = analyse(contract);
            for (SecurityDetection d : report)
                if (d.getVulnerability() == SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL)
                System.out.println(d);
            System.out.println("Total store write after unsafe call: " + report.countDetections(SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL));
        } catch (NotSolidityContractException e) {
            e.printStackTrace();
        }

        // Test for tx-origin
        try {
            Contract contract = new Contract("txorigin", txorigin, false);
            SecurityAnalysisReport report = analyse(contract);
            for (SecurityDetection d : report)
                if (d.getVulnerability() == SecurityVulnerability.TX_ORIGIN_AS_AUTHENTICATION)
                    System.out.println(d);
            System.out.println("Total tx.origin as authentication: " + report.countDetections(SecurityVulnerability.TX_ORIGIN_AS_AUTHENTICATION));
        } catch (NotSolidityContractException e) {
            e.printStackTrace();
        }
    }

}
