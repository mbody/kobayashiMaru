package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 06/02/12
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Topic extends Model {
    public String label;
    public String description;
    @OneToMany
    public List<Question> questions;

    @Override
    public String toString(){
        return label;
    }
}
