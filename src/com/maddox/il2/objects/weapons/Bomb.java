/* 4.101 class used from Storebor. UP Compatible. */
package com.maddox.il2.objects.weapons;

import java.util.AbstractCollection;

import com.maddox.JGP.Color3f;
import com.maddox.JGP.Geom;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.MsgExplosion;
import com.maddox.il2.ai.RangeRandom;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorFilter;
import com.maddox.il2.engine.ActorMesh;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.Interpolate;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.il2.engine.LightPointWorld;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Mesh;
import com.maddox.il2.engine.MeshShared;
import com.maddox.il2.engine.MsgCollisionListener;
import com.maddox.il2.engine.MsgCollisionRequestListener;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.game.Mission;
import com.maddox.il2.net.Chat;
import com.maddox.il2.objects.ActorCrater;
import com.maddox.il2.objects.ActorLand;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.buildings.Plate;
import com.maddox.il2.objects.effects.Explosions;
import com.maddox.rts.Message;
import com.maddox.rts.MsgInvokeMethod_Object;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.TrueRandom;
import com.maddox.sound.SoundFX;

public class Bomb extends ActorMesh implements MsgCollisionRequestListener, MsgCollisionListener {

    class Interpolater extends Interpolate {

        public boolean tick() {
            Bomb.this.interpolateTick();
            return true;
        }

        Interpolater() {
        }
    }

    static class DelayParam {

        Actor   other;
        String  otherChunk;
        Point3d p;
        Loc     loc;

        DelayParam(Actor actor, String s, Loc loc1) {
            this.p = new Point3d();
            this.other = actor;
            this.otherChunk = s;
            loc1.get(this.p);
            if (Actor.isValid(actor)) {
                this.loc = new Loc();
                this.other.pos.getTime(Time.current(), Bomb.__loc);
                this.loc.sub(loc1, Bomb.__loc);
            }
        }
    }

    private static class PlateFilter implements ActorFilter {

        public boolean isUse(Actor actor, double d) {
            if (!(actor instanceof Plate)) {
                return true;
            }
            Mesh mesh = ((ActorMesh) actor).mesh();
            mesh.getBoundBox(Bomb.plateBox);
            Bomb.corn1.set(Bomb.corn);
            Loc loc1 = actor.pos.getAbs();
            loc1.transformInv(Bomb.corn1);
            if (((Bomb.plateBox[0] - 2.5F) < Bomb.corn1.x) && (Bomb.corn1.x < (Bomb.plateBox[3] + 2.5F)) && ((Bomb.plateBox[1] - 2.5F) < Bomb.corn1.y) && (Bomb.corn1.y < (Bomb.plateBox[4] + 2.5F))) {
                Bomb.bPlateExist = true;
            }
            return true;
        }

        private PlateFilter() {
        }
    }

    public void msgCollisionRequest(Actor actor, boolean aflag[]) {
        if (actor == this.getOwner()) {
            aflag[0] = false;
        }
    }

