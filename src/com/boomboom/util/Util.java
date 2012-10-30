package com.boomboom.util;

import org.powerbot.game.api.methods.Environment;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.NumberFormat;

public class Util {

    public static BufferedImage getImage(final String name, final String URL, final String format) {
        final File file = new File(Environment.getStorageDirectory(), name);
        try {
            if (!file.exists()) {
                final BufferedImage image = ImageIO.read(new java.net.URL(URL));
                ImageIO.write(image, format, file);
                return image;
            } else
                return ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String format(final long runTime, final boolean day) {
        long secs = runTime / 1000 % 60;
        long mins = runTime / 1000 / 60 % 60;
        long hours = runTime / 1000 / 60 / 60 % 24;
        long days = runTime / 1000 / 60 / 60 / 24;
        String output = "";
        if (day && days > 0)
            output += (days + " Day" + (days > 1 ? "s " : " "));
        if (hours > 0)
            output += ((hours < 10 ? "0" : "") + hours + " Hour" + (hours != 1 ? "s " : " "));
        if (mins > 0)
            output += ((mins < 10 ? "0" : "") + mins + " Minute" + (mins != 1 ? "s " : " "));
        output += ((secs < 10 ? "0" : "") + secs + " Second" + (secs != 1 ? "s" : ""));
        return output;
    }

    public static String formatCommas(final int i) {
        return NumberFormat.getIntegerInstance().format(i);
    }

    public static int parseMultiplier(String value) {
        value = value.toLowerCase();
        if (value.contains("b") || value.contains("m") || value.contains("k")) {
            return (int) (Double.parseDouble(value.substring(0, value.length() - 1))
                    * (value.endsWith("b") ? 1000000000D : value.endsWith("m") ? 1000000
                    : value.endsWith("k") ? 1000 : 1));
        } else {
            return Integer.parseInt(value);
        }
    }

    public static void openURL(final String link) {
        try {
            Desktop.getDesktop().browse(new URI(link));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
