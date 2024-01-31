// src/main/java/org/example/Product.java

package org.example;

public class Product {
    private String id;
    private String name;
    private String description;
    private Category category;

    // Constructors, getters, and setters...

    public Product(String id, String name, String description, Category category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.category = category;
    }

    public Product(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    // Getters and setters...
}
