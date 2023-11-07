// Source File Name: CockpitMig_17PF.java
// Author:           
// Last Modified by: Storebror 2011-06-01
package com.maddox.il2.objects.air;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.AnglesFork;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.objects.weapons.Gun;
import com.maddox.rts.Time;
import com.maddox.sound.ReverbFXRoom;

public class CockpitMig_17PF extends CockpitPilot
{
    private Variables setOld = new Variables();
    private Variables setNew = new Variables();
    private Variables setTmp;
    public Vector3f w = new Vector3f();
    private float pictAiler = 0.0F;
    private float pictElev = 0.0F;
    private float pictGear = 0.0F;
    private float pictMet1 = 0.0F;

    private static final float[] rpmScale
	= { 0.0F, 8.0F, 23.5F, 40.0F, 58.5F, 81.0F, 104.5F, 130.2F, 158.5F,
	    187.0F, 217.5F, 251.1F, 281.5F, 289.5F, 295.5F };
    private static final float[] k14TargetMarkScale
	= { -0.0F, -4.5F, -27.5F, -42.5F, -56.5F, -61.5F, -70.0F, -95.0F,
	    -102.5F, -106.0F };
    private static final float[] k14TargetWingspanScale
	= { 11.0F, 11.3F, 11.8F, 15.0F, 20.0F, 25.0F, 30.0F, 35.0F, 40.0F,
	    43.05F };
    private static final float[] k14BulletDrop
	= { 5.812F, 6.168F, 6.508F, 6.978F, 7.24F, 7.576F, 7.849F, 8.108F,
	    8.473F, 8.699F, 8.911F, 9.111F, 9.384F, 9.554F, 9.787F, 9.928F,
	    9.992F, 10.282F, 10.381F, 10.513F, 10.603F, 10.704F, 10.739F,
	    10.782F, 10.789F };
    private Gun gun[] = {
	        null, null, null
	    };
    
    private class Variables
    {
	float throttle;
    float dimPosition;
	float vspeed;
	float altimeter;
    AnglesFork azimuth;
    AnglesFork waypointAzimuth;
    float beaconDirection;
    float beaconRange;
	float k14wingspan;
	float k14mode;
	float k14x;
	float k14y;
	float k14w;
	float stbyPosition;
	float stbyPosition2;
	float stbyPosition3;
	
	private Variables() {
        azimuth = new AnglesFork();
        waypointAzimuth = new AnglesFork();
	}
    }
    
