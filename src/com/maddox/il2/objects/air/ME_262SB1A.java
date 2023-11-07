package com.maddox.il2.objects.air;

import java.security.SecureRandom;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Tuple3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.RangeRandom;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.objects.electronics.RadarLiSN2;
import com.maddox.il2.objects.weapons.Bomb;
import com.maddox.il2.objects.weapons.BombStarthilfe109500;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class ME_262SB1A extends ME_262 implements TypeStormovik, TypeX4Carrier, TypeRadarLiSN2Carrier {

    public ME_262SB1A() {
        this.booster = null;
        this.bHasBoosters = false;
        this.boosterFireOutTime = -1L;
        // Seed Pseudo-Random Generator with really random hash.
        SecureRandom secRandom = new SecureRandom();
        secRandom.setSeed(System.currentTimeMillis());
        RangeRandom rr = new RangeRandom(secRandom.nextLong());
        for (int i = 0; i < this.rndgear.length; i++) {
            this.rndgear[i] = rr.nextFloat(0.0F, 0.15F);
        }
    }

    public void destroy() {
        this.doCutBoosters();
        super.destroy();
    }

    public void doFireBoosters() {
        Eff3DActor.New(this, this.findHook("_Booster1"), null, 1.0F, "3DO/Effects/Tracers/HydrogenRocket/rocket.eff", 30F);
    }

    public void doCutBoosters() {
        if (this.booster != null) {
            this.booster.start();
            this.booster = null;
        }
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        if (thisWeaponsName.equalsIgnoreCase("default")
                || thisWeaponsName.equalsIgnoreCase("pb1")
                || thisWeaponsName.equalsIgnoreCase("pb2")
                || thisWeaponsName.equalsIgnoreCase("24r4m")
                || thisWeaponsName.equalsIgnoreCase("none")) {
            hierMesh.chunkVisible("PylonL_D0", false);
            hierMesh.chunkVisible("PylonR_D0", false);
        } else if (thisWeaponsName.toLowerCase().startsWith("1x")) {
            hierMesh.chunkVisible("PylonL_D0", true);
            hierMesh.chunkVisible("PylonR_D0", false);
        } else {
            hierMesh.chunkVisible("PylonL_D0", true);
            hierMesh.chunkVisible("PylonR_D0", true);
        }

        if ((thisWeaponsName.toLowerCase().indexOf("wfrgr") < 0) && (thisWeaponsName.toLowerCase().indexOf("r4m") < 0)) {
            hierMesh.hideSubTrees("Antenna_L_D0");
            hierMesh.hideSubTrees("Antenna_R_D0");
        } else {
            String[] chunks = { "Antenna_L_D0", "Antenna_Aa_D0", "Antenna_Aaa_D0", "Antenna_Aab_D0", "Antenna_Ab_D0", "Antenna_Aba_D0", "Antenna_Abb_D0",
                    "Antenna_R_D0", "Antenna_Ac_D0", "Antenna_Aca_D0", "Antenna_Acb_D0", "Antenna_Ad_D0", "Antenna_Ada_D0", "Antenna_Adb_D0"};
            for (int chunkIndex=0; chunkIndex<chunks.length; chunkIndex++) {
                hierMesh.chunkVisible(chunks[chunkIndex], true);
            }
        }
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
                this.doCutBoosters();
                this.FM.AS.setGliderBoostOff();
                this.bHasBoosters = false;
                break;
        }
        return super.cutFM(i, j, actor);
    }

    public void update(float f) {
        super.update(f);
        if (!(this.FM instanceof Pilot)) {
            return;
        }
        if (this.bHasBoosters) {
            if (((this.FM.getAltitude() - World.land().HQ_Air(this.FM.Loc.x, this.FM.Loc.y)) > 300F) && (this.boosterFireOutTime == -1L) && (((Tuple3d) (this.FM.Loc)).z != 0.0D) && (World.Rnd().nextFloat() < 0.05F)) {
                this.doCutBoosters();
                this.FM.AS.setGliderBoostOff();
                this.bHasBoosters = false;
            }
            if (this.bHasBoosters && (this.boosterFireOutTime == -1L) && this.FM.Gears.onGround() && (this.FM.EI.getPowerOutput() > 0.8F) && (this.FM.EI.engines[0].getStage() == 6) && (this.FM.EI.engines[1].getStage() == 6) && (this.FM.getSpeedKMH() > 20F)) {
                this.boosterFireOutTime = Time.current() + 30000L;
                this.doFireBoosters();
                this.FM.AS.setGliderBoostOn();
            }
            if (this.bHasBoosters && (this.boosterFireOutTime > 0L)) {
                if (Time.current() < this.boosterFireOutTime) {
                    this.FM.producedAF.x += 15000D;
                }
                if (Time.current() > (this.boosterFireOutTime + 10000L)) {
                    this.doCutBoosters();
                    this.FM.AS.setGliderBoostOff();
                    this.bHasBoosters = false;
                }
            }

        }
    }

    public void setOnGround(Point3d point3d, Orient orient, Vector3d vector3d) {
        super.setOnGround(point3d, orient, vector3d);
        try {
            this.booster = new BombStarthilfe109500();
            this.booster.pos.setBase(this, this.findHook("_BoosterH1"), false);
            this.booster.pos.resetAsBase();
            this.booster.drawing(true);
            this.bHasBoosters = true;
        } catch (Exception exception) {
            this.debugprintln("Structure corrupt - can't hang Starthilferakete..");
        }
    }

    // New Gear Animation Code,
    // historically accurate in terms of moving main gear before tail wheel
    // In order to do so, new Parameter "bDown" has been introduced to distinguish between
    // gear up and gear down
    public static void moveGear(HierMesh hiermesh, float leftGearPos, float rightGearPos, float frontWheelPos, boolean bDown, float[] rnd) {
        myResetYPRmodifier();
        xyz[2] = smoothCvt(frontWheelPos, 0.01F + rnd[2], 0.11F + rnd[2], 0.0F, 0.1F);
        ypr[1] = smoothCvt(frontWheelPos, 0.01F + rnd[2], 0.11F + rnd[2], 0.0F, -90F);
// hiermesh.chunkSetAngles("GearC5_D0", 0.0F, smoothCvt(frontWheelPos, 0.01F, 0.11F, 0.0F, -90F), 0.0F);
        hiermesh.chunkSetLocate("GearC5_D0", xyz, ypr);

        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, smoothCvt(frontWheelPos, 0.11F + rnd[2], 0.84F + +rnd[2], 0.0F, 103F), 0.0F);
        hiermesh.chunkSetAngles("GearC21_D0", 0.0F, smoothCvt(frontWheelPos, 0.15F + rnd[2], 0.35F + rnd[2], 0.0F, -90F), 0.0F);
        if (frontWheelPos < 0.2F) {
            hiermesh.chunkSetAngles("GearC6_D0", 0.0F, smoothCvt(frontWheelPos, 0.01F + rnd[2], 0.11F + rnd[2], 0.0F, -90F), 0.0F);
        } else {
            hiermesh.chunkSetAngles("GearC6_D0", 0.0F, smoothCvt(frontWheelPos, 0.51F + rnd[2], 0.65F + rnd[2], -90.0F, 0.0F), 0.0F);
        }

