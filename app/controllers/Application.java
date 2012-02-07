package controllers;

import play.*;
import play.i18n.Messages;
import play.mvc.*;

import java.util.*;

import models.*;
import security.Secure;

public class Application extends SecuredController {

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
    public static void home(){
        render();
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