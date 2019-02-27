package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.transactionModels.ProPayAccount;
import Bendispository.Abschlussprojekt.model.transactionModels.Reservation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
public class ProPaySubscriberTests {

	ProPayAccount proPayAccount1;
	ProPayAccount proPayAccount2;
	Request request;

	Person dummy1;
	Person dummy2;

	Item dummyItem1;
	Item dummyItem2;

	Reservation r1;
	Reservation r2;

	@Before
	public void setUp() {

		proPayAccount1 = new ProPayAccount();
		proPayAccount1.setAccount("iamanoriginalname");
		proPayAccount1.setAmount(30.0);
		r1 = new Reservation();
		r1.setId(7);
		r1.setAmount(15.0);
		r2 = new Reservation();
		r2.setId(8);
		r2.setAmount(25.0);
		proPayAccount1.setReservations(new Reservation[]{r1, r2});

		proPayAccount2 = new ProPayAccount();
		proPayAccount2.setAccount("iamanoriginalname");
		proPayAccount2.setAmount(30);
		proPayAccount2.setReservations(new Reservation[]{r1});

		dummy1 = new Person();
		dummy1.setUsername("iamanoriginalname");
		dummy1.setPassword("abcdabcd");
		dummy1.setId(1L);

		dummyItem1 = new Item();
		dummyItem1.setOwner(dummy1);
		dummyItem1.setName("Stuhl");
		dummyItem1.setDeposit(40);
		dummyItem1.setDescription("bin billig");
		dummyItem1.setCostPerDay(10);
		dummyItem1.setId(3L);

		dummy1.setItems(new ArrayList<Item>() {{
			add(dummyItem1);
		}});

		dummy2 = new Person();
		dummy2.setUsername("iamanoriginalname");
		dummy2.setPassword("abcdabcd");
		dummy2.setId(2L);

		dummyItem2 = new Item();
		dummyItem2.setOwner(dummy2);
		dummyItem2.setName("Hocker");
		dummyItem2.setDeposit(20);
		dummyItem2.setDescription("bin noch billiger");
		dummyItem2.setCostPerDay(5);
		dummyItem2.setId(4L);

		dummy2.setItems(new ArrayList<Item>() {{
			add(dummyItem2);
		}});

		request = new Request();
		request.setRequester(dummy1);
		request.setRequestedItem(dummyItem2);
		request.setDuration(3);
		request.setStartDate(LocalDate.of(2019, 5, 4));
		request.setEndDate(LocalDate.of(2019, 5, 7));


            /*
            personsRepo.save(dummy1);
            itemRepo.save(dummyItem1);
            */
	}

	@Test
	public void makeDepositCallCorrect() {
		ProPaySubscriber proPaySubscriber = mock(ProPaySubscriber.class);
		when(proPaySubscriber.makeReservation(anyString(), anyString(), anyDouble())).thenReturn(r1);
		when(proPaySubscriber.makeDeposit(any(Request.class))).thenCallRealMethod();

		int reservationId =
				proPaySubscriber.makeDeposit(request);
		assertEquals(7, reservationId);
	}

	@Test
	public void makeDepositAndReservationCallNotCorrectReal() {
		ProPaySubscriber proPaySubscriber = mock(ProPaySubscriber.class);
		when(proPaySubscriber.makeReservation(anyString(), anyString(), anyDouble())).thenCallRealMethod();

		Reservation reservation =
				proPaySubscriber.makeReservation(
						request.getRequester().getUsername(),
						request.getRequester().getUsername(),
						request.getRequestedItem().getDeposit());
		assertEquals(null, reservation);
	}

	@Test
	public void makeDepositCallNotCorrect() {
		ProPaySubscriber proPaySubscriber = mock(ProPaySubscriber.class);
		when(proPaySubscriber.makeReservation(anyString(), anyString(), anyDouble())).thenReturn(null);
		when(proPaySubscriber.makeDeposit(any(Request.class))).thenCallRealMethod();

		int reservationId =
				proPaySubscriber.makeDeposit(request);
		assertEquals(-1, reservationId);
	}

