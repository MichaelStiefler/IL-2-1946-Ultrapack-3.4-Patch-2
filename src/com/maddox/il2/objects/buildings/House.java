package com.maddox.il2.objects.buildings;

import com.maddox.JGP.Matrix4d;
import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.Explosion;
import com.maddox.il2.ai.MsgExplosionListener;
import com.maddox.il2.ai.MsgShotListener;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorMesh;
import com.maddox.il2.engine.ActorSpawn;
import com.maddox.il2.engine.ActorSpawnArg;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.MeshShared;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.objects.ActorAlign;
import com.maddox.il2.objects.Statics;
import com.maddox.il2.objects.effects.Explosions;
import com.maddox.rts.Finger;
import com.maddox.rts.Message;
import com.maddox.rts.SectFile;
import com.maddox.rts.SoftClass;
import com.maddox.rts.Spawn;

public final class House extends ActorMesh implements MsgExplosionListener, MsgShotListener, ActorAlign, SoftClass {
    public static class SPAWN implements ActorSpawn {

        private static float getF(SectFile sectfile, String s, String s1, float f, float f1) {
            float f2 = sectfile.get(s, s1, -9865.345F);
            if (f2 == -9865.345F || f2 < f || f2 > f1) {
                if (f2 == -9865.345F) System.out.println("House: Value of [" + s + "]:<" + s1 + "> " + "not found");
                else System.out.println("House: Value of [" + s + "]:<" + s1 + "> (" + f2 + ")" + " is out of range (" + f + ";" + f1 + ")");
                throw new RuntimeException("Can't set property");
            } else return f2;
        }

        private static float getF(SectFile sectfile, String s, String s1, float f, float f1, float f2) {
            float f3 = sectfile.get(s, s1, -9865.345F);
            if (f3 == -9865.345F) return f;
            if (f3 < f1 || f3 > f2) {
                System.out.println("House: Value of [" + s + "]:<" + s1 + "> (" + f3 + ")" + " is out of range (" + f1 + ";" + f2 + ")");
                throw new RuntimeException("Can't set property");
            } else return f3;
        }

        private static String getS(SectFile sectfile, String s, String s1) {
            String s2 = sectfile.get(s, s1);
            if (s2 == null || s2.length() <= 0) {
                System.out.print("House: Parameter [" + s + "]:<" + s1 + "> ");
                System.out.println(s2 != null ? "is empty" : "not found");
                throw new RuntimeException("Can't set property");
            } else return s2;
        }

