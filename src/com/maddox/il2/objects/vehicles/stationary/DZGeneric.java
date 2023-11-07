package com.maddox.il2.objects.vehicles.stationary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorHMesh;
import com.maddox.il2.engine.ActorNet;
import com.maddox.il2.engine.ActorSpawn;
import com.maddox.il2.engine.ActorSpawnArg;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.Interpolate;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.objects.ActorAlign;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.rts.Message;
import com.maddox.rts.NetChannel;
import com.maddox.rts.NetMsgFiltered;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.ObjState;
import com.maddox.rts.Property;
import com.maddox.rts.Spawn;
import com.maddox.rts.Time;
import com.maddox.sound.SoundFX;

public abstract class DZGeneric extends ActorHMesh implements ActorAlign {
    class Move extends Interpolate {

        public boolean tick() {
            if (DZGeneric.this.engineSFX != null) {
                if (DZGeneric.this.engineSTimer >= 0) {
                    if (--DZGeneric.this.engineSTimer <= 0) {
                        DZGeneric.this.engineSTimer = (int) DZGeneric.SecsToTicks(DZGeneric.Rnd(5F, 5F));
                        if (!DZGeneric.this.danger()) {
                            DZGeneric.this.engineSTimer = -DZGeneric.this.engineSTimer;
                        }
                    }
                } else if (++DZGeneric.this.engineSTimer >= 0) {
                    DZGeneric.this.engineSTimer = -(int) DZGeneric.SecsToTicks(DZGeneric.Rnd(5F, 5F));
                    if (DZGeneric.this.danger()) {
                        DZGeneric.this.engineSTimer = -DZGeneric.this.engineSTimer;
                    }
                }
            }
            return true;
        }

        Move() {
        }
    }

    class Master extends ActorNet {

        public boolean netInput(NetMsgInput netmsginput) throws IOException {
            return true;
        }

        public Master(Actor actor) {
            super(actor);
        }
    }

    class Mirror extends ActorNet {

        public boolean netInput(NetMsgInput netmsginput) throws IOException {
            return true;
        }

        NetMsgFiltered out;

        public Mirror(Actor actor, NetChannel netchannel, int i) {
            super(actor, netchannel, i);
            this.out = new NetMsgFiltered();
        }
    }

    public static class SPAWN implements ActorSpawn {

        public Actor actorSpawn(ActorSpawnArg actorspawnarg) {
            DZGeneric dzgeneric = null;
            try {
                DZGeneric.constr_arg2 = actorspawnarg;
                dzgeneric = (DZGeneric) this.cls.newInstance();
                DZGeneric.constr_arg2 = null;
            } catch (Exception exception) {
                DZGeneric.constr_arg2 = null;
                System.out.println(exception.getMessage());
                exception.printStackTrace();
                System.out.println("SPAWN: Can't create DZGeneric object [class:" + this.cls.getName() + "]");
                return null;
            }
            return dzgeneric;
        }

        public Class cls;

        public SPAWN(Class class1) {
            Property.set(class1, "iconName", "icons/unknown.mat");
            Property.set(class1, "meshName", DZGeneric.mesh_name);
            this.cls = class1;
            Spawn.add(this.cls, this);
        }
    }

    public static final double Rnd(double d, double d1) {
        return World.Rnd().nextDouble(d, d1);
    }

    public static final float Rnd(float f, float f1) {
        return World.Rnd().nextFloat(f, f1);
    }

    private static final long SecsToTicks(float f) {
        long l = (long) (0.5D + (f / Time.tickLenFs()));
        return l < 1L ? 1L : l;
    }

    public void destroy() {
        if (this.isDestroyed()) {
            return;
        } else {
            this.engineSFX = null;
            this.engineSTimer = 0xfa0a1f01; // Integer.MAX_VALUE;
            this.breakSounds();
            super.destroy();
            return;
        }
    }

    public Object getSwitchListener(Message message) {
        return this;
    }

    public boolean isStaticPos() {
        return true;
    }

    protected DZGeneric() {
        this(DZGeneric.constr_arg2);
    }

    private DZGeneric(ActorSpawnArg actorspawnarg) {
        super(DZGeneric.mesh_name);
        this.engineSFX = null;
        this.engineSTimer = 9999999;
        actorspawnarg.setStationary(this);
        this.myArmy = this.getArmy();
        this.collide(false);
        this.drawing(true);
        this.createNetObject(actorspawnarg.netChannel, actorspawnarg.netIdRemote);
        this.heightAboveLandSurface = 0.0F;
        if (actorspawnarg.timeLenExist) {
            this.setDzRadius((int)actorspawnarg.timeLen);
            System.out.println("DZ Radius set to: " + this.getDzRadius());
        }
        this.Align();
        this.startMove();
    }

