package graphviz;

import SolidityInfo.SolidityVersionUnknownException;
import parseTree.Contract;
import parseTree.NotSolidityContractException;
import parseTree.cfg.BasicBlock;
import parseTree.cfg.Cfg;
import rebuiltabi.AbiExtractor;
import rebuiltabi.RebuiltAbi;

public class MainGV {

    public static void main(String[] args){
        String testContract = "608060405234801561001057600080fd5b506103e0806100206000396000f3fe6080604052600436106100345760003560e01c806312065fe0146100395780632e1a7d4d14610064578063d0e30db01461009f575b600080fd5b34801561004557600080fd5b5061004e6100a9565b6040518082815260200191505060405180910390f35b34801561007057600080fd5b5061009d6004803603602081101561008757600080fd5b81019080803590602001909291905050506100ef565b005b6100a761028d565b005b60008060003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16815260200190815260200160002054905090565b806000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205410156101a3576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260148152602001807f496e73756666696369656e742062616c616e636500000000000000000000000081525060200191505060405180910390fd5b806000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000206000828254039250508190555060003390508073ffffffffffffffffffffffffffffffffffffffff166108fc839081150290604051600060405180830381858888f1935050505015801561023a573d6000803e3d6000fd5b503373ffffffffffffffffffffffffffffffffffffffff167f7fcf532c15f0a6db0bd6d0e038bea71d30d808c7d98cb3bf7268a95bf5081b65836040518082815260200191505060405180910390a25050565b600034116102e6576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004018080602001828103825260288152602001806103836028913960400191505060405180910390fd5b346000803373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055503373ffffffffffffffffffffffffffffffffffffffff167fe1fffcc4923d04b559f4d29a8bfc6cda04eb5b0d3c460751c2402c5c5cc9109c346040518082815260200191505060405180910390a256fe4465706f73697420616d6f756e74206d7573742062652067726561746572207468616e207a65726fa26469706673582212206b69fe9604459d55d1ddfeef53dd9e6cc88cb8bad0c06d277ef8f0ca53f66a7a64736f6c63430006000033";

        System.out.println("START");
        long pre = System.currentTimeMillis();
        Contract contract = null;

        try {
            contract = new Contract("Test Contract", testContract, false, "0x0");
        } catch (NotSolidityContractException e) {
            e.printStackTrace();
            return;
        }

        RebuiltAbi abi = AbiExtractor.getAbiFromContract(contract);
        abi.setCfg(contract.getRuntimeCfg());

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
