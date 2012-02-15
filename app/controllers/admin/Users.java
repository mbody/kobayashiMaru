package controllers.admin;

import controllers.CRUD;
import controllers.SecuredCrud;
import models.Role;
import models.User;
import play.exceptions.TemplateNotFoundException;
import play.libs.Codec;
import play.mvc.Http;
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

    public static void save(Long id){
        ObjectType type = ObjectType.get(getControllerClass());
        User user = User.findById(id);
        user.lastname = Http.Request.current().params.get("object.lastname");
        user.firstname = Http.Request.current().params.get("object.firstname");
        user.email = Http.Request.current().params.get("object.email");
        String pwd = Http.Request.current().params.get("object.passwordHash");
        if (pwd.compareTo(user.passwordHash)!=0)
            user.passwordHash = Codec.hexMD5(pwd);
        validation.valid(user);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/show.html", type, user);
            } catch (TemplateNotFoundException e) {
                render("CRUD/show.html", type, user);
            }
        }
        user._save();
        flash.success(play.i18n.Messages.get("crud.saved", Users.class.getSimpleName()));
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        redirect(request.controller + ".show", id);
    }
}
