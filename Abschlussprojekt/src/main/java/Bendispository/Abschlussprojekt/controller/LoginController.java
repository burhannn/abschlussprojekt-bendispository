package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Rating;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {

	List<Rating> ratings = new ArrayList<>();
	@Autowired
	PersonsRepo personRepo;

	@GetMapping(path = "/registration")
	public String SaveRegistration(Model model) {
		return "authTmpl/registration";
	}

	@PostMapping(path = "/registration")
	public String Registration(Model model,
							   Person person) {
		model.addAttribute("newPerson", person);
		if (personRepo.findByUsername(person.getUsername()) == null) {
			person.setRatings(ratings);
			personRepo.save(person);
			return "authTmpl/login";
		} else {
			return "authTmpl/registrationError";
		}
	}

	@GetMapping("/login")
	public String login() {
		return "authTmpl/login";
	}

	@PostMapping("/login")
	public String loggedIn() {
		return "OverviewAllItems";
	}

	@GetMapping("/loggedOut")
	public String logout() {
		return "authTmpl/loggedOut";
	}
}
