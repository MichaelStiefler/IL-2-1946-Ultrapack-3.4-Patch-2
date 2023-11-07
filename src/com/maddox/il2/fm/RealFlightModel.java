package com.maddox.il2.fm;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Tuple3d;
import com.maddox.JGP.Tuple3f;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Airport;
import com.maddox.il2.ai.DifficultySettings;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.TextScr;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.F4U;
import com.maddox.il2.objects.air.NetAircraft;
import com.maddox.il2.objects.air.TypeBomber;
import com.maddox.il2.objects.air.TypeGSuit;
import com.maddox.il2.objects.air.TypeSupersonic;
import com.maddox.il2.objects.effects.ForceFeedback;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.rts.CmdEnv;
import com.maddox.rts.Time;
import com.maddox.sound.SoundFX;

public class RealFlightModel extends Pilot {

    public RealFlightModel(String s) {
        super(s);
        this.RealMode = true;
        this.indSpeed = 0.0F;
        this.Cwl = new Vector3d();
        this.Cwr = new Vector3d();
        this.Chl = new Vector3d();
        this.Chr = new Vector3d();
        this.Cv = new Vector3d();
        this.Fwl = new Vector3d();
        this.Fwr = new Vector3d();
        this.Fhl = new Vector3d();
        this.Fhr = new Vector3d();
        this.Fv = new Vector3d();
        this.superFuel = 10F;
        this.shakeLevel = 0.0F;
        this.producedShakeLevel = 0.0F;
        this.lastAcc = 1.0F;
        this.ailerInfluence = 1.0F;
        this.rudderInfluence = 1.0F;
        this.indiffDnTime = 4F;
        this.knockDnTime = 0.0F;
        this.indiffUpTime = 4F;
        this.knockUpTime = 0.0F;
        this.saveDeep = 0.0F;
        this.su26add = 0.0D;
        this.spinCoeff = 0.0D;
        this.bSound = true;
        this.Current_G_Limit = 8F;
        this.cycleCounter = 0;
        this.timeCounter = 0.0F;
        this.gearCutCounter = 0;
        this.bGearCut = false;
        this.max_G_Cycle = 1.0F;
        this.maxSpeed = 0.0F;
        this.airborneState = 0;
        this.airborneStartPoint = new Point3d();
        this.TmpP = new Point3d();
        this.Vn = new Vector3d();
        this.TmpV = new Vector3d();
        this.TmpVd = new Vector3d();
        this.plAccel = new Vector3d();
        this.AP = new Autopilot(this);
        this.Realism = World.cur().diffCur;
        this.maxSpeed = this.VmaxAllowed;
    }

    public Vector3d getW() {
        return this.RealMode ? this.W : this.Wtrue;
    }

    private void flutter() {
        if (this.Realism.Flutter_Effect) ((Aircraft) this.actor).msgCollision(this.actor, "CF_D0", "CF_D0");
    }

    private void flutterDamage() {
        if (this.Realism.Flutter_Effect) {
            String s;
            switch (World.Rnd().nextInt(0, 29)) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 20:
                    s = "AroneL";
                    break;

                case 4:
                case 5:
                case 6:
                case 7:
                case 21:
                    s = "AroneR";
                    break;

                case 8:
                case 9:
                case 10:
                case 22:
                    s = "VatorL";
                    break;

                case 11:
                case 12:
                case 13:
                case 23:
                    s = "VatorR";
                    break;

                case 24:
                case 25:
                case 26:
                    s = "Rudder1";
                    break;

                case 27:
                case 28:
                case 29:
                    s = "Rudder2";
                    break;

                case 14:
                    s = "WingLOut";
                    break;

                case 15:
                    s = "WingROut";
                    break;

                case 16:
                    s = "WingLMid";
                    break;

                case 17:
                    s = "WingRMid";
                    break;

                case 18:
                    s = "WingLIn";
                    break;

                case 19:
                    s = "WingRIn";
                    break;

                default:
                    s = "CF";
                    break;
            }
            s = s + "_D0";
            ((Aircraft) this.actor).msgCollision(this.actor, s, s);
        }
    }

    private void cutWing() {
        if (this.Realism.Flutter_Effect) {
            String s;
            switch (World.Rnd().nextInt(0, 8)) {
                case 0:
                    s = "Tail1";
                    break;

                case 1:
                case 2:
                    s = "WingRMid";
                    break;

                case 3:
                case 4:
                    s = "WingLMid";
                    break;

                case 5:
                case 6:
                    s = "WingLIn";
                    break;

                default:
                    s = "WingRIn";
                    break;
            }
            s = s + "_D0";
            ((Aircraft) this.actor).msgCollision(this.actor, s, s);
        }
    }

    private void cutPart(int i) {
        if (this.Realism.Flutter_Effect) {
            String s;
            switch (i) {
                case 0:
                    s = "WingLOut";
                    break;

                case 1:
                    s = "WingLMid";
                    break;

                case 2:
                    s = "WingLIn";
                    break;

                case 3:
                    s = "WingRIn";
                    break;

                case 4:
                    s = "WingRMid";
                    break;

                case 5:
                    s = "WingROut";
                    break;

                case 6:
                    s = "Tail1";
                    break;

                default:
                    s = "Tail1";
                    break;
            }
            s = s + "_D0";
            ((Aircraft) this.actor).msgCollision(this.actor, s, s);
        }
    }

    private void dangerEM() {
        if (Time.tickCounter() < this.lastDangerTick + 1L) return;
        this.lastDangerTick = Time.tickCounter();
        Actor actor = War.GetNearestEnemy(this.actor, -1, 700F);
        if (!(actor instanceof Aircraft)) return;
        Aircraft aircraft = (Aircraft) actor;
        this.TmpVd.set(aircraft.FM.Loc);
        this.TmpVd.sub(this.Loc);
        this.Or.transformInv(this.TmpVd);
        this.TmpVd.normalize();
        if (this.TmpVd.x < 0.98D) return;
        if (!(aircraft.FM instanceof Pilot)) return;
        else {
            Pilot pilot = (Pilot) aircraft.FM;
            pilot.setAsDanger(this.actor);
            return;
        }
    }

    private void dangerEMAces() {
        Actor actor = War.GetNearestEnemy(this.actor, -1, 300F);
        if (!(actor instanceof Aircraft)) return;
        Aircraft aircraft = (Aircraft) actor;
        this.TmpVd.set(aircraft.FM.Loc);
        this.TmpVd.sub(this.Loc);
        this.Or.transformInv(this.TmpVd);
        this.TmpVd.normalize();
        if (this.TmpV.x < 0.98D) return;
        if (!(aircraft.FM instanceof Pilot)) return;
        else {
            Pilot pilot = (Pilot) aircraft.FM;
            pilot.setAsDanger(this.actor);
            return;
        }
    }

