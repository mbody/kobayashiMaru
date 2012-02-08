package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
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

    public String candidateName;
    public String candidateFirstName;
    public Calendar interviewDate;
    public User examiner;

    public Interview(){

    }

    public Interview(String candidateName, String candidateFirstName, Calendar interviewDate, User examiner){
        this.candidateName = candidateName;
        this.candidateFirstName = candidateFirstName;
        this.interviewDate = interviewDate;
        this.examiner = examiner;
    }

    /**
     * Méthode permettant de retourner l'ensemble des questions posées durant un entretien
     * @return Une liste de questions liées à l'entretien (List<InterviewQuestion>)
     */
    public List<InterviewQuestion> getListQuestionInterview(){
        return InterviewQuestion.find("select iq from InterviewQuestion as iq where iq.interview.id = ?", this.id).fetch();
    }
}
