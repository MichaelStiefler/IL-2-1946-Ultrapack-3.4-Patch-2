package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorHMesh;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.fm.Motor;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.Wreckage;
import com.maddox.rts.CmdEnv;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sound.AudioStream;
import com.maddox.sound.SoundFX;

public abstract class TA_152NEW extends Scheme1 implements TypeFighter, TypeBNZFighter {

    public TA_152NEW() {
        this.trimElevator = 0.0F;
        this.bHasElevatorControl = true;
        this.boulon = this.newSound("boulon", false);
        this.bouton = this.newSound("fw190_button", false);
        this.arrach = this.newSound("aircraft.arrach", false);
        this.windExtFw = this.newSound("windextfw", false);
        this.doorSndControl = 0.0F;
        this.doorSound = null;
        this.doorPrev = 0.0F;
        this.doorSndPos = null;
//        this.bSmokeEffect = false;
//        if (Config.cur.ini.get("Mods", "SmokeEffect", 0) > 0) {
//            this.bSmokeEffect = true;
//        }
        this.kangle = 0.0F;
        this.k14Mode = 0;
        this.k14WingspanType = 0;
        this.k14Distance = 200F;
        this.bToFire = false;
        this.tX4Prev = 0L;
        this.deltaAzimuth = 0.0F;
        this.deltaTangage = 0.0F;
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
      boolean winter = Config.isUSE_RENDER() && (World.cur().camouflage == 1);
      hierMesh.chunkVisible("GearL5_D0", !winter);
      hierMesh.chunkVisible("GearR5_D0", !winter);
      _WeaponSlot[] weaponSlotsRegistered = Aircraft.getWeaponSlotsRegistered(aircraftClass, thisWeaponsName);
//      System.out.println("TA_152NEW prepareWeapons");
      if (hierMesh.chunkFindCheck("20mmL1_D0") > 0) {
          hierMesh.chunkVisible("20mmL1_D0", weaponSlotsRegistered[1] != null);
      }
      if (hierMesh.chunkFindCheck("20mmR1_D0") > 0) {
          hierMesh.chunkVisible("20mmR1_D0", weaponSlotsRegistered[2] != null);
      }
    }

