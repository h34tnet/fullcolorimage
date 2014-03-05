package net.h34t.fullcolorimage;

import gnu.trove.list.array.TIntArrayList;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

/**
 * 
 * Codegolf exercise: generate a (pretty) image where every pixel in the RGB 24bit color space is used exactly (/at
 * most) once.
 * 
 * http://codegolf.stackexchange.com/questions/22144/images-with-all-colors
 * 
 * @author sschallerl
 */

public class FullColorImage {

    public static final int THREAD_COUNT = 16;

    public static void main(String[] args) {
        String input = args[0];
        String output;
        long seed = 0;
        int passes = 10;
        int threadCount = THREAD_COUNT;

        if (args.length > 1) {
            seed = Long.parseLong(args[1]);
        }

        if (args.length > 2) {
            passes = Integer.parseInt(args[2]);
        }

        if (args.length > 3) {
            threadCount = Integer.parseInt(args[3]);
        }

        if (args.length > 4) {
            output = args[4];
        } else {
            output = null;
        }

        FullColorImage fci = new FullColorImage();

        try {
            fci.process(input, output, seed, passes, threadCount);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void process(String input, String output, long seed, int passes, final int threadCount) throws IOException {

        Random rand = new Random(seed);
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        Log.msg("loading: " + input);
        BufferedImage in = ImageIO.read(new File(input));
        Log.msg("saving as: " + output);

        int width = in.getWidth();
        int height = in.getHeight();

        Log.msg("preparing output (" + width + "x" + height + ")");
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Log.msg("generating color map");
        TIntArrayList availableColors = Colr.createAvailableColors();

        // shuffle color list, so threads will get a random part of the available colors leading to a more even
        // distribution
        Colr.shuffleColorsList(rand, availableColors);

        // create chunks of (random) available color for each thread
        // this is necessary to avoid that a color is being used twice
        List<TIntArrayList> colorBuckets = makeColorBuckets(threadCount, availableColors);

        Log.msg("matching colors, using seed " + seed + " and " + passes + " passes with " + threadCount + " threads");

        int blockSize = height / threadCount;

        long st = System.currentTimeMillis();

        // create the workers
        for (int i = 0; i < threadCount; i++) {
            service.execute(new Worker(i, seed + i, in, out, blockSize * i, blockSize, width, colorBuckets.get(i), passes));
        }

        service.shutdown();

        // now wait for the jobs to finish
        try {
            service.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // done
        long et = System.currentTimeMillis();

        // if not provided, chose an output file name
        if (output == null)
            output = String.format(input + ".seed.%d.passes.%d.threads.%d.seconds.%d.fullcolor.png",
                    seed, passes, threadCount, (et - st) / 1000);

        // save
        ImageIO.write(out, "PNG", new File(output));

        // print statistics
        Log.msg(width + "x" + height + " with " + threadCount + " threads and " + passes + " passes:");
        Log.msg(String.format("finished in %dsec or %.2fmin", ((et - st) / 1000), ((et - st) / 1000f / 60f)));
    }

    /**
     * Chunks the TIntArrayList into a List of same-sized TIntArrayLists.
     * 
     * @param threadCount
     * @param colors
     * @return A list of TIntArrayLists
     */
    private List<TIntArrayList> makeColorBuckets(int threadCount, TIntArrayList colors) {
        List<TIntArrayList> buckets = new ArrayList<TIntArrayList>(threadCount);

        for (int i = 0, ii = threadCount; i < ii; i++) {
            buckets.add(new TIntArrayList(colors.size() / threadCount));
        }

        for (int i = 0, ii = colors.size(); i < ii; i++) {
            buckets.get(i % threadCount).add(colors.get(i));
        }

        return buckets;
    }
}
