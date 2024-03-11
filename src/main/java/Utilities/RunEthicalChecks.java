package Utilities;

import Checker.Ontology;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;

import java.util.ArrayList;

public class RunEthicalChecks {
    static final String[] Check1 = {"child", "criminal", "disab"};
    static final String[] Check2 = {"sex", "gender", "age", "ethnicity", "religion", "nationality"};
    static final String[] Check3 = {"crime", "education", "assault", "income"};



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
//                    found = true;
//                    QuerySolution solution = results.nextSolution();
//                    Resource name = solution.getResource("s");
//                    System.out.println(name);
                }
            } finally {
                qexec.close();
            }
        }

        return terms_found.toArray(new String[0]);
    }

}
