package models;

/**
 * Created by IntelliJ IDEA.
 * User: Romano
 * Date: 08/02/12
 * Time: 10:10
 * To change this template use File | Settings | File Templates.
 */
public enum Difficulty {
    BEGINNER, INTERMEDIATE, ADVANCED, EXPERT;

    public Difficulty levelUp(){
        return getComplexityFromOrdinal(ordinal() + 1);
    }

    public Difficulty levelDown(){
        return getComplexityFromOrdinal(ordinal() - 1);
    }

    public static Difficulty getComplexityFromOrdinal(int level){
        for (Difficulty difficulty : Difficulty.values()) {
            if(difficulty.ordinal()==level){
                return difficulty;
            }
        }
        return null;
    }
}
