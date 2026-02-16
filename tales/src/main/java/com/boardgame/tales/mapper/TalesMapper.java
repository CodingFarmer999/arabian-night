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

  @Select("SELECT * FROM story_outcomes WHERE event_id = #{eventId} ORDER BY id")
  List<StoryOutcome> findOutcomesByEventId(@Param("eventId") int eventId);

  @Select("SELECT * FROM encounter_cards WHERE type = 'SPECIAL' ORDER BY adj_id")
  List<EncounterCard> findAllSpecialCards();

  @Select("SELECT * FROM encounter_cards WHERE id = #{id}")
  EncounterCard findCardById(@Param("id") int id);
}
