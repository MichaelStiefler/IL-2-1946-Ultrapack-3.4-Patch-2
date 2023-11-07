package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.AircraftState;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.Wreckage;
import com.maddox.il2.objects.weapons.GuidedMissileUtils;
import com.maddox.rts.MsgAction;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class DH116xyz extends Scheme1
    implements TypeFighter, TypeBNZFighter, TypeStormovik, TypeGSuit, TypeGuidedMissileCarrier, TypeCountermeasure, TypeThreatDetector, TypeFastJet, TypeSupersonic
{

    public DH116xyz()
    {
        overrideBailout = false;
        ejectComplete = false;
        lTimeNextEject = 0L;
        guidedMissileUtils = null;
        hasChaff = false;
        hasFlare = false;
        lastChaffDeployed = 0L;
        lastFlareDeployed = 0L;
        lastCommonThreatActive = 0L;
        intervalCommonThreat = 1000L;
        lastRadarLockThreatActive = 0L;
        intervalRadarLockThreat = 1000L;
        lastMissileLaunchThreatActive = 0L;
        intervalMissileLaunchThreat = 1000L;
        bToFire = false;
        guidedMissileUtils = new GuidedMissileUtils(this);
        this.transsonicEffects = new TransonicEffects(this, 0.0F, 9000F, 0.8F, 1.0F, 0.01F, 1.0F, 0.2F, 1.0F, 0.45F, 0.58F, 0.0F, 0.9F, 1.0F, 1.25F);
    }

    public void getGFactors(TypeGSuit.GFactors theGFactors)
    {
        theGFactors.setGFactors(NEG_G_TOLERANCE_FACTOR, NEG_G_TIME_FACTOR, NEG_G_RECOVERY_FACTOR, POS_G_TOLERANCE_FACTOR, POS_G_TIME_FACTOR, POS_G_RECOVERY_FACTOR);
    }
    
    public long getChaffDeployed()
    {
        if(hasChaff)
            return lastChaffDeployed;
        else
            return 0L;
    }

    public long getFlareDeployed()
    {
        if(hasFlare)
            return lastFlareDeployed;
        else
            return 0L;
    }

    public void setCommonThreatActive()
    {
        long curTime = Time.current();
        if(curTime - lastCommonThreatActive > intervalCommonThreat)
        {
            lastCommonThreatActive = curTime;
            doDealCommonThreat();
        }
    }

    public void setRadarLockThreatActive()
    {
        long curTime = Time.current();
        if(curTime - lastRadarLockThreatActive > intervalRadarLockThreat)
        {
            lastRadarLockThreatActive = curTime;
            doDealRadarLockThreat();
        }
    }

    public void setMissileLaunchThreatActive()
    {
        long curTime = Time.current();
        if(curTime - lastMissileLaunchThreatActive > intervalMissileLaunchThreat)
        {
            lastMissileLaunchThreatActive = curTime;
            doDealMissileLaunchThreat();
        }
    }

    private void doDealCommonThreat()
    {
    }

    private void doDealRadarLockThreat()
    {
    }

    private void doDealMissileLaunchThreat()
    {
    }

    public GuidedMissileUtils getGuidedMissileUtils()
    {
        return guidedMissileUtils;
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        this.transsonicEffects.onAircraftLoaded();
        guidedMissileUtils.onAircraftLoaded();
    }

    public void moveArrestorHook(float f)
    {
        hierMesh().chunkSetAngles("Hook1_D0", 0.0F, -37F * f, 0.0F);
    }

    public void doMurderPilot(int i)
    {
        switch(i)
        {
        case 0:
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("HMask1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            break;
        }
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if((this.FM.Gears.nearGround() || this.FM.Gears.onGround()) && this.FM.CT.getCockpitDoor() == 1.0F)
            hierMesh().chunkVisible("HMask1_D0", false);
        else
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Head1_D0"));
        if(!this.FM.isPlayers() && (this.FM instanceof Maneuver))
            if((this.FM.AP.way.isLanding() || this.FM.AP.way.isLandingOnShip()) && this.FM.AP.getWayPointDistance() < 2500F)
            {
                if((double)this.FM.getSpeedKMH() < (double)this.FM.VmaxFLAPS * 0.4D)
                    this.FM.CT.AirBrakeControl = 0.0F;
                else
                    this.FM.CT.AirBrakeControl = 1.0F;
            } else
            if(((Maneuver)this.FM).get_maneuver() == 66)
                this.FM.CT.AirBrakeControl = 0.0F;
            else
            if(((Maneuver)this.FM).get_maneuver() == 7)
                this.FM.CT.AirBrakeControl = 1.0F;
            else
                this.FM.CT.AirBrakeControl = 0.0F;
    }

    public static void moveGear(HierMesh hiermesh, float f)
    {
        float f1 = f > 0.5F ? Aircraft.cvt(f, 0.8F, 1.0F, 90F, 0.0F) : Aircraft.cvt(f, 0.0F, 0.5F, 0.0F, 90F);
        float f2 = f > 0.5F ? Aircraft.cvt(f, 0.8F, 1.0F, 90F, 0.0F) : Aircraft.cvt(f, 0.0F, 0.2F, 0.0F, 90F);
        float f3 = f > 0.5F ? Aircraft.cvt(f, 0.8F, 1.0F, -90F, 0.0F) : Aircraft.cvt(f, 0.0F, 0.2F, 0.0F, -90F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, Aircraft.cvt(f, 0.2F, 0.9F, -15F, -90F), 0.0F);
        hiermesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(f, 0.1F, 0.2F, 0.0F, -100F), 0.0F);
        hiermesh.chunkSetAngles("GearC4_D0", 0.0F, f1, 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(f, 0.2F, 0.6F, 0.0F, -88F), 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, f2, 0.0F);
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, Aircraft.cvt(f, 0.1F, 0.2F, 0.0F, -100F), 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(f, 0.2F, 0.6F, 0.0F, 88F), 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, f3, 0.0F);
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, Aircraft.cvt(f, 0.1F, 0.2F, 0.0F, 100F), 0.0F);
    }

    protected void moveGear(float f)
    {
        moveGear(hierMesh(), f);
    }

    public void moveWheelSink()
    {
        resetYPRmodifier();
        Aircraft.xyz[0] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.15F, 0.0F, -0.1F);
        hierMesh().chunkSetLocate("GearLPiston", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
        Aircraft.xyz[0] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.15F, 0.0F, 0.1F);
        hierMesh().chunkSetLocate("GearRPiston", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
        Aircraft.xyz[0] = Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 0.2F, 0.0F, -0.4F);
        hierMesh().chunkSetLocate("GearCPiston", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
    }

    protected void moveElevator(float f)
    {
        hierMesh().chunkSetAngles("Vator_D0", 0.0F, -30F * f, 0.0F);
    }

    protected void moveAileron(float f)
    {
        hierMesh().chunkSetAngles("AroneL_D0", 0.0F, 30F * f, 0.0F);
        hierMesh().chunkSetAngles("AroneR_D0", 0.0F, 30F * f, 0.0F);
    }

    protected void moveRudder(float f)
    {
        hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, 20F * f, 0.0F);
        hierMesh().chunkSetAngles("Rudder2_D0", 0.0F, 20F * f, 0.0F);
        if(this.FM.CT.getGear() > 0.8F)
            hierMesh().chunkSetAngles("FrontForks", 0.0F, 0.0F, -40F * f);
    }

    protected void moveFlap(float f)
    {
        resetYPRmodifier();
        Aircraft.xyz[1] = 0.68F * f;
        Aircraft.xyz[2] = 0.1F * f;
        hierMesh().chunkSetLocate("Flap01_D0", Aircraft.xyz, Aircraft.ypr);
        float f1 = -30F * f;
        float f2 = 30F * f;
        hierMesh().chunkSetAngles("Flap02_D0", 0.0F, f1, 0.0F);
        hierMesh().chunkSetAngles("Flap03_D0", 0.0F, f2, 0.0F);
    }

    protected void moveAirBrake(float f)
    {
        resetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.15F);
        hierMesh().chunkSetLocate("AirBrakeL", Aircraft.xyz, Aircraft.ypr);
        hierMesh().chunkSetLocate("AirBrakeR", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
    }

    public void moveCockpitDoor(float f)
    {
        resetYPRmodifier();
        hierMesh().chunkSetAngles("Blister1_D0", 0.0F, 55F * f, 0.0F);
        if(Config.isUSE_RENDER())
        {
            if(Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null)
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            setDoorSnd(f);
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xx"))
        {
            if(s.startsWith("xxengine"))
            {
                byte byte0 = 0;
                if(s.startsWith("xxengine2"))
                    byte0 = 1;
                if(s.startsWith("xxengine3"))
                    byte0 = 2;
                if(s.startsWith("xxengine4"))
                    byte0 = 3;
                debuggunnery("Engine Module[" + byte0 + "]: Hit..");
                if(getEnergyPastArmor(World.Rnd().nextFloat(0.2F, 0.55F), shot) > 0.0F)
                {
                    if(World.Rnd().nextFloat() < shot.power / 280000F)
                    {
                        debuggunnery("Engine Module: Engine Crank Case Hit - Engine Stucks..");
                        this.FM.AS.setEngineStuck(shot.initiator, byte0);
                    }
                    if(World.Rnd().nextFloat() < shot.power / 100000F)
                    {
                        debuggunnery("Engine Module: Engine Crank Case Hit - Engine Damaged..");
                        this.FM.AS.hitEngine(shot.initiator, byte0, 2);
                    }
                }
                if(getEnergyPastArmor(0.85F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[byte0].getCylindersRatio() * 0.66F)
                {
                    this.FM.EI.engines[byte0].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int)(shot.power / 32200F)));
                    debuggunnery("Engine Module: Cylinders Hit, " + this.FM.EI.engines[byte0].getCylindersOperable() + "/" + this.FM.EI.engines[byte0].getCylinders() + " Left..");
                    if(World.Rnd().nextFloat() < shot.power / 1000000F)
                    {
                        this.FM.AS.hitEngine(shot.initiator, byte0, 2);
                        debuggunnery("Engine Module: Cylinders Hit - Engine Fires..");
                    }
                }
                getEnergyPastArmor(25F, shot);
            } else
            if(s.startsWith("xxtank"))
            {
                int i = s.charAt(6) - 49;
                if(getEnergyPastArmor(2.1F, shot) > 0.0F && World.Rnd().nextFloat() < 10.25F)
                {
                    if(this.FM.AS.astateTankStates[i] == 0)
                    {
                        debuggunnery("Fuel Tank (" + i + "): Pierced..");
                        this.FM.AS.hitTank(shot.initiator, i, 1);
                        this.FM.AS.doSetTankState(shot.initiator, i, 1);
                    }
                    if(shot.powerType == 3 && World.Rnd().nextFloat() < 0.1F)
                    {
                        this.FM.AS.hitTank(shot.initiator, i, 2);
                        debuggunnery("Fuel Tank (" + i + "): Hit..");
                    }
                }
            }
        } else
        if(s.startsWith("xcf"))
        {
            hitChunk("CF", shot);
            if(point3d.x > 0.5D)
            {
                if(point3d.z > 0.913D)
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
                if(point3d.z > 0.341D)
                {
                    if(point3d.x < 1.402D)
                    {
                        if(point3d.y > 0.0D)
                            this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
                        else
                            this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
                    } else
                    {
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
                    }
                } else
                if(point3d.y > 0.0D)
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
                else
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
                if(point3d.x > 1.691D && point3d.x < 1.98D)
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
            }
        } else
        if(s.startsWith("xwinglin"))
        {
            if(chunkDamageVisible("WingLIn") < 1)
                hitChunk("WingLIn", shot);
        } else
        if(s.startsWith("xwingrin"))
        {
            if(chunkDamageVisible("WingRIn") < 1)
                hitChunk("WingRIn", shot);
        } else
        if(s.startsWith("xwinglmid"))
            hitChunk("WingLMid", shot);
        else
        if(s.startsWith("xwingrmid"))
            hitChunk("WingRMid", shot);
        else
        if(s.startsWith("xwinglout"))
        {
            if(chunkDamageVisible("WingLOut") < 1)
                hitChunk("WingLOut", shot);
        } else
        if(s.startsWith("xwingrout"))
        {
            if(chunkDamageVisible("WingROut") < 1)
                hitChunk("WingROut", shot);
        } else
        if(s.startsWith("xarone"))
        {
            if(s.startsWith("xaronel") && chunkDamageVisible("AroneL") < 1)
                hitChunk("AroneL", shot);
            if(s.startsWith("xaroner") && chunkDamageVisible("AroneR") < 1)
                hitChunk("AroneR", shot);
        } else
        if(s.startsWith("xrudder"))
        {
            if(chunkDamageVisible("Rudder1_D0") < 1)
                hitChunk("Rudder1_D0", shot);
        } else
        if(s.startsWith("xvator"))
        {
            if(chunkDamageVisible("Vator1_D0") < 1)
                hitChunk("Vator1_D0", shot);
        } else
        if(s.startsWith("xgearl"))
            hitChunk("GearL2", shot);
        else
        if(s.startsWith("xgearr"))
            hitChunk("GearR2", shot);
    }

    public void update(float f)
    {
        if(!this.FM.isPlayers() && this.FM.Gears.onGround())
            if(this.FM.EI.engines[0].getRPM() < 100F)
                this.FM.CT.cockpitDoorControl = 1.0F;
            else
                this.FM.CT.cockpitDoorControl = 0.0F;
        if((this.FM.AS.bIsAboutToBailout || overrideBailout) && !ejectComplete && this.FM.getSpeedKMH() > 15F)
        {
            overrideBailout = true;
            this.FM.AS.bIsAboutToBailout = false;
            if(Time.current() > lTimeNextEject)
                bailout();
        }
        if(this.FM.getSpeedKMH() > 56F)
            this.FM.CT.cockpitDoorControl = 0.0F;
        if(this.FM.getSpeedKMH() > 340F)
            this.FM.CT.GearControl = 0.0F;
        if(Config.isUSE_RENDER() && this.FM.AS.isMaster())
            if(this.FM.EI.engines[0].getPowerOutput() > 0.7F && this.FM.EI.engines[0].getStage() == 6)
            {
                if(this.FM.EI.engines[0].getPowerOutput() > 0.99F)
                    this.FM.AS.setSootState(this, 0, 3);
                else
                    this.FM.AS.setSootState(this, 0, 2);
            } else
            {
                this.FM.AS.setSootState(this, 0, 0);
            }
        if(this.FM.EI.engines[0].getPowerOutput() > 0.55F)
            hierMesh().chunkVisible("ExhaustGas1", true);
        else
            hierMesh().chunkVisible("ExhaustGas1", false);
        if(this.FM.EI.engines[0].getPowerOutput() > 0.65F)
            hierMesh().chunkVisible("ExhaustGas1", false);
        if(this.FM.EI.engines[0].getPowerOutput() > 0.65F)
            hierMesh().chunkVisible("ExhaustGas2", true);
        else
            hierMesh().chunkVisible("ExhaustGas2", false);
        if(this.FM.EI.engines[0].getPowerOutput() > 0.75F)
            hierMesh().chunkVisible("ExhaustGas2", false);
        if(this.FM.EI.engines[0].getPowerOutput() > 0.75F)
            hierMesh().chunkVisible("ExhaustGas3", true);
        else
            hierMesh().chunkVisible("ExhaustGas3", false);
        if(this.FM.EI.engines[0].getPowerOutput() > 0.9F)
            hierMesh().chunkVisible("ExhaustGas3", false);
        if(this.FM.EI.engines[0].getPowerOutput() > 0.9F)
            hierMesh().chunkVisible("ExhaustGas4", true);
        else
            hierMesh().chunkVisible("ExhaustGas4", false);
        if(this.FM.EI.engines[0].getPowerOutput() > 0.6F)
            hierMesh().chunkVisible("CB", true);
        else
            hierMesh().chunkVisible("CB", false);
        guidedMissileUtils.update();
        this.soundbarier();
        super.update(f);
    }

    protected boolean cutFM(int i, int j, Actor actor)
    {
        switch(i)
        {
        case 19:
            this.FM.EI.engines[0].setEngineDies(actor);
            return super.cutFM(i, j, actor);

        case 11:
            cut("StabL");
            cut("StabR");
            this.FM.cut(17, j, actor);
            this.FM.cut(18, j, actor);
            break;

        case 13:
            return false;
        }
        return super.cutFM(i, j, actor);
    }

    public void doEjectCatapult()
    {
        new MsgAction(false, this) {

            public void doAction(Object paramObject)
            {
                Aircraft localAircraft = (Aircraft)paramObject;
                if(!Actor.isValid(localAircraft))
                {
                    return;
                } else
                {
                    Loc localLoc1 = new Loc();
                    Loc localLoc2 = new Loc();
                    Vector3d localVector3d = new Vector3d(0.0D, 0.0D, 10D);
                    HookNamed localHookNamed = new HookNamed(localAircraft, "_ExternalSeat01");
                    localAircraft.pos.getAbs(localLoc2);
                    localHookNamed.computePos(localAircraft, localLoc2, localLoc1);
                    localLoc1.transform(localVector3d);
                    localVector3d.x += localAircraft.FM.Vwld.x;
                    localVector3d.y += localAircraft.FM.Vwld.y;
                    localVector3d.z += localAircraft.FM.Vwld.z;
                    new EjectionSeat(1, localLoc1, localVector3d, localAircraft);
                    return;
                }
            }

        }
;
        hierMesh().chunkVisible("Seat", false);
    }

    private void bailout()
    {
        if(overrideBailout)
            if(this.FM.AS.astateBailoutStep >= 0 && this.FM.AS.astateBailoutStep < 2)
            {
                if(this.FM.CT.cockpitDoorControl > 0.5F && this.FM.CT.getCockpitDoor() > 0.5F)
                    this.FM.AS.astateBailoutStep = 11;
                else
                    this.FM.AS.astateBailoutStep = 2;
            } else
            if(this.FM.AS.astateBailoutStep >= 2 && this.FM.AS.astateBailoutStep <= 3)
            {
                switch(this.FM.AS.astateBailoutStep)
                {
                case 2:
                    if(this.FM.CT.cockpitDoorControl < 0.5F)
                        doRemoveBlister1();
                    break;

                case 3:
                    lTimeNextEject = Time.current() + 1000L;
                    break;
                }
                if(this.FM.AS.isMaster())
                    this.FM.AS.netToMirrors(20, this.FM.AS.astateBailoutStep, 1, null);
                AircraftState tmp178_177 = this.FM.AS;
                tmp178_177.astateBailoutStep = (byte)(tmp178_177.astateBailoutStep + 1);
                if(this.FM.AS.astateBailoutStep == 4)
                    this.FM.AS.astateBailoutStep = 11;
            } else
            if(this.FM.AS.astateBailoutStep >= 11 && this.FM.AS.astateBailoutStep <= 19)
            {
                int i = this.FM.AS.astateBailoutStep;
                if(this.FM.AS.isMaster())
                    this.FM.AS.netToMirrors(20, this.FM.AS.astateBailoutStep, 1, null);
                AircraftState tmp383_382 = this.FM.AS;
                tmp383_382.astateBailoutStep = (byte)(tmp383_382.astateBailoutStep + 1);
                if(i == 11)
                {
                    this.FM.setTakenMortalDamage(true, null);
                    if((this.FM instanceof Maneuver) && ((Maneuver)this.FM).get_maneuver() != 44)
                    {
                        World.cur();
                        if(this.FM.AS.actor != World.getPlayerAircraft())
                            ((Maneuver)this.FM).set_maneuver(44);
                    }
                }
                if(this.FM.AS.astatePilotStates[i - 11] < 99)
                {
                    doRemoveBodyFromPlane(i - 10);
                    if(i == 11)
                    {
                        doEjectCatapult();
                        lTimeNextEject = Time.current() + 1000L;
                        this.FM.setTakenMortalDamage(true, null);
                        this.FM.CT.WeaponControl[0] = false;
                        this.FM.CT.WeaponControl[1] = false;
                        this.FM.AS.astateBailoutStep = -1;
                        overrideBailout = false;
                        this.FM.AS.bIsAboutToBailout = true;
                        ejectComplete = true;
                        if(i > 10 && i <= 19)
                            EventLog.onBailedOut(this, i - 11);
                        return;
                    }
                }
            }
    }

    private final void doRemoveBlister1()
    {
        if(hierMesh().chunkFindCheck("Blister1_D0") != -1 && this.FM.AS.getPilotHealth(0) > 0.0F)
        {
            hierMesh().hideSubTrees("Blister1_D0");
            Wreckage localWreckage = new Wreckage(this, hierMesh().chunkFind("Blister1_D0"));
            localWreckage.collide(false);
            Vector3d localVector3d = new Vector3d();
            localVector3d.set(this.FM.Vwld);
            localWreckage.setSpeed(localVector3d);
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

    private final TransonicEffects transsonicEffects;

    private GuidedMissileUtils guidedMissileUtils;
    public boolean bToFire;
    private static final float NEG_G_TOLERANCE_FACTOR = 1.5F;
    private static final float NEG_G_TIME_FACTOR = 1.5F;
    private static final float NEG_G_RECOVERY_FACTOR = 1F;
    private static final float POS_G_TOLERANCE_FACTOR = 2F;
    private static final float POS_G_TIME_FACTOR = 2F;
    private static final float POS_G_RECOVERY_FACTOR = 2F;
    private boolean hasChaff;
    private boolean hasFlare;
    private long lastChaffDeployed;
    private long lastFlareDeployed;
    private long lastCommonThreatActive;
    private long intervalCommonThreat;
    private long lastRadarLockThreatActive;
    private long intervalRadarLockThreat;
    private long lastMissileLaunchThreatActive;
    private long intervalMissileLaunchThreat;
    private long lTimeNextEject;
    private boolean ejectComplete;
    private boolean overrideBailout;

    static 
    {
        Class class1 = DH116xyz.class;
        Property.set(class1, "originCountry", PaintScheme.countryUSA);
    }
}
