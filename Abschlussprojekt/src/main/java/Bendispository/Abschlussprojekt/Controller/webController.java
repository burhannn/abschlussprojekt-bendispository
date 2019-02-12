package Bendispository.Abschlussprojekt.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class webController {

    @GetMapping("/login")
    public String einloggen(){
        return "login.html";
    }
}
