package Bendispository.Abschlussprojekt.Controller;

import Bendispository.Abschlussprojekt.Model.Item;
import Bendispository.Abschlussprojekt.Model.Person;
import Bendispository.Abschlussprojekt.Repo.ItemRepo;
import Bendispository.Abschlussprojekt.Repo.PersonsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Optional;


@Controller
public class ProjektController {
    @Autowired
    ItemRepo itemRepo;
    @Autowired
    PersonsRepo personRepo;

    @GetMapping(path = "/addItem")
    public String addItemPage(){
        return "AddItem";
    }

    @PostMapping(path = "/addItem")
    public String addItemsToDatabase(Model model, Item item){
        model.addAttribute("newItem", item);
        itemRepo.save(item);
        return "AddItem";
    }

    @GetMapping(path = "/Item/{id}" )
    public String ItemProfile(Model model, @PathVariable Long id) {
        Optional <Item> item = itemRepo.findById(id);
        model.addAttribute("itemProfile", item.get());
        return "ItemProfile";
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
        return "OverviewAllItems";
    }
    @GetMapping(path= "/profile/{id}")
    public String Overview(Model model, @PathVariable Long id){
        Optional<Person> person = personRepo.findById(id);
        personRepo.findById(id).ifPresent(o -> model.addAttribute("person",o));
        return "profile";
    }

    @GetMapping(path= "/profilub")
    public String profilPage(Model model){
        List<Person> all = personRepo.findAll();
        model.addAttribute("personen", all);
        return "profileDetails";
    }
}