package Checker;

import org.json.JSONArray;
import org.json.JSONObject;
import Utilities.FoopsCheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Foops {
    final String URI;
    private String ontology_title;
    private double overall_score;
    private List<FoopsCheck> checks;

    public Foops(String uri) throws IOException {
        this.URI = uri;
        send_http_request();
    }

    private void send_http_request() throws IOException {
        // URL and JSON payload
        String url = "https://foops.linkeddata.es/assessOntology";
        String jsonPayload = "{\"ontologyUri\":\"" + URI + "\"}";

        // Create connection
        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json;charset=UTF-8");
        conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        conn.setDoOutput(true);

        // Write JSON data to output stream
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        // Read response
        try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            System.out.println("Response: ");
            System.out.println(response);
            set_results(response.toString());
        }

        // Close connection
        conn.disconnect();
    }

    private void set_results(String json){
        JSONObject jsonObject = new JSONObject(json);
        this.ontology_title = jsonObject.getString("ontology_title");
        this.overall_score = jsonObject.getDouble("overall_score");
        // Parsing checks array
        JSONArray checksArray = jsonObject.getJSONArray("checks");
        List<FoopsCheck> checks1 = new ArrayList<FoopsCheck>();
        for (int i = 0; i < checksArray.length(); i++) {
            JSONObject checkObject = checksArray.getJSONObject(i);
            FoopsCheck check = new FoopsCheck();
            check.setId(checkObject.getString("id"));
            check.setPrincipleId(checkObject.getString("principle_id"));
            check.setCategoryId(checkObject.getString("category_id"));
            check.setStatus(checkObject.getString("status"));
            check.setTitle(checkObject.getString("title"));
            check.setExplanation(checkObject.optString("explanation"));
            check.setDescription(checkObject.getString("description"));
            check.setTotalPassedTests(checkObject.getInt("total_passed_tests"));
            check.setTotalTestsRun(checkObject.getInt("total_tests_run"));

            checks1.add(check);
        }
        this.checks = checks1;
    }
    // Getters
    public String getOntology_title() {
        return ontology_title;
    }

    public double getOverall_score() {
        return overall_score;
    }

    public List<FoopsCheck> getChecks() {
        return checks;
    }

}