    public void msgCollision(Actor actor, String s, String s1) {
        this.pos.getTime(Time.current(), Bomb.p);
        this.impact = Time.current() - this.started;
        if ((this.impact < this.armingTime) && this.isArmed) {
            //            System.out.println("================================");
            this.isArmed = false;
        } else if ((this.impact >= this.armingTime) && !this.isArmed) {
            if (!(actor instanceof ActorLand)) {
                //                System.out.println("================================");
                this.isArmed = true;
            }
        }

        if ((actor != null) && (actor instanceof ActorLand) && this.isPointApplicableForJump()) {
            if (this.speed.z >= 0.0D) {
                return;
            }
            float f = (float) this.speed.length();
            if (f >= 30F) {
                Bomb.dir.set(this.speed);
                Bomb.dir.scale(1.0F / f);
                if (-Bomb.dir.z < 0.31F) {
                    this.pos.getAbs(Bomb.or);
                    Bomb.dirN.set(1.0F, 0.0F, 0.0F);
                    Bomb.or.transform(Bomb.dirN);
                    if (Bomb.dir.dot(Bomb.dirN) >= 0.91F) {
                        float f1 = -Bomb.dir.z;
                        f1 *= 3.225806F;
                        f1 = 0.85F - (0.35F * f1);
                        // TODO: Storebror: Bomb Spread Replication
                        // ------------------------------------
                        f1 *= this.getRnd().nextFloat(0.85F, 1.0F);
                        // ------------------------------------
                        this.speed.scale(f1);
                        this.speed.z *= f1;
                        if (this.speed.z < 0.0D) {
                            this.speed.z = -this.speed.z;
                        }
                        Bomb.p.z = Engine.land().HQ(Bomb.p.x, Bomb.p.y);
                        this.pos.setAbs(Bomb.p);
                        if (this.M >= 200F) {
                            f1 = 1.0F;
                        } else if (this.M <= 5F) {
                            f1 = 0.0F;
                        } else {
                            f1 = (this.M - 5F) / 195F;
                        }
                        float f2 = 3.5F + (f1 * 12F);
                        if (Engine.land().isWater(Bomb.p.x, Bomb.p.y)) {
                            Explosions.SomethingDrop_Water(Bomb.p, f2);
                        } else {
                            Explosions.Explode10Kg_Land(Bomb.p, 2.0F, 2.0F);
                        }
                        return;
                    }
                }
            }
        }
        if ((this.getOwner() == World.getPlayerAircraft()) && !(actor instanceof ActorLand)) {
            World.cur().scoreCounter.bombHit++;
            if (Mission.isNet() && (actor instanceof Aircraft) && ((Aircraft) actor).isNetPlayer()) {
                Chat.sendLogRnd(3, "gore_bombed", (Aircraft) this.getOwner(), (Aircraft) actor);
            }
        }
        if (this.delayExplosion > 0.0F) {
            // TODO: Storebror: +++ Bomb/Rocket Fuze/Delay Replication
            Bomb.printDebug(this.getOwner(), "Bomb " + Bomb.simpleClassName(this) + " msgCollision, delayExplosion=" + this.delayExplosion);
            // TODO: Storebror: --- Bomb/Rocket Fuze/Delay Replication
            this.pos.getTime(Time.current(), Bomb.loc);
            this.collide(false);
            this.drawing(false);
            // TODO: Storebror: +++ Make Bomb stop moving after impact when delayed explosion is set
            this.speed.set(0, 0, 0);
            // TODO: Storebror: --- Make Bomb stop moving after impact when delayed explosion is set
            DelayParam delayparam = new DelayParam(actor, s1, Bomb.loc);
            if (Bomb.p.z < (Engine.land().HQ(Bomb.p.x, Bomb.p.y) + 5D)) {
                if (Engine.land().isWater(Bomb.p.x, Bomb.p.y)) {
                    Explosions.Explode10Kg_Water(Bomb.p, 2.0F, 2.0F);
                } else {
                    Explosions.Explode10Kg_Land(Bomb.p, 2.0F, 2.0F);
                }
            }
            this.interpEndAll();
            new MsgInvokeMethod_Object("doDelayExplosion", delayparam).post(this, this.delayExplosion);
            if (this.sound != null) {
                this.sound.cancel();
            }
        } else {
            this.doExplosion(actor, s1);
        }
    }

    private boolean isPointApplicableForJump() {
        if (Engine.land().isWater(Bomb.p.x, Bomb.p.y)) {
            return true;
        }
        float f = 200F;
        Bomb.bPlateExist = false;
        Bomb.p.get(Bomb.corn);
        Engine.drawEnv().getFiltered((AbstractCollection) null, Bomb.corn.x - f, Bomb.corn.y - f, Bomb.corn.x + f, Bomb.corn.y + f, 1, Bomb.plateFilter);
        if (Bomb.bPlateExist) {
            return true;
        }
        int i = Engine.cur.land.HQ_RoadTypeHere(Bomb.p.x, Bomb.p.y);
        switch (i) {
            case 1:
                return true;

            case 2:
                return true;

            case 3:
                return false;
        }
        return false;
    }

