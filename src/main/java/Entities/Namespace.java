package Entities;

import OntologyService.Foops;
import com.google.gson.JsonObject;

import java.io.IOException;


public class Namespace {
    String ns;
    private RDFmodel RDFmodel = null;
    boolean downloadable=false;
    private String model_loaded = "false";
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

    public void setOntology(RDFmodel RDFmodel) {
        this.RDFmodel = RDFmodel;
    }

    public Foops getFoops() {
        return foops;
    }

    public JsonObject getJSON(){
        JsonObject json = new JsonObject();
        json.addProperty("ns_uri", this.ns);
        json.addProperty("ns_downloadable", this.downloadable);
        json.addProperty("ns_model_loaded", this.model_loaded);
        // Convert the JSONObject to a String
        if (this.foops != null) {
            json.addProperty("ns_foops_overall_score", this.foops.getOverall_score());
        } else {
            json.addProperty("ns_foops_results", "none");
        }
        if (this.RDFmodel != null) {
            json.add("ns_ontology", this.RDFmodel.get_ont_JSON());
        }
        return json;
    }

    public RDFmodel getOntology() {
        return RDFmodel;
    }

    public void setModel_loaded(String model_loaded) {
        this.model_loaded = model_loaded;
    }

    public String getModel_loaded() {
        return model_loaded;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }
}
