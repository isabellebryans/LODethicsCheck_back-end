package Entities;

import ExtractionService.ExtractProperties;
import OntologyService.DownloadFile;
import OntologyService.LoadModel;
import Utils.*;
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
    private final RDFmodel rdFmodel;
    private Set<Property> properties;
    private final List<Namespace> namespaces;
    private final RDFmodel[] ontologies;
    private final String fileName;
    private final Test[] tests;


    public Dataset(Model model1, String fileName, Test[] tests) throws IOException {
        this.fileName = fileName;
        this.tests = tests;
        this.rdFmodel = new RDFmodel(model1, "");
        this.rdFmodel.setTests(tests);
        // Extract properties and namespaces
        this.properties = ExtractProperties.extractProperties(model1);
        this.namespaces = ExtractProperties.extractNamespaces(properties, model1);
        // Run ethical tests at dataset level
        this.rdFmodel.runTestsDataset(this.properties);
        // Download Ontologies
        Path ontologiesFolder = downloadOntologies();
        this.ontologies = LoadModel.loadOntologiesFromFolder(ontologiesFolder);
        DownloadFile.removeTemporaryFolders(ontologiesFolder);
        // Run ethical tests on ontologies
        testOntologies();
        link_Namespace_Ontology();
    }

    // Run ethical tests on the ontologies
    // Test ontologies with foops
    private void testOntologies(){
        for (RDFmodel o : this.ontologies){
            o.setTests(this.tests);
            o.runFOOPS();
            o.runTestsOntology();
        }
    }

    // Create JSON object for response
    public String export_JSON(){
        JsonObject json = new JsonObject();
        json.addProperty("file_name", this.fileName);
        json.addProperty("dataset_title", this.rdFmodel.getTitle() != null ? this.rdFmodel.getTitle() : "none");
        json.addProperty("dataset_description", this.rdFmodel.getDescription() != null ? this.rdFmodel.getDescription() : "none");

        json.add("dataset_namespaces", this.getNamespaceStrings() != null ? new Gson().toJsonTree(this.getNamespaceStrings()) : new JsonArray());
        json.add("dataset_unavailable_namespaces", this.undownloadableNamespaces() != null ? new Gson().toJsonTree(this.undownloadableNamespaces()) : new JsonArray());
        JsonArray ethicsTests = new JsonArray();
        for (Results r : this.rdFmodel.results){
            ethicsTests.add(this.rdFmodel.test_JSON(r));
        }
        json.add("dataset_ethics_tests", ethicsTests);
        JsonArray namespacesTested = new JsonArray();
        for (Namespace ns : this.namespaces) {
            namespacesTested.add(ns.getJSON());
        }
        json.add("namespaces_tested", namespacesTested);
        Gson gson = new Gson();
        String jsonString = gson.toJson(json);
        System.out.println(jsonString);
        return jsonString;
    }


    // Attempt to download each ontology
    private Path downloadOntologies() {
        Path folder = DownloadFile.createTempFolder();
        System.out.println("Temp folder created is "+folder.toString());
        for (Namespace namespace : namespaces){
            if (other.ArrayContains(DownloadFile.common_vocabs, namespace.getNs())){
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

    // Link ontology models to the namespace
    private void link_Namespace_Ontology(){
        for (RDFmodel RDFmodel : ontologies){
            String ont_uri = RDFmodel.getUri();
            String ont_uri_sub = ont_uri.substring(5);
           for(Namespace ns :namespaces){
               String ns_string = ns.getNs();
                if(ns_string.contains(ont_uri_sub)){
                    // Link ontology to namespace
                    ns.setOntology(RDFmodel);
                    ns.setModel_loaded("true");
                    break;
                }
           }
        }
    }


    public RDFmodel[] getOntologies() {
        return ontologies;
    }

    public RDFmodel getRdFmodel() {
        return rdFmodel;
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
