package Checker;

import Utilities.ExtractMetadata;
import Utilities.RunEthicalChecks;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Ontology {
    Model ontModel;
    Foops foopsResult=null;
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
        printMetadata();
    }


    public void runFOOPS() {
        try{
            this.foopsResult = new Foops(this.uri);
            if(this.title==null && !Objects.equals(this.foopsResult.getOntology_title(), "Title unavailable")){
                this.title = this.foopsResult.getOntology_title();
            }
        }
        catch (IOException ignored){

        }
    }

    public void runTests(){
        // Test description and title first
        Check1 = RunEthicalChecks.runCheck1(this);
        Check2 = RunEthicalChecks.runCheck2(this);
        Check3 = RunEthicalChecks.runCheck3(this);
        System.out.println("Check 1 contains: "+ Arrays.toString(Check1));
        System.out.println("Check 2 contains: "+ Arrays.toString(Check2));
        System.out.println("Check 3 contains: "+ Arrays.toString(Check3));
    }
    public void level3_testProperties(Set<Property> properties){
        Set<String> stringSet1 = new HashSet<>(Arrays.asList(Check1));
        Set<String> stringSet2 = new HashSet<>(Arrays.asList(Check2));
        Set<String> stringSet3 = new HashSet<>(Arrays.asList(Check3));

        stringSet1.addAll(Arrays.asList(RunEthicalChecks.level3_check1(ontModel, properties)));
        stringSet2.addAll(Arrays.asList(RunEthicalChecks.level3_check2(ontModel, properties)));
        stringSet3.addAll(Arrays.asList(RunEthicalChecks.level3_check3(ontModel, properties)));

        Check1 = stringSet1.toArray(new String[0]);
        Check2 = stringSet2.toArray(new String[0]);
        Check3 = stringSet3.toArray(new String[0]);

        System.out.println("Ran extra tests on dataset: ");
        System.out.println("Check 1 contains: "+ Arrays.toString(Check1));
        System.out.println("Check 2 contains: "+ Arrays.toString(Check2));
        System.out.println("Check 3 contains: "+ Arrays.toString(Check3));
    }

    public String get_JSON(){
        String title, description, ont_checks1, ont_checks2, ont_checks3, foops_score;
        if (this.title!=null){
            title = "\"ontology_title\": \""+this.title+"\",\n";
        }else{
            title = "\"ontology_title\": \"none\",\n";
        }
        if (this.description!=null){
            description = "\"ontology_description\": \""+this.description+"\",\n";
        }else{
            description = "\"ontology_description\": \"none\",\n";
        }
        if(this.foopsResult !=null){
            foops_score = "\"foops_score\": \""+this.foopsResult.getOverall_score()+"\",\n";
        }else{
            foops_score = "\"foops_score\": \"none\",\n";
        }
        if (this.Check1!=null){
            ont_checks1= "\"ontology_checks1\": \""+ Arrays.toString(this.Check1) +"\",\n";
        }else{
            ont_checks1="\"ontology_checks1\": \"none\",\n";
        }
        if (this.Check2!=null){
            ont_checks2= "\"ontology_checks2\": \""+ Arrays.toString(this.Check2) +"\",\n";
        }else{
            ont_checks2="\"ontology_checks2\": \"none\",\n";
        }
        if (this.Check3!=null){
            ont_checks3= "\"ontology_checks3\": \""+ Arrays.toString(this.Check3) +"\"\n";
        }else{
            ont_checks3="\"ontology_checks3\": \"none\"\n";
        }
        String out = "{\n\"ontology_uri\": \""+ this.uri +"\",\n " +
                title +
                description +
                foops_score +
                ont_checks1 +
                ont_checks2 +
                ont_checks3 + "}\n";
        return out;
    }

    private void printMetadata(){
        System.out.println("Title: "+title);
        System.out.println("Description: "+description);
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

    public String[] getCheck1() {
        return Check1;
    }
    public String[] getCheck2() {
        return Check2;
    }
    public String[] getCheck3() {
        return Check3;
    }
}
