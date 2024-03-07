package mips.generators.utils;

public class Multiplier {
    private long mul;
    private int log;
    private int sh;

    public Multiplier(int d) {
        long ud = (d & 0xFFFFFFFFL);
        log = log2(ud);
        sh = log;
        long mlow = Long.divideUnsigned(1L << (32 + log), ud);
        long mhigh = Long.divideUnsigned((1L << (32 + log)) | (1L << (log + 1)), ud);
        while (Long.compareUnsigned(Long.divideUnsigned(mlow, 2L),
                Long.divideUnsigned(mhigh, 2L)) < 0
                && sh > 0) {
            mlow = Long.divideUnsigned(mlow, 2L);
            mhigh = Long.divideUnsigned(mhigh, 2L);
            sh--;
        }
        mul = mhigh;
    }

    private int log2(long x) {
        int ret = 31;
        return ret - leadingZero(x);
    }

    private int leadingZero(long x) {
        int ret = 0;
        for (int i = 31; i >= 0; i--) {
            if ((x & (1L << i)) == 0) {
                ret++;
            } else {
                break;
            }
        }
        return ret;
    }

    public long getMul() {
        return mul;
    }

    public int getLog() {
        return log;
    }

    public int getSh() {
        return sh;
    }
}
