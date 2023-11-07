package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.Polares;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.Wreckage;
import com.maddox.rts.MsgAction;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.Reflection;

public class Hunter extends Scheme1 implements TypeSupersonic, TypeFighter, TypeBNZFighter, TypeFighterAceMaker, TypeGSuit {
    public Hunter() {
        this.oldctl = -1F;
        this.curctl = -1F;
        this.curthrl = -1F;
        this.oldthrl = -1F;
        this.AirBrakeControl = 0.0F;
        this.k14Mode = 0;
        this.k14WingspanType = 0;
        this.k14Distance = 200F;
        this.APmode1 = false;
        this.APmode2 = false;
        this.overrideBailout = false;
        this.ejectComplete = false;
        this.ejectTime = 0L;
        this.blisterRemoved = false;
        this.transsonicEffects = new TransonicEffects(this, 0.0F, 9000F, 0.8F, 1.0F, 0.01F, 1.0F, 0.2F, 1.0F, 0.45F, 0.58F, 0.0F, 0.9F, 1.0F, 1.25F);
    }

    /**
     * G-Force Resistance, Tolerance and Recovery parmeters. See
     * TypeGSuit.GFactors Private fields implementation for further details.
     */
    private static final float NEG_G_TOLERANCE_FACTOR = 1.5F;
    private static final float NEG_G_TIME_FACTOR      = 1.5F;
    private static final float NEG_G_RECOVERY_FACTOR  = 1.0F;
    private static final float POS_G_TOLERANCE_FACTOR = 2.0F;
    private static final float POS_G_TIME_FACTOR      = 2.0F;
    private static final float POS_G_RECOVERY_FACTOR  = 2.0F;

    public void getGFactors(GFactors theGFactors) {
        theGFactors.setGFactors(NEG_G_TOLERANCE_FACTOR, NEG_G_TIME_FACTOR, NEG_G_RECOVERY_FACTOR, POS_G_TOLERANCE_FACTOR, POS_G_TIME_FACTOR, POS_G_RECOVERY_FACTOR);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        if (hierMesh.chunkFindCheck("PylonInL") >= 0) hierMesh.chunkVisible("PylonInL", thisWeaponsName.indexOf("in_") != -1);
        if (hierMesh.chunkFindCheck("PylonInR") >= 0) hierMesh.chunkVisible("PylonInR", thisWeaponsName.indexOf("in_") != -1);
        if (hierMesh.chunkFindCheck("PylonOutL") >= 0) hierMesh.chunkVisible("PylonOutL", thisWeaponsName.indexOf("out_") != -1);
        if (hierMesh.chunkFindCheck("PylonOutR") >= 0) hierMesh.chunkVisible("PylonOutR", thisWeaponsName.indexOf("out_") != -1);
        if (hierMesh.chunkFindCheck("Tank100InL") >= 0) hierMesh.chunkVisible("Tank100InL", thisWeaponsName.indexOf("in_100gal") != -1);
        if (hierMesh.chunkFindCheck("Tank100InR") >= 0) hierMesh.chunkVisible("Tank100InR", thisWeaponsName.indexOf("in_100gal") != -1);
        if (hierMesh.chunkFindCheck("Tank230L") >= 0) hierMesh.chunkVisible("Tank230L", thisWeaponsName.indexOf("in_230gal") != -1);
        if (hierMesh.chunkFindCheck("Tank230R") >= 0) hierMesh.chunkVisible("Tank230R", thisWeaponsName.indexOf("in_230gal") != -1);
        if (hierMesh.chunkFindCheck("Tank100OutL") >= 0) hierMesh.chunkVisible("Tank100OutL", thisWeaponsName.indexOf("out_100gal") != -1);
        if (hierMesh.chunkFindCheck("Tank100OutR") >= 0) hierMesh.chunkVisible("Tank100OutR", thisWeaponsName.indexOf("out_100gal") != -1);
    }
    
    public void onAircraftLoaded() {
        this.dropTanks[DT_IN_100] = this.thisWeaponsName.indexOf("in_100gal") != -1;
        this.dropTanks[DT_IN_230] = this.thisWeaponsName.indexOf("in_230gal") != -1;
        this.dropTanks[DT_OUT_100] = this.thisWeaponsName.indexOf("out_100gal") != -1;
        super.onAircraftLoaded();
        Hunter.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
        this.FM.AS.wantBeaconsNet(true);
        this.transsonicEffects.onAircraftLoaded();
    }

    public void dropExternalTanks() {
        if (this.dropTanks[DT_IN_230]) {
            this.dropTanks[DT_IN_230] = false;
            this.doRemoveTank("Tank230L");
            this.doRemoveTank("Tank230R");
        }
        if (this.dropTanks[DT_IN_100]) {
            this.dropTanks[DT_IN_100] = false;
            this.doRemoveTank("Tank100InL");
            this.doRemoveTank("Tank100InR");
        }
        if (this.dropTanks[DT_OUT_100]) {
            this.dropTanks[DT_OUT_100] = false;
            this.doRemoveTank("Tank100OutL");
            this.doRemoveTank("Tank100OutR");
        }
    }