// hiermesh.chunkSetAngles("GearC2_D0", 0.0F, 103F * frontWheelPos, 0.0F);
// hiermesh.chunkSetAngles("GearC21_D0", 0.0F, -90F * frontWheelPos, 0.0F);
// hiermesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(frontWheelPos, 0.01F, 0.11F, 0.0F, -90F), 0.0F);
// hiermesh.chunkSetAngles("GearC6_D0", 0.0F, Aircraft.cvt(frontWheelPos, 0.01F, 0.11F, 0.0F, -90F), 0.0F);

        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, smoothCvt(leftGearPos, 0.15F + rnd[0], 0.74F + rnd[0], 0F, 73F), 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, smoothCvt(rightGearPos, 0.25F + rnd[1], 0.84F + rnd[1], 0F, 73F), 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, smoothCvt(leftGearPos, 0.15F + rnd[0], 0.74F + rnd[0], 0F, 82F), 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, smoothCvt(rightGearPos, 0.25F + rnd[1], 0.84F + rnd[1], 0F, 82F), 0.0F);

        if (leftGearPos < 0.2F) {
            hiermesh.chunkSetAngles("GearL6_D0", 0.0F, smoothCvt(leftGearPos, 0.05F + rnd[0], 0.15F + rnd[0], 0F, -90F), 0.0F);
        } else {
            hiermesh.chunkSetAngles("GearL6_D0", 0.0F, smoothCvt(leftGearPos, 0.45F + rnd[0], 0.55F + rnd[0], -90F, 0F), 0.0F);
        }
        if (rightGearPos < 0.3F) {
            hiermesh.chunkSetAngles("GearR6_D0", 0.0F, smoothCvt(rightGearPos, 0.15F + rnd[1], 0.25F + rnd[1], 0F, -90F), 0.0F);
        } else {
            hiermesh.chunkSetAngles("GearR6_D0", 0.0F, smoothCvt(rightGearPos, 0.55F + rnd[1], 0.65F + rnd[1], -90F, 0F), 0.0F);
        }

