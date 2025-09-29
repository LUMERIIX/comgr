package Rays;

import java.util.Random;

public class UniformRandom {
    private static final Random rand = new Random();

    public static double uniform0to1() {
        return rand.nextDouble(); // [0,1)
    }

    public static double uniformMinus1to1() {
        return 2 * rand.nextDouble() - 1; // [-1,1)
    }
}
