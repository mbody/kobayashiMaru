package models;

import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 06/02/12
 * Time: 14:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class Theme extends Model {
    public String libelle;
    public String description;
}
