package com.boardgame.tales.model;

import lombok.Data;
import java.util.List;

@Data
public class Event {
    private Integer id;
    private String type;
    private String title;
    private String description;

    // Transient field to hold outcomes
    private List<StoryOutcome> outcomes;
}
