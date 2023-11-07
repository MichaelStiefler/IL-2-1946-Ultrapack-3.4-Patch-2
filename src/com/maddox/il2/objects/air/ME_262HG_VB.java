package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.electronics.RadarLiSN2;
import com.maddox.rts.MsgAction;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class ME_262HG_VB extends ME_262 implements TypeX4Carrier, TypeRadarLiSN2Carrier {

    public ME_262HG_VB() {
        this.bToFire = false;
        this.tX4Prev = 0L;
        this.deltaAzimuth = 0.0F;
        this.deltaTangage = 0.0F;
        this.headPos = new float[3];
        this.headOr = new float[3];
        this.curPilot = 1;
        this.blisterRemoved = new boolean[2];
        this.blisterRemoved[0] = false;
        this.blisterRemoved[1] = false;
        this.radarGain = 50;
        this.radarMode = RadarLiSN2.RADAR_MODE_NORMAL;
    }

    protected void moveRudder(float f) {
        this.updateControlsVisuals();
        if (this.FM.CT.getGear() > 0.75F) {
            this.hierMesh().chunkSetAngles("GearC21_D0", 0.0F, 40F * f, 0.0F);
        }
    }

    protected void moveElevator(float f) {
        this.updateControlsVisuals();
    }

    private final void updateControlsVisuals() {
        this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, (-21.25F * this.FM.CT.getElevator()) - (21.25F * this.FM.CT.getRudder()), 0.0F);
        this.hierMesh().chunkSetAngles("VatorR_D0", 0.0F, (-21.25F * this.FM.CT.getElevator()) + (21.25F * this.FM.CT.getRudder()), 0.0F);
    }

    public void doEjectCatapult() {
        new MsgAction(false, this) {

            public void doAction(Object obj) {
                Aircraft aircraft = (Aircraft) obj;
                if (!Actor.isValid(aircraft)) {
                    return;
                } else {
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
    }

    protected void moveFlap(float paramFloat) {
        float f = -45.0F * paramFloat;
        this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, f, 0.0F);
        this.hierMesh().chunkSetAngles("Flap04_D0", 0.0F, f, 0.0F);
    }

    public void blisterRemoved(int i) {
        if ((i < 1) || (i > 2)) {
            return;
        }
        this.blisterRemoved[i - 1] = true;
    }

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

    public void setCurPilot(int theCurPilot) {
        this.curPilot = theCurPilot;
    }

    public int getCurPilot() {
        return this.curPilot;
    }

    protected void moveFan(float f) {
        for (int j = 0; j < this.FM.EI.getNum(); j++) {
            this.propPos[j] = (this.propPos[j] + (57.3F * this.FM.EI.engines[j].getw() * f)) % 360F;
            this.hierMesh().chunkSetAngles(Props[j][0], 0.0F, -this.propPos[j], 0.0F);
        }

    }

    // New Gear Animation Code,
    // historically accurate in terms of moving main gear before tail wheel
    // In order to do so, new Parameter "bDown" has been introduced to distinguish between
    // gear up and gear down
    public static void moveGear(HierMesh hiermesh, float f, float f1, float f2, boolean bDown) {
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, Aircraft.cvt(f2, 0.11F, 0.89F, 0.0F, 111F), 0.0F);
        hiermesh.chunkSetAngles("GearC21_D0", 0.0F, 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(f2, 0.11F, 0.89F, 0.0F, 90F), 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(f, 0.11F, 0.89F, 0.0F, 73F), 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(f1, 0.11F, 0.89F, 0.0F, 73F), 0.0F);
        if (f2 < 0.5F) {
            hiermesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(f2, 0.01F, 0.11F, 0.0F, 90F), 0.0F);
        } else {
            hiermesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(f2, 0.89F, 0.99F, 90F, 0.0F), 0.0F);
        }
        if (f < 0.5F) {
            hiermesh.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.11F, 0.0F, 88F), 0.0F);
            hiermesh.chunkSetAngles("GearL6_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.11F, 0.0F, -130F), 0.0F);
        } else {
            hiermesh.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(f, 0.89F, 0.99F, 88F, 0.0F), 0.0F);
            hiermesh.chunkSetAngles("GearL6_D0", 0.0F, Aircraft.cvt(f, 0.89F, 0.99F, -130F, -90F), 0.0F);
        }
        if (f1 < 0.5F) {
            hiermesh.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(f1, 0.01F, 0.11F, 0.0F, 88F), 0.0F);
            hiermesh.chunkSetAngles("GearR6_D0", 0.0F, Aircraft.cvt(f1, 0.01F, 0.11F, 0.0F, -130F), 0.0F);
        } else {
            hiermesh.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(f1, 0.89F, 0.99F, 88F, 0.0F), 0.0F);
            hiermesh.chunkSetAngles("GearR6_D0", 0.0F, Aircraft.cvt(f1, 0.89F, 0.99F, -130F, -90F), 0.0F);
        }
    }

    public static void moveGear(HierMesh hiermesh, float f, float f1, float f2) {
        moveGear(hiermesh, f, f1, f2, true);
    }

    protected void moveGear(float f, float f1, float f2) {
        moveGear(this.hierMesh(), f, f1, f2, this.FM.CT.GearControl > 0.5F);
    }

    // ************************************************************************************************
    // Gear code for backward compatibility, older base game versions don't indepently move their gears
    public static void moveGear(HierMesh hiermesh, float f, boolean bDown) {
        moveGear(hiermesh, f, f, f, bDown); // re-route old style function calls to new code
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        moveGear(hiermesh, f, f, f, true); // re-route old style function calls to new code
    }

    protected void moveGear(float f) {
        moveGear(this.hierMesh(), f, this.FM.CT.GearControl > 0.5F);
    }

    // ************************************************************************************************

    public void moveCockpitDoor(float f) {
        this.resetYPRmodifier();
        this.hierMesh().chunkSetAngles("Blister1_D0", 0.0F, 100F * f, 0.0F);
        if (Config.isUSE_RENDER()) {
            if ((Main3D.cur3D().cockpits != null) && (Main3D.cur3D().cockpits[0] != null)) {
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            }
            this.setDoorSnd(f);
        }
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (((this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) || !flag || !(this.FM instanceof Pilot)) {
            return;
        }
        Pilot pilot = (Pilot) this.FM;
        if ((pilot.get_maneuver() == 63) && (pilot.target != null)) {
            Point3d point3d = new Point3d(pilot.target.Loc);
            point3d.sub(this.FM.Loc);
            this.FM.Or.transformInv(point3d);
            if ((((point3d.x > 4000D) && (point3d.x < 5500D)) || ((point3d.x > 100D) && (point3d.x < 5000D) && (World.Rnd().nextFloat() < 0.33F))) && (Time.current() > (this.tX4Prev + 10000L))) {
                this.bToFire = true;
                this.tX4Prev = Time.current();
            }
        }
    }

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

    public void doMurderPilot(int i) {
        switch (i) {
            default:
                break;

            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                if (!this.FM.AS.bIsAboutToBailout) {
                    this.hierMesh().chunkVisible("Gore1_D0", true);
                }
                break;

            case 1:
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("Head2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                if (!this.FM.AS.bIsAboutToBailout) {
                    this.hierMesh().chunkVisible("Gore3_D0", true);
                }
                break;
        }
    }

    public void update(float f) {
        if (this.FM.isPlayers() && !Main3D.cur3D().isViewOutside()) {
            this.hierMesh().chunkVisible("Blister1_D0", false);
        } else {
            this.hierMesh().chunkVisible("Blister1_D0", this.bHasBlister);
        }
        if (this.FM.AS.bIsAboutToBailout) {
            this.hierMesh().chunkVisible("Blister1_D0", false);
        }
        super.update(f);
    }

    public int getRadarGain() {
        return this.radarGain;
    }

    public int getRadarMode() {
        return this.radarMode;
    }

    public boolean        bToFire;
    private long          tX4Prev;
    private float         deltaAzimuth;
    private float         deltaTangage;
    private float         headPos[];
    private float         headOr[];
    private static Orient tmpOrLH = new Orient();
    private float         headYp;
    private float         headTp;
    private float         headYm;
    private float         headTm;
    private int           curPilot;
    public boolean        blisterRemoved[];

    private int           radarGain;
    private int           radarMode;

    static {
        Class class1 = ME_262HG_VB.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Me 262");
        Property.set(class1, "meshName", "3DO/Plane/Me-262HG-VB/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1944.1F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/Me-262HG-VB.fmd");
// Property.set(class1, "cockpitClass", new Class[] { CockpitME_262HG_VB.class, CockpitME_262HG_VB_Rear.class, CockpitME_262HG_VB_FuG_224.class });
        Property.set(class1, "cockpitClass", new Class[] { CockpitME_262HG_VB.class, CockpitME_262HG_VB_Rear.class });
        Property.set(class1, "LOSElevation", 0.7498F);
        weaponTriggersRegister(class1, new int[] { 0, 0, 1, 1, 9, 9, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2 });
        weaponHooksRegister(class1, new String[] { "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalDev01", "_ExternalDev02", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalRock07", "_ExternalRock08", "_ExternalRock09", "_ExternalRock10", "_ExternalRock11", "_ExternalRock12", "_ExternalRock13", "_ExternalRock14", "_ExternalRock15", "_ExternalRock16", "_ExternalRock17", "_ExternalRock18", "_ExternalRock19", "_ExternalRock20", "_ExternalRock21", "_ExternalRock22", "_ExternalRock23", "_ExternalRock24" });
    }
}
