package Server;
import Checker.Dataset;
import Utilities.DownloadFile;
import Utilities.LoadModel;
import org.apache.jena.base.Sys;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
@CrossOrigin
@RestController
public class EthicalLODCheckerController {
        @GetMapping("/hello")
        public ResponseEntity<String> sayHello() {
            System.out.println("Hello!");
            return ResponseEntity.ok("Hello world!");
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
        public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
            // Process the file
            try {
                // Create a temporary file in the system's temporary directory
                Path tempFile = Files.createTempFile(null, file.getOriginalFilename());

                // Transfer the uploaded file to the temporary file
                file.transferTo(tempFile.toFile());

                // Process the file
                Model m = LoadModel.initAndLoadModelFromFolder(tempFile.toFile(), Lang.RDFXML);
                if (m==null){
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to get model from file");

                }
                Dataset dataset = new Dataset(m);
                // After processing, if you no longer need the file, consider deleting it
                Files.delete(tempFile);
                System.out.println("Here");
                // Return a success response
                //return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename() + " at " + tempFile);
                ResponseEntity<String> res = ResponseEntity.ok().build();
                System.out.println(res.toString());
                return ResponseEntity.ok().build();
            } catch (IOException e) {
                e.printStackTrace();
                // Return an error response with a 500 Internal Server Error status
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
            }
        }


}
