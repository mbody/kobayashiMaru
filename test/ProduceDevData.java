import models.Difficulty;

/**
 * Created by IntelliJ IDEA.
 * User: Mathurin
 * Date: 13/02/12
 * Time: 09:58
 * To change this template use File | Settings | File Templates.
 */
public class ProduceDevData {

    public static final int NB_QUESTIONS_PER_LEVEL = 10;
    public static void main(String[] args) {
        for (int i = 0; i < 6; i++) {
            String topicId = "topic" + (i+1);
            for (Difficulty difficulty : Difficulty.values()) {
                for (int j = 0; j < NB_QUESTIONS_PER_LEVEL; j++) {
                    String questionId = "Q-"+topicId+"."+difficulty.name()+"."+(j+1);
                    System.out.println("Question("+questionId+"):");
                    System.out.println("    difficulty: " + difficulty.name());
                    System.out.println("    label: Libellé de la question '" + questionId + "'");
                    System.out.println("    topic: " + topicId + "\n");
                }
            }
        }
    }
}
