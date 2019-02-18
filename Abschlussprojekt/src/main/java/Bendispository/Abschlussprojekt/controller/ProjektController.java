package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@Controller
public class ProjektController {

    @Autowired
    ItemRepo itemRepo;

    @Autowired
    PersonsRepo personRepo;

    @Autowired
    RequestRepo requestRepo;

    AuthenticationService authenticationService;

    public ProjektController(ItemRepo itemRepo, PersonsRepo personsRepo, RequestRepo requestRepo){
        this.itemRepo = itemRepo;
        this.personRepo = personsRepo;
        this.requestRepo = requestRepo;
        this.authenticationService = new AuthenticationService(personRepo);
    }


    @GetMapping(path = "/addItem")
    public String addItemPage(){
        return "AddItem";
    }

    @PostMapping(path = "/addItem")
    public String addItemsToDatabase(Model model,
                                     Item item){

        Person loggedIn = authenticationService.getCurrentUser();
        model.addAttribute("newItem", item);


        item.setOwner(loggedIn);
        itemRepo.save(item);

        List<Item> itemsOwner = new ArrayList<>();
        itemsOwner.addAll(itemRepo.findByOwner(loggedIn));
        loggedIn.setItems(itemsOwner);

        personRepo.save(loggedIn);
        return "AddItem";
    }

    @GetMapping(path = "/Item/{id}" )
    public String ItemProfile(Model model,
                              @PathVariable Long id) {
        Optional <Item> item = itemRepo.findById(id);
        model.addAttribute("itemProfile", item.get());
        model.addAttribute("itemOwner", item.get().getOwner());
        return "itemProfile";
    }

    @GetMapping(path= "/")
    public String Overview(Model model){
        List<Item> all = itemRepo.findAll();

        Person loggedIn = authenticationService.getCurrentUser();
        model.addAttribute("OverviewAllItems", all);
        model.addAttribute("loggedInPerson",loggedIn);
        return "overviewAllItems";
    }

    @GetMapping(path= "/profile")
    public String profile(Model model){

        Person loggedIn = authenticationService.getCurrentUser();
        model.addAttribute("person",loggedIn);
        return "profile";
    }

    @GetMapping(path= "/profile/{id}")
    public String profileOther(Model model,
                               @PathVariable Long id){
        Optional<Person> person = personRepo.findById(id);
        personRepo.findById(id).ifPresent(o -> model.addAttribute("person",o));
        return "profileOther";
    }

    @GetMapping(path= "/profilub")
    public String profilPage(Model model){
        List<Person> all = personRepo.findAll();
        model.addAttribute("personen", all);
        return "profileDetails";
    }

}
