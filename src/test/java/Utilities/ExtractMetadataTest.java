package Utilities;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static Utilities.ExtractMetadata.extractTitle;
import static org.junit.jupiter.api.Assertions.*;

class ExtractMetadataTest {
    Model m = LoadModel.initAndLoadModelFromResource("static/czso-unemployment-rate-ontology.rdf", Lang.RDFXML);

    @Test
    void bla(){
        System.out.println(extractTitle(m));
    }
    ExtractMetadataTest() throws IOException {
    }
}