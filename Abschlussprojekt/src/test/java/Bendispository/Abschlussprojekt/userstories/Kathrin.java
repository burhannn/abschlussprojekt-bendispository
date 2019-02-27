package Bendispository.Abschlussprojekt.userstories;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Rating;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
public class Kathrin {

	@MockBean
	ProPaySubscriber proPaySubscriber;

	@Test
	public void kathrinsLife() {
		//user kathrin mit geld erstellen
		Person kathrin = new Person();
		List<Rating> ratings = new ArrayList<>();
		mkPerson("kathrin@kathrin.de", "kathrin", "Kathrin", "Gottlieb", "Wuppertal", "abcdabcd", ratings);

		proPaySubscriber.chargeAccount(kathrin.getUsername(), 500.0);

		//hacksler product mit besitzer erstellen


		//leih anfrage wird angenommen
		//assert that amount is reserved on kathrins account
		//geliehenes produkt wird zurueck gegeben
		//besitzer findet es in guten zustand
		//assert that reservation is released and the lending cost is paid
	}

	private Person mkPerson(String email, String username, String firstName, String lastName, String city, String password, List<Rating> ratings) {
		Person p = new Person();
		System.out.println();
		p.setEmail(email);
		p.setUsername(username);
		p.setFirstName(firstName);
		p.setLastName(lastName);
		p.setCity(city);
		p.setPassword(password);
		p.setRatings(ratings);
		return p;
	}

	private Item mkItem(int cost, int deposit, String desc, String name, Person person, String place) {
		System.out.println();
		Item item = new Item();
		item.setCostPerDay(cost);
		item.setDeposit(deposit);
		item.setDescription(desc);
		item.setName(name);
		item.setOwner(person);
		item.setPlace(place);
		return item;
	}

	private void PersonAddItem(Person person, Item... items) {
		List<Item> item = new ArrayList<Item>();
		item.addAll(Arrays.asList(items));
		person.setItems(item);
	}
}
