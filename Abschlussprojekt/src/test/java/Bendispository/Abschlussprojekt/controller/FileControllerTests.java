package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.transactionModels.MarketType;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
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
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    Item dummyItem4;

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
        dummyItem4 = new Item();

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
        dummyItem1.setOwner(dummy1);
        dummyItem1.setMarketType(MarketType.LEND);

        dummyItem2.setName("playstation");
        dummyItem2.setDeposit(250);
        dummyItem2.setDescription("bin teuer");
        dummyItem2.setCostPerDay(120);
        dummyItem2.setId(4L);
        dummyItem2.setOwner(dummy2);
        dummyItem2.setMarketType(MarketType.LEND);

        dummyItem3.setName("Kulli");
        dummyItem3.setDeposit(5);
        dummyItem3.setDescription("schicker kulli");
        dummyItem3.setCostPerDay(1);
        dummyItem3.setId(5L);
        dummyItem3.setOwner(dummy3);
        dummyItem3.setMarketType(MarketType.LEND);

        dummyItem4.setName("Bildschirm");
        dummyItem4.setDescription("Full HD");
        dummyItem4.setRetailPrice(50);
        dummyItem4.setId(7L);
        dummyItem4.setOwner(dummy3);
        dummyItem4.setMarketType(MarketType.SELL);

        List<Item> items1 = new ArrayList<Item>();

        items1.addAll(Arrays.asList(dummyItem1, dummyItem2));
        dummy1.setItems(items1);

        List<Item> items2 = new ArrayList<Item>();
        items2.addAll(Arrays.asList(dummyItem3, dummyItem4));
        dummy2.setItems(items2);

        itemRepo.save(dummyItem1);
        itemRepo.save(dummyItem2);
        itemRepo.save(dummyItem3);
        itemRepo.save(dummyItem4);
        itemRepo.saveAll(Arrays.asList(dummyItem1, dummyItem2, dummyItem3, dummyItem4));
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

        Mockito.when(itemRepo.findById(7L))
                .thenReturn(Optional.ofNullable(dummyItem4));

        Mockito.when(personsRepo.findById(6L))
                .thenReturn(Optional.ofNullable(dummy3));

        //loggedIn = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Mockito.when(authenticationService.getCurrentUser()).thenReturn(dummy1);
    }

    @After
    public void delete(){
        personsRepo.deleteAll();
        itemRepo.deleteAll();
    }

    @Test
    @WithMockUser(username = "momo", password = "abcdabcd")
    public void retrieve() throws Exception{
        mvc.perform(get("/additem")).andExpect(status().isOk());
        mvc.perform(get("/addsellitem")).andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "momo", password = "abcdabcd")
    public void checkAddItem() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());

        mvc.perform(multipart("/additem")
                .file("file", new byte[0])
                .param("name", "lasso")
                .param("description", "komm hol das lasso raus")
                .param("place", "köln")
                .param("deposit", "69")
                .param("costPerDay", "69")
                .sessionAttr("newItem", new Item()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/item/null"));
    }

    @Test
    @WithMockUser(username = "momo", password = "abcdabcd")
    public void checkAddSellItem() throws Exception {

        MockMultipartFile file = new MockMultipartFile("file", "orig", null, "bar".getBytes());

        mvc.perform(multipart("/addsellitem")
                .file("file", new byte[0])
                .param("name", "lasso")
                .param("description", "komm hol das lasso raus")
                .param("place", "köln")
                .param("retailPrice", "69")
                .sessionAttr("newItem", new Item()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/item/null"));
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

        mvc.perform(get("/item/{id}", 7L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("itemProfile"))
                .andExpect(view().name("itemTmpl/itemProfileSell"))
                .andExpect(model().attribute("itemProfile", hasProperty("id", equalTo(7L))))
                .andExpect(model().attribute("itemProfile", hasProperty("name", equalTo("Bildschirm"))))
                .andExpect(model().attribute("itemProfile", hasProperty("retailPrice", equalTo(50))))
                .andExpect(model().attribute("itemProfile", hasProperty("description", equalTo("Full HD"))));

    }

    @Test
    @WithMockUser(roles = "USER")
    public void checkNONExistingItemProfile() throws Exception {
        mvc.perform(get("/item/{id}", 8L))
                .andDo(print())
                .andExpect(view().name("redirect:/"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "momo", password = "abcdabcd")
    public void checkDeleteItem() throws Exception {

        mvc.perform(get("/deleteitem/{id}", 3L))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"))
                .andExpect(flash().attribute("message", "Item has been deleted!"));
    }

    @Test
    @WithMockUser(username = "momo", password = "abcdabcd")
    public void checkEditItem() throws Exception{

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
    @WithMockUser(username = "momo", password = "abcdabcd")
    public void checkEditItemNotExisting() throws Exception{

        mvc.perform(get("/edititem/{id}", 10L))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }
}
