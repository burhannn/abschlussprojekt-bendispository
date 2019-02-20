package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import org.springframework.beans.factory.annotation.Autowired;
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
        MyUserPrincipal userDetails = (MyUserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }
}
