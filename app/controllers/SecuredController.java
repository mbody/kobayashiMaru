package controllers;

import models.Role;
import models.User;
import play.mvc.Before;
import play.mvc.Controller;
import security.Secure;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 07/02/12
 * Time: 13:34
 * To change this template use File | Settings | File Templates.
 */
public class SecuredController extends Controller {

    @Before
    static void checkSecure() {
        Secure secure = getActionAnnotation(Secure.class);
        if (secure != null) {
            if (connectedUser() == null || !(connectedUser().hasRole(secure.role()) || secure.role().equals(Role.AUTHENTICATED))) {
                forbidden();
            }
        }
    }

    @Before
    static void globals() {
        renderArgs.put("currentUser", connectedUser());
    }

    static User connectedUser() {
        String userId = session.get("logged");
        return userId == null ? null : (User) User.findById(Long.parseLong(userId));
    }
}
