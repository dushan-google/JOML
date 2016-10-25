/*
 * (C) Copyright 2016 JOML

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 THE SOFTWARE.

 */
package org.joml;

/**
 * Creates samples on a N x N strata.
 * 
 * @author Kai Burjack
 */
public class StratifiedSampling {

    private final Random rnd;

    /**
     * Create a new instance of {@link StratifiedSampling} and initialize the random number generator with the given
     * <code>seed</code>.
     * 
     * @param seed
     *            the seed to initialize the random number generator with
     */
    public StratifiedSampling(long seed) {
        this.rnd = new Random(seed);
    }

    /**
     * Generate <tt>n * n</tt> random sample positions in the unit square of <tt>x, y = [-1..+1]</tt>.
     * <p>
     * Each sample within its stratum is distributed randomly.
     * 
     * @param n
     *            the number of strata in each dimension
     * @param callback
     *            will be called for each generated sample position
     */
    public void generateRandom(int n, Sampling2dCallback callback) {
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                float sampleX = (rnd.nextFloat() / n + (float) x / n) * 2.0f - 1.0f;
                float sampleY = (rnd.nextFloat() / n + (float) y / n) * 2.0f - 1.0f;
                callback.onNewSample(sampleX, sampleY);
            }
        }
    }

    /**
     * Generate <tt>n * n</tt> random sample positions in the unit square of <tt>x, y = [-1..+1]</tt>.
     * <p>
     * Each sample within its stratum is confined to be within <tt>[-centering/2..1-centering]</tt> of its stratum.
     * 
     * @param n
     *            the number of strata in each dimension
     * @param centering
     *            determines how much the random samples in each stratum are confined to be near the center of the
     *            stratum. Possible values are <tt>[0..1]</tt>
     * @param callback
     *            will be called for each generated sample position
     */
    public void generateCentered(int n, float centering, Sampling2dCallback callback) {
        float start = centering * 0.5f;
        float end = 1.0f - centering;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                float sampleX = ((start + rnd.nextFloat() * end) / n + (float) x / n) * 2.0f - 1.0f;
                float sampleY = ((start + rnd.nextFloat() * end) / n + (float) y / n) * 2.0f - 1.0f;
                callback.onNewSample(sampleX, sampleY);
            }
        }
    }

}