package org.ventry.docx;

/**
 * file: org.ventry.docx.LengthUnit
 * author: ventry
 * create: 18/5/19 15:25
 * description:
 */

public enum LengthUnit {
    INCHES {
        public double toPoints(double len) {
            return len * C1;
        }

        public double toPixels(double len) {
            return toPoints(len) * TIMES;
        }
    },
    POINTS {
        public double toPoints(double len) {
            return len;
        }

        public double toPixels(double len) {
            return toPoints(len) * TIMES;
        }
    },
    EMUS {
        public double toPoints(double len) {
            return (len / C2) * C1;
        }

        public double toPixels(double len) {
            return toPoints(len) * TIMES;
        }
    };

    private static final double C1 = 72D;
    private static final double C2 = C1 * 12700D;
    private static final double USER_UNITS = 72D;
    private static final double DEFAULT_DPI = 96D;
    private static final double TIMES = DEFAULT_DPI * 1D / USER_UNITS;

    public abstract double toPoints(double len);

    public abstract double toPixels(double len);
}
