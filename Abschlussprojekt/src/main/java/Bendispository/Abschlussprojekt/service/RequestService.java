package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static Bendispository.Abschlussprojekt.model.RequestStatus.PENDING;

@Component
public class RequestService {

    PersonsRepo personsRepo;

    RequestRepo requestRepo;

    @Autowired
    public RequestService(PersonsRepo personsRepo, RequestRepo requestRepo){
        this.personsRepo = personsRepo;
        this.requestRepo = requestRepo;
    }

    public void showRequests(Model model,
                              Long id) {
        Person me = personsRepo.findById(id).orElse(null);
        List<Request> myRequests = requestRepo.findByRequesterAndStatus(me, PENDING);
        deleteObsoleteRequests(myRequests);
        model.addAttribute("myRequests", myRequests);
        List<Request> requestsMyItems = requestRepo.findByRequestedItemOwnerAndStatus(me, PENDING);
        deleteObsoleteRequests(requestsMyItems);
        model.addAttribute("requestsMyItems", requestsMyItems);
    }

    private void deleteObsoleteRequests(List<Request> myRequests) {
        List<Request> toRemove = new ArrayList<>();
        for(Request request : myRequests){
            if(request.getStartDate().isBefore(LocalDate.now())) {
                requestRepo.delete(request);
                toRemove.add(request);
            }
        }
        myRequests.removeAll(toRemove);
    }
}
