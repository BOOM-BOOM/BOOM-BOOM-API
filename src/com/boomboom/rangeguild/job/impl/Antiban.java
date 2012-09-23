package com.boomboom.rangeguild.job.impl;

import com.boomboom.rangeguild.job.Node;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.util.Timer;

public class Antiban extends Node {

    private String status = "Initializing...";
    private final Timer timer = new Timer(0);
    private final boolean mouseAnti, skillAnti, afkMode;

    public Antiban (final boolean mouseAnti, final boolean skillAnti, final boolean afkMode) {
        this.mouseAnti = mouseAnti;
        this.skillAnti = skillAnti;
        this.afkMode = afkMode;
        timer.setEndIn(Random.nextInt(300000, 900000));
    }

    @Override
    public boolean activate() {
        return !timer.isRunning();
    }

    @Override
    public String status() {
        return status;
    }

    @Override
    public void execute() {
        timer.setEndIn(Random.nextInt(300000, 900000));

        if (mouseAnti && Random.nextInt(0, 100) <= 25) {
            status = "Moving mouse randomly...";
            Timer moveTimer = new Timer(Random.nextInt(500, 1500));
            while (moveTimer.isRunning())
                Mouse.move((int) Mouse.getLocation().getX() + Random.nextInt(-8, 8), (int) Mouse.getLocation().getY() + Random.nextInt(-8, 8));
        }

        if (skillAnti && Random.nextInt(0, 100) <= 25) {
            status = "Checking skill...";
            //checkingSkills = true;
            if (!Tabs.getCurrent().equals(Tabs.STATS)) {
                Tabs.STATS.open();
                for (int i = 0; i < 20 && !Tabs.getCurrent().equals(Tabs.STATS); i++)
                    sleep(100);
            }

            if (Tabs.getCurrent().equals(Tabs.STATS)) {
                Widgets.get(320, 36).hover();
                sleep(Random.nextInt(1400, 2100));
            }
            //checkingSkills = false;
        }

        if (afkMode && Random.nextInt(0, 100) <= 50) {
            status = "AFK...";
            Timer afkTimer = new Timer(Random.nextInt(10000, 30000));
            while (afkTimer.isRunning())
                sleep(100);
        }
    }

}
