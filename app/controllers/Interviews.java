package controllers;

import models.*;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import play.db.jpa.JPABase;
import play.mvc.Http;
import security.Secure;


import java.text.SimpleDateFormat;
import java.util.*;
import javax.persistence.Query;

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
        List<InterviewQuestion> interviewQuestions = InterviewQuestion.find("interview.id = ?", idEntretien).fetch();
        //compute the average by topic and level.
        String jpql = "select new models.NoteAggregate( avg(iq.mark), iq.question.topic, iq.question.difficulty) from InterviewQuestion as iq where iq.interview.id= :interviewId group by iq.question.topic, iq.question.difficulty";
        Query query= JPA.em().createQuery(jpql);
        query.setParameter("interviewId", idEntretien);
        List<NoteAggregate> resultList = query.getResultList();
        Map<String, String> notesForTopic = buildMapOfResult(resultList);
        Set<String> keysForMap = notesForTopic.keySet();
        String ticks=getLabelList();
        render(interview, interviewQuestions, resultList, notesForTopic, keysForMap, ticks);
    }
    public static Question getNexQuestion(InterviewQuestion previousInterviewQuestion){
        return null;
    }



    //TODO change later bug hack for js generation
    public static class NoteForDifficulty{
        public String difficulty;
        public double average;

        public NoteForDifficulty(String difficulty, double average) {
            this.difficulty = difficulty;
            this.average = average;
        }
    }

    private static String getLabelList(){
        Difficulty[] values = Difficulty.values();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(Difficulty difficulty : values){
            sb.append("'").append(difficulty.toString()).append("',");
        }
        String result = sb.substring(0, sb.length()-1);
        return result+"]";
    }
    
    private static Map<String, String> buildMapOfResult(List<NoteAggregate> noteAggregateList){
        Map<String, Map<Difficulty, Double>> mapForSorting = new HashMap<String, Map<Difficulty, Double>>();
        for(NoteAggregate noteAggregate : noteAggregateList){
            Map<Difficulty, Double> noteForDifficulties = mapForSorting.get(noteAggregate.topic.label);
            if(noteForDifficulties==null) {
                noteForDifficulties = new HashMap<Difficulty, Double>();
                mapForSorting.put(noteAggregate.topic.label, noteForDifficulties);
            }
            noteForDifficulties.put(noteAggregate.difficulty, noteAggregate.average);
        }

        Map<String, String> result = new HashMap<String, String>();
        for(Map.Entry<String, Map<Difficulty,Double>> entry : mapForSorting.entrySet()) {
            Map<Difficulty, Double> resultForTopic = entry.getValue();
            StringBuilder sortedNotes = new StringBuilder();
            sortedNotes.append("[").append(resultForTopic.containsKey(Difficulty.BEGINNER)?resultForTopic.get(Difficulty.BEGINNER):"0");
            sortedNotes.append(",").append(resultForTopic.containsKey(Difficulty.INTERMEDIATE)?resultForTopic.get(Difficulty.INTERMEDIATE):"0");
            sortedNotes.append(",").append(resultForTopic.containsKey(Difficulty.ADVANCED)?resultForTopic.get(Difficulty.ADVANCED):"0");
            sortedNotes.append(",").append(resultForTopic.containsKey(Difficulty.EXPERT)?resultForTopic.get(Difficulty.EXPERT):"0");
            sortedNotes.append("]");
            result.put(entry.getKey(), sortedNotes.toString());
        }
        
        return result;
    }

}