	@Test
	public void checkDepositAccountIsNull() {
		ProPaySubscriber proPaySubscriber = mock(ProPaySubscriber.class);
		when(proPaySubscriber.getAccount(anyString())).thenReturn(null);
		when(proPaySubscriber.makeDeposit(any(Request.class))).thenCallRealMethod();

		boolean check =
				proPaySubscriber.checkDeposit(20, "check");
		assertEquals(false, check);
	}

	@Test
	public void checkDepositAmountInsufficient() {
		ProPaySubscriber proPaySubscriber = mock(ProPaySubscriber.class);
		ProPayAccount proPayAccount = mock(ProPayAccount.class);
		when(proPaySubscriber.getAccount(anyString())).thenReturn(null);
		when(proPayAccount.getAmount()).thenReturn(-1.2);
		when(proPaySubscriber.checkDeposit(anyDouble(), anyString())).thenCallRealMethod();

		boolean check =
				proPaySubscriber.checkDeposit(20, "check");
		assertEquals(false, check);
	}

	@Test
	public void checkDepositAmountSufficient() {
		ProPaySubscriber proPaySubscriber = mock(ProPaySubscriber.class);
		ProPayAccount proPayAccount = mock(ProPayAccount.class);
		when(proPaySubscriber.getAccount(anyString())).thenReturn(proPayAccount1);
		when(proPayAccount.getAmount()).thenReturn(1.1);
		when(proPaySubscriber.checkDeposit(anyDouble(), anyString())).thenCallRealMethod();

		boolean check =
				proPaySubscriber.checkDeposit(20, "check");
		assertEquals(true, check);
	}

	@Test
	public void transferMoneyEverythingRight() {
		ProPaySubscriber proPaySubscriber = mock(ProPaySubscriber.class);
		when(proPaySubscriber.executeTransfer(anyString(), anyString(), anyDouble())).thenReturn(true);
		when(proPaySubscriber.transferMoney(anyString(), anyString(), anyDouble())).thenCallRealMethod();

		boolean check =
				proPaySubscriber.transferMoney("checkov", "check", 2.1);
		assertEquals(true, check);
	}

	@Test
	public void transferMoneySomethingWrong() {
		ProPaySubscriber proPaySubscriber = mock(ProPaySubscriber.class);
		when(proPaySubscriber.executeTransfer(anyString(), anyString(), anyDouble())).thenReturn(false);
		when(proPaySubscriber.transferMoney(anyString(), anyString(), anyDouble())).thenCallRealMethod();

		boolean check =
				proPaySubscriber.transferMoney("checkov", "check", 2.1);
		assertEquals(false, check);
	}
/*
        @Test
        public void checkGetAccount(){
            ProPaySubscriber proPaySubscriber = mock(ProPaySubscriber.class);
            when(proPaySubscriber.getAccount(dummy1.getUsername())).thenReturn(proPayAccount1);

            ProPayAccount account = proPaySubscriber.getAccount("iamanoriginalname");
            Reservation[] reservations = account.getReservations();

            assertEquals("iamanoriginalname", account.getAccount());
            assertEquals(30, account.getAmount(), 0.001);
            assertEquals(7, reservations[0].getId());
            assertEquals(15, reservations[0].getAmount(), 0.001);
            assertEquals(8, reservations[1].getId());
            assertEquals(25, reservations[1].getAmount(), 0.001);
        }

        @Test
        public void makeDepositAndReservationCallCorrect(){
            ProPaySubscriber proPaySubscriber = mock(ProPaySubscriber.class);
            when(proPaySubscriber.makeReservation(anyString(), anyString(), anyDouble())).thenReturn(r1);

            Reservation reservation =
                    proPaySubscriber.makeReservation(
                            request.getRequester().getUsername(),
                            request.getRequestedItem().getOwner().getUsername(),
                            request.getRequestedItem().getDeposit());
            assertEquals(7, reservation.getId());
            assertEquals(15.0, reservation.getAmount(), 0.001);
        }



        @Test
        public void releaseReservationCallCorrect(){
            ProPaySubscriber proPaySubscriber = mock(ProPaySubscriber.class);
            when(proPaySubscriber.releaseReservation(anyString(), anyInt())).thenReturn(proPayAccount2);

            ProPayAccount account =
                    proPaySubscriber.releaseReservation(dummy1.getUsername(), 8);
            assertEquals(1, account.getReservations().length);
        }*/


}