    public void startMove() {
        if (!this.interpEnd("move")) {
            this.engineSFX = this.newSound("objects.siren", false);
            this.engineSTimer = -(int) DZGeneric.SecsToTicks(DZGeneric.Rnd(30F, 30F));
            this.interpPut(new Move(), "move", Time.current(), null);
        }
    }

    private void Align() {
        this.pos.getAbs(DZGeneric.p);
        DZGeneric.p.z = Engine.land().HQ(DZGeneric.p.x, DZGeneric.p.y) + this.heightAboveLandSurface;
        DZGeneric.o.setYPR(this.pos.getAbsOrient().getYaw(), 0.0F, 0.0F);
        Engine.land().N(DZGeneric.p.x, DZGeneric.p.y, DZGeneric.n);
        DZGeneric.o.orient(DZGeneric.n);
        this.pos.setAbs(DZGeneric.p, DZGeneric.o);
    }

    public void align() {
        this.Align();
    }

    private boolean danger() {
//        if (!Actor.isValid(World.getPlayerAircraft())) {
//            return true;
//        }
//        Point3d point3d = new Point3d();
//        this.pos.getAbs(point3d);
//        for (Map.Entry entry = Engine.name2Actor().nextEntry(null); entry != null; entry = Engine.name2Actor().nextEntry(entry)) {
//            Actor actor = (Actor) entry.getValue();
//            if (Actor.isAlive(actor) && actor != World.getPlayerAircraft() && actor instanceof Aircraft && actor.getArmy() == this.myArmy) {
//                this.pos.getAbs(point3d);
//                double d = Main3D.cur3D().land2D.worldOfsX() + point3d.x;
//                double d1 = Main3D.cur3D().land2D.worldOfsY() + point3d.y;
//                double d2 = Main3D.cur3D().land2D.worldOfsX() + actor.pos.getAbsPoint().x;
//                double d3 = Main3D.cur3D().land2D.worldOfsY() + actor.pos.getAbsPoint().y;
//                double d4 = d2 - d;
//                double d5 = d3 - d1;
//                int i = (int) Math.ceil(Math.sqrt(d5 * d5 + d4 * d4));
//                if (i <= getDzDistance()) ObjState.destroy(actor);
//            }
//        }

        Iterator itr = Engine.name2Actor().entrySet().iterator();
        ArrayList toBeDestroyed = new ArrayList();
        while (itr.hasNext()) {
            Entry entry = (Entry) itr.next();
            Actor actor = (Actor) entry.getValue();
            if (Actor.isAlive(actor) && (actor != World.getPlayerAircraft()) && (actor instanceof Aircraft) && !((Aircraft)actor).isNetPlayer() && (actor.getArmy() == this.myArmy)) {
                double x = actor.pos.getAbsPoint().x - this.pos.getAbsPoint().x;
                double y = actor.pos.getAbsPoint().y - this.pos.getAbsPoint().y;
                int i = (int) Math.ceil(Math.sqrt((x * x) + (y * y)));
                if (i <= this.getDzRadius()) {
                    toBeDestroyed.add(actor);
//                    ObjState.destroy(actor);
                }
            }
        }
        for (int i=0; i<toBeDestroyed.size(); i++) {
            Actor actor = (Actor)toBeDestroyed.get(i);
            ObjState.destroy(actor);
        }
        toBeDestroyed.clear();
        
        return true;
    }

    public void createNetObject(NetChannel netchannel, int i) {
        if (netchannel == null) {
            this.net = new Master(this);
        } else {
            this.net = new Mirror(this, netchannel, i);
        }
    }

    public void netFirstUpdate(NetChannel netchannel) throws IOException {
    }

    public int getDzRadius() {
//        return radius;
        return Property.intValue(this, "radius", 1000);
    }
    
    public void setDzRadius(int radius) {
//        this.radius = radius;
        Property.set(this, "radius", radius);
    }
    
    private static String        mesh_name   = "3do/primitive/siren/mono.sim";
    private float                heightAboveLandSurface;
    protected SoundFX            engineSFX;
    protected int                engineSTimer;
    private int                  myArmy;
//    private int                  radius;
    private static ActorSpawnArg constr_arg2 = null;
    private static Point3d       p           = new Point3d();
    private static Orient        o           = new Orient();
    private static Vector3f      n           = new Vector3f();
}