    class Interpolater extends InterpolateRef
    {
	public boolean tick() {
	    if (fm != null) {
		setTmp = setOld;
		setOld = setNew;
		setNew = setTmp;
		setNew.throttle
		    = 0.9F * setOld.throttle + fm.CT.PowerControl * 0.1F;
		setNew.altimeter = fm.getAltitude();
		if(cockpitDimControl)
        {
            if(setNew.dimPosition > 0.0F)
                setNew.dimPosition = setOld.dimPosition - 0.05F;
        } else
        if(setNew.dimPosition < 1.0F)
            setNew.dimPosition = setOld.dimPosition + 0.05F;
        float a = waypointAzimuth();
        if(useRealisticNavigationInstruments())
        {
            setNew.waypointAzimuth.setDeg(a - 90F);
            setOld.waypointAzimuth.setDeg(a - 90F);
        } else
        {
            setNew.waypointAzimuth.setDeg(setOld.waypointAzimuth.getDeg(0.1F), a - setOld.azimuth.getDeg(1.0F));
        }
        setNew.azimuth.setDeg(setOld.azimuth.getDeg(1.0F), fm.Or.azimut());
        setNew.beaconDirection = (10F * setOld.beaconDirection + getBeaconDirection()) / 11F;
        setNew.beaconRange = (10F * setOld.beaconRange + getBeaconRange()) / 11F;
		setNew.vspeed
		    = (199.0F * setOld.vspeed + fm.getVertSpeed()) / 200.0F;
        if(fm.EI.engines[0].getStage() < 6)
        {
            if(setNew.stbyPosition2 > 0.0F)
                setNew.stbyPosition2 = setOld.stbyPosition2 - 0.005F;
            if(setNew.stbyPosition > 0.0F)
                setNew.stbyPosition = setOld.stbyPosition - 0.025F;
        } else
        {
        if(setNew.stbyPosition2 < 1.0F)
            setNew.stbyPosition2 = setOld.stbyPosition2 + 0.005F;
        if(setNew.stbyPosition < 1.0F)
            setNew.stbyPosition = setOld.stbyPosition + 0.025F;
        }
        if (fm.EI.engines[0].getStage() < 6
                && !fm.CT.bHasAileronControl && !fm.CT.bHasElevatorControl
                && !fm.CT.bHasGearControl && !fm.CT.bHasAirBrakeControl
                && !fm.CT.bHasFlapsControl) {
          if (setNew.stbyPosition3 > 0.0F) {
            setNew.stbyPosition3 = setOld.stbyPosition3 - 0.0050F;
          }
        } else if (setNew.stbyPosition3 < 1.0F) {
          setNew.stbyPosition3 = setOld.stbyPosition3 + 0.0050F;
        }
		float f = (((Mig_17) (Object) CockpitMig_17PF.this.aircraft())
			   .k14Distance);
		setNew.k14w
		    = 5.0F * (CockpitMig_17PF.k14TargetWingspanScale
			      [(((Mig_17) (Object) CockpitMig_17PF.this.aircraft())
				.k14WingspanType)]) / f;
		setNew.k14w = 0.9F * setOld.k14w + 0.1F * setNew.k14w;
		setNew.k14wingspan
		    = (0.9F * setOld.k14wingspan
		       + 0.1F * (CockpitMig_17PF.k14TargetMarkScale
				 [(((Mig_17) (Object)
				    CockpitMig_17PF.this.aircraft())
				   .k14WingspanType)]));
		setNew.k14mode
		    = (0.8F * setOld.k14mode
		       + 0.2F * (float) (((Mig_17) (Object)
					  CockpitMig_17PF.this.aircraft())
					 .k14Mode));
		Vector3d vector3d = CockpitMig_17PF.this.aircraft().FM.getW();
		double d = 0.00125 * (double) f;
		float f_0_ = (float) Math.toDegrees(d * vector3d.z);
		float f_1_ = -(float) Math.toDegrees(d * vector3d.y);
		float f_2_
		    = (CockpitMig_17PF.this.floatindex((f - 200.0F) * 0.04F,
						    CockpitMig_17PF.k14BulletDrop)
		       - CockpitMig_17PF.k14BulletDrop[0]);
		f_1_ += (float) Math.toDegrees(Math.atan((double) (f_2_ / f)));
		setNew.k14x = 0.92F * setOld.k14x + 0.08F * f_0_;
		setNew.k14y = 0.92F * setOld.k14y + 0.08F * f_1_;
		if (setNew.k14x > 7.0F)
		    setNew.k14x = 7.0F;
		if (setNew.k14x < -7.0F)
		    setNew.k14x = -7.0F;
		if (setNew.k14y > 7.0F)
		    setNew.k14y = 7.0F;
		if (setNew.k14y < -7.0F)
		    setNew.k14y = -7.0F;
	    }
	    return true;
	}
    }
    
    protected boolean doFocusEnter() {
	    if (super.doFocusEnter()) {
	      aircraft().hierMesh().chunkVisible("Blister1_D0", false);
	      aircraft().hierMesh().chunkVisible("Head1_D0", false);
	      aircraft().hierMesh().chunkVisible("Seat_D0", false);
	      aircraft().hierMesh().chunkVisible("Cockpit_D0", false);
	      return true;
	    } else {
	      return false;
	    }
	  }

	  protected void doFocusLeave() {
	    if (!isFocused()) {
	      return;
	    } else {
	      aircraft().hierMesh().chunkVisible("Blister1_D0", true);
	      aircraft().hierMesh().chunkVisible("Head1_D0", true);
	      aircraft().hierMesh().chunkVisible("Seat_D0", true);
	      aircraft().hierMesh().chunkVisible("Cockpit_D0", true);
	      super.doFocusLeave();
	      return;
	    }
	  }
    

    public CockpitMig_17PF() {
	super("3DO/Cockpit/MiG-17PF/MiG_17PF.him", "bf109");
	cockpitNightMats
	    = new String[] { "Gauges_01", "Gauges_02", "Gauges_03",
			     "Gauges_04", "Gauges_05", "Gauges_06",
			     "Gauges_08", "MiG-15_Compass" };
	setNightMats(false);
	interpPut(new Interpolater(), null, Time.current(), null);
	if (acoustics != null)
	    acoustics.globFX = new ReverbFXRoom(0.45F);
    }
        
