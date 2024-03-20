package Checker;

import Utilities.*;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Dataset {
    private List<Namespace> ontsUnavailable= new ArrayList<>();
    private final Ontology ont;
    private Set<Property> properties;
    private final List<Namespace> namespaces;
    private final Ontology[] ontologies;
    private final String fileName;


    public Dataset(Model model1, String fileName) throws IOException {
        this.fileName = fileName;
        this.ont = new Ontology(model1, "");
        this.ont.runTests();
        this.properties = ExtractionMethods.extractProperties(model1);
        this.namespaces = ExtractionMethods.extractNamespaces(properties, model1);
        Path ontologiesFolder = downloadOntologies();
        this.ontologies = LoadModel.loadOntologiesFromFolder(ontologiesFolder);
        DownloadFile.removeTemporaryFolders(ontologiesFolder);
        printNamespaces();
        level3_testDataset();
        testOntologies();
        link_Namespace_Ontology();
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
        boolean downloadable=true;
        if (ont.getTitle() == null && ont.getDescription() == null){
            // Go to test 3
            ont.level3_testProperties(this.properties);
            ont.level4_testObjects();
        }
//        if (!downloadable || this.ontologies.length==0 || (ont.getTitle() == null && ont.getDescription() == null)){
//
//        }
    }

    private void printNamespaces(){
        System.out.println("Namespaces: ");
        for(Namespace ns : namespaces){
            System.out.println(ns.getNs()+" "+ns.isDownloadable());
            }
    }

    public String export_JSON(){
        JsonObject json = new JsonObject();
        json.addProperty("file_name", this.fileName);
        json.addProperty("dataset_title", this.ont.getTitle() != null ? this.ont.getTitle() : "none");
        json.addProperty("dataset_description", this.ont.getDescription() != null ? this.ont.getDescription() : "none");

        // Convert arrays directly to JsonArray, assuming getCheck1(), etc., return String[] or Collection<String>
        json.add("dataset_checks1", this.ont.getCheck1() != null ? new Gson().toJsonTree(this.ont.getCheck1()) : new JsonArray());
        json.add("dataset_checks2", this.ont.getCheck2() != null ? new Gson().toJsonTree(this.ont.getCheck2()) : new JsonArray());
        json.add("dataset_checks3", this.ont.getCheck3() != null ? new Gson().toJsonTree(this.ont.getCheck3()) : new JsonArray());
        json.add("dataset_namespaces", this.getNamespaceStrings() != null ? new Gson().toJsonTree(this.getNamespaceStrings()) : new JsonArray());
        json.add("dataset_unavailable_namespaces", this.undownloadableNamespaces() != null ? new Gson().toJsonTree(this.undownloadableNamespaces()) : new JsonArray());

        // For ontologies_tested, assuming each Ontology object has a method to convert itself to a JsonObject
        JsonArray namespacesTested = new JsonArray();
        for (Namespace ns : this.namespaces) {
            // Here, it's assumed Ontology has a method to return its representation as a JsonObject.
            // If not, you would construct this JsonObject similarly to above.
            namespacesTested.add(ns.getJSON());
        }
        json.add("namespaces_tested", namespacesTested);
        Gson gson = new Gson();
        String jsonString = gson.toJson(json);
        System.out.println(jsonString);
        return jsonString;
    }


    private Path downloadOntologies() {
        Path folder = DownloadFile.createTempFolder();
        System.out.println("Temp folder created is "+folder.toString());
        for (Namespace namespace : namespaces){
            if (Utils.ArrayContains(DownloadFile.common_vocabs, namespace.getNs())){
                namespace.setDownloadable(true);
                namespace.setModel_loaded("standard");
                continue;
            }
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
    private void link_Namespace_Ontology(){
        for (Ontology ontology : ontologies){
            String ont_uri = ontology.getUri();
            String ont_uri_sub = ont_uri.substring(5);
           for(Namespace ns :namespaces){
               String ns_string = ns.getNs();
                if(ns_string.contains(ont_uri_sub)){
                    // Link ontology to namespace
                    ns.setOntology(ontology);
                    ns.setModel_loaded("true");
                    break;
                }
           }
        }
    }


    public Ontology[] getOntologies() {
        return ontologies;
    }

    public Ontology getOnt() {
        return ont;
    }
    public String getFileName(){ return fileName; }

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
        for (Namespace ns : this.namespaces){
            if (Objects.equals(ns.getModel_loaded(), "false")){
                namespaceStrings.add(ns.getNs());
            }
        }
        String[] new_array = namespaceStrings.toArray(new String[0]);
        if (new_array.length == 0){
            return null;
        }
        return new_array;
    }
}
