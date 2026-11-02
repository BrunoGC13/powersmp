package net.tgc.powersmp.skills;

public class Skill {
    private String name;
    private int target;
    private int progress;
    public Skill(String name, int target) {
        this.name = name;
        this.target = target;
        this.progress = progress;
    }
    public String getName() {
        return name;
    }
    public int getTarget() {
        return target;
    }
}
