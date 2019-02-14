package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RequestController {
    
    @Autowired
    RequestRepo requestRepo;
    @Autowired
    ItemRepo itemRepo;

    @GetMapping(path = "/item{id}/requestItem")
    public String request(Model model, @PathVariable Long id){
        itemRepo.findById(id).ifPresent(o -> model.addAttribute("thisItem",o));
        return "formRequest";
    }

    @PostMapping(path = "/item{id}/requestItem")
    public String addRequestToLender(@ModelAttribute("request") Request request,
                                     Model model,
                                     @PathVariable Long id,
                                     int duration
                                     ){
        Item item = itemRepo.findById(id).orElse(null);
        /*Person me = personRepo.findById(MEINE_ID).orELse(null);
        request.setRequester(me);*/
        request.setRequestedItem(item);
        request.setDuration(duration);
        request.setStatus(RequestStatus.PENDING);
        item.setAvailable(false);
        requestRepo.save(request);
        itemRepo.findById(id).ifPresent(o -> model.addAttribute("thisItem",o));
        return "formRequest";
    }
}

