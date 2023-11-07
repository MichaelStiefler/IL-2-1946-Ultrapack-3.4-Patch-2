package com.maddox.il2.objects.air;

import java.io.IOException;
import java.util.ArrayList;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Orientation;
import com.maddox.il2.fm.FMMath;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.il2.objects.humans.Soldier;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.il2.objects.weapons.RocketGunX4;
import com.maddox.il2.objects.weapons.RocketGunX4R;
import com.maddox.il2.objects.weapons.RocketGunX4homing;
import com.maddox.il2.objects.weapons.RocketGunX7;
import com.maddox.il2.objects.weapons.RocketGunX7homing;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class GO_229A3 extends GO_229
    implements TypeFighterAceMaker, TypeFighter, TypeBNZFighter, TypeX4Carrier, TypeStormovik
{

    public GO_229A3()
    {
        tX4Prev = 0L;
        deltaAzimuth = 0.0F;
        deltaTangage = 0.0F;
        X4check = false;
        missilesList = new ArrayList();
        homing = false;
        X7 = false;
        IR = false;
        k14Mode = 0;
        k14WingspanType = 0;
        k14Distance = 200F;
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        missilesList.clear();
        createMissilesList();
        pos.getRelPoint();
        pos.getRelOrient();
    }

    public void createMissilesList()
    {
        for(int i = 0; i < FM.CT.Weapons.length; i++)
            if(FM.CT.Weapons[i] != null)
            {
                for(int j = 0; j < FM.CT.Weapons[i].length; j++)
                    if(FM.CT.Weapons[i][j] instanceof RocketGunX4)
                    {
                        if((FM.CT.Weapons[i][j] instanceof RocketGunX4homing) || (FM.CT.Weapons[i][j] instanceof RocketGunX7homing))
                            homing = true;
                        if(FM.CT.Weapons[i][j] instanceof RocketGunX7)
                            X7 = true;
                        if(FM.CT.Weapons[i][j] instanceof RocketGunX4R)
                            IR = true;
                        missilesList.add(FM.CT.Weapons[i][j]);
                    }

            }

    }

    public void launchMsl()
    {
        if(missilesList.isEmpty())
        {
            return;
        } else
        {
            ((RocketGunX4)missilesList.remove(0)).shots(1);
            return;
        }
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if((FM instanceof RealFlightModel) && ((RealFlightModel)FM).isRealMode() || !flag || !(FM instanceof Pilot))
            return;
        if(missilesList.isEmpty())
            return;
        if(Time.current() > tX4Prev + (homing || IR ? 10000L : 500L))
        {
            Pilot pilot = (Pilot)FM;
            if((pilot.get_maneuver() == 27 || pilot.get_maneuver() == 62 || pilot.get_maneuver() == 63) && pilot.target != null)
            {
                Point3d point3d = new Point3d(pilot.target.Loc);
                point3d.sub(FM.Loc);
                FM.Or.transformInv(point3d);
                if(homing && !X7)
                {
                    if(pilot.get_maneuver() == 63 ? point3d.x > 7000D && point3d.x < 10000D || point3d.x > 350D && point3d.x < 9000D && World.Rnd().nextFloat() < 0.33F : point3d.x > (FM.CT.Weapons[0][1].countBullets() > 20 ? 800D : 350D) && point3d.x < 3500D + 500D * (double)FM.Skill)
                    {
                        double d = Math.pow(point3d.x / 5000D, 2D) * 5000D;
                        if(point3d.y < d && point3d.y > -d && point3d.z < d && point3d.z > -d && (pilot.get_maneuver() != 63 || point3d.x < 7000D || point3d.y * 2D < point3d.x && point3d.y * 2D > -point3d.x && point3d.z * 2D < point3d.x && point3d.z * 2D > -point3d.x))
                        {
                            Orientation orientation2 = new Orientation();
                            Orientation orientation3 = new Orientation();
                            FM.getOrient(orientation2);
                            pilot.target.getOrient(orientation3);
                            float f3 = Math.abs(orientation2.getAzimut() - orientation3.getAzimut()) % 360F;
                            f3 = f3 > 180F ? 360F - f3 : f3;
                            f3 = f3 > 90F ? 180F - f3 : f3;
                            float f4 = Math.abs(orientation2.getTangage() - orientation3.getTangage()) % 360F;
                            f4 = f4 > 180F ? 360F - f4 : f4;
                            f4 = f4 > 90F ? 180F - f4 : f4;
                            double d2 = (point3d.x * (double)(5 - FM.Skill)) / (double)(pilot.target.getSpeed() + 1.0F);
                            if((double)f3 < d2 && (double)f4 < d2)
                            {
                                launchMsl();
                                tX4Prev = Time.current();
                                Voice.speakAttackByRockets(this);
                            }
                        }
                    }
                } else
                if((pilot.get_maneuver() == 63 ? point3d.x > 4000D && point3d.x < 5500D || point3d.x > 100D && point3d.x < 5000D && World.Rnd().nextFloat() < 0.33F : point3d.x > (FM.CT.Weapons[0][1].countBullets() > 20 ? 800D : 350D) && point3d.x < 2000D + 1000D * (double)FM.Skill) && (point3d.y * 1.5D < point3d.x && point3d.y * 1.5D > -point3d.x && point3d.z * 2D < point3d.x && point3d.z * 2D > -point3d.x))
                {
                    Orientation orientation = new Orientation();
                    Orientation orientation1 = new Orientation();
                    FM.getOrient(orientation);
                    pilot.target.getOrient(orientation1);
                    float f1 = Math.abs(orientation.getAzimut() - orientation1.getAzimut()) % 360F;
                    f1 = f1 > 180F ? 360F - f1 : f1;
                    f1 = f1 > 90F ? 180F - f1 : f1;
                    float f2 = Math.abs(orientation.getTangage() - orientation1.getTangage()) % 360F;
                    f2 = f2 > 180F ? 360F - f2 : f2;
                    f2 = f2 > 90F ? 180F - f2 : f2;
                    double d1 = (point3d.x * (4.5D - (double)FM.Skill)) / (double)(pilot.target.getSpeed() + 1.0F);
                    if((double)f1 < d1 && (double)f2 < d1)
                    {
                        double d3 = FMMath.RAD2DEG(Math.atan(Math.sqrt(Math.pow(Math.tan(FMMath.DEG2RAD(f1)), 2D) + Math.pow(Math.tan(FMMath.DEG2RAD(f2)), 2D))));
                        Vector3d vector3d = pilot.target.getAccel();
                        double d4 = Math.sqrt(Math.pow(vector3d.x, 2D) + Math.pow(vector3d.y, 2D) + Math.pow(vector3d.z, 2D));
                        double d5 = (90D - d3) / (double)(9 + FM.Skill);
                        if(d4 < d5)
                        {
                            launchMsl();
                            tX4Prev = Time.current();
                            Voice.speakAttackByRockets(this);
                        }
                    }
                }
            } else
            if(pilot.get_maneuver() == 43 && pilot.target_ground != null && (X7 || !IR && !homing) && (!(pilot.target_ground instanceof Soldier) && !(pilot.target_ground instanceof BridgeSegment) || !homing))
            {
                Point3d point3d1 = new Point3d();
                pilot.target_ground.pos.getAbs(point3d1);
                point3d1.sub(FM.Loc);
                FM.Or.transformInv(point3d1);
                if(point3d1.x > 400D && point3d1.x < (homing ? 4250D : IR ? 2250D : 1250D) + 250D * (double)FM.Skill)
                {
                    if(!homing || !IR)
                        point3d1.x /= 2 - FM.Skill / 3;
                    if(homing ? point3d1.y * 5D < point3d1.x && point3d1.y * 5D > -point3d1.x && point3d1.z * 5D < point3d1.x && point3d1.z * 5D > -point3d1.x : point3d1.y < point3d1.x && point3d1.y > -point3d1.x && point3d1.z * 1.5D < point3d1.x && point3d1.z * 2D > -point3d1.x)
                    {
                        launchMsl();
                        tX4Prev = Time.current();
                        Voice.speakAttackByRockets(this);
                    }
                }
            }
        }
    }

    public void typeX4CAdjAttitudePlus()
    {
        deltaTangage = 1.0F;
    }

    public void typeX4CAdjAttitudeMinus()
    {
        deltaTangage = -1F;
    }

    public void typeX4CAdjSidePlus()
    {
        deltaAzimuth = 1.0F;
        if(X4check)
            X4check = false;
        else
        if((FM instanceof RealFlightModel) && ((RealFlightModel)FM).isRealMode())
            typeFighterAceMakerAdjSideslipPlus();
    }

    public void typeX4CAdjSideMinus()
    {
        deltaAzimuth = -1F;
        if(X4check)
            X4check = false;
        else
        if((FM instanceof RealFlightModel) && ((RealFlightModel)FM).isRealMode())
            typeFighterAceMakerAdjSideslipMinus();
    }

    public void typeX4CResetControls()
    {
        deltaAzimuth = deltaTangage = 0.0F;
        tX4Prev = Time.current();
        X4check = true;
    }

    public float typeX4CgetdeltaAzimuth()
    {
        return deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage()
    {
        return deltaTangage;
    }

    public boolean typeFighterAceMakerToggleAutomation()
    {
        k14Mode++;
        if(k14Mode > 2)
            k14Mode = 0;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerMode" + k14Mode);
        return true;
    }

    public void typeFighterAceMakerAdjDistanceReset()
    {
    }

    public void typeFighterAceMakerAdjDistancePlus()
    {
        k14Distance += 10F;
        if(k14Distance > 800F)
            k14Distance = 800F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerInc");
    }

    public void typeFighterAceMakerAdjDistanceMinus()
    {
        k14Distance -= 10F;
        if(k14Distance < 200F)
            k14Distance = 200F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerDec");
    }

    public void typeFighterAceMakerAdjSideslipReset()
    {
    }

    public void typeFighterAceMakerAdjSideslipPlus()
    {
        k14WingspanType--;
        if(k14WingspanType < 0)
            k14WingspanType = 0;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "AskaniaWing" + k14WingspanType);
    }

    public void typeFighterAceMakerAdjSideslipMinus()
    {
        k14WingspanType++;
        if(k14WingspanType > 9)
            k14WingspanType = 9;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "AskaniaWing" + k14WingspanType);
    }

    public void typeFighterAceMakerReplicateToNet(NetMsgGuaranted netmsgguaranted)
        throws IOException
    {
        netmsgguaranted.writeByte(k14Mode);
        netmsgguaranted.writeByte(k14WingspanType);
        netmsgguaranted.writeFloat(k14Distance);
    }

    public void typeFighterAceMakerReplicateFromNet(NetMsgInput netmsginput)
        throws IOException
    {
        k14Mode = netmsginput.readByte();
        k14WingspanType = netmsginput.readByte();
        k14Distance = netmsginput.readFloat();
    }

    private long tX4Prev;
    private float deltaAzimuth;
    private float deltaTangage;
    private boolean X4check;
    private ArrayList missilesList;
    private boolean homing;
    private boolean X7;
    private boolean IR;
    public int k14Mode;
    public int k14WingspanType;
    public float k14Distance;

    static 
    {
        Class class1 = GO_229A3.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Go-229");
        Property.set(class1, "meshName", "3DO/Plane/Go-229A3/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1946.5F);
        Property.set(class1, "yearExpired", 1999F);
        Property.set(class1, "FlightModel", "FlightModels/Ho-229A3.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitGO_229A3.class
        });
        Property.set(class1, "LOSElevation", 0.51305F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 1, 1, 3, 3, 3, 3, 3, 3, 
            3, 3, 9, 9, 9, 9, 2, 2, 2, 2, 
            2, 2, 2, 2, 2, 2, 2, 2
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalBomb01", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb03", 
            "_ExternalBomb04", "_ExternalBomb04", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalRock01", "_ExternalRock01", "_ExternalRock02", "_ExternalRock02", 
            "_ExternalRock03", "_ExternalRock03", "_ExternalRock04", "_ExternalRock04", "_ExternalRock05", "_ExternalRock05", "_ExternalRock06", "_ExternalRock06"
        });
    }
}
