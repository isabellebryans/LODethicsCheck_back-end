package Utilities;

import org.apache.jena.rdf.model.*;
import org.apache.jena.vocabulary.DC_11;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Extract properties from a rdf model
public class ExtractionMethods {

    public static Set<Property> extractProperties(Model model){
        // Collect unique properties
        Set<Property> properties = new HashSet<>();
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Property property = stmt.getPredicate();
            properties.add(property);
        }

        // Print the unique properties
        System.out.println("Unique Properties:");
        for (Property property : properties) {
            System.out.println(property.getURI());
        }
        return properties;
    }

    public static List<Namespace> extractNamespaces(Set<Property> properties){
        Set<String> namespace_string = new HashSet<>();
        ArrayList<Namespace> namespaces1 = new ArrayList<>();
        for (Property property : properties) {
            String namespace = extractNamespace(property.getURI());
            namespace_string.add(namespace);
        }
        for (String ns : namespace_string) {
            Namespace namespace = new Namespace(ns);
            namespaces1.add(namespace);
        }
        return namespaces1;
    }

    private static String extractNamespace(String uri) {
        // Find the index of the last occurrence of '/' or '#'
        int lastSlashIndex = uri.lastIndexOf('/');
        int lastHashIndex = uri.lastIndexOf('#');

        // Determine the last index among '/' and '#'
        int lastIndex = Math.max(lastSlashIndex, lastHashIndex);

        if (lastIndex != -1) {
            // Extract and return the substring up to the last '/' or '#'
            return uri.substring(0, lastIndex + 1);
        }

        // If '/' or '#' is not found, return the whole URI
        return uri;
    }

}