        private static Properties LoadHouseProperties(SectFile sectfile, String s, String s1, String s2) {
            Properties properties = new Properties();
            properties.SoftClassInnerName = s2;
            properties.FingerOfFullClassName = Finger.Int(s1);
            String s3 = sectfile.get(s, "equals");
            if (s3 == null || s3.length() <= 0) s3 = s;
            properties.MESH0_NAME = getS(sectfile, s, "MeshLive");
            properties.MESH1_NAME = sectfile.get(s, "MeshDead");
            if (properties.MESH1_NAME == null) getS(sectfile, s, "MeshDead");
            // TODO: Fixed by SAS~Storebror to avoid possible null dereference
//            if (properties.MESH1_NAME.equals("")) properties.MESH1_NAME = null;
            if (properties.MESH1_NAME != null && properties.MESH1_NAME.equals("")) properties.MESH1_NAME = null;
            properties.ADD_HEIGHT_0 = getF(sectfile, s3, "AddHeightLive", 0.0F, -100F, 100F);
            properties.ADD_HEIGHT_1 = getF(sectfile, s3, "AddHeightDead", 0.0F, -100F, 100F);
            properties.ALIGN_TO_LAND_NORMAL = getF(sectfile, s3, "AlignToLand", 0.0F, 0.0F, 1.0F) > 0.0F;
            properties.ALIGN_TO_LAND_NORMAL = getF(sectfile, s3, "AlignToLand", 0.0F, 0.0F, 1.0F) > 0.0F;
            properties.IGNORE_SHADOW_DATA = false;
            if (sectfile.get(s, "IgnoreShadowData") != null) properties.IGNORE_SHADOW_DATA = true;
            properties.PANZER = getF(sectfile, s3, "Panzer", 0.0001F, 50F);
            String s4 = getS(sectfile, s3, "Body");
            if (s4.equalsIgnoreCase("WoodSmall")) {
                properties.MAT_TYPE = MAT_WOOD;
                properties.EFF_BODY_MATERIAL = 3;
                properties.EXPL_TYPE = 0;
                properties.MIN_TNT = 0.25F * (properties.PANZER / 0.025F);
                properties.MAX_TNT = properties.MIN_TNT * 2.0F;
                properties.PROBAB_DEATH_WHEN_EXPLOSION = 0.01F;
            } else if (s4.equalsIgnoreCase("WoodMiddle")) {
                properties.MAT_TYPE = MAT_WOOD;
                properties.EFF_BODY_MATERIAL = 3;
                properties.EXPL_TYPE = 1;
                properties.MIN_TNT = 5F * (properties.PANZER / 0.15F);
                properties.MAX_TNT = properties.MIN_TNT * 1.7F;
                properties.PROBAB_DEATH_WHEN_EXPLOSION = 0.01F;
            } else if (s4.equalsIgnoreCase("RockMiddle")) {
                properties.MAT_TYPE = MAT_BRICK;
                properties.EFF_BODY_MATERIAL = 4;
                properties.EXPL_TYPE = 2;
                properties.MIN_TNT = 6F * (properties.PANZER / 0.12F);
                properties.MAX_TNT = properties.MIN_TNT * 1.7F;
                properties.PROBAB_DEATH_WHEN_EXPLOSION = 0.01F;
            } else if (s4.equalsIgnoreCase("RockBig")) {
                properties.MAT_TYPE = MAT_BRICK;
                properties.EFF_BODY_MATERIAL = 4;
                properties.EXPL_TYPE = 3;
                properties.MIN_TNT = 12F * (properties.PANZER / 0.24F);
                properties.MAX_TNT = properties.MIN_TNT * 1.7F;
                properties.PROBAB_DEATH_WHEN_EXPLOSION = 0.05F;
            } else if (s4.equalsIgnoreCase("RockHuge")) {
                properties.MAT_TYPE = MAT_BRICK;
                properties.EFF_BODY_MATERIAL = 4;
                properties.EXPL_TYPE = 4;
                properties.MIN_TNT = 30F * (properties.PANZER / 0.48F);
                properties.MAX_TNT = properties.MIN_TNT * 1.7F;
                properties.PROBAB_DEATH_WHEN_EXPLOSION = 0.05F;
            } else if (s4.equalsIgnoreCase("FuelSmall")) {
                properties.MAT_TYPE = MAT_STEEL;
                properties.EFF_BODY_MATERIAL = 2;
                properties.EXPL_TYPE = 5;
                properties.MIN_TNT = 0.2F * (properties.PANZER / 0.002F);
                properties.MAX_TNT = properties.MIN_TNT * 1.7F;
                properties.PROBAB_DEATH_WHEN_EXPLOSION = 0.01F;
            } else if (s4.equalsIgnoreCase("FuelBig")) {
                properties.MAT_TYPE = MAT_STEEL;
                properties.EFF_BODY_MATERIAL = 2;
                properties.EXPL_TYPE = 6;
                properties.MIN_TNT = 0.8F * (properties.PANZER / 0.008F);
                properties.MAX_TNT = properties.MIN_TNT * 1.7F;
                properties.PROBAB_DEATH_WHEN_EXPLOSION = 0.01F;
            } else {
                System.out.println("House: Undefined Body type in class '" + s1 + "'");
                System.out.println("Allowed body types are:WoodSmall,WoodMiddle,RockMiddle,RockBig,RockHuge,FuelSmall,FuelBig");
                throw new RuntimeException("Can't register house object");
            }
            return properties;
        }

