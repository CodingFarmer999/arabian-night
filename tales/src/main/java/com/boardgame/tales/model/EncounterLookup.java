package com.boardgame.tales.model;

import lombok.Data;

@Data
public class EncounterLookup {
    private Integer encounterId;
    private Integer dieRoll;
    private String fullText;
    private Integer adjId;
    private String reactionLetter;
}
