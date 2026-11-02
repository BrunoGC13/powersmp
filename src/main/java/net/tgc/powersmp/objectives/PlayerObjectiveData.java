package net.tgc.powersmp.objectives;

import java.util.HashMap;
import java.util.Map;

public class PlayerObjectiveData {
    private final Map<String, Integer> questSteps = new HashMap<>();
    private final Map<String, Boolean> questRewarded = new HashMap<>();

    public int getStep(String questId) {
        return questSteps.getOrDefault(questId, 0);
    }

    public void setStep(String questId, int step) {
        questSteps.put(questId, step);
    }

    public boolean isRewarded(String questId) {
        return questRewarded.getOrDefault(questId, false);
    }

    public void setRewarded(String questId, boolean value) {
        questRewarded.put(questId, value);
    }

    public Map<String, Integer> getQuestSteps() {
        return questSteps;
    }

    public Map<String, Boolean> getQuestRewarded() {
        return questRewarded;
    }
}
