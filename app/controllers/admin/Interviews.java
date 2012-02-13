package controllers.admin;

import controllers.CRUD;
import controllers.SecuredCrud;
import models.Interview;
import models.Role;
import security.Secure;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 13/02/12
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
@Secure(role = Role.STAFF_ADMIN)
@CRUD.For(Interview.class)
public class Interviews extends SecuredCrud {
}