// hiermesh.chunkSetAngles("GearL2_D0", 0.0F, 73F * leftGearPos, 0.0F);
// hiermesh.chunkSetAngles("GearR2_D0", 0.0F, 73F * rightGearPos, 0.0F);
// hiermesh.chunkSetAngles("GearL4_D0", 0.0F, 88F * leftGearPos, 0.0F);
// hiermesh.chunkSetAngles("GearR4_D0", 0.0F, 88F * rightGearPos, 0.0F);
// float f1 = Math.max(-leftGearPos * 1500F, -90F);
// float f2 = Math.max(-rightGearPos * 1500F, -90F);
// hiermesh.chunkSetAngles("GearL6_D0", 0.0F, f1, 0.0F);
// hiermesh.chunkSetAngles("GearR6_D0", 0.0F, f2, 0.0F);
    }

    public static void moveGear(HierMesh hiermesh, float leftGearPos, float rightGearPos, float frontWheelPos) {
        moveGear(hiermesh, leftGearPos, rightGearPos, frontWheelPos, true, rndgearnull);
    }

    protected void moveGear(float leftGearPos, float rightGearPos, float frontWheelPos) {
        moveGear(this.hierMesh(), leftGearPos, rightGearPos, frontWheelPos, this.FM.CT.GearControl > 0.5F, this.rndgear);
    }

    // ************************************************************************************************
    // Gear code for backward compatibility, older base game versions don't indepently move their gears
    public static void moveGear(HierMesh hiermesh, float gearPos, boolean bDown) {
        moveGear(hiermesh, gearPos, gearPos, gearPos, bDown, rndgearnull); // re-route old style function calls to new code
    }

    public static void moveGear(HierMesh hiermesh, float gearPos) {
        moveGear(hiermesh, gearPos, gearPos, gearPos, true, rndgearnull); // re-route old style function calls to new code
    }

    protected void moveGear(float gearPos) {
        moveGear(this.hierMesh(), gearPos, gearPos, gearPos, this.FM.CT.GearControl > 0.5F, this.rndgear);
    }
    // ************************************************************************************************

    // Static Helper Method to initialize the static YPR and XYZ modifiers of the Aircraft class.
    // In IL-2 base game only instance method for this purpose exists, but this instance method cannot be called from static methods.
    static void myResetYPRmodifier() {
        Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0.0F;
    }

    static float smoothCvt(float inputValue, float inMin, float inMax, float outMin, float outMax) {
        inputValue = Math.min(Math.max(inputValue, inMin), inMax);
        return outMin + ((outMax - outMin) * ((-0.5F * (float) Math.cos(((inputValue - inMin) / (inMax - inMin)) * Math.PI)) + 0.5F));
    }

