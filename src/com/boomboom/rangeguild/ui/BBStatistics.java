package com.boomboom.rangeguild.ui;

import org.powerbot.game.bot.Context;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.lang.String;
import javax.swing.*;
import javax.swing.table.*;

public class BBStatistics extends JFrame {

    private JButton pauseButton;
    private JLabel timeLabel;
    private JTable infoTable;
    
    public BBStatistics() {
        initComponents();
    }

    private void initComponents() {
        JMenuBar menuBar1 = new JMenuBar();
        JMenu menu1 = new JMenu();
        JMenuItem menuItem1 = new JMenuItem();
        pauseButton = new JButton();
        JButton stopButton = new JButton();
        JButton screenshotButton = new JButton();
        JButton resetButton = new JButton();
        JLabel runTimeLabel = new JLabel();
        timeLabel = new JLabel();
        JScrollPane infoPane = new JScrollPane();
        infoTable = new JTable();

        setTitle("BBStatistics");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        final Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        ((GridBagLayout)contentPane.getLayout()).columnWidths = new int[] {6, 56, 6, 56, 6, 56, 6, 56, 4, 0};
        ((GridBagLayout)contentPane.getLayout()).rowHeights = new int[] {6, 20, 24, 36, 4, 0};
        ((GridBagLayout)contentPane.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
        ((GridBagLayout)contentPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

        //======== menuBar1 ========
        {

            //======== menu1 ========
            {
                menu1.setText("Options");

                //---- menuItem1 ----
                menuItem1.setText("Stuff");
                menu1.add(menuItem1);
            }
            menuBar1.add(menu1);
        }
        setJMenuBar(menuBar1);

        //---- pauseButton ----
        pauseButton.setText("Pause");
        pauseButton.setMaximumSize(new Dimension(54, 18));
        pauseButton.setMinimumSize(new Dimension(54, 18));
        contentPane.add(pauseButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 2, 2), 0, 0));

        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pauseButton.getText().equalsIgnoreCase("pause")) {
                    pauseButton.setText("Play");
                    Context.get().getScriptHandler().pause();
                } else if (pauseButton.getText().equalsIgnoreCase("play")) {
                    pauseButton.setText("Pause");
                    Context.get().getScriptHandler().resume();
                }
            }
        });

        //---- stopButton ----
        stopButton.setText("Stop");
        contentPane.add(stopButton, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 2, 2), 0, 0));

        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                StatisticsHandler.setStop(true);
                dispose();
            }
        });

        //---- screenshotButton ----
        screenshotButton.setText("Screenshot");
        contentPane.add(screenshotButton, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 2, 2), 0, 0));

        screenshotButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {
                final BufferedImage image = new BufferedImage(contentPane.getWidth(), contentPane.getHeight(), BufferedImage.TYPE_INT_RGB);
                final Graphics2D g2d = image.createGraphics();
                contentPane.paint(g2d);
                StatisticsHandler.setScreenshot(image);
                StatisticsHandler.setUploadImage(true);
            }
        });

        //---- resetButton ----
        resetButton.setText("Reset");
        resetButton.setEnabled(false);
        contentPane.add(resetButton, new GridBagConstraints(7, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 2, 2), 0, 0));

        //---- runTimeLabel ----
        runTimeLabel.setText("Run time:");
        runTimeLabel.setFont(new Font("Arial", Font.BOLD, 13));
        contentPane.add(runTimeLabel, new GridBagConstraints(1, 2, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 2, 2), 0, 0));

        //---- timeLabel ----
        timeLabel.setText("");
        timeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        contentPane.add(timeLabel, new GridBagConstraints(3, 2, 5, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 2, 2), 0, 0));

        infoPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        infoPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        infoPane.setEnabled(false);
        infoPane.setPreferredSize(new Dimension(300, 71));

        //---- infoTable ----
        infoTable.setFont(new Font("Arial", Font.PLAIN, 11));
        infoTable.setModel(new DefaultTableModel(
                new Object[][] {
                        {"Ranged", "0", "0", "0"},
                        {"Profit", "0", "0", null},
                        {"Tickets", "0", "0", null},
                },
                new String[] {
                        "Title", "Amount", "Hourly", "Levels"
                }
        ));

        TableColumnModel cm = infoTable.getColumnModel();
        for (int i = 0; i < 4; i++)
            cm.getColumn(i).setResizable(false);
        infoTable.setEnabled(false);
        infoPane.setViewportView(infoTable);
        contentPane.add(infoPane, new GridBagConstraints(1, 3, 7, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 2, 2), 0, 0));
        pack();
        setVisible(true);
        setLocationRelativeTo(getOwner());
    }

    public JLabel getTimeLabel() {
        return timeLabel;
    }

    public JTable getTable() {
        return infoTable;
    }
    
}
