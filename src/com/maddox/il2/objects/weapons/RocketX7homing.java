package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.NearestTargets;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.ai.ground.TgtTank;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Selector;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.il2.objects.humans.Soldier;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.rts.NetChannel;
import com.maddox.rts.Property;
import com.maddox.rts.Spawn;
import com.maddox.rts.Time;

public class RocketX7homing extends RocketX4
{

    public boolean interpolateStep()
    {
        float f = Time.tickLenFs();
        float f1 = (float)getSpeed((Vector3d)null);
        f1 += (320F - f1) * 0.1F * f;
        pos.getAbs(p, or);
        v.set(1.0D, 0.0D, 0.0D);
        or.transform(v);
        v.scale(f1);
        setSpeed(v);
        p.x += v.x * (double)f;
        p.y += v.y * (double)f;
        p.z += v.z * (double)f;
        if(isNet() && isNetMirror())
        {
            pos.setAbs(p, or);
            return false;
        }
        if(Time.current() > tStart + 350L)
            if(!isValid(victim))
            {
                if((victim = NearestTargets.getEnemy(0, -1, p, 5000D, 0)) != null)
                {
                    if(victim instanceof BridgeSegment)
                        victim = null;
                    else
                    if(getOwner().getArmy() == victim.getArmy())
                        if(victim instanceof BigshipGeneric)
                            victim = null;
                        else
                        if(victim instanceof TgtTank)
                            victim = null;
                    if(victim != null)
                    {
                        victim.pos.getAbs(pT);
                        pT.sub(p);
                        or.transformInv(pT);
                        if(pT.y > pT.x / 4D || pT.y < -pT.x / 4D || pT.z > pT.x / 4D || pT.z < -pT.x / 4D)
                            victim = null;
                    }
                }
                or.increment(0.0F, 0.0F, 720F * f);
            } else
            {
                victim.pos.getAbs(pT);
                pT.sub(p);
                or.transformInv(pT);
                if(pT.x > 0.0D)
                {
                    this.stepBeamRider(victim);
                    deltaAzimuth = deltaTangage = 0.0F;
                } else
                {
                    victim = null;
                }
            }
        pos.setAbs(p, or);
        return false;
    }

    public RocketX7homing() {}

    public RocketX7homing(Actor actor, NetChannel netchannel, int i, Point3d point3d, Orient orient, float f)
    {
        super(actor, netchannel, i, point3d, orient, f);
    }

    void doStart(float f)
    {
        super.doStart(-1F);
        if(isNet() && isNetMirror())
            return;
        if((getOwner() != World.getPlayerAircraft() || !((RealFlightModel)fm).isRealMode()) && (fm instanceof Pilot))
        {
            if(((Pilot)fm).target_ground != null)
                victim = ((Pilot)fm).target_ground;
            else
            if(((Pilot)fm).target.actor != null)
                victim = ((Pilot)fm).target.actor;
        } else {
            victim = Main3D.cur3D().getViewPadlockEnemy();
            if (victim == null) victim = Selector.look(true, true, Main3D.cur3D()._camera3D[Main3D.cur3D().getRenderIndx()], -1, -1, World.getPlayerAircraft(), false);
            if(victim instanceof BridgeSegment || victim instanceof Soldier || (getOwner().getArmy() == victim.getArmy() && (victim instanceof Aircraft || victim instanceof TgtTank)))
                victim = null;
        }
    }

    static 
    {
        Class class1 = RocketX7homing.class;
        Property.set(class1, "mesh", "3do/arms/X-7/mono.sim");
        Property.set(class1, "timeLife", 300F);
        Property.set(class1, "timeFire", 2.5F);
        Property.set(class1, "massa", 90F);
        Property.set(class1, "massaEnd", 67.5F);
        Spawn.add(class1, new RocketX4.SPAWN(class1));
    }
}
