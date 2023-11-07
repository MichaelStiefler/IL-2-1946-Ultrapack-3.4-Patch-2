package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.ships.BigshipGeneric;
import com.maddox.il2.objects.ships.ShipGeneric;
import com.maddox.rts.Property;
import com.maddox.sound.Sample;
import com.maddox.sound.SoundFX;

public class SeaHornet_NF21 extends DH103
    implements TypeX4Carrier
{

    public SeaHornet_NF21()
    {
        arrestor = 0.0F;
        obsLookTime = 0;
        obsLookAzimuth = 0.0F;
        obsLookElevation = 0.0F;
        obsAzimuth = 0.0F;
        obsElevation = 0.0F;
        obsAzimuthOld = 0.0F;
        obsElevationOld = 0.0F;
        obsMove = 0.0F;
        obsMoveTot = 0.0F;
        RSOKilled = false;
        scopemode = 1;
        bToFire = false;
        deltaAzimuth = 0.0F;
        deltaTangage = 0.0F;
        fSightCurDistance = 0.0F;
        fSightCurForwardAngle = 0.0F;
        fSightCurSideslip = 0.0F;
        fSightCurAltitude = 3000F;
        fSightCurSpeed = 200F;
        fSightCurReadyness = 0.0F;
        hasKAB = false;
        fxKAB = newSound("weapon.K5.lock", false);
        smplKAB = new Sample("K5_lock.wav", 256, 65535);
        smplKAB.setInfinite(true);
        KABSoundPlaying = false;
        KABEngaged = false;
        KAB = 0;
    }

    public void typeX4CAdjSidePlus()
    {
        deltaAzimuth = 0.002F;
        k14WingspanType--;
        if(k14WingspanType < 0)
            k14WingspanType = 0;
    }

    public void typeX4CAdjSideMinus()
    {
        deltaAzimuth = -0.002F;
        k14WingspanType++;
        if(k14WingspanType > 9)
            k14WingspanType = 9;
    }

    public void typeX4CAdjAttitudePlus()
    {
        deltaTangage = 0.002F;
    }

    public void typeX4CAdjAttitudeMinus()
    {
        deltaTangage = -0.002F;
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

    private boolean KABscan()
    {
        Point3d point3d = new Point3d();
        pos.getAbs(point3d);
        Aircraft aircraft = World.getPlayerAircraft();
        double d = Main3D.cur3D().land2D.worldOfsX() + aircraft.pos.getAbsPoint().x;
        double d1 = Main3D.cur3D().land2D.worldOfsY() + aircraft.pos.getAbsPoint().y;
        double d2 = Main3D.cur3D().land2D.worldOfsY() + aircraft.pos.getAbsPoint().z;
        int i = (int)(-((double)aircraft.pos.getAbsOrient().getYaw() - 90D));
        if(i < 0)
            i = 360 + i;
        int j = (int)(-((double)aircraft.pos.getAbsOrient().getPitch() - 90D));
        if(j < 0)
            j = 360 + j;
        for(java.util.Map.Entry entry = Engine.name2Actor().nextEntry(null); entry != null; entry = Engine.name2Actor().nextEntry(entry))
        {
            Actor actor = (Actor)entry.getValue();
            if(((actor instanceof BigshipGeneric) || (actor instanceof ShipGeneric)) && actor.getArmy() != World.getPlayerArmy() && actor.isAlive() && actor != World.getPlayerAircraft())
            {
                pos.getAbs(point3d);
                double d3 = Main3D.cur3D().land2D.worldOfsX() + actor.pos.getAbsPoint().x;
                double d4 = Main3D.cur3D().land2D.worldOfsY() + actor.pos.getAbsPoint().y;
                double d5 = Main3D.cur3D().land2D.worldOfsY() + actor.pos.getAbsPoint().z;
                new String();
                new String();
                double d6 = (int)(Math.ceil((d2 - d5) / 10D) * 10D);
                new String();
                double d7 = d3 - d;
                double d8 = d4 - d1;
                float f = 57.32484F * (float)Math.atan2(d8, -d7);
                int i1 = (int)(Math.floor((int)f) - 90D);
                if(i1 < 0)
                    i1 = 360 + i1;
                int j1 = i1 - i;
                double d9 = d - d3;
                double d10 = d1 - d4;
                double d11 = Math.sqrt(d6 * d6);
                int k1 = (int)Math.ceil(Math.sqrt(d10 * d10 + d9 * d9));
                float f1 = 57.32484F * (float)Math.atan2(k1, d11);
                int l1 = (int)(Math.floor((int)f1) - 90D);
                if(l1 < 0)
                    l1 = 360 + l1;
                k1 = (int)((double)k1 / 1000D);
                int j2 = (int)Math.ceil(k1);
                byte byte0 = 9;
                if(actor instanceof ShipGeneric)
                    byte0 = 40;
                if(actor instanceof BigshipGeneric)
                    byte0 = 60;
                if(j1 < 0)
                    j1 = 360 + j1;
                double d12 = FM.getAltitude() / (float)j2;
                if(k1 <= byte0 && (double)j2 <= 15D)
                {
                    if((double)j1 <= 20D || (double)j1 >= 340D)
                    {
                        if(d12 >= 325D)
                        {
                            KABEngaged = true;
                            playKAB(KABEngaged);
                        } else
                        {
                            KABEngaged = false;
                            playKAB(KABEngaged);
                        }
                    } else
                    {
                        KABEngaged = false;
                        playKAB(KABEngaged);
                    }
                } else
                {
                    KABEngaged = false;
                    playKAB(KABEngaged);
                }
            }
        }

        return true;
    }

    public void playKAB(boolean flag)
    {
        if(flag && !KABSoundPlaying)
        {
            KABSoundPlaying = true;
            fxKAB.play(smplKAB);
            KAB = KAB + 1;
            if(this.FM.actor == World.getPlayerAircraft())
                HUD.log(AircraftHotKeys.hudLogWeaponId, "KAB-500M engaged");
        } else
        if(!flag && KABSoundPlaying)
        {
            KABSoundPlaying = false;
            fxKAB.cancel();
            if(KAB > 1 && this.FM.actor == World.getPlayerAircraft())
                HUD.log(AircraftHotKeys.hudLogWeaponId, "KAB-500M disengaged");
        }
    }

    protected void moveWingFold(HierMesh hiermesh, float f)
    {
        hiermesh.chunkSetAngles("WingLOut_D0", 0.0F, 150F * f, 0.0F);
        hiermesh.chunkSetAngles("WingROut_D0", 0.0F, -150F * f, 0.0F);
    }

    public void moveWingFold(float f)
    {
        if(f < 0.001F)
        {
            setGunPodsOn(true);
            hideWingWeapons(false);
        } else
        {
            setGunPodsOn(false);
            FM.CT.WeaponControl[0] = false;
            hideWingWeapons(true);
        }
        moveWingFold(hierMesh(), f);
    }

    public void moveArrestorHook(float f)
    {
        hierMesh().chunkSetAngles("TailHook_D0", 0.0F, 35F * f, 0.0F);
        arrestor = f;
    }

    public void doMurderPilot(int i)
    {
        switch(i)
        {
        case 0:
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("HMask1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            radarmode = 5;
            this.FM.AS.bIsAboutToBailout = true;
            break;

        case 1:
            hierMesh().chunkVisible("Pilot2_D0", false);
            hierMesh().chunkVisible("Head2_D0", false);
            hierMesh().chunkVisible("HMask2_D0", false);
            hierMesh().chunkVisible("Pilot2_D1", true);
            RSOKilled = true;
            break;
        }
    }

    protected boolean cutFM(int i, int j, Actor actor)
    {
        switch(i)
        {
        case 33:
        case 34:
        case 35:
        case 36:
        case 37:
        case 38:
        default:
            return super.cutFM(i, j, actor);
        }
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName)
    {
        hierMesh.chunkVisible("S1ASR", false);
        hierMesh.chunkVisible("S2FAB", false);
        hierMesh.chunkVisible("S2PTB", false);
        hierMesh.chunkVisible("S2ASM", false);
        hierMesh.chunkVisible("S3FAB", false);
        hierMesh.chunkVisible("S4ASM", false);
        hierMesh.chunkVisible("S4KAB", false);
        hierMesh.chunkVisible("S5FAB", false);
        hierMesh.chunkVisible("S6ASM", false);
        hierMesh.chunkVisible("S6PTB", false);
        hierMesh.chunkVisible("S6FAB", false);
        hierMesh.chunkVisible("S7ASR", false);
        if(thisWeaponsName.startsWith("01"))
        {
            hierMesh.chunkVisible("S1ASR", true);
            hierMesh.chunkVisible("S7ASR", true);
        }
        if(thisWeaponsName.startsWith("02"))
        {
            hierMesh.chunkVisible("S2FAB", true);
            hierMesh.chunkVisible("S6FAB", true);
        }
        if(thisWeaponsName.startsWith("03"))
        {
            hierMesh.chunkVisible("S2FAB", true);
            hierMesh.chunkVisible("S6FAB", true);
        }
        if(thisWeaponsName.startsWith("04"))
        {
            hierMesh.chunkVisible("S2PTB", true);
            hierMesh.chunkVisible("S6PTB", true);
        }
        if(thisWeaponsName.startsWith("05"))
        {
            hierMesh.chunkVisible("S1ASR", true);
            hierMesh.chunkVisible("S2FAB", true);
            hierMesh.chunkVisible("S6FAB", true);
            hierMesh.chunkVisible("S7ASR", true);
        }
        if(thisWeaponsName.startsWith("06"))
        {
            hierMesh.chunkVisible("S1ASR", true);
            hierMesh.chunkVisible("S2FAB", true);
            hierMesh.chunkVisible("S6FAB", true);
            hierMesh.chunkVisible("S7ASR", true);
        }
        if(thisWeaponsName.startsWith("07"))
        {
            hierMesh.chunkVisible("S1ASR", true);
            hierMesh.chunkVisible("S2PTB", true);
            hierMesh.chunkVisible("S6PTB", true);
            hierMesh.chunkVisible("S7ASR", true);
        }
        if(thisWeaponsName.startsWith("08"))
        {
            hierMesh.chunkVisible("S2ASM", true);
            hierMesh.chunkVisible("S4ASM", true);
            hierMesh.chunkVisible("S6ASM", true);
        }
        if(thisWeaponsName.startsWith("09"))
        {
            hierMesh.chunkVisible("S2ASM", true);
            hierMesh.chunkVisible("S3FAB", true);
            hierMesh.chunkVisible("S4ASM", true);
            hierMesh.chunkVisible("S5FAB", true);
            hierMesh.chunkVisible("S6ASM", true);
        }
        if(thisWeaponsName.startsWith("10"))
        {
            hierMesh.chunkVisible("S4KAB", true);
        }
        if(thisWeaponsName.startsWith("11"))
        {
            hierMesh.chunkVisible("S2PTB", true);
            hierMesh.chunkVisible("S4KAB", true);
            hierMesh.chunkVisible("S6PTB", true);
        }
        if(thisWeaponsName.startsWith("12"))
        {
            hierMesh.chunkVisible("S2ASM", true);
            hierMesh.chunkVisible("S4KAB", true);
            hierMesh.chunkVisible("S6ASM", true);
        }
        if(thisWeaponsName.startsWith("13"))
        {
            hierMesh.chunkVisible("S4KAB", true);
        }
        if(thisWeaponsName.startsWith("14"))
        {
            hierMesh.chunkVisible("S2PTB", true);
            hierMesh.chunkVisible("S4KAB", true);
            hierMesh.chunkVisible("S6PTB", true);
        }
        if(thisWeaponsName.startsWith("15"))
        {
            hierMesh.chunkVisible("S2ASM", true);
            hierMesh.chunkVisible("S4KAB", true);
            hierMesh.chunkVisible("S6ASM", true);
        }
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        SeaHornet_NF21.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
        if(this.thisWeaponsName.startsWith("08"))
        {
            scopemode = 2;
        }
        if(this.thisWeaponsName.startsWith("09"))
        {
            scopemode = 2;
        }
        if(this.thisWeaponsName.startsWith("10"))
        {
            scopemode = 2;
        }
        if(this.thisWeaponsName.startsWith("11"))
        {
            scopemode = 2;
        }
        if(this.thisWeaponsName.startsWith("12"))
        {
            scopemode = 2;
        }
        if(this.thisWeaponsName.startsWith("13"))
        {
            hasKAB = true;
            scopemode = 2;
        }
        if(this.thisWeaponsName.startsWith("14"))
        {
            hasKAB = true;
        }
        if(this.thisWeaponsName.startsWith("15"))
        {
            hasKAB = true;
            scopemode = 2;
        }
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(hasKAB)
            KABscan();
        if((this.FM.Gears.nearGround() || this.FM.Gears.onGround()) && this.FM.CT.getCockpitDoor() == 1.0F)
        {
            hierMesh().chunkVisible("HMask1_D0", false);
            hierMesh().chunkVisible("HMask2_D0", false);
        } else
        {
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Pilot1_D0"));
            hierMesh().chunkVisible("HMask2_D0", hierMesh().isChunkVisible("Pilot2_D0"));
        }
        if(!RSOKilled)
            if(obsLookTime == 0)
            {
                obsLookTime = 2 + World.Rnd().nextInt(1, 3);
                obsMoveTot = 1.0F + World.Rnd().nextFloat() * 1.5F;
                obsMove = 0.0F;
                obsAzimuthOld = obsAzimuth;
                obsElevationOld = obsElevation;
                if((double)World.Rnd().nextFloat() > 0.8D)
                {
                    obsAzimuth = 0.0F;
                    obsElevation = 0.0F;
                } else
                {
                    obsAzimuth = World.Rnd().nextFloat() * 120F - 70F;
                    obsElevation = World.Rnd().nextFloat() * 40F - 20F;
                }
            } else
            {
                obsLookTime--;
            }
    }

    public void update(float f)
    {
        super.update(f);
        if(!(this.FM instanceof Pilot))
            return;
        if(hasKAB && !this.FM.CT.Weapons[3][0].haveBullets())
        {
            hasKAB = false;
            fxKAB.cancel();
            if(this.FM.actor == World.getPlayerAircraft())
                HUD.log(AircraftHotKeys.hudLogWeaponId, "KAB-500M launched");
        }
        if(obsMove < obsMoveTot && !RSOKilled && !this.FM.AS.isPilotParatrooper(1))
        {
            if(obsMove < 0.2F || obsMove > obsMoveTot - 0.2F)
                obsMove += 0.3D * (double)f;
            else
            if(obsMove < 0.1F || obsMove > obsMoveTot - 0.1F)
                obsMove += 0.15F;
            else
                obsMove += 1.2D * (double)f;
            obsLookAzimuth = Aircraft.cvt(obsMove, 0.0F, obsMoveTot, obsAzimuthOld, obsAzimuth);
            obsLookElevation = Aircraft.cvt(obsMove, 0.0F, obsMoveTot, obsElevationOld, obsElevation);
            hierMesh().chunkSetAngles("Head2_D0", 0.0F, obsLookAzimuth, obsLookElevation);
        }
        if(this.FM.CT.getArrestor() > 0.2F)
            if(this.FM.Gears.arrestorVAngle != 0.0F)
            {
                float f1 = Aircraft.cvt(this.FM.Gears.arrestorVAngle, -50F, 7F, 1.0F, 0.0F);
                arrestor = 0.8F * arrestor + 0.2F * f1;
                moveArrestorHook(arrestor);
            } else
            {
                float f2 = (-33F * this.FM.Gears.arrestorVSink) / 57F;
                if(f2 < 0.0F && this.FM.getSpeedKMH() > 60F)
                    Eff3DActor.New(this, this.FM.Gears.arrestorHook, null, 1.0F, "3DO/Effects/Fireworks/04_Sparks.eff", 0.1F);
                if(f2 > 0.0F && this.FM.CT.getArrestor() < 0.95F)
                    f2 = 0.0F;
                if(f2 > 0.2F)
                    f2 = 0.2F;
                if(f2 > 0.0F)
                    arrestor = 0.7F * arrestor + 0.3F * (arrestor + f2);
                else
                    arrestor = 0.3F * arrestor + 0.7F * (arrestor + f2);
                if(arrestor < 0.0F)
                    arrestor = 0.0F;
                else
                if(arrestor > 1.0F)
                    arrestor = 1.0F;
                moveArrestorHook(arrestor);
            }
    }

    private float arrestor;
    private int obsLookTime;
    private float obsLookAzimuth;
    private float obsLookElevation;
    private float obsAzimuth;
    private float obsElevation;
    private float obsAzimuthOld;
    private float obsElevationOld;
    private float obsMove;
    private float obsMoveTot;
    private float deltaAzimuth;
    private float deltaTangage;
    public float fSightCurDistance;
    public float fSightCurForwardAngle;
    public float fSightCurSideslip;
    public float fSightCurAltitude;
    public float fSightCurSpeed;
    public float fSightCurReadyness;
    private boolean hasKAB;
    private SoundFX fxKAB;
    private Sample smplKAB;
    private boolean KABSoundPlaying;
    private boolean KABEngaged;
    private int KAB;

    static 
    {
        Class class1 = SeaHornet_NF21.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "SeaHornet NF.21");
        Property.set(class1, "meshName", "3DO/Plane/SeaHornetNF21(Multi1)/Hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1946.8F);
        Property.set(class1, "yearExpired", 1957.7F);
        Property.set(class1, "FlightModel", "FlightModels/DH103-NF21.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitDH103.class, CockpitDH_ASH.class
        });
        Property.set(class1, "LOSElevation", 0.6731F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 0, 0, 9, 9, 3, 3, 3, 3, 
            2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 
            2, 2, 2, 2, 3, 3, 3, 3, 3, 3
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalDev01", "_ExternalDev02", "_ExternalBomb01", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb02", 
            "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalRock07", "_ExternalRock08", "_ExternalRock09", "_ExternalRock09", 
            "_ExternalRock10", "_ExternalRock10", "_ExternalRock11", "_ExternalRock11", "_ExternalBomb03", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06"
        });
    }
}
