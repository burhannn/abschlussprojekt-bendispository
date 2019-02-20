package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.UploadFile;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


@Controller
public class ProjektController {

    ItemRepo itemRepo;
    PersonsRepo personRepo;
    RequestRepo requestRepo;
    AuthenticationService authenticationService;

    @Autowired
    public ProjektController(ItemRepo itemRepo, PersonsRepo personsRepo, RequestRepo requestRepo, AuthenticationService authenticationService){
        this.itemRepo = itemRepo;
        this.personRepo = personsRepo;
        this.requestRepo = requestRepo;
        this.authenticationService = authenticationService;
    }

    @GetMapping(path = "/addItem")
    public String addItemPage(){
        return "AddItem";
    }

    @PostMapping(path = "/addItem", consumes = {"multipart/form-data"})
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
        return "AddItem";
    }

    @GetMapping(path = "/Item/{id}" )
    public String ItemProfile(Model model,
                              @PathVariable Long id) {

        Item item = itemRepo.findById(id).orElse(null);
        model.addAttribute("itemProfile", item);
        model.addAttribute("itemOwner", item.getOwner());

        if(item.getUploadFile() != null){
            model.addAttribute("pic", Base64.getEncoder().encodeToString((item.getUploadFile().getData())));
        }else{
            model.addAttribute("pic",null);
        }
        return "itemProfile";
    }

    @GetMapping(path= "/")
    public String Overview(Principal principal, Model model){
        Person loggedIn = authenticationService.getCurrentUser();
        List<Item> allOtherItems = itemRepo.findByOwnerNot(personRepo.findByUsername(loggedIn.getUsername()));
        model.addAttribute("OverviewAllItems", allOtherItems);
        model.addAttribute("loggedInPerson",loggedIn);
        return "overviewAllItems";
    }

    @GetMapping(path= "/profile")
    public String profile(Model model){
        Person loggedIn = authenticationService.getCurrentUser();
        model.addAttribute("person", loggedIn);
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
        model.addAttribute("loggedInPerson", authenticationService.getCurrentUser());
        return "profileDetails";
    }

    @GetMapping("/trouble")
    public String trouble(){
        Person loggedin = authenticationService.getCurrentUser();
        if(loggedin.getUsername().equals("admin")) return "trouble";
        else return "redirect:/";
    }


    @GetMapping(value="deleteUser/{username}")
    public String deleteUser(@PathVariable String username){
        Person deletePerson = personRepo.findByUsername(username);
        personRepo.delete(deletePerson);
        return "redirect:/profilub";
    }
}
