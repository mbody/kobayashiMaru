import org.junit.Test;
import play.mvc.Http;
import play.test.Fixtures;
import play.test.FunctionalTest;

/**
 * Created by IntelliJ IDEA.
 * User: AREVEL
 * Date: 07/02/12
 * Time: 11:32
 * To change this template use File | Settings | File Templates.
 */
public class InterviewsTest extends FunctionalTest {

    @Test
    public void getQuestionsTest() {
        Fixtures.deleteDatabase();
        Fixtures.loadModels("data.yml");
        Http.Response listInterviewsResponse = GET("/interviews");
        assertIsOk(listInterviewsResponse);
    }
}