//
//
// public static void moveGear(HierMesh hiermesh, float f) {
// hiermesh.chunkSetAngles("GearC2_D0", 0.0F, 103F * f, 0.0F);
// hiermesh.chunkSetAngles("GearC21_D0", 0.0F, -90F * f, 0.0F);
// hiermesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.11F, 0.0F, -90F), 0.0F);
// hiermesh.chunkSetAngles("GearC6_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.11F, 0.0F, -90F), 0.0F);
// hiermesh.chunkSetAngles("GearL2_D0", 0.0F, 73F * f, 0.0F);
// hiermesh.chunkSetAngles("GearR2_D0", 0.0F, 73F * f, 0.0F);
// hiermesh.chunkSetAngles("GearL4_D0", 0.0F, 88F * f, 0.0F);
// hiermesh.chunkSetAngles("GearR4_D0", 0.0F, 88F * f, 0.0F);
// float f1 = Math.max(-f * 1500F, -90F);
// hiermesh.chunkSetAngles("GearL6_D0", 0.0F, f1, 0.0F);
// hiermesh.chunkSetAngles("GearR6_D0", 0.0F, f1, 0.0F);
// }
//
// protected void moveGear(float f) {
// moveGear(this.hierMesh(), f);
// }

    public void moveWheelSink() {
        this.resetYPRmodifier();
        float f = this.FM.Gears.gWheelSinking[2];
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.0F, 0.19F, 0.0F, 0.19F);
        this.hierMesh().chunkSetLocate("GearC22_D0", Aircraft.xyz, Aircraft.ypr);
    }

    protected void moveRudder(float f) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -30F * f, 0.0F);
        if (this.FM.CT.getGear() > 0.75F) {
            this.hierMesh().chunkSetAngles("GearC21_D0", 0.0F, -90F + (40F * f), 0.0F);
        }
    }

    // +++ X4Carrier +++
    public void typeX4CAdjSidePlus() {
        this.radarMode++;
        if (this.radarMode > RadarLiSN2.RADAR_MODE_SHORT) {
            this.radarMode = RadarLiSN2.RADAR_MODE_NORMAL;
        }
    }

    public void typeX4CAdjSideMinus() {
        this.radarMode--;
        if (this.radarMode < RadarLiSN2.RADAR_MODE_NORMAL) {
            this.radarMode = RadarLiSN2.RADAR_MODE_SHORT;
        }
    }

    public void typeX4CAdjAttitudePlus() {
        this.radarGain += 10;
        if (this.radarGain > 100) {
            this.radarGain = 100;
        }
    }

    public void typeX4CAdjAttitudeMinus() {
        this.radarGain -= 10;
        if (this.radarGain < 0) {
            this.radarGain = 0;
        }
    }

    public void typeX4CResetControls() {
        this.radarGain = 50;
    }

    public float typeX4CgetdeltaAzimuth() {
        return this.deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage() {
        return this.deltaTangage;
    }

    private float deltaAzimuth = 0.0F;
    private float deltaTangage = 0.0F;
    // +++ X4Carrier +++

    // +++ RadarLiSN2Carrier +++
    public void setCurPilot(int theCurPilot) {
        System.out.println("### Attempt to set Pilot Index on single crew plane!!! ###");
    }

    public int getCurPilot() {
        return 1;
    }

    public int getRadarGain() {
        return this.radarGain;
    }

    public int getRadarMode() {
        return this.radarMode;
    }

    private int       radarGain   = 50;
    private int       radarMode   = RadarLiSN2.RADAR_MODE_NORMAL;;
    // --- RadarLiSN2Carrier ---

    private Bomb      booster;
    protected boolean bHasBoosters;
    protected long    boosterFireOutTime;

    float[]           rndgear     = { 0.0F, 0.0F, 0.0F };
    static float[]    rndgearnull = { 0.0F, 0.0F, 0.0F };                 // Used for Plane Land Pose calculation when Aircraft.setFM calls static gear methods

    static {
        Class class1 = ME_262SB1A.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Me 262");
        Property.set(class1, "meshName", "3DO/Plane/Me-262SB-1a/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar05());
        Property.set(class1, "yearService", 1944.1F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/Me-262SB1a.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitME_262Sb.class });
        Property.set(class1, "LOSElevation", 0.74615F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 9, 9, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 9, 9, 9, 9 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06", "_ExternalBomb07", "_ExternalBomb08", "_ExternalBomb09", "_ExternalBomb10", "_ExternalBomb11", "_ExternalBomb12", "_ExternalBomb13", "_ExternalBomb14", "_ExternalDev01", "_ExternalDev02", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalRock07", "_ExternalRock08", "_ExternalRock09", "_ExternalRock10", "_ExternalRock11", "_ExternalRock12", "_ExternalRock13", "_ExternalRock14", "_ExternalRock15", "_ExternalRock16", "_ExternalRock17", "_ExternalRock18", "_ExternalRock19", "_ExternalRock20", "_ExternalRock21", "_ExternalRock22", "_ExternalRock23", "_ExternalRock24", "_ExternalRock25", "_ExternalRock26", "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06" });

//
// Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 3, 3, 9, 9, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 9, 9, 9, 9 });
// Aircraft.weaponHooksRegister(class1, new String[] { "_CANNON01", "_CANNON02", "_ExternalBomb01", "_ExternalBomb02", "_ExternalDev01", "_ExternalDev02", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalRock07", "_ExternalRock08", "_ExternalRock09", "_ExternalRock10", "_ExternalRock11", "_ExternalRock12", "_ExternalRock13", "_ExternalRock14", "_ExternalRock15", "_ExternalRock16", "_ExternalRock17", "_ExternalRock18", "_ExternalRock19", "_ExternalRock20", "_ExternalRock21", "_ExternalRock22", "_ExternalRock23", "_ExternalRock24", "_ExternalRock25", "_ExternalRock26", "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06" });
// String as[] = new String[36];
// as[0] = "MGunMK108k 100";
// as[1] = "MGunMK108k 100";
// Aircraft.weaponsRegister(class1, "default", as);
// Aircraft.weaponsRegister(class1, "2xTypeD", new String[] { "MGunMK108k 100", "MGunMK108k 100", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "FuelTankGun_Type_D", "FuelTankGun_Type_D" });
// String as1[] = new String[36];
// as1[0] = "MGunMK108k 100";
// as1[1] = "MGunMK108k 100";
// as1[2] = "BombGunSC250";
// as1[3] = "BombGunSC250";
// Aircraft.weaponsRegister(class1, "2xSC250", as1);
// String as2[] = new String[36];
// as2[0] = "MGunMK108k 100";
// as2[1] = "MGunMK108k 100";
// as2[2] = "BombGunAB250";
// as2[3] = "BombGunAB250";
// Aircraft.weaponsRegister(class1, "2xAB250", as2);
// String as3[] = new String[36];
// as3[0] = "MGunMK108k 100";
// as3[1] = "MGunMK108k 100";
// as3[2] = "BombGunSC500";
// as3[3] = "BombGunSC500";
// Aircraft.weaponsRegister(class1, "2xSC500", as3);
// String as4[] = new String[36];
// as4[0] = "MGunMK108k 100";
// as4[1] = "MGunMK108k 100";
// as4[2] = "BombGunSD500";
// as4[3] = "BombGunSD500";
// Aircraft.weaponsRegister(class1, "2xSD500", as4);
// String as5[] = new String[36];
// as5[0] = "MGunMK108k 100";
// as5[1] = "MGunMK108k 100";
// as5[2] = "BombGunAB500";
// as5[3] = "BombGunAB500";
// Aircraft.weaponsRegister(class1, "2xAB500", as5);
// String as6[] = new String[36];
// as6[0] = "MGunMK108k 100";
// as6[1] = "MGunMK108k 100";
// as6[2] = "BombGunSC1000";
// Aircraft.weaponsRegister(class1, "1xSC1000", as6);
// String as7[] = new String[36];
// as7[0] = "MGunMK108k 100";
// as7[1] = "MGunMK108k 100";
// as7[2] = "BombGunSD1000";
// Aircraft.weaponsRegister(class1, "1xSD1000", as7);
// String as8[] = new String[36];
// as8[0] = "MGunMK108k 100";
// as8[1] = "MGunMK108k 100";
// as8[2] = "BombGunAB1000";
// Aircraft.weaponsRegister(class1, "1xAB1000", as8);
// String as9[] = new String[36];
// as9[0] = "MGunMK108k 100";
// as9[1] = "MGunMK108k 100";
// as9[2] = "BombGunPC1000RS";
// Aircraft.weaponsRegister(class1, "1xPC1000RS", as9);
// String as10[] = new String[36];
// as10[0] = "MGunMK108k 100";
// as10[1] = "MGunMK108k 100";
// as10[30] = "RocketGunWfrGr21 1";
// as10[31] = "RocketGunWfrGr21 1";
// as10[32] = "PylonRO_WfrGr21_262";
// as10[33] = "PylonRO_WfrGr21_262";
// Aircraft.weaponsRegister(class1, "2xWfrGr21", as10);
// String as11[] = new String[36];
// as11[0] = "MGunMK108k 100";
// as11[1] = "MGunMK108k 100";
// as11[4] = "PylonMe262_R4M_Left";
// as11[5] = "PylonMe262_R4M_Right";
// as11[6] = "RocketGunR4M 1";
// as11[7] = "RocketGunR4M 1";
// as11[8] = "RocketGunR4M 1";
// as11[9] = "RocketGunR4M 1";
// as11[10] = "RocketGunR4Ms 1";
// as11[11] = "RocketGunR4Ms 1";
// as11[12] = "RocketGunR4M 1";
// as11[13] = "RocketGunR4M 1";
// as11[14] = "RocketGunR4M 1";
// as11[15] = "RocketGunR4M 1";
// as11[16] = "RocketGunR4Ms 1";
// as11[17] = "RocketGunR4Ms 1";
// as11[18] = "RocketGunR4M 1";
// as11[19] = "RocketGunR4M 1";
// as11[20] = "RocketGunR4M 1";
// as11[21] = "RocketGunR4M 1";
// as11[22] = "RocketGunR4Ms 1";
// as11[23] = "RocketGunR4Ms 1";
// as11[24] = "RocketGunR4M 1";
// as11[25] = "RocketGunR4M 1";
// as11[26] = "RocketGunR4M 1";
// as11[27] = "RocketGunR4M 1";
// as11[28] = "RocketGunR4Ms 1";
// as11[29] = "RocketGunR4Ms 1";
// Aircraft.weaponsRegister(class1, "24r4m", as11);
// Aircraft.weaponsRegister(class1, "24r4m_2xTypeD", new String[] { "MGunMK108k 100", "MGunMK108k 100", null, null, "PylonMe262_R4M_Left", "PylonMe262_R4M_Right", "RocketGunR4M 1", "RocketGunR4M 1", "RocketGunR4M 1", "RocketGunR4M 1", "RocketGunR4Ms 1", "RocketGunR4Ms 1", "RocketGunR4M 1", "RocketGunR4M 1", "RocketGunR4M 1", "RocketGunR4M 1", "RocketGunR4Ms 1", "RocketGunR4Ms 1", "RocketGunR4M 1", "RocketGunR4M 1", "RocketGunR4M 1", "RocketGunR4M 1", "RocketGunR4Ms 1", "RocketGunR4Ms 1", "RocketGunR4M 1", "RocketGunR4M 1", "RocketGunR4M 1", "RocketGunR4M 1", "RocketGunR4Ms 1", "RocketGunR4Ms 1", null, null, null, null, "FuelTankGun_Type_D", "FuelTankGun_Type_D" });
// Aircraft.weaponsRegister(class1, "none", new String[36]);
    }
}
