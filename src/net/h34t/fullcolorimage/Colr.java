package net.h34t.fullcolorimage;

import gnu.trove.list.array.TIntArrayList;

import java.util.Random;

/**
 * Assumed byte order is ARGB.
 * 
 * @author sschallerl
 */
public abstract class Colr {

    /**
     * Ignores alpha.
     * 
     * @param r
     *            red
     * @param g
     *            green
     * @param b
     *            blue
     * @return int color value
     */
    public static int rgbToInt(int r, int g, int b) {
        return r * 256 * 256 + g * 256 + b;
    }

    /**
     * Calculates the naive difference (squared) between two RGB values. No color space/human perception taken into
     * consideration.
     * 
     * @param a
     *            first color
     * @param b
     *            second color
     * @return difference between colors squared (the further apart, the bigger the difference)
     */
    public static int df(int a, int b) {
        int dr = ((a & 0x00FF0000) >> 16) - ((b & 0x00FF0000) >> 16);
        int dg = ((a & 0x0000FF00) >> 8) - ((b & 0x0000FF00) >> 8);
        int db = (a & 0x000000FF) - (b & 0x000000FF);
        return dr * dr + dg * dg + db * db;
    }

    /**
     * @return An array of all RGB colors (will be 256*256*256 = 16.7m unique ints).
     */
    public static TIntArrayList createAvailableColors() {
        TIntArrayList availableColors = new TIntArrayList(256 * 256 * 256);

        for (int r = 0; r < 256; r++)
            for (int g = 0; g < 256; g++)
                for (int b = 0; b < 256; b++)
                    availableColors.add(rgbToInt(r, g, b));

        return availableColors;
    }

    /**
     * Array shuffle for the color list. Used for distributing colors between threads evenly.
     * 
     * @param rand
     * @param colors
     */
    public static void shuffleColorsList(Random rand, TIntArrayList colors) {
        int idx, tmp;

        for (int i = 0, ii = colors.size(); i < ii; i++) {
            idx = rand.nextInt(ii);
            tmp = colors.get(i);
            colors.set(i, colors.get(idx));
            colors.set(idx, tmp);
        }
    }
}
