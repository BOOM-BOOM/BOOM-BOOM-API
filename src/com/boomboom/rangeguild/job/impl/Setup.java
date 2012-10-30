package com.boomboom.rangeguild.job.impl;

import com.boomboom.rangeguild.Main;
import com.boomboom.rangeguild.job.Node;
import com.boomboom.rangeguild.ui.BBRangeGuildGUI;
import com.boomboom.util.SkillData;
import com.boomboom.util.Util;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Tabs;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.widget.Widget;
import org.powerbot.game.bot.Context;

import javax.swing.*;

public class Setup extends Node {

    private String status = "Initializing...";
    private boolean resizeable, launched;
    private BBRangeGuildGUI gui;
    private final org.powerbot.game.api.util.Timer timeout = new org.powerbot.game.api.util.Timer(15000);

    @Override
    public boolean activate() {
        return Game.isLoggedIn() && Players.getLocal() != null && Players.getLocal().isOnScreen()
                && !Widgets.get(1252, 1).visible() && !Widgets.get(1234, 10).visible();
    }

    @Override
    public String status() {
        return status;
    }

    @Override
    public void execute() {
        if (gui == null && !launched) {
            launched = true;
            timeout.reset();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    gui = BBRangeGuildGUI.create();
                }
            });

            final Widget widget = Widgets.get(548);
            for (int i = 0; i < widget.getChildren().length; i++) {
                if (widget.getChild(i).getTextColor() == 16776960) {
                    Main.setCoinsId(i);
                }

                if (widget.getChild(i).getTextureId() == 8958) {
                    Main.setButtonId(i);
                }
            }

            sleep(100);

            if (!Widgets.get(548, Main.getCoinsId()).visible()) {
                if (Widgets.get(548, Main.getButtonId()).click(true)) {
                    for (int i = 0; i < 20 && !Widgets.get(548, Main.getCoinsId()).visible(); i++)
                        sleep(100);
                }
            }

            String money = null;
            if (Inventory.getCount(true, 995) > 200 || Widgets.get(548, Main.getCoinsId()).visible() &&
                    (money = Widgets.get(548, Main.getCoinsId()).getText()) != null && Util.parseMultiplier(money) > 200) {
                if (!Widgets.get(978, 21).visible()) {
                    if (Tabs.getCurrent() != Tabs.OPTIONS)
                        Tabs.OPTIONS.open();

                    if (Tabs.getCurrent() == Tabs.OPTIONS) {
                        if (Widgets.get(261, 22).click(true)) {
                            for (int i = 0; i < 20 && !Widgets.get(978, 21).visible(); i++)
                                sleep(100);
                        }
                    }
                }

                if (Widgets.get(978, 21).visible()) {
                    resizeable = Widgets.get(978, 21).getTextureId() != 3123;
                    sleep(100);
                    org.powerbot.game.api.util.Timer timeout = new org.powerbot.game.api.util.Timer(10000);
                    do {
                        if (Widgets.get(742, 18).visible() && Widgets.get(742, 18).click(true)) {
                            for (int i = 0; i < 20 && Widgets.get(978, 21).visible(); i++)
                                sleep(100);
                        }

                        Tabs.INVENTORY.open();
                        sleep(100);
                    } while (Widgets.get(978, 21).visible() && timeout.isRunning());
                }

                while (Context.get().getScriptHandler().isActive() && gui == null)
                    sleep(100);
            } else if (Widgets.get(548, Main.getCoinsId()).visible() && money == null) {
                Context.get().getScriptHandler().log.info("You do not have any coins with you.");
                status = "Shutting down...";
                gui.dispose();
                Context.get().getScriptHandler().shutdown();
            }
        }  else if (gui == null && timeout.isRunning()) {
            sleep(500);
        } else if (gui == null && !timeout.isRunning()) {
            Context.get().getScriptHandler().log.info("GUI could not be initialized, shutting down.");
            status = "Shutting down...";
            Context.get().getScriptHandler().shutdown();
        } else if (gui != null && launched) {
            if (gui.isVisible() && gui.isRunning()) {
                sleep(500);
            } else if (!gui.isVisible() && gui.isRunning()) {
                status = "Shutting down...";
                Context.get().getScriptHandler().shutdown();
            } else {
                Main.getJobs().getNodes().remove(this);

                if (gui.isCompeting()) {
                    Main.getJobs().getNodes().add(new Combat());
                    Main.getJobs().getNodes().add(new Failsafe(resizeable));

                    if (gui.isAntiban())
                        Main.getJobs().getNodes().add(new Antiban(gui.isSkillAntiban(), gui.isMouseAntiban(), gui.isAfkMode()));

                    Main.getJobs().getNodes().add(new Equip(gui.isEquippingArrows()));
                    Main.getJobs().getNodes().add(new Compete());
                    Main.getJobs().getNodes().add(new Shoot(gui.isSpamClicking()));
                } else {
                    Main.getJobs().getNodes().add(new Exchange(gui.getExchangeIndex(), gui.getAmount()));
                }

                Main.setSkillData(new SkillData());
            }
        }
    }

}
