package models;

import play.db.jpa.JPA;

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
public class MarkAggregate {

    private static String QUERY_AGGREGATE = "select new models.MarkAggregate(iq.question.topic, iq.question.difficulty, count(iq.id), sum(iq.mark)) from InterviewQuestion as iq where iq.interview.id = :interviewId group by iq.question.topic, iq.question.difficulty";

    public Topic topic;
    public Difficulty difficulty;
    public long nbQuestions;
    public long totalMark;

    public MarkAggregate(Topic topic, Difficulty difficulty, long nbQuestions, long totalMark) {
        this.difficulty = difficulty;
        this.nbQuestions = nbQuestions;
        this.topic = topic;
        this.totalMark = totalMark;
    }

    public static Map<Topic, Map<Difficulty, MarkAggregate>> getMarkAggregateMap(Long idInterview){
        Query query= JPA.em().createQuery(QUERY_AGGREGATE);
        query.setParameter("interviewId", idInterview);
        List<MarkAggregate> markAggregates = query.getResultList();
        
        Map<Topic, Map<Difficulty, MarkAggregate>> resultMap = new HashMap<Topic, Map<Difficulty, MarkAggregate>>();
        for(MarkAggregate markAggregate : markAggregates){
            Map<Difficulty, MarkAggregate> difficultyMarkAggregateMap = resultMap.get(markAggregate.topic);
            if(difficultyMarkAggregateMap==null){
                difficultyMarkAggregateMap = new HashMap<Difficulty, MarkAggregate>();
            }
            difficultyMarkAggregateMap.put(markAggregate.difficulty, markAggregate);
            resultMap.put(markAggregate.topic, difficultyMarkAggregateMap);
       }
        return resultMap;
    }
}
