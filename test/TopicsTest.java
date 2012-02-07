import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Topic;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.test.Fixtures;
import play.test.FunctionalTest;

import javax.lang.model.element.NestingKind;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: christophe
 * Date: 07/02/12
 * Time: 10:29
 * To change this template use File | Settings | File Templates.
 */
public class TopicsTest extends FunctionalTest{
    
    @Before
    public void setUp() {
        Fixtures.deleteDatabase();
        Fixtures.loadModels("data.yml");
    }
    
    @Test
    public void should_return_topic_list() {
        Collection<Topic> topicList = getTopicsCollection();
        assertTrue(topicList.size()==2);

    }
    
    @Test
    public void should_find_topic_by_id(){
        Collection<Topic> topicList = getTopicsCollection();
        Long id = topicList.iterator().next().getId();
        Http.Response topicResponse = GET("/api/topics/"+id);
        assertIsOk(topicResponse);
    }

    private Collection<Topic> getTopicsCollection() {
        Http.Response listThemeResponse = GET("/api/topics/");
        assertIsOk(listThemeResponse);
        Gson gson = new Gson();
        Type type = new TypeToken<Collection<Topic>>() {}.getType();
        return gson.fromJson(listThemeResponse.out.toString(), type);
    }

    @Test
    public void should_return_404_when_topic_not_found() {
        Http.Response topicResponse = GET("/api/topics/2000000000");
        assertStatus(Http.StatusCode.NOT_FOUND, topicResponse);
    }

    @Test
    public void should_return_201_on_Topic_creation(){
        Topic topic = new Topic();
        topic.description = "new Topic";
        topic.label = "super label";
        Gson gson = new Gson();
        String body = gson.toJson(topic);

        Http.Response postResponse = POST("/api/topics/", "application/json", body);
        assertStatus(Http.StatusCode.CREATED, postResponse);
        Topic topicReturned = gson.fromJson(postResponse.out.toString(), Topic.class);
        assertNotNull(topicReturned.id);

    }
    
    @Test
    public void should_update_Topic() {
        Collection<Topic> topicsCollection = getTopicsCollection();
        Topic topic = topicsCollection.iterator().next();
        Long idToRetrieve = topic.getId();
        String newLabel = "new label";
        topic.label = newLabel;
        
        Gson gson = new Gson();
        String body = gson.toJson(topic);

        Http.Response putResponse = PUT("/api/topics/" + idToRetrieve, "application/json", body);

        Http.Response topicResponse = GET("/api/topics/" + idToRetrieve);
        assertStatus(Http.StatusCode.OK, topicResponse);

        Topic topicReturned = gson.fromJson(topicResponse.out.toString(), Topic.class);
        assertEquals(newLabel,topic.label);
    }
    
    @Test
    public void should_delete_Topic() {
        Collection<Topic> topicsCollection = getTopicsCollection();
        Topic topicToDelete = topicsCollection.iterator().next();

        Http.Response deleteResponse = DELETE("/api/topics/" + topicToDelete.id);
        assertStatus(Http.StatusCode.OK, deleteResponse);

        Collection<Topic> topicsCollectionAfterDelete = getTopicsCollection();
        assertTrue(topicsCollection.size() - 1 == topicsCollectionAfterDelete.size());

        Http.Response getResponse = GET("/api/topics/" + topicToDelete.id);
        assertStatus(Http.StatusCode.NOT_FOUND, getResponse);

    }
}
