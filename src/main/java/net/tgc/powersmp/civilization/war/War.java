package net.tgc.powersmp.civilization.war;

import net.tgc.powersmp.civilization.Civilization;

import java.util.UUID;

public class War {
    private Civilization attacker;
    private Civilization target;
    private boolean won;
    private UUID id;

    public War(Civilization attacker, Civilization target, UUID warID) {
        this.attacker = attacker;
        this.target = target;
        this.id = warID;
        this.won = false;
    }

    public Civilization getAttacker() {
        return attacker;
    }

    public Civilization getTarget() {
        return target;
    }

    public boolean isWon() {
        return won;
    }

    public UUID getId() {
        return id;
    }
}
