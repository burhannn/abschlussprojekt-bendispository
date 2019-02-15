package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import java.time.LocalDate;
import java.time.Period;

import static Bendispository.Abschlussprojekt.service.ProPaySubscriber.*;
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
                                     @PathVariable Long id
                                     //@RequestParam("startDay")
                                     ){
        String username = "";
        Item item = itemRepo.findById(id).orElse(null);
        if(checkDeposit(item.getDeposit(), username)){


            /*

            Kaution reicht aus, wird "abgeschickt" (erstellt und gespeichert)

            request.setRequestedItem(item);
            LocalDate startDate = LocalDate.of(1,1,1), endDate = LocalDate.of(2,1,1);
            request.setDuration(Period.between(startDate, endDate).getDays());
            requestRepo.save(request);
            itemRepo.findById(id).ifPresent(o -> model.addAttribute("thisItem",o));
            return "formRequest";
            */


        }
        return "Could_not_send_Request";
    }
}

