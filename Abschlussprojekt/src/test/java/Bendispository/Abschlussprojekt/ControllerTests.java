package Bendispository.Abschlussprojekt;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest
public class ControllerTests {

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
        dummy1.setBankaccount(100);
        dummy1.setCity("kölle");
        dummy1.setEmail("momo@gmail.com");
        dummy1.setUsername("momo");
        dummy1.setId(1L);

        dummy2.setFirstName("nina");
        dummy2.setLastName("fischi");
        dummy2.setBankaccount(200);
        dummy2.setCity("düssi");
        dummy2.setEmail("nini@gmail.com");
        dummy2.setUsername("nini");
        dummy2.setId(2L);


        dummyItem1.setName("stuhl");
        dummyItem1.setAvailable(true);
        dummyItem1.setDeposit(40);
        dummyItem1.setDescription("bin billig");
        dummyItem1.setCostPerDay(10);
        dummyItem1.setId(3L);

        dummyItem2.setName("playstation");
        dummyItem2.setAvailable(true);
        dummyItem2.setDeposit(250);
        dummyItem2.setDescription("bin teuer");
        dummyItem2.setCostPerDay(120);
        dummyItem2.setId(4L);

        dummyItem3.setName("Kulli");
        dummyItem3.setAvailable(true);
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
    }


    @Test
    public void retrieve() throws Exception {

        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/profilub")).andExpect(status().isOk());
        mvc.perform(get("/profile/{id}", 1L)).andExpect(status().isOk());
        mvc.perform(get("/Item/{id}", 3L)).andExpect(status().isOk());
        mvc.perform(get("/addItem")).andExpect(status().isOk());
        mvc.perform(get("/registration")).andExpect(status().isOk());
    }

    @Test
    public void Overview() throws Exception {

        Mockito.when(itemRepo.findAll())
                .thenReturn(Arrays.asList(dummyItem1, dummyItem2, dummyItem3));

        mvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("OverviewAllItems"))
                .andExpect(view().name("overviewAllItems"))
                .andExpect(model().attribute("OverviewAllItems", hasSize(3)))
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

    @Test
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
                .andExpect(view().name("registration"))
                .andExpect(model().attribute("newPerson", hasProperty("id")))
                .andExpect(model().attribute("newPerson", hasProperty("firstName", equalTo("clara"))))
                .andExpect(model().attribute("newPerson", hasProperty("lastName", equalTo("soft"))))
                .andExpect(model().attribute("newPerson", hasProperty("username", equalTo("clari"))))
                .andExpect(model().attribute("newPerson", hasProperty("email", equalTo("clari@gmx.de"))))
                .andExpect(model().attribute("newPerson", hasProperty("city", equalTo("Düsseldorf"))))
                .andExpect(model().attribute("newPerson", hasProperty("account", equalTo(0))));

    }

    @Test
    public void UserProfile() throws Exception {

        mvc.perform(get("/profile/{id}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("person"))
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("person", hasProperty("id", equalTo(1L))))
                .andExpect(model().attribute( "person", hasProperty("firstName", equalTo("mandy"))))
                .andExpect(model().attribute("person", hasProperty("lastName", equalTo("moraru"))))
                .andExpect(model().attribute("person", hasProperty("username", equalTo("momo"))))
                .andExpect(model().attribute("person", hasProperty("email", equalTo("momo@gmail.com"))))
                .andExpect(model().attribute("person", hasProperty("account", equalTo(100))))
                .andExpect(model().attribute("person", hasProperty("city", equalTo("kölle"))));
                //.andExpect(model().attribute("person", hasProperty("items", )));

        mvc.perform(get("/profile/{id}", 2L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("person"))
                .andExpect(view().name("profile"))
                .andExpect(model().attribute("person", hasProperty("id", equalTo(2L))))
                .andExpect(model().attribute( "person", hasProperty("firstName", equalTo("nina"))))
                .andExpect(model().attribute("person", hasProperty("lastName", equalTo("fischi"))))
                .andExpect(model().attribute("person", hasProperty("username", equalTo("nini"))))
                .andExpect(model().attribute("person", hasProperty("email", equalTo("nini@gmail.com"))))
                .andExpect(model().attribute("person", hasProperty("account", equalTo(200))))
                .andExpect(model().attribute("person", hasProperty("city", equalTo("düssi"))));
                //.andExpect(model().attribute("person", hasProperty("items", )));
    }

    @Test
    public void usersUebersicht() throws Exception {

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
                                hasProperty("account", equalTo(100)),
                                hasProperty("city", equalTo("kölle")),
                                hasProperty("email", equalTo("momo@gmail.com")),
                                hasProperty("username", equalTo("momo")))
                )))
                .andExpect(model().attribute("personen", hasItem(
                        allOf(
                                hasProperty("id", equalTo(2L)),
                                hasProperty("firstName", equalTo("nina")),
                                hasProperty("lastName", equalTo("fischi")),
                                hasProperty("account", equalTo(200)),
                                hasProperty("city", equalTo("düssi")),
                                hasProperty("email", equalTo("nini@gmail.com")),
                                hasProperty("username", equalTo("nini")))
                )));

    }

    @Test
    public void addItem() throws Exception {

        mvc.perform(post("/registration").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("lastName", "soft")
                .param("firstName", "clara")
                .param("username", "clari")
                .param("email", "clari@gmx.de")
                .param("account", "0")
                .param("city", "Düsseldorf")
                .sessionAttr("newPerson", new Person()))
                .andExpect(status().isOk())
                .andExpect(view().name("registration"))
                .andExpect(model().attribute("newPerson", hasProperty("id")))
                .andExpect(model().attribute("newPerson", hasProperty("firstName", equalTo("clara"))))
                .andExpect(model().attribute("newPerson", hasProperty("lastName", equalTo("soft"))))
                .andExpect(model().attribute("newPerson", hasProperty("username", equalTo("clari"))))
                .andExpect(model().attribute("newPerson", hasProperty("email", equalTo("clari@gmx.de"))))
                .andExpect(model().attribute("newPerson", hasProperty("city", equalTo("Düsseldorf"))))
                .andExpect(model().attribute("newPerson", hasProperty("account", equalTo(0))));

    }


    @Test
    public void ItemProfile() throws Exception {

        mvc.perform(get("/Item/{id}", 3L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemProfile"))
                .andExpect(view().name("itemProfile"))
                .andExpect(model().attribute("itemProfile", hasProperty("id", equalTo(3L))))
                .andExpect(model().attribute("itemProfile", hasProperty("name", equalTo("stuhl"))))
                .andExpect(model().attribute("itemProfile", hasProperty("available", equalTo(true))))
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
                .andExpect(model().attribute("itemProfile", hasProperty("available", equalTo(true))))
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
                .andExpect(model().attribute("itemProfile", hasProperty("available", equalTo(true))))
                .andExpect(model().attribute("itemProfile", hasProperty("deposit", equalTo(5))))
                .andExpect(model().attribute("itemProfile", hasProperty("description", equalTo("schicker kulli"))))
                .andExpect(model().attribute("itemProfile", hasProperty("costPerDay", equalTo(1))));
    }

    @Test
    public void requestsFromUser() throws Exception{

    }

    @Test
    public void rentedItemsFromUser() throws Exception{

    }

    @Test
    public void makeRequestForItem() throws Exception {

    }

}


