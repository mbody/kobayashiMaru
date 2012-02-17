package models;

import org.hibernate.annotations.Type;
import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.*;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 06/02/12
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Question extends Model {

    @Enumerated(EnumType.ORDINAL)
    public Difficulty difficulty;
    @Required
    public String label;
    @Required @Lob @MaxSize(10000)
    public String description;
    @Required @Lob @MaxSize(10000)
    public String answer;
    @Required
    @ManyToOne
    public Topic topic;
}