    private final void doRemoveTank(String meshName) {
        if (this.hierMesh().chunkFindCheck(meshName) != -1) {
            this.hierMesh().hideSubTrees(meshName);
            Wreckage wreckage = new Wreckage(this, this.hierMesh().chunkFind(meshName));
            wreckage.collide(true);
            Vector3d vector3d = new Vector3d();
            this.getSpeed(vector3d);
            vector3d.z -= 10D;
            vector3d.set(vector3d);
            wreckage.setSpeed(vector3d);
        }
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (super.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask1_D0", false);
        } else {
            this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
        }
        if ((!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel)this.FM).isRealMode()) && (this.FM instanceof Maneuver)) {
            Maneuver maneuver = (Maneuver) this.FM;
            //FIXME!!!!
            if (maneuver.AP.way.isLanding() && (maneuver.getSpeed() > maneuver.VmaxFLAPS * 1.2F) && (maneuver.CT.AirBrakeControl != 1.0F)) {
                if (this == World.getPlayerAircraft()) HUD.log("DivebrakeON"); // Deploy divebrake automatically to assist AI landing!
                maneuver.CT.AirBrakeControl = 1.0F;
            } else if (this.FM.AP.way.isLanding() && (super.FM.getSpeed() < (maneuver.VmaxFLAPS * 0.9F)) && (maneuver.CT.AirBrakeControl > 0.99F)) {
                if (this == World.getPlayerAircraft()) HUD.log("DivebrakeOFF"); // Retract divebrake automatically to assist AI landing!
                maneuver.CT.AirBrakeControl = 0.0F;
            }
        }
    }

    public boolean typeFighterAceMakerToggleAutomation() {
        this.k14Mode++;
        if (this.k14Mode > 2) {
            this.k14Mode = 0;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerMode" + this.k14Mode);
        return true;
    }

    public void typeFighterAceMakerAdjDistanceReset() {
    }

    public void typeFighterAceMakerAdjDistancePlus() {
        this.k14Distance += 10F;
        if (this.k14Distance > 800F) {
            this.k14Distance = 800F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerInc");
    }

    public void typeFighterAceMakerAdjDistanceMinus() {
        this.k14Distance -= 10F;
        if (this.k14Distance < 200F) {
            this.k14Distance = 200F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerDec");
    }

    public void typeFighterAceMakerAdjSideslipReset() {
    }

    public void typeFighterAceMakerAdjSideslipPlus() {
        this.k14WingspanType--;
        if (this.k14WingspanType < 0) {
            this.k14WingspanType = 0;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerWing" + this.k14WingspanType);
    }

    public void typeFighterAceMakerAdjSideslipMinus() {
        this.k14WingspanType++;
        if (this.k14WingspanType > 9) {
            this.k14WingspanType = 9;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerWing" + this.k14WingspanType);
    }

    public void typeFighterAceMakerReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
        netmsgguaranted.writeByte(this.k14Mode);
        netmsgguaranted.writeByte(this.k14WingspanType);
        netmsgguaranted.writeFloat(this.k14Distance);
    }

    public void typeFighterAceMakerReplicateFromNet(NetMsgInput netmsginput) throws IOException {
        this.k14Mode = netmsginput.readByte();
        this.k14WingspanType = netmsginput.readByte();
        this.k14Distance = netmsginput.readFloat();
    }

    public void doMurderPilot(int i) {
        switch (i) {
            case 0: // '\0'
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                break;
        }
    }

    public void moveCockpitDoor(float paramFloat) {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(paramFloat, 0.01F, 0.95F, 0.0F, 0.9F);
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        float f1 = (float) Math.sin(Aircraft.cvt(paramFloat, 0.4F, 0.99F, 0.0F, 3.141593F));
        this.hierMesh().chunkSetAngles("Pilot1_D0", 0.0F, 0.0F, 9.0F * f1);
        this.hierMesh().chunkSetAngles("Head1_D0", 14.0F * f1, 0.0F, 0.0F);
        if (com.maddox.il2.engine.Config.isUSE_RENDER()) {
            if ((Main3D.cur3D().cockpits != null) && (Main3D.cur3D().cockpits[0] != null)) {
                Main3D.cur3D().cockpits[0].onDoorMoved(paramFloat);
            }
            this.setDoorSnd(paramFloat);
        }
    }

    public static void moveGearOld(HierMesh hierMesh, float f) {
        hierMesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.11F, 0.0F, -90F), 0.0F);
        if (Math.abs(f) < 0.27F) {
            hierMesh.chunkSetAngles("GearL6_D0", 0.0F, Aircraft.cvt(f, 0.15F, 0.26F, 0.0F, -90F), 0.0F);
            hierMesh.chunkSetAngles("GearR6_D0", 0.0F, Aircraft.cvt(f, 0.09F, 0.22F, 0.0F, -90F), 0.0F);
        } else {
            hierMesh.chunkSetAngles("GearL6_D0", 0.0F, Aircraft.cvt(f, 0.65F, 0.74F, -90F, 0.0F), 0.0F);
            hierMesh.chunkSetAngles("GearR6_D0", 0.0F, Aircraft.cvt(f, 0.67F, 0.78F, -90F, 0.0F), 0.0F);
        }
        hierMesh.chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(f, 0.23F, 0.65F, 0.0F, -85F), 0.0F);
        hierMesh.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(f, 0.23F, 0.65F, 0.0F, -85F), 0.0F);
        hierMesh.chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(f, 0.28F, 0.7F, 0.0F, -85F), 0.0F);
        hierMesh.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(f, 0.28F, 0.7F, 0.0F, -85F), 0.0F);
        hierMesh.chunkSetAngles("GearC10_D0", 0.0F, Aircraft.cvt(f, 0.69F, 0.74F, 0.0F, -90F), 0.0F);
        hierMesh.chunkSetAngles("GearC2_D0", 0.0F, Aircraft.cvt(f, 0.63F, 0.99F, 0.0F, -105F), 0.0F);
        hierMesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(f, 0.63F, 0.99F, 0.0F, -125F), 0.0F);
    }

    protected void moveGearOld(float paramFloat) {
        moveGear(this.hierMesh(), paramFloat);
    }

    // New Gear Animation Code,
    // historically accurate in terms of moving main gear before tail wheel
    // In order to do so, new Parameter "bDown" has been introduced to distinguish between
    // gear up and gear down
    public static void moveGear(HierMesh hierMesh, float leftGearPos, float rightGearPos, float frontWheelPos, boolean bDown) {
        // Front Gear
        if (Math.abs(leftGearPos) < 0.5F) {
            hierMesh.chunkSetAngles("GearC4_D0", 0.0F, smoothCvt(frontWheelPos, 0.0F, 0.33F, 0.0F, -80F), 0.0F); // Front Door
        } else {
            hierMesh.chunkSetAngles("GearC4_D0", 0.0F, smoothCvt(frontWheelPos, 0.8F, 0.99F, -80.0F, -60F), 0.0F); // Front Door
        }
        hierMesh.chunkSetAngles("GearC10_D0", 0.0F, smoothCvt(frontWheelPos, 0.0F, 0.20F, 0.0F, -90F), 0.0F); // Clamp
        hierMesh.chunkSetAngles("GearC2_D0", 0.0F, smoothCvt(frontWheelPos, 0.20F, 0.99F, 0.0F, -105F), 0.0F); // Gear Strut

        ypr[0] = 0F;
        ypr[1] = smoothCvt(frontWheelPos, 0.20F, 0.99F, 0.0F, -145F);
        ypr[2] = 0F;
        xyz[0] = smoothCvt(frontWheelPos, 0.20F, 0.99F, 0.0F, 0.12F);
        xyz[1] = 0F;
        xyz[2] = smoothCvt(frontWheelPos, 0.20F, 0.99F, 0.0F, 0.12F);
        hierMesh.chunkSetLocate("GearC5_D0", xyz, ypr);

        // Left Gear
        if (Math.abs(leftGearPos) < 0.5F) {
            hierMesh.chunkSetAngles("GearL6_D0", 0.0F, smoothCvt(leftGearPos, 0.15F, 0.26F, 0.0F, -90F), 0.0F);
        } else {
            hierMesh.chunkSetAngles("GearL6_D0", 0.0F, smoothCvt(leftGearPos, 0.65F, 0.74F, -90F, 0.0F), 0.0F);
        }
        hierMesh.chunkSetAngles("GearL2_D0", 0.0F, smoothCvt(leftGearPos, 0.23F, 0.65F, 0.0F, -85F), 0.0F);
        hierMesh.chunkSetAngles("GearL4_D0", 0.0F, smoothCvt(leftGearPos, 0.23F, 0.65F, 0.0F, -85F), 0.0F);

        // Right Gear
        if (Math.abs(rightGearPos) < 0.5F) {
            hierMesh.chunkSetAngles("GearR6_D0", 0.0F, smoothCvt(rightGearPos, 0.09F, 0.22F, 0.0F, -90F), 0.0F);
        } else {
            hierMesh.chunkSetAngles("GearR6_D0", 0.0F, Aircraft.cvt(rightGearPos, 0.67F, 0.78F, -90F, 0.0F), 0.0F);
        }
        hierMesh.chunkSetAngles("GearR2_D0", 0.0F, smoothCvt(rightGearPos, 0.28F, 0.7F, 0.0F, -85F), 0.0F);
        hierMesh.chunkSetAngles("GearR4_D0", 0.0F, smoothCvt(rightGearPos, 0.28F, 0.7F, 0.0F, -85F), 0.0F);
    }

    public static void moveGear(HierMesh hiermesh, float leftGearPos, float rightGearPos, float tailWheelPos) {
        moveGear(hiermesh, leftGearPos, rightGearPos, tailWheelPos, true);
    }

    protected void moveGear(float leftGearPos, float rightGearPos, float tailWheelPos) {
        moveGear(this.hierMesh(), leftGearPos, rightGearPos, tailWheelPos, this.FM.CT.GearControl > 0.5F);
    }

    // ************************************************************************************************
    // Gear code for backward compatibility, older base game versions don't indepently move their gears
    public static void moveGear(HierMesh hiermesh, float gearPos, boolean bDown) {
        moveGear(hiermesh, gearPos, gearPos, gearPos, bDown); // re-route old style function calls to new code
    }

    public static void moveGear(HierMesh hiermesh, float gearPos) {
        moveGear(hiermesh, gearPos, gearPos, gearPos, true); // re-route old style function calls to new code
    }

    protected void moveGear(float gearPos) {
        moveGear(this.hierMesh(), gearPos, this.FM.CT.GearControl > 0.5F);
    }

    private static float smoothCvt(float inputValue, float inMin, float inMax, float outMin, float outMax) {
        inputValue = Math.min(Math.max(inputValue, inMin), inMax);
        return outMin + ((outMax - outMin) * ((-0.5F * (float) Math.cos(((inputValue - inMin) / (inMax - inMin)) * Math.PI)) + 0.5F));
    }
    // ************************************************************************************************

    public void moveWheelSink() {
        if (this.curctl == -1F) {
            this.curctl = this.oldctl = this.FM.CT.getBrake();
            this.H1 = 0.17F;
            this.FM.Gears.tailStiffness = 0.4F;
        } else {
            this.curctl = this.FM.CT.getBrake();
        }
        if (!super.FM.brakeShoe && this.FM.Gears.cgear) {
            if ((this.curctl - this.oldctl) < -0.02F) {
                this.curctl = this.oldctl - 0.02F;
            }
            if (this.curctl < 0.0F) {
                this.curctl = 0.0F;
            }
            float tr = 0.25F * this.curctl * Math.max(Aircraft.cvt(this.FM.EI.engines[0].getThrustOutput(), 0.5F, 0.8F, 0.0F, 1.0F), Aircraft.cvt(super.FM.getSpeedKMH(), 0.0F, 80F, 0.0F, 1.0F));
            super.FM.setGC_Gear_Shift(this.H1 - tr);
            this.resetYPRmodifier();
            Aircraft.xyz[0] = -0.4F * tr;
            this.hierMesh().chunkSetLocate("GearC6_D0", Aircraft.xyz, Aircraft.ypr);
        }
        this.oldctl = this.curctl;
    }

    protected void moveRudder(float paramFloat) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -30F * paramFloat, 0.0F);
        if (this.FM.CT.GearControl > 0.5F) {
            this.hierMesh().chunkSetAngles("GearC7_D0", 0.0F, -50F * paramFloat, 0.0F);
        }
    }

    protected void moveAileron(float f) {
        this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, -30.0F * f, 0.0F);
        this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -30.0F * f, 0.0F);
    }

    protected void moveFlap(float paramFloat) {
        float f = -45F * paramFloat;
        this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, f, 0.0F);
        this.hierMesh().chunkSetAngles("Flap02_D0", 0.0F, f, 0.0F);
    }

    protected void moveElevator(float f) {
        this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -10F * f, 0.0F);
        this.hierMesh().chunkSetAngles("VatorR_D0", 0.0F, -10F * f, 0.0F);
    }

    protected void moveFan(float f1) {
    }

    protected void moveAirBrake(float paramFloat) {
        this.resetYPRmodifier();
        this.hierMesh().chunkSetAngles("Brake01_D0", 0.0F, -45F * paramFloat, 0.0F);
        this.hierMesh().chunkSetAngles("BrakeB01_D0", 0.0F, -25F * paramFloat, 0.0F);
        if (paramFloat < 0.2) {
            Aircraft.xyz[1] = Aircraft.cvt(paramFloat, 0.01F, 0.0F, 0.50F, 0.0F);
        } else {
            Aircraft.xyz[1] = Aircraft.cvt(paramFloat, 0.01F, 0.0F, -0.10F, 0.0F);
        }
        this.hierMesh().chunkSetLocate("BrakeB01e_D0", Aircraft.xyz, Aircraft.ypr);
    }

    protected void hitBone(String string, Shot shot, Point3d point3d) {
        if (string.startsWith("xx")) {
            if (string.startsWith("xxarmor")) {
                this.debuggunnery("Armor: Hit..");
                if (string.endsWith("p1")) {
                    this.getEnergyPastArmor((13.35D / (Math.abs(Aircraft.v1.x) + 0.0001D)), shot);
                    if (shot.power <= 0.0F) {
                        this.doRicochetBack(shot);
                    }
                } else if (string.endsWith("p2")) {
                    this.getEnergyPastArmor(8.77F, shot);
                } else if (string.endsWith("g1")) {
                    this.getEnergyPastArmor((World.Rnd().nextFloat(40.0F, 60.0F) / (Math.abs(Aircraft.v1.x) + 0.0001D)), shot);
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x2);
                    if (shot.power <= 0.0F) {
                        this.doRicochetBack(shot);
                    }
                }
            } else if (string.startsWith("xxcontrols")) {
                this.debuggunnery("Controls: Hit..");
                int i = string.charAt(10) - 48;
                switch (i) {
                    default:
                        break;
                    case 1:
                    case 2:
                        if ((World.Rnd().nextFloat() < 0.5F) && (this.getEnergyPastArmor(1.1F, shot) > 0.0F)) {
                            this.debuggunnery("Controls: Ailerones Controls: Out..");
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                        }
                        break;
                    case 3:
                    case 4:
                        if ((this.getEnergyPastArmor(World.Rnd().nextFloat(0.5F, 2.93F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F)) {
                            this.debuggunnery("Controls: Elevator Controls: Disabled / Strings Broken..");
                            this.FM.AS.setControlsDamage(shot.initiator, 1);
                        }
                        if ((this.getEnergyPastArmor(World.Rnd().nextFloat(0.5F, 2.93F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F)) {
                            this.debuggunnery("Controls: Rudder Controls: Disabled / Strings Broken..");
                            this.FM.AS.setControlsDamage(shot.initiator, 2);
                        }
                }
            } else if (string.startsWith("xxeng1")) {
                this.debuggunnery("Engine Module: Hit..");
                if (string.endsWith("bloc")) {
                    this.getEnergyPastArmor((World.Rnd().nextFloat(0.0F, 60.0F) / (Math.abs(Aircraft.v1.x) + 9.999999747378752E-5)), shot);
                }
                if (string.endsWith("cams") && (this.getEnergyPastArmor(0.45F, shot) > 0.0F) && (World.Rnd().nextFloat() < (this.FM.EI.engines[0].getCylindersRatio() * 20.0F))) {
                    this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 4800.0F)));
                    this.debuggunnery("Engine Module: Engine Cams Hit, " + this.FM.EI.engines[0].getCylindersOperable() + "/" + this.FM.EI.engines[0].getCylinders() + " Left..");
                    if (World.Rnd().nextFloat() < (shot.power / 24000.0F)) {
                        this.FM.AS.hitEngine(shot.initiator, 0, 2);
                        this.debuggunnery("Engine Module: Engine Cams Hit - Engine Fires..");
                    }
                    if ((shot.powerType == 3) && (World.Rnd().nextFloat() < 0.75F)) {
                        this.FM.AS.hitEngine(shot.initiator, 0, 1);
                        this.debuggunnery("Engine Module: Engine Cams Hit (2) - Engine Fires..");
                    }
                }
                if (string.endsWith("eqpt") && (World.Rnd().nextFloat() < (shot.power / 24000.0F))) {
                    this.FM.AS.hitEngine(shot.initiator, 0, 3);
                    this.debuggunnery("Engine Module: Hit - Engine Fires..");
                }
            } else if (string.startsWith("xxmgun0")) {
                int i = string.charAt(7) - 49;
                if (this.getEnergyPastArmor(1.5F, shot) > 0.0F) {
                    this.debuggunnery("Armament: Machine Gun (" + i + ") Disabled..");
                    this.FM.AS.setJamBullets(0, i);
                    this.getEnergyPastArmor(World.Rnd().nextFloat(0.5F, 23.325F), shot);
                }
            } else if (string.startsWith("xxtank")) {
                int i = string.charAt(6) - 49;
                if ((this.getEnergyPastArmor(0.1F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F)) {
                    if (this.FM.AS.astateTankStates[i] == 0) {
                        this.debuggunnery("Fuel Tank (" + i + "): Pierced..");
                        this.FM.AS.hitTank(shot.initiator, i, 1);
                        this.FM.AS.doSetTankState(shot.initiator, i, 1);
                    }
                    if ((shot.powerType == 3) && (World.Rnd().nextFloat() < 0.075F)) {
                        this.FM.AS.hitTank(shot.initiator, i, 2);
                        this.debuggunnery("Fuel Tank (" + i + "): Hit..");
                    }
                }
            } else if (string.startsWith("xxspar")) {
                this.debuggunnery("Spar Construction: Hit..");
                if (string.startsWith("xxsparlm") && (this.chunkDamageVisible("WingLMid") > 2) && (this.getEnergyPastArmor((16.5F * World.Rnd().nextFloat(1.0F, 1.5F)), shot) > 0.0F)) {
                    this.debuggunnery("Spar Construction: WingLMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (string.startsWith("xxsparrm") && (this.chunkDamageVisible("WingRMid") > 2) && (this.getEnergyPastArmor((16.5F * World.Rnd().nextFloat(1.0F, 1.5F)), shot) > 0.0F)) {
                    this.debuggunnery("Spar Construction: WingRMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (string.startsWith("xxsparlo") && (this.chunkDamageVisible("WingLOut") > 2) && (this.getEnergyPastArmor((16.5F * World.Rnd().nextFloat(1.0F, 1.5F)), shot) > 0.0F)) {
                    this.debuggunnery("Spar Construction: WingLOut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if (string.startsWith("xxsparro") && (this.chunkDamageVisible("WingROut") > 2) && (this.getEnergyPastArmor((16.5F * World.Rnd().nextFloat(1.0F, 1.5F)), shot) > 0.0F)) {
                    this.debuggunnery("Spar Construction: WingROut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
            } else if (string.startsWith("xxhyd")) {
                this.FM.AS.setInternalDamage(shot.initiator, 3);
            } else if (string.startsWith("xxpnm")) {
                this.FM.AS.setInternalDamage(shot.initiator, 1);
            }
        } else {
            if (string.startsWith("xcockpit")) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x1);
                this.getEnergyPastArmor(0.05F, shot);
            }
            if (string.startsWith("xcf")) {
                this.hitChunk("CF", shot);
            } else if (string.startsWith("xnose")) {
                this.hitChunk("Nose", shot);
            } else if (string.startsWith("xtail")) {
                if (this.chunkDamageVisible("Tail1") < 3) {
                    this.hitChunk("Tail1", shot);
                }
            } else if (string.startsWith("xkeel")) {
                if (this.chunkDamageVisible("Keel1") < 2) {
                    this.hitChunk("Keel1", shot);
                }
            } else if (string.startsWith("xrudder")) {
                this.hitChunk("Rudder1", shot);
            } else if (string.startsWith("xstab")) {
                if (string.startsWith("xstabl") && (this.chunkDamageVisible("StabL") < 2)) {
                    this.hitChunk("StabL", shot);
                }
                if (string.startsWith("xstabr") && (this.chunkDamageVisible("StabR") < 1)) {
                    this.hitChunk("StabR", shot);
                }
            } else if (string.startsWith("xvator")) {
                if (string.startsWith("xvatorl")) {
                    this.hitChunk("VatorL", shot);
                }
                if (string.startsWith("xvatorr")) {
                    this.hitChunk("VatorR", shot);
                }
            } else if (string.startsWith("xwing")) {
                if (string.startsWith("xwinglin") && (this.chunkDamageVisible("WingLIn") < 3)) {
                    this.hitChunk("WingLIn", shot);
                }
                if (string.startsWith("xwingrin") && (this.chunkDamageVisible("WingRIn") < 3)) {
                    this.hitChunk("WingRIn", shot);
                }
                if (string.startsWith("xwinglmid") && (this.chunkDamageVisible("WingLMid") < 3)) {
                    this.hitChunk("WingLMid", shot);
                }
                if (string.startsWith("xwingrmid") && (this.chunkDamageVisible("WingRMid") < 3)) {
                    this.hitChunk("WingRMid", shot);
                }
                if (string.startsWith("xwinglout") && (this.chunkDamageVisible("WingLOut") < 3)) {
                    this.hitChunk("WingLOut", shot);
                }
                if (string.startsWith("xwingrout") && (this.chunkDamageVisible("WingROut") < 3)) {
                    this.hitChunk("WingROut", shot);
                }
            } else if (string.startsWith("xarone")) {
                if (string.startsWith("xaronel")) {
                    this.hitChunk("AroneL", shot);
                }
                if (string.startsWith("xaroner")) {
                    this.hitChunk("AroneR", shot);
                }
            } else if (string.startsWith("xgear")) {
                if (string.endsWith("1") && (World.Rnd().nextFloat() < 0.05F)) {
                    this.debuggunnery("Hydro System: Disabled..");
                    this.FM.AS.setInternalDamage(shot.initiator, 0);
                }
                if (string.endsWith("2") && (World.Rnd().nextFloat() < 0.1F) && (this.getEnergyPastArmor(World.Rnd().nextFloat(1.2F, 3.435F), shot) > 0.0F)) {
                    this.debuggunnery("Undercarriage: Stuck..");
                    this.FM.AS.setInternalDamage(shot.initiator, 3);
                }
            } else if (string.startsWith("xpilot") || string.startsWith("xhead")) {
                int i = 0;
                int i_3_;
                if (string.endsWith("a")) {
                    i = 1;
                    i_3_ = string.charAt(6) - 49;
                } else if (string.endsWith("b")) {
                    i = 2;
                    i_3_ = string.charAt(6) - 49;
                } else {
                    i_3_ = string.charAt(5) - 49;
                }
                this.hitFlesh(i_3_, shot, i);
            }
        }
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 19:
                this.FM.EI.engines[0].setEngineDies(actor);
                return super.cutFM(i, j, actor);
        }
        return super.cutFM(i, j, actor);
    }

    public void typeFighterAceMakerRangeFinder() {
        if (this.k14Mode == 2) {
            return;
        }
        hunted = Main3D.cur3D().getViewPadlockEnemy();
        if (hunted == null) {
            this.k14Distance = 200F;
            hunted = War.GetNearestEnemyAircraft(this.FM.actor, 2700F, 9);
        }
        if (hunted != null) {
            this.k14Distance = (float) this.FM.actor.pos.getAbsPoint().distance(hunted.pos.getAbsPoint());
            if (this.k14Distance > 800F) {
                this.k14Distance = 800F;
            } else if (this.k14Distance < 200F) {
                this.k14Distance = 200F;
            }
        }
    }

    public void engineSurge(float f) {
        if (this.FM.AS.isMaster()) {
            if (this.curthrl < -0.99F) {
                this.curthrl = this.oldthrl = this.FM.EI.engines[0].getControlThrottle();
            } else {
                this.curthrl = this.FM.EI.engines[0].getControlThrottle();
                if (this.curthrl < 1.05F) {
                    if ((((this.curthrl - this.oldthrl) / f) > 20.0F) && (this.FM.EI.engines[0].getRPM() < 3200.0F) && (this.FM.EI.engines[0].getStage() == 6) && (World.Rnd().nextFloat() < 0.40F)) {
                        if (this.FM.actor == World.getPlayerAircraft()) {
                            HUD.log(AircraftHotKeys.hudLogWeaponId, "Compressor Stall!");
                        }
                        super.playSound("weapon.MGunMk108s", true);
                        this.engineSurgeDamage += 0.01D * (this.FM.EI.engines[0].getRPM() / 1000F);
                        this.FM.EI.engines[0].doSetReadyness(this.FM.EI.engines[0].getReadyness() - this.engineSurgeDamage);
                        if ((World.Rnd().nextFloat() < 0.05F) && (this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) {
                            this.FM.AS.hitEngine(this, 0, 100);
                        }
                        if ((World.Rnd().nextFloat() < 0.05F) && (this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) {
                            this.FM.EI.engines[0].setEngineDies(this);
                        }
                    }
                    if ((((this.curthrl - this.oldthrl) / f) < -20.0F) && (((this.curthrl - this.oldthrl) / f) > -100.0F) && (this.FM.EI.engines[0].getRPM() < 3200.0F) && (this.FM.EI.engines[0].getStage() == 6)) {
                        super.playSound("weapon.MGunMk108s", true);
                        this.engineSurgeDamage += 0.001D * (this.FM.EI.engines[0].getRPM() / 1000F);
                        this.FM.EI.engines[0].doSetReadyness(this.FM.EI.engines[0].getReadyness() - this.engineSurgeDamage);
                        if ((World.Rnd().nextFloat() < 0.40F) && (this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) {
                            if (this.FM.actor == World.getPlayerAircraft()) {
                                HUD.log(AircraftHotKeys.hudLogWeaponId, "Engine Flameout!");
                            }
                            this.FM.EI.engines[0].setEngineStops(this);
                        } else {
                            if (this.FM.actor == World.getPlayerAircraft()) {
                                HUD.log(AircraftHotKeys.hudLogWeaponId, "Compressor Stall!");
                            }
                        }
                    }
                }
                this.oldthrl = this.curthrl;
            }
        }
    }

    public void update(float f) {
        if ((!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel)this.FM).isRealMode()) && (this.FM instanceof Maneuver)) {
            Maneuver maneuver = (Maneuver) this.FM;
            if ((maneuver.get_maneuver() == 25) && (maneuver.Alt < 60.0F)) {
                if (maneuver.Or.getTangage() > 4F) { // Limit nose up attitude to 4 degrees on touchdown to avoid tail strike (only for AI)!
                    maneuver.Or.increment(0.0F, -(maneuver.Or.getTangage() - 4F), 0.0F);
                }
            }
        }
        this.checkBailout(f);
        if (this.FM.AS.isMaster() && Config.isUSE_RENDER()) {
            if ((this.FM.EI.engines[0].getPowerOutput() > 0.45F) && (this.FM.EI.engines[0].getStage() == 6)) {
                if (this.FM.EI.engines[0].getPowerOutput() > 0.65F) {
                    this.FM.AS.setSootState(this, 0, 3);
                } else {
                    this.FM.AS.setSootState(this, 0, 2);
                }
            } else {
                this.FM.AS.setSootState(this, 0, 0);
            }
        }
        super.update(f);
        this.engineSurge(f);
        this.typeFighterAceMakerRangeFinder();
        this.soundbarier();
        this.computeLift();
        this.computeEnergy();
        this.computeEngine();
        if (this.FM.AS.isMaster()) {
            if (this.curctl == -1.0F) {
                this.curctl = this.oldctl = this.FM.EI.engines[0].getControlThrottle();
            } else {
                this.curctl = this.FM.EI.engines[0].getControlThrottle();
                this.oldctl = this.curctl;
            }
        }
        if ((this.FM.CT.getGear() > 0.01F) && (this.FM.CT.AirBrakeControl > 0.01F) && this.FM.Gears.nearGround()) {
            this.FM.CT.AirBrakeControl = 0.0F;
            if (this == World.getPlayerAircraft()) HUD.log("DivebrakeOFF");
        }
        if ((this.FM.getSpeedKMH() > 500.0F) && this.FM.CT.bHasFlapsControl) {
            this.FM.CT.FlapsControl = 0.0F;
            this.FM.CT.bHasFlapsControl = false;
        } else {
            this.FM.CT.bHasFlapsControl = true;
        }
//        DecimalFormat df = new DecimalFormat("0.00");
//        HUD.training("E:" + df.format(this.FM.CT.ElevatorControl) + " T:" + df.format(this.FM.CT.getTrimElevatorControl()) );
    }

    // Lift Code by Vega
    public void computeLift() {
        Polares polares = (Polares) Reflection.getValue(this.FM, "Wing");
        float x = this.calculateMach();
        this.FM.setGCenter(Aircraft.cvt(x, 0.3F, 0.9F, 0.3F, 0.8F)); // GC moves backward at higher speed, by SAS~Storebror
        float Lift = 0.0F;
        if ((double) x > 1.1F) {
            Lift = 0.05F;
        } else {
            float x2 = x * x;
            float x3 = x2 * x;
            Lift = (((-0.0392084F * x3) + (0.0269944F * x2)) - (0.0195244F * x)) + 0.091F;
        }
        polares.lineCyCoeff = Lift;
    }

    // Energy Bleed Code by Vega
    public void computeEnergy() {
        float x = this.FM.getOverload();
        float Energy = 0.0F;
        if ((double) x >= 10F) {
            Energy = 0.085F;
        } else {
            float x2 = x * x;
            float x3 = x2 * x;
            float x4 = x3 * x;
            float x5 = x4 * x;
            Energy = (((0.000000842F * x5) + (0.0000009877F * x4)) - (0.00000947291F * x3)) + (0.00000222222F * x2) + (0.0000170512F * x);

        }
        this.FM.Sq.dragParasiteCx += Energy;

    }

    // High Altitude Thrust Degradation Code by Vega
    public void computeEngine() {
        float x = this.FM.getAltitude() / 1000F;
        float ThrustDegradation = 0.0F;
        if (x > 13.5F) {
            ThrustDegradation = 1.5F;
        } else {
            float x2 = x * x;
            ThrustDegradation = (0.0130719F * x2) - (0.0653595F * x);
        }
        this.FM.producedAF.x -= ThrustDegradation * 1000F;
    }

    public void auxPressed(int i) {
        super.auxPressed(i);
        if (i == 20) {
            if (!this.APmode1) {
                this.APmode1 = true;
                HUD.log("Autopilot Mode: Altitude ON");
                this.FM.AP.setStabAltitude(1000F);
            } else if (this.APmode1) {
                this.APmode1 = false;
                HUD.log("Autopilot Mode: Altitude OFF");
                this.FM.AP.setStabAltitude(false);
            }
        }
        if (i == 21) {
            if (!this.APmode2) {
                this.APmode2 = true;
                HUD.log("Autopilot Mode: Direction ON");
                this.FM.AP.setStabDirection(true);
                this.FM.CT.bHasRudderControl = false;
            } else if (this.APmode2) {
                this.APmode2 = false;
                HUD.log("Autopilot Mode: Direction OFF");
                this.FM.AP.setStabDirection(false);
                this.FM.CT.bHasRudderControl = true;
            }
        }
    }

    /*
     * Catapult bailout code
     */
    // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public void doEjectCatapult() {
        new MsgAction(false, this) {
            public void doAction(Object paramObject) {
                Aircraft localAircraft = (Aircraft) paramObject;
                if (Actor.isValid(localAircraft)) {
                    Loc localLoc1 = new Loc();
                    Loc localLoc2 = new Loc();
                    Vector3d localVector3d = new Vector3d(0.0, 0.0, 10.0);
                    HookNamed localHookNamed = new HookNamed(localAircraft, "_ExternalSeat01");
                    localAircraft.pos.getAbs(localLoc2);
                    localHookNamed.computePos(localAircraft, localLoc2, localLoc1);
                    localLoc1.transform(localVector3d);
                    localVector3d.x += localAircraft.FM.Vwld.x;
                    localVector3d.y += localAircraft.FM.Vwld.y;
                    localVector3d.z += localAircraft.FM.Vwld.z;
                    new EjectionSeat(1, localLoc1, localVector3d, localAircraft);
                }
            }
        };
        this.hierMesh().chunkVisible("Seat_D0", false);
        this.FM.setTakenMortalDamage(true, null);
        this.FM.CT.WeaponControl[0] = false;
        this.FM.CT.WeaponControl[1] = false;
        this.FM.CT.WeaponControl[2] = false;
        this.FM.CT.WeaponControl[3] = false;
        this.FM.AP.setStabAltitude(false);
        this.FM.AP.setStabDirection(false);
        this.FM.CT.bHasAileronControl = false;
        this.FM.CT.bHasRudderControl = false;
        this.FM.CT.bHasElevatorControl = false;
        this.FM.Sq.dragProducedCx += 0.12F;
    }

    private void bailout() {
        if (this.overrideBailout) {
            if ((this.FM.AS.astateBailoutStep >= 0) && (this.FM.AS.astateBailoutStep < 2)) {
                if ((this.FM.CT.cockpitDoorControl > 0.5F) && (this.FM.CT.getCockpitDoor() > 0.5F)) {
                    this.FM.AS.astateBailoutStep = (byte) 11;
                    this.doRemoveBlister();
                    this.ejectTime = Time.current() + 100L;
                } else {
                    this.FM.AS.astateBailoutStep = (byte) 2;
                }
            } else if ((this.FM.AS.astateBailoutStep >= 2) && (this.FM.AS.astateBailoutStep <= 3)) {
                switch (this.FM.AS.astateBailoutStep) {
                    case 2:
                        if (this.FM.CT.cockpitDoorControl < 0.5F) {
                            this.doRemoveBlister();
                            this.ejectTime = Time.current() + 1000L;
                        }
                        break;
                    case 3:
                        this.doRemoveBlister();
                        this.ejectTime = Time.current() + 1000L;
                        break;
                }
                if (this.FM.AS.isMaster()) {
                    this.FM.AS.netToMirrors(20, this.FM.AS.astateBailoutStep, 1, null);
                }
                this.FM.AS.astateBailoutStep = (byte) (this.FM.AS.astateBailoutStep + 1);
                if (this.FM.AS.astateBailoutStep == 4) {
                    this.FM.AS.astateBailoutStep = (byte) 11;
                }
            } else if ((this.FM.AS.astateBailoutStep >= 11) && (this.FM.AS.astateBailoutStep <= 19) && (Time.current() >= this.ejectTime)) {
                int i = this.FM.AS.astateBailoutStep;
                if (this.FM.AS.isMaster()) {
                    this.FM.AS.netToMirrors(20, this.FM.AS.astateBailoutStep, 1, null);
                }
                this.FM.AS.astateBailoutStep = (byte) (this.FM.AS.astateBailoutStep + 1);
                if (i == 11) {
                    this.FM.setTakenMortalDamage(true, null);
                    if ((this.FM instanceof Maneuver) && (((Maneuver) this.FM).get_maneuver() != 44)) {
                        if (this.FM.AS.actor != World.getPlayerAircraft()) {
                            ((Maneuver) this.FM).set_maneuver(44);
                        }
                    }
                }
                if (this.FM.AS.astatePilotStates[i - 11] < 99) {
                    this.doRemoveBodyFromPlane(i - 10);
                    if (i == 11) {
                        this.doEjectCatapult();
                        this.FM.setTakenMortalDamage(true, null);
                        this.FM.CT.WeaponControl[0] = false;
                        this.FM.CT.WeaponControl[1] = false;
                        this.FM.AS.astateBailoutStep = (byte) -1;
                        this.overrideBailout = false;
                        this.FM.AS.bIsAboutToBailout = true;
                        this.ejectComplete = true;
                        if ((i > 10) && (i <= 19)) {
                            EventLog.onBailedOut(this, i - 11);
                        }
                    }
                }
            }
        }
    }

    private final void doRemoveBlister() {
        if (this.blisterRemoved) {
            return;
        }
        if ((this.hierMesh().chunkFindCheck("Blister1_D0") != -1) && (this.FM.AS.getPilotHealth(0) > 0.0F)) {
            this.hierMesh().hideSubTrees("Blister1_D0");
            Wreckage localWreckage = new Wreckage(this, this.hierMesh().chunkFind("Blister1_D0"));
            localWreckage.collide(false);
            Vector3d localVector3d = new Vector3d();
            localVector3d.set(this.FM.Vwld);
            localWreckage.setSpeed(localVector3d);
        }
        this.blisterRemoved = true;
    }

    private void checkBailout(float f) {
        if ((this.FM.AS.bIsAboutToBailout || this.overrideBailout) && !this.ejectComplete && !this.FM.isStationedOnGround()) {
            this.overrideBailout = true;
            this.FM.AS.bIsAboutToBailout = false;
            this.bailout();
        }
    }

    // --------------------------------------------------------------------------------------------------

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

    private float            H1;
    private float            oldctl;
    private float            curctl;
    public float             AirBrakeControl;
    public int               k14Mode;
    public int               k14WingspanType;
    public float             k14Distance;
    public boolean           APmode1;
    public boolean           APmode2;
    private float            oldthrl;
    private float            curthrl;
    private float            engineSurgeDamage;
    static Actor             hunted            = null;
    boolean[]                dropTanks         = { false, false, false };
    static final int         DT_IN_230         = 0;
    static final int         DT_IN_100         = 1;
    static final int         DT_OUT_100        = 2;
    private boolean          overrideBailout;
    private boolean          ejectComplete;
    private boolean          blisterRemoved;
    private long             ejectTime;

    static {
        Class class1 = Hunter.class;
        Property.set(class1, "originCountry", PaintScheme.countryBritain);
    }
}
