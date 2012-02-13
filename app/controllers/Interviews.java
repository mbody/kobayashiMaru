package controllers;

import models.*;
import play.db.jpa.GenericModel;
import play.db.jpa.JPA;
import play.db.jpa.JPABase;
import play.mvc.Http;
import security.Secure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
    public static void saveQuestion(Long idInterviewQuestion, int mark){
        InterviewQuestion interviewQuestion = InterviewQuestion.findById(idInterviewQuestion);
        interviewQuestion.mark = mark;
        interviewQuestion.save();
        question(interviewQuestion.interview.id, interviewQuestion.index+1);
    }

    @Secure(role = Role.EXAMINER)
    public static void question(Long idEntretien, int questionIndex){
        Interview interview = Interview.findById(idEntretien);
        InterviewQuestion interviewQuestion = InterviewQuestion.getInterviewQuestion(idEntretien, questionIndex);
        boolean lastQuestion = (interviewQuestion==null);
        if(lastQuestion){
            InterviewQuestion previousInterviewQuestion= null;
            if(questionIndex>0){
                previousInterviewQuestion = InterviewQuestion.getInterviewQuestion(idEntretien, questionIndex-1);
            }
            interviewQuestion = createInterviewQuestion(interview, previousInterviewQuestion);
            if(interviewQuestion == null)
                render();
            else
                render(interview, interviewQuestion, lastQuestion);
        }else{
            render(interview, interviewQuestion, lastQuestion);
        }
    }

    private static InterviewQuestion createInterviewQuestion(Interview currentInterview, InterviewQuestion previousInterviewQuestion){

        //Define the current topic
        InterviewTopic currentInterviewTopic;
        boolean firstQuestionInTopic = true;
        if(previousInterviewQuestion == null)
            currentInterviewTopic = currentInterview.topics.get((0));
        else if(previousInterviewQuestion.index % 5 == 0){
            int currentTopicIndex =(int) Math.floor(previousInterviewQuestion.index / 5);
            if(currentTopicIndex <= currentInterview.topics.size())
                currentInterviewTopic = currentInterview.topics.get(currentTopicIndex);
            else
                return null;
        }
        else{
            firstQuestionInTopic = false;
            currentInterviewTopic = InterviewTopic.findById(previousInterviewQuestion.question.topic.id);
        }

        //Define the current difficulty
        int currentDifficulty;
        if(previousInterviewQuestion == null || firstQuestionInTopic)
            currentDifficulty = currentInterviewTopic.initialDifficulty.ordinal();
        else{
            currentDifficulty = previousInterviewQuestion.question.difficulty.ordinal();
            switch(previousInterviewQuestion.mark){
                case 0:
                    if(currentDifficulty > 0)
                        currentDifficulty --;
                    break;
                case 4:
                    if(currentDifficulty < 3)
                        currentDifficulty ++;
                    break;
            }
        }

        //Test if we reach the last question for the last topic


        //Return the next available question with those criterias
        Question question = getNewRandomQuestion(currentInterview, currentInterviewTopic.topic, currentDifficulty);
//        Question question = new Question();
//        question.label = "la bete " + Math.random();
//        question.topic = currentInterviewTopic.topic;
//        question.difficulty = Difficulty.getDifficultyFromOrdinal(currentDifficulty);
//        question.save();
        InterviewQuestion iq = new InterviewQuestion();
        iq.question = question;
        iq.interview = currentInterview;

        //Define the current question index
        if(previousInterviewQuestion == null)
            iq.index = 0;
        else
            iq.index = previousInterviewQuestion.index + 1;

        iq.save();

        return iq;
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
    
    private static Question getNewRandomQuestion(Interview interview, Topic currentTopic, int currentDifficulty){

        List<Question> list = interview.findUnusedQuestionByTopicAndDifficulty(currentTopic, currentDifficulty);
        Random rnd = new Random();

        if(list.isEmpty())
            return null;
        else
            return list.get(rnd.nextInt(list.size()));
    }
}
