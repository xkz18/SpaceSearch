package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;


public class graphPanel extends JPanel {
    SimManager sim = GUI.getSim();

    private final int YS = sim.getHeight(); // Height
    private final int SIDE = sim.getWidth();  // Width
    private JButton[][] buttons = new JButton[YS][SIDE];

    private static final int LS = 1;
    private static final int MAR = 3;
    private static final Color GK = Color.BLACK;




    public graphPanel() {
        setBackground(GK);
        setLayout(new GridLayout(YS, LS, MAR, MAR));
        setBorder(BorderFactory.createEmptyBorder(MAR, MAR, MAR, MAR));

        JPanel[][] smallPanels = new JPanel[YS][LS];
        for (int i = 0; i < smallPanels.length; i++) {
            for (int j = 0; j < smallPanels[i].length; j++) {
                smallPanels[i][j] = new JPanel(new GridLayout(LS, LS));
                add(smallPanels[i][j]);
            }
        }

        // image sun
        // Sun jpg figure source: https://www.iemoji.com/view/emoji/183/animals-nature/sun
        ImageIcon sun = new ImageIcon(getClass().getResource("sun.jpg"));
        Image imgSun = sun.getImage() ;
        Image newImgSun = imgSun.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ;
        sun = new ImageIcon( newImgSun );

        // image star
        // Star jpg figure source: https://www.clipartkey.com/view/bhiRRT_emoji-stars-clipart-png-download-iphone-sparkle-emoji/
        ImageIcon star = new ImageIcon(getClass().getResource("star.jpg"));
        Image imgStar = star.getImage() ;
        Image newImgStar = imgStar.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ;
        star = new ImageIcon( newImgStar );


        // image drone
        // Drone figure source: http://90sheji.com/png/159276.html
        BufferedImage droneImg = null;
        try {
            droneImg = ImageIO.read(this.getClass().getResource("drone.jpg"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        Image drone_ = null;



        // image empty
        ImageIcon empty = new ImageIcon(getClass().getResource("empty.jpg"));
        Image imgEmpty = empty.getImage() ;
        Image newImgEmtpy = imgEmpty.getScaledInstance( 50, 50,  java.awt.Image.SCALE_SMOOTH ) ;
        empty = new ImageIcon( newImgEmtpy );



        Boolean isGP = GUI.getIsGP();
        if (isGP == false) {
            for (int i = 0; i < buttons.length; i++) { //Height
                // int panelI = i / SML_SIDE;
                for (int j = 0; j < buttons[i].length; j++) { //Length

                    buttons[i][j] = new JButton("");
                    smallPanels[buttons.length - i - 1][0].add(buttons[i][j]);
                }
            }
        } else {

            for (int i = 0; i < buttons.length; i++) { //Height
                // int panelI = i / SML_SIDE;
                for (int j = 0; j < buttons[i].length; j++) { //Length
                    String droneStatus = sim.getRegionInfo()[j][i];
                    switch (droneStatus) {
                        case "EMPTY":
                            buttons[i][j] = new JButton(empty);
                            smallPanels[buttons.length - i - 1][0].add(buttons[i][j]);
                            break;
                        case "STARS":
                            buttons[i][j] = new JButton(star);
                            smallPanels[buttons.length - i - 1][0].add(buttons[i][j]);
                            break;
                        case "SUN":
                            buttons[i][j] = new JButton(sun);
                            smallPanels[buttons.length - i - 1][0].add(buttons[i][j]);
                            break;
                        default:
                            int droneID = Integer.parseInt(droneStatus);
                            String direction = sim.getDroneDirection_()[droneID];
                            switch (direction) {
                                case "north":
                                    drone_ = rotateImage.rotateImage(droneImg, 0);
                                    Image newImgDrone_n = drone_.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
                                    ImageIcon droneIcon_n = new ImageIcon(newImgDrone_n);

                                    buttons[i][j] = new JButton(sim.getRegionInfo()[j][i], droneIcon_n);
                                    smallPanels[buttons.length - i - 1][0].add(buttons[i][j]);
                                    break;
                                case "northeast":
                                    drone_ = rotateImage.rotateImage(droneImg, 45);
                                    Image newImgDrone_ne = drone_.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
                                    ImageIcon droneIcon_ne = new ImageIcon(newImgDrone_ne);

                                    buttons[i][j] = new JButton(sim.getRegionInfo()[j][i], droneIcon_ne);
                                    smallPanels[buttons.length - i - 1][0].add(buttons[i][j]);
                                    break;
                                case "east":
                                    drone_ = rotateImage.rotateImage(droneImg, 90);
                                    Image newImgDrone_e = drone_.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
                                    ImageIcon droneIcon_e = new ImageIcon(newImgDrone_e);

                                    buttons[i][j] = new JButton(sim.getRegionInfo()[j][i], droneIcon_e);
                                    smallPanels[buttons.length - i - 1][0].add(buttons[i][j]);
                                    break;
                                case "southeast":
                                    drone_ = rotateImage.rotateImage(droneImg, 135);
                                    Image newImgDrone_se = drone_.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
                                    ImageIcon droneIcon_se = new ImageIcon(newImgDrone_se);

                                    buttons[i][j] = new JButton(sim.getRegionInfo()[j][i], droneIcon_se);
                                    smallPanels[buttons.length - i - 1][0].add(buttons[i][j]);
                                    break;
                                case "south":
                                    drone_ = rotateImage.rotateImage(droneImg, 180);
                                    Image newImgDrone_s = drone_.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
                                    ImageIcon droneIcon_s = new ImageIcon(newImgDrone_s);


                                    buttons[i][j] = new JButton(sim.getRegionInfo()[j][i], droneIcon_s);
                                    smallPanels[buttons.length - i - 1][0].add(buttons[i][j]);
                                    break;
                                case "southwest":
                                    drone_ = rotateImage.rotateImage(droneImg, 225);
                                    Image newImgDrone_sw = drone_.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
                                    ImageIcon droneIcon_sw = new ImageIcon(newImgDrone_sw);

                                    buttons[i][j] = new JButton(sim.getRegionInfo()[j][i], droneIcon_sw);
                                    smallPanels[buttons.length - i - 1][0].add(buttons[i][j]);
                                    break;
                                case "west":
                                    drone_ = rotateImage.rotateImage(droneImg, 270);
                                    Image newImgDrone_w = drone_.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
                                    ImageIcon droneIcon_w = new ImageIcon(newImgDrone_w);

                                    buttons[i][j] = new JButton(sim.getRegionInfo()[j][i], droneIcon_w);
                                    smallPanels[buttons.length - i - 1][0].add(buttons[i][j]);
                                    break;
                                case "northwest":
                                    drone_ = rotateImage.rotateImage(droneImg, 315);
                                    Image newImgDrone_nw = drone_.getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH);
                                    ImageIcon droneIcon_nw = new ImageIcon(newImgDrone_nw);

                                    buttons[i][j] = new JButton(sim.getRegionInfo()[j][i], droneIcon_nw);
                                    smallPanels[buttons.length - i - 1][0].add(buttons[i][j]);
                                    break;


                            }


                    }
                }
            }
        }
        }

}




