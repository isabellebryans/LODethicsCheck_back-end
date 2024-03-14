package Utilities;

import Checker.Foops;
import Checker.Ontology;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import netscape.javascript.JSObject;
import org.json.JSONObject;

import java.io.IOException;

public class Namespace {
    String ns;
    private Ontology ontology= null;
    boolean downloadable=false;
    private Foops foops;


    public Namespace(String uri)  {
        this.ns = uri;
        try{
            this.foops = new Foops(uri);
        } catch (IOException e){
            System.out.println("Foops error for Namespace: "+this.ns);
            this.foops = null;
        }
    }

    public String getNs() {
        return ns;
    }

    public boolean isDownloadable() {
        return downloadable;
    }

    public void setOntology(Ontology ontology) {
        this.ontology = ontology;
    }

    public Foops getFoops() {
        return foops;
    }

    public JsonObject getJSON(){
        JsonObject json = new JsonObject();
        json.addProperty("ns_uri", this.ns);
        json.addProperty("ns_downloadable", this.downloadable);
        // Convert the JSONObject to a String
        if (this.foops != null) {
            json.addProperty("ns_foops_overall_score", this.foops.getOverall_score());
            String resultsString = this.foops.getResults().toString();
            JsonObject convertedJsonObject = JsonParser.parseString(resultsString).getAsJsonObject();
            json.add("ns_foops_results", convertedJsonObject);
        } else {
            // Assuming you still want to indicate 'none' or similar if there's no score.
            // If not having a score should simply omit the property, you can adjust this logic.
            json.addProperty("ns_foops_results", "none");
        }
        if (this.ontology != null) {
            json.add("ns_ontology", this.ontology.get_JSON());
        }
        return json;
    }

    public Ontology getOntology() {
        return ontology;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }
}
