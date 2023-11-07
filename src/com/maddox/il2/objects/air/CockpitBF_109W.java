package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.LightPoint;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class CockpitBF_109W extends CockpitPilot {
    class Interpolater extends InterpolateRef {

        public boolean tick() {
            CockpitBF_109W.this.setTmp = CockpitBF_109W.this.setOld;
            CockpitBF_109W.this.setOld = CockpitBF_109W.this.setNew;
            CockpitBF_109W.this.setNew = CockpitBF_109W.this.setTmp;
            CockpitBF_109W.this.checkAircraftFrameDamageState();
            CockpitBF_109W.this.setNew.altimeter = CockpitBF_109W.this.fm.getAltitude();
            if (CockpitBF_109W.this.cockpitDimControl) {
                if (CockpitBF_109W.this.setOld.dimPosition > 0.0F)
                    CockpitBF_109W.this.setOld.dimPosition = CockpitBF_109W.this.setTmp.dimPosition - 0.05F;
            } else if (CockpitBF_109W.this.setOld.dimPosition < 1.0F)
                CockpitBF_109W.this.setOld.dimPosition = CockpitBF_109W.this.setTmp.dimPosition + 0.05F;
            CockpitBF_109W.this.setOld.throttle = (10F * CockpitBF_109W.this.setTmp.throttle + CockpitBF_109W.this.fm.CT.PowerControl) / 11F;
            CockpitBF_109W.this.setOld.mix = (8F * CockpitBF_109W.this.setTmp.mix + CockpitBF_109W.this.fm.EI.engines[0].getControlMix()) / 9F;
            CockpitBF_109W.this.setOld.azimuth = CockpitBF_109W.this.fm.Or.getYaw();
            if (CockpitBF_109W.this.setTmp.azimuth > 270F && CockpitBF_109W.this.setOld.azimuth < 90F)
                CockpitBF_109W.this.setTmp.azimuth -= 360F;
            if (CockpitBF_109W.this.setTmp.azimuth < 90F && CockpitBF_109W.this.setOld.azimuth > 270F)
                CockpitBF_109W.this.setTmp.azimuth += 360F;
            CockpitBF_109W.this.setOld.waypointAzimuth = (10F * CockpitBF_109W.this.setTmp.waypointAzimuth + (CockpitBF_109W.this.waypointAzimuth() - CockpitBF_109W.this.setTmp.azimuth) + World.Rnd().nextFloat(-30F, 30F)) / 11F;
            return true;
        }

        Interpolater() {}
    }

    private class Variables {

        float altimeter;
        float throttle;
        float dimPosition;
        float azimuth;
        float waypointAzimuth;
        float mix;

        private Variables() {}

    }

    protected float waypointAzimuth() {
        WayPoint waypoint = this.fm.AP.way.curr();
        if (waypoint == null) {
            return 0.0F;
        } else {
            waypoint.getP(this.tmpP);
            this.tmpV.sub(this.tmpP, this.fm.Loc);
            return (float) (57.295779513082323D * Math.atan2(this.tmpV.y, this.tmpV.x));
        }
    }

    public CockpitBF_109W() {
        super("3do/cockpit/Bf-109W/hier.him", "bf109");
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.pictManifold = 0.0F;
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.tmpP = new Point3d();
        this.tmpV = new Vector3d();
        this.setNew.dimPosition = 1.0F;
        HookNamed hooknamed = new HookNamed(this.mesh, "LAMPHOOK1");
        Loc loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        hooknamed.computePos(this, new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F), loc);
        this.light1 = new LightPointActor(new LightPoint(), loc.getPoint());
        this.light1.light.setColor(126F, 232F, 245F);
        this.light1.light.setEmit(0.0F, 0.0F);
        this.pos.base().draw.lightMap().put("LAMPHOOK1", this.light1);
        hooknamed = new HookNamed(this.mesh, "LAMPHOOK2");
        loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        hooknamed.computePos(this, new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F), loc);
        this.light2 = new LightPointActor(new LightPoint(), loc.getPoint());
        this.light2.light.setColor(126F, 232F, 245F);
        this.light2.light.setEmit(0.0F, 0.0F);
        this.pos.base().draw.lightMap().put("LAMPHOOK2", this.light2);
        this.cockpitNightMats = (new String[] { "ZClocks1", "ZClocks1DMG", "ZClocks2", "ZClocks3", "FW190A4Compass" });
        this.setNightMats(false);
        this.interpPut(new Interpolater(), null, Time.current(), null);
        this.loadBuzzerFX();
        this.hasCanopy = true;
    }

    public void removeCanopy() {
        this.hasCanopy = false;
        if (this.mesh.chunkFindCheck("TopOpen") >= 0)
            this.mesh.chunkVisible("TopOpen", false);
        this.mesh.chunkVisible("Z_Holes2_D1", false);
        this.mesh.chunkVisible("Z_Holes1_D1", false);
        if (this.mesh.chunkFindCheck("Top2") >= 0)
            this.mesh.chunkVisible("Top2", false);
    }

    public void reflectWorldToInstruments(float f) {
        if (this.gun[0] == null) {
            this.gun[0] = ((Aircraft) this.fm.actor).getGunByHookName("_MGUN01");
            this.gun[1] = ((Aircraft) this.fm.actor).getGunByHookName("_CANNON01");
            this.gun[2] = ((Aircraft) this.fm.actor).getGunByHookName("_MGUN02");
        }
        this.mesh.chunkSetAngles("Top", 0.0F, 100F * ((FlightModelMain) (this.fm)).CT.getCockpitDoor(), 0.0F);
        this.mesh.chunkSetAngles("Z_Altimeter1", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 10000F, 0.0F, 3600F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Altimeter2", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 10000F, 0.0F, 180F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_ReviTinter", this.cvt(this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -30F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_ReviTint", this.cvt(this.interp(this.setNew.dimPosition, this.setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, 40F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_ATA1", this.cvt(this.pictManifold = 0.75F * this.pictManifold + 0.25F * this.fm.EI.engines[0].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 329F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Speedometer1", this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 800F, 0.0F, 16F), speedometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RPM1", this.floatindex(this.cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 4000F, 0.0F, 8F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelQuantity1", -44.5F + this.floatindex(this.cvt(this.fm.M.fuel / 0.72F, 0.0F, 400F, 0.0F, 8F), fuelScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_EngTemp1", this.cvt(this.fm.EI.engines[0].tOilOut, 0.0F, 160F, 0.0F, 58.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_FuelPress1", this.cvt(this.fm.M.fuel <= 1.0F ? 0.0F : 0.26F, 0.0F, 3F, 0.0F, 135F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_OilPress1", this.cvt(1.0F + 0.05F * this.fm.EI.engines[0].tOilOut, 0.0F, 15F, 0.0F, 135F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass1", 0.0F, this.interp(this.setNew.azimuth, this.setOld.azimuth, f), 0.0F);
        this.mesh.chunkSetAngles("Z_Azimuth1", -this.interp(this.setNew.waypointAzimuth, this.setOld.waypointAzimuth, f), 0.0F, 0.0F);
        float f1;
        if (this.aircraft().isFMTrackMirror()) {
            f1 = this.aircraft().fmTrack().getCockpitAzimuthSpeed();
        } else {
            f1 = this.cvt((this.setNew.azimuth - this.setOld.azimuth) / Time.tickLenFs(), -3F, 3F, 30F, -30F);
            if (this.aircraft().fmTrack() != null)
                this.aircraft().fmTrack().setCockpitAzimuthSpeed(f1);
        }
        this.mesh.chunkSetAngles("Z_TurnBank1", f1, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_TurnBank2", this.cvt(this.getBall(6D), -6F, 6F, -4.5F, 4.5F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Horizon1", 0.0F, 0.0F, this.fm.Or.getKren());
        this.mesh.chunkSetAngles("Z_Horizon2", this.cvt(this.fm.Or.getTangage(), -45F, 45F, -13F, 13F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_PropPitch1", 270F - (float) Math.toDegrees(this.fm.EI.engines[0].getPropPhi() - this.fm.EI.engines[0].getPropPhiMin()) * 60F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_PropPitch2", 105F - (float) Math.toDegrees(this.fm.EI.engines[0].getPropPhi() - this.fm.EI.engines[0].getPropPhiMin()) * 5F, 0.0F, 0.0F);
        this.mesh.chunkVisible("Z_FuelWarning1", this.fm.M.fuel < 36F);
        if (this.gun[0] != null)
            this.mesh.chunkSetAngles("Z_AmmoCounter1", this.cvt(this.gun[0].countBullets(), 0.0F, 500F, 15F, 0.0F), 0.0F, 0.0F);
        if (this.gun[1] != null)
            this.mesh.chunkSetAngles("Z_AmmoCounter2", this.cvt(this.gun[1].countBullets(), 0.0F, 500F, 15F, 0.0F), 0.0F, 0.0F);
        if (this.gun[2] != null)
            this.mesh.chunkSetAngles("Z_AmmoCounter3", this.cvt(this.gun[2].countBullets(), 0.0F, 500F, 15F, 0.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Hour1", this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Minute1", this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Second1", this.cvt(((World.getTimeofDay() % 1.0F) * 60F) % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Column", (this.pictAiler = 0.85F * this.pictAiler + 0.15F * this.fm.CT.AileronControl) * 15F, 0.0F, (this.pictElev = 0.85F * this.pictElev + 0.15F * this.fm.CT.ElevatorControl) * 10F);
        this.mesh.chunkSetAngles("Z_PedalStrut", this.fm.CT.getRudder() * 15F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_LeftPedal", -this.fm.CT.getRudder() * 15F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_RightPedal", -this.fm.CT.getRudder() * 15F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Column", this.fm.CT.getAileron() * 15F, 0.0F, this.fm.CT.getElevator() * 10F);
        if ((this.fm.AS.astateCockpitState & 8) == 0)
            this.mesh.chunkSetAngles("Z_Throttle", this.interp(this.setNew.throttle, this.setOld.throttle, f) * 68.18182F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Mix", this.interp(this.setNew.mix, this.setOld.mix, f) * 62.5F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_MagnetoSwitch", -45F + 28.333F * this.fm.EI.engines[0].getControlMagnetos(), 0.0F, 0.0F);
        if (this.fm.AS.bIsAboutToBailout) {
            this.mesh.chunkVisible("Top", false);
            this.mesh.chunkVisible("Z_Holes2_D1", false);
            this.mesh.chunkVisible("Z_Holes1_D1", false);
        }
    }

    public void toggleDim() {
        this.cockpitDimControl = !this.cockpitDimControl;
    }

    public void toggleLight() {
        this.cockpitLightControl = !this.cockpitLightControl;
        if (this.cockpitLightControl) {
            this.light1.light.setEmit(0.005F, 0.5F);
            this.light2.light.setEmit(0.005F, 0.5F);
            this.setNightMats(true);
        } else {
            this.light1.light.setEmit(0.0F, 0.0F);
            this.light2.light.setEmit(0.0F, 0.0F);
            this.setNightMats(false);
        }
    }

    public void reflectCockpitState() {
        if ((this.fm.AS.astateCockpitState & 2) != 0) {
            this.mesh.chunkVisible("Z_Holes3_D1", true);
            this.mesh.chunkVisible("Revi_D0", false);
            this.mesh.chunkVisible("Z_ReviTint", false);
            this.mesh.chunkVisible("Z_ReviTinter", false);
            this.mesh.chunkVisible("Z_Z_RETICLE", false);
            this.mesh.chunkVisible("Z_Z_MASK", false);
            this.mesh.chunkVisible("Revi_D1", true);
        }
        if ((this.fm.AS.astateCockpitState & 1) != 0)
            this.mesh.chunkVisible("Z_Holes2_D1", true);
        if ((this.fm.AS.astateCockpitState & 0x40) != 0) {
            this.mesh.chunkVisible("PoppedPanel_D0", false);
            this.mesh.chunkVisible("Z_Repeater1", false);
            this.mesh.chunkVisible("Z_Azimuth1", false);
            this.mesh.chunkVisible("Z_Compass1", false);
            this.mesh.chunkVisible("Z_Speedometer1", false);
            this.mesh.chunkVisible("PoppedPanel_D1", true);
        }
        if ((this.fm.AS.astateCockpitState & 4) != 0)
            this.mesh.chunkVisible("Z_Holes1_D1", true);
        if ((this.fm.AS.astateCockpitState & 8) != 0)
            ;
        if ((this.fm.AS.astateCockpitState & 0x80) != 0)
            this.mesh.chunkVisible("Z_OilSplats_D1", true);
        if ((this.fm.AS.astateCockpitState & 0x10) != 0) {
            this.mesh.chunkVisible("Radio_D0", false);
            this.mesh.chunkVisible("Radio_D1", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x20) != 0)
            this.mesh.chunkVisible("Z_Holes1_D1", true);
    }

    protected boolean doFocusEnter() {
        if (super.doFocusEnter()) {
            this.isFocussed = true;
            this.checkAircraftFrameDamageState();
            this.aircraft().hierMesh().chunkVisible("Cockpit2_D0", false);
            return true;
        } else {
            return false;
        }
    }

    protected void doFocusLeave() {
        this.isFocussed = false;
        this.aircraft().hierMesh().chunkVisible("CF_D" + this.aircraftFrameDamageState, true);
        if (this.hasCanopy)
            this.aircraft().hierMesh().chunkVisible("Blister1_D" + this.aircraftBlisterDamageState, true);
        this.aircraft().hierMesh().chunkVisible("Cockpit2_D0", true);
        this.aircraftFrameDamageState = -1;
        this.aircraftBlisterDamageState = -1;
        super.doFocusLeave();
    }

    private int chunkDamageVisible(String s) {
        if (s.lastIndexOf("_") == -1)
            s = s + "_D";
        for (int i = 0; i < 4; i++)
            if (this.aircraft().hierMesh().chunkFindCheck(s + i) != -1 && this.aircraft().hierMesh().isChunkVisible(s + i))
                return i;

        return -1;
    }

    protected void checkAircraftFrameDamageState() {
        if (!this.isFocussed)
            return;
        int frameDamageState = this.chunkDamageVisible("CF");
        if (frameDamageState != -1 && frameDamageState != this.aircraftFrameDamageState) {
            this.aircraftFrameDamageState = frameDamageState;
            this.aircraft().hierMesh().chunkVisible("CF_D" + this.aircraftFrameDamageState, false);
        }
        if (!this.hasCanopy)
            return;
        int blisterDamageState = this.chunkDamageVisible("Blister1");
        if (blisterDamageState != -1 && blisterDamageState != this.aircraftBlisterDamageState) {
            this.aircraftBlisterDamageState = blisterDamageState;
            this.aircraft().hierMesh().chunkVisible("Blister1_D" + this.aircraftBlisterDamageState, false);
        }
    }

    private int                aircraftFrameDamageState   = -1;
    private int                aircraftBlisterDamageState = -1;
    private boolean            isFocussed                 = false;
    boolean                    hasCanopy;

    private Gun                gun[]                      = { null, null, null };
    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    private LightPointActor    light1;
    private LightPointActor    light2;
    private float              pictAiler;
    private float              pictElev;
    private float              pictManifold;
    private static final float speedometerScale[]         = { 0.0F, -12.33333F, 18.5F, 37F, 62.5F, 90F, 110.5F, 134F, 158.5F, 186F, 212.5F, 238.5F, 265F, 289.5F, 315F, 339.5F, 346F, 346F };
    private static final float rpmScale[]                 = { 0.0F, 11.25F, 54F, 111F, 171.5F, 229.5F, 282.5F, 334F, 342.5F, 342.5F };
    private static final float fuelScale[]                = { 0.0F, 9F, 21F, 29.5F, 37F, 48F, 61.5F, 75.5F, 92F, 92F };
    private Point3d            tmpP;
    private Vector3d           tmpV;

    static {
        Property.set(CockpitBF_109W.class, "normZN", 0.4F);
        Property.set(CockpitBF_109W.class, "gsZN", 0.4F);
    }
}
