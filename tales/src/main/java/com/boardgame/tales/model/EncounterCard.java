package com.boardgame.tales.model;

import lombok.Data;

@Data
public class EncounterCard {
    private Integer id;
    private String name;
    private String type;
    private Integer adjId;
    private Integer encLocation;
    private Integer encMorning;
    private Integer encNoon;
    private Integer encNight;
    private String encMountain;
    private String encDesert;
    private String encOcean;
    private String encForest;
    private String encCity;
    private String encIsland;
}
