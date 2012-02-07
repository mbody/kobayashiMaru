package models;

import javax.persistence.*;

import java.util.*;

import play.*;
import play.db.jpa.*;
import play.libs.*;
import play.data.validation.*;

@Entity
public class User extends Model {

    @Required
    public String firstname;

    @Required
    public String lastname;

    @Email
    @Required
    public String email;
    
    @Required
    public String passwordHash;
    
//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userTo")
//    public List<Interview> interviews;

    @OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "user")
    public Set<UserRole> roles;


    // ~~~~~~~~~~~~
    public User(String email, String password, String firstname, String lastname) {
        this.email = email;
        this.passwordHash = Codec.hexMD5(password);
        this.firstname = firstname;
        this.lastname = lastname;
        create();
    }
    
    // ~~~~~~~~~~~~ 
    
    public boolean checkPassword(String password) {
        return passwordHash.equals(Codec.hexMD5(password));
    }

    public boolean isAdmin() {
        return email.equals(Play.configuration.getProperty("forum.adminEmail", ""));
    }
    
    // ~~~~~~~~~~~~ 
    
    public static User findByEmail(String email) {
        return find("email", email).first();
    }

    public static List<User> findAll(int page, int pageSize) {
        return User.all().fetch(page, pageSize);
    }

    public static boolean isEmailAvailable(String email) {
        return findByEmail(email) == null;
    }

    public void addRole(Role role) {
        if(roles==null){
            roles = new HashSet<UserRole>();
        }
        roles.add(new UserRole(this, role));
    }

    public boolean hasRole(Role role) {
        if(roles == null){
            return false;
        }
        for(UserRole ur : roles){
            if(ur.role.equals(role)){
                return true;
            }
        }
        return false;
    }

    public boolean hasNotRole(Role role) {
        return !hasRole(role);
    }

    public String getFullname() {
        return firstname + " " + lastname.toUpperCase();
    }
}

