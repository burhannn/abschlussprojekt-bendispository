package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.Model.Request;
import Bendispository.Abschlussprojekt.Repo.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class RequestController {
    
    @Autowired
    RequestRepo requestRepo;

    @GetMapping(path = "/item{id}/requestItem")
    public String request(Model model){
        return "formRequest";
    }

    @GetMapping(path = "/profile/request")
    public String listAllRequests(Model model){
        List<Request> allRequests = requestRepo.findAll();
        model.addAttribute("allRequests", allRequests);
        return "request";
    }

    @PostMapping(path = "/item{id}/requestItem")
    public String addRequestToLender(Model model, @PathVariable Long id, Request request){
        model.addAttribute("Request", request);
        model.addAttribute("requestedItem", request.getRequestedItem());
        requestRepo.save(request);
        return "formRequest";
    }
}

