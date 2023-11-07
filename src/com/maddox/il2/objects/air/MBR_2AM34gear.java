package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Regiment;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Mission;
import com.maddox.il2.objects.Wreckage;
import com.maddox.rts.Property;

public class MBR_2AM34gear extends MBR_2xyz {

    public MBR_2AM34gear() {
        this.skiAngleL = 0.0F;
        this.skiAngleR = 0.0F;
        this.spring = 0.15F;
        this.UseSki = false;
        this.tmpp = new Point3d();
        this.isGround = false;
        this.isWater = false;
    }

    public static String getSkinPrefix(String s, Regiment regiment) {
        if (regiment == null || regiment.country() == null) return "";
        if (regiment.country().equals(PaintScheme.countryRussia)) {
            int i = Mission.getMissionDate(true);
            if (i > 0) {
                if (i > 19420701) return "42_";
                if (i > 19410801) return "41_";
                else return "";
            }
        }
        if (regiment.country().equals(PaintScheme.countryFinland) || regiment.country().equals(PaintScheme.countryGermany) || regiment.country().equals(PaintScheme.countryHungary) || regiment.country().equals(PaintScheme.countryItaly)
                || regiment.country().equals(PaintScheme.countryJapan) || regiment.country().equals(PaintScheme.countryRomania) || regiment.country().equals(PaintScheme.countrySlovakia))
            return "fi_";
        else return "";
    }

    public void missionStarting() {
        super.missionStarting();
        this.clipdetect();
    }

    public void update(float f) {
        super.update(f);
        this.clipdetect();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 2; j++)
                if (this.FM.Gears.clpGearEff[i][j] != null) {
                    this.tmpp.set(this.FM.Gears.clpGearEff[i][j].pos.getAbsPoint());
                    this.tmpp.z = 0.01D;
                    this.FM.Gears.clpGearEff[i][j].pos.setAbs(this.tmpp);
                    this.FM.Gears.clpGearEff[i][j].pos.reset();
                }

