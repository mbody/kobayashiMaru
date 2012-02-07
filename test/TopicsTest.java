import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Topic;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.test.Fixtures;
import play.test.FunctionalTest;

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
        Fixtures.loadModels("Topics.yml");
    }
    
    @Test
    public void should_return_topic_list() {
        Http.Response listThemeResponse = GET("/api/topics/");
        assertIsOk(listThemeResponse);
        Gson gson = new Gson();
        Type type = new TypeToken<Collection<Topic>>() {}.getType();
        Collection<Topic> topicList = gson.fromJson(listThemeResponse.out.toString(), type);
        assertTrue(topicList.size()==2);

    }
    
    @Test
    public void should_find_topic_by_id(){
        Http.Response listThemeResponse = GET("/api/topics/");
        assertIsOk(listThemeResponse);
        Gson gson = new Gson();
        Type type = new TypeToken<Collection<Topic>>() {}.getType();
        Collection<Topic> topicList = gson.fromJson(listThemeResponse.out.toString(), type);
        Long id = topicList.iterator().next().getId();
        Http.Response topicResponse = GET("/api/topics/"+id);
        assertIsOk(topicResponse);
    }
    
    @Test
    public void should_return_404_when_topic_not_found() {
        Http.Response topicResponse = GET("/api/topics/2000000000");
        assertStatus(Http.StatusCode.NOT_FOUND, topicResponse);
    }
}
