package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

/**
 * Created by IntelliJ IDEA.
 * User: AREVEL
 * Date: 08/02/12
 * Time: 15:59
 * To change this template use File | Settings | File Templates.
 */
@Entity
public class InterviewTopic extends Model {

    @Enumerated(EnumType.ORDINAL)
    public Difficulty initialDifficulty;
    @ManyToOne
    public Topic topic;
    @ManyToOne
    public Interview interview;

    public static InterviewTopic findByTopicId(Long topicId) {
        return find("topic.id = ?", topicId).first();
    }
}
