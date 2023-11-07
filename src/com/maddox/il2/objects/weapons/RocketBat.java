package com.maddox.il2.objects.weapons;

import java.io.IOException;
import java.util.List;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.MsgExplosion;
import com.maddox.il2.ai.ground.TgtShip;
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

public class RocketBat extends RocketBomb
{
    class Master extends ActorNet implements NetUpdate
    {
        NetMsgFiltered out;
        
        public void msgNetNewChannel( NetChannel netChannel) {
            if (!Actor.isValid(this.actor())) {
                return;
            }
            if (netChannel.isMirrored(this)) {
                return;
            }
            try {
                if (netChannel.userState == 0) {
                     NetMsgSpawn netReplicate = this.actor().netReplicate(netChannel);
                    if (netReplicate != null) {
                        this.postTo(netChannel, netReplicate);
                        this.actor().netFirstUpdate(netChannel);
                    }
                }
            }
            catch (Exception ex) {
                NetObj.printDebug(ex);
            }
        }
        
        public boolean netInput( NetMsgInput netMsgInput) throws IOException {
            return false;
        }
        
        public void netUpdate() {
            try {
                this.out.unLockAndClear();
                RocketBat.this.getSpeed(RocketBat.v);
                this.out.writeFloat((float)RocketBat.v.x);
                this.out.writeFloat((float)RocketBat.v.y);
                this.out.writeFloat((float)RocketBat.v.z);
                this.post(Time.current(), this.out);
            }
            catch (Exception ex) {
                NetObj.printDebug(ex);
            }
        }
        
        public Master( Actor actor) {
            super(actor);
            this.out = new NetMsgFiltered();
        }
    }

    class Mirror extends ActorNet
    {
        NetMsgFiltered out;
        
        public void msgNetNewChannel( NetChannel netChannel) {
            if (!Actor.isValid(this.actor())) {
                return;
            }
            if (netChannel.isMirrored(this)) {
                return;
            }
            try {
                if (netChannel.userState == 0) {
                     NetMsgSpawn netReplicate = this.actor().netReplicate(netChannel);
                    if (netReplicate != null) {
                        this.postTo(netChannel, netReplicate);
                        this.actor().netFirstUpdate(netChannel);
                    }
                }
            }
            catch (Exception ex) {
                NetObj.printDebug(ex);
            }
        }
        
        public boolean netInput( NetMsgInput netMsgInput) throws IOException {
            if (netMsgInput.isGuaranted()) {
                return false;
            }
            if (this.isMirrored()) {
                this.out.unLockAndSet(netMsgInput, 0);
                this.postReal(Message.currentTime(true), this.out);
            }
            RocketBat.v.x = netMsgInput.readFloat();
            RocketBat.v.y = netMsgInput.readFloat();
            RocketBat.v.z = netMsgInput.readFloat();
            RocketBat.this.setSpeed(RocketBat.v);
            return true;
        }
        
