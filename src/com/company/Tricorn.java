package com.company;

import java.awt.geom.Rectangle2D;

public class Tricorn extends FractalGenerator{

    public static final int MAX_ITERATIONS = 2000;


    @Override
    public void getInitialRange(Rectangle2D.Double range) {
        range.x = -2;
        range.y = -2;
        range.width = 4;
        range.height = 4;
    }

    @Override
    public int numIterations(double c_r, double c_i) {
        /**
         *      Let c = c_r + c_i
         *      Let z = z_r + z_i
         *
         *      z' = z*z + c
         *      = (z_r + z_i)(z_r + z_i) + (c_r + c_i)
         *      = z_r² + 2*z_r*z_i - z_i² + c_r + c_i
         *      z_r' = z_r² - z_i² + c_r
         *      z_i' = -2*z_i*z_r + c_i
         */

        double z_r = 0.0;
        double z_i = 0.0;

        int iterCount = 0;

        // Modulus (distance) formula:
        // √(a² + b²) <= 2.0
        // a² + b² <= 4.0
        while ( z_r*z_r + z_i*z_i <= 4.0 ) {

            double z_r_tmp = z_r;

            z_r = z_r * z_r - z_i * z_i + c_r;
            z_i = -2 * z_i * z_r_tmp + c_i;

            // Point was inside the Mandelbrot set
            if (iterCount >= MAX_ITERATIONS)
                return MAX_ITERATIONS;

            iterCount++;
        }

        // Complex point was outside Mandelbrot set
        return iterCount;
    }


    @Override
    public String toString() {
        return "Tricorn";
    }
}
