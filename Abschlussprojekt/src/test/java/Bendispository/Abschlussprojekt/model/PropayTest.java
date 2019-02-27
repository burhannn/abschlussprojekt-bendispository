package Bendispository.Abschlussprojekt.model;

import Bendispository.Abschlussprojekt.model.transactionModels.ProPayAccount;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
public class PropayTest {

    @Test
    public void toStringPropay(){
        ProPayAccount proPayAccount = new ProPayAccount();
        proPayAccount.setAccount("xyz");
        proPayAccount.setAmount(34);

        Assert.assertEquals("account: xyz amount: 34.0", proPayAccount.toString());
    }
}
