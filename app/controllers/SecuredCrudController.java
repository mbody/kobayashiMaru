package controllers;

import models.User;
import play.mvc.Before;
import security.Secure;

/**
 * Created by IntelliJ IDEA.
 * User: AREVEL
 * Date: 13/02/12
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */
public class SecuredCrudController extends CRUD{
    @Before
    static void checkSecure() {
        Secure secure = getActionAnnotation(Secure.class);
        if (secure != null) {
            if (connectedUser() == null || (connectedUser().hasNotRole(secure.role()))) {
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
