package OntologyService;

import Entities.RDFmodel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Methods to load ontologies from folder
public class LoadModel {
    public static RDFmodel[] loadOntologiesFromFolder(Path folder) throws IOException {
        // Create a File object for the temporary folder
        File tempFolder = new File(folder.toString());
        List<RDFmodel> onts = new ArrayList<>();
        // Check if the folder exists
        if (tempFolder.exists() && tempFolder.isDirectory()) {
            // List all files in the folder
            File[] files = tempFolder.listFiles();
            System.out.println("Temp folder is a directory");
            // Iterate through each file
            for (File file : files) {
                if (file.isFile()) {
                    try  {
                        String uri = LoadModel.getURIfromFile(file);

                        Model m = LoadModel.initAndLoadModelFromFolder(file, Lang.RDFXML);
                        if (m != null){
                            // System.out.println(m);
                            RDFmodel RDFmodel = new RDFmodel(m, uri);
                            // ADD ONTOLOGY TO ONTOLOGIES ARRAY HERE
                            onts.add(RDFmodel);
                        }
                    } catch (IOException e) {
                        System.out.println("Couldn't get model from file");
                    }
                }
            }
        } else {
            System.out.println("Temporary folder does not exist or is not a directory.");
            return null;
        }
        return onts.toArray(new RDFmodel[0]);
    }

    private static String getURIfromFile(File file) throws IOException{
        try {
            InputStream tempIn = new FileInputStream(file);
            String uri = readUntilDelimiter(tempIn, '>');
            tempIn.close();
            return shave_uri(uri);
        }
        catch(Exception e){
            System.out.println("Couldn't get URI");
        }
        return null;
    }

    private static String shave_uri(String input){
        // Check if the string starts with "<!--" and ends with "-->"
        if (input.startsWith("<!--") && input.endsWith("-->")) {
            // Remove the first 4 characters "<!--" and the last 3 characters "-->"
            String result = input.substring(4, input.length() - 3);
            return result;
        } else {
            System.out.println("The input string does not have the expected format.");
            return input;
        }
    }

    private static String readUntilDelimiter(InputStream inputStream, char delimiter) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        int character;

        while ((character = inputStream.read()) != -1) {
            char currentChar = (char) character;

            // Append the character to the StringBuilder
            stringBuilder.append(currentChar);

            // Check if the delimiter character is encountered
            if (currentChar == delimiter) {
                break; // Stop reading if the delimiter is found
            }
        }

        return stringBuilder.toString();
    }

    public static Model initAndLoadModelFromFolder(File newFile, Lang lang) throws IOException {
        // Turn file into input stream to be read
        try {
            InputStream dataModelIS = new FileInputStream(newFile);
            Model dataModel = ModelFactory.createDefaultModel();
            RDFDataMgr.read(dataModel, dataModelIS, lang);
            return dataModel;
        } catch(Exception e){
            System.out.println("Couldn't load model");
        }
        return null;
    }

}
