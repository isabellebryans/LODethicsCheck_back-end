package Utils;

import Entities.Test;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

// Method to parse JSON tests from Resources and Create an array of Tests
public class parseJSONtests {
    public static Test[] parseJSONtestsFromResources (){
        String fileListPath = "static/ethical_tests/testList.txt";
        ArrayList<Test> tests = new ArrayList<>();
        try (InputStream is = parseJSONtests.class.getClassLoader().getResourceAsStream(fileListPath)) {
            if (is == null) {
                throw new NullPointerException("Cannot find resource file list at " + fileListPath);
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            reader.lines().forEach(fileName -> {
                // Process each file listed and capture the test name
                Test test = parseJSONtests.parseJSONtest("static/ethical_tests/" + fileName);
                tests.add(test);
                // Use the test name as needed
                System.out.println("Processed file: " + fileName);
            });
            return tests.toArray(new Test[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    private static Test parseJSONtest (String filePath) {
        try (InputStream is = parseJSONtests.class.getClassLoader().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new NullPointerException("Cannot find resource file: "+filePath);
            }
            // Read content from InputStream
            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            // Parse the JSON data using Gson
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(content, JsonObject.class);

            // Extract fields
            String name = jsonObject.get("name").getAsString();
            String target = jsonObject.get("target").getAsString();
            JsonArray termsArray = jsonObject.getAsJsonArray("terms");

            // Convert JsonArray to an array of strings
            String[] terms = new String[termsArray.size()];
            for (int i = 0; i < termsArray.size(); i++) {
                terms[i] = termsArray.get(i).getAsString();
            }
            Test test = new Test(name, target, terms);

            // Example usage
            System.out.println("Name: " + name);
            System.out.println("Target: " + target);
            for (String term : terms) {
                System.out.println("Term: " + term);
            }
            return test;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
