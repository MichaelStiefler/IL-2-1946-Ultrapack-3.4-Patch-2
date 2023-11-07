package com.maddox.il2.objects.air;

import java.util.ArrayList;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Orientation;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.il2.objects.weapons.RocketGunX4homing;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class J7W2 extends J7Wx
    implements TypeX4Carrier
{

    public J7W2()
    {
        bToFire = false;
        tX4Prev = 0L;
        deltaAzimuth = 0.0F;
        deltaTangage = 0.0F;
        missilesList = new ArrayList();
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if((this.FM instanceof RealFlightModel) && ((RealFlightModel)this.FM).isRealMode() || !flag || !(this.FM instanceof Pilot))
            return;
        if(missilesList.isEmpty())
            return;
        Pilot pilot = (Pilot)this.FM;
        if(Time.current() > tX4Prev + 10000L && (pilot.get_maneuver() == 27 || pilot.get_maneuver() == 62 || pilot.get_maneuver() == 63) && ((Maneuver) (pilot)).target != null)
        {
            Point3d point3d = new Point3d(((FlightModelMain) (((Maneuver) (pilot)).target)).Loc);
            point3d.sub(this.FM.Loc);
            this.FM.Or.transformInv(point3d);
            if(pilot.get_maneuver() != 63 ? point3d.x > (this.FM.CT.Weapons[0][1].countBullets() <= 20 ? 350D : 800D) && point3d.x < 1500D + 250D * (double)this.FM.Skill : point3d.x > 2000D && point3d.x < 3500D || point3d.x > 100D && point3d.x < 3000D && World.Rnd().nextFloat() < 0.33F)
            {
                double d = Math.pow(point3d.x / 1500D, 2D) * 1500D;
                if(point3d.y < d && point3d.y > -d && point3d.z < d && point3d.z > -d && (pilot.get_maneuver() != 63 || point3d.x < 2500D || point3d.y * 2D < point3d.x && point3d.y * 2D > -point3d.x && point3d.z * 2D < point3d.x && point3d.z * 2D > -point3d.x))
                {
                    Orientation orientation = new Orientation();
                    Orientation orientation1 = new Orientation();
                    this.FM.getOrient(orientation);
                    ((Maneuver) (pilot)).target.getOrient(orientation1);
                    float f1 = Math.abs(orientation.getAzimut() - orientation1.getAzimut()) % 360F;
                    f1 = f1 <= 180F ? f1 : 360F - f1;
                    f1 = f1 <= 90F ? f1 : 180F - f1;
                    float f2 = Math.abs(orientation.getTangage() - orientation1.getTangage()) % 360F;
                    f2 = f2 <= 180F ? f2 : 360F - f2;
                    f2 = f2 <= 90F ? f2 : 180F - f2;
                    double d1 = (point3d.x * (double)(5 - this.FM.Skill)) / (double)(((Maneuver) (pilot)).target.getSpeed() + 1.0F);
                    if((double)f1 < d1 && (double)f2 < d1)
                    {
                        launchMsl();
                        tX4Prev = Time.current();
                        Voice.speakAttackByRockets(this);
                    }
                }
            }
        }
    }

    public void createMissilesList()
    {
        for(int i = 0; i < this.FM.CT.Weapons.length; i++)
            if(this.FM.CT.Weapons[i] != null)
            {
                for(int j = 0; j < this.FM.CT.Weapons[i].length; j++)
                    if(this.FM.CT.Weapons[i][j] instanceof RocketGunX4homing)
                        missilesList.add(this.FM.CT.Weapons[i][j]);

            }

    }

    public void launchMsl()
    {
        if(missilesList.isEmpty())
        {
            return;
        } else
        {
            ((RocketGunX4homing)missilesList.remove(0)).shots(1);
            return;
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

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        missilesList.clear();
        createMissilesList();
        if(thisWeaponsName.startsWith("light"))
            FM.M.massEmpty -= 70F;
        if(thisWeaponsName.startsWith("heavy"))
            FM.M.massEmpty += 120F;
    }

    public void update(float f)
    {
        super.update(f);
        if(Config.isUSE_RENDER() && FM.AS.isMaster())
            if(FM.EI.engines[0].getPowerOutput() > 0.8F && FM.EI.engines[0].getStage() == 6)
            {
                if(FM.EI.engines[0].getPowerOutput() > 0.95F)
                    FM.AS.setSootState(this, 0, 3);
                else
                    FM.AS.setSootState(this, 0, 2);
            } else
            {
                FM.AS.setSootState(this, 0, 0);
            }
        if(FM.AS.isMaster() && FM.AS.astateBailoutStep == 2)
            this.FM.EI.engines[0].setEngineDies(this);
    }

    public void doSetSootState(int i, int j)
    {
        for(int k = 0; k < 2; k++)
        {
            if(FM.AS.astateSootEffects[i][k] != null)
                Eff3DActor.finish(FM.AS.astateSootEffects[i][k]);
            FM.AS.astateSootEffects[i][k] = null;
        }

        switch(j)
        {
        case 1:
            FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "ES_01"), null, 1.8F, "3DO/Effects/Aircraft/BlackSmallTSPD.eff", -1F);
            FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "ES_02"), null, 1.8F, "3DO/Effects/Aircraft/BlackSmallTSPD.eff", -1F);
            break;

        case 3:
            FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "EF_01"), null, 1.8F, "3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1F);
            // fall through

        case 2:
            FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "EF_01"), null, 1.4F, "3DO/Effects/Aircraft/TurboZippo.eff", -1F);
            break;

        case 5:
            FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "EF_01"), null, 3F, "3DO/Effects/Aircraft/TurboJRD1100F.eff", -1F);
            // fall through

        case 4:
            FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "EF_01"), null, 1.0F, "3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1F);
            break;
        }
    }

    public boolean bToFire;
    private long tX4Prev;
    private float deltaAzimuth;
    private float deltaTangage;
    private ArrayList missilesList;

    static 
    {
        Class class1 = J7W2.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "J7W");
        Property.set(class1, "meshName", "3DO/Plane/J7W2/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar01());
        Property.set(class1, "yearService", 1946F);
        Property.set(class1, "yearExpired", 1956F);
        Property.set(class1, "FlightModel", "FlightModels/J7W2_Vasya.fmd");
//        Property.set(class1, "FlightModel", "FlightModels/J7W2.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitJ7W.class
        });
        Property.set(class1, "LOSElevation", 1.0151F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 1, 1, 3, 3, 3, 3, 9, 9, 
            9, 9, 9, 9, 2, 2, 2, 2, 9, 9
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_Cannon01", "_Cannon02", "_Cannon03", "_Cannon04", "_Externalbomb01", "_Externalbomb02", "_Externalbomb03", "_Externalbomb04", "_Externaldev01", "_Externaldev02", 
            "_Externaldev03", "_Externaldev04", "_Externaldev05", "_Externaldev06", "_X401", "_X402", "_X403", "_X403", "_Externaldev07", "_Externaldev08"
        });
    }
}