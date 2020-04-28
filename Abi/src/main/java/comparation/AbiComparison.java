package comparation;

import java.util.Arrays;
import java.util.HashMap;

public class AbiComparison {
    public static final int FUNCTIONS_FOUND = 0;
    public static final int FUNCTIONS_INPUTS = 1;
    public static final int FUNCTIONS_INPUTS_SIZE = 2;

    private final HashMap<String, double[]> functionsScore;
    private int foundFunctions;
    private int totalOriginalFunctions;
    private int totalRebuiltFunctions;

    public AbiComparison(){
        functionsScore = new HashMap<>();
        foundFunctions = 0;
        totalOriginalFunctions = 0;
        totalRebuiltFunctions = 0;
    }

    public void addScore(String name, double[] score){
        functionsScore.put(name, score);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String hash : functionsScore.keySet()) {
            sb.append(hash);
            sb.append(": ");
            sb.append(Arrays.toString(functionsScore.get(hash)));
            sb.append('\n');
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

    public void addMatch() {
        foundFunctions++;
        totalOriginalFunctions++;
        totalRebuiltFunctions++;
    }

    public void addMismatch() {
        totalOriginalFunctions++;
    }

    public double precision(int depth) {
        return countCorrectSignatures(depth) / totalRebuiltFunctions;
    }

    public double recall(int depth){
        return countCorrectSignatures(depth) / totalOriginalFunctions;
    }

    private double countCorrectSignatures(int depth){
        double correctSignature = 0;
        switch (depth){
            case 0:
                correctSignature = foundFunctions;
                break;
            case 1:
                for (String hash : functionsScore.keySet()) {
                    if (functionsScore.get(hash) != null) {
                        for (double score : functionsScore.get(hash))
                            if (score == AbiComparator.NO_MATCH)
                                break;
                        correctSignature++;
                    }
                }
                break;
            case 2:
                for (String hash : functionsScore.keySet()) {
                    if (functionsScore.get(hash) != null) {
                        for (double score : functionsScore.get(hash))
                            if (score != AbiComparator.COMPLETE_MATCH)
                                break;
                        correctSignature++;
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("depth must be in range [0-2]");
        }
        return correctSignature;
    }

    public int getMatch() {
        return foundFunctions;
    }

    public void addMismatchedRebuiltFunctions(int i) {
        totalRebuiltFunctions += i;
    }

    public int getTotalOriginalFunctions() {
        return totalOriginalFunctions;
    }

    public int getTotalRebuiltFunctions() {
        return totalRebuiltFunctions;
    }
}
