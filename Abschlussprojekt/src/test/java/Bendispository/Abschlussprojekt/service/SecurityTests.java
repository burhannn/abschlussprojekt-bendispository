package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@WebMvcTest
public class SecurityTests {

	@Autowired
	MockMvc mvc;

	@MockBean
	ProPaySubscriber proPaySubscriber;

	@MockBean
	TransactionService transactionService;

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
	ConflictService conflictService;

	@MockBean
	RatingRepo ratingRepo;

	@MockBean
	RequestService requestService;

	@MockBean
	ItemService itemService;

	Person dummy1;

	@Before
	public void setUp() {
		dummy1 = new Person();

		dummy1.setFirstName("mandy");
		dummy1.setLastName("moraru");
		dummy1.setCity("kölle");
		dummy1.setEmail("momo@gmail.com");
		dummy1.setUsername("mandypandy");
		dummy1.setPassword("abcdabcd");
		dummy1.setId(1L);

		personsRepo.save(dummy1);
		Mockito.when(personsRepo.findByUsername("mandypandy")).thenReturn(dummy1);
	}

	@Test
	@WithMockUser(username = "mandypandy", password = "abcdabcd", roles = "USER")
	public void checkAuthenticationService() {
		//ARRANGE
		SecurityContext context = SecurityContextHolder.getContext();

		personsRepo.save(dummy1);
		MyUserPrincipal principal = new MyUserPrincipal(dummy1);
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, "abcdabcd", principal.getAuthorities());
		AuthenticationService authService = new AuthenticationService(personsRepo);
		//ACT
		context.setAuthentication(auth);

		//ASSERT
		Assert.assertEquals(authService.getCurrentUser(), dummy1);
		Assert.assertEquals(principal, context.getAuthentication().getPrincipal());
	}

	@Test
	@WithMockUser(username = "mandypandy", password = "abcdabcd", roles = "USER")
	public void checkPrincipal() {
		//ARRANGE
		SecurityContext context = SecurityContextHolder.getContext();

		personsRepo.save(dummy1);
		MyUserPrincipal principal = new MyUserPrincipal(dummy1);
		Authentication auth = new UsernamePasswordAuthenticationToken(principal, "abcdabcd", principal.getAuthorities());
		AuthenticationService authService = new AuthenticationService(personsRepo);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		//ACT
		context.setAuthentication(auth);
		String result = principal.getPassword();

		//ASSERT
		assertThat(encoder.matches("abcdabcd", result)).isTrue();
		Assert.assertEquals(authService.getCurrentUser(), dummy1);
		Assert.assertEquals(principal, context.getAuthentication().getPrincipal());
	}

	@Test
	@WithMockUser(username = "mandypandy", password = "abcdabcd", roles = "USER")
	public void testLoadUserByUsername() {

		//ARRANGE
		MyUserPrincipal principal1 = new MyUserPrincipal(dummy1);
		boolean a, b, c, d;

		personsRepo.save(dummy1);
		CustomUserDetailsService customUserDetailsService = new CustomUserDetailsService(personsRepo);

		//ACT
		UserDetails principal2 = customUserDetailsService.loadUserByUsername("mandypandy");
		a = principal2.isAccountNonExpired();
		b = principal2.isAccountNonLocked();
		c = principal2.isCredentialsNonExpired();
		d = principal2.isEnabled();

		//ASSERT
		Assert.assertEquals(principal1, principal2);
		assertThat(true).isIn(a, b, c, d);

	}

	@Test(expected = NullPointerException.class)
	public void testEmptyUsername() {

		//ARRANGE
		CustomUserDetailsService customUserDetailsServiceLeer = new CustomUserDetailsService();

		//ACT
		UserDetails emptyprincipal = customUserDetailsServiceLeer.loadUserByUsername(null);

		//ASSERT
		Assert.assertNull(emptyprincipal);

	}
}


