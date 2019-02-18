package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {

    private final PersonsRepo personsRepo;

    @Autowired
    public AuthenticationService(PersonsRepo personsRepo){
        this.personsRepo = personsRepo;
    }

    public Person getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        return personsRepo.findByUsername(name);
    }
}
