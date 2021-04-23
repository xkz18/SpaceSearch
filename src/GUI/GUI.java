package GUI;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class GUI extends JFrame{
    private JPanel SpaceSearch;
    private JButton startButton;
    private JButton nextButton;
    private JButton resumeButton;
    private JButton stopButton;
    private JTextField id;
    private JTextField position;
    private JTextField direction;
    private JLabel Info;
    private JTextField energy;
    private JTextArea stepInfo;
    private JLabel actionResult;
    private JButton continueButton;
    private JButton chooseFileButton;
    private JPanel graphPanel_b;
    private JTextField action;

    private graphPanel panel1;

    // path
    private static File testFile;

    // Boolean
    private static boolean isRun = false;


    // FileChoose
    private static boolean isFileChoosed = false;

    private final JFileChooser fileChooser;

    // Sim
    private static SimManager sim;
    public static void setSim(SimManager inputSim){
        //sim = UI.getSim();
        sim = inputSim;
    }
    public static SimManager getSim(){
        return sim;
    }

    //isGP
    private static boolean isGP = true;
    public static void setIsGP(boolean b){
        isGP = b;
    }
    public static boolean getIsGP(){
        return isGP;
    }


    public GUI(String title) {
        super(title);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.add(SpaceSearch, BorderLayout.WEST);
        //this.setContentPane(SpaceSearch);
        this.pack();


        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (GUI.isRun) return;
                if (!isFileChoosed) {
                    JOptionPane.showMessageDialog(null, "Please select file first");
                    return;
                }

                retStatistics();
                SimManager sim = UI.restart(testFile);
                stepInfo.setText(sim.getDroneStatus());
                setSim(sim);
                isGP = true;
                updatePanel(sim,isGP);
                GUI.isRun = true;
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!GUI.isRun) {
                    JOptionPane.showMessageDialog(null, "Please first start/Resume the simulation");
                }
                else {

                    if (!sim.isTerminated()) {
                        setSim(UI.step());
                        isGP = true;
                        updatePanel(sim,isGP);
                        setStatistics(sim);
                    }
                    else{
                        setSim(UI.step());

                        isGP = false;
                        updatePanel(sim,isGP);

                        String result = sim.finalReport();
                        JOptionPane.showMessageDialog(null, result);
                        GUI.isRun = false;
                        isFileChoosed = false;
                        retStatistics();
                    }
                }
            }
        });
        resumeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (GUI.isRun) {
                    return;
                }

                SimManager sim = UI.Resume();
                if (sim == null || sim.isTerminated()) {
                    JOptionPane.showMessageDialog(null, "Please choose file to start");
                    return;
                }

                GUI.isRun = true;
                GUI.isFileChoosed = true;

                setSim(sim);
                isGP = true;
                updatePanel(sim,isGP);
                setStatistics(sim);
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!GUI.isRun) {
                    JOptionPane.showMessageDialog(null, "Please first start/Resume the simulation");
                    return;
                }

                GUI.isRun = false;
                // For graph Panel
                isGP = false;
                updatePanel(sim,isGP);
                //
                retStatistics();
                String result = sim.finalReport();
                JOptionPane.showMessageDialog(null, result);
            }
        });

        continueButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!GUI.isRun) {
                    JOptionPane.showMessageDialog(null, "Please Start/Resume the simulation");
                    return;
                }

                SwingWorker worker = new SwingWorker<Integer, Void>() {
                    @Override
                    public Integer doInBackground() {
                        SimManager sim = UI.step();
                        while (!sim.isTerminated()) {
                            setSim(sim);
                            updatePanel(sim, isGP);
                            setStatistics(sim);

                            try
                            {
                                Thread.sleep(500);
                            }
                            catch(InterruptedException ex)
                            {
                                Thread.currentThread().interrupt();
                            }

                            sim = UI.step();
                        }

                        if (sim.isTerminated()) {
                            isGP = false;
                            setSim(sim);
                            updatePanel(sim,isGP);

                            String result = sim.finalReport();
                            JOptionPane.showMessageDialog(null, result);
                            GUI.isRun = false;
                            isFileChoosed = false;
                            retStatistics();
                        }
                        return 1;
                    }
                };

                worker.execute();
            }
        });

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    testFile = fileChooser.getSelectedFile();
                    isFileChoosed = true;
                }else {
                    JOptionPane.showMessageDialog(null, "Please choose a file");
                    return;
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new GUI("SpaceSearch");
        // frame.setSize(new Dimension(1000, 800));
        frame.setVisible(true);
    }

    private void setStatistics(SimManager simManager) {
        if (simManager == null) return;

        Drone curDrone = simManager.getCurDrone();
        this.id.setText(String.valueOf(simManager.getCurDroneIdForUi()));
        this.direction.setText(curDrone.getDroneDirection());
        this.position.setText("Location X: " + curDrone.getDroneX() + "; Y: " + curDrone.getDroneY());
        this.energy.setText(String.valueOf(curDrone.getDroneEnergyLevel()));
        this.action.setText(simManager.GetTrackAction());
        this.stepInfo.setText(simManager.getDroneStatus());
    }

    private void updatePanel(SimManager simManager, Boolean isGP) {
            if (isGP == false) {
                this.getGlassPane().setVisible(true);
                // graphPanel_st panel1 = new graphPanel_st();
                // this.add(panel1, BorderLayout.EAST);
                // panel1.validate();
                // panel1.repaint();


            } else {

                this.getGlassPane().setVisible(false);
                graphPanel panel1 = new graphPanel();
                this.add(panel1, BorderLayout.EAST);
                // graphPanel_b.add(panel1);
                panel1.validate();
                panel1.repaint();
            }


    }

    private void retStatistics() {
        this.id.setText("");
        this.direction.setText("");
        this.position.setText("");
        this.energy.setText("");
        this.stepInfo.setText("");
        this.action.setText("");
    }
}
