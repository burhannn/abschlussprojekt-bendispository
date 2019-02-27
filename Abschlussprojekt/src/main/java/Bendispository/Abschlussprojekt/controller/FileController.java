package Bendispository.Abschlussprojekt.controller;

import Bendispository.Abschlussprojekt.model.Item;
import Bendispository.Abschlussprojekt.model.Person;
import Bendispository.Abschlussprojekt.model.Request;
import Bendispository.Abschlussprojekt.model.RequestStatus;
import Bendispository.Abschlussprojekt.model.transactionModels.MarketType;
import Bendispository.Abschlussprojekt.repos.ItemRepo;
import Bendispository.Abschlussprojekt.repos.PersonsRepo;
import Bendispository.Abschlussprojekt.repos.RequestRepo;
import Bendispository.Abschlussprojekt.service.AuthenticationService;
import Bendispository.Abschlussprojekt.service.ItemService;
import Bendispository.Abschlussprojekt.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;


@Controller
public class FileController {

	private ItemRepo itemRepo;
	private PersonsRepo personRepo;
	private RequestRepo requestRepo;
	private AuthenticationService authenticationService;
	private ItemService itemService;
	private RequestService requestService;

	@Autowired
	public FileController(ItemRepo itemRepo,
						  PersonsRepo personRepo,
						  RequestRepo requestRepo,
						  AuthenticationService authenticationService,
						  ItemService itemService,
						  RequestService requestService) {
		this.itemRepo = itemRepo;
		this.personRepo = personRepo;
		this.requestRepo = requestRepo;
		this.authenticationService = authenticationService;
		this.itemService = itemService;
		this.requestService = requestService;
	}

	@GetMapping(path = "/additem")
	public String addItemPage() {
		return "itemTmpl/AddItem";
	}

	@PostMapping(path = "/additem", consumes = {"multipart/form-data"})
	public String addItemsToDatabase(Model model,
									 @Valid @RequestParam("file") MultipartFile multipartFile,
									 Item item) throws IOException {

		itemService.addFile(item, multipartFile);

		model.addAttribute("newItem", item);
		itemService.addItem(item, MarketType.LEND);

		return "redirect:/item/" + item.getId() + "";
	}

	@GetMapping(path = "/addsellitem")
	public String addSellItemPage() {
		return "itemTmpl/addItemSell";
	}

	@PostMapping(path = "/addsellitem", consumes = {"multipart/form-data"})
	public String addSellItemToDatabase(Model model,
										@Valid @RequestParam("file") MultipartFile multipartFile,
										Item item) throws IOException {

		itemService.addFile(item, multipartFile);

		model.addAttribute("newItem", item);
		itemService.addItem(item, MarketType.SELL);

		return "redirect:/item/" + item.getId() + "";
	}

	@GetMapping(path = "/item/{id}")
	public String ItemProfile(Model model,
							  @PathVariable Long id) {
		if (itemRepo.findById(id).orElse(null) == null) {
			return "redirect:/";
		}

		Item item = itemRepo.findById(id).orElse(null);
		model.addAttribute("itemProfile", item);
		model.addAttribute("itemOwner", item.getOwner());
		model.addAttribute("loggedInPerson", authenticationService.getCurrentUser());

		if (item.getUploadFile() != null) {
			model.addAttribute("pic", Base64.getEncoder().encodeToString((item.getUploadFile().getData())));
		} else {
			model.addAttribute("pic", null);
		}

		if (item.getMarketType() == MarketType.SELL)
			return "itemTmpl/itemProfileSell";

		return "itemTmpl/itemProfile";
	}

	@PostMapping(path = "/item/{id}")
	public String itemBuyRequest(Model model,
								 RedirectAttributes redirectAttributes,
								 @PathVariable Long id) {

		Item item = itemRepo.findById(id).orElse(null);
		List<Request> requests = requestRepo.findByRequesterAndRequestedItemAndStatus
				(authenticationService.getCurrentUser(), item, RequestStatus.AWAITING_SHIPMENT);

		if (!(requests.isEmpty())) {
			redirectAttributes.addFlashAttribute("message",
					"You cannot buy the same item twice!");
			return "redirect:/item/{id}";
		}

		Request request = requestService.addBuyRequest(id);
		if (request == null) {
			redirectAttributes.addFlashAttribute("message", "You don't have enough funds for this transaction!");
			return "redirect:/item/{id}";
		}

		boolean isItemBought = requestService.buyItemAndTransferMoney(request);

		if (!isItemBought) {
			redirectAttributes.addFlashAttribute("messageBalance",
					"There is a Problem with ProPay!");
			return "redirect:/item/{id}";
		}

		itemRepo.findById(id).ifPresent(o -> model.addAttribute("thisItem", o));
		redirectAttributes.addFlashAttribute("success", "Item bought!");

		return "redirect:/item/{id}";
	}

	@RequestMapping(method = RequestMethod.GET, value = "/deleteitem/{id}")
	public String deleteItem(@PathVariable("id") Long id,
							 RedirectAttributes redirectAttributes) {
		itemService.deleteItem(id);
		redirectAttributes.addFlashAttribute("message", "Item has been deleted!");
		return "redirect:/";
	}

	@GetMapping(path = "/edititem/{id}")
	public String editItem(Model model,
						   @PathVariable Long id) {
		if (itemRepo.findById(id).orElse(null) == null) {
			return "redirect:/";
		}
		Item item = itemRepo.findById(id).orElse(null);
		Person loggedIn = authenticationService.getCurrentUser();
		model.addAttribute("Item", item);
		if (loggedIn.getUsername().equals(item.getOwner().getUsername())) {
			if (item.getMarketType().equals(MarketType.SELL)) {
				return "itemTmpl/editItemSell";
			}
			return "itemTmpl/editItem";
		}

		return "redirect:/";
	}

	@PostMapping(path = "/edititem/{id}")
	public String editItemInDatabase(Model model,
									 @PathVariable Long id,
									 Item inputItem) {
		Optional<Item> item = itemRepo.findById(id);
		model.addAttribute("Item", item.get());
		itemService.editItem(inputItem, item, id);
		return "redirect:/item/{id}";
	}
}
