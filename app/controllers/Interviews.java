package controllers;

import models.Interview;
import models.Role;
import models.Topic;
import play.db.jpa.JPABase;
import play.mvc.Http;
import security.Secure;

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

    public static void
        create(Interview interview) {
        interview.save();
        response.status = Http.StatusCode.CREATED;
        renderJSON(interview);
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
}
