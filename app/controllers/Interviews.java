package controllers;

import models.Interview;
import models.InterviewQuestion;
import models.InterviewTopic;
import models.Question;
import play.mvc.Controller;
import play.mvc.Http;

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

    public static void question(){
        render();
    }

    public static Question getNexQuestion(InterviewQuestion previousInterviewQuestion){
        return null;
    }
}
