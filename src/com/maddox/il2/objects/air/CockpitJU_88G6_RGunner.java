package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sound.ReverbFXRoom;

public class CockpitJU_88G6_RGunner extends CockpitGunner {
    class Interpolater extends InterpolateRef {

        public boolean tick() {
            if (CockpitJU_88G6_RGunner.this.fm != null) {
                CockpitJU_88G6_RGunner.this.setTmp = CockpitJU_88G6_RGunner.this.setOld;
                CockpitJU_88G6_RGunner.this.setOld = CockpitJU_88G6_RGunner.this.setNew;
                CockpitJU_88G6_RGunner.this.setNew = CockpitJU_88G6_RGunner.this.setTmp;
                CockpitJU_88G6_RGunner.this.setNew.throttle1 = (0.85F * CockpitJU_88G6_RGunner.this.setOld.throttle1) + (CockpitJU_88G6_RGunner.this.fm.EI.engines[0].getControlThrottle() * 0.15F);
                CockpitJU_88G6_RGunner.this.setNew.prop1 = (0.85F * CockpitJU_88G6_RGunner.this.setOld.prop1) + (CockpitJU_88G6_RGunner.this.fm.EI.engines[0].getControlProp() * 0.15F);
                CockpitJU_88G6_RGunner.this.setNew.throttle2 = (0.85F * CockpitJU_88G6_RGunner.this.setOld.throttle2) + (CockpitJU_88G6_RGunner.this.fm.EI.engines[1].getControlThrottle() * 0.15F);
                CockpitJU_88G6_RGunner.this.setNew.prop2 = (0.85F * CockpitJU_88G6_RGunner.this.setOld.prop2) + (CockpitJU_88G6_RGunner.this.fm.EI.engines[1].getControlProp() * 0.15F);
                CockpitJU_88G6_RGunner.this.setNew.altimeter = CockpitJU_88G6_RGunner.this.fm.getAltitude();
                float f = CockpitJU_88G6_RGunner.this.waypointAzimuth();
                CockpitJU_88G6_RGunner.this.setNew.waypointAzimuth.setDeg(CockpitJU_88G6_RGunner.this.setOld.waypointAzimuth.getDeg(0.1F), f - CockpitJU_88G6_RGunner.this.setOld.azimuth.getDeg(1.0F));
                CockpitJU_88G6_RGunner.this.setNew.azimuth.setDeg(CockpitJU_88G6_RGunner.this.setOld.azimuth.getDeg(1.0F), CockpitJU_88G6_RGunner.this.fm.Or.azimut());
                CockpitJU_88G6_RGunner.this.setNew.vspeed = ((199F * CockpitJU_88G6_RGunner.this.setOld.vspeed) + CockpitJU_88G6_RGunner.this.fm.getVertSpeed()) / 200F;
                if (CockpitJU_88G6_RGunner.this.cockpitDimControl) {
                    if (CockpitJU_88G6_RGunner.this.setNew.dimPosition > 0.0F) {
                        CockpitJU_88G6_RGunner.this.setNew.dimPosition = CockpitJU_88G6_RGunner.this.setOld.dimPosition - 0.05F;
                    }
                } else if (CockpitJU_88G6_RGunner.this.setNew.dimPosition < 1.0F) {
                    CockpitJU_88G6_RGunner.this.setNew.dimPosition = CockpitJU_88G6_RGunner.this.setOld.dimPosition + 0.05F;
                }
                float f1 = CockpitJU_88G6_RGunner.this.prevFuel - CockpitJU_88G6_RGunner.this.fm.M.fuel;
                CockpitJU_88G6_RGunner.this.prevFuel = CockpitJU_88G6_RGunner.this.fm.M.fuel;
                f1 /= 0.72F;
                f1 /= Time.tickLenFs();
                f1 *= 3600F;
                CockpitJU_88G6_RGunner.this.setNew.cons = (0.91F * CockpitJU_88G6_RGunner.this.setOld.cons) + (0.09F * f1);
            }
            return true;
        }

        Interpolater() {
        }
    }

    private class Variables {

        float      throttle1;
        float      prop1;
        float      throttle2;
        float      prop2;
        float      altimeter;
        AnglesFork azimuth;
        AnglesFork waypointAzimuth;
        float      vspeed;
        float      dimPosition;
        float      cons;

        private Variables() {
            this.azimuth = new AnglesFork();
            this.waypointAzimuth = new AnglesFork();
        }

    }

    protected boolean doFocusEnter() {
        if (super.doFocusEnter()) {
            ((TypeRadarLiSN2Carrier) this.aircraft()).setCurPilot(3);
            this.aircraft().hierMesh().chunkVisible("fakeNose_D0", false);
            this.aircraft().hierMesh().chunkVisible("fakeNose_D1", false);
            this.aircraft().hierMesh().chunkVisible("fakeNose_D2", false);
            this.aircraft().hierMesh().chunkVisible("fakeNose_D3", false);
            this.aircraft().hierMesh().chunkVisible("BlisterTop_D0", false);
            return true;
        } else {
            return false;
        }
    }

