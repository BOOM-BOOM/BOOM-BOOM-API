package com.boomboom.rangeguild.job.impl;

import com.boomboom.rangeguild.job.Node;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;

public class Failsafe extends Node {

    private String status = "Initializing...";
    private final boolean resizeable;

    public Failsafe(final boolean resizeable) {
        this.resizeable = resizeable;
    }

    @Override
    public boolean activate() {
        final int yaw = Camera.getYaw();
        return Game.isLoggedIn() && (Camera.getPitch() > (resizeable ? 54 : 40) || (yaw <= 318 && yaw >= 302) || yaw <= 258 || yaw >= 358 || Widgets.get(594, 0).visible());
    }

    @Override
    public String status() {
        return status;
    }

    @Override
    public void execute() {
        if (Camera.getPitch() > (resizeable ? 48 : 8)) {
            status = "Resetting pitch...";
            Camera.setPitch((resizeable ? Random.nextInt(30, 49) : Random.nextInt(4, 9)));
        }

        final int yaw = Camera.getYaw();
        if (yaw <= 318 && yaw >= 302) {
            status = "Resetting yaw...";
            Camera.setAngle(Random.nextBoolean() ? Random.nextInt(320, 335) : Random.nextInt(285, 300));
        }

        if (yaw <= 258) {
            status = "Resetting yaw...";
            Camera.setAngle(Random.nextBoolean() ? Random.nextInt(320, 335) : Random.nextInt(285, 300));
        }

        if (yaw >= 358) {
            status = "Resetting yaw...";
            Camera.setAngle(Random.nextBoolean() ? Random.nextInt(320, 335) : Random.nextInt(285, 300));
        }

        if (Widgets.get(594, 0).visible()) {
            status = "Closing report a player...";
            if (Widgets.get(594, 17).click(true)) {
                for (int i = 0; i < 20 && Widgets.get(594, 0).visible(); i++)
                    sleep(100);
            }
        }
    }

}
