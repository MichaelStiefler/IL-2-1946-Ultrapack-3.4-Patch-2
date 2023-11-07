package com.maddox.il2.objects.weapons;

import java.io.IOException;
import java.util.List;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.MsgExplosion;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.ground.TgtShip;
import com.maddox.il2.ai.ground.TgtTank;
import com.maddox.il2.ai.ground.TgtTrain;
import com.maddox.il2.ai.ground.TgtVehicle;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorNet;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.TypeX4Carrier;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.rts.Message;
import com.maddox.rts.NetChannel;
import com.maddox.rts.NetMsgFiltered;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.NetMsgSpawn;
import com.maddox.rts.NetObj;
import com.maddox.rts.NetSpawn;
import com.maddox.rts.NetUpdate;
import com.maddox.rts.Property;
import com.maddox.rts.Spawn;
import com.maddox.rts.Time;

public class KAB500M extends RocketBomb
{
    class Master extends ActorNet
        implements NetUpdate
    {

        public void msgNetNewChannel(NetChannel netchannel)
        {
            if(!Actor.isValid(actor()))
                return;
            if(netchannel.isMirrored(this))
                return;
            try
            {
                if(netchannel.userState == 0)
                {
                    NetMsgSpawn netmsgspawn = actor().netReplicate(netchannel);
                    if(netmsgspawn != null)
                    {
                        postTo(netchannel, netmsgspawn);
                        actor().netFirstUpdate(netchannel);
                    }
                }
            }
            catch(Exception exception)
            {
                NetObj.printDebug(exception);
            }
        }

        public boolean netInput(NetMsgInput netmsginput)
            throws IOException
        {
            return false;
        }

        public void netUpdate()
        {
            try
            {
                out.unLockAndClear();
                getSpeed(KAB500M.v);
                out.writeFloat((float)KAB500M.v.x);
                out.writeFloat((float)KAB500M.v.y);
                out.writeFloat((float)KAB500M.v.z);
                post(Time.current(), out);
            }
            catch(Exception exception)
            {
                NetObj.printDebug(exception);
            }
        }

        NetMsgFiltered out;

        public Master(Actor actor)
        {
            super(actor);
            out = new NetMsgFiltered();
        }
    }

    class Mirror extends ActorNet
    {

        public void msgNetNewChannel(NetChannel netchannel)
        {
            if(!Actor.isValid(actor()))
                return;
            if(netchannel.isMirrored(this))
                return;
            try
            {
                if(netchannel.userState == 0)
                {
                    NetMsgSpawn netmsgspawn = actor().netReplicate(netchannel);
                    if(netmsgspawn != null)
                    {
                        postTo(netchannel, netmsgspawn);
                        actor().netFirstUpdate(netchannel);
                    }
                }
            }
            catch(Exception exception)
            {
                NetObj.printDebug(exception);
            }
        }

        public boolean netInput(NetMsgInput netmsginput)
            throws IOException
        {
            if(netmsginput.isGuaranted())
                return false;
            if(isMirrored())
            {
                out.unLockAndSet(netmsginput, 0);
                postReal(Message.currentTime(true), out);
            }
            KAB500M.v.x = netmsginput.readFloat();
            KAB500M.v.y = netmsginput.readFloat();
            KAB500M.v.z = netmsginput.readFloat();
            setSpeed(KAB500M.v);
            return true;
        }

        NetMsgFiltered out;

        public Mirror(Actor actor, NetChannel netchannel, int i)
        {
            super(actor, netchannel, i);
            out = new NetMsgFiltered();
        }
    }

    static class SPAWN
        implements NetSpawn
    {

        public void netSpawn(int i, NetMsgInput netmsginput)
        {
            NetObj netobj = netmsginput.readNetObj();
            if(netobj == null)
                return;
            try
            {
                Actor actor = (Actor)netobj.superObj();
                Point3d point3d = new Point3d(netmsginput.readFloat(), netmsginput.readFloat(), netmsginput.readFloat());
                Orient orient = new Orient(netmsginput.readFloat(), netmsginput.readFloat(), 0.0F);
                float f = netmsginput.readFloat();
                new KAB500M(actor, netmsginput.channel(), i, point3d, orient, f);
            }
            catch(Exception exception)
            {
                System.out.println(exception.getMessage());
                exception.printStackTrace();
            }
        }

        SPAWN()
        {
        }
    }

