package Bendispository.Abschlussprojekt;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConcludeTransactionRepo;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    ConcludeTransactionRepo concludetransRepo;

    @MockBean
    ConflictTransactionRepo conflictTransactionRepo;

    @MockBean
    LeaseTransactionRepo leaseTransactionRepo;

    @MockBean
    PaymentTransactionRepo paymentTransactionRepo;

    @MockBean
    RequestRepo requestRepo;

    @Before


    @Test
    public void retrieve() throws Exception {

        Person dummy1 = new Person();
        dummy1.setFirstName("mandy");
        dummy1.setLastName("moraru");
        dummy1.setAccount(100);
        dummy1.setCity("kölle");
        dummy1.setEmail("momo@gmail.com");
        dummy1.setUsername("momo");
        dummy1.setId(1L);

        Item dummyitem = new Item();
        dummyitem.setName("stuhl");
        dummyitem.setAvailable(true);
        dummyitem.setDeposit(20);
        dummyitem.setDescription("item");
        dummyitem.setCostPerDay(10);
        dummyitem.setId(2L);

        List<Item> item = new ArrayList<Item>();
        item.add(dummyitem);
        dummy1.setItems(item);

        itemRepo.save(dummyitem);
        personsRepo.save(dummy1);

        Mockito.when(personsRepo.findById(1L))
                .thenReturn(Optional.ofNullable(dummy1));
        Mockito.when(itemRepo.findAll())
                .thenReturn(Arrays.asList(dummyitem));
        Mockito.when(itemRepo.findById(2L))
                .thenReturn(Optional.ofNullable(dummyitem));

        mvc.perform(get("/")).andExpect(status().isOk());
        mvc.perform(get("/profilub")).andExpect(status().isOk());
        mvc.perform(get("/profile/{id}", 1L)).andExpect(status().isOk());
        mvc.perform(get("/Item/{id}", 2L)).andExpect(status().isOk());
        mvc.perform(get("/addItem")).andExpect(status().isOk());
        mvc.perform(get("/registration")).andExpect(status().isOk());
    }

    @Test
    public void Overview() throws Exception {

        Item dummyitem = new Item();
        dummyitem.setName("stuhl");
        dummyitem.setAvailable(true);
        dummyitem.setDeposit(20);
        dummyitem.setDescription("item");
        dummyitem.setCostPerDay(10);
        dummyitem.setId(2L);

        mvc.perform(get("/")).andExpect(model().attributeExists("OverviewAllItems"))
                .andExpect(view().name("overviewAllItems"))
                .andExpect(model().attribute("OverviewAllItems", hasItems(
                        allOf(
                                hasProperty("id", equalTo(dummyitem.getId())),
                                hasProperty("vorname", equalTo("peter")),
                                hasProperty("nachname", equalTo("Lauch")),
                                hasProperty("kontaktdaten", equalTo("lauchi@gmx.de")),
                                hasProperty("skills", arrayContainingInAnyOrder("PHP", "JAVA"))),
                                hasProperty("jahreslohn", notNullValue()))));

    }

    @Test
    public void checkRegistration() throws Exception {

    }

    @Test
    public void UserProfile() throws Exception {

    }

    @Test
    public void usersUebersicht() throws Exception {

    }

    @Test
    public void addItem() throws Exception {

    }

    @Test
    public void ItemProfile() throws Exception {

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


