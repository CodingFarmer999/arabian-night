package com.boardgame.tales.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.boardgame.tales.mapper.TalesMapper;
import com.boardgame.tales.model.Action;
import com.boardgame.tales.model.EncounterCard;
import com.boardgame.tales.model.EncounterLookup;
import com.boardgame.tales.model.EncounterResponse;
import com.boardgame.tales.model.Event;
import com.boardgame.tales.model.StoryOutcome;

@Service
public class TalesService {

    Logger logger = LoggerFactory.getLogger(TalesService.class);

    private final TalesMapper talesMapper;

    public TalesService(TalesMapper talesMapper) {
        this.talesMapper = talesMapper;
    }

    /**
     * 取得所有特殊遭遇卡列表 (用於下拉選單)
     */
    @Cacheable("specialEncounters")
    public List<EncounterCard> getSpecialEncounters() {
        return talesMapper.findAllSpecialCards();
    }

    /**
     * 處理遭遇邏輯：
     * 1. 若有選擇特殊遭遇 ID，則優先處理 (強制反應矩陣為 'N')。
     * 2. 否則根據 遭遇編號 (Encounter ID) + 骰子點數 (Die Roll) 查詢基礎列表。
     * 3. 回傳包含 反應矩陣字母、形容詞ID、可選行動列表 的回應物件。
     */
    public EncounterResponse processEncounter(Integer encounterId, Integer dieRoll, Integer specialEncounterId) {
        EncounterResponse response = new EncounterResponse();

        // 0. 處理特殊遭遇 (Handle Special Encounter)
        if (specialEncounterId != null) {
            EncounterCard card = talesMapper.findCardById(specialEncounterId);
            if (card != null) {
                // If Special, Reaction Letter is 'N'
                response.setReactionLetter("N");

                // Construct a dummy lookup for display
                EncounterLookup dummyLookup = new EncounterLookup();
                dummyLookup.setFullText(card.getName());
                // Lookup Actions for Matrix N using the card's adjId
                Integer adjId = card.getAdjId();
                if (adjId != null) {
                    List<Action> actions = talesMapper.findActionsByMatrix("N", adjId);
                    response.setAvailableActions(actions);
                    dummyLookup.setAdjId(adjId);
                    response.setLookup(dummyLookup);
                } else {
                    response.setAvailableActions(Collections.emptyList());
                    response.setLookup(dummyLookup); // Ensure lookup is set even if no actions
                }
            }
            return response;
        }

        // 1. Lookup Encounter
        EncounterLookup lookup = talesMapper.findLookup(encounterId, dieRoll);

        if (lookup == null) {
            // Handle case where no encounter is found (optional: throw exception or return
            // empty)
            response.setReactionLetter(null);
            response.setAvailableActions(Collections.emptyList());
            return response;
        }

        response.setLookup(lookup);
        response.setReactionLetter(lookup.getReactionLetter());

        // 2. Lookup Actions from Reaction Matrix
        // We need the matrix letter (from reaction_letter) and the adjective (from
        // adj_id)
        if (lookup.getReactionLetter() != null && lookup.getAdjId() != null) {
            List<Action> actions = talesMapper.findActionsByMatrix(lookup.getReactionLetter(), lookup.getAdjId());
            response.setAvailableActions(actions);
        } else {
            response.setAvailableActions(Collections.emptyList());
        }

        return response;
    }

    /**
     * 根據玩家選擇的行動，查詢最終的故事結果
     *
     * @param matrixLetter 反應矩陣字母 (如 'A', 'N')
     * @param adjId        形容詞 ID (決定了矩陣中的具體 column)
     * @param actionId     玩家選擇的行動 ID
     * @return Event 物件，包含故事標題、描述、以及可能的技能檢定結果
     */
    public Event getEvent(String matrixLetter, int adjId, int actionId) {
        // 1. 查詢目標 Event ID
        Integer eventId = talesMapper.findTargetEventId(matrixLetter, adjId, actionId);
        if (eventId == null) {
            return null;
        }
        // 2. 查詢 Event 詳細內容
        Event event = talesMapper.findEventById(eventId);
        if (event != null) {
            // 3. 查詢該 Event 的所有分歧結果 (技能檢定、獎勵等)
            List<StoryOutcome> allOutcomes = talesMapper.findOutcomesByEventId(eventId);
            // Build hierarchy
            List<StoryOutcome> rootOutcomes = buildHierarchy(allOutcomes);
            event.setOutcomes(rootOutcomes);
        }
        return event;
    }

    /**
     * Helper to organize flat list of outcomes into a tree based on parentId.
     */
    private List<StoryOutcome> buildHierarchy(List<StoryOutcome> allOutcomes) {
        List<StoryOutcome> roots = new ArrayList<>();

        // Map ID -> Outcome
        java.util.Map<Integer, StoryOutcome> map = new java.util.HashMap<>();
        for (StoryOutcome outcome : allOutcomes) {
            map.put(outcome.getId(), outcome);
        }

        for (StoryOutcome outcome : allOutcomes) {
            // Treat null or 0 as root
            if (outcome.getParentId() == null || outcome.getParentId() == 0) {
                roots.add(outcome);
            } else {
                StoryOutcome parent = map.get(outcome.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(outcome);
                }
            }
        }
        return roots;
    }

    /**
     * 直接根據 Event ID 查詢故事事件 (跳過遭遇/矩陣)
     *
     * @param eventId 事件 ID
     * @return Event 物件
     */
    public Event getEventById(int eventId) {
        Event event = talesMapper.findEventById(eventId);
        if (event != null) {
            List<StoryOutcome> allOutcomes = talesMapper.findOutcomesByEventId(eventId);
            List<StoryOutcome> rootOutcomes = buildHierarchy(allOutcomes);
            event.setOutcomes(rootOutcomes);
        }
        return event;
    }
}
