package com.boomboom.rangeguild.job.impl;

import com.boomboom.rangeguild.job.Node;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.wrappers.node.Item;
import org.powerbot.game.bot.Context;

public class Equip extends Node {

    private String status = "Initializing...";
    private boolean isEquippingArrows;

    public Equip (final boolean isEquippingArrows) {
        this.isEquippingArrows = isEquippingArrows;
    }

    @Override
    public boolean activate() {
        String text;
        return Game.isLoggedIn() &&(isEquippingArrows && Inventory.getCount(882) > 0)
                || Widgets.get(1184, 13).visible() && (text = Widgets.get(1184, 13).getText()) != null && text.contains("bronze arrows");
    }

    @Override
    public String status() {
        return status;
    }

    @Override
    public void execute() {
        if (Inventory.getCount(882) > 0) {
            status = "Equipping arrows...";
            for (final Item item : Inventory.getItems()) {
                if (item.getId() == 882 && item.getWidgetChild().click(true)) {
                    for (int i = 0; i < 15 && Inventory.getCount(882) > 0; i++)
                        sleep(100);
                    return;
                }
            }
        } else if (!isEquippingArrows) {
            Context.get().getScriptHandler().log.info("You do not have any more arrows.");
            status = "Shutting down...";
            Context.get().getScriptHandler().shutdown();
        }
    }

}
