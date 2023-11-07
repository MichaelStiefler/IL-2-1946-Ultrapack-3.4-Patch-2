package com.maddox.il2.objects.air;

import java.io.IOException;
import java.security.SecureRandom;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.RangeRandom;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.objects.Wreckage;
import com.maddox.il2.objects.electronics.RadarLiSN2;
import com.maddox.il2.objects.weapons.RocketGun;
import com.maddox.il2.objects.weapons.RocketGunR4M;
import com.maddox.il2.objects.weapons.RocketGunX4;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class ME_P1101 extends HE_162 implements TypeSupersonic, TypeFighter, TypeBNZFighter, TypeX4Carrier, TypeFighterAceMaker, TypeRadarLiSN2Carrier {

    public ME_P1101() {
        // Seed Pseudo-Random Generator with really random hash.
        SecureRandom secRandom = new SecureRandom();
        secRandom.setSeed(System.currentTimeMillis());
        RangeRandom rr = new RangeRandom(secRandom.nextLong());
        for (int i = 0; i < this.rndgear.length; i++) {
            this.rndgear[i] = rr.nextFloat(0.0F, 0.15F);
        }
        this.transsonicEffects = new TransonicEffects(this, 0.0F, 9000F, 0.8F, 1.0F, 0.01F, 1.0F, 0.2F, 1.0F, 0.45F, 0.58F, 0.0F, 0.9F, 1.0F, 1.25F);
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        ME_P1101.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
        this.transsonicEffects.onAircraftLoaded();
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {

        hierMesh.chunkVisible("Radar1", thisWeaponsName.endsWith("NF"));
        hierMesh.chunkVisible("Radar2", thisWeaponsName.endsWith("NF"));
        hierMesh.chunkVisible("R4ML", thisWeaponsName.indexOf("24r4m") >= 0);
        hierMesh.chunkVisible("R4MR", thisWeaponsName.indexOf("24r4m") >= 0);
        hierMesh.chunkVisible("Pilon1_D0", thisWeaponsName.indexOf("4x4") >= 0);
        hierMesh.chunkVisible("Pilon2_D0", thisWeaponsName.indexOf("4x4") >= 0);
        hierMesh.chunkVisible("Pilon3_D0", thisWeaponsName.indexOf("4x4") >= 0);
        hierMesh.chunkVisible("Pilon4_D0", thisWeaponsName.indexOf("4x4") >= 0);
    }

    protected void moveSlats(float aoa) {
        this.resetYPRmodifier();
        Aircraft.xyz[0] = Aircraft.cvt(aoa, 6.8F, 15F, 0.0F, 0.12F);
        Aircraft.xyz[1] = Aircraft.cvt(aoa, 6.8F, 15F, 0.0F, -0.10F);
        Aircraft.xyz[2] = Aircraft.cvt(aoa, 6.8F, 15F, 0.0F, -0.05F);

        Aircraft.ypr[0] = Aircraft.cvt(aoa, 6.8F, 15F, 0.0F, 1.0F);
        Aircraft.ypr[1] = Aircraft.cvt(aoa, 6.8F, 15F, 0.0F, -25F);
        Aircraft.ypr[2] = Aircraft.cvt(aoa, 6.8F, 15F, 0.0F, -0.5F);

        this.hierMesh().chunkSetLocate("Slat_L", Aircraft.xyz, Aircraft.ypr);

        Aircraft.xyz[1] = Aircraft.cvt(aoa, 6.8F, 15F, 0.0F, 0.10F);
        Aircraft.ypr[0] = Aircraft.cvt(aoa, 6.8F, 15F, 0.0F, -1.0F);
        Aircraft.ypr[2] = Aircraft.cvt(aoa, 6.8F, 15F, 0.0F, 0.5F);
        this.hierMesh().chunkSetLocate("Slat_R", Aircraft.xyz, Aircraft.ypr);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if ((!(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) && flag && (this.FM instanceof Pilot)) {
            Pilot pilot = (Pilot) this.FM;
            if ((pilot.get_maneuver() == 63) && (pilot.target != null)) {
                Point3d point3d = new Point3d(pilot.target.Loc);
                point3d.sub(this.FM.Loc);
                this.FM.Or.transformInv(point3d);
                if ((((point3d.x > 4000.0D) && (point3d.x < 5500.0D)) || ((point3d.x > 100.0D) && (point3d.x < 5000.0D) && (World.Rnd().nextFloat() < 0.33F))) && (Time.current() > (this.tX4Prev + 10000L))) {
                    this.bToFire = true;
                    this.tX4Prev = Time.current();
                }
            }
        }
    }

    // New Gear Animation Code,
    // historically accurate in terms of moving main gear before tail wheel
    // In order to do so, new Parameter "bDown" has been introduced to distinguish between
    // gear up and gear down
    public static void moveGear(HierMesh hiermesh, float leftGearPos, float rightGearPos, float noseWheelPos, boolean bDown, float[] rnd) {
        hiermesh.chunkSetAngles("GearC3_D0", 0.0F, ME_P1101.smoothCvt(noseWheelPos, rnd[2] + 0.01F, rnd[2] + 0.1F, 0F, 80F) - ME_P1101.smoothCvt(noseWheelPos, rnd[2] + 0.7F, rnd[2] + 0.8F, 0F, 80F), 0.0F);
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, ME_P1101.smoothCvt(leftGearPos, rnd[0] + 0.01F, rnd[0] + 0.1F, 0.0F, 60F) - ME_P1101.smoothCvt(leftGearPos, rnd[0] + 0.7F, rnd[0] + 0.8F, 0.0F, 60F), 0.0F);
        hiermesh.chunkSetAngles("GearL3b_D0", 0.0F, ME_P1101.smoothCvt(leftGearPos, rnd[0] + 0.01F, rnd[0] + 0.1F, 0.0F, 50F) - ME_P1101.smoothCvt(leftGearPos, rnd[0] + 0.7F, rnd[0] + 0.8F, 0.0F, 20F), 0.0F);
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, ME_P1101.smoothCvt(rightGearPos, rnd[1] + 0.01F, rnd[1] + 0.1F, 0.0F, -60F) + ME_P1101.smoothCvt(leftGearPos, rnd[1] + 0.7F, rnd[1] + 0.8F, 0.0F, 60F), 0.0F);
        hiermesh.chunkSetAngles("GearR3b_D0", 0.0F, ME_P1101.smoothCvt(rightGearPos, rnd[1] + 0.01F, rnd[1] + 0.1F, 0.0F, -50F) + ME_P1101.smoothCvt(rightGearPos, rnd[1] + 0.7F, rnd[1] + 0.8F, 0.0F, 20F), 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, ME_P1101.smoothCvt(leftGearPos, rnd[0] + 0.1F, rnd[0] + 0.84F, 0.0F, 100F), ME_P1101.smoothCvt(leftGearPos, rnd[0] + 0.13F, rnd[0] + 0.44F, 0.0F, 20.0F));
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, ME_P1101.smoothCvt(rightGearPos, rnd[1] + 0.1F, rnd[1] + 0.84F, 0.0F, 100F), ME_P1101.smoothCvt(leftGearPos, rnd[1] + 0.13F, rnd[1] + 0.44F, 0.0F, 20.0F));
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, ME_P1101.smoothCvt(leftGearPos, rnd[0] + 0.01F, rnd[0] + 0.84F, 0F, 110F), 0.0F);
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, ME_P1101.smoothCvt(leftGearPos, rnd[1] + 0.01F, rnd[1] + 0.84F, 0F, -110F), 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, ME_P1101.smoothCvt(noseWheelPos, rnd[2] + 0.01F, rnd[2] + 0.84F, 0F, 100F), 0.0F);
        hiermesh.chunkSetAngles("GearC27_D0", 0.0F, 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearC28_D0", 0.0F, 0.0F, 0.0F);
        Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0.0F;
        Aircraft.xyz[2] = ME_P1101.smoothCvt(noseWheelPos, rnd[2] + 0.01F, rnd[2] + 0.84F, -0.0833F, 0.0F);
        hiermesh.chunkSetLocate("GearC64_D0", Aircraft.xyz, Aircraft.ypr);
        Aircraft.xyz[2] = 0;
        Aircraft.xyz[1] = ME_P1101.smoothCvt(leftGearPos, rnd[0] + 0.2F, rnd[0] + 0.8F, 0.0F, -0.36F);
        Aircraft.ypr[1] = ME_P1101.smoothCvt(leftGearPos, rnd[0] + 0.01F, rnd[0] + 0.1F, 0.0F, -45F) + ME_P1101.smoothCvt(leftGearPos, rnd[0] + 0.3F, rnd[0] + 0.7F, 0.0F, 40F);
        hiermesh.chunkSetLocate("GearL25_D0", Aircraft.xyz, Aircraft.ypr);
        Aircraft.xyz[1] = ME_P1101.smoothCvt(rightGearPos, rnd[1] + 0.2F, rnd[1] + 0.8F, 0.0F, -0.36F);
        Aircraft.ypr[1] = ME_P1101.smoothCvt(rightGearPos, rnd[1] + 0.01F, rnd[1] + 0.1F, 0.0F, 45F) - ME_P1101.smoothCvt(rightGearPos, rnd[1] + 0.3F, rnd[1] + 0.7F, 0.0F, 40F);
        hiermesh.chunkSetLocate("GearR25_D0", Aircraft.xyz, Aircraft.ypr);
        Aircraft.xyz[1] = ME_P1101.smoothCvt(rightGearPos, rnd[2] + 0.4F, rnd[2] + 0.8F, -0.08F, 0.0F);
        Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0.0F;
        hiermesh.chunkSetLocate("GearC25_D0", Aircraft.xyz, Aircraft.ypr);
    }

    public static void moveGear(HierMesh hiermesh, float leftGearPos, float rightGearPos, float noseWheelPos) {
        ME_P1101.moveGear(hiermesh, leftGearPos, rightGearPos, noseWheelPos, true, ME_P1101.rndgearnull);
    }

    protected void moveGear(float leftGearPos, float rightGearPos, float noseWheelPos) {
        ME_P1101.moveGear(this.hierMesh(), leftGearPos, rightGearPos, noseWheelPos, this.FM.CT.GearControl > 0.5F, this.rndgear);
    }

    // ************************************************************************************************
    // Gear code for backward compatibility, older base game versions don't indepently move their gears
    public static void moveGear(HierMesh hiermesh, float gearPos, boolean bDown) {
        ME_P1101.moveGear(hiermesh, gearPos, gearPos, gearPos, bDown, ME_P1101.rndgearnull); // re-route old style function calls to new code
    }

    public static void moveGear(HierMesh hiermesh, float gearPos) {
        ME_P1101.moveGear(hiermesh, gearPos, gearPos, gearPos, true, ME_P1101.rndgearnull); // re-route old style function calls to new code
    }

    protected void moveGear(float gearPos) {
        ME_P1101.moveGear(this.hierMesh(), gearPos, gearPos, gearPos, this.FM.CT.GearControl > 0.5F, this.rndgear);
    }
    // ************************************************************************************************

    public void moveWheelSink() {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.0632F, 0.0F, 0.0632F);
        if (this.FM.CT.getGearC() > 0.99F) {
            Aircraft.ypr[1] = 40F * this.FM.CT.getRudder();
        }
        this.hierMesh().chunkSetLocate("GearC25_D0", Aircraft.xyz, Aircraft.ypr);
        this.hierMesh().chunkSetAngles("GearC27_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.0632F, 0.0F, -15F), 0.0F);
        this.hierMesh().chunkSetAngles("GearC28_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.0632F, 0.0F, 30F), 0.0F);
        this.resetYPRmodifier();
        if (this.FM.CT.getGearL() > 0.99F) {
            Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.36F, -0.36F, 0F);
            this.hierMesh().chunkSetLocate("GearL25_D0", Aircraft.xyz, Aircraft.ypr);
        }
        if (this.FM.CT.getGearR() > 0.99F) {
            Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.36F, -0.36F, 0F);
            this.hierMesh().chunkSetLocate("GearR25_D0", Aircraft.xyz, Aircraft.ypr);
        }
    }

    public void update(float f) {
        if (Config.isUSE_RENDER()) {
            if (this.FM.AS.isMaster()) {
                if (this.FM instanceof RealFlightModel) {
                    this.transsonicEffects.update();
                }
            }
        }
        this.soundbarier();

        super.update(f);
        this.moveSlats(this.FM.getSpeed() > 50F ? this.FM.getAOA() : Aircraft.cvt(this.FM.getSpeed(), 1F, 49F, 15F, 6.8F));
        if (thisWeaponsName.endsWith("NF")) this.FM.Sq.dragParasiteCx += 0.004F;
    }

    protected void moveRudder(float f) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -50F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Rudder2_D0", 0.0F, -50F * f, 0.0F);
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.0632F, 0.0F, 0.0632F);
        if (this.FM.CT.getGear() > 0.99F) {
            Aircraft.ypr[1] = 40F * this.FM.CT.getRudder();
            this.hierMesh().chunkSetLocate("GearC25_D0", Aircraft.xyz, Aircraft.ypr);
        }
        this.hierMesh().chunkSetAngles("GearC27_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.0632F, 0.0F, -15F), 0.0F);
        this.hierMesh().chunkSetAngles("GearC28_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.0632F, 0.0F, 30F), 0.0F);
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 33:
                return super.cutFM(34, j, actor);

            case 36:
                return super.cutFM(37, j, actor);

            case 17:
                return super.cutFM(11, j, actor);

            case 18:
                return super.cutFM(12, j, actor);
        }
        return super.cutFM(i, j, actor);
    }

    public void typeX4CAdjSidePlus() {
        this.deltaAzimuth = 1.0F;
        this.radarMode++;
        if (this.radarMode > RadarLiSN2.RADAR_MODE_SHORT) {
            this.radarMode = RadarLiSN2.RADAR_MODE_NORMAL;
        }
    }

    public void typeX4CAdjSideMinus() {
        this.deltaAzimuth = -1.0F;
        this.radarMode--;
        if (this.radarMode < RadarLiSN2.RADAR_MODE_NORMAL) {
            this.radarMode = RadarLiSN2.RADAR_MODE_SHORT;
        }
    }

    public void typeX4CAdjAttitudePlus() {
        this.deltaTangage = 1.0F;
        this.radarGain += 10;
        if (this.radarGain > 100) {
            this.radarGain = 100;
        }
    }

    public void typeX4CAdjAttitudeMinus() {
        this.deltaTangage = -1.0F;
        this.radarGain -= 10;
        if (this.radarGain < 0) {
            this.radarGain = 0;
        }
    }

    public void typeX4CResetControls() {
        this.deltaAzimuth = this.deltaTangage = 0.0F;
        this.radarGain = 50;
    }

    public float typeX4CgetdeltaAzimuth() {
        return this.deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage() {
        return this.deltaTangage;
    }

    public boolean typeFighterAceMakerToggleAutomation() {
        ++this.k14Mode;
        if (this.k14Mode > 2) {
            this.k14Mode = 0;
        }

        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerMode" + this.k14Mode);
        return true;
    }

    public void typeFighterAceMakerAdjDistanceReset() {
    }

    // -------------------------------------------------------------------------------------------------------
    // TODO: skylla: gyro-gunsight distance HUD log (for details please see
    // P_51D25NA.class):

    public void typeFighterAceMakerAdjDistancePlus() {
        this.adjustK14AceMakerDistance(+10.0f);
    }

    public void typeFighterAceMakerAdjDistanceMinus() {
        this.adjustK14AceMakerDistance(-10.0f);
    }

    private void adjustK14AceMakerDistance(float f) {
        this.k14Distance += f;
        if (this.k14Distance > 1000.0f) {
            this.k14Distance = 1000.0f;
        } else if (this.k14Distance < 160.0f) {
            this.k14Distance = 160.0f;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "Sight Distance: " + (int) this.k14Distance + "m");
    }

    // Allied plane wingspans (approximately):
    public void typeFighterAceMakerAdjSideslipPlus() {
        try {
            this.adjustAceMakerSideSlip(1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void typeFighterAceMakerAdjSideslipMinus() {
        try {
            this.adjustAceMakerSideSlip(-1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void adjustAceMakerSideSlip(int i) throws IOException {
        if (!((i == 1) || (i == -1))) {
            throw new IOException("Wrong input value! Only +1 and -1 allowed!");
        }
        this.k14WingspanType += i;
        String s = "Wingspan Selected: ";
        String s1 = "Yak-3/Yak-9/La-5/P-39/MiG-3";
        String s2 = "B-24";

        switch (this.k14WingspanType) {
            // case 0: s += s1; break; //like Bf-109
            case 1:
                s += "P-51/P-47/P-80/Spitfire/Typhoon/Hurricane";
                break; // like Fw-190
            case 2:
                s += "P-38";
                break; // like Ju-87
            case 3:
                s += "Mosquito/IL-2/Beaufighter";
                break; // like Me-210
            case 4:
                s += "A-20/Pe-2";
                break; // like Do-217
            case 5:
                s += "20m";
                break; // like Ju-88
            case 6:
                s += "B-25/A-26";
                break; // like Ju-188
            case 7:
                s += "DC-3";
                break; // like Ju-52
            case 8:
                s += "B-17/Halifax/Lancaster";
                break; // like He-177
            case 9:
                s += s2;
                break; // like Fw-200
            case 10:
                this.adjustAceMakerSideSlip(-1);
                s += s2;
                break;
            case -1:
                this.adjustAceMakerSideSlip(1);
                s += s1;
                break;
            default:
                this.k14WingspanType = 0;
                s += s1;
                break;
        }

        HUD.log(AircraftHotKeys.hudLogWeaponId, s);
    }

    public void typeFighterAceMakerAdjSideslipReset() {
    }

    public void typeFighterAceMakerReplicateToNet(NetMsgGuaranted var1) throws IOException {
        var1.writeByte(this.k14Mode);
        var1.writeByte(this.k14WingspanType);
        var1.writeFloat(this.k14Distance);
    }

    public void typeFighterAceMakerReplicateFromNet(NetMsgInput var1) throws IOException {
        this.k14Mode = var1.readByte();
        this.k14WingspanType = var1.readByte();
        this.k14Distance = var1.readFloat();
    }

    static float smoothCvt(float inputValue, float inMin, float inMax, float outMin, float outMax) {
        inputValue = Math.min(Math.max(inputValue, inMin), inMax);
        return outMin + ((outMax - outMin) * ((-0.5F * (float) Math.cos(((inputValue - inMin) / (inMax - inMin)) * Math.PI)) + 0.5F));
    }

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

    public boolean dropExternalStores(boolean flag) {
//        boolean retVal = false;
        int dRidx = 0;
        for (int i = 0; i < this.FM.CT.Weapons.length; i++) {
            BulletEmitter[] abe = this.FM.CT.Weapons[i];
            if (abe != null) {
                for (int j = 0; j < abe.length; j++) {
                    BulletEmitter be = abe[j];
                    if (be instanceof RocketGun) {
                        ((RocketGun) be).setHookToRel(true);
                        ((RocketGun) be).shots(0);
                        ((RocketGun) be).hide(true);
                        dRidx++;
                        if (be.countBullets() > 0) {
//                            retVal = true;
                            this.dropRocket(be, dRidx);
                        }
                        ((RocketGun) be).loadBullets(0);
                    }
                }
            }
        }

//        if (!retVal) {
//            return false;
//        }


        if (this.thisWeaponsName.indexOf("24r4m") >= 0) {
            for (int i = 0; i < 2; i++) {
                String chunkName = "R4M" + (i == 0 ? "L" : "R");
                if (!this.hierMesh().isChunkVisible(chunkName)) {
                    continue;
                }
                this.sfxHit(1.0F, new Point3d(0.0D, 0.0D, -1D));
                this.hierMesh().hideSubTrees(chunkName);
                Wreckage wreckage = new Wreckage(this, this.hierMesh().chunkFind(chunkName));
                Vector3d vector3d = new Vector3d();
                vector3d.set(this.FM.Vwld);
                vector3d.x += java.lang.Math.random() + 0.5D;
                vector3d.y += java.lang.Math.random() + 0.5D;
                vector3d.z += java.lang.Math.random() - (java.lang.Math.random() * 10D) - 5D;
                wreckage.setSpeed(vector3d);
                wreckage.collide(true);
            }
        } else if (this.thisWeaponsName.indexOf("4x4") >= 0) {
            for (int i = 1; i < 5; i++) {
                String chunkName = "Pilon" + i + "_D0";
                if (!this.hierMesh().isChunkVisible(chunkName)) {
                    continue;
                }
                this.sfxHit(1.0F, new Point3d(0.0D, 0.0D, -1D));
                this.hierMesh().hideSubTrees(chunkName);
                Wreckage wreckage = new Wreckage(this, this.hierMesh().chunkFind(chunkName));
                Vector3d vector3d = new Vector3d();
                vector3d.set(this.FM.Vwld);
                vector3d.x += java.lang.Math.random() + 0.5D;
                vector3d.y += java.lang.Math.random() + 0.5D;
                vector3d.z += java.lang.Math.random() - (java.lang.Math.random() * 10D) - 5D;
                wreckage.setSpeed(vector3d);
                wreckage.collide(true);
            }
        }

        return true;
    }

    private void dropRocket(BulletEmitter be, int index) {
        String chunkName = ((be instanceof RocketGunR4M) ? "R4M_" : (be instanceof RocketGunX4) ? "X4_" : "") + ((index < 10) ? "0" : "") + index;
        if (chunkName.length() < 3) {
            return;
        }
        Wreckage wreckage = new Wreckage(this, this.hierMesh().chunkFind(chunkName));
        Vector3d vector3d = new Vector3d();
        vector3d.set(this.FM.Vwld);
        vector3d.x += java.lang.Math.random() + 0.5D;
        vector3d.y += java.lang.Math.random() + 0.5D;
        vector3d.z += java.lang.Math.random() - (java.lang.Math.random() * 10D) - 8D;
        wreckage.setSpeed(vector3d);
        wreckage.collide(true);
    }

    protected void hitBone(String string, Shot shot, Point3d point3d) {
        super.hitBone(string, shot, point3d);
        int ii = this.part(string);
        this.transsonicEffects.reduceSensitivity(ii);
    }

    public float getAirPressure(float theAltitude) {
        return this.transsonicEffects.getAirPressure(theAltitude);
    }

    public float getAirPressureFactor(float theAltitude) {
        return this.transsonicEffects.getAirPressureFactor(theAltitude);
    }

    public float getAirDensity(float theAltitude) {
        return this.transsonicEffects.getAirDensity(theAltitude);
    }

    public float getAirDensityFactor(float theAltitude) {
        return this.transsonicEffects.getAirDensityFactor(theAltitude);
    }

    public float getMachForAlt(float theAltValue) {
        return this.transsonicEffects.getMachForAlt(theAltValue);
    }

    public float calculateMach() {
        return this.FM.getSpeedKMH() / this.getMachForAlt(this.FM.getAltitude());
    }

    public void soundbarier() {
        this.transsonicEffects.soundbarrier();
    }

    private final TransonicEffects transsonicEffects;

    private int                    radarGain       = 50;
    private int                    radarMode       = RadarLiSN2.RADAR_MODE_NORMAL;
    // --- RadarLiSN2Carrier ---

    public boolean                 bToFire         = false;
    private long                   tX4Prev         = 0L;
    private float                  deltaAzimuth    = 0.0F;
    private float                  deltaTangage    = 0.0F;
    public int                     k14Mode         = 0;
    public int                     k14WingspanType = 0;
    public float                   k14Distance     = 200.0F;
    float[]                        rndgear         = { 0.0F, 0.0F, 0.0F };
    static float[]                 rndgearnull     = { 0.0F, 0.0F, 0.0F }; // Used for Plane Land Pose calculation when Aircraft.setFM calls static gear methods

    static {
        Class class1 = ME_P1101.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
        Property.set(class1, "iconFar_shortClassName", "Me-P1101");
        Property.set(class1, "meshName", "3DO/Plane/Me-P1101/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1944.2F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/Me-P1101.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitME_P1101.class });
        Property.set(class1, "LOSElevation", 0.5099F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalRock07", "_ExternalRock08", "_ExternalRock09", "_ExternalRock10", "_ExternalRock11", "_ExternalRock12", "_ExternalRock13", "_ExternalRock14", "_ExternalRock15", "_ExternalRock16", "_ExternalRock17", "_ExternalRock18", "_ExternalRock19", "_ExternalRock20", "_ExternalRock21", "_ExternalRock22", "_ExternalRock23", "_ExternalRock24", "_ExternalRock25", "_ExternalRock25", "_ExternalRock26", "_ExternalRock26", "_ExternalRock27", "_ExternalRock27", "_ExternalRock28", "_ExternalRock28" });
    }
}
