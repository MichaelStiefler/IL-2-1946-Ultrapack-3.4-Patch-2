package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.HookNamed;
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

public class Mig_15F extends Scheme1 implements TypeSupersonic, TypeFighter, TypeBNZFighter, TypeFighterAceMaker {
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

    public Mig_15F() {
        this.curst = false;
        this.oldctl = -1F;
        this.curctl = -1F;
        this.oldthrl = -1F;
        this.curthrl = -1F;
        this.overrideBailout = false;
        this.ejectComplete = false;
        this.k14Mode = 0;
        this.k14WingspanType = 0;
        this.k14Distance = 200F;
        this.engineSurgeDamage = 0.0F;
        this.transsonicEffects = new TransonicEffects(this, 0.0F, 14000F, 0.65F, 1.0F, 0.05F, 1.0F, 0.4F, 1.0F, 0.46F, 0.55F, 0.65F, 0.9F, 1.0F, 1.25F);
    }

    public boolean typeFighterAceMakerToggleAutomation() {
        this.k14Mode++;
        if (this.k14Mode > 2) this.k14Mode = 0;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerMode" + this.k14Mode);
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
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerWing" + this.k14WingspanType);
    }

    public void typeFighterAceMakerAdjSideslipMinus() {
        this.k14WingspanType++;
        if (this.k14WingspanType > 9) this.k14WingspanType = 9;
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

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.FM.AS.wantBeaconsNet(true);
        this.transsonicEffects.onAircraftLoaded();
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
            } else if (this.FM.CT.AirBrakeControl != 0.0F) this.FM.CT.AirBrakeControl = 0.0F;
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

    protected void nextDMGLevel(String paramString, int paramInt, Actor paramActor) {
        super.nextDMGLevel(paramString, paramInt, paramActor);
        if (this.FM.isPlayers()) bChangedPit = true;
    }

    protected void nextCUTLevel(String paramString, int paramInt, Actor paramActor) {
        super.nextCUTLevel(paramString, paramInt, paramActor);
        if (this.FM.isPlayers()) bChangedPit = true;
    }

    public void moveCockpitDoor(float paramFloat) {
        this.resetYPRmodifier();
        if (paramFloat < 0.1F) Aircraft.xyz[1] = Aircraft.cvt(paramFloat, 0.01F, 0.08F, 0.0F, 0.1F);
        else Aircraft.xyz[1] = Aircraft.cvt(paramFloat, 0.17F, 0.99F, 0.1F, 0.4F);
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if (Config.isUSE_RENDER()) {
            if (Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null) Main3D.cur3D().cockpits[0].onDoorMoved(paramFloat);
            this.setDoorSnd(paramFloat);
        }
    }

