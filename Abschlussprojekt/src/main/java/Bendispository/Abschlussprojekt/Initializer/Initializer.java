package Bendispository.Abschlussprojekt.Initializer;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Rating;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import Bendispository.Abschlussprojekt.service.ProPaySubscriber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class Initializer implements ServletContextInitializer {

    @Autowired
    ItemRepo itemRepo;

    @Autowired
    PersonsRepo personRepo;

    @Autowired
    LeaseTransactionRepo leaseTransactionRepo;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        List<Rating> Ratings = new ArrayList<>();

        Person dummy_1 = mkPerson(300000, "momo@gmail.com", "mandypandy", "mandy", "pandy", "Köln","abcd", Ratings);
        Person dummy_2 = mkPerson(12345, "mimi@gmail.com", "pandycandy", "pandy", "candy", "Düsseldorf", "abcd", Ratings);

        Item dummyItem1 = mkItem(12, 5, "Ich bin ein stuhl", "stuhl", dummy_1);
        Item dummyItem2 = mkItem(44, 213123, "ich bin teuer", "playstation" , dummy_1);
        Item dummyItem3 = mkItem(1, 12, "ich bin billig", "stift", dummy_2);

        itemRepo.saveAll(Arrays.asList(dummyItem1, dummyItem2, dummyItem3));

        PersonAddItem(dummy_1, dummyItem1, dummyItem2);
        PersonAddItem(dummy_2, dummyItem3);

        personRepo.saveAll(Arrays.asList(dummy_1, dummy_2));

        ProPaySubscriber proPaySubscriber = new ProPaySubscriber(personRepo, leaseTransactionRepo);
        proPaySubscriber.chargeAccount(dummy_1.getUsername(), 5000.0);
        proPaySubscriber.chargeAccount(dummy_1.getUsername(), 5000.0);

    }

    private Person mkPerson(int account, String email, String username, String firstName, String lastName, String city, String password, List<Rating> Ratings){
        Person p = new Person();
        p.setEmail(email);
        p.setUsername(username);
        p.setFirstName(firstName);
        p.setLastName(lastName);
        p.setCity(city);
        p.setPassword(password);
        p.setRatings(Ratings);
        return p;
    }

    private Item mkItem(int cost, int deposit, String desc, String name, Person person){
        Item item = new Item();
        item.setCostPerDay(cost);
        item.setDeposit(deposit);
        item.setDescription(desc);
        item.setName(name);
        item.setOwner(person);
        return item;
    }

    private void PersonAddItem(Person person, Item... items){
        List<Item> item = new ArrayList<Item>();
        item.addAll(Arrays.asList(items));
        person.setItems(item);
    }
}
