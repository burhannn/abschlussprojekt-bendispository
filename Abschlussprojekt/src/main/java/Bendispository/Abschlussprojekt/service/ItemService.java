package Bendispository.Abschlussprojekt.service;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.UploadFile;
import Bendispository.Abschlussprojekt.model.transactionModels.LeaseTransaction;
import Bendispository.Abschlussprojekt.model.transactionModels.MarketType;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.transactionRepos.LeaseTransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ItemService {

    private final ItemRepo itemRepo;

    private final PersonsRepo personRepo;

    private final AuthenticationService authenticationService;

    private Clock clock;

    @Autowired
    public ItemService(ItemRepo itemRepo, PersonsRepo personsRepo, AuthenticationService authenticationService, Clock clock){
        this.itemRepo = itemRepo;
        this.personRepo = personsRepo;
        this.authenticationService = authenticationService;
        this.clock = clock;
    }

    public void addItem(Item item, MarketType marketType){
        Person loggedIn = authenticationService.getCurrentUser();
        item.setOwner(personRepo.findByUsername(loggedIn.getUsername()));
        item.setMarketType(marketType);
        itemRepo.save(item);
        List<Item> itemsOwner = new ArrayList<>();
        itemsOwner.addAll(itemRepo.findByOwner(loggedIn));
        loggedIn.setItems(itemsOwner);
        personRepo.save(loggedIn);
    }

    public void deleteItem(Long id){
        Item item = itemRepo.findById(id).orElse(null);
        Person person = item.getOwner();
        item.setOwner(null);
        person.deleteItem(item);
        itemRepo.deleteById(id);
        personRepo.save(person);
    }

    public void editItem(Item inputItem, Optional<Item> item, Long id){
        Person loggedIn = authenticationService.getCurrentUser();
        inputItem.setOwner(personRepo.findByUsername(loggedIn.getUsername()));
        List<Item> itemsOwner = itemRepo.findByOwner(loggedIn);
        loggedIn.setItems(itemsOwner);
        inputItem.setMarketType(item.get().getMarketType());
        if(item.get().getUploadFile() != null) {
            inputItem.setUploadFile(item.get().getUploadFile());
        }
        itemRepo.save(inputItem);
    }

    public void addFile(Item item,
                        @Valid @RequestParam("file") MultipartFile multipartFile) throws IOException {

        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        if(!fileName.isEmpty()){
            UploadFile uploadFile = new UploadFile(fileName, multipartFile.getBytes());
            item.setUploadFile(uploadFile);
        }
    }

    /*public boolean itemIsAvailable(Long id){
        List<LeaseTransaction> leaseTransactions = leaseTransactionRepo.findAllByItemId(id);
        LocalDate now = LocalDate.now(clock);
        for (LeaseTransaction lease : leaseTransactions)
            if (TransactionService.isOverlapping(now, now, lease.getStartDate(), lease.getEndDate()))
                return false;
        return true;
    }*/

}
