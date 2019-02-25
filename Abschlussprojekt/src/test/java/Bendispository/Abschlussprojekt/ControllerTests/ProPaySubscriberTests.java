package Bendispository.Abschlussprojekt.ControllerTests;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.transactionModels.ProPayAccount;
import Bendispository.Abschlussprojekt.model.transactionModels.Reservation;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;

@RunWith(SpringRunner.class)
public class ProPaySubscriberTests {

    /*@Autowired
    PersonsRepo personsRepo;

    @Autowired
    ItemRepo itemRepo;*/

    ProPayAccount proPayAccount;

    Person dummy1;

    Item dummyItem1;

    @Before
    public void setUp(){

        proPayAccount = new ProPayAccount();
        proPayAccount.setAccount("iamanoriginalname");
        proPayAccount.setAmount(30.0);
        Reservation r1 = new Reservation();
        r1.setId(7); r1.setAmount(15);
        proPayAccount.setReservations(new Reservation[]{r1});

        dummy1 = new Person();
        dummy1.setUsername("iamanoriginalname");
        dummy1.setPassword("abcdabcd");
        dummy1.setId(1L);

        dummyItem1 = new Item();
        dummyItem1.setName("stuhl");
        dummyItem1.setDeposit(40);
        dummyItem1.setDescription("bin billig");
        dummyItem1.setCostPerDay(10);
        dummyItem1.setId(3L);

        dummy1.setItems(new ArrayList<Item>(){{add(dummyItem1);}});

        /*
        personsRepo.save(dummy1);
        itemRepo.save(dummyItem1);
        */
    }

    @Test
    public void makeDeposit(){
        ProPaySubscriber proPaySubscriber = mock(ProPaySubscriber.class);
        when(proPaySubscriber.getAccount(dummy1.getUsername())).thenReturn(proPayAccount);

        ProPayAccount account = proPaySubscriber.getAccount("iamanoriginalname");
        Reservation[] reservations = account.getReservations();

        assertEquals("iamanoriginalname", account.getAccount());
        assertEquals(30, account.getAmount(), 0.01);
        assertEquals(7, reservations[0].getId());
        assertEquals(15, reservations[0].getAmount(), 0.01);
    }
}