    public void doDelayExplosion(Object obj) {
        DelayParam delayparam = (DelayParam) obj;
        // TODO: Storebror: +++ Bomb/Rocket Fuze/Delay Replication
        Bomb.printDebug(this.getOwner(), "Bomb " + Bomb.simpleClassName(this) + " doDelayExplosion");
        // TODO: Storebror: --- Bomb/Rocket Fuze/Delay Replication
        if (Actor.isValid(delayparam.other)) {
            delayparam.other.pos.getTime(Time.current(), Bomb.__loc);
            delayparam.loc.add(Bomb.__loc);
            this.doExplosion(delayparam.other, delayparam.otherChunk, delayparam.loc.getPoint());
        } else {
            this.doExplosion(Engine.actorLand(), "Body", delayparam.p);
        }
    }

    protected void doExplosion(Actor actor, String s) {
        // TODO: Storebror: +++ Bomb/Rocket Fuze/Delay Replication
        Bomb.printDebug(this.getOwner(), "Bomb " + Bomb.simpleClassName(this) + " doExplosion w/o Point3d");
        // TODO: Storebror: --- Bomb/Rocket Fuze/Delay Replication
        this.pos.getTime(Time.current(), Bomb.p);
        if (this.isArmed()) {
            this.doExplosion(actor, s, Bomb.p);
        } else {
            if (Bomb.p.z < (Engine.land().HQ(Bomb.p.x, Bomb.p.y) + 5D)) {
                if (Engine.land().isWater(Bomb.p.x, Bomb.p.y)) {
                    Explosions.Explode10Kg_Water(Bomb.p, 2.0F, 2.0F);
                } else {
                    Explosions.Explode10Kg_Land(Bomb.p, 2.0F, 2.0F);
                }
            }
            this.destroy();
        }
    }

    protected void doExplosion(Actor actor, String s, Point3d point3d) {
        // TODO: Storebror: +++ Bomb/Rocket Fuze/Delay Replication
        Bomb.printDebug(this.getOwner(), "Bomb " + Bomb.simpleClassName(this) + " doExplosion w/ Point3d");
        // TODO: Storebror: --- Bomb/Rocket Fuze/Delay Replication
        Class class1 = this.getClass();
        float f = Property.floatValue(class1, "power", 1000F);
        int i = Property.intValue(class1, "powerType", 0);
        float f1 = Property.floatValue(class1, "radius", 150F);

        // TODO: Storebror: Random Bomb Explosion Radius
        // ------------------------------------
        f1 *= this.getRnd().nextFloat(0.85F, 1.0F);
        // ------------------------------------

        int j = Property.intValue(class1, "newEffect", 0);
        int k = Property.intValue(class1, "nuke", 0);
        if (this.isArmed()) {
            MsgExplosion.send(actor, s, point3d, this.getOwner(), this.M, f, i, f1, k);
            ActorCrater.initOwner = this.getOwner();
            // Explosions.generate(actor, point3d, f, i, f1, !Mission.isNet(), j);
            Explosions.generate(actor, point3d, f, i, f1, false, j);
            ActorCrater.initOwner = null;
            this.destroy();
        } else {
            this.destroy();
        }
    }

    private boolean isArmed() {
        return this.isArmed || (this instanceof BombSD2A) || (this instanceof BombSD4HL) || (this instanceof BombB22EZ);
    }

    public boolean isFuseArmed() {
        return this.isArmed;
    }

    public void interpolateTick() {
        this.curTm += Time.tickLenFs();
        Ballistics.updateBomb(this, this.M, this.S, this.J, this.DistFromCMtoStab);
        this.updateSound();
    }

    // TODO: Storebror: Bomb Spread Replication
    // ------------------------------------
    float RndFloatSign(float f, float f1) {
        f = this.getRnd().nextFloat(f, f1);
        return this.getRnd().nextFloat(0.0F, 1.0F) <= 0.5F ? -f : f;
    }

