package com.company.menu;

public class MenuItem {
    private final String title;
    private final String key;

    public MenuItem(String title, String key) {
        this.title = title;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public String getKey() {
        return key;
    }
}
