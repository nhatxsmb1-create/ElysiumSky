package dev.elysium.sky.island;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class IslandData {

    private final UUID ownerUuid;
    private String     ownerName;
    private long       points;
    private int        level;
    private final Map<UpgradeType, Integer> upgrades = new EnumMap<>(UpgradeType.class);

    public IslandData(UUID ownerUuid, String ownerName) {
        this.ownerUuid = ownerUuid;
        this.ownerName = ownerName;
        this.points    = 0;
        this.level     = 1;
        for (UpgradeType t : UpgradeType.values()) upgrades.put(t, 0);
    }

    public void addPoints(long amount) {
        this.points += amount;
        recalcLevel();
    }

    private void recalcLevel() {
        this.level = 1;
        while (level < 100 && points >= getLevelRequired(level + 1)) level++;
    }

    /** Points can de len level tiep theo */
    public long getLevelRequired(int lvl) {
        return (long)(500 * Math.pow(1.5, lvl - 1));
    }

    public int  getUpgrade(UpgradeType t)          { return upgrades.getOrDefault(t, 0); }
    public void setUpgrade(UpgradeType t, int lvl) { upgrades.put(t, lvl); }

    public UUID   getOwnerUuid()                       { return ownerUuid; }
    public String getOwnerName()                       { return ownerName; }
    public void   setOwnerName(String n)               { this.ownerName = n; }
    public long   getPoints()                          { return points; }
    public void   setPoints(long p)                    { this.points = p; recalcLevel(); }
    public int    getLevel()                           { return level; }
    public Map<UpgradeType, Integer> getUpgrades()     { return upgrades; }
}
