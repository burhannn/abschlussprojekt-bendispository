package Bendispository.Abschlussprojekt.DatabaseInitializer;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.repo.ItemsList;
import Bendispository.Abschlussprojekt.repo.PersonsList;
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
    ItemsList itemRepo;

    @Autowired
    PersonsList personRepo;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {

        Person dummy_1 = mkPerson(300000, "momo@gmail.com", "mandypandy");
        Person dummy_2 = mkPerson(12345, "mimi@gmail.com", "pandycandy");

        Item dummyItem1 = mkItem(12, 300, "Ich bin ein stuhl", "stuhl", dummy_1);
        Item dummyItem2 = mkItem(44, 213123, "ich bin teuer", "playstation" , dummy_1);
        Item dummyItem3 = mkItem(1, 12, "ich bin billig", "stift", dummy_2);

        itemRepo.saveAll(Arrays.asList(dummyItem1, dummyItem2, dummyItem3));

        addItem(dummy_1, dummyItem1, dummyItem2);
        addItem(dummy_2, dummyItem3);

        personRepo.saveAll(Arrays.asList(dummy_1, dummy_2));

    }

    private Person mkPerson(int account, String email, String username){
        Person p = new Person();
        p.setAccount(account);
        p.setEmail(email);
        p.setUsername(username);
        return p;
    }

    private Item mkItem(int cost, int deposit, String desc, String name, Person person){
        Item item = new Item();
        item.setCostPerDay(cost);
        item.setDeposit(deposit);
        item.setDescription(desc);
        item.setFree(true);
        item.setName(name);
        //item.setOwner(person);
        return item;
    }

    private void addItem(Person person, Item... items){
        List<Item> item = new ArrayList<Item>();
        item.addAll(Arrays.asList(items));
        //person.setItems(item);
    }
}
