package com.maddox.il2.fm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Point3f;
import com.maddox.JGP.Tuple3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.Formation;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorHMesh;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.engine.Orientation;
import com.maddox.il2.objects.air.Aircraft;
import com.maddox.il2.objects.air.Scheme1;
import com.maddox.il2.objects.air.Scheme2a;
import com.maddox.il2.objects.air.Scheme5;
import com.maddox.il2.objects.air.Scheme7;
import com.maddox.il2.objects.effects.Explosions;
import com.maddox.il2.objects.sounds.SndAircraft;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.rts.Finger;
import com.maddox.rts.HomePath;
import com.maddox.rts.InOutStreams;
import com.maddox.rts.KryptoInputFilter;
import com.maddox.rts.Property;
import com.maddox.rts.SFSInputStream;
import com.maddox.rts.SectFile;
import com.maddox.rts.Time;

public class FlightModelMain extends FMMath {

    public float getSpeedKMH() {
        return (float) (this.Vflow.x * 3.6D);
    }

    public float getSpeed2KMH() {
        return (float) (this.Vflow.x * 3.6D);
    }

    public float getSpeed() {
        return (float) this.Vflow.x;
    }

    public void getSpeed(Vector3d vector3d) {
        vector3d.set(this.Vair);
    }

    public float getVertSpeed() {
        return (float) this.Vair.z;
    }

    public float getAltitude() {
        return (float) this.Loc.z;
    }

    public float getAOA() {
        return this.AOA;
    }

    public float getAOS() {
        return this.AOS;
    }

    public void getLoc(Point3f point3f) {
        point3f.set(this.Loc);
    }

    public void getLoc(Point3d point3d) {
        point3d.set(this.Loc);
    }

    public void getOrient(Orientation orientation) {
        orientation.set(this.Or);
    }

    public Vector3d getW() {
        return this.W;
    }

    public Vector3d getAW() {
        return this.AW;
    }

    public Vector3d getAccel() {
        return this.Accel;
    }

    public Vector3d getLocalAccel() {
        return this.LocalAccel;
    }

    public Vector3d getBallAccel() {
        TmpV.set(this.LocalAccel);
        if (this.Vwld.lengthSquared() < 10D) {
            double d = this.Vwld.lengthSquared() - 5D;
            if (d < 0.0D) d = 0.0D;
            TmpV.scale(0.2D * d);
        }
        TmpA.set(0.0D, 0.0D, -Atmosphere.g());
        this.Or.transformInv(TmpA);
        TmpA.sub(TmpV);
        return TmpA;
    }

    public Vector3d getAM() {
        return this.AM;
    }

    public Vector3d getVflow() {
        return this.Vflow;
    }

