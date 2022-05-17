package ru.job4j.todo.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Item;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.ItemService;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class ItemController {
    private static final String ALL_ITEM = "Все задания";
    private static final String COMPLETED_ITEM = "Завершенные задания";
    private static final String NEW_ITEM = "Новые задания";
    private final ItemService itemsService;

    public ItemController(ItemService itemsService) {
        this.itemsService = itemsService;
    }

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "statusSuccess", required = false) Boolean statusSuccess,
                        @RequestParam(name = "statusErr", required = false) Boolean statusErr,
                        HttpSession session) {
        User user = getUserSession(session);
        model.addAttribute("user", user);
        model.addAttribute("statusSuccess", statusSuccess != null);
        model.addAttribute("statusErr", statusErr != null);
        model.addAttribute("pageName", ALL_ITEM);
        model.addAttribute("items", itemsService.findAllItem(user));
        return "index";
    }

    @GetMapping("/doneItems")
    public String doneItems(Model model, HttpSession session) {
        User user = getUserSession(session);
        model.addAttribute("user", user);
        model.addAttribute("pageName", COMPLETED_ITEM);
        model.addAttribute("items", itemsService.findDoneItem(user));
        return "index";
    }

    @GetMapping("/newItems")
    public String newItems(Model model, HttpSession session) {
        User user = getUserSession(session);
        model.addAttribute("user", user);
        model.addAttribute("pageName", NEW_ITEM);
        model.addAttribute("items", itemsService.findNewItem(user));
        return "index";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") int id,
                         @RequestParam(name = "statusSuccess", required = false) Boolean statusSuccess,
                         @RequestParam(name = "statusErr", required = false) Boolean statusErr,
                         HttpSession session) {
        Optional<Item> item = itemsService.findByIdItem(id);
        User user = getUserSession(session);
        if (item.isPresent() && user.equals(item.get().getUser())) {
            model.addAttribute("user", user);
            model.addAttribute("statusSuccess", statusSuccess != null);
            model.addAttribute("statusErr", statusErr != null);
            model.addAttribute("item", item.get());
            return "detail";
        }
        return "redirect:/?statusErr=true";
    }

     @GetMapping("/new")
    public String addItem(Model model,
                          HttpSession session) {
        model.addAttribute("user", getUserSession(session));
        return "new";
    }

    @PostMapping("/createItem")
    public String createItem(@ModelAttribute("item") Item item,
                             HttpSession session) {
        item.setUser((User) session.getAttribute("user"));
        itemsService.add(item);
        return "redirect:/detail/" + item.getId() + "?statusSuccess=true";
    }

    @GetMapping("/edit")
    public String edit(Model model,
                       @ModelAttribute("item") Item item,
                       HttpSession session) {
        User user = getUserSession(session);
        Optional<Item> findItem = itemsService.findByIdItem(item.getId());
        if (findItem.isPresent() && user.equals(findItem.get().getUser())) {
            model.addAttribute("user", user);
            model.addAttribute("item", findItem.get());
            return "edit";
        }
        return "redirect:/?statusErr=true";
    }

    @PostMapping("/editItem")
    public String editItem(@ModelAttribute("item") Item item) {
        if (!itemsService.updateItem(item.getId(), item)) {
            return "redirect:/?statusErr=true";
        }
        return "redirect:/detail/" + item.getId() + "?statusSuccess=true";
    }

    @PostMapping("/doneItem")
    public String doneItem(@ModelAttribute("id") int id) {
        if (!itemsService.doneItem(id)) {
            return "redirect:/detail/" + id + "?statusErr=true";
        }
        return "redirect:/detail/" + id + "?statusSuccess=true";
    }

    @PostMapping("/deleteItem")
    public String deleteItem(@ModelAttribute("id") int id) {
        if (!itemsService.deleteItem(id)) {
            return "redirect:/detail/" + id + "?statusErr=true";
        }
        return "redirect:/?statusSuccess=true";
    }

    private User getUserSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = User.of("Гость", "");
            user.setId(-1);
        }
        return user;
    }
}