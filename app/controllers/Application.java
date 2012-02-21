package controllers;

import models.Interview;
import models.Role;
import models.User;
import play.i18n.Messages;
import play.libs.Codec;
import play.mvc.Http;
import security.Secure;

import java.util.List;

public class Application extends SecuredController {

    public enum menu{
        HOME, ADMIN, ACCOUNT, ABOUT
    }

    public static void index() {
        render();
    }

    public static void authenticate(String email, String password) {
        User user = User.findByEmail(email);
        if (user == null || !user.checkPassword(password)) {
            flash.error(Messages.get("invalid.credentials"));
            flash.put("email", email);
            index();
        }
        connect(user);
        flash.success(Messages.get("login.success", user.firstname));
        home();
    }
    
    @Secure
    public static void admin(){
        session.put("menu", menu.ADMIN);
        render("admin/index.html");
    }

    @Secure
    public static void home(){
        session.put("menu", menu.HOME);
        User currentUser = connectedUser();
        List<Interview> examinerUncompletedInterviews = null;
        List<Interview> examinerLastCompletedInterviews = null;
        List<Interview> uncompletedInterviews = null;
        List<Interview> lastCompletedInterviews = null;
        if(currentUser.hasRole(Role.EXAMINER)){
            examinerUncompletedInterviews = Interview.findAllUncompleted(currentUser);
            examinerLastCompletedInterviews = Interview.findLastCompleted(currentUser, 10);
        }
        if(currentUser.hasRole(Role.STAFF_ADMIN)){
            uncompletedInterviews = Interview.findAllUncompleted();
            lastCompletedInterviews = Interview.findLastCompleted(10);
        }

        render(examinerUncompletedInterviews, examinerLastCompletedInterviews, uncompletedInterviews, lastCompletedInterviews);
    }

    public static void changepwd(){
        session.put("menu", menu.ACCOUNT);
        render();
    }

    @Secure
    public static void saveNewPwd(){
        String oldPwd = Http.Request.current().params.get("login.password.old");
        String newPwd = Http.Request.current().params.get("login.password.new");
        String newPwdConfirm = Http.Request.current().params.get("login.password.new.confirm");

        // Vérifications
        User usr = connectedUser();
        if (!usr.checkPassword(oldPwd)){
            flash.error(Messages.get("login.password.old.error"));
            changepwd();
        }
        if (!newPwdConfirm.equals(newPwd)){
            flash.error(Messages.get("login.password.new.confirm.error"));
            changepwd();
        }
        if (newPwd.equals(oldPwd)){
            flash.error(Messages.get("login.password.new.error"));
            changepwd();
        }

        usr.passwordHash = Codec.hexMD5(newPwd);
        usr.save();
        flash.success(Messages.get("login.password.new.success"));
        home();

    }

    public static void logout() {
        flash.success("You've been logged out");
        session.clear();
        index();
    }

    static void connect(User user) {
        session.put("logged", user.id);
    }

}
