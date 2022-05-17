package ru.job4j.todo.control;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.job4j.todo.model.User;
import ru.job4j.todo.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class UserController {
    private static final String IN = "Авторизация";
    private static final String REG = "Регистрация";
    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/login")
    public String login(Model model,
                        @RequestParam(value = "userErr", required = false) Boolean userErr,
                        HttpSession session) {
        model.addAttribute("user", getUserSession(session));
        model.addAttribute("userErr", userErr != null);
        return "login";
    }

    @PostMapping("/login")
    public String loginPost(@ModelAttribute User user,
                            HttpServletRequest req) {
        Optional<User> userDb = service.findByNamePassword(user.getName(), user.getPassword());
        if (userDb.isEmpty()) {
            return "redirect:/login?userErr=true";
        }
        HttpSession session = req.getSession();
        session.setAttribute("user", userDb.get());
        return "redirect:/";
    }

    @GetMapping("/newUser")
    public String createUser(Model model,
                             @RequestParam(value = "userErr", required = false) Boolean userErr,
                             HttpSession session) {
        model.addAttribute("user", getUserSession(session));
        model.addAttribute("userErr", userErr != null);
        return "newUser";
    }

    @PostMapping("newUser")
    public String createUserPost(@ModelAttribute("user") User user, HttpServletRequest req) {
        if (!service.create(user)) {
            return "redirect:/newUser?userErr=true";
        }
        HttpSession session = req.getSession();
        session.setAttribute("user", user);
        return "redirect:/";
    }

    @GetMapping("/editUser")
    public String editUser(Model model,
                           @RequestParam(value = "userErr", required = false) Boolean userErr,
                           HttpSession session) {
        User user = getUserSession(session);
        model.addAttribute("userErr", userErr != null);
        model.addAttribute("user", user);
        model.addAttribute("editUser", user);
        return "editUser";
    }

    @PostMapping("/editUser")
    public String editUserPost(@ModelAttribute("editUser") User editUser,
                               HttpServletRequest req) {
        if (!service.updateUser(editUser.getId(), editUser)) {
            return "redirect:/editUser/?userErr=true";
        }
        HttpSession session = req.getSession();
        session.setAttribute("user", editUser);
        return "redirect:/?statusSuccess=true";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private User getUserSession(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = User.of("Гость", "");
        }
        return user;
    }
}