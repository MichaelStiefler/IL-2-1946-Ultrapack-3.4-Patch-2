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
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Landscape;
import com.maddox.il2.engine.LightPointWorld;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.AircraftState;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.Wreckage;
import com.maddox.rts.MsgAction;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;

public class F_86F extends Scheme1 implements TypeSupersonic, TypeFighter, TypeBNZFighter, TypeFighterAceMaker, TypeStormovik, TypeGSuit {
    public float getDragForce(float arg0, float arg1, float arg2, float arg3) {
        throw new UnsupportedOperationException("getDragForce not supported anymore.");
    }

    public float getDragInGravity(float arg0, float arg1, float arg2, float arg3, float arg4, float arg5) {
        throw new UnsupportedOperationException("getDragInGravity supported anymore.");
    }

    public float getForceInGravity(float arg0, float arg1, float arg2) {
        throw new UnsupportedOperationException("getForceInGravity supported anymore.");
    }

    public float getDegPerSec(float arg0, float arg1) {
        throw new UnsupportedOperationException("getDegPerSec supported anymore.");
    }

    public float getGForce(float arg0, float arg1) {
        throw new UnsupportedOperationException("getGForce supported anymore.");
    }

    public F_86F() {
        this.bSlatsOff = false;
        this.oldctl = -1F;
        this.curctl = -1F;
        this.oldthrl = -1F;
        this.curthrl = -1F;
        this.k14Mode = 0;
        this.k14WingspanType = 0;
        this.k14Distance = 200F;
        this.AirBrakeControl = 0.0F;
        this.overrideBailout = false;
        this.ejectComplete = false;
        this.lightTime = 0.0F;
        this.ft = 0.0F;
        this.engineSurgeDamage = 0.0F;
        this.gearTargetAngle = -1F;
        this.gearCurrentAngle = -1F;
        this.hasHydraulicPressure = true;
        this.transsonicEffects = new TransonicEffects(this, 0.0F, 13000F, 0.65F, 1.0F, 0.01F, 1.0F, 0.2F, 1.0F, 0.5F, 0.6F, 0.0F, 0.9F, 1.0F, 1.25F);
    }

    public void getGFactors(TypeGSuit.GFactors theGFactors) {
        theGFactors.setGFactors(NEG_G_TOLERANCE_FACTOR, NEG_G_TIME_FACTOR, NEG_G_RECOVERY_FACTOR, POS_G_TOLERANCE_FACTOR, POS_G_TIME_FACTOR, POS_G_RECOVERY_FACTOR);
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.FM.AS.wantBeaconsNet(true);
        this.transsonicEffects.onAircraftLoaded();
    }

    public void checkHydraulicStatus() {
        if (this.FM.EI.engines[0].getStage() < 6 && this.FM.Gears.nOfGearsOnGr > 0) {
            this.gearTargetAngle = 90F;
            this.hasHydraulicPressure = false;
            this.FM.CT.bHasAileronControl = false;
            this.FM.CT.bHasElevatorControl = false;
            this.FM.CT.AirBrakeControl = 1.0F;
        } else if (!this.hasHydraulicPressure) {
            this.gearTargetAngle = 0.0F;
            this.hasHydraulicPressure = true;
            this.FM.CT.bHasAileronControl = true;
            this.FM.CT.bHasElevatorControl = true;
            this.FM.CT.bHasAirBrakeControl = true;
        }
    }

    public void moveHydraulics(float f) {
        if (this.gearTargetAngle >= 0.0F) {
            if (this.gearCurrentAngle < this.gearTargetAngle) {
                this.gearCurrentAngle += 90F * f * 0.8F;
                if (this.gearCurrentAngle >= this.gearTargetAngle) {
                    this.gearCurrentAngle = this.gearTargetAngle;
                    this.gearTargetAngle = -1F;
                }
            } else {
                this.gearCurrentAngle -= 90F * f * 0.8F;
                if (this.gearCurrentAngle <= this.gearTargetAngle) {
                    this.gearCurrentAngle = this.gearTargetAngle;
                    this.gearTargetAngle = -1F;
                }
            }
            this.hierMesh().chunkSetAngles("GearL6_D0", 0.0F, -this.gearCurrentAngle, 0.0F);
            this.hierMesh().chunkSetAngles("GearR6_D0", 0.0F, -this.gearCurrentAngle, 0.0F);
            this.hierMesh().chunkSetAngles("GearC4_D0", 0.0F, -this.gearCurrentAngle, 0.0F);
        }
    }

