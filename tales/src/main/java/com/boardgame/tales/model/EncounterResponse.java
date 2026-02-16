package com.boardgame.tales.model;

import lombok.Data;
import java.util.List;

@Data
public class EncounterResponse {
    private String reactionLetter;
    private List<Action> availableActions;
    private EncounterLookup lookup; // Optional: include full lookup details if needed
}