//    private float MulForce(float f)
//    {
//        if(f < 40F || f > 180F)
//            return 1.0F;
//        else
//            return 1.0F + (70F - Math.abs(f - 110F)) * 0.04F;
//    }

    public boolean isRealMode() {
        return this.RealMode;
    }

    public void setRealMode(boolean flag) {
        if (this.RealMode == flag) return;
        this.RealMode = flag;
        if (this.RealMode) this.AP.setStabAll(false);
    }

    private void checkAirborneState() {
        if (World.getPlayerFM() != this) return;
        if (!Actor.isAlive(this.actor)) return;
        switch (this.airborneState) {
            default:
                break;

            case 0:
                if (this.getAltitude() - Engine.land().HQ_Air(this.Loc.x, this.Loc.y) > 40D) {
                    this.airborneState = 2;
                    this.setWasAirborne(true);
                    this.setStationedOnGround(false);
                    EventLog.onAirInflight((Aircraft) this.actor);
                    // +++ By SAS~Storebror: Enter Cockpit if External Views are allowed on ground only! +++
                    if (this.actor == World.getPlayerAircraft() && Config.isUSE_RENDER()) {
                        Main3D.cur3D().aircraftHotKeys.checkViewOnAirborne();
                    }
                    // --- By SAS~Storebror: Enter Cockpit if External Views are allowed on ground only! ---
                    if (!Mission.hasRadioStations) CmdEnv.top().exec("music RAND music/inflight");
                } else {
                    this.airborneState = 1;
                    this.setStationedOnGround(true);
                    if (!Mission.hasRadioStations) CmdEnv.top().exec("music RAND music/takeoff");
                }
                this.setCrossCountry(false);
                break;

            case 1:
                if (this.Vrel.length() > this.Vmin) this.setStationedOnGround(false);
                if (this.getAltitude() - Engine.land().HQ_Air(this.Loc.x, this.Loc.y) <= 40D || this.Vrel.length() <= this.Vmin * 1.15F) break;
                this.airborneState = 2;
                this.setStationedOnGround(false);
                this.setNearAirdrome(false);
                this.setWasAirborne(true);
                this.airborneStartPoint.set(this.Loc);
                World.cur().scoreCounter.playerTakeoff();
                EventLog.onAirInflight((Aircraft) this.actor);
                // +++ By SAS~Storebror: Enter Cockpit if External Views are allowed on ground only! +++
                if (this.actor == World.getPlayerAircraft() && Config.isUSE_RENDER()) {
                    Main3D.cur3D().aircraftHotKeys.checkViewOnAirborne();
                }
                // --- By SAS~Storebror: Enter Cockpit if External Views are allowed on ground only! ---
                if (!Mission.hasRadioStations) CmdEnv.top().exec("music RAND music/inflight");
                break;

            case 2:
                if (!this.isCrossCountry() && this.Loc.distance(this.airborneStartPoint) > 50000D) {
                    this.setCrossCountry(true);
                    World.cur().scoreCounter.playerDoCrossCountry();
                }
                if (!this.Gears.onGround || this.Vrel.length() >= 1.0D) break;
                this.airborneState = 1;
                this.setStationedOnGround(true);
                if (!Mission.hasRadioStations) CmdEnv.top().exec("music RAND music/takeoff");
                if (Airport.distToNearestAirport(this.Loc) > 1500D) {
                    World.cur().scoreCounter.playerLanding(true);
                    this.setNearAirdrome(false);
                } else {
                    World.cur().scoreCounter.playerLanding(false);
                    this.setNearAirdrome(true);
                }
                break;
        }
        Mission.initRadioSounds();
    }

    private void initSound(Actor actor) {
        this.structuralFX = ((Aircraft) actor).newSound("models.structuralFX", false);
        this.setSound(false);
    }

    private void setSound(boolean flag) {
        this.bSound = flag;
    }

    private boolean getSound() {
        return this.bSound;
    }

    public void update(float f) {
        if (this.actor.isNetMirror()) {
            ((NetAircraft.Mirror) this.actor.net).fmUpdate(f);
            return;
        }
        if (this.getSound()) this.initSound(this.actor);
        this.V2 = (float) this.Vflow.lengthSquared();
        this.V = (float) Math.sqrt(this.V2);
        if (this.V * f > 5F) {
            this.update(f * 0.5F);
            this.update(f * 0.5F);
            return;
        }
//        float f3 = 0.0F;
//        float f4 = 0.0F;
//        float f5 = 0.0F;
        if (!this.RealMode) {
            this.shakeLevel = 0.0F;
            super.update(f);
            if (this.isTick(44, 0)) this.checkAirborneState();
            if (World.cur().diffCur.Blackouts_N_Redouts) this.calcOverLoad(f, false);
            this.producedAM.set(0.0D, 0.0D, 0.0D);
            this.producedAF.set(0.0D, 0.0D, 0.0D);
            return;
        }
        this.moveCarrier();
        this.decDangerAggressiveness();
        if (this.Loc.z < -20D) ((Aircraft) this.actor).postEndAction(0.0D, this.actor, 4, null);
        if (!this.isOk() && this.Group != null) this.Group.delAircraft((Aircraft) this.actor);
        if (Config.isUSE_RENDER() && Maneuver.showFM && this.actor == Main3D.cur3D().viewActor()) {
            float f6 = (float) this.W.x / (this.CT.getAileron() * 111.111F * this.SensRoll) * this.Sq.squareWing / 0.8F;
            if (Math.abs(f6) > 50F) f6 = 0.0F;
            float f8 = (float) this.W.y / (-this.CT.getElevator() * 111.111F * this.SensPitch) * this.Sq.squareWing / 0.27F;
            if (Math.abs(f8) > 50F) f8 = 0.0F;
            float f10 = (float) this.W.z / ((this.AOS - this.CT.getRudder() * 12F) * 111.111F * this.SensYaw) * this.Sq.squareWing / 0.15F;
            if (Math.abs(f10) > 50F) f10 = 0.0F;
            TextScr.output(5, 60, "~S RUDDR = " + (int) (f10 * 100F) / 100F);
            TextScr.output(5, 80, "~S VATOR = " + (int) (f8 * 100F) / 100F);
            TextScr.output(5, 100, "~S AERON = " + (int) (f6 * 100F) / 100F);
            String s = "";
            for (int i = 0; i < this.shakeLevel * 10.5F; i++)
                s = s + ">";

            TextScr.output(5, 120, "SHAKE LVL -" + this.shakeLevel);
            TextScr.output(5, 670, "Pylon = " + this.M.pylonCoeff);
            TextScr.output(5, 640, "WIND = " + (int) (this.Vwind.length() * 10D) / 10F + " " + (int) (this.Vwind.z * 10D) / 10F + " m/s");
            TextScr.output(5, 140, "BRAKE = " + this.CT.getBrake());
            int j = 0;
            TextScr.output(225, 140, "---ENGINES (" + this.EI.getNum() + ")---" + this.EI.engines[j].getStage());
            TextScr.output(245, 120, "THTL " + (int) (100F * this.EI.engines[j].getControlThrottle()) + "%" + (this.EI.engines[j].getControlAfterburner() ? " (NITROS)" : ""));
            TextScr.output(245, 100, "PROP " + (int) (100F * this.EI.engines[j].getControlProp()) + "%" + (this.CT.getStepControlAuto() ? " (AUTO)" : ""));
            TextScr.output(245, 80, "MIX " + (int) (100F * this.EI.engines[j].getControlMix()) + "%");
            TextScr.output(245, 60, "RAD " + (int) (100F * this.EI.engines[j].getControlRadiator()) + "%" + (this.CT.getRadiatorControlAuto() ? " (AUTO)" : ""));
            TextScr.output(245, 40, "SUPC " + this.EI.engines[j].getControlCompressor() + "x");
            TextScr.output(245, 20, "PropAoA :" + (int) Math.toDegrees(this.EI.engines[j].getPropAoA()));
            TextScr.output(245, 0, "PropPhi :" + (int) Math.toDegrees(this.EI.engines[j].getPropPhi()));
            TextScr.output(455, 120, "Cyls/Cams " + this.EI.engines[j].getCylindersOperable() + "/" + this.EI.engines[j].getCylinders());
            TextScr.output(455, 100, "Readyness " + (int) (100F * this.EI.engines[j].getReadyness()) + "%");
            TextScr.output(455, 80, "PRM " + (int) ((int) (this.EI.engines[j].getRPM() * 0.02F) * 50F) + " rpm");
            TextScr.output(455, 60, "Thrust " + (int) ((Tuple3f) this.EI.engines[j].getEngineForce()).x + " N");
            TextScr.output(455, 40, "Fuel " + (int) (100F * this.M.fuel / this.M.maxFuel) + "% Nitro " + (int) (100F * this.M.nitro / this.M.maxNitro) + "%");
            TextScr.output(455, 20, "MPrs " + (int) (1000F * this.EI.engines[j].getManifoldPressure()) + " mBar");
            TextScr.output(640, 140, "---Controls---");
            TextScr.output(640, 120, "A/C: " + (this.CT.bHasAileronControl ? "" : "AIL ") + (this.CT.bHasElevatorControl ? "" : "ELEV ") + (this.CT.bHasRudderControl ? "" : "RUD ") + (this.Gears.bIsHydroOperable ? "" : "GEAR "));
            TextScr.output(640, 100, "ENG: " + (this.EI.engines[j].isHasControlThrottle() ? "" : "THTL ") + (this.EI.engines[j].isHasControlProp() ? "" : "PROP ") + (this.EI.engines[j].isHasControlMix() ? "" : "MIX ")
                    + (this.EI.engines[j].isHasControlCompressor() ? "" : "SUPC ") + (this.EI.engines[j].isPropAngleDeviceOperational() ? "" : "GVRNR "));
            TextScr.output(640, 80, "PIL: (" + (int) (this.AS.getPilotHealth(0) * 100F) + "%)");
            TextScr.output(640, 60, "Sens: " + this.CT.Sensitivity);
            TextScr.output(400, 500, "+");
            TextScr.output(400, 400, "|");
            TextScr.output((int) (400F + 200F * this.CT.AileronControl), (int) (500F - 200F * this.CT.ElevatorControl), "+");
            TextScr.output((int) (400F + 200F * this.CT.RudderControl), 400, "|");
            TextScr.output(5, 200, "AOA = " + this.AOA);
            TextScr.output(5, 220, "Mass = " + this.M.getFullMass());
            TextScr.output(5, 320, "AERON TR = " + this.CT.trimAileron);
            TextScr.output(5, 300, "VATOR TR = " + this.CT.trimElevator);
            TextScr.output(5, 280, "RUDDR TR = " + this.CT.trimRudder);
            TextScr.output(245, 160, " pF = " + this.EI.engines[0].zatizeni * 100D + "%/hr");
            this.hpOld = this.hpOld * 0.95F + 0.05F * this.EI.engines[0].w * this.EI.engines[0].engineMoment / 746F;
            TextScr.output(245, 180, " hp = " + this.hpOld);
            TextScr.output(245, 200, " eMoment = " + this.EI.engines[0].engineMoment);
            TextScr.output(245, 220, " pMoment = " + this.EI.engines[0].propMoment);
        }
        if (!this.Realism.Limited_Fuel) this.superFuel = this.M.fuel = Math.max(this.superFuel, this.M.fuel);
        this.AP.update(f);
        ((Aircraft) this.actor).netUpdateWayPoint();
        this.CT.update(f, (float) this.Vflow.x, this.EI, true);
        float f7 = (float) (this.Vflow.x * this.Vflow.x) / 11000F;
        if (f7 > 1.0F) f7 = 1.0F;
        ForceFeedback.fxSetSpringGain(f7);
        if (this.CT.saveWeaponControl[0] || this.CT.saveWeaponControl[1] || this.CT.saveWeaponControl[2]) this.dangerEM();
        this.Wing.setFlaps(this.CT.getFlap());
        this.FMupdate(f);
        this.EI.update(f);
        this.Gravity = this.M.getFullMass() * Atmosphere.g();
        this.M.computeFullJ(this.J, this.J0);
        if (this.Realism.G_Limits) {
            if (this.G_ClassCoeff < 0.0F || !((Aircraft) this.actor instanceof TypeBomber)) this.Current_G_Limit = this.ReferenceForce / this.M.getFullMass() - this.M.pylonCoeff;
            else this.Current_G_Limit = this.ReferenceForce / this.M.getFullMass();
            this.setLimitLoad(this.Current_G_Limit);
        }
        if (this.isTick(44, 0)) {
            this.AS.update(f * 44F);
            ((Aircraft) this.actor).rareAction(f * 44F, true);
            this.M.computeParasiteMass(this.CT.Weapons);
            this.Sq.computeParasiteDrag(this.CT, this.CT.Weapons);
            this.checkAirborneState();
            this.putScareShpere();
            this.dangerEMAces();
            if (this.turnOffCollisions && !this.Gears.onGround && this.getAltitude() - Engine.land().HQ_Air(this.Loc.x, this.Loc.y) > 30D) this.turnOffCollisions = false;
        }
        this.Or.wrap();
        if (this.Realism.Wind_N_Turbulence) World.wind().getVector(this.Loc, this.Vwind);
        else this.Vwind.set(0.0D, 0.0D, 0.0D);
        this.Vair.sub(this.Vwld, this.Vwind);
        this.Or.transformInv(this.Vair, this.Vflow);
        this.Density = Atmosphere.density((float) this.Loc.z);
        this.AOA = FMMath.RAD2DEG(-(float) Math.atan2(this.Vflow.z, this.Vflow.x));
        this.AOS = FMMath.RAD2DEG((float) Math.atan2(this.Vflow.y, this.Vflow.x));
        this.indSpeed = this.getSpeed() * (float) Math.sqrt(this.Density / 1.225F);
        this.Mach = this.V / Atmosphere.sonicSpeed((float) this.Loc.z);
        float fDragFactor = 1.0F;
        float fDragParasiteFactor = 1.0F;
        if (this.Ss.allParamsSet) {
            float fMachDrag = this.Ss.getDragFactorForMach(this.Mach);
            fDragFactor = (float) Math.sqrt(fMachDrag);
            fDragParasiteFactor = (float) Math.pow(fMachDrag, 5D);
        }// else
//        if(this.Ss.getDragFactorForMach(this.Mach) <= 1.0F);
            if (this.Mach > 0.8F) this.Mach = 0.8F;
        this.Kq = 1.0F / (float) Math.sqrt(1.0F - this.Mach * this.Mach);
        this.q_ = this.Density * this.V2 * 0.5F;
        double d1 = this.Loc.z - this.Gears.screenHQ;
        if (d1 < 0.0D) d1 = 0.0D;
        float f1 = this.CT.getAileron() * 14F;
        f1 = this.Arms.WING_V * (float) Math.sin(FMMath.DEG2RAD(this.AOS)) + this.SensRoll * this.ailerInfluence * (1.0F - 0.1F * this.CT.getFlap()) * f1;
        double d2 = 0.0D;
        double d4 = 0.0D;
        if (this.EI.engines[0].getType() < 2) {
            d2 = this.EI.engines[0].addVflow;
            if (this.Realism.Torque_N_Gyro_Effects) d4 = 0.5D * this.EI.engines[0].addVside;
        }
        this.Vn.set(-this.Arms.GCENTER, 0.85D * this.Arms.WING_END, -0.5D);
        this.Vn.cross(this.W, this.Vn);
        this.Vn.add(this.Vflow);
        float f12 = f1 - FMMath.RAD2DEG((float) Math.atan2(this.Vn.z, this.Vn.x));
        this.Vn.x += 0.07D * d2;
        double d = this.Vn.lengthSquared();
        d *= 0.5F * this.Density;
        f7 = f1 - FMMath.RAD2DEG((float) Math.atan2(this.Vn.z + 0.07D * d4 * this.EI.getPropDirSign(), this.Vn.x));
        float f14 = 0.015F * f1;
        if (f14 < 0.0F) f14 *= 0.18F;
        this.Cwl.x = -d * (this.Wing.new_Cx(f7) + f14 + this.GearCX * this.CT.getGear() + this.radiatorCX * (this.EI.getRadiatorPos() + this.CT.getCockpitDoor()) + this.Sq.dragAirbrakeCx * this.CT.getAirBrake());
        this.Cwl.z = d * this.Wing.new_Cy(f7) * this.Kq;
        if (this.fmsfxCurrentType != 0) {
            if (this.fmsfxCurrentType == 1) this.Cwl.z *= Aircraft.cvt(this.fmsfxPrevValue, 0.003F, 0.8F, 1.0F, 0.0F);
            if (this.fmsfxCurrentType == 2) {
                this.Cwl.z = 0.0D;
                if (Time.current() >= this.fmsfxTimeDisable) this.doRequestFMSFX(0, 0);
            }
        }
        this.Vn.set(-this.Arms.GCENTER, -this.Arms.WING_END, -0.5D);
        this.Vn.cross(this.W, this.Vn);
        this.Vn.add(this.Vflow);
        float f13 = -f1 - FMMath.RAD2DEG((float) Math.atan2(this.Vn.z, this.Vn.x));
        this.Vn.x += 0.07D * d2;
        d = this.Vn.lengthSquared();
        d *= 0.5F * this.Density;
        float f9 = -f1 - FMMath.RAD2DEG((float) Math.atan2(this.Vn.z - 0.07D * d4 * this.EI.getPropDirSign(), this.Vn.x));
        f14 = -0.015F * f1;
        if (f14 < 0.0F) f14 *= 0.18F;
        this.Cwr.x = -d * (this.Wing.new_Cx(f9) + f14 + this.GearCX * this.CT.getGear() + this.radiatorCX * this.EI.getRadiatorPos() + this.Sq.dragAirbrakeCx * this.CT.getAirBrake());
        this.Cwr.z = d * this.Wing.new_Cy(f9) * this.Kq;
        if (this.fmsfxCurrentType != 0) {
            if (this.fmsfxCurrentType == 1) this.Cwr.z *= Aircraft.cvt(this.fmsfxPrevValue, 0.003F, 0.8F, 1.0F, 0.0F);
            if (this.fmsfxCurrentType == 3) {
                this.Cwr.z = 0.0D;
                if (Time.current() >= this.fmsfxTimeDisable) this.doRequestFMSFX(0, 0);
            }
        }
        this.Cwl.y = -d * this.Fusel.new_Cy(this.AOS);
        this.Cwl.x -= d * this.Fusel.new_Cx(this.AOS);
        this.Cwr.y = -d * this.Fusel.new_Cy(this.AOS);
        this.Cwr.x -= d * this.Fusel.new_Cx(this.AOS);
        float f15 = this.Wing.get_AOA_CRYT();
        double d7 = 1.0D;
        double d8 = 0.5D + 0.4D * this.EI.getPowerOutput();
        double d9 = 1.2D + 0.4D * this.EI.getPowerOutput();
        if (this.spinCoeff < d8) this.spinCoeff = d8;
        if (this.spinCoeff > d9) this.spinCoeff = d9;
        f7 = f12;
        f9 = f13;
        if (!this.Realism.Stalls_N_Spins || this.Gears.isUnderDeck()) {
            if (f7 > f9) {
                if (this.Cwl.z < this.Cwr.z) {
                    double d5 = this.Cwl.z;
                    this.Cwl.z = this.Cwr.z;
                    this.Cwr.z = d5;
                }
            } else if (this.Cwl.z > this.Cwr.z) {
                double d6 = this.Cwl.z;
                this.Cwl.z = this.Cwr.z;
                this.Cwr.z = d6;
            }
        } else if (f7 > f15 || f9 > f15) {
            this.spinCoeff += 0.2D * f;
            if (this.Sq.squareRudders > 0.0D && Math.abs(this.CT.RudderControl) > 0.5D && this.CT.RudderControl * this.W.z > 0.0D) this.spinCoeff -= 0.3D * f;
            float f16;
            if (f7 > f9) f16 = f7;
            else f16 = f9;
            this.turbCoeff = 0.8F * (f16 - f15);
            if (this.turbCoeff < 1.0F) this.turbCoeff = 1.0F;
            if (this.turbCoeff > 15F) this.turbCoeff = 15F;
            d7 = 1.0D - 0.2D * (f16 - f15);
            if (d7 < 0.2D) d7 = 0.2D;
            d7 /= this.turbCoeff;
            double d12 = d * this.turbCoeff * this.spinCoeff;
            float f17 = this.getAltitude() - (float) Engine.land().HQ_Air(this.Loc.x, this.Loc.y);
            if (f17 < 10F) d12 *= 0.1F * f17;
            if (f7 > f9) {
                this.Cwr.x += 0.02D * d12 * this.Sq.spinCxloss;
                this.Cwl.x -= 0.25D * d12 * this.Sq.spinCxloss;
                this.Cwr.z += 0.02D * d12 * this.Sq.spinCyloss;
                this.Cwl.z -= 0.1D * d12 * this.Sq.spinCyloss;
            } else {
                this.Cwl.x += 0.02D * d12 * this.Sq.spinCxloss;
                this.Cwr.x -= 0.25D * d12 * this.Sq.spinCxloss;
                this.Cwl.z += 0.02D * d12 * this.Sq.spinCyloss;
                this.Cwr.z -= 0.1D * d12 * this.Sq.spinCyloss;
            }
            this.rudderInfluence = 1.0F + 0.035F * this.turbCoeff;
        } else {
            this.turbCoeff = 1.0F;
            d7 = 1.0D;
            this.spinCoeff -= 0.2D * f;
            this.ailerInfluence = 1.0F;
            this.rudderInfluence = 1.0F;
        }
        if (this.isTick(15, 0)) if (Math.abs(f7 - f9) > 5F) ForceFeedback.fxSetSpringZero((f9 - f7) * 0.04F, 0.0F);
        else ForceFeedback.fxSetSpringZero(0.0F, 0.0F);
        if (d1 < 0.4D * this.Length) {
            double d10 = 1.0D - d1 / (0.4D * this.Length);
            double d13 = 1.0D + 0.2D * d10;
            double d16 = 1.0D + 0.2D * d10;
            this.Cwl.z *= d13;
            this.Cwl.x *= d16;
            this.Cwr.z *= d13;
            this.Cwr.x *= d16;
        }
        f1 = this.CT.getElevator() * (this.CT.getElevator() > 0.0F ? 28F : 20F);
        this.Vn.set(-this.Arms.VER_STAB, 0.0D, 0.0D);
        this.Vn.cross(this.W, this.Vn);
        this.Vn.add(this.Vflow);
        double d11 = Math.sqrt(this.Vn.y * this.Vn.y + this.Vn.z * this.Vn.z);
        d2 = 0.0D;
        d4 = 0.0D;
        if (this.EI.engines[0].getType() < 2) {
            double d14 = 1.0D + 0.04D * this.Arms.RUDDER;
            d14 = 1.0D / (d14 * d14);
            double d17 = this.Vn.x + d14 * this.EI.engines[0].addVflow;
            if (d17 < 0.2D) d17 = 0.2D;
            double d19 = 1.0D - 1.5D * d11 / d17;
            if (d19 < 0.0D) d19 = 0.0D;
            double d3 = d19 * d14 * this.EI.engines[0].addVflow;
            this.Vn.x += d3;
            double d21 = Math.min(0.0011D * this.Vn.x * this.Vn.x, 1.0D);
            if (this.Vn.x < 0.0D) d21 = 0.0D;
            if (this.Realism.Torque_N_Gyro_Effects) d4 = d19 * d21 * this.EI.engines[0].addVside;
        }
        double d15 = this.Density * this.Vn.lengthSquared() * 0.5D;
        if (this.EI.getNum() == 1 && this.EI.engines[0].getType() < 2) {
            f7 = -FMMath.RAD2DEG((float) Math.atan2(this.Vn.z - 0.36D * d4 * this.EI.getPropDirSign(), this.Vn.x)) - 2.0F - 0.002F * this.V - this.SensPitch * f1;
            f9 = -FMMath.RAD2DEG((float) Math.atan2(this.Vn.z + 0.36D * d4 * this.EI.getPropDirSign(), this.Vn.x)) - 2.0F - 0.002F * this.V - this.SensPitch * f1;
        } else f7 = f9 = -FMMath.RAD2DEG((float) Math.atan2(this.Vn.z, this.Vn.x)) - 2.0F - 0.002F * this.V - this.SensPitch * f1;
        this.Chl.x = -d15 * this.Tail.new_Cx(f7);
        this.Chl.z = d15 * this.Tail.new_Cy(f7);
        this.Chr.x = -d15 * this.Tail.new_Cx(f9);
        this.Chr.z = d15 * this.Tail.new_Cy(f9);
        this.Chl.y = this.Chr.y = 0.0D;
        f1 = this.CT.getRudder() * (this.Sq.squareRudders < 0.05F ? 0.0F : 28F);
        float f11;
        if (this.EI.engines[0].getType() < 2) f11 = -FMMath.RAD2DEG((float) Math.atan2(this.Vn.y - 0.5D * d4 * this.EI.getPropDirSign(), this.Vn.x)) + this.SensYaw * this.rudderInfluence * f1;
        else f11 = -FMMath.RAD2DEG((float) Math.atan2(this.Vn.y, this.Vn.x)) + this.SensYaw * this.rudderInfluence * f1;
        this.Cv.x = -d15 * this.Tail.new_Cx(f11);
        this.Cv.y = d15 * this.Tail.new_Cy(f11);
        this.Cv.z = 0.0D;
        if (!this.Realism.Stalls_N_Spins) this.Cv.y += ((Tuple3d) this.Cv).y;
        this.Vn.set(this.Vflow);
        d = this.Density * this.Vn.lengthSquared() * 0.5D;
        this.Fwl.scale(this.Sq.liftWingLIn + this.Sq.liftWingLMid + this.Sq.liftWingLOut, this.Cwl);
        this.Fwr.scale(this.Sq.liftWingRIn + this.Sq.liftWingRMid + this.Sq.liftWingROut, this.Cwr);
        this.Fwl.x -= d * (this.Sq.dragParasiteCx * fDragParasiteFactor + this.Sq.dragProducedCx) * 0.5D;
        this.Fwr.x -= d * (this.Sq.dragParasiteCx * fDragParasiteFactor + this.Sq.dragProducedCx) * 0.5D;
        this.Fhl.scale((this.Sq.liftStab + this.Sq.squareElevators) * 0.5F, this.Chl);
        this.Fhr.scale((this.Sq.liftStab + this.Sq.squareElevators) * 0.5F, this.Chr);
        this.Fv.scale(0.2F + this.Sq.liftKeel * 1.5F + this.Sq.squareRudders, this.Cv);
        this.Fwl.x *= fDragFactor;
        this.Fwr.x *= fDragFactor;
        this.Fhl.x *= fDragFactor;
        this.Fhr.x *= fDragFactor;
        this.Fv.x *= fDragFactor;
        this.AF.set(this.Fwl);
        this.AF.add(this.Fwr);
        if (FMMath.isNAN(this.AF)) {
            this.AF.set(0.0D, 0.0D, 0.0D);
            this.flutter();
            if (World.cur().isDebugFM()) System.out.println("AF isNAN");
        } else if (this.AF.length() > this.Gravity * 50F) {
            this.flutter();
            if (World.cur().isDebugFM()) System.out.println("A > 50.0");
            this.AF.normalize();
            this.AF.scale(this.Gravity * 50F);
        } else {
            if (this.Realism.G_Limits) {
                if ((this.getOverload() > this.getUltimateLoad() + World.Rnd().nextFloat(0.0F, 1.0F) || this.getOverload() < this.Negative_G_Limit - World.Rnd().nextFloat(0.0F, 0.5F)) && !this.Gears.onGround() && World.Rnd().nextInt(0, 100) > 98)
                    if (this.cutPart < 0) this.cutWing();
                else this.cutPart(this.cutPart);
                if (this.getOverload() > this.Current_G_Limit || this.getOverload() < this.Negative_G_Limit) {
                    float f19 = Math.abs(this.getOverload());
                    if (f19 > this.max_G_Cycle) this.max_G_Cycle = f19;
                    this.timeCounter += f;
                    if (this.timeCounter > 0.75F) {
                        this.cycleCounter++;
                        if (this.cycleCounter > 1) {
                            float f18;
                            if (this.getOverload() > 1.0F) f18 = (this.max_G_Cycle - this.Current_G_Limit) / this.Current_G_Limit;
                            else f18 = (this.max_G_Cycle + this.Negative_G_Limit) / this.Negative_G_Limit;
                            f18 *= f18;
                            this.setSafetyFactor(f18);
                            if (this.structuralFX != null) this.structuralFX.play();
                            this.VmaxAllowed = this.maxSpeed * (this.getSafetyFactor() * 0.3F + 0.55F);
                            this.rD = World.Rnd().nextFloat();
                            if (this.rD < 0.001F) {
                                if (this.CT.bHasGearControl) {
                                    ((Aircraft) this.actor).msgCollision(this.actor, "GearR2_D0", "GearR2_D0");
                                    this.gearCutCounter++;
                                }
                                this.Wing.CxMin_0 += 6F * this.rD;
                                this.setSafetyFactor(250F * this.rD);
                                this.CT.bHasGearControl = false;
                            } else if (this.rD < 0.002F) {
                                if (this.CT.bHasGearControl) {
                                    ((Aircraft) this.actor).msgCollision(this.actor, "GearL2_D0", "GearL2_D0");
                                    this.gearCutCounter += 2;
                                }
                                this.Wing.CxMin_0 += 3F * this.rD;
                                this.setSafetyFactor(125F * this.rD);
                                this.CT.bHasGearControl = false;
                            } else if (this.rD < 0.0025F) {
                                if (this.CT.bHasGearControl) {
                                    this.CT.GearControl = 1.0F;
                                    ((Aircraft) this.actor).msgCollision(this.actor, "GearL2_D0", "GearL2_D0");
                                    this.CT.forceGear(1.0F);
                                    this.gearCutCounter += 2;
                                }
                                this.Wing.CxMin_0 += 3F * this.rD;
                                this.setSafetyFactor(125F * this.rD);
                                this.CT.bHasGearControl = false;
                            } else if (this.rD < 0.003F) {
                                if (this.CT.bHasGearControl) {
                                    this.CT.GearControl = 1.0F;
                                    ((Aircraft) this.actor).msgCollision(this.actor, "GearR2_D0", "GearR2_D0");
                                    this.CT.forceGear(1.0F);
                                    this.gearCutCounter++;
                                }
                                this.Wing.CxMin_0 += 3F * this.rD;
                                this.setSafetyFactor(125F * this.rD);
                                this.CT.bHasGearControl = false;
                            } else if (this.rD < 0.0035F) {
                                if (this.CT.bHasGearControl) {
                                    this.CT.dvGear = 1.0F;
                                    this.CT.forceGear(1.0F);
                                    this.CT.GearControl = 1.0F;
                                    ((Aircraft) this.actor).msgCollision(this.actor, "GearR2_D0", "GearR2_D0");
                                    ((Aircraft) this.actor).msgCollision(this.actor, "GearL2_D0", "GearL2_D0");
                                    this.gearCutCounter += 3;
                                }
                                this.Wing.CxMin_0 += 8F * this.rD;
                                this.setSafetyFactor(125F * this.rD);
                                this.CT.bHasGearControl = false;
                            } else if (this.rD < 0.04F) this.SensYaw *= 0.68F;
                            else if (this.rD < 0.05F) this.SensPitch *= 0.68F;
                            else if (this.rD < 0.06F) this.SensRoll *= 0.68F;
                            else if (this.rD < 0.061F) this.CT.dropFuelTanks();
                            else if (this.rD < 0.065F) this.CT.bHasFlapsControl = false;
                            else if (this.rD >= 0.5F) if (this.rD < 0.6F) this.Wing.CxMin_0 += 0.011F * this.rD;
                            else if ((int) this.M.getFullMass() % 2 == 0) {
                                this.Sq.getClass();
                                this.Sq.liftWingROut *= 0.95F - 0.2F * this.rD;
                                this.Wing.CxMin_0 += 0.011F * this.rD;
                            } else {
                                this.Sq.getClass();
                                this.Sq.liftWingLOut *= 0.95F - 0.2F * this.rD;
                                this.Wing.CxMin_0 += 0.011F * this.rD;
                            }
                        }
                        this.timeCounter = 0.0F;
                        this.max_G_Cycle = 1.0F;
                    }
                } else {
                    this.timeCounter = 0.0F;
                    this.max_G_Cycle = 1.0F;
                }
            } else if (this.actor instanceof TypeSupersonic && this.getOverload() > 15F && !this.Gears.onGround() && World.Rnd().nextInt(0, 100) > 98) this.cutWing();
            else if (this.getOverload() > 13.5F && !this.Gears.onGround() && World.Rnd().nextInt(0, 100) > 98) this.cutWing();
            // TODO: Added by SAS~Storebror: +++ Additional parameter for max flaps & gear speeds +++
            if (this.indSpeed > this.getvMaxGear() && World.Rnd().nextInt(0, 100) > 98 && this.CT.getGear() > 0.3F && this.CT.GearControl == 1.0F) {
            //if (this.indSpeed > 112.5F && World.Rnd().nextInt(0, 100) > 98 && this.CT.getGear() > 0.3F && this.CT.GearControl == 1.0F) {
            // TODO: Added by SAS~Storebror: --- Additional parameter for max flaps & gear speeds ---
                if (this.CT.getGear() >= 0.1F && this.CT.GearControl != 0.0F && !this.bGearCut) if (!(this.actor instanceof F4U) && !(this.actor instanceof TypeSupersonic)) {
                    if (World.Rnd().nextInt(0, 100) > 76 && this.gearCutCounter != 1) {
                        ((Aircraft) this.actor).msgCollision(this.actor, "GearR2_D0", "GearR2_D0");
                        this.gearCutCounter++;
                    }
                    if (World.Rnd().nextInt(0, 100) > 76 && this.gearCutCounter != 2) {
                        ((Aircraft) this.actor).msgCollision(this.actor, "GearL2_D0", "GearL2_D0");
                        this.gearCutCounter += 2;
                    }
                } else if (this.indSpeed > 180F && !(this.actor instanceof TypeSupersonic)) {
                    if (World.Rnd().nextInt(0, 100) > 76 && this.gearCutCounter != 1) {
                        ((Aircraft) this.actor).msgCollision(this.actor, "GearR2_D0", "GearR2_D0");
                        this.gearCutCounter++;
                    }
                    if (World.Rnd().nextInt(0, 100) > 76 && this.gearCutCounter != 2) {
                        ((Aircraft) this.actor).msgCollision(this.actor, "GearL2_D0", "GearL2_D0");
                        this.gearCutCounter += 2;
                    }
                }
                if (this.indSpeed > 350F && !(this.actor instanceof TypeSupersonic)) {
                    if (World.Rnd().nextInt(0, 100) > 76 && this.gearCutCounter != 1) {
                        ((Aircraft) this.actor).msgCollision(this.actor, "GearR2_D0", "GearR2_D0");
                        this.gearCutCounter++;
                    }
                    if (World.Rnd().nextInt(0, 100) > 76 && this.gearCutCounter != 2) {
                        ((Aircraft) this.actor).msgCollision(this.actor, "GearL2_D0", "GearL2_D0");
                        this.gearCutCounter += 2;
                    }
                }
                if (this.gearCutCounter > 2) {
                    this.bGearCut = true;
                    this.CT.bHasGearControl = false;
                }
            }
            if (this.indSpeed > 60.5F && this.CT.getWing() > 0.1F) {
                if (World.Rnd().nextInt(0, 100) > 90 && ((Aircraft) this.actor).isChunkAnyDamageVisible("WingLMid")) ((Aircraft) this.actor).msgCollision(this.actor, "WingLMid_D0", "WingLMid_D0");
                if (World.Rnd().nextInt(0, 100) > 90 && ((Aircraft) this.actor).isChunkAnyDamageVisible("WingRMid")) ((Aircraft) this.actor).msgCollision(this.actor, "WingRMid_D0", "WingRMid_D0");
            }
            // TODO: Added by SAS~Storebror: +++ Additional parameter for max flaps & gear speeds +++
            if (!(this.actor instanceof TypeSupersonic) && this.indSpeed > this.getvJamFlaps() && this.CT.bHasFlapsControl && this.CT.FlapsControl > 0.21F && (this.indSpeed - this.getvJamFlaps()) * this.CT.getFlap() > 8F) {
            //if (!(this.actor instanceof TypeSupersonic) && this.indSpeed > 81F && this.CT.bHasFlapsControl && this.CT.FlapsControl > 0.21F && (this.indSpeed - 81F) * this.CT.getFlap() > 8F) {
            // TODO: Added by SAS~Storebror: --- Additional parameter for max flaps & gear speeds ---
                if (World.getPlayerAircraft() == this.actor && this.CT.bHasFlapsControl) HUD.log("FailedFlaps");
                this.CT.bHasFlapsControl = false;
                this.CT.FlapsControl = 0.0F;
            }
            if (this.indSpeed > this.VmaxAllowed && World.Rnd().nextFloat(0.0F, 16F) < this.indSpeed - this.VmaxAllowed && World.Rnd().nextInt(0, 99) < 2) this.flutterDamage();
            if (!(this.actor instanceof TypeSupersonic)) {
                if (this.indSpeed > 610F) {
                    if (World.cur().isDebugFM()) System.out.println("*** Sonic overspeed....");
                    this.flutter();
                }
            } else if (!(this.actor instanceof TypeSupersonic) && this.indSpeed > 310F) {
                if (World.cur().isDebugFM()) System.out.println("*** Sonic overspeed....");
                this.flutter();
            }
        }
        this.AM.set(0.0D, 0.0D, 0.0D);
        if (Math.abs(this.AOA) < 12F) {
            float f2 = this.Or.getKren();
            if (f2 > 30F) f2 = 30F;
            else if (f2 < -30F) f2 = -30F;
            f2 = (float) (f2 * (Math.min(this.Vflow.x - 50D, 50D) * 0.003D));
            this.AM.add(-f2 * 0.01F * this.Gravity, 0.0D, 0.0D);
        }
        if (!this.getOp(19)) {
            this.AM.y += 8F * this.Sq.squareWing * this.Vflow.x;
            this.AM.z += 200F * this.Sq.squareWing * this.EI.getPropDirSign();
        }
        double d18 = this.CT.getFlap() * 3D;
        if (d18 > 1.0D) d18 = 1.0D;
        double d20 = 0.0111D * Math.abs(this.AOA);
        if (this.Wing.AOACritL < this.AOA && this.AOA < this.Wing.AOACritH) d20 = 0.0D;
        else if (this.AOA >= this.Wing.AOACritH) d20 = Math.min(d20, 0.3D * (this.AOA - this.Wing.AOACritH));
        else if (this.Wing.AOACritL <= this.AOA) d20 = Math.min(d20, 0.3D * (this.Wing.AOACritL - this.AOA));
        double d22 = this.Arms.GCENTER + this.Arms.GC_FLAPS_SHIFT * d18 * (1.0D - d20) + this.Arms.GC_AOA_SHIFT * d20;
        this.TmpV.set(-d22, this.Arms.WING_MIDDLE * (1.3D + 1.0D * Math.sin(FMMath.DEG2RAD(this.AOS))), -this.Arms.GCENTER_Z);
        this.TmpV.cross(this.TmpV, this.Fwl);
        this.AM.add(this.TmpV);
        this.TmpV.set(-d22, -this.Arms.WING_MIDDLE * (1.3D - 1.0D * Math.sin(FMMath.DEG2RAD(this.AOS))), -this.Arms.GCENTER_Z);
        this.TmpV.cross(this.TmpV, this.Fwr);
        this.AM.add(this.TmpV);
        this.AM.x += this.su26add;
        this.TmpV.set(-this.Arms.HOR_STAB, 1.0D, 0.0D);
        this.TmpV.cross(this.TmpV, this.Fhl);
        this.AM.add(this.TmpV);
        this.TmpV.set(-this.Arms.HOR_STAB, -1D, 0.0D);
        this.TmpV.cross(this.TmpV, this.Fhr);
        this.AM.add(this.TmpV);
        this.TmpV.set(-this.Arms.VER_STAB, 0.0D, 1.0D);
        this.TmpV.cross(this.TmpV, this.Fv);
        this.AM.add(this.TmpV);
        double d23 = 1.0D - 0.00001D * this.indSpeed;
        if (d23 < 0.8D) d23 = 0.8D;
        this.W.scale(d23);
        if (!this.Realism.Stalls_N_Spins) this.AM.y += ((Tuple3d) this.AF).z * 0.5D * Math.sin(FMMath.DEG2RAD(Math.abs(this.AOA)));
        if (this.W.lengthSquared() > 25D) this.W.scale(5D / this.W.length());
        if (!this.Realism.Stalls_N_Spins && this.Vflow.x > 20D) this.W.z += this.AOS * f;
        this.AF.add(this.producedAF);
        this.AM.add(this.producedAM);
        this.producedAF.set(0.0D, 0.0D, 0.0D);
        this.producedAM.set(0.0D, 0.0D, 0.0D);
        this.AF.add(this.EI.producedF);
        this.AM.add(this.EI.producedM);
        if (World.cur().diffCur.Torque_N_Gyro_Effects) {
            this.GM.set(this.EI.getGyro());
            this.GM.scale(d7);
            this.AM.add(this.GM);
        }
        this.GF.set(0.0D, 0.0D, 0.0D);
        this.GM.set(0.0D, 0.0D, 0.0D);
        if (Time.tickCounter() % 2 != 0) this.Gears.roughness = this.Gears.plateFriction(this);
        this.Gears.ground(this, true);
        int k = 5;
        if (this.GF.lengthSquared() == 0.0D && this.GM.lengthSquared() == 0.0D) k = 1;
        this.SummF.add(this.AF, this.GF);
        this.ACmeter.set(this.SummF);
        this.ACmeter.scale(1.0F / this.Gravity);
        this.TmpV.set(0.0D, 0.0D, -this.Gravity);
        this.Or.transformInv(this.TmpV);
        this.GF.add(this.TmpV);
        this.SummF.add(this.AF, this.GF);
        this.SummM.add(this.AM, this.GM);
        double d24 = 1.0D / this.M.mass;
        this.LocalAccel.scale(d24, this.SummF);
        if (Math.abs(this.getRollAcceleration()) > 50000.5F) {
            ForceFeedback.fxPunch(this.SummM.x > 0.0D ? 0.9F : -0.9F, 0.0F, 1.0F);
            if (World.cur().isDebugFM()) System.out.println("Punched (Axial = " + this.SummM.x + ")");
        }
        if (Math.abs(this.getOverload() - this.lastAcc) > 0.5F) {
            ForceFeedback.fxPunch(World.Rnd().nextFloat(-0.5F, 0.5F), -0.9F, this.getSpeed() * 0.05F);
            if (World.cur().isDebugFM()) System.out.println("Punched (Lat = " + Math.abs(this.getOverload() - this.lastAcc) + ")");
        }
        this.lastAcc = this.getOverload();
        if (FMMath.isNAN(this.AM)) {
            this.AM.set(0.0D, 0.0D, 0.0D);
            this.flutter();
            if (World.cur().isDebugFM()) System.out.println("AM isNAN");
        } else if (this.AM.length() > this.Gravity * 150F) {
            this.flutter();
            if (World.cur().isDebugFM()) System.out.println("SummM > 150g");
            this.AM.normalize();
            this.AM.scale(this.Gravity * 150F);
        }
        this.dryFriction -= 0.01D;
        if (this.Gears.gearsChanged) this.dryFriction = 1.0F;
        if (this.Gears.nOfPoiOnGr > 0) this.dryFriction += 0.02F;
        if (this.dryFriction < 1.0F) this.dryFriction = 1.0F;
        if (this.dryFriction > 32F) this.dryFriction = 32F;
        float f20 = 4F * (0.25F - this.EI.getPowerOutput());
        if (f20 < 0.0F) f20 = 0.0F;
        f20 *= f20;
        f20 *= this.dryFriction;
        float f21 = f20 * this.M.mass * this.M.mass;
        if (!this.brakeShoe && (this.Gears.nOfPoiOnGr == 0 && this.Gears.nOfGearsOnGr < 3 || f20 == 0.0F || this.SummM.lengthSquared() > 2.0F * f21 || this.SummF.lengthSquared() > 80F * f21 || this.W.lengthSquared() > 0.00014F * f20
                || this.Vwld.lengthSquared() > 0.09F * f20)) {
            double d25 = 1.0D / k;
            for (int l = 0; l < k; l++) {
                this.SummF.add(this.AF, this.GF);
                this.SummM.add(this.AM, this.GM);
                this.AW.x = ((this.J.y - this.J.z) * this.W.y * this.W.z + this.SummM.x) / this.J.x;
                this.AW.y = ((this.J.z - this.J.x) * this.W.z * this.W.x + this.SummM.y) / this.J.y;
                this.AW.z = ((this.J.x - this.J.y) * this.W.x * this.W.y + this.SummM.z) / this.J.z;
                this.TmpV.scale(d25 * f, this.AW);
                this.W.add(this.TmpV);
                this.Or.transform(this.W, this.Vn);
                this.TmpV.scale(d25 * f, this.W);
                this.Or.increment((float) -FMMath.RAD2DEG(this.TmpV.z), (float) -FMMath.RAD2DEG(this.TmpV.y), (float) FMMath.RAD2DEG(this.TmpV.x));
                this.Or.transformInv(this.Vn, this.W);
                this.TmpV.scale(d24, this.SummF);
                this.Or.transform(this.TmpV);
                this.Accel.set(this.TmpV);
                this.TmpV.scale(d25 * f);
                this.Vwld.add(this.TmpV);
                this.TmpV.scale(d25 * f, this.Vwld);
                this.TmpP.set(this.TmpV);
                this.Loc.add(this.TmpP);
                this.GF.set(0.0D, 0.0D, 0.0D);
                this.GM.set(0.0D, 0.0D, 0.0D);
                if (l < k - 1) {
                    this.Gears.ground(this, true);
                    this.TmpV.set(0.0D, 0.0D, -this.Gravity);
                    this.Or.transformInv(this.TmpV);
                    this.GF.add(this.TmpV);
                }
            }

            for (int i1 = 0; i1 < 3; i1++) {
                this.Gears.gWheelAngles[i1] = (this.Gears.gWheelAngles[i1] + (float) Math.toDegrees(Math.atan(this.Gears.gVelocity[i1] * f / 0.375D))) % 360F;
                this.Gears.gVelocity[i1] *= 0.95D;
            }

            this.HM.chunkSetAngles("GearL1_D0", 0.0F, -this.Gears.gWheelAngles[0], 0.0F);
            this.HM.chunkSetAngles("GearR1_D0", 0.0F, -this.Gears.gWheelAngles[1], 0.0F);
            this.HM.chunkSetAngles("GearC1_D0", 0.0F, -this.Gears.gWheelAngles[2], 0.0F);
        }
        if (this.Leader != null && this.isTick(128, 97) && Actor.isAlive(this.Leader.actor) && !this.Gears.onGround) {
            float f22 = (float) this.Loc.distance(this.Leader.Loc);
            if (f22 > 3000F) Voice.speakDeviateBig((Aircraft) this.Leader.actor);
            else if (f22 > 1700F) Voice.speakDeviateSmall((Aircraft) this.Leader.actor);
        }
        this.shakeLevel = 0.0F;
        if (this.Gears.onGround()) this.shakeLevel += 30D * this.Gears.roughness * this.Vrel.length() / this.M.mass;
        else {
            if (this.indSpeed > 10F) {
                float f23 = (float) Math.sin(Math.toRadians(Math.abs(this.AOA)));
                if (f23 > 0.02F) {
                    f23 *= f23;
                    this.shakeLevel += 0.07F * (f23 - 0.0004F) * (this.indSpeed - 10F);
                    if (this.isTick(30, 0) && this.shakeLevel > 0.6F) HUD.log(stallStringID, "Stall");
                }
            }
            if (this.indSpeed > 35F) {
                if (this.CT.bHasGearControl && (this.Gears.lgear || this.Gears.rgear) && this.CT.getGear() > 0.0F) this.shakeLevel += 0.004F * this.CT.getGear() * (this.indSpeed - 35F);
                if (this.CT.getFlap() > 0.0F) this.shakeLevel += 0.004F * this.CT.getFlap() * (this.indSpeed - 35F);
            }
        }
        if (this.indSpeed > this.VmaxAllowed * 0.8F) this.shakeLevel = 0.01F * (this.indSpeed - this.VmaxAllowed * 0.8F);
        if (World.cur().diffCur.Head_Shake) {
            this.shakeLevel += this.producedShakeLevel;
            this.producedShakeLevel *= 0.9F;
        }
        if (this.shakeLevel > 1.0F) this.shakeLevel = 1.0F;
        ForceFeedback.fxShake(this.shakeLevel);
        if (World.cur().diffCur.Blackouts_N_Redouts) this.calcOverLoad(f, true);
    }

    private void calcOverLoad(float f, boolean flag) {
        TypeGSuit.GFactors theGFactors = new TypeGSuit.GFactors();
        float fGPosLimit = 2.3F;
        float fGNegLimit = 2.3F;
        if ((Aircraft) this.actor instanceof TypeGSuit) ((TypeGSuit) this.actor).getGFactors(theGFactors);
        fGPosLimit *= theGFactors.getPosGToleranceFactor();
        fGNegLimit *= theGFactors.getNegGToleranceFactor();
        if (f > 1.0F) f = 1.0F;
        if (this.Gears.onGround() || !flag) this.plAccel.set(0.0D, 0.0D, 0.0D);
        else {
            this.plAccel.set(this.getAccel());
            this.plAccel.scale(0.102D);
        }
        this.plAccel.z += 0.5D;
        this.Or.transformInv(this.plAccel);
        float f1 = -0.5F + (float) ((Tuple3d) this.plAccel).z;
        this.deep = 0.0F;
        if (f1 < -0.6F) this.deep = f1 + 0.6F;
        if (f1 > 2.2F) this.deep = f1 - 2.2F;
        if (this.knockDnTime > 0.0F) this.knockDnTime -= f * theGFactors.getPosGRecoveryFactor();
        if (this.knockUpTime > 0.0F) this.knockUpTime -= f * theGFactors.getNegGRecoveryFactor();
        if (this.indiffDnTime < 4F * theGFactors.getPosGTimeFactor()) this.indiffDnTime += f * theGFactors.getPosGRecoveryFactor();
        if (this.indiffUpTime < 4F * theGFactors.getNegGTimeFactor()) this.indiffUpTime += 0.3F * f * theGFactors.getNegGRecoveryFactor();
        if (this.deep > 0.0F) {
            if (this.indiffDnTime > 0.0F) this.indiffDnTime -= 0.8F * this.deep * f;
            if (this.deep > fGPosLimit && this.knockDnTime < 18.4F) this.knockDnTime += 0.75F * this.deep * f;
            if (this.indiffDnTime > 0.1F) this.currDeep = 0.0F;
            else {
                this.currDeep = this.deep * 0.08F * 3.5F;
                if (this.currDeep > 0.8F) this.currDeep = 0.8F;
            }
        } else if (this.deep < 0.0F) {
            this.deep = -this.deep;
            if (this.deep < 0.84F) this.deep = 0.84F;
            if (this.indiffUpTime > 0.0F) this.indiffUpTime -= 1.2F * this.deep * f;
            if (this.deep > fGNegLimit && this.knockUpTime < 16.1F) this.knockUpTime += this.deep * f;
            if (this.indiffUpTime > 0.1F) this.currDeep = 0.0F;
            else this.currDeep = this.deep * 0.42F * 0.88F;
            this.currDeep = -this.currDeep;
        } else this.currDeep = 0.0F;
        if (this.knockUpTime > 10.81F) this.currDeep = -0.88F;
        if (this.knockDnTime > 14.03F) this.currDeep = 3.5F;
        if (this.currDeep > 3.5F) this.currDeep = 3.5F;
        if (this.currDeep < -0.88F) this.currDeep = -0.88F;
        if (this.saveDeep > 0.8F) {
            this.CT.Sensitivity = 1.0F - (this.saveDeep - 0.8F);
            if (this.CT.Sensitivity < 0.0F) this.CT.Sensitivity = 0.0F;
        } else if (this.saveDeep < -0.4F) {
            this.CT.Sensitivity = 1.0F + (this.saveDeep + 0.4F);
            if (this.CT.Sensitivity < 0.0F) this.CT.Sensitivity = 0.0F;
        } else this.CT.Sensitivity = 1.0F;
        this.CT.Sensitivity *= this.AS.getPilotHealth(0);
        if (this.saveDeep < this.currDeep) {
            this.saveDeep += 0.3F * f;
            if (this.saveDeep > this.currDeep) this.saveDeep = this.currDeep;
        } else {
            this.saveDeep -= 0.2F * f;
            if (this.saveDeep < this.currDeep) this.saveDeep = this.currDeep;
        }
    }

    public void gunMomentum(Vector3d vector3d, boolean flag) {
        this.producedAM.x += vector3d.x;
        this.producedAM.y += vector3d.y;
        this.producedAM.z += vector3d.z;
        float f = (float) vector3d.length() * 0.000035F;
        if (flag && f > 0.5F) f *= 0.05F;
        if (this.producedShakeLevel < f) this.producedShakeLevel = f;
    }

    public boolean            RealMode;
