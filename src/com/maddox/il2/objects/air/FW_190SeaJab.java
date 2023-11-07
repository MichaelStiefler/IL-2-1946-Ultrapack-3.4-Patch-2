package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class FW_190SeaJab extends FW_190Sea
    implements TypeX4Carrier
{

    public FW_190SeaJab()
    {
        bToFire = false;
        tX4Prev = 0L;
        deltaAzimuth = 0.0F;
        deltaTangage = 0.0F;
    }

    public void rareAction(float f, boolean bool)
    {
        super.rareAction(f, bool);
        if((!(this.FM instanceof RealFlightModel) || !((RealFlightModel)this.FM).isRealMode()) && bool && (this.FM instanceof Pilot))
        {
            Pilot pilot = (Pilot)this.FM;
            if(pilot.get_maneuver() == 63 && ((Maneuver) (pilot)).target != null)
            {
                Point3d point3d = new Point3d(((FlightModelMain) (((Maneuver) (pilot)).target)).Loc);
                point3d.sub(this.FM.Loc);
                this.FM.Or.transformInv(point3d);
                if((point3d.x > 4000D && point3d.x < 5500D || point3d.x > 100D && point3d.x < 5000D && World.Rnd().nextFloat() < 0.33F) && Time.current() > tX4Prev + 10000L)
                {
                    bToFire = true;
                    tX4Prev = Time.current();
                }
            }
        }
    }

    public void typeX4CAdjSidePlus()
    {
        deltaAzimuth = 1.0F;
    }

    public void typeX4CAdjSideMinus()
    {
        deltaAzimuth = -1F;
    }

    public void typeX4CAdjAttitudePlus()
    {
        deltaTangage = 1.0F;
    }

    public void typeX4CAdjAttitudeMinus()
    {
        deltaTangage = -1F;
    }

    public void typeX4CResetControls()
    {
        deltaAzimuth = deltaTangage = 0.0F;
    }

    public float typeX4CgetdeltaAzimuth()
    {
        return deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage()
    {
        return deltaTangage;
    }

    public boolean bToFire;
    private long tX4Prev;
    private float deltaAzimuth;
    private float deltaTangage;

    static 
    {
        Class var_class = FW_190SeaJab.class;
        new NetAircraft.SPAWN(var_class);
        Property.set(var_class, "iconFar_shortClassName", "FW190");
        Property.set(var_class, "meshName", "3DO/Plane/Fw-190F-9T/hier.him");
        Property.set(var_class, "PaintScheme", new PaintSchemeBMPar03());
        Property.set(var_class, "yearService", 1944F);
        Property.set(var_class, "yearExpired", 1948F);
        Property.set(var_class, "FlightModel", "FlightModels/Fw-190F-9N (Ultrapack).fmd");
        Property.set(var_class, "cockpitClass", new Class[] { CockpitFW_190F8T.class });
        Property.set(var_class, "LOSElevation", 0.764106F);
        Aircraft.weaponTriggersRegister(var_class, new int[] {
            0, 0, 1, 1, 3, 3, 3, 3, 3, 3, 
            3, 3, 3, 9, 9, 9, 2, 2, 2, 2, 
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
            9, 9, 9, 9, 9, 9, 2, 2, 1, 1, 
            1, 1, 9, 9, 2, 2, 2, 2, 2, 2, 
            2, 2, 9, 9, 1, 1, 1, 1, 9, 9, 
            1, 1, 9, 9, 3, 9, 1, 3, 3, 3, 
            9, 9
        });
        Aircraft.weaponHooksRegister(var_class, new String[] {
            "_MGUN01", "_MGUN02", "_CANNON01", "_CANNON02", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb13", "_ExternalBomb06", 
            "_ExternalBomb07", "_ExternalBomb08", "_ExternalBomb09", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", 
            "_ExternalRock05", "_ExternalRock06", "_ExternalRock07", "_ExternalRock08", "_ExternalRock09", "_ExternalRock10", "_ExternalRock11", "_ExternalRock12", "_ExternalRock13", "_ExternalRock14", 
            "_ExternalRock15", "_ExternalRock16", "_ExternalRock17", "_ExternalRock18", "_ExternalRock19", "_ExternalRock20", "_ExternalRock21", "_ExternalRock22", "_ExternalRock23", "_ExternalRock24", 
            "_ExternalDev04", "_ExternalDev05", "_ExternalDev06", "_ExternalDev07", "_ExternalDev08", "_ExternalDev09", "_ExternalRock25", "_ExternalRock26", "_CANNON01", "_CANNON02", 
            "_CANNON03", "_CANNON04", "_ExternalDev10", "_ExternalDev11", "_ExternalRock27", "_ExternalRock28", "_ExternalRock29", "_ExternalRock30", "_ExternalRock31", "_ExternalRock31", 
            "_ExternalRock32", "_ExternalRock32", "_ExternalDev12", "_ExternalDev13", "_CANNON05", "_CANNON06", "_CANNON07", "_CANNON08", "_ExternalDev14", "_ExternalDev15", 
            "_CANNON09", "_CANNON10", "_ExternalDev16", "_ExternalDev17", "_ExternalBomb10", "_ExternalDev18", "_CANNON11", "_ExternalBomb11", "_ExternalBomb12", "_ExternalBomb05", 
            "_ExternalDev19", "_ExternalDev20"
        });
    }
}
