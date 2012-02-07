package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import java.util.Calendar;

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
    public String assessorName;
    public String assessorFirstName;

    public Interview(){

    }

    public Interview(String candidateName, String candidateFirstName, Calendar interviewDate, String assessorName, String assessorFirstName){
        this.candidateName = candidateName;
        this.candidateFirstName = candidateFirstName;
        this.interviewDate = interviewDate;
        this.assessorName = assessorName;
        this.assessorFirstName = assessorFirstName;
    }
}
