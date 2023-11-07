package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Selector;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.rts.NetChannel;
import com.maddox.rts.Spawn;
import com.maddox.rts.Time;

public class RocketX4R extends RocketX4 {

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
        if (Actor.isValid(this.getOwner())) {
            if (((this.getOwner() != World.getPlayerAircraft()) || !((RealFlightModel) this.fm).isRealMode()) && (this.fm instanceof Pilot)) {
                Pilot pilot = (Pilot) this.fm;
                if (pilot.target != null) {
                    this.victim = pilot.target.actor;
                } else {
                    this.victim = null;
                }
            } else if ((this.victim = Main3D.cur3D().getViewPadlockEnemy()) == null) {
                this.victim = Selector.look(true, false, Main3D.cur3D()._camera3D[Main3D.cur3D().getRenderIndx()], World.getPlayerAircraft().getArmy(), -1, World.getPlayerAircraft(), false);
            }
            if (this.victim != null) {
                this.victim.pos.getAbs(pT);
                pT.sub(p);
                or.transformInv(pT);
                if (pT.x > -10D) {
                    this.stepBeamRider(this.victim);
                    this.deltaAzimuth = this.deltaTangage = 0.0F;
                }
            }
        }
        this.pos.setAbs(p, or);
        if (Time.current() > (this.tStart + 500L)) {
            if (Actor.isValid(this.victim)) {
                float f2 = (float) p.distance(this.victim.pos.getAbsPoint());

                if ((this.victim instanceof Aircraft) && (this.prevd != 1000F)) {
                    Point3d proximityFuseDetonationPoint = Fuze_Proximity.checkBlowProximityFuze(this, this.victim, p, this.lastPos, 30D);
                    if (proximityFuseDetonationPoint != null) {
                        this.pos.setAbs(proximityFuseDetonationPoint, or);
                        this.doExplosionAir();
                        this.postDestroy();
                        this.collide(false);
                        this.drawing(false);
                    }
                }
                this.prevd = f2;
            } else {
                this.prevd = 1000F;
            }
            this.lastPos.set(p);
        }
        if (!Actor.isValid(this.getOwner()) || !(this.getOwner() instanceof Aircraft)) {
            this.doExplosionAir();
            this.postDestroy();
            this.collide(false);
            this.drawing(false);
        }
        return false;
    }
    
    public RocketX4R() {}
    
    public RocketX4R(Actor actor, NetChannel netchannel, int i, Point3d point3d, Orient orient, float f) {
        super(actor, netchannel, i, point3d, orient, f);
    }

    static {
        Class class1 = RocketX4R.class;
        Spawn.add(class1, new RocketX4.SPAWN(class1));
    }
}
