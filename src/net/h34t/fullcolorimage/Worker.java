package net.h34t.fullcolorimage;

import gnu.trove.list.array.TIntArrayList;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Process one part of the image (a number of lines) and chose the closest unique color in the provided list of colors
 * for this thread for each pixel.
 * 
 * @author sschallerl
 */
public class Worker implements Runnable {

    private static final int[] STEPS = new int[] { 1523, 1531, 1543, 1549, 1553, 1559, 1567, 1571, 1579, 1583 };

    private final int number;
    private final long seed;
    private final BufferedImage in;
    private final BufferedImage out;
    private final int fromLine;
    private final int lineCount;
    private final int width;
    private final TIntArrayList colors;
    private final int sampleSize;

    public Worker(int number, long seed, BufferedImage in, BufferedImage out, int fromLine, int lineCount, int width,
            TIntArrayList colors, int sampleSize) {

        this.number = number;
        this.seed = seed;
        this.in = in;
        this.out = out;
        this.fromLine = fromLine;
        this.lineCount = lineCount;
        this.width = width;
        this.colors = colors;
        this.sampleSize = sampleSize;
    }

    @Override
    public void run() {
        Random rand = new Random(this.seed);

        int sourceColor, bestMatch, xpos, step;
        for (int y = fromLine; y < fromLine + lineCount; y++) {
            Log.msg(String.format("thread %d: %d%%", number, (y - fromLine) * 100 / lineCount));
            step = STEPS[rand.nextInt(STEPS.length)];

            for (int x = 0; x < width; x++) {
                xpos = x * step % width;
                sourceColor = in.getRGB(xpos, y);
                bestMatch = selectBestMatch(rand, sourceColor, colors, sampleSize);
                out.setRGB(xpos, y, bestMatch);
            }
        }
    }

    /**
     * Inspect sampleSize values and select the "nearest" one (naively RGB distance squared)
     * 
     * @param r
     * @param sourceColor
     * @param availableColors
     * @param sampleSize
     * @return the best matching color of [sampleSize] inspected
     */
    private static int selectBestMatch(final Random r, final int sourceColor, final TIntArrayList availableColors,
            final int sampleSize) {

        int numAvailableColors = availableColors.size();
        int idxBest = 0;
        int dfBest = Integer.MAX_VALUE;

        int idx, col, df;

        for (int i = 0, ii = Math.min(sampleSize, numAvailableColors); i < ii; i++) {
            idx = r.nextInt(numAvailableColors);
            col = availableColors.get(idx);
            df = Colr.df(col, sourceColor);

            if (df < dfBest) {
                idxBest = idx;
                dfBest = df;
            }
        }

        int bcol = availableColors.get(idxBest);
        availableColors.removeAt(idxBest);

        return bcol;
    }
}
