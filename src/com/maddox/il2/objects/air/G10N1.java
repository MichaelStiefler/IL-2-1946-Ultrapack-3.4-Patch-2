package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.AircraftState;
import com.maddox.il2.fm.Gear;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.objects.air.electronics.RadarFuG200;
import com.maddox.il2.objects.weapons.BombGunNull;
import com.maddox.il2.objects.weapons.BombRatoJap;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.CommonTools;
import com.maddox.sas1946.il2.util.Reflection;
import com.maddox.sas1946.il2.util.TrueRandom;

public class G10N1 extends Scheme7 implements TypeBomber, TypeX4Carrier, TypeGuidedBombCarrier, TypeRadarFuG200Carrier, TypeRocketBoost {

    public G10N1() {
        bSightAutomation = false;
        bSightBombDump = false;
        fSightCurDistance = 0.0F;
        fSightCurForwardAngle = 0.0F;
        fSightCurSideslip = 0.0F;
        fSightCurAltitude = 850F;
        fSightCurSpeed = 150F;
        fSightCurReadyness = 0.0F;
        this.booster = new BombRatoJap[6];
        this.boosterEffects = new Eff3DActor[6];
        this.boostState = AircraftState._AS_BOOST_NOBOOST;
        this.boosterFireOutTime = -1L;
        this.prevFuel = -1F;
        this.totalBombs = 0;
        this.fuelTanks = 0;
        boolean repeat = false;
        do {
            for (int i = 0; i < this.rndgear.length; i++) {
                this.rndgear[i] = TrueRandom.nextFloat(0.0F, 0.2F);
            }
            repeat = false;
            if (Math.abs(this.rndgear[0] - this.rndgear[1]) < 0.05F) repeat = true;
            if (Math.abs(this.rndgear[0] - this.rndgear[2]) < 0.05F) repeat = true;
            if (Math.abs(this.rndgear[1] - this.rndgear[2]) < 0.05F) repeat = true;
        } while (repeat);
    }
    
    public void destroy() {
        this.doCutBoosters();
        super.destroy();
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        float fuelLevel = this.FM.M.fuel / this.FM.M.maxFuel;
        if (this.thisWeaponsName.endsWith("_er")) {
            fuelTanks = 1;
            this.FM.M.fuel += 10000F * fuelLevel;
        } else if (this.thisWeaponsName.endsWith("_lr")) {
            fuelTanks = 2;
            this.FM.M.fuel += 15000F * fuelLevel;
        }
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
        
        // Count total number of bombs
        this.totalBombs = 0;
        if (World.cur().diffCur.Limited_Ammo) {
            if (this.FM.CT != null && this.FM.CT.Weapons != null && this.FM.CT.Weapons[3] != null) {
                for (int i=0; i<this.FM.CT.Weapons[3].length; i++) {
                    if (this.FM.CT.Weapons[3][i] != null && !(this.FM.CT.Weapons[3][i] instanceof BombGunNull) && this.FM.CT.Weapons[3][i].haveBullets()) this.totalBombs += this.FM.CT.Weapons[3][i].countBullets();
                }
            }
        } else this.totalBombs = 500;
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("FuelTank_ER_Front", thisWeaponsName.endsWith("_er") || thisWeaponsName.endsWith("_lr"));
        hierMesh.chunkVisible("FuelTank_ER_Back", thisWeaponsName.endsWith("_er"));
        hierMesh.chunkVisible("FuelTank_LR_Back", thisWeaponsName.endsWith("_lr"));
    }
    
    public void setOnGround(Point3d point3d, Orient orient, Vector3d vector3d) {
        super.setOnGround(point3d, orient, vector3d);
        if (!this.isNetMaster()) return; // FIXME: Maybe FM.AS.isMaster() works better? Idea is to deal with "setOnGround" in single player missions only, or in Dogfight missions on server side only.
        if (this.thisWeaponsName.indexOf("_boost") != -1) this.FM.AS.setBoostState(this, this.boostState | AircraftState._AS_BOOST_EXISTS);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (flag) {
            for (int i = 0; i < 6; i++) {
                if (FM.AS.astateEngineStates[i] > 3 && World.Rnd().nextFloat() < 0.0023F) FM.AS.hitTank(this, i, 1);
            }

            for (int i = 0; i < 4; i++) {
                if (FM.AS.astateTankStates[i] > 4 && World.Rnd().nextFloat() < 0.04F) nextDMGLevel(FM.AS.astateEffectChunks[i] + "0", 0, this);
            }
        }

        if (prevFuel < 0F) {
            prevFuel = this.FM.M.fuel;
        } else {
            if (prevFuel > this.FM.M.fuel && this.FM.M.fuel > 100F) {
                this.FM.M.fuel += (prevFuel - this.FM.M.fuel) * 0.8F;
            }
            prevFuel = this.FM.M.fuel;
        }
    }
    
    public void update(float f) {
        super.update(f);
        
//        DecimalFormat df = new DecimalFormat("0.00");
//        HUD.training("AoA=" + df.format(this.FM.getAOA()) + ", RPM=" + (int)this.FM.EI.engines[0].getRPM() + ", PPhi=" + df.format(Math.toDegrees(this.FM.EI.engines[0].propPhi)) + ", PAoA" + df.format(Math.toDegrees(this.FM.EI.engines[0].propAoA)));

        // The following code serves the purpose to avoid tailstrikes on touchdown when AI is controlling the airplane.
        // Usually AI will land with flaps fully deployed, attempting to reach about 17° AoA.
        // That's too much for a plane like this, so we limit the AoA to 8° here.
        // Note that this only affects AI. A player can pull as much AoA as he likes, up and until the tailstrike happens.
        if ((!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) && (this.FM instanceof Maneuver)) {
            //this.FM.M.massEmpty = 3000F;
            Maneuver maneuver = (Maneuver) this.FM;
            if (maneuver.get_maneuver() == Maneuver.LANDING && maneuver.Alt < 60.0F) { // Plane is in landing pattern and near ground
                if (maneuver.Or.getTangage() > 6F) { // Limit nose up attitude to 6 degrees on touchdown to avoid tail strike (only for AI)!
                    maneuver.Or.increment(0.0F, -(maneuver.Or.getTangage() - 6F), 0.0F); // apply AoA limit
                }
            } else if (maneuver.get_maneuver() == Maneuver.TAKEOFF && this.FM.getSpeedKMH() > 10F && this.FM.getSpeedKMH() < 210F && (this.FM.Gears.onGround() || this.FM.Gears.nearGround())) { // Plane is in takeoff run but well below rotate speed
                this.FM.CT.Elevators = this.FM.CT.ElevatorControl = CommonTools.cvt(this.FM.getSpeedKMH(), 180F, 210F, 0F, this.FM.CT.ElevatorControl);
            }
        }

        this.hierMesh().chunkSetAngles("Pilot5_D0", this.FM.turret[0].tu[0], 0F, 0F);
        this.hierMesh().chunkSetAngles("Pilot6_D0", this.FM.turret[1].tu[0], 0F, 0F);

        for (int engineIndex = 4; engineIndex < 6; engineIndex++)
            if (this.oldProp[engineIndex] == 2 && this.FM.EI.engines[engineIndex].getStage() < 7) this.FM.AS.setEngineStuck(this, engineIndex);

        this.scheme7PropCollisionFix();
        this.boostUpdate();
    }
    
