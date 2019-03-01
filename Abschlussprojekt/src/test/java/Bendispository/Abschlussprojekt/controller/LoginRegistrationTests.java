package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import Bendispository.Abschlussprojekt.service.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest
public class LoginRegistrationTests {

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
	CustomUserDetailsService blabla;

	@MockBean
	AuthenticationService authenticationService;

	@MockBean
	ConflictService conflictService;

	@MockBean
	RatingRepo ratingRepo;

	@MockBean
	RequestService requestService;

	@MockBean
	ItemService itemService;

	@MockBean
	ProPaySubscriber proPaySubscriber;

	@MockBean
	TransactionService transactionService;

	Person dummy1;

	@Before
	public void setUp() {
		mvc = MockMvcBuilders
				.webAppContextSetup(context)
				.apply(springSecurity())
				.build();

		dummy1 = new Person();

		dummy1.setFirstName("mandy");
		dummy1.setLastName("moraru");
		dummy1.setCity("kölle");
		dummy1.setEmail("momo@gmail.com");
		dummy1.setUsername("momo");
		dummy1.setPassword("abcdabcd");
		dummy1.setId(1L);

		personsRepo.save(dummy1);
		Mockito.when(authenticationService.getCurrentUser()).thenReturn(dummy1);
		Mockito.when(personsRepo.findByUsername("momo")).thenReturn(dummy1);
	}

	@Test
	public void checkLoginFail() throws Exception {

		mvc.perform(post("/login").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("username", "")
				.param("password", ""))
				.andExpect(status().is3xxRedirection());
	}

	@Test
	@WithMockUser(username = "momo", password = "abcd", roles = "USER")
	public void checkRegistration() throws Exception {

		mvc.perform(post("/registration").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("lastName", "soft")
				.param("firstName", "clara")
				.param("username", "clari")
				.param("email", "clari@gmx.de")
				.param("city", "Düsseldorf")
				.sessionAttr("newPerson", new Person()))
				.andExpect(status().isOk())
				.andExpect(view().name("authTmpl/login"))
				.andExpect(model().attribute("newPerson", hasProperty("id")))
				.andExpect(model().attribute("newPerson", hasProperty("firstName", equalTo("clara"))))
				.andExpect(model().attribute("newPerson", hasProperty("lastName", equalTo("soft"))))
				.andExpect(model().attribute("newPerson", hasProperty("username", equalTo("clari"))))
				.andExpect(model().attribute("newPerson", hasProperty("email", equalTo("clari@gmx.de"))))
				.andExpect(model().attribute("newPerson", hasProperty("city", equalTo("Düsseldorf"))));
	}

	@Test
	@WithMockUser(username = "momo", password = "abcdabcd", roles = "USER")
	public void checkRegistrationfailUsernameNotAvailable() throws Exception {

		mvc.perform(post("/registration").contentType(MediaType.APPLICATION_FORM_URLENCODED)
				.param("lastName", "mandy")
				.param("firstName", "clara")
				.param("username", "momo")
				.param("email", "clari@gmx.de")
				.param("city", "Düsseldorf")
				.param("password", "abcdabcd")
				.sessionAttr("newPerson", new Person()))
				.andExpect(view().name("authTmpl/registrationError"))
				.andExpect(status().isOk());
	}

	@Test
	public void retrieve() throws Exception {

		mvc.perform(get("/registration")).andExpect(status().isOk());
		mvc.perform(get("/login")).andExpect(status().isOk());
		mvc.perform(get("/loggedOut")).andExpect(status().isOk());
	}
}
