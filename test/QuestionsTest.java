import org.junit.Test;
import play.mvc.Http.Response;
import play.test.FunctionalTest;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 06/02/12
 * Time: 16:08
 * To change this template use File | Settings | File Templates.
 */
public class QuestionsTest extends FunctionalTest {

    @Test
    public void getQuestionsTest() {
        Response addQuestionResponse = POST("/question/", APPLICATION_X_WWW_FORM_URLENCODED, "question.difficulte=1");
        assertStatus(302, addQuestionResponse);
        Response listQuestionsResponse = GET("/questions");
        assertIsOk(listQuestionsResponse);
}
        }
