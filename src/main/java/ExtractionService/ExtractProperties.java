package ExtractionService;

import Entities.Namespace;
import org.apache.jena.rdf.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Methods to extract properties from a rdf model
public class ExtractProperties {

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

    public static Set<Resource> extractObjects(Model model){
        // Collect unique properties
        Set<Resource> objects = new HashSet<>();
        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            RDFNode objct = stmt.getObject();
            if (!objct.isLiteral()){
                // Make sure not literal OR blank node
                if(objct.asResource().getURI()!=null){
                    objects.add(objct.asResource());
                }
            }

        }

        // Print the unique properties
        System.out.println("Unique Object Resources:");
        for (Resource resource : objects) {
            System.out.println(resource.getURI());
        }
        return objects;
    }



    public static List<Namespace> extractNamespaces(Set<Property> properties, Model model){
        Set<Resource> resources = extractObjects(model);
        Set<String> namespace_string = new HashSet<>();
        ArrayList<Namespace> namespaces1 = new ArrayList<>();
        for (Property property : properties) {
            String namespace = extractNamespace(property.getURI());
            namespace_string.add(namespace);
        }
        for (Resource r : resources){
            String namespace = extractNamespace(r.getURI());
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
