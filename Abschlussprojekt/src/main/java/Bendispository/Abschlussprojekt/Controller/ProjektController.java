package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repo.ItemRepo;
import Bendispository.Abschlussprojekt.repo.PersonsList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Optional;


@Controller
public class ProjektController {
    @Autowired
    ItemRepo itemRepo;
    @Autowired
    PersonsList personRepo;

    @GetMapping(path = "/addItem")
    public String addItemPage(){
        return "AddItem";
    }

    @PostMapping(path = "/addItem")
    public String addItemsToDatabase(Model model,@PathVariable Long id, Item item){
        model.addAttribute("newItem", item);
        itemRepo.save(item);
        return "AddItem";
    }

    @GetMapping(path = "/Item/{id}")
    public String ItemProfile(Model model, @PathVariable Long id) {
        Optional <Item> item = itemRepo.findById(id);
        model.addAttribute("itemProfile", item.get());
        return "ItemProfile";
    }

    @GetMapping(path = "/login")
    public String Overview(Model model) {
        return "login";
    }

    @GetMapping(path = "/registration")
    public String Registration(Model model, Person person) {
        model.addAttribute("newPerson", person);
        personRepo.save(person);
        return "registration";
    }
}
