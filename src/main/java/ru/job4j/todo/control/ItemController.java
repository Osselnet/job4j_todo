package ru.job4j.todo.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.job4j.todo.model.Item;
import ru.job4j.todo.service.ItemService;

import java.util.Optional;

@Controller
public class ItemController {
    private static final String ALL_ITEM = "Все задания";
    private static final String COMPLETED_ITEM = "Завершенные задания";
    private static final String NEW_ITEM = "Новые задания";
    private final ItemService service;

    public ItemController(ItemService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model,
                        @RequestParam(name = "statusSuccess", required = false) Boolean statusSuccess,
                        @RequestParam(name = "statusErr", required = false) Boolean statusErr) {
        model.addAttribute("statusSuccess", statusSuccess != null);
        model.addAttribute("statusErr", statusErr != null);
        model.addAttribute("pageName", ALL_ITEM);
        model.addAttribute("items", service.findAllItem());
        return "index";
    }

    @GetMapping("/doneItems")
    public String doneItems(Model model) {
        model.addAttribute("pageName", COMPLETED_ITEM);
        model.addAttribute("items", service.findCompletedItem());
        return "index";
    }

    @GetMapping("/newItem")
    public String newItem(Model model) {
        model.addAttribute("pageName", NEW_ITEM);
        model.addAttribute("items", service.findNewItem());
        return "index";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") int id,
                         @RequestParam(name = "statusSuccess", required = false) Boolean statusSuccess,
                         @RequestParam(name = "statusErr", required = false) Boolean statusErr) {
        model.addAttribute("statusSuccess", statusSuccess != null);
        model.addAttribute("statusErr", statusErr != null);
        Optional<Item> item = service.findByIdItem(id);
        if (item.isEmpty()) {
            return "/?statusErr=true";
        }
        model.addAttribute("item", item.get());
        return "detail";
    }

    @GetMapping("/new")
    public String addItem(@ModelAttribute("item") Item item) {
        return "new";
    }

    @PostMapping("/createItem")
    public String createItem(@ModelAttribute("item") Item item) {
        service.add(item);
        return "redirect:/detail/" + item.getId() + "?statusSuccess=true";
    }

    @GetMapping("/edit")
    public String edit(Model model, @ModelAttribute("item") Item item) {
        model.addAttribute("item", service.findByIdItem(item.getId()).get());
        return "edit";
    }

    @PostMapping("/editItem")
    public String editItem(@ModelAttribute("item") Item item) {
        if (!service.updateItem(item.getId(), item)) {
            return "redirect:/detail/" + item.getId() + "?statusErr=true";
        }
        return "redirect:/detail/" + item.getId() + "?statusSuccess=true";
    }

    @PostMapping("/doneItem")
    public String doneItem(@ModelAttribute("id") int id) {
        if (!service.doneItem(id)) {
            return "redirect:/detail/" + id + "?statusErr=true";
        }
        return "redirect:/detail/" + id + "?statusSuccess=true";
    }

    @PostMapping("/deleteItem")
    public String deleteItem(@ModelAttribute("id") int id) {
        if (!service.deleteItem(id)) {
            return "redirect:/detail/" + id + "?statusErr=true";
        }
        return "redirect:/?statusSuccess=true";
    }
}