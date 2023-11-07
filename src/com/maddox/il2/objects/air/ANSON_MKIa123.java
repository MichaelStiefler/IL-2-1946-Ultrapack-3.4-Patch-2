package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.rts.Property;

public class ANSON_MKIa123 extends Scheme2
    implements TypeStormovik, TypeScout, TypeTransport
{
    public static void moveGear(HierMesh hiermesh, float f)
    {
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, 0.0F, -23F * f);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, 0.0F, -23F * f);
    }

    protected void moveGear(float f)
    {
        moveGear(hierMesh(), f);
    }

    protected void moveElevator(float f)
    {
        hierMesh().chunkSetAngles("VatorL_D0", 0.0F, 0.0F, -25F * f);
        hierMesh().chunkSetAngles("VatorR_D0", 0.0F, 0.0F, -25F * f);
    }

    protected void moveAileron(float f)
    {
        hierMesh().chunkSetAngles("AroneL_D0", 0.0F, 0.0F, 25F * f);
        hierMesh().chunkSetAngles("AroneR_D0", 0.0F, 0.0F, -25F * f);
    }

    protected void moveRudder(float f)
    {
        hierMesh().chunkSetAngles("Rudder1_D0", 30F * f, 0.0F, 0.0F);
    }

    protected void moveFlap(float f)
    {
        hierMesh().chunkSetAngles("FlapL_D0", 0.0F, 0.0F, 45F * f);
        hierMesh().chunkSetAngles("FlapR_D0", 0.0F, 0.0F, 45F * f);
    }

    protected void moveBayDoor(float f)
    {
        hierMesh().chunkSetAngles("Bay1_D0", 0.0F, 110F * f, 0.0F);
        hierMesh().chunkSetAngles("Bay2_D0", 0.0F, -110F * f, 0.0F);
        hierMesh().chunkSetAngles("Bay3_D0", 0.0F, 110F * f, 0.0F);
        hierMesh().chunkSetAngles("Bay4_D0", 0.0F, -110F * f, 0.0F);
        hierMesh().chunkSetAngles("Bay5_D0", 0.0F, 110F * f, 0.0F);
        hierMesh().chunkSetAngles("Bay6_D0", 0.0F, -110F * f, 0.0F);
        hierMesh().chunkSetAngles("Bay7_D0", 0.0F, 110F * f, 0.0F);
        hierMesh().chunkSetAngles("Bay8_D0", 0.0F, -110F * f, 0.0F);
    }

    public void doKillPilot(int i)
    {
        switch(i)
        {
        case 3:
            this.FM.turret[0].bIsOperable = false;
            break;
        }
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
            break;

        case 1:
            hierMesh().chunkVisible("Pilot2_D0", false);
            hierMesh().chunkVisible("HMask2_D0", false);
            hierMesh().chunkVisible("Pilot2_D1", true);
            break;

        case 2:
            hierMesh().chunkVisible("Pilot3_D0", false);
            hierMesh().chunkVisible("HMask3_D0", false);
            hierMesh().chunkVisible("Pilot3_D1", true);
            break;

        case 3:
            hierMesh().chunkVisible("Pilot4_D0", false);
            hierMesh().chunkVisible("HMask4_D0", false);
            hierMesh().chunkVisible("Pilot4_D1", true);
            break;
        }
    }

    public boolean turretAngles(int i, float af[])
    {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        switch(i)
        {
        case 0:
            if(f < -100F)
            {
                f = -100F;
                flag = false;
            }
            if(f > 100F)
            {
                f = 100F;
                flag = false;
            }
            if(f1 < -3F)
            {
                f1 = -3F;
                flag = false;
            }
            if(f1 > 50F)
            {
                f1 = 50F;
                flag = false;
            }
            break;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xx"))
        {
            if(s.startsWith("xxeng"))
            {
                int i = s.charAt(5) - 49;
                if(s.endsWith("case"))
                {
                    if(getEnergyPastArmor(1.7F, shot) > 0.0F)
                    {
                        if(World.Rnd().nextFloat(20000F, 140000F) < shot.power)
                            this.FM.AS.setEngineStuck(shot.initiator, i);
                        if(World.Rnd().nextFloat(10000F, 50000F) < shot.power)
                            this.FM.AS.hitEngine(shot.initiator, i, 2);
                    } else
                    if(World.Rnd().nextFloat() < 0.04F)
                        this.FM.EI.engines[i].setCyliderKnockOut(shot.initiator, 1);
                    else
                        this.FM.EI.engines[i].setReadyness(shot.initiator, this.FM.EI.engines[i].getReadyness() - 0.02F);
                    getEnergyPastArmor(12F, shot);
                }
                if(s.endsWith("cyls"))
                {
                    if(getEnergyPastArmor(0.85F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[i].getCylindersRatio() * 0.9878F)
                    {
                        this.FM.EI.engines[i].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int)(shot.power / 19000F)));
                        if(World.Rnd().nextFloat() < shot.power / 48000F)
                            this.FM.AS.hitEngine(shot.initiator, 0, 2);
                    }
                    getEnergyPastArmor(25F, shot);
                }
                if(s.endsWith("supc"))
                {
                    if(getEnergyPastArmor(0.05F, shot) > 0.0F)
                        this.FM.EI.engines[i].setKillCompressor(shot.initiator);
                    getEnergyPastArmor(2.0F, shot);
                }
                if(s.endsWith("oil1") && World.Rnd().nextFloat() < 0.5F && getEnergyPastArmor(0.25F, shot) > 0.0F)
                    this.FM.AS.hitOil(shot.initiator, 0);
                return;
            }
            if(s.startsWith("xxtank"))
            {
                int j = s.charAt(6) - 49;
                if(getEnergyPastArmor(1.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.4F)
                {
                    if(this.FM.AS.astateTankStates[j] == 0)
                    {
                        this.FM.AS.hitTank(shot.initiator, j, 1);
                        this.FM.AS.doSetTankState(shot.initiator, j, 1);
                    }
                    if(World.Rnd().nextFloat() < 0.003F || shot.powerType == 3 && World.Rnd().nextFloat() < 0.2F)
                        this.FM.AS.hitTank(shot.initiator, j, 4);
                }
                return;
            }
        }
        if(s.startsWith("xcf"))
        {
            if(chunkDamageVisible("CF") < 2)
                hitChunk("CF", shot);
        } else
        if(s.startsWith("xtail"))
        {
            if(chunkDamageVisible("Tail1") < 2)
                hitChunk("Tail1", shot);
        } else
        if(s.startsWith("xrudder1"))
        {
            if(chunkDamageVisible("Rudder1") < 2)
                hitChunk("Rudder1", shot);
        } else
        if(s.startsWith("xstabl"))
        {
            if(chunkDamageVisible("StabL") < 2)
                hitChunk("StabL", shot);
        } else
        if(s.startsWith("xstabr"))
        {
            if(chunkDamageVisible("StabR") < 2)
                hitChunk("StabR", shot);
        } else
        if(s.startsWith("xvatorl"))
        {
            if(chunkDamageVisible("VatorL") < 2)
                hitChunk("VatorL", shot);
        } else
        if(s.startsWith("xvatorr"))
        {
            if(chunkDamageVisible("VatorR") < 2)
                hitChunk("VatorR", shot);
        } else
        if(s.startsWith("xwinglin"))
        {
            if(chunkDamageVisible("WingLIn") < 2)
                hitChunk("WingLIn", shot);
        } else
        if(s.startsWith("xwingrin"))
        {
            if(chunkDamageVisible("WingRIn") < 2)
                hitChunk("WingRIn", shot);
        } else
        if(s.startsWith("xwinglout"))
        {
            if(chunkDamageVisible("WingLOut") < 2)
                hitChunk("WingLOut", shot);
        } else
        if(s.startsWith("xwingrout"))
        {
            if(chunkDamageVisible("WingROut") < 2)
                hitChunk("WingROut", shot);
        } else
        if(s.startsWith("xaronel"))
        {
            if(chunkDamageVisible("AroneL") < 2)
                hitChunk("AroneL", shot);
        } else
        if(s.startsWith("xaroner"))
        {
            if(chunkDamageVisible("AroneR") < 2)
                hitChunk("AroneR", shot);
        } else
        if(s.startsWith("xengine1"))
        {
            if(chunkDamageVisible("Engine1") < 2)
                hitChunk("Engine1", shot);
        } else
        if(s.startsWith("xengine2"))
        {
            if(chunkDamageVisible("Engine2") < 2)
                hitChunk("Engine2", shot);
        } else
        if(s.startsWith("xgear"))
        {
            if(s.startsWith("xgear") && World.Rnd().nextFloat() < 0.1F && getEnergyPastArmor(World.Rnd().nextFloat(12.88F, 16.96F), shot) > 0.0F)
                this.FM.AS.setInternalDamage(shot.initiator, 3);
        } else
        if(s.startsWith("xpilot") || s.startsWith("xhead"))
        {
            byte byte0 = 0;
            int k;
            if(s.endsWith("a") || s.endsWith("a2"))
            {
                byte0 = 1;
                k = s.charAt(6) - 49;
            } else
            if(s.endsWith("b") || s.endsWith("b2"))
            {
                byte0 = 2;
                k = s.charAt(6) - 49;
            } else
            {
                k = s.charAt(5) - 49;
            }
            hitFlesh(k, shot, byte0);
        }
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        for(int i = 1; i < 5; i++)
            if(this.FM.getAltitude() < 3000F)
                hierMesh().chunkVisible("HMask" + i + "_D0", false);
            else
                hierMesh().chunkVisible("HMask" + i + "_D0", hierMesh().isChunkVisible("Pilot" + i + "_D0"));

    }

    public void update(float f)
    {
        if((this.FM instanceof RealFlightModel) && ((RealFlightModel)this.FM).isRealMode())
        {
            float f1 = this.FM.EI.engines[0].getRPM();
            if(f1 < 300F && f1 > 30F)
                ((RealFlightModel)this.FM).producedShakeLevel = (1500F - f1) / 3000F;
            float f3 = this.FM.EI.engines[0].getRPM();
            if(f3 < 1000F && f3 > 301F)
                ((RealFlightModel)this.FM).producedShakeLevel = (1500F - f3) / 8000F;
            float f5 = this.FM.EI.engines[0].getRPM();
            if(f5 > 1001F && f5 < 1500F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.07F;
            float f7 = this.FM.EI.engines[0].getRPM();
            if(f7 > 1501F && f7 < 2000F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.05F;
            float f9 = this.FM.EI.engines[0].getRPM();
            if(f9 > 2001F && f9 < 2500F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.04F;
            float f11 = this.FM.EI.engines[0].getRPM();
            if(f11 > 2501F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.03F;
        }
        if((this.FM instanceof RealFlightModel) && ((RealFlightModel)this.FM).isRealMode())
        {
            float f2 = this.FM.EI.engines[1].getRPM();
            if(f2 < 300F && f2 > 30F)
                ((RealFlightModel)this.FM).producedShakeLevel = (1500F - f2) / 3000F;
            float f4 = this.FM.EI.engines[1].getRPM();
            if(f4 < 1000F && f4 > 301F)
                ((RealFlightModel)this.FM).producedShakeLevel = (1500F - f4) / 8000F;
            float f6 = this.FM.EI.engines[1].getRPM();
            if(f6 > 1001F && f6 < 1500F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.07F;
            float f8 = this.FM.EI.engines[1].getRPM();
            if(f8 > 1501F && f8 < 2000F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.05F;
            float f10 = this.FM.EI.engines[1].getRPM();
            if(f10 > 2001F && f10 < 2500F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.04F;
            float f12 = this.FM.EI.engines[1].getRPM();
            if(f12 > 2501F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.03F;
        }
        if(this.FM.getSpeedKMH() > 250F && this.FM.getVertSpeed() > 0.0F && this.FM.getAltitude() < 5000F)
            this.FM.producedAF.x += 20F * (250F - this.FM.getSpeedKMH());
        if(this.FM.isPlayers() && this.FM.Sq.squareElevators > 0.0F)
        {
            if(this.FM.getSpeedKMH() > 300F && this.FM.getSpeedKMH() < 400F)
            {
                this.FM.SensPitch = 0.2F;
                this.FM.producedAM.y -= 400F * (300F - this.FM.getSpeedKMH());
            }
            if(this.FM.getSpeedKMH() >= 400F)
            {
                this.FM.SensPitch = 0.17F;
                this.FM.producedAM.y -= 250F * (300F - this.FM.getSpeedKMH());
            } else
            {
                this.FM.SensPitch = 0.59F;
            }
        }
        super.update(f);
    }

    static 
    {
        Class class1 = ANSON_MKIa123.class;
        Property.set(class1, "originCountry", PaintScheme.countryBritain);
    }
}
