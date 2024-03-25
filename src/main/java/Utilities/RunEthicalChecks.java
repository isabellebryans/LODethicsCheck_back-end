package Utilities;

import Checker.RDFmodel;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;

import java.util.ArrayList;
import java.util.Set;

public class RunEthicalChecks {

    public static final String[] Check1 = {"child", "minor", "youth", "school",
            "homeless", "elderly", "senior", "retire",
            "migrant", "refugee", "asylum seeker", "immigrant",
            "criminal", "disab", "impair", "disadvantaged"};
    public static final String[] Check2 = {"sex", "gender", "age", "ethnic", "race", "religion", "nationality", "sexual orientation"};
    public static final String[] Check3 = {"medical", "health", "psychiatric", "addiction", "treatment", "disease", "disorder",
            "income", "debt", "credit", "poverty", "wealth", "salary","unemploy",
            "crime", "convict", "arrest", "incarcerat", "legal status","assault",
            "sexual orientation", "lgbt", "transgender",
            "offence",
            "political", "voting", "affiliation", "activism",
            "educat"};

    public static String[] runLevel1Checks(RDFmodel o, String[] terms){
        Model m = o.getModel();
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

            // Check rdfs:comment in model
            String query_string1 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>" +
                    "SELECT * " +
                    "WHERE{" +
                    "?s rdfs:comment ?o." +
                    "FILTER (CONTAINS(lcase(?o), \""+ term + "\") )" +
                    "}";
            Query q1 = QueryFactory.create(query_string1);
            QueryExecution qexec1 = QueryExecutionFactory.create(q1, m);
            try{
                ResultSet results1 = qexec1.execSelect();
                while (results1.hasNext()){
                    terms_found.add(term);
                    break;
                }
            } finally {
                qexec.close();
            }
        }
        return terms_found.toArray(new String[0]);
    }


    public static String[] test_properties(Model m, Set<Property> properties, String[] terms){
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

    public static String[] test_all_objects(Model m, String[] terms){
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


}