    protected void doFocusLeave() {
        if (this.isFocused()) {
            this.aircraft().hierMesh().chunkVisible("fakeNose_D0", this.aircraft().hierMesh().isChunkVisible("Nose_D0"));
            this.aircraft().hierMesh().chunkVisible("fakeNose_D1", this.aircraft().hierMesh().isChunkVisible("Nose_D1"));
            this.aircraft().hierMesh().chunkVisible("fakeNose_D2", this.aircraft().hierMesh().isChunkVisible("Nose_D2"));
            this.aircraft().hierMesh().chunkVisible("fakeNose_D3", this.aircraft().hierMesh().isChunkVisible("Nose_D3"));
            this.aircraft().hierMesh().chunkVisible("BlisterTop_D0", true);
            super.doFocusLeave();
        }
    }

    public void moveGun(Orient orient) {
        super.moveGun(orient);
        float f = orient.getYaw();
        float f1 = orient.getTangage();
        this.mesh.chunkSetAngles("zTurret1A", 0.0F, -f, 0.0F);
        this.mesh.chunkSetAngles("zTurret1B", 0.0F, f1, 0.0F);
        if (f > 10F) {
            f = 10F;
        }
        if (f < -25F) {
            f = -25F;
        }
        if (f1 < -10F) {
            f1 = -10F;
        }
        if (f1 > 15F) {
            f1 = 15F;
        }
        if ((f > 0.0F) && (f1 < -4.5F)) {
            f1 = -4.5F;
        }
        this.mesh.chunkSetAngles("CameraRodA", 0.0F, -f, 0.0F);
        this.mesh.chunkSetAngles("CameraRodB", 0.0F, f1, 0.0F);
    }

    public void clipAnglesGun(Orient orient) {
        if (this.isRealMode()) {
            if (!this.aiTurret().bIsOperable) {
                orient.setYPR(0.0F, 0.0F, 0.0F);
            } else {
                float f = orient.getYaw();
                float f1 = orient.getTangage();
                if (f < -45F) {
                    f = -45F;
                }
                if (f > 25F) {
                    f = 25F;
                }
                if (f1 > 60F) {
                    f1 = 60F;
                }
                if (f1 < -10F) {
                    f1 = -10F;
                }
                if (f < -2F) {
                    if (f1 < this.cvt(f, -6.8F, -2F, -10F, -2.99F)) {
                        f1 = this.cvt(f, -6.8F, -2F, -10F, -2.99F);
                    }
                } else if (f < 0.5F) {
                    if (f1 < this.cvt(f, -2F, 0.5F, -2.99F, -2.3F)) {
                        f1 = this.cvt(f, -2F, 0.5F, -2.99F, -2.3F);
                    }
                } else if (f < 5.3F) {
                    if (f1 < this.cvt(f, 0.5F, 5.3F, -2.3F, -2.3F)) {
                        f1 = this.cvt(f, 0.5F, 5.3F, -2.3F, -2.3F);
                    }
                } else if (f1 < this.cvt(f, 5.3F, 25F, -2.3F, -7.2F)) {
                    f1 = this.cvt(f, 5.3F, 25F, -2.3F, -7.2F);
                }
                orient.setYPR(f, f1, 0.0F);
                orient.wrap();
            }
        }
    }

    protected void interpTick() {
        if (this.isRealMode()) {
            if ((this.emitter == null) || !this.emitter.haveBullets() || !this.aiTurret().bIsOperable) {
                this.bGunFire = false;
            }
            this.fm.CT.WeaponControl[this.weaponControlNum()] = this.bGunFire;
        }
    }

    public void doGunFire(boolean flag) {
        if (this.isRealMode()) {
            if ((this.emitter == null) || !this.emitter.haveBullets() || !this.aiTurret().bIsOperable) {
                this.bGunFire = false;
            } else {
                this.bGunFire = flag;
            }
            this.fm.CT.WeaponControl[this.weaponControlNum()] = this.bGunFire;
        }
    }

    public CockpitJU_88G6_RGunner() {
        super("3DO/Cockpit/Ju-88G-6-RGun/G6hier.him", "he111");
        this.bNeedSetUp = true;
        this.setOld = new Variables();
        this.setNew = new Variables();
        this.w = new Vector3f();
        this.pictAiler = 0.0F;
        this.pictElev = 0.0F;
        this.pictManf1 = 1.0F;
        this.pictManf2 = 1.0F;
        this.prevFuel = 0.0F;
        this.tmpP = new Point3d();
        this.tmpV = new Vector3d();
        this.cockpitNightMats = (new String[] { "88A4_I_Set1", "88A4_I_Set2", "88A4_I_Set3", "88a4_I_Set4", "88a4_I_Set5", "88a4_I_Set6", "88a4_SlidingGlass", "88gardinen", "Peil1", "Peil2", "skala" });
        this.setNightMats(false);
        this.setNew.dimPosition = this.setOld.dimPosition = 1.0F;
        this.cockpitDimControl = !this.cockpitDimControl;
        this.interpPut(new Interpolater(), null, Time.current(), null);
        if (this.acoustics != null) {
            this.acoustics.globFX = new ReverbFXRoom(0.45F);
        }
    }

