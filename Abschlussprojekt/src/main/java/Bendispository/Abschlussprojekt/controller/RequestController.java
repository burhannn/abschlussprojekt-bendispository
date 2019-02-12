package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.repo.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RequestController {
    @Autowired
    RequestRepo requestRepo;

    @GetMapping(path = "/item+{id}/request"){
        public String request(Model model){
            return "request";
        }
    }
}
