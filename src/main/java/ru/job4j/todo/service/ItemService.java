package ru.job4j.todo.service;

import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Item;
import ru.job4j.todo.model.User;
import ru.job4j.todo.persistence.ItemDBStore;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    private final ItemDBStore store;

    public ItemService(ItemDBStore store) {
        this.store = store;
    }

    public boolean add(Item item, List<String> idCategory) {
        return store.create(item, idCategory);
    }

    public Optional<Item> findByIdItem(int id) {
        return store.findById(id);
    }

    public boolean updateItem(int id, Item item) {
        return store.update(id, item);
    }

    public boolean doneItem(int id) {
        boolean result = false;
        Optional<Item> item = store.findById(id);
        if (item.isPresent()) {
            item.get().setDone(LocalDateTime.now().withNano(0));
            store.update(item.get().getId(), item.get());
            result = true;
        }
        return result;
    }

    public boolean deleteItem(int id) {
        return store.delete(id);
    }

    public List<Item> findAllItem(final User user) {
        return store.findAll(user);
    }

    public List<Item> findNewItem(final User user) {
        return store.findNew(user);
    }

    public List<Item> findCompletedItem() {
        return store.findCompleted();
    }

    public List<Item> findDoneItem(final User user) {
        return store.findDone(user);
    }
}