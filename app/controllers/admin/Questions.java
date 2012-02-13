package controllers.admin;

import controllers.CRUD;
import controllers.SecuredCrud;
import models.Interview;
import models.Question;
import models.Role;
import security.Secure;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 13/02/12
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
@Secure(role = Role.TECHNICAL_ADMIN)
@CRUD.For(Question.class)
public class Questions extends SecuredCrud {
}
