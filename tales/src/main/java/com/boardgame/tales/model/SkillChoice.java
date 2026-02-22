package com.boardgame.tales.model;

import lombok.Data;

@Data
public class SkillChoice {
    private Integer targetEventId;
    private Integer skillId;
    private String skillName;
}
