package Bendispository.Abschlussprojekt.ServiceTests;

import Bendispository.Abschlussprojekt.Service.ProPaySubscriber;
import Bendispository.Abschlussprojekt.Service.TransactionService;
import Bendispository.Abschlussprojekt.repos.RatingRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.ConflictTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.PaymentTransactionRepo;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static org.mockito.Mockito.doReturn;

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
    Clock clock;

    Clock fakeClock;

    TransactionService transactionService;

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

    }

}