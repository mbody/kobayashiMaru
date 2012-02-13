package controllers;

import models.Question;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 06/02/12
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
public class Questions extends SecuredController {
    
    public static void questions(){
        List<Question> questionList = Question.findAll();
        renderJSON(questionList);
    }

    public static void create(Question question) {
        question.save();
        question(question.id);
    }

    public static void update(Long id, Question question) {
        question.save();
        question(id);
    }

    public static void delete(Long id) {
        Question question = Question.findById(id);
        question.delete();
        renderText("success");
    }

    public static void question(Long id)  {
        Question question = Question.findById(id);
        render(question);
    }
}
