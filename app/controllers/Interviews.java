package controllers;

import models.*;
import play.db.jpa.JPABase;
import play.mvc.Http;
import security.Secure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import java.text.SimpleDateFormat;
import java.util.*;

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
        question(interviewQuestion.interview.id, interviewQuestion.index + 1);
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
                bilan(idEntretien);
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
            if(currentTopicIndex <= currentInterview.topics.size()-1)
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
            iq.index = 1;
        else
            iq.index = previousInterviewQuestion.index + 1;

        iq.save();

        return iq;
    }
    
//    @Secure(role = Role.EXAMINER)
    public static void bilan(Long idInterview){
        Interview interview = Interview.findById(idInterview);
        //compute the average by topic and level.
        Map<Topic, Map<Difficulty, NoteAggregate>> mapOfNoteAggregate = NoteAggregate.getNoteAggregate(idInterview);
        Map<String, String> notesAboveForTopic = buildMapOfResult(mapOfNoteAggregate, true);
        Map<String, String> notesUnderForTopic = buildMapOfResult(mapOfNoteAggregate, false);
        Set<String> keysForMap = notesAboveForTopic.keySet();
        String ticks= getGraphLabelList();
        render(interview, notesAboveForTopic, notesUnderForTopic, keysForMap, ticks);
    }

    private static String getGraphLabelList(){
        Difficulty[] values = Difficulty.values();
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(Difficulty difficulty : values){
            sb.append("'").append(difficulty.toString()).append("',");
        }
        String result = sb.substring(0, sb.length()-1);
        return result+"]";
    }

    private static Map<String, String> buildMapOfResult(Map<Topic, Map<Difficulty, NoteAggregate>> mapOfNoteAggregate, boolean above3){
        Map<String, String> result = new HashMap<String, String>();
        for(Map.Entry<Topic, Map<Difficulty,NoteAggregate>> entry : mapOfNoteAggregate.entrySet()) {
            Map<Difficulty,NoteAggregate> resultForTopic = entry.getValue();
            StringBuilder sortedNotes = new StringBuilder();
            sortedNotes.append("[").append(above3? getValueAbove3ToAppend(resultForTopic, Difficulty.BEGINNER): getValueUnder3ToAppend(resultForTopic, Difficulty.BEGINNER));
            sortedNotes.append(",").append(above3? getValueAbove3ToAppend(resultForTopic, Difficulty.INTERMEDIATE): getValueUnder3ToAppend(resultForTopic, Difficulty.INTERMEDIATE));
            sortedNotes.append(",").append(above3? getValueAbove3ToAppend(resultForTopic, Difficulty.ADVANCED): getValueUnder3ToAppend(resultForTopic, Difficulty.ADVANCED));
            sortedNotes.append(",").append(above3? getValueAbove3ToAppend(resultForTopic, Difficulty.EXPERT):getValueUnder3ToAppend(resultForTopic, Difficulty.EXPERT));
            sortedNotes.append("]");
            result.put(entry.getKey().label, sortedNotes.toString());
        }

        return result;
    }

    private static Object getValueAbove3ToAppend(Map<Difficulty, NoteAggregate> resultForTopic, Difficulty difficulty) {
        return resultForTopic.containsKey(difficulty) ? resultForTopic.get(difficulty).sumOfNoteAbove3 : "0";
    }
    private static Object getValueUnder3ToAppend(Map<Difficulty, NoteAggregate> resultForTopic, Difficulty difficulty) {
        return resultForTopic.containsKey(difficulty) ? resultForTopic.get(difficulty).sumOfNoteUnder3 : "0";
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
