package Bendispository.Abschlussprojekt.ControllerTests;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import Bendispository.Abschlussprojekt.service.CustomUserDetailsService;
import Bendispository.Abschlussprojekt.service.MyUserPrincipal;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WebMvcTest
@WithMockUser(username = "momo", password = "abcd")
public class RequestControllerTest {


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


    Person dummy1;
    Person dummy2;

    Item dummyItem1;
    Item dummyItem2;
    Item dummyItem3;

    @Before
    public void setUp() {

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
        dummy1.setPassword("abcd");
        dummy1.setId(1L);

        dummy2.setFirstName("nina");
        dummy2.setLastName("fischi");
        dummy2.setCity("düssi");
        dummy2.setEmail("nini@gmail.com");
        dummy2.setUsername("nini");
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
        mvc.perform(get("/item3/requestItem")).andExpect(status().isOk());
        mvc.perform(get("/profile/requests", 1L)).andExpect(status().isOk());
        mvc.perform(get("/profile/rentedItems", 3L)).andExpect(status().isOk());
    }

    @Test
    public void checkAddItemRequest() throws Exception {

        mvc.perform(get("/item{id}/requestItem", 3L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("thisItem"))
                .andExpect(view().name("formRequest"));

        /*

         */

    }

    @Test
    @Ignore
    public void checkRequestOverview() throws  Exception {
        mvc.perform(get("/profile/requests", 3L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("requests"));
        /*

         */
    }
} 