    public void setBoostState(int state) {
        if (this.boostState == state) return; // Nothing to do here, we are in the requested state already.
        if (((state ^ this.boostState) & AircraftState._AS_BOOST_EXISTS) != 0) { // The existence of boosters has changed
            if ((state & AircraftState._AS_BOOST_EXISTS) != 0) { // Boosters exist now
                this.doAttachBoosters();
            } else { // Boosters don't exist anymore
                this.doCutBoosters();
            }
        }
        if (((state ^ this.boostState) & AircraftState._AS_BOOST_ACTIVE) != 0) { // The boosters activity state changed
            if ((state & AircraftState._AS_BOOST_ACTIVE) != 0) { // Boosters are active now
                this.doFireBoosters();
            } else { // Boosters aren't active
                this.doShutoffBoosters();
            }
        }
        this.boostState = state;
    }

    public int getBoostState() {
        return this.boostState;
    }

    public void doAttachBoosters() {
        for (int i = 0; i < 6; i++) {
            try {
                this.booster[i] = new BombRatoJap();
                this.booster[i].pos.setBase(this, this.findHook("_BoosterH" + (i + 1)), false);
                this.booster[i].pos.resetAsBase();
                this.booster[i].drawing(true);
            } catch (Exception exception) {
                this.debugprintln("Structure corrupt - can't hang Starthilferakete..");
            }
        }
    }

    public void doCutBoosters() {
        for (int i = 0; i < 6; i++) {
            if (this.booster[i] != null) {
                this.booster[i].start();
                this.booster[i] = null;
            }
        }
        this.stopBoosterSound();
    }

    public void doFireBoosters() {
        for (int i = 0; i < 6; i++) {
            this.boosterEffects[i] = Eff3DActor.New(this, this.findHook("_Booster" + (i + 1)), null, 1.0F, "3DO/Effects/Tracers/RatoJap/rato.eff", 60F);
        }
        this.startBoosterSound();
    }

    public void doShutoffBoosters() {
        for (int i = 0; i < 6; i++) {
            Eff3DActor.finish(this.boosterEffects[i]); // No null checks etc. required here, it's done internally already.
        }
        this.stopBoosterSound();
    }

    public void startBoosterSound() {
        for (int i = 0; i < 6; i++) {
            if (this.booster[i] != null) {
                this.booster[i].startSound();
            }
        }
    }

    public void stopBoosterSound() {
        for (int i = 0; i < 6; i++) {
            if (this.booster[i] != null) {
                this.booster[i].stopSound();
            }
        }
    }

    private void boostUpdate() {
        if (!(this.FM instanceof Pilot)) return;
        if ((this.boostState & AircraftState._AS_BOOST_EXISTS) == 0) return;
        // TODO: Changed Booster cutoff reasons from absolute altitude to altitude above ground
        if (this.FM.getAltitude() - World.land().HQ_Air(this.FM.Loc.x, this.FM.Loc.y) > 300F && this.boosterFireOutTime == -1L && this.FM.Loc.z != 0.0D && World.Rnd().nextFloat() < 0.05F) {
            this.FM.AS.setBoostState(this, AircraftState._AS_BOOST_NOBOOST);
            this.FM.AS.setGliderBoostOff();
        }
        if (this.boosterFireOutTime == -1L && this.FM.Gears.onGround() && this.FM.EI.getPowerOutput() > 0.8F && this.FM.EI.engines[0].getStage() == 6 && this.FM.EI.engines[1].getStage() == 6 && this.FM.getSpeedKMH() > 20F) {
            this.boosterFireOutTime = Time.current() + 60000L;
            this.FM.AS.setBoostState(this, this.boostState | AircraftState._AS_BOOST_ACTIVE);
            this.FM.AS.setGliderBoostOn();
        }
        if (this.boosterFireOutTime > 0L) {
            if (Time.current() < this.boosterFireOutTime) {
                this.FM.producedAF.x += 150000D;
                this.FM.producedAF.z += 100000D;
            } else { // Stop sound
                this.FM.AS.setBoostState(this, this.boostState & ~AircraftState._AS_BOOST_ACTIVE);
            }
            if (Time.current() > this.boosterFireOutTime + 10000L) { // cut boosters 10 seconds after burnout regardless altitude if not done so before.
                this.FM.AS.setBoostState(this, AircraftState._AS_BOOST_NOBOOST);
                this.FM.AS.setGliderBoostOff();
            }
        }
    }

    protected void moveFlap(float f) {
        this.resetYPRmodifier();
        ypr[0] = CommonTools.smoothCvt(f, 0F, 0.2F, 0F, -1F);// + CommonTools.smoothCvt(f, 0.2F, 1F, 0F, -1F);
        ypr[1] = CommonTools.smoothCvt(f, 0F, 0.2F, 0F, -10F) + CommonTools.smoothCvt(f, 0.2F, 0.4F, 0F, -15F) + CommonTools.smoothCvt(f, 0.4F, 1F, 0F, -25F);
        ypr[2] = CommonTools.smoothCvt(f, 0F, 0.2F, 0F, 0.4F) + CommonTools.smoothCvt(-ypr[1], 10F, 60F, 0F, 0.1F);
        xyz[0] = CommonTools.smoothCvt(f, 0F, 0.2F, 0F, 1.2F) + CommonTools.smoothCvt(-ypr[1], 10F, 60F, 0F, -0.25F);
        xyz[1] = CommonTools.smoothCvt(f, 0F, 0.2F, 0F, 0.235F);
        xyz[2] = CommonTools.smoothCvt(f, 0F, 0.2F, 0F, -0.035F) + CommonTools.smoothCvt(-ypr[1], 10F, 60F, 0F, 0.045F);
        this.hierMesh().chunkSetLocate("Flap01_D0", xyz, ypr);
        ypr[0] = - ypr[0];
        ypr[2] = - ypr[2];
        xyz[1] = -xyz[1];
        this.hierMesh().chunkSetLocate("Flap02_D0", xyz, ypr);
    }

    protected void moveBayDoor(float f) {
        this.resetYPRmodifier();
        ypr[1] = CommonTools.smoothCvt(f, 0F, 0.7F, 0F, -90F);
        xyz[2] = CommonTools.smoothCvt(f, 0.3F, 1F, 0F, 0.8F);
        this.hierMesh().chunkSetLocate("Bay1_D0", xyz, ypr);
        this.hierMesh().chunkSetLocate("Bay2_D0", xyz, ypr);
        if (this.fuelTanks != 2) {
            this.hierMesh().chunkSetLocate("Bay3_D0", xyz, ypr);
            this.hierMesh().chunkSetLocate("Bay4_D0", xyz, ypr);
        }
    }

