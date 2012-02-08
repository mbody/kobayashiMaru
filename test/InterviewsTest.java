import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import models.Interview;
import models.User;
import org.junit.Before;
import org.junit.Test;
import play.mvc.Http;
import play.test.Fixtures;
import play.test.FunctionalTest;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * User: AREVEL
 * Date: 07/02/12
 * Time: 11:32
 * To change this template use File | Settings | File Templates.
 */
public class InterviewsTest extends FunctionalTest {

    @Before
    public void setUp() {
        Fixtures.deleteDatabase();
        Fixtures.loadModels("data.yml");
    }


    @Test
    public void getQuestionsTest() {
        Http.Response listInterviewsResponse = GET("/api/interviews");
        assertIsOk(listInterviewsResponse);
        Gson gson = new Gson();
        Type type = new TypeToken<Collection<Interview>>() {}.getType();
        Collection<Interview> interviewsList = gson.fromJson(listInterviewsResponse.out.toString(), type);
        assertTrue(interviewsList.size()==2);
    }

    @Test
    public void getQuestionByIdTest() {
        Http.Response listInterviewResponse = GET("/api/interviews");
        assertIsOk(listInterviewResponse);
        Gson gson = new Gson();
        Type type = new TypeToken<Collection<Interview>>() {}.getType();
        Collection<Interview> interviewList = gson.fromJson(listInterviewResponse.out.toString(), type);
        Long id = interviewList.iterator().next().getId();
        Http.Response interviewResponse = GET("/api/interviews/"+id);
        assertIsOk(interviewResponse);
    }

    @Test
    public void creationInterviewTest() {
        Interview interview = new Interview();
        interview.candidateName = "Durant";
        interview.candidateFirstName = "Ludovic";
        interview.interviewDate = Calendar.getInstance();
        User examiner = new User("revel@apside.fr","lbg", "Amaury", "REVEL") ;
        interview.examiner = examiner;
        Gson gson = new Gson();
        String body = gson.toJson(interview);
        Http.Response postResponse = POST("/api/interviews/", "application/json" , body);
        assertStatus(Http.StatusCode.CREATED, postResponse);
    }

    @Test
    public void modificationInterviewTest(){
        Http.Response listInterviewResponse = GET("/api/interviews");
        assertIsOk(listInterviewResponse);
        Gson gson = new Gson();
        Type type = new TypeToken<Collection<Interview>>() {}.getType();
        Collection<Interview> interviewList = gson.fromJson(listInterviewResponse.out.toString(), type);
        Interview interviewToModify = interviewList.iterator().next();
        interviewToModify.interviewDate = Calendar.getInstance();
        String body = gson.toJson(interviewToModify);
        Http.Response putResponse = PUT("/api/interviews/" + interviewToModify.id, "application/json", body);
        assertStatus(Http.StatusCode.OK, putResponse);
    }

    @Test
    public void deleteInterviewTest(){
        Http.Response listInterviewResponse = GET("/api/interviews");
        assertIsOk(listInterviewResponse);
        Gson gson = new Gson();
        Type type = new TypeToken<Collection<Interview>>() {}.getType();
        Collection<Interview> interviewList = gson.fromJson(listInterviewResponse.out.toString(), type);
        Long id = interviewList.iterator().next().getId();
        Http.Response putResponse = DELETE("/api/interviews/" + id);
        assertStatus(Http.StatusCode.OK, putResponse);
    }
}
