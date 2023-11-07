package com.maddox.il2.objects.air;

import java.io.IOException;
import java.text.DecimalFormat;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Explosion;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.AircraftState;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.fm.Turret;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.objects.Wreckage;
import com.maddox.il2.objects.weapons.BombRato264;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.CommonTools;
import com.maddox.sas1946.il2.util.Reflection;
import com.maddox.sas1946.il2.util.TrueRandom;

public abstract class ME_264 extends Scheme4 implements TypeBomber, TypeRocketBoost {

    public ME_264() {
        this.booster = new BombRato264[6];
        this.boosterEffects = new Eff3DActor[6];
        this.boostState = AircraftState._AS_BOOST_NOBOOST;
        this.boosterFireOutTime = -1L;
        boolean repeat = false;
        do {
            for (int i = 0; i < this.rndgear.length; i++) {
                this.rndgear[i] = TrueRandom.nextFloat(0.0F, 0.2F);
            }
            repeat = false;
            if (Math.abs(this.rndgear[0] - this.rndgear[1]) < 0.05F)
                repeat = true;
            if (Math.abs(this.rndgear[0] - this.rndgear[2]) < 0.05F)
                repeat = true;
            if (Math.abs(this.rndgear[1] - this.rndgear[2]) < 0.05F)
                repeat = true;
        } while (repeat);
    }

    public void destroy() {
        this.doCutBoosters();
        super.destroy();
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();

        for (int sootEffectIndex = 0; sootEffectIndex < this.FM.AS.astateSootEffects.length; sootEffectIndex++)
            this.FM.AS.astateSootEffects[sootEffectIndex] = new Eff3DActor[14];
    }

    public void setOnGround(Point3d point3d, Orient orient, Vector3d vector3d) {
        super.setOnGround(point3d, orient, vector3d);
        if (!this.isNetMaster())
            return; // FIXME: Maybe FM.AS.isMaster() works better? Idea is to deal with "setOnGround" in single player missions only, or in Dogfight missions on server side only.
        if (this.thisWeaponsName.indexOf("_boost") != -1)
            this.FM.AS.setBoostState(this, this.boostState | AircraftState._AS_BOOST_EXISTS);
    }

    public void update(float f) {
        super.update(f);
        // The following code serves the purpose to avoid tailstrikes on touchdown when AI is controlling the airplane.
        // Usually AI will land with flaps fully deployed, attempting to reach about 17� AoA.
        // That's too much for a plane like this, so we limit the AoA to 8� here.
        // Note that this only affects AI. A player can pull as much AoA as he likes, up and until the tailstrike happens.
        if ((!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) && (this.FM instanceof Maneuver)) {
            Maneuver maneuver = (Maneuver) this.FM;
            if (maneuver.get_maneuver() == Maneuver.LANDING && maneuver.Alt < 60.0F) { // Plane is in landing pattern and near ground
                if (maneuver.Or.getTangage() > 6F) { // Limit nose up attitude to 6 degrees on touchdown to avoid tail strike (only for AI)!
                    maneuver.Or.increment(0.0F, -(maneuver.Or.getTangage() - 6F), 0.0F); // apply AoA limit
                }
            } else if (maneuver.get_maneuver() == Maneuver.TAKEOFF && this.FM.getSpeedKMH() > 10F && this.FM.getSpeedKMH() < 210F && (this.FM.Gears.onGround() || this.FM.Gears.nearGround())) { // Plane is in takeoff run but well below rotate speed
                Reflection.setFloat(this.FM.CT, "Elevators", this.FM.CT.ElevatorControl = CommonTools.cvt(this.FM.getSpeedKMH(), 180F, 210F, 0F, this.FM.CT.ElevatorControl));
            }
        }
        this.boostUpdate();

        // Check if bay door is open and AI tries to reset front belly turret position.
        float bayDoor = this.getBayDoor();

        if (bayDoor > 0.01F && this.FM.turret[2].bIsAIControlled && CommonTools.equals(this.FM.turret[2].tuLim[1], 0F)) {
            // If so, make sure the turret position respects bomb bay door position.
            Object[] objects = { this.FM.turret[2], new Float(f) };
            Class[] classes = { Turret.class, float.class };
            this.turretAngles(2, this.FM.turret[2].tu);
            Reflection.invokeMethod(this.FM, "updateRotation", classes, objects);
        }

        if (!this.FM.Gears.onGround() && this.steering > 0.01F) {
            this.steering *= 0.99F;
            hierMesh().chunkSetAngles("GearC2b_D0", 0F, this.steering * this.FM.CT.getGear(), 0F);
        }
    }

    public void setBoostState(int state) {
        if (this.boostState == state)
            return; // Nothing to do here, we are in the requested state already.
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
                this.booster[i] = new BombRato264();
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
            this.boosterEffects[i] = Eff3DActor.New(this, this.findHook("_Booster" + (i + 1)), null, 1.0F, "3DO/Effects/Tracers/Rato264/rato.eff", 40F);
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
        if (!(this.FM instanceof Pilot))
            return;
        if ((this.boostState & AircraftState._AS_BOOST_EXISTS) == 0)
            return;
        // TODO: Changed Booster cutoff reasons from absolute altitude to altitude above ground
        if (this.FM.getAltitude() - World.land().HQ_Air(this.FM.Loc.x, this.FM.Loc.y) > 300F && this.boosterFireOutTime == -1L && this.FM.Loc.z != 0.0D && World.Rnd().nextFloat() < 0.05F) {
            this.FM.AS.setBoostState(this, AircraftState._AS_BOOST_NOBOOST);
            this.FM.AS.setGliderBoostOff();
        }
        if (this.boosterFireOutTime == -1L && this.FM.Gears.onGround() && this.FM.EI.getPowerOutput() > 0.8F && this.FM.EI.engines[0].getStage() == 6 && this.FM.EI.engines[1].getStage() == 6 && this.FM.getSpeedKMH() > 20F) {
            this.boosterFireOutTime = Time.current() + 40000L;
            this.FM.AS.setBoostState(this, this.boostState | AircraftState._AS_BOOST_ACTIVE);
            this.FM.AS.setGliderBoostOn();
        }
        if (this.boosterFireOutTime > 0L) {
            if (Time.current() < this.boosterFireOutTime) {
                this.FM.producedAF.x += 60000D;
                this.FM.producedAF.z += 20000D;
            } else { // Stop sound
                this.FM.AS.setBoostState(this, this.boostState & ~AircraftState._AS_BOOST_ACTIVE);
            }
            if (Time.current() > this.boosterFireOutTime + 10000L) { // cut boosters 10 seconds after burnout regardless altitude if not done so before.
                this.FM.AS.setBoostState(this, AircraftState._AS_BOOST_NOBOOST);
                this.FM.AS.setGliderBoostOff();
            }
        }
    }

