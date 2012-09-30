package com.boomboom.rangeguild.job;

import org.powerbot.core.script.job.Task;

public abstract class Node extends Task {
	public abstract boolean activate();

    public abstract String status();
}
