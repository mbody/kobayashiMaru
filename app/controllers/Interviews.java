package controllers;

import models.*;
import org.apache.commons.lang.StringUtils;
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
public class Interviews extends SecuredController {

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

    public static void startInterview(Long interviewId){
        Interview interview = Interview.findById(interviewId);
        question(interviewId, 1);
    }
    
    public static void save(Interview interview, List<InterviewTopic> topics) {
        for (Iterator<InterviewTopic> iterator = topics.iterator(); iterator.hasNext(); ) {
            InterviewTopic next =  iterator.next();
            if (next.initialDifficulty == null || next.initialDifficulty.equals(""))
            {
                iterator.remove();
            }else
            {
                next.interview=interview;
            }
        }
        if(interview.topics!=null){
            interview.topics.clear();
            interview.topics.addAll(topics);
        }else{
            interview.topics = topics;
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
        interview.save();
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
    public static void prepare(Long id) {
        List<JPABase> topics = Topic.findAll();
        Interview interview = null;
        if(id!=null){
            interview = Interview.findById(id);
        }
        render(topics, interview);
    }

    @Secure(role = Role.EXAMINER)
    public static void saveMark(Long idInterviewQuestion, int mark){
        InterviewQuestion interviewQuestion = InterviewQuestion.findById(idInterviewQuestion);
        interviewQuestion.mark = mark;
        interviewQuestion.save();
        renderText("success!");
    }

    @Secure(role = Role.EXAMINER)
    public static void question(Long idInterview, int questionIndex){
        Interview interview = Interview.findById(idInterview);
        InterviewQuestion interviewQuestion = InterviewQuestion.getInterviewQuestion(idInterview, questionIndex);
        if(interviewQuestion==null){
            InterviewQuestion previousInterviewQuestion = InterviewQuestion.getInterviewQuestion(idInterview, questionIndex-1);
            interviewQuestion = createInterviewQuestion(interview, previousInterviewQuestion);
            render(interview, interviewQuestion);
        }else{
            render(interview, interviewQuestion);
        }
    }

    @Secure(role = Role.EXAMINER)
    public static void finalize(Long idInterview){
        Interview interview = Interview.findById(idInterview);
        interview.complete = true;
        interview.save();
        bilan(idInterview);
    }

    @Secure(role = Role.EXAMINER)
    public static void saveComments(Long idInterview, String comments){
        Interview interview = Interview.findById(idInterview);
        interview.examinerComment = comments;
        interview.save();
        renderText("comment updated !");
    }

    private static InterviewQuestion createInterviewQuestion(Interview currentInterview, InterviewQuestion previousInterviewQuestion){

        //Define the current topic
        InterviewTopic currentInterviewTopic;
        boolean firstQuestionInTopic = true;
        if(previousInterviewQuestion == null)
            currentInterviewTopic = currentInterview.topics.get(0);
        else if(previousInterviewQuestion.index % Interview.NB_QUESTIONS_PER_TOPIC == 0){
            int currentTopicIndex =(int) Math.floor(previousInterviewQuestion.index / Interview.NB_QUESTIONS_PER_TOPIC);
            if(currentTopicIndex <= currentInterview.topics.size()-1)
                currentInterviewTopic = currentInterview.topics.get(currentTopicIndex);
            else
                return null;
        }
        else{
            firstQuestionInTopic = false;
            currentInterviewTopic = InterviewTopic.findByTopicId(previousInterviewQuestion.question.topic.id);
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

        //Return the next available question with those criterias
        Question question = getNewRandomQuestion(currentInterview, currentInterviewTopic.topic, currentDifficulty);
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

    @Secure(role = Role.EXAMINER)
    public static void bilan(Long idInterview){
        Interview interview = Interview.findById(idInterview);
        //compute the average by topic and level.
        Map<Topic, Map<Difficulty, MarkAggregate>> mapOfNoteAggregate = MarkAggregate.getMarkAggregateMap(idInterview);
        Map<Long, String> goodMarkSeriesByTopic = buildMapOfSeries(mapOfNoteAggregate, true);
        Map<Long, String> badMarkSeriesByTopic = buildMapOfSeries(mapOfNoteAggregate, false);
        String ticks= getGraphLabelList();
        Set<Topic> topics = mapOfNoteAggregate.keySet();
        render(interview, goodMarkSeriesByTopic, badMarkSeriesByTopic, ticks, topics);
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

    private static Map<Long, String> buildMapOfSeries(Map<Topic, Map<Difficulty, MarkAggregate>> mapOfNoteAggregate, boolean isGoodAnswer){
        Map<Long, String> result = new HashMap<Long, String>();
        for(Topic topic: mapOfNoteAggregate.keySet()) {
            Map<Difficulty,MarkAggregate> markAggregateForTopic = mapOfNoteAggregate.get(topic);
            Double[] data = new Double[Difficulty.values().length];
            for (Difficulty difficulty : Difficulty.values()) {
                MarkAggregate markAggregate = markAggregateForTopic.get(difficulty);
                double markRatio;
                // calcul de la note (1 point de la note sur 4 => 0.25)
                markRatio =  markAggregate!=null?markAggregate.totalMark * 0.25:0;
                if(!isGoodAnswer){
                    // pour le calcul du restant de la note, on retranche au nb de questions posées
                    markRatio = markAggregate!=null?(markAggregate.nbQuestions - markRatio):0;
                }
                data[difficulty.ordinal()]=markRatio;
            }
            result.put(topic.id, "[" + StringUtils.join(data, ',') + "]");
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
