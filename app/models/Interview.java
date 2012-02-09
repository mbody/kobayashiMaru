package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Date;
import java.util.Set;

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
    public Date interviewDate;
    public User examiner;

    @OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "interview")
    public Set<InterviewTopic> topics;

    public Interview(){

    }

    public Interview(String candidateName, String candidateFirstName, Date interviewDate, User examiner){
        this.candidateName = candidateName;
        this.candidateFirstName = candidateFirstName;
        this.interviewDate = interviewDate;
        this.examiner = examiner;
    }
}
