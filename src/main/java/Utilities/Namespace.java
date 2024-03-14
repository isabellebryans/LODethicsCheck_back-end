package Utilities;

import Checker.Foops;
import Checker.Ontology;

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

    public Ontology getOntology() {
        return ontology;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }
}
