package com.boardgame.tales.mapper;

import com.boardgame.tales.model.Action;
import com.boardgame.tales.model.EncounterCard;
import com.boardgame.tales.model.EncounterLookup;
import com.boardgame.tales.model.Event;
import com.boardgame.tales.model.StoryOutcome;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TalesMapper {

  @Select("SELECT * FROM encounter_lookups WHERE encounter_id = #{encounterId} AND die_roll = #{dieRoll}")
  EncounterLookup findLookup(@Param("encounterId") int encounterId, @Param("dieRoll") int dieRoll);

  @Select("""
          SELECT a.id, a.name
          FROM actions a
          JOIN reaction_matrices rm ON a.id = rm.action_id
          WHERE rm.matrix_letter = #{matrixLetter}
            AND rm.adj_id = #{adjId}
      """)
  List<Action> findActionsByMatrix(@Param("matrixLetter") String matrixLetter, @Param("adjId") int adjId);

  @Select("""
          SELECT target_event_id
          FROM reaction_matrices
          WHERE matrix_letter = #{matrixLetter}
            AND adj_id = #{adjId}
            AND action_id = #{actionId}
      """)
  Integer findTargetEventId(@Param("matrixLetter") String matrixLetter,
      @Param("adjId") int adjId,
      @Param("actionId") int actionId);

  @Select("SELECT * FROM events WHERE id = #{eventId}")
  Event findEventById(@Param("eventId") int eventId);

  @Select("""
          SELECT so.id, so.event_id, so.parent_id, so.outcome_text, so.reward_str,
            COALESCE(
              (
                SELECT GROUP_CONCAT(
                  a.name || CASE WHEN oc.is_mandatory = 1 THEN '(強制)' ELSE '' END,
                  '、'
                )
                FROM outcome_conditions oc
                JOIN attributes a ON a.id = oc.attribute_id
                WHERE oc.id = so.condition_param
              ),
              so.condition_param
            ) AS condition_param
          FROM story_outcomes so
          WHERE so.event_id = #{eventId}
          ORDER BY so.id
      """)
  List<StoryOutcome> findOutcomesByEventId(@Param("eventId") int eventId);

  @Select("SELECT * FROM encounter_cards WHERE type = 'SPECIAL' ORDER BY adj_id")
  List<EncounterCard> findAllSpecialCards();

  @Select("SELECT * FROM encounter_cards WHERE id = #{id}")
  EncounterCard findCardById(@Param("id") int id);

  @Select("""
          SELECT DISTINCT so.event_id AS targetEventId, a.id AS skillId, a.name AS skillName
          FROM story_outcomes so
          JOIN outcome_conditions oc ON so.condition_param = CAST(oc.id AS VARCHAR)
          JOIN attributes a ON a.id = oc.attribute_id AND a.attr_type = 'SKILL'
          WHERE so.event_id IN (#{baseEventId}-1, #{baseEventId}, #{baseEventId}+1)
          ORDER BY a.id, so.event_id
      """)
  List<com.boardgame.tales.model.SkillChoice> findSkillChoicesForEvents(@Param("baseEventId") int baseEventId);
}
