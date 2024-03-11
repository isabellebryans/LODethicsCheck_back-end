package Checker;

import Utilities.LoadModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class DatasetTest {
    Model m = LoadModel.initAndLoadModelFromResource("static/streetCrimeCamden.rdf", Lang.RDFXML);

    @Test
    void Dataset_test() throws IOException {
        Dataset ds = new Dataset(m);
        System.out.println(ds.export_JSON());
    }
    DatasetTest() throws IOException {
    }
}