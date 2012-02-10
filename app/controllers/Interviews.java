package controllers;

import models.*;
import play.db.jpa.JPABase;
import play.mvc.Http;
import security.Secure;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: AREVEL
 * Date: 07/02/12
 * Time: 11:31
 * To change this template use File | Settings | File Templates.
 */
public class Interviews extends SecuredController{

    public static void interviews(){
        List<Interview> interviewList = Interview.findAll();
        renderJSON(interviewList);
    }

    public static void interview(Long id){
        Interview interview = Interview.findById(id);
        if(interview == null){
            response.status = Http.StatusCode.NOT_FOUND;
        }
        renderJSON(interview);
    }

    public static void create(Interview interview) {
        for (Iterator<InterviewTopic> iterator = interview.topics.iterator(); iterator.hasNext(); ) {
            InterviewTopic next =  iterator.next();
            if (next.initialDifficulty == null || next.initialDifficulty.equals(""))
            {
                iterator.remove();
            }else
            {
                next.interview=interview;
            }
        }
        String s = Http.Request.current().params.get("interview.interviewDate");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Calendar cal = Calendar.getInstance();

        try{
            Date dateInterview= sdf.parse(s);
            cal.setTime(dateInterview);
        }catch (Exception ex){

        }
        interview.interviewDate = cal;
        interview.examiner = connectedUser();
        interview.create();
        response.status = Http.StatusCode.CREATED;
        Application.home();
    }

    public static void update(Long id, Interview interview) {
        interview.save();
        response.status = Http.StatusCode.OK;
        renderJSON(interview);
    }

    public static void delete(Long id) {
        Interview interview = Interview.findById(id);
        interview.delete();
        response.status = Http.StatusCode.OK;
    }

    @Secure(role = Role.EXAMINER)
    public static void prepare() {
        List<JPABase> topics = Topic.findAll();
        render(topics);
    }
    
    @Secure(role = Role.EXAMINER)
    public static void question(Long idEntretien, Long questionNumber){
        render();
    }
    
    @Secure(role = Role.EXAMINER)
    public static void bilan(Long idEntretien){
        Interview interview = Interview.findById(idEntretien);
        render(interview);
    }
    public static Question getNexQuestion(InterviewQuestion previousInterviewQuestion){
        return null;
    }
}