//    public float indSpeed;
    private static int        stallStringID = HUD.makeIdLog();
    public DifficultySettings Realism;
    Vector3d                  Cwl;
    Vector3d                  Cwr;
    Vector3d                  Chl;
    Vector3d                  Chr;
    Vector3d                  Cv;
    Vector3d                  Fwl;
    Vector3d                  Fwr;
    Vector3d                  Fhl;
    Vector3d                  Fhr;
    Vector3d                  Fv;
    private float             superFuel;
    private long              lastDangerTick;
    public float              shakeLevel;
    public float              producedShakeLevel;
    private float             lastAcc;
    private float             ailerInfluence;
    private float             rudderInfluence;
    private float             deep;
    private float             currDeep;
    private float             indiffDnTime;
    private float             knockDnTime;
    private float             indiffUpTime;
    private float             knockUpTime;
    public float              saveDeep;
    private double            su26add;
    private double            spinCoeff;
    private SoundFX           structuralFX;
    private boolean           bSound;
    private float             rD;
    public float              Current_G_Limit;
    private int               cycleCounter;
    private float             timeCounter;
    private int               gearCutCounter;
    private boolean           bGearCut;
    private float             max_G_Cycle;
    private float             maxSpeed;
    private float             hpOld;
    private int               airborneState;
    private Point3d           airborneStartPoint;
    private Point3d           TmpP;
    private Vector3d          Vn;
    private Vector3d          TmpV;
    private Vector3d          TmpVd;
    private Vector3d          plAccel;

}
