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

        String testContract = "608060405234801561001057600080fd5b506102f3806100206000396000f3fe6080604052600436106100295760003560e01c80631998aeef1461004c5780633ccfd60b14610056575b6000600154111561004157600060018190555061004a565b61004961006d565b5b005b610054610076565b005b34801561006257600080fd5b5061006b6101fc565b005b60018081905550565b60015434116100ed576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040180806020018281038252601f8152602001807f426964206d75737420626520686967686572207468616e2063757272656e740081525060200191505060405180910390fd5b600073ffffffffffffffffffffffffffffffffffffffff166000809054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16146101b357600154600260008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020600082825401925050819055505b336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555034600181905550565b6000600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020019081526020016000205490506000600260003373ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001908152602001600020819055503373ffffffffffffffffffffffffffffffffffffffff166108fc829081150290604051600060405180830381858888f19350505050505056fea265627a7a723158209084032fff0aede27808eea335f926c301c6c38545cc9e75174bf4e7152d738264736f6c63430005110032";

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
