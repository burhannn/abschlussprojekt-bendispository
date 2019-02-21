package Bendispository.Abschlussprojekt.ControllerTests;

import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.service.*;
import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.junit.Before;
import org.junit.Ignore;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest
@WithMockUser(username = "momo", password = "abcdabcd")
public class ProfilControllerTests {

    @Autowired
    private WebApplicationContext wac;

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
    AuthenticationService authenticationService;

    @MockBean
    MyUserPrincipal blablabla;

    @MockBean
    RatingRepo ratingRepo;

    @MockBean
    ConflictService conflictService;

    @MockBean
    RequestService requestService;

    @MockBean
    ItemService itemService;

    Person dummy1;
    Person dummy2;

    Item dummyItem1;
    Item dummyItem2;
    Item dummyItem3;

    @Before
    public void setUp(){
        dummy1 = new Person();
        dummy2 = new Person();
        dummyItem1 = new Item();
        dummyItem2 = new Item();
        dummyItem3 = new Item();

        dummy1.setFirstName("mandy");
        dummy1.setLastName("moraru");
        dummy1.setCity("kölle");
        dummy1.setEmail("momo@gmail.com");
        dummy1.setUsername("momo");
        dummy1.setPassword("abcdabcd");
        dummy1.setId(1L);

        dummy2.setFirstName("nina");
        dummy2.setLastName("fischi");
        dummy2.setCity("düssi");
        dummy2.setEmail("nini@gmail.com");
        dummy2.setUsername("nini");
        dummy1.setPassword("abcdabcd");
        dummy2.setId(2L);


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
        personsRepo.saveAll(Arrays.asList(dummy1, dummy2));


        Mockito.when(personsRepo.findById(1L))
                .thenReturn(Optional.ofNullable(dummy1));
        Mockito.when(personsRepo.findById(2L))
                .thenReturn(Optional.ofNullable(dummy2));
        Mockito.when(itemRepo.findById(3L))
                .thenReturn(Optional.ofNullable(dummyItem1));
        Mockito.when(itemRepo.findById(4L))
                .thenReturn(Optional.ofNullable(dummyItem2));
        Mockito.when(itemRepo.findById(5L))
                .thenReturn(Optional.ofNullable(dummyItem3));

        Mockito.when(authenticationService.getCurrentUser()).thenReturn(dummy1);
    }


    @Test
    @Ignore
    public void retrieve() throws Exception {

        mvc.perform(get("/profilub")).andExpect(status().isOk());
        mvc.perform(get("/profile/{id}", 1L)).andExpect(status().isOk());
        mvc.perform(get("/Item/{id}", 3L)).andExpect(status().isOk());
        mvc.perform(get("/addItem")).andExpect(status().isOk());
        mvc.perform(get("/registration")).andExpect(status().isOk());
    }

