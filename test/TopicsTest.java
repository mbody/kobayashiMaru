import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.test.Fixtures;
import play.test.FunctionalTest;

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
    public void getTopicsTest() {
        Http.Response listThemeResponse = GET("/api/topics/");
        assertIsOk(listThemeResponse);
    }
    
}
