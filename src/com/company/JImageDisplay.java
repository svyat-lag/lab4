package com.company;

import java.awt.*;
import java.awt.image.BufferedImage;

public class JImageDisplay extends javax.swing.JComponent {
    private BufferedImage bufferedImage;

    public BufferedImage getImage() {
        return bufferedImage;
    }

    public JImageDisplay(int width, int height){
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        this.setPreferredSize(new Dimension(width, height));
    }

    public void paintComponent(Graphics g){
        g.drawImage(bufferedImage, 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null);
    }

    // Setts all image's pixels to black
    public void clearImage(){
        for (int i = 0; i < bufferedImage.getHeight(); i++) {
            for (int j = 0; j < bufferedImage.getWidth(); j++) {
                bufferedImage.setRGB(i, j, Color.BLACK.getRGB());
            }
        }
    }

    // Setts the specified pixel to the specified color
    public void drawPixel(int x, int y, int rgbColor){
        bufferedImage.setRGB(x, y, rgbColor);
    }
}
