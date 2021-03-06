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
        
        try {
            Contract contract = new Contract("OttOtt", bytecode, false);
            SecurityAnalysisReport report = analyse(contract);
            for (SecurityDetection d : report)
                if (d.getVulnerability() == SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL)
                System.out.println(d);
            System.out.println("Total store write after unsafe call: " + report.countDetections(SecurityVulnerability.STORE_WRITE_AFTER_UNSAFE_CALL));
        } catch (NotSolidityContractException e) {
            e.printStackTrace();
        }
    }

}