    protected float waypointAzimuth()
    {
        return waypointAzimuthInvertMinus(30F);
    }
    
	private float machNumber(){
		return ((Mig_17)super.aircraft()).calculateMach();
	}
        
	  public void reflectWorldToInstruments(float f) {
		    if (com.maddox.il2.objects.air.Mig_17.bChangedPit) {
		        com.maddox.il2.objects.air.Mig_17.bChangedPit = false;
		      }
	    if ((fm.AS.astateCockpitState & 0x2) == 0) {
	      int i = ((Mig_17) (Object) aircraft()).k14Mode;
	      boolean bool = i < 2;
	      mesh.chunkVisible("Z_Z_RETICLE", bool);
	      bool = i > 0;
	      mesh.chunkVisible("Z_Z_RETICLE1", bool);
	      mesh.chunkSetAngles("Z_Z_RETICLE1", 0.0F, setNew.k14x,
	              setNew.k14y);
	      resetYPRmodifier();
	      Cockpit.xyz[0] = setNew.k14w;
	      for (int i_4_ = 1; i_4_ < 7; i_4_++) {
	        mesh.chunkVisible("Z_Z_AIMMARK" + i_4_, bool);
	        mesh.chunkSetLocate("Z_Z_AIMMARK" + i_4_, Cockpit.xyz,
	                Cockpit.ypr);
	      }
	    }
	    if(gun[0] == null)
	    {
	        gun[0] = ((Aircraft)fm.actor).getGunByHookName("_CANNON01");
	        gun[1] = ((Aircraft)fm.actor).getGunByHookName("_CANNON02");
	        gun[2] = ((Aircraft)fm.actor).getGunByHookName("_CANNON03");
	    }
	    resetYPRmodifier();
	    Cockpit.xyz[1] = cvt(fm.CT.getCockpitDoor(), 0.01F, 0.99F, -0.49F, 0.0F);
	    Cockpit.xyz[2] = cvt(fm.CT.getCockpitDoor(), 0.01F, 0.99F, -0.065F, 0.0F);
	    Cockpit.ypr[2] = cvt(fm.CT.getCockpitDoor(), 0.99F, 0.01F, -3.0F, 0.0F);
	    mesh.chunkSetLocate("CanopyOpen01", Cockpit.xyz, Cockpit.ypr);
	    mesh.chunkSetLocate("CanopyOpen02", Cockpit.xyz, Cockpit.ypr);
	    mesh.chunkSetLocate("CanopyOpen03", Cockpit.xyz, Cockpit.ypr);
	    mesh.chunkSetLocate("CanopyOpen04", Cockpit.xyz, Cockpit.ypr);
	    mesh.chunkSetLocate("CanopyOpen05", Cockpit.xyz, Cockpit.ypr);
	    mesh.chunkSetLocate("CanopyOpen06", Cockpit.xyz, Cockpit.ypr);
	    mesh.chunkSetLocate("CanopyOpen07", Cockpit.xyz, Cockpit.ypr);
	    mesh.chunkSetLocate("CanopyOpen08", Cockpit.xyz, Cockpit.ypr);
	    mesh.chunkSetLocate("CanopyOpen09", Cockpit.xyz, Cockpit.ypr);
	    mesh.chunkSetLocate("XGlassDamage4", Cockpit.xyz, Cockpit.ypr);
	    mesh.chunkSetAngles("GearHandle", 0.0F, 0.0F,
	            50.0F * (pictGear = (0.82F * pictGear
	            + 0.18F * fm.CT.GearControl)));
	    mesh.chunkSetAngles("Z_FlapsLever", -35.0F * fm.CT.FlapsControl, 0.0F,
	            0.0F);
	    mesh.chunkSetAngles("Z_Gas1a", 0.0F,
	            cvt(fm.M.fuel / 2.0F, 0.0F, 700.0F, 0.0F, 180.0F),
	            0.0F);
	    mesh.chunkSetAngles("Z_Target1", 1.2F * setNew.k14wingspan, 0.0F,
	            0.0F);
	    mesh.chunkSetAngles("Z_Gunsight_Button", -10.0F * setNew.k14wingspan,
	            0.0F, 0.0F);
	    mesh.chunkSetAngles("Z_Gunsight_Mire", 0.0F, cvt(interp(setNew.dimPosition, setOld.dimPosition, f), 0.0F, 1.0F, 0.0F, 47.0F), 0.0F);
	    mesh.chunkSetAngles("Z_Amp", 0.0F, cvt(interp(setNew.stbyPosition2,
	            setOld.stbyPosition2, f),
	            0.0F, 1.0F, 0.0F, 40.0F), 0.0F);
	    mesh.chunkSetAngles("Z_HydroPressure", 0.0F,
	            cvt(interp(setNew.stbyPosition3,
	            setOld.stbyPosition3, f),
	            0.0F, 1.0F, 0.0F, 190.0F),
	            0.0F);
	    w.set(fm.getW());
	    fm.Or.transform(w);
	    mesh.chunkSetAngles("Z_Turn1a", 0.0F,
	            cvt(w.z, -0.23562F, 0.23562F, 30.0F, -30.0F),
	            0.0F);
	    mesh.chunkSetAngles("Z_Slide1a", 0.0F,
	            cvt(getBall(8.0), -8.0F, 8.0F, -24.0F, 24.0F),
	            0.0F);
	    mesh.chunkSetAngles("Z_Slide1a2", 0.0F,
	            cvt(getBall(8.0), -8.0F, 8.0F, -20.0F, 20.0F),
	            0.0F);

	    if (useRealisticNavigationInstruments()) {
	      mesh.chunkSetAngles("Z_Compass2", (90.0F + setNew.azimuth.getDeg(f * 0.1F)) + setNew.beaconDirection, 0.0F, 0.0F);
	    } else {
	      mesh.chunkSetAngles("Z_Compass2", 90.0F + setNew.azimuth.getDeg(f * 0.1F), 0.0F, 0.0F);
	    }
	    mesh.chunkSetAngles("Z_horizont1a", 0.0F, -fm.Or.getKren(), 0.0F);
	    mesh.chunkSetAngles("Z_GasPrs1a", 0.0F,
	            cvt((fm.M.fuel > 1.0F
	            ? cvt(fm.EI.engines[0].getRPM(), 0.0F,
	            3050.0F, 0.0F, 4.0F)
	            : 0.0F),
	            0.0F, 5.0F, -45.0F, 225.0F),
	            0.0F);
	    mesh.chunkSetAngles("Z_GasPrs2a", 0.0F,
	            cvt((fm.M.fuel > 1.0F
	            ? cvt(fm.EI.engines[0].getRPM(), 0.0F,
	            3050.0F, 0.0F, 4.0F)
	            : 0.0F),
	            0.0F, 5.0F, -180.0F, 0.0F),
	            0.0F);
	    mesh.chunkSetAngles("Z_TOilOut1a", 0.0F,
	            cvt(fm.EI.engines[0].tOilOut, 0.0F, 110.0F, 5.0F,
	            300.0F),
	            0.0F);
	    mesh.chunkSetAngles("Z_OilPrs1a", 0.0F,
	            cvt(1.0F + 0.05F * fm.EI.engines[0].tOilOut, 0.0F,
	            15.0F, -155.0F, -360.0F),
	            0.0F);
	    resetYPRmodifier();
	    Cockpit.xyz[1] = cvt(fm.CT.getRudder(), -1.0F, 1.0F, -0.035F, 0.035F);
	    mesh.chunkSetLocate("Pedal_L", Cockpit.xyz, Cockpit.ypr);
	    Cockpit.xyz[1] = -Cockpit.xyz[1];
	    mesh.chunkSetLocate("Pedal_R", Cockpit.xyz, Cockpit.ypr);
	    resetYPRmodifier();
	    Cockpit.xyz[2] = cvt(fm.Or.getTangage(), -45.0F, 45.0F, 0.0328F, -0.0328F);
	    mesh.chunkSetLocate("Z_horizont1b", Cockpit.xyz, Cockpit.ypr);
	    mesh.chunkSetAngles("Stick01", 0.0F, 0.0F,
	            10.0F * (pictElev = (0.85F * pictElev
	            + 0.15F * fm.CT.ElevatorControl)));
	    mesh.chunkSetAngles("Stick02", 0.0F,
	            10.0F * (pictAiler = (0.85F * pictAiler
	            + 0.15F * fm.CT.AileronControl)),
	            0.0F);
	    mesh.chunkSetAngles("Stick03", 0.0F,
	            10.0F * (pictAiler = (0.85F * pictAiler
	            + 0.15F * fm.CT.AileronControl)),
	            0.0F);
	    mesh.chunkSetAngles("Stick04", 0.0F,
	            10.0F * (pictAiler = (0.85F * pictAiler
	            + 0.15F * fm.CT.AileronControl)),
	            0.0F);
	    mesh.chunkSetAngles("Stick05", 0.0F,
	            10.0F * (pictAiler = (0.85F * pictAiler
	            + 0.15F * fm.CT.AileronControl)),
	            0.0F);
	    mesh.chunkSetAngles("Stick06", 0.0F,
	            10.0F * (pictAiler = (0.85F * pictAiler
	            + 0.15F * fm.CT.AileronControl)),
	            0.0F);
	    mesh.chunkSetAngles("Stick07", 0.0F,
	            10.0F * (pictAiler = (0.85F * pictAiler
	            + 0.15F * fm.CT.AileronControl)),
	            0.0F);
	    mesh.chunkSetAngles("Z_RPM1", cvt(fm.EI.engines[0].getRPM(), 0.0F,
	            3480.0F, 90.0F, 625.0F), 0.0F, 0.0F);
	    pictMet1 = (0.96F * pictMet1
	            + 0.04F * (0.6F * fm.EI.engines[0].getThrustOutput()
	            * fm.EI.engines[0].getControlThrottle()
	            * (fm.EI.engines[0].getStage() == 6 ? 1.0F
	            : 0.02F)));
	    mesh.chunkSetAngles("Z_FuelPress1",
	            cvt(fm.M.fuel > 1.0F ? 0.55F : 0.0F, 0.0F, 1.0F,
	            0.0F, 284.0F),
	            0.0F, 0.0F);
	    mesh.chunkSetAngles("Z_ExstT1", 0.0F,
	            cvt(fm.EI.engines[0].tWaterOut, 0.0F, 1200.0F,
	            0.0F, 112.0F),
	            0.0F);
	    mesh.chunkSetAngles("Z_Azimuth1", -setNew.azimuth.getDeg(f * 0.1F)+setNew.waypointAzimuth.getDeg(f * 0.1F), 0.0F, 0.0F);
	    mesh.chunkSetAngles("Z_Compass1", 0.0F, -setNew.azimuth.getDeg(f * 0.1F), 0.0F);

	    mesh.chunkSetAngles("Z_Oxypres1", 142.5F, 0.0F, 0.0F);
	    if (machNumber() < 0.95F || machNumber() > 1.0F) {
	      mesh.chunkSetAngles("Z_Alt_Km", 0.0F,
	              cvt(interp(setNew.altimeter, setOld.altimeter, f),
	              0.0F, 60000.0F, 0.0F, 2160.0F),
	              0.0F);
	      mesh.chunkSetAngles("Z_Alt_M", 0.0F,
	              cvt(interp(setNew.altimeter, setOld.altimeter, f),
	              0.0F, 60000.0F, 0.0F, 21600.0F),
	              0.0F);
	      mesh.chunkSetAngles("Z_Alt2_Km", 0.0F,
	              cvt(interp(setNew.altimeter, setOld.altimeter, f),
	              0.0F, 12000.0F, 225.0F, 495.0F),
	              0.0F);
	      mesh.chunkSetAngles("Z_Alt3_Km", 0.0F,
	              cvt(interp(setNew.altimeter, setOld.altimeter, f),
	              0.0F, 12000.0F, 295.0F, 395.0F),
	              0.0F);
	      mesh.chunkSetAngles("Z_Speed", 0.0F,
	              cvt(Pitot.Indicator((float) fm.Loc.z,
	              fm.getSpeedKMH()),
	              0.0F, 1200.0F, 0.0F, 360.0F),
	              0.0F);
	      mesh.chunkSetAngles("Z_AirFlow", 0.0F,
	              cvt(Pitot.Indicator((float) fm.Loc.z,
	              fm.getSpeedKMH()),
	              0.0F, 800.0F, -90.0F, 160.0F),
	              0.0F);
	      mesh.chunkSetAngles("Z_Climb", 0.0F,
	              cvt(setNew.vspeed, -30.0F, 30.0F, -180.0F, 180.0F),
	              0.0F);
	    }
	    mesh.chunkSetAngles("Z_Vibrations", 0.0F,
	            floatindex(cvt(fm.EI.engines[0].getRPM(), 0.0F,
	            4500.0F, 5.5F, 14.0F),
	            rpmScale),
	            0.0F);
	    mesh.chunkSetAngles("Z_Throttle",
	            cvt(fm.EI.engines[0].getRPM(), 0.0F, 3480.0F,
	            -110.0F, 170.0F),
	            0.0F, 0.0F);
	    mesh.chunkSetAngles("Z_Hour1", cvt(World.getTimeofDay(), 0.0F, 24.0F,
	            0.0F, 720.0F), 0.0F, 0.0F);
	    mesh.chunkSetAngles("Z_Minute1", cvt(World.getTimeofDay() % 1.0F, 0.0F,
	            1.0F, 0.0F, 360.0F), 0.0F, 0.0F);
	    mesh.chunkSetAngles("Z_Second1",
	            cvt(World.getTimeofDay() % 1.0F * 60.0F % 1.0F,
	            0.0F, 1.0F, 0.0F, 360.0F),
	            0.0F, 0.0F);
	    mesh.chunkVisible("FlareGearUp_R", (fm.CT.getGear() < 0.01F || !fm.Gears.rgear));
	    mesh.chunkVisible("FlareGearUp_L", (fm.CT.getGear() < 0.01F || !fm.Gears.lgear));
	    mesh.chunkVisible("FlareGearUp_C", fm.CT.getGear() < 0.01F);
	    mesh.chunkVisible("FlareGearDn_R", (fm.CT.getGear() > 0.99F && fm.Gears.rgear));
	    mesh.chunkVisible("FlareGearDn_L", (fm.CT.getGear() > 0.99F && fm.Gears.lgear));
	    mesh.chunkVisible("FlareGearDn_C", fm.CT.getGear() > 0.99F);
	    mesh.chunkVisible("FlareFuel", fm.M.fuel < 296.1F);
	    mesh.chunkVisible("FlareTankFuel", (fm.M.fuel < 1215.0F && fm.M.fuel > 1207.0F));
	    mesh.chunkVisible("FlareFire", fm.AS.astateEngineStates[0] > 2);
	    mesh.chunkVisible("FlareAirBrake", ((FlightModelMain) (super.fm)).CT.AirBrakeControl > 0.01F);
	    mesh.chunkVisible("FlareAltitude", super.fm.getAltitude() < 500F);
	    mesh.chunkVisible("FlareIgnition", fm.Gears.nOfGearsOnGr == 0 && fm.EI.engines[0].getStage() < 6);
	    if(gun[0] != null)
	    	mesh.chunkVisible("FlareN37", gun[0].haveBullets());
	    if(gun[1] != null)
	    	mesh.chunkVisible("FlareNS23a", gun[1].haveBullets());
	    if(gun[2] != null)
	    	mesh.chunkVisible("FlareNS23b", gun[2].haveBullets());
	    mesh.chunkVisible("FlareBattery", true);
	    mesh.chunkVisible("FlareGenerator", true);
	    resetYPRmodifier();
	    Cockpit.ypr[0] = interp(setNew.throttle, setOld.throttle, f) * 65.0F;
	    Cockpit.xyz[2] = cvt(Cockpit.ypr[0], 7.5F, 11.5F, 0.0F, 0.0F);
	    mesh.chunkSetLocate("Throttle", Cockpit.xyz, Cockpit.ypr);
	    if (fm.CT.getCockpitDoor() == 1.0F) {
	      mesh.chunkVisible("V_G", false);
	      mesh.chunkVisible("V_D", false);
	    } else {
	      mesh.chunkVisible("V_G", true);
	      mesh.chunkVisible("V_D", true);
	    }
	    if (fm.CT.getCockpitDoor() == 1.0F && !fm.AS.bIsAboutToBailout) {
	      mesh.chunkVisible("CanopyOpen07", true);
	    } else {
	      mesh.chunkVisible("CanopyOpen07", false);
	    }
	    if (fm.CT.getCockpitDoor() < 1.0F && fm.CT.bHasCockpitDoorControl) {
	      mesh.chunkVisible("CanopyOpen06", true);
	    } else {
	      mesh.chunkVisible("CanopyOpen06", false);
	    }
	    if (fm.AS.bIsAboutToBailout) {
	      mesh.chunkVisible("CanopyOpen01", false);
	      mesh.chunkVisible("CanopyOpen02", false);
	      mesh.chunkVisible("CanopyOpen03", false);
	      mesh.chunkVisible("CanopyOpen04", false);
	      mesh.chunkVisible("CanopyOpen05", false);
	      mesh.chunkVisible("CanopyOpen08", false);
	      mesh.chunkVisible("CanopyOpen09", false);
	      mesh.chunkVisible("XGlassDamage4", false);
	    }
	    if (fm.CT.BayDoorControl == 1.0F) {
	      mesh.chunkVisible("Stick04", false);
	      mesh.chunkVisible("Stick05", true);
	    }
	    if (fm.CT.BayDoorControl == 0.0F) {
	      mesh.chunkVisible("Stick04", true);
	      mesh.chunkVisible("Stick05", false);
	    }
	    mesh.chunkVisible("Z_Z_RETICLE", true);
	    mesh.chunkVisible("Z_Gunsight_Button2", false);
	    mesh.chunkVisible("Z_Gunsight_Button3", true);
	    if (((Mig_17) (Object) aircraft()).k14Mode == 2) {
	      mesh.chunkVisible("Z_Z_RETICLE", false);
	      mesh.chunkVisible("Z_Gunsight_Button2", true);
	      mesh.chunkVisible("Z_Gunsight_Button3", false);
	    }
	  }
    
