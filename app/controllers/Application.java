package controllers;

import models.Interview;
import models.Role;
import models.User;
import play.i18n.Messages;
import security.Secure;

import java.util.Calendar;
import java.util.List;

public class Application extends SecuredController {

    public enum menu{
        HOME, ADMIN, ABOUT
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

    public static void logout() {
        flash.success("You've been logged out");
        session.clear();
        index();
    }

    static void connect(User user) {
        session.put("logged", user.id);
    }

}