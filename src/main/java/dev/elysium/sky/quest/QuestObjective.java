package dev.elysium.sky.quest;

public class QuestObjective {

    public enum Type {
        BREAK_BLOCK,
        PLACE_BLOCK,
        KILL_MOB,
        COLLECT_ITEM,
        REACH_ISLAND_LEVEL
    }

    private final Type   type;
    private final String target;   // Ten Material hoac EntityType
    private final int    required;

    public QuestObjective(Type type, String target, int required) {
        this.type     = type;
        this.target   = target;
        this.required = required;
    }

    public String getDescription() {
        return switch (type) {
            case BREAK_BLOCK       -> "Pha " + required + "x " + target;
            case PLACE_BLOCK       -> "Dat " + required + "x " + target;
            case KILL_MOB          -> "Giet " + required + "x " + target;
            case COLLECT_ITEM      -> "Thu thap " + required + "x " + target;
            case REACH_ISLAND_LEVEL -> "Dat Island Level " + required;
        };
    }

    public Type   getType()     { return type; }
    public String getTarget()   { return target; }
    public int    getRequired() { return required; }
    }
