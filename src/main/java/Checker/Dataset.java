package Checker;

import Utilities.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Dataset {
    private List<Namespace> ontsUnavailable= new ArrayList<>();
    private final Ontology ont;
    private Set<Property> properties;
    private final List<Namespace> namespaces;
    private final Ontology[] ontologies;


    public Dataset(Model model1) throws IOException {
        this.ont = new Ontology(model1, "");
        this.ont.runTests();
        this.properties = ExtractionMethods.extractProperties(model1);
        this.namespaces = ExtractionMethods.extractNamespaces(properties);
        Path ontologiesFolder = downloadOntologies();
        this.ontologies = LoadModel.loadOntologiesFromFolder(ontologiesFolder);
        DownloadFile.removeTemporaryFolders(ontologiesFolder);
        printNamespaces();
        level3_testDataset();
        testOntologies();
    }

    private void testOntologies(){
        for (Ontology o : this.ontologies){
        // runn foops
            o.runFOOPS();
            o.runTests();
        }
    }

    // See if all ontologies are downloadable.
    public void level3_testDataset(){
        for(Namespace ns : namespaces){
            System.out.println(ns.getNs()+" "+ns.isDownloadable());
            if (!ns.isDownloadable()){
                ontsUnavailable.add(ns);
            }
        }
        if (ont.getTitle() == null && ont.getDescription() == null){
            // Go to test 3
            ont.level3_testProperties(this.properties);
        }
    }

    private void printNamespaces(){
        System.out.println("Namespaces: ");
        for(Namespace ns : namespaces){
            System.out.println(ns.getNs()+" "+ns.isDownloadable());
            }
    }

    public String export_JSON(){
        JsonObject json = new JsonObject();

        json.addProperty("dataset_title", this.ont.getTitle() != null ? this.ont.getTitle() : "none");
        json.addProperty("dataset_description", this.ont.getDescription() != null ? this.ont.getDescription() : "none");

        // Convert arrays directly to JsonArray, assuming getCheck1(), etc., return String[] or Collection<String>
        json.add("dataset_checks1", this.ont.getCheck1() != null ? new Gson().toJsonTree(this.ont.getCheck1()) : new JsonArray());
        json.add("dataset_checks2", this.ont.getCheck2() != null ? new Gson().toJsonTree(this.ont.getCheck2()) : new JsonArray());
        json.add("dataset_checks3", this.ont.getCheck3() != null ? new Gson().toJsonTree(this.ont.getCheck3()) : new JsonArray());
        json.add("dataset_namespaces", this.getNamespaceStrings() != null ? new Gson().toJsonTree(this.getNamespaceStrings()) : new JsonArray());
        json.add("dataset_unavailable_namespaces", this.undownloadableNamespaces() != null ? new Gson().toJsonTree(this.undownloadableNamespaces()) : new JsonArray());

        // For ontologies_tested, assuming each Ontology object has a method to convert itself to a JsonObject
        JsonArray ontologiesTested = new JsonArray();
        for (Ontology o : this.ontologies) {
            // Here, it's assumed Ontology has a method to return its representation as a JsonObject.
            // If not, you would construct this JsonObject similarly to above.
            ontologiesTested.add(o.get_JSON());
        }
        json.add("ontologies_tested", ontologiesTested);
        Gson gson = new Gson();
        String jsonString = gson.toJson(json);
        System.out.println(jsonString);
        return jsonString;
    }


    private Path downloadOntologies() {
        Path folder = DownloadFile.createTempFolder();
        System.out.println("Temp folder created is "+folder.toString());
        for (Namespace namespace : namespaces){
            try {
                DownloadFile.downloadOntology(namespace.getNs(), folder);
                namespace.setDownloadable(true);
            } catch (Exception e){
                System.out.println("Couldn't download ontology "+namespace.getNs());
            }
        }
        return folder;
    }

    // Getters and setters



    public Ontology[] getOntologies() {
        return ontologies;
    }

    public Ontology getOnt() {
        return ont;
    }

    public List<Namespace> getNamespaces() {
        return namespaces;
    }
    public String[] getNamespaceStrings() {
        ArrayList<String> namespaceStrings = new ArrayList<>();
        for (Namespace ns :namespaces){
            namespaceStrings.add(ns.getNs());
        }
        return namespaceStrings.toArray(new String[0]);
    }

    public String[] undownloadableNamespaces(){
        ArrayList<String> namespaceStrings = new ArrayList<>();
        if (ontsUnavailable==null){
            return null;
        }
        for (Namespace ns : ontsUnavailable){
            namespaceStrings.add(ns.getNs());
        }
        return namespaceStrings.toArray(new String[0]);
    }
}
