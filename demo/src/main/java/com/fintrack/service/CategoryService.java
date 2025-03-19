package com.fintrack.service;

import com.fintrack.type.Category;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    public List<Category> getAllCategories() {
        return List.of(Category.values());
    }

    public Optional<Category> getCategoryByName(String name) {
        return Optional.ofNullable(Category.valueOf(name.toUpperCase()));
    }

    public Category createCategory(String name) {
        throw new UnsupportedOperationException("Cannot create new categories for enums");
    }

    public Optional<Category> updateCategory(String oldName, String newName) {
        throw new UnsupportedOperationException("Cannot update existing enum values");
    }

    public boolean deleteCategory(String name) {
        throw new UnsupportedOperationException("Cannot delete enum values");
    }
}
