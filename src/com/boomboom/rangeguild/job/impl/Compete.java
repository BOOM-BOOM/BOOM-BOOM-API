package com.boomboom.rangeguild.job.impl;

import com.boomboom.rangeguild.Main;
import com.boomboom.rangeguild.job.Node;
import com.boomboom.util.Util;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Settings;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.bot.Context;

public class Compete extends Node {

    private String status = "Initializing...";

    @Override
    public boolean activate() {
        return Game.isLoggedIn() && (Settings.get(156) == 0 || Widgets.get(1184, 0).visible() || Widgets.get(1188, 3).visible());
    }

    @Override
    public String status() {
        return status;
    }

    @Override
    public void execute() {
        String money;
        if (Inventory.getCount(true, 995) > 200 || Widgets.get(548, Main.getCoinsId()).visible() &&
                (money = Widgets.get(548, Main.getCoinsId()).getText()) != null && Util.parseMultiplier(money) > 200) {
            if (!Widgets.get(1188, 3).visible()) {
                status = "Talking to Judge...";
                final NPC judge = NPCs.getNearest(693);
                if (judge != null) {
                    if (judge.isOnScreen()) {
                        if (judge.interact("Compete", "Competition Judge")) {
                            for (int i = 0; i < 20 && !Widgets.get(1188, 3).visible(); i++)
                                sleep(100);
                        }
                    } else
                        Camera.turnTo(judge);
                }
            }

            if (Widgets.get(1188, 3).visible()) {
                status = "Paying Judge...";
                Shoot.clear();
                if (Widgets.get(1188, 3).click(true)) {
                    for (int i = 0; i < 25 && Widgets.get(1188, 3).visible(); i++)
                        sleep(100);
                }
            }
        } else if (!Widgets.get(548, Main.getCoinsId()).visible()) {
            if (Widgets.get(548, Main.getButtonId()).click(true)) {
                for (int i = 0; i < 20 && !Widgets.get(548, Main.getCoinsId()).visible(); i++)
                    sleep(100);
            }
        } else {
            Context.get().getScriptHandler().log.info("You do not have any coins with you.");
            status = "Shutting down...";
            Context.get().getScriptHandler().shutdown();
        }
    }

}