    public static void moveGear(HierMesh paramHierMesh, float paramFloat) {
        float f = Math.max(-paramFloat * 1500F, -90F);
        paramHierMesh.chunkSetAngles("GearC2_D0", 0.0F, -127F * paramFloat, 0.0F);
        paramHierMesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(paramFloat, 0.0F, 0.15F, 0.0F, -90F), 0.0F);
        paramHierMesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(paramFloat, 0.0F, 0.15F, 0.0F, -95F), 0.0F);
        paramHierMesh.chunkSetAngles("GearL2_D0", -90F * paramFloat, -38F * paramFloat, 90F * paramFloat);
        paramHierMesh.chunkSetAngles("GearR2_D0", 90F * paramFloat, -38F * paramFloat, -90F * paramFloat);
        paramHierMesh.chunkSetAngles("GearL4_D0", 0.0F, -115F * paramFloat, 0.0F);
        paramHierMesh.chunkSetAngles("GearR4_D0", 0.0F, -115F * paramFloat, 0.0F);
        paramHierMesh.chunkSetAngles("GearL6_D0", 0.0F, f, 0.0F);
        paramHierMesh.chunkSetAngles("GearR6_D0", 0.0F, f, 0.0F);
    }

    protected void moveGear(float paramFloat) {
        moveGear(this.hierMesh(), paramFloat);
    }

    public void moveWheelSink() {
        float f = Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.2F, 0.0F, 1.0F);
        this.resetYPRmodifier();
        Aircraft.xyz[2] = -0.2F * f;
        this.hierMesh().chunkSetLocate("GearC6_D0", Aircraft.xyz, Aircraft.ypr);
        this.hierMesh().chunkSetAngles("GearC8_D0", 0.0F, -15F * f, 0.0F);
    }

    protected void moveRudder(float paramFloat) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -30F * paramFloat, 0.0F);
        if (this.FM.CT.GearControl > 0.5F) this.hierMesh().chunkSetAngles("GearC7_D0", 0.0F, 40F * paramFloat, 0.0F);
    }

    protected void moveFlap(float paramFloat) {
        float f = -45F * paramFloat;
        this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, f, 0.0F);
        this.hierMesh().chunkSetAngles("Flap02_D0", 0.0F, f, 0.0F);
    }

    protected void moveFan(float f) {
    }

    protected void moveAirBrake(float paramFloat) {
        this.resetYPRmodifier();
        this.hierMesh().chunkSetAngles("Brake01_D0", 0.0F, -70F * paramFloat, 0.0F);
        this.hierMesh().chunkSetAngles("Brake02_D0", 0.0F, -70F * paramFloat, 0.0F);
    }

    protected void hitBone(String paramString, Shot paramShot, Point3d paramPoint3d) {
        int ii = this.part(paramString);
        this.transsonicEffects.reduceSensitivity(ii);
        if (paramString.startsWith("xx")) {
            if (paramString.startsWith("xxammo")) {
                if (World.Rnd().nextFloat(0.0F, 20000F) < paramShot.power && World.Rnd().nextFloat() < 0.25F) this.FM.AS.setJamBullets(0, World.Rnd().nextInt(0, 2));
                this.getEnergyPastArmor(11.4F, paramShot);
            } else if (paramString.startsWith("xxarmor")) {
                this.debuggunnery("Armor: Hit..");
                if (paramString.endsWith("p1")) {
                    this.getEnergyPastArmor(13.35D / (Math.abs(Aircraft.v1.x) + 0.0001D), paramShot);
                    if (paramShot.power <= 0.0F) this.doRicochetBack(paramShot);
                } else if (paramString.endsWith("p2")) this.getEnergyPastArmor(8.770001F, paramShot);
                else if (paramString.endsWith("P3")) this.getEnergyPastArmor(8.770001F, paramShot);
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
            } else if (paramString.startsWith("xxcannon0")) {
                int i = paramString.charAt(9) - 49;
                if (this.getEnergyPastArmor(1.5F, paramShot) > 0.0F) {
                    this.debuggunnery("Armament: Cannon (" + i + ") Disabled..");
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
            if (paramString.startsWith("xblister")) {
                this.FM.AS.setCockpitState(paramShot.initiator, this.FM.AS.astateCockpitState | 1);
                this.getEnergyPastArmor(0.05F, paramShot);
            }
            if (paramString.startsWith("xcf")) this.hitChunk("CF", paramShot);
            else if (paramString.startsWith("xnose")) this.hitChunk("Nose", paramShot);
            else if (paramString.startsWith("xTail")) {
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
            default:
                break;

            case 13:
                this.FM.Gears.cgear = false;
                float t = World.Rnd().nextFloat();
                if (t < 0.1F) {
                    this.FM.AS.hitEngine(this, 0, 100);
                    if (World.Rnd().nextFloat() < 0.49D) this.FM.EI.engines[0].setEngineDies(paramActor);
                    break;
                }
                if (t > 0.55D) this.FM.EI.engines[0].setEngineDies(paramActor);
                break;

            case 34:
                this.FM.Gears.lgear = false;
                break;

            case 37:
                this.FM.Gears.rgear = false;
                break;

            case 19:
                this.FM.CT.bHasAirBrakeControl = false;
                this.FM.EI.engines[0].setEngineDies(paramActor);
                break;

            case 11:
                this.FM.CT.bHasElevatorControl = false;
                this.FM.CT.bHasRudderControl = false;
                this.FM.CT.bHasRudderTrim = false;
                this.FM.CT.bHasElevatorTrim = false;
                break;
        }
        return super.cutFM(paramInt1, paramInt2, paramActor);
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
        if (Config.isUSE_RENDER()) {
            this.setExhaustFlame(Math.round(Aircraft.cvt(this.FM.EI.engines[0].getThrustOutput(), 0.7F, 0.87F, 0.0F, 12F)), 0);
            if (this.FM.AS.isMaster()) {
                if (this.FM.EI.engines[0].getPowerOutput() > 0.5F && this.FM.EI.engines[0].getStage() == 6) {
                    if (this.FM.EI.engines[0].getPowerOutput() > 0.5F) this.FM.AS.setSootState(this, 0, 3);
                } else this.FM.AS.setSootState(this, 0, 0);
                if (this.FM instanceof RealFlightModel) {
                    this.transsonicEffects.update();
                }
                if (this.curctl == -1F) this.curctl = this.oldctl = this.FM.CT.getAirBrake();
                else {
                    this.curctl = this.FM.CT.getAirBrake();
                    if (this.curctl > 0.05F) if (this.curctl - this.oldctl > 0.0F) this.curst = true;
                    else if (this.curctl - this.oldctl == 0.0F && this.oldctl == 1.0F) this.curst = true;
                    else this.curst = false;
                }
                this.oldctl = this.curctl;
            }
        }
        if ((this.FM.AS.bIsAboutToBailout || this.overrideBailout) && !this.ejectComplete && this.FM.getSpeedKMH() > 15F) {
            this.overrideBailout = true;
            this.FM.AS.bIsAboutToBailout = false;
            this.bailout();
        }
        this.soundbarier();
        this.engineSurge(paramFloat);
        super.update(paramFloat);
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

    public void doSetSootState(int paramInt1, int paramInt2) {
        for (int i = 0; i < 2; i++) {
            if (this.FM.AS.astateSootEffects[paramInt1][i] != null) Eff3DActor.finish(this.FM.AS.astateSootEffects[paramInt1][i]);
            this.FM.AS.astateSootEffects[paramInt1][i] = null;
        }

        switch (paramInt2) {
            case 3:
                this.FM.AS.astateSootEffects[paramInt1][0] = Eff3DActor.New(this, this.findHook("_Engine1EF_01"), null, 0.75F, "3DO/Effects/Aircraft/TurboZippo.eff", -1F);
                // fall through

            case 0:
            case 1:
            case 2:
            default:
                return;
        }
    }

    protected boolean     curst;
    private float         oldctl;
    private float         curctl;
    private float         oldthrl;
    private float         curthrl;
    private boolean       overrideBailout;
    private boolean       ejectComplete;
    public static boolean bChangedPit = false;
    private final TransonicEffects transsonicEffects;
    public int            k14Mode;
    public int            k14WingspanType;
    public float          k14Distance;
    private float         engineSurgeDamage;

    static {
        Class localClass = Mig_15F.class;
        Property.set(localClass, "originCountry", PaintScheme.countryRussia);
    }

}
