package com.sergreen.bowrunner.Game.GameObjects.Targets;

import com.sergreen.bowrunner.Game.GameObjects.GameObject;

/**
 * Created on 22.02.2015 [SerGreen]
 */
public abstract class Target extends GameObject {
    protected  int scoreCost;
    public static enum TargetType { CHICKEN, BIRD, TARGET };
    public final int type;

    protected Target(String id, int type) {
        super(id);
        this.type = type;
    }

    public int getScoreCost() {
        return scoreCost;
    }
}