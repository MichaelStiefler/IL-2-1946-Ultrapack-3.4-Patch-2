package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.NearestTargets;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.ai.ground.TgtTank;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Selector;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.Paratrooper;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.il2.objects.humans.Soldier;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.WeakBody;
import com.maddox.rts.NetChannel;
import com.maddox.rts.Property;
import com.maddox.rts.Spawn;
import com.maddox.rts.Time;

public class ASRKS20 extends RocketX4
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
            if(!Actor.isValid(victim))
            {
                if((victim = NearestTargets.getEnemy(0, -1, p, 5000D, 0)) != null)
                {
                    if(victim instanceof BridgeSegment)
                        victim = null;
                    else
                    if(victim instanceof Soldier)
                        victim = null;
                    else
                    if(victim instanceof Paratrooper)
                        victim = null;
                    else
                    if(victim instanceof WeakBody)
                        victim = null;
                    else
                    if(getOwner().getArmy() == victim.getArmy())
                        if(victim instanceof Aircraft)
                            victim = null;
                        else
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
                or.increment(0.0F, 0.0F, 5F * f);
            } else
            {
                victim.pos.getAbs(pT);
                pT.sub(p);
                or.transformInv(pT);
                if(pT.x > 0.0D)
                {
                    if(pT.y > 0.1D)
                        deltaAzimuth = -1F;
                    if(pT.y < -0.1D)
                        deltaAzimuth = 1.0F;
                    if(pT.z < -0.1D)
                        deltaTangage = -1F;
                    if(pT.z > 0.1D)
                        deltaTangage = 1.0F;
                    or.increment(1.0F * f * deltaAzimuth, 1.0F * f * deltaTangage, 5F * f);
                    deltaAzimuth = deltaTangage = 0.0F;
                } else
                {
                    victim = null;
                }
            }
        pos.setAbs(p, or);
        return false;
    }

    public ASRKS20()
    {
        victim = null;
        fm = null;
        tStart = 0L;
        deltaAzimuth = 0.0F;
        deltaTangage = 0.0F;
    }

    public ASRKS20(Actor actor, NetChannel netchannel, int i, Point3d point3d, Orient orient, float f)
    {
        victim = null;
        fm = null;
        tStart = 0L;
        net = new RocketX4.Mirror(this, netchannel, i);
        pos.setAbs(point3d, orient);
        pos.reset();
        pos.setBase(actor, (Hook)null, true);
        doStart(-1F);
        v.set(1.0D, 0.0D, 0.0D);
        orient.transform(v);
        v.scale(f);
        setSpeed(v);
        collide(false);
    }

    public void start(float f, int i)
    {
        Actor actor = pos.base();
        if(Actor.isValid(actor) && (actor instanceof Aircraft))
        {
            if(actor.isNetMirror())
            {
                destroy();
                return;
            }
            net = new RocketX4.Master(this);
        }
        doStart(f);
    }

    void doStart(float f)
    {
//        super.start(-1F, 0);
        super.doStart(-1F);
        fm = ((Aircraft)getOwner()).FM;
        tStart = Time.current();
        if(Config.isUSE_RENDER())
            flame.drawing(true);
        pos.getAbs(p, or);
        or.setYPR(or.getYaw(), or.getPitch(), 0.0F);
        pos.setAbs(p, or);
        if(isNet() && isNetMirror())
            return;
        if((getOwner() != World.getPlayerAircraft() || !((RealFlightModel)fm).isRealMode()) && (fm instanceof Pilot))
        {
            Pilot pilot = (Pilot)fm;
            if(pilot.target_ground != null)
                victim = pilot.target_ground;
            else
            if(pilot.target.actor != null)
                victim = pilot.target.actor;
        } else
        if((victim = Main3D.cur3D().getViewPadlockEnemy()) == null && (victim = Selector.look(true, true, Main3D.cur3D()._camera3D[Main3D.cur3D().getRenderIndx()], -1, -1, World.getPlayerAircraft(), false)) != null)
            if(victim instanceof BridgeSegment)
                victim = null;
            else
            if(victim instanceof Soldier)
                victim = null;
            else
            if(victim instanceof Paratrooper)
                victim = null;
            else
            if(getOwner().getArmy() == victim.getArmy())
                if(victim instanceof Aircraft)
                    victim = null;
                else
                if(victim instanceof TgtTank)
                    victim = null;
    }

    private FlightModel fm;
    private static Orient or = new Orient();
    private static Point3d p = new Point3d();
    private static Point3d pT = new Point3d();
    private static Vector3d v = new Vector3d();
    private long tStart;
    private float deltaAzimuth;
    private float deltaTangage;
    private Actor victim;

    static 
    {
        Class class1 = ASRKS20.class;
        Property.set(class1, "mesh", "3do/Arms/P85-KS20/mono.sim");
        Property.set(class1, "sprite", "3do/Effects/Tracers/GuidedRocket/Black.eff");
        Property.set(class1, "flame", "3do/Effects/RocketSidewinder/RocketSidewinderFlame.sim");
        Property.set(class1, "smoke", "3do/Effects/P85/P85_RocketSmoke.eff");
        Property.set(class1, "sound", "weapon.rocket_132");
        Property.set(class1, "emitLen", 0.0F);
        Property.set(class1, "emitMax", 0.0F);
        Property.set(class1, "radius", 45F);
        Property.set(class1, "timeLife", 60F);
        Property.set(class1, "timeFire", 33F);
        Property.set(class1, "force", 2.0F);
        Property.set(class1, "power", 31F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 0.21F);
        Property.set(class1, "massa", 109F);
        Property.set(class1, "massaEnd", 56F);
        Spawn.add(class1, new RocketX4.SPAWN(class1));
    }
}