        Pn.set(this.FM.Loc);
        Pn.z = Engine.cur.land.HQ(Pn.x, Pn.y);
        if (Engine.cur.land.isWater(Pn.x, Pn.y)) {
            if (this.FM.Gears.lgear && (this.FM.Gears.clpGearEff[0][0] != null || this.FM.Gears.clpGearEff[0][1] != null)) this.cutFM(9, 0, null);
            if (this.FM.Gears.rgear && (this.FM.Gears.clpGearEff[1][0] != null || this.FM.Gears.clpGearEff[1][1] != null)) this.cutFM(10, 0, null);
        }
    }

    public void clipdetect() {
        Pn.set(this.FM.Loc);
        Pn.z = Engine.cur.land.HQ(Pn.x, Pn.y);
        if (Pn.z < 50D & Engine.cur.land.isWater(Pn.x, Pn.y)) {
            if (!this.isWater) {
                this.isWater = true;
                this.isGround = false;
            }
        } else if (!this.isGround) {
            this.isWater = false;
            this.isGround = true;
        }
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.clipdetect();
        if (this.thisWeaponsName.endsWith("RS82")) {
            this.bRSArmed = true;
        }
        if (Config.isUSE_RENDER() && World.cur().camouflage == 1) {
            this.UseSki = true;
        }
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("BombRackInL_D0", !thisWeaponsName.endsWith("RS82") && !thisWeaponsName.endsWith("default") && !thisWeaponsName.endsWith("none"));
        hierMesh.chunkVisible("BombRackInR_D0", !thisWeaponsName.endsWith("RS82") && !thisWeaponsName.endsWith("default") && !thisWeaponsName.endsWith("none"));
        hierMesh.chunkVisible("RSRackL_D0", thisWeaponsName.endsWith("RS82"));
        hierMesh.chunkVisible("RSRackR_D0", thisWeaponsName.endsWith("RS82"));
        System.out.println("camouflage=" + World.cur().camouflage);
        boolean winter = Config.isUSE_RENDER() && World.cur().camouflage == 1;
        hierMesh.chunkVisible("GearL1_D0", !winter);
        hierMesh.chunkVisible("GearR1_D0", !winter);
        hierMesh.chunkVisible("SkiC0_D0", winter);
        hierMesh.chunkVisible("SkiC1_D0", winter);
        hierMesh.chunkVisible("SkiC2_D0", winter);
        hierMesh.chunkVisible("SkiC3_D0", winter);
        hierMesh.chunkVisible("SkiL0_D0", winter);
        hierMesh.chunkVisible("SkiL1_D0", winter);
        hierMesh.chunkVisible("SkiL2_D0", winter);
        hierMesh.chunkVisible("SkiL3_D0", winter);
        hierMesh.chunkVisible("SkiL4_D0", winter);
        hierMesh.chunkVisible("SkiL5_D0", winter);
        hierMesh.chunkVisible("SkiL6_D0", winter);
        hierMesh.chunkVisible("SkiL7_D0", winter);
        hierMesh.chunkVisible("SkiL8_D0", winter);
        hierMesh.chunkVisible("SkiR0_D0", winter);
        hierMesh.chunkVisible("SkiR1_D0", winter);
        hierMesh.chunkVisible("SkiR2_D0", winter);
        hierMesh.chunkVisible("SkiR3_D0", winter);
        hierMesh.chunkVisible("SkiR4_D0", winter);
        hierMesh.chunkVisible("SkiR5_D0", winter);
        hierMesh.chunkVisible("SkiR6_D0", winter);
        hierMesh.chunkVisible("SkiR7_D0", winter);
        hierMesh.chunkVisible("SkiR8_D0", winter);
    }
    
    public void moveWheelSink() {
        if (Config.isUSE_RENDER()) {
            boolean flag = false;
            float f6 = 0.0F;
            float f7 = 0.0F;
            float f8 = 0.0F;
            if (!this.UseSki) {
                float f5 = Aircraft.cvt(this.FM.getSpeed(), 0.0F, 5F, 0.007F, 0.001F);
                float f10 = 0.2F;
                if (this.FM.getSpeed() > 5F) {
                    if (this.FM.Gears.gWheelSinking[0] > 0.002F) f6 = World.Rnd().nextFloat(0.0F, 0.001F);
                    if (this.FM.Gears.gWheelSinking[1] > 0.002F) f7 = World.Rnd().nextFloat(0.0F, 0.001F);
                    if (this.FM.Gears.gWheelSinking[2] > 0.002F) f8 = World.Rnd().nextFloat(0.0F, 0.02F);
                }
                f6 += this.FM.Gears.gWheelSinking[0];
                f7 += this.FM.Gears.gWheelSinking[1];
                f8 += this.FM.Gears.gWheelSinking[2];
                this.resetYPRmodifier();
                float f11 = Aircraft.cvt(f6, 0.0F, f10, -5F, 10F);
                this.hierMesh().chunkSetAngles("GearL2_D0", 0.0F, f11, 0.0F);
                float f13 = Aircraft.cvt(f6, 0.0F, f10, 0.0F, 3F);
                float f15 = Aircraft.floatindex(f13, GearXScale);
                float f17 = Aircraft.floatindex(f13, GearYScale);
                this.hierMesh().chunkSetAngles("GearL3_D0", -f15, -f17, 0.0F);
                float f19 = Aircraft.floatindex(f13, GearXXScale);
                float f21 = Aircraft.floatindex(f13, GearYYScale);
                this.hierMesh().chunkSetAngles("GearL5_D0", -f19, f21, 0.0F);
                float f23 = Aircraft.cvt(f7, 0.0F, f10, -5F, 10F);
                this.hierMesh().chunkSetAngles("GearR2_D0", 0.0F, -f23, 0.0F);
                float f24 = Aircraft.cvt(f7, 0.0F, f10, 0.0F, 3F);
                float f25 = Aircraft.floatindex(f24, GearXScale);
                float f26 = Aircraft.floatindex(f24, GearYScale);
                this.hierMesh().chunkSetAngles("GearR3_D0", f25, f26, 0.0F);
                float f27 = Aircraft.floatindex(f24, GearXXScale);
                float f28 = Aircraft.floatindex(f24, GearYYScale);
                this.hierMesh().chunkSetAngles("GearR5_D0", f27, -f28, 0.0F);
                float f29 = Aircraft.cvt(f8, f5, 0.15F, 0.0F, 6F);
                float f30 = Aircraft.floatindex(f29, GearCScale);
                this.hierMesh().chunkSetAngles("GearC2_D0", 0.0F, 0.0F, -f29);
                this.hierMesh().chunkSetAngles("GearC3_D0", 0.0F, 0.0F, -f30);
                this.hierMesh().chunkSetAngles("GearC4_D0", 0.0F, 0.0F, -f30);
            } else {
                float f1 = Aircraft.cvt(this.FM.getSpeed(), 20F, 50F, 1.0F, 0.0F);
                float f3 = Aircraft.cvt(this.FM.getSpeed(), 0.0F, 20F, 0.0F, 0.5F);
                if (this.FM.Gears.gWheelSinking[0] > 0.0F) {
                    flag = true;
                    this.skiAngleL = 0.5F * this.skiAngleL + 0.5F * this.FM.Or.getTangage();
                    if (this.skiAngleL > 20F) this.skiAngleL = this.skiAngleL - this.spring;
                    this.hierMesh().chunkSetAngles("SkiL0_D0", World.Rnd().nextFloat(-f3 * 2.0F, f3 * 2.0F) - this.skiAngleL, World.Rnd().nextFloat(-f3, f3), World.Rnd().nextFloat(f3, f3));
                } else {
                    if (this.skiAngleL > f1 * -10F + 0.01D) {
                        this.skiAngleL = this.skiAngleL - this.spring;
                        flag = true;
                    } else if (this.skiAngleL < f1 * -10F - 0.01D) {
                        this.skiAngleL = this.skiAngleL + this.spring;
                        flag = true;
                    }
                    this.hierMesh().chunkSetAngles("SkiL0_D0", -this.skiAngleL, 0.0F, 0.0F);
                }
                if (this.FM.Gears.gWheelSinking[1] > 0.0F) {
                    flag = true;
                    this.skiAngleR = 0.5F * this.skiAngleR + 0.5F * this.FM.Or.getTangage();
                    if (this.skiAngleR > 20F) this.skiAngleR = this.skiAngleR - this.spring;
                    this.hierMesh().chunkSetAngles("SkiR0_D0", World.Rnd().nextFloat(-f3 * 2.0F, f3 * 2.0F) - this.skiAngleR, World.Rnd().nextFloat(-f3, f3), World.Rnd().nextFloat(f3, f3));
                    if (this.FM.Gears.gWheelSinking[0] == 0.0F && this.FM.Or.getRoll() < 365F && this.FM.Or.getRoll() > 355F) {
                        this.skiAngleL = this.skiAngleR;
                        this.hierMesh().chunkSetAngles("SkiL0_D0", World.Rnd().nextFloat(-f3 * 2.0F, f3 * 2.0F) - this.skiAngleL, World.Rnd().nextFloat(-f3, f3), World.Rnd().nextFloat(f3, f3));
                    }
                } else {
                    if (this.skiAngleR > f1 * -10F + 0.01D) {
                        this.skiAngleR = this.skiAngleR - this.spring;
                        flag = true;
                    } else if (this.skiAngleR < f1 * -10F - 0.01D) {
                        this.skiAngleR = this.skiAngleR + this.spring;
                        flag = true;
                    }
                    this.hierMesh().chunkSetAngles("SkiR0_D0", -this.skiAngleR, 0.0F, 0.0F);
                }
                if (!flag && f1 == 0.0F) return;
                this.hierMesh().chunkSetAngles("SkiC0_D0", -(this.skiAngleL + this.skiAngleR) / 2.0F, 0.0F, 0.0F);
                float f12 = this.skiAngleL / 20F;
                float f14 = this.skiAngleL / 60F;
                float f16 = this.skiAngleL / 80F;
                this.hierMesh().chunkSetAngles("SkiL2_D0", 0.0F, 0.0F, -f12 * 5.7F);
                this.resetYPRmodifier();
                Aircraft.xyz[2] = -f14;
                this.hierMesh().chunkSetLocate("SkiL1_D0", Aircraft.xyz, Aircraft.ypr);
                Aircraft.xyz[2] = f16;
                this.hierMesh().chunkSetLocate("SkiL3_D0", Aircraft.xyz, Aircraft.ypr);
                this.hierMesh().chunkSetLocate("SkiL4_D0", Aircraft.xyz, Aircraft.ypr);
                float f18 = this.skiAngleR / 20F;
                float f20 = this.skiAngleR / 60F;
                float f22 = this.skiAngleR / 80F;
                this.hierMesh().chunkSetAngles("SkiR2_D0", 0.0F, 0.0F, -f18 * 5.7F);
                this.resetYPRmodifier();
                Aircraft.xyz[2] = -f20;
                this.hierMesh().chunkSetLocate("SkiR1_D0", Aircraft.xyz, Aircraft.ypr);
                Aircraft.xyz[2] = f22;
                this.hierMesh().chunkSetLocate("SkiR3_D0", Aircraft.xyz, Aircraft.ypr);
                this.hierMesh().chunkSetLocate("SkiR4_D0", Aircraft.xyz, Aircraft.ypr);
                this.hierMesh().chunkSetAngles("SkiC1_D0", 0.0F, 0.0F, -((this.skiAngleL + this.skiAngleR) / 2.0F / 20F) * 5.7F);
                this.resetYPRmodifier();
                Aircraft.xyz[2] = -((this.skiAngleL + this.skiAngleR) / 2.0F / 80F);
                this.hierMesh().chunkSetLocate("SkiC3_D0", Aircraft.xyz, Aircraft.ypr);
            }
        }
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            default:
                break;

            case 19:
                this.FM.Gears.hitCentreGear();
                break;

            case 33:
                if (this.UseSki) {
                    this.hierMesh().chunkVisible("SkiL1_D0", false);
                    this.hierMesh().chunkVisible("SkiL2_D0", false);
                    this.hierMesh().chunkVisible("SkiL3_D0", false);
                    this.hierMesh().chunkVisible("SkiL4_D0", false);
                    this.hierMesh().chunkVisible("SkiL5_D0", false);
                    this.hierMesh().chunkVisible("SkiL6_D0", false);
                    this.hierMesh().chunkVisible("SkiL7_D0", false);
                    this.hierMesh().chunkVisible("SkiL8_D0", false);
                }
                break;

            case 36:
                if (this.UseSki) {
                    this.hierMesh().chunkVisible("SkiR1_D0", false);
                    this.hierMesh().chunkVisible("SkiR2_D0", false);
                    this.hierMesh().chunkVisible("SkiR3_D0", false);
                    this.hierMesh().chunkVisible("SkiR4_D0", false);
                    this.hierMesh().chunkVisible("SkiR5_D0", false);
                    this.hierMesh().chunkVisible("SkiR6_D0", false);
                    this.hierMesh().chunkVisible("SkiR7_D0", false);
                    this.hierMesh().chunkVisible("SkiR8_D0", false);
                }
                break;

            case 7:
                this.hierMesh().chunkVisible("GearC4_D0", false);
                if (this.UseSki) {
                    this.hierMesh().chunkVisible("SkiC1_D0", false);
                    this.hierMesh().chunkVisible("SkiC2_D0", false);
                    this.hierMesh().chunkVisible("SkiC3_D0", false);
                }
                break;

            case 9:
                if (this.hierMesh().chunkFindCheck("GearL2_D0") == -1) break;
                this.hierMesh().hideSubTrees("GearL2_D0");
                Wreckage wreckage = new Wreckage(this, this.hierMesh().chunkFind("GearL2_D0"));
                wreckage.collide(true);
                this.hierMesh().chunkVisible("GearL5_D0", false);
                if (this.UseSki) {
                    this.hierMesh().chunkVisible("SkiL1_D0", false);
                    this.hierMesh().chunkVisible("SkiL2_D0", false);
                    this.hierMesh().chunkVisible("SkiL3_D0", false);
                    this.hierMesh().chunkVisible("SkiL4_D0", false);
                    this.hierMesh().chunkVisible("SkiL5_D0", false);
                    this.hierMesh().chunkVisible("SkiL6_D0", false);
                    this.hierMesh().chunkVisible("SkiL7_D0", false);
                }
                this.FM.Gears.hitLeftGear();
                break;

            case 10:
                if (this.hierMesh().chunkFindCheck("GearR2_D0") == -1) break;
                this.hierMesh().hideSubTrees("GearR2_D0");
                Wreckage wreckage1 = new Wreckage(this, this.hierMesh().chunkFind("GearR2_D0"));
                wreckage1.collide(true);
                this.hierMesh().chunkVisible("GearR5_D0", false);
                if (this.UseSki) {
                    this.hierMesh().chunkVisible("SkiR1_D0", false);
                    this.hierMesh().chunkVisible("SkiR2_D0", false);
                    this.hierMesh().chunkVisible("SkiR3_D0", false);
                    this.hierMesh().chunkVisible("SkiR4_D0", false);
                    this.hierMesh().chunkVisible("SkiR5_D0", false);
                    this.hierMesh().chunkVisible("SkiR6_D0", false);
                    this.hierMesh().chunkVisible("SkiR7_D0", false);
                }
                this.FM.Gears.hitRightGear();
                break;
        }
        return super.cutFM(i, j, actor);
    }

    private float              skiAngleL;
    private float              skiAngleR;
    private float              spring;
    private boolean            UseSki;
    private Point3d            tmpp;
    private static Point3d     Pn            = new Point3d();
    private boolean            isGround;
    private boolean            isWater;
    private static final float GearCScale[]  = { 0.0F, 0.97109F, 1.9775F, 3.02345F, 4.1136F, 5.2537F, 6.450173F };
    private static final float GearXScale[]  = { -0.09576F, 0.0F, 0.108246F, 0.231629F };
    private static final float GearYScale[]  = { -2.955F, 0.0F, 2.98F, 5.989001F };
    private static final float GearXXScale[] = { -0.10052F, 0.0F, 0.103608F, 0.213349F };
    private static final float GearYYScale[] = { -2.022726F, 0.0F, 2.002502F, 3.976208F };

    static {
        Class class1 = MBR_2AM34gear.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "MBR-2");
        Property.set(class1, "meshName", "3DO/Plane/MBR-2-AM-34-GEAR(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar02());
        Property.set(class1, "meshName_ru", "3DO/Plane/MBR-2-AM-34-GEAR/hier.him");
        Property.set(class1, "PaintScheme_ru", new PaintSchemeFCSPar01());
        Property.set(class1, "originCountry", PaintScheme.countryRussia);
        Property.set(class1, "yearService", 1937F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/MBR-2-AM-34gear.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitMBR_2AM34.class, CockpitMBR_2AM34_Bombardier.class, CockpitMBR_2AM34_FGunner.class, CockpitMBR_2AM34_TGunner.class });
        Aircraft.weaponTriggersRegister(class1, new int[] { 10, 11, 3, 3, 3, 3, 3, 3, 3, 3, 2, 2, 2, 2 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06", "_ExternalBomb07", "_ExternalBomb08", "_ExternalRock01",
                "_ExternalRock02", "_ExternalRock03", "_ExternalRock04" });
    }
}
