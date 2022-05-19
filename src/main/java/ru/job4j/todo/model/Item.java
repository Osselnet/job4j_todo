package ru.job4j.todo.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "items")
public class Item implements Comparable<Item> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private LocalDateTime created = LocalDateTime.now().withNano(0);
    private LocalDateTime done;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Transient
    private final DateTimeFormatter
            formatter = DateTimeFormatter.ofPattern("dd-MMMM-EEEE-yyyy HH:mm:ss");
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private Set<Category> categories = new HashSet<>();

    public Item() {
    }

    public Item(int id) {
        this.id = id;
    }

    public Item(String name) {
        this.name = name;
    }

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Item(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public String getCreatedFormat() {
        return formatter.format(created);
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getDone() {
        return done;
    }

    public String getDoneFormat() {
        return formatter.format(done);
    }

    public void setDone(LocalDateTime done) {
        this.done = done;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("id: %s, name: %s, description: %s, user: %s, create: %s, done %s",
                id, name, description, user.getName(), formatter.format(created), formatter.format(done));
    }

    @Override
    public int compareTo(Item item) {
        return Integer.compare(id, item.id);
    }
}