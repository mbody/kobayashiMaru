import models.Interview;
import org.junit.Before;
import org.junit.Test;
import play.test.Fixtures;
import play.test.UnitTest;

/**
 * Created by IntelliJ IDEA.
 * User: AREVEL
 * Date: 07/02/12
 * Time: 16:54
 * To change this template use File | Settings | File Templates.
 */
public class InterviewTest extends UnitTest{

    @Before
    public void setUp() {
        Fixtures.deleteDatabase();
        Fixtures.loadModels("data.yml");
    }

    @Test
    public void getListQuestionInterview(){
         Interview interview = new Interview();
    }
}
