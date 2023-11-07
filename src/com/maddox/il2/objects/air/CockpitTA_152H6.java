package com.maddox.il2.objects.air;

import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.LightPoint;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.fm.Atmosphere;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.il2.objects.weapons.GunEmpty;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class CockpitTA_152H6 extends CockpitPilot
{
    private class Variables
    {

        float altimeter;
        float throttle;
        float dimPosition;
        AnglesFork azimuth;
        AnglesFork waypointAzimuth;
        float turn;
        float beaconDirection;
        float beaconRange;
        float vspeed;
        float k14wingspan;
        float k14mode;
        float k14x;
        float k14y;
        float k14w;

        private Variables()
        {
            azimuth = new AnglesFork();
            waypointAzimuth = new AnglesFork();
        }
    }

    class Interpolater extends InterpolateRef
    {

        public boolean tick()
        {
            if(fm != null)
            {
                setTmp = setOld;
                setOld = setNew;
                setNew = setTmp;
                setNew.altimeter = fm.getAltitude();
                if(cockpitDimControl)
                {
                    if(setNew.dimPosition > 0.0F)
                        setNew.dimPosition = setOld.dimPosition - 0.05F;
                } else
                if(setNew.dimPosition < 1.0F)
                    setNew.dimPosition = setOld.dimPosition + 0.05F;
                setNew.throttle = (10F * setOld.throttle + fm.CT.PowerControl) / 11F;
                setNew.vspeed = (499F * setOld.vspeed + fm.getVertSpeed()) / 500F;
                float f = waypointAzimuth();
                if(useRealisticNavigationInstruments())
                {
                    setNew.waypointAzimuth.setDeg(f - 90F);
                    setOld.waypointAzimuth.setDeg(f - 90F);
                } else
                {
                    setNew.waypointAzimuth.setDeg(setOld.waypointAzimuth.getDeg(0.1F), f - setOld.azimuth.getDeg(1.0F));
                }
                setNew.azimuth.setDeg(setOld.azimuth.getDeg(1.0F), fm.Or.azimut());
                w.set(fm.getW());
                fm.Or.transform(w);
                setNew.turn = (12F * setOld.turn + w.z) / 13F;
                setNew.beaconDirection = (10F * setOld.beaconDirection + getBeaconDirection()) / 11F;
                setNew.beaconRange = (10F * setOld.beaconRange + getBeaconRange()) / 11F;
                float f1 = ((TA_152H6)aircraft()).k14Distance;
                setNew.k14w = (5F * CockpitTA_152H6.k14TargetWingspanScale[((TA_152H6)aircraft()).k14WingspanType]) / f1;
                setNew.k14w = 0.9F * setOld.k14w + 0.1F * setNew.k14w;
                setNew.k14wingspan = 0.9F * setOld.k14wingspan + 0.1F * CockpitTA_152H6.k14TargetMarkScale[((TA_152H6)aircraft()).k14WingspanType];
                setNew.k14mode = 0.8F * setOld.k14mode + 0.2F * (float)((TA_152H6)aircraft()).k14Mode;
                Vector3d vector3d = aircraft().FM.getW();
                double d = 0.00125D * (double)f1;
                float f2 = (float)Math.toDegrees(d * vector3d.z);
                float f3 = -(float)Math.toDegrees(d * vector3d.y);
                float f4 = floatindex((f1 - 200F) * 0.04F, CockpitTA_152H6.k14BulletDrop) - CockpitTA_152H6.k14BulletDrop[0];
                f3 += (float)Math.toDegrees(Math.atan(f4 / f1));
                setNew.k14x = 0.92F * setOld.k14x + 0.08F * f2;
                setNew.k14y = 0.92F * setOld.k14y + 0.08F * f3;
                if(setNew.k14x > 7F)
                    setNew.k14x = 7F;
                if(setNew.k14x < -7F)
                    setNew.k14x = -7F;
                if(setNew.k14y > 7F)
                    setNew.k14y = 7F;
                if(setNew.k14y < -7F)
                    setNew.k14y = -7F;
            }
            return true;
        }

        Interpolater()
        {
        }
    }

    protected float waypointAzimuth()
    {
        return waypointAzimuthInvertMinus(30F);
    }

    public CockpitTA_152H6()
    {
        super("3DO/Cockpit/Ta-152H-Paulus/hier_EZ42.him", "bf109");
        gun = new Gun[5];
        bomb = new BulletEmitter[4];
        AircraftLH.printCompassHeading = true;
        ((AircraftLH)aircraft()).bWantBeaconKeys = true;
        setOld = new Variables();
        setNew = new Variables();
        pictAiler = 0.0F;
        pictElev = 0.0F;
        w = new Vector3f();
        setNew.dimPosition = 1.0F;
        bNeedSetUp = true;
        HookNamed hooknamed = new HookNamed(this.mesh, "LIGHTHOOK_L");
        Loc loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        hooknamed.computePos(this, new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F), loc);
        light1 = new LightPointActor(new LightPoint(), loc.getPoint());
        light1.light.setColor(126F, 232F, 245F);
        light1.light.setEmit(0.0F, 0.0F);
        this.pos.base().draw.lightMap().put("LAMPHOOK1", light1);
        hooknamed = new HookNamed(this.mesh, "LIGHTHOOK_R");
        loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        hooknamed.computePos(this, new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F), loc);
        light2 = new LightPointActor(new LightPoint(), loc.getPoint());
        light2.light.setColor(126F, 232F, 245F);
        light2.light.setEmit(0.0F, 0.0F);
        this.pos.base().draw.lightMap().put("LAMPHOOK2", light2);
        this.cockpitNightMats = (new String[] {
            "D9GP1", "D9GP2", "Ta152GP3", "A5GP3Km", "Ta152GP4_MW50", "Ta152GP5", "A4GP6", "Ta152Trans2", "D9GP2", "Trans4", 
            "Ta152Panel3", "Ta152EQpt5"
        });
        setNightMats(false);
        this.cockpitDimControl = !this.cockpitDimControl;
        interpPut(new Interpolater(), null, Time.current(), null);
        AircraftLH.printCompassHeading = true;
    }

    public void reflectWorldToInstruments(float f)
    {
        int i = ((TA_152H6)aircraft()).k14Mode;
        boolean flag = i < 2;
        this.mesh.chunkVisible("Z_Z_RETICLE", flag);
        flag = i > 0;
        this.mesh.chunkVisible("Z_Z_RETICLE1", flag);
        this.mesh.chunkSetAngles("Z_Z_RETICLE1", 0.0F, setNew.k14x, setNew.k14y);
        resetYPRmodifier();
        Cockpit.xyz[0] = setNew.k14w;
        for(int j = 1; j < 7; j++)
        {
            this.mesh.chunkVisible("Z_Z_AIMMARK" + j, flag);
            this.mesh.chunkSetLocate("Z_Z_AIMMARK" + j, Cockpit.xyz, Cockpit.ypr);
        }

        if(gun[0] == null)
        {
            gun[0] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON01");
            gun[1] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON03");
            gun[2] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON04");
            gun[3] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON05");
            gun[4] = ((Aircraft)this.fm.actor).getGunByHookName("_CANNON06");
        }
        if(bNeedSetUp)
        {
            reflectPlaneMats();
            bNeedSetUp = false;
        }
        if(bomb[0] == null)
        {
            for(int k = 0; k < bomb.length; k++)
                bomb[k] = GunEmpty.get();

            if(((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalBomb05") != GunEmpty.get())
            {
                bomb[1] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalBomb05");
                bomb[2] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalBomb05");
            } else
            if(((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalDev02") != GunEmpty.get())
            {
                bomb[1] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalDev02");
                bomb[2] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalDev02");
            }
            if(((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalRock01") != GunEmpty.get())
            {
                bomb[0] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalRock01");
                bomb[3] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalRock02");
            } else
            if(((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalBomb03") != GunEmpty.get())
            {
                bomb[0] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalBomb03");
                bomb[3] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalBomb04");
            } else
            if(((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalDev05") != GunEmpty.get())
            {
                bomb[0] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalDev05");
                bomb[3] = ((Aircraft)this.fm.actor).getBulletEmitterByHookName("_ExternalDev06");
            }
        }
        resetYPRmodifier();
        Cockpit.xyz[1] = cvt(this.fm.CT.getCockpitDoor(), 0.01F, 0.99F, 0.0F, 0.48F);
        Cockpit.xyz[2] = cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, 0.01F);
        Cockpit.ypr[2] = cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, -2F);
        this.mesh.chunkSetLocate("CanopyTop", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("EjectatorHandle", 0.0F, 0.0F, 2520F * this.fm.CT.getCockpitDoor());
        this.mesh.chunkSetAngles("CanopyFrameL", -cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, 2.0F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("CanopyFrameR", cvt(this.fm.CT.getCockpitDoor(), 0.1F, 0.99F, 0.0F, 2.0F), 0.0F, 0.0F);
        resetYPRmodifier();
        if(this.fm.CT.GearControl == 0.0F)
            Cockpit.xyz[2] = 0.02F;
        this.mesh.chunkSetLocate("FahrHandle", Cockpit.xyz, Cockpit.ypr);
        if((this.fm.AS.astateCockpitState & 0x20) == 0 && (this.fm.AS.astateCockpitState & 0x80) == 0)
        {
            this.mesh.chunkSetAngles("NeedleALT", -cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 13000F, 0.0F, 4680F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleALT1", -cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 13000F, 0.0F, 468F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleALTKm", 0.0F, 0.0F, cvt(Atmosphere.pressure((float)this.fm.Loc.z), 0.0F, 100000F, 0.0F, 180F));
        }
        this.mesh.chunkSetAngles("NeedleManPress", -cvt(this.fm.EI.engines[0].getManifoldPressure(), 0.6F, 1.8F, 0.0F, 336F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleKMH", -floatindex(cvt(Pitot.Indicator((float)this.fm.Loc.z, this.fm.getSpeedKMH()), 0.0F, 900F, 0.0F, 9F), speedometerScale), 0.0F, 0.0F);
        if((this.fm.AS.astateCockpitState & 0x40) == 0 && (this.fm.AS.astateCockpitState & 8) == 0 && (this.fm.AS.astateCockpitState & 0x10) == 0)
            this.mesh.chunkSetAngles("NeedleRPM", -floatindex(cvt(this.fm.EI.engines[0].getRPM(), 0.0F, 4000F, 0.0F, 8F), rpmScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleFuel", floatindex(cvt(this.fm.M.fuel / 0.92F, 0.0F, 400F, 0.0F, 4F), fuelScale), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleWaterTemp", cvt(this.fm.EI.engines[0].tWaterOut, 0.0F, 120F, 0.0F, 60F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleOilTemp", cvt(this.fm.EI.engines[0].tOilOut, 0.0F, 120F, 0.0F, 60F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleAirPress", cvt(this.fm.EI.engines[0].getRPM() / 1500F, 0.0F, 15F, 0.0F, 200F), 0.0F, 0.0F);
        if((this.fm.AS.astateCockpitState & 2) == 0 && (this.fm.AS.astateCockpitState & 1) == 0 && (this.fm.AS.astateCockpitState & 4) == 0)
        {
            this.mesh.chunkSetAngles("NeedleFuelPress", cvt(this.fm.EI.engines[0].getRPM() / 1500F, 0.0F, 3F, 0.0F, 135F), 0.0F, 0.0F);
            if(this.fm.getOverload() < 0.0F)
                this.mesh.chunkSetAngles("NeedleOilPress", -cvt(1.0F + 0.05F * this.fm.EI.engines[0].tOilOut + this.fm.EI.engines[0].getRPM() / 1300F + this.fm.getOverload() * 2.0F, 0.0F, 15F, 0.0F, 135F), 0.0F, 0.0F);
            else
                this.mesh.chunkSetAngles("NeedleOilPress", -cvt(1.0F + 0.05F * this.fm.EI.engines[0].tOilOut + this.fm.EI.engines[0].getRPM() / 1300F + this.fm.getOverload() / 2.0F, 0.0F, 15F, 0.0F, 135F), 0.0F, 0.0F);
            if(this.fm.EI.engines[0].getRPM() < 200F)
                this.mesh.chunkSetAngles("NeedleOilPress", -cvt(0.0F, 0.0F, 15F, 0.0F, 135F), 0.0F, 0.0F);
        }
        if((this.fm.AS.astateCockpitState & 0x40) == 0 && (this.fm.AS.astateCockpitState & 8) == 0 && (this.fm.AS.astateCockpitState & 0x10) == 0)
        {
            this.mesh.chunkSetAngles("NeedleAHTurn", 0.0F, cvt(setNew.turn, -0.23562F, 0.23562F, 25F, -25F), 0.0F);
            this.mesh.chunkSetAngles("NeedleAHBank", 0.0F, -cvt(getBall(6D), -6F, 6F, 11F, -11F), 0.0F);
            this.mesh.chunkSetAngles("NeedleAHCyl", 0.0F, 0.0F, this.fm.Or.getKren());
            this.mesh.chunkSetAngles("NeedleAHBar", 0.0F, 0.0F, cvt(this.fm.Or.getTangage(), -45F, 45F, 12F, -12F));
            this.mesh.chunkSetAngles("NeedleAirPress1", -floatindex(cvt(this.fm.getAltitude() / 1000F, 0.0F, 15F, 0.0F, 8F), altScale), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleAirTemp", cvt(Atmosphere.temperature((float)this.fm.Loc.z), 213.09F, 313.09F, -8.5F, 87F), 0.0F, 0.0F);
            this.mesh.chunkSetAngles("NeedleAirPress2", floatindex(cvt(this.fm.getAltitude() / 1000F, 0.0F, 2.5F, 0.0F, 8F), presScale), 0.0F, 0.0F);
        }
        if((this.fm.AS.astateCockpitState & 0x20) == 0 && (this.fm.AS.astateCockpitState & 0x80) == 0)
            this.mesh.chunkSetAngles("NeedleCD", setNew.vspeed < 0.0F ? floatindex(cvt(-setNew.vspeed, 0.0F, 30F, 0.0F, 6F), vsiNeedleScale) : -floatindex(cvt(setNew.vspeed, 0.0F, 30F, 0.0F, 6F), vsiNeedleScale), 0.0F, 0.0F);
        if((this.fm.AS.astateCockpitState & 0x40) == 0 && (this.fm.AS.astateCockpitState & 8) == 0 && (this.fm.AS.astateCockpitState & 0x10) == 0)
            if(useRealisticNavigationInstruments())
            {
                this.mesh.chunkSetAngles("RepeaterPlane", -setNew.azimuth.getDeg(f) + setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
                this.mesh.chunkSetAngles("RepeaterOuter", setNew.waypointAzimuth.getDeg(f), 0.0F, 0.0F);
            } else
            {
                this.mesh.chunkSetAngles("RepeaterOuter", setNew.azimuth.getDeg(f), 0.0F, 0.0F);
                this.mesh.chunkSetAngles("RepeaterPlane", -setNew.waypointAzimuth.getDeg(f * 0.1F), 0.0F, 0.0F);
            }
        if((this.fm.AS.astateCockpitState & 0x40) == 0 && (this.fm.AS.astateCockpitState & 8) == 0 && (this.fm.AS.astateCockpitState & 0x10) == 0)
            this.mesh.chunkSetAngles("NeedleTrimmung", this.fm.CT.getTrimElevatorControl() * 25F, 0.0F, 0.0F);
        resetYPRmodifier();
        if(gun[0] != null)
        {
            Cockpit.xyz[0] = cvt(gun[0].countBullets(), 0.0F, 55F, -0.018F, 0.0F);
            this.mesh.chunkVisible("XLampMK108", gun[0].haveBullets());
        }
        if(gun[3] != null)
        {
            Cockpit.xyz[0] = cvt(gun[3].countBullets(), 0.0F, 500F, -0.044F, 0.0F);
            this.mesh.chunkSetLocate("RC_MG17_L", Cockpit.xyz, Cockpit.ypr);
            this.mesh.chunkVisible("XLampMG17_1", gun[3].haveBullets());
        }
        if(gun[4] != null)
        {
            Cockpit.xyz[0] = cvt(gun[4].countBullets(), 0.0F, 500F, -0.044F, 0.0F);
            this.mesh.chunkSetLocate("RC_MG17_R", Cockpit.xyz, Cockpit.ypr);
            this.mesh.chunkVisible("XLampMG17_2", gun[4].haveBullets());
        }
        if(gun[1] != null)
        {
            Cockpit.xyz[0] = cvt(gun[1].countBullets(), 0.0F, 200F, -0.017F, 0.0F);
            this.mesh.chunkSetLocate("RC_MG151_L", Cockpit.xyz, Cockpit.ypr);
            this.mesh.chunkVisible("XLampMG151_1", gun[1].haveBullets());
        }
        if(gun[2] != null)
        {
            Cockpit.xyz[0] = cvt(gun[2].countBullets(), 0.0F, 200F, -0.017F, 0.0F);
            this.mesh.chunkSetLocate("RC_MG151_R", Cockpit.xyz, Cockpit.ypr);
            this.mesh.chunkVisible("XLampMG151_2", gun[2].haveBullets());
        }
        this.mesh.chunkSetAngles("IgnitionSwitch", 24F * (float)this.fm.EI.engines[0].getControlMagnetos(), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Revi16TinterEZ42", cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -45F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("EZ42Filter", cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -85F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("EZ42FLever", cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, -90F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("EZ42Range", 0.0F, 0.0F, 0.0F);
        this.mesh.chunkSetAngles("EZ42Size", cvt(interp(-setNew.k14wingspan / 106F, -setOld.k14wingspan / 106F, f), 0.0F, 1.0F, 36.5F, -57.2F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("Stick", 0.0F, (pictAiler = 0.85F * pictAiler + 0.15F * this.fm.CT.AileronControl) * 20F, (pictElev = 0.85F * pictElev + 0.15F * this.fm.CT.ElevatorControl) * 20F);
        resetYPRmodifier();
        if(this.fm.CT.saveWeaponControl[1])
            Cockpit.xyz[2] = -0.004F;
        this.mesh.chunkSetLocate("SecTrigger", Cockpit.xyz, Cockpit.ypr);
        float f1 = 0.0F;
        int l = 0;
        if(this.fm.CT.saveWeaponControl[0] || this.fm.CT.saveWeaponControl[1] || this.fm.CT.saveWeaponControl[3])
        {
            if(this.fm.CT.saveWeaponControl[0] || this.fm.CT.saveWeaponControl[1])
                f1 = 20F;
            tClap = Time.tickCounter() + World.Rnd().nextInt(500, 1000);
        }
        if(Time.tickCounter() < tClap)
            l = 1;
        this.mesh.chunkSetAngles("SecTrigger2", -(240F + f1) * (pictClap = 0.85F * pictClap + 0.15F * (float)l), 0.0F, 0.0F);
        this.mesh.chunkSetLocate("SecTrigger", Cockpit.xyz, Cockpit.ypr);
        Cockpit.ypr[0] = interp(setNew.throttle, setOld.throttle, f) * 34F * 0.91F;
        Cockpit.xyz[2] = Cockpit.ypr[0] <= 7F ? 0.0F : -0.006F;
        this.mesh.chunkSetLocate("ThrottleQuad", Cockpit.xyz, Cockpit.ypr);
        this.mesh.chunkSetAngles("RPedalBase", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F);
        this.mesh.chunkSetAngles("RPedalStrut", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F);
        this.mesh.chunkSetAngles("RPedal", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F - this.fm.CT.getBrake() * 15F);
        this.mesh.chunkSetAngles("LPedalBase", 0.0F, 0.0F, -this.fm.CT.getRudder() * 15F);
        this.mesh.chunkSetAngles("LPedalStrut", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F);
        this.mesh.chunkSetAngles("LPedal", 0.0F, 0.0F, this.fm.CT.getRudder() * 15F - this.fm.CT.getBrake() * 15F);
        this.mesh.chunkVisible("XLampTankSwitch", (float)this.fm.EI.engines[0].getStage() > 0.0F);
        this.mesh.chunkVisible("XLampFuelLow", this.fm.M.fuel < 43.2F);
        this.mesh.chunkVisible("XLampGearL_1", this.fm.CT.getGear() < 0.05F);
        this.mesh.chunkVisible("XLampGearL_2", this.fm.CT.getGear() > 0.95F && this.fm.Gears.lgear);
        this.mesh.chunkVisible("XLampGearR_1", this.fm.CT.getGear() < 0.05F);
        this.mesh.chunkVisible("XLampGearR_2", this.fm.CT.getGear() > 0.95F && this.fm.Gears.rgear);
        this.mesh.chunkVisible("XLampGearC_1", this.fm.CT.getFlap() < 0.1F);
        this.mesh.chunkVisible("XLampGearC_3", this.fm.CT.getFlap() > 0.1F && this.fm.CT.getFlap() < 0.5F);
        this.mesh.chunkVisible("XLampGearC_2", this.fm.CT.getFlap() > 0.9F);
        resetYPRmodifier();
        Cockpit.xyz[2] = this.fm.CT.GearControl >= 0.5F ? 0.0F : 0.02F;
        this.mesh.chunkSetAngles("NeedleNahe1", cvt(setNew.beaconDirection, -45F, 45F, 20F, -20F), 0.0F, 0.0F);
        this.mesh.chunkSetAngles("NeedleNahe2", cvt(setNew.beaconRange, 0.0F, 1.0F, -20F, 20F), 0.0F, 0.0F);
        this.mesh.chunkVisible("AFN2_RED", isOnBlindLandingMarker());
        if(this.fm.EI.engines[0].getControlAfterburner() && this.fm.getAltitude() < 9000F)
        {
            this.mesh.chunkSetAngles("SwitchMW50", -23F, 0.0F, 0.0F);
            this.mesh.chunkVisible("XLampMW50", true);
        } else
        {
            this.mesh.chunkSetAngles("SwitchMW50", 23F, 0.0F, 0.0F);
            this.mesh.chunkVisible("XLampMW50", false);
        }
        if(this.fm.EI.engines[0].getControlAfterburner() && this.fm.getAltitude() > 9000F)
            this.mesh.chunkSetAngles("SwitchGM1", -23F, 0.0F, 0.0F);
        else
            this.mesh.chunkSetAngles("SwitchGM1", 23F, 0.0F, 0.0F);
        if(this.fm.getAltitude() > 8990F)
            this.mesh.chunkVisible("XLampAlt", true);
        else
            this.mesh.chunkVisible("XLampAlt", false);
        float f2 = Atmosphere.temperature((float)this.fm.Loc.z) - 273.15F;
        if(f2 < -16F)
            this.mesh.chunkVisible("XLampPitot", true);
        else
        if(f2 > -12F)
            this.mesh.chunkVisible("XLampPitot", false);
    }

    public void toggleDim()
    {
        this.cockpitDimControl = !this.cockpitDimControl;
    }

    public void toggleLight()
    {
        this.cockpitLightControl = !this.cockpitLightControl;
        if(this.cockpitLightControl)
        {
            light1.light.setEmit(0.0012F, 0.75F);
            light2.light.setEmit(0.0012F, 0.75F);
            setNightMats(true);
        } else
        {
            light1.light.setEmit(0.0F, 0.0F);
            light2.light.setEmit(0.0F, 0.0F);
            setNightMats(false);
        }
    }

    private void retoggleLight()
    {
        if(this.cockpitLightControl)
        {
            setNightMats(false);
            setNightMats(true);
        } else
        {
            setNightMats(true);
            setNightMats(false);
        }
    }

    public void reflectCockpitState()
    {
        if((this.fm.AS.astateCockpitState & 2) != 0 || (this.fm.AS.astateCockpitState & 1) != 0 || (this.fm.AS.astateCockpitState & 4) != 0)
        {
            this.mesh.chunkVisible("EZ42", false);
            this.mesh.chunkVisible("Revi16TinterEZ42", false);
            this.mesh.chunkVisible("Z_Z_MASK", false);
            this.mesh.chunkVisible("Z_Z_RETICLEEZ42", false);
            this.mesh.chunkVisible("DEZ42", true);
            this.mesh.materialReplace("D9GP1", "DD9GP1");
            this.mesh.materialReplace("D9GP1_night", "DD9GP1_night");
            this.mesh.chunkVisible("NeedleManPress", false);
            this.mesh.chunkVisible("NeedleRPM", false);
            this.mesh.chunkVisible("RepeaterOuter", false);
            this.mesh.chunkVisible("RepeaterPlane", false);
        }
        if((this.fm.AS.astateCockpitState & 0x40) != 0 || (this.fm.AS.astateCockpitState & 8) != 0 || (this.fm.AS.astateCockpitState & 0x10) != 0)
        {
            this.mesh.chunkVisible("XGlassDamage2", true);
            this.mesh.chunkVisible("XGlassDamage4", true);
            this.mesh.materialReplace("D9GP2", "DD9GP2");
            this.mesh.materialReplace("D9GP2_night", "DD9GP2_night");
            this.mesh.chunkVisible("NeedleAHCyl", false);
            this.mesh.chunkVisible("NeedleAHBank", false);
            this.mesh.chunkVisible("NeedleAHBar", false);
            this.mesh.chunkVisible("NeedleAHTurn", false);
            this.mesh.chunkVisible("NeedleFuelPress", false);
            this.mesh.chunkVisible("NeedleOilPress", false);
            this.mesh.materialReplace("Ta152GP4_MW50", "DTa152GP4");
            this.mesh.materialReplace("Ta152GP4_MW50_night", "DTa152GP4_night");
            this.mesh.chunkVisible("NeedleFuel", false);
            resetYPRmodifier();
            Cockpit.xyz[0] = -0.001F;
            Cockpit.xyz[1] = 0.008F;
            Cockpit.xyz[2] = 0.025F;
            Cockpit.ypr[0] = 3F;
            Cockpit.ypr[1] = -6F;
            Cockpit.ypr[2] = 1.5F;
            this.mesh.chunkSetLocate("IPCentral", Cockpit.xyz, Cockpit.ypr);
        }
        if((this.fm.AS.astateCockpitState & 0x20) != 0 || (this.fm.AS.astateCockpitState & 0x80) != 0)
        {
            this.mesh.chunkVisible("XGlassDamage3", true);
            this.mesh.materialReplace("Ta152GP3", "DTa152GP3");
            this.mesh.materialReplace("Ta152GP3_night", "DTa152GP3_night");
            this.mesh.chunkVisible("NeedleKMH", false);
            this.mesh.chunkVisible("NeedleCD", false);
            this.mesh.chunkVisible("NeedleAlt", false);
            this.mesh.chunkVisible("NeedleAltKM", false);
            this.mesh.materialReplace("Ta152Trans2", "DTa152Trans2");
            this.mesh.materialReplace("Ta152Trans2_night", "DTa152Trans2_night");
            this.mesh.chunkVisible("NeedleWaterTemp", false);
            this.mesh.chunkVisible("NeedleOilTemp", false);
        }
        retoggleLight();
    }

    protected void reflectPlaneMats()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat);
    }

    private Gun gun[];
    private Variables setOld;
    private Variables setNew;
    private Variables setTmp;
    private LightPointActor light1;
    private LightPointActor light2;
    private boolean bNeedSetUp;
    private BulletEmitter bomb[];
    private float pictAiler;
    private float pictElev;
    public Vector3f w;
    private static final float speedometerScale[] = {
        0.0F, 18.5F, 67F, 117F, 164F, 215F, 267F, 320F, 379F, 427F, 
        428F
    };
    private static final float rpmScale[] = {
        0.0F, 11.25F, 53F, 108F, 170F, 229F, 282F, 334F, 342.5F, 342.5F
    };
    private static final float fuelScale[] = {
        0.0F, 16F, 35F, 52.5F, 72F, 72F
    };
    private static final float altScale[] = {
        0.0F, 40F, 80F, 123F, 148F, 183F, 210F
    };
    private static final float presScale[] = {
        0.0F, 10F, 40F, 60F, 77F
    };
    private static final float vsiNeedleScale[] = {
        0.0F, 48F, 82F, 96.5F, 111F, 120.5F, 130F, 130F
    };
    private static final float k14TargetMarkScale[] = {
        0.0F, -0.2377F, -5.7991F, -21.9606F, -35.46F, -44.539F, -48.5318F, -84.2771F, -96.9686F, -106F
    };
    private static final float k14TargetWingspanScale[] = {
        11.23F, 11.28F, 12.45F, 15.85F, 18.69F, 20.6F, 21.44F, 28.96F, 31.65F, 33.53F
    };
    private static final float k14BulletDrop[] = {
        5.812F, 6.168F, 6.508F, 6.978F, 7.24F, 7.576F, 7.849F, 8.108F, 8.473F, 8.699F, 
        8.911F, 9.111F, 9.384F, 9.554F, 9.787F, 9.928F, 9.992F, 10.282F, 10.381F, 10.513F, 
        10.603F, 10.704F, 10.739F, 10.782F, 10.789F
    };
    private int tClap;
    private float pictClap;

    static 
    {
        Property.set(CockpitTA_152H6.class, "normZN", 0.74F);
        Property.set(CockpitTA_152H6.class, "gsZN", 0.66F);
    }

}
