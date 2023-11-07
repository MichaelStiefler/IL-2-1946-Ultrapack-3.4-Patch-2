package com.maddox.il2.objects.air;

import com.maddox.JGP.Tuple3d;
import com.maddox.JGP.Tuple3f;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.LightPoint;
import com.maddox.il2.engine.LightPointActor;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.sounds.SndAircraft;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class CockpitRWD_14_Bombardier extends CockpitPilot
{
    private class Variables
    {

        float altimeter;
        float azimuth;
        float mix;
        float throttle;
        float turn;
        float power;
        float fuelpressure;
        float vspeed;

        private Variables()
        {
        }

    }

    class Interpolater extends InterpolateRef
    {

        public boolean tick()
        {
            if(bNeedSetUp)
            {
                reflectPlaneMats();
                bNeedSetUp = false;
            }
            if(RWD_14.bChangedPit)
            {
                reflectPlaneToModel();
                RWD_14.bChangedPit = false;
            }
            setTmp = setOld;
            setOld = setNew;
            setNew = setTmp;
            setNew.altimeter = fm.getAltitude();
            if(Math.abs(((FlightModelMain) (fm)).Or.getKren()) < 30F)
                setNew.azimuth = (35F * setOld.azimuth + ((FlightModelMain) (fm)).Or.azimut()) / 36F;
            if(setOld.azimuth > 270F && setNew.azimuth < 90F)
                setOld.azimuth -= 360F;
            if(setOld.azimuth < 90F && setNew.azimuth > 270F)
                setOld.azimuth += 360F;
            setNew.mix = (10F * setOld.mix + ((FlightModelMain) (fm)).EI.engines[0].getControlMix()) / 11F;
            setNew.throttle = (10F * setOld.throttle + ((FlightModelMain) (fm)).CT.PowerControl) / 11F;
            w.set(fm.getW());
            ((FlightModelMain) (fm)).Or.transform(w);
            setNew.turn = (33F * setOld.turn + ((Tuple3f) (w)).z) / 34F;
            setNew.power = 0.85F * setOld.power + ((FlightModelMain) (fm)).EI.engines[0].getPowerOutput() * 0.15F;
            setNew.fuelpressure = 0.9F * setOld.fuelpressure + (((FlightModelMain) (fm)).M.fuel <= 1.0F || ((FlightModelMain) (fm)).EI.engines[0].getStage() != 6 ? 0.0F : 0.026F * (10F + (float)Math.sqrt(setNew.power))) * 0.1F;
            setNew.vspeed = (199F * setOld.vspeed + fm.getVertSpeed()) / 200F;
            mesh.chunkSetAngles("Turret1A", 0.0F, -((SndAircraft) (aircraft())).FM.turret[0].tu[0], 0.0F);
            mesh.chunkSetAngles("Turret1B", 0.0F, ((SndAircraft) (aircraft())).FM.turret[0].tu[1], 0.0F);
            return true;
        }

        Interpolater()
        {
        }
    }


    public CockpitRWD_14_Bombardier()
    {
        super("3DO/Cockpit/RWD-14/hierbomb.him", "i16");
        setOld = new Variables();
        setNew = new Variables();
        w = new Vector3f();
        bNeedSetUp = true;
        pictAiler = 0.0F;
        pictElev = 0.0F;
        HookNamed hooknamed = new HookNamed(super.mesh, "LAMPHOOK1");
        Loc loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        hooknamed.computePos(this, new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F), loc);
        light1 = new LightPointActor(new LightPoint(), loc.getPoint());
        light1.light.setColor(0.8980392F, 0.8117647F, 0.9235294F);
        light1.light.setEmit(0.0F, 0.0F);
        super.pos.base().draw.lightMap().put("LAMPHOOK1", light1);
        hooknamed = new HookNamed(super.mesh, "LAMPHOOK2");
        loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        hooknamed.computePos(this, new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F), loc);
        light2 = new LightPointActor(new LightPoint(), loc.getPoint());
        light2.light.setColor(0.8980392F, 0.8117647F, 0.9235294F);
        light2.light.setEmit(0.0F, 0.0F);
        super.pos.base().draw.lightMap().put("LAMPHOOK2", light2);
        hooknamed = new HookNamed(super.mesh, "LAMPHOOK3");
        loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        hooknamed.computePos(this, new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F), loc);
        light3 = new LightPointActor(new LightPoint(), loc.getPoint());
        light3.light.setColor(0.8980392F, 0.8117647F, 0.9235294F);
        light3.light.setEmit(0.0F, 0.0F);
        super.pos.base().draw.lightMap().put("LAMPHOOK3", light3);
        hooknamed = new HookNamed(super.mesh, "LAMPHOOK4");
        loc = new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F);
        hooknamed.computePos(this, new Loc(0.0D, 0.0D, 0.0D, 0.0F, 0.0F, 0.0F), loc);
        light4 = new LightPointActor(new LightPoint(), loc.getPoint());
        light4.light.setColor(0.8980392F, 0.8117647F, 0.9235294F);
        light4.light.setEmit(0.0F, 0.0F);
        super.pos.base().draw.lightMap().put("LAMPHOOK4", light4);
        super.cockpitNightMats = (new String[] {
            "compass", "dials1", "dials2", "dials3", "gauges", "compass1"
        });
        setNightMats(false);
        interpPut(new Interpolater(), null, Time.current(), null);
    }

    public void reflectWorldToInstruments(float f)
    {
        if(bNeedSetUp)
        {
            reflectPlaneMats();
            bNeedSetUp = false;
        }
        super.mesh.chunkSetAngles("xAlt", 0.0F, cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 7000F, 0.0F, 360F), 0.0F);
        super.mesh.chunkSetAngles("xAlt1", 0.0F, cvt(interp(setNew.altimeter, setOld.altimeter, f), 0.0F, 7000F, 0.0F, 360F), 0.0F);
        super.mesh.chunkSetAngles("xSpeed", 0.0F, floatindex(cvt(Pitot.Indicator((float)((Tuple3d) (((FlightModelMain) (super.fm)).Loc)).z, super.fm.getSpeedKMH()), 0.0F, 400F, 0.0F, 20F), speedometerScale), 0.0F);
        super.mesh.chunkSetAngles("xCompass", 0.0F, 90F + interp(-setNew.azimuth, -setOld.azimuth, f), 0.0F);
        super.mesh.chunkSetAngles("xCompass1", 0.0F, 90F + interp(-setNew.azimuth, -setOld.azimuth, f), 0.0F);
        super.mesh.chunkSetAngles("xFuelPrs", 0.0F, cvt(setNew.fuelpressure, 0.0F, 0.6F, 0.0F, 270F), 0.0F);
        super.mesh.chunkSetAngles("xFuel", 0.0F, floatindex(cvt(((FlightModelMain) (super.fm)).M.fuel, 0.0F, 250F, 0.0F, 18F), fuelScale), 0.0F);
        super.mesh.chunkSetAngles("xMinute", 0.0F, cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F);
        super.mesh.chunkSetAngles("xHour", 0.0F, cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F);
        super.mesh.chunkSetAngles("xMinute1", 0.0F, cvt(World.getTimeofDay() % 1.0F, 0.0F, 1.0F, 0.0F, 360F), 0.0F);
        super.mesh.chunkSetAngles("xHour1", 0.0F, cvt(World.getTimeofDay(), 0.0F, 24F, 0.0F, 720F), 0.0F);    
        super.mesh.chunkSetAngles("xOilPrs", 0.0F, cvt(1.0F + 0.05F * ((FlightModelMain) (super.fm)).EI.engines[0].tOilOut, 0.0F, 10F, 0.0F, 268F), 0.0F);
        super.mesh.chunkSetAngles("xOilOut", 0.0F, floatindex(cvt(((FlightModelMain) (super.fm)).EI.engines[0].tOilOut, 0.0F, 140F, 0.0F, 7F), oilTempScale), 0.0F);
        resetYPRmodifier();
        Cockpit.xyz[0] = cvt(((FlightModelMain) (super.fm)).Or.getTangage(), -20F, 20F, 0.057F, -0.057F);
        super.mesh.chunkSetLocate("xPitch", Cockpit.xyz, Cockpit.ypr);
        super.mesh.chunkSetAngles("xRPM", 0.0F, floatindex(cvt(((FlightModelMain) (super.fm)).EI.engines[0].getRPM(), 0.0F, 2400F, 0.0F, 12F), rpmScale), 0.0F);
        super.mesh.chunkSetAngles("xTurn", 0.0F, cvt(setNew.turn, -0.7F, 0.7F, -1.5F, 1.5F), 0.0F);
        super.mesh.chunkSetAngles("Stick1", 0.0F, 15F * (pictAiler = 0.85F * pictAiler + 0.15F * ((FlightModelMain) (super.fm)).CT.AileronControl), 15F * (pictElev = 0.85F * pictElev + 0.15F * ((FlightModelMain) (super.fm)).CT.ElevatorControl));
        super.mesh.chunkSetAngles("Rudder1", 15F * ((FlightModelMain) (super.fm)).CT.getRudder(), 0.0F, 0.0F);
        super.mesh.chunkSetAngles("Throttle1", 0.0F, 0.0F, 0.0F - 45F * interp(setNew.throttle, setOld.throttle, f));
        super.mesh.chunkSetAngles("xVspeed", 0.0F, cvt(setNew.vspeed, -10F, 10F, -180F, 180F), 0.0F);
    }

    protected boolean doFocusEnter()
    {
        if(super.doFocusEnter())
        {
            aircraft().hierMesh().chunkVisible("Cockpit", false);            
            return true;
        } else
        {
            return false;
        }
    }

    protected void doFocusLeave()
    {
        if(Actor.isAlive(aircraft()))
            aircraft().hierMesh().chunkVisible("Cockpit", true);
        super.doFocusLeave();
    }

    protected void reflectPlaneToModel()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        super.mesh.chunkVisible("CF_D0", hiermesh.isChunkVisible("CF_D0"));
        super.mesh.chunkVisible("CF_D1", hiermesh.isChunkVisible("CF_D1"));
        super.mesh.chunkVisible("CF_D2", hiermesh.isChunkVisible("CF_D2"));
        super.mesh.chunkVisible("Tail1_D0", hiermesh.isChunkVisible("Tail1_D0"));
        super.mesh.chunkVisible("Tail1_D1", hiermesh.isChunkVisible("Tail1_D1"));
        super.mesh.chunkVisible("Tail1_D2", hiermesh.isChunkVisible("Tail1_D2"));
        super.mesh.chunkVisible("WingLIn_D0", hiermesh.isChunkVisible("WingLIn_D0"));
        super.mesh.chunkVisible("WingLIn_D1", hiermesh.isChunkVisible("WingLIn_D1"));
        super.mesh.chunkVisible("WingLIn_D2", hiermesh.isChunkVisible("WingLIn_D2"));
        super.mesh.chunkVisible("WingRIn_D0", hiermesh.isChunkVisible("WingRIn_D0"));
        super.mesh.chunkVisible("WingRIn_D1", hiermesh.isChunkVisible("WingRIn_D1"));
        super.mesh.chunkVisible("WingRIn_D2", hiermesh.isChunkVisible("WingRIn_D2"));
        super.mesh.chunkVisible("Pilot1_D0", hiermesh.isChunkVisible("Pilot1_D0"));
        super.mesh.chunkVisible("Head1_D0", hiermesh.isChunkVisible("Head1_D0"));
        super.mesh.chunkVisible("Pilot1_D1", hiermesh.isChunkVisible("Pilot1_D1"));       
    }

    protected void reflectPlaneMats()
    {
        HierMesh hiermesh = aircraft().hierMesh();
        com.maddox.il2.engine.Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        super.mesh.materialReplace("Gloss1D0o", mat);
        mat = hiermesh.material(hiermesh.materialFind("Gloss1D1o"));
        super.mesh.materialReplace("Gloss1D1o", mat);
        mat = hiermesh.material(hiermesh.materialFind("Gloss2D2o"));
        super.mesh.materialReplace("Gloss2D2o", mat);
        mat = hiermesh.material(hiermesh.materialFind("Matt1D0o"));
        super.mesh.materialReplace("Matt1D0o", mat);
    }

    public void toggleLight()
    {
        super.cockpitLightControl = !super.cockpitLightControl;
        if(super.cockpitLightControl)
        {
            light1.light.setEmit(0.2F, 0.2F);
            light2.light.setEmit(0.2F, 0.2F);
            light3.light.setEmit(0.2F, 0.2F);
            light4.light.setEmit(0.2F, 0.2F);
            setNightMats(true);
        } else
        {
            light1.light.setEmit(0.0F, 0.0F);
            light2.light.setEmit(0.0F, 0.0F);
            light3.light.setEmit(0.0F, 0.0F);
            light4.light.setEmit(0.0F, 0.0F);
            setNightMats(false);
        }
    }

    private Variables setOld;
    private Variables setNew;
    private Variables setTmp;
    private LightPointActor light1;
    private LightPointActor light2;
    private LightPointActor light3;
    private LightPointActor light4;
    private Vector3f w;
    private boolean bNeedSetUp;
    private float pictAiler;
    private float pictElev;
    
    private static final float speedometerScale[] = {
        0.0F, 3F, 6F, 9F, 13F, 28F, 41.5F, 54F, 71.5F, 90F, 
        112.5F, 135F, 156F, 180F, 203.5F, 224.5F, 247F, 270F, 289.5F, 313.5F, 
        336F
    };
    private static final float rpmScale[] = {
        0.0F, 15F, 30F, 60F, 90F, 120F, 150F, 180F, 210F, 240F, 
        270F, 300F, 330F
    };
    private static final float fuelScale[] = {
        0.0F, 15F, 30F, 44F, 56F, 70F, 83F, 95F, 106F, 124F, 
        141F, 158F, 175F, 192F, 210F, 230F, 247F, 268F, 288F
    };
    private static final float oilTempScale[] = {
        0.0F, 20F, 40F, 80F, 120F, 160F, 240F, 320F
    };

    static 
    {
        Property.set(CockpitRWD_14_Bombardier.class, "astatePilotIndx", 0);
    }
}