    public void toggleDim() {
        this.cockpitDimControl = !this.cockpitDimControl;
    }

    public void reflectWorldToInstruments(float f) {
        if (this.bNeedSetUp) {
            this.reflectPlaneMats();
            this.bNeedSetUp = false;
        }
        this.mesh.chunkSetAngles("Z_Trim1", this.cvt(this.fm.CT.getTrimElevatorControl(), -0.5F, 0.5F, -750F, 750F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zColumn1", 7F * (this.pictElev = (0.85F * this.pictElev) + (0.15F * this.fm.CT.ElevatorControl)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zColumn2", 52.2F * (this.pictAiler = (0.85F * this.pictAiler) + (0.15F * this.fm.CT.AileronControl)), 0.0F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = -0.1F * this.fm.CT.getRudder();
        this.mesh.chunkSetLocate("zPedalL", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = 0.1F * this.fm.CT.getRudder();
        this.mesh.chunkSetLocate("zPedalR", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("TempMeter", this.floatindex(this.cvt(Atmosphere.temperature((float) this.fm.Loc.z), 213.09F, 293.09F, 0.0F, 8F), frAirTempScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Radiator_Sw1", this.cvt(this.fm.EI.engines[0].getControlRadiator(), 0.0F, 1.0F, 0.0F, -120F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Radiator_Sw2", this.cvt(this.fm.EI.engines[1].getControlRadiator(), 0.0F, 1.0F, 0.0F, -120F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_MagnetoSw1", this.cvt(this.fm.EI.engines[0].getControlMagnetos(), 0.0F, 3F, 100F, 0.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_MagnetoSw2", this.cvt(this.fm.EI.engines[1].getControlMagnetos(), 0.0F, 3F, 100F, 0.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zHour1", this.cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zMinute1", this.cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zAlt1", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 20000F, 0.0F, 7200F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zAlt2", this.cvt(this.interp(this.setNew.altimeter, this.setOld.altimeter, f), 0.0F, 6000F, 0.0F, 360F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zSpeed", this.floatindex(this.cvt(Pitot.Indicator((float) this.fm.Loc.z, this.fm.getSpeedKMH()), 50F, 750F, 0.0F, 14F), speedometerScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Climb1", this.cvt(this.setNew.vspeed, -15F, 15F, -151F, 151F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zWaterTemp1", this.cvt(this.fm.EI.engines[0].tWaterOut, 0.0F, 120F, 0.0F, 64F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zWaterTemp2", this.cvt(this.fm.EI.engines[1].tWaterOut, 0.0F, 120F, 0.0F, 64F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zOilPress1", this.cvt(1.0F + (0.05F * this.fm.EI.engines[0].tOilOut * this.fm.EI.engines[0].getReadyness()), 0.0F, 7.47F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zOilPress2", this.cvt(1.0F + (0.05F * this.fm.EI.engines[1].tOilOut * this.fm.EI.engines[1].getReadyness()), 0.0F, 7.47F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuelPress1", this.cvt(this.fm.M.fuel > 1.0F ? 0.32F : 0.0F, 0.0F, 0.5F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuelPress2", this.cvt(this.fm.M.fuel > 1.0F ? 0.32F : 0.0F, 0.0F, 0.5F, 0.0F, 90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zRPM1", this.cvt(this.fm.EI.engines[0].getRPM(), 600F, 3600F, 0.0F, 331F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zRPM2", this.cvt(this.fm.EI.engines[1].getRPM(), 600F, 3600F, 0.0F, 331F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ATA1", this.pictManf1 = (0.9F * this.pictManf1) + (0.1F * this.cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 325.5F)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("ATA2", this.pictManf2 = (0.9F * this.pictManf2) + (0.1F * this.cvt(this.fm.EI.engines[1].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 325.5F)), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuel1", this.cvt(this.fm.M.fuel, 0.0F, 1008F, 0.0F, 77F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuel2", this.cvt(this.fm.M.fuel, 0.0F, 1008F, 0.0F, 77F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zFuelPress", this.cvt(this.setNew.cons, 100F, 500F, 0.0F, 240F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass3", this.setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass4", -this.setNew.azimuth.getDeg(f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass5", 180F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass6", 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass7", this.setNew.azimuth.getDeg(f), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Z_Compass8", this.setNew.waypointAzimuth.getDeg(f) + this.setNew.azimuth.getDeg(f) + 90F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("zHORIZ1", -this.fm.Or.getKren(), 0.0F, 0.0F);
        this.resetYPRmodifier();
        Cockpit.xyz[1] = this.cvt(this.fm.Or.getTangage(), -45F, 45F, 0.045F, -0.045F);
        this.mesh.chunkSetLocate("zHORIZ2", Cockpit.xyz, Cockpit.ypr);
        this.resetYPRmodifier();
        Cockpit.xyz[2] = 0.02125F * this.fm.CT.getTrimElevatorControl();
        this.mesh.chunkSetLocate("zTRIM1", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = 0.02125F * this.fm.CT.getTrimAileronControl();
        this.mesh.chunkSetLocate("zTRIM2", Cockpit.xyz, Cockpit.ypr);
        Cockpit.xyz[2] = 0.02125F * this.fm.CT.getTrimRudderControl();
        this.mesh.chunkSetLocate("zTRIM3", Cockpit.xyz, Cockpit.ypr);
        this.w.set(this.fm.getW());
        this.fm.Or.transform(this.w);
        this.mesh.chunkSetAngles("Z_TurnBank1", this.cvt(this.w.z, -0.23562F, 0.23562F, 25F, -25F), 0.0F, 0.0F);
    }

    protected float waypointAzimuth() {
        WayPoint waypoint = this.fm.AP.way.curr();
        if (waypoint == null) {
            return 0.0F;
        }
        waypoint.getP(this.tmpP);
        this.tmpV.sub(this.tmpP, this.fm.Loc);
        float f;
        for (f = (float) (57.295779513082323D * Math.atan2(-this.tmpV.y, this.tmpV.x)); f <= -180F; f += 180F) {
            ;
        }
        for (; f > 180F; f -= 180F) {
            ;
        }
        return f;
    }

    public void reflectCockpitState() {
        if ((this.fm.AS.astateCockpitState & 0x80) == 0) {
            ;
        }
        if ((this.fm.AS.astateCockpitState & 2) != 0) {
            this.mesh.chunkVisible("XGlassDamage1", true);
        }
        if ((this.fm.AS.astateCockpitState & 1) != 0) {
            this.mesh.chunkVisible("XGlassDamage5", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x40) == 0) {
            ;
        }
        if ((this.fm.AS.astateCockpitState & 4) != 0) {
            this.mesh.chunkVisible("XGlassDamage3", true);
        }
        if ((this.fm.AS.astateCockpitState & 8) != 0) {
            this.mesh.chunkVisible("XGlassDamage4", true);
            this.mesh.chunkVisible("XGlassDamage5", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x10) != 0) {
            this.mesh.chunkVisible("XGlassDamage2", true);
        }
        if ((this.fm.AS.astateCockpitState & 0x20) != 0) {
            this.mesh.chunkVisible("XGlassDamage6", true);
        }
        this.retoggleLight();
    }

    public void toggleLight() {
        this.cockpitLightControl = !this.cockpitLightControl;
        if (this.cockpitLightControl) {
            this.setNightMats(true);
        } else {
            this.setNightMats(false);
        }
    }

    private void retoggleLight() {
        if (this.cockpitLightControl) {
            this.setNightMats(false);
            this.setNightMats(true);
        } else {
            this.setNightMats(true);
            this.setNightMats(false);
        }
    }

    protected void reflectPlaneMats() {
        HierMesh hiermesh = this.aircraft().hierMesh();
        com.maddox.il2.engine.Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat);
        mat = hiermesh.material(hiermesh.materialFind("Matt1D0o"));
        this.mesh.materialReplace("Matt1D0o", mat);
    }

    private boolean            bNeedSetUp;
    private Variables          setOld;
    private Variables          setNew;
    private Variables          setTmp;
    public Vector3f            w;
    private float              pictAiler;
    private float              pictElev;
    private float              pictManf1;
    private float              pictManf2;
    private float              prevFuel;
    private static final float speedometerScale[] = { 0.0F, 16F, 35.5F, 60.5F, 88F, 112.5F, 136F, 159.5F, 186.5F, 211.5F, 240F, 268F, 295.5F, 321F, 347F };
    private static final float frAirTempScale[]   = { 76.5F, 68F, 57F, 44.5F, 29.5F, 14.5F, 1.5F, -10F, -19F };
    private Point3d            tmpP;
    private Vector3d           tmpV;

    static {
        Property.set(CockpitJU_88G6_RGunner.class, "aiTuretNum", 0);
        Property.set(CockpitJU_88G6_RGunner.class, "weaponControlNum", 10);
        Property.set(CockpitJU_88G6_RGunner.class, "astatePilotIndx", 1);
    }
}
