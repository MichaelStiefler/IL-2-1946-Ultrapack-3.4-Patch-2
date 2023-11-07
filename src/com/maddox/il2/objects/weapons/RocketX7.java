package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.objects.air.TypeX4Carrier;
import com.maddox.rts.NetChannel;
import com.maddox.rts.Property;
import com.maddox.rts.Spawn;
import com.maddox.rts.Time;

public class RocketX7 extends RocketX4 {

    public boolean interpolateStep() {
        float f = Time.tickLenFs();
        float f1 = (float) this.getSpeed(null);
        f1 += (320F - f1) * 0.1F * f;
        super.pos.getAbs(p, or);
        v.set(1.0D, 0.0D, 0.0D);
        or.transform(v);
        v.scale(f1);
        this.setSpeed(v);
        p.x += v.x * f;
        p.y += v.y * f;
        p.z += v.z * f;
        if (this.isNet() && this.isNetMirror()) {
            super.pos.setAbs(p, or);
            return false;
        }
        if (Actor.isValid(this.getOwner())) {
            int smoothFactor = SMOOTH_FACTOR;
            if (((this.getOwner() != World.getPlayerAircraft()) || !((RealFlightModel) this.fm).isRealMode()) && (this.fm instanceof Pilot)) {
                smoothFactor /= 4;
                Pilot pilot = (Pilot) this.fm;
                Actor target = pilot.target_ground;
                if ((pilot.target_ground != null) || (pilot.target != null)) {
                    if (pilot.target_ground != null) {
                        pilot.target_ground.pos.getAbs(pT);
                    } else {
                        ((FlightModelMain) (pilot.target)).Loc.get(pT);
                        target = pilot.target.actor;
                    }
                    pT.sub(p);
                    or.transformInv(pT);
                    
                    if (pT.x > -10D) {
                        this.stepBeamRider(target);
                        deltaAzimuth = deltaTangage = 0.0F;
                    }
                }
            } else {
                this.deltaAzimuth = this.smoothAdjust(this.deltaAzimuth, ((TypeX4Carrier) this.fm.actor).typeX4CgetdeltaAzimuth(), smoothFactor);
                this.deltaTangage = this.smoothAdjust(this.deltaTangage, ((TypeX4Carrier) this.fm.actor).typeX4CgetdeltaTangage(), smoothFactor);
                or.increment(50F * f * this.deltaAzimuth, 50F * f * this.deltaTangage, 0.0F);
            }
            or.setYPR(or.getYaw(), or.getPitch(), 0.0F);
            ((TypeX4Carrier) this.fm.actor).typeX4CResetControls();
        }
        super.pos.setAbs(p, or);
        return false;
    }

    public RocketX7() {}

    public RocketX7(Actor actor, NetChannel netchannel, int i, Point3d point3d, Orient orient, float f) {
        super(actor, netchannel, i, point3d, orient, f);
    }

    static {
        Class class1 = RocketX7.class;
        Property.set(class1, "mesh", "3do/arms/X-7/mono.sim");
        Property.set(class1, "timeLife", 15F);
        Property.set(class1, "timeFire", 16.5F);
        Property.set(class1, "radius", 75F);
        Property.set(class1, "power", 25F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.32F);
        Property.set(class1, "massa", 90F);
        Property.set(class1, "massaEnd", 67.5F);
        Spawn.add(class1, new RocketX4.SPAWN(class1));
    }
}
