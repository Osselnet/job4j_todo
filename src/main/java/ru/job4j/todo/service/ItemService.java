package ru.job4j.todo.service;

import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Item;
import ru.job4j.todo.persistence.Store;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ItemService {
    private final Store<Item> store;

    public ItemService(Store<Item> store) {
        this.store = store;
    }

    public Optional<Item> add(Item item) {
        return store.create(item);
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

    public List<Item> findAllItem() {
        return store.findAll();
    }

    public List<Item> findNewItem() {
        return store.findNew();
    }

    public List<Item> findCompletedItem() {
        return store.findCompleted();
    }
}