package com.boardgame.tales.model;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class StoryOutcome {
    private Integer id;
    private Integer eventId;
    private Integer parentId;
    private String conditionParam;
    private String outcomeText;
    private String rewardStr;

    // Transient field for hierarchy
    private List<StoryOutcome> children = new ArrayList<>();
}
