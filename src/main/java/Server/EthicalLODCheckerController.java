package Server;
import Checker.Dataset;
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
        public ResponseEntity<String> handleFileUpload(@RequestParam("file") MultipartFile file) {
            // Process the file
            try {
                // Logic to process the file
                System.out.println("Original filename: " + file.getOriginalFilename());
                System.out.println("File size: " + file.getSize());

                // Place your file processing logic here

                return ResponseEntity.ok("File uploaded successfully: " + file.getOriginalFilename());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body("Could not upload the file: " + e.getMessage());
            }
        }


}
