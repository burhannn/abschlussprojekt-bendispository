package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.transactionModels.*;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
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

import static org.mockito.Mockito.doReturn;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@ContextConfiguration
@WebAppConfiguration
@WebMvcTest(controllers = ConflictController.class)
public class ConflictControllerTests {

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
	@MockBean
	ProPaySubscriber proPaySubscriber;

	Request request;
	LeaseTransaction leaseTransaction;
	ConflictTransaction conflictTransaction;
	PaymentTransaction paymentTransaction;
	Person dummy1;
	Person dummy2;
	Person admin;
	Item dummyItem1;
	ProPayAccount proPayAccount1;
	ProPayAccount proPayAccount2;

	@Autowired
	private WebApplicationContext context;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(context)
				.apply(springSecurity())
				.build();

		dummy1 = new Person();
		dummy2 = new Person();
		admin = new Person();
		dummyItem1 = new Item();
		proPayAccount1 = new ProPayAccount();
		proPayAccount2 = new ProPayAccount();

		dummy1.setFirstName("mandy");
		dummy1.setLastName("moraru");
		dummy1.setCity("kölle");
		dummy1.setEmail("momo@gmail.com");
		dummy1.setUsername("momo");
		dummy1.setPassword("abcdabcd");
		dummy1.setId(1L);
		proPayAccount1.setAccount("momo");
		proPayAccount1.setAmount(100);

		dummy2.setFirstName("nina");
		dummy2.setLastName("fischi");
		dummy2.setCity("düssi");
		dummy2.setEmail("nini@gmail.com");
		dummy2.setUsername("nini");
		dummy2.setPassword("abcdabcd");
		dummy2.setId(2L);
		proPayAccount2.setAccount("nini");
		proPayAccount2.setAmount(100);

		dummyItem1.setName("stuhl");
		dummyItem1.setDeposit(40);
		dummyItem1.setDescription("bin billig");
		dummyItem1.setCostPerDay(10);
		dummyItem1.setId(3L);
		dummyItem1.setOwner(dummy1);

		admin.setFirstName("admin");
		admin.setLastName("admin");
		admin.setCity("admin");
		admin.setEmail("admin@gmail.com");
		admin.setUsername("admin");
		admin.setPassword("rootroot");
		admin.setId(4L);

		List<Item> items1 = new ArrayList<Item>();
		items1.addAll(Arrays.asList(dummyItem1));
		dummy1.setItems(items1);

		itemRepo.saveAll(Arrays.asList(dummyItem1));
		personsRepo.saveAll(Arrays.asList(dummy1, dummy2, admin));

		request = new Request();
		request.setId(5L);
		request.setRequestedItem(dummyItem1);
		request.setRequester(dummy2);
		requestRepo.save(request);

		leaseTransaction = new LeaseTransaction();
		leaseTransaction.setRequestId(request.getId());
		leaseTransaction.setLeaser(dummy2);
		leaseTransaction.setItem(dummyItem1);
		leaseTransaction.setDepositId(7);

		paymentTransaction = new PaymentTransaction();
		paymentTransaction.setLeaseTransaction(leaseTransaction);
		paymentTransaction.setType(PaymentType.DEPOSIT);
		paymentTransactionRepo.save(paymentTransaction);
		leaseTransaction.addPaymentTransaction(paymentTransaction);

		conflictTransaction = new ConflictTransaction();
		conflictTransaction.setId(6L);
		leaseTransaction.setConflictTransaction(conflictTransaction);
		conflictTransaction.setLeaseTransaction(leaseTransaction);
		conflictTransaction.setCommentary("Ich bin sauer!");
		leaseTransactionRepo.save(leaseTransaction);
		conflictTransactionRepo.save(conflictTransaction);

		dummy1.setLeaseTransactions(Arrays.asList(leaseTransaction));
		dummy2.setLeaseTransactions(Arrays.asList(leaseTransaction));

		Mockito.when(personsRepo.findById(1L)).thenReturn(Optional.ofNullable(dummy1));
		Mockito.when(personsRepo.findById(2L)).thenReturn(Optional.ofNullable(dummy2));
		Mockito.when(itemRepo.findById(3L)).thenReturn(Optional.ofNullable(dummyItem1));
		Mockito.when(conflictTransactionRepo.findById(6L)).thenReturn(Optional.ofNullable(conflictTransaction));

		Mockito.when(proPaySubscriber.getAccount("momo")).thenReturn(proPayAccount1);
		Mockito.when(proPaySubscriber.getAccount("nini")).thenReturn(proPayAccount2);
		Mockito.when(conflictTransactionRepo.findAllByLenderAcceptedIsFalseAndLeaserAcceptedIsFalse()).thenReturn(Arrays.asList(conflictTransaction));
	}

	@After
	public void delete() {
		personsRepo.deleteAll();
		itemRepo.deleteAll();
		requestRepo.deleteAll();
		leaseTransactionRepo.deleteAll();
		conflictTransactionRepo.deleteAll();
		paymentTransactionRepo.deleteAll();
	}

  @Test
  @WithMockUser(username = "admin", password = "rootroot")
  public void checkNotSolveConflict() throws Exception {
		Mockito.when(authenticationService.getCurrentUser()).thenReturn(admin);
		Mockito.when(proPaySubscriber.releaseReservation("nini", 7)).thenReturn(null);

		mvc.perform(post("/conflicts")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("conflictId", "6")
				.param("beneficiary", "-1"))
				.andDo(print())
        		.andExpect(status().is3xxRedirection())
        		.andExpect(view().name("redirect:/conflicts"))
        		.andExpect(flash().attribute("message", "Something went wrong with ProPay!"));
	}
	@Test
	@WithMockUser(username = "admin", password = "rootroot")
	public void checkSolveConflict() throws Exception {
		Mockito.when(authenticationService.getCurrentUser()).thenReturn(admin);
		Mockito.when(proPaySubscriber.releaseReservation("nini", 7)).thenReturn(proPayAccount2);
		doReturn(true).when(conflictService).resolveConflict(conflictTransaction,false);

		mvc.perform(post("/conflicts")
				.contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("conflictId", "6")
				.param("beneficiary", "-1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(view().name("rentsTmpl/conflictTransaction"))
				.andExpect(model().attributeExists("allConflicts"));
	}

	@Test
	@WithMockUser(username = "admin", password = "rootroot")
	public void checkConflictUebersicht() throws Exception {
		Mockito.when(authenticationService.getCurrentUser()).thenReturn(admin);

		mvc.perform(get("/conflicts"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attributeExists("allConflicts"))
				.andExpect(view().name("rentsTmpl/conflictTransaction"));
	}

	@Test
	@WithMockUser(username = "momo", password = "abcdabcd")
	public void checkConflictUebersichtFail() throws Exception {
		Mockito.when(authenticationService.getCurrentUser()).thenReturn(dummy1);

		mvc.perform(get("/conflicts"))
				.andDo(print())
				.andExpect(status().is3xxRedirection())
				.andExpect(view().name("redirect:/"));

	}
}