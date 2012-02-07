package controllers;

import models.Topic;
import org.omg.IOP.ComponentIdHelper;
import play.mvc.Controller;
import play.mvc.Http;

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
    
    public static void topic(Long id) {
        Topic topic = Topic.findById(id);
        if(topic == null){
            response.status = Http.StatusCode.NOT_FOUND;
        }
        renderJSON(topic);
    }
}