        public Actor actorSpawn(ActorSpawnArg actorspawnarg) {
            House house = null;
            try {
                house = (House) this.cls.newInstance();
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                exception.printStackTrace();
                return null;
            }
            house.prop = this.prop;
            if (!actorspawnarg.armyExist) {
                actorspawnarg.armyExist = true;
                actorspawnarg.army = 0;
            }
            actorspawnarg.setStationaryNoIcon(house);
            try {
                house.activateMesh();
            } catch (RuntimeException runtimeexception) {
                house.destroy();
                throw runtimeexception;
            }
            if (!house.prop.meshTested && house.mesh().collisionR() <= 0.0F) printDebugMessage("##### House without collision (" + house.prop.MESH0_NAME + ")");
            house.prop.meshTested = true;
            return house;
        }

        public Class      cls;
        public Properties prop;

        protected SPAWN(String s) {
            this.cls = this.getClass().getDeclaringClass();
            String s1 = this.cls.getName();
            String s2 = s1.substring(s1.lastIndexOf('.') + 1);
            String s3 = s2 + '$' + s;
            String s4 = s1 + '$' + s;
            SectFile sectfile = Statics.getBuildingsFile();
            String s5 = null;
            int i = sectfile.sections();
            int j = 0;
            do {
                if (j >= i) break;
                if (sectfile.sectionName(j).endsWith(s3)) {
                    s5 = sectfile.sectionName(j);
                    break;
                }
                j++;
            } while (true);
            if (s5 == null) {
                System.out.println("House: Section " + s3 + " not found");
                throw new RuntimeException("Can't register spawner");
            }
            try {
                this.prop = LoadHouseProperties(sectfile, s5, s4, s);
            } catch (Exception exception) {
                System.out.println(exception.getMessage());
                exception.printStackTrace();
                System.out.println("Problem in house spawn registration: " + s4);
            }
            Spawn.add_SoftClass(s4, this);
        }
    }

    public static class Properties {

        public String   SoftClassInnerName;
        public int      FingerOfFullClassName;
        public String   MESH0_NAME;
        public String   MESH1_NAME;
        private boolean meshTested;
        public float    ADD_HEIGHT_0;
        public float    ADD_HEIGHT_1;
        public boolean  ALIGN_TO_LAND_NORMAL;
        public boolean  IGNORE_SHADOW_DATA;
        public int      MAT_TYPE;
        public int      EFF_BODY_MATERIAL;
        public int      EXPL_TYPE;
        public float    PANZER;
        public float    MIN_TNT;
        public float    MAX_TNT;
        public float    PROBAB_DEATH_WHEN_EXPLOSION;
        public boolean  bInitHeight0;
        public boolean  bInitHeight1;
        public float    heightAboveLandSurface0;
        public float    heightAboveLandSurface1;

        public Properties() {
            this.SoftClassInnerName = null;
            this.FingerOfFullClassName = 0;
            this.MESH0_NAME = "3do/BuildingsGeneral/NameNotSpecified.sim";
            this.MESH1_NAME = "3do/BuildingsGeneral/DMG/NameNotSpecified.sim";
            this.meshTested = false;
            this.ADD_HEIGHT_0 = 0.0F;
            this.ADD_HEIGHT_1 = 0.0F;
            this.ALIGN_TO_LAND_NORMAL = false;
            this.IGNORE_SHADOW_DATA = false;
            this.MAT_TYPE = -1;
            this.EFF_BODY_MATERIAL = -1;
            this.EXPL_TYPE = -1;
            this.PANZER = 0.001F;
            this.MIN_TNT = 0.07F;
            this.MAX_TNT = 0.071F;
            this.PROBAB_DEATH_WHEN_EXPLOSION = 0.4F;
            this.bInitHeight0 = false;
            this.bInitHeight1 = false;
        }
    }

