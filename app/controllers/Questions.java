package controllers;

import models.Question;
import play.mvc.Controller;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 06/02/12
 * Time: 15:00
 * To change this template use File | Settings | File Templates.
 */
public class Questions extends Controller {
    
    public void listAll(){
        List<Question> questionList = Question.findAll();
        renderJSON(questionList);
    }
    
    public void create(Question quest)
}
