package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.Model.Request;
import Bendispository.Abschlussprojekt.Repo.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RequestController {
    
    @Autowired
    RequestRepo requestRepo;

    @GetMapping(path = "/item+{id}/request")
    public String request(Model model){
        return "request";
    }

    @PostMapping(path = "/item+{id}/request")
    public String addRequestToLender(Model model, @PathVariable Long id, Request request){
        model.addAttribute("newRequest", request);
        requestRepo.save(request);
        return "request";
    }
}

