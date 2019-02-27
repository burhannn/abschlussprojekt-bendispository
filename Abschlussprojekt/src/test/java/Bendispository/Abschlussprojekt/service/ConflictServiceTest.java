package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.transactionModels.ConflictTransaction;
import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringRunner.class)
public class ConflictServiceTest {

    @MockBean
    ConflictService conflictService;
    @MockBean
    TransactionService transactionService;
    @MockBean
    LeaseTransaction leaseTransaction;

    ConflictTransaction conflictTransaction;

    @Before
    public void setUp(){
        doReturn(false).when(transactionService).itemIsIntact(leaseTransaction);
        doReturn(false).when(transactionService).itemIsNotIntactConclusion(leaseTransaction);
    }

    @Test
    public void resolveConflictTrue(){
        boolean depositBackToLeaser = true;
        Assert.assertEquals(false, conflictService.resolveConflict(conflictTransaction,depositBackToLeaser));
    }

    @Test
    public void resolveConflictFalse(){
        boolean depositBackToLeaser = false;
        Assert.assertEquals(false, conflictService.resolveConflict(conflictTransaction,depositBackToLeaser));
    }
}
