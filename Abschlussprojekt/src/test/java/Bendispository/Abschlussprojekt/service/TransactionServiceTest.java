package Bendispository.Abschlussprojekt.service;


import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import Bendispository.Abschlussprojekt.model.transactionModels.*;
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

import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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
    LeaseTransaction leaseTransaction2;

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
        r1.setId(1L);
        r1.setStartDate(LocalDate.of(2019,1,5));
        r1.setEndDate(LocalDate.of(2019,1,8));
        item1 = new Item();
        item1.setId(1L);
        item1.setDeposit(30);
        item1.setCostPerDay(5);
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
        leaseTransaction.setStartDate(LocalDate.of(2019,1,5));
        leaseTransaction.setEndDate(LocalDate.of(2019,1,8));
        leaseTransaction.setDuration(Period.between(LocalDate.of(2019,1,5), LocalDate.of(2019,1,8)).getDays());
        leaseTransaction.setRequestId(1L);
        leaseTransaction2 = new LeaseTransaction();
        leaseTransaction2.setLeaser(person1);
        leaseTransaction2.setItem(item1);
        leaseTransaction2.setStartDate(LocalDate.of(2019,1,1));
        leaseTransaction2.setEndDate(LocalDate.of(2019,1,2));
        leaseTransaction2.setDuration(Period.between(LocalDate.of(2019,1,1), LocalDate.of(2019,1,2)).getDays());
        leaseTransaction2.setRequestId(1L);
    }

    @Test
    public void lenderApprovedProPayReturnsFalse() {
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
    public void lenderApprovedProPayReturnsTrueButNoDeposit() {
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
    public void lenderApprovedProPayReturnsTrueAndDepositWasMade() {
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
        assertEquals(PaymentType.DEPOSIT, r1.getLeaseTransaction().getPayments().get(0).getType());
        assertEquals(false, r1.getLeaseTransaction().getPayments().get(0).isPaymentIsConcluded());
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

    @Test
    public void lenderApprovedOthersAtSameTimeAreDenied(){
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
                        spy,
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
    }

    @Test
    public void itemIsAvailableOnTimeFalse(){
        LeaseTransaction l1 = new LeaseTransaction();
        LeaseTransaction l2 = new LeaseTransaction();
        LeaseTransaction l3 = new LeaseTransaction();

        // r1 => 5.1-8.1

        l1.setStartDate(LocalDate.of(2019,1,6));
        l1.setEndDate(LocalDate.of(2019,1,8));
        l2.setStartDate(LocalDate.of(2019,1,9));
        l2.setEndDate(LocalDate.of(2019,1,10));
        l3.setStartDate(LocalDate.of(2019,1,7));
        l3.setEndDate(LocalDate.of(2019,1,8));

        LeaseTransactionRepo spy = Mockito.spy(LeaseTransactionRepo.class);
        List<LeaseTransaction> ret = Arrays.asList(l1, l2, l3);
        Mockito.doReturn(ret).when(spy).findAllByItemId(1L);

        transactionService =
                new TransactionService(
                        spy,
                        requestRepo,
                        proPaySubscriber,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);

        boolean check = transactionService.itemIsAvailableOnTime(r1);

        assertEquals(false, check);
    }

    @Test
    public void itemIsAvailableOnTimeTrue(){
        LeaseTransaction l1 = new LeaseTransaction();
        LeaseTransaction l2 = new LeaseTransaction();
        LeaseTransaction l3 = new LeaseTransaction();

        // r1 => 5.1-8.1

        l1.setStartDate(LocalDate.of(2019,1,19));
        l1.setEndDate(LocalDate.of(2019,1,22));
        l2.setStartDate(LocalDate.of(2019,1,9));
        l2.setEndDate(LocalDate.of(2019,1,10));
        l3.setStartDate(LocalDate.of(2019,1,11));
        l3.setEndDate(LocalDate.of(2019,1,17));

        LeaseTransactionRepo spy = Mockito.spy(LeaseTransactionRepo.class);
        List<LeaseTransaction> ret = Arrays.asList(l1, l2, l3);
        Mockito.doReturn(ret).when(spy).findAllByItemId(1L);

        transactionService =
                new TransactionService(
                        spy,
                        requestRepo,
                        proPaySubscriber,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);

        boolean check = transactionService.itemIsAvailableOnTime(r1);

        assertEquals(true, check);
    }

    @Test
    public void itemReturnedToLenderIssueWithProPay(){
        ProPaySubscriber spy = Mockito.spy(proPaySubscriber);
        Mockito.doReturn(false).when(spy).transferMoney(anyString(), anyString(), anyDouble());
        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        spy,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);

        boolean check = transactionService.itemReturnedToLender(leaseTransaction);

        assertEquals(false, check);
    }

    @Test
    public void itemReturnedToLenderNoIssueWithProPay(){
        ProPaySubscriber spy = Mockito.spy(proPaySubscriber);
        Mockito.doReturn(true).when(spy).transferMoney(anyString(), anyString(), anyDouble());
        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        spy,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);

        boolean check = transactionService.itemReturnedToLender(leaseTransaction);

        assertEquals(true, check);
    }

    @Test
    public void itemReturnedToLenderNotInTimeNoIssueThenIssueWithProPayWhenPayingDelay(){
        ProPaySubscriber spy = Mockito.spy(proPaySubscriber);
        Mockito.doReturn(true, false).when(spy).transferMoney(anyString(), anyString(), anyDouble());
        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        spy,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        fakeClock);

        boolean check = transactionService.itemReturnedToLender(leaseTransaction2);

        assertEquals(false, check);
    }

    @Test
    public void isNotReturnedInTimeIssueWithProPay(){
        ProPaySubscriber spy = Mockito.spy(proPaySubscriber);
        Mockito.doReturn(false).when(spy).transferMoney(anyString(), anyString(), anyDouble());

        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        spy,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        fakeClock);
        boolean check = transactionService.isReturnedInTime(leaseTransaction2, person1, person2);

        assertEquals(false, check);
    }

    @Test
    public void isNotReturnedInTimeNoIssueWithProPay(){
        ProPaySubscriber spy = Mockito.spy(proPaySubscriber);
        Mockito.doReturn(true).when(spy).transferMoney(anyString(), anyString(), anyDouble());

        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        spy,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        fakeClock);
        boolean check = transactionService.isReturnedInTime(leaseTransaction2, person1, person2);

        assertEquals(true, check);
        assertEquals(true, leaseTransaction2.isTimeframeViolation());
        assertEquals(1, leaseTransaction2.getLengthOfTimeframeViolation());
        assertEquals(1, leaseTransaction2.getPayments().size());
    }

    @Test
    public void itemIsIntactIssueWithProPay(){
        ProPaySubscriber spy = Mockito.spy(proPaySubscriber);
        Mockito.doReturn(null).when(spy).releaseReservation(anyString(),anyInt());

        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        spy,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);
        boolean check = transactionService.itemIsIntact(leaseTransaction);
        assertEquals(false, check);
    }

    @Test
    public void itemIsIntactNoIssueWithProPay(){
        ProPaySubscriber spy = Mockito.spy(proPaySubscriber);
        ProPayAccount account = new ProPayAccount();
        Mockito.doReturn(account).when(spy).releaseReservation(anyString(),anyInt());

        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        spy,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);


        PaymentTransaction p1 = transactionService.makePayment(person1, person2, r1.getRequestedItem().getDeposit(), leaseTransaction, PaymentType.DEPOSIT);
        leaseTransaction.addPaymentTransaction(p1);

        boolean check = transactionService.itemIsIntact(leaseTransaction);

        assertEquals(true, check);
    }

    @Test
    public void itemIsNotIntact(){
        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        proPaySubscriber,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);

        ConflictTransaction conflictTransaction = transactionService.itemIsNotIntact(new Person(), leaseTransaction, "Echo!");
        Mockito.verify(conflictTransactionRepo, times(1)).save(isA(ConflictTransaction.class));
        assertEquals("Echo!", conflictTransaction.getCommentary());
        assert(leaseTransaction.equals(conflictTransaction.getLeaseTransaction()));
    }

    @Test
    public void itemIsNotIntactConclusionIssueWithProPay(){
        Mockito.doReturn(null).when(proPaySubscriber).releaseReservationAndPunishUser(anyString(), anyInt());
        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        proPaySubscriber,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);

        boolean check = transactionService.itemIsNotIntactConclusion(leaseTransaction);
        assertEquals(false, check);
    }

    @Test
    public void itemIsNotIntactConclusionNoIssueWithProPay(){
        Mockito.doReturn(new ProPayAccount()).when(proPaySubscriber).releaseReservationAndPunishUser(anyString(), anyInt());
        transactionService =
                new TransactionService(
                        leaseTransactionRepo,
                        requestRepo,
                        proPaySubscriber,
                        paymentTransactionRepo,
                        conflictTransactionRepo,
                        ratingRepo,
                        clock);

        boolean check = transactionService.itemIsNotIntactConclusion(leaseTransaction);
        assertEquals(true, check);
    }

    @Test
    public void notIntact(){
        String comment = "comment";
        Person me = new Person();
        Optional<LeaseTransaction> l2 = Optional.of(leaseTransaction);
        Mockito.doReturn(l2).when(leaseTransactionRepo).findById(anyLong());

        ConflictTransaction conflictTransaction = transactionService.notIntact(1L, comment, me);

        assert(conflictTransaction.getLeaseTransaction().equals(l2.get()));
        Mockito.verify(conflictTransactionRepo, times(1)).save(isA(ConflictTransaction.class));
    }

}