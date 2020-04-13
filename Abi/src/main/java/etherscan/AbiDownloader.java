package etherscan;

import abi.Abi;
import abi.AbiExtractor;
import com.google.gson.Gson;
import etherscan.gson.EtherscanResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AbiDownloader {
    private static AbiDownloader ilSoloEUnico = new AbiDownloader();

    private AbiDownloader(){}

    public static AbiDownloader getInstance(){
        return ilSoloEUnico;
    }

    public Abi getContractAbi(String address) throws IOException {
        String abiString = requestAbi(address);
        Gson gson = new Gson();
        EtherscanResponse response = gson.fromJson(abiString, EtherscanResponse.class);
        return AbiExtractor.getInstance().extractAbi(response.getResult());
    }


    private String requestAbi(String address) throws IOException {
        String request = "http://api.etherscan.io/api?module=contract&action=getabi&address=" + address;
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
