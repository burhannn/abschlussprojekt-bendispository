package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
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
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RequestServiceTest {

	@MockBean
	PersonsRepo personsRepo;
	@MockBean
	RequestRepo requestRepo;
	@MockBean
	ItemRepo itemRepo;
	@MockBean
	LeaseTransactionRepo leaseTransactionRepo;
	@MockBean
	PaymentTransactionRepo paymentTransactionRepo;
	@MockBean
	ConflictTransactionRepo conflictTransactionRepo;
	@MockBean
	RedirectAttributes redirectAttributes;
	@MockBean
	TransactionService transactionService;
	@MockBean
	ProPaySubscriber proPaySubscriber;
	@MockBean
	AuthenticationService authenticationService;
	RequestService requestService;
	Request r1;
	Request r2;
	Request r3;
	Request r4;
	Person user;
	Item item;
	Person owner;
	Item item2;
	@MockBean
	private Clock clock;
	private Clock fakeClock;

	@Before
	public void sup() {
		MockitoAnnotations.initMocks(this);


		requestService = new RequestService(personsRepo, requestRepo, itemRepo, authenticationService, clock, transactionService, proPaySubscriber, leaseTransactionRepo);


		fakeClock = Clock.fixed(Instant.parse("2019-01-03T10:15:30.00Z"), ZoneId.of("UTC"));
		doReturn(fakeClock.instant()).when(clock).instant();
		doReturn(fakeClock.getZone()).when(clock).getZone();

		r1 = new Request();
		r1.setStartDate(LocalDate.of(2019, 1, 1));
		r1.setEndDate(LocalDate.of(2019, 1, 2));
		r2 = new Request();
		r2.setStartDate(LocalDate.of(2019, 1, 2));
		r2.setEndDate(LocalDate.of(2019, 1, 3));
		r3 = new Request();
		r3.setStartDate(LocalDate.of(2019, 1, 3));
		r3.setEndDate(LocalDate.of(2019, 1, 4));
		r4 = new Request();
		r4.setStartDate(LocalDate.of(2019, 1, 4));
		r4.setEndDate(LocalDate.of(2019, 1, 5));

		user = new Person();
		user.setUsername("user");
		Mockito.doReturn(user).when(authenticationService).getCurrentUser();

		owner = new Person();
		owner.setUsername("owner");

		item = new Item();
		item.setOwner(owner);
		item.setRetailPrice(23);

		item2 = new Item();
		Mockito.doReturn(Optional.of(item)).when(itemRepo).findById(anyLong());

		r1.setRequestedItem(item);
		r2.setRequestedItem(item2);

		requestRepo.saveAll(Arrays.asList(r1, r2, r3, r4));
		when(requestRepo.findAll()).thenReturn(Arrays.asList(r1, r2, r3, r4));
		Mockito.doReturn(user).when(personsRepo).findByUsername(anyString());
		//when(redirectAttributes.addFlashAttribute(anyString(),anyString())).thenReturn(redirectAttributes);

	}

	@Test
	public void deletingTwoObsoleteRequests() {
		List<Request> repoList = requestRepo.findAll();
		List<Request> expectedReturn = new ArrayList<Request>() {{
			add(r3);
			add(r4);
		}};
		List<Request> actualReturn = requestService.deleteObsoleteRequests(repoList);

		assertEquals(expectedReturn, actualReturn);

	}

	@Test
	public void checkingRequestedDate() {
		RequestService spy = Mockito.spy(requestService);
		when(spy.checkRequestedDate("", "")).thenCallRealMethod();

		boolean check = spy.checkRequestedDate("2019.01.02", "2019.01.03");
		assertEquals(false, check);
		check = spy.checkRequestedDate("2019-01-02", "2019-01-02");
		assertEquals(false, check);
		check = spy.checkRequestedDate("2019-01-03", "2019-01-02");
		assertEquals(false, check);
		check = spy.checkRequestedDate("2019-01-02", "2019-01-03");      // false due to LocalDate.now(fakeClock)!
		assertEquals(false, check);
		check = spy.checkRequestedDate("2019-01-05", "2019-01-06");
		assertEquals(true, check);
	}

	@Test
	public void checkingRequestedAvailabilityNotAvailable() {
		doReturn(true).when(transactionService).itemIsAvailableOnTime(any(Request.class));

		boolean check = requestService.checkRequestedAvailability(r1);
		assertEquals(true, check);
	}

	@Test
	public void checkingRequestedAvailabilityIsAvailable() {
		doReturn(false).when(transactionService).itemIsAvailableOnTime(any(Request.class));

		boolean check = requestService.checkRequestedAvailability(r1);
		assertEquals(false, check);
	}

	@Test
	public void checkingRequesterBalanceSufficient() {
		doReturn(false).when(proPaySubscriber).checkDeposit(anyDouble(), eq(""));

		boolean check = requestService.checkRequesterBalance(new Item(), "");
		assertEquals(false, check);
	}

	@Test
	public void checkingRequesterBalanceInsufficient() {
		doReturn(true).when(proPaySubscriber).checkDeposit(anyDouble(), eq(""));

		boolean check = requestService.checkRequesterBalance(new Item(), "");
		assertEquals(true, check);
	}

	@Test
	public void addingBuyRequestIssueWithProPay() {
		Mockito.doReturn(false).when(proPaySubscriber).checkDeposit(anyDouble(), anyString());

		Request request = requestService.addBuyRequest(1L);
		assertEquals(null, request);
	}

	@Test
	public void addingBuyRequestNoIssueWithProPay() {
		Mockito.doReturn(true).when(proPaySubscriber).checkDeposit(anyDouble(), anyString());
		Request request = requestService.addBuyRequest(1L);
		assertEquals(user, request.getRequester());
		assertEquals(RequestStatus.AWAITING_SHIPMENT, request.getStatus());
		assertEquals(item, request.getRequestedItem());
	}

	@Test
	public void buyItemAndTransferMoneyIssueWithProPay() {
		Mockito.doReturn(false).when(proPaySubscriber).transferMoney(anyString(), anyString(), anyDouble());

		boolean check = requestService.buyItemAndTransferMoney(r1);
		assertEquals(false, check);
	}

	@Test
	public void buyItemAndTransferMoneyNoIssueWithProPay() {
		Mockito.doReturn(true).when(proPaySubscriber).transferMoney(anyString(), anyString(), anyDouble());

		boolean check = requestService.buyItemAndTransferMoney(r1);
		assertEquals(true, check);
		assertEquals(false, item.isActive());
		Mockito.verify(requestRepo, times(1)).save(isA(Request.class));
	}

	@Test
	public void addRequestIssueWithDate() {
		Request request = requestService.addRequest("2019-1-5", "2019-1-4", 1L);
		assertEquals(null, request);
		request = requestService.addRequest("2019-1-2", "2019-1-4", 1L);
		assertEquals(null, request);
	}

	@Test
	public void addRequestNoIssueWithDate() {
		Request request = requestService.addRequest("2019-01-04", "2019-01-05", 1L);
		assertEquals(user, request.getRequester());
		assertEquals(LocalDate.of(2019, 1, 4), request.getStartDate());
		assertEquals(LocalDate.of(2019, 1, 5), request.getEndDate());
		assertEquals(1, request.getDuration());
		assertEquals(item, request.getRequestedItem());
	}

	@Test
	public void saveRequestItemNotAvailable() {
		Mockito.doReturn(false).when(transactionService).itemIsAvailableOnTime(any(Request.class));
		boolean check = requestService.saveRequest(r1);
		assertEquals(false, check);
	}

	@Test
	public void saveRequestIssueWithProPay() {
		Mockito.doReturn(true).when(transactionService).itemIsAvailableOnTime(any(Request.class));
		Mockito.doReturn(false).when(proPaySubscriber).checkDeposit(anyDouble(), anyString());
		boolean check = requestService.saveRequest(r2);
		assertEquals(false, check);
	}

	@Test
	public void saveRequestNoIssues() {
		Mockito.doReturn(true).when(transactionService).itemIsAvailableOnTime(any(Request.class));
		Mockito.doReturn(true).when(proPaySubscriber).checkDeposit(anyDouble(), anyString());
		boolean check = requestService.saveRequest(r2);
		assertEquals(true, check);
		Mockito.verify(requestRepo, times(1)).save(isA(Request.class));
	}

}