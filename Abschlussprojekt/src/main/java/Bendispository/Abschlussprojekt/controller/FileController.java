package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.UploadFile;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import Bendispository.Abschlussprojekt.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


@Controller
public class FileController {

    private ItemRepo itemRepo;
    private PersonsRepo personRepo;
    private RequestRepo requestRepo;
    private AuthenticationService authenticationService;
    private ItemService itemService;

    @Autowired
    public FileController(ItemRepo itemRepo,
                          PersonsRepo personRepo,
                          RequestRepo requestRepo,
                          AuthenticationService authenticationService,
                          ItemService itemService){
        this.itemRepo = itemRepo;
        this.personRepo = personRepo;
        this.requestRepo = requestRepo;
        this.authenticationService = authenticationService;
        this.itemService = itemService;
    }

    @GetMapping(path = "/additem")
    public String addItemPage(){
        return "itemTmpl/AddItem";
    }

    @PostMapping(path = "/additem", consumes = {"multipart/form-data"})
    public String addItemsToDatabase(Model model,
                                     @Valid @RequestParam("file") MultipartFile multipart,
                                     Item item) throws IOException, SQLException {

        String fileName = StringUtils.cleanPath(multipart.getOriginalFilename());
        UploadFile uploadFile = new UploadFile(fileName, multipart.getBytes());
        item.setUploadFile(uploadFile);
        Person loggedIn = authenticationService.getCurrentUser();
        model.addAttribute("newItem", item);
        item.setOwner(personRepo.findByUsername(loggedIn.getUsername()));
        itemRepo.save(item);
        List<Item> itemsOwner = new ArrayList<>();
        itemsOwner.addAll(itemRepo.findByOwner(loggedIn));
        loggedIn.setItems(itemsOwner);
        personRepo.save(loggedIn);
        return "redirect:/item/" + item.getId() + "";
    }

    @GetMapping(path = "/item/{id}" )
    public String ItemProfile(Model model,
                              @PathVariable Long id) {
        Item item = itemRepo.findById(id).orElse(null);
        model.addAttribute("itemProfile", item);
        model.addAttribute("itemOwner", item.getOwner());
        model.addAttribute("loggedInPerson", authenticationService.getCurrentUser());
        model.addAttribute("isAvailable", itemService.itemIsAvailable(id));
        if(item.getUploadFile() != null){
            model.addAttribute("pic", Base64.getEncoder().encodeToString((item.getUploadFile().getData())));
        }else{
            model.addAttribute("pic",null);
        }
        return "itemTmpl/itemProfile";
    }
    @RequestMapping(method=RequestMethod.GET, value="/deleteitem/{id}")
    public String deleteItem(@PathVariable("id") Long id,
                             Model model) {
        Item item = itemRepo.findById(id).orElse(null);
        Person person = item.getOwner();
        itemRepo.deleteById(id);
        person.deleteItem(item);
        personRepo.save(person);
        return "redirect:/";
    }

    @GetMapping(path = "/edititem/{id}")
    public String editItem(Model model,
                           @PathVariable Long id){
        Optional<Item> item = itemRepo.findById(id);
        Person loggedIn = authenticationService.getCurrentUser();
        model.addAttribute("Item", item.get());
        if(loggedIn.getUsername().equals(item.get().getOwner().getUsername())){
            return "itemTmpl/editItem";
        }

        return "redirect:/";
    }

    @PostMapping(path = "/edititem/{id}")
    public String editItemInDatabase(Model model,
                                     @PathVariable Long id, Item inpItem){
        Optional<Item> item = itemRepo.findById(id);
        model.addAttribute("Item", item.get());
        Person loggedIn = authenticationService.getCurrentUser();
        inpItem.setOwner(personRepo.findByUsername(loggedIn.getUsername()));
        List<Item> itemsOwner = itemRepo.findByOwner(loggedIn);
        loggedIn.setItems(itemsOwner);
        itemRepo.save(inpItem);
        return "redirect:/";
    }
}
