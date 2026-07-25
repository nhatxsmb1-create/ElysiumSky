package dev.elysium.sky.quest;

public class QuestReward {

    public enum Type { MONEY, EXP, ISLAND_POINTS }

    private final Type   type;
    private final String value;
    private final double amount;

    public QuestReward(Type type, String value, double amount) {
        this.type   = type;
        this.value  = value;
        this.amount = amount;
    }

    public String getDescription() {
        return switch (type) {
            case MONEY         -> "&6" + (long)amount + " coins";
            case EXP           -> "&a" + (long)amount + " EXP";
            case ISLAND_POINTS -> "&b" + (long)amount + " Island Points";
        };
    }

    public Type   getType()   { return type; }
    public String getValue()  { return value; }
    public double getAmount() { return amount; }
}
