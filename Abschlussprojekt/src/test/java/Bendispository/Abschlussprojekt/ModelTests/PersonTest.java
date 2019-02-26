package Bendispository.Abschlussprojekt.ModelTests;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Rating;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
public class PersonTest {

    @Test
    public void getAverageNoRating(){
        List<Rating> ratings = new ArrayList<>();
        Person dum1 = new Person();
        dum1.setRatings(ratings);

        Assert.assertEquals(-1,dum1.getAverageRatings());
    }

    @Test
    public void getAverageOneRating(){
        List<Rating> ratings = new ArrayList<>();
        Person dum1 = new Person();
        dum1.setRatings(ratings);

        Rating rating1 = new Rating();
        rating1.setRatingPoints(1);

        dum1.addRating(rating1);

        Assert.assertEquals(1,dum1.getAverageRatings());
    }

    @Test
    public void getAverageMultipleRatings(){
        List<Rating> ratings = new ArrayList<>();
        Person dum1 = new Person();
        dum1.setRatings(ratings);

        Rating rating1 = new Rating();
        rating1.setRatingPoints(2);
        dum1.addRating(rating1);

        Rating rating2 = new Rating();
        rating2.setRatingPoints(5);
        dum1.addRating(rating2);

        Rating rating3 = new Rating();
        rating3.setRatingPoints(1);
        dum1.addRating(rating3);

        Assert.assertEquals(3,dum1.getAverageRatings());
    }

}
