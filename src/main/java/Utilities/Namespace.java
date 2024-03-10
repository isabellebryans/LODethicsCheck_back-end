package Utilities;

import Checker.Ontology;

public class Namespace {
    String ns;
    private Ontology ontology= null;
    boolean downloadable=false;
    public Namespace(String uri){
        this.ns = uri;
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

    public Ontology getOntology() {
        return ontology;
    }

    public void setDownloadable(boolean downloadable) {
        this.downloadable = downloadable;
    }
}
