package processing;

import etherscan.EtherScanDownloader;
import json_utils.Response;

import java.io.IOException;

public class AddressProcessor {
    private final static Response CONTRACT_DOWNLOAD_ERROR = new Response("0",
            "A critical error occurred during contract download",
            null);
    private final static Response CONTRACT_ADDRESS_FORMAT_ERROR = new Response("0",
            "Address is malformed",
            null);

    public static Response process(String name, String address){
        try {
            if (! address.startsWith("0x"))
                address = "0x" + address;
            if (! address.matches("0x([0-9a-fA-F]{40})"))
                return CONTRACT_ADDRESS_FORMAT_ERROR;
            String bytecode = EtherScanDownloader.getContractBytecode(address);
            return BytecodeProcessor.process(name, bytecode, true, address);
        } catch (IOException e) {
            return CONTRACT_DOWNLOAD_ERROR;
        }
    }
}
