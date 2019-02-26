package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.transactionModels.PaymentTransaction;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.hibernate.jdbc.Expectations;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.swing.*;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyList;
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
    RatingRepo ratingRepo;

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

    @Before
    public void sup(){
        MockitoAnnotations.initMocks(this);

        //requestService = new RequestService(personsRepo, requestRepo, itemRepo, authenticationService, clock, transactionService, proPaySubscriber);

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

        requestRepo.saveAll(Arrays.asList(r1,r2,r3,r4));
        when(requestRepo.findAll()).thenReturn(Arrays.asList(r1,r2,r3,r4));
        when(redirectAttributes.addFlashAttribute(anyString(),anyString())).thenReturn(redirectAttributes);
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

        boolean check = requestService.checkRequestedAvailability(redirectAttributes, r1);
        assertEquals(true, check);
    }

    @Test
    public void checkingRequestedAvailabilityIsAvailable(){
        doReturn(false).when(transactionService).itemIsAvailableOnTime(any(Request.class));

        boolean check = requestService.checkRequestedAvailability(redirectAttributes, r1);
        assertEquals(false, check);
    }

    @Test
    public void checkingRequesterBalanceSufficient(){
        doReturn(false).when(proPaySubscriber).checkDeposit(anyDouble(), eq(""));

        boolean check = requestService.checkRequesterBalance(redirectAttributes, new Item(), "");
        assertEquals(false, check);
    }

    @Test
    public void checkingRequesterBalanceInsufficient(){
        doReturn(true).when(proPaySubscriber).checkDeposit(anyDouble(), eq(""));

        boolean check = requestService.checkRequesterBalance(redirectAttributes, new Item(), "");
        assertEquals(true, check);
    }



}