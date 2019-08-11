import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;

/*
Assignment 1 - 17370553 (Nathan Lewry)
 */
public class JFWindow extends JFrame implements ActionListener, MouseMotionListener {
    //Initialize all objects in JFrame
    private JMenuBar m_menu = new JMenuBar(); //Menu bar container

    private JMenu m_file = new JMenu("File"); //File dropdown

    private JMenuItem m_open = new JMenuItem("Open"); //Open File button
    private JMenuItem m_quit = new JMenuItem("Quit"); //Quit button

    private JFPanel m_jfpanel = new JFPanel(); //Image panel

    private JLabel m_rgbInfo = new JLabel("Please load an Image."); //Label with XY RGBA information

    private JButton m_flipX = new JButton("Flip Horizontal"); //Flip X button
    private JButton m_flipY = new JButton("Flip Vertical"); //Flip Y button
    private JButton m_reset = new JButton("Reset Image"); //Reset Image button
    private JButton m_invertColor = new JButton("Invert Color"); //Invert button

    private boolean m_FlippedX = false; //Bool switches for resetting the image
    private boolean m_FlippedY = false;
    private boolean m_Negative = false;

    public JFWindow(){
        this.setTitle("Assignment 1 - 17370553");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Menu buttons and Dropdown
        m_file.add(m_open);
        m_file.add(m_quit);
        m_menu.add(m_file);

        //Buttons and Info Label
        m_jfpanel.add(m_flipX);
        m_jfpanel.add(m_flipY);
        m_jfpanel.add(m_invertColor);
        m_jfpanel.add(m_reset);
        m_jfpanel.add(m_rgbInfo);

        this.add(m_jfpanel);

        //Mouse Listener
        m_jfpanel.addMouseMotionListener(this);

        //Button Listeners
        m_open.addActionListener(this);
        m_quit.addActionListener(this);
        m_flipX.addActionListener(this);
        m_flipY.addActionListener(this);
        m_invertColor.addActionListener(this);
        m_reset.addActionListener(this);

        //Set Menu Bar
        this.setJMenuBar(m_menu);

        this.pack();
        this.setSize(1400, 800);
        this.setVisible(true);
    }
    //Button Listeners
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == m_open){
            OpenFile(); //Open file
        }
        else if(e.getSource() == m_quit){
            System.exit(1); //Quit Button
        }
        else if(e.getSource() == m_flipX){
            m_FlippedX = !m_FlippedX;
            AlterImageFlip(1); //Flip X
        }
        else if(e.getSource() == m_flipY){
            m_FlippedY = !m_FlippedY;
            AlterImageFlip(0); //Flip Y
        }
        else if(e.getSource() == m_invertColor){
            m_Negative = !m_Negative;
            AlterImageInvert(); //Invert
        }
        else if(e.getSource() == m_reset){
            ResetImage(); //Reset
        }
    }
    //Mouse Listeners
    public void mouseDragged(MouseEvent event) { }
    public void mouseMoved(MouseEvent event) {
        int x = event.getX();
        int y = event.getY();
        y -= 50; //Because image is translated -50Y, to keep it under the buttons and label at the top
        if(m_jfpanel.image == null){ //If image is not present
            m_rgbInfo.setText("Please load an Image.");
        }
        else if(m_jfpanel.image != null && x < m_jfpanel.image.getWidth() && y < m_jfpanel.image.getHeight() && y > 0) {
            Color color = new Color(m_jfpanel.image.getRGB(x, y), true);
            m_rgbInfo.setText("X: " + x + "  Y: " + y + "    R:" + color.getRed() + "  G: " + color.getGreen() + "  B: " + color.getBlue() + "    A: " + color.getAlpha()); //Displays XY, RGBA at top of application. Could change location for ease on eyes.
        }
        else if(m_jfpanel.image != null){
            m_rgbInfo.setText("Cursor outside of Image bounds."); //If image is present, but cursor is out of XY
        }
    }
    //Custom Methods
    private void OpenFile(){
        //Self-explanatory. On open click, show open dialog filtering for JPG, JPEG, and PNG files. If File was found, load to JPanel Using JFPanel Class
        JFileChooser tmp = new JFileChooser();
        FileNameExtensionFilter filter_tmp = new FileNameExtensionFilter("Image", "jpg", "png", "jpeg");
        tmp.setFileFilter(filter_tmp);
        int returnVal = tmp.showDialog(new Panel(), "Open");
        if(returnVal == tmp.APPROVE_OPTION){
            File img_tmp = tmp.getSelectedFile();
            m_jfpanel.getImage(img_tmp);
            m_jfpanel.repaint();
        }
    }
    private void AlterImageFlip(int type){
        //This is for flipping the image, if the int passed in is 0, flip Y. If 1, flip X.
        AffineTransform atTmp = new AffineTransform();
        BufferedImage newTmp = new BufferedImage( m_jfpanel.image.getWidth(),  m_jfpanel.image.getHeight(), BufferedImage.TYPE_INT_ARGB); //Create empty image of same size
        Graphics2D gTmp = newTmp.createGraphics(); //Load empty into Graphics2D
        if(type == 0){
            //Flip Y
            atTmp.concatenate(AffineTransform.getScaleInstance(1,-1)); //Set scale to 1,-1
            atTmp.concatenate(AffineTransform.getTranslateInstance(0,-m_jfpanel.image.getHeight())); //Set translate to 0, -height to keep in same place
        }
        else if(type == 1){
            //Flip X
            atTmp.concatenate(AffineTransform.getScaleInstance(-1,1)); //Set scale to -1,1
            atTmp.concatenate(AffineTransform.getTranslateInstance(-m_jfpanel.image.getWidth(), 0)); //Set translate to -width, 0 to keep in same place
        }
        gTmp.transform(atTmp); //Apply transformation
        gTmp.drawImage(m_jfpanel.image, 0, 0, null); //Draw original image to Graphics2D
        gTmp.dispose(); //Delete Graphics2D, dont think this is needed but for peace of mind
        m_jfpanel.image = newTmp; //Replace old image
        m_jfpanel.repaint(); //Refresh
    }
    private void AlterImageInvert(){
        //This is for inverting the colors of the image
        for(int i = 0; i < m_jfpanel.image.getWidth(); i++){ //Run through each Y of each X, could be other way around but works either way
            for(int j = 0; j < m_jfpanel.image.getHeight(); j++){
                Color tmpColorA = new Color(m_jfpanel.image.getRGB(i,j), true); //Gets color of each pixel
                Color tmpColorB = new Color(255 - tmpColorA.getRed(), 255 - tmpColorA.getGreen(), 255 - tmpColorA.getBlue(), tmpColorA.getAlpha()); //Inverts it, keeping alpha the same (Cannot be done with setRBG = 255-getRBG)
                m_jfpanel.image.setRGB(i, j, tmpColorB.getRGB()); //Replace pixel
            }
        }
        m_jfpanel.repaint(); //Refresh
    }
    private void ResetImage(){
        //This is for resetting the image, each time the image if flipped the private bool is switched, therefore keeping track of flips.
        if(m_FlippedX){ //If X is flipped, flip again
            AlterImageFlip(1);
            m_FlippedX = false;
        }
        if(m_FlippedY){ //If Y is flipped, flip again
            AlterImageFlip(0);
            m_FlippedY = false;
        }
        if(m_Negative){ //If is inverted, invert again
            AlterImageInvert();
            m_Negative = false;
        }
    }
}