        public Mirror( Actor actor,  NetChannel netChannel,  int n) {
            super(actor, netChannel, n);
            this.out = new NetMsgFiltered();
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
                new RocketBat(actor, netmsginput.channel(), i, point3d, orient, f);
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
            getSpeed(spd);
            float f1 = (float)spd.length();
            Vector3d vector3d = new Vector3d(0.0D, 0.0D, 0.0D);
//            vector3d.y = -azimuthControlScaleFact * (double)f1 * (double)((TypeX4Carrier)fm.actor).typeX4CgetdeltaAzimuth();
//            vector3d.z = tangageControlScaleFact * (double)f1 * (double)((TypeX4Carrier)fm.actor).typeX4CgetdeltaTangage();
            int smoothFactor = SMOOTH_FACTOR / 4;
            this.deltaAzimuth = this.smoothAdjust(this.deltaAzimuth, ((TypeX4Carrier) this.fm.actor).typeX4CgetdeltaAzimuth(), smoothFactor);
            this.deltaTangage = this.smoothAdjust(this.deltaTangage, ((TypeX4Carrier) this.fm.actor).typeX4CgetdeltaTangage(), smoothFactor);
            vector3d.y = -azimuthControlScaleFact * f1 * this.deltaAzimuth;
            vector3d.z = tangageControlScaleFact * f1 * this.deltaTangage;
            if(vector3d.y != 0.0D || vector3d.z != 0.0D)
                f1 *= 0.9992F;
            pos.getAbs(Or);
            Or.transform(vector3d);
            vector3d.z += (double)f1 * 0.007D * (double)f * (double)Atmosphere.g();
            spd.add(vector3d);
            float f2 = (float)spd.length();
            float f3 = f1 / f2;
            spd.scale(f3);
            setSpeed(spd);
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

    public RocketBat()
    {
        first = true;
        targetRCSMax = 0.0F;
        fm = null;
    }

    public RocketBat(Actor actor, NetChannel netchannel, int i, Point3d point3d, Orient orient, float f)
    {
        first = true;
        targetRCSMax = 0.0F;
        fm = null;
        net = new Mirror(this, netchannel, i);
        pos.setAbs(point3d, orient);
        pos.reset();
        pos.setBase(actor, null, true);
        doStart(-1F);
        v.set(1.0D, 0.0D, 0.0D);
        orient.transform(v);
        v.scale(f);
        setSpeed(v);
        collide(false);
    }

    public void start(float f)
    {
        Actor actor = pos.base();
        if(Actor.isValid(actor) && (actor instanceof Aircraft))
        {
            if(actor.isNetMirror())
            {
                destroy();
                return;
            }
            net = new Master(this);
        }
        doStart(f);
    }

    private void doStart(float f)
    {
        super.start(-1F);
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
            if(!(actor1 instanceof TgtShip) && !(actor1 instanceof BridgeSegment))
                continue;
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
        if(d > 0.0D && (actor instanceof TgtShip))
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
        pos.getTime(Time.current(), p);
        MsgExplosion.send(null, null, p, getOwner(), 45F, 2.0F, 1, 550F);
        super.doExplosionAir();
    }

    public NetMsgSpawn netReplicate(NetChannel netchannel)
        throws IOException
    {
        NetMsgSpawn netmsgspawn = super.netReplicate(netchannel);
        netmsgspawn.writeNetObj(getOwner().net);
        Point3d point3d = pos.getAbsPoint();
        netmsgspawn.writeFloat((float)point3d.x);
        netmsgspawn.writeFloat((float)point3d.y);
        netmsgspawn.writeFloat((float)point3d.z);
        Orient orient = pos.getAbsOrient();
        netmsgspawn.writeFloat(orient.azimut());
        netmsgspawn.writeFloat(orient.tangage());
        float f = (float)getSpeed(null);
        netmsgspawn.writeFloat(f);
        return netmsgspawn;
    }

    protected void mydebug(String s)
    {
    }

    float smoothAdjust(float oldValue, float newValue, int smoothFactor) {
        return ((oldValue * (smoothFactor - 1F)) + newValue) / smoothFactor;
    }

    float             deltaAzimuth                 = 0.0F;
    float             deltaTangage                 = 0.0F;
    static int        SMOOTH_FACTOR                = 20;
    private FlightModel fm;
    private Actor target;
    private static Orient or = new Orient();
    private static Point3d p = new Point3d();
    private static Point3d pT = new Point3d();
    private static Vector3d v = new Vector3d();
    private static double azimuthControlScaleFact = 0.9D;
    private static double tangageControlScaleFact = 0.9D;
    private boolean first;
    private float targetRCSMax;

    static 
    {
        Class class1 = RocketBat.class;
        Property.set(class1, "mesh", "3do/arms/Bat/mono.sim");
        Property.set(class1, "sound", "weapon.bomb_std");
        Property.set(class1, "emitColor", new Color3f(1.0F, 1.0F, 0.5F));
        Property.set(class1, "emitLen", 0.0F);
        Property.set(class1, "emitMax", 0.0F);
        Property.set(class1, "radius", 100F);
        Property.set(class1, "timeLife", 1000F);
        Property.set(class1, "timeFire", 12F);
        Property.set(class1, "force", 2.0F);
        Property.set(class1, "power", 250F);
        Property.set(class1, "powerType", 0);
        Property.set(class1, "kalibr", 1.1F);
        Property.set(class1, "massa", 853F);
        Property.set(class1, "massaEnd", 853F);
        Spawn.add(class1, new SPAWN());
    }

}