    public void toggleDim()
    {
        cockpitDimControl = !cockpitDimControl;
    }
    
    public void reflectCockpitState() {
	if ((fm.AS.astateCockpitState & 0x2) != 0) {
	    mesh.chunkVisible("Instruments", false);
	    mesh.chunkVisible("InstrumentsD", true);
	    mesh.chunkVisible("Z_Z_RETICLE", false);
	    mesh.chunkVisible("Z_Z_MASK", false);
	    mesh.chunkVisible("XGlassDamage1", true);
	    mesh.chunkVisible("XGlassDamage2", true);
	    mesh.chunkVisible("XGlassDamage3", true);
	    mesh.chunkVisible("XGlassDamage4", true);
	    mesh.chunkVisible("Z_Speed", false);
	    mesh.chunkVisible("Z_Compass1", false);
	    mesh.chunkVisible("Z_Azimuth1", false);
	    mesh.chunkVisible("Z_GasPrs1a", false);
	    mesh.chunkVisible("Z_GasPrs2a", false);
	    mesh.chunkVisible("Z_Alt_Km", false);
	    mesh.chunkVisible("Z_Alt_M", false);
	    mesh.chunkVisible("Z_Turn", false);
	    mesh.chunkVisible("Z_Turn1a", false);
	    mesh.chunkVisible("Z_Slide1a", false);
	    mesh.chunkVisible("Z_RPM1", false);
	    mesh.chunkVisible("Z_Z_RETICLE1", false);
	    for (int i = 1; i < 7; i++)
		mesh.chunkVisible("Z_Z_AIMMARK" + i, false);
	}
	if ((fm.AS.astateCockpitState & 0x1) != 0) {
	    mesh.chunkVisible("XGlassDamage1", true);
	    mesh.chunkVisible("XGlassDamage2", true);
	}
	if (((fm.AS.astateCockpitState & 0x80) != 0
	     || (fm.AS.astateCockpitState & 0x40) != 0)
	    && (fm.AS.astateCockpitState & 0x4) != 0) {
	    mesh.chunkVisible("XGlassDamage1", true);
	    mesh.chunkVisible("XGlassDamage2", true);
	    mesh.chunkVisible("XGlassDamage3", true);
	    mesh.chunkVisible("XGlassDamage4", true);
	}
	if (((fm.AS.astateCockpitState & 0x8) != 0
	     || (fm.AS.astateCockpitState & 0x10) != 0)
	    && (fm.AS.astateCockpitState & 0x20) != 0)
	    retoggleLight();
    }
    
    public void toggleLight() {
	cockpitLightControl = !cockpitLightControl;
	if (cockpitLightControl)
	    setNightMats(true);
	else
	    setNightMats(false);
    }
    
    private void retoggleLight() {
	if (cockpitLightControl) {
	    setNightMats(false);
	    setNightMats(true);
	} else {
	    setNightMats(true);
	    setNightMats(false);
	}
    }
}
