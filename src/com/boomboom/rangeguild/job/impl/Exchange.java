package com.boomboom.rangeguild.job.impl;

import com.boomboom.rangeguild.job.Node;
import org.powerbot.game.api.methods.Widgets;
import org.powerbot.game.api.methods.interactive.NPCs;
import org.powerbot.game.api.methods.interactive.Players;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.wrappers.interactive.NPC;
import org.powerbot.game.api.wrappers.widget.WidgetChild;
import org.powerbot.game.bot.Context;

public class Exchange extends Node {

    /**
     * @author BOOM BOOM
     */
    public class ExchangeItem {

        private int childId, value, itemId;

        public ExchangeItem (final int childId, final int value, final int itemId) {
            this.childId = childId;
            this.value = value;
            this.itemId = itemId;
        }

        public int getChildId() {
            return childId;
        }

        public int getValue() {
            return value;
        }

        public int getItemId() {
            return itemId;
        }

    }

    private String status = "Initializing...";
    private int amount;
    private ExchangeItem[] items = {
            new ExchangeItem(0, 114, 47),
            new ExchangeItem(2, 1020, 892),
            new ExchangeItem(5, 292, 829)
    };
    private ExchangeItem item = items[0];

    public Exchange (final int index, final int amount) {
        item = items[index];
        this.amount = amount;
    }

    @Override
    public boolean activate() {
        return true;
    }

    @Override
    public String status() {
        return status;
    }

    @Override
    public void execute() {
        if (Widgets.get(278, 0).visible() &&  Widgets.get(512, 0).visible()) {
            status = "Exchanging...";
            for (WidgetChild widgetChild : Widgets.get(512, 0).getChildren()) {
                if (widgetChild.getChildId() == 1464 && (amount > 0 ? maxAmount() : widgetChild.getChildStackSize() < item.getValue())) {
                    Context.get().getScriptHandler().log.info("You do not have enough tickets to exchange. You have: " + widgetChild.getChildStackSize());
                    status = "Shutting down...";
                    Context.get().getScriptHandler().shutdown();
                    return;
                }
            }

            if (Widgets.get(278, 16).getChild(item.getChildId()).interact("Buy")) {
                sleep(100, 300);
            }
        } else {
            status = "Trading...";

            final NPC merchant = NPCs.getNearest(694);
            if (merchant != null) {
                if (merchant.isOnScreen()) {
                    if (merchant.interact("Trade")) {
                        for (int i = 0; i < 25 && !Widgets.get(278, 0).visible() && !Widgets.get(512, 0).visible(); i++) {
                            if (Players.getLocal().isMoving())
                                i = 0;
                            sleep(100);
                        }
                    }
                } else
                    Camera.turnTo(merchant);
            }
        }
    }

    private boolean maxAmount() {
        for (WidgetChild widgetChild : Widgets.get(512, 0).getChildren()) {
            if (widgetChild.getChildId() == item.getItemId())
                return widgetChild.getChildStackSize() >= amount;
        }
        return false;
    }

}
