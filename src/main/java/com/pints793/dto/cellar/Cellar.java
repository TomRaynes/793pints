package com.pints793.dto.cellar;

import java.util.ArrayList;
import java.util.List;

public class Cellar {

    private String name;
    private final List<Cask> casks;

    public Cellar() {
        casks = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Cellar setName(String name) {
        this.name = name;
        return this;
    }

    public List<Cask> getCasks() {
        return casks;
    }

    public Cellar addCask(Cask cask) {
        casks.add(cask);
        return this;
    }
}