    private void randomizeStart(Orient orient, Vector3d vector3d, float f, int i) {
        // ------------------------------------
        if (i != 0) {
            Bomb.dir.set(this.RndFloatSign(0.1F, 1.0F), this.RndFloatSign(0.1F, 1.0F), this.RndFloatSign(0.1F, 1.0F));
            Bomb.dir.normalize();
        } else {
            Bomb.dir.set(1.0F, 0.0F, 0.0F);
            orient.transform(Bomb.dir);
            float f1 = 0.04F;
            // TODO: Storebror: Bomb Spread Replication
            // ------------------------------------
            Bomb.dir.add(this.getRnd().nextFloat(-f1, f1), this.getRnd().nextFloat(-f1, f1), this.getRnd().nextFloat(-f1, f1));
            // ------------------------------------
            Bomb.dir.normalize();
        }
        orient.setAT0(Bomb.dir);
        vector3d.set(this.RndFloatSign(0.1F, 1.0F), this.RndFloatSign(0.1F, 1.0F), this.RndFloatSign(0.1F, 1.0F));
        vector3d.normalize();
        float f2 = Geom.DEG2RAD(this.RndFloatSign(2.0F, 35F));
        if (f > 60F) {
            float f3 = 0.05F;
            if (f < 350F) {
                f3 = 1.0F - ((f - 60F) / 290F);
                f3 = (f3 * 0.95F) + 0.05F;
            }
            f2 *= f3;
        }
        if (i != 0) {
            f2 *= 0.2F;
        }
        vector3d.scale(f2);
    }

    public double getSpeed(Vector3d vector3d) {
        if (vector3d != null) {
            vector3d.set(this.speed);
        }
        return this.speed.length();
    }

    public void setSpeed(Vector3d vector3d) {
        this.speed.set(vector3d);
    }

    protected void init(float f, float f1) {
        if (Actor.isValid(this.getOwner()) && (World.getPlayerAircraft() == this.getOwner())) {
            this.setName("_bomb_");
        }
        super.getSpeed(this.speed);
        if ((f1 > 35F) && World.cur().diffCur.Wind_N_Turbulence) {
            Point3d point3d = new Point3d();
            Vector3d vector3d = new Vector3d();
            this.pos.getAbs(point3d);
            World.wind().getVectorWeapon(point3d, vector3d);
            this.speed.add(-vector3d.x, -vector3d.y, 0.0D);
        }
        this.S = (float) ((Math.PI * f * f) / 4D);
        this.M = f1;
        // TODO: Storebror: Bomb Spread Replication
        // ------------------------------------
        this.M *= this.getRnd().nextFloat(1.0F, 1.06F);
        // ------------------------------------
        float f2 = f * 0.5F;
        float f3 = f * 4F;
        float f4 = f2;
        float f5 = f3 * 0.5F;
        this.J = this.M * 0.1F * (f4 * f4 * f5 * f5);
        this.DistFromCMtoStab = f3 * 0.05F;
    }

    protected void updateSound() {
        if (this.sound != null) {
            this.sound.setControl(200, (float) this.getSpeed(null));
            if (this.curTm < 5F) {
                this.sound.setControl(201, this.curTm);
            } else if (this.curTm < (5F + (2 * Time.tickConstLen()))) {
                this.sound.setControl(201, 5F);
            }
        }
    }

    protected boolean haveSound() {
        return true;
    }

    public boolean isBomb() {
        if (this instanceof BombNull) return false;
        if (this instanceof FuelTank) return false;
        if (Property.floatValue(this.getClass(), "power", 0F) < 0.1F) return false;
        return true;
    }

