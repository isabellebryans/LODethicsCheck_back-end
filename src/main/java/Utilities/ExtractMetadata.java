package Utilities;

import org.apache.jena.base.Sys;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.*;

public class ExtractMetadata {
    private static final String datasetQueryString = "PREFIX dq: <http://purl.org/linked-data/cube#>" +
            "PREFIX void: <http://rdfs.org/ns/void#>" +
            "PREFIX dcat: <http://www.w3.org/ns/dcat#>" +
            "PREFIX dctype: <http://purl.org/dc/dcmitype/>" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>"+
            "SELECT ?s  WHERE {VALUES ?datasetType { dq:DataSet void:Dataset dcat:Dataset dctype:Dataset }" +
                        "?s a ?datasetType .}";

    public static String extractDescription(Model model){
        // First option:
        NodeIterator descriptionIterator = model.listObjectsOfProperty(DCTerms.description);
        Literal description = get_literal_from_iterator(descriptionIterator);
        if (description != null){
            return description.getString();
        }
        // Second option:
        descriptionIterator = model.listObjectsOfProperty(DC_11.description);
        description = get_literal_from_iterator(descriptionIterator);
        if (description != null){
            return description.getString();
        }

        // Try third option:
        // ?ont a owl:Ontology.
        // ?ont rdfs:comment ?DESCRIPTION.
        ResIterator ontologies = model.listResourcesWithProperty(RDF.type, OWL.Ontology);
        if (ontologies.hasNext()) {
            Resource ontology = ontologies.nextResource();
            System.out.println(ontology.getURI()); // Print the URI of each ontology instance
            descriptionIterator = model.listObjectsOfProperty(ontology, RDFS.comment);
            description = get_literal_from_iterator(descriptionIterator);
            if (description != null){
                return description.getString();
            }
        }

        // Try fourth option:
        // ?ont a ?dataSetType.
        // ?ont rdfs:comment ?DESCRIPTION.
        RDFNode ds = getdataset(model);
        if (ds!=null) {
            descriptionIterator = model.listObjectsOfProperty(ds.asResource(), RDFS.comment);
            description = get_literal_from_iterator(descriptionIterator);
            if (description != null) {
                return description.getString();
            }
        }
        return null;
    }

    public static String extractTitle(Model model){
        // Try first option: ?s dc:title ?TITLE.
        NodeIterator titleIterator = model.listObjectsOfProperty(DC_11.title);
        Literal title = get_literal_from_iterator(titleIterator);
        if (title != null){
            return title.getString();
        }
        // Try second option: ?s dcterms:title ?TITLE.
        titleIterator = model.listObjectsOfProperty(DCTerms.title);
        title = get_literal_from_iterator(titleIterator);
        if (title != null){
            return title.getString();
        }
        // Try third option:
        // ?ds a owl:Ontology.
        // ?ds rdfs:label ?TITLE.
        ResIterator ontologies = model.listResourcesWithProperty(RDF.type, OWL.Ontology);
        if (ontologies.hasNext()) {
            Resource ontology = ontologies.nextResource();
            System.out.println(ontology.getURI()); // Print the URI of each ontology instance
            titleIterator = model.listObjectsOfProperty(ontology, RDFS.label);
            title = get_literal_from_iterator(titleIterator);
            if (title != null){
                return title.getString();
            }
        }
        // Try fourth option:
        // ?ds a ?datasetType.
        // ?ds rdfs:label ?TITLE.
        RDFNode ds = getdataset(model);
        if (ds!=null){
            titleIterator = model.listObjectsOfProperty(ds.asResource(), RDFS.label);
            title = get_literal_from_iterator(titleIterator);
            if (title != null){
                return title.getString();
            }
        }
        // If all fails, return null
        return null;
    }
    private static RDFNode getdataset(Model m){
        Query q = QueryFactory.create(datasetQueryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(q, m)) {
            ResultSet results = qexec.execSelect(); // Execute the query and get the results
            if (results.hasNext()) { // Check if there's at least one result
                QuerySolution solution = results.nextSolution(); // Get the next solution
                RDFNode subject = solution.get("s"); // Get the resource bound to ?subject
                System.out.println(subject); // Print out the resource (for debugging purposes)
                return subject; // Return the found resource
            } else {
                System.out.println("No dataset found.");
                return null; // Return null if no dataset is found
            }
        }
    }
    private static Literal get_literal_from_iterator(NodeIterator NI) {
        Literal literal = null;
        while (NI.hasNext()) {
            RDFNode node = NI.nextNode();
            if (node.isLiteral()) {
                literal = node.asLiteral();
                if ("en".equals(literal.getLanguage()) || literal.getLanguage().isEmpty()) { // Check if the literal's language is English
                    System.out.println("English Literal value: " + literal.getString());
                    return literal;
                }
            } else {
                // Handle non-literal nodes if needed
                System.out.println("Object of dcterms:title is not a Literal: "+node.toString());
            }

        }
        return null;
    }
}
