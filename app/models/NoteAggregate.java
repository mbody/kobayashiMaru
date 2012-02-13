package models;

import jj.play.ns.com.jhlabs.image.DiffusionFilter;
import play.db.jpa.JPA;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Query;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: christophe
 * Date: 10/02/12
 * Time: 12:22
 */
public class NoteAggregate {
   
    public Topic topic;
    public Difficulty difficulty;
    public long sumOfNoteAbove3;
    public long sumOfNoteUnder3;
    
    private static String noteAbove3Query = "select new models.QueryResult(count(iq.id), iq.question.topic, iq.question.difficulty) from InterviewQuestion as iq where iq.interview.id = :interviewId and iq.mark >=3 group by iq.question.topic, iq.question.difficulty";
    private static String noteUnder3Query = "select new models.QueryResult(count(iq.id), iq.question.topic, iq.question.difficulty) from InterviewQuestion as iq where iq.interview.id = :interviewId and iq.mark <3 group by iq.question.topic, iq.question.difficulty";
    
    public static Map<Topic, Map<Difficulty, NoteAggregate>> getNoteAggregate(Long idInterview){
        String jpql = noteAbove3Query;
        Query query= JPA.em().createQuery(jpql);
        query.setParameter("interviewId", idInterview);
        List<QueryResult> resultAbove3List = query.getResultList();
        
        jpql = noteUnder3Query;
        query = JPA.em().createQuery(jpql);
        query.setParameter("interviewId", idInterview);
        List<QueryResult> resultUnder3List = query.getResultList();

        Map<Topic, Map<Difficulty, NoteAggregate>> resultMap = new HashMap<Topic, Map<Difficulty, NoteAggregate>>();
        for(QueryResult queryResult : resultAbove3List){
            NoteAggregate noteAggregate = getNoteAggregate(resultMap, queryResult);
            noteAggregate.sumOfNoteAbove3 = queryResult.countOfNote;
        }
        for(QueryResult queryResult : resultUnder3List){
            NoteAggregate noteAggregate = getNoteAggregate(resultMap, queryResult);
            noteAggregate.sumOfNoteUnder3 = queryResult.countOfNote;
        }
        return resultMap;
    }

    private static NoteAggregate getNoteAggregate(Map<Topic, Map<Difficulty, NoteAggregate>> resultMap, QueryResult queryResult) {
        Map<Difficulty, NoteAggregate> difficultyNoteAggregateMap = resultMap.get(queryResult.topic);
        if(difficultyNoteAggregateMap == null) {
            difficultyNoteAggregateMap = new HashMap<Difficulty, NoteAggregate>();
            resultMap.put(queryResult.topic, difficultyNoteAggregateMap);
        }
        NoteAggregate noteAggregate = difficultyNoteAggregateMap.get(queryResult.difficulty);
        if (noteAggregate == null) {
            noteAggregate = new NoteAggregate();
            noteAggregate.topic = queryResult.topic;
            noteAggregate.difficulty = queryResult.difficulty;
            difficultyNoteAggregateMap.put(queryResult.difficulty, noteAggregate);
        }
        return noteAggregate;
    }

}
