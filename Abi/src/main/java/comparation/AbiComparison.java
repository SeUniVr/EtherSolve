package comparation;

import java.util.HashMap;

public class AbiComparison {
    private HashMap<String, Double> functionsScore;
    private double globalScore;

    public AbiComparison(){
        functionsScore = new HashMap<>();
        globalScore = 0.;
    }

    public double getFunctionScore(String name) {
        return functionsScore.get(name);
    }

    public double getGlobalScore() {
        return globalScore;
    }

    public void addScore(String name, double score){
        functionsScore.put(name, score);
        // Recalculate the average score
        globalScore = 0;
        for (String function : functionsScore.keySet())
            globalScore += functionsScore.get(function);
        globalScore /= functionsScore.size();
    }


}
