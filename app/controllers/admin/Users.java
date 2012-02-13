package controllers.admin;

import controllers.CRUD;
import controllers.SecuredCrud;
import models.Role;
import models.User;
import security.Secure;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 13/02/12
 * Time: 10:22
 * To change this template use File | Settings | File Templates.
 */
@Secure(role = Role.STAFF_ADMIN)
@CRUD.For(User.class)
public class Users extends SecuredCrud {

}
