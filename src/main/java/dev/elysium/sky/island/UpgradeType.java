package dev.elysium.sky.island;

public enum UpgradeType {

    //                    Display name          Max  Cost[0 unused, 1..max]
    SIZE        ("Kich thuoc dao",          5, new long[]{0, 5_000,  15_000, 35_000, 75_000,  150_000}),
    SPAWNER_LIMIT("Gioi han Spawner",       5, new long[]{0, 3_000,   8_000, 20_000, 50_000,  100_000}),
    MEMBER_LIMIT ("So thanh vien",          5, new long[]{0, 2_000,   6_000, 15_000, 35_000,   80_000}),
    GENERATOR_TIER("Cap do Generator",      3, new long[]{0, 10_000, 30_000, 70_000});

    private final String displayName;
    private final int    maxLevel;
    private final long[] costs;

    UpgradeType(String displayName, int maxLevel, long[] costs) {
        this.displayName = displayName;
        this.maxLevel    = maxLevel;
        this.costs       = costs;
    }

    /** Chi phi de nang len 'toLevel'. Long.MAX_VALUE neu vuot max. */
    public long getCost(int toLevel) {
        if (toLevel <= 0 || toLevel >= costs.length) return Long.MAX_VALUE;
        return costs[toLevel];
    }

    public String getDisplayName() { return displayName; }
    public int    getMaxLevel()    { return maxLevel; }
}
