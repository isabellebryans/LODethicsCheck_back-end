package ie.tcd.EthicalLODCheckerServer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EthicalLODCheckerController {
        @GetMapping("/hello")
        public String sayHello() {
            return "Hello, World!";
        }
}
