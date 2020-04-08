package analyzer.ABI;

import com.google.gson.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class HashDB {
    private static HashDB ilSoloEUnico = new HashDB();

    private HashDB() {
    }

    public static HashDB getInstance() {
        return ilSoloEUnico;
    }

    private static class FourBytesResult {
        public String id;
        public String created_at;
        public String text_signature;
        public String hex_signature;
        public String bytes_signature;
    }

    private static class FourBytesResponse {
        public int count;
        public String next;
        public String previous;
        public List<FourBytesResult> results = new ArrayList<>();
    }

    private String get4ByteJsonFromHash(String hash) throws IOException {
        String stringUrl = "https://www.4byte.directory/api/v1/signatures/?format=json&hex_signature=";
        stringUrl += URLEncoder.encode(hash, "UTF-8");

        URL urlForGetRequest = new URL(stringUrl);
        HttpURLConnection conn = (HttpURLConnection) urlForGetRequest.openConnection();
        conn.setRequestMethod("GET");
        String USER_AGENT = "Mozilla/5.0";
        conn.setRequestProperty("User-Agent", USER_AGENT);
        int responseCode = conn.getResponseCode();

        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();

            String readLine = null;
            while ((readLine = in.readLine()) != null) {
                response.append(readLine);
            }
            in.close();
            return response.toString();
        }
        return null;
    }

    public String getSignatureFromHash(String hash) throws IOException{
        Gson gson = new Gson();
        FourBytesResponse fbr = gson.fromJson(get4ByteJsonFromHash(hash), FourBytesResponse.class);
        return fbr.results.get(0).text_signature;
    }

    public AbiFunction getAbiFromHash(String hash) throws Exception{
        return null;
    }

    public static void main(String[] args) throws Exception{
        System.out.println(HashDB.getInstance().getSignatureFromHash("0xbeca2e86"));
    }
}
