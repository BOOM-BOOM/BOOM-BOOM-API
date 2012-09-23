package com.boomboom.rangeguild.job.impl;

import com.boomboom.rangeguild.job.Node;
import org.powerbot.game.api.methods.Calculations;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Walking;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.node.*;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Random;
import org.powerbot.game.api.wrappers.Tile;
import org.powerbot.game.api.wrappers.node.SceneObject;

import java.awt.*;

public class Shoot extends Node {

    private String status = "Initializing...";
    private boolean isSpamClicking;
    private static final Tile TARGET_TILE = new Tile(2679, 3426, 0);
    private static final Tile SPOT = new Tile(2670, 3418, 0);
    private static int fails;
    private static Point centralPoint;

    public Shoot (final boolean isSpamClicking) {
        this.isSpamClicking = isSpamClicking;
    }

    @Override
    public boolean activate() {
        return Game.isLoggedIn() && !Combat.isInitialized();
    }

    @Override
    public String status() {
        return status;
    }

    @Override
    public void execute() {
        if (Players.getLocal().isInCombat()) {
            Combat.initialize();
            return;
        }

        if (Calculations.distance(Players.getLocal().getLocation(), SPOT) > 4) {
            if (Walking.walk(SPOT)) {
                status = "Walking to spot...";
                for (int i = 0; i < 25 && Calculations.distance(Players.getLocal().getLocation(), SPOT) > 1; i++) {
                    if (Players.getLocal().isMoving())
                        i = 0;
                    sleep(100);
                }
            }
        }

        final SceneObject target = getAt(TARGET_TILE);
        if (target != null) {
            if (target.isOnScreen()) {
                status = "Firing...";

                if (isSpamClicking) {
                    if (interact(target, "Fire-at", Widgets.get(325, 40).visible()))
                        fails = 0;

                    if (fails > 5) {
                        if (Widgets.get(325, 40).visible()) {
                            status = "Closing target...";
                            if (Widgets.get(325, 40).click(true)) {
                                for (int i = 0; i < 20 && Widgets.get(325, 40).visible(); i++)
                                    sleep(100);
                            }
                        }
                    } else if (Widgets.get(325, 40).visible())
                        fails++;
                } else {
                    if (interact(target, "Fire-at", false)) {
                        for (int i = 0; i < 30 && !Widgets.get(325, 40).visible(); i++)
                            sleep(100);
                    }

                    if (Widgets.get(325, 40).visible()) {
                        status = "Closing target...";
                        if (Widgets.get(325, 40).click(true)) {
                            for (int i = 0; i < 20 && Widgets.get(325, 40).visible(); i++)
                                sleep(100);
                        }
                    }
                }
            } else
                Camera.turnTo(target);
        }
    }

    private boolean interact(final SceneObject location, final String action, final boolean open) {
        final Point center = location.getCentralPoint();
        if (centralPoint == null || distanceBetween(Mouse.getLocation(), center) > 4) {
            centralPoint = center;
            if (!Calculations.isOnScreen(centralPoint)) {
                status = "Resetting yaw...";
                Camera.setAngle(Random.nextBoolean() ? Random.nextInt(320, 335) : Random.nextInt(285, 300));
            }
            Mouse.move(center.x, center.y, 4, 4);
        }

        if (open) {
            Mouse.click(true);
            return false;
        }
        return Menu.contains(action) && Menu.select(action);
    }

    private double distanceBetween(final Point current, final Point destination) {
        return Math.sqrt(((current.x - destination.x) * (current.x - destination.x)) + ((current.y - destination.y) * (current.y - destination.y)));
    }

    private SceneObject getAt(final Tile tile) {
        final SceneObject[] locations = SceneEntities.getLoaded(tile);
        return locations.length > 0 ? locations[0] : null;
    }

    public static void clear() {
        fails = 0;
        centralPoint = null;
    }

}
