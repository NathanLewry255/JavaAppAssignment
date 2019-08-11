import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
Assignment 1 - 17370553 (Nathan Lewry)
 */
public class JFPanel extends JPanel {
    public BufferedImage image = null;

    public void getImage(File imageFile) {
        //Reads file, then try/catches the file if image. Should always be an image because of the filters I set
        try {
            image = ImageIO.read(imageFile);
            //System.out.println("X: " + image.getWidth() + " Y: " + image.getHeight());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void paintComponent(Graphics g) {
        //Refresh Image
        Graphics2D g2 = (Graphics2D)g; //Gets Graphics passed in from repaint()
        g2.clearRect(0,0,this.getWidth(), this.getHeight()); //To clear the stuff in rect. Otherwise the open dialog renders under the image (Depending on GPU)
        if (image != null) {
            g2.drawImage(image, 0, 50, null); //Draw image to JPanel
        }
    }
}
