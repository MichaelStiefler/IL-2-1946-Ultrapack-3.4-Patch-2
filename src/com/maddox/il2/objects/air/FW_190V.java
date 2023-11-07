package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorHMesh;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.Wreckage;
import com.maddox.rts.CmdEnv;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.Property;
import com.maddox.sound.AudioStream;
import com.maddox.sound.SoundFX;

public abstract class FW_190V extends Scheme1
    implements TypeFighter, TypeBNZFighter
{

    public FW_190V()
    {
        trimElevator = 0.0F;
        bHasElevatorControl = true;
        sinkAngle = new double[2];
        boulon = newSound("boulon", false);
        bouton = newSound("fw190_button", false);
        arrach = newSound("aircraft.arrach", false);
        windExtFw = newSound("windextfw", false);
        doorSndControl = 0.0F;
        doorSound = null;
        doorPrev = 0.0F;
        doorSndPos = null;
        bSmokeEffect = false;
        if(Config.cur.ini.get("Mods", "SmokeEffect", 0) > 0)
            bSmokeEffect = true;
    }

    static double getDst(double x1, double y1, double x2, double y2)
    {
        double dst = 0.0D;
        dst = (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
        dst = Math.sqrt(dst);
        return dst;
    }

    public static void moveGear(HierMesh hiermesh, float f)
    {
        if(f < 0.002777778F)
            f = 0.0F;
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, 77F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, 77F * f, 0.0F);
        double theta0 = -0.14D;
        AxisPos[axisD][idX] = Math.cos(theta0) * (double)LengthA_D;
        AxisPos[axisD][idY] = Math.sin(theta0) * (double)LengthA_D;
        double theta1 = (((double)f * 77D) / 180D) * Math.PI;
        AxisPos[axisB][idX] = Math.cos(theta1) * (double)LengthA_B;
        AxisPos[axisB][idY] = Math.sin(theta1) * (double)LengthA_B;
        double LengthB_D = getDst(AxisPos[axisB][idX], AxisPos[axisB][idY], AxisPos[axisD][idX], AxisPos[axisD][idY]);
        double theta4_1 = Math.acos(((double)LengthA_D - AxisPos[axisB][idX]) / LengthB_D);
        double theta2_1 = Math.PI - theta1 - theta4_1;
        double Cos2_2 = 0.0D;
        double Cos4_2 = 0.0D;
        double Cos3 = 0.0D;
        double theta2_2 = 0.0D;
        double theta4_2 = 0.0D;
        double theta3 = 0.0D;
        Cos2_2 = ((LengthB_D * LengthB_D + (double)(LengthB_C * LengthB_C)) - (double)(LengthC_D * LengthC_D)) / (2D * LengthB_D * (double)LengthB_C);
        Cos2_2 = Cos2_2 <= 1.0D ? Cos2_2 : 1.0D;
        Cos2_2 = Cos2_2 >= -1D ? Cos2_2 : -1D;
        theta2_2 = Math.acos(Cos2_2);
        Cos4_2 = ((LengthB_D * LengthB_D + (double)(LengthC_D * LengthC_D)) - (double)(LengthB_C * LengthB_C)) / (2D * LengthB_D * (double)LengthC_D);
        Cos4_2 = Cos4_2 <= 1.0D ? Cos4_2 : 1.0D;
        Cos4_2 = Cos4_2 >= -1D ? Cos4_2 : -1D;
        theta4_2 = Math.acos(Cos4_2);
        Cos3 = ((double)(LengthC_D * LengthC_D + LengthB_C * LengthB_C) - LengthB_D * LengthB_D) / (double)(2.0F * LengthC_D * LengthB_C);
        Cos3 = Cos3 <= 1.0D ? Cos3 : 1.0D;
        Cos3 = Cos3 >= -1D ? Cos3 : -1D;
        theta3 = Math.acos(Cos3);
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, (float)(((theta2_1 + theta2_2) * 180D) / Math.PI) - 180F, 0.0F);
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, (float)(((theta2_1 + theta2_2) * 180D) / Math.PI) - 180F, 0.0F);
        hiermesh.chunkSetAngles("GearR3A_D0", 0.0F, (float)((theta3 * 180D) / Math.PI) - 5F, 0.0F);
        hiermesh.chunkSetAngles("GearL3A_D0", 0.0F, (float)((theta3 * 180D) / Math.PI) - 5F, 0.0F);
        hiermesh.chunkSetAngles("GearRMortor_D0", 0.0F, (float)(-(((theta4_1 + theta4_2) * 180D) / Math.PI)), 0.0F);
        hiermesh.chunkSetAngles("GearLMortor_D0", 0.0F, (float)(((theta4_1 + theta4_2) * 180D) / Math.PI), 0.0F);
        hiermesh.chunkSetAngles("GearC99_D0", 20F * f, 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, 0.0F, 0.0F);
        Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = 0.0F;
        Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0.0F;
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.0F, 1.0F, 0.0F, 0.1F);
        hiermesh.chunkSetLocate("poleL_D0", Aircraft.xyz, Aircraft.ypr);
        hiermesh.chunkSetLocate("poleR_D0", Aircraft.xyz, Aircraft.ypr);
        float f1 = -1F * (f <= 0.5F ? Aircraft.cvt(f, 0.0F, 0.15F, 0.0F, 94F) : Aircraft.cvt(f, 0.85F, 1.0F, 94F, 0.0F));
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, -f1, 0.0F);
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, -f1, 0.0F);
    }

    protected void moveGear(float f) {
        FW_190V.moveGear(this.hierMesh(), f);
    }

    public void moveWheelSink()
    {
        if(this.FM.Gears.onGround())
        {
            for(int i = 0; i < 2; i++)
                sinkAngle[i] = ((1.0D - Math.acos((double)this.FM.Gears.gWheelSinking[i] + 0.0555555559694767D) / 1.5707963267948966D) * 50D) / 16D;

            resetYPRmodifier();
            Aircraft.xyz[2] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 1.0F, 0.0F, 1.0F);
            hierMesh().chunkSetLocate("MGearL3C_D0", Aircraft.xyz, Aircraft.ypr);
            hierMesh().chunkSetAngles("MGearLArm1_D0", 0.0F, 0.0F, Aircraft.cvt((float)sinkAngle[0], 0.0F, 1.0F, 0.0F, 90F));
            hierMesh().chunkSetAngles("MGearLArm2_D0", 0.0F, 0.0F, Aircraft.cvt((float)sinkAngle[0], 0.0F, 1.0F, 0.0F, 90F) * 2.0F);
            Aircraft.xyz[2] = -Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 1.0F, 0.0F, 1.0F);
            hierMesh().chunkSetLocate("MGearR3C_D0", Aircraft.xyz, Aircraft.ypr);
            hierMesh().chunkSetAngles("MGearRArm1_D0", 0.0F, 0.0F, -Aircraft.cvt((float)sinkAngle[1], 0.0F, 1.0F, 0.0F, 90F));
            hierMesh().chunkSetAngles("MGearRArm2_D0", 0.0F, 0.0F, -Aircraft.cvt((float)sinkAngle[1], 0.0F, 1.0F, 0.0F, 90F) * 2.0F);
            Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[2], 0.0F, 1.0F, 0.0F, 1.0F);
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
            hierMesh().chunkVisible("Head1_D1", true);
            hierMesh().chunkVisible("pilotarm2_d0", false);
            hierMesh().chunkVisible("pilotarm1_d0", false);
            break;
        }
    }

    public void doRemoveBodyFromPlane(int i)
    {
        super.doRemoveBodyFromPlane(i);
        hierMesh().chunkVisible("pilotarm2_d0", false);
        hierMesh().chunkVisible("pilotarm1_d0", false);
    }

    public void missionStarting()
    {
        super.missionStarting();
        hierMesh().chunkVisible("pilotarm2_d0", true);
        hierMesh().chunkVisible("pilotarm1_d0", true);
    }

    public void prepareCamouflage()
    {
        super.prepareCamouflage();
        hierMesh().chunkVisible("pilotarm2_d0", true);
        hierMesh().chunkVisible("pilotarm1_d0", true);
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(this.FM.getAltitude() < 3000F)
            hierMesh().chunkVisible("HMask1_D0", false);
        else
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Pilot1_D0"));
    }

    public void moveCockpitDoor(float f)
    {
        resetYPRmodifier();
        Aircraft.xyz[0] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, -0.5F);
        hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        if(Aircraft.xyz[0] < -0.3F)
        {
            hierMesh().chunkVisible("Wire_D0", false);
            hierMesh().chunkVisible("WireOP_D0", true);
        } else
        {
            hierMesh().chunkVisible("Wire_D0", true);
            hierMesh().chunkVisible("WireOP_D0", false);
        }
        Aircraft.xyz[0] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, -0.3F);
        hierMesh().chunkSetLocate("Step1_D0", Aircraft.xyz, Aircraft.ypr);
        if(Config.isUSE_RENDER())
        {
            if(Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null)
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            setDoorSndFW(f);
        }
    }

    public void setDoorSndFW(float f)
    {
        if(this.FM != null)
        {
            doorSndControl = f;
            if(doorSound == null && this.FM.CT.dvCockpitDoor > 0.0F)
            {
                byte byte0 = 1;
                float f1 = 1.0F / this.FM.CT.dvCockpitDoor;
                doorSndPos = new Point3d(-1D, 0.0D, 0.0D);
                doorSound = newSound("cockpit.fw", false);
                if(doorSound != null)
                {
                    doorSound.setParent(getRootFX());
                    if(f1 <= 1.1F)
                        byte0 = 0;
                    else
                    if(f1 >= 1.8F)
                        byte0 = 2;
                    doorSound.setUsrFlag(byte0);
                }
            }
            float f2 = this.FM.EI.engines[0].getRPM();
            if((f != 0.0F && doorPrev == 0.0F || f != 1.0F && doorPrev == 1.0F) && doorSound != null && this.FM.CT.dvCockpitDoor < 10F)
                doorSound.play(doorSndPos);
            if(f != 0.0F && doorPrev == 0.0F && doorSound != null && !hierMesh().isChunkVisible("Prop1_D1"))
                windExtFw.start();
            else
            if(f != 1.0F && doorPrev == 1.0F && doorSound != null)
                windExtFw.cancel();
            if(f2 > 100F)
                doorSound.setVolume(1.5F);
            else
                doorSound.setVolume(0.7F);
            doorPrev = f;
        }
    }

    public void EjectCanopy()
    {
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("CanopyTop", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("Glass", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("RearArmorPlate", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("CanopyFrameL", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("CanopyFrameR", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("Wire_D0", false);
        Main3D.cur3D().cockpitCur.mesh.chunkVisible("WireOP_D0", false);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("Wire_D0", true);
        if(World.cur().camouflage == 1)
        {
            hierMesh.chunkVisible("GearL5_D0", false);
            hierMesh.chunkVisible("GearR5_D0", false);
        }
        if(World.cur().camouflage == 1 && World.Rnd().nextFloat() < 0.05F)
        {
            hierMesh.chunkVisible("MGearL3C_D0", false);
            hierMesh.chunkVisible("MGearR3C_D0", false);
        }
        if(World.cur().camouflage == 1 && World.Rnd().nextFloat() < 0.03F)
        {
            hierMesh.chunkVisible("CoverL2_D0", true);
            hierMesh.chunkVisible("CoverR2_D0", true);
        }
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        if(this.FM.isPlayers())
            this.FM.EI.engines[0].setControlPropAuto(false);
        this.FM.CT.bHasCockpitDoorControl = true;
        this.FM.CT.dvCockpitDoor = 0.2564102F;
        FW_190V.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public void update(float f)
    {
        if(this.FM.CT.cockpitDoorControl > 0.9F && this.FM.getSpeedKMH() > 240F && !this.FM.Gears.onGround() && this.FM == World.getPlayerFM() && hierMesh().chunkFindCheck("Blister1_D0") != -1 && this.FM.AS.getPilotHealth(0) > 0.0F)
        {
            arrach.setPlay(true);
            EjectCanopy();
            hierMesh().hideSubTrees("Blister1_D0");
            Wreckage wreckage = new Wreckage((ActorHMesh)this.FM.actor, hierMesh().chunkFind("Blister1_D0"));
            wreckage.collide(true);
            Vector3d vector3d = new Vector3d();
            vector3d.set(this.FM.Vwld);
            wreckage.setSpeed(vector3d);
            hierMesh().hideSubTrees("Wire_D0");
            Wreckage wreckage2 = new Wreckage((ActorHMesh)this.FM.actor, hierMesh().chunkFind("Wire_D0"));
            wreckage2.collide(true);
            Vector3d vector3d2 = new Vector3d();
            vector3d2.set(this.FM.Vwld);
            wreckage2.setSpeed(vector3d2);
            hierMesh().chunkVisible("WireOP_D0", false);
            this.FM.CT.cockpitDoorControl = 0.9F;
            this.FM.CT.bHasCockpitDoorControl = false;
            this.FM.VmaxAllowed = 161F;
            this.FM.Sq.dragEngineCx[0] *= 6.2F;
        }
        super.update(f);
        if(HotKeyCmd.getByRecordedId(272).isActive() && this.FM.isPlayers())
        {
            CmdEnv.top().exec("fov 90");
            hierMesh().chunkVisible("Wire_D0", false);
            Main3D.cur3D().setViewInside();
            EjectCanopy();
            if(!hierMesh().isChunkVisible("Prop1_D1") && this.FM.CT.cockpitDoorControl < 0.5F)
                boulon.start();
            if(!Main3D.cur3D().isViewOutside())
                windExtFw.start();
        }
        if(this.FM.AS.bIsAboutToBailout && !this.FM.isPlayers())
            hierMesh().chunkVisible("Wire_D0", false);
        if(bSmokeEffect)
            if(this.FM == World.getPlayerFM() && this.FM.CT.PowerControl >= 1.0F && this.FM.EI.engines[0].getRPM() > 100F || this.FM == World.getPlayerFM() && this.FM.CT.getAfterburnerControl() && this.FM.EI.engines[0].getRPM() > 100F)
                this.FM.AS.setSootState(this, 0, 1);
            else
                this.FM.AS.setSootState(this, 0, 0);
        if(Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null && this.FM.isPlayers())
            if(Main3D.cur3D().cockpits[0].isFocused() || this.FM.CT.cockpitDoorControl == 0.9F && !this.FM.Gears.onGround() || this.FM.CT.cockpitDoorControl == 0.9F && !this.FM.Gears.getWheelsOnGround())
                hierMesh().chunkVisible("Blister1_D0", false);
            else
            if(!this.FM.AS.bIsAboutToBailout || this.FM.AS.bIsAboutToBailout && this.FM.Gears.onGround() && this.FM.CT.cockpitDoorControl > 0.9F)
                hierMesh().chunkVisible("Blister1_D0", true);
        if(this.FM.isPlayers() && (HotKeyCmd.getByRecordedId(161).isActive() && this.FM.EI.isSelectionHasControlAfterburner() || HotKeyCmd.getByRecordedId(142).isActive() && this.FM.EI.isSelectionAllowsAutoProp()))
            bouton.setPlay(true);
        if(this.FM.isPlayers() && this.FM.CT.cockpitDoorControl == 1.0F && this.FM.Gears.onGround())
            this.FM.CT.dvCockpitDoor = 0.2564102F;
        if(this.FM.isPlayers() && this.FM.getSpeedKMH() > 240F)
            HotKeyCmd.getByRecordedId(348).enable(false);
        else
            HotKeyCmd.getByRecordedId(348).enable(true);
        if(!this.FM.isPlayers() && this.FM.Gears.onGround())
            if(this.FM.EI.engines[0].getRPM() < 100F)
                this.FM.CT.cockpitDoorControl = 1.0F;
            else
                this.FM.CT.cockpitDoorControl = 0.0F;
        if ((!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel)this.FM).isRealMode()) && this.FM.Gears.onGround()) {
            if (this.FM.getSpeedKMH() < 50F) {
                this.FM.CT.cockpitDoorControl = 1.0F;
            } else {
                this.FM.CT.cockpitDoorControl = 0.0F;
            }
        }
//        if(!getOp(31) || !getOp(32))
//            this.FM.CT.trimAileron = ((this.FM.CT.ElevatorControl * (s32 - s31) + this.FM.CT.trimElevator * (s18 - s17)) * this.FM.SensPitch) / 3F;
        if(!bHasElevatorControl)
            this.FM.CT.ElevatorControl = 0.0F;
        if(trimElevator != this.FM.CT.trimElevator)
        {
            trimElevator = this.FM.CT.trimElevator;
            hierMesh().chunkSetAngles("StabL_D0", 0.0F, -16F * trimElevator, 0.0F);
            hierMesh().chunkSetAngles("StabR_D0", 0.0F, -16F * trimElevator, 0.0F);
        }
        if(this.FM.Gears.onGround() && this.FM.CT.ElevatorControl >= 0.06F)
            this.FM.Gears.bTailwheelLocked = true;
        else
        if(this.FM.Gears.onGround() && this.FM.CT.ElevatorControl <= 0.06F)
            this.FM.Gears.bTailwheelLocked = false;
    }

    protected void moveElevator(float f)
    {
        f -= trimElevator;
        if(bHasElevatorControl)
        {
            hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -30F * f, 0.0F);
            hierMesh().chunkSetAngles("VatorR_D0", 0.0F, -30F * f, 0.0F);
        }
        float f1 = this.FM.CT.getAileron();
        hierMesh().chunkSetAngles("pilotarm2_d0", Aircraft.cvt(f1, -1F, 1.0F, 14F, -16F), 0.0F, Aircraft.cvt(f1, -1F, 1.0F, 6F, -8F) - Aircraft.cvt(f, -1F, 1.0F, -37F, 35F));
        hierMesh().chunkSetAngles("pilotarm1_d0", 0.0F, 0.0F, Aircraft.cvt(f1, -1F, 1.0F, -16F, 14F) + Aircraft.cvt(f, -1F, 0.0F, -61F, 0.0F) + Aircraft.cvt(f, 0.0F, 1.0F, 0.0F, 43F));
        if(f1 < 0.0F)
            f /= 2.0F;
        hierMesh().chunkSetAngles("Stick01_D0", 0.0F, 15F * f1, Aircraft.cvt(f, -1F, 1.0F, -16F, 16F));
    }

    protected void moveAileron(float f)
    {
        hierMesh().chunkSetAngles("AroneL_D0", 0.0F, -16F * f, 0.0F);
        hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -16F * f, 0.0F);
        float f1 = this.FM.CT.getElevator();
        hierMesh().chunkSetAngles("pilotarm2_d0", Aircraft.cvt(f, -1F, 1.0F, 14F, -16F), 0.0F, Aircraft.cvt(f, -1F, 1.0F, 6F, -8F) - Aircraft.cvt(f1, -1F, 1.0F, -37F, 35F));
        hierMesh().chunkSetAngles("pilotarm1_d0", 0.0F, 0.0F, Aircraft.cvt(f, -1F, 1.0F, -16F, 14F) + Aircraft.cvt(f1, -1F, 0.0F, -61F, 0.0F) + Aircraft.cvt(f1, 0.0F, 1.0F, 0.0F, 43F));
        if(f1 < 0.0F)
            f1 /= 2.0F;
        hierMesh().chunkSetAngles("Stick01_D0", 0.0F, 15F * f, Aircraft.cvt(f1, -1F, 1.0F, -16F, 20F));
    }

    public void moveSteering(float f)
    {
        if(this.FM.CT.getGear() >= 0.98F)
            hierMesh().chunkSetAngles("GearC2_D0", 0.0F, -f, 0.0F);
    }

    public void setOnGround(Point3d point3d, Orient orient, Vector3d vector3d)
    {
        super.setOnGround(point3d, orient, vector3d);
        if(this.FM.isPlayers())
            this.FM.CT.dvCockpitDoor = 80F;
        this.FM.CT.cockpitDoorControl = 1.0F;
    }

    public boolean cut(String s)
    {
        if(s.startsWith("Tail1"))
            this.FM.AS.hitTank(this, 2, 4);
        return super.cut(s);
    }

    protected boolean cutFM1(int i, int j, Actor actor)
    {
        switch(i)
        {
        case 33:
            return this.cutFM(34, j, actor);

        case 36:
            return this.cutFM(37, j, actor);

        case 34:
        case 35:
        default:
            return this.cutFM(i, j, actor);
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xx"))
        {
            if(s.startsWith("xxarmor"))
            {
                Aircraft.debugprintln(this, "*** Armor: Hit..");
                if(s.endsWith("p1"))
                {
                    getEnergyPastArmor(World.Rnd().nextFloat(50F, 50F), shot);
                    if(World.Rnd().nextFloat() < 0.15F)
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
                    Aircraft.debugprintln(this, "*** Armor Glass: Hit..");
                    if(shot.power <= 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** Armor Glass: Bullet Stopped..");
                        if(World.Rnd().nextFloat() < 0.5F)
                            doRicochetBack(shot);
                    }
                } else
                if(s.endsWith("p3"))
                {
                    if(point3d.z < -0.27D)
                        getEnergyPastArmor(4.1D / (Math.abs(Aircraft.v1.z) + 0.00001D), shot);
                    else
                        getEnergyPastArmor(8.1D / (Math.abs(Aircraft.v1.x) + 0.00001D), shot);
                } else
                if(s.endsWith("p6"))
                    getEnergyPastArmor(8D / (Math.abs(Aircraft.v1.x) + 0.00001D), shot);
                return;
            }
            if(s.startsWith("xxcontrols"))
            {
                int i = s.charAt(10) - 48;
                switch(i)
                {
                case 7:
                default:
                    break;

                case 1:
                case 4:
                    if(getEnergyPastArmor(0.1F, shot) > 0.0F)
                    {
                        this.FM.AS.setControlsDamage(shot.initiator, 0);
                        Aircraft.debugprintln(this, "*** Aileron Controls: Control Crank Destroyed..");
                    }
                    break;

                case 2:
                case 3:
                    if(getEnergyPastArmor(0.12F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F)
                    {
                        this.FM.AS.setControlsDamage(shot.initiator, 0);
                        Aircraft.debugprintln(this, "*** Aileron Controls: Disabled..");
                    }
                    break;

                case 5:
                    if(getEnergyPastArmor(0.12F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F)
                    {
                        this.FM.AS.setControlsDamage(shot.initiator, 1);
                        Aircraft.debugprintln(this, "*** Elevator Controls: Disabled / Strings Broken..");
                    }
                    break;

                case 6:
                    if(getEnergyPastArmor(0.12F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F)
                    {
                        this.FM.AS.setControlsDamage(shot.initiator, 2);
                        Aircraft.debugprintln(this, "*** Rudder Controls: Disabled / Strings Broken..");
                    }
                    break;

                case 8:
                    if(getEnergyPastArmor(3.2F, shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** Control Column: Hit, Controls Destroyed..");
                        this.FM.AS.setControlsDamage(shot.initiator, 2);
                        this.FM.AS.setControlsDamage(shot.initiator, 1);
                        this.FM.AS.setControlsDamage(shot.initiator, 0);
                    }
                    break;
                }
                return;
            }
            if(s.startsWith("xxspar"))
            {
                Aircraft.debugprintln(this, "*** Spar Construction: Hit..");
                if(s.startsWith("xxspart") && chunkDamageVisible("Tail1") > 2 && getEnergyPastArmor(2.4F / (float)Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F)
                {
                    Aircraft.debugprintln(this, "*** Tail1 Spars Broken in Half..");
                    nextDMGLevels(1, 2, "Tail1_D3", shot.initiator);
                }
                if(s.startsWith("xxsparli") && chunkDamageVisible("WingLIn") > 2 && getEnergyPastArmor(18F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                {
                    Aircraft.debugprintln(this, "*** WingLIn Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if(s.startsWith("xxsparri") && chunkDamageVisible("WingRIn") > 2 && getEnergyPastArmor(18F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                {
                    Aircraft.debugprintln(this, "*** WingRIn Spars Damaged..");
                    nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if(s.startsWith("xxsparlm") && chunkDamageVisible("WingLMid") > 2 && getEnergyPastArmor(12.7F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                {
                    Aircraft.debugprintln(this, "*** WingLMid Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if(s.startsWith("xxsparrm") && chunkDamageVisible("WingRMid") > 2 && getEnergyPastArmor(12.7F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                {
                    Aircraft.debugprintln(this, "*** WingRMid Spars Damaged..");
                    nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if(s.startsWith("xxsparlo") && chunkDamageVisible("WingLOut") > 2 && getEnergyPastArmor(12.7F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                {
                    Aircraft.debugprintln(this, "*** WingLOut Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if(s.startsWith("xxsparro") && chunkDamageVisible("WingROut") > 2 && getEnergyPastArmor(12.7F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                {
                    Aircraft.debugprintln(this, "*** WingROut Spars Damaged..");
                    nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                return;
            }
            if(s.startsWith("xxlock"))
            {
                Aircraft.debugprintln(this, "*** Lock Construction: Hit..");
                if(s.startsWith("xxlockr") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                {
                    Aircraft.debugprintln(this, "*** Rudder Lock Shot Off..");
                    nextDMGLevels(3, 2, "Rudder1_D" + chunkDamageVisible("Rudder1"), shot.initiator);
                }
                if(s.startsWith("xxlockvl") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                {
                    Aircraft.debugprintln(this, "*** VatorL Lock Shot Off..");
                    nextDMGLevels(3, 2, "VatorL_D" + chunkDamageVisible("VatorL"), shot.initiator);
                }
                if(s.startsWith("xxlockvr") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                {
                    Aircraft.debugprintln(this, "*** VatorR Lock Shot Off..");
                    nextDMGLevels(3, 2, "VatorR_D" + chunkDamageVisible("VatorR"), shot.initiator);
                }
                return;
            }
            if(s.startsWith("xxeng"))
            {
                Aircraft.debugprintln(this, "*** Engine Module: Hit..");
                if(s.endsWith("pipe"))
                {
                    if(World.Rnd().nextFloat() < 0.1F && this.FM.EI.engines[0].getType() == 0 && this.FM.CT.Weapons[1] != null && this.FM.CT.Weapons[1].length != 2)
                    {
                        this.FM.AS.setJamBullets(1, 0);
                        Aircraft.debugprintln(this, "*** Engine Module: Nose Nozzle Pipe Bent..");
                    }
                    getEnergyPastArmor(0.3F, shot);
                } else
                if(s.endsWith("prop"))
                {
                    if(getEnergyPastArmor(0.1F, shot) > 0.0F && World.Rnd().nextFloat() < 0.8F)
                        if(World.Rnd().nextFloat() < 0.5F)
                        {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 3);
                            Aircraft.debugprintln(this, "*** Engine Module: Prop Governor Hit, Disabled..");
                        } else
                        {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 4);
                            Aircraft.debugprintln(this, "*** Engine Module: Prop Governor Hit, Damaged..");
                        }
                } else
                if(s.endsWith("gear"))
                {
                    if(getEnergyPastArmor(4.6F, shot) > 0.0F)
                        if(World.Rnd().nextFloat() < 0.5F)
                        {
                            this.FM.EI.engines[0].setEngineStuck(shot.initiator);
                            Aircraft.debugprintln(this, "*** Engine Module: Bullet Jams Reductor Gear..");
                        } else
                        {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 3);
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 4);
                            Aircraft.debugprintln(this, "*** Engine Module: Reductor Gear Damaged, Prop Governor Failed..");
                        }
                } else
                if(s.endsWith("supc"))
                {
                    if(getEnergyPastArmor(0.1F, shot) > 0.0F)
                    {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 0);
                        Aircraft.debugprintln(this, "*** Engine Module: Turbine Disabled..");
                    }
                    getEnergyPastArmor(0.5F, shot);
                } else
                if(s.endsWith("feed"))
                {
                    if(getEnergyPastArmor(8.9F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F && this.FM.EI.engines[0].getPowerOutput() > 0.7F && this.FM.EI.engines[0].getType() == 0)
                    {
                        this.FM.AS.hitEngine(shot.initiator, 0, 100);
                        Aircraft.debugprintln(this, "*** Engine Module: Pressurized Fuel Line Pierced, Fuel Flamed..");
                    }
                    getEnergyPastArmor(1.0F, shot);
                } else
                if(s.endsWith("fuel"))
                {
                    if(getEnergyPastArmor(1.1F, shot) > 0.0F && this.FM.EI.engines[0].getType() == 0)
                    {
                        this.FM.EI.engines[0].setEngineStops(shot.initiator);
                        Aircraft.debugprintln(this, "*** Engine Module: Fuel Line Stalled, Engine Stalled..");
                    }
                    getEnergyPastArmor(1.0F, shot);
                } else
                if(s.endsWith("case"))
                {
                    if(getEnergyPastArmor(4.2F, shot) > 0.0F)
                    {
                        if(World.Rnd().nextFloat() < shot.power / 175000F)
                        {
                            this.FM.AS.setEngineStuck(shot.initiator, 0);
                            Aircraft.debugprintln(this, "*** Engine Module: Bullet Jams Crank Ball Bearing..");
                        }
                        if(World.Rnd().nextFloat() < shot.power / 50000F && this.FM.EI.engines[0].getType() == 0)
                        {
                            this.FM.AS.hitEngine(shot.initiator, 0, 2);
                            Aircraft.debugprintln(this, "*** Engine Module: Crank Case Hit, Readyness Reduced to " + this.FM.EI.engines[0].getReadyness() + "..");
                        }
                        if(this.FM.EI.engines[0].getType() == 0)
                            this.FM.EI.engines[0].setReadyness(shot.initiator, this.FM.EI.engines[0].getReadyness() - World.Rnd().nextFloat(0.0F, shot.power / 48000F));
                        Aircraft.debugprintln(this, "*** Engine Module: Crank Case Hit, Readyness Reduced to " + this.FM.EI.engines[0].getReadyness() + "..");
                    }
                    getEnergyPastArmor(27.5F, shot);
                } else
                if(s.startsWith("xxeng1cyl"))
                {
                    if(getEnergyPastArmor(2.4F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[0].getCylindersRatio() * (this.FM.EI.engines[0].getType() != 0 ? 0.5F : 1.75F))
                    {
                        if(this.FM.EI.engines[0].getType() == 0)
                            this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int)(shot.power / 4800F)));
                        else
                            this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int)(shot.power / 19200F)));
                        Aircraft.debugprintln(this, "*** Engine Module: Cylinders Hit, " + this.FM.EI.engines[0].getCylindersOperable() + "/" + this.FM.EI.engines[0].getCylinders() + " Left..");
                        if(World.Rnd().nextFloat() < shot.power / 96000F && this.FM.EI.engines[0].getType() == 0)
                        {
                            this.FM.AS.hitEngine(shot.initiator, 0, 3);
                            Aircraft.debugprintln(this, "*** Engine Module: Cylinders Hit, Engine Fires..");
                        }
                        if(World.Rnd().nextFloat() < shot.power / 96000F && this.FM.EI.engines[0].getType() == 1)
                        {
                            this.FM.AS.hitEngine(shot.initiator, 0, 1);
                            Aircraft.debugprintln(this, "*** Engine Module: Cylinders Hit, Engine Fires..");
                        }
                        if(World.Rnd().nextFloat() < 0.01F)
                        {
                            this.FM.AS.setEngineStuck(shot.initiator, 0);
                            Aircraft.debugprintln(this, "*** Engine Module: Bullet Jams Piston Head..");
                        }
                        getEnergyPastArmor(43.6F, shot);
                    }
                } else
                if(s.startsWith("xxeng1mag"))
                {
                    int j = s.charAt(9) - 49;
                    this.FM.EI.engines[0].setMagnetoKnockOut(shot.initiator, j);
                    Aircraft.debugprintln(this, "*** Engine Module: Magneto " + j + " Destroyed..");
                } else
                if(s.endsWith("sync"))
                {
                    if(getEnergyPastArmor(2.1F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F)
                        Aircraft.debugprintln(this, "*** Engine Module: Gun Synchronized Hit, Nose Guns Lose Authority..");
                } else
                if(s.endsWith("oil1") && getEnergyPastArmor(2.4F, shot) > 0.0F)
                {
                    this.FM.AS.hitOil(shot.initiator, 0);
                    Aircraft.debugprintln(this, "*** Engine Module: Oil Radiator Hit..");
                }
                return;
            }
            if(s.startsWith("xxtank"))
            {
                int k = s.charAt(6) - 48;
                switch(k)
                {
                default:
                    break;

                case 1:
                    if(getEnergyPastArmor(0.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F)
                    {
                        if(this.FM.AS.astateTankStates[2] == 0)
                        {
                            Aircraft.debugprintln(this, "*** Fuel Tank: Pierced..");
                            this.FM.AS.hitTank(shot.initiator, 2, 1);
                            this.FM.AS.doSetTankState(shot.initiator, 2, 1);
                        } else
                        if(shot.powerType == 3 && World.Rnd().nextFloat() < 0.9F || World.Rnd().nextFloat() < 0.03F)
                        {
                            this.FM.AS.hitTank(shot.initiator, 2, 2);
                            Aircraft.debugprintln(this, "*** Fuel Tank: Hit..");
                        }
                        if(shot.power > 200000F)
                        {
                            this.FM.AS.hitTank(shot.initiator, 2, 99);
                            Aircraft.debugprintln(this, "*** Fuel Tank: Major Hit..");
                        }
                    }
                    break;

                case 2:
                    if(getEnergyPastArmor(1.2F, shot) <= 0.0F || World.Rnd().nextFloat() >= 0.25F)
                        break;
                    if(this.FM.AS.astateTankStates[1] == 0)
                    {
                        Aircraft.debugprintln(this, "*** Fuel Tank: Pierced..");
                        this.FM.AS.hitTank(shot.initiator, 1, 1);
                        this.FM.AS.doSetTankState(shot.initiator, 1, 1);
                    } else
                    if(shot.powerType == 3 && World.Rnd().nextFloat() < 0.8F || World.Rnd().nextFloat() < 0.03F)
                    {
                        this.FM.AS.hitTank(shot.initiator, 1, 2);
                        Aircraft.debugprintln(this, "*** Fuel Tank: Hit..");
                    }
                    if(shot.power > 200000F)
                    {
                        this.FM.AS.hitTank(shot.initiator, 1, 99);
                        Aircraft.debugprintln(this, "*** Fuel Tank: Major Hit..");
                    }
                    break;

                case 3:
                    if(getEnergyPastArmor(1.2F, shot) <= 0.0F || World.Rnd().nextFloat() >= 0.25F)
                        break;
                    if(this.FM.AS.astateTankStates[0] == 0)
                    {
                        Aircraft.debugprintln(this, "*** Fuel Tank: Pierced..");
                        this.FM.AS.hitTank(shot.initiator, 0, 1);
                        this.FM.AS.doSetTankState(shot.initiator, 0, 1);
                    } else
                    if(shot.powerType == 3 && World.Rnd().nextFloat() < 0.8F || World.Rnd().nextFloat() < 0.03F)
                    {
                        this.FM.AS.hitTank(shot.initiator, 0, 2);
                        Aircraft.debugprintln(this, "*** Fuel Tank: Hit..");
                    }
                    if(shot.power > 200000F)
                    {
                        this.FM.AS.hitTank(shot.initiator, 0, 99);
                        Aircraft.debugprintln(this, "*** Fuel Tank: Major Hit..");
                    }
                    break;
                }
                return;
            }
            if(s.startsWith("xxmw50"))
            {
                if(World.Rnd().nextFloat() < 0.05F)
                {
                    Aircraft.debugprintln(this, "*** MW50 Tank: Pierced..");
                    this.FM.AS.setInternalDamage(shot.initiator, 2);
                }
                return;
            }
            if(s.startsWith("xxmgun"))
            {
                if(s.endsWith("01"))
                    this.FM.AS.setJamBullets(1, 0);
                if(s.endsWith("02"))
                    this.FM.AS.setJamBullets(1, 1);
                getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 24.6F), shot);
                return;
            }
            if(s.startsWith("xxcannon"))
            {
                Aircraft.debugprintln(this, "*** Nose Cannon: Disabled..");
                this.FM.AS.setJamBullets(0, 0);
                getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 24.6F), shot);
                return;
            }
            if(s.startsWith("xxradiat"))
            {
                this.FM.EI.engines[0].setReadyness(shot.initiator, this.FM.EI.engines[0].getReadyness() - World.Rnd().nextFloat(0.0F, 0.05F));
                Aircraft.debugprintln(this, "*** Engine Module: Radiator Hit, Readyness Reduced to " + this.FM.EI.engines[0].getReadyness() + "..");
            }
            return;
        }
        if(s.startsWith("xcf") || s.startsWith("xcockpit"))
        {
            if(chunkDamageVisible("CF") < 3)
                hitChunk("CF", shot);
            if(s.startsWith("xcockpit"))
            {
                if(point3d.z > 0.4D)
                {
                    if(World.Rnd().nextFloat() < 0.2F)
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
                    if(World.Rnd().nextFloat() < 0.2F)
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
                } else
                if(point3d.y > 0.0D)
                {
                    if(World.Rnd().nextFloat() < 0.2F)
                        this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
                } else
                if(World.Rnd().nextFloat() < 0.2F)
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
                if(point3d.x > 0.2D && World.Rnd().nextFloat() < 0.2F)
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
            }
        } else
        if(s.startsWith("xeng"))
        {
            if(chunkDamageVisible("Engine1") < 2)
                hitChunk("Engine1", shot);
        } else
        if(s.startsWith("xtail"))
        {
            if(chunkDamageVisible("Tail1") < 3)
                hitChunk("Tail1", shot);
        } else
        if(s.startsWith("xkeel"))
        {
            if(chunkDamageVisible("Keel1") < 2)
                hitChunk("Keel1", shot);
        } else
        if(s.startsWith("xrudder"))
        {
            if(chunkDamageVisible("Rudder1") < 1)
                hitChunk("Rudder1", shot);
        } else
        if(s.startsWith("xstab"))
        {
            if(s.startsWith("xstabl") && chunkDamageVisible("StabL") < 2)
                hitChunk("StabL", shot);
            if(s.startsWith("xstabr") && chunkDamageVisible("StabR") < 1)
                hitChunk("StabR", shot);
        } else
        if(s.startsWith("xvator"))
        {
            if(s.startsWith("xvatorl") && chunkDamageVisible("VatorL") < 1)
                hitChunk("VatorL", shot);
            if(s.startsWith("xvatorr") && chunkDamageVisible("VatorR") < 1)
                hitChunk("VatorR", shot);
        } else
        if(s.startsWith("xwing"))
        {
            if(s.startsWith("xwinglin") && chunkDamageVisible("WingLIn") < 3)
                hitChunk("WingLIn", shot);
            if(s.startsWith("xwingrin") && chunkDamageVisible("WingRIn") < 3)
                hitChunk("WingRIn", shot);
            if(s.startsWith("xwinglmid") && chunkDamageVisible("WingLMid") < 3)
                hitChunk("WingLMid", shot);
            if(s.startsWith("xwingrmid") && chunkDamageVisible("WingRMid") < 3)
                hitChunk("WingRMid", shot);
            if(s.startsWith("xwinglout") && chunkDamageVisible("WingLOut") < 3)
                hitChunk("WingLOut", shot);
            if(s.startsWith("xwingrout") && chunkDamageVisible("WingROut") < 3)
                hitChunk("WingROut", shot);
        } else
        if(s.startsWith("xarone"))
        {
            if(s.startsWith("xaronel"))
                hitChunk("AroneL", shot);
            if(s.startsWith("xaroner"))
                hitChunk("AroneR", shot);
        } else
        if(s.startsWith("xpilot") || s.startsWith("xhead"))
        {
            byte byte0 = 0;
            int l;
            if(s.endsWith("a"))
            {
                byte0 = 1;
                l = s.charAt(6) - 49;
            } else
            if(s.endsWith("b"))
            {
                byte0 = 2;
                l = s.charAt(6) - 49;
            } else
            {
                l = s.charAt(5) - 49;
            }
            hitFlesh(l, shot, byte0);
        }
    }

    private boolean bSmokeEffect;
    private float trimElevator;
    private boolean bHasElevatorControl;
    double sinkAngle[];
    static float LengthA_B = 0.543601F;
    static float LengthA_D = 0.4612681F;
    static float LengthB_C = 0.3014931F;
    static float LengthC_D = 0.38F;
    static double AxisPos[][] = new double[4][2];
    static int axisA = 0;
    static int axisB = 1;
    static int axisC = 2;
    static int axisD = 3;
    static int idX = 0;
    static int idY = 1;
    private Point3d doorSndPos;
    private float doorPrev;
    protected float doorSndControl;
    protected SoundFX doorSound;
    protected AudioStream boulon;
    protected AudioStream bouton;
    protected AudioStream arrach;
    private AudioStream windExtFw;

    static 
    {
        Class class1 = FW_190V.class;
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
    }
}
