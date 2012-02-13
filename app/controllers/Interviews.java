package controllers;

import models.*;
import play.db.jpa.JPABase;
import play.mvc.Http;
import security.Secure;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
        render(interview);
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