    public boolean interpolateStep()
    {
        float f = Time.tickLenFs();
        if(!OnGlide && Time.current() > DelayKAB)
        {
            OnGlide = true;
            setMesh("3do/Arms/P85-KAB500M/mono_deploy.sim");
        }
        pos.getAbs(p, or);
        if(first)
            first = false;
        if(Actor.isValid(getOwner()))
        {
            ((TypeX4Carrier)fm.actor).typeX4CResetControls();
            if(target != null)
            {
                pT = target.pos.getAbsPoint();
                Point3d point3d = new Point3d();
                point3d.x = pT.x;
                point3d.y = pT.y;
                point3d.z = pT.z + 2.5D;
                point3d.sub(p);
                or.transformInv(point3d);
                if(point3d.x > -10D)
                {
                    double d;
                    if(target instanceof TgtShip)
                        d = Aircraft.cvt(fm.Skill, 0.0F, 3F, 4F / targetRCSMax, 1.0F / targetRCSMax);
                    else
                        d = Aircraft.cvt(fm.Skill, 0.0F, 3F, 20F, 4F);
                    if(point3d.y > d)
                        ((TypeX4Carrier)fm.actor).typeX4CAdjSideMinus();
                    if(point3d.y < -d)
                        ((TypeX4Carrier)fm.actor).typeX4CAdjSidePlus();
                    if(point3d.z < -d)
                        ((TypeX4Carrier)fm.actor).typeX4CAdjAttitudeMinus();
                    if(point3d.z > d)
                        ((TypeX4Carrier)fm.actor).typeX4CAdjAttitudePlus();
                }
            }
            getSpeed(RocketBomb.spd);
            float f1 = (float)RocketBomb.spd.length();
            Vector3d vector3d = new Vector3d(0.0D, 0.0D, 0.0D);
            vector3d.y = -azimuthControlScaleFact * (double)f1 * (double)((TypeX4Carrier)fm.actor).typeX4CgetdeltaAzimuth();
            vector3d.z = tangageControlScaleFact * (double)f1 * (double)((TypeX4Carrier)fm.actor).typeX4CgetdeltaTangage();
            if(vector3d.y != 0.0D || vector3d.z != 0.0D)
                f1 *= 0.9992F;
            pos.getAbs(RocketBomb.Or);
            RocketBomb.Or.transform(vector3d);
            vector3d.z += (double)f1 * 0.007D * (double)f * (double)Atmosphere.g();
            RocketBomb.spd.add(vector3d);
            float f2 = (float)RocketBomb.spd.length();
            float f3 = f1 / f2;
            RocketBomb.spd.scale(f3);
            setSpeed(RocketBomb.spd);
            ((TypeX4Carrier)fm.actor).typeX4CResetControls();
        }
        if(!Actor.isValid(getOwner()) || !(getOwner() instanceof Aircraft))
        {
            doExplosionAir();
            postDestroy();
            collide(false);
            drawing(false);
            return true;
        } else
        {
            return true;
        }
    }

    public KAB500M()
    {
        first = true;
        targetRCSMax = 0.0F;
        fm = null;
    }

    public KAB500M(Actor actor, NetChannel netchannel, int i, Point3d point3d, Orient orient, float f)
    {
        first = true;
        targetRCSMax = 0.0F;
        fm = null;
        this.net = new Mirror(this, netchannel, i);
        this.pos.setAbs(point3d, orient);
        this.pos.reset();
        this.pos.setBase(actor, null, true);
        doStart(-1F);
        v.set(1.0D, 0.0D, 0.0D);
        orient.transform(v);
        v.scale(f);
        setSpeed(v);
        collide(false);
        OnGlide = false;
    }

    public void start(float f)
    {
        Actor actor = this.pos.base();
        if(Actor.isValid(actor) && (actor instanceof Aircraft))
        {
            if(actor.isNetMirror())
            {
                destroy();
                return;
            }
            this.net = new Master(this);
        }
        doStart(f);
    }

    private void doStart(float f)
    {
        super.start(-1F);
        DelayKAB = Time.current() + 800L + World.Rnd().nextLong(0L, 850L);
        pos.getAbs(p, or);
        or.setYPR(or.getYaw(), or.getPitch(), 0.0F);
        pos.setAbs(p, or);
        fm = ((Aircraft)getOwner()).FM;
        List list = Engine.targets();
        int i = list.size();
        float f1 = 0.0F;
        Actor actor = null;
        for(int j = 0; j < i; j++)
        {
            Actor actor1 = (Actor)list.get(j);
            if((actor1 instanceof TgtShip) || (actor1 instanceof BridgeSegment))
            {
                Point3d point3d = new Point3d();
                Point3d point3d1 = actor1.pos.getAbsPoint();
                point3d.x = point3d1.x;
                point3d.y = point3d1.y;
                point3d.z = point3d1.z;
                pos.getAbs(p, or);
                point3d.sub(p);
                or.transformInv(point3d);
                float f2 = antennaPattern(point3d, actor1);
                if(f2 > f1 && (double)f2 > 0.001D)
                {
                    f1 = f2;
                    actor = actor1;
                    targetRCSMax = estimateRCS(actor);
                }
            }
        }

        target = actor;
    }

