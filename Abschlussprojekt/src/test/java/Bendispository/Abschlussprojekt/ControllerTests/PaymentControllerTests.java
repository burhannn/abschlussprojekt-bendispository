package Bendispository.Abschlussprojekt.ControllerTests;

import Bendispository.Abschlussprojekt.service.*;
import Bendispository.Abschlussprojekt.controller.PaymentController;
import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Rating;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration
@WebAppConfiguration
@WebMvcTest(controllers = PaymentController.class)
@WithMockUser(username = "user", password = "abcdabcd", roles = "USER")
public class PaymentControllerTests {

    @Autowired
    private WebApplicationContext context;
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
    CustomUserDetailsService customUserDetailsService;
    @MockBean
    AuthenticationService authenticationService;
    @MockBean
    RatingRepo ratingRepo;
    @MockBean
    ConflictService conflictService;
    @MockBean
    RequestService requestService;
    @MockBean
    ItemService itemService;
    @MockBean
    ProPaySubscriber proPaySubscriber;


    Person dummy1;
    Person dummy2;
    Person dummy3;

    Item dummyItem1;
    Item dummyItem2;
    Item dummyItem3;

    Rating rating1;
    Rating rating2;
    Rating rating3;

    @Before
    public void setUp(){
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        dummy1 = new Person();
        dummy2 = new Person();
        dummy3 = new Person();
        dummyItem1 = new Item();
        dummyItem2 = new Item();
        dummyItem3 = new Item();
        rating1 = new Rating();
        rating2 = new Rating();
        rating3 = new Rating();

        rating1.setRatingPoints(5);
        rating1.setRater(dummy1);
        rating2.setRatingPoints(3);
        rating2.setRater(dummy2);
        rating3.setRatingPoints(1);
        rating3.setRater(dummy3);

        dummy1.setFirstName("mandy");
        dummy1.setLastName("moraru");
        dummy1.setCity("kölle");
        dummy1.setEmail("momo@gmail.com");
        dummy1.setUsername("user");
        dummy1.setPassword("abcdabcd");
        dummy1.setId(1L);
        dummy1.setRatings(Arrays.asList(rating2));

        dummy2.setFirstName("nina");
        dummy2.setLastName("fischi");
        dummy2.setCity("düssi");
        dummy2.setEmail("nini@gmail.com");
        dummy2.setUsername("nini");
        dummy2.setPassword("abcdabcd");
        dummy2.setId(2L);
        dummy2.setRatings(Arrays.asList(rating1));

        dummy3.setFirstName("clara");
        dummy3.setLastName("maassen");
        dummy3.setCity("viersi");
        dummy3.setEmail("clara@gmail.com");
        dummy3.setUsername("claraaa");
        dummy3.setPassword("abcdabcd");
        dummy3.setId(6L);
        dummy3.setRatings(Arrays.asList(rating3));

        dummyItem1.setName("stuhl");
        dummyItem1.setDeposit(40);
        dummyItem1.setDescription("bin billig");
        dummyItem1.setCostPerDay(10);
        dummyItem1.setId(3L);

        dummyItem2.setName("playstation");
        dummyItem2.setDeposit(250);
        dummyItem2.setDescription("bin teuer");
        dummyItem2.setCostPerDay(120);
        dummyItem2.setId(4L);

        dummyItem3.setName("Kulli");
        dummyItem3.setDeposit(5);
        dummyItem3.setDescription("schicker kulli");
        dummyItem3.setCostPerDay(1);
        dummyItem3.setId(5L);

        List<Item> items1 = new ArrayList<Item>();
        items1.addAll(Arrays.asList(dummyItem1, dummyItem2));
        dummy1.setItems(items1);
        List<Item> items2 = new ArrayList<Item>();
        items2.addAll(Arrays.asList(dummyItem3));
        dummy2.setItems(items2);

        itemRepo.saveAll(Arrays.asList(dummyItem1, dummyItem2, dummyItem3));
        personsRepo.saveAll(Arrays.asList(dummy1, dummy2, dummy3));
        ratingRepo.saveAll(Arrays.asList(rating1, rating2, rating3));


        Mockito.when(personsRepo.findById(1L)).thenReturn(Optional.ofNullable(dummy1));
        Mockito.when(personsRepo.findById(2L)).thenReturn(Optional.ofNullable(dummy2));
        Mockito.when(itemRepo.findById(3L)).thenReturn(Optional.ofNullable(dummyItem1));
        Mockito.when(itemRepo.findById(4L)).thenReturn(Optional.ofNullable(dummyItem2));
        Mockito.when(itemRepo.findById(5L)).thenReturn(Optional.ofNullable(dummyItem3));
        Mockito.when(personsRepo.findById(6L)).thenReturn(Optional.ofNullable(dummy3));
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(dummy1);
        Mockito.when(personsRepo.findByUsername("user")).thenReturn(dummy1);
    }

    @After
    public void delete(){
        personsRepo.deleteAll();
        itemRepo.deleteAll();
    }

    @Test
    public void retrieve() throws Exception{
        mvc.perform(get("/chargeaccount")).andExpect(status().isOk());
    }

    @Test
    public void checkSaveAccount() throws Exception{
        mvc.perform(get("/chargeaccount"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("person"))
                .andExpect(view().name("rentsTmpl/chargeAccount"))
                .andExpect(model().attribute("person", hasProperty("id", equalTo(1L))))
                .andExpect(model().attribute( "person", hasProperty("firstName", equalTo("mandy"))))
                .andExpect(model().attribute("person", hasProperty("lastName", equalTo("moraru"))))
                .andExpect(model().attribute("person", hasProperty("username", equalTo("user"))))
                .andExpect(model().attribute("person", hasProperty("email", equalTo("momo@gmail.com"))))
                .andExpect(model().attribute("person", hasProperty("city", equalTo("kölle"))))
                .andExpect(model().attribute("person", hasProperty("items", containsInAnyOrder(dummyItem1, dummyItem2))));

        mvc.perform(get("/chargeaccount"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("rentsTmpl/chargeAccount"));
    }

    @Test
    public void checkChargeAccount() throws Exception{
        mvc.perform(post("/chargeaccount").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("amount", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("rentsTmpl/chargeAccount"))
                .andExpect(model().attributeExists("success"))
                .andExpect(model().attributeExists("person"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attribute("success", "Account has been charged!"));
    }

    @Test
    public void checkChargeAccountNegativeInput() throws Exception{
        mvc.perform(post("/chargeaccount").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("amount", "-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/chargeaccount"))
                .andExpect(flash().attribute("message", "Amount can't be negative!"));
    }




}
