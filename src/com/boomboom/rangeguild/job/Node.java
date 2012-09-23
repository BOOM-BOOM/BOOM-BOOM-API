package com.boomboom.rangeguild.job;

import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.util.Random;

public abstract class Node extends Task {
	public abstract boolean activate();

    public abstract String status();

    public static void sleep(final int a, final int b) {
        sleep(Random.nextInt(a, b));
    }
}
