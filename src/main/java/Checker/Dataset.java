package Checker;

import Utilities.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
        String title, description, ds_checks1, ds_checks2, ds_checks3, ds_namespaces, ds_unav_ns;
        if (this.ont.getTitle()!=null){
            title = "\"dataset_title\": \""+this.ont.getTitle()+"\",\n";
        }else{
            title = "\"dataset_title\": \"none\",\n";
        }
        if (this.ont.getDescription()!=null){
            description = "\"dataset_description\": \""+this.ont.getDescription()+"\",\n";
        }else{
            description = "\"dataset_description\": \"none\",\n";
        }
        if (this.ont.getCheck1()!=null){
            ds_checks1= "\"dataset_checks1\": \""+ Arrays.toString(this.ont.getCheck1()) +"\",\n";
        }else{
            ds_checks1="\"dataset_checks1\": \"none\",\n";
        }
        if (this.ont.getCheck2()!=null){
            ds_checks2= "\"dataset_checks2\": \""+ Arrays.toString(this.ont.getCheck2()) +"\",\n";
        }else{
            ds_checks2="\"dataset_checks2\": \"none\",\n";
        }
        if (this.ont.getCheck3()!=null){
            ds_checks3= "\"dataset_checks3\": \""+ Arrays.toString(this.ont.getCheck3()) +"\",\n";
        }else{
            ds_checks3="\"dataset_checks3\": \"none\",\n";
        }
        if (this.getNamespaceStrings()!=null){
            ds_namespaces= "\"dataset_namespaces\": \""+ Arrays.toString(this.getNamespaceStrings()) +"\",\n";
        }else{
            ds_namespaces="\"dataset_namespaces\": \"none\",\n";
        }
        if (this.undownloadableNamespaces()!=null){
            ds_unav_ns= "\"dataset_unavailable_namespaces\": \""+ Arrays.toString(this.undownloadableNamespaces()) +"\",\n";
        }else{
            ds_unav_ns="\"dataset_unavailable_namespaces\": \"none\",\n";
        }
        String out = "{\n " +
                title +
                description +

                ds_checks1 +
                ds_checks2 +
                ds_checks3 +
                ds_namespaces+
                ds_unav_ns+
                "\"ontologies_tested\":[";
        int i=0;
        for (Ontology o : this.ontologies){
            if (i>0){
                out+= ",";
            }
            out+= o.get_JSON();
            i+=1;
        }
        out += "\n]}";
        return out;
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