    public House() {
        this.prop = null;
    }

    public static void registerSpawner(String s) {
        new SPAWN(s);
    }

    public String fullClassName() {
        return this.getClass().getName() + "$" + this.prop.SoftClassInnerName;
    }

    public int fingerOfFullClassName() {
        return this.prop.FingerOfFullClassName;
    }

    public String getMeshLiveName() {
        return this.prop.MESH0_NAME;
    }

    private static final boolean RndB(float f) {
        return World.Rnd().nextFloat(0.0F, 1.0F) < f;
    }

    private static final float RndF(float f, float f1) {
        return World.Rnd().nextFloat(f, f1);
    }

    private static final void internalerrror(int i) {
        System.out.println("*** Internal error#" + i + " in House ***");
        throw new RuntimeException("Can't initialize House");
    }

    private static final void InitTablesOfEnergyToKill() {
        PenetrateEnergyToKill = new float[3][][];
        PenetrateThickness = new float[3][];
        PenetrateThickness[0] = new float[] { 0.025F, 0.15F };
        PenetrateEnergyToKill[0] = new float[][] { new float[] { KinEnergy_20, KinEnergy_37, KinEnergy_45 }, new float[] { KinEnergy_37, KinEnergy_50, KinEnergy_75 } };
        PenetrateThickness[1] = new float[] { 0.12F, 0.24F };
        PenetrateEnergyToKill[1] = new float[][] { new float[] { KinEnergy_37, KinEnergy_45, KinEnergy_100 }, new float[] { KinEnergy_50, KinEnergy_100, KinEnergy_203 } };
        PenetrateThickness[2] = new float[] { 0.002F, 0.008F };
        PenetrateEnergyToKill[2] = new float[][] { new float[] { KinEnergy_4, KinEnergy_7_62, KinEnergy_20 }, new float[] { KinEnergy_7_62, KinEnergy_12_7, KinEnergy_20 } };
        for (int i = 0; i < N_MAT_TYPES; i++) {
            if (Math.abs(PenetrateThickness[i][0] - PenetrateThickness[i][1]) < 0.001D) internalerrror(1);
            for (int j = 0; j <= 1; j++) {
                float af[] = PenetrateEnergyToKill[i][j];
                if (af[1] - af[0] < 0.001D || af[2] - af[1] < 0.001D) internalerrror(2);
            }

            float f = Math.min(PenetrateEnergyToKill[i][0][0], PenetrateEnergyToKill[i][1][0]);
            float f1 = Math.max(PenetrateEnergyToKill[i][0][2], PenetrateEnergyToKill[i][1][2]);
            for (int k = 0; k <= 100; k++) {
                float f2 = f + (f1 - f) * k / 100F;
                float f3 = ComputeProbabOfPenetrateKill(i, 0, f2);
                float f4 = ComputeProbabOfPenetrateKill(i, 1, f2);
                if (f3 < f4) {
                    System.out.println(i + " i,e0,e1,e:" + k + " " + f + " " + f1 + " " + f2 + " prob0,1: " + f3 + " " + f4);
                    internalerrror(3);
                }
            }

        }

    }

    private static final float ComputeProbabOfPenetrateKill(int i, int j, float f) {
        float af[] = PenetrateEnergyToKill[i][j];
        float f1;
        float f2;
        if (f < af[1]) {
            if (f < af[0]) return 0.0F;
            f1 = 0.2F / (af[1] - af[0]);
            f2 = 0.1F - af[0] * f1;
        } else {
            if (f >= af[2]) return 1.0F;
            f1 = 0.7F / (af[2] - af[1]);
            f2 = 0.3F - af[1] * f1;
        }
        return f * f1 + f2;
    }

