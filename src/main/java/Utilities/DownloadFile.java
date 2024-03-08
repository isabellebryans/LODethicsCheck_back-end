package Utilities;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.text.Utilities;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public class DownloadFile {
    private static int number;
    private static final Logger logger = LoggerFactory.getLogger(DownloadFile.class);
    private static final String[] common_vocabs ={
            "http://www.w3.org/1999/02/22-rdf-syntax-ns#",
            "http://www.w3.org/2000/01/rdf-schema#",
            "http://www.w3.org/2002/07/owl#",
            "http://www.w3.org/2001/XMLSchema#",
            "http://www.w3.org/ns/dcat#",
            "http://www.w3.org/2004/02/skos/core#",
            "http://purl.org/linked-data/cube#",
            "http://dbpedia.org/resource/"
    };

    public static boolean downloadOntology(String ontURL, Path tmpFolder) throws IOException {
        // Create temp folder
        // If the ontology is a common benign vocab, ignore
        if (Utils.ArrayContains(common_vocabs, ontURL)){

            return false;
        }
        number=number+1;
        String ontologyPath = tmpFolder + File.separator + "ontology" + number + ".rdf";
        downloadFile(ontURL, ontologyPath);
        logger.info("Loading ontology ");
        return true;
    }

    public static Path createTempFolder(){
        number=0;
        Path tmpFolder = null;
        try{
            tmpFolder = Files.createTempDirectory(Path.of("."), "onts");
        } catch(Exception e){
            logger.error("Could not create temporary folder. Exiting");
            return null;
        }
        return tmpFolder;
    }

    public static void downloadFile(String fileURL, String savePath) throws IOException {
        // Check if the URL starts with "http://"
        if (fileURL.startsWith("http://")) {
            // Replace "http://" with "https://"
            fileURL = fileURL.replaceFirst("http://", "https://");
        }

        if (fileURL.endsWith("/")|| fileURL.endsWith(("#"))) {
            // Remove the trailing "/"
            fileURL = fileURL.substring(0, fileURL.length() - 1);
        }
        // Prepend the fileURL to the content
        String firstLine = "<!--"+fileURL+"-->" + System.lineSeparator();

        URL url = new URL(fileURL+".rdf");
        System.out.println("Trying to download " + url.toString());
        try {
            URLConnection conn = url.openConnection();
            InputStream inputStream = conn.getInputStream();

            try (FileOutputStream outputStream = new FileOutputStream(savePath)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                outputStream.write(firstLine.getBytes());
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;

                // Skip the XML declaration and other processing instructions
                while ((line = reader.readLine()) != null) {
                    if (!line.startsWith("<?")) {
                        outputStream.write(line.getBytes());
                        outputStream.write(System.lineSeparator().getBytes());
                    }
                }
                System.out.println("Downloaded successfully");

            }
        } catch (IOException e) {
            // Print an error message if download fails
            System.out.println("Could not download file: " + e.getMessage());
        }
    }

    public static void removeTemporaryFolders(Path tmpFolder){
        try {
            FileUtils.deleteDirectory(new File(tmpFolder.toString()));
            logger.info("Deleted temp directory "+tmpFolder.toString());
            System.out.println("Deleted temp directory "+tmpFolder.toString());
        }catch(Exception e){
            logger.error("Could not delete tmp folder");
        }
    }
}
