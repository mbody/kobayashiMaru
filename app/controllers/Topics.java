package controllers;

import models.Topic;
import play.mvc.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: christophe
 * Date: 07/02/12
 * Time: 10:36
 * To change this template use File | Settings | File Templates.
 */
public class Topics extends Controller {
    
    public static void topics() {
        List<Topic> topicList = Topic.findAll();
        renderJSON(topicList);
    }
}
