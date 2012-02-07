import models.Role;
import models.User;
import org.junit.Test;
import play.test.UnitTest;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 07/02/12
 * Time: 11:28
 * To change this template use File | Settings | File Templates.
 */
public class UserTest extends UnitTest {
    
    @Test
    public void simpleCrudTest(){
        User user = new User("mathurin.body@apside.fr", "apside", "Mathurin", "BODY");
        user.addRole(Role.EXAMINER);
        user.create();
        User userfromDB = User.findById(user.id);
        assertTrue(userfromDB.hasRole(Role.EXAMINER));
        assertTrue(userfromDB.hasNotRole(Role.STAFF_ADMIN));
    }
}
