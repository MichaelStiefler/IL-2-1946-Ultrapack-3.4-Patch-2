package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.NearestTargets;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Selector;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.rts.NetChannel;
import com.maddox.rts.Property;
import com.maddox.rts.Spawn;
import com.maddox.rts.Time;

public class RocketX4homing extends RocketX4 {

    public boolean interpolateStep() {
        float f = Time.tickLenFs();
        float f1 = (float) this.getSpeed((Vector3d) null);
        f1 += (320F - f1) * 0.1F * f;
        this.pos.getAbs(p, or);
        v.set(1.0D, 0.0D, 0.0D);
        or.transform(v);
        v.scale(f1);
        this.setSpeed(v);
        p.x += v.x * f;
        p.y += v.y * f;
        p.z += v.z * f;
        if (this.isNet() && this.isNetMirror()) {
            this.pos.setAbs(p, or);
            return false;
        }
        if (Time.current() > (this.tStart + 350L)) {
            if (!Actor.isValid(this.victim)) {
                if ((this.victim = NearestTargets.getEnemy(9, -1, p, 5000D, this.getOwner().getArmy())) == null) {
                    this.victim = NearestTargets.getEnemy(8, -1, p, 10000D, this.getOwner().getArmy());
                }
            } else {
                this.victim.pos.getAbs(pT);
                pT.sub(p);
                or.transformInv(pT);
                if (pT.x > 0.0D) {
                    this.stepBeamRider(this.victim);
                    this.deltaAzimuth = this.deltaTangage = 0.0F;
                } else if (p.distance(this.victim.pos.getAbsPoint()) > 30D) {
                    this.victim = null;
                } else {
                    if (this.victim instanceof Aircraft) {
                        Point3d proximityFuseDetonationPoint = Fuze_Proximity.checkBlowProximityFuze(this, this.victim, p, this.lastPos, 30D);
                        if (proximityFuseDetonationPoint != null) {
                            this.pos.setAbs(proximityFuseDetonationPoint, or);
                            this.doExplosionAir();
                            this.postDestroy();
                            this.collide(false);
                            this.drawing(false);
                        }
                    }
                }
            }
            this.lastPos.set(p);
        }
        this.pos.setAbs(p, or);
        if (!Actor.isValid(this.getOwner()) || !(this.getOwner() instanceof Aircraft)) {
            this.doExplosionAir();
            this.postDestroy();
            this.collide(false);
            this.drawing(false);
        }
        return false;
    }
    
    public RocketX4homing() {}

    public RocketX4homing(Actor actor, NetChannel netchannel, int i, Point3d point3d, Orient orient, float f) {
        super(actor, netchannel, i, point3d, orient, f);
    }

    void doStart(float f) {
        super.doStart(-1F);
        if (this.isNet() && this.isNetMirror()) {
            return;
        }
        if ((this.getOwner() == World.getPlayerAircraft()) && ((RealFlightModel) this.fm).isRealMode()) {
            this.victim = Selector.look(true, false, Main3D.cur3D()._camera3D[Main3D.cur3D().getRenderIndx()], World.getPlayerAircraft().getArmy(), -1, World.getPlayerAircraft(), false);
        }
    }

    static {
        Class class1 = RocketX4homing.class;
        Property.set(class1, "timeFire", 2.5F);
        Property.set(class1, "radius", 30F);
        Property.set(class1, "power", 1.0F);
        Spawn.add(class1, new RocketX4.SPAWN(class1));
    }
}
