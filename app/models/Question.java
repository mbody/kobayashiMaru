package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 06/02/12
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Question extends Model {

    @Enumerated(EnumType.STRING)
    public Difficulty difficulty;
    public String label;
    public Topic topic;
}
