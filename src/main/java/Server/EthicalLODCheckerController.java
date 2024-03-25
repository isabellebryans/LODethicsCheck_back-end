package Server;
import Checker.Dataset;
import Checker.Test;
import Utilities.DownloadFile;
import Utilities.LoadModel;
import Utilities.RunEthicalChecks;
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
import java.util.HashMap;
import java.util.Map;
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
           // Dataset dataSet = new Dataset(model1);

            return "Done";
        }

        @PostMapping("/upload")
        public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                       @RequestParam("json") String json) {
            String response;
            Test test1 = new Test("Vulnerability Test", "all", RunEthicalChecks.Check1);
            Test test2 = new Test("Discrimination Test", "properties", RunEthicalChecks.Check2);
            Test test3 = new Test("Sensitivity Test", "all", RunEthicalChecks.Check3);
            Test[] tests = {test1, test2, test3};
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
                // In case of failure, adjust the response accordingly
                response = "errorMessage: "+ e.getMessage();

            }
            return response;
        }


}
