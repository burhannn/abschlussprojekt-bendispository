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
    private Clock clock;

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

    private Clock fakeClock;

    RequestService requestService;

    Request r1;
    Request r2;
    Request r3;
    Request r4;

    Person user;
    Item item;
    Person owner;
    Item item2;
    Request rbuy;

    @Before
    public void sup(){
        MockitoAnnotations.initMocks(this);


        requestService = new RequestService(personsRepo, requestRepo, itemRepo, authenticationService, clock, transactionService, proPaySubscriber, leaseTransactionRepo);


        fakeClock = Clock.fixed(Instant.parse("2019-01-03T10:15:30.00Z"), ZoneId.of("UTC"));
        doReturn(fakeClock.instant()).when(clock).instant();
        doReturn(fakeClock.getZone()).when(clock).getZone();

        r1 = new Request();
        r1.setStartDate(LocalDate.of(2019,1,1));
        r1.setEndDate(LocalDate.of(2019,1,2));
        r2 = new Request();
        r2.setStartDate(LocalDate.of(2019,1,2));
        r2.setEndDate(LocalDate.of(2019,1,3));
        r3 = new Request();
        r3.setStartDate(LocalDate.of(2019,1,3));
        r3.setEndDate(LocalDate.of(2019,1,4));
        r4 = new Request();
        r4.setStartDate(LocalDate.of(2019,1,4));
        r4.setEndDate(LocalDate.of(2019,1,5));

        rbuy = new Request();
        rbuy.setStartDate(LocalDate.of(2019,1,1));
        rbuy.setEndDate(LocalDate.of(2019,1,2));
        rbuy.setStatus(RequestStatus.AWAITING_SHIPMENT);

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
        r1.setRequester(user);
        r2.setRequestedItem(item2);
        rbuy.setRequestedItem(item);


        requestRepo.saveAll(Arrays.asList(r1,r2,r3,r4));
        when(requestRepo.findAll()).thenReturn(Arrays.asList(r1,r2,r3,r4));
        Mockito.doReturn(user).when(personsRepo).findByUsername(anyString());
    }

    @Test
    public void deletingTwoObsoleteRequests(){
        List<Request> repoList = requestRepo.findAll();
        List<Request> expectedReturn = new ArrayList<Request>(){{add(r3);add(r4);}};
        List<Request> actualReturn = requestService.deleteObsoleteRequests(repoList);

        assertEquals(expectedReturn, actualReturn);

    }

    @Test
    public void checkingRequestedDate(){
        RequestService spy = Mockito.spy(requestService);
        when(spy.checkRequestedDate("","")).thenCallRealMethod();

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
    public void checkingRequestedAvailabilityNotAvailable(){
        doReturn(true).when(transactionService).itemIsAvailableOnTime(any(Request.class));

        boolean check = requestService.checkRequestedAvailability(r1);
        assertEquals(true, check);
    }

    @Test
    public void checkingRequestedAvailabilityIsAvailable(){
        doReturn(false).when(transactionService).itemIsAvailableOnTime(any(Request.class));

        boolean check = requestService.checkRequestedAvailability(r1);
        assertEquals(false, check);
    }

    @Test
    public void checkingRequesterBalanceSufficient(){
        doReturn(false).when(proPaySubscriber).checkDeposit(anyDouble(), eq(""));

        boolean check = requestService.checkRequesterBalance(new Item(), "");
        assertEquals(false, check);
    }

    @Test
    public void checkingRequesterBalanceInsufficient(){
        doReturn(true).when(proPaySubscriber).checkDeposit(anyDouble(), eq(""));

        boolean check = requestService.checkRequesterBalance(new Item(), "");
        assertEquals(true, check);
    }

    @Test
    public void addingBuyRequestIssueWithProPay(){
        Mockito.doReturn(false).when(proPaySubscriber).checkDeposit(anyDouble(), anyString());

        Request request = requestService.addBuyRequest(1L);
        assertEquals(null, request);
    }

    @Test
    public void addingBuyRequestNoIssueWithProPay(){
        Mockito.doReturn(true).when(proPaySubscriber).checkDeposit(anyDouble(), anyString());
        Request request = requestService.addBuyRequest(1L);
        assertEquals(user, request.getRequester());
        assertEquals(RequestStatus.AWAITING_SHIPMENT, request.getStatus());
        assertEquals(item, request.getRequestedItem());
    }

    @Test
    public void buyItemAndTransferMoneyIssueWithProPay(){
        Mockito.doReturn(false).when(proPaySubscriber).transferMoney(anyString(), anyString(), anyDouble());

        boolean check = requestService.buyItemAndTransferMoney(r1);
        assertEquals(false, check);
    }

    @Test
    public void buyItemAndTransferMoneyNoIssueWithProPay(){
        Mockito.doReturn(true).when(proPaySubscriber).transferMoney(anyString(), anyString(), anyDouble());

        boolean check = requestService.buyItemAndTransferMoney(r1);
        assertEquals(true, check);
        assertEquals(false, item.isActive());
        Mockito.verify(requestRepo, times(1)).save(isA(Request.class));
    }

    @Test
    public void addRequestIssueWithDate(){
        Request request = requestService.addRequest("2019-1-5", "2019-1-4", 1L);
        assertEquals(null, request);
        request = requestService.addRequest("2019-1-2", "2019-1-4", 1L);
        assertEquals(null, request);
    }

    @Test
    public void addRequestNoIssueWithDate(){
        Request request = requestService.addRequest("2019-01-04", "2019-01-05", 1L);
        assertEquals(user, request.getRequester());
        assertEquals(LocalDate.of(2019,1,4), request.getStartDate());
        assertEquals(LocalDate.of(2019,1,5), request.getEndDate());
        assertEquals(1, request.getDuration());
        assertEquals(item, request.getRequestedItem());
    }

    @Test
    public void checkSaveRequest(){
        requestService.saveRequest(r1);
        Mockito.verify(requestRepo, times(1)).save(isA(Request.class));
    }

    @Test
    public void checkRequestedAvailabilityIsAvailable(){
        Mockito.doReturn(true).when(transactionService).itemIsAvailableOnTime(any(Request.class));
        boolean check = requestService.checkRequestedAvailability(r1);
        assertEquals(true, check);
    }

    @Test
    public void checkRequestedAvailabilityIsNotAvailable(){
        Mockito.doReturn(false).when(transactionService).itemIsAvailableOnTime(any(Request.class));
        boolean check = requestService.checkRequestedAvailability(r1);
        assertEquals(false, check);
    }

    @Test
    public void checkRequesterBalanceIsSufficient(){
        Mockito.doReturn(true).when(proPaySubscriber).checkDeposit(anyDouble(), anyString());
        boolean check = requestService.checkRequesterBalance(item, r1.getRequester().getUsername());
        assertEquals(true, check);
    }

    @Test
    public void checkRequesterBalanceIsNotSufficient(){
        Mockito.doReturn(false).when(proPaySubscriber).checkDeposit(anyDouble(), anyString());
        boolean check = requestService.checkRequesterBalance(item, r1.getRequester().getUsername());
        assertEquals(false, check);
    }

    @Test
    public void wasShipped(){
        boolean check = requestService.wasShipped(rbuy, -1);
        assertEquals(true, check);
        assertEquals(RequestStatus.SHIPPED, rbuy.getStatus());
    }

    @Test
    public void wasNotShipped(){
        boolean check = requestService.wasShipped(rbuy, null);
        assertEquals(false, check);
        assertEquals(RequestStatus.AWAITING_SHIPMENT, rbuy.getStatus());
    }

    @Test
    public void wasDenied(){
        boolean check = requestService.wasDeniedOrAccepted(-1, r2);
        assertEquals(RequestStatus.DENIED, r2.getStatus());
        verify(requestRepo, times(1)).save(isA(Request.class));
        assertEquals(check, true);
    }

    @Test
    public void wasAcceptedIssueWithProPayOrAccepting(){
        Mockito.doReturn(false).when(transactionService).lenderApproved(any(Request.class));
        boolean check = requestService.wasDeniedOrAccepted(1, r2);
        assertEquals(RequestStatus.PENDING, r2.getStatus());
        assertEquals(check, false);
    }

    @Test
    public void wasAcceptedNoIssueWithProPayOrAccepting(){
        Mockito.doReturn(true).when(transactionService).lenderApproved(any(Request.class));
        boolean check = requestService.wasDeniedOrAccepted(1, r2);
        assertEquals(RequestStatus.PENDING, r2.getStatus());
        assertEquals(check, true);
    }
}