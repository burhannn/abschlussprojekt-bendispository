package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@Controller
public class LoginController {

    @Autowired
    PersonsRepo personRepo;

    @GetMapping(path="/registration")
    public String SaveRegistration(Model model){
        return "registration";

    }

    @PostMapping(path = "/registration")
    public String Registration(Model model,
                               Person person) {
        model.addAttribute("newPerson", person);
        personRepo.save(person);
        return "login";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String loggedIn() {
        return "OverviewAllItems"; }
}
