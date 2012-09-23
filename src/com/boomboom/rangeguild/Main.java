package com.boomboom.rangeguild;

import com.boomboom.rangeguild.job.Node;
import com.boomboom.rangeguild.job.Tree;
import com.boomboom.rangeguild.job.impl.Setup;
import com.boomboom.util.SkillData;
import com.boomboom.util.Util;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.util.Random;

import javax.swing.*;

@Manifest(name = "BBRangeGuild",
        authors = "BOOM BOOM",
        version = 1.04D,
        description = "The ultimate Range Guild script! Over a year in experience!",
        website = "https://www.powerbot.org/community/topic/679291-bbrangeguild-over-a-year-in-range-guilding-experience/",
        topic = 679291)
public class Main extends ActiveScript {

    private Node currentJob;
    private static Tree jobs;
    private static SkillData skillData;

    public static Tree getJobs() {
        return jobs;
    }

    public static void setSkillData(final SkillData skillData) {
        Main.skillData = skillData;
    }

    @Override
    public int loop() {
        if (jobs == null) {
            jobs = new Tree(new Node[] { new Setup()});
        }

        final Node job = jobs.state();
        if (job != null) {
            currentJob = job;
            jobs.set(currentJob);
            getContainer().submit(currentJob);
            currentJob.join();
            return Random.nextInt(50, 100);
        }
        return Random.nextInt(100, 250);
    }

    @Override
    public void onStop() {
        if (skillData != null) {
            final int option = JOptionPane.showConfirmDialog(new JFrame(), "Would you like to leave feedback?", "Feedback", JOptionPane.YES_NO_OPTION);
            if (option == 0)
                Util.openURL("https://www.powerbot.org/community/topic/679291-bbrangeguild-over-a-year-in-range-guilding-experience/");
        }
    }

}
