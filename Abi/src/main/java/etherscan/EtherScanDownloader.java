package etherscan;

import abi.Abi;
import com.google.gson.Gson;
import etherscan.gson.EtherscanResponse;
import rebuiltabi.AbiExtractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EtherScanDownloader {

    private final static String API_KEY = "YA5HAVF4DMYX6X3VM3NS7JKHCKK7FAXK68";

    public static Abi getContractAbi(String address) throws IOException {
        String abiString = request("contract", "getabi", address);
        Gson gson = new Gson();
        EtherscanResponse response = gson.fromJson(abiString, EtherscanResponse.class);
        return Abi.fromJson(response.getResult());
    }

    public static String getContractBytecode(String address) throws IOException {
        String bytecodeRequest = request("proxy", "eth_getCode", address);
        Gson gson = new Gson();
        EtherscanResponse response = gson.fromJson(bytecodeRequest, EtherscanResponse.class);
        return response.getResult().substring(2);
    }


    private static String request(String module, String action, String address) throws IOException {
        String request = String.format("https://api.etherscan.io/api?module=%s&action=%s&address=%s&apikey=%s", module, action, address, API_KEY);
        URL requestUrl = new URL(request);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK){
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String readLine = null;
            while ((readLine = in.readLine()) != null) {
                sb.append(readLine);
            }
            in.close();
            return sb.toString();
        } else {
            return null;
        }
    }
}
