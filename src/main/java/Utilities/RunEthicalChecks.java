package Utilities;

import Checker.Ontology;
import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import java.util.ArrayList;
import java.util.Set;

public class RunEthicalChecks {
    static final String[] Check1 = {"child", "minor", "youth", "school",
            "homeless", "elderly", "senior", "retire",
            "migrant", "refugee", "asylum seeker", "immigrant",
            "criminal", "disab", "impair", "disadvantaged"};
    static final String[] Check2 = {"sex", "gender", "age", "ethnic", "race", "religion", "nationality"};
    static final String[] Check3 = {"medical", "health", "psychiatric", "addiction", "treatment", "disease", "disorder",
            "income", "debt", "credit", "poverty", "wealth", "salary","unemploy",
            "crime", "convict", "arrest", "incarcerat", "legal status","assault",
            "sexual orientation", "lgbt", "transgender",
            "political", "voting", "affiliation", "activism",
            "educat"};


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

    private static String[] runChecks(Ontology o, String[] terms){
        Model m = o.getOntModel();
        ArrayList<String> terms_found = new ArrayList<>();
        for (String term : terms) {
            // Check title
            if (o.getTitle()!=null && o.getTitle().contains(term)){
                terms_found.add(term);
                continue;
            }
            // Check description
            if (o.getDescription()!=null && o.getDescription().contains(term)){
                terms_found.add(term);
                continue;
            }
            // Check rdfs:labels in model
            String query_string = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    "SELECT * " +
                    "WHERE{" +
                    "?s rdfs:label ?o." +
                    "FILTER (CONTAINS(lcase(?o), \""+ term + "\") )" +
                    "}";
            Query q = QueryFactory.create(query_string);
            QueryExecution qexec = QueryExecutionFactory.create(q, m);
            try{
                ResultSet results = qexec.execSelect();
                while (results.hasNext()){
                    terms_found.add(term);
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

    private static String[] test_properties(Model m, Set<Property> properties, String[] terms){
        ArrayList<String> terms_found = new ArrayList<>();
        for (String term : terms){
            for(Property p : properties){
                String p_string = p.getLocalName();
               // System.out.println("Testing "+ p_string +" for "+term);
                if (p_string.contains(term)){
                    terms_found.add(term);
                    break;
                }
            }
        }
        return terms_found.toArray(new String[0]);
    }

    public static String[] level4_check1(Model m){
        return test_all_objects(m, Check1);
    }
    public static String[] level4_check2(Model m){
        return test_all_objects(m, Check2);
    }
    public static String[] level4_check3(Model m){
        return test_all_objects(m, Check3);
    }
    private static String[] test_all_objects(Model m, String[] terms){
        ArrayList<String> terms_found = new ArrayList<>();
        for(String term:terms){
            StmtIterator it = m.listStatements();
            while(it.hasNext()){
                RDFNode object = it.nextStatement().getObject();

                if (object.isLiteral()){
                   // System.out.println("Testing literal: "+object.asLiteral().getString()+" for term: "+term);
                    if(object.asLiteral().getString().contains(term)){
                        terms_found.add(term);
                        break;
                    }
                } else if (!object.asNode().isBlank()){
                  //  System.out.println("Testing resource: "+object.asResource().getURI()+" for term: "+term);
                    if (object.asResource().getLocalName().contains(term)){
                        terms_found.add(term);
                        break;
                    }
                }
            }
        }
        return terms_found.toArray(new String[0]);
    }
//    private static String[] test_all_objects1(Model m, String[] terms){
//        ArrayList<String> terms_found = new ArrayList<>();
//        for(String term : terms){
//            String query_string =
//                    "SELECT * " +
//                    "WHERE{" +
//                    "?s ?p ?o." +
//                    "FILTER (CONTAINS(lcase(?o), \""+ term + "\") )" +
//                    "}";
//            Query q = QueryFactory.create(query_string);
//            QueryExecution qexec = QueryExecutionFactory.create(q, m);
//            try{
//                ResultSet results = qexec.execSelect();
//                while (results.hasNext()){
//                    terms_found.add(term);
//                    break;
//                }
//            } finally {
//                qexec.close();
//            }
//        }
//        return terms_found.toArray(new String[0]);
//    }

}