    @Test
    @Ignore
    public void checkOverviewItems() throws Exception {

        Mockito.when(itemRepo.findAll())
                .thenReturn(Arrays.asList(dummyItem1, dummyItem2, dummyItem3));

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("OverviewAllItems"))
                .andExpect(model().attributeExists("loggedInPerson"))
                .andExpect(view().name("overviewAllItems"))
                //.andExpect(model().attribute("OverviewAllItems", hasSize(3)))  (änderung im controller --> nur items von anderen leuten sind sichtbar)
                .andExpect(model().attribute("OverviewAllItems", hasItem(
                        allOf(
                                hasProperty("id", equalTo(3L)),
                                hasProperty("name", equalTo("stuhl")),
                                hasProperty("description", equalTo("bin billig")))
                )))
                .andExpect(model().attribute("OverviewAllItems", hasItem(
                        allOf(
                                hasProperty("id", equalTo(4L)),
                                hasProperty("name", equalTo("playstation")),
                                hasProperty("description", equalTo("bin teuer")))
                )))
                .andExpect(model().attribute("OverviewAllItems", hasItem(
                        allOf(
                                hasProperty("id", equalTo(5L)),
                                hasProperty("name", equalTo("Kulli")),
                                hasProperty("description", equalTo("schicker kulli"))
                        )
                )));
    }

    //tests für profile anderer User
    @Test
    @Ignore
    public void checkMyProfile() throws Exception {

        Mockito.when(authenticationService.getCurrentUser()).thenReturn(dummy1);
        mvc.perform(get("/profile"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("person"))
                .andExpect(model().attributeExists("account"))
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("person", hasProperty("id", equalTo(1L))))
                .andExpect(model().attribute( "person", hasProperty("firstName", equalTo("mandy"))))
                .andExpect(model().attribute("person", hasProperty("lastName", equalTo("moraru"))))
                .andExpect(model().attribute("person", hasProperty("username", equalTo("momo"))))
                .andExpect(model().attribute("person", hasProperty("email", equalTo("momo@gmail.com"))))
                .andExpect(model().attribute("person", hasProperty("city", equalTo("kölle"))))
                .andExpect(model().attribute("person", hasProperty("items", containsInAnyOrder(dummyItem1, dummyItem2))));
    }

    @Test
    @Ignore
    public void checkExistingUserProfilOther() throws Exception {

        mvc.perform(get("/profile/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("person"))
                .andExpect(view().name("profileOther"))
                .andExpect(model().attribute("person", hasProperty("id", equalTo(1L))))
                .andExpect(model().attribute("person", hasProperty("username", equalTo("momo"))))
                .andExpect(model().attribute("person", hasProperty("email", equalTo("momo@gmail.com"))))
                .andExpect(model().attribute("person", hasProperty("city", equalTo("kölle"))))
                .andExpect(model().attribute("person", hasProperty("items", containsInAnyOrder(dummyItem2, dummyItem1))));

        mvc.perform(get("/profile/{id}", 2L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("person"))
                .andExpect(view().name("profileOther"))
                .andExpect(model().attribute("person", hasProperty("id", equalTo(2L))))
                .andExpect(model().attribute("person", hasProperty("username", equalTo("nini"))))
                .andExpect(model().attribute("person", hasProperty("email", equalTo("nini@gmail.com"))))
                .andExpect(model().attribute("person", hasProperty("city", equalTo("düssi"))))
                .andExpect(model().attribute("person", hasProperty("items", containsInAnyOrder(dummyItem3))));
    }

    @Test
    @Ignore
    public void checkNONExistingUserProfilOther() throws Exception {
        mvc.perform(get("/profile/{id}", 4L))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    @Ignore
    public void checkUsersUebersicht() throws Exception {

        Mockito.when(personsRepo.findAll())
                .thenReturn(Arrays.asList(dummy1, dummy2));

        mvc.perform(get("/profilub"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("personen"))
                .andExpect(view().name("profileDetails"))
                .andExpect(model().attribute("personen", hasSize(2)))
                .andExpect(model().attribute("personen", hasItem(
                        allOf(
                                hasProperty("id", equalTo(1L)),
                                hasProperty("firstName", equalTo("mandy")),
                                hasProperty("lastName", equalTo("moraru")),
                                hasProperty("city", equalTo("kölle")),
                                hasProperty("email", equalTo("momo@gmail.com")),
                                hasProperty("username", equalTo("momo")))
                )))
                .andExpect(model().attribute("personen", hasItem(
                        allOf(
                                hasProperty("id", equalTo(2L)),
                                hasProperty("firstName", equalTo("nina")),
                                hasProperty("lastName", equalTo("fischi")),
                                hasProperty("city", equalTo("düssi")),
                                hasProperty("email", equalTo("nini@gmail.com")),
                                hasProperty("username", equalTo("nini")))
                )));

    }

    @Test
    @Ignore
    public void checkAddItem() throws Exception {

        mvc.perform(post("/addItem").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "lasso")
                .param("description", "komm hol das lasso raus")
                .param("deposit", "69")
                .param("costPerDay", "69")
                .sessionAttr("newItem", new Item()))
                .andExpect(status().isOk())
                .andExpect(view().name("AddItem"))
                .andExpect(model().attribute("newItem", hasProperty("id")))
                .andExpect(model().attribute("newItem", hasProperty("name", equalTo("lasso"))))
                .andExpect(model().attribute("newItem", hasProperty("description", equalTo("komm hol das lasso raus"))))
                .andExpect(model().attribute("newItem", hasProperty("deposit", equalTo(69))))
                .andExpect(model().attribute("newItem", hasProperty("costPerDay", equalTo(69))));
    }

    @Test
        public void checkItemProfiles() throws Exception {

        mvc.perform(get("/Item/{id}", 3L).with(user("momo").password("abcdabcd")))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemProfile"))
                .andExpect(view().name("itemProfile"))
                .andExpect(model().attribute("itemProfile", hasProperty("id", equalTo(3L))))
                .andExpect(model().attribute("itemProfile", hasProperty("name", equalTo("stuhl"))))
                .andExpect(model().attribute("itemProfile", hasProperty("deposit", equalTo(40))))
                .andExpect(model().attribute("itemProfile", hasProperty("description", equalTo("bin billig"))))
                .andExpect(model().attribute("itemProfile", hasProperty("costPerDay", equalTo(10))));

        mvc.perform(get("/Item/{id}", 4L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemProfile"))
                .andExpect(view().name("itemProfile"))
                .andExpect(model().attribute("itemProfile", hasProperty("id", equalTo(4L))))
                .andExpect(model().attribute("itemProfile", hasProperty("name", equalTo("playstation"))))
                .andExpect(model().attribute("itemProfile", hasProperty("deposit", equalTo(250))))
                .andExpect(model().attribute("itemProfile", hasProperty("description", equalTo("bin teuer"))))
                .andExpect(model().attribute("itemProfile", hasProperty("costPerDay", equalTo(120))));

        mvc.perform(get("/Item/{id}", 5L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemProfile"))
                .andExpect(view().name("itemProfile"))
                .andExpect(model().attribute("itemProfile", hasProperty("id", equalTo(5L))))
                .andExpect(model().attribute("itemProfile", hasProperty("name", equalTo("Kulli"))))
                .andExpect(model().attribute("itemProfile", hasProperty("deposit", equalTo(5))))
                .andExpect(model().attribute("itemProfile", hasProperty("description", equalTo("schicker kulli"))))
                .andExpect(model().attribute("itemProfile", hasProperty("costPerDay", equalTo(1))));
    }

    @Test
    @Ignore
    public void checkNONExistingItemProfile() throws Exception {
        mvc.perform(get("/item/{id}", 8L))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

}


