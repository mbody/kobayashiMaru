package models;

import org.junit.Test;
import play.db.jpa.Model;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 07/02/12
 * Time: 12:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class UserRole extends Model {

    @Enumerated(EnumType.STRING)
    public Role role;

    @ManyToOne
    public User user;

    public UserRole(User user, Role role) {
        this.role=role;
        this.user=user;
    }

    public UserRole(){

    }
}
