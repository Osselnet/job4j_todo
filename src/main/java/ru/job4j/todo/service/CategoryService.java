package ru.job4j.todo.service;

import org.springframework.stereotype.Service;
import ru.job4j.todo.model.Category;
import ru.job4j.todo.persistence.CategoryDBStore;

import java.util.List;

@Service
public class CategoryService {
    private final CategoryDBStore story;

    public CategoryService(CategoryDBStore story) {
        this.story = story;
    }

    public List<Category> allCategory() {
        return story.findAll();
    }
}

