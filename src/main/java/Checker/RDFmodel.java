package Checker;

import Utilities.ExtractMetadata;
import Utilities.RunEthicalChecks;
import com.google.gson.Gson;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import java.io.IOException;
import java.util.*;

import com.google.gson.*;

public class RDFmodel {
    Model model;
    Foops foopsResult=null;
    String uri;
    // Metadata
    String description=null;
    String title=null;
    int metaDataScore=0;

    Test[] tests;
    Results[] results;
    // Check1 = check for the presence of vulnerable groups/people

    public RDFmodel(Model m, String uri){
        this.model = m;
        this.uri = uri;
        System.out.println("Ontology created from uri "+uri);
        setMetaData();
        printMetadata();
    }


    public void runFOOPS() {
        try{
            this.foopsResult = new Foops(this.uri);
            if(this.title==null && !Objects.equals(this.foopsResult.getOntology_title(), "Title unavailable")){
                this.title = this.foopsResult.getOntology_title();
            }
        }
        catch (IOException ignored){

        }
    }

    public void setTests(Test[] tests) {
        this.tests = tests;
    }

    public void runTestsDataset(Set<Property> properties){
        ArrayList<Results> results1 = new ArrayList<>();
        for (Test t : this.tests){
            Results results2 = new Results(t);
            String[] level1 = RunEthicalChecks.runLevel1Checks(this, t.getTest_terms());
            results2.setResults(Arrays.asList(level1));
            if(!t.getTest_places().equals("objects")){
                String[] level2 = RunEthicalChecks.test_properties(this.model, properties, t.getTest_terms());
                results2.setResults(Arrays.asList(level2));
            }
            if(!t.getTest_places().equals("properties")){
                String[] level3 = RunEthicalChecks.test_all_objects(this.model, t.getTest_terms());
                results2.setResults(Arrays.asList(level3));
            }
            results1.add(results2);
        }

        this.results = results1.toArray(new Results[0]);
    }

    public void runTestsOntology(){
        ArrayList<Results> results1 = new ArrayList<>();
        for (Test t : this.tests){
            String[] level1 = RunEthicalChecks.runLevel1Checks(this, t.getTest_terms());

            Results results2 = new Results(t);
            results2.setResults(Arrays.asList(level1));
            results1.add(results2);
        }
        this.results = results1.toArray(new Results[0]);
    }

    public JsonObject get_ont_JSON() {
        JsonObject json = new JsonObject();
        // Directly add properties to the JsonObject
        //json.addProperty("ontology_uri", this.uri);
        json.addProperty("ontology_title", this.title != null ? this.title : "none");
        json.addProperty("ontology_description", this.description != null ? this.description : "none");

        // Assuming Check1, Check2, Check3 are arrays or collections of strings
        // Convert them directly to JsonArrays using Gson's toJsonTree method
        // This assumes Check1, Check2, Check3, etc. are String[] or similar;
        // if they're not, you'll need to adjust this accordingly.
        JsonArray EthicalTests = new JsonArray();
        int i=0;
        for (Results r : results){
            i=i+1;
            System.out.println("Result "+i);
            EthicalTests.add(test_JSON(r));
        }
        json.add("ontology_ethics_tests", EthicalTests);
        // Serialize the JsonObject to a JSON string
        return json;
    }

    public JsonObject test_JSON(Results r){
        JsonObject json = new JsonObject();
        json.addProperty("test_name", r.getTest().getName());
        json.add("test_results", r.getResults() != null ? new Gson().toJsonTree(r.getResults()) : new JsonArray());
        return json;
    };

    private void printMetadata(){
        System.out.println("Title: "+title);
        System.out.println("Description: "+description);
    }

    private void setMetaData(){
        title = ExtractMetadata.extractTitle(model);
        description = ExtractMetadata.extractDescription(model);
    }
    public String getDescription(){
        return description;
    }

    public String getTitle() {
        return title;
    }

    public Model getModel() {
        return model;
    }
    public String getUri(){
        return uri;
    }

}
