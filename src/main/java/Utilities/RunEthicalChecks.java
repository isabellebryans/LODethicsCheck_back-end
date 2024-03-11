package Utilities;

import Checker.Ontology;
import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;

import java.util.ArrayList;
import java.util.Set;

public class RunEthicalChecks {
    static final String[] Check1 = {"child", "criminal", "disab", "latitude"};
    static final String[] Check2 = {"sex", "gender", "age", "ethnic","post",  "religion", "nationality"};
    static final String[] Check3 = {"crime", "educat", "assault", "income", "unemploy"};


    // Level 1 and 2 checks
    public static String[] runCheck1(Ontology o){
        return runChecks(o, Check1);
    }
    public static String[] runCheck2(Ontology o){
        return runChecks(o, Check2);
    }

    public static String[] runCheck3(Ontology o){
        return runChecks(o, Check3);
    }

    private static String[] runChecks(Ontology o, String[] words){
        Model m = o.getOntModel();
        ArrayList<String> terms_found = new ArrayList<>();
        for (String word : words) {
            // Check title
            if (o.getTitle()!=null && o.getTitle().contains(word)){
                terms_found.add(word);
                continue;
            }
            // Check description
            if (o.getDescription()!=null && o.getDescription().contains(word)){
                terms_found.add(word);
                continue;
            }
            // Check rdfs:labels in model
            String query_string = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    "SELECT * " +
                    "WHERE{" +
                    "?s rdfs:label ?o." +
                    "FILTER (CONTAINS(?o, \""+ word + "\") )" +
                    "}";
            Query q = QueryFactory.create(query_string);
            QueryExecution qexec = QueryExecutionFactory.create(q, m);
            try{
                ResultSet results = qexec.execSelect();
                while (results.hasNext()){
                    terms_found.add(word);
                    break;
                }
            } finally {
                qexec.close();
            }
        }

        return terms_found.toArray(new String[0]);
    }

    public static String[] level3_check1(Model m, Set<Property> properties){
        return test_properties(m,properties, Check1);
    }
    public static String[] level3_check2(Model m, Set<Property> properties){
        return test_properties(m, properties, Check2);
    }
    public static String[] level3_check3(Model m, Set<Property> properties){
        return test_properties(m, properties, Check3);
    }

    private static String[] test_properties(Model m, Set<Property> properties, String[] words){
        ArrayList<String> terms_found = new ArrayList<>();
        for (String word : words){
            for(Property p : properties){
                String p_string = p.getLocalName();
                System.out.println("Testing "+ p_string +" for "+word);
                if (p_string.contains(word)){
                    terms_found.add(word);
                    break;
                }
            }
        }
        return terms_found.toArray(new String[0]);
    }
    private static String[] test_properties1(Model m, String[] words){
        ArrayList<String> terms_found = new ArrayList<>();
        for (String word : words) {
            System.out.println("Testing for "+ word +" in predicates");
            // Check predicates in model
            String query_string =
                    "SELECT ?p " +
                    "WHERE{" +
                    "?s ?p ?o." +
                    "FILTER (CONTAINS(?p, \""+ word + "\") )" +
                    "}";
            System.out.println(query_string);
            Query q = QueryFactory.create(query_string);
            try (QueryExecution qexec = QueryExecutionFactory.create(q, m)) {
                ResultSet results = qexec.execSelect();
                while (results.hasNext()) {
                    System.out.println(results.nextSolution().getResource("p").getURI());
                    terms_found.add(word);
                    System.out.println("Found: "+word);

                }
            }

        }
        return terms_found.toArray(new String[0]);
    }


}
