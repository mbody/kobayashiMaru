package models;

import play.db.jpa.GenericModel;
import play.data.validation.Required;
import play.db.jpa.JPA;
import play.db.jpa.Model;

import javax.persistence.*;
import java.util.Calendar;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: AREVEL
 * Date: 07/02/12
 * Time: 10:48
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Interview extends Model {

    public static final int NB_QUESTIONS_PER_TOPIC = 5;
    /** True si l'entretien est achevé, false sinon */
    public boolean complete;
    @Required
    public String candidateName;
    @Required
    public String candidateFirstName;
    @Required
    public Calendar interviewDate;
    @ManyToOne
    @Required
    public User examiner;
    public String examinerComment;

    @OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "interview", orphanRemoval = true)
    @OrderBy("id ASC")
    public List<InterviewTopic> topics;

    @OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "interview")
    @OrderBy("index ASC")
    public List<InterviewQuestion> questions;

    public Interview(){

    }

    public Interview(String candidateName, String candidateFirstName, Calendar interviewDate, User examiner){
        this.candidateName = candidateName;
        this.candidateFirstName = candidateFirstName;
        this.interviewDate = interviewDate;
        this.examiner = examiner;
    }
    
    public List<Question> findUnusedQuestionByTopicAndDifficulty(Topic topic, int difficulty){
        String queryStr = "select q from Question q where q.difficulty = " + difficulty + " and q.topic.id = " + topic.id +
                " and q.id not in (select qi.question.id from InterviewQuestion qi where qi.interview.id = " + this.id + ")";
        Query query = JPA.em().createQuery(queryStr);
        return query.getResultList();
    }
    
    public int getNbQuestions(){
        return topics.size() * NB_QUESTIONS_PER_TOPIC;
    }

    public static List<Interview> findAllUncompleted() {
        return getQueryByCompletedAndUser(false, null).fetch();
    }
    public static List<Interview> findAllUncompleted(User user) {
        return getQueryByCompletedAndUser(false, user).fetch();
    }

    public static List<Interview> findLastCompleted(User user, int max) {
        return getQueryByCompletedAndUser(true, user).fetch(max);
    }

    public static List<Interview> findLastCompleted(int max) {
        return getQueryByCompletedAndUser(true, null).fetch(max);
    }
    
    private static JPAQuery getQueryByCompletedAndUser(boolean complete, User user){
        Object[] params;
        String query = "complete = ? ";

        if(user!=null){
            query += "and examiner = ? ";
            params = new Object[]{complete, user};
        }else{
            params = new Object[]{complete};
        }

        query += "order by interviewDate desc";

        return find(query, params);
    }

    /**
     * Méthode utilitaire
     * @param topic
     * @param difficulty
     * @return true si l'entretien courant contient la topic et est initialisé à la difficulté passées en param, false sinon
     */
    public boolean hasInitialDifficulty(Topic topic, Difficulty difficulty){
        for (InterviewTopic interviewTopic : topics) {
            if(interviewTopic.topic.id == topic.id){
                return interviewTopic.initialDifficulty.equals(difficulty);
            }
        }
        return false;
    }


    /**
     * Méthode utilitaire
     * @param topic
     * @return true si l'entretien courant contient la topic
     */
    public boolean containsTopic(Topic topic){
        for (InterviewTopic interviewTopic : topics) {
            if(interviewTopic.topic.id == topic.id){
                return true;
            }
        }
        return false;
    }
}

