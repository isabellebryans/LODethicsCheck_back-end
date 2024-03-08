package Server;
import Checker.Dataset;
import Utilities.LoadModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
