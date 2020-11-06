package com.sziffer.series;

import oracle.sql.CHAR;
import oracle.sql.DATE;
import oracle.sql.NUMBER;

import java.sql.Date;

public class SeriesItem {

    private String name;
    private int id;
    private String description;
    private Date nextEpisode;

    public SeriesItem(int id, String name, String description, Date nextEpisode) {
        this.name = name;
        this.id = id;
        this.description = description;
        this.nextEpisode = nextEpisode;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public Date getNextEpisode() {
        return nextEpisode;
    }

    @Override
    public String toString() {
        return "SeriesItem{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", description='" + description + '\'' +
                ", nextEpisode=" + nextEpisode +
                '}';
    }
}
