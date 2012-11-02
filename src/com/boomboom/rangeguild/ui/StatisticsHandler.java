package com.boomboom.rangeguild.ui;

import com.boomboom.rangeguild.Main;
import com.boomboom.rangeguild.job.impl.Setup;
import com.boomboom.util.SkillData;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.powerbot.core.script.job.LoopTask;
import org.powerbot.core.script.job.Task;
import org.powerbot.game.api.methods.Environment;
import org.powerbot.game.api.methods.tab.Skills;
import org.powerbot.game.bot.Context;

import javax.imageio.ImageIO;
import javax.swing.table.TableModel;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StatisticsHandler extends LoopTask {

    private static boolean uploadImage, stop;
    private static BufferedImage screenshot;

    private final SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
    private String imageDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
    private final ArrayList<String> links = new ArrayList<String>();
    private boolean first;

    @Override
    public int loop() {
        if (Setup.getStatistics() != null) {
            Setup.getStatistics().getTimeLabel().setText(com.boomboom.util.Util.format(Main.getSkillData().getTimer().getElapsed(), true));
            final TableModel model = Setup.getStatistics().getTable().getModel();
            model.setValueAt(Main.getSkillData().experience(Skills.RANGE), 0, 1);
            model.setValueAt(Main.getSkillData().experience(SkillData.Rate.HOUR, Skills.RANGE), 0, 2);
            model.setValueAt(Main.getSkillData().level(Skills.RANGE), 0, 3);
        }

        if (stop) {
            Context.get().getScriptHandler().shutdown();
            System.out.println("Script stopped, shutting down.");
            return -1;
        }

        if (uploadImage) {
            uploadImage = false;
            getContainer().submit(new Task() {
                @Override
                public void execute() {
                    final String name = date.format(new Date());
                    final File file = new File(Environment.getStorageDirectory(), name + ".png");
                    final BufferedImage image = screenshot;

                    try {
                        ImageIO.write(image, "png", file);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("Could not save image.");
                    }

                    final StringBuilder stringBuilder = new StringBuilder();
                    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    try {
                        ImageIO.write(image, "png", outputStream);
                    } catch(final IOException e) {
                        System.out.println("Failed to upload screenshot.");
                    }

                    try {
                        stringBuilder.append(URLEncoder.encode("image", "UTF-8")).append("=").append(URLEncoder.encode(
                                Base64.encode(outputStream.toByteArray()), "UTF-8")).append("&").append(URLEncoder.encode(
                                "key", "UTF-8")).append("=").append(URLEncoder.encode("cb1f37b246a92f1e8790399faeb9dc30", "UTF-8"));
                    } catch(final UnsupportedEncodingException e) {
                        System.out.println("Failed to upload screenshot.");
                    }

                    URLConnection urlConnection = null;
                    OutputStreamWriter streamWriter = null;
                    try {
                        urlConnection = new URL("http://api.imgur.com/2/upload").openConnection();
                        urlConnection.setDoOutput(true);

                        streamWriter = new OutputStreamWriter(urlConnection.getOutputStream());
                        streamWriter.write(stringBuilder.toString());
                    } catch(final IOException e) {
                        System.out.println("Failed to upload screenshot.");
                    } finally {
                        if(streamWriter != null) {
                            try {
                                streamWriter.flush();
                                streamWriter.close();
                            } catch(final IOException ignored) { }
                        }
                    }

                    BufferedReader bufferedReader = null;
                    try {
                        bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                        stringBuilder.replace(0, stringBuilder.length(), "");

                        String currentLine;
                        while((currentLine = bufferedReader.readLine()) != null)
                            stringBuilder.append(currentLine);
                    } catch(final IOException e) {
                        System.out.println("Failed to upload screenshot.");
                    } finally {
                        if(bufferedReader != null) {
                            try {
                                bufferedReader.close();
                            } catch(IOException ignored) { }
                        }
                    }

                    final Matcher matcher = Pattern.compile("<original>(.+)</original>").matcher(stringBuilder.toString());
                    if (matcher.find()) {
                        final String link = matcher.group(1);

                        System.out.println("Screenshot uploaded. Link: " + link);
                        links.add(name + " - " + link);

                        com.boomboom.util.Util.openURL(link);

                        final String testDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                        if (!imageDate.equals(testDate))
                            imageDate = testDate;

                        File list = new File(Environment.getStorageDirectory(), imageDate + ".txt");
                        try {
                            if (list.createNewFile())
                                System.out.println(list.getAbsolutePath() + " created.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (list.exists()) {
                            try {
                                ArrayList<String> origLinks = new ArrayList<String>();

                                if (!first) {
                                    BufferedReader reader = new BufferedReader(new FileReader(list));
                                    String line;

                                    while ((line = reader.readLine()) != null) {
                                        origLinks.add(line);
                                    }
                                }

                                BufferedWriter writer2 = new BufferedWriter(new FileWriter(list));

                                if (!first) {
                                    first = true;
                                    for (String origLink : origLinks) {
                                        writer2.write(origLink);
                                        writer2.newLine();
                                    }
                                }

                                for (int i2 = 0; i2 < links.size(); i2++) {
                                    writer2.write(links.get(i2));
                                    if (i2 < links.size() -1)
                                        writer2.newLine();
                                }

                                writer2.flush();
                                writer2.close();
                            } catch (IOException e) {
                                System.out.println("Failed to upload screenshot.");
                            }
                        }
                    }
                }
            });
        }
        return 500;
    }

    public static void setUploadImage(final boolean uploadImage) {
        StatisticsHandler.uploadImage = uploadImage;
    }

    public static void setScreenshot(final BufferedImage screenshot) {
       StatisticsHandler.screenshot = screenshot;
    }

    public static void setStop(final boolean stop) {
        StatisticsHandler.stop = stop;
    }

}
