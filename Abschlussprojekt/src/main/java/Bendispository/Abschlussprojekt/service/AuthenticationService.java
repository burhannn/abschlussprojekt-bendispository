package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {
    public Person getCurrentUser(){
        MyUserPrincipal userDetails = (MyUserPrincipal)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userDetails.getUser();
    }
}
