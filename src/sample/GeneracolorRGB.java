package sample;

import java.util.Random;

public class GeneracolorRGB {

    static Random randomGenerator;

    static {
        randomGenerator = new Random();
    }


    static String generateColor() {
        int newColor = 0x1000000 + randomGenerator.nextInt(0x1000000);
        return "#" + Integer.toHexString(newColor).substring(1, 7);
    }
}