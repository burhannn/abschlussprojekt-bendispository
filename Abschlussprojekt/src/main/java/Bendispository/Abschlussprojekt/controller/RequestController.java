package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
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

    @GetMapping(path = "/item{id}/requestItem")
    public String request(Model model){
        return "formRequest";
    }

    @PostMapping(path = "/item{id}/requestItem")
    public String addRequestToLender(Model model, @PathVariable Long id, Request request){
        model.addAttribute("Request", request);
        model.addAttribute("requestedItem", request.getRequestedItem());
        requestRepo.save(request);
        return "formRequest";
    }
}