    private final float ComputeProbabOfPenetrateKill(float f, int i) {
        if (i <= 0) return 0.0F;
        float f1 = ComputeProbabOfPenetrateKill(this.prop.MAT_TYPE, 0, f);
        float f2 = ComputeProbabOfPenetrateKill(this.prop.MAT_TYPE, 1, f);
        float af[] = PenetrateThickness[this.prop.MAT_TYPE];
        float f3 = (f2 - f1) / (af[1] - af[0]);
        float f4 = f1 - af[0] * f3;
        float f5 = this.prop.PANZER * f3 + f4;
        if (f5 < 0.1F) f5 = 0.0F;
        else if (f5 >= 1.0F) f5 = 1.0F;
        else if (i > 1) f5 = 1.0F - (float) Math.pow(1.0F - f5, i);
        return f5;
    }

    public void msgShot(Shot shot) {
        shot.bodyMaterial = this.prop.EFF_BODY_MATERIAL;
        if (!this.isAlive()) return;
        if (shot.power <= 0.0F) return;
        if (shot.powerType == 1) {
            if (this.prop.MAT_TYPE != 2) return;
            float f = shot.power * RndF(0.75F, 1.15F);
            float f2 = 0.256F * (float) Math.sqrt(Math.sqrt(f));
            if (this.prop.PANZER > f2) return;
            else {
                this.die(shot.initiator, true);
                return;
            }
        }
        float f1 = this.ComputeProbabOfPenetrateKill(shot.power, 1);
        if (!RndB(f1)) return;
        else {
            this.die(shot.initiator, true);
            return;
        }
    }

    public void msgExplosion(Explosion explosion) {
        if (!this.isAlive()) return;
        if (explosion.power <= 0.0F) return;
        if (explosion.powerType == 1) {
            float af[] = new float[6];
            this.mesh().getBoundBox(af);
            this.pos.getAbs(p);
            p.x = p.x - af[0] + (af[3] - af[0]);
            p.y = p.y - af[1] + (af[4] - af[1]);
            p.z = p.z - af[2] + (af[5] - af[2]);
            float af1[] = new float[2];
            explosion.computeSplintersHit(p, this.mesh().collisionR(), 0.7F, af1);
            float f = 0.015F * af1[1] * af1[1] * 0.5F;
            float f1 = this.ComputeProbabOfPenetrateKill(f, (int) (af1[0] + 0.5F));
            if (RndB(f1)) this.die(explosion.initiator, true);
            return;
        }
        if (explosion.powerType == 0) {
            if (Explosion.killable(this, explosion.receivedPower(this), this.prop.MIN_TNT, this.prop.MAX_TNT, this.prop.PROBAB_DEATH_WHEN_EXPLOSION)) this.die(explosion.initiator, true);
            return;
        }
        if (this.prop.MAT_TYPE == 1) return;
        else {
            this.die(explosion.initiator, true);
            return;
        }
    }

    protected void runDeathShow() {
        float af[] = new float[6];
        this.mesh().getBoundBox(af);
        Explosions.HouseExplode(this.prop.EXPL_TYPE, this.pos.getAbs(), af);
    }

    private void die(Actor actor, boolean flag) {
        if (!this.isAlive()) return;
        if (flag) this.runDeathShow();
        World.onActorDied(this, actor);
        if (this.getOwner() instanceof HouseManager) ((HouseManager) this.getOwner()).onHouseDie(this, actor);
        else if (World.cur().statics != null) World.cur().statics.onHouseDied(this, actor);
    }

    public void doDieShow() {
        this.runDeathShow();
    }

    public void setDiedFlag(boolean flag) {
        super.setDiedFlag(flag);
        this.activateMesh();
    }

