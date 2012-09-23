package com.boomboom.rangeguild.job.impl;

import com.boomboom.rangeguild.job.Node;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.SceneEntities;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.SceneObject;

public class Combat extends Node {

    private String status = "Initializing...";
    private static boolean initialized;
    private static final Tile SPOT = new Tile(2670, 3418, 0);

    @Override
    public boolean activate() {
        return Game.isLoggedIn() && Players.getLocal().isInCombat() || initialized;
    }

    @Override
    public String status() {
        return status;
    }

    @Override
    public void execute() {
        if (!initialized)
            initialized = true;

        if (Game.getPlane() == 0) {
            if (Widgets.get(564, 0).visible()) {
                if (Widgets.get(564, 15).click(true)) {
                    for (int i = 0; i < 25 && Widgets.get(564, 0).visible(); i++)
                        sleep(100);
                }
            } else {
                final SceneObject ladder = SceneEntities.getNearest(2511);
                if (ladder != null) {
                    if (Calculations.distance(Players.getLocal().getLocation(), ladder.getLocation()) < 8 && ladder.isOnScreen()) {
                        status = "Climbing up ladder...";
                        if (ladder.interact("Climb-up")) {
                            for (int i = 0; i < 25 && Game.getPlane() == 0 && !Widgets.get(564, 0).visible(); i++)
                                sleep(100);
                        }
                    } else if (Calculations.distance(Players.getLocal().getLocation(), ladder.getLocation()) < 8 && !ladder.isOnScreen())
                        Camera.turnTo(ladder);
                    else {
                        status = "Walking towards ladder...";
                        if (Walking.walk(ladder.getLocation())) {
                            for (int i = 0; i < 25 && Calculations.distance(Players.getLocal().getLocation(), ladder.getLocation()) > 1; i++) {
                                if (Players.getLocal().isMoving())
                                    i = 0;
                                sleep(100);
                            }
                        }
                    }
                }
            }
        } else {
            final SceneObject ladder = SceneEntities.getNearest(2512);
            if (ladder != null) {
                if (ladder.isOnScreen()) {
                    status = "Climbing down ladder...";
                    if (ladder.interact("Climb-down")) {
                        for (int i = 0; i < 25 && Game.getPlane() != 0; i++)
                            sleep(100);

                        if (Game.getPlane() == 0) {
                            initialized = false;
                            status = "Walking towards spot...";
                            if (Walking.walk(SPOT)) {
                                for (int i = 0; i < 25 && Calculations.distance(Players.getLocal().getLocation(), SPOT) > 1; i++) {
                                    if (Players.getLocal().isMoving())
                                        i = 0;
                                    sleep(100);
                                }
                            }
                        }
                    }
                } else
                    Camera.turnTo(ladder);
            }
        }
    }

    public static void initialize() {
        initialized = true;
    }

    public static boolean isInitialized() {
        return initialized;
    }

}
