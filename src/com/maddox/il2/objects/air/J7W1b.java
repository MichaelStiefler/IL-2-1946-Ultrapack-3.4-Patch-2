package com.maddox.il2.objects.air;

import java.util.ArrayList;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Orientation;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.objects.Wreckage;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.il2.objects.weapons.RocketGunX4homing;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class J7W1b extends J7Wx
    implements TypeX4Carrier
{

    public J7W1b()
    {
        SecondProp = 0;
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
        if(FM.AS.isMaster() && FM.AS.astateBailoutStep == 2)
            doRemoveProp();
    }

    protected void moveFan(float f)
    {
        hierMesh().chunkFind(Aircraft.Props[1][0]);
        SecondProp = 1;
        int i = 0;
        for(int j = 0; j < (SecondProp == 1 ? 2 : 1); j++)
        {
            if(oldProp[j] < 2)
            {
                i = Math.abs((int)(FM.EI.engines[0].getw() * 0.06F));
                if(i >= 1)
                    i = 1;
                if(i != oldProp[j] && hierMesh().isChunkVisible(Aircraft.Props[j][oldProp[j]]))
                {
                    hierMesh().chunkVisible(Aircraft.Props[j][oldProp[j]], false);
                    oldProp[j] = i;
                    hierMesh().chunkVisible(Aircraft.Props[j][i], true);
                }
            }
            if(i == 0)
            {
                propPos[j] = (propPos[j] + 57.3F * FM.EI.engines[0].getw() * f) % 360F;
            } else
            {
                float f1 = 57.3F * FM.EI.engines[0].getw();
                f1 %= 2880F;
                f1 /= 2880F;
                if(f1 <= 0.5F)
                    f1 *= 2.0F;
                else
                    f1 = f1 * 2.0F - 2.0F;
                f1 *= 1200F;
                propPos[j] = (propPos[j] + f1 * f) % 360F;
            }
            if(j == 0)
                hierMesh().chunkSetAngles(Aircraft.Props[j][i], 0.0F, propPos[j], 0.0F);
            else
                hierMesh().chunkSetAngles(Aircraft.Props[j][i], 0.0F, -(propPos[j] - 20F), 0.0F);
        }

    }

    public void hitProp(int i, int j, Actor actor)
    {
        if(i > FM.EI.getNum() - 1 || oldProp[i] == 2)
            return;
        if((isChunkAnyDamageVisible("Prop" + (i + 1)) || isChunkAnyDamageVisible("PropRot" + (i + 1))) && SecondProp == 1)
        {
            hierMesh().chunkVisible(Aircraft.Props[i + 1][0], false);
            hierMesh().chunkVisible(Aircraft.Props[i + 1][1], false);
            hierMesh().chunkVisible(Aircraft.Props[i + 1][2], true);
        }
        super.hitProp(i, j, actor);
    }

    private final void doRemoveProp()
    {
        oldProp[1] = 99;
        if(hierMesh().isChunkVisible("PropRot1_D0"))
        {
            Wreckage wreckage = new Wreckage(this, hierMesh().chunkFind("Prop1_D0"));
            Wreckage wreckage1 = new Wreckage(this, hierMesh().chunkFind("Prop2_D0"));
            Wreckage wreckage2 = new Wreckage(this, hierMesh().chunkFind("Fan"));
            Eff3DActor.New(wreckage, null, null, 1.0F, Wreckage.SMOKE, 3F);
            Eff3DActor.New(wreckage1, null, null, 1.0F, Wreckage.SMOKE, 3F);
            Eff3DActor.New(wreckage2, null, null, 1.0F, Wreckage.SMOKE, 3F);
            Vector3d vector3d = new Vector3d();
            vector3d.set(FM.Vwld);
            wreckage.setSpeed(vector3d);
            wreckage1.setSpeed(vector3d);
            wreckage2.setSpeed(vector3d);
            hierMesh().chunkVisible("Prop1_D0", false);
            hierMesh().chunkVisible("Prop1_D1", false);
            hierMesh().chunkVisible("PropRot1_D0", false);
            hierMesh().chunkVisible("Prop2_D0", false);
            hierMesh().chunkVisible("Prop2_D1", false);
            hierMesh().chunkVisible("PropRot2_D0", false);
            hierMesh().chunkVisible("Fan", false);
            this.FM.EI.engines[0].setEngineDies(this);
        }
    }

    protected int SecondProp;
    public boolean bToFire;
    private long tX4Prev;
    private float deltaAzimuth;
    private float deltaTangage;
    private ArrayList missilesList;

    static 
    {
        Class class1 = J7W1b.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "J7W");
        Property.set(class1, "meshName", "3DO/Plane/J7W1b/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar01());
        Property.set(class1, "yearService", 1945F);
        Property.set(class1, "yearExpired", 1949F);
        Property.set(class1, "FlightModel", "FlightModels/J7W1b_Vasya.fmd");
//        Property.set(class1, "FlightModel", "FlightModels/J7W1b.fmd");
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
