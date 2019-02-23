package Bendispository.Abschlussprojekt.ControllerTests;

import Bendispository.Abschlussprojekt.controller.FileController;
import Bendispository.Abschlussprojekt.controller.ProfilController;
import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import Bendispository.Abschlussprojekt.service.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@ContextConfiguration
@WebAppConfiguration
@WebMvcTest(controllers = FileController.class)
public class FileControllerTests {

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
    UserDetails loggedIn;

    Person dummy1;
    Person dummy2;
    Person dummy3;

    Item dummyItem1;
    Item dummyItem2;
    Item dummyItem3;

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
        dummy2.setPassword("abcdabcd");
        dummy2.setId(2L);

        dummy3.setFirstName("clara");
        dummy3.setLastName("maassen");
        dummy3.setCity("viersi");
        dummy3.setEmail("clara@gmail.com");
        dummy3.setUsername("claraaa");
        dummy3.setPassword("abcdabcd");
        dummy3.setId(6L);


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

        //items1.addAll(Arrays.asList(dummyItem1, dummyItem2));
        items1.add(dummyItem1);
        items1.add(dummyItem2);
        dummy1.setItems(items1);

        List<Item> items2 = new ArrayList<Item>();
        //items2.addAll(Arrays.asList(dummyItem3));
        items2.add(dummyItem3);
        dummy2.setItems(items2);

        itemRepo.save(dummyItem1);
        itemRepo.save(dummyItem2);
        itemRepo.save(dummyItem3);
        //itemRepo.saveAll(Arrays.asList(dummyItem1, dummyItem2, dummyItem3));
        personsRepo.saveAll(Arrays.asList(dummy1, dummy2, dummy3));


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
        Mockito.when(personsRepo.findById(6L))
                .thenReturn(Optional.ofNullable(dummy3));


        //loggedIn = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        //Mockito.when(authenticationService.getCurrentUser()).thenReturn(dummy1);
    }

    @After
    public void delete(){
        personsRepo.deleteAll();
        itemRepo.deleteAll();
    }

    @Test
    @WithMockUser(username = "momo", password = "abcdabcd")
    public void checkAddItem() throws Exception {

        mvc.perform(get("/additem").contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name", "lasso")
                .param("description", "komm hol das lasso raus")
                .param("place", "köln")
                .param("deposit", "69")
                .param("costPerDay", "69")
                .param("file", "file.jpg")
                .sessionAttr("newItem", new Item()))
                .andExpect(status().isOk())
                .andExpect(view().name("itemTmpl/AddItem"));/*
                .andExpect(model().attribute("newItem", hasProperty("name", is("lasso"))));
              /*  .andExpect(model().attribute("newItem", hasProperty("name", equalTo("lasso"))))
                .andExpect(model().attribute("newItem", hasProperty("description", equalTo("komm hol das lasso raus"))))
                .andExpect(model().attribute("newItem", hasProperty("place", equalTo("köln"))))
                .andExpect(model().attribute("newItem", hasProperty("deposit", equalTo(69))))
                .andExpect(model().attribute("newItem", hasProperty("costPerDay", equalTo(69))));*/
    }

    @Test
    @WithMockUser(username = "claraaa", password = "abcdabcd")
    public void checkItemProfiles() throws Exception {

        mvc.perform(get("/item/{id}", 3L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemProfile"))
                .andExpect(view().name("itemTmpl/itemProfile"))
                .andExpect(model().attribute("itemProfile", hasProperty("id", equalTo(3L))))
                .andExpect(model().attribute("itemProfile", hasProperty("name", equalTo("stuhl"))))
                .andExpect(model().attribute("itemProfile", hasProperty("deposit", equalTo(40))))
                .andExpect(model().attribute("itemProfile", hasProperty("description", equalTo("bin billig"))))
                .andExpect(model().attribute("itemProfile", hasProperty("costPerDay", equalTo(10))));

        mvc.perform(get("/item/{id}", 4L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemProfile"))
                .andExpect(view().name("itemTmpl/itemProfile"))
                .andExpect(model().attribute("itemProfile", hasProperty("id", equalTo(4L))))
                .andExpect(model().attribute("itemProfile", hasProperty("name", equalTo("playstation"))))
                .andExpect(model().attribute("itemProfile", hasProperty("deposit", equalTo(250))))
                .andExpect(model().attribute("itemProfile", hasProperty("description", equalTo("bin teuer"))))
                .andExpect(model().attribute("itemProfile", hasProperty("costPerDay", equalTo(120))));

        mvc.perform(get("/item/{id}", 5L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemProfile"))
                .andExpect(view().name("itemTmpl/itemProfile"))
                .andExpect(model().attribute("itemProfile", hasProperty("id", equalTo(5L))))
                .andExpect(model().attribute("itemProfile", hasProperty("name", equalTo("Kulli"))))
                .andExpect(model().attribute("itemProfile", hasProperty("deposit", equalTo(5))))
                .andExpect(model().attribute("itemProfile", hasProperty("description", equalTo("schicker kulli"))))
                .andExpect(model().attribute("itemProfile", hasProperty("costPerDay", equalTo(1))));
    }

    @Test
    @WithMockUser(username = "momo", password = "abcdabcd")
    public void ckeckEditItem() throws Exception{
        mvc.perform(get("/edititem/{id}", 3L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("Item"))
                .andExpect(view().name("itemTmpl/editItem"))
                .andExpect(model().attribute("Item", hasProperty("id", equalTo(3L))))
                .andExpect(model().attribute("Item", hasProperty("name", equalTo("stuhl"))))
                .andExpect(model().attribute("Item", hasProperty("deposit", equalTo(40))))
                .andExpect(model().attribute("Item", hasProperty("description", equalTo("bin billig"))))
                .andExpect(model().attribute("Item", hasProperty("costPerDay", equalTo(10))));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void checkNONExistingItemProfile() throws Exception {
        mvc.perform(get("/item/{id}", 8L))
                .andDo(print())
                .andExpect(view().name("redirect:/"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

}
