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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static Bendispository.Abschlussprojekt.model.RequestStatus.APPROVED;


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
        return "AddItem";
    }

    @PostMapping(path = "/addItem")
    public String addItemsToDatabase(Model model,
                                     Item item){
        Person loggedIn = PersonLoggedIn();
        model.addAttribute("newItem", item);

        item.setOwner(loggedIn);
        itemRepo.save(item);
        List<Item> itemsOwner = itemRepo.findByOwner(loggedIn);
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

    @GetMapping(path="/registration")
    public String SaveRegistration(Model model){
        return "registration";

    }

    @PostMapping(path = "/registration")
    public String Registration(Model model,
                               Person person) {
        model.addAttribute("newPerson", person);
        if(personRepo.findByUsername(person.getUsername())== null) {
            personRepo.save(person);
            return "login";
        } else {
            return "registrationError";
        }
    }

    @GetMapping(path= "/")
    public String Overview(Model model){
        List<Item> all = itemRepo.findAll();
        Person loggedIn = PersonLoggedIn();
        model.addAttribute("OverviewAllItems", all);
        model.addAttribute("loggedInPerson",loggedIn);
        return "overviewAllItems";
    }

    @GetMapping(path= "/profile")
    public String profile(Model model){
        Person loggedIn = PersonLoggedIn();
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

    @GetMapping(path="/profile/requests")
    public String Requests(Model model){
        Long id = PersonLoggedIn().getId();
        setRequests(model,id);
        return "requests";
    }
    @PostMapping(path="/profile/requests")
    public String AcceptDeclineRequests(Model model,
                                        Long requestID,
                                        Integer requestMyItems){
        Request request = requestRepo.findById(requestID).orElse(null);
        request.setStatus(requestMyItems == -1 ? RequestStatus.DENIED : RequestStatus.APPROVED);
        requestRepo.save(request);
        Long id = PersonLoggedIn().getId();
        setRequests(model,id);
        return "requests";
    }

    @GetMapping(path="/profile/rentedItems")
    public String rentedItems(Model model){
        Long id = PersonLoggedIn().getId();
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
        return "OverviewAllItems"; }

    @GetMapping(path= "/profilub")
    public String profilPage(Model model){
        List<Person> all = personRepo.findAll();
        model.addAttribute("personen", all);
        model.addAttribute("loggedInPerson", PersonLoggedIn());
        return "profileDetails";
    }

    private void setRequests(Model model,
                             Long id) {
        Person me = personRepo.findById(id).orElse(null);
        List<Request> listMyRequests = requestRepo.findByRequester(me);
        model.addAttribute("myRequests", listMyRequests);
        List<Request> RequestsMyItems = requestRepo.findByRequestedItemOwner(me);
        model.addAttribute("requestsMyItems", RequestsMyItems);
    }

    private Person PersonLoggedIn(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        Person loggedIn = personRepo.findByUsername(name);
        return loggedIn;
    }

	@GetMapping("/loggedOut")
	public String logout(){
		return "loggedOut";
	}

	@GetMapping("/trouble")
	public String trouble(){
    	if(PersonLoggedIn().getUsername().equals("admin")) return "trouble";
		else return "redirect:/";
	}
	@GetMapping(value="deleteUser/{username}")
    public String deleteUser(@PathVariable String username){
        Person deletePerson = personRepo.findByUsername(username);
        personRepo.delete(deletePerson);
        return "redirect:/profilub";
    }
}
