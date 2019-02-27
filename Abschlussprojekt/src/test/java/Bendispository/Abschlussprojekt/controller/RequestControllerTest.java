package Bendispository.Abschlussprojekt.controller;


import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import Bendispository.Abschlussprojekt.service.ConflictService;
import Bendispository.Abschlussprojekt.service.CustomUserDetailsService;
import Bendispository.Abschlussprojekt.service.RequestService;
import Bendispository.Abschlussprojekt.model.Rating;
import Bendispository.Abschlussprojekt.service.*;
import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@WithMockUser(username = "momo", password = "abcdabcd")
@WebMvcTest(controllers = RequestController.class)
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
    RatingRepo ratingRepo;

    @MockBean
    ConflictService conflictService;

    @MockBean
    RequestService requestService;

    @MockBean
    TransactionService transactionService;
    Person dummy1;
    Person dummy2;
    Person dummy3;
    Person dummyAdmin;

    Rating rating1;
    Rating rating2;
    Rating rating3;

    Item dummyItem1;
    Item dummyItem2;
    Item dummyItem3;

    @Before
    public void setUp() {

        dummy1 = new Person();
        dummy2 = new Person();
        dummy3 = new Person();
        dummyAdmin = new Person();
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
        dummy1.setUsername("momo");
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

        dummyAdmin.setId(0L);
        dummyAdmin.setFirstName("random");
        dummyAdmin.setLastName("random");
        dummyAdmin.setUsername("admin");
        dummyAdmin.setPassword("rootroot");
        dummyAdmin.setEmail("admin@gmail.com");

        dummyItem1.setName("stuhl");
        dummyItem1.setDeposit(40);
        dummyItem1.setDescription("bin billig");
        dummyItem1.setCostPerDay(10);
        dummyItem1.setId(3L);
        dummyItem1.setOwner(dummy1);

        dummyItem2.setName("playstation");
        dummyItem2.setDeposit(250);
        dummyItem2.setDescription("bin teuer");
        dummyItem2.setCostPerDay(120);
        dummyItem2.setId(4L);
        dummyItem2.setOwner(dummy1);

        dummyItem3.setName("Kulli");
        dummyItem3.setDeposit(5);
        dummyItem3.setDescription("schicker kulli");
        dummyItem3.setCostPerDay(1);
        dummyItem3.setId(5L);
        dummyItem3.setOwner(dummy2);

        List<Item> items1 = new ArrayList<Item>();
        items1.addAll(Arrays.asList(dummyItem1, dummyItem2));
        dummy1.setItems(items1);
        List<Item> items2 = new ArrayList<Item>();
        items2.addAll(Arrays.asList(dummyItem3));
        dummy2.setItems(items2);

        itemRepo.saveAll(Arrays.asList(dummyItem1, dummyItem2, dummyItem3));
        personsRepo.saveAll(Arrays.asList(dummy1, dummy2, dummy3));
        ratingRepo.saveAll(Arrays.asList(rating1, rating2, rating3));

        List<Request> requests = new ArrayList<>();
        List<LeaseTransaction> leaseTransactions = new ArrayList<>();

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

        Mockito.when(requestRepo.findByRequesterAndStatus(any(), any())).thenReturn(requests);
        Mockito.when(requestRepo.findByRequestedItemOwnerAndStatus(any(), any())).thenReturn(requests);

        Mockito.when(leaseTransactionRepo.findAllByLeaserAndItemIsReturnedIsFalse(dummy1)).thenReturn(leaseTransactions);
        Mockito.when(leaseTransactionRepo.findAllByItemOwnerAndItemIsReturnedIsFalse(dummy1)).thenReturn(leaseTransactions);
        Mockito.when(leaseTransactionRepo.findAllByItemIsReturnedIsTrueAndLeaseIsConcludedIsFalseAndItemOwner(dummy1)).thenReturn(leaseTransactions);
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(dummy1);

    }

    @Test
    public void retrieve() throws Exception {

        mvc.perform(get("/profile/returneditems")).andExpect(status().isOk());
        mvc.perform(get("/profile/requests")).andExpect(status().isOk());
        mvc.perform(get("/profile/renteditems")).andExpect(status().isOk());
    }

    @Test
    public void checkAddItemRequest() throws Exception {

        mvc.perform(get("/item/{id}/requestitem", 5L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("thisItem"))
                .andExpect(model().attributeExists("leases"))
                .andExpect(view().name("rentsTmpl/formRequest"));
    }

    @Test
    public void checkRequestOverview() throws  Exception {
        mvc.perform(get("/profile/requests"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("rentsTmpl/requests"));
    }
}
