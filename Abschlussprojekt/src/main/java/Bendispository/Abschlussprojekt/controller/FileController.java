package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.UploadFile;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Convert;
import javax.validation.Valid;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Controller
public class FileController {
    @Autowired
    ItemRepo itemRepo;
    @Autowired
    PersonsRepo personRepo;
    @Autowired
    RequestRepo requestRepo;

    private static String UPLOADED_FOLDER = "F://temp//";

    @GetMapping(path = "/addItem")
    public String addItemPage(){
        return "AddItem";
    }

    @PostMapping(path = "/addItem", consumes = {"multipart/form-data"})
    public String addItemsToDatabase(@Valid @RequestParam("file")MultipartFile multipart,
                                     Item item) throws IOException, SQLException {
        Person loggedIn = PersonLoggedIn();

        String fileName = StringUtils.cleanPath(multipart.getOriginalFilename());
        UploadFile uploadFile = new UploadFile(fileName, multipart.getBytes());
        item.setUploadFile(uploadFile);

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
        Item item = itemRepo.findById(id).orElse(null);
        model.addAttribute("itemProfile", item);
        model.addAttribute("pic",Base64.getEncoder().encodeToString((item.getUploadFile().getData())));
        return "itemProfile";
    }
    private Person PersonLoggedIn(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        Person loggedIn = personRepo.findByUsername(name);
        return loggedIn;
    }
}