    public void updateLLights() {
        this.pos.getRender(Actor._tmpLoc);
        if (this.lLight == null) {
            if (Actor._tmpLoc.getX() >= 1.0D) {
                this.lLight = new LightPointWorld[] { null, null, null, null };
                for (int i = 0; i < 4; i++) {
                    this.lLight[i] = new LightPointWorld();
                    this.lLight[i].setColor(1.0F, 1.0F, 1.0F);
                    this.lLight[i].setEmit(0.0F, 0.0F);
                    try {
                        this.lLightHook[i] = new HookNamed(this, "_LandingLight0" + i);
                    } catch (Exception exception) {}
                }

            }
        } else for (int i = 0; i < 4; i++) {
            if (this.FM.AS.astateLandingLightEffects[i] != null) {
                lLightLoc1.set(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
                this.lLightHook[i].computePos(this, Actor._tmpLoc, lLightLoc1);
                lLightLoc1.get(lLightP1);
                lLightLoc1.set(2000D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
                this.lLightHook[i].computePos(this, Actor._tmpLoc, lLightLoc1);
                lLightLoc1.get(lLightP2);
                Engine.land();
                if (Landscape.rayHitHQ(lLightP1, lLightP2, lLightPL)) {
                    lLightPL.z++;
                    lLightP2.interpolate(lLightP1, lLightPL, 0.95F);
                    this.lLight[i].setPos(lLightP2);
                    float f1 = (float) lLightP1.distance(lLightPL);
                    float f2 = f1 * 0.5F + 60F;
                    float f3 = 0.7F - 0.8F * f1 * this.lightTime / 2000F;
                    this.lLight[i].setEmit(f3, f2);
                } else this.lLight[i].setEmit(0.0F, 0.0F);
                continue;
            }
            if (this.lLight[i].getR() != 0.0F) this.lLight[i].setEmit(0.0F, 0.0F);
        }
    }

    protected void nextDMGLevel(String paramString, int paramInt, Actor paramActor) {
        super.nextDMGLevel(paramString, paramInt, paramActor);
        if (this.FM.isPlayers()) bChangedPit = true;
    }

    protected void nextCUTLevel(String paramString, int paramInt, Actor paramActor) {
        super.nextCUTLevel(paramString, paramInt, paramActor);
        if (this.FM.isPlayers()) bChangedPit = true;
    }

    public void rareAction(float paramFloat, boolean paramBoolean) {
        super.rareAction(paramFloat, paramBoolean);
        if ((this.FM.Gears.nearGround() || this.FM.Gears.onGround()) && this.FM.CT.getCockpitDoor() == 1.0F) this.hierMesh().chunkVisible("HMask1_D0", false);
        else this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
        if ((!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) && this.FM instanceof Maneuver)
            if (this.FM.AP.way.isLanding() && this.FM.getSpeed() > this.FM.VmaxFLAPS && this.FM.getSpeed() > this.FM.AP.way.curr().getV() * 1.4F) {
                if (this.FM.CT.AirBrakeControl != 1.0F) this.FM.CT.AirBrakeControl = 1.0F;
            } else if (((Maneuver) this.FM).get_maneuver() == 25 && this.FM.AP.way.isLanding() && this.FM.getSpeed() < this.FM.VmaxFLAPS * 1.16F) {
                if (this.FM.getSpeed() > this.FM.VminFLAPS * 0.5F && (this.FM.Gears.nearGround() || this.FM.Gears.onGround())) {
                    if (this.FM.CT.AirBrakeControl != 1.0F) this.FM.CT.AirBrakeControl = 1.0F;
                } else if (this.FM.CT.AirBrakeControl != 0.0F) this.FM.CT.AirBrakeControl = 0.0F;
            } else if (((Maneuver) this.FM).get_maneuver() == 66) {
                if (this.FM.CT.AirBrakeControl != 0.0F) this.FM.CT.AirBrakeControl = 0.0F;
            } else if (((Maneuver) this.FM).get_maneuver() == 7) {
                if (this.FM.CT.AirBrakeControl != 1.0F) this.FM.CT.AirBrakeControl = 1.0F;
            } else if (this.hasHydraulicPressure && this.FM.CT.AirBrakeControl != 0.0F) this.FM.CT.AirBrakeControl = 0.0F;
        this.ft = World.getTimeofDay() % 0.01F;
        if (this.ft == 0.0F) this.UpdateLightIntensity();
    }

    private final void UpdateLightIntensity() {
        if (World.getTimeofDay() >= 6F && World.getTimeofDay() < 7F) this.lightTime = Aircraft.cvt(World.getTimeofDay(), 6F, 7F, 1.0F, 0.1F);
        else if (World.getTimeofDay() >= 18F && World.getTimeofDay() < 19F) this.lightTime = Aircraft.cvt(World.getTimeofDay(), 18F, 19F, 0.1F, 1.0F);
        else if (World.getTimeofDay() >= 7F && World.getTimeofDay() < 18F) this.lightTime = 0.1F;
        else this.lightTime = 1.0F;
    }

    public boolean typeFighterAceMakerToggleAutomation() {
        this.k14Mode++;
        if (this.k14Mode > 2) this.k14Mode = 0;
        if (this.k14Mode == 0) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Sight Mode: Caged");
        } else if (this.k14Mode == 1) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Sight Mode: Uncaged");
        } else if (this.k14Mode == 2 && this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Sight Off");
        return true;
    }

    public void typeFighterAceMakerAdjDistanceReset() {
    }

    public void typeFighterAceMakerAdjDistancePlus() {
        this.k14Distance += 10F;
        if (this.k14Distance > 800F) this.k14Distance = 800F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerInc");
    }

    public void typeFighterAceMakerAdjDistanceMinus() {
        this.k14Distance -= 10F;
        if (this.k14Distance < 200F) this.k14Distance = 200F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerDec");
    }

    public void typeFighterAceMakerAdjSideslipReset() {
    }

    public void typeFighterAceMakerAdjSideslipPlus() {
        this.k14WingspanType--;
        if (this.k14WingspanType < 0) this.k14WingspanType = 0;
        if (this.k14WingspanType == 0) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: MiG-17/19/21");
        } else if (this.k14WingspanType == 1) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: MiG-15");
        } else if (this.k14WingspanType == 2) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: Me-262");
        } else if (this.k14WingspanType == 3) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: Pe-2");
        } else if (this.k14WingspanType == 4) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: 60ft");
        } else if (this.k14WingspanType == 5) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: Canberra Bomber");
        } else if (this.k14WingspanType == 6) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: Yak-28/Il-28");
        } else if (this.k14WingspanType == 7) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: C-47");
        } else if (this.k14WingspanType == 8) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: Tu-16");
        } else if (this.k14WingspanType == 9 && this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: Tu-4");
    }

    public void typeFighterAceMakerAdjSideslipMinus() {
        this.k14WingspanType++;
        if (this.k14WingspanType > 9) this.k14WingspanType = 9;
        if (this.k14WingspanType == 0) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: MiG-17/19/21");
        } else if (this.k14WingspanType == 1) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: MiG-15");
        } else if (this.k14WingspanType == 2) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: Me-262");
        } else if (this.k14WingspanType == 3) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: Pe-2");
        } else if (this.k14WingspanType == 4) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: 60ft");
        } else if (this.k14WingspanType == 5) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: Canberra Bomber");
        } else if (this.k14WingspanType == 6) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: Yak-28/Il-28");
        } else if (this.k14WingspanType == 7) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: C-47");
        } else if (this.k14WingspanType == 8) {
            if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: Tu-16");
        } else if (this.k14WingspanType == 9 && this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Wingspan Selected: Tu-4");
    }

    public void typeFighterAceMakerReplicateToNet(NetMsgGuaranted paramNetMsgGuaranted) throws IOException {
        paramNetMsgGuaranted.writeByte(this.k14Mode);
        paramNetMsgGuaranted.writeByte(this.k14WingspanType);
        paramNetMsgGuaranted.writeFloat(this.k14Distance);
    }

    public void typeFighterAceMakerReplicateFromNet(NetMsgInput paramNetMsgInput) throws IOException {
        this.k14Mode = paramNetMsgInput.readByte();
        this.k14WingspanType = paramNetMsgInput.readByte();
        this.k14Distance = paramNetMsgInput.readFloat();
    }

    public void doMurderPilot(int paramInt) {
        switch (paramInt) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                break;
        }
    }

    public void doEjectCatapult() {
        new MsgAction(false, this) {

            public void doAction(Object obj) {
                Aircraft aircraft = (Aircraft) obj;
                if (!Actor.isValid(aircraft)) return;
                else {
                    Loc loc = new Loc();
                    Loc loc1 = new Loc();
                    Vector3d vector3d = new Vector3d(0.0D, 0.0D, 10D);
                    HookNamed hooknamed = new HookNamed(aircraft, "_ExternalSeat01");
                    aircraft.pos.getAbs(loc1);
                    hooknamed.computePos(aircraft, loc1, loc);
                    loc.transform(vector3d);
                    vector3d.x += aircraft.FM.Vwld.x;
                    vector3d.y += aircraft.FM.Vwld.y;
                    vector3d.z += aircraft.FM.Vwld.z;
                    new EjectionSeat(2, loc, vector3d, aircraft);
                    return;
                }
            }

        };
        this.hierMesh().chunkVisible("Seat_D0", false);
        this.FM.setTakenMortalDamage(true, null);
        this.FM.CT.WeaponControl[0] = false;
        this.FM.CT.WeaponControl[1] = false;
        this.FM.CT.bHasAileronControl = false;
        this.FM.CT.bHasRudderControl = false;
        this.FM.CT.bHasElevatorControl = false;
    }

    public void moveCockpitDoor(float paramFloat) {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(paramFloat, 0.01F, 0.95F, 0.0F, 0.9F);
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        float f = (float) Math.sin(Aircraft.cvt(paramFloat, 0.4F, 0.99F, 0.0F, (float) Math.PI));
        this.hierMesh().chunkSetAngles("Pilot1_D0", 0.0F, 0.0F, 9F * f);
        this.hierMesh().chunkSetAngles("Head1_D0", 14F * f, 0.0F, 0.0F);
        if (Config.isUSE_RENDER()) {
            if (Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null) Main3D.cur3D().cockpits[0].onDoorMoved(paramFloat);
            this.setDoorSnd(paramFloat);
        }
    }

    public static void moveGear(HierMesh paramHierMesh, float paramFloat) {
        if (Math.abs(paramFloat) < 0.27F) {
            paramHierMesh.chunkSetAngles("GearL6_D0", 0.0F, Aircraft.cvt(paramFloat, 0.15F, 0.26F, 0.0F, -90F), 0.0F);
            paramHierMesh.chunkSetAngles("GearR6_D0", 0.0F, Aircraft.cvt(paramFloat, 0.09F, 0.22F, 0.0F, -90F), 0.0F);
            paramHierMesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(paramFloat, 0.0F, 0.11F, 0.0F, -90F), 0.0F);
        } else {
            paramHierMesh.chunkSetAngles("GearL6_D0", 0.0F, Aircraft.cvt(paramFloat, 0.65F, 0.74F, -90F, 0.0F), 0.0F);
            paramHierMesh.chunkSetAngles("GearR6_D0", 0.0F, Aircraft.cvt(paramFloat, 0.67F, 0.78F, -90F, 0.0F), 0.0F);
            paramHierMesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(paramFloat, 0.89F, 0.99F, -90F, 0.0F), 0.0F);
        }
        paramHierMesh.chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(paramFloat, 0.23F, 0.65F, 0.0F, -85F), 0.0F);
        paramHierMesh.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(paramFloat, 0.23F, 0.65F, 0.0F, -85F), 0.0F);
        paramHierMesh.chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(paramFloat, 0.28F, 0.7F, 0.0F, -85F), 0.0F);
        paramHierMesh.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(paramFloat, 0.28F, 0.7F, 0.0F, -85F), 0.0F);
        paramHierMesh.chunkSetAngles("GearC10_D0", 0.0F, Aircraft.cvt(paramFloat, 0.69F, 0.74F, 0.0F, -90F), 0.0F);
        paramHierMesh.chunkSetAngles("GearC2_D0", 0.0F, Aircraft.cvt(paramFloat, 0.63F, 0.99F, 0.0F, -105F), 0.0F);
        paramHierMesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(paramFloat, 0.63F, 0.99F, 0.0F, -95F), 0.0F);
        paramHierMesh.chunkSetAngles("Gear5e_D0", 0.0F, Aircraft.cvt(paramFloat, 0.63F, 0.99F, 0.0F, -90F), 0.0F);
    }

    protected void moveGear(float paramFloat) {
        moveGear(this.hierMesh(), paramFloat);
    }

    public void moveWheelSink() {
        if (this.curctl == -1F) {
            this.curctl = this.oldctl = this.FM.CT.getBrake();
            this.H1 = 0.17F;
            this.FM.Gears.tailStiffness = 0.4F;
        } else this.curctl = this.FM.CT.getBrake();
        if (!this.FM.brakeShoe && this.FM.Gears.cgear) {
            if (this.curctl - this.oldctl < -0.02F) this.curctl = this.oldctl - 0.02F;
            if (this.curctl < 0.0F) this.curctl = 0.0F;
            float tr = 0.25F * this.curctl * Math.max(Aircraft.cvt(this.FM.EI.engines[0].getThrustOutput(), 0.5F, 0.8F, 0.0F, 1.0F), Aircraft.cvt(this.FM.getSpeedKMH(), 0.0F, 80F, 0.0F, 1.0F));
            this.FM.setGC_Gear_Shift(this.H1 - tr);
            this.resetYPRmodifier();
            Aircraft.xyz[0] = -0.4F * tr;
            float f = Aircraft.cvt(this.FM.Gears.gWheelSinking[2] - Aircraft.xyz[0], 0.0F, 0.45F, 0.0F, 1.0F);
            this.hierMesh().chunkSetLocate("GearC6_D0", Aircraft.xyz, Aircraft.ypr);
            this.hierMesh().chunkSetAngles("GearC8_D0", 0.0F, -37.5F * f, 0.0F);
            this.hierMesh().chunkSetAngles("GearC9_D0", 0.0F, -75F * f, 0.0F);
        }
        this.oldctl = this.curctl;
    }

    protected void moveRudder(float paramFloat) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -30F * paramFloat, 0.0F);
        if (this.FM.CT.GearControl > 0.5F) this.hierMesh().chunkSetAngles("GearC7_D0", 0.0F, -50F * paramFloat, 0.0F);
    }

    protected void moveElevator(float f) {
        this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -12F * f, 0.0F);
        this.hierMesh().chunkSetAngles("VatorR_D0", 0.0F, -12F * f, 0.0F);
    }

    protected void moveFlap(float paramFloat) {
        float f = -45F * paramFloat;
        this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, f, 0.0F);
        this.hierMesh().chunkSetAngles("Flap02_D0", 0.0F, f, 0.0F);
    }

    protected void moveFan(float f) {
    }

    protected void hitBone(String paramString, Shot paramShot, Point3d paramPoint3d) {
        int ii = this.part(paramString);
        this.transsonicEffects.reduceSensitivity(ii);
        if (paramString.startsWith("xx")) {
            if (paramString.startsWith("xxarmor")) {
                this.debuggunnery("Armor: Hit..");
                if (paramString.endsWith("p1")) {
                    this.getEnergyPastArmor(13.35D / (Math.abs(Aircraft.v1.x) + 0.0001D), paramShot);
                    if (paramShot.power <= 0.0F) this.doRicochetBack(paramShot);
                } else if (paramString.endsWith("p2")) this.getEnergyPastArmor(8.770001F, paramShot);
                else if (paramString.endsWith("g1")) {
                    this.getEnergyPastArmor(World.Rnd().nextFloat(40F, 60F) / (Math.abs(Aircraft.v1.x) + 0.0001D), paramShot);
                    this.FM.AS.setCockpitState(paramShot.initiator, this.FM.AS.astateCockpitState | 2);
                    if (paramShot.power <= 0.0F) this.doRicochetBack(paramShot);
                }
            } else if (paramString.startsWith("xxcontrols")) {
                this.debuggunnery("Controls: Hit..");
                int i = paramString.charAt(10) - 48;
                switch (i) {
                    case 1:
                    case 2:
                        if (World.Rnd().nextFloat() < 0.5F && this.getEnergyPastArmor(1.1F, paramShot) > 0.0F) {
                            this.debuggunnery("Controls: Ailerones Controls: Out..");
                            this.FM.AS.setControlsDamage(paramShot.initiator, 0);
                        }
                        break;

                    case 3:
                    case 4:
                        if (this.getEnergyPastArmor(World.Rnd().nextFloat(0.5F, 2.93F), paramShot) > 0.0F && World.Rnd().nextFloat() < 0.25F) {
                            this.debuggunnery("Controls: Elevator Controls: Disabled / Strings Broken..");
                            this.FM.AS.setControlsDamage(paramShot.initiator, 1);
                        }
                        if (this.getEnergyPastArmor(World.Rnd().nextFloat(0.5F, 2.93F), paramShot) > 0.0F && World.Rnd().nextFloat() < 0.25F) {
                            this.debuggunnery("Controls: Rudder Controls: Disabled / Strings Broken..");
                            this.FM.AS.setControlsDamage(paramShot.initiator, 2);
                        }
                        break;
                }
            } else if (paramString.startsWith("xxeng1")) {
                this.debuggunnery("Engine Module: Hit..");
                if (paramString.endsWith("bloc")) this.getEnergyPastArmor(World.Rnd().nextFloat(0.0F, 60F) / (Math.abs(Aircraft.v1.x) + 0.0001D), paramShot);
                if (paramString.endsWith("cams") && this.getEnergyPastArmor(0.45F, paramShot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[0].getCylindersRatio() * 20F) {
                    this.FM.EI.engines[0].setCyliderKnockOut(paramShot.initiator, World.Rnd().nextInt(1, (int) (paramShot.power / 4800F)));
                    this.debuggunnery("Engine Module: Engine Cams Hit, " + this.FM.EI.engines[0].getCylindersOperable() + "/" + this.FM.EI.engines[0].getCylinders() + " Left..");
                    if (World.Rnd().nextFloat() < paramShot.power / 24000F) {
                        this.FM.AS.hitEngine(paramShot.initiator, 0, 2);
                        this.debuggunnery("Engine Module: Engine Cams Hit - Engine Fires..");
                    }
                    if (paramShot.powerType == 3 && World.Rnd().nextFloat() < 0.75F) {
                        this.FM.AS.hitEngine(paramShot.initiator, 0, 1);
                        this.debuggunnery("Engine Module: Engine Cams Hit (2) - Engine Fires..");
                    }
                }
                if (paramString.endsWith("eqpt") && World.Rnd().nextFloat() < paramShot.power / 24000F) {
                    this.FM.AS.hitEngine(paramShot.initiator, 0, 3);
                    this.debuggunnery("Engine Module: Hit - Engine Fires..");
                }
                if (paramString.endsWith("exht"));
            } else if (paramString.startsWith("xxmgun0")) {
                int i = paramString.charAt(7) - 49;
                if (this.getEnergyPastArmor(1.5F, paramShot) > 0.0F) {
                    this.debuggunnery("Armament: mnine Gun (" + i + ") Disabled..");
                    this.FM.AS.setJamBullets(0, i);
                    this.getEnergyPastArmor(World.Rnd().nextFloat(0.5F, 23.325F), paramShot);
                }
            } else if (paramString.startsWith("xxtank")) {
                int i = paramString.charAt(6) - 49;
                if (this.getEnergyPastArmor(0.1F, paramShot) > 0.0F && World.Rnd().nextFloat() < 0.25F) {
                    if (this.FM.AS.astateTankStates[i] == 0) {
                        this.debuggunnery("Fuel Tank (" + i + "): Pierced..");
                        this.FM.AS.hitTank(paramShot.initiator, i, 1);
                        this.FM.AS.doSetTankState(paramShot.initiator, i, 1);
                    }
                    if (paramShot.powerType == 3 && World.Rnd().nextFloat() < 0.075F) {
                        this.FM.AS.hitTank(paramShot.initiator, i, 2);
                        this.debuggunnery("Fuel Tank (" + i + "): Hit..");
                    }
                }
            } else if (paramString.startsWith("xxspar")) {
                this.debuggunnery("Spar Construction: Hit..");
                if (paramString.startsWith("xxsparlm") && this.chunkDamageVisible("WingLMid") > 2 && this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.5F), paramShot) > 0.0F) {
                    this.debuggunnery("Spar Construction: WingLMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", paramShot.initiator);
                }
                if (paramString.startsWith("xxsparrm") && this.chunkDamageVisible("WingRMid") > 2 && this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.5F), paramShot) > 0.0F) {
                    this.debuggunnery("Spar Construction: WingRMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", paramShot.initiator);
                }
                if (paramString.startsWith("xxsparlo") && this.chunkDamageVisible("WingLOut") > 2 && this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.5F), paramShot) > 0.0F) {
                    this.debuggunnery("Spar Construction: WingLOut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLOut_D3", paramShot.initiator);
                }
                if (paramString.startsWith("xxsparro") && this.chunkDamageVisible("WingROut") > 2 && this.getEnergyPastArmor(16.5F * World.Rnd().nextFloat(1.0F, 1.5F), paramShot) > 0.0F) {
                    this.debuggunnery("Spar Construction: WingROut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingROut_D3", paramShot.initiator);
                }
            } else if (paramString.startsWith("xxhyd")) this.FM.AS.setInternalDamage(paramShot.initiator, 3);
            else if (paramString.startsWith("xxpnm")) this.FM.AS.setInternalDamage(paramShot.initiator, 1);
        } else {
            if (paramString.startsWith("xcockpit")) {
                this.FM.AS.setCockpitState(paramShot.initiator, this.FM.AS.astateCockpitState | 1);
                this.getEnergyPastArmor(0.05F, paramShot);
            }
            if (paramString.startsWith("xcf")) this.hitChunk("CF", paramShot);
            else if (paramString.startsWith("xnose")) this.hitChunk("Nose", paramShot);
            else if (paramString.startsWith("xtail")) {
                if (this.chunkDamageVisible("Tail1") < 3) this.hitChunk("Tail1", paramShot);
            } else if (paramString.startsWith("xkeel")) {
                if (this.chunkDamageVisible("Keel1") < 2) this.hitChunk("Keel1", paramShot);
            } else if (paramString.startsWith("xrudder")) this.hitChunk("Rudder1", paramShot);
            else if (paramString.startsWith("xstab")) {
                if (paramString.startsWith("xstabl") && this.chunkDamageVisible("StabL") < 2) this.hitChunk("StabL", paramShot);
                if (paramString.startsWith("xstabr") && this.chunkDamageVisible("StabR") < 1) this.hitChunk("StabR", paramShot);
            } else if (paramString.startsWith("xvator")) {
                if (paramString.startsWith("xvatorl")) this.hitChunk("VatorL", paramShot);
                if (paramString.startsWith("xvatorr")) this.hitChunk("VatorR", paramShot);
            } else if (paramString.startsWith("xwing")) {
                if (paramString.startsWith("xwinglin") && this.chunkDamageVisible("WingLIn") < 3) this.hitChunk("WingLIn", paramShot);
                if (paramString.startsWith("xwingrin") && this.chunkDamageVisible("WingRIn") < 3) this.hitChunk("WingRIn", paramShot);
                if (paramString.startsWith("xwinglmid") && this.chunkDamageVisible("WingLMid") < 3) this.hitChunk("WingLMid", paramShot);
                if (paramString.startsWith("xwingrmid") && this.chunkDamageVisible("WingRMid") < 3) this.hitChunk("WingRMid", paramShot);
                if (paramString.startsWith("xwinglout") && this.chunkDamageVisible("WingLOut") < 3) this.hitChunk("WingLOut", paramShot);
                if (paramString.startsWith("xwingrout") && this.chunkDamageVisible("WingROut") < 3) this.hitChunk("WingROut", paramShot);
            } else if (paramString.startsWith("xarone")) {
                if (paramString.startsWith("xaronel")) this.hitChunk("AroneL", paramShot);
                if (paramString.startsWith("xaroner")) this.hitChunk("AroneR", paramShot);
            } else if (paramString.startsWith("xgear")) {
                if (paramString.endsWith("1") && World.Rnd().nextFloat() < 0.05F) {
                    this.debuggunnery("Hydro System: Disabled..");
                    this.FM.AS.setInternalDamage(paramShot.initiator, 0);
                }
                if (paramString.endsWith("2") && World.Rnd().nextFloat() < 0.1F && this.getEnergyPastArmor(World.Rnd().nextFloat(1.2F, 3.435F), paramShot) > 0.0F) {
                    this.debuggunnery("Undercarriage: Stuck..");
                    this.FM.AS.setInternalDamage(paramShot.initiator, 3);
                }
            } else if (paramString.startsWith("xpilot") || paramString.startsWith("xhead")) {
                int i = 0;
                int j;
                if (paramString.endsWith("a")) {
                    i = 1;
                    j = paramString.charAt(6) - 49;
                } else if (paramString.endsWith("b")) {
                    i = 2;
                    j = paramString.charAt(6) - 49;
                } else j = paramString.charAt(5) - 49;
                this.hitFlesh(j, paramShot, i);
            }
        }
    }

    protected boolean cutFM(int paramInt1, int paramInt2, Actor paramActor) {
        switch (paramInt1) {
            case 13:
                this.FM.Gears.cgear = false;
                float t = World.Rnd().nextFloat(0.0F, 1.0F);
                if (t < 0.1F) {
                    this.FM.AS.hitEngine(this, 0, 100);
                    if (World.Rnd().nextFloat(0.0F, 1.0F) < 0.49D) this.FM.EI.engines[0].setEngineDies(paramActor);
                } else if (t > 0.55D) this.FM.EI.engines[0].setEngineDies(paramActor);
                return super.cutFM(paramInt1, paramInt2, paramActor);

            case 19:
                this.FM.EI.engines[0].setEngineDies(paramActor);
                return super.cutFM(paramInt1, paramInt2, paramActor);
        }
        return super.cutFM(paramInt1, paramInt2, paramActor);
    }

    public void typeFighterAceMakerRangeFinder() {
        if (this.k14Mode == 2) return;
        if (!Config.isUSE_RENDER()) return;
        hunted = Main3D.cur3D().getViewPadlockEnemy();
        if (hunted == null) {
            this.k14Distance = 200F;
            hunted = War.GetNearestEnemyAircraft(this.FM.actor, 2700F, 9);
        }
        if (hunted != null) {
            this.k14Distance = (float) this.FM.actor.pos.getAbsPoint().distance(hunted.pos.getAbsPoint());
            if (this.k14Distance > 800F) this.k14Distance = 800F;
            else if (this.k14Distance < 200F) this.k14Distance = 200F;
        }
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
    
    public void engineSurge(float f) {
        if (this.FM.AS.isMaster()) if (this.curthrl == -1F) this.curthrl = this.oldthrl = this.FM.EI.engines[0].getControlThrottle();
        else {
            this.curthrl = this.FM.EI.engines[0].getControlThrottle();
            if (this.curthrl < 1.05F) {
                if ((this.curthrl - this.oldthrl) / f > 20F && this.FM.EI.engines[0].getRPM() < 3200F && this.FM.EI.engines[0].getStage() == 6 && World.Rnd().nextFloat() < 0.4F) {
                    if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Compressor Stall!");
                    this.playSound("weapon.MGunMk108s", true);
                    this.engineSurgeDamage += 0.01D * (this.FM.EI.engines[0].getRPM() / 1000F);
                    this.FM.EI.engines[0].doSetReadyness(this.FM.EI.engines[0].getReadyness() - this.engineSurgeDamage);
                    if (World.Rnd().nextFloat() < 0.05F && this.FM instanceof RealFlightModel && ((RealFlightModel) this.FM).isRealMode()) this.FM.AS.hitEngine(this, 0, 100);
                    if (World.Rnd().nextFloat() < 0.05F && this.FM instanceof RealFlightModel && ((RealFlightModel) this.FM).isRealMode()) this.FM.EI.engines[0].setEngineDies(this);
                }
                if ((this.curthrl - this.oldthrl) / f < -20F && (this.curthrl - this.oldthrl) / f > -100F && this.FM.EI.engines[0].getRPM() < 3200F && this.FM.EI.engines[0].getStage() == 6) {
                    this.playSound("weapon.MGunMk108s", true);
                    this.engineSurgeDamage += 0.001D * (this.FM.EI.engines[0].getRPM() / 1000F);
                    this.FM.EI.engines[0].doSetReadyness(this.FM.EI.engines[0].getReadyness() - this.engineSurgeDamage);
                    if (World.Rnd().nextFloat() < 0.4F && this.FM instanceof RealFlightModel && ((RealFlightModel) this.FM).isRealMode()) {
                        if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Engine Flameout!");
                        this.FM.EI.engines[0].setEngineStops(this);
                    } else if (this.FM.actor == World.getPlayerAircraft()) HUD.log(AircraftHotKeys.hudLogWeaponId, "Compressor Stall!");
                }
            }
            this.oldthrl = this.curthrl;
        }
    }

    public void update(float paramFloat) {
        if ((this.FM.AS.bIsAboutToBailout || this.overrideBailout) && !this.ejectComplete && this.FM.getSpeedKMH() > 15F) {
            this.overrideBailout = true;
            this.FM.AS.bIsAboutToBailout = false;
            this.bailout();
        }
        if (Config.isUSE_RENDER()) {
            this.setExhaustFlame((int) Aircraft.cvt(this.FM.EI.engines[0].getThrustOutput(), 0.7F, 0.87F, 0.0F, 12F), 0);
            if (this.FM.AS.isMaster()) {
                if (this.FM.EI.engines[0].getPowerOutput() > 0.45F && this.FM.EI.engines[0].getStage() == 6) {
                    if (this.FM.EI.engines[0].getPowerOutput() > 0.65F) this.FM.AS.setSootState(this, 0, 3);
                    else this.FM.AS.setSootState(this, 0, 2);
                } else this.FM.AS.setSootState(this, 0, 0);
                if (this.FM instanceof RealFlightModel) {
                    this.transsonicEffects.update();
                }
            }
        }
        this.engineSurge(paramFloat);
        this.typeFighterAceMakerRangeFinder();
        this.checkHydraulicStatus();
        this.moveHydraulics(paramFloat);
        this.soundbarier();
        super.update(paramFloat);
    }

    public void doSetSootState(int i, int j) {
        for (int k = 0; k < 2; k++) {
            if (this.FM.AS.astateSootEffects[i][k] != null) Eff3DActor.finish(this.FM.AS.astateSootEffects[i][k]);
            this.FM.AS.astateSootEffects[i][k] = null;
        }

        switch (j) {
            case 1:
                this.FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "ES_01"), null, 1.0F, "3DO/Effects/Aircraft/BlackSmallTSPD.eff", -1F);
                this.FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "ES_02"), null, 1.0F, "3DO/Effects/Aircraft/BlackSmallTSPD.eff", -1F);
                break;

            case 3:
                this.FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "EF_01"), null, 1.0F, "3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1F);
                // fall through

            case 2:
                this.FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "EF_01"), null, 0.75F, "3DO/Effects/Aircraft/TurboZippo.eff", -1F);
                break;

            case 5:
                this.FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "EF_01"), null, 1.5F, "3DO/Effects/Aircraft/TurboJRD1100F.eff", -1F);
                // fall through

            case 4:
                this.FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, this.findHook("_Engine" + (i + 1) + "EF_01"), null, 1.0F, "3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1F);
                break;
        }
    }

    protected void moveAirBrake(float paramFloat) {
        this.resetYPRmodifier();
        this.hierMesh().chunkSetAngles("Brake01_D0", 0.0F, -70F * paramFloat, 0.0F);
        this.hierMesh().chunkSetAngles("BrakeB01_D0", 0.0F, -30F * paramFloat, 0.0F);
        this.hierMesh().chunkSetAngles("Brake02_D0", 0.0F, -70F * paramFloat, 0.0F);
        this.hierMesh().chunkSetAngles("BrakeB02_D0", 0.0F, 30F * paramFloat, 0.0F);
        if (paramFloat < 0.2D) Aircraft.xyz[2] = Aircraft.cvt(paramFloat, 0.01F, 0.18F, 0.0F, -0.05F);
        else Aircraft.xyz[2] = Aircraft.cvt(paramFloat, 0.22F, 0.99F, -0.05F, -0.22F);
        this.hierMesh().chunkSetLocate("BrakeB01e_D0", Aircraft.xyz, Aircraft.ypr);
        this.hierMesh().chunkSetLocate("BrakeB02e_D0", Aircraft.xyz, Aircraft.ypr);
    }

    public void setExhaustFlame(int stage, int i) {
        if (i == 0) switch (stage) {
            case 0:
                this.hierMesh().chunkVisible("Exhaust1", false);
                this.hierMesh().chunkVisible("Exhaust2", false);
                this.hierMesh().chunkVisible("Exhaust3", false);
                this.hierMesh().chunkVisible("Exhaust4", false);
                this.hierMesh().chunkVisible("Exhaust5", false);
                break;

            case 1:
                this.hierMesh().chunkVisible("Exhaust1", true);
                this.hierMesh().chunkVisible("Exhaust2", false);
                this.hierMesh().chunkVisible("Exhaust3", false);
                this.hierMesh().chunkVisible("Exhaust4", false);
                this.hierMesh().chunkVisible("Exhaust5", false);
                break;

            case 2:
                this.hierMesh().chunkVisible("Exhaust1", false);
                this.hierMesh().chunkVisible("Exhaust2", true);
                this.hierMesh().chunkVisible("Exhaust3", false);
                this.hierMesh().chunkVisible("Exhaust4", false);
                this.hierMesh().chunkVisible("Exhaust5", false);
                break;

            case 3:
                this.hierMesh().chunkVisible("Exhaust1", true);
                this.hierMesh().chunkVisible("Exhaust2", true);
                this.hierMesh().chunkVisible("Exhaust3", false);
                this.hierMesh().chunkVisible("Exhaust4", false);
                this.hierMesh().chunkVisible("Exhaust5", false);
                // fall through

            case 4:
                this.hierMesh().chunkVisible("Exhaust1", false);
                this.hierMesh().chunkVisible("Exhaust2", false);
                this.hierMesh().chunkVisible("Exhaust3", true);
                this.hierMesh().chunkVisible("Exhaust4", false);
                this.hierMesh().chunkVisible("Exhaust5", false);
                break;

            case 5:
                this.hierMesh().chunkVisible("Exhaust1", true);
                this.hierMesh().chunkVisible("Exhaust2", false);
                this.hierMesh().chunkVisible("Exhaust3", true);
                this.hierMesh().chunkVisible("Exhaust4", false);
                this.hierMesh().chunkVisible("Exhaust5", false);
                break;

            case 6:
                this.hierMesh().chunkVisible("Exhaust1", false);
                this.hierMesh().chunkVisible("Exhaust2", true);
                this.hierMesh().chunkVisible("Exhaust3", true);
                this.hierMesh().chunkVisible("Exhaust4", false);
                this.hierMesh().chunkVisible("Exhaust5", false);
                break;

            case 7:
                this.hierMesh().chunkVisible("Exhaust1", true);
                this.hierMesh().chunkVisible("Exhaust2", false);
                this.hierMesh().chunkVisible("Exhaust3", false);
                this.hierMesh().chunkVisible("Exhaust4", true);
                this.hierMesh().chunkVisible("Exhaust5", false);
                break;

            case 8:
                this.hierMesh().chunkVisible("Exhaust1", false);
                this.hierMesh().chunkVisible("Exhaust2", true);
                this.hierMesh().chunkVisible("Exhaust3", false);
                this.hierMesh().chunkVisible("Exhaust4", true);
                this.hierMesh().chunkVisible("Exhaust5", false);
                break;

            case 9:
                this.hierMesh().chunkVisible("Exhaust1", false);
                this.hierMesh().chunkVisible("Exhaust2", false);
                this.hierMesh().chunkVisible("Exhaust3", true);
                this.hierMesh().chunkVisible("Exhaust4", true);
                this.hierMesh().chunkVisible("Exhaust5", false);
                break;

            case 10:
                this.hierMesh().chunkVisible("Exhaust1", true);
                this.hierMesh().chunkVisible("Exhaust2", false);
                this.hierMesh().chunkVisible("Exhaust3", false);
                this.hierMesh().chunkVisible("Exhaust4", false);
                this.hierMesh().chunkVisible("Exhaust5", true);
                break;

            case 11:
                this.hierMesh().chunkVisible("Exhaust1", false);
                this.hierMesh().chunkVisible("Exhaust2", true);
                this.hierMesh().chunkVisible("Exhaust3", false);
                this.hierMesh().chunkVisible("Exhaust4", false);
                this.hierMesh().chunkVisible("Exhaust5", true);
                break;

            case 12:
                this.hierMesh().chunkVisible("Exhaust1", false);
                this.hierMesh().chunkVisible("Exhaust2", false);
                this.hierMesh().chunkVisible("Exhaust3", true);
                this.hierMesh().chunkVisible("Exhaust4", false);
                this.hierMesh().chunkVisible("Exhaust5", true);
                break;

            default:
                this.hierMesh().chunkVisible("Exhaust1", false);
                this.hierMesh().chunkVisible("Exhaust2", false);
                this.hierMesh().chunkVisible("Exhaust3", false);
                this.hierMesh().chunkVisible("Exhaust4", false);
                this.hierMesh().chunkVisible("Exhaust5", false);
                break;
        }
    }

    private void bailout() {
        if (this.overrideBailout) if (this.FM.AS.astateBailoutStep >= 0 && this.FM.AS.astateBailoutStep < 2) {
            if (this.FM.CT.cockpitDoorControl > 0.5F && this.FM.CT.getCockpitDoor() > 0.5F) {
                this.FM.AS.astateBailoutStep = 11;
                this.doRemoveBlisters();
            } else this.FM.AS.astateBailoutStep = 2;
        } else if (this.FM.AS.astateBailoutStep >= 2 && this.FM.AS.astateBailoutStep <= 3) {
            switch (this.FM.AS.astateBailoutStep) {
                case 2:
                    if (this.FM.CT.cockpitDoorControl < 0.5F) this.doRemoveBlister1();
                    break;

                case 3:
                    this.doRemoveBlisters();
                    break;
            }
            if (this.FM.AS.isMaster()) this.FM.AS.netToMirrors(20, this.FM.AS.astateBailoutStep, 1, null);
            AircraftState tmp178_177 = this.FM.AS;
            tmp178_177.astateBailoutStep = (byte) (tmp178_177.astateBailoutStep + 1);
            if (this.FM.AS.astateBailoutStep == 4) this.FM.AS.astateBailoutStep = 11;
        } else if (this.FM.AS.astateBailoutStep >= 11 && this.FM.AS.astateBailoutStep <= 19) {
            int i = this.FM.AS.astateBailoutStep;
            if (this.FM.AS.isMaster()) this.FM.AS.netToMirrors(20, this.FM.AS.astateBailoutStep, 1, null);
            AircraftState tmp383_382 = this.FM.AS;
            tmp383_382.astateBailoutStep = (byte) (tmp383_382.astateBailoutStep + 1);
            if (i == 11) {
                this.FM.setTakenMortalDamage(true, null);
                if (this.FM instanceof Maneuver && ((Maneuver) this.FM).get_maneuver() != 44) {
                    World.cur();
                    if (this.FM.AS.actor != World.getPlayerAircraft()) ((Maneuver) this.FM).set_maneuver(44);
                }
            }
            if (this.FM.AS.astatePilotStates[i - 11] < 99) {
                this.doRemoveBodyFromPlane(i - 10);
                if (i == 11) {
                    this.doEjectCatapult();
                    this.FM.setTakenMortalDamage(true, null);
                    this.FM.CT.WeaponControl[0] = false;
                    this.FM.CT.WeaponControl[1] = false;
                    this.FM.AS.astateBailoutStep = -1;
                    this.overrideBailout = false;
                    this.FM.AS.bIsAboutToBailout = true;
                    this.ejectComplete = true;
                    if (i > 10 && i <= 19) EventLog.onBailedOut(this, i - 11);
                }
            }
        }
    }

    private final void doRemoveBlister1() {
        if (this.hierMesh().chunkFindCheck("Blister1_D0") != -1 && this.FM.AS.getPilotHealth(0) > 0.0F) {
            this.hierMesh().hideSubTrees("Blister1_D0");
            Wreckage localWreckage = new Wreckage(this, this.hierMesh().chunkFind("Blister1_D0"));
            localWreckage.collide(false);
            Vector3d localVector3d = new Vector3d();
            localVector3d.set(this.FM.Vwld);
            localWreckage.setSpeed(localVector3d);
        }
    }

    private final void doRemoveBlisters() {
        for (int i = 2; i < 10; i++)
            if (this.hierMesh().chunkFindCheck("Blister" + i + "_D0") != -1 && this.FM.AS.getPilotHealth(i - 1) > 0.0F) {
                this.hierMesh().hideSubTrees("Blister" + i + "_D0");
                Wreckage localWreckage = new Wreckage(this, this.hierMesh().chunkFind("Blister" + i + "_D0"));
                localWreckage.collide(false);
                Vector3d localVector3d = new Vector3d();
                localVector3d.set(this.FM.Vwld);
                localWreckage.setSpeed(localVector3d);
            }

    }

    protected boolean          bSlatsOff;
