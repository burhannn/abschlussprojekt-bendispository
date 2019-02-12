package Bendispository.Abschlussprojekt.Controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.repo.ItemsList;
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
    ItemsList itemsList;

    @GetMapping(path = "/{id}/addItem")
    public String addItemPage(){
        return "AddItem";
    }

    @PostMapping(path = "/{id}/addItem")
    public String addItemsToDatabase(Model model,@PathVariable Long id, Item item){
        model.addAttribute("newItem", item);
        itemsList.save(item);
        return "AddItem";
    }

    @GetMapping(path = "/Item/{id}")
    public String ItemProfile(Model model, @PathVariable Long id){
        Optional<Item> item = itemsList.findById(id);
        model.addAttribute("itemProfile", item.get());
        return "ItemProfile";
    }

    @GetMapping(path = "/overview")
    public String Overview(Model model){
        return "overview";
    }

    @GetMapping(path = "/overview/registration")
    public String Registration(Model model){
        return "registration";
    }

    @GetMapping(path = "/overview/login")
    public String login(Model model){
        return "login";
    }
}
