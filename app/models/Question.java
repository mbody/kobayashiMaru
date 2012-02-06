package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 06/02/12
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Question extends Model {
    public int difficulte;
    public String libelle;
    public Theme theme;
}