//    private final _cls0        $1                     = new _cls0(0.0F, 13000F, 0.65F, 1.0F, 0.01F, 1.0F, 0.2F, 1.0F, 0.5F, 0.6F, 0.0F);
    private final TransonicEffects transsonicEffects;
    private float              oldctl;
    private float              curctl;
    private float              oldthrl;
    private float              curthrl;
    public int                 k14Mode;
    public int                 k14WingspanType;
    public float               k14Distance;
    public float               AirBrakeControl;
    private boolean            overrideBailout;
    private boolean            ejectComplete;
    private float              lightTime;
    private float              ft;
    private LightPointWorld    lLight[];
    private Hook               lLightHook[]           = { null, null, null, null };
    private static Loc         lLightLoc1             = new Loc();
    private static Point3d     lLightP1               = new Point3d();
    private static Point3d     lLightP2               = new Point3d();
    private static Point3d     lLightPL               = new Point3d();
    private float              H1;
    public static boolean      bChangedPit            = false;
    public static int          LockState              = 0;
    static Actor               hunted                 = null;
    private float              engineSurgeDamage;
    private float              gearTargetAngle;
    private float              gearCurrentAngle;
    public boolean             hasHydraulicPressure;
    private static final float NEG_G_TOLERANCE_FACTOR = 1.5F;
    private static final float NEG_G_TIME_FACTOR      = 1.5F;
    private static final float NEG_G_RECOVERY_FACTOR  = 1F;
    private static final float POS_G_TOLERANCE_FACTOR = 2F;
    private static final float POS_G_TIME_FACTOR      = 2F;
    private static final float POS_G_RECOVERY_FACTOR  = 2F;

    static {
        Class localClass = F_86F.class;
        Property.set(localClass, "originCountry", PaintScheme.countryUSA);
    }

}