    protected void moveRudder(float f) {
        hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -30F * f, 0.0F);
        hierMesh().chunkSetAngles("Rudder2_D0", 0.0F, -30F * f, 0.0F);
    }

    protected void moveFlap(float f) {
        float f1 = -35.5F * f;
        hierMesh().chunkSetAngles("Flap01_D0", 0.0F, f1, 0.0F);
        hierMesh().chunkSetAngles("Flap02_D0", 0.0F, f1, 0.0F);
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        int k1 = 0;
        if (s.startsWith(".")) s=s.substring(1);
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) {
                if (s.endsWith("01"))
                    getEnergyPastArmor(12.7D / (Math.abs(Aircraft.v1.z) + 0.000001D), shot);
                else if (s.endsWith("02"))
                    getEnergyPastArmor(12.7D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("03"))
                    getEnergyPastArmor(9.53D / (Math.abs(Aircraft.v1.y) + 0.000001D), shot);
                else if (s.endsWith("04"))
                    getEnergyPastArmor(9.53D / (Math.abs(Aircraft.v1.y) + 0.000001D), shot);
                else if (s.endsWith("05")) {
                    getEnergyPastArmor(0.5D / (Math.abs(Aircraft.v1.z) + 0.000001D), shot);
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x80);
                } else if (s.endsWith("06"))
                    getEnergyPastArmor(9.53D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("07"))
                    getEnergyPastArmor(9.53D / (Math.abs(Aircraft.v1.y) + 0.000001D), shot);
                else if (s.endsWith("08"))
                    getEnergyPastArmor(9.53D / (Math.abs(Aircraft.v1.y) + 0.000001D), shot);
                else if (s.endsWith("09"))
                    getEnergyPastArmor(6.35D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("10")) {
                    getEnergyPastArmor(22D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
                } else if (s.endsWith("11")) {
                    if (getEnergyPastArmor(52D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot) > 0.0F)
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
                } else if (s.endsWith("12"))
                    getEnergyPastArmor(52D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("13"))
                    getEnergyPastArmor(6.35D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("14"))
                    getEnergyPastArmor(7.9375D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("15")) {
                    getEnergyPastArmor(9.525D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
                } else if (s.endsWith("16")) {
                    getEnergyPastArmor(9.525D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
                } else if (s.endsWith("17"))
                    getEnergyPastArmor(15.875D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("18"))
                    getEnergyPastArmor(9.525D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("19"))
                    getEnergyPastArmor(9.525D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("20"))
                    getEnergyPastArmor(51D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("21"))
                    getEnergyPastArmor(22D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("22"))
                    getEnergyPastArmor(52D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("23"))
                    getEnergyPastArmor(9.525D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
                else if (s.endsWith("24"))
                    getEnergyPastArmor(9.525D / (Math.abs(Aircraft.v1.x) + 0.000001D), shot);
            }
            if (s.equals("xxfrontwindow"))
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
            if (s.equals("xxleftwindow"))
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
            if (s.equals("xxrightwindow"))
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
            if (s.equals("xxtopwindow"))
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
            if (s.equals("xxpanelleft"))
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
            if (s.equals("xxpanelright"))
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
            if (s.startsWith("xxammo")) {
                int i = s.charAt(6) - 48;
                if (s.length() == 8)
                    i = 10 + (s.charAt(7) - 48);
                if (getEnergyPastArmor(6.87F, shot) > 0.0F && World.Rnd().nextFloat() < 0.05F) {
                    switch (i) {
                        case 1:
                            i = 1;
                            k1 = 0;
                            break;

                        case 2:
                            return;

                        case 3:
                            i = 11;
                            k1 = 0;
                            break;

                        case 4:
                            i = 11;
                            k1 = 1;
                            break;

                        case 5:
                            i = 12;
                            k1 = 0;
                            break;

                        case 6:
                            i = 12;
                            k1 = 1;
                            break;

                        case 8:
                            i = 13;
                            k1 = 0;
                            break;

                        case 9:
                            i = 10;
                            k1 = 0;
                            break;

                        case 10:
                            i = 10;
                            k1 = 1;
                            break;

                    }
                    this.FM.AS.setJamBullets(i, k1);
                    return;
                }
            }
            if (s.startsWith("xxcontrols")) {
                int j = s.charAt(10) - 48;
                if (s.length() == 12)
                    j = 10 + (s.charAt(11) - 48);
                switch (j) {
                    default:
                        break;

                    case 1:
                    case 2:
                    case 11:
                    case 12:
                        if (getEnergyPastArmor(5F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F) {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                            Aircraft.debugprintln(this, "*** Aileron Controls Out..");
                        }
                        break;

                    case 3:
                    case 4:
                    case 5:
                    case 6:
                        if (World.Rnd().nextFloat() < 0.252F && getEnergyPastArmor(5.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.125F)
                            this.FM.AS.setControlsDamage(shot.initiator, 2);
                        getEnergyPastArmor(2.0F, shot);
                        break;

                    case 7:
                    case 8:
                    case 9:
                    case 10:
                        if (World.Rnd().nextFloat() < 0.252F && getEnergyPastArmor(5.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.125F)
                            this.FM.AS.setControlsDamage(shot.initiator, 1);
                        getEnergyPastArmor(2.0F, shot);
                        break;
                }
                return;
            }
            if (s.startsWith("xxeng")) {
                int k = s.charAt(5) - 49;
                if (s.endsWith("case")) {
                    if (getEnergyPastArmor(0.2F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < shot.power / 140000F) {
                            this.FM.AS.setEngineStuck(shot.initiator, k);
                            Aircraft.debugprintln(this, "*** Engine (" + k + ") Crank Case Hit - Engine Stucks..");
                        }
                        if (World.Rnd().nextFloat() < shot.power / 85000F) {
                            this.FM.AS.hitEngine(shot.initiator, k, 2);
                            Aircraft.debugprintln(this, "*** Engine (" + k + ") Crank Case Hit - Engine Damaged..");
                        }
                    } else if (World.Rnd().nextFloat() < 0.005F) {
                        this.FM.EI.engines[k].setCyliderKnockOut(shot.initiator, 1);
                    } else {
                        this.FM.EI.engines[k].setReadyness(shot.initiator, this.FM.EI.engines[k].getReadyness() - 0.00082F);
                        Aircraft.debugprintln(this, "*** Engine (" + k + ") Crank Case Hit - Readyness Reduced to " + this.FM.EI.engines[k].getReadyness() + "..");
                    }
                    getEnergyPastArmor(12F, shot);
                }
                if (s.endsWith("cyls")) {
                    if (getEnergyPastArmor(5.85F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[k].getCylindersRatio() * 0.75F) {
                        this.FM.EI.engines[k].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 19000F)));
                        Aircraft.debugprintln(this, "*** Engine (" + k + ") Cylinders Hit, " + this.FM.EI.engines[k].getCylindersOperable() + "/" + this.FM.EI.engines[k].getCylinders() + " Left..");
                        if (World.Rnd().nextFloat() < shot.power / 18000F) {
                            this.FM.AS.hitEngine(shot.initiator, k, 2);
                            Aircraft.debugprintln(this, "*** Engine (" + k + ") Cylinders Hit - Engine Fires..");
                        }
                    }
                    getEnergyPastArmor(25F, shot);
                }
                if (s.endsWith("mag1")) {
                    this.FM.EI.engines[k].setMagnetoKnockOut(shot.initiator, 0);
                    Aircraft.debugprintln(this, "*** Engine (" + k + ") Module: Magneto #0 Destroyed..");
                    getEnergyPastArmor(25F, shot);
                }
                if (s.endsWith("mag2")) {
                    this.FM.EI.engines[k].setMagnetoKnockOut(shot.initiator, 1);
                    Aircraft.debugprintln(this, "*** Engine (" + k + ") Module: Magneto #1 Destroyed..");
                    getEnergyPastArmor(25F, shot);
                }
                if (s.endsWith("oil1") && getEnergyPastArmor(0.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F) {
                    this.FM.AS.setOilState(shot.initiator, k, 1);
                    Aircraft.debugprintln(this, "*** Engine (" + k + ") Module: Oil Filter Pierced..");
                }
                return;
            }
            if (s.startsWith("xxlock")) {
                debuggunnery("Lock Construction: Hit..");
                if (s.startsWith("xxlockr1") && getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    debuggunnery("Lock Construction: Rudder1 Lock Shot Off..");
                    nextDMGLevels(3, 2, "Rudder1_D" + chunkDamageVisible("Rudder1"), shot.initiator);
                }
                if (s.startsWith("xxlockr2") && getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    debuggunnery("Lock Construction: Rudder2 Lock Shot Off..");
                    nextDMGLevels(3, 2, "Rudder2_D" + chunkDamageVisible("Rudder2"), shot.initiator);
                }
                if (s.startsWith("xxlockvl") && getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    debuggunnery("Lock Construction: VatorL Lock Shot Off..");
                    nextDMGLevels(3, 2, "VatorL_D" + chunkDamageVisible("VatorL"), shot.initiator);
                }
                if (s.startsWith("xxlockvr") && getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    debuggunnery("Lock Construction: VatorR Lock Shot Off..");
                    nextDMGLevels(3, 2, "VatorR_D" + chunkDamageVisible("VatorR"), shot.initiator);
                }
                if (s.startsWith("xxlockal") && getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    debuggunnery("Lock Construction: AroneL Lock Shot Off..");
                    nextDMGLevels(3, 2, "AroneL_D" + chunkDamageVisible("AroneL"), shot.initiator);
                }
                if (s.startsWith("xxlockar") && getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    debuggunnery("Lock Construction: AroneR Lock Shot Off..");
                    nextDMGLevels(3, 2, "AroneR_D" + chunkDamageVisible("AroneR"), shot.initiator);
                }
                return;
            }
            if (s.startsWith("xxoil")) {
                int l = s.charAt(5) - 49;
                if (getEnergyPastArmor(0.21F, shot) > 0.0F && World.Rnd().nextFloat() < 0.2435F)
                    this.FM.AS.hitOil(shot.initiator, l);
                Aircraft.debugprintln(this, "*** Engine (" + l + ") Module: Oil Tank Pierced..");
                return;
            }
            if (s.startsWith("xxpnm")) {
                if (getEnergyPastArmor(World.Rnd().nextFloat(0.25F, 1.22F), shot) > 0.0F) {
                    debuggunnery("Pneumo System: Disabled..");
                    this.FM.AS.setInternalDamage(shot.initiator, 1);
                }
                return;
            }
            if (s.startsWith("xxradio")) {
                getEnergyPastArmor(World.Rnd().nextFloat(5F, 25F), shot);
                return;
            }
            if (s.startsWith("xxautopilot")) {
                if (getEnergyPastArmor(World.Rnd().nextFloat(1.0F, 4F), shot) > 0.0F)
                    this.FM.AS.setControlsDamage(shot.initiator, 2);
                return;
            }
            if (s.startsWith("xxspar")) {
                if (s.startsWith("xxsparli") && chunkDamageVisible("WingLIn") > 2 && getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    Aircraft.debugprintln(this, "*** WingLIn Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparri") && chunkDamageVisible("WingRIn") > 2 && getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    Aircraft.debugprintln(this, "*** WingRIn Spars Damaged..");
                    nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlm") && chunkDamageVisible("WingLMid") > 2 && getEnergyPastArmor(16.8F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    Aircraft.debugprintln(this, "*** WingLMid Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparrm") && chunkDamageVisible("WingRMid") > 2 && getEnergyPastArmor(16.8F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    Aircraft.debugprintln(this, "*** WingRMid Spars Damaged..");
                    nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlo") && chunkDamageVisible("WingLOut") > 2 && getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    Aircraft.debugprintln(this, "*** WingLOut Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if (s.startsWith("xxsparro") && chunkDamageVisible("WingROut") > 2 && getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F) {
                    Aircraft.debugprintln(this, "*** WingROut Spars Damaged..");
                    nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                if (s.startsWith("xxspark1") && chunkDamageVisible("Keel1") > 1 && getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    Aircraft.debugprintln(this, "*** Keel1 Spars Damaged..");
                    nextDMGLevels(1, 2, "Keel1_D" + chunkDamageVisible("Keel1"), shot.initiator);
                }
                if (s.startsWith("xxspark2") && chunkDamageVisible("Keel2") > 1 && getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    Aircraft.debugprintln(this, "*** Keel2 Spars Damaged..");
                    nextDMGLevels(1, 2, "Keel2_D" + chunkDamageVisible("Keel2"), shot.initiator);
                }
                if (s.startsWith("xxsparsl") && chunkDamageVisible("StabL") > 1 && getEnergyPastArmor(11.2F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    debuggunnery("*** StabL Spars Damaged..");
                    nextDMGLevels(1, 2, "StabL_D2", shot.initiator);
                }
                if (s.startsWith("xxsparsr") && chunkDamageVisible("StabR") > 1 && getEnergyPastArmor(11.2F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    debuggunnery("*** StabR Spars Damaged..");
                    nextDMGLevels(1, 2, "StabR_D2", shot.initiator);
                }
                return;
            }
            if (s.startsWith("xxtank")) {
                int i1 = s.charAt(6) - 49;
                if (getEnergyPastArmor(0.06F, shot) > 0.0F) {
                    if (this.FM.AS.astateTankStates[i1] == 0) {
                        this.FM.AS.hitTank(shot.initiator, i1, 1);
                        this.FM.AS.doSetTankState(shot.initiator, i1, 1);
                    }
                    if (shot.powerType == 3) {
                        if (shot.power < 16100F) {
                            if (this.FM.AS.astateTankStates[i1] < 4 && World.Rnd().nextFloat() < 0.21F)
                                this.FM.AS.hitTank(shot.initiator, i1, 1);
                        } else {
                            this.FM.AS.hitTank(shot.initiator, i1, World.Rnd().nextInt(1, 1 + (int) (shot.power / 16100F)));
                        }
                    } else if (shot.power > 16100F)
                        this.FM.AS.hitTank(shot.initiator, i1, World.Rnd().nextInt(1, 1 + (int) (shot.power / 16100F)));
                }
                return;
            } else {
                return;
            }
        }
        if (s.startsWith("xcf")) {
            if (chunkDamageVisible("CF") < 3)
                hitChunk("CF", shot);
            return;
        }
        if (s.startsWith("xtail")) {
            if (chunkDamageVisible("Tail1") < 3)
                hitChunk("Tail1", shot);
            return;
        }
        if (s.startsWith("xkeel1")) {
            if (chunkDamageVisible("Keel1") < 2)
                hitChunk("Keel1", shot);
            return;
        }
        if (s.startsWith("xkeel2")) {
            if (chunkDamageVisible("Keel2") < 2)
                hitChunk("Keel2", shot);
            return;
        }
        if (s.startsWith("xrudder1")) {
            if (chunkDamageVisible("Rudder1") < 1)
                hitChunk("Rudder1", shot);
            return;
        }
        if (s.startsWith("xrudder2")) {
            if (chunkDamageVisible("Rudder2") < 1)
                hitChunk("Rudder2", shot);
            return;
        }
        if (s.startsWith("xvatorl")) {
            if (chunkDamageVisible("VatorL") < 2)
                hitChunk("VatorL", shot);
            return;
        }
        if (s.startsWith("xvatorr")) {
            if (chunkDamageVisible("VatorR") < 2)
                hitChunk("VatorR", shot);
            return;
        }
        if (s.startsWith("xstabl")) {
            if (chunkDamageVisible("StabL") < 2)
                hitChunk("StabL", shot);
            return;
        }
        if (s.startsWith("xstabr")) {
            if (chunkDamageVisible("StabR") < 2)
                hitChunk("StabR", shot);
            return;
        }
        if (s.startsWith("xwinglin")) {
            if (chunkDamageVisible("WingLIn") < 3)
                hitChunk("WingLIn", shot);
            return;
        }
        if (s.startsWith("xwingrin")) {
            if (chunkDamageVisible("WingRIn") < 3)
                hitChunk("WingRIn", shot);
            return;
        }
        if (s.startsWith("xwinglmid")) {
            if (chunkDamageVisible("WingLMid") < 3)
                hitChunk("WingLMid", shot);
            return;
        }
        if (s.startsWith("xwingrmid")) {
            if (chunkDamageVisible("WingRMid") < 3)
                hitChunk("WingRMid", shot);
            return;
        }
        if (s.startsWith("xwinglout")) {
            if (chunkDamageVisible("WingLOut") < 3)
                hitChunk("WingLOut", shot);
            return;
        }
        if (s.startsWith("xwingrout")) {
            if (chunkDamageVisible("WingROut") < 3)
                hitChunk("WingROut", shot);
            return;
        }
        if (s.startsWith("xaronel")) {
            if (chunkDamageVisible("AroneL") < 1)
                hitChunk("AroneL", shot);
            return;
        }
        if (s.startsWith("xaroner")) {
            if (chunkDamageVisible("AroneR") < 1)
                hitChunk("AroneR", shot);
            return;
        }
        if (s.startsWith("xengine1")) {
            if (chunkDamageVisible("Engine1") < 2)
                hitChunk("Engine1", shot);
            return;
        }
        if (s.startsWith("xengine2")) {
            if (chunkDamageVisible("Engine2") < 2)
                hitChunk("Engine2", shot);
            return;
        }
        if (s.startsWith("xengine3")) {
            if (chunkDamageVisible("Engine3") < 2)
                hitChunk("Engine3", shot);
            return;
        }
        if (s.startsWith("xengine4")) {
            if (chunkDamageVisible("Engine4") < 2)
                hitChunk("Engine4", shot);
            return;
        }
        if (s.startsWith("xgear")) {
            if (World.Rnd().nextFloat() < 0.05F) {
                Aircraft.debugprintln(this, "*** Gear Hydro Failed..");
                this.FM.Gears.setHydroOperable(false);
            }
            return;
        }
        if (s.startsWith("xturret"))
            return;
        if (s.startsWith("xmgun")) {
            int j1 = 10 * (s.charAt(5) - 48) + (s.charAt(6) - 48);
            if (getEnergyPastArmor(6.45F, shot) > 0.0F && World.Rnd().nextFloat() < 0.35F) {
                switch (j1) {
                    case 1:
                        j1 = 1;
                        k1 = 0;
                        break;

                    case 2:
                        return;

                    case 3:
                        j1 = 11;
                        k1 = 0;
                        break;

                    case 4:
                        j1 = 11;
                        k1 = 1;
                        break;

                    case 5:
                        j1 = 12;
                        k1 = 0;
                        break;

                    case 6:
                        j1 = 12;
                        k1 = 1;
                        break;

                    case 8:
                        j1 = 13;
                        k1 = 0;
                        break;

                    case 9:
                        j1 = 10;
                        k1 = 0;
                        break;

                    case 10:
                        j1 = 10;
                        k1 = 1;
                        break;

                }
                this.FM.AS.setJamBullets(j1, k1);
            }
            return;
        }
        if (s.startsWith("xpilot") || s.startsWith("xhead")) {
            byte byte0 = 0;
            int l1;
            if (s.endsWith("a")) {
                byte0 = 1;
                l1 = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte0 = 2;
                l1 = s.charAt(6) - 49;
            } else {
                l1 = s.charAt(5) - 49;
            }
            hitFlesh(l1, shot, byte0);
            return;
        } else {
            return;
        }
    }

    public void msgExplosion(Explosion explosion) {
        setExplosion(explosion);
        if (explosion.chunkName == null || explosion.power <= 0.0F || !explosion.chunkName.equals("Tail1_D3") && !explosion.chunkName.equals("WingLIn_D3") && !explosion.chunkName.equals("WingRIn_D3") && !explosion.chunkName.equals("WingLMid_D3") && !explosion.chunkName.equals("WingRMid_D3") && !explosion.chunkName.equals("WingLOut_D3") && !explosion.chunkName.equals("WingROut_D3"))
            super.msgExplosion(explosion);
    }

    protected void moveBayDoor(float f) {
        hierMesh().chunkSetAngles("Bay01_D0", 0.0F, -48F * f, 0.0F);
        hierMesh().chunkSetAngles("Bay02_D0", 0.0F, -48F * f, 0.0F);
        Object[] objects = { this.FM.turret[2], new Float(Time.tickConstLenFs()) };
        Class[] classes = { Turret.class, float.class };
        this.turretAngles(2, this.FM.turret[2].tu);
        Reflection.invokeMethod(this.FM, "updateRotation", classes, objects);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (flag) {
            if (this.FM.AS.astateTankStates[0] > 4 && World.Rnd().nextFloat() < 0.04F)
                nextDMGLevel(this.FM.AS.astateEffectChunks[0] + "0", 0, this);
            if (this.FM.AS.astateTankStates[1] > 4 && World.Rnd().nextFloat() < 0.04F)
                nextDMGLevel(this.FM.AS.astateEffectChunks[1] + "0", 0, this);
            if (this.FM.AS.astateTankStates[2] > 4 && World.Rnd().nextFloat() < 0.04F)
                nextDMGLevel(this.FM.AS.astateEffectChunks[2] + "0", 0, this);
            if (this.FM.AS.astateTankStates[3] > 4 && World.Rnd().nextFloat() < 0.04F)
                nextDMGLevel(this.FM.AS.astateEffectChunks[3] + "0", 0, this);
            if (!(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) {
                for (int i = 0; i < this.FM.EI.getNum(); i++)
                    if (this.FM.AS.astateEngineStates[i] > 3 && World.Rnd().nextFloat() < 0.2F)
                        this.FM.EI.engines[i].setExtinguisherFire();

            }
        }
        if (this.FM.getAltitude() < 3000F) {
            hierMesh().chunkVisible("HMask1_D0", false);
            hierMesh().chunkVisible("HMask2_D0", false);
            hierMesh().chunkVisible("HMask4_D0", false);
        } else {
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Pilot1_D0"));
            hierMesh().chunkVisible("HMask2_D0", hierMesh().isChunkVisible("Pilot2_D0"));
            hierMesh().chunkVisible("HMask4_D0", hierMesh().isChunkVisible("Pilot4_D0"));
        }
    }

    public void doKillPilot(int i) {
        if (i >= 4 && i <= 7)
            this.FM.turret[i - 4].bIsOperable = false;
    }

    public void doMurderPilot(int i) {
        hierMesh().chunkVisible("Pilot" + (i + 1) + "_D0", false);
        if (i < 4)
            hierMesh().chunkVisible("HMask" + (i + 1) + "_D0", false);
        hierMesh().chunkVisible("Pilot" + (i + 1) + "_D1", true);
        if (i < 4)
            hierMesh().chunkVisible("Head" + (i + 1) + "_D0", false);
    }

    private static final float toMeters(float f) {
        return 0.3048F * f;
    }

    private static final float toMetersPerSecond(float f) {
        return 0.4470401F * f;
    }

    public boolean typeBomberToggleAutomation() {
        bSightAutomation = !bSightAutomation;
        bSightBombDump = false;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAutomation" + (bSightAutomation ? "ON" : "OFF"));
        return bSightAutomation;
    }

    public void typeBomberAdjDistanceReset() {
        fSightCurDistance = 0.0F;
        fSightCurForwardAngle = 0.0F;
    }

    public void typeBomberAdjDistancePlus() {
        fSightCurForwardAngle++;
        if (fSightCurForwardAngle > 85F)
            fSightCurForwardAngle = 85F;
        fSightCurDistance = toMeters(fSightCurAltitude) * (float) Math.tan(Math.toRadians(fSightCurForwardAngle));
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) fSightCurForwardAngle) });
        if (bSightAutomation)
            typeBomberToggleAutomation();
    }

    public void typeBomberAdjDistanceMinus() {
        fSightCurForwardAngle--;
        if (fSightCurForwardAngle < 0.0F)
            fSightCurForwardAngle = 0.0F;
        fSightCurDistance = toMeters(fSightCurAltitude) * (float) Math.tan(Math.toRadians(fSightCurForwardAngle));
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) fSightCurForwardAngle) });
        if (bSightAutomation)
            typeBomberToggleAutomation();
    }

    public void typeBomberAdjSideslipReset() {
        fSightCurSideslip = 0.0F;
    }

    public void typeBomberAdjSideslipPlus() {
        fSightCurSideslip += 0.1F;
        if (fSightCurSideslip > 3F)
            fSightCurSideslip = 3F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Integer((int) (fSightCurSideslip * 10F)) });
    }

    public void typeBomberAdjSideslipMinus() {
        fSightCurSideslip -= 0.1F;
        if (fSightCurSideslip < -3F)
            fSightCurSideslip = -3F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Integer((int) (fSightCurSideslip * 10F)) });
    }

    public void typeBomberAdjAltitudeReset() {
        fSightCurAltitude = 3000F;
    }

    public void typeBomberAdjAltitudePlus() {
        fSightCurAltitude += 50F;
        if (fSightCurAltitude > 50000F)
            fSightCurAltitude = 50000F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitudeft", new Object[] { new Integer((int) fSightCurAltitude) });
        fSightCurDistance = toMeters(fSightCurAltitude) * (float) Math.tan(Math.toRadians(fSightCurForwardAngle));
    }

    public void typeBomberAdjAltitudeMinus() {
        fSightCurAltitude -= 50F;
        if (fSightCurAltitude < 1000F)
            fSightCurAltitude = 1000F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitudeft", new Object[] { new Integer((int) fSightCurAltitude) });
        fSightCurDistance = toMeters(fSightCurAltitude) * (float) Math.tan(Math.toRadians(fSightCurForwardAngle));
    }

    public void typeBomberAdjSpeedReset() {
        fSightCurSpeed = 200F;
    }

    public void typeBomberAdjSpeedPlus() {
        fSightCurSpeed += 10F;
        if (fSightCurSpeed > 450F)
            fSightCurSpeed = 450F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeedMPH", new Object[] { new Integer((int) fSightCurSpeed) });
    }

    public void typeBomberAdjSpeedMinus() {
        fSightCurSpeed -= 10F;
        if (fSightCurSpeed < 100F)
            fSightCurSpeed = 100F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeedMPH", new Object[] { new Integer((int) fSightCurSpeed) });
    }

    public void typeBomberUpdate(float f) {
        if ((double) Math.abs(this.FM.Or.getKren()) > 4.5D) {
            fSightCurReadyness -= 0.0666666F * f;
            if (fSightCurReadyness < 0.0F)
                fSightCurReadyness = 0.0F;
        }
        if (fSightCurReadyness < 1.0F)
            fSightCurReadyness += 0.0333333F * f;
        else if (bSightAutomation) {
            fSightCurDistance -= toMetersPerSecond(fSightCurSpeed) * f;
            if (fSightCurDistance < 0.0F) {
                fSightCurDistance = 0.0F;
                typeBomberToggleAutomation();
            }
            fSightCurForwardAngle = (float) Math.toDegrees(Math.atan(fSightCurDistance / toMeters(fSightCurAltitude)));
            if ((double) fSightCurDistance < (double) toMetersPerSecond(fSightCurSpeed) * Math.sqrt(toMeters(fSightCurAltitude) * (2F / 9.81F)))
                bSightBombDump = true;
            if (bSightBombDump)
                if (this.FM.isTick(3, 0)) {
                    if (this.FM.CT.Weapons[3] != null && this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1] != null && this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1].haveBullets()) {
                        this.FM.CT.WeaponControl[3] = true;
                        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightBombdrop");
                    }
                } else {
                    this.FM.CT.WeaponControl[3] = false;
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
    }

    protected void moveElevator(float f) {
        hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -30F * f, 0.0F);
        hierMesh().chunkSetAngles("VatorR_D0", 0.0F, -30F * f, 0.0F);
    }

    protected void moveAileron(float f) {
        hierMesh().chunkSetAngles("AroneL_D0", 0.0F, -30F * f, 0.0F);
        hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -30F * f, 0.0F);
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 33:
                hitProp(1, j, actor);
                this.FM.EI.engines[1].setEngineStuck(actor);
                this.FM.AS.hitTank(actor, 1, World.Rnd().nextInt(0, 9));
                // fall through

            case 34:
                hitProp(0, j, actor);
                this.FM.EI.engines[0].setEngineStuck(actor);
                this.FM.AS.hitTank(actor, 0, World.Rnd().nextInt(2, 8));
                this.FM.AS.hitTank(actor, 1, World.Rnd().nextInt(0, 5));
                // fall through

            case 35:
                this.FM.AS.hitTank(actor, 0, World.Rnd().nextInt(0, 4));
                break;

            case 36:
                hitProp(2, j, actor);
                this.FM.EI.engines[2].setEngineStuck(actor);
                this.FM.AS.hitTank(actor, 2, World.Rnd().nextInt(0, 9));
                // fall through

            case 37:
                hitProp(3, j, actor);
                this.FM.EI.engines[3].setEngineStuck(actor);
                this.FM.AS.hitTank(actor, 2, World.Rnd().nextInt(0, 5));
                this.FM.AS.hitTank(actor, 3, World.Rnd().nextInt(2, 8));
                // fall through

            case 38:
                this.FM.AS.hitTank(actor, 3, World.Rnd().nextInt(0, 4));
                break;

            case 25:
                this.FM.turret[0].bIsOperable = false;
                return false;

            case 26:
                this.FM.turret[1].bIsOperable = false;
                return false;

            case 27:
                this.FM.turret[2].bIsOperable = false;
                return false;

            case 28:
                this.FM.turret[3].bIsOperable = false;
                return false;

            case 29:
            case 30:
                return false;

            case 17:
                cut("Keel1");
                hierMesh().chunkVisible("Keel1_CAP", false);
                break;

            case 18:
                cut("Keel2");
                hierMesh().chunkVisible("Keel2_CAP", false);
                break;

            case 19:
                killPilot(this, 3);
                killPilot(this, 4);
                killPilot(this, 5);
                killPilot(this, 6);
                killPilot(this, 7);
                cut("StabL");
                cut("StabR");
                break;

            case 13:
                killPilot(this, 0);
                killPilot(this, 1);
                killPilot(this, 2);
                hierMesh().chunkVisible("Nose_Cap", true);
                hierMesh().chunkVisible("Nose_D0", false);
                hierMesh().chunkVisible("Nose_D1", false);
                hierMesh().chunkVisible("Nose_D2", false);
                hierMesh().chunkVisible("NoseAux_D0", false);
                hierMesh().chunkVisible("Blister1_D0", false);
                hierMesh().chunkVisible("GearC2_D0", false);
                hierMesh().chunkVisible("GearC3_D0", false);
                hierMesh().chunkVisible("GearC1_D0", false);
                hierMesh().chunkVisible("GearC8_D0", false);
                hierMesh().chunkVisible("GearC9_D0", false);
                hierMesh().chunkVisible("GearC10_D0", false);
                hierMesh().chunkVisible("Pilot1_D1", false);
                hierMesh().chunkVisible("Pilot2_D1", false);
                hierMesh().chunkVisible("Pilot3_D1", false);
                hierMesh().chunkVisible("WindowR_D0", false);
                hierMesh().chunkVisible("WindowL_D0", false);
                hierMesh().chunkVisible("Turret1A_D0", false);
                hierMesh().chunkVisible("Turret1B_D0", false);
                break;
        }
        return super.cutFM(i, j, actor);
    }

    public void doWreck(String s) {
        if (hierMesh().chunkFindCheck(s) != -1) {
            hierMesh().hideSubTrees(s);
            Wreckage wreckage = new Wreckage(this, hierMesh().chunkFind(s));
            wreckage.collide(true);
            Vector3d vector3d = new Vector3d();
            vector3d.set(this.FM.Vwld);
            wreckage.setSpeed(vector3d);
        }
    }

    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        float yaw = -af[0];
        while (yaw < -180F)
            yaw += 360F;
        while (yaw > 180F)
            yaw -= 360F;
        float yawAbs = Math.abs(yaw);
        float pitch = af[1];
        float pitchLimit;
        switch (i) {
            default:
                break;

            case 0:
                pitchLimit = 1F;

                if (yaw < -3F && yaw > -7F)
                    pitchLimit = Math.max(pitchLimit, cvt(yaw, -7F, -3F, 8F, 1F));
                else if (yaw <= -7F && yaw > -17F)
                    pitchLimit = Math.max(pitchLimit, 8F);
                else if (yaw <= -17F)
                    pitchLimit = Math.max(pitchLimit, cvt(yaw, -22F, -17F, 1F, 8F));
                else if (yaw > 9F && yaw < 13F)
                    pitchLimit = Math.max(pitchLimit, cvt(yaw, 9F, 13F, 1F, 8F));
                else if (yaw >= 13F && yaw < 17F)
                    pitchLimit = Math.max(pitchLimit, 8F);
                else if (yaw >= 17F)
                    pitchLimit = Math.max(pitchLimit, cvt(yaw, 17F, 22F, 8F, 1F));

                if (pitch > 80F) {
                    pitch = 80F;
                    flag = false;
                } else if (pitch < pitchLimit) {
                    pitch = pitchLimit;
                    flag = false;
                }

                break;

            case 1:
                pitchLimit = -5F;
                if (yawAbs < 13F)
                    pitchLimit = Math.max(pitchLimit, 0F);
                else if (yawAbs < 18F)
                    pitchLimit = Math.max(pitchLimit, CommonTools.smoothCvt(yawAbs, 13F, 18F, 0F, 10F));
                else if (yawAbs < 30F)
                    pitchLimit = Math.max(pitchLimit, 10F);
                else if (yawAbs < 35F)
                    pitchLimit = Math.max(pitchLimit, CommonTools.smoothCvt(yawAbs, 30F, 35F, 10F, -5F));
                else if (yawAbs < 85F)
                    pitchLimit = Math.max(pitchLimit, -5F);
                else if (yawAbs < 90F)
                    pitchLimit = Math.max(pitchLimit, CommonTools.smoothCvt(yawAbs, 85F, 90F, -5F, 2F));
                else
                    pitchLimit = Math.max(pitchLimit, 2F);
                if (pitch > 73F) {
                    pitch = 73F;
                    flag = false;
                }
                if (pitch < pitchLimit) {
                    pitch = pitchLimit;
                    flag = false;
                }
                break;

            case 2: {
                pitchLimit = 5F;
                if (yawAbs < 5F)
                    pitchLimit = Math.min(pitchLimit, CommonTools.smoothCvt(yawAbs, 0F, 5F, -2F, 0F));
                else if (yawAbs < 45F)
                    pitchLimit = Math.min(pitchLimit, CommonTools.smoothCvt(yawAbs, 5F, 45F, 0F, 5F));
                else if (yawAbs > 135F)
                    pitchLimit = Math.min(pitchLimit, CommonTools.smoothCvt(yawAbs, 135F, 180F, 5F, 0F));
                float bayDoor = this.getBayDoor();
                if (yawAbs < 80F && bayDoor > 0.01F) {
                    pitchLimit = Math.min(pitchLimit, (float) Math.cos(yawAbs / 160F * Math.PI) * -73F * bayDoor);
                }

                if (pitch < -73F) {
                    pitch = -73F;
                    flag = false;
                }
                if (pitch > pitchLimit) {
                    pitch = pitchLimit;
                    flag = false;
                }
                float gearL = this.FM.CT.getGearL();
                float gearR = this.FM.CT.getGearR();
                float gearC = this.FM.CT.getGearC();
                if (gearL > 0F && yaw > 35F && yaw < 65F && pitch > -15F)
                    flag = false;
                if (gearR > 0F && yaw < -35F && yaw > -65F && pitch > -15F)
                    flag = false;
                if (gearC > 0F && yawAbs > 140F && pitch > -45F)
                    flag = false;
            }
                break;

            case 3: {
                pitchLimit = 5F;
                if (yawAbs < 45F)
                    pitchLimit = Math.min(pitchLimit, CommonTools.smoothCvt(yawAbs, 0F, 45F, 0F, 5F));
                else if (yawAbs > 135F)
                    pitchLimit = Math.min(pitchLimit, CommonTools.smoothCvt(yawAbs, 135F, 180F, 5F, -2F));
                if (pitch < -73F) {
                    pitch = -73F;
                    flag = false;
                }
                if (pitch > pitchLimit) {
                    pitch = pitchLimit;
                    flag = false;
                }
                float gearL = this.FM.CT.getGearL();
                float gearR = this.FM.CT.getGearR();
                float gearC = this.FM.CT.getGearC();
                if (gearL > 0F && yaw > 115F && yaw < 145F && pitch > -15F)
                    flag = false;
                if (gearR > 0F && yaw < -115F && yaw > -145F && pitch > -15F)
                    flag = false;
                if (gearC > 0F && yawAbs > 175F && pitch > -10F)
                    flag = false;
            }
                break;

        }
        af[0] = -yaw;
        af[1] = pitch;
        return flag;
    }

    public static void moveGear(HierMesh hiermesh, float leftGearPos, float rightGearPos, float frontGearPos, float[] rnd, float steering) {
        Aircraft.xyz[0] = CommonTools.smoothCvt(frontGearPos, rnd[2] + 0.05F, rnd[2] + 0.75F, 0.0F, -0.3F);
        Aircraft.ypr[0] = 0.0F;
        Aircraft.xyz[1] = 0.0F;
        Aircraft.ypr[1] = CommonTools.smoothCvt(frontGearPos, rnd[2] + 0.05F, rnd[2] + 0.75F, 0.0F, -105F);
        Aircraft.ypr[2] = 0.0F;
        Aircraft.xyz[2] = CommonTools.smoothCvt(frontGearPos, rnd[2] + 0.05F, rnd[2] + 0.75F, 0.0F, 0.2F);
        hiermesh.chunkSetLocate("GearC3_D0", Aircraft.xyz, Aircraft.ypr);
        hiermesh.chunkSetAngles("GearC10_D0", 0.0F, CommonTools.smoothCvt(frontGearPos, rnd[2] + 0.05F, rnd[2] + 0.75F, 0.0F, -90F), 0.0F);
        hiermesh.chunkSetAngles("GearC8_D0", 0.0F, CommonTools.smoothCvt(frontGearPos, rnd[2] + 0.01F, rnd[2] + 0.3F, 0.0F, -60F), Aircraft.cvt(frontGearPos, rnd[2] + 0.01F, rnd[2] + 0.3F, 0.0F, 10F));
        hiermesh.chunkSetAngles("GearC9_D0", 0.0F, CommonTools.smoothCvt(frontGearPos, rnd[2] + 0.01F, rnd[2] + 0.3F, 0.0F, -60F), Aircraft.cvt(frontGearPos, rnd[2] + 0.01F, rnd[2] + 0.3F, 0.0F, -10F));
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, CommonTools.smoothCvt(leftGearPos, rnd[0] + 0.12F, rnd[0] + 0.79F, 0.0F, -85F), 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, CommonTools.smoothCvt(leftGearPos, rnd[0] + 0.12F, rnd[0] + 0.79F, 0.0F, 85F), 0.0F);
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, CommonTools.smoothCvt(leftGearPos, rnd[0] + 0.01F, rnd[0] + 0.4F, 0.0F, -100F), 0.0F);
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, CommonTools.smoothCvt(rightGearPos, rnd[1] + 0.12F, rnd[1] + 0.79F, 0.0F, -85F), 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, CommonTools.smoothCvt(rightGearPos, rnd[1] + 0.12F, rnd[1] + 0.79F, 0.0F, -85F), 0.0F);
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, CommonTools.smoothCvt(rightGearPos, rnd[1] + 0.01F, rnd[1] + 0.4F, 0.0F, 100F), 0.0F);
        hiermesh.chunkSetAngles("GearC2b_D0", 0F, steering * frontGearPos, 0F);
    }

    public static void moveGear(HierMesh hiermesh, float leftGearPos, float rightGearPos, float frontGearPos) {
        moveGear(hiermesh, leftGearPos, rightGearPos, frontGearPos, rndgearnull, 0F);
    }

    protected void moveGear(float leftGearPos, float rightGearPos, float frontGearPos) {
        moveGear(this.hierMesh(), leftGearPos, rightGearPos, frontGearPos, this.rndgear, this.steering);
    }

    public static void moveGear(HierMesh hiermesh, float gearPos) {
        moveGear(hiermesh, gearPos, gearPos, gearPos, rndgearnull, 0F);
    }

    protected void moveGear(float gearPos) {
        moveGear(this.hierMesh(), gearPos, gearPos, gearPos, this.rndgear, this.steering);
    }

    public void moveSteering(float f) {
        this.steering = f * cvt(this.FM.getSpeedKMH(), 0F, 150F, 2F, 0F);
        hierMesh().chunkSetAngles("GearC2b_D0", this.steering * this.FM.CT.getGear(), 0F, 0F);
    }

    public void moveWheelSink() {
        // This is the gear's suspension code.
        // The maximum wheelsink we accept is 0.6m (out of 0.0m ... 1.0m). Above this, the gear gets stiff.
        // The suspension will sink max. 0.5m, which means at full gear pressure, the tire will be flattened by 10cm.

        resetYPRmodifier();
        // Calculate sink value for left gear.
        xyz[1] = cvt(FM.Gears.gWheelSinking[0], 0.0F, 0.6F, 0.0F, 0.5F);
        // Apply suspension movement to part "L4". "L1" is the wheel, it's attached to "L4" (which sinks in), which in turn is attached to "L2" (which holds the clip hook, that hook must not move).
        hierMesh().chunkSetLocate("GearL2_D0", xyz, ypr);

        resetYPRmodifier();
        // Calculate sink value for right gear.
        xyz[1] = cvt(FM.Gears.gWheelSinking[1], 0.0F, 0.6F, 0.0F, 0.5F);
        // Apply suspension movement to part "R4". "R1" is the wheel, it's attached to "R4" (which sinks in), which in turn is attached to "R2" (which holds the clip hook, that hook must not move).
        hierMesh().chunkSetLocate("GearR2_D0", xyz, ypr);

        resetYPRmodifier();
        // Calculate sink value for nose gear.
        xyz[1] = cvt(FM.Gears.gWheelSinking[2], 0.0F, 1.0F, 0.0F, 0.5F);
        // Apply suspension movement to part "C0". "C1" is the wheel, it's attached to "C2" (which holds the clip hook, that hook must not move).
        // Therefore we had to squeeze a new "Placeholder" mesh in, in order to get the suspension to move. That placeholder is the "C0" part.
        hierMesh().chunkSetLocate("GearC2_D0", xyz, ypr);
    }

    public float getBombSightPDI() {
        return 0.0F;
    }

    public void doSetSootState(int engineIndex, int sootState) {
        DecimalFormat df = new DecimalFormat("00");
        for (int sootEffectIndex = 0; sootEffectIndex < 14; sootEffectIndex++) {
            if (this.FM.AS.astateSootEffects[engineIndex][sootEffectIndex] != null)
                Eff3DActor.finish(this.FM.AS.astateSootEffects[engineIndex][sootEffectIndex]);
            this.FM.AS.astateSootEffects[engineIndex][sootEffectIndex] = null;
        }

        switch (sootState) {
            case 1:
                for (int sootEffectIndex = 0; sootEffectIndex < 14; sootEffectIndex++)
                    this.FM.AS.astateSootEffects[engineIndex][sootEffectIndex] = Eff3DActor.New(this, findHook("_Engine" + (engineIndex + 1) + "ES_" + df.format(sootEffectIndex + 1)), null, 1.0F, "3DO/Effects/Aircraft/BlackSmallTSPD.eff", -1F);
                break;

            case 3:
                for (int sootEffectIndex = 0; sootEffectIndex < 14; sootEffectIndex++)
                    this.FM.AS.astateSootEffects[engineIndex][sootEffectIndex] = Eff3DActor.New(this, findHook("_Engine" + (engineIndex + 1) + "EF_" + df.format(sootEffectIndex + 1)), null, 1.0F, "3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1F);
                break;

            case 2:
                for (int sootEffectIndex = 0; sootEffectIndex < 14; sootEffectIndex++)
                    this.FM.AS.astateSootEffects[engineIndex][sootEffectIndex] = Eff3DActor.New(this, findHook("_Engine" + (engineIndex + 1) + "EF_" + df.format(sootEffectIndex + 1)), null, 1.0F, "3DO/Effects/Aircraft/TurboZippo.eff", -1F);
                break;

            case 5:
                for (int sootEffectIndex = 0; sootEffectIndex < 14; sootEffectIndex++)
                    this.FM.AS.astateSootEffects[engineIndex][sootEffectIndex] = Eff3DActor.New(this, findHook("_Engine" + (engineIndex + 1) + "EF_" + df.format(sootEffectIndex + 1)), null, 3F, "3DO/Effects/Aircraft/TurboJRD1100F.eff", -1F);
                break;

            case 4:
                for (int sootEffectIndex = 0; sootEffectIndex < 14; sootEffectIndex++)
                    this.FM.AS.astateSootEffects[engineIndex][sootEffectIndex] = Eff3DActor.New(this, findHook("_Engine" + (engineIndex + 1) + "EF_" + df.format(sootEffectIndex + 1)), null, 1.0F, "3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1F);
                break;
        }
    }

    public static boolean checkTurretVisibilityPlaneBlocking(int turretIndex) {
        return false;
    }

    private BombRato264   booster[];
    private Eff3DActor    boosterEffects[];
    private int           boostState;
    protected long        boosterFireOutTime;
    private float         steering    = 0F;

    public static boolean bChangedPit = false;
    float                 fSightCurReadyness;
    private boolean       bSightAutomation;
    private boolean       bSightBombDump;
    public float          fSightCurDistance;
    public float          fSightCurForwardAngle;
    public float          fSightSetForwardAngle;
    public float          fSightCurSideslip;
    public float          fSightCurAltitude;
    public float          fSightCurSpeed;
    float[]               rndgear     = { 0.0F, 0.0F, 0.0F };
    static float[]        rndgearnull = { 0.0F, 0.0F, 0.0F }; // Used for Plane Land Pose calculation when Aircraft.setFM calls static gear methods

    static {
        Class class1 = ME_264.class;
        Property.set(class1, "originCountry", PaintScheme.countryUSA);
    }
}