    private float antennaPattern(Point3d point3d, Actor actor)
    {
        float f = (float)Math.sqrt(point3d.x * point3d.x + point3d.y * point3d.y + point3d.z * point3d.z);
        if(f > 32000F)
            return 0.0F;
        float f1 = (float)Math.atan2(point3d.y, point3d.x);
        float f2 = (float)Math.sqrt(point3d.x * point3d.x + point3d.y * point3d.y);
        float f3 = (float)Math.atan2(point3d.z, f2);
        f3 += 0.2617992F;
        f /= 1000F;
        double d;
        if(Math.cos(f1) > 0.0D && Math.cos(f3) > 0.0D)
            d = (Math.cos(f1) * Math.cos(f3)) / (double)(f * f);
        else
            d = 0.0D;
        if(d > 0.0D && (actor instanceof TgtVehicle) || (actor instanceof TgtTank) || (actor instanceof TgtTrain))
        {
            float f4 = estimateRCS(actor);
            d *= f4;
        }
        return (float)d;
    }

    private float estimateRCS(Actor actor)
    {
        if((actor instanceof com.maddox.il2.objects.ships.Ship.PilotWater_US) || (actor instanceof com.maddox.il2.objects.ships.Ship.PilotBoatWater_US) || (actor instanceof com.maddox.il2.objects.ships.Ship.PilotWater_JA) || (actor instanceof com.maddox.il2.objects.ships.Ship.RwyCon) || (actor instanceof com.maddox.il2.objects.ships.Ship.RwySteel) || (actor instanceof com.maddox.il2.objects.ships.Ship.RwySteelLow) || (actor instanceof com.maddox.il2.objects.ships.Ship.RwyTransp) || (actor instanceof com.maddox.il2.objects.ships.Ship.RwyTranspWide) || (actor instanceof com.maddox.il2.objects.ships.Ship.RwyTranspSqr))
            return 0.0F;
        float f = 0.0F;
        f = actor.collisionR();
        if(f < 5F)
            f = 5F;
        return f / 40F;
    }

    public void destroy()
    {
        if(isNet() && isNetMirror())
            doExplosionAir();
        super.destroy();
    }

    protected void doExplosionAir()
    {
        this.pos.getTime(Time.current(), p);
        MsgExplosion.send(null, null, p, getOwner(), 45F, 2.0F, 1, 550F);
        super.doExplosionAir();
    }

    public NetMsgSpawn netReplicate(NetChannel netchannel)
        throws IOException
    {
        NetMsgSpawn netmsgspawn = super.netReplicate(netchannel);
        netmsgspawn.writeNetObj(getOwner().net);
        Point3d point3d = this.pos.getAbsPoint();
        netmsgspawn.writeFloat((float)point3d.x);
        netmsgspawn.writeFloat((float)point3d.y);
        netmsgspawn.writeFloat((float)point3d.z);
        Orient orient = this.pos.getAbsOrient();
        netmsgspawn.writeFloat(orient.azimut());
        netmsgspawn.writeFloat(orient.tangage());
        float f = (float)getSpeed(null);
        netmsgspawn.writeFloat(f);
        return netmsgspawn;
    }

    protected void mydebug(String s)
    {
    }

    private FlightModel fm;
    private Actor target;
    private static Orient or = new Orient();
    private static Point3d p = new Point3d();
    private static Point3d pT = new Point3d();
    private static Vector3d v = new Vector3d();
    private static double azimuthControlScaleFact = 0.905D;
    private static double tangageControlScaleFact = 0.905D;
    private boolean first;
    private float targetRCSMax;
    private boolean OnGlide;
    private long DelayKAB;

    static 
    {
        Class class1 = KAB500M.class;
        Property.set(class1, "mesh", "3do/Arms/P85-KAB500M/mono.sim");
        Property.set(class1, "sound", "weapon.bomb_std");
        Property.set(class1, "emitColor", new Color3f(1.0F, 1.0F, 0.5F));
        Property.set(class1, "emitLen", 0.0F);
        Property.set(class1, "emitMax", 0.0F);
        Property.set(class1, "radius", 100F);
        Property.set(class1, "timeLife", 1000F);
        Property.set(class1, "timeFire", 12F);
        Property.set(class1, "force", 2.0F);
        Property.set(class1, "power", 248F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 1.1F);
        Property.set(class1, "massa", 602F);
        Property.set(class1, "massaEnd", 602F);
        Spawn.add(class1, new SPAWN());
    }

}
