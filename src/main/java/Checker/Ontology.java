package Checker;

import Utilities.ExtractMetadata;
import Utilities.RunEthicalChecks;
import org.apache.jena.rdf.model.Model;

import java.util.Arrays;

public class Ontology {
    Model ontModel;
    String uri;
    // Metadata
    String description=null;
    String title=null;

    int metaDataScore=0;

    // Check1 = check for the presence of vulnerable groups/people
    String[] Check1=null;
    // Check2 = check for the distinction of traits that could be discriminated against
    String[] Check2=null;
    // Check3 = check for sensitive topics
    String[] Check3=null;

    public Ontology(Model m, String uri){
        this.ontModel = m;
        this.uri = uri;
        System.out.println("Ontology created from uri "+uri);
        setMetaData();
        testOntology();
        System.out.println("Check 1 contains: "+ Arrays.toString(Check1));
        System.out.println("Check 2 contains: "+ Arrays.toString(Check2));
        System.out.println("Check 3 contains: "+ Arrays.toString(Check3));
    }

    public void testOntology(){
        // Test description and title first

        Check1 = RunEthicalChecks.runCheck1(this);
        Check2 = RunEthicalChecks.runCheck2(this);
        Check3 = RunEthicalChecks.runCheck3(this);
    }

    private void setMetaData(){
        title = ExtractMetadata.extractTitle(ontModel);
        description = ExtractMetadata.extractDescription(ontModel);
    }
    public String getDescription(){
        return description;
    }

    public String getTitle() {
        return title;
    }

    public Model getOntModel() {
        return ontModel;
    }
    public String getUri(){
        return uri;
    }
}
