package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.rts.Property;

public class Fuze_Proximity extends Fuze {

    public static Point3d checkBlowProximityFuze(Rocket rocket, Actor target, Point3d curPos, Point3d lastPos, double maxDistance) {
        double oldDistance = lastPos.distance(target.pos.getAbsPoint());                                     // Distance from Missile to Target on last Tick
        double newDistance = curPos.distance(target.pos.getAbsPoint());                                      // Current distance from Missile to Target
        if (newDistance < oldDistance) return null;                                                          // As long as we're getting closer, don't blow the fuze!
        double tickTravelDistance = curPos.distance(lastPos);                                                // Calculate the distance traveled by the missile between last and current tick
        if (oldDistance > Math.max(tickTravelDistance * 2D, maxDistance * 2D)                                // Check if we're further than the twice the max. Distance or, depending on missile speed,
         && newDistance > Math.max(tickTravelDistance * 2D, maxDistance * 2D)) return null;                  // twice the distance it travels per tick (just to be on the safe side)
        
        Vector3d dvOldNew = new Vector3d(curPos);                                                            // Current Missile position in Vector notation
        dvOldNew.sub(lastPos);                                                                               // Subtract the previous tick's Missile position, result is Missile Ray Vector "AC"
        Vector3d dvTarget = new Vector3d(target.pos.getAbsPoint());                                          // Target's current position in Vector notation
        dvTarget.sub(lastPos);                                                                               // Subtract the previous tick's Missile position, result is Target Vector "AB"
        
        double scalar = dvTarget.dot(dvOldNew) / dvOldNew.dot(dvOldNew);                                     // dot Product Equation (2) gets us scalar "t".
        
        Point3d proximityPoint = new Point3d(dvOldNew);                                                      // Point according to Missile Ray Vector with Offset (0, 0, 0)
        proximityPoint.scale(scalar);                                                                        // Scale that point (i.e. the ray vector) according to scalar "t", this is "t(C - A)"
        proximityPoint.add(lastPos);                                                                         // Use Missile's last tick's position as new offset, i.e. add "A"
        // +++ DEBUG OUTPUT +++
        if (Config.cur.ini.get("Mods", "debugProximityFuze", 0) == 1) {
            double minDistance = target.pos.getAbsPoint().distance(proximityPoint);
            System.out.println("Proximity Fuze Detonation at Factor (" + scalar + "):");
            System.out.println("tickTravelDistance:" + tickTravelDistance);
            System.out.println("oldDistance:" + oldDistance);
            System.out.println("newDistance:" + newDistance);
            System.out.println("minDistance:" + minDistance);
            System.out.println("lastPos:" + lastPos.toString());
            System.out.println("curPos:" + curPos.toString());
            System.out.println("proximityPoint:" + proximityPoint.toString());
        }
        // --- DEBUG OUTPUT ---
        if (target.pos.getAbsPoint().distance(proximityPoint) > maxDistance) return null;                    // If the distance from the calculated Proximity Fuze Point to the Missile Target
        return proximityPoint;                                                                               // is larger than maxDistance, return null, otherwise return the detonation point
    }
    
    static {
        Class localClass = Fuze_Proximity.class;
        Property.set(localClass, "type", 8);
        Property.set(localClass, "airTravelToArm", 0.0F);
        Property.set(localClass, "fixedDelay", new float[] { 100F, 300F, 500F, 600F, 700F, 800F, 900F, 1000F, 1100F, 1200F, 1300F, 1400F, 1500F, 1600F, 1700F, 1800F, 1900F, 2000F, 2100F, 2200F, 2300F, 2400F, 2500F, 2600F, 2700F, 2800F, 2900F, 3000F, 3200F, 3400F, 3600F, 3800F, 4000F, 4200F, 4400F, 4600F, 4800F, 5000F, 5500F, 6000F, 7000F, 8000F, 9000F, 10000F });
    }
}
