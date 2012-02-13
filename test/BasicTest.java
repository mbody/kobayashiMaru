import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

    @Before
    public void setUp() {
        Fixtures.deleteDatabase();
        Fixtures.loadModels("data.yml");
    }

    @Test
    public void aVeryImportantThingToTest() {
        assertEquals(2, 1 + 1);
    }

    @Test
    public void testInterviewQuestion() {
        Interview interview = (Interview) Interview.findAll().get(0);
        InterviewQuestion interviewQuestion = InterviewQuestion.getInterviewQuestion(interview.id, 0);
        assertNotNull(interviewQuestion);
    }
}
