package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.Wing;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.AirGroup;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.FlightModel;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.Wreckage;
import com.maddox.rts.MsgAction;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.NetObj;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class F9F8T extends F9F_Cougar
    implements TypeDockable
{

    public F9F8T()
    {
        lTimeNextEject = 0L;
        overrideBailout = false;
        ejectComplete = false;
        lTimeNextEject = 0L;
        emergencyEject = false;
        super.isTrainer = true;
    }

    public void update(float f)
    {
        if(bNeedSetup)
            checkAsDrone();
        int i = aircIndex();
        if(this.FM instanceof Maneuver)
            if(typeDockableIsDocked())
            {
                if(!(this.FM instanceof RealFlightModel) || !((RealFlightModel)this.FM).isRealMode())
                {
                    ((Maneuver)this.FM).unblock();
                    ((Maneuver)this.FM).set_maneuver(48);
                    for(int j = 0; j < i; j++)
                        ((Maneuver)this.FM).push(48);

                    if(this.FM.AP.way.curr().Action != 3)
                        ((Maneuver)this.FM).AP.way.setCur(((Aircraft)queen_).FM.AP.way.Cur());
                    ((Pilot)this.FM).setDumbTime(3000L);
                }
                if(this.FM.M.fuel < this.FM.M.maxFuel)
                    this.FM.M.fuel += 20F * f;
            } else
            if(!(this.FM instanceof RealFlightModel) || !((RealFlightModel)this.FM).isRealMode())
            {
                if(this.FM.CT.GearControl == 0.0F && this.FM.EI.engines[0].getStage() == 0)
                    this.FM.EI.setEngineRunning();
                if(dtime > 0L && ((Maneuver)this.FM).Group != null)
                {
                    ((Maneuver)this.FM).Group.leaderGroup = null;
                    ((Maneuver)this.FM).set_maneuver(22);
                    ((Pilot)this.FM).setDumbTime(3000L);
                    if(Time.current() > dtime + 3000L)
                    {
                        dtime = -1L;
                        ((Maneuver)this.FM).clear_stack();
                        ((Maneuver)this.FM).set_maneuver(0);
                        ((Pilot)this.FM).setDumbTime(0L);
                    }
                } else
                if(this.FM.AP.way.curr().Action == 0)
                {
                    Maneuver maneuver = (Maneuver)this.FM;
                    if(maneuver.Group != null && maneuver.Group.airc[0] == this && maneuver.Group.clientGroup != null)
                        maneuver.Group.setGroupTask(2);
                }
            }
        if((this.FM.AS.bIsAboutToBailout || overrideBailout) && !ejectComplete && !this.FM.Gears.onGround())
        {
            overrideBailout = true;
            this.FM.AS.bIsAboutToBailout = false;
            if(Time.current() > lTimeNextEject)
                bailout();
        }
        super.update(f);
    }

    public void msgCollisionRequest(Actor actor, boolean aflag[])
    {
        super.msgCollisionRequest(actor, aflag);
        if(queen_last != null && queen_last == actor && (queen_time == 0L || Time.current() < queen_time + 5000L))
            aflag[0] = false;
        else
            aflag[0] = true;
    }

    public void missionStarting()
    {
        checkAsDrone();
    }

    private void checkAsDrone()
    {
        if(target_ == null)
        {
            if(this.FM.AP.way.curr().getTarget() == null)
                this.FM.AP.way.next();
            target_ = this.FM.AP.way.curr().getTarget();
            if(Actor.isValid(target_) && (target_ instanceof Wing))
            {
                Wing wing = (Wing)target_;
                int i = aircIndex();
                if(Actor.isValid(wing.airc[i / 2]))
                    target_ = wing.airc[i / 2];
                else
                    target_ = null;
            }
        }
        if(Actor.isValid(target_) && (target_ instanceof TypeTankerDrogue))
        {
            queen_last = target_;
            queen_time = Time.current();
            if(isNetMaster())
                ((TypeDockable)target_).typeDockableRequestAttach(this, aircIndex() % 2, true);
        }
        bNeedSetup = false;
        target_ = null;
    }

    public int typeDockableGetDockport()
    {
        if(typeDockableIsDocked())
            return dockport_;
        else
            return -1;
    }

    public Actor typeDockableGetQueen()
    {
        return queen_;
    }

    public boolean typeDockableIsDocked()
    {
        return Actor.isValid(queen_);
    }

    public void typeDockableAttemptAttach()
    {
        if(this.FM.AS.isMaster() && !typeDockableIsDocked())
        {
            Aircraft aircraft = War.getNearestFriend(this);
            if(aircraft instanceof TypeTankerDrogue)
                ((TypeDockable)aircraft).typeDockableRequestAttach(this);
        }
    }

    public void typeDockableAttemptDetach()
    {
        if(this.FM.AS.isMaster() && typeDockableIsDocked() && Actor.isValid(queen_))
            ((TypeDockable)queen_).typeDockableRequestDetach(this);
    }

    public void typeDockableRequestAttach(Actor actor)
    {
    }

    public void typeDockableRequestDetach(Actor actor)
    {
    }

    public void typeDockableRequestAttach(Actor actor, int i, boolean flag)
    {
    }

    public void typeDockableRequestDetach(Actor actor, int i, boolean flag)
    {
    }

    public void typeDockableDoAttachToDrone(Actor actor, int i)
    {
    }

    public void typeDockableDoDetachFromDrone(int i)
    {
    }

    public void typeDockableDoAttachToQueen(Actor actor, int i)
    {
        queen_ = actor;
        dockport_ = i;
        queen_last = queen_;
        queen_time = 0L;
        this.FM.EI.setEngineRunning();
        this.FM.CT.setGearAirborne();
        moveGear(0.0F);
        FlightModel flightmodel = ((Aircraft)queen_).FM;
        if(aircIndex() == 0 && (this.FM instanceof Maneuver) && (flightmodel instanceof Maneuver))
        {
            Maneuver maneuver = (Maneuver)flightmodel;
            Maneuver maneuver1 = (Maneuver)this.FM;
            if(maneuver.Group != null && maneuver1.Group != null && maneuver1.Group.numInGroup(this) == maneuver1.Group.nOfAirc - 1)
            {
                AirGroup airgroup = new AirGroup(maneuver1.Group);
                maneuver1.Group.delAircraft(this);
                airgroup.addAircraft(this);
                airgroup.attachGroup(maneuver.Group);
                airgroup.rejoinGroup = null;
                airgroup.leaderGroup = null;
                airgroup.clientGroup = maneuver.Group;
            }
        }
    }

    public void typeDockableDoDetachFromQueen(int i)
    {
        if(dockport_ == i)
        {
            queen_last = queen_;
            queen_time = Time.current();
            queen_ = null;
            dockport_ = 0;
        }
    }

    public void typeDockableReplicateToNet(NetMsgGuaranted netmsgguaranted)
        throws IOException
    {
        if(typeDockableIsDocked())
        {
            netmsgguaranted.writeByte(1);
            com.maddox.il2.engine.ActorNet actornet = null;
            if(Actor.isValid(queen_))
            {
                actornet = queen_.net;
                if(actornet.countNoMirrors() > 0)
                    actornet = null;
            }
            netmsgguaranted.writeByte(dockport_);
            netmsgguaranted.writeNetObj(actornet);
        } else
        {
            netmsgguaranted.writeByte(0);
        }
    }

    public void typeDockableReplicateFromNet(NetMsgInput netmsginput)
        throws IOException
    {
        if(netmsginput.readByte() == 1)
        {
            dockport_ = netmsginput.readByte();
            NetObj netobj = netmsginput.readNetObj();
            if(netobj != null)
            {
                Actor actor = (Actor)netobj.superObj();
                ((TypeDockable)actor).typeDockableDoAttachToDrone(this, dockport_);
            }
        }
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if((this.FM instanceof RealFlightModel) && ((RealFlightModel)this.FM).isRealMode() || !flag || !(this.FM instanceof Pilot))
            return;
        if(flag && this.FM.AP.way.curr().Action == 3 && typeDockableIsDocked() && Math.abs(((Aircraft)queen_).FM.Or.getKren()) < 3F)
            if(this.FM.isPlayers())
            {
                if((this.FM instanceof RealFlightModel) && !((RealFlightModel)this.FM).isRealMode())
                {
                    typeDockableAttemptDetach();
                    ((Maneuver)this.FM).set_maneuver(22);
                    ((Maneuver)this.FM).setCheckStrike(false);
                    this.FM.Vwld.z -= 5D;
                    dtime = Time.current();
                }
            } else
            {
                typeDockableAttemptDetach();
                ((Maneuver)this.FM).set_maneuver(22);
                ((Maneuver)this.FM).setCheckStrike(false);
                this.FM.Vwld.z -= 5D;
                dtime = Time.current();
            }
        if((this.FM.Gears.nearGround() || this.FM.Gears.onGround()) && this.FM.CT.getCockpitDoor() == 1.0F)
            hierMesh().chunkVisible("HMask2_D0", false);
        else
            hierMesh().chunkVisible("HMask2_D0", hierMesh().isChunkVisible("Pilot2_D0"));
    }

    public void doEjectCatapultStudent()
    {
        new MsgAction(false, this) {

            public void doAction(Object paramObject)
            {
                Aircraft localAircraft = (Aircraft)paramObject;
                if(Actor.isValid(localAircraft))
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
                    new EjectionSeat(4, localLoc1, localVector3d, localAircraft);
                }
            }

        }
;
        hierMesh().chunkVisible("Seat2_D0", false);
    }

    public void doEjectCatapultInstructor()
    {
        new MsgAction(false, this) {

            public void doAction(Object paramObject)
            {
                Aircraft localAircraft = (Aircraft)paramObject;
                if(Actor.isValid(localAircraft))
                {
                    Loc localLoc1 = new Loc();
                    Loc localLoc2 = new Loc();
                    Vector3d localVector3d = new Vector3d(0.0D, 0.0D, 10D);
                    HookNamed localHookNamed = new HookNamed(localAircraft, "_ExternalSeat02");
                    localAircraft.pos.getAbs(localLoc2);
                    localHookNamed.computePos(localAircraft, localLoc2, localLoc1);
                    localLoc1.transform(localVector3d);
                    localVector3d.x += localAircraft.FM.Vwld.x;
                    localVector3d.y += localAircraft.FM.Vwld.y;
                    localVector3d.z += localAircraft.FM.Vwld.z;
                    new EjectionSeat(4, localLoc1, localVector3d, localAircraft);
                }
            }

        }
;
        hierMesh().chunkVisible("Seat1_D0", false);
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
                        if(this.FM.AS.getPilotHealth(0) < 0.5F || this.FM.AS.getPilotHealth(1) < 0.5F || this.FM.EI.engines[0].getReadyness() < 0.7F || !this.FM.CT.bHasCockpitDoorControl || this.FM.getSpeedKMH() < 250F || World.Rnd().nextFloat() < 0.2F)
                        {
                            emergencyEject = true;
                            breakBlister();
                        } else
                        {
                            doRemoveBlister1();
                        }
                    break;

                case 3:
                    if(!emergencyEject)
                        lTimeNextEject = Time.current() + 1000L;
                    break;
                }
                if(this.FM.AS.isMaster())
                    this.FM.AS.netToMirrors(20, this.FM.AS.astateBailoutStep, 1, null);
                this.FM.AS.astateBailoutStep = (byte)(this.FM.AS.astateBailoutStep + 1);
                if(this.FM.AS.astateBailoutStep == 4)
                    this.FM.AS.astateBailoutStep = 11;
            } else
            if(this.FM.AS.astateBailoutStep >= 11 && this.FM.AS.astateBailoutStep <= 19)
            {
                int i = this.FM.AS.astateBailoutStep;
                if(this.FM.AS.isMaster())
                    this.FM.AS.netToMirrors(20, this.FM.AS.astateBailoutStep, 1, null);
                this.FM.AS.astateBailoutStep = (byte)(this.FM.AS.astateBailoutStep + 1);
                if((this.FM instanceof Maneuver) && ((Maneuver)this.FM).get_maneuver() != 44)
                {
                    World.cur();
                    if(this.FM.AS.actor != World.getPlayerAircraft())
                        ((Maneuver)this.FM).set_maneuver(44);
                }
                if(this.FM.AS.astatePilotStates[i - 11] < 99)
                {
                    if(i == 11)
                    {
                        doRemoveBodyFromPlane(2);
                        doEjectCatapultStudent();
                        lTimeNextEject = Time.current() + 1000L;
                    } else
                    if(i == 12)
                    {
                        doRemoveBodyFromPlane(1);
                        doEjectCatapultInstructor();
                        this.FM.AS.astateBailoutStep = 51;
                        this.FM.setTakenMortalDamage(true, null);
                        this.FM.CT.WeaponControl[0] = false;
                        this.FM.CT.WeaponControl[1] = false;
                        this.FM.AS.astateBailoutStep = -1;
                        overrideBailout = false;
                        this.FM.AS.bIsAboutToBailout = true;
                        ejectComplete = true;
                    }
                    this.FM.AS.astatePilotStates[i - 11] = 99;
                } else
                {
                    EventLog.type("astatePilotStates[" + (i - 11) + "]=" + this.FM.AS.astatePilotStates[i - 11]);
                }
            }
    }

    private final void breakBlister()
    {
        if(hierMesh().chunkFindCheck("Blister1_D0") != -1 && this.FM.AS.getPilotHealth(0) > 0.0F)
        {
            hierMesh().hideSubTrees("Blister1_D0");
            Wreckage localWreckage = new Wreckage(this, hierMesh().chunkFind("Blister1_D0"));
            localWreckage.collide(false);
            Vector3d localVector3d1 = new Vector3d();
            localVector3d1.set(this.FM.Vwld);
            localWreckage.setSpeed(localVector3d1);
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

    public void doMurderPilot(int i)
    {
        switch(i)
        {
        case 0:
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            break;

        case 1:
            hierMesh().chunkVisible("Pilot2_D0", false);
            hierMesh().chunkVisible("Head2_D0", false);
            hierMesh().chunkVisible("Pilot2_D1", true);
            break;
        }
    }

    protected boolean cutFM(int i, int j, Actor actor)
    {
        switch(i)
        {
        case 33:
            return super.cutFM(34, j, actor);

        case 36:
            return super.cutFM(37, j, actor);

        case 11:
            cutFM(17, j, actor);
            this.FM.cut(17, j, actor);
            cutFM(18, j, actor);
            this.FM.cut(18, j, actor);
            return super.cutFM(i, j, actor);
        }
        return super.cutFM(i, j, actor);
    }

    public void moveCockpitDoor(float paramFloat)
    {
        resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(paramFloat, 0.01F, 0.99F, 0.0F, 2.3F);
        Aircraft.xyz[2] = Aircraft.cvt(paramFloat, 0.15F, 0.99F, 0.0F, -0.25F);
        Aircraft.ypr[2] = Aircraft.cvt(paramFloat, 0.01F, 0.99F, 0.0F, 8F);
        hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        float f = (float)Math.sin(Aircraft.cvt(paramFloat, 0.01F, 0.6F, 0.0F, 3.141593F));
        hierMesh().chunkSetAngles("Pilot1_D0", 0.0F, 0.0F, 9F * f);
        hierMesh().chunkSetAngles("Head1_D0", 14F * f, 0.0F, 0.0F);
        hierMesh().chunkSetAngles("Pilot2_D0", 0.0F, 0.0F, 9F * f);
        hierMesh().chunkSetAngles("Head2_D0", 14F * f, 0.0F, 0.0F);
        if(Config.isUSE_RENDER())
        {
            if(Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null)
                Main3D.cur3D().cockpits[0].onDoorMoved(paramFloat);
            setDoorSnd(paramFloat);
        }
    }

    private Actor queen_last;
    private long queen_time;
    private boolean bNeedSetup;
    private long dtime;
    private Actor target_;
    private Actor queen_;
    private int dockport_;
    public static boolean bChangedPit = false;
    private boolean overrideBailout;
    private boolean ejectComplete;
    private long lTimeNextEject;
    private boolean emergencyEject;

    static 
    {
        Class class1 = F9F8T.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "F9F8");
        Property.set(class1, "meshName", "3DO/Plane/F9F8T/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(class1, "yearService", 1946.9F);
        Property.set(class1, "yearExpired", 1955.3F);
        Property.set(class1, "FlightModel", "FlightModels/F9F8T.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitF9F_Student.class, CockpitF9F_Instructor.class
        });
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 0, 0, 9, 9, 9, 9, 9, 9, 
            9, 9, 3, 3, 3, 3, 3, 3, 9, 9, 
            2, 2, 2, 2, 2, 2, 2, 2, 9, 9, 
            9, 9, 2, 2, 2, 2, 2, 2, 2, 2, 
            3
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06", 
            "_ExternalDev07", "_ExternalDev08", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06", "_ExternalDev09", "_ExternalDev10", 
            "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalRock07", "_ExternalRock08", "_ExternalDev11", "_ExternalDev12", 
            "_ExternalDev13", "_ExternalDev14", "_ExternalRock09", "_ExternalRock09", "_ExternalRock10", "_ExternalRock10", "_ExternalRock11", "_ExternalRock11", "_ExternalRock12", "_ExternalRock12", 
            "_ExternalBomb07"
        });
    }
}
