package Bendispository.Abschlussprojekt.Service;


import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repo.PersonsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

}

