package Bendispository.Abschlussprojekt.ControllerTests;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import Bendispository.Abschlussprojekt.service.ConflictService;
import Bendispository.Abschlussprojekt.service.CustomUserDetailsService;
import Bendispository.Abschlussprojekt.service.MyUserPrincipal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest
public class LoginRegistrationTests {

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemRepo itemRepo;

    @MockBean
    PersonsRepo personsRepo;

    @MockBean
    ConflictTransactionRepo conflictTransactionRepo;

    @MockBean
    LeaseTransactionRepo leaseTransactionRepo;

    @MockBean
    PaymentTransactionRepo paymentTransactionRepo;

    @MockBean
    RequestRepo requestRepo;

    @MockBean
    CustomUserDetailsService blabla;

    @MockBean
    MyUserPrincipal blablabla;

    @MockBean
    AuthenticationService authenticationService;

    @MockBean
    ConflictService conflictService;

    @MockBean
    RatingRepo ratingRepo;

    Person dummy1;

    @Before
    public void setUp(){
        dummy1 = new Person();

        dummy1.setFirstName("mandy");
        dummy1.setLastName("moraru");
        dummy1.setCity("kölle");
        dummy1.setEmail("momo@gmail.com");
        dummy1.setUsername("momo");
        dummy1.setPassword("abcd");
        dummy1.setId(1L);

        personsRepo.save(dummy1);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(dummy1);
        Mockito.when(personsRepo.findByUsername("momo")).thenReturn(dummy1);
    }


    @Test
    @WithMockUser(username = "momo", password = "abcd", roles = "USER")
    public void checkRegistration() throws Exception {

        mvc.perform(post("/registration").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("lastName", "soft")
                .param("firstName", "clara")
                .param("username", "clari")
                .param("email", "clari@gmx.de")
                .param("account", "0")
                .param("city", "Düsseldorf")
                .sessionAttr("newPerson", new Person()))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(model().attribute("newPerson", hasProperty("id")))
                .andExpect(model().attribute("newPerson", hasProperty("firstName", equalTo("clara"))))
                .andExpect(model().attribute("newPerson", hasProperty("lastName", equalTo("soft"))))
                .andExpect(model().attribute("newPerson", hasProperty("username", equalTo("clari"))))
                .andExpect(model().attribute("newPerson", hasProperty("email", equalTo("clari@gmx.de"))))
                .andExpect(model().attribute("newPerson", hasProperty("city", equalTo("Düsseldorf"))));

        //Mockito.verify(personsRepo).save(any(Person.class));
    }

    @Test
    @WithMockUser(username = "momo", password = "abcd", roles = "USER")
    public void checkRegistrationfail() throws Exception {

        mvc.perform(post("/registration").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("lastName", "mandy")
                .param("firstName", "clara")
                .param("username", "momo")
                .param("email", "clari@gmx.de")
                .param("account", "0")
                .param("city", "Düsseldorf")
                .param("password", "abcd")
                .sessionAttr("newPerson", new Person()))
                .andExpect(view().name("login"))
                .andExpect(status().isOk());

        //Mockito.verify(personsRepo).save(any(Person.class));
    }
}
