package com.boomboom.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/** Utility for parsing skill information from the RuneScape Hiscores.
 *
 * @author BOOM BOOM
 */
public class Hiscores {

    private final String name, skillInfo;
    private static final String WEBSITE = "http://hiscore.runescape.com/index_lite.ws?player=";
    private static final String[] SKILLS = { "Overall", "Attack", "Defence", "Strength", "Constitution",
            "Ranged", "Prayer", "Magic", "Cooking", "WoodCutting",
            "Fletching", "Fishing", "Firemaking", "Crafting", "Smithing",
            "Mining", "Herblore", "Agility", "Thieving", "Slayer",
            "Farming", "Runecrafting", "Hunter", "Construction", "Summoning", "Dungeoneering" };

    private Hiscores (final String name, final String skillInfo) {
        this.name = name;
        this.skillInfo = skillInfo;
    }

    public static Hiscores lookup(String name) {
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(WEBSITE + name).openStream()));
            final StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null)
                builder.append(line).append(":");
            return new Hiscores(name, builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getRank(final String skill) {
        if (isSkill(skill)) {
            final int placement = getPlacement(skill);
            if (placement == -1)
                return -2;
            else  {
                final String[] skillsInfo = skillInfo.split(":");
                return Integer.parseInt(skillsInfo[placement].split(",")[0]);
            }
        }
        return -1;
    }

    public int getRank(final int skill) {
        return getRank(SKILLS[skill]);
    }

    public int getLevel(final String skill) {
        if (isSkill(skill)) {
            final int placement = getPlacement(skill);
            if (placement == -1)
                return -2;
            else  {
                final String[] skillsInfo = skillInfo.split(":");
                return Integer.parseInt(skillsInfo[placement].split(",")[1]);
            }
        }
        return -1;
    }

    public int getLevel(final int skill) {
        return getLevel(SKILLS[skill]);
    }

    public int getExperience(final String skill) {
        if (isSkill(skill)) {
            final int placement = getPlacement(skill);
            if (placement == -1)
                return -2;
            else  {
                final String[] skillsInfo = skillInfo.split(":");
                return Integer.parseInt(skillsInfo[placement].split(",")[2]);
            }
        }
        return -1;
    }

    public int getExperience(final int skill) {
        return getLevel(SKILLS[skill]);
    }

    private boolean isSkill(final String checkSkill) {
        for (String skill : SKILLS) {
            if (skill.equalsIgnoreCase(checkSkill))
                return true;
        }
        return false;
    }

    private int getPlacement (final String checkSkill) {
        for (int i = 0; i < SKILLS.length; i++) {
            if (SKILLS[i].equalsIgnoreCase(checkSkill))
                return i;
        }
        return -1;
    }

}
