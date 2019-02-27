package Bendispository.Abschlussprojekt.model;

import Bendispository.Abschlussprojekt.model.transactionModels.PaymentTransaction;
import Bendispository.Abschlussprojekt.model.transactionModels.PaymentType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
public class PaymentTransactionTest {

	@Test
	public void toStringPayTrans() {
		PaymentTransaction paymentTransaction = new PaymentTransaction();
		paymentTransaction.setPaymentIsConcluded(true);
		paymentTransaction.setType(PaymentType.DAMAGES);
		paymentTransaction.setAmount(34);

		Assert.assertEquals("amount:34.0 paymentisconcluded:true type:DAMAGES", paymentTransaction.toString());

	}
}
