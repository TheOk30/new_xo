package com.example;

import com.example.models.Stats;

public interface gameInterface {
    public void refreshGrid();

    public void disableGrid();

    public void enableGrid();

    public void win(Stats stats, String username);

    public void lose(Stats stats, String username);

    public void draw(Stats stats, String username);

    public void refresh();
}
