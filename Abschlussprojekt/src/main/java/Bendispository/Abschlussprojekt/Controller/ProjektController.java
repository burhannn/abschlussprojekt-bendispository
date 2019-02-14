package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

import static Bendispository.Abschlussprojekt.model.RequestStatus.APPROVED;
import static Bendispository.Abschlussprojekt.model.RequestStatus.DENIED;
import static java.lang.Enum.valueOf;


@Controller
public class ProjektController {

    @Autowired
    ItemRepo itemRepo;
    @Autowired
    PersonsRepo personRepo;
    @Autowired
    RequestRepo requestRepo;


    @GetMapping(path = "/addItem")
    public String addItemPage(){
        return "addItem";
    }

    @PostMapping(path = "/addItem")
    public String addItemsToDatabase(Model model, Item item){
        model.addAttribute("newItem", item);
        itemRepo.save(item);
        return "addItem";
    }

    @GetMapping(path = "/Item/{id}" )
    public String ItemProfile(Model model, @PathVariable Long id) {
        Optional <Item> item = itemRepo.findById(id);
        model.addAttribute("itemProfile", item.get());
        model.addAttribute("itemOwner", item.get().getOwner());
        return "itemProfile";
    }

    @GetMapping(path="/registration")
    public String SaveRegistration(Model model){
        return "registration";

    }

    @PostMapping(path = "/registration")
    public String Registration(Model model, Person person) {
        model.addAttribute("newPerson", person);
        personRepo.save(person);
        return "registration";
    }

    @GetMapping(path= "/")
    public String Overview(Model model){
        List<Item> all = itemRepo.findAll();
        model.addAttribute("OverviewAllItems", all);
        return "overviewAllItems";
    }

    @GetMapping(path= "/profile/{id}")
    public String Overview(Model model, @PathVariable Long id){
        Optional<Person> person = personRepo.findById(id);
        personRepo.findById(id).ifPresent(o -> model.addAttribute("person",o));
        return "profile";
    }

    @GetMapping(path="/profile/{id}/requests")
    public String Requests(Model model, @PathVariable Long id){
        setRequests(model,id);
        return "requests";
    }
    @PostMapping(path="/profile/{id}/requests")
    public String AcceptDeclineRequests(Model model,
                                        @PathVariable Long id,
                                        Long requestID,
                                        Integer requestMyItems){
        Request request = requestRepo.findById(requestID).orElse(null);
        request.setStatus(requestMyItems == -1 ? RequestStatus.DENIED : RequestStatus.APPROVED);
        requestRepo.save(request);
        setRequests(model,id);
        return "requests";
    }

    @GetMapping(path="/profile/{id}/rentedItems")
    public String rentedItems(Model model, @PathVariable Long id){
        Person me = personRepo.findById(id).orElse(null);
        List<Request> myRentedItems = requestRepo.findByRequesterAndStatus(me, APPROVED);
        model.addAttribute("myRentedItems", myRentedItems);
        return "rentedItems";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @PostMapping("/login")
    public String loggedIn() {
        return "OverviewAllItems";

    @GetMapping(path= "/profilub")
    public String profilPage(Model model){
        List<Person> all = personRepo.findAll();
        model.addAttribute("personen", all);
        return "profileDetails";
    }

    private void setRequests(Model model, Long id) {
        Person me = personRepo.findById(id).orElse(null);
        List<Request> listMyRequests = requestRepo.findByRequester(me);
        model.addAttribute("myRequests", listMyRequests);
        List<Request> RequestsMyItems = requestRepo.findByRequestedItemOwner(me);
        model.addAttribute("requestsMyItems", RequestsMyItems);
    }
}