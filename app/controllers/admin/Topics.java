package controllers.admin;

import controllers.CRUD;
import controllers.SecuredCrud;
import models.Role;
import models.Topic;
import models.User;
import security.Secure;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 13/02/12
 * Time: 10:22
 * To change this template use File | Settings | File Templates.
 */
@Secure(role = Role.TECHNICAL_ADMIN)
@CRUD.For(Topic.class)
public class Topics extends SecuredCrud {

}
