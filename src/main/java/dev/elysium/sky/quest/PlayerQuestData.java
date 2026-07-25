package dev.elysium.sky.quest;

import java.util.*;

public class PlayerQuestData {

    private final UUID uuid;
    // questId -> objectiveIndex -> progress hien tai
    private final Map<String, Map<Integer, Integer>> progress  = new HashMap<>();
    private final Set<String>                        completed = new HashSet<>();

    public PlayerQuestData(UUID uuid) { this.uuid = uuid; }

    public int getProgress(String questId, int objIndex) {
        return progress.getOrDefault(questId, Collections.emptyMap())
                       .getOrDefault(objIndex, 0);
    }

    public void addProgress(String questId, int objIndex, int amount) {
        progress.computeIfAbsent(questId, k -> new HashMap<>())
                .merge(objIndex, amount, Integer::sum);
    }

    public void complete(String questId) {
        completed.add(questId);
        progress.remove(questId);
    }

    public boolean isCompleted(String questId)              { return completed.contains(questId); }
    public Set<String> getCompleted()                       { return completed; }
    public Map<String, Map<Integer, Integer>> getAllProgress(){ return progress; }
    public UUID getUuid()                                   { return uuid; }
}