    public void start() {
        Class class1 = this.getClass();
        this.init(Property.floatValue(class1, "kalibr", 0.082F), Property.floatValue(class1, "massa", 6.8F));
        this.started = Time.current();
        this.curTm = 0.0F;

        // TODO: 4.14.1 Backport +++
        if (Property.intValue(class1, "rotate", 0) == 1) {
            this.rotatingFins = true;
            // TODO: 4.14.1 Backport ---
        }

        this.setOwner(this.pos.base(), false, false, false);
        this.pos.setBase(null, null, true);
        this.pos.setAbs(this.pos.getCurrent());
        this.pos.getAbs(Bomb.or);
        this.randomizeStart(Bomb.or, this.rotAxis, this.M, Property.intValue(class1, "randomOrient", 0));
        this.pos.setAbs(Bomb.or);
        this.getSpeed(Bomb.spd);
        this.pos.getAbs(Bomb.P, Bomb.Or);
        Vector3d vector3d = new Vector3d(0.0D, 0.0D, 0.0D);
        vector3d.x += this.getRnd().nextFloat_Dome(-2F, 2.0F);
        vector3d.y += this.getRnd().nextFloat_Dome(-1.2F, 1.2F);
        Bomb.Or.transform(vector3d);
        Bomb.spd.add(vector3d);
        this.setSpeed(Bomb.spd);
        this.getSpeed(Bomb.spd);
        this.collide(true);
        this.interpPut(new Interpolater(), null, Time.current(), null);
        this.drawing(true);
        if (Actor.isAlive(World.getPlayerAircraft()) && this.getOwner() == World.getPlayerAircraft()) {
            if (this.isBomb()) World.cur().scoreCounter.bombFire++;
            FlightModel flightmodel = World.getPlayerFM();
            flightmodel.M.computeParasiteMass(flightmodel.CT.Weapons);
            // TODO: By SAS~Storebror: Mass Factors +++
//            flightmodel.getW().y -= 0.0004F * Math.min(this.M, 50F);
            flightmodel.getW().y -= 0.0004F * Math.min(this.M * flightmodel.M.getParasiteFactor(), 50F);
            // TODO: By SAS~Storebror: Mass Factors ---
        }
        if (Property.containsValue(class1, "emitColor")) {
            LightPointActor lightpointactor = new LightPointActor(new LightPointWorld(), new Point3d());
            lightpointactor.light.setColor((Color3f) Property.value(class1, "emitColor", new Color3f(1.0F, 1.0F, 0.5F)));
            lightpointactor.light.setEmit(Property.floatValue(class1, "emitMax", 1.0F), Property.floatValue(class1, "emitLen", 50F));
            this.draw.lightMap().put("light", lightpointactor);
        }
        if (this.haveSound()) {
            String s = Property.stringValue(class1, "sound", null);
            if (s != null) {
                this.sound = this.newSound(s, true);
            }
        }
    }

    public Object getSwitchListener(Message message) {
        return this;
    }

    // TODO: Storebror: Bomb Spread Replication
    // ------------------------------------
    public void setSeed(int seedValue) {
        this.seed = seedValue;
        this.rnd = new RangeRandom(this.seed);
//        System.out.println("Bomb setSeed(" + seedValue + ")");
    }

    public RangeRandom getRnd() {
        return this.rnd;
    }
    // ------------------------------------

    public Bomb() {
        this.setMesh(MeshShared.get(Property.stringValue(this.getClass(), "mesh", null)));
        this.isArmed = true;
        this.armingTime = 2000L;
        this.delayExplosion = 0.0F;
        this.speed = new Vector3d();
        this.rotAxis = new Vector3d(0.0D, 1.0D, 0.0D);
        this.sound = null;
        String s = Property.stringValue(this.getClass(), "mesh", null);
        this.setMesh(MeshShared.get(s));
        this.flags |= 0xe0;
        this.collide(false);
        this.drawing(true);
        // TODO: Storebror: Bomb Spread Replication
        // ------------------------------------
        this.rnd = new RangeRandom(TrueRandom.nextInt());
        // ------------------------------------
        // TODO: 4.14.1 Backport +++
        this.rotatingFins = false;
        this.rotatingSpeed = 0.0F;
        // TODO: 4.14.1 Backport ---
    }

    private long                 started;
    private long                 impact;
    private boolean              isArmed;
    public long                  armingTime;
    static Vector3d              spd            = new Vector3d();
    static Orient                Or             = new Orient();
    static Point3d               P              = new Point3d();
    protected float              delayExplosion;
    float                        curTm;
    protected Vector3d           speed;
    protected float              S;
    protected float              M;
    protected float              J;
    protected float              DistFromCMtoStab;
    Vector3d                     rotAxis;
    protected int                index;
    private static Point3d       p              = new Point3d();
    private static Vector3f      dir            = new Vector3f();
    private static Vector3f      dirN           = new Vector3f();
    private static Orient        or             = new Orient();
    private static Loc           loc            = new Loc();
    private static PlateFilter   plateFilter    = new PlateFilter();
    private static Point3d       corn           = new Point3d();
    private static Point3d       corn1          = new Point3d();
    private static float         plateBox[]     = new float[6];
    private static boolean       bPlateExist    = false;
    private static Loc           __loc          = new Loc();
    protected SoundFX            sound;
    protected static final float SND_TIME_BOUND = 5F;
    // TODO: Storebror: Bomb Spread Replication
    // ------------------------------------
    RangeRandom                  rnd;
    public int                   seed;
    // ------------------------------------

