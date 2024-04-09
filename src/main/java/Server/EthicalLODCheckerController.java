package Server;
import Entities.Dataset;
import Entities.Test;
import OntologyService.LoadModel;
import Utils.parseJSONtests;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
@CrossOrigin
@RestController
public class EthicalLODCheckerController {
        @PostMapping("/upload")
        public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                       @RequestParam("json") String json) {
            String response;
            Test[] tests = parseJSONtests.parseJSONtestsFromResources();
            // Process the file
            try {
                // Create a temporary file in the system's temporary directory
                Path tempFile = Files.createTempFile(null, file.getOriginalFilename());
                file.transferTo(tempFile.toFile());
                // Process the file
                Model m = LoadModel.initAndLoadModelFromFolder(tempFile.toFile(), Lang.RDFXML);
                if (m==null){
                    response = "Failed to get model from file";
                }
                else {
                    Dataset dataset = new Dataset(m, file.getOriginalFilename(), tests);
                    // After processing, delete file
                    Files.delete(tempFile);
                    System.out.println("Here");
                    // Return a success response
                    response = dataset.export_JSON();
                }
            } catch (IOException e) {
                e.printStackTrace();
                response = "errorMessage: "+ e.getMessage();

            }
            return response;
        }
}
