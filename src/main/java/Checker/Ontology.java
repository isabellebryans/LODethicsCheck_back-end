package Checker;

import Utilities.ExtractMetadata;
import Utilities.RunEthicalChecks;
import com.google.gson.Gson;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import com.google.gson.*;

public class Ontology {
    Model ontModel;
    Foops foopsResult=null;
    String uri;
    // Metadata
    String description=null;
    String title=null;
    int metaDataScore=0;

    // Check1 = check for the presence of vulnerable groups/people
    String[] Check1=null;
    // Check2 = check for the distinction of traits that could be discriminated against
    String[] Check2=null;
    // Check3 = check for sensitive topics
    String[] Check3=null;

    public Ontology(Model m, String uri){
        this.ontModel = m;
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

    public void runTests(){
        // Test description and title first
        Check1 = RunEthicalChecks.runCheck1(this);
        Check2 = RunEthicalChecks.runCheck2(this);
        Check3 = RunEthicalChecks.runCheck3(this);
        System.out.println("Check 1 contains: "+ Arrays.toString(Check1));
        System.out.println("Check 2 contains: "+ Arrays.toString(Check2));
        System.out.println("Check 3 contains: "+ Arrays.toString(Check3));
    }
    public void level3_testProperties(Set<Property> properties){
        Set<String> stringSet1 = new HashSet<>(Arrays.asList(Check1));
        Set<String> stringSet2 = new HashSet<>(Arrays.asList(Check2));
        Set<String> stringSet3 = new HashSet<>(Arrays.asList(Check3));

        stringSet1.addAll(Arrays.asList(RunEthicalChecks.level3_check1(ontModel, properties)));
        stringSet2.addAll(Arrays.asList(RunEthicalChecks.level3_check2(ontModel, properties)));
        stringSet3.addAll(Arrays.asList(RunEthicalChecks.level3_check3(ontModel, properties)));

        Check1 = stringSet1.toArray(new String[0]);
        Check2 = stringSet2.toArray(new String[0]);
        Check3 = stringSet3.toArray(new String[0]);

        System.out.println("Ran level 3 property tests on dataset: ");
        System.out.println("Check 1 contains: "+ Arrays.toString(Check1));
        System.out.println("Check 2 contains: "+ Arrays.toString(Check2));
        System.out.println("Check 3 contains: "+ Arrays.toString(Check3));
    }
    public void level4_testObjects(){
        Set<String> stringSet1 = new HashSet<>(Arrays.asList(Check1));
        Set<String> stringSet3 = new HashSet<>(Arrays.asList(Check3));

        stringSet1.addAll(Arrays.asList(RunEthicalChecks.level4_check1(ontModel)));
        stringSet3.addAll(Arrays.asList(RunEthicalChecks.level4_check3(ontModel)));

        Check1 = stringSet1.toArray(new String[0]);
        Check3 = stringSet3.toArray(new String[0]);

        System.out.println("Ran level 4 object tests on dataset: ");
        System.out.println("Check 1 contains: "+ Arrays.toString(Check1));
        System.out.println("Check 2 contains: "+ Arrays.toString(Check2));
        System.out.println("Check 3 contains: "+ Arrays.toString(Check3));
    }


    public JsonObject get_JSON() {
        JsonObject json = new JsonObject();
        // Directly add properties to the JsonObject
        json.addProperty("ontology_uri", this.uri);
        json.addProperty("ontology_title", this.title != null ? this.title : "none");
        json.addProperty("ontology_description", this.description != null ? this.description : "none");
        // Add foops_score as a double
        if (this.foopsResult != null) {
            json.addProperty("foops_score", this.foopsResult.getOverall_score());
        } else {
            // Assuming you still want to indicate 'none' or similar if there's no score.
            // If not having a score should simply omit the property, you can adjust this logic.
            json.addProperty("foops_score", "none");
        }
        // Assuming Check1, Check2, Check3 are arrays or collections of strings
        // Convert them directly to JsonArrays using Gson's toJsonTree method
        // This assumes Check1, Check2, Check3, etc. are String[] or similar;
        // if they're not, you'll need to adjust this accordingly.
        json.add("ontology_checks1", this.getCheck1() != null ? new Gson().toJsonTree(this.getCheck1()) : new JsonArray());
        json.add("ontology_checks2", this.getCheck2() != null ? new Gson().toJsonTree(this.getCheck2()) : new JsonArray());
        json.add("ontology_checks3", this.getCheck3() != null ? new Gson().toJsonTree(this.getCheck3()) : new JsonArray());


        // Serialize the JsonObject to a JSON string
        return json;
    }

    private void printMetadata(){
        System.out.println("Title: "+title);
        System.out.println("Description: "+description);
    }

    private void setMetaData(){
        title = ExtractMetadata.extractTitle(ontModel);
        description = ExtractMetadata.extractDescription(ontModel);
    }
    public String getDescription(){
        return description;
    }

    public String getTitle() {
        return title;
    }

    public Model getOntModel() {
        return ontModel;
    }
    public String getUri(){
        return uri;
    }

    public String[] getCheck1() {
        return Check1;
    }
    public String[] getCheck2() {
        return Check2;
    }
    public String[] getCheck3() {
        return Check3;
    }
}
