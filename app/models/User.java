package models;

import play.data.validation.Email;
import play.data.validation.Required;
import play.db.jpa.Model;
import play.libs.Codec;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @Required
    @OneToMany(cascade = javax.persistence.CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    public Set<UserRole> roles;

    // ~~~~~~~~~~~~
    public User(String email, String password, String firstname, String lastname) {
        this.email = email;
        this.passwordHash = Codec.hexMD5(password);
        this.firstname = firstname;
        this.lastname = lastname;
        create();
    }
    
    public User(){

    }
    
    // ~~~~~~~~~~~~ 
    public boolean checkPassword(String password) {
        return passwordHash.equals(Codec.hexMD5(password));
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

    public void clearRoles(){
        roles.clear();
    }

    public void addRole(Role role) {
        if(roles==null){
            roles = new HashSet<UserRole>();
        }
        if(role!=null){
            roles.add(new UserRole(this, role));
        }
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
    
    public boolean isStaffAdmin() {
        return hasRole(Role.STAFF_ADMIN);
    }

    public boolean isTechAdmin() {
        return hasRole(Role.TECHNICAL_ADMIN);
    }

    public boolean isExaminer() {
        return hasRole(Role.EXAMINER);
    }
}