    public void load(String s) {
        String s1 = "Error loading params from " + s;
        SectFile sectfile = sectFile(s);
        String s2 = "Aircraft";
        this.Wingspan = sectfile.get(s2, "Wingspan", 0.0F);
        if (this.Wingspan == 0.0F) throw new RuntimeException(s1);
        this.Length = sectfile.get(s2, "Length", 0.0F);
        if (this.Length == 0.0F) throw new RuntimeException(s1);
        this.Scheme = sectfile.get(s2, "Type", -1);
        if (this.Scheme == -1) throw new RuntimeException(s1);
        this.crew = sectfile.get(s2, "Crew", 0, 0, 9);
        if (this.crew == 0) throw new RuntimeException(s1);
        for (int k = 0; k < this.AS.astatePilotFunctions.length; k++) {
            int i = sectfile.get(s2, "CrewFunction" + k, -1);
            if (i != -1) this.AS.astatePilotFunctions[k] = (byte) i;
        }

        // TODO: SAS~Storebror --- backport from 4.14.1
        this.MaxNumFuelTank = sectfile.get(s2, "MaxNumFuelTank", 4);
        if (this.MaxNumFuelTank < 4) this.MaxNumFuelTank = 4;
        else if (this.MaxNumFuelTank > 20) this.MaxNumFuelTank = 20;
        this.MaxNumRudder = sectfile.get(s2, "MaxNumRudder", 1);
        if (this.MaxNumRudder < 1) this.MaxNumRudder = 1;
        else if (this.MaxNumRudder > 2) this.MaxNumRudder = 2;
        this.ShowFuelLevel = sectfile.get(s2, "ShowFuelLevel", -1);
        // ---
        
        // TODO: By SAS~Storebror: New Formation Parameters +++
        String formationDefaultName = sectfile.get(s2, "FormationDefault", "None");
        boolean formationDefaultIsNumeric = false;
        try {
            this.formationDefault = Byte.parseByte(formationDefaultName);
            formationDefaultIsNumeric = true;
            if (this.formationDefault != Formation.F_DEFAULT && (this.formationDefault < Formation.F_ECHELONRIGHT || this.formationDefault > Formation.F_JAVELIN))
                this.formationDefault = Formation.F_DEFAULT;
        } catch (Exception e) {}
        if (!formationDefaultIsNumeric) {
            if (formationDefaultName.equalsIgnoreCase("EchelonRight")) this.formationDefault = Formation.F_ECHELONRIGHT;
            else if (formationDefaultName.equalsIgnoreCase("EchelonLeft")) this.formationDefault = Formation.F_ECHELONLEFT;
            else if (formationDefaultName.equalsIgnoreCase("LineAbreast")) this.formationDefault = Formation.F_LINEABREAST;
            else if (formationDefaultName.equalsIgnoreCase("LineAstern")) this.formationDefault = Formation.F_LINEASTERN;
            else if (formationDefaultName.equalsIgnoreCase("Vic")) this.formationDefault = Formation.F_VIC;
            else if (formationDefaultName.equalsIgnoreCase("FingerFour")) this.formationDefault = Formation.F_FINGERFOUR;
            else if (formationDefaultName.equalsIgnoreCase("Diamond")) this.formationDefault = Formation.F_DIAMOND;
            else if (formationDefaultName.equalsIgnoreCase("Line")) this.formationDefault = Formation.F_LINE;
            else if (formationDefaultName.equalsIgnoreCase("LineAsternLong")) this.formationDefault = Formation.F_LINEASTERNLONG;
            else if (formationDefaultName.equalsIgnoreCase("LineupStacked")) this.formationDefault = Formation.F_LINEUPSTACKED;
            else if (formationDefaultName.equalsIgnoreCase("Javelin")) this.formationDefault = Formation.F_JAVELIN;
            else this.formationDefault = Formation.F_DEFAULT;
        }
        
        String formationOffsetName = sectfile.get(s2, "FormationOffset", "");
        if (formationOffsetName.equalsIgnoreCase("CombatBox")) {
            this.formationOffset[0] = new Vector3d(300D, -150D, 0.0D);
            this.formationOffset[1] = new Vector3d(100D, -80D, -30D);
            this.formationOffset[2] = new Vector3d(100D, 80D, 25D);
            this.formationOffset[3] = new Vector3d(200D, 0.0D, 50D);
            this.formationOffsetValid = true;
        }
        
        if (!this.formationOffsetValid) {
            try {
                int validOffsets = 0;
                for (int wingSquadIndex = 0; wingSquadIndex < 4; wingSquadIndex++) {
                    float leaderOffsetX = sectfile.get(s2, "FormationOffset" + (wingSquadIndex + 1) + "X", Float.NaN);
                    if (Float.isNaN(leaderOffsetX))
                        break;
                    float leaderOffsetY = sectfile.get(s2, "FormationOffset" + (wingSquadIndex + 1) + "Y", Float.NaN);
                    if (Float.isNaN(leaderOffsetY))
                        break;
                    float leaderOffsetZ = sectfile.get(s2, "FormationOffset" + (wingSquadIndex + 1) + "Z", Float.NaN);
                    if (Float.isNaN(leaderOffsetZ))
                        break;
                    this.formationOffset[wingSquadIndex] = new Vector3d(leaderOffsetX, leaderOffsetY, leaderOffsetZ);
                    validOffsets++;
                }
                this.formationOffsetValid = validOffsets>3;
            } catch (Exception e) {
            }
        }
        this.formationScaleCoeff = sectfile.get(s2, "FormationScaleCoeff", Float.NaN);
        // TODO: By SAS~Storebror: New Formation Parameters ---

        s2 = "Controls";
        int j = sectfile.get(s2, "CDiveBrake", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.AIRBRAKE = j == 1;
        j = sectfile.get(s2, "CAileron", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasAileronControl = j == 1;
        j = sectfile.get(s2, "CAileronTrim", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasAileronTrim = j == 1;
        j = sectfile.get(s2, "CElevator", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasElevatorControl = j == 1;
        j = sectfile.get(s2, "CElevatorTrim", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasElevatorTrim = j == 1;
        j = sectfile.get(s2, "CRudder", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasRudderControl = j == 1;
        j = sectfile.get(s2, "CRudderTrim", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasRudderTrim = j == 1;
        j = sectfile.get(s2, "CFlap", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasFlapsControl = j == 1;
        j = sectfile.get(s2, "CFlapPos", -1);

        // TODO: Changed by SAS~Storebror because it's just BS.
//        if (j < 0 || j > 3) throw new RuntimeException(s1);
//        this.CT.bHasFlapsControlRed = j < 3;
        this.CT.bHasFlapsControlRed = j > 0 && j < 3;
        // ---

        j = sectfile.get(s2, "CDiveBrake", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasAirBrakeControl = j == 1;
        j = sectfile.get(s2, "CUndercarriage", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasGearControl = j == 1;
        j = sectfile.get(s2, "CArrestorHook", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasArrestorControl = j == 1;
        j = sectfile.get(s2, "CWingFold", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasWingControl = j == 1;
        j = sectfile.get(s2, "CCockpitDoor", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasCockpitDoorControl = j == 1;
        j = sectfile.get(s2, "CWheelBrakes", 1);
        this.CT.bHasBrakeControl = j == 1;
        j = sectfile.get(s2, "CLockTailwheel", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasLockGearControl = j == 1;
        this.CT.AilThr = 0.27778F * sectfile.get(s2, "CAileronThreshold", 360F);
        this.CT.RudThr = 0.27778F * sectfile.get(s2, "CRudderThreshold", 360F);
        this.CT.ElevThr = 0.27778F * sectfile.get(s2, "CElevatorThreshold", 403.2F);
        this.CT.CalcTresholds();

        j = sectfile.get(s2, "CBombBay", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasBayDoorControl = j == 1;

        // TODO: SAS~Storebror: Added for Cross Version Compatibility.
        j = sectfile.get(s2, "CBayDoors", 0);
        this.CT.bHasBayDoorControl |= j == 1;
        // ---

        j = sectfile.get(s2, "CSideDoor", 0);
//        if (j != 0 && j != 1) throw new RuntimeException(s1); // TODO: Removed by SAS~Storebror because... what gives?
        this.CT.bHasSideDoorControl = j == 1;

        // TODO: Changed by SAS~Storebror because it's just BS.
//        this.CT.setTrimAileronControl(sectfile.get(s2, "DefaultAileronTrim", -999F));
//        if (this.CT.getTrimAileronControl() == -999F) throw new RuntimeException(s1);
        this.CT.setTrimAileronControl(sectfile.get(s2, "DefaultAileronTrim", 0F));
        // ---
        if (!this.CT.bHasElevatorTrim) // TODO: Changed by SAS~Storebror because it's just BS.
//            this.CT.setTrimElevatorControl(sectfile.get(s2, "DefaultElevatorTrim", -999F));
//            if (this.CT.getTrimElevatorControl() == -999F) throw new RuntimeException(s1);
            this.CT.setTrimElevatorControl(sectfile.get(s2, "DefaultElevatorTrim", 0F));
        // ---
        // TODO: Changed by SAS~Storebror because it's just BS.
//        this.CT.setTrimRudderControl(sectfile.get(s2, "DefaultRudderTrim", -999F));
//        if (this.CT.getTrimRudderControl() == -999F) throw new RuntimeException(s1);
        this.CT.setTrimRudderControl(sectfile.get(s2, "DefaultRudderTrim", 0F));
        // ---
        if (!this.CT.bHasGearControl) {
            this.GearCX = 0.0F;
            this.CT.GearControl = 1.0F;
            this.CT.setFixedGear(true);
        }
        if (!this.CT.bHasFlapsControl) this.CT.FlapsControl = 0.0F;
        j = sectfile.get(s2, "cElectricProp", 0);
        this.CT.bUseElectricProp = j == 1;
        float f = sectfile.get(s2, "GearPeriod", -999F);
        if (f != -999F) this.CT.dvGear = 1.0F / f;
        f = sectfile.get(s2, "WingPeriod", -999F);
        if (f != -999F) this.CT.dvWing = 1.0F / f;
        f = sectfile.get(s2, "CockpitDoorPeriod", -999F);
        if (f != -999F) this.CT.dvCockpitDoor = 1.0F / f;

        // TODO: EngineMod 2.8 Backport +++
        // New FM parameter - AI Take-off pitch up target degrees in rotation and continuous climb
        // example: short gear leg jets - Rotation=5 deg, Climb=15 deg. Common jets - Rotation=10 deg, Climb=15 deg.
        // --------------------------------------------------------
        f = sectfile.get(s2, "CTargetPitchDegreeAITakeoffRotation", -1F);
        if (f > 0.0F && f <= 30.0F) CT.targetDegreeAITakeoffRotation = f;
        f = sectfile.get(s2, "CTargetPitchDegreeAITakeoffClimb", -1F);
        if (f > 0.0F && f <= 30.0F) CT.targetDegreeAITakeoffClimb = f;
        // TODO: EngineMod 2.8 Backport ---
        
        switch (this.Scheme) {
            default:
                throw new RuntimeException("Invalid Plane Scheme (Can't Get There!)..");

            case 0:
            case 1:
                float f1 = this.Length * 0.35F;
                f1 *= f1;
                float f11 = this.Length * 0.125F;
                f11 *= f11;
                float f6 = this.Wingspan * 0.2F;
                f6 *= f6;
                float f16 = this.Length * 0.07F;
                f16 *= f16;
                this.J0.z = f1 * 0.2F + f11 * 0.4F + f6 * 0.4F;
                this.J0.y = f1 * 0.2F + f11 * 0.4F + f16 * 0.4F;
                this.J0.x = f16 * 0.6F + f6 * 0.4F;
                break;

            case 2:
                float f2 = this.Length * 0.35F;
                f2 *= f2;
                float f12 = this.Length * 0.125F;
                f12 *= f12;
                float f7 = this.Wingspan * 0.2F;
                f7 *= f7;
                float f17 = this.Length * 0.07F;
                f17 *= f17;
                this.J0.z = f2 * 0.2F + f12 * 0.1F + f7 * 0.7F;
                this.J0.y = f2 * 0.2F + f12 * 0.1F + f17 * 0.7F;
                this.J0.x = f17 * 0.3F + f7 * 0.7F;
                break;

            case 3:
                float f3 = this.Length * 0.35F;
                f3 *= f3;
                float f13 = this.Length * 0.125F;
                f13 *= f13;
                float f8 = this.Wingspan * 0.2F;
                f8 *= f8;
                float f18 = this.Length * 0.07F;
                f18 *= f18;
                this.J0.z = f3 * 0.2F + f13 * 0.2F + f8 * 0.6F;
                this.J0.y = f3 * 0.2F + f13 * 0.2F + f18 * 0.6F;
                this.J0.x = f18 * 0.2F + f8 * 0.8F;
                break;

            case 4:
            case 5:
            case 7:
            case 8:
            case 10:
                float f4 = this.Length * 0.35F;
                f4 *= f4;
                float f14 = this.Length * 0.125F;
                f14 *= f14;
                float f9 = this.Wingspan * 0.2F;
                f9 *= f9;
                float f19 = this.Length * 0.07F;
                f19 *= f19;
                this.J0.z = f4 * 0.25F + f14 * 0.15F + f9 * 0.6F;
                this.J0.y = f4 * 0.25F + f14 * 0.15F + f19 * 0.6F;
                this.J0.x = f19 * 0.4F + f9 * 0.6F;
                break;

            case 6:
                float f5 = this.Length * 0.35F;
                f5 *= f5;
                float f15 = this.Length * 0.125F;
                f15 *= f15;
                float f10 = this.Wingspan * 0.2F;
                f10 *= f10;
                float f20 = this.Length * 0.07F;
                f20 *= f20;
                this.J0.z = f5 * 0.25F + f15 * 0.15F + f10 * 0.6F;
                this.J0.y = f5 * 0.25F + f15 * 0.15F + f20 * 0.6F;
                this.J0.x = f20 * 0.4F + f10 * 0.6F;
                break;
        }
        s2 = "Params";
        if (sectfile.exist(s2, "ReferenceWeight")) this.refM = sectfile.get(s2, "ReferenceWeight", 0.0F, -2000F, 2000F);
        else this.refM = 0.0F;
        this.M.load(sectfile, this);
        this.Sq.load(sectfile);
        this.Arms.load(sectfile);
        Aircraft.debugprintln(super.actor, "Calling engines interface to resolve file '" + sectfile.toString() + "'....");
        this.EI.load((FlightModel) this, sectfile);
        this.Gears.load(sectfile);
        this.Ss.load(sectfile);
        if (sectfile.exist(s2, "G_CLASS")) {
            this.LimitLoad = sectfile.get(s2, "G_CLASS", 12F, 0.0F, 15F);
            this.LimitLoad = this.LimitLoad / 1.5F;
        } else this.LimitLoad = 12F;
        if (sectfile.exist(s2, "G_CLASS_COEFF")) this.G_ClassCoeff = sectfile.get(s2, "G_CLASS_COEFF", 20F, -30F, 50F);
        else this.G_ClassCoeff = 20F;
        float f21 = this.M.maxWeight * Atmosphere.g();
        float f22 = this.Sq.squareWing;
        this.Vmax = sectfile.get(s2, "Vmax", 1.0F);
        this.VmaxH = sectfile.get(s2, "VmaxH", 1.0F);
        this.Vmin = sectfile.get(s2, "Vmin", 1.0F);
        this.HofVmax = sectfile.get(s2, "HofVmax", 1.0F);
        this.VmaxFLAPS = sectfile.get(s2, "VmaxFLAPS", 1.0F);
        this.VminFLAPS = sectfile.get(s2, "VminFLAPS", 1.0F);
        this.SensYaw = sectfile.get(s2, "SensYaw", 1.0F);
        this.SensPitch = sectfile.get(s2, "SensPitch", 1.0F);
        this.SensRoll = sectfile.get(s2, "SensRoll", 1.0F);
        this.VmaxAllowed = sectfile.get(s2, "VmaxAllowed", this.VmaxH * 1.3F);
        this.Range = sectfile.get(s2, "Range", 800F);
        this.CruiseSpeed = sectfile.get(s2, "CruiseSpeed", 0.7F * this.Vmax);
        this.FuelConsumption = this.M.maxFuel / (0.64F * (this.Range / this.CruiseSpeed * 3600F) * this.EI.getNum());
        
        // TODO: Added by SAS~Storebror: +++ Additional parameter for max flaps & gear speeds +++
        this.vMaxGear = sectfile.get(s2, "VmaxGEAR", 405F);
        this.vJamFlaps = sectfile.get(s2, "VjamFLAPS", -1F);
        if (this.vJamFlaps < 0F) this.vJamFlaps = Math.max(this.VmaxFLAPS, 300F);
        this.vMaxGear /= 3.6F;
        this.vJamFlaps /= 3.6F;
        // TODO: Added by SAS~Storebror: --- Additional parameter for max flaps & gear speeds ---
        
        this.Vmax /= 3.6F;
        this.VmaxH /= 3.6F;
        this.Vmin /= 3.6F;
        this.VmaxFLAPS /= 3.6F;
        this.VminFLAPS *= 0.2416667F;
        this.VmaxAllowed /= 3.6F;
        this.Fusel.lineCyCoeff = 0.02F;
        this.Fusel.AOAMinCx_Shift = 0.0F;
        this.Fusel.Cy0_0 = 0.0F;
        this.Fusel.AOACritH_0 = 17F;
        this.Fusel.AOACritL_0 = -17F;
        this.Fusel.CyCritH_0 = 0.2F;
        this.Fusel.CyCritL_0 = -0.2F;
        this.Fusel.parabCxCoeff_0 = 0.0006F;
        this.Fusel.CxMin_0 = 0.0F;
        this.Fusel.Cy0_1 = 0.0F;
        this.Fusel.AOACritH_1 = 17F;
        this.Fusel.AOACritL_1 = -17F;
        this.Fusel.CyCritH_1 = 0.2F;
        this.Fusel.CyCritL_1 = -0.2F;
        this.Fusel.CxMin_1 = 0.0F;
        this.Fusel.parabCxCoeff_1 = 0.0006F;
        this.Fusel.declineCoeff = 0.007F;
        this.Fusel.maxDistAng = 30F;
        this.Fusel.parabAngle = 5F;
        this.Fusel.setFlaps(0.0F);
        this.Tail.lineCyCoeff = 0.085F;
        this.Tail.AOAMinCx_Shift = 0.0F;
        this.Tail.Cy0_0 = 0.0F;
        this.Tail.AOACritH_0 = 17F;
        this.Tail.AOACritL_0 = -17F;
        this.Tail.CyCritH_0 = 1.1F;
        this.Tail.CyCritL_0 = -1.1F;
        this.Tail.parabCxCoeff_0 = 0.0006F;
        this.Tail.CxMin_0 = 0.02F;
        this.Tail.Cy0_1 = 0.0F;
        this.Tail.AOACritH_1 = 17F;
        this.Tail.AOACritL_1 = -17F;
        this.Tail.CyCritH_1 = 1.1F;
        this.Tail.CyCritL_1 = -1.1F;
        this.Tail.CxMin_1 = 0.02F;
        this.Tail.parabCxCoeff_1 = 0.0006F;
        this.Tail.declineCoeff = 0.007F;
        this.Tail.maxDistAng = 30F;
        this.Tail.parabAngle = 5F;
        this.Tail.setFlaps(0.0F);
        s2 = "Params";
        this.Wing.AOA_crit = sectfile.get(s2, "CriticalAOA", 16F);
        this.Wing.V_max = 0.27778F * sectfile.get(s2, "Vmax", 500F);
        this.Wing.V_min = 0.27778F * sectfile.get(s2, "Vmin", 160F);
        this.Wing.V_maxFlaps = 0.27778F * sectfile.get(s2, "VmaxFLAPS", 270F);
        this.Wing.V_land = 0.27778F * sectfile.get(s2, "VminFLAPS", 140F);
        this.Wing.T_turn = sectfile.get(s2, "T_turn", 20F);
        this.Wing.V_turn = 0.27778F * sectfile.get(s2, "V_turn", 300F);
        this.Wing.Vz_climb = sectfile.get(s2, "Vz_climb", 18F);
        this.Wing.V_climb = 0.27778F * sectfile.get(s2, "V_climb", 270F);
        this.Wing.K_max = sectfile.get(s2, "K_max", 14F);
        this.Wing.Cy0_max = sectfile.get(s2, "Cy0_max", 0.15F);
        this.Wing.FlapsMult = sectfile.get(s2, "FlapsMult", 0.16F);
        this.Wing.FlapsAngSh = sectfile.get(s2, "FlapsAngSh", 4F);
        this.Wing.P_Vmax = this.EI.forcePropAOA(this.Wing.V_max, 0.0F, 1.1F, true);
        this.Wing.S = f22;
        this.Wing.G = f21;
        s2 = "Polares";
        f = sectfile.get(s2, "lineCyCoeff", -999F);
        if (f != -999F) {
            this.Wing.lineCyCoeff = f;
            this.Wing.AOAMinCx_Shift = sectfile.get(s2, "AOAMinCx_Shift", 0.0F);
            this.Wing.Cy0_0 = sectfile.get(s2, "Cy0_0", 0.15F);
            this.Wing.AOACritH_0 = sectfile.get(s2, "AOACritH_0", 16F);
            this.Wing.AOACritL_0 = sectfile.get(s2, "AOACritL_0", -16F);
            this.Wing.CyCritH_0 = sectfile.get(s2, "CyCritH_0", 1.1F);
            this.Wing.CyCritL_0 = sectfile.get(s2, "CyCritL_0", -0.8F);
            this.Wing.parabCxCoeff_0 = sectfile.get(s2, "parabCxCoeff_0", 0.0008F);
            this.Wing.CxMin_0 = sectfile.get(s2, "CxMin_0", 0.026F);
            this.Wing.Cy0_1 = sectfile.get(s2, "Cy0_1", 0.65F);
            this.Wing.AOACritH_1 = sectfile.get(s2, "AOACritH_1", 15F);
            this.Wing.AOACritL_1 = sectfile.get(s2, "AOACritL_1", -18F);
            this.Wing.CyCritH_1 = sectfile.get(s2, "CyCritH_1", 1.6F);
            this.Wing.CyCritL_1 = sectfile.get(s2, "CyCritL_1", -0.75F);
            this.Wing.CxMin_1 = sectfile.get(s2, "CxMin_1", 0.09F);
            this.Wing.parabCxCoeff_1 = sectfile.get(s2, "parabCxCoeff_1", 0.0025F);
            this.Wing.parabAngle = sectfile.get(s2, "parabAngle", 5F);
            this.Wing.declineCoeff = sectfile.get(s2, "Decline", 0.007F);
            this.Wing.maxDistAng = sectfile.get(s2, "maxDistAng", 30F);
            this.Wing.setFlaps(0.0F);
        } else throw new RuntimeException(s1);
    }

    public void drawData() {
        for (int i = 0; i < 250; i++) {
            this.Wing.normP[i] = this.EI.forcePropAOA(i, 0.0F, 1.0F, false);
            this.Wing.maxP[i] = this.EI.forcePropAOA(i, 1000F, 1.1F, true);
        }

        this.Wing.drawGraphs(this.turnFile);
        this.Wing.setFlaps(0.0F);
        this.drawSpeed(this.speedFile);
        this.Wing.setFlaps(0.0F);
    }

    public void drawSpeed(String s) {
        try {
            PrintWriter printwriter = new PrintWriter(new BufferedWriter(new FileWriter(HomePath.toFileSystemName(s, 0))));
            for (int i = 0; i <= 10000; i += 100) {
                printwriter.print(i + "\t");
                float f = -1000F;
                float f1 = -1000F;
                int j = 50;
                do {
                    if (j >= 1500) break;
                    float f2 = this.EI.forcePropAOA(j * 0.27778F, i, 1.1F, true);
                    float f4 = this.Wing.getClimb(j * 0.27778F, i, f2);
                    if (f4 > f) f = f4;
                    if (f4 < 0.0F && f4 < f) {
                        f1 = j;
                        break;
                    }
                    j++;
                } while (true);
                if (f1 < 0.0F) printwriter.print("\t");
                else printwriter.print(f1 + "\t");
                printwriter.print(f * this.Wing.Vyfac + "\t");
                f = -1000F;
                f1 = -1000F;
                j = 50;
                do {
                    if (j >= 1500) break;
                    float f3 = this.EI.forcePropAOA(j * 0.27778F, i, 1.0F, false);
                    float f5 = this.Wing.getClimb(j * 0.27778F, i, f3);
                    if (f5 > f) f = f5;
                    if (f5 < 0.0F && f5 < f) {
                        f1 = j;
                        break;
                    }
                    j++;
                } while (true);
                if (f1 < 0.0F) printwriter.print("\t");
                else printwriter.print(f1 + "\t");
                printwriter.print(f * this.Wing.Vyfac + "\t");
                printwriter.println();
            }

            printwriter.close();
        } catch (IOException ioexception) {
            System.out.println("File save failed: " + ioexception.getMessage());
            ioexception.printStackTrace();
        }
    }

    public FlightModelMain(String s) {
        this.flags0 = 240L;
        this.bDamagedGround = false;
        this.bDamaged = false;
        this.damagedInitiator = null;
        this.CT = new Controls(this);
        this.SensYaw = 0.3F;
        this.SensPitch = 0.5F;
        this.SensRoll = 0.4F;
        this.GearCX = 0.035F;
        this.radiatorCX = 0.003F;
        this.Loc = new Point3d();
        this.Or = new Orientation();
        this.Offset = new Vector3d(25D, 25D, 0.0D);
        this.formationType = 0;
        this.formationScale = 1.0F;
        this.minElevCoeff = 4F;
        this.BarometerZ = 0.0F;
        this.WingDiff = 0.0F;
        this.WingLoss = 0.0F;
        this.turbCoeff = 1.0F;
        this.FuelConsumption = 0.06F;
        this.producedAM = new Vector3d(0.0D, 0.0D, 0.0D);
        this.producedAF = new Vector3d(0.0D, 0.0D, 0.0D);
        this.fmsfxCurrentType = -1;
        this.fmsfxPrevValue = 0.0F;
        this.fmsfxTimeDisable = -1L;
        this.AF = new Vector3d();
        this.AM = new Vector3d();
        this.GF = new Vector3d();
        this.GM = new Vector3d();
        this.SummF = new Vector3d();
        this.SummM = new Vector3d();
        this.ACmeter = new Vector3d();
        this.Accel = new Vector3d();
        this.LocalAccel = new Vector3d();
        this.BallAccel = new Vector3d();
        this.Vwld = new Vector3d();
        this.Vrel = new Vector3d();
        this.Vair = new Vector3d();
        this.Vflow = new Vector3d();
        this.Vwind = new Vector3d();
        this.J0 = new Vector3d();
        this.J = new Vector3d();
        this.W = new Vector3d();
        this.AW = new Vector3d();
        this.EI = new EnginesInterface();
        this.M = new Mass();
        this.AS = new AircraftState();
        this.Sq = new Squares();
        this.Ss = new Supersonic();
        this.Arms = new Arm();
        this.Gears = new Gear();
        this.Wing = new Polares();
        this.Tail = new Polares();
        this.Fusel = new Polares();
        this.Vmax = 116.6667F;
        this.VmaxH = 122.2222F;
        this.Vmin = 43.33334F;
        this.HofVmax = 277.7778F;
        this.VmaxFLAPS = 72.22223F;
        this.VminFLAPS = 39.72223F;
        this.VmaxAllowed = 208.3333F;
        this.indSpeed = 0.0F;
        this.AOA_Crit = 15.5F;
        this.Range = 800F;
        this.CruiseSpeed = 370F;
        this.damagedParts = new int[7];
        this.maxDamage = 0;
        this.cutPart = -1;
        this.UltimateLoad = 12F;
        this.Negative_G_Limit = -4F;
        this.Negative_G_Ultimate = -6F;
        this.LimitLoad = 8F;
        this.SafetyFactor = 1.5F;
        this.turnFile = new String();
        this.speedFile = new String();
        this.Operate = 0xfffffffffffL;
        
        // TODO: +++ TD AI code backport from 4.13 +++
        this.producedAMM = new Vector3d(0.0D, 0.0D, 0.0D);
        // TODO: --- TD AI code backport from 4.13 ---
        
        // TODO: Added by SAS~Storebror: +++ Additional parameter for max flaps & gear speeds +++
        this.vMaxGear = 405F;
        this.vJamFlaps = 300F;
        // TODO: Added by SAS~Storebror: --- Additional parameter for max flaps & gear speeds ---
        
        this.load(s);
        this.init_G_Limits();
    }

    public void set(Loc loc, Vector3f vector3f) {
        super.actor.pos.setAbs(loc);
        this.Vwld.set(vector3f);
        loc.get(this.Loc, this.Or);
    }

    public void set(Point3d point3d, Orient orient, Vector3f vector3f) {
        super.actor.pos.setAbs(point3d, orient);
        this.Vwld.set(vector3f);
        this.Loc.set(point3d);
        this.Or.set(orient);
    }

    public void update(float f) {
        ((Aircraft) super.actor).update(f);
    }

    public boolean tick() {
        float f = Time.tickLenFs();
        super.actor.pos.getAbs(this.Loc, this.Or);
        int i = (int) (f / 0.05F) + 1;
        float f2 = f / i;
        for (int j = 0; j < i; j++)
            this.update(f2);

        this.Gears.bFlatTopGearCheck = false;
        if (super.actor.pos.base() == null) super.actor.pos.setAbs(this.Loc, this.Or);
        else {
            if (super.actor.pos.base() instanceof Aircraft) this.Vwld.set(((FlightModelMain) ((SndAircraft) (Aircraft) super.actor.pos.base()).FM).Vwld);
            else {
                super.actor.pos.speed(actVwld);
                this.Vwld.x = (float) ((Tuple3d) actVwld).x;
                this.Vwld.y = (float) ((Tuple3d) actVwld).y;
                this.Vwld.z = (float) ((Tuple3d) actVwld).z;
            }
            super.actor.pos.getAbs(this.Or);
            this.producedAF.z += 9.81F * this.M.mass;
        }
        this.Energy = Atmosphere.g() * (float) ((Tuple3d) this.Loc).z + this.V2 * 0.5F;
        return true;
    }

    public float getOverload() {
        return (float) ((Tuple3d) this.ACmeter).z;
    }

    public float getForwAccel() {
        return (float) ((Tuple3d) this.ACmeter).x;
    }

    public float getRollAcceleration() {
        return this.Gravity==0F?0F:(float) ((Tuple3d) this.AM).x / this.Gravity;
//        return (float) ((Tuple3d) this.AM).x / this.Gravity;
    }

    public void gunPulse(Vector3d vector3d) {
        GPulse.set(vector3d);
        GPulse.scale(1.0F / this.M.mass);
        this.Vwld.sub(GPulse);
    }

    private void cutOp(int i) {
        this.Operate &= ~(1L << i);
    }

    protected boolean getOp(int i) {
        return (this.Operate & 1L << i) != 0L;
    }

    private float Op(int i) {
        return this.getOp(i) ? 1.0F : 0.0F;
    }

    public final boolean isPlayers() {
        return super.actor != null && super.actor == World.getPlayerAircraft();
    }

    public final boolean isCapableOfACM() {
        return (this.flags0 & FMFLAGS_CAPABLEACM) != 0L;
    }

    public final boolean isCapableOfBMP() {
        return (this.flags0 & FMFLAGS_CAPABLEAIRWORTHY) != 0L;
    }

    public final boolean isCapableOfTaxiing() {
        return (this.flags0 & FMFLAGS_CAPABLETAXI) != 0L;
    }

    public final boolean isReadyToDie() {
        return (this.flags0 & FMFLAGS_READYTODIE) != 0L;
    }

    public final boolean isReadyToReturn() {
        return (this.flags0 & FMFLAGS_READYTORETURN) != 0L;
    }

    public final boolean isTakenMortalDamage() {
        return (this.flags0 & FMFLAGS_TAKENMORTALDAMAGE) != 0L;
    }

    public final boolean isStationedOnGround() {
        return (this.flags0 & FMFLAGS_STATIONEDONGROUND) != 0L;
    }

    public final boolean isCrashedOnGround() {
        return (this.flags0 & FMFLAGS_CRASHEDONGROUND) != 0L;
    }

    public final boolean isNearAirdrome() {
        return (this.flags0 & FMFLAGS_NEARAIRDROME) != 0L;
    }

    public final boolean isCrossCountry() {
        return (this.flags0 & FMFLAGS_ISCROSSCOUNTRY) != 0L;
    }

    public final boolean isWasAirborne() {
        return (this.flags0 & FMFLAGS_WASAIRBORNE) != 0L;
    }

    public final boolean isSentWingNote() {
        return (this.flags0 & FMFLAGS_NETSENTWINGNOTE) != 0L;
    }

    public final boolean isSentBuryNote() {
        return (this.flags0 & FMFLAGS_NETSENTBURYNOTE) != 0L;
    }

    public final boolean isSentControlsOutNote() {
        return (this.flags0 & FMFLAGS_NETSENTCTRLSDMG) != 0L;
    }

    public boolean isOk() {
        return this.isCapableOfBMP() && !this.isReadyToDie() && !this.isTakenMortalDamage();
    }

    private void checkDamaged() {
        if (!this.bDamaged && Actor.isValid(this.damagedInitiator) && (!this.isCapableOfBMP() || this.isTakenMortalDamage())) {
            this.bDamaged = true;
            if (super.actor != this.damagedInitiator) EventLog.onDamaged(super.actor, this.damagedInitiator);
            this.damagedInitiator = null;
        }
        if (!this.bDamagedGround && this.isStationedOnGround() && (!this.isCapableOfBMP() || !this.isCapableOfTaxiing() || this.isReadyToDie() || this.isTakenMortalDamage() || this.isSentControlsOutNote())) {
            this.bDamagedGround = true;
            EventLog.onDamagedGround(super.actor);
        }
    }

    public final void setCapableOfACM(boolean flag) {
        if (this.isCapableOfACM() == flag) return;
        if (flag) this.flags0 |= FMFLAGS_CAPABLEACM;
        else this.flags0 &= -33L;
    }

    public final void setCapableOfBMP(boolean flag, Actor actor) {
        if (this.isCapableOfBMP() == flag) return;
        if (this.isCapableOfBMP() && World.Rnd().nextInt(0, 99) < 25) Voice.speakMayday((Aircraft) super.actor);
        if (flag) this.flags0 |= FMFLAGS_CAPABLEAIRWORTHY;
        else {
            this.flags0 &= -17L;
            if (!this.bDamaged) this.damagedInitiator = actor;
            this.checkDamaged();
        }
    }

    public final void setCapableOfTaxiing(boolean flag) {
        if (this.isCapableOfTaxiing() == flag) return;
        if (flag) this.flags0 |= FMFLAGS_CAPABLETAXI;
        else {
            this.flags0 &= -65L;
            this.checkDamaged();
        }
    }

    public final void setReadyToDie(boolean flag) {
        if (this.isReadyToDie() == flag) return;
        if (!this.isReadyToDie()) {
            if (World.Rnd().nextInt(0, 99) < 75) Voice.speakMayday((Aircraft) super.actor);
            Explosions.generateComicBulb(super.actor, "OnFire", 9F);
        }
        if (flag) {
            this.flags0 |= 4L;
            this.checkDamaged();
        } else this.flags0 &= -5L;
    }

    public final void setReadyToDieSoftly(boolean flag) {
        if (this.isReadyToDie() == flag) return;
        if (flag) {
            this.flags0 |= 4L;
            this.checkDamaged();
        } else this.flags0 &= -5L;
    }

    public final void setReadyToReturn(boolean flag) {
        if (this.isReadyToReturn() == flag) return;
        if (!this.isReadyToReturn()) Explosions.generateComicBulb(super.actor, "RTB", 9F);
        if (flag) this.flags0 |= 2L;
        else this.flags0 &= -3L;
        Voice.speakToReturn((Aircraft) super.actor);
    }

    public final void setReadyToReturnSoftly(boolean flag) {
        if (this.isReadyToReturn() == flag) return;
        if (flag) this.flags0 |= 2L;
        else this.flags0 &= -3L;
    }

    public final void setTakenMortalDamage(boolean flag, Actor actor) {
        if (this.isTakenMortalDamage() == flag) return;
        if (flag) {
            this.flags0 |= 8L;
            if (!this.bDamaged && !Actor.isValid(this.damagedInitiator)) this.damagedInitiator = actor;
            this.checkDamaged();
        } else this.flags0 &= -9L;
        if (flag && super.actor != World.getPlayerAircraft() && ((SndAircraft) (Aircraft) super.actor).FM.turret.length > 0) for (int i = 0; i < ((SndAircraft) (Aircraft) super.actor).FM.turret.length; i++)
            ((SndAircraft) (Aircraft) super.actor).FM.turret[i].bIsOperable = false;
    }

    public final void setStationedOnGround(boolean flag) {
        if (this.isStationedOnGround() == flag) return;
        if (flag) {
            this.flags0 |= 128L;
            EventLog.onAirLanded((Aircraft) super.actor);
            this.checkDamaged();
        } else this.flags0 &= -129L;
    }

    public final void setCrashedOnGround(boolean flag) {
        if (this.isCrashedOnGround() == flag) return;
        if (flag) {
            this.flags0 |= 256L;
            this.checkDamaged();
        } else this.flags0 &= -257L;
    }

    public final void setNearAirdrome(boolean flag) {
        if (this.isNearAirdrome() == flag) return;
        if (flag) this.flags0 |= 512L;
        else this.flags0 &= -513L;
    }

    public final void setCrossCountry(boolean flag) {
        if (this.isCrossCountry() == flag) return;
        if (flag) this.flags0 |= 1024L;
        else this.flags0 &= -1025L;
    }

    public final void setWasAirborne(boolean flag) {
        if (this.isWasAirborne() == flag) return;
        if (flag) this.flags0 |= 2048L;
        else this.flags0 &= -2049L;
    }

    public final void setSentWingNote(boolean flag) {
        if (this.isSentWingNote() == flag) return;
        if (flag) this.flags0 |= 4096L;
        else this.flags0 &= -4097L;
    }

    public final void setSentBuryNote(boolean flag) {
        if (this.isSentBuryNote() == flag) return;
        if (flag) this.flags0 |= 16384L;
        else this.flags0 &= -16385L;
    }

    public final void setSentControlsOutNote(boolean flag) {
        if (this.isSentControlsOutNote() == flag) return;
        if (flag) {
            this.flags0 |= 32768L;
            this.checkDamaged();
        } else this.flags0 &= -32769L;
    }

    public void hit(int i) {
        Aircraft.debugprintln(super.actor, "Detected NDL in " + Aircraft.partNames()[i] + "..");
        if (i < 0 || i >= 44) return;
        if (this instanceof RealFlightModel) this.bReal = true;
        else this.bReal = false;
        switch (i) {
            case 2:
            case 7:
            case 8:
            case 9:
            case 10:
            case 14:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            default:
                break;

            case 13:
                this.Sq.dragFuselageCx = this.Sq.dragFuselageCx > 0.08F ? this.Sq.dragFuselageCx * 2.0F : 0.08F;
                break;

            case 0:
                this.Sq.liftWingLOut *= 0.95F;
                this.SensRoll *= 0.68F;
                break;

            case 1:
                this.Sq.liftWingROut *= 0.95F;
                this.SensRoll *= 0.68F;
                break;

            case 17:
                this.Sq.liftStab *= 0.68F;
                this.Sq.getClass();
                this.Sq.dragProducedCx += 0.12F;
                break;

            case 18:
                this.Sq.liftStab *= 0.68F;
                this.Sq.getClass();
                this.Sq.dragProducedCx += 0.12F;
                break;

            case 31:
                this.Sq.squareElevators *= 0.68F;
                this.SensPitch *= 0.68F;
                break;

            case 32:
                this.Sq.squareElevators *= 0.68F;
                this.SensPitch *= 0.68F;
                break;

            case 11:
                this.Sq.liftKeel *= 0.68F;
                this.Sq.getClass();
                this.Sq.dragProducedCx += 0.12F;
                break;

            case 12:
                this.Sq.liftKeel *= 0.68F;
                this.Sq.getClass();
                this.Sq.dragProducedCx += 0.12F;
                break;

            case 15:
                this.Sq.squareRudders *= 0.5F;
                this.SensYaw *= 0.68F;
                break;

            case 16:
                this.Sq.squareRudders *= 0.5F;
                this.SensYaw *= 0.68F;
                break;

            case 33:
                if (this.bReal) this.setDmgLoadLimit(0.6F, 2);
                this.Sq.getClass();
                this.Sq.liftWingLIn -= 0.8F;
                this.Sq.getClass();
                this.Sq.dragProducedCx += 0.12F;
                if (World.Rnd().nextFloat() < 0.09F) this.setReadyToReturn(true);
                if (World.Rnd().nextFloat() < 0.09F) this.setCapableOfACM(false);
                break;

            case 36:
                if (this.bReal) this.setDmgLoadLimit(0.6F, 3);
                this.Sq.getClass();
                this.Sq.liftWingRIn -= 0.8F;
                this.Sq.getClass();
                this.Sq.dragProducedCx += 0.12F;
                if (World.Rnd().nextFloat() < 0.09F) this.setReadyToReturn(true);
                if (World.Rnd().nextFloat() < 0.09F) this.setCapableOfACM(false);
                break;

            case 34:
                if (this.bReal) this.setDmgLoadLimit(0.7F, 1);
                this.Sq.getClass();
                this.Sq.liftWingLMid -= 0.8F;
                this.Sq.getClass();
                this.Sq.dragProducedCx += 0.12F;
                if (World.Rnd().nextFloat() < 0.09F) this.setReadyToReturn(true);
                if (World.Rnd().nextFloat() < 0.09F) this.setCapableOfACM(false);
                break;

            case 37:
                if (this.bReal) this.setDmgLoadLimit(0.7F, 4);
                this.Sq.getClass();
                this.Sq.liftWingRMid -= 0.8F;
                this.Sq.getClass();
                this.Sq.dragProducedCx += 0.12F;
                if (World.Rnd().nextFloat() < 0.09F) this.setReadyToReturn(true);
                if (World.Rnd().nextFloat() < 0.09F) this.setCapableOfACM(false);
                break;

            case 35:
                if (this.bReal) this.setDmgLoadLimit(0.8F, 0);
                this.Sq.getClass();
                this.Sq.liftWingLOut -= 0.8F;
                this.Sq.getClass();
                this.Sq.dragProducedCx += 0.12F;
                if (World.Rnd().nextFloat() < 0.12F) this.setReadyToReturn(true);
                if (World.Rnd().nextFloat() < 0.12F) this.setCapableOfACM(false);
                break;

            case 38:
                if (this.bReal) this.setDmgLoadLimit(0.8F, 5);
                this.Sq.getClass();
                this.Sq.liftWingROut -= 0.8F;
                this.Sq.getClass();
                this.Sq.dragProducedCx += 0.12F;
                if (World.Rnd().nextFloat() < 0.12F) this.setReadyToReturn(true);
                if (World.Rnd().nextFloat() < 0.12F) this.setCapableOfACM(false);
                break;

            case 3:
                if (this.Sq.dragEngineCx[0] < 0.15F) this.Sq.dragEngineCx[0] += 0.05D;
                if (World.Rnd().nextFloat() < 0.12F) this.setReadyToReturn(true);
                break;

            case 4:
                if (this.Sq.dragEngineCx[1] < 0.15F) this.Sq.dragEngineCx[1] += 0.05F;
                if (World.Rnd().nextFloat() < 0.12F) this.setReadyToReturn(true);
                break;

            case 5:
                if (this.Sq.dragEngineCx[2] < 0.15F) this.Sq.dragEngineCx[2] += 0.05F;
                if (World.Rnd().nextFloat() < 0.12F) this.setReadyToReturn(true);
                break;

            case 6:
                if (this.Sq.dragEngineCx[3] < 0.15F) this.Sq.dragEngineCx[3] += 0.05F;
                if (World.Rnd().nextFloat() < 0.12F) this.setReadyToReturn(true);
                break;

            case 19:
            case 20:
                if (this.bReal) this.setDmgLoadLimit(0.5F, 6);
                break;
        }
    }

    public void cut(int i, int j, Actor actor) {
        if (i < 0 || i >= 44) return;
        Aircraft.debugprintln(super.actor, "cutting part #" + i + " (" + Aircraft.partNames()[i] + ")");
        if (!this.getOp(i)) {
            Aircraft.debugprintln(super.actor, "part #" + i + " (" + Aircraft.partNames()[i] + ") already cut off");
            return;
        }
        this.cutOp(i);
        switch (i) {
            case 8:
            case 14:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            default:
                break;

            case 13:
                this.setCapableOfBMP(false, actor);
                this.setCapableOfACM(false);
                this.Sq.dragFuselageCx = this.Sq.dragFuselageCx > 0.24F ? this.Sq.dragFuselageCx * 2.0F : 0.24F;
                break;

            case 9:
                this.setCapableOfTaxiing(false);
                this.Gears.hitLeftGear();
                break;

            case 10:
                this.setCapableOfTaxiing(false);
                this.Gears.hitRightGear();
                break;

            case 7:
                this.setCapableOfTaxiing(false);
                this.Gears.hitCentreGear();
                break;

            case 2:
                this.setCapableOfACM(false);
                this.setCapableOfBMP(false, actor);
                this.setCapableOfTaxiing(false);
                this.setTakenMortalDamage(true, actor);
                for (int k = 0; k < this.EI.getNum(); k++)
                    this.EI.engines[k].setReadyness(super.actor, 0.0F);

                this.cutOp(19);
                this.cutOp(20);
                // fall through

            case 19:
            case 20:
                this.setCapableOfACM(false);
                this.setCapableOfBMP(false, actor);
                this.setReadyToDie(true);
                this.setTakenMortalDamage(true, actor);
                this.Arms.GCENTER = 0.0F;
                this.W.y += World.Rnd().nextFloat(-0.1F, 0.1F);
                this.W.z += World.Rnd().nextFloat(-0.1F, 0.1F);
                this.J0.y *= 0.5D;
                this.cut(17, j, actor);
                this.cut(18, j, actor);
                this.cut(11, j, actor);
                this.cut(12, j, actor);
                break;

            case 17:
                if (World.Rnd().nextInt(-1, 8) < this.Skill) this.setReadyToReturn(true);
                if (World.Rnd().nextInt(-1, 16) < this.Skill) this.setReadyToDie(true);
                this.Sq.liftStab *= 0.5F * this.Op(18) + 0.1F;
                if (this.isPlayers()) {
                    this.Sq.getClass();
                    this.Sq.dragProducedCx += 0.12F;
                } else {
                    this.Sq.getClass();
                    this.Sq.dragProducedCx += 0.06F;
                }
                this.Sq.liftWingRMid *= 0.9F;
                this.Sq.liftWingLMid *= 1.1F;
                this.Sq.liftWingROut *= 0.9F;
                this.Sq.liftWingLOut *= 1.1F;
                if (this.Op(18) == 0.0F) {
                    this.CT.setTrimAileronControl(this.CT.getTrimAileronControl() - 0.25F);
                    if (World.Rnd().nextBoolean()) {
                        this.Sq.liftWingLOut *= 0.95F;
                        this.Sq.liftWingLMid *= 0.95F;
                        this.Sq.liftWingLIn *= 0.95F;
                        this.Sq.liftWingRIn *= 0.75F;
                        this.Sq.liftWingRMid *= 0.75F;
                        this.Sq.liftWingROut *= 0.75F;
                    } else {
                        this.Sq.liftWingROut *= 0.75F;
                        this.Sq.liftWingRMid *= 0.75F;
                        this.Sq.liftWingRIn *= 0.75F;
                        this.Sq.liftWingLIn *= 0.95F;
                        this.Sq.liftWingLMid *= 0.95F;
                        this.Sq.liftWingLOut *= 0.95F;
                    }
                }
                this.cutOp(31);
                // fall through

            case 31:
                this.setCapableOfACM(false);
                if (this.Op(32) == 0.0F) this.setReadyToDie(true);
                this.Sq.squareElevators *= 0.5F * this.Op(32);
                this.SensPitch *= 0.5F * this.Op(32);
                break;

            case 18:
                if (World.Rnd().nextInt(-1, 8) < this.Skill) this.setReadyToReturn(true);
                if (World.Rnd().nextInt(-1, 16) < this.Skill) this.setReadyToDie(true);
                this.Sq.liftStab *= 0.5F * this.Op(17) + 0.1F;
                if (this.isPlayers()) {
                    this.Sq.getClass();
                    this.Sq.dragProducedCx += 0.12F;
                } else {
                    this.Sq.getClass();
                    this.Sq.dragProducedCx += 0.06F;
                }
                this.Sq.liftWingLMid *= 0.9F;
                this.Sq.liftWingRMid *= 1.1F;
                this.Sq.liftWingLOut *= 0.9F;
                this.Sq.liftWingROut *= 1.1F;
                if (this.Op(17) == 0.0F) {
                    this.CT.setTrimAileronControl(this.CT.getTrimAileronControl() + 0.25F);
                    if (World.Rnd().nextBoolean()) {
                        this.Sq.liftWingLOut *= 0.95F;
                        this.Sq.liftWingLMid *= 0.95F;
                        this.Sq.liftWingLIn *= 0.95F;
                        this.Sq.liftWingRIn *= 0.75F;
                        this.Sq.liftWingRMid *= 0.75F;
                        this.Sq.liftWingROut *= 0.75F;
                    } else {
                        this.Sq.liftWingROut *= 0.75F;
                        this.Sq.liftWingRMid *= 0.75F;
                        this.Sq.liftWingRIn *= 0.75F;
                        this.Sq.liftWingLIn *= 0.95F;
                        this.Sq.liftWingLMid *= 0.95F;
                        this.Sq.liftWingLOut *= 0.95F;
                    }
                }
                this.cutOp(32);
                // fall through

            case 32:
                this.setCapableOfACM(false);
                if (this.Op(31) == 0.0F) this.setReadyToDie(true);
                this.Sq.squareElevators *= 0.5F * this.Op(31);
                this.SensPitch *= 0.5F * this.Op(31);
                break;

            case 11:
                if (World.Rnd().nextInt(-1, 8) < this.Skill) this.setReadyToReturn(true);
                if (World.Rnd().nextInt(-1, 16) < this.Skill) this.setReadyToDie(true);
                if (super.actor instanceof Scheme2a || super.actor instanceof Scheme5 || super.actor instanceof Scheme7) this.Sq.liftKeel *= 0.25F * this.Op(12);
                else this.Sq.liftKeel *= 0.0F;
                this.cutOp(15);
                // fall through

            case 15:
                this.setCapableOfACM(false);
                if (World.Rnd().nextInt(-1, 8) < this.Skill) this.setReadyToReturn(true);
                if (super.actor instanceof Scheme2a || super.actor instanceof Scheme5 || super.actor instanceof Scheme7) {
                    this.Sq.squareRudders *= 0.5F;
                    this.SensYaw *= 0.25F;
                } else {
                    this.Sq.squareRudders *= 0.0F;
                    this.SensYaw *= 0.0F;
                }
                break;

            case 12:
                if (World.Rnd().nextInt(-1, 8) < this.Skill) this.setReadyToReturn(true);
                if (World.Rnd().nextInt(-1, 16) < this.Skill) this.setReadyToDie(true);
                if (super.actor instanceof Scheme2a || super.actor instanceof Scheme5 || super.actor instanceof Scheme7) this.Sq.liftKeel *= 0.25F * this.Op(12);
                else this.Sq.liftKeel *= 0.0F;
                this.cutOp(16);
                // fall through

            case 16:
                this.setCapableOfACM(false);
                if (World.Rnd().nextInt(-1, 8) < this.Skill) this.setReadyToReturn(true);
                if (super.actor instanceof Scheme2a || super.actor instanceof Scheme5 || super.actor instanceof Scheme7) {
                    this.Sq.squareRudders *= 0.5F;
                    this.SensYaw *= 0.25F;
                } else {
                    this.Sq.squareRudders *= 0.0F;
                    this.SensYaw *= 0.0F;
                }
                break;

            case 33:
                this.Sq.liftWingLIn *= 0.25F;
                ((ActorHMesh) super.actor).destroyChildFiltered(com.maddox.il2.objects.weapons.BombGun.class);
                ((ActorHMesh) super.actor).destroyChildFiltered(com.maddox.il2.objects.weapons.RocketBombGun.class);
                this.cut(9, j, actor);
                this.cutOp(34);
                // fall through

            case 34:
                this.setTakenMortalDamage(true, actor);
                this.setReadyToDie(true);
                this.Sq.liftWingLMid *= 0.0F;
                this.Sq.liftWingLIn *= 0.9F;
                ((ActorHMesh) super.actor).destroyChildFiltered(com.maddox.il2.objects.weapons.RocketGun.class);
                this.cutOp(35);
                // fall through

            case 35:
                this.setCapableOfBMP(false, actor);
                this.setCapableOfACM(false);
                this.AS.bWingTipLExists = false;
                this.AS.setStallState(false);
                this.AS.setAirShowState(false);
                this.Sq.liftWingLOut *= 0.0F;
                this.Sq.liftWingLMid *= 0.5F;
                this.Sq.liftWingLOut = 0.0F;
                this.Sq.liftWingLMid = 0.0F;
                this.Sq.liftWingLIn = 0.0F;
                this.CT.bHasAileronControl = false;
                this.cutOp(0);
                // fall through

            case 0:
                if (this.Op(1) == 0.0F) this.setCapableOfACM(false);
                this.Sq.squareAilerons *= 0.5F;
                this.SensRoll *= 0.5F * this.Op(1);
                break;

            case 36:
                this.Sq.liftWingRIn *= 0.25F;
                ((ActorHMesh) super.actor).destroyChildFiltered(com.maddox.il2.objects.weapons.BombGun.class);
                ((ActorHMesh) super.actor).destroyChildFiltered(com.maddox.il2.objects.weapons.RocketBombGun.class);
                this.cut(10, j, actor);
                this.cutOp(37);
                // fall through

            case 37:
                this.setTakenMortalDamage(true, actor);
                this.setReadyToDie(true);
                this.Sq.liftWingRMid *= 0.0F;
                this.Sq.liftWingRIn *= 0.9F;
                ((ActorHMesh) super.actor).destroyChildFiltered(com.maddox.il2.objects.weapons.RocketGun.class);
                this.cutOp(38);
                // fall through

            case 38:
                this.setCapableOfBMP(false, actor);
                this.setCapableOfACM(false);
                this.AS.bWingTipRExists = false;
                this.AS.setStallState(false);
                this.AS.setAirShowState(false);
                this.Sq.liftWingROut *= 0.0F;
                this.Sq.liftWingRMid *= 0.5F;
                this.Sq.liftWingROut = 0.0F;
                this.Sq.liftWingRMid = 0.0F;
                this.Sq.liftWingRIn = 0.0F;
                this.CT.bHasAileronControl = false;
                this.cutOp(1);
                // fall through

            case 1:
                if (this.Op(0) == 0.0F) this.setCapableOfACM(false);
                this.Sq.squareAilerons *= 0.5F;
                this.SensRoll *= 0.5F * this.Op(0);
                break;

            case 3:
                if (this.EI.engines.length <= 0) break;
                this.setCapableOfTaxiing(false);
                if (!(super.actor instanceof Scheme1)) this.setReadyToDie(true);
                this.EI.engines[0].setEngineDies(super.actor);
                this.Sq.dragEngineCx[0] = 0.15F;
                break;

            case 4:
                if (this.EI.engines.length > 1) {
                    this.setCapableOfTaxiing(false);
                    this.setReadyToDie(true);
                    this.EI.engines[1].setEngineDies(super.actor);
                    this.Sq.dragEngineCx[1] = 0.15F;
                }
                break;

            case 5:
                if (this.EI.engines.length > 2) {
                    this.setCapableOfTaxiing(false);
                    this.setReadyToDie(true);
                    this.EI.engines[2].setEngineDies(super.actor);
                    this.Sq.dragEngineCx[2] = 0.15F;
                }
                break;

            case 6:
                if (this.EI.engines.length > 3) {
                    this.setCapableOfTaxiing(false);
                    this.setReadyToDie(true);
                    this.EI.engines[3].setEngineDies(super.actor);
                    this.Sq.dragEngineCx[3] = 0.15F;
                }
                break;
        }
    }

    private void init_G_Limits() {
        this.UltimateLoad = this.LimitLoad * 1.5F;
        this.ReferenceForce = this.LimitLoad * (this.M.referenceWeight + this.refM);
        this.Negative_G_Limit = -0.5F * this.LimitLoad;
        this.Negative_G_Ultimate = this.Negative_G_Limit * 1.5F;
    }

    public void setDmgLoadLimit(float f, int i) {
        this.setLimitLoad(this.LimitLoad -= f);
        this.damagedParts[i]++;
        for (i = 0; i < this.damagedParts.length; i++)
            if (this.damagedParts[i] > this.maxDamage) this.cutPart = i;

    }

    public void setUltimateLoad(float f) {
        this.UltimateLoad = f * this.SafetyFactor;
        this.Negative_G_Ultimate = this.UltimateLoad * -0.5F;
    }

    public float getUltimateLoad() {
        return this.UltimateLoad;
    }

    public void setLimitLoad(float f) {
        this.LimitLoad = f;
        this.Negative_G_Limit = -f * 0.5F;
        this.setUltimateLoad(f);
    }

    public float getLimitLoad() {
        return this.LimitLoad;
    }

    public void setSafetyFactor(float f) {
        this.SafetyFactor -= f;
    }

    public float getSafetyFactor() {
        return this.SafetyFactor;
    }

    public float getLoadDiff() {
        return this.getLimitLoad() - this.getOverload();
    }

    public void doRequestFMSFX(int i, int j) {
        if (this.fmsfxCurrentType == 1 && i != 1) return;
        switch (i) {
            default:
                break;

            case 0:
                this.fmsfxCurrentType = i;
                break;

            case 1:
                this.fmsfxCurrentType = i;
                this.fmsfxPrevValue = j * 0.01F;
                if (this.fmsfxPrevValue < 0.05F) this.fmsfxCurrentType = 0;
                break;

            case 2:
            case 3:
                this.fmsfxCurrentType = i;
                this.fmsfxTimeDisable = Time.current() + 100 * j;
                break;
        }
    }

    public void setGCenter(float f) {
        this.Arms.GCENTER = f;
    }

    public void setGC_Gear_Shift(float f) {
        this.Arms.GC_GEAR_SHIFT = f;
    }

    public void setFlapsShift(float f) {
        this.Wing.setFlaps(f);
    }

    public static long finger(long l, String s) {
        SectFile sectfile = sectFile(s);
        l = sectfile.finger(l);
        int i = 0;
        do {
            if (i >= 10) break;
            String s1 = "Engine" + i + "Family";
            String s2 = sectfile.get("Engine", s1);
            if (s2 == null) break;
            SectFile sectfile1 = sectFile("FlightModels/" + s2 + ".emd");
            l = sectfile1.finger(l);
            i++;
        } while (true);
        return l;
    }

    private static boolean existSfsFile(String s) {
        try {
            SFSInputStream sfsinputstream = new SFSInputStream(s);
            sfsinputstream.close();
        } catch (Exception exception) {
            return false;
        }
        return true;
    }
    
    private synchronized static String fmToPlainTextFileName(String s) {
        s = s.toLowerCase();
        if (s.lastIndexOf('/') > -1) s = s.substring(s.lastIndexOf('/'));
        if (s.lastIndexOf('\\') > -1) s = s.substring(s.lastIndexOf('\\'));
        String s1 = s;
        int colonPos = s.indexOf(':');
        if (colonPos > -1) {
            String s2 = s.substring(colonPos + 1, s.length());
            s1 = s.substring(0, colonPos);
            String s3 = "";
            int dotPos = s1.lastIndexOf('.');
            if (dotPos > -1) {
                s3 = s1.substring(0, dotPos) + "_" + s2 + s1.substring(dotPos);
            } else {
                s3 = s1 + "_" + s2;
            }
            String s4 = HomePath.toFileSystemName("FlightModels/fmdata" + s3, 0);
            File fmFile = new File(s4);
            if (fmFile.exists()) return s4;
        }
        String s5 = HomePath.toFileSystemName("FlightModels/fmdata" + s1, 0);
        
        File fmFile = new File(s5);
        if (fmFile.exists()) return s5;
        return null;
    }
    
    private synchronized static SectFile plainTextSectFile(String s) {
        String fileName = fmToPlainTextFileName(s);
//        if (fileName!=null) System.out.println("Clear Text Filename for " + s + " is " + fileName);
        if (fileName == null) return null;
        SectFile sectfile = new SectFile(fileName);
//        System.out.println("SectFile is " + (sectfile==null?"":"not ") + "null");
        if (sectfile != null) System.out.println("Loading Flight Model " + s + " from Clear Text File " + fileName);
        return sectfile;
    }

    public synchronized static SectFile sectFile(String s) {
        SectFile sectfile = Config.isPlainTextFlightModelAllowed()?plainTextSectFile(s):null;
        if (sectfile != null) {
//            Exception e = new Exception("TEST");
//            e.printStackTrace();
            return sectfile;
        }
//        SectFile sectfile = null;
        String s1 = s.toLowerCase();
        String s2 = "gui/game/buttons";
        int i = s1.indexOf(":");
        if (i > -1) {
            s2 = s1.substring(i + 1, s1.length());
            s1 = s1.substring(0, i);
            if (s.endsWith(".emd")) {
                s2 = s2.substring(0, s2.length() - 4);
                s1 = s1 + ".emd";
            }
            System.out.println("FM called '" + s + "' is being loaded from File: '" + s2 + "'");
        }
        if (prButtons != null && prButtons.length() > 0) {
            String s3 = prButtons + s2;
            int ra = s2.lastIndexOf('/');
            if (ra > -1) s3 = s2.substring(0, ra + 1) + prButtons + s2.substring(ra + 1, s2.length());
            if (existSfsFile(s3)) {
                s2 = s3;
                System.out.println("FM called '" + s + "' is being loaded from Alternative File: '" + s2 + "'");
            }
        }
        try {
            Object obj = Property.value(s, "stream", null);
            InputStream inputstream;
            if (obj != null) inputstream = (InputStream) obj;
            else {
                if (fmDir == null) {
                    fmDir = new InOutStreams();
                    fmDir.open(Finger.LongFN(0L, s2));
                    fmDirs.add(fmDir);
                    fmDirNames.add(s2);
                } else if (!s2.equalsIgnoreCase(lastFMFile)) {
                    fmDir = null;
                    int j = 0;
                    do {
                        if (j >= fmDirNames.size()) break;
                        String s3 = (String) fmDirNames.get(j);
                        if (s2.equalsIgnoreCase(s3)) {
                            fmDir = (InOutStreams) fmDirs.get(j);
                            break;
                        }
                        j++;
                    } while (true);
                    if (null == fmDir) {
                        fmDir = new InOutStreams();
                        fmDir.open(Finger.LongFN(0L, s2));
                        fmDirs.add(fmDir);
                        fmDirNames.add(s2);
                    }
                }
                lastFMFile = s2;
                inputstream = fmDir.openStream("" + Finger.Int(s1 + "d2w0"));
                if (inputstream == null) inputstream = fmDir.openStream("" + Finger.Int(s1 + "d2wO"));
                if (inputstream == null) inputstream = fmDir.openStream("" + Finger.Int(s1 + "d2w5"));
                if (inputstream == null) {
                    System.out.println("Error loading FM called '" + s + "'!");
                    System.out.println("This FM is not present in '" + s2 + "' File");
                    if (s.endsWith(".emd")) System.out.println("*** This should have been the reason of the 'Explosion in the Air' when mission started ***");
                    else System.out.println("*** This was the Reason of your 60% or 70% CTD ***");
                }
            }
            // TODO: Fixed by SAS~Storebror to avoid possible null dereference
            if (inputstream == null) throw new RuntimeException("Error loading FM called '" + s + "'!");

            inputstream.mark(0);
            sectfile = new SectFile(new InputStreamReader(new KryptoInputFilter(inputstream, getSwTbl(Finger.Int(s1 + "ogh9"), inputstream.available())), "Cp1252"));
            inputstream.reset();
            if (obj == null) Property.set(s, "stream", inputstream);
        } catch (RuntimeException exception) {
            throw exception;
        } catch (Exception exception) {}
        return sectfile;
    }

    public synchronized static SectFile sectFileOld(String s) {
        SectFile sectfile = null;
        String s1 = s.toLowerCase();
        try {
            Object obj = Property.value(s, "stream", null);
            InputStream inputstream;
            if (obj != null) inputstream = (InputStream) obj;
            else {
                if (fmDir == null) {
                    fmDir = new InOutStreams();
                    fmDir.open(Finger.LongFN(0L, "gui/game/buttons"));
                }
                inputstream = fmDir.openStream("" + Finger.Int(s1 + "d2wO"));
            }
            inputstream.mark(0);
            sectfile = new SectFile(new InputStreamReader(new KryptoInputFilter(inputstream, getSwTbl(Finger.Int(s1 + "ogh9"), inputstream.available())), "Cp1252"));
            inputstream.reset();
            if (obj == null) Property.set(s, "stream", inputstream);
        } catch (Exception exception) {}
        return sectfile;
    }

    private static int[] getSwTbl(int i, int j) {
        if (i < 0) i = -i;
        if (j < 0) j = -j;
        int k = (j + i / 5) % 16 + 14;
        int l = (j + i / 19) % Finger.kTable.length;
        if (k < 0) k = -k % 16;
        if (k < 10) k = 10;
        if (l < 0) l = -l % Finger.kTable.length;
        int ai[] = new int[k];
        for (int i1 = 0; i1 < k; i1++)
            ai[i1] = Finger.kTable[(l + i1) % Finger.kTable.length];

        return ai;
    }
    
    // TODO: Added by SAS~Storebror: +++ Additional parameter for max flaps & gear speeds +++
    public float getvMaxGear() {
        return vMaxGear;
    }

    public void setvMaxGear(float vMaxGear) {
        this.vMaxGear = vMaxGear;
    }

    public float getvJamFlaps() {
        return vJamFlaps;
    }

    public void setvJamFlaps(float vJamFlaps) {
        this.vJamFlaps = vJamFlaps;
    }
    // TODO: Added by SAS~Storebror: --- Additional parameter for max flaps & gear speeds ---

    public static final int     __DEBUG__IL2C_DUMP_LEVEL__ = 0;
    public static final int     ROOKIE                     = 0;
    public static final int     NORMAL                     = 1;
    public static final int     VETERAN                    = 2;
    public static final int     ACE                        = 3;
    private static final long   FMFLAGS_READYTORETURN      = 2L;
    private static final long   FMFLAGS_READYTODIE         = 4L;
    private static final long   FMFLAGS_TAKENMORTALDAMAGE  = 8L;
    private static final long   FMFLAGS_CAPABLEAIRWORTHY   = 16L;
    private static final long   FMFLAGS_CAPABLEACM         = 32L;
    private static final long   FMFLAGS_CAPABLETAXI        = 64L;
    private static final long   FMFLAGS_STATIONEDONGROUND  = 128L;
    private static final long   FMFLAGS_CRASHEDONGROUND    = 256L;
    private static final long   FMFLAGS_NEARAIRDROME       = 512L;
    private static final long   FMFLAGS_ISCROSSCOUNTRY     = 1024L;
    private static final long   FMFLAGS_WASAIRBORNE        = 2048L;
    private static final long   FMFLAGS_NETSENTWINGNOTE    = 4096L;
    private static final long   FMFLAGS_NETSENTBURYNOTE    = 16384L;
    private static final long   FMFLAGS_NETSENTCTRLSDMG    = 32768L;
    public static final long    FMFLAGS_NETSENT4           = 0x10000L;
    public static final long    FMFLAGS_NETSENT5           = 0x20000L;
    public static final long    FMFLAGS_NETSENT6           = 0x40000L;
    public static final long    FMFLAGS_NETSENT7           = 0x80000L;
    public static final int     FMSFX_NOOP                 = 0;
    public static final int     FMSFX_DROP_WINGFOLDED      = 1;
    public static final int     FMSFX_DROP_LEFTWING        = 2;
    public static final int     FMSFX_DROP_RIGHTWING       = 3;
    public static float         outEngineAOA               = 0.0F;
    private long                flags0;
    private boolean             bDamagedGround;
    private boolean             bDamaged;
    private Actor               damagedInitiator;
    public int                  Skill;
    public int                  crew;
    public int                  turretSkill;
    public Autopilotage         AP;
    public Controls             CT;
    public float                SensYaw;
    public float                SensPitch;
    public float                SensRoll;
    public float                GearCX;
    public float                radiatorCX;
    public Point3d              Loc;
    public Orientation          Or;
    public FlightModel          Leader;
    public FlightModel          Wingman;
    public Vector3d             Offset;
    public byte                 formationType;
    public float                formationScale;
    public float                minElevCoeff;
    protected float             AOA;
    protected float             AOS;
    protected float             V;
    protected float             V2;
    protected float             q_;
    protected float             Gravity;
    protected float             Mach;
    public float                Energy;
    public float                BarometerZ;
    public float                WingDiff;
    public float                WingLoss;
    public float                turbCoeff;
    public float                FuelConsumption;
    public Vector3d             producedAM;
    public Vector3d             producedAF;
    protected int               fmsfxCurrentType;
    protected float             fmsfxPrevValue;
    protected long              fmsfxTimeDisable;
    protected Vector3d          AF;
    protected Vector3d          AM;
    protected Vector3d          GF;
    protected Vector3d          GM;
    protected Vector3d          SummF;
    protected Vector3d          SummM;
    private static Vector3d     TmpA                       = new Vector3d();
    private static Vector3d     TmpV                       = new Vector3d();
    protected Vector3d          ACmeter;
    protected Vector3d          Accel;
    protected Vector3d          LocalAccel;
    protected Vector3d          BallAccel;
    public Vector3d             Vwld;
    public Vector3d             Vrel;
    protected Vector3d          Vair;
    protected Vector3d          Vflow;
    protected Vector3d          Vwind;
    protected Vector3d          J0;
    protected Vector3d          J;
    protected Vector3d          W;
    protected Vector3d          AW;
    public EnginesInterface     EI;
    public Mass                 M;
    public AircraftState        AS;
    public Squares              Sq;
    public Supersonic           Ss;
    protected Arm               Arms;
    public Gear                 Gears;
    public boolean              AIRBRAKE;
    protected Polares           Wing;
    protected Polares           Tail;
    protected Polares           Fusel;
    public int                  Scheme;
    public float                Wingspan;
    public float                Length;
    public float                Vmax;
    public float                VmaxH;
    public float                Vmin;
    public float                HofVmax;
    
    // TODO: Added by SAS~Storebror: +++ Additional parameter for max flaps & gear speeds +++
    private float               vMaxGear;
    private float               vJamFlaps;
    // TODO: Added by SAS~Storebror: --- Additional parameter for max flaps & gear speeds ---
    
    public float                VmaxFLAPS;
    public float                VminFLAPS;
    public float                VmaxAllowed;
    public float                indSpeed;
    public float                AOA_Crit;
    public float                Range;
    public float                CruiseSpeed;
    public int                  damagedParts[];
    public int                  maxDamage;
    public int                  cutPart;
    private boolean             bReal;
    public float                UltimateLoad;
    public float                Negative_G_Limit;
    public float                Negative_G_Ultimate;
    public float                LimitLoad;
    public float                G_ClassCoeff;
    public float                refM;
    public float                SafetyFactor;
    public float                ReferenceForce;
    String                      turnFile;
    String                      speedFile;
    private static Vector3d     actVwld                    = new Vector3d();
    public long                 Operate;
    private static Vector3d     GPulse                     = new Vector3d();
    public static boolean       bCY_CRIT04                 = true;
    private static InOutStreams fmDir;

    private static String       lastFMFile;
    private static ArrayList    fmDirs                     = new ArrayList();
    private static ArrayList    fmDirNames                 = new ArrayList();
    private static String       prButtons;
    // TODO: +++ TD AI code backport from 4.13 +++
    public Vector3d             producedAMM;
    // TODO: --- TD AI code backport from 4.13 ---

    public int MaxNumFuelTank;
    public int MaxNumRudder;
    public int ShowFuelLevel;
    
    // TODO: By SAS~Storebror: New Formation Parameters +++
    private Vector3d            formationOffset[]             = new Vector3d[4];
    private boolean             formationOffsetValid;
    private byte                formationDefault;
    private float               formationScaleCoeff;

    public Vector3d[] getLeaderOffset() {
        return formationOffset;
    }

    public Vector3d getLeaderOffset(int index) {
        return formationOffset[index];
    }

    public boolean isLeaderOffsetValid() {
        return formationOffsetValid;
    }

    public byte getFormationDefault() {
        return formationDefault;
    }

    public float getFormationScaleCoeff() {
        return formationScaleCoeff;
    }
    // TODO: By SAS~Storebror: New Formation Parameters ---

    // TODO: By SAS~Storebror: Array Interpolation for Mass Factors etc. +++
    private static class InterpolateComparator implements Comparator {

        public int compare(Object arg0, Object arg1) {
           float[] f0 = (float[])arg0;
           float[] f1 = (float[])arg1;
           if (f0[0] < f1[0]) return -1;
           else if (f0[0] > f1[0]) return -1;
           else return 0;
        }
        
    }

    public static void sort(float[] x, float[] y) {
        float[][] values = new float[x.length][2];
        for (int i=0; i<x.length; i++) {
            values[i][0] = x[i];
            values[i][1] = y[i];
        }
        Arrays.sort(values, new InterpolateComparator());
        for (int i=0; i<x.length; i++) {
            x[i] = values[i][0];
            y[i] = values[i][1];
        }
    }
    
    public static float interpolate(float[] x, float[] y, float xi) {
        if (x.length != y.length) return 0F;
        if (xi <= x[0]) return y[0];
        if (xi >= x[x.length-1]) return y[x.length-1];
        int searchIndex = Arrays.binarySearch(x, xi);
        if (searchIndex >= 0) return y[searchIndex];
        searchIndex = -searchIndex - 1;
        return y[searchIndex - 1] + (xi - x[searchIndex - 1]) * (y[searchIndex] - y[searchIndex - 1]) / (x[searchIndex] - x[searchIndex - 1]);
    }
    // TODO: By SAS~Storebror: Array Interpolation for Mass Factors etc. ---

}
