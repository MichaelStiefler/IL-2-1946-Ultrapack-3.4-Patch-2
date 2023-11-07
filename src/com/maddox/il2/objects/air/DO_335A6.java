package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.EventLog;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.objects.Wreckage;
import com.maddox.il2.objects.electronics.RadarLiSN2;
import com.maddox.rts.MsgAction;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class DO_335A6 extends DO_335 implements TypeX4Carrier, TypeRadarLiSN2Carrier {

    public DO_335A6() {
        this.lTimeNextEject = 0L;
        this.overrideBailout = false;
        this.ejectComplete = false;
        this.lTimeNextEject = 0L;
        this.bKeelUp = true;
    }

    public void onAircraftLoaded() {
        this.FM.crew = 2;
        this.FM.AS.astatePilotFunctions[1] = 2;
    }

    public void update(float f) {
        if ((this.FM.AS.bIsAboutToBailout || this.overrideBailout) && !this.ejectComplete && (this.FM.getSpeedKMH() > 15F) && !this.FM.isStationedOnGround()) {
            this.overrideBailout = true;
            this.FM.AS.bIsAboutToBailout = false;
            if (Time.current() > this.lTimeNextEject) {
                this.bailout();
            }
        }
        float f1 = this.FM.EI.engines[1].getControlRadiator();
        if (Math.abs(this.flapps[1] - f1) > 0.01F) {
            this.flapps[1] = f1;
            this.hierMesh().chunkSetAngles("Water1_D0", 0.0F, -20F * f1, 0.0F);
            this.hierMesh().chunkSetAngles("Water2_D0", 0.0F, -10F * f1, 0.0F);
            this.hierMesh().chunkSetAngles("Water3_D0", 0.0F, -10F * f1, 0.0F);
        }
        f1 = this.FM.EI.engines[0].getControlRadiator();
        if (Math.abs(this.flapps[0] - f1) > 0.01F) {
            this.flapps[0] = f1;
            for (int i = 2; i < 8; i++) {
                String s = "Cowflap" + i + "_D0";
                this.hierMesh().chunkSetAngles(s, 0.0F, -30F * f1, 0.0F);
            }

        }
        super.update(f);
        if (this.FM.AS.isMaster() && this.bKeelUp && (this.FM.AS.astateBailoutStep == 3) && !this.FM.isStationedOnGround()) {
            this.FM.AS.setInternalDamage(this, 5);
            this.FM.AS.setInternalDamage(this, 4);
            this.bKeelUp = false;
        }
        super.update(f);
    }

    public final void doKeelShutoff() {
        this.nextDMGLevels(4, 2, "Keel1_D" + this.chunkDamageVisible("Keel1"), this);
        this.oldProp[1] = 99;
        Wreckage wreckage;
        if (this.hierMesh().isChunkVisible("Prop2_D1")) {
            wreckage = new Wreckage(this, this.hierMesh().chunkFind("Prop2_D1"));
        } else {
            wreckage = new Wreckage(this, this.hierMesh().chunkFind("Prop2_D0"));
        }
        Eff3DActor.New(wreckage, null, null, 1.0F, Wreckage.SMOKE, 3F);
        Vector3d vector3d = new Vector3d();
        vector3d.set(this.FM.Vwld);
        wreckage.setSpeed(vector3d);
        this.hierMesh().chunkVisible("Prop2_D0", false);
        this.hierMesh().chunkVisible("Prop2_D1", false);
        this.hierMesh().chunkVisible("PropRot2_D0", false);
    }

    public void doMurderPilot(int i) {
        switch (i) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                break;

            case 1:
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("Head2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                break;
        }
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 33:
                return super.cutFM(34, j, actor);

            case 36:
                return super.cutFM(37, j, actor);

            case 11:
                this.cutFM(17, j, actor);
                this.FM.cut(17, j, actor);
                this.cutFM(18, j, actor);
                this.FM.cut(18, j, actor);
                return super.cutFM(i, j, actor);
        }
        return super.cutFM(i, j, actor);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (this.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask2_D0", false);
        } else {
            this.hierMesh().chunkVisible("HMask2_D0", this.hierMesh().isChunkVisible("Pilot2_D0"));
        }
    }

    public void doEjectCatapultRadar() {
        new MsgAction(false, this) {

            public void doAction(Object paramObject) {
                Aircraft localAircraft = (Aircraft) paramObject;
                if (Actor.isValid(localAircraft)) {
                    Loc localLoc1 = new Loc();
                    Loc localLoc2 = new Loc();
                    Vector3d localVector3d = new Vector3d(0.0D, 0.0D, 10D);
                    HookNamed localHookNamed = new HookNamed(localAircraft, "_ExternalSeat02");
                    ((Actor) (localAircraft)).pos.getAbs(localLoc2);
                    localHookNamed.computePos(localAircraft, localLoc2, localLoc1);
                    localLoc1.transform(localVector3d);
                    localVector3d.x += DO_335A6.this.FM.Vwld.x;
                    localVector3d.y += DO_335A6.this.FM.Vwld.y;
                    localVector3d.z += DO_335A6.this.FM.Vwld.z;
                    new EjectionSeat(1, localLoc1, localVector3d, localAircraft);
                }
            }

        };
        this.hierMesh().chunkVisible("Seat1_D0", false);
    }

    public void doEjectCatapultPilot() {
        new MsgAction(false, this) {

            public void doAction(Object paramObject) {
                Aircraft localAircraft = (Aircraft) paramObject;
                if (Actor.isValid(localAircraft)) {
                    Loc localLoc1 = new Loc();
                    Loc localLoc2 = new Loc();
                    Vector3d localVector3d = new Vector3d(0.0D, 0.0D, 10D);
                    HookNamed localHookNamed = new HookNamed(localAircraft, "_ExternalSeat01");
                    ((Actor) (localAircraft)).pos.getAbs(localLoc2);
                    localHookNamed.computePos(localAircraft, localLoc2, localLoc1);
                    localLoc1.transform(localVector3d);
                    localVector3d.x += DO_335A6.this.FM.Vwld.x;
                    localVector3d.y += DO_335A6.this.FM.Vwld.y;
                    localVector3d.z += DO_335A6.this.FM.Vwld.z;
                    new EjectionSeat(1, localLoc1, localVector3d, localAircraft);
                }
            }

        };
        this.hierMesh().chunkVisible("Seat_D0", false);
    }

    private void bailout() {
        if (this.overrideBailout) {
            if ((this.FM.AS.astateBailoutStep >= 0) && (this.FM.AS.astateBailoutStep < 2)) {
                if ((this.FM.CT.cockpitDoorControl > 0.5F) && (this.FM.CT.getCockpitDoor() > 0.5F)) {
                    this.FM.AS.astateBailoutStep = 11;
                } else {
                    this.FM.AS.astateBailoutStep = 2;
                }
            } else if ((this.FM.AS.astateBailoutStep >= 2) && (this.FM.AS.astateBailoutStep <= 3)) {
                switch (this.FM.AS.astateBailoutStep) {
                    case 2:
                        if (this.FM.CT.cockpitDoorControl < 0.5F) {
                            this.doRemoveBlisters();
                        }
                        break;

                    case 3:
                        this.lTimeNextEject = Time.current() + 1000L;
                        break;
                }
                if (this.FM.AS.isMaster()) {
                    this.FM.AS.netToMirrors(20, this.FM.AS.astateBailoutStep, 1, null);
                }
                this.FM.AS.astateBailoutStep = (byte) (this.FM.AS.astateBailoutStep + 1);
                if (this.FM.AS.astateBailoutStep == 4) {
                    this.FM.AS.astateBailoutStep = 11;
                }
            } else if ((this.FM.AS.astateBailoutStep >= 11) && (this.FM.AS.astateBailoutStep <= 19)) {
                int i = this.FM.AS.astateBailoutStep;
                if (this.FM.AS.isMaster()) {
                    this.FM.AS.netToMirrors(20, this.FM.AS.astateBailoutStep, 1, null);
                }
                this.FM.AS.astateBailoutStep = (byte) (this.FM.AS.astateBailoutStep + 1);
                if ((this.FM instanceof Maneuver) && (((Maneuver) this.FM).get_maneuver() != 44)) {
                    World.cur();
                    if (this.FM.AS.actor != World.getPlayerAircraft()) {
                        ((Maneuver) this.FM).set_maneuver(44);
                    }
                }
                if (this.FM.AS.astatePilotStates[i - 11] < 99) {
                    if (i == 11) {
                        this.doRemoveBodyFromPlane(2);
                        this.doEjectCatapultRadar();
                        this.lTimeNextEject = Time.current() + 1000L;
                    } else if (i == 12) {
                        this.doRemoveBodyFromPlane(1);
                        this.doEjectCatapultPilot();
                        this.FM.AS.astateBailoutStep = 51;
                        this.FM.setTakenMortalDamage(true, null);
                        this.FM.CT.WeaponControl[0] = false;
                        this.FM.CT.WeaponControl[1] = false;
                        this.FM.AS.astateBailoutStep = -1;
                        this.overrideBailout = false;
                        this.FM.AS.bIsAboutToBailout = true;
                        this.ejectComplete = true;
                    }
                    this.FM.AS.astatePilotStates[i - 11] = 99;
                } else {
                    EventLog.type("astatePilotStates[" + (i - 11) + "]=" + this.FM.AS.astatePilotStates[i - 11]);
                }
            }
        }
    }

    private final void doRemoveBlisters() {
        for (int i = 1; i < 10; i++) {
            if ((this.hierMesh().chunkFindCheck("Blister" + i + "_D0") != -1) && (this.FM.AS.getPilotHealth(i - 1) > 0.0F)) {
                this.hierMesh().hideSubTrees("Blister" + i + "_D0");
                Wreckage localWreckage = new Wreckage(this, this.hierMesh().chunkFind("Blister" + i + "_D0"));
                localWreckage.collide(false);
                Vector3d localVector3d = new Vector3d();
                localVector3d.set(this.FM.Vwld);
                localWreckage.setSpeed(localVector3d);
            }
        }

    }

    // +++ X4Carrier +++
    public void typeX4CAdjSidePlus() {
        if (this.curPilot == 1) {
            this.deltaAzimuth = 1.0F;
            return;
        }
        this.radarMode++;
        if (this.radarMode > RadarLiSN2.RADAR_MODE_SHORT) {
            this.radarMode = RadarLiSN2.RADAR_MODE_NORMAL;
        }
    }

    public void typeX4CAdjSideMinus() {
        if (this.curPilot == 1) {
            this.deltaAzimuth = -1F;
            return;
        }
        this.radarMode--;
        if (this.radarMode < RadarLiSN2.RADAR_MODE_NORMAL) {
            this.radarMode = RadarLiSN2.RADAR_MODE_SHORT;
        }
    }

    public void typeX4CAdjAttitudePlus() {
        if (this.curPilot == 1) {
            this.deltaTangage = 1.0F;
            return;
        }
        this.radarGain += 10;
        if (this.radarGain > 100) {
            this.radarGain = 100;
        }
    }

    public void typeX4CAdjAttitudeMinus() {
        if (this.curPilot == 1) {
            this.deltaTangage = -1F;
            return;
        }
        this.radarGain -= 10;
        if (this.radarGain < 0) {
            this.radarGain = 0;
        }
    }

    public void typeX4CResetControls() {
        if (this.curPilot == 1) {
            this.deltaAzimuth = this.deltaTangage = 0.0F;
            return;
        }
        this.radarGain = 50;
    }

    public float typeX4CgetdeltaAzimuth() {
        return this.deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage() {
        return this.deltaTangage;
    }

    private float deltaAzimuth = 0.0F;
    private float deltaTangage = 0.0F;
    // +++ X4Carrier +++

    // +++ RadarLiSN2Carrier +++
    public void setCurPilot(int theCurPilot) {
        this.curPilot = theCurPilot;
    }

    public int getCurPilot() {
        return this.curPilot;
    }

    public int getRadarGain() {
        return this.radarGain;
    }

    public int getRadarMode() {
        return this.radarMode;
    }

    private int curPilot  = 1;
    private int radarGain = 50;
    private int radarMode = RadarLiSN2.RADAR_MODE_NORMAL;;
    // --- RadarLiSN2Carrier ---

    // +++ 2nd Pilot Head Movement +++
    public void movePilotsHead(float f, float f1) {
        if (Config.isUSE_RENDER() && ((this.headTp < f1) || (this.headTm > f1) || (this.headYp < f) || (this.headYm > f))) {
            this.headTp = f1 + 0.0005F;
            this.headTm = f1 - 0.0005F;
            this.headYp = f + 0.0005F;
            this.headYm = f - 0.0005F;
            f *= 0.7F;
            f1 *= 0.7F;
            tmpOrLH.setYPR(0.0F, 0.0F, 0.0F);
            tmpOrLH.increment(0.0F, f, 0.0F);
            tmpOrLH.increment(f1, 0.0F, 0.0F);
            tmpOrLH.increment(0.0F, 0.0F, (-0.2F * f1) + (0.05F * f));
            this.headOr[0] = tmpOrLH.getYaw();
            this.headOr[1] = tmpOrLH.getPitch();
            this.headOr[2] = tmpOrLH.getRoll();
            this.headPos[0] = 0.0005F * Math.abs(f);
            this.headPos[1] = -0.0001F * Math.abs(f);
            this.headPos[2] = 0.0F;
            this.hierMesh().chunkSetLocate("Head" + this.curPilot + "_D0", this.headPos, this.headOr);
        }
    }

    private float         headPos[]   = new float[3];
    private float         headOr[]    = new float[3];
    private static Orient tmpOrLH     = new Orient();
    private float         headYp;
    private float         headTp;
    private float         headYm;
    private float         headTm;
    // --- 2nd Pilot Head Movement ---

    public static boolean bChangedPit = false;
    private boolean       overrideBailout;
    private boolean       ejectComplete;
    private long          lTimeNextEject;

    private boolean       bKeelUp;
    private float         flapps[]    = { 0.0F, 0.0F };

    static {
        Class class1 = DO_335A6.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Do-335");
        Property.set(class1, "meshName", "3DO/Plane/Do-335A-6/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1945F);
        Property.set(class1, "yearExpired", 1948F);
        Property.set(class1, "FlightModel", "FlightModels/Do-335A-6.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitDO_335.class, CockpitDO_335RearRadar.class });
        Property.set(class1, "LOSElevation", 1.00705F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 1 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN02", "_MGUN03", "_MGUN01" });
    }
}