    public static void moveGear(HierMesh hiermesh, float leftGearPos, float rightGearPos, float frontGearPos, float[] rnd) {

        hiermesh.chunkSetAngles("GearC0_D0", 0.0F, CommonTools.smoothCvt(frontGearPos, rnd[2] + 0.1F, rnd[2] + 0.69F, 0F, 110F), 0.0F);
        hiermesh.chunkSetAngles("GearC3_D0", 0.0F, CommonTools.smoothCvt(frontGearPos, rnd[2] + 0.05F, rnd[2] + 0.24F, 0F, -90F), 0.0F);
        hiermesh.chunkSetAngles("GearC4_D0", 0.0F, CommonTools.smoothCvt(frontGearPos, rnd[2] + 0.05F, rnd[2] + 0.24F, 0F, 90F), 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, 0.0F, CommonTools.smoothCvt(leftGearPos, rnd[0] + 0.01F, rnd[2] + 0.79F, 0F, -90F));
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, CommonTools.smoothCvt(leftGearPos, rnd[0] + 0.01F, rnd[0] + 0.29F, 0F, 80F), 0.0F);
        hiermesh.chunkSetAngles("GearL6_D0", 0.0F, CommonTools.smoothCvt(leftGearPos, rnd[0] + 0.01F, rnd[0] + 0.29F, 0F, -80F), 0.0F);
        hiermesh.chunkSetAngles("GearL7_D0", 0.0F, 0.0F, CommonTools.smoothCvt(leftGearPos, rnd[0] + 0.01F, rnd[0] + 0.59F, 0F, -100F));
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, 0.0F, CommonTools.smoothCvt(rightGearPos, rnd[1] + 0.01F, rnd[1] + 0.79F, 0F, -90F));
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, CommonTools.smoothCvt(rightGearPos, rnd[1] + 0.01F, rnd[1] + 0.29F, 0F, -80F), 0.0F);
        hiermesh.chunkSetAngles("GearR6_D0", 0.0F, CommonTools.smoothCvt(rightGearPos, rnd[1] + 0.01F, rnd[1] + 0.29F, 0F, 80F), 0.0F);
        hiermesh.chunkSetAngles("GearR7_D0", 0.0F, 0.0F, CommonTools.smoothCvt(rightGearPos, rnd[1] + 0.01F, rnd[1] + 0.59F, 0F, -100F));
    }

    public static void moveGear(HierMesh hiermesh, float leftGearPos, float rightGearPos, float frontGearPos) {
        moveGear(hiermesh, leftGearPos, rightGearPos, frontGearPos, rndgearnull);
    }

    protected void moveGear(float leftGearPos, float rightGearPos, float frontGearPos) {
        moveGear(this.hierMesh(), leftGearPos, rightGearPos, frontGearPos, this.rndgear);
    }

    public static void moveGear(HierMesh hiermesh, float gearPos) {
        moveGear(hiermesh, gearPos, gearPos, gearPos, rndgearnull);
    }

    protected void moveGear(float gearPos) {
        moveGear(this.hierMesh(), gearPos, gearPos, gearPos, this.rndgear);
    }

    public void moveWheelSink() {
        // This is the gear's suspension code.
        // The maximum wheelsink we accept is 0.6m (out of 0.0m ... 1.0m). Above this, the gear gets stiff.
        // The suspension will sink max. 0.5m, which means at full gear pressure, the tire will be flattened by 10cm.

        resetYPRmodifier();
        // Calculate sink value for left gear.
        xyz[2] = cvt(FM.Gears.gWheelSinking[0], 0.0F, 0.6F, 0.0F, 0.5F);
        // Apply suspension movement to part "L4". "L1" is the wheel, it's attached to "L4" (which sinks in), which in turn is attached to "L2" (which holds the clip hook, that hook must not move).
        hierMesh().chunkSetLocate("GearL4_D0", xyz, ypr);

        resetYPRmodifier();
        // Calculate sink value for right gear.
        xyz[2] = cvt(FM.Gears.gWheelSinking[1], 0.0F, 0.6F, 0.0F, 0.5F);
        // Apply suspension movement to part "R4". "R1" is the wheel, it's attached to "R4" (which sinks in), which in turn is attached to "R2" (which holds the clip hook, that hook must not move).
        hierMesh().chunkSetLocate("GearR4_D0", xyz, ypr);

        resetYPRmodifier();
        // Calculate sink value for nose gear.
//        xyz[2] = cvt(FM.Gears.gWheelSinking[2], 0.0F, 0.6F, 0.0F, 0.5F);
        xyz[2] = cvt(FM.Gears.gWheelSinking[2], 0.0F, 1.0F, 0.0F, 0.9F);
        // Apply suspension movement to part "C0". "C1" is the wheel, it's attached to "C2" (which holds the clip hook, that hook must not move).
        // Therefore we had to squeeze a new "Placeholder" mesh in, in order to get the suspension to move. That placeholder is the "C0" part.
//        hierMesh().chunkSetLocate("GearC0_D0", xyz, ypr);
        hierMesh().chunkSetLocate("GearC2_D0", xyz, ypr);

    }

    public void moveSteering(float f) {
        // Move the nose wheel according to steering input
        hierMesh().chunkSetAngles("GearC0b_D0", f, 0.0F, 0.0F);
    }

    private String secondPropString(String firstPropString) {
        return firstPropString.substring(0, firstPropString.length() - 3) + "b" + firstPropString.substring(firstPropString.length() - 3);
    }

    protected void moveFan(float f) {
        this.hierMesh().chunkFind(Aircraft.Props[1][0]);
        for (int engineIndex = 0; engineIndex < this.FM.EI.getNum(); engineIndex++) {
            int propStage = 0;
            if (this.oldProp[engineIndex] < 2) {
                propStage = Math.abs((int) (this.FM.EI.engines[engineIndex].getw() * 0.06F));
                if (propStage >= 1) {
                    propStage = 1;
                }
                if ((propStage != this.oldProp[engineIndex]) && this.hierMesh().isChunkVisible(Aircraft.Props[engineIndex][this.oldProp[engineIndex]])) {
                    this.hierMesh().chunkVisible(Aircraft.Props[engineIndex][this.oldProp[engineIndex]], false);
                    this.hierMesh().chunkVisible(this.secondPropString(Aircraft.Props[engineIndex][this.oldProp[engineIndex]]), false);
                    this.oldProp[engineIndex] = propStage;
                    this.hierMesh().chunkVisible(Aircraft.Props[engineIndex][propStage], true);
                    this.hierMesh().chunkVisible(this.secondPropString(Aircraft.Props[engineIndex][propStage]), true);
                }
            }
            float propMovement = 57.3F * this.FM.EI.engines[engineIndex].getw();
            if (propStage == 0) {} else {
                propMovement %= 2880F;
                propMovement /= 2880F;
                if (propMovement <= 0.5F) {
                    propMovement *= 2.0F;
                } else {
                    propMovement = (propMovement * 2.0F) - 2.0F;
                }
                propMovement *= 1200F;
            }
            this.propPos[engineIndex] = (this.propPos[engineIndex] + (propMovement * f)) % 360F;
            this.hierMesh().chunkSetAngles(Aircraft.Props[engineIndex][propStage], 0.0F, -this.propPos[engineIndex], 0.0F);
            this.hierMesh().chunkSetAngles(this.secondPropString(Aircraft.Props[engineIndex][propStage]), 0.0F, -this.propPos[engineIndex], 0.0F);
        }
    }

    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        float yaw = -af[0];
        float pitch = af[1];
        float yawAbs = Math.abs(yaw);
        float maxPitch = 90F;
        float minPitch = -90F;
        switch (i) {
            default:
                break;

            case 0: // Top Front Gunner
                maxPitch = 73F;
                minPitch = -5F;
                if (yawAbs > 168F) minPitch = CommonTools.smoothCvt(yawAbs, 168F, 178F, -5F, 15F);
                else if (yawAbs > 152F) minPitch = CommonTools.smoothCvt(yawAbs, 152F, 162F, 0F, 5F);
                else if (yawAbs > 80F) minPitch = CommonTools.smoothCvt(yawAbs, 80F, 90F, -5F, 0F);
                else if (yawAbs < 40F) minPitch = CommonTools.smoothCvt(yawAbs, 30F, 40F, 0F, -5F);
                if (pitch > maxPitch) {
                    pitch = maxPitch;
                    flag = false;
                } else if (pitch < minPitch) {
                    pitch = minPitch;
                    flag = false;
                }
                break;
            case 1: // Top Center Gunner
                maxPitch = 73F;
                minPitch = 0F;
                if (yawAbs > 166F) minPitch = CommonTools.smoothCvt(yawAbs, 166F, 176F, 0F, 25F);
                else if (yawAbs > 152F) minPitch = CommonTools.smoothCvt(yawAbs, 152F, 162F, 0F, 5F);
                else if (yawAbs < 15F) minPitch = CommonTools.smoothCvt(yawAbs, 5F, 15F, 2F, 0F);
                if (pitch > maxPitch) {
                    pitch = maxPitch;
                    flag = false;
                } else if (pitch < minPitch) {
                    pitch = minPitch;
                    flag = false;
                }
                break;
            case 2: // Top Aft Gunner
                maxPitch = 73F;
                minPitch = -5F;
                if (yawAbs > 163F) minPitch = CommonTools.smoothCvt(yawAbs, 168F, 178F, -5F, 45F);
                else if (yawAbs > 125F) minPitch = CommonTools.smoothCvt(yawAbs, 125F, 135F, -5F, 0F);
                else if (yawAbs < 15F) minPitch = CommonTools.smoothCvt(yawAbs, 5F, 15F, 3F, 0F);
                else if (yawAbs < 80F) minPitch = CommonTools.smoothCvt(yawAbs, 70F, 80F, 0F, -5F);
                if (pitch > maxPitch) {
                    pitch = maxPitch;
                    flag = false;
                } else if (pitch < minPitch) {
                    pitch = minPitch;
                    flag = false;
                }
                break;
            case 3: // Belly Front Gunner
                maxPitch = 0F;
                minPitch = -73F;
                if (yawAbs < 15F) maxPitch = CommonTools.smoothCvt(yawAbs, 5F, 15F, -3F, 0F);
                else if (yawAbs > (this.FM.CT.getGear() > 0.01F ? 30F : 50F) && yawAbs < 90F) maxPitch = CommonTools.smoothCvt(yawAbs, this.FM.CT.getGear() > 0.01F ? 30F : 50F, this.FM.CT.getGear() > 0.01F ? 40F : 60F, 0F, -10F);
                else if (yawAbs > 90F) maxPitch = CommonTools.smoothCvt(yawAbs, 90F, 100F, -10F, 0F);
                if (this.FM.CT.getGear() > 0.01F && yawAbs > 115F) maxPitch = CommonTools.smoothCvt(yawAbs, 115F, 125F, 0F, -45F);
                if (pitch > maxPitch) {
                    pitch = maxPitch;
                    flag = false;
                } else if (pitch < minPitch) {
                    pitch = minPitch;
                    flag = false;
                }
                break;
            case 4: // Belly Aft Gunner
                maxPitch = 0F;
                minPitch = -73F;
                if (yawAbs > 115F) maxPitch = CommonTools.smoothCvt(yawAbs, 115F, 125F, 0F, -10F);
                if (pitch > maxPitch) {
                    pitch = maxPitch;
                    flag = false;
                } else if (pitch < minPitch) {
                    pitch = minPitch;
                    flag = false;
                }
                break;
            case 5: // Tail Gunner
                if (yaw < -38F) {
                    yaw = -38F;
                    flag = false;
                } else if (yaw > 38F) {
                    yaw = 38F;
                    flag = false;
                }
                if (pitch > 43F) {
                    pitch = 43F;
                    flag = false;
                } else if (pitch < -41F) {
                    pitch = -41F;
                    flag = false;
                }
                break;

        }
        af[0] = -yaw;
        af[1] = pitch;
        return flag;
    }

    public void hitProp(int i, int j, Actor actor) {
        if ((i > (this.FM.EI.getNum() - 1)) || (this.oldProp[i] == 2)) {
            return;
        }
        if (this.isChunkAnyDamageVisible("Prop" + (i + 1) + "b") || this.isChunkAnyDamageVisible("PropRot" + (i + 1) + "b")) {
            this.hierMesh().chunkVisible(this.secondPropString(Aircraft.Props[i][0]), false);
            this.hierMesh().chunkVisible(this.secondPropString(Aircraft.Props[i][1]), false);
            this.hierMesh().chunkVisible(this.secondPropString(Aircraft.Props[i][2]), true);
        }
        super.hitProp(i, j, actor);
    }

    private void scheme7PropCollisionFix() {
        Vector3d normal = new Vector3d();// (Vector3d)Reflection.getValue(Gear.class, "Normal");

        Point3d pn = new Point3d();
        pn.set(this.FM.Loc);
        Engine.cur.land.EQN(pn.x, pn.y, normal);

        this.FM.Or.transformInv(normal);
        pn.x = 0.0D;
        pn.y = 0.0D;
        pn.z = Engine.cur.land.HQ(pn.x, pn.y) - this.FM.Loc.z;
        this.FM.Or.transformInv(pn);
        double D = -normal.dot(pn);
        if (D > 50D) return;
        Point3f[] Pnt = (Point3f[]) Reflection.getValue(Gear.class, "Pnt");
        for (int i = 3; i < 9; i++) {
            Point3d PnT = new Point3d();
            PnT.set(Pnt[i]);
            double d = normal.dot(PnT) + D;
            if (d < 0.0D) {
                this.scheme7PropCollisionFix(i);
            }
        }
    }

    private boolean scheme7PropCollisionFix(int i) {
        if (Reflection.getBoolean(this.FM.Gears, "bIsMaster") && i >= 3 && i <= 8) {
            if (this == World.getPlayerAircraft() && !World.cur().diffCur.Realistic_Landings) return false;
            FM.setCapableOfTaxiing(false);
            this.hitProp(i - 3, 0, Engine.actorLand());
            return false;
        } else {
            return true;
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        int i = 0;
        if (s.startsWith(".")) s = s.substring(1);
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) {
                debuggunnery("Armor: Hit..");
                if (s.endsWith("p1")) getEnergyPastArmor((double) World.Rnd().nextFloat(5F, 12.7F) / (Math.abs(v1.x) + 0.0001D), shot);
                else if (s.endsWith("p2")) getEnergyPastArmor((double) World.Rnd().nextFloat(0.0F, 12.7F) / (Math.abs(v1.x) + 0.0001D), shot);
                else if (s.endsWith("g1")) {
                    getEnergyPastArmor((double) World.Rnd().nextFloat(20F, 30F) / (Math.abs(v1.x) + 0.0001D), shot);
                    if (World.Rnd().nextFloat() < 0.2F) FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 2);
                    if (shot.power <= 0.0F) doRicochetBack(shot);
                } else if (s.endsWith("g2")) {
                    getEnergyPastArmor((double) World.Rnd().nextFloat(20F, 30F) / (Math.abs(v1.x) + 0.0001D), shot);
                    if (shot.power <= 0.0F) doRicochetBack(shot);
                }
                return;
            }
            if (s.startsWith("xxcontrols")) {
                i = s.charAt(10) - '0';
                switch (i) {
                    case 1:
                        if ((getEnergyPastArmor(1.0F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                            if (World.Rnd().nextFloat() < 0.5F) {
                                this.FM.AS.setControlsDamage(shot.initiator, 1);
                                debugprintln(this, "*** Elevator Controls Out..");
                            } else {
                                this.FM.AS.setControlsDamage(shot.initiator, 2);
                                debugprintln(this, "*** Rudder Controls Out..");
                            }
                        }
                        break;
                }
                return;
            }

            if (s.startsWith("xxbomb") && World.Rnd().nextFloat() < 0.002F && FM.CT.Weapons[3] != null && FM.CT.Weapons[3][0].haveBullets()) {
                debugprintln(this, "*** Bomb Payload Detonates..");
                FM.AS.hitTank(shot.initiator, 0, 10);
                FM.AS.hitTank(shot.initiator, 1, 10);
                FM.AS.hitTank(shot.initiator, 2, 10);
                FM.AS.hitTank(shot.initiator, 3, 10);
                msgCollision(this, "CF_D0", "CF_D0");
            }

            if (s.startsWith("xxeng")) {
                i = s.charAt(5) - '1';
                if (s.endsWith("base")) {
                    if (getEnergyPastArmor(0.2F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < shot.power / 140000.0F) {
                            this.FM.AS.setEngineStuck(shot.initiator, i);
                            debugprintln(this, "*** Engine (" + i + ") Crank Case Hit - Engine Stucks..");
                        }
                        if (World.Rnd().nextFloat() < shot.power / 85000.0F) {
                            this.FM.AS.hitEngine(shot.initiator, i, 2);
                            debugprintln(this, "*** Engine (" + i + ") Crank Case Hit - Engine Damaged..");
                        }
                    } else if (World.Rnd().nextFloat() < 0.005F) {
                        this.FM.EI.engines[i].setCyliderKnockOut(shot.initiator, 1);
                    } else {
                        this.FM.EI.engines[i].setReadyness(shot.initiator, this.FM.EI.engines[i].getReadyness() - 0.00082F);
                        debugprintln(this, "*** Engine (" + i + ") Crank Case Hit - Readyness Reduced to " + this.FM.EI.engines[i].getReadyness() + "..");
                    }
                    getEnergyPastArmor(12.0F, shot);
                }
                if (s.endsWith("cyl")) {
                    if ((getEnergyPastArmor(5.85F, shot) > 0.0F) && (World.Rnd().nextFloat() < this.FM.EI.engines[i].getCylindersRatio() * 0.75F)) {
                        this.FM.EI.engines[i].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 19000.0F)));
                        debugprintln(this, "*** Engine (" + i + ") Cylinders Hit, " + this.FM.EI.engines[i].getCylindersOperable() + "/" + this.FM.EI.engines[i].getCylinders() + " Left..");
                        if (World.Rnd().nextFloat() < shot.power / 18000.0F) {
                            this.FM.AS.hitEngine(shot.initiator, i, 2);
                            debugprintln(this, "*** Engine (" + i + ") Cylinders Hit - Engine Fires..");
                        }
                    }
                    getEnergyPastArmor(25.0F, shot);
                }
                if (s.endsWith("wat")) {
                    this.FM.EI.engines[i].setMagnetoKnockOut(shot.initiator, 0);
                    debugprintln(this, "*** Engine (" + i + ") Module: Magneto #0 Destroyed..");
                    getEnergyPastArmor(25.0F, shot);
                }
                if ((s.endsWith("oil")) && (getEnergyPastArmor(0.2F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                    this.FM.AS.setOilState(shot.initiator, i, 1);
                    debugprintln(this, "*** Engine (" + i + ") Module: Oil Filter Pierced..");
                }
                return;
            }
            if (s.startsWith("xxspar")) {
                if ((s.startsWith("xxspare1")) && (chunkDamageVisible("Engine1") > 2) && (getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    debugprintln(this, "*** Engine1 Spars Damaged..");
                    nextDMGLevels(1, 2, "Engine1_D3", shot.initiator);
                }
                if ((s.startsWith("xxspare2")) && (chunkDamageVisible("Engine2") > 2) && (getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    debugprintln(this, "*** Engine2 Spars Damaged..");
                    nextDMGLevels(1, 2, "Engine2_D3", shot.initiator);
                }
                if ((s.startsWith("xxspare3")) && (chunkDamageVisible("Engine3") > 2) && (getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    debugprintln(this, "*** Engine3 Spars Damaged..");
                    nextDMGLevels(1, 2, "Engine3_D3", shot.initiator);
                }
                if ((s.startsWith("xxspare4")) && (chunkDamageVisible("Engine4") > 2) && (getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    debugprintln(this, "*** Engine4 Spars Damaged..");
                    nextDMGLevels(1, 2, "Engine4_D3", shot.initiator);
                }
                if ((s.startsWith("xxspare5")) && (chunkDamageVisible("Engine5") > 2) && (getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    debugprintln(this, "*** Engine5 Spars Damaged..");
                    nextDMGLevels(1, 2, "Engine5_D3", shot.initiator);
                }
                if ((s.startsWith("xxspare6")) && (chunkDamageVisible("Engine6") > 2) && (getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    debugprintln(this, "*** Engine6 Spars Damaged..");
                    nextDMGLevels(1, 2, "Engine6_D3", shot.initiator);
                }
                if ((s.startsWith("xxsparli")) && (chunkDamageVisible("WingLIn") > 2) && (getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    debugprintln(this, "*** WingLIn Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if ((s.startsWith("xxsparri")) && (chunkDamageVisible("WingRIn") > 2) && (getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    debugprintln(this, "*** WingRIn Spars Damaged..");
                    nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if ((s.startsWith("xxsparlm")) && (chunkDamageVisible("WingLMid") > 2) && (getEnergyPastArmor(16.799999F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    debugprintln(this, "*** WingLMid Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if ((s.startsWith("xxsparrm")) && (chunkDamageVisible("WingRMid") > 2) && (getEnergyPastArmor(16.799999F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    debugprintln(this, "*** WingRMid Spars Damaged..");
                    nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if ((s.startsWith("xxsparlo")) && (chunkDamageVisible("WingLOut") > 2) && (getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    debugprintln(this, "*** WingLOut Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if ((s.startsWith("xxsparro")) && (chunkDamageVisible("WingROut") > 2) && (getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.125F)) {
                    debugprintln(this, "*** WingROut Spars Damaged..");
                    nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                if ((s.startsWith("xxspark")) && (chunkDamageVisible("Keel1") > 1) && (getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.125F)) {
                    debugprintln(this, "*** Keel1 Spars Damaged..");
                    nextDMGLevels(1, 2, "Keel1_D" + chunkDamageVisible("Keel1"), shot.initiator);
                }
                if ((s.startsWith("xxsparsl")) && (chunkDamageVisible("StabL") > 2) && (getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.125F)) {
                    debugprintln(this, "*** StabL: Spars Damaged..");
                    nextDMGLevels(1, 2, "StabL_D" + chunkDamageVisible("StabL"), shot.initiator);
                }
                if ((s.startsWith("xxsparsr")) && (chunkDamageVisible("StabR") > 2) && (getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.125F)) {
                    debugprintln(this, "*** StabR: Spars Damaged..");
                    nextDMGLevels(1, 2, "StabR_D" + chunkDamageVisible("StabR"), shot.initiator);
                }
                if ((s.startsWith("xxspart")) && (chunkDamageVisible("Tail1") > 2) && (getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.125F)) {}
                return;
            }
            if (s.startsWith("xxtank")) {
                i = (s.charAt(6) - 49) / 2;
                if (getEnergyPastArmor(0.06F, shot) > 0.0F) {
                    if (this.FM.AS.astateTankStates[i] == 0) {
                        this.FM.AS.hitTank(shot.initiator, i, 1);
                        this.FM.AS.doSetTankState(shot.initiator, i, 1);
                    }
                    if (shot.powerType == 3) {
                        if (shot.power < 16100.0F) {
                            if ((this.FM.AS.astateTankStates[i] < 4) && (World.Rnd().nextFloat() < 0.21F)) {
                                this.FM.AS.hitTank(shot.initiator, i, 1);
                            }
                        } else {
                            this.FM.AS.hitTank(shot.initiator, i, World.Rnd().nextInt(1, 1 + (int) (shot.power / 16100.0F)));
                        }
                    } else if (shot.power > 16100.0F) {
                        this.FM.AS.hitTank(shot.initiator, i, World.Rnd().nextInt(1, 1 + (int) (shot.power / 16100.0F)));
                    }
                }
                return;
            }
            return;
        }
        if (s.startsWith("xcf")) {
            if (chunkDamageVisible("CF") < 3) {
                hitChunk("CF", shot);
            }
            return;
        }
        if (s.startsWith("xtail")) {
            if (chunkDamageVisible("Tail1") < 3) {
                hitChunk("Tail1", shot);
            }
            return;
        }
        if (s.startsWith("xkeel")) {
            if (chunkDamageVisible("Keel1") < 2) {
                hitChunk("Keel1", shot);
            }
            return;
        }
        if (s.startsWith("xrudder")) {
            if (chunkDamageVisible("Rudder1") < 2) {
                hitChunk("Rudder1", shot);
            }
            return;
        }
        if (s.startsWith("xstabl")) {
            if (chunkDamageVisible("StabL") < 2) {
                hitChunk("StabL", shot);
            }
            return;
        }
        if (s.startsWith("xstabr")) {
            if (chunkDamageVisible("StabR") < 2) {
                hitChunk("StabR", shot);
            }
            return;
        }
        if (s.startsWith("xvatorl")) {
            if (chunkDamageVisible("VatorL") < 2) {
                hitChunk("VatorL", shot);
            }
            return;
        }
        if (s.startsWith("xvatorr")) {
            if (chunkDamageVisible("VatorR") < 2) {
                hitChunk("VatorR", shot);
            }
            return;
        }
        if (s.startsWith("xwinglin")) {
            if (chunkDamageVisible("WingLIn") < 3) {
                hitChunk("WingLIn", shot);
            }
            return;
        }
        if (s.startsWith("xwingrin")) {
            if (chunkDamageVisible("WingRIn") < 3) {
                hitChunk("WingRIn", shot);
            }
            return;
        }
        if (s.startsWith("xwinglmid")) {
            if (chunkDamageVisible("WingLMid") < 3) {
                hitChunk("WingLMid", shot);
            }
            return;
        }
        if (s.startsWith("xwingrmid")) {
            if (chunkDamageVisible("WingRMid") < 3) {
                hitChunk("WingRMid", shot);
            }
            return;
        }
        if (s.startsWith("xwinglout")) {
            if (chunkDamageVisible("WingLOut") < 3) {
                hitChunk("WingLOut", shot);
            }
            return;
        }
        if (s.startsWith("xwingrout")) {
            if (chunkDamageVisible("WingROut") < 3) {
                hitChunk("WingROut", shot);
            }
            return;
        }
        if (s.startsWith("xaronel")) {
            if (chunkDamageVisible("AroneL") < 2) {
                hitChunk("AroneL", shot);
            }
            return;
        }
        if (s.startsWith("xaroner")) {
            if (chunkDamageVisible("AroneR") < 2) {
                hitChunk("AroneR", shot);
            }
            return;
        }
        if (s.startsWith("xengine1")) {
            if (chunkDamageVisible("Engine1") < 2) {
                hitChunk("Engine1", shot);
            }
            return;
        }
        if (s.startsWith("xengine2")) {
            if (chunkDamageVisible("Engine2") < 2) {
                hitChunk("Engine2", shot);
            }
            return;
        }
        if (s.startsWith("xengine3")) {
            if (chunkDamageVisible("Engine3") < 2) {
                hitChunk("Engine3", shot);
            }
            return;
        }
        if (s.startsWith("xengine4")) {
            if (chunkDamageVisible("Engine4") < 2) {
                hitChunk("Engine4", shot);
            }
            return;
        }
        if (s.startsWith("xengine5")) {
            if (chunkDamageVisible("Engine5") < 2) {
                hitChunk("Engine5", shot);
            }
            return;
        }
        if (s.startsWith("xengine6")) {
            if (chunkDamageVisible("Engine6") < 2) {
                hitChunk("Engine6", shot);
            }
            return;
        }
        if (s.startsWith("xgear")) {
            if (World.Rnd().nextFloat() < 0.05F) {
                debugprintln(this, "*** Gear Hydro Failed..");
                this.FM.Gears.setHydroOperable(false);
            }
            return;
        }
        if (s.startsWith("xturret")) {
            return;
        }
        if ((s.startsWith("xpilot")) || (s.startsWith("xhead"))) {
            i = 0;
            int j;
            if (s.endsWith("a")) {
                i = 1;
                j = s.charAt(6) - '1';
            } else if (s.endsWith("b")) {
                i = 2;
                j = s.charAt(6) - '1';
            } else {
                j = s.charAt(5) - '1';
            }
            hitFlesh(j, shot, i);
            return;
        }
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 33:
                return super.cutFM(34, j, actor);

            case 36:
                return super.cutFM(37, j, actor);

            case 3:
                return false;

            case 4:
                return false;

            case 5:
                return false;

            case 6:
                return false;
        }
        return super.cutFM(i, j, actor);
    }

    public void doKillPilot(int i) {
        switch (i) {
            case 4:
                this.FM.turret[0].bIsOperable = false;
                break;

            case 5:
                this.FM.turret[1].bIsOperable = false;
                break;

            case 6:
                this.FM.turret[2].bIsOperable = false;
                break;

            case 7:
                this.FM.turret[3].bIsOperable = false;
                break;

            case 8:
                this.FM.turret[4].bIsOperable = false;
                break;

            case 3:
                this.FM.turret[5].bIsOperable = false;
                break;
        }
    }

    public void doMurderPilot(int i) {
        switch (i) {
            default:
                break;

            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                if (this.hierMesh().isChunkVisible("Blister1_D0")) this.hierMesh().chunkVisible("Gore1_D0", true);
                break;

            case 1:
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                if (this.hierMesh().isChunkVisible("Blister1_D0")) this.hierMesh().chunkVisible("Gore2_D0", true);
                break;

            case 2:
                this.hierMesh().chunkVisible("Pilot3_D0", false);
                break;

            case 3:
                this.hierMesh().chunkVisible("Pilot4_D0", false);
                break;

            case 4:
                this.hierMesh().chunkVisible("Pilot5_D0", false);
                break;

            case 5:
                this.hierMesh().chunkVisible("Pilot6_D0", false);
                break;

            case 6:
                this.hierMesh().chunkVisible("Pilot7_D0", false);
                break;

            case 7:
                this.hierMesh().chunkVisible("Pilot8_D0", false);
                break;

            case 8:
                this.hierMesh().chunkVisible("Pilot9_D0", false);
                break;
        }
    }

    public void msgShot(Shot shot) {
        this.setShot(shot);
        if ("CF_D3".equals(shot.chunkName)) return;
        else {
            super.msgShot(shot);
            return;
        }
    }

    public boolean typeBomberToggleAutomation() {
        if (this.isGuidingBomb) {
            this.typeX4CResetControls();
            return bSightAutomation;
        }
        bSightAutomation = !bSightAutomation;
        bSightBombDump = false;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAutomation" + (bSightAutomation ? "ON" : "OFF"));
        return bSightAutomation;
    }

    public void typeBomberAdjDistanceReset() {
        if (this.isGuidingBomb) {
            this.typeX4CResetControls();
            return;
        }
        fSightCurDistance = 0.0F;
        fSightCurForwardAngle = 0.0F;
    }

    public void typeBomberAdjDistancePlus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjAttitudePlus();
            return;
        }
        fSightCurForwardAngle++;
        if (fSightCurForwardAngle > 85F) fSightCurForwardAngle = 85F;
        fSightCurDistance = fSightCurAltitude * (float) Math.tan(Math.toRadians(fSightCurForwardAngle));
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) fSightCurForwardAngle) });
        if (bSightAutomation) typeBomberToggleAutomation();
    }

    public void typeBomberAdjDistanceMinus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjAttitudeMinus();
            return;
        }
        fSightCurForwardAngle--;
        if (fSightCurForwardAngle < 0.0F) fSightCurForwardAngle = 0.0F;
        fSightCurDistance = fSightCurAltitude * (float) Math.tan(Math.toRadians(fSightCurForwardAngle));
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) fSightCurForwardAngle) });
        if (bSightAutomation) typeBomberToggleAutomation();
    }

    public void typeBomberAdjSideslipReset() {
        if (this.isGuidingBomb) {
            this.typeX4CResetControls();
            return;
        }
        fSightCurSideslip = 0.0F;
    }

    public void typeBomberAdjSideslipPlus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjSidePlus();
            return;
        }
        if (this.getCurPilot() == 1) {
            fSightCurSideslip += 0.05F;
            if (fSightCurSideslip > 3F) fSightCurSideslip = 3F;
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Float(fSightCurSideslip * 10F) });
        } else {
            this.radarMode++;
            if (this.radarMode > RadarFuG200.RADAR_MODE_SHORT) {
                this.radarMode = RadarFuG200.RADAR_MODE_NORMAL;
            }
        }
    }

    public void typeBomberAdjSideslipMinus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjSideMinus();
            return;
        }
        if (this.getCurPilot() == 1) {
            fSightCurSideslip -= 0.05F;
            if (fSightCurSideslip < -3F) fSightCurSideslip = -3F;
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Float(fSightCurSideslip * 10F) });
        } else {
            this.radarMode--;
            if (this.radarMode < RadarFuG200.RADAR_MODE_NORMAL) {
                this.radarMode = RadarFuG200.RADAR_MODE_SHORT;
            }
        }
    }

    public void typeBomberAdjAltitudeReset() {
        if (this.isGuidingBomb) {
            this.typeX4CResetControls();
            return;
        }
        fSightCurAltitude = 850F;
    }

    public void typeBomberAdjAltitudePlus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjAttitudePlus();
            return;
        }
        if (this.getCurPilot() == 1) {
            fSightCurAltitude += 10F;
            if (fSightCurAltitude > 15000F) fSightCurAltitude = 15000F;
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) fSightCurAltitude) });
            fSightCurDistance = fSightCurAltitude * (float) Math.tan(Math.toRadians(fSightCurForwardAngle));
        } else {
            this.radarGain += 10;
            if (this.radarGain > 100) {
                this.radarGain = 100;
            }
        }
    }

    public void typeBomberAdjAltitudeMinus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjAttitudeMinus();
            return;
        }
        if (this.getCurPilot() == 1) {
            fSightCurAltitude -= 10F;
            if (fSightCurAltitude < 250F) fSightCurAltitude = 250F;
            HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) fSightCurAltitude) });
            fSightCurDistance = fSightCurAltitude * (float) Math.tan(Math.toRadians(fSightCurForwardAngle));
        } else {
            this.radarGain -= 10;
            if (this.radarGain < 0) {
                this.radarGain = 0;
            }
        }
    }

    public void typeBomberAdjSpeedReset() {
        if (this.isGuidingBomb) {
            this.typeX4CResetControls();
            return;
        }
        fSightCurSpeed = 150F;
    }

    public void typeBomberAdjSpeedPlus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjAttitudePlus();
            return;
        }
        fSightCurSpeed += 10F;
        if (fSightCurSpeed > 800F) fSightCurSpeed = 800F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] { new Integer((int) fSightCurSpeed) });
    }

    public void typeBomberAdjSpeedMinus() {
        if (this.isGuidingBomb) {
            this.typeX4CAdjAttitudeMinus();
            return;
        }
        fSightCurSpeed -= 10F;
        if (fSightCurSpeed < 150F) fSightCurSpeed = 150F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] { new Integer((int) fSightCurSpeed) });
    }

    public void typeBomberUpdate(float f) {
        if ((double) Math.abs(FM.Or.getKren()) > 4.5D) {
            fSightCurReadyness -= 0.0666666F * f;
            if (fSightCurReadyness < 0.0F) fSightCurReadyness = 0.0F;
        }
        if (fSightCurReadyness < 1.0F) fSightCurReadyness += 0.0333333F * f;
        else if (bSightAutomation) {
            fSightCurDistance -= (fSightCurSpeed / 3.6F) * f;
            if (fSightCurDistance < 0.0F) {
                fSightCurDistance = 0.0F;
                typeBomberToggleAutomation();
            }
            fSightCurForwardAngle = (float) Math.toDegrees(Math.atan(fSightCurDistance / fSightCurAltitude));
            if ((double) fSightCurDistance < (double) (fSightCurSpeed / 3.6F) * Math.sqrt(fSightCurAltitude * 0.2038736F)) bSightBombDump = true;
            
            int tickDivider = 3;
            if (this.totalBombs >= 300) tickDivider = 1;
            else if (this.totalBombs >= 150) tickDivider = 2;
            
            if (bSightBombDump && FM.isTick(tickDivider, 0)) {
                if (FM.CT.Weapons[3] != null && FM.CT.Weapons[3][FM.CT.Weapons[3].length - 1] != null && FM.CT.Weapons[3][FM.CT.Weapons[3].length - 1].haveBullets()) {
                    FM.CT.WeaponControl[3] = true;
                    HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightBombdrop");
                }
            } else {
                FM.CT.WeaponControl[3] = false;
            }
        }
    }

    public void typeBomberReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
        netmsgguaranted.writeByte((bSightAutomation ? 1 : 0) | (bSightBombDump ? 2 : 0));
        netmsgguaranted.writeFloat(fSightCurDistance);
        netmsgguaranted.writeByte((int) fSightCurForwardAngle);
        netmsgguaranted.writeByte((int) ((fSightCurSideslip + 3F) * 33.33333F));
        netmsgguaranted.writeFloat(fSightCurAltitude);
        netmsgguaranted.writeByte((int) (fSightCurSpeed / 2.5F));
        netmsgguaranted.writeByte((int) (fSightCurReadyness * 200F));
        for (int i = 0; i < this.rndgear.length; i++) {
            netmsgguaranted.writeFloat(this.rndgear[i]);
        }
    }

    public void typeBomberReplicateFromNet(NetMsgInput netmsginput) throws IOException {
        int i = netmsginput.readUnsignedByte();
        bSightAutomation = (i & 1) != 0;
        bSightBombDump = (i & 2) != 0;
        fSightCurDistance = netmsginput.readFloat();
        fSightCurForwardAngle = netmsginput.readUnsignedByte();
        fSightCurSideslip = -3F + (float) netmsginput.readUnsignedByte() / 33.33333F;
        fSightCurAltitude = netmsginput.readFloat();
        fSightCurSpeed = (float) netmsginput.readUnsignedByte() * 2.5F;
        fSightCurReadyness = (float) netmsginput.readUnsignedByte() / 200F;
        for (i = 0; i < this.rndgear.length; i++) {
            this.rndgear[i] = netmsginput.readFloat();
        }
    }

    public boolean typeGuidedBombCisMasterAlive() {
        return isMasterAlive;
    }

    public void typeGuidedBombCsetMasterAlive(boolean flag) {
        isMasterAlive = flag;
    }

    public boolean typeGuidedBombCgetIsGuiding() {
        return isGuidingBomb;
    }

    public void typeGuidedBombCsetIsGuiding(boolean flag) {
        isGuidingBomb = flag;
    }

    public void typeX4CAdjSidePlus() {
        deltaAzimuth = 0.002F;
    }

    public void typeX4CAdjSideMinus() {
        deltaAzimuth = -0.002F;
    }

    public void typeX4CAdjAttitudePlus() {
        deltaTangage = 0.002F;
    }

    public void typeX4CAdjAttitudeMinus() {
        deltaTangage = -0.002F;
    }

    public void typeX4CResetControls() {
        deltaAzimuth = deltaTangage = 0.0F;
    }

    public float typeX4CgetdeltaAzimuth() {
        return deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage() {
        return deltaTangage;
    }

    public void setCurPilot(int theCurPilot) {
        this.pilotIndex = theCurPilot;
    }

    public int getCurPilot() {
        return this.pilotIndex;
    }

    public int getRadarGain() {
        return this.radarGain;
    }

    public int getRadarMode() {
        return this.radarMode;
    }

    private int pilotIndex = 0;
    private int radarGain  = 50;
    private int radarMode  = RadarFuG200.RADAR_MODE_NORMAL;;

    private boolean     bSightAutomation;
    private boolean     bSightBombDump;
    private float       fSightCurDistance;
    public float        fSightCurForwardAngle;
    public float        fSightCurSideslip;
    public float        fSightCurAltitude;
    public float        fSightCurSpeed;
    public float        fSightCurReadyness;

    private BombRatoJap booster[];
    private Eff3DActor  boosterEffects[];
    private int         boostState;
    protected long      boosterFireOutTime;

    private float   deltaAzimuth;
    private float   deltaTangage;
    private boolean isGuidingBomb;
    private boolean isMasterAlive;
    private float prevFuel;
    private int     totalBombs;
    private byte    fuelTanks;
    
    float[]        rndgear               = { 0.0F, 0.0F, 0.0F };
    static float[] rndgearnull           = { 0.0F, 0.0F, 0.0F }; // Used for Plane Land Pose calculation when Aircraft.setFM calls static gear methods

    static {
        Class class1 = G10N1.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "G10N1");
        Property.set(class1, "meshName", "3Do/Plane/G10N1(multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar02());
        Property.set(class1, "originCountry", PaintScheme.countryJapan);
        Property.set(class1, "yearService", 1945F);
        Property.set(class1, "yearExpired", 1947F);
        Property.set(class1, "cockpitClass", new Class[] { CockpitG10N1.class, CockpitG10N1_Bombardier.class, CockpitG10N1_T1Gunner.class, CockpitG10N1_T2Gunner.class, CockpitG10N1_T3Gunner.class, CockpitG10N1_B1Gunner.class, CockpitG10N1_B2Gunner.class,
                CockpitG10N1_AGunner.class });
        Property.set(class1, "FlightModel", "FlightModels/G10N1.fmd");
        Property.set(class1, "AutopilotElevatorAboveReferenceAltitudeFactor", 1.2E-4F); // TODO: Avoid AI Autopilot pulling up above reference altitude. Needs BAT 3.8 or newer, UP3.3 Patch 5 or newer, or appropriate EngineMod Version (newer than 2.8.16).
        int triggerLength = 12 + 1108*2; // 12 Gun + 1208 Bomb Spawns, Bombs are twice for BombGunNull
        int triggers[] = new int[triggerLength];
        String hooks[] = new String[triggerLength];
        for (int triggerIndex = 0; triggerIndex < triggerLength; triggerIndex++) {
            if (triggerIndex < 12) {
                triggers[triggerIndex] = 10 + triggerIndex / 2;
                hooks[triggerIndex] = "_MGUN" + (triggerIndex < 9 ? "0" : "") + (triggerIndex + 1);
            } else {
                int spawnIndex = (triggerIndex - 10) / 2;
                triggers[triggerIndex] = 3;
                hooks[triggerIndex] = "_BombSpawn" + (spawnIndex < 10 ? "0" : "") + spawnIndex;
            }
        }
        Aircraft.weaponTriggersRegister(class1, triggers);
        Aircraft.weaponHooksRegister(class1, hooks);
    }
}
