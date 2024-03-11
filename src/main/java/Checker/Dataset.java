package Checker;

import Utilities.*;
import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class Dataset {
    private Model model;
    private String title;
    private String description;
    private Set<Property> properties;
    private List<Namespace> namespaces;
    private Path ontologiesFolder;
    private Ontology[] ontologies;

    public Dataset(Model model1) throws IOException {
        this.model = model1;
        setMetaData();
        this.properties = ExtractionMethods.extractProperties(model1);
        this.namespaces = ExtractionMethods.extractNamespaces(properties);
        this.ontologiesFolder = downloadOntologies();
        this.ontologies = LoadModel.loadOntologiesFromFolder(this.ontologiesFolder);
        DownloadFile.removeTemporaryFolders(ontologiesFolder);
        printNamespaces();
        printMetadata();
    }
    private void printNamespaces(){
        System.out.println("Namespaces: ");
        for(Namespace ns : namespaces){
            System.out.println(ns.getNs()+" "+ns.isDownloadable());
            }
    }

    private void printMetadata(){
        System.out.println("Title: "+title);
        System.out.println("Description: "+description);
    }
    private void setMetaData(){
        title = ExtractMetadata.extractTitle(model);
        description = ExtractMetadata.extractDescription(model);
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
}
