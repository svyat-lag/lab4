package com.company;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;

public class FractalExplorer {

    /** Integer display size is the width and height of display in pixels. **/
    private int displaySize;

    /**
     * JImageDisplay reference to update display from various methods as
     * the fractal is computed.
     */
    private JImageDisplay display;

    /** A FractalGenerator object for every type of fractal. **/
    private FractalGenerator fractal;

    /**
     * A Rectangle2D.Double object which specifies the range of the complex
     * that which we are currently displaying.
     */
    private Rectangle2D.Double range;


    /**
     * A constructor that takes a display-size, stores it, and
     * initializes the range and fractal-generator objects.
     */
    public FractalExplorer(int displaySize){
        this.displaySize = displaySize;

        fractal = new Mandelbrot();
        range = new Rectangle2D.Double(0, 0, displaySize, displaySize);
        fractal.getInitialRange(range);
        display = new JImageDisplay(displaySize, displaySize);
    }


    /**
     * This method intializes the Swing GUI with a JFrame holding the
     * JImageDisplay object and a button to reset the display, a button
     * to save the current fractal image, and a JComboBox to select the
     * type of fractal.  The JComboBox is held in a JPanel with a label.
     */
    public void createAndShowGUI (){

        /** Set the frame to use a java.awt.BorderLayout for its contents. **/
        //display.setLayout(new BorderLayout());
        JFrame frame = new JFrame("Fractal Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());


        // Create comboBox with fractal's names
        JComboBox<FractalGenerator> fractalSelector = new JComboBox<>();

        // Filling the comboBox
        fractalSelector.addItem(new Mandelbrot());
        fractalSelector.addItem(new Tricorn());
        fractalSelector.addItem(new BurningShip());

        // Create panel with label and comboBox
        JPanel header = new JPanel();
        frame.add(header, BorderLayout.NORTH);
        header.add(new JLabel("Select fractal: "));
        header.add(fractalSelector);

        // Create resetDisplay button
        JButton resetDisplay = new JButton("Reset display");

        // Create save button
        JButton saveButton = new JButton("Save image");

        // Create panel with save button and reset display button
        JPanel footer = new JPanel();
        frame.add(footer, BorderLayout.SOUTH);
        footer.add(resetDisplay);
        footer.add(saveButton);

        // EventListeners for buttons, comboBox and mouse
        resetDisplay.addActionListener(new ButtonHadler());
        fractalSelector.addActionListener(new ButtonHadler());
        saveButton.addActionListener(new ButtonHadler());
        display.addMouseListener(new MouseHandler());

        frame.add(display, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);
    }


    /**
     * Private helper method to display the fractal.  This method loops
     * through every pixel in the display and computes the number of
     * iterations for the corresponding coordinates in the fractal's
     * display area.  If the number of iterations is -1 set the pixel's color
     * to black.  Otherwise, choose a value based on the number of iterations.
     * Update the display with the color for each pixel and repaint
     * JImageDisplay when all pixels have been drawn.
     */
    private void drawFractal (){

        /** Loop through every pixel in the display **/
        for (int x = 0; x < display.getWidth(); x++) {
            for (int y = 0; y < display.getHeight(); y++) {

                /**
                 * Find the corresponding coordinates xCoord and yCoord
                 * in the fractal's display area.
                 */
                double xCoord = FractalGenerator.getCoord(range.x,range.x + range.width,
                        displaySize, x);
                double yCoord = FractalGenerator.getCoord(range.x, range.x + range.width,
                        displaySize, y);

                /**
                 * Compute the number of iterations for the coordinates in
                 * the fractal's display area.
                 */
                int numIters = fractal.numIterations(xCoord, yCoord);

                /** If number of iterations is -1, set the pixel to black. **/
                if (numIters == -1) {
                    display.drawPixel(x, y, 0);
                } else {
                    /**
                     * Otherwise, choose a hue value based on the number
                     * of iterations.
                     */
                    float hue = 0.8f - (float) numIters / 100f;
                    int rgbColor = Color.HSBtoRGB(hue, 1f, 1f);

                    /** Update the display with the color for each pixel. **/
                    display.drawPixel(x, y, rgbColor);
                }

            }
        }
        /**
         * When all the pixels have been drawn, repaint JImageDisplay to match
         * current contents of its image.
         */
        display.repaint();
    }


    /**
     * An inner class to handle ActionListener events.
     */
    private class ButtonHadler implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (e.getSource() instanceof JComboBox) {
                JComboBox source = (JComboBox) e.getSource();
                fractal = (FractalGenerator) source.getSelectedItem();
                fractal.getInitialRange(range);
                drawFractal();
            }

            if (command.equals("Reset display")) {
                fractal.getInitialRange(range);
                drawFractal();
            }

            if (command.equals("Save image")) {
                /** Allow the user to choose a file to save the image to. **/
                JFileChooser fileChooser = new JFileChooser();

                /** Save only PNG images. **/
                FileFilter extensionFilter =
                        new FileNameExtensionFilter("PNG Images", "png");
                fileChooser.setFileFilter(extensionFilter);
                /**
                 * Ensures that the filechooser won't allow non-".png"
                 * filenames.
                 */
                fileChooser.setAcceptAllFileFilterUsed(false);

                int userSelection = fileChooser.showSaveDialog(display);

                /**
                 * If the outcome of the file-selection operation is
                 * APPROVE_OPTION, continue with the file-save operation.
                 */
                if (userSelection == JFileChooser.APPROVE_OPTION) {

                    /** Get the file and file name. **/
                    File file = fileChooser.getSelectedFile();

                    /** Try saving the fractal image to disk. **/
                    try {
                        javax.imageio.ImageIO.write(display.getImage(), "png", file);
                    }
                    /**
                     * Catches all exceptions and prints a message with the
                     * exception.
                     */
                    catch (Exception exception) {
                        JOptionPane.showMessageDialog(display,
                                exception.getMessage(), "Cannot Save Image",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                /**
                 * If the file-save operation is not APPROVE_OPTION, return.
                 */
                else return;
            }
        }
    }


    /**
     * An inner class to handle MouseListener events from the display.
     */
    private class MouseHandler extends MouseAdapter{
        @Override
        public void mouseClicked(MouseEvent e) {
            // Get x coordinate of display area of mouse click.
            int x = e.getX();
            double xCoord = fractal.getCoord(range.x,
                    range.x + range.width, displaySize, x);

            // Get y coordinate of display area of mouse click.
            int y = e.getY();
            double yCoord = fractal.getCoord(range.y,
                    range.y + range.height, displaySize, y);

            // Call the generator's recenterAndZoomRange() method with
            // coordinates that were clicked and a 0.5 scale.
            fractal.recenterAndZoomRange(range, xCoord, yCoord, 0.5);

            // Redraw the fractal after the area being displayed has changed.
            drawFractal();
        }
    }


    public static void main(String[] args){
        FractalExplorer fractalExplorer = new FractalExplorer(600);
        fractalExplorer.createAndShowGUI();
        fractalExplorer.drawFractal();
    }
}
