package GUI;

// Reference: This rotateImage.class is a work of several authors and was collected and posted online
// Code source: https://flyingdogz.wordpress.com/2008/02/11/image-rotate-in-java-2-easier-to-use/
// Source of each methods was indicated before each code block

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.PixelGrabber;
import javax.swing.ImageIcon;

public class rotateImage {

    public static Image rotateImage(Image img,double degree){
        BufferedImage bufImg = toBufferedImage(img);
        double angle = Math.toRadians(degree);

        return tilt(bufImg,angle);
    }

    public static BufferedImage tilt(BufferedImage image, double angle) {
        double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
        int w = image.getWidth(), h = image.getHeight();
        int neww = (int)Math.floor(w*cos+h*sin), newh = (int)Math.floor(h*cos+w*sin);
        GraphicsConfiguration gc = getDefaultConfiguration();
        BufferedImage result = gc.createCompatibleImage(neww, newh, Transparency.TRANSLUCENT);
        Graphics2D g = result.createGraphics();
        g.translate((neww-w)/2, (newh-h)/2);
        g.rotate(angle, w/2, h/2);
        g.drawRenderedImage(image, null);
        g.dispose();
        return result;
    }

    public static GraphicsConfiguration getDefaultConfiguration() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        return gd.getDefaultConfiguration();
    }

    // http://www.exampledepot.com/egs/java.awt.image/Image2Buf.html
    public static BufferedImage toBufferedImage(Image image) {
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }

        image = new ImageIcon(image).getImage();
        boolean hasAlpha = hasAlpha(image);

        BufferedImage bimage = null;
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        try {
            int transparency = Transparency.OPAQUE;
            if (hasAlpha) {
                transparency = Transparency.BITMASK;
            }

            GraphicsDevice gs = ge.getDefaultScreenDevice();
            GraphicsConfiguration gc = gs.getDefaultConfiguration();
            bimage = gc.createCompatibleImage(
                    image.getWidth(null), image.getHeight(null), transparency);
        } catch (HeadlessException e) {
        }

        if (bimage == null) {

            int type = BufferedImage.TYPE_INT_RGB;
            if (hasAlpha) {
                type = BufferedImage.TYPE_INT_ARGB;
            }
            bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
        }


        Graphics g = bimage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bimage;
    }

    // http://www.exampledepot.com/egs/java.awt.image/HasAlpha.html
    public static boolean hasAlpha(Image image) {
        if (image instanceof BufferedImage) {
            BufferedImage bimage = (BufferedImage)image;
            return bimage.getColorModel().hasAlpha();
        }

        PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
        try {
            pg.grabPixels();
        } catch (InterruptedException e) {
        }


        ColorModel cm = pg.getColorModel();
        return cm.hasAlpha();
    }
}

// Reference: This above rotateImage.class is a work of several authors and was collected and posted online
// Code source: https://flyingdogz.wordpress.com/2008/02/11/image-rotate-in-java-2-easier-to-use/