    private void activateMesh() {
        boolean flag = this.getDiedFlag();
        String s = flag ? this.prop.MESH1_NAME : this.prop.MESH0_NAME;
        if (s == null) {
            this.collide(false);
            this.drawing(false);
            return;
        }
        try {
            this.setMesh(MeshShared.get(s)); // TODO: Modified by SAS~Storebror, deal with missing dead meshes
        } catch (Exception e) {
            System.out.println("House activateMesh(): Couldn't set mesh \"" + s + "\".");
            return;
        }
        if (flag ? !this.prop.bInitHeight1 : !this.prop.bInitHeight0) {
            int i = this.mesh().hookFind("Ground_Level");
            float f = 0.0F;
            if (i != -1) {
                Matrix4d matrix4d = new Matrix4d();
                this.mesh().hookMatrix(i, matrix4d);
                f = (float) -matrix4d.m23;
            } else {
                float af[] = new float[6];
                this.mesh().getBoundBox(af);
                f = -af[2];
            }
            if (!flag) {
                this.prop.bInitHeight0 = true;
                this.prop.heightAboveLandSurface0 = f;
            } else {
                this.prop.bInitHeight1 = true;
                this.prop.heightAboveLandSurface1 = f;
            }
        }
        if (this.prop.IGNORE_SHADOW_DATA) this.mesh().setFastShadowVisibility(2);
        else this.mesh().setFastShadowVisibility(1);
        this.align();
        this.collide(!flag);
        this.drawing(true);
    }

    public void align() {
        this.pos.getAbs(p);
        p.z = Engine.land().HQ(p.x, p.y);
        if (!this.getDiedFlag()) p.z += this.prop.ADD_HEIGHT_0 + this.prop.heightAboveLandSurface0;
        else p.z += this.prop.ADD_HEIGHT_1 + this.prop.heightAboveLandSurface1;
        o.setYPR(this.pos.getAbsOrient().getYaw(), 0.0F, 0.0F);
        if (this.prop.ALIGN_TO_LAND_NORMAL) {
            Engine.land().N(p.x, p.y, n);
            o.orient(n);
        }
        this.pos.setAbs(p, o);
    }

    public Object getSwitchListener(Message message) {
        return this;
    }

    public boolean isStaticPos() {
        return true;
    }

    public float futurePosition(float f, Point3d point3d) {
        this.pos.getAbs(point3d);
        if (f <= 0.0F) return 0.0F;
        else return f;
    }

    private Properties         prop;
    private static final int   MAT_WOOD                    = 0;
    private static final int   MAT_BRICK                   = 1;
    private static final int   MAT_STEEL                   = 2;
    private static final int   N_MAT_TYPES                 = 3;
    private static float       PenetrateEnergyToKill[][][] = null;
    private static float       PenetrateThickness[][]      = null;
    private static final float KinEnergy_4                 = 511.225F;
    private static final float KinEnergy_7_62              = 2453.88F;
    private static final float KinEnergy_12_7              = 10140F;
    private static final float KinEnergy_20                = 23400F;
    private static final float KinEnergy_37                = 131400F;
    private static final float KinEnergy_45                = 252000F;
    private static final float KinEnergy_50                = 369000F;
    private static final float KinEnergy_75                = 1224000F;
    private static final float KinEnergy_100               = 3295500F;
    private static final float KinEnergy_203               = 5120000F;
    private static Point3d     p                           = new Point3d();
    private static Orient      o                           = new Orient();
    private static Vector3f    n                           = new Vector3f();
//    private static float probab[] = new float[2];

    private static int       debugLevel    = Integer.MIN_VALUE;
    private static final int DEBUG_DEFAULT = 0;

    private static int curDebugLevel() {
        if (debugLevel == Integer.MIN_VALUE) debugLevel = Config.cur.ini.get("Mods", "DEBUG_HOUSE", DEBUG_DEFAULT);
        return debugLevel;
    }

    public static void printDebugMessage(String theMessage) {
        if (curDebugLevel() == 0) return;
        System.out.println(theMessage);
    }

    static {
        InitTablesOfEnergyToKill();
    }

}
