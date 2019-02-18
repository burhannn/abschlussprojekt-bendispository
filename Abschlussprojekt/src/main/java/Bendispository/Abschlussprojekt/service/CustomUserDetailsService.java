package Bendispository.Abschlussprojekt.service;


import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// testet ob User existieren

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonsRepo personsRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person user = personsRepo.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("Invalid Username");
        }
        return new MyUserPrincipal(user);
    }

    public Person PersonLoggedIn(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        Person loggedIn = personsRepo.findByUsername(name);
        return loggedIn;
    }

}