    public void doMurderPilot(int i) {
        // System.out.println("doMurderPilot(" + i + ")");
        super.doMurderPilot(i);
        switch (i) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                this.hierMesh().chunkVisible("Head1_D1", true);
                this.hierMesh().chunkVisible("pilotarm2_d0", false);
                this.hierMesh().chunkVisible("pilotarm1_d0", false);
                break;
        }
    }

    public void doRemoveBodyFromPlane(int i) {
        // System.out.println("doRemoveBodyFromPlane(" + i + ")");
        super.doRemoveBodyFromPlane(i);
        this.hierMesh().chunkVisible("pilotarm2_d0", false);
        this.hierMesh().chunkVisible("pilotarm1_d0", false);
    }

    public void missionStarting() {
        super.missionStarting();
        this.hierMesh().chunkVisible("pilotarm2_d0", true);
        this.hierMesh().chunkVisible("pilotarm1_d0", true);
    }

    public void prepareCamouflage() {
        super.prepareCamouflage();
        this.hierMesh().chunkVisible("pilotarm2_d0", true);
        this.hierMesh().chunkVisible("pilotarm1_d0", true);
    }

    public void rareAction(float f, boolean flag) {
        // System.out.println("rareAction(" + f + "," + flag + ")");
        super.rareAction(f, flag);
        if (this.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask1_D0", false);
        } else {
            this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
        }
        if (((this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) || !flag || !(this.FM instanceof Pilot)) {
            return;
        }
        Pilot pilot = (Pilot) this.FM;
        if ((pilot.get_maneuver() == Maneuver.ATTACK_BOMBER || (pilot.get_maneuver() == Maneuver.ATTACK && pilot.isMissileValidForFighterAttack())) && pilot.target != null) {
            Point3d point3d = new Point3d(((FlightModelMain) (((Maneuver) (pilot)).target)).Loc);
            point3d.sub(this.FM.Loc);
            this.FM.Or.transformInv(point3d);
            if ((((point3d.x > 4000D) && (point3d.x < 5500D)) || ((point3d.x > 100D) && (point3d.x < 5000D) && (World.Rnd().nextFloat() < 0.33F))) && (Time.current() > (this.tX4Prev + 10000L))) {
                this.bToFire = true;
                this.tX4Prev = Time.current();
            }
        }
    }

    public void moveCockpitDoor(float f) {
        // System.out.println("moveCockpitDoor(" + f + ")");
        this.resetYPRmodifier();
        Aircraft.xyz[0] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, -0.5F);
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if (Aircraft.xyz[0] < -0.3F) {
            this.hierMesh().chunkVisible("Wire_D0", false);
            this.hierMesh().chunkVisible("WireOP_D0", true);
        } else {
            this.hierMesh().chunkVisible("Wire_D0", true);
            this.hierMesh().chunkVisible("WireOP_D0", false);
        }
        Aircraft.xyz[0] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, -0.3F);
        this.hierMesh().chunkSetLocate("Step1_D0", Aircraft.xyz, Aircraft.ypr);
        if (Config.isUSE_RENDER()) {
            if ((Main3D.cur3D().cockpits != null) && (Main3D.cur3D().cockpits[0] != null)) {
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            }
            this.setDoorSndFW(f);
        }
    }

    public void setDoorSndFW(float f) {
        // System.out.println("setDoorSndFW(" + f + ")");
        if (this.FM != null) {
            this.doorSndControl = f;
            if ((this.doorSound == null) && (this.FM.CT.dvCockpitDoor > 0.0F)) {
                byte byte0 = 1;
                float f1 = 1.0F / this.FM.CT.dvCockpitDoor;
                this.doorSndPos = new Point3d(-1D, 0.0D, 0.0D);
                this.doorSound = this.newSound("cockpit.fw", false);
                if (this.doorSound != null) {
                    this.doorSound.setParent(this.getRootFX());
                    if (f1 <= 1.1F) {
                        byte0 = 0;
                    } else if (f1 >= 1.8F) {
                        byte0 = 2;
                    }
                    this.doorSound.setUsrFlag(byte0);
                }
            }
            float f2 = this.FM.EI.engines[0].getRPM();
            if ((((f != 0.0F) && (this.doorPrev == 0.0F)) || ((f != 1.0F) && (this.doorPrev == 1.0F))) && (this.doorSound != null) && (this.FM.CT.dvCockpitDoor < 10F)) {
                this.doorSound.play(this.doorSndPos);
            }
            if ((f != 0.0F) && (this.doorPrev == 0.0F) && (this.doorSound != null) && !this.hierMesh().isChunkVisible("Prop1_D1")) {
                this.windExtFw.start();
            } else if ((f != 1.0F) && (this.doorPrev == 1.0F) && (this.doorSound != null)) {
                this.windExtFw.cancel();
            }
            if (f2 > 100F) {
                this.doorSound.setVolume(1.5F);
            } else {
                this.doorSound.setVolume(0.7F);
            }
            this.doorPrev = f;
        }
    }

    public void EjectCanopy() {
        // System.out.println("EjectCanopy()");
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("CanopyTop", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("Glass", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("RearArmorPlate", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("CanopyFrameL", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("CanopyFrameR", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("Wire_D0", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("WireOP_D0", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("GlassBreather", false);
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
//        this.curAfterBurnerType = this.FM.EI.engines[0].getAfterburnerType();
        this.FM.AS.wantBeaconsNet(true);
        if (this.FM.isPlayers()) {
            this.FM.EI.engines[0].setControlPropAuto(false);
            this.FM.CT.bHasCockpitDoorControl = true;
            this.FM.CT.dvCockpitDoor = 0.2564102F;
            this.hierMesh().chunkVisible("Wire_D0", true);
        }
//        TA_152NEW.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
        if (this.hierMesh().chunkFindCheck("Flap04_D0") >= 0) this.fourFlaps = true;
    }

    public void update(float f) {
        // System.out.println("update(" + f + ")");
        for (int i = 1; i < 15; i++) {
            this.hierMesh().chunkSetAngles("Water" + i + "_D0", 0.0F, -20F * this.kangle, 0.0F);
        }
        this.kangle = (0.95F * this.kangle) + (0.05F * this.FM.EI.engines[0].getControlRadiator());
        
        if ((this.FM.CT.cockpitDoorControl > 0.9F) && (this.FM.getSpeedKMH() > 240F) && !this.FM.Gears.onGround() && (this.FM == World.getPlayerFM()) && (this.hierMesh().chunkFindCheck("Blister1_D0") != -1) && (this.FM.AS.getPilotHealth(0) > 0.0F)) {
            this.arrach.setPlay(true);
            this.EjectCanopy();
            this.hierMesh().hideSubTrees("Blister1_D0");
            Wreckage wreckage = new Wreckage((ActorHMesh) this.FM.actor, this.hierMesh().chunkFind("Blister1_D0"));
            wreckage.collide(true);
            Vector3d vector3d = new Vector3d();
            vector3d.set(this.FM.Vwld);
            wreckage.setSpeed(vector3d);
            this.hierMesh().hideSubTrees("Wire_D0");
            Wreckage wreckage2 = new Wreckage((ActorHMesh) this.FM.actor, this.hierMesh().chunkFind("Wire_D0"));
            wreckage2.collide(true);
            Vector3d vector3d2 = new Vector3d();
            vector3d2.set(this.FM.Vwld);
            wreckage2.setSpeed(vector3d2);
            this.hierMesh().chunkVisible("WireOP_D0", false);
            this.FM.CT.cockpitDoorControl = 0.9F;
            this.FM.CT.bHasCockpitDoorControl = false;
            this.FM.VmaxAllowed = 161F;
            this.FM.Sq.dragEngineCx[0] *= 6.2F;
        }
        super.update(f);
        if (this.FM.EI.engines[0].getControlAfterburner() && (this.FM.getAltitude() > 9000F) && (this.FM.getAltitude() < 9002F) && !((Aircraft) this.FM.actor instanceof TA_152B) && !((Aircraft) this.FM.actor instanceof TA_152C0) && !((Aircraft) this.FM.actor instanceof TA_152C1) && !((Aircraft) this.FM.actor instanceof TA_152C3) && !((Aircraft) this.FM.actor instanceof TA_152C)) {
            this.bouton.setPlay(true);
        }
        if (this.FM.AS.bIsAboutToBailout && !this.FM.isPlayers()) {
            this.hierMesh().chunkVisible("Wire_D0", false);
        }
//        if (this.bSmokeEffect) {
            if (((this.FM == World.getPlayerFM()) && (this.FM.CT.PowerControl >= 1.0F) && (this.FM.EI.engines[0].getRPM() > 100F)) || ((this.FM == World.getPlayerFM()) && this.FM.CT.getAfterburnerControl() && (this.FM.EI.engines[0].getRPM() > 100F))) {
                this.FM.AS.setSootState(this, 0, 1);
            } else {
                this.FM.AS.setSootState(this, 0, 0);
            }
//        }
        if ((!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel)this.FM).isRealMode()) && this.FM.Gears.onGround()) {
            if (this.FM.getSpeedKMH() < 50F) {
                this.FM.CT.cockpitDoorControl = 1.0F;
            } else {
                this.FM.CT.cockpitDoorControl = 0.0F;
            }
        }
//        if (!this.getOp(31) || !this.getOp(32)) {
//            this.FM.CT.trimAileron = (((this.FM.CT.ElevatorControl * (this.s32 - this.s31)) + (this.FM.CT.trimElevator * (this.s18 - this.s17))) * this.FM.SensPitch) / 3F;
//        }
        if (!this.bHasElevatorControl) {
            this.FM.CT.ElevatorControl = 0.0F;
        }
        if (this.trimElevator != this.FM.CT.trimElevator) {
            this.trimElevator = this.FM.CT.trimElevator;
            this.hierMesh().chunkSetAngles("StabL_D0", 0.0F, 0.0F, -16F * this.trimElevator);
            this.hierMesh().chunkSetAngles("StabR_D0", 0.0F, 0.0F, -16F * this.trimElevator);
        }
        if (this.FM.Gears.onGround() && (this.FM.CT.ElevatorControl >= 0.06F)) {
            this.FM.Gears.bTailwheelLocked = true;
        } else if (this.FM.Gears.onGround() && (this.FM.CT.ElevatorControl <= 0.06F)) {
            this.FM.Gears.bTailwheelLocked = false;
        }
        
        if (!this.FM.AS.isMaster() || !Config.isUSE_RENDER()) return;
        
        if ((Main3D.cur3D().cockpits != null) && (Main3D.cur3D().cockpits[0] != null) && this.FM.isPlayers()) {
            if (Main3D.cur3D().cockpits[0].isFocused() || ((this.FM.CT.cockpitDoorControl == 0.9F) && !this.FM.Gears.onGround()) || ((this.FM.CT.cockpitDoorControl == 0.9F) && !this.FM.Gears.getWheelsOnGround())) {
                this.hierMesh().chunkVisible("Blister1_D0", false);
            } else if (!this.FM.AS.bIsAboutToBailout || (this.FM.AS.bIsAboutToBailout && this.FM.Gears.onGround() && (this.FM.CT.cockpitDoorControl > 0.9F))) {
                this.hierMesh().chunkVisible("Blister1_D0", true);
            }
        }
        if (HotKeyCmd.getByRecordedId(272).isActive() && this.FM.isPlayers()) {
            CmdEnv.top().exec("fov 90");
            this.hierMesh().chunkVisible("Wire_D0", false);
            Main3D.cur3D().setViewInside();
            this.EjectCanopy();
            if (!this.hierMesh().isChunkVisible("Prop1_D1") && (this.FM.CT.cockpitDoorControl < 0.5F)) {
                this.boulon.start();
            }
            if (!Main3D.cur3D().isViewOutside()) {
                this.windExtFw.start();
            }
        }
        if (this.FM.isPlayers() && ((HotKeyCmd.getByRecordedId(161).isActive() && this.FM.EI.isSelectionHasControlAfterburner()) || (HotKeyCmd.getByRecordedId(142).isActive() && this.FM.EI.isSelectionAllowsAutoProp()))) {
            this.bouton.setPlay(true);
        }
        if (this.FM.isPlayers() && (this.FM.getSpeedKMH() > 240F)) {
            HotKeyCmd.getByRecordedId(348).enable(false);
        } else {
            HotKeyCmd.getByRecordedId(348).enable(true);
        }
    }

    protected void moveElevator(float f) {
        f -= this.trimElevator;
        if (this.bHasElevatorControl) {
            this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -30F * f, 0.0F);
            this.hierMesh().chunkSetAngles("VatorR_D0", 0.0F, -30F * f, 0.0F);
        }
        float f1 = this.FM.CT.getAileron();
        this.hierMesh().chunkSetAngles("pilotarm2_d0", Aircraft.cvt(f1, -1F, 1.0F, 14F, -16F), 0.0F, Aircraft.cvt(f1, -1F, 1.0F, 6F, -8F) - Aircraft.cvt(f, -1F, 1.0F, -37F, 35F));
        this.hierMesh().chunkSetAngles("pilotarm1_d0", 0.0F, 0.0F, Aircraft.cvt(f1, -1F, 1.0F, -16F, 14F) + Aircraft.cvt(f, -1F, 0.0F, -61F, 0.0F) + Aircraft.cvt(f, 0.0F, 1.0F, 0.0F, 43F));
        if (f < 0.0F) {
            f /= 2.0F;
        }
        this.hierMesh().chunkSetAngles("Stick01_D0", 0.0F, 15F * f1, Aircraft.cvt(f, -1F, 1.0F, -16F, 16F));
    }

    protected void moveAileron(float f) {
        this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, -16F * f, 0.0F);
        this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -16F * f, 0.0F);
        float f1 = this.FM.CT.getElevator();
        this.hierMesh().chunkSetAngles("pilotarm2_d0", Aircraft.cvt(f, -1F, 1.0F, 14F, -16F), 0.0F, Aircraft.cvt(f, -1F, 1.0F, 6F, -8F) - Aircraft.cvt(f1, -1F, 1.0F, -37F, 35F));
        this.hierMesh().chunkSetAngles("pilotarm1_d0", 0.0F, 0.0F, Aircraft.cvt(f, -1F, 1.0F, -16F, 14F) + Aircraft.cvt(f1, -1F, 0.0F, -61F, 0.0F) + Aircraft.cvt(f1, 0.0F, 1.0F, 0.0F, 43F));
        if (f1 < 0.0F) {
            f1 /= 2.0F;
        }
        this.hierMesh().chunkSetAngles("Stick01_D0", 0.0F, 15F * f, Aircraft.cvt(f1, -1F, 1.0F, -16F, 20F));
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, -77F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, -77F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, -102F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, -102F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 20F * f, 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearC3_D0", 0.0F, 0.0F, 0.0F);
        float f1 = Math.max(-f * 1500F, -94F);
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, f1, 0.0F);
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, f1, 0.0F);
        Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = 0.0F;
        Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0.0F;
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.0F, 1.0F, 0.0F, 0.1F);
        hiermesh.chunkSetLocate("poleL_D0", Aircraft.xyz, Aircraft.ypr);
        hiermesh.chunkSetLocate("poleR_D0", Aircraft.xyz, Aircraft.ypr);
    }

    protected void moveGear(float f) {
        TA_152NEW.moveGear(this.hierMesh(), f);
    }

    public void moveSteering(float f) {
        if (this.FM.CT.getGear() >= 0.98F) {
            this.hierMesh().chunkSetAngles("GearC3_D0", 0.0F, -f, 0.0F);
        }
    }

    public void moveWheelSink() {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.44F, 0.0F, 0.44F);
        this.hierMesh().chunkSetLocate("GearL2a_D0", Aircraft.xyz, Aircraft.ypr);
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.44F, 0.0F, 0.44F);
        this.hierMesh().chunkSetLocate("GearR2a_D0", Aircraft.xyz, Aircraft.ypr);
    }

    protected void moveFlap(float f) {
        float f1 = -50F * f;
        this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, f1, 0.0F);
        this.hierMesh().chunkSetAngles("Flap02_D0", 0.0F, f1 * (this.fourFlaps?1F:-1F), 0.0F);
        if (!this.fourFlaps) return;
        this.hierMesh().chunkSetAngles("Flap03_D0", 0.0F, f1, 0.0F);
        this.hierMesh().chunkSetAngles("Flap04_D0", 0.0F, f1, 0.0F);
    }

    public void setOnGround(Point3d point3d, Orient orient, Vector3d vector3d) {
        super.setOnGround(point3d, orient, vector3d);
        if (this.FM.isPlayers()) {
            this.FM.CT.dvCockpitDoor = 80F;
        }
        this.FM.CT.cockpitDoorControl = 1.0F;
    }

    public boolean cut(String s) {
        // System.out.println("cut(" + s + ")");
        if (s.startsWith("Tail1")) {
            this.FM.AS.hitTank(this, 2, 4);
        }
        return super.cut(s);
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        // System.out.println("cutFM(" + i + ", " + j + ", actor)");
        switch (i) {
            case 33:
                return super.cutFM(34, j, actor);

            case 36:
                return super.cutFM(37, j, actor);

            default:
                return super.cutFM(i, j, actor);
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        // System.out.println("hitBone(" + s + ", " + shot.chunkName + ", p3d");
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) {
                Aircraft.debugprintln(this, "*** Armor: Hit..");
                if (s.endsWith("p1")) {
                    this.getEnergyPastArmor(World.Rnd().nextFloat(50F, 50F), shot);
                    if (World.Rnd().nextFloat() < 0.15F) {
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
                    }
                    Aircraft.debugprintln(this, "*** Armor Glass: Hit..");
                    if (shot.power <= 0.0F) {
                        Aircraft.debugprintln(this, "*** Armor Glass: Bullet Stopped..");
                        if (World.Rnd().nextFloat() < 0.5F) {
                            this.doRicochetBack(shot);
                        }
                    }
                } else if (s.endsWith("p3")) {
                    if (point3d.z < -0.27D) {
                        this.getEnergyPastArmor(4.1D / (Math.abs(Aircraft.v1.z) + 0.00001D), shot);
                    } else {
                        this.getEnergyPastArmor(8.1D / (Math.abs(Aircraft.v1.x) + 0.00001D), shot);
                    }
                } else if (s.endsWith("p6")) {
                    this.getEnergyPastArmor(8D / (Math.abs(Aircraft.v1.x) + 0.00001D), shot);
                }
                return;
            }
            if (s.startsWith("xxcontrols")) {
                int i = s.charAt(10) - 48;
                switch (i) {
                    case 7:
                    default:
                        break;

                    case 1:
                    case 4:
                        if (this.getEnergyPastArmor(0.1F, shot) > 0.0F) {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                            Aircraft.debugprintln(this, "*** Aileron Controls: Control Crank Destroyed..");
                        }
                        break;

                    case 2:
                    case 3:
                        if ((this.getEnergyPastArmor(0.12F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                            Aircraft.debugprintln(this, "*** Aileron Controls: Disabled..");
                        }
                        break;

                    case 5:
                        if ((this.getEnergyPastArmor(0.12F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                            this.FM.AS.setControlsDamage(shot.initiator, 1);
                            Aircraft.debugprintln(this, "*** Elevator Controls: Disabled / Strings Broken..");
                        }
                        break;

                    case 6:
                        if ((this.getEnergyPastArmor(0.12F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                            this.FM.AS.setControlsDamage(shot.initiator, 2);
                            Aircraft.debugprintln(this, "*** Rudder Controls: Disabled / Strings Broken..");
                        }
                        break;

                    case 8:
                        if (this.getEnergyPastArmor(3.2F, shot) > 0.0F) {
                            Aircraft.debugprintln(this, "*** Control Column: Hit, Controls Destroyed..");
                            this.FM.AS.setControlsDamage(shot.initiator, 2);
                            this.FM.AS.setControlsDamage(shot.initiator, 1);
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                        }
                        break;
                }
                return;
            }
            if (s.startsWith("xxspar")) {
                Aircraft.debugprintln(this, "*** Spar Construction: Hit..");
                if (s.startsWith("xxspart") && (this.chunkDamageVisible("Tail1") > 2) && (this.getEnergyPastArmor(2.4F / (float) Math.sqrt((Aircraft.v1.y * Aircraft.v1.y) + (Aircraft.v1.z * Aircraft.v1.z)), shot) > 0.0F)) {
                    Aircraft.debugprintln(this, "*** Tail1 Spars Broken in Half..");
                    this.nextDMGLevels(1, 2, "Tail1_D3", shot.initiator);
                }
                if (s.startsWith("xxsparli") && (this.chunkDamageVisible("WingLIn") > 2) && (this.getEnergyPastArmor(18F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    Aircraft.debugprintln(this, "*** WingLIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparri") && (this.chunkDamageVisible("WingRIn") > 2) && (this.getEnergyPastArmor(18F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    Aircraft.debugprintln(this, "*** WingRIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlm") && (this.chunkDamageVisible("WingLMid") > 2) && (this.getEnergyPastArmor(12.7F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    Aircraft.debugprintln(this, "*** WingLMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparrm") && (this.chunkDamageVisible("WingRMid") > 2) && (this.getEnergyPastArmor(12.7F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    Aircraft.debugprintln(this, "*** WingRMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlo") && (this.chunkDamageVisible("WingLOut") > 2) && (this.getEnergyPastArmor(12.7F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    Aircraft.debugprintln(this, "*** WingLOut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if (s.startsWith("xxsparro") && (this.chunkDamageVisible("WingROut") > 2) && (this.getEnergyPastArmor(12.7F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    Aircraft.debugprintln(this, "*** WingROut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                return;
            }
            if (s.startsWith("xxlock")) {
                Aircraft.debugprintln(this, "*** Lock Construction: Hit..");
                if (s.startsWith("xxlockr") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    Aircraft.debugprintln(this, "*** Rudder Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "Rudder1_D" + this.chunkDamageVisible("Rudder1"), shot.initiator);
                }
                if (s.startsWith("xxlockvl") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    Aircraft.debugprintln(this, "*** VatorL Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "VatorL_D" + this.chunkDamageVisible("VatorL"), shot.initiator);
                }
                if (s.startsWith("xxlockvr") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)) {
                    Aircraft.debugprintln(this, "*** VatorR Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "VatorR_D" + this.chunkDamageVisible("VatorR"), shot.initiator);
                }
                return;
            }
            if (s.startsWith("xxeng")) {
                Aircraft.debugprintln(this, "*** Engine Module: Hit..");
                if (s.endsWith("pipe")) {
                    if ((World.Rnd().nextFloat() < 0.1F) && (this.FM.EI.engines[0].getType() == 0) && (this.FM.CT.Weapons[1] != null) && (this.FM.CT.Weapons[1].length != 2)) {
                        this.FM.AS.setJamBullets(1, 0);
                        Aircraft.debugprintln(this, "*** Engine Module: Nose Nozzle Pipe Bent..");
                    }
                    this.getEnergyPastArmor(0.3F, shot);
                } else if (s.endsWith("prop")) {
                    if ((this.getEnergyPastArmor(0.1F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.8F)) {
                        if (World.Rnd().nextFloat() < 0.5F) {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 3);
                            Aircraft.debugprintln(this, "*** Engine Module: Prop Governor Hit, Disabled..");
                        } else {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 4);
                            Aircraft.debugprintln(this, "*** Engine Module: Prop Governor Hit, Damaged..");
                        }
                    }
                } else if (s.endsWith("gear")) {
                    if (this.getEnergyPastArmor(4.6F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < 0.5F) {
                            this.FM.EI.engines[0].setEngineStuck(shot.initiator);
                            Aircraft.debugprintln(this, "*** Engine Module: Bullet Jams Reductor Gear..");
                        } else {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 3);
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 4);
                            Aircraft.debugprintln(this, "*** Engine Module: Reductor Gear Damaged, Prop Governor Failed..");
                        }
                    }
                } else if (s.endsWith("supc")) {
                    if (this.getEnergyPastArmor(0.1F, shot) > 0.0F) {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 0);
                        Aircraft.debugprintln(this, "*** Engine Module: Supercharger Disabled..");
                    }
                    this.getEnergyPastArmor(0.5F, shot);
                } else if (s.endsWith("feed")) {
                    if ((this.getEnergyPastArmor(8.9F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F) && (this.FM.EI.engines[0].getPowerOutput() > 0.7F) && (this.FM.EI.engines[0].getType() == 0)) {
                        this.FM.AS.hitEngine(shot.initiator, 0, 100);
                        Aircraft.debugprintln(this, "*** Engine Module: Pressurized Fuel Line Pierced, Fuel Flamed..");
                    }
                    this.getEnergyPastArmor(1.0F, shot);
                } else if (s.endsWith("fuel")) {
                    if ((this.getEnergyPastArmor(1.1F, shot) > 0.0F) && (this.FM.EI.engines[0].getType() == 0)) {
                        this.FM.EI.engines[0].setEngineStops(shot.initiator);
                        Aircraft.debugprintln(this, "*** Engine Module: Fuel Line Stalled, Engine Stalled..");
                    }
                    this.getEnergyPastArmor(1.0F, shot);
                } else if (s.endsWith("case")) {
                    if (this.getEnergyPastArmor(4.2F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < (shot.power / 175000F)) {
                            this.FM.AS.setEngineStuck(shot.initiator, 0);
                            Aircraft.debugprintln(this, "*** Engine Module: Bullet Jams Crank Ball Bearing..");
                        }
                        if ((World.Rnd().nextFloat() < (shot.power / 50000F)) && (this.FM.EI.engines[0].getType() == 0)) {
                            this.FM.AS.hitEngine(shot.initiator, 0, 2);
                            Aircraft.debugprintln(this, "*** Engine Module: Crank Case Hit, Readyness Reduced to " + this.FM.EI.engines[0].getReadyness() + "..");
                        }
                        if (this.FM.EI.engines[0].getType() == 0) {
                            this.FM.EI.engines[0].setReadyness(shot.initiator, this.FM.EI.engines[0].getReadyness() - World.Rnd().nextFloat(0.0F, shot.power / 48000F));
                        }
                        Aircraft.debugprintln(this, "*** Engine Module: Crank Case Hit, Readyness Reduced to " + this.FM.EI.engines[0].getReadyness() + "..");
                    }
                    this.getEnergyPastArmor(27.5F, shot);
                } else if (s.startsWith("xxeng1cyl")) {
                    if ((this.getEnergyPastArmor(2.4F, shot) > 0.0F) && (World.Rnd().nextFloat() < (this.FM.EI.engines[0].getCylindersRatio() * (this.FM.EI.engines[0].getType() != 0 ? 0.5F : 1.75F)))) {
                        if (this.FM.EI.engines[0].getType() == 0) {
                            this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 4800F)));
                        } else {
                            this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 19200F)));
                        }
                        Aircraft.debugprintln(this, "*** Engine Module: Cylinders Hit, " + this.FM.EI.engines[0].getCylindersOperable() + "/" + this.FM.EI.engines[0].getCylinders() + " Left..");
                        if ((World.Rnd().nextFloat() < (shot.power / 96000F)) && (this.FM.EI.engines[0].getType() == 0)) {
                            this.FM.AS.hitEngine(shot.initiator, 0, 3);
                            Aircraft.debugprintln(this, "*** Engine Module: Cylinders Hit, Engine Fires..");
                        }
                        if ((World.Rnd().nextFloat() < (shot.power / 96000F)) && (this.FM.EI.engines[0].getType() == 1)) {
                            this.FM.AS.hitEngine(shot.initiator, 0, 1);
                            Aircraft.debugprintln(this, "*** Engine Module: Cylinders Hit, Engine Fires..");
                        }
                        if (World.Rnd().nextFloat() < 0.01F) {
                            this.FM.AS.setEngineStuck(shot.initiator, 0);
                            Aircraft.debugprintln(this, "*** Engine Module: Bullet Jams Piston Head..");
                        }
                        this.getEnergyPastArmor(43.6F, shot);
                    }
                } else if (s.startsWith("xxeng1mag")) {
                    int j = s.charAt(9) - 49;
                    this.FM.EI.engines[0].setMagnetoKnockOut(shot.initiator, j);
                    Aircraft.debugprintln(this, "*** Engine Module: Magneto " + j + " Destroyed..");
                } else if (s.endsWith("sync")) {
                    if ((this.getEnergyPastArmor(2.1F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                        Aircraft.debugprintln(this, "*** Engine Module: Gun Synchronized Hit, Nose Guns Lose Authority..");
                    }
                } else if (s.endsWith("oil1") && (this.getEnergyPastArmor(2.4F, shot) > 0.0F)) {
                    this.FM.AS.hitOil(shot.initiator, 0);
                    Aircraft.debugprintln(this, "*** Engine Module: Oil Radiator Hit..");
                }
                return;
            }
            if (s.startsWith("xxtank")) {
                int k = s.charAt(6) - 48;
                switch (k) {
                    default:
                        break;

                    case 1:
                        if ((this.getEnergyPastArmor(0.2F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                            if (this.FM.AS.astateTankStates[2] == 0) {
                                Aircraft.debugprintln(this, "*** Fuel Tank: Pierced..");
                                this.FM.AS.hitTank(shot.initiator, 2, 1);
                                this.FM.AS.doSetTankState(shot.initiator, 2, 1);
                            } else if (((shot.powerType == 3) && (World.Rnd().nextFloat() < 0.9F)) || (World.Rnd().nextFloat() < 0.03F)) {
                                this.FM.AS.hitTank(shot.initiator, 2, 2);
                                Aircraft.debugprintln(this, "*** Fuel Tank: Hit..");
                            }
                            if (shot.power > 200000F) {
                                this.FM.AS.hitTank(shot.initiator, 2, 99);
                                Aircraft.debugprintln(this, "*** Fuel Tank: Major Hit..");
                            }
                        }
                        break;

                    case 2:
                        if ((this.getEnergyPastArmor(1.2F, shot) <= 0.0F) || (World.Rnd().nextFloat() >= 0.25F)) {
                            break;
                        }
                        if (this.FM.AS.astateTankStates[1] == 0) {
                            Aircraft.debugprintln(this, "*** Fuel Tank: Pierced..");
                            this.FM.AS.hitTank(shot.initiator, 1, 1);
                            this.FM.AS.doSetTankState(shot.initiator, 1, 1);
                        } else if (((shot.powerType == 3) && (World.Rnd().nextFloat() < 0.8F)) || (World.Rnd().nextFloat() < 0.03F)) {
                            this.FM.AS.hitTank(shot.initiator, 1, 2);
                            Aircraft.debugprintln(this, "*** Fuel Tank: Hit..");
                        }
                        if (shot.power > 200000F) {
                            this.FM.AS.hitTank(shot.initiator, 1, 99);
                            Aircraft.debugprintln(this, "*** Fuel Tank: Major Hit..");
                        }
                        break;

                    case 3:
                        if ((this.getEnergyPastArmor(1.2F, shot) <= 0.0F) || (World.Rnd().nextFloat() >= 0.25F)) {
                            break;
                        }
                        if (this.FM.AS.astateTankStates[0] == 0) {
                            Aircraft.debugprintln(this, "*** Fuel Tank: Pierced..");
                            this.FM.AS.hitTank(shot.initiator, 0, 1);
                            this.FM.AS.doSetTankState(shot.initiator, 0, 1);
                        } else if (((shot.powerType == 3) && (World.Rnd().nextFloat() < 0.8F)) || (World.Rnd().nextFloat() < 0.03F)) {
                            this.FM.AS.hitTank(shot.initiator, 0, 2);
                            Aircraft.debugprintln(this, "*** Fuel Tank: Hit..");
                        }
                        if (shot.power > 200000F) {
                            this.FM.AS.hitTank(shot.initiator, 0, 99);
                            Aircraft.debugprintln(this, "*** Fuel Tank: Major Hit..");
                        }
                        break;
                }
                return;
            }
            if (s.startsWith("xxmw50")) {
                if (World.Rnd().nextFloat() < 0.05F) {
                    Aircraft.debugprintln(this, "*** MW50 Tank: Pierced..");
                    this.FM.AS.setInternalDamage(shot.initiator, 2);
                }
                return;
            }
            if (s.startsWith("xxmgun")) {
                if (s.endsWith("01")) {
                    this.FM.AS.setJamBullets(1, 0);
                }
                if (s.endsWith("02")) {
                    this.FM.AS.setJamBullets(1, 1);
                }
                this.getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 24.6F), shot);
                return;
            }
            if (s.startsWith("xxcannon")) {
                Aircraft.debugprintln(this, "*** Nose Cannon: Disabled..");
                this.FM.AS.setJamBullets(0, 0);
                this.getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 24.6F), shot);
                return;
            }
            if (s.startsWith("xxradiat")) {
                this.FM.EI.engines[0].setReadyness(shot.initiator, this.FM.EI.engines[0].getReadyness() - World.Rnd().nextFloat(0.0F, 0.05F));
                Aircraft.debugprintln(this, "*** Engine Module: Radiator Hit, Readyness Reduced to " + this.FM.EI.engines[0].getReadyness() + "..");
            }
            return;
        }
        if (s.startsWith("xcf") || s.startsWith("xcockpit")) {
            if (this.chunkDamageVisible("CF") < 3) {
                this.hitChunk("CF", shot);
            }
            if (s.startsWith("xcockpit")) {
                if (point3d.z > 0.4D) {
                    if (World.Rnd().nextFloat() < 0.2F) {
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
                    }
                    if (World.Rnd().nextFloat() < 0.2F) {
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
                    }
                } else if (point3d.y > 0.0D) {
                    if (World.Rnd().nextFloat() < 0.2F) {
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
                    }
                } else if (World.Rnd().nextFloat() < 0.2F) {
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
                }
                if ((point3d.x > 0.2D) && (World.Rnd().nextFloat() < 0.2F)) {
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
                }
            }
        } else if (s.startsWith("xeng")) {
            if (this.chunkDamageVisible("Engine1") < 2) {
                this.hitChunk("Engine1", shot);
            }
        } else if (s.startsWith("xtail")) {
            if (this.chunkDamageVisible("Tail1") < 3) {
                this.hitChunk("Tail1", shot);
            }
        } else if (s.startsWith("xkeel")) {
            if (this.chunkDamageVisible("Keel1") < 2) {
                this.hitChunk("Keel1", shot);
            }
        } else if (s.startsWith("xrudder")) {
            if (this.chunkDamageVisible("Rudder1") < 1) {
                this.hitChunk("Rudder1", shot);
            }
        } else if (s.startsWith("xstab")) {
            if (s.startsWith("xstabl") && (this.chunkDamageVisible("StabL") < 2)) {
                this.hitChunk("StabL", shot);
            }
            if (s.startsWith("xstabr") && (this.chunkDamageVisible("StabR") < 1)) {
                this.hitChunk("StabR", shot);
            }
        } else if (s.startsWith("xvator")) {
            if (s.startsWith("xvatorl") && (this.chunkDamageVisible("VatorL") < 1)) {
                this.hitChunk("VatorL", shot);
            }
            if (s.startsWith("xvatorr") && (this.chunkDamageVisible("VatorR") < 1)) {
                this.hitChunk("VatorR", shot);
            }
        } else if (s.startsWith("xwing")) {
            if (s.startsWith("xwinglin") && (this.chunkDamageVisible("WingLIn") < 3)) {
                this.hitChunk("WingLIn", shot);
            }
            if (s.startsWith("xwingrin") && (this.chunkDamageVisible("WingRIn") < 3)) {
                this.hitChunk("WingRIn", shot);
            }
            if (s.startsWith("xwinglmid") && (this.chunkDamageVisible("WingLMid") < 3)) {
                this.hitChunk("WingLMid", shot);
            }
            if (s.startsWith("xwingrmid") && (this.chunkDamageVisible("WingRMid") < 3)) {
                this.hitChunk("WingRMid", shot);
            }
            if (s.startsWith("xwinglout") && (this.chunkDamageVisible("WingLOut") < 3)) {
                this.hitChunk("WingLOut", shot);
            }
            if (s.startsWith("xwingrout") && (this.chunkDamageVisible("WingROut") < 3)) {
                this.hitChunk("WingROut", shot);
            }
        } else if (s.startsWith("xarone")) {
            if (s.startsWith("xaronel")) {
                this.hitChunk("AroneL", shot);
            }
            if (s.startsWith("xaroner")) {
                this.hitChunk("AroneR", shot);
            }
        } else if (s.startsWith("xpilot") || s.startsWith("xhead")) {
            byte byte0 = 0;
            int l;
            if (s.endsWith("a")) {
                byte0 = 1;
                l = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte0 = 2;
                l = s.charAt(6) - 49;
            } else {
                l = s.charAt(5) - 49;
            }
            this.hitFlesh(l, shot, byte0);
        }
    }

//    private void cutOp(int i) {
//        System.out.println("cutOp(" + i + ")");
//        this.FM.Operate &= ~(1L << i);
//    }
//
//    protected boolean getOp(int i) {
//        System.out.println("getOp(" + i + ")");
//        return (this.FM.Operate & (1L << i)) != 0L;
//    }
//
//    private float Op(int i) {
//        System.out.println("Op(" + i + ")");
//        return this.getOp(i) ? 1.0F : 0.0F;
//    }
//
//    protected boolean cutFM(int i, int j, Actor actor) {
//        System.out.println("cutFM(" + i + ", " + j + ", actor)");
//        if (!this.getOp(i)) {
//            return false;
//        }
//        switch (i) {
//            case 17:
//                this.cut("StabL");
//                this.cutOp(17);
//                this.FM.setCapableOfACM(false);
//                if (World.Rnd().nextInt(-1, 8) < this.FM.Skill) {
//                    this.FM.setReadyToReturn(true);
//                }
//                if (World.Rnd().nextInt(-1, 16) < this.FM.Skill) {
//                    this.FM.setReadyToDie(true);
//                }
//                this.FM.Sq.liftStab *= (0.5F * this.Op(18)) + 0.1F;
//                this.FM.Sq.liftWingLIn *= 1.1F;
//                this.FM.Sq.liftWingRIn *= 0.9F;
//                this.FM.Sq.dragProducedCx -= 0.06F;
//                if (this.Op(18) == 0.0F) {
//                    this.FM.SensPitch = 0.0F;
//                    this.FM.setGCenter(0.2F);
//                } else {
//                    this.FM.setGCenter(0.1F);
//                    this.s17 = 0.0F;
//                    this.FM.SensPitch *= this.s17 + this.s18 + this.s31 + this.s32;
//                    this.X = 1.0F / (this.s17 + this.s18 + this.s31 + this.s32);
//                    this.s18 *= this.X;
//                    this.s31 *= this.X;
//                    this.s32 *= this.X;
//                }
//                // fall through
//
//            case 31:
//                if (this.Op(31) == 0.0F) {
//                    return false;
//                }
//                this.cut("VatorL");
//                this.cutOp(31);
//                if (this.Op(32) == 0.0F) {
//                    this.bHasElevatorControl = false;
//                    this.FM.setCapableOfACM(false);
//                    if (this.Op(18) == 0.0F) {
//                        this.FM.setReadyToDie(true);
//                    }
//                }
//                this.FM.Sq.squareElevators *= 0.5F * this.Op(32);
//                this.FM.Sq.dragProducedCx += 0.06F;
//                this.s31 = 0.0F;
//                this.FM.SensPitch *= this.s17 + this.s18 + this.s31 + this.s32;
//                this.X = 1.0F / (this.s17 + this.s18 + this.s31 + this.s32);
//                this.s17 *= this.X;
//                this.s18 *= this.X;
//                this.s32 *= this.X;
//                return false;
//
//            case 18:
//                this.cut("StabR");
//                this.cutOp(18);
//                this.FM.setCapableOfACM(false);
//                if (World.Rnd().nextInt(-1, 8) < this.FM.Skill) {
//                    this.FM.setReadyToReturn(true);
//                }
//                if (World.Rnd().nextInt(-1, 16) < this.FM.Skill) {
//                    this.FM.setReadyToDie(true);
//                }
//                this.FM.Sq.liftStab *= (0.5F * this.Op(17)) + 0.1F;
//                this.FM.Sq.liftWingLIn *= 0.9F;
//                this.FM.Sq.liftWingRIn *= 1.1F;
//                this.FM.Sq.dragProducedCx -= 0.06F;
//                if (this.Op(17) == 0.0F) {
//                    this.FM.SensPitch = 0.0F;
//                    this.FM.setGCenter(0.2F);
//                } else {
//                    this.FM.setGCenter(0.1F);
//                    this.s18 = 0.0F;
//                    this.FM.SensPitch *= this.s17 + this.s18 + this.s31 + this.s32;
//                    this.X = 1.0F / (this.s17 + this.s18 + this.s31 + this.s32);
//                    this.s17 *= this.X;
//                    this.s31 *= this.X;
//                    this.s32 *= this.X;
//                }
//                // fall through
//
//            case 32:
//                if (this.Op(32) == 0.0F) {
//                    return false;
//                }
//                this.cut("VatorR");
//                this.cutOp(32);
//                if (this.Op(31) == 0.0F) {
//                    this.bHasElevatorControl = false;
//                    this.FM.setCapableOfACM(false);
//                    if (this.Op(17) == 0.0F) {
//                        this.FM.setReadyToDie(true);
//                    }
//                }
//                this.FM.Sq.squareElevators *= 0.5F * this.Op(31);
//                this.FM.Sq.dragProducedCx += 0.06F;
//                this.s32 = 0.0F;
//                this.FM.SensPitch *= this.s17 + this.s18 + this.s31 + this.s32;
//                this.X = 1.0F / (this.s17 + this.s18 + this.s31 + this.s32);
//                this.s17 *= this.X;
//                this.s18 *= this.X;
//                this.s31 *= this.X;
//                return false;
//
//            default:
//                return super.cutFM(i, j, actor);
//        }
//    }

//    private void cutGearCovers(String s) {
//        Vector3d vector3d = new Vector3d();
//        if (World.Rnd().nextFloat() < 0.3F) {
//            Wreckage wreckage = new Wreckage(this, this.hierMesh().chunkFind("Gear" + s + 5 + "_D0"));
//            wreckage.collide(true);
//            vector3d.set(this.FM.Vwld);
//            wreckage.setSpeed(vector3d);
//            this.hierMesh().chunkVisible("Gear" + s + 5 + "_D0", false);
//            Wreckage wreckage1 = new Wreckage(this, this.hierMesh().chunkFind("Gear" + s + 6 + "_D0"));
//            wreckage1.collide(true);
//            vector3d.set(this.FM.Vwld);
//            wreckage1.setSpeed(vector3d);
//            this.hierMesh().chunkVisible("Gear" + s + 6 + "_D0", false);
//        } else if (World.Rnd().nextFloat() < 0.3F) {
//            int i = World.Rnd().nextInt(2) + 5;
//            Wreckage wreckage2 = new Wreckage(this, this.hierMesh().chunkFind("Gear" + s + i + "_D0"));
//            wreckage2.collide(true);
//            vector3d.set(this.FM.Vwld);
//            wreckage2.setSpeed(vector3d);
//            this.hierMesh().chunkVisible("Gear" + s + i + "_D0", false);
//        }
//    }

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
        HUD.log(AircraftHotKeys.hudLogWeaponId, "AskaniaWing" + this.k14WingspanType);
    }

    public void typeFighterAceMakerAdjSideslipMinus() {
        this.k14WingspanType++;
        if (this.k14WingspanType > 9) {
            this.k14WingspanType = 9;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "AskaniaWing" + this.k14WingspanType);
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
    
    public void typeX4CAdjSidePlus() {
        this.deltaAzimuth = 1.0F;
    }

    public void typeX4CAdjSideMinus() {
        this.deltaAzimuth = -1F;
    }

    public void typeX4CAdjAttitudePlus() {
        this.deltaTangage = 1.0F;
    }

    public void typeX4CAdjAttitudeMinus() {
        this.deltaTangage = -1F;
    }

    public void typeX4CResetControls() {
        this.deltaAzimuth = this.deltaTangage = 0.0F;
    }

    public float typeX4CgetdeltaAzimuth() {
        return this.deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage() {
        return this.deltaTangage;
    }
    
//    void updateAfterburner() {
//        if (this.FM.Loc.z > 9000D) {
//            if (!this.FM.EI.engines[0].getControlAfterburner()) {
//                this.FM.EI.engines[0].bHasAfterburnerControl = true;
//            }
//            this.FM.EI.engines[0].setAfterburnerType(2);
//        } else if (!this.FM.EI.engines[0].getControlAfterburner()) {
//            this.FM.EI.engines[0].bHasAfterburnerControl = false;
//        }
//    }
    
    void updateAfterburner() {
        if (!this.FM.EI.engines[0].bHasAfterburnerControl) return;
        if (this.FM.EI.engines[0].getControlAfterburner()) return;
        switch (this.FM.EI.engines[0].getAfterburnerType()) {
            case Motor._E_AFTERBURNER_MW50:
                if (this.FM.getAltitude() > 9000F) {
                    this.FM.EI.engines[0].setAfterburnerType(Motor._E_AFTERBURNER_GM1);
                }
                break;
            case Motor._E_AFTERBURNER_GM1:
                if (this.FM.getAltitude() < 9000F) {
                    this.FM.EI.engines[0].setAfterburnerType(Motor._E_AFTERBURNER_MW50);
                }
                break;
            default:
                break;
        }
    }

//    private boolean       bSmokeEffect;
    private float         trimElevator;
    private boolean       bHasElevatorControl;
//    private float         X;
//    private float         s17;
//    private float         s18;
//    private float         s31;
//    private float         s32;
    double                sinkAngle[];
    static float          LengthA_B   = 0.543601F;
    static float          LengthA_D   = 0.4612681F;
    static float          LengthB_C   = 0.3014931F;
    static float          LengthC_D   = 0.38F;
    static double         AxisPos[][] = new double[4][2];
    static int            axisA       = 0;
    static int            axisB       = 1;
    static int            axisC       = 2;
    static int            axisD       = 3;
    static int            idX         = 0;
    static int            idY         = 1;
//    private static float  kl          = 1.0F;
//    private static float  kr          = 1.0F;
//    private static float  kc          = 1.0F;
    private Point3d       doorSndPos;
    private float         doorPrev;
    protected float       doorSndControl;
    protected SoundFX     doorSound;
    protected AudioStream boulon;
    protected AudioStream bouton;
    protected AudioStream arrach;
    private AudioStream   windExtFw;
    private float kangle;
    public boolean bToFire;
    private long   tX4Prev;
    public int     k14Mode;
    public int     k14WingspanType;
    public float   k14Distance;
    private float  deltaAzimuth;
    private float  deltaTangage;
    private boolean fourFlaps = false;
//    private int curAfterBurnerType = 0;

    static {
        Class class1 = TA_152NEW.class;
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
    }
}
