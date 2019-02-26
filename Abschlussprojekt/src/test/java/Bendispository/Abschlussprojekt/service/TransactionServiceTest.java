package Bendispository.Abschlussprojekt.service;


import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.model.transactionModels.PaymentTransaction;
import Bendispository.Abschlussprojekt.model.transactionModels.PaymentType;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import Bendispository.Abschlussprojekt.service.TransactionService;

import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;


import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.doReturn;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {

    @MockBean
    LeaseTransactionRepo leaseTransactionRepo;

    @MockBean
    RequestRepo requestRepo;

    @MockBean
    ProPaySubscriber proPaySubscriber;

    @MockBean
    PaymentTransactionRepo paymentTransactionRepo;

    @MockBean
    ConflictTransactionRepo conflictTransactionRepo;

    @MockBean
    RatingRepo ratingRepo;

    @MockBean
    private Clock clock;

    Clock fakeClock;

    TransactionService transactionService;

    Request r1;
    Item item1;
    Person person1;
    Person person2;
    LeaseTransaction leaseTransaction;

    @Before
    public void sup(){
        MockitoAnnotations.initMocks(this);

        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        proPaySubscriber,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock
                );

        fakeClock = Clock.fixed(Instant.parse("2019-01-03T10:15:30.00Z"), ZoneId.of("UTC"));
        doReturn(fakeClock.instant()).when(clock).instant();
        doReturn(fakeClock.getZone()).when(clock).getZone();

        r1 = new Request();
        r1.setStartDate(LocalDate.of(2019,1,5));
        r1.setEndDate(LocalDate.of(2019,1,8));
        item1 = new Item();
        item1.setDeposit(30);
        person1 = new Person();
        person1.setUsername("user");
        person2 = new Person();
        person2.setUsername("owner");
        item1.setOwner(person2);
        r1.setRequestedItem(item1);
        r1.setRequester(person1);
        leaseTransaction = new LeaseTransaction();
        leaseTransaction.setLeaser(person1);
        leaseTransaction.setItem(item1);
    }

    @Test
    public void lenderApproveProPayReturnsFalse() {
        ProPaySubscriber spy = Mockito.spy(proPaySubscriber);
        doReturn(false).when(spy).checkDeposit(anyDouble(), eq(""));
        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        spy,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);
        boolean check = transactionService.lenderApproved(r1);
        assertEquals(false, check);
    }

    @Test
    public void lenderApproveProPayReturnsTrueButNoDeposit() {
        ProPaySubscriber spy = Mockito.spy(proPaySubscriber);
        doReturn(true).when(spy).checkDeposit(anyDouble(), anyString());
        doReturn(-1).when(spy).makeDeposit(r1);
        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        spy,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);
        boolean check = transactionService.lenderApproved(r1);
        assertEquals(false, check);
    }

    @Test
    public void lenderApproveProPayReturnsTrueAndDepositWasMade() {
        ProPaySubscriber spy = Mockito.spy(proPaySubscriber);
        Mockito.doReturn(true).when(spy).checkDeposit(anyDouble(), anyString());
        Mockito.doReturn(1).when(spy).makeDeposit(any(Request.class));
        //transactionService = Mockito.mock(TransactionService.class);
        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        spy,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);

        leaseTransaction = Mockito.mock(LeaseTransaction.class);
        Mockito.doNothing().when(leaseTransaction).addLeaseTransaction(any(Request.class), anyInt());

        boolean check = transactionService.lenderApproved(r1);
        assertEquals(true, check);
    }

    @Test
    public void isOverlapping(){
        LocalDate date1 = LocalDate.of(2019,01,01);
        LocalDate date2 = LocalDate.of(2019,01,02);
        LocalDate date3 = LocalDate.of(2019,01,03);
        LocalDate date4 = LocalDate.of(2019,01,04);

        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        proPaySubscriber,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);

        boolean check =
                transactionService
                        .isOverlapping(
                                date1, date2,
                                date3, date4);
        assertEquals(false, check);

        check =
                transactionService
                        .isOverlapping(
                                date1, date3,
                                date2, date4);
        assertEquals(true, check);

        check =
                transactionService
                        .isOverlapping(
                                date1, date4,
                                date2, date3);
        assertEquals(true, check);

        check =
                transactionService
                        .isOverlapping(
                                date3, date4,
                                date1, date2);
        assertEquals(false, check);
    }

    /*@Test
    public void lenderApprovedOthersAtSamePointInTimeAreDenied(){
        ProPaySubscriber spy = Mockito.spy(proPaySubscriber);
        Mockito.doReturn(true).when(spy).checkDeposit(anyDouble(), anyString());
        Mockito.doReturn(1).when(spy).makeDeposit(any(Request.class));

        Request r2 = new Request();
        r2.setStartDate(LocalDate.of(2019,1,6));
        r2.setEndDate(LocalDate.of(2019,1,8));
        r2.setRequestedItem(item1);
        Request r3 = new Request();
        r3.setStartDate(LocalDate.of(2019,1,9));
        r3.setEndDate(LocalDate.of(2019,1,10));
        r3.setRequestedItem(item1);
        Request r4 = new Request();
        r4.setStartDate(LocalDate.of(2019,1,7));
        r4.setEndDate(LocalDate.of(2019,1,8));
        r4.setRequestedItem(item1);

        Mockito.doReturn(Arrays.asList(r1,r2,r3,r4)).when(requestRepo).findAllByRequestedItem(any(Item.class));

        leaseTransaction = Mockito.mock(LeaseTransaction.class);
        Mockito.doNothing().when(leaseTransaction).addLeaseTransaction(any(Request.class), anyInt());

        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        proPaySubscriber,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);
        boolean check = transactionService.lenderApproved(r1);
        assertEquals(true, check);
        assertEquals(RequestStatus.DENIED, r2.getStatus());
        assertEquals(RequestStatus.PENDING, r3.getStatus());
        assertEquals(RequestStatus.DENIED, r4.getStatus());
        assertEquals(RequestStatus.APPROVED, r1.getStatus());
    }*/
}