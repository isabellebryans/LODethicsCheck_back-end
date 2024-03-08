package Server;
import Checker.Dataset;
import Utilities.DownloadFile;
import Utilities.LoadModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
public class EthicalLODCheckerController {
        @GetMapping("/hello")
        public String sayHello() {
            return "Hello, World!";
        }
        @GetMapping("/runCamdenEg")
        public String camdenEg() throws IOException {
            Model model1 = LoadModel.initAndLoadModelFromResource("static/streetCrimeCamden.rdf", Lang.RDFXML);
            if (model1==null){
                return "Can't load model from resource";
            }
            //return "Can load model";
            Dataset dataSet = new Dataset(model1);

            return "Done";
        }

        @PostMapping("/upload")
        public String handleFileUpload(@RequestParam("file") MultipartFile file) {
            // Process the file
            try {
                // Create a temporary file in the system's temporary directory
                Path tempFile = Files.createTempFile(null, file.getOriginalFilename());

                // Transfer the uploaded file to the temporary file
                file.transferTo(tempFile.toFile());

                // Process the file
                Model m = LoadModel.initAndLoadModelFromFolder(tempFile.toFile(), Lang.RDFXML);
                if (m==null){
                    return "Can't load model from the file, incorrect RDF";
                }
                Dataset dataset = new Dataset(m);
                // After processing, if you no longer need the file, consider deleting it
                Files.delete(tempFile);

                // Return a success response
                return "File uploaded successfully: " + file.getOriginalFilename() + " at " + tempFile;
            } catch (IOException e) {
                e.printStackTrace();
                return "Failed to upload file: " + e.getMessage();
            }
        }


}
