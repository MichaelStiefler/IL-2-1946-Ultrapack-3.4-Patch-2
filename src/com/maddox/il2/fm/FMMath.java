/*
 * 4.13.1 Class
 * Backport by SAS~Storebror for TD AI code implementation
 */

package com.maddox.il2.fm;

import com.maddox.JGP.Tuple3d;
import com.maddox.JGP.Tuple3f;
import com.maddox.il2.engine.Interpolate;
import com.maddox.rts.Time;

public class FMMath extends Interpolate {

    public FMMath() {
    }

    public boolean isTick(int i, int j) {
        if (this.actor == null) return false;
        else return (Time.tickCounter() + this.actor.hashCode()) % i == j;
    }

    public static final float DEG2RAD(float f) {
        return f * 0.01745329F;
    }

    public static final float RAD2DEG(float f) {
        return f * 57.29578F;
    }

    public static final double DEG2RAD(double d) {
        return d * 0.017453292519943295D;
    }

    public static final double RAD2DEG(double d) {
        return d * 57.295779513082323D;
    }

    public static final float hypot(float f, float f1) {
        return (float) Math.sqrt(f * f + f1 * f1);
    }

    public static final double hypot(double d, double d1) {
        return Math.sqrt(d * d + d1 * d1);
    }

    public static final float hypot(float f, float f1, float f2) {
        return (float) Math.sqrt(f * f + f1 * f1 + f2 * f2);
    }

    public static final double hypot(double d, double d1, double d2) {
        return Math.sqrt(d * d + d1 * d1 + d2 * d2);
    }

    public static final float interpolate(float f, float f1, float f2) {
        return f + (f1 - f) * f2;
    }

    public static final float sqrta(float f) {
        if (f >= 0.0F) return (float) Math.sqrt(f);
        else return -(float) Math.sqrt(-f);
    }

    public static final boolean isNAN(float f) {
        return f >= 1.0F && f <= 0.0F;
    }

    public static final boolean isNAN(double d) {
        return d >= 1.0D && d <= 0.0D;
    }

    public static final boolean isNAN(Tuple3f tuple3f) {
        return isNAN(tuple3f.x) || isNAN(tuple3f.y) || isNAN(tuple3f.z);
    }

    public static final boolean isNAN(Tuple3d tuple3d) {
        return isNAN(tuple3d.x) || isNAN(tuple3d.y) || isNAN(tuple3d.z);
    }

    public static final float clamp(float f, float f1, float f2) {
        return Math.min(f2, Math.max(f1, f));
    }

    public static final float positiveSquareEquation(float f, float f1, float f2) {
        float f3 = f1 * f1 - 4F * f * f2;
        if (f3 < 0.0F) return -1F;
        f3 = (float) Math.sqrt(f3);
        float f4 = (-f1 + f3) / (2.0F * f);
        if (f4 > 0.0F) return f4;
        f4 = (-f1 - f3) / (2.0F * f);
        if (f4 > 0.0F) return f4;
        else return -1F;
    }

    public static final double positiveSquareEquation(double d, double d1, double d2) {
        double d3 = d1 * d1 - 4D * d * d2;
        if (d3 < 0.0D) return -1D;
        d3 = Math.sqrt(d3);
        double d4 = (-d1 + d3) / (2D * d);
        if (d4 > 0.0D) return d4;
        d4 = (-d1 - d3) / (2D * d);
        if (d4 > 0.0D) return d4;
        else return -1D;
    }

    private static native long nInit();

    private static native void nInitSeed(long l);

    public static void initSeed(long l) {
        rndSeed = l;
        nInitSeed(l);
    }

    public static void initSeed() {
        rndSeed = nInit();
    }

    public static long getSeed() {
        return rndSeed;
    }

    public static final void loadNative() {
        if (!libLoaded) {
            libLoaded = true;
            nInit();
        }
    }

    public static final float PI        = (float) Math.PI;
    private static boolean    libLoaded = false;
    private static long       rndSeed;

    static {
//        TrackIRWin.loadNative();
        loadNative();
    }
}
