package com.boomboom.rangeguild;

import com.boomboom.rangeguild.job.Node;
import com.boomboom.rangeguild.job.Tree;
import com.boomboom.rangeguild.job.impl.Setup;
import com.boomboom.util.MousePathPoint;
import com.boomboom.util.SkillData;
import com.boomboom.util.Util;
import org.powerbot.core.event.listeners.PaintListener;
import org.powerbot.core.script.ActiveScript;
import org.powerbot.game.api.Manifest;
import org.powerbot.game.api.methods.Game;
import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.api.util.Random;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.LinkedList;

@Manifest(name = "BBRangeGuild",
        authors = "BOOM BOOM",
        version = 1.04D,
        description = "The ultimate Range Guild script! Over a year in experience!",
        website = "https://www.powerbot.org/community/topic/679291-bbrangeguild-over-a-year-in-range-guilding-experience/",
        topic = 679291)
public class Main extends ActiveScript implements PaintListener {

    private static Tree jobs;
    private static SkillData skillData;

    private BufferedImage paintBar;
    private final double version = Main.class.getAnnotation(Manifest.class).version();
    private final DecimalFormat format = new DecimalFormat("0.00");
    private static final RenderingHints RENDERING_HINTS = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    private static final Color PAINT_TEXT_COLOR1 = new Color(168, 9, 9), BLACK = Color.BLACK, WHITE = Color.WHITE,
            BACKGROUND = new Color(62, 59, 54), BACKGROUND2 = new Color(136, 125, 103), FOREGROUND = new Color(86, 80, 69);
    private static final Font ARIAL_12_BOLD = new Font("Arial", Font.BOLD, 12), ARIAL_11_BOLD = new Font("Arial", Font.BOLD, 11);
    private static final LinkedList<MousePathPoint> MOUSE_PATH_POINTS = new LinkedList<MousePathPoint>();

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
            paintBar = Util.getImage("paint_bar.png", "http://puu.sh/17VqH.png", "png");
        }

        final Node job = jobs.state();
        if (job != null) {
            jobs.set(job);
            getContainer().submit(job);
            job.join();
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

    @Override
    public void onRepaint(Graphics graphics) {
        if (Game.isLoggedIn()) {
            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHints(RENDERING_HINTS);

            g.setColor(new Color(194, 178, 146));
            g.fillRect(0, 0, (int) Game.getDimensions().getWidth(), 50);
            g.setColor(new Color(49, 42, 27));
            g.drawRect(0, 0, (int) Game.getDimensions().getWidth(), 50);

            g.setFont(ARIAL_12_BOLD);
            g.setColor(PAINT_TEXT_COLOR1);
            g.drawString("Time Running: " + (skillData != null ? Util.format(skillData.getTimer().getElapsed(), true) : "Initializing..."), 10, 18);

            drawInfoBox(g, 10, 26, 222, 17);
            g.setFont(ARIAL_11_BOLD);
            g.setColor(WHITE);

            String status = null;
            if (jobs.get() != null)
                status = jobs.get().status();
            g.drawString("Status: " + (status != null ? status : "Null..."), 18, 38);

            drawInfoBox(g, 238, 26, 88, 17);
            g.setFont(ARIAL_11_BOLD);
            g.setColor(WHITE);
            g.drawString("Version: " + format.format(version), 246, 38);

            int percent = 0;
            if (skillData != null)
                percent = Skills.getRealLevel(Skills.RANGE) == 99 ? 100 : skillData.getPercentToNextLevel(Skills.RANGE);
            g.drawImage(paintBar, 335, 4, null);
            g.setColor(new Color(108, 108, 107));
            g.drawRect(344, 13, (int) 4.07 * percent, 22);
            g.setColor(new Color(214, 196, 160));

            int diff = 407 - ((int) 4.07 * percent);
            if (diff - 1 > 0)
                g.fillRect(((int) 4.07 * percent) + 345, 14, diff - 1, 21);

            int change = 0;
            if (skillData != null)
                change = skillData.level(Skills.RANGE);
            String levelInfo = percent + "% To Level " + (Skills.getRealLevel(Skills.RANGE) == 99 ? 99 : Skills.getRealLevel(Skills.RANGE) + 1)
                    + " (" + change + " Gained)";
            g.setFont(ARIAL_12_BOLD);
            g.setColor(BLACK);
            g.drawString(levelInfo, 625 - (int) g.getFontMetrics().getStringBounds(levelInfo, g).getWidth(), 29);
            drawMouse(g);
        }
    }

    private void drawInfoBox(final Graphics g, final int x, final int y, final int width, final int height) {
        g.setColor(BACKGROUND);
        g.fillRect(x, y, width, height);
        g.setColor(BACKGROUND2);
        g.fillRect(x + 1, y + 1, width - 2, height - 2);
        g.setColor(FOREGROUND);
        g.fillRect(x + 2, y + 2, width - 4, height - 4);
    }

    private void drawMouse(final Graphics g) {
        final Point location = Mouse.getLocation();
        while (!MOUSE_PATH_POINTS.isEmpty() && MOUSE_PATH_POINTS.peek().isUp())
            MOUSE_PATH_POINTS.remove();
        Point clientCursor = Mouse.getLocation();
        MousePathPoint mpp = new MousePathPoint(clientCursor.x, clientCursor.y, 1500);
        if (MOUSE_PATH_POINTS.isEmpty() || !MOUSE_PATH_POINTS.getLast().equals(mpp))
            MOUSE_PATH_POINTS.add(mpp);
        MousePathPoint lastPoint = null;
        for (MousePathPoint a : MOUSE_PATH_POINTS) {
            if (lastPoint != null) {
                g.setColor(a.getColor());
                g.drawLine(a.x, a.y, lastPoint.x, lastPoint.y);
            }
            lastPoint = a;
        }

        if (System.currentTimeMillis() - Mouse.getPressTime() < 500)
            g.setColor(new Color(168, 9, 9));
        else
            g.setColor(new Color(32, 95, 0));
        g.fillOval(location.x - 5, location.y - 5, 10, 10);
        g.setColor(Color.BLACK);
        g.drawLine(location.x - 7, location.y, location.x + 7, location.y);
        g.drawLine(location.x, location.y - 7, location.x, location.y + 7);
        g.drawOval(location.x - 5, location.y - 5, 10, 10);
    }
}