    // TODO: Storebror: +++ Bomb/Rocket Fuze/Delay Replication
    private static int           debugLevel     = Integer.MIN_VALUE;
    private static final int     DEBUG_DEFAULT  = 0;

    // TODO: 4.14.1 Backport +++
    public boolean               rotatingFins;
    public float                 rotatingSpeed;
    // TODO: 4.14.1 Backport ---

    private static int curDebugLevel() {
        if (Bomb.debugLevel == Integer.MIN_VALUE) {
            Bomb.debugLevel = Config.cur.ini.get("Mods", "DEBUG_BOMB", Bomb.DEBUG_DEFAULT);
        }
        return Bomb.debugLevel;
    }

    public static void printDebug(Actor actor, String theMessage) {
        if (Bomb.curDebugLevel() == 0) {
            return;
        }
        if (actor == null) {
            System.out.println("[DEBUG_BOMB] (null) " + theMessage);
            return;
        }
        if (!(actor instanceof NetAircraft)) {
            System.out.println("[DEBUG_BOMB] " + actor.name() + " (" + Bomb.simpleClassName(actor) + ") " + theMessage);
            return;
        }
        NetAircraft netAircraft = (NetAircraft) actor;
        if (netAircraft.netUser() != null) {
            System.out.println("[DEBUG_BOMB] " + netAircraft.netUser().uniqueName() + " (" + Bomb.simpleClassName(actor) + ") " + theMessage);
        } else {
            System.out.println("[DEBUG_BOMB] " + netAircraft.name() + " (" + Bomb.simpleClassName(actor) + ") " + theMessage);
        }

    }

    public static void printDebug(String theMessage) {
        if (Bomb.curDebugLevel() == 0) {
            return;
        }
        System.out.println("[DEBUG_BOMB] " + theMessage);
    }

    public static String simpleClassName(Object o) {
        String fullClassName = o.getClass().getName();
        return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
    }
    // TODO: Storebror: --- Bomb/Rocket Fuze/Delay Replication

    // TODO: Backport from 4.12 Fuzes
    public Fuze fuze                     = null;
    boolean     delayedDetonationStarted = false;

    public void setStartDelayedExplosion(boolean paramBoolean) {
        this.delayedDetonationStarted = paramBoolean;
    }

    private Actor  other;
    private String otherChunk;
    boolean        stopTick = false;

    protected void doMidAirExplosion() {
        this.pos.getTime(Time.current(), Bomb.p);
        this.doMidAirExplosion(Bomb.p);
    }

    private void doMidAirExplosion(Point3d paramPoint3d) {
        Class localClass = this.getClass();
        float f1 = Property.floatValue(localClass, "power", 1000.0F);
        int i = Property.intValue(localClass, "powerType", 0);
        float f2 = Property.floatValue(localClass, "radius", 150.0F);

        MsgExplosion.send(this.other, this.otherChunk, paramPoint3d, this.getScoreOwner(), this.M, f1, i, f2);
        ActorCrater.initOwner = null;
        //Explosions.generateMidAirBombExp(paramPoint3d, f1, i, f2, false);

        this.destroy();

        this.stop();
        this.drawing(false);
    }

    private void stop() {
        this.pos.getTime(Time.current(), Bomb.loc);
        this.interpEndAll();
        this.stopTick = true;
        this.collide(false);
    }

    public Actor getScoreOwner() {
        Actor localActor = super.getOwner();
        if ((localActor instanceof Aircraft)) {
            Aircraft localAircraft1 = (Aircraft) localActor;
            if (!localAircraft1.isNetPlayer()) {
                Aircraft localAircraft2 = localAircraft1.getBombScoreOwner();
                if (localAircraft2 != null) {
                    return localAircraft2;
                }
            }
        }
        return localActor;
    }
    // ---

}
