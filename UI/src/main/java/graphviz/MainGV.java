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
        String testContract = "6060604052341561000f57600080fd5b5b61029e8061001f6000396000f3006060604052361561004a576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680631998aeef1461007a5780633ccfd60b14610084575b341561005557600080fd5b5b6000600154111561006e576000600181905550610077565b610076610099565b5b5b005b6100826100a3565b005b341561008f57600080fd5b6100976101af565b005b600180819055505b565b600154341115156100b357600080fd5b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1614151561016557600154600260008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055505b336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550346001819055505b565b6000600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205490506000600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055503373ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050505b505600a165627a7a72305820ecd34789f632a06241a849b80fb1b10c4833ca9713ce9b9976e5e5edef339a3e0029";

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
