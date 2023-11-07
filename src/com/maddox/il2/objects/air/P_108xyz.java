package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class P_108xyz extends Scheme4
    implements TypeBomber, TypeTransport
{

    public P_108xyz()
    {
        btme = -1L;
        bGunUp = false;
        fGunPos = 0.0F;
        kl = 1.0F;
        kr = 1.0F;
        fSightCurAltitude = 1000F;
        fSightCurSpeed = 150F;
        fSightCurForwardAngle = 0.0F;
        fSightSetForwardAngle = 0.0F;
        fSightCurSideslip = 0.0F;
    }

    public void update(float f)
    {
        super.update(f);
        if(!bGunUp)
        {
            if(fGunPos > 0.0F)
            {
                fGunPos -= 0.2F * f;
                this.FM.turret[3].bIsOperable = false;
            }
        } else
        if(fGunPos < 1.0F)
        {
            fGunPos += 0.2F * f;
            if(fGunPos > 0.8F && fGunPos < 0.9F)
                this.FM.turret[3].bIsOperable = true;
        }
        if(fGunPos < 0.333F)
            hierMesh().chunkSetAngles("Turret4C_D0", 0.0F, -Aircraft.cvt(fGunPos, 0.0F, 0.333F, 0.0F, 41F), 0.0F);
        else
        if(fGunPos < 0.666F)
        {
            resetYPRmodifier();
            Aircraft.xyz[1] = Aircraft.cvt(fGunPos, 0.333F, 0.666F, 0.0F, 0.5F);
            hierMesh().chunkSetLocate("Turret4C_D0", Aircraft.xyz, Aircraft.ypr);
        }
        if(this.FM.turret[3].bIsAIControlled)
        {
            if(this.FM.turret[3].target != null && this.FM.AS.astatePilotStates[2] < 90)
                bGunUp = true;
            if(Time.current() > btme)
            {
                btme = Time.current() + World.Rnd().nextLong(5000L, 12000L);
                if(this.FM.turret[3].target == null && this.FM.AS.astatePilotStates[2] < 90)
                    bGunUp = false;
            }
        }
        if((this.FM instanceof RealFlightModel) && ((RealFlightModel)this.FM).isRealMode())
        {
            float f1 = this.FM.EI.engines[0].getRPM();
            if(f1 < 300F && f1 > 30F)
                ((RealFlightModel)this.FM).producedShakeLevel = (1500F - f1) / 3000F;
            float f5 = this.FM.EI.engines[0].getRPM();
            if(f5 < 1000F && f5 > 301F)
                ((RealFlightModel)this.FM).producedShakeLevel = (1500F - f5) / 8000F;
            float f9 = this.FM.EI.engines[0].getRPM();
            if(f9 > 1001F && f9 < 1500F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.07F;
            float f13 = this.FM.EI.engines[0].getRPM();
            if(f13 > 1501F && f13 < 2000F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.05F;
            float f17 = this.FM.EI.engines[0].getRPM();
            if(f17 > 2001F && f17 < 2500F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.04F;
            float f21 = this.FM.EI.engines[0].getRPM();
            if(f21 > 2501F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.03F;
        }
        if((this.FM instanceof RealFlightModel) && ((RealFlightModel)this.FM).isRealMode())
        {
            float f2 = this.FM.EI.engines[1].getRPM();
            if(f2 < 300F && f2 > 30F)
                ((RealFlightModel)this.FM).producedShakeLevel = (1500F - f2) / 3000F;
            float f6 = this.FM.EI.engines[1].getRPM();
            if(f6 < 1000F && f6 > 301F)
                ((RealFlightModel)this.FM).producedShakeLevel = (1500F - f6) / 8000F;
            float f10 = this.FM.EI.engines[1].getRPM();
            if(f10 > 1001F && f10 < 1500F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.07F;
            float f14 = this.FM.EI.engines[1].getRPM();
            if(f14 > 1501F && f14 < 2000F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.05F;
            float f18 = this.FM.EI.engines[1].getRPM();
            if(f18 > 2001F && f18 < 2500F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.04F;
            float f22 = this.FM.EI.engines[1].getRPM();
            if(f22 > 2501F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.03F;
        }
        if((this.FM instanceof RealFlightModel) && ((RealFlightModel)this.FM).isRealMode())
        {
            float f3 = this.FM.EI.engines[2].getRPM();
            if(f3 < 300F && f3 > 30F)
                ((RealFlightModel)this.FM).producedShakeLevel = (1500F - f3) / 3000F;
            float f7 = this.FM.EI.engines[2].getRPM();
            if(f7 < 1000F && f7 > 301F)
                ((RealFlightModel)this.FM).producedShakeLevel = (1500F - f7) / 8000F;
            float f11 = this.FM.EI.engines[2].getRPM();
            if(f11 > 1001F && f11 < 1500F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.07F;
            float f15 = this.FM.EI.engines[2].getRPM();
            if(f15 > 1501F && f15 < 2000F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.05F;
            float f19 = this.FM.EI.engines[2].getRPM();
            if(f19 > 2001F && f19 < 2500F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.04F;
            float f23 = this.FM.EI.engines[2].getRPM();
            if(f23 > 2501F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.03F;
        }
        if((this.FM instanceof RealFlightModel) && ((RealFlightModel)this.FM).isRealMode())
        {
            float f4 = this.FM.EI.engines[3].getRPM();
            if(f4 < 300F && f4 > 30F)
                ((RealFlightModel)this.FM).producedShakeLevel = (1500F - f4) / 3000F;
            float f8 = this.FM.EI.engines[3].getRPM();
            if(f8 < 1000F && f8 > 301F)
                ((RealFlightModel)this.FM).producedShakeLevel = (1500F - f8) / 8000F;
            float f12 = this.FM.EI.engines[3].getRPM();
            if(f12 > 1001F && f12 < 1500F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.07F;
            float f16 = this.FM.EI.engines[3].getRPM();
            if(f16 > 1501F && f16 < 2000F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.05F;
            float f20 = this.FM.EI.engines[3].getRPM();
            if(f20 > 2001F && f20 < 2500F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.04F;
            float f24 = this.FM.EI.engines[3].getRPM();
            if(f24 > 2501F)
                ((RealFlightModel)this.FM).producedShakeLevel = 0.03F;
        }
        if(this.FM.getSpeedKMH() > 250F && this.FM.getVertSpeed() > 0.0F && this.FM.getAltitude() < 5000F)
            this.FM.producedAF.x += 20F * (250F - this.FM.getSpeedKMH());
        if(this.FM.isPlayers() && this.FM.Sq.squareElevators > 0.0F)
        {
            RealFlightModel realflightmodel = (RealFlightModel)this.FM;
            if(realflightmodel.RealMode && realflightmodel.indSpeed > 120F)
            {
                float f2 = 1.0F + 0.005F * (120F - realflightmodel.indSpeed);
                if(f2 < 0.0F)
                    f2 = 0.0F;
                this.FM.SensPitch = 0.35F * f2;
                if(realflightmodel.indSpeed > 120F)
                    this.FM.producedAM.y -= 1720F * (120F - realflightmodel.indSpeed);
            } else
            {
                this.FM.SensPitch = 0.41F;
            }
        }
        if(this.FM.getSpeed() > 80F)
        {
            hierMesh().chunkSetAngles("SlatL_D0", Aircraft.cvt(this.FM.getAOA(), 6.8F, 11F, 0.0F, 1.5F), 0.0F, 0.0F);
            hierMesh().chunkSetAngles("SlatR_D0", Aircraft.cvt(this.FM.getAOA(), 6.8F, 11F, 0.0F, 1.5F), 0.0F, 0.0F);
        }
    }

    public static void moveGear(HierMesh hiermesh, float f)
    {
        float f1 = Math.max(-f * 800F, -80F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, f1 * kl, 0.0F);
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, -f1 * kl, 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, -f1 * kr, 0.0F);
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, f1 * kr, 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, 0.0F, -90F * f * kl);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, 0.0F, -90F * f * kr);
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, 0.0F, 55F * f * kl);
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, 0.0F, 55F * f * kr);
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
        hierMesh().chunkSetAngles("Flap01_D0", 0.0F, 0.0F, 45F * f);
        hierMesh().chunkSetAngles("Flap02_D0", 0.0F, 0.0F, 45F * f);
    }

    protected void moveBayDoor(float f)
    {
        hierMesh().chunkSetAngles("BayCL_D0", 0.0F, 90F * f, 0.0F);
        hierMesh().chunkSetAngles("BayCR_D0", 0.0F, -90F * f, 0.0F);
        hierMesh().chunkSetAngles("BayL_D0", 0.0F, 90F * f, 0.0F);
        hierMesh().chunkSetAngles("BayR_D0", 0.0F, -90F * f, 0.0F);
    }

    public boolean turretAngles(int i, float af[])
    {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        switch(i)
        {
        default:
            break;

        case 0:
            if(f < -30F)
            {
                f = -30F;
                flag = false;
            }
            if(f > 30F)
            {
                f = 30F;
                flag = false;
            }
            if(f1 < -10F)
            {
                f1 = -10F;
                flag = false;
            }
            if(f1 > 30F)
            {
                f1 = 30F;
                flag = false;
            }
            break;

        case 1:
            if(f < -30F)
            {
                f = -30F;
                flag = false;
            }
            if(f > 30F)
            {
                f = 30F;
                flag = false;
            }
            if(f1 < -3F)
            {
                f1 = -3F;
                flag = false;
            }
            if(f1 > 30F)
            {
                f1 = 30F;
                flag = false;
            }
            break;

        case 2:
            if(f < -30F)
            {
                f = -30F;
                flag = false;
            }
            if(f > 30F)
            {
                f = 30F;
                flag = false;
            }
            if(f1 < -3F)
            {
                f1 = -3F;
                flag = false;
            }
            if(f1 > 30F)
            {
                f1 = 30F;
                flag = false;
            }
            break;

        case 3:
            if(f1 < -40F)
            {
                f1 = -40F;
                flag = false;
            }
            if(f1 > 3F)
            {
                f1 = 3F;
                flag = false;
            }
            break;

        case 4:
            if(f < -5F)
            {
                f = -5F;
                flag = false;
            }
            if(f > 30F)
            {
                f = 30F;
                flag = false;
            }
            if(f1 < -20F)
            {
                f1 = -20F;
                flag = false;
            }
            if(f1 > 20F)
            {
                f1 = 20F;
                flag = false;
            }
            break;

        case 5:
            if(f < -30F)
            {
                f = -30F;
                flag = false;
            }
            if(f > 5F)
            {
                f = 5F;
                flag = false;
            }
            if(f1 < -20F)
            {
                f1 = -20F;
                flag = false;
            }
            if(f1 > 20F)
            {
                f1 = 20F;
                flag = false;
            }
            break;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    public void doKillPilot(int i)
    {
        switch(i)
        {
        case 2:
            this.FM.turret[0].bIsOperable = false;
            break;

        case 3:
            this.FM.turret[1].bIsOperable = false;
            break;

        case 4:
            this.FM.turret[2].bIsOperable = false;
            break;

        case 5:
            this.FM.turret[3].bIsOperable = false;
            break;

        case 6:
            this.FM.turret[4].bIsOperable = false;
            break;

        case 7:
            this.FM.turret[5].bIsOperable = false;
            break;
        }
    }

    public void doMurderPilot(int i)
    {
        switch(i)
        {
        case 0:
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("HMask1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            hierMesh().chunkVisible("Head1_D0", false);
            break;

        default:
            hierMesh().chunkVisible("Pilot" + (i + 1) + "_D0", false);
            hierMesh().chunkVisible("HMask" + (i + 1) + "_D0", false);
            hierMesh().chunkVisible("Pilot" + (i + 1) + "_D1", true);
            break;
        }
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(flag && (this.FM instanceof RealFlightModel) && ((RealFlightModel)this.FM).isRealMode())
        {
            for(int i = 1; i < 9; i++)
                if(this.FM.getAltitude() < 3000F)
                    hierMesh().chunkVisible("HMask" + i + "_D0", false);
                else
                    hierMesh().chunkVisible("HMask" + i + "_D0", hierMesh().isChunkVisible("Pilot" + i + "_D0"));

        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xx"))
        {
            if(s.startsWith("xxArmor"))
            {
                debuggunnery("Armor: Hit..");
                if(s.endsWith("p1"))
                    getEnergyPastArmor(12.7D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                else
                if(s.endsWith("p2"))
                    getEnergyPastArmor(12.7D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                else
                if(s.endsWith("p3"))
                    getEnergyPastArmor(12.7D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                else
                if(s.endsWith("p4"))
                    getEnergyPastArmor(12.7D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                else
                if(s.endsWith("p5"))
                    getEnergyPastArmor(9D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                return;
            }
            if(s.startsWith("xxcontrols"))
            {
                debuggunnery("Controls: Hit..");
                int i = s.charAt(10) - 48;
                switch(i)
                {
                default:
                    break;

                case 1:
                    if(getEnergyPastArmor(4.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                    {
                        debuggunnery("Controls: Elevator Controls: Disabled..");
                        this.FM.AS.setControlsDamage(shot.initiator, 1);
                    }
                    if(getEnergyPastArmor(0.002F, shot) > 0.0F && World.Rnd().nextFloat() < 0.11F)
                    {
                        debuggunnery("Controls: Rudder Controls: Disabled / Strings Broken..");
                        this.FM.AS.setControlsDamage(shot.initiator, 2);
                    }
                    break;

                case 2:
                    if(getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat() < 0.12F)
                    {
                        this.FM.AS.setControlsDamage(shot.initiator, 0);
                        debuggunnery("Ailerons Controls Out..");
                    }
                    break;

                case 3:
                    if(getEnergyPastArmor(0.1F, shot) <= 0.0F)
                        break;
                    if(World.Rnd().nextFloat() < 0.25F)
                    {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                        debuggunnery("*** Engine1 Throttle Controls Out..");
                    }
                    if(World.Rnd().nextFloat() < 0.15F)
                    {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 6);
                        debuggunnery("*** Engine1 Prop Controls Out..");
                    }
                    break;

                case 4:
                    if(getEnergyPastArmor(0.1F, shot) <= 0.0F)
                        break;
                    if(World.Rnd().nextFloat() < 0.15F)
                    {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 1, 1);
                        debuggunnery("*** Engine2 Throttle Controls Out..");
                    }
                    if(World.Rnd().nextFloat() < 0.15F)
                    {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 1, 6);
                        debuggunnery("*** Engine2 Prop Controls Out..");
                    }
                    break;

                case 5:
                    if(getEnergyPastArmor(0.1F, shot) <= 0.0F)
                        break;
                    if(World.Rnd().nextFloat() < 0.25F)
                    {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 2, 1);
                        debuggunnery("*** Engine3 Throttle Controls Out..");
                    }
                    if(World.Rnd().nextFloat() < 0.15F)
                    {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 2, 6);
                        debuggunnery("*** Engine3 Prop Controls Out..");
                    }
                    break;

                case 6:
                    if(getEnergyPastArmor(0.1F, shot) <= 0.0F)
                        break;
                    if(World.Rnd().nextFloat() < 0.15F)
                    {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 3, 1);
                        debuggunnery("*** Engine4 Throttle Controls Out..");
                    }
                    if(World.Rnd().nextFloat() < 0.15F)
                    {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 3, 6);
                        debuggunnery("*** Engine4 Prop Controls Out..");
                    }
                    break;
                }
                return;
            }
            if(s.startsWith("xxlock"))
            {
                debuggunnery("Lock Construction: Hit..");
                if(s.startsWith("xxlockr1") && getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: Rudder1 Lock Shot Off..");
                    nextDMGLevels(3, 2, "Rudder1_D" + chunkDamageVisible("Rudder1"), shot.initiator);
                }
                if(s.startsWith("xxlockvl") && getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: VatorL Lock Shot Off..");
                    nextDMGLevels(3, 2, "VatorL_D" + chunkDamageVisible("VatorL"), shot.initiator);
                }
                if(s.startsWith("xxlockvr") && getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: VatorR Lock Shot Off..");
                    nextDMGLevels(3, 2, "VatorR_D" + chunkDamageVisible("VatorR"), shot.initiator);
                }
                if(s.startsWith("xxlockal") && getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: AroneL Lock Shot Off..");
                    nextDMGLevels(3, 2, "AroneL_D" + chunkDamageVisible("AroneL"), shot.initiator);
                }
                if(s.startsWith("xxlockar") && getEnergyPastArmor(6.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: AroneR Lock Shot Off..");
                    nextDMGLevels(3, 2, "AroneR_D" + chunkDamageVisible("AroneR"), shot.initiator);
                }
                return;
            }
            if(s.startsWith("xxbomb"))
            {
                if(World.Rnd().nextFloat() < 0.01F && this.FM.CT.Weapons[3] != null && this.FM.CT.Weapons[3][0].haveBullets())
                {
                    debuggunnery("*** Bomb Payload Detonates..");
                    this.FM.AS.hitTank(shot.initiator, 0, 100);
                    this.FM.AS.hitTank(shot.initiator, 1, 100);
                    this.FM.AS.hitTank(shot.initiator, 2, 100);
                    this.FM.AS.hitTank(shot.initiator, 3, 100);
                    nextDMGLevels(3, 2, "CF_D0", shot.initiator);
                }
                return;
            }
            if(s.startsWith("xxoil1"))
            {
                if(getEnergyPastArmor(0.25F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                {
                    this.FM.AS.hitOil(shot.initiator, 0);
                    getEnergyPastArmor(0.22F, shot);
                    debuggunnery("Engine Module: Oil Radiator 1 Pierced..");
                }
                return;
            }
            if(s.startsWith("xxoil2"))
            {
                if(getEnergyPastArmor(0.25F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                {
                    this.FM.AS.hitOil(shot.initiator, 1);
                    getEnergyPastArmor(0.22F, shot);
                    debuggunnery("Engine Module: Oil Radiator 2 Pierced..");
                }
                return;
            }
            if(s.startsWith("xxoil3"))
            {
                if(getEnergyPastArmor(0.25F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                {
                    this.FM.AS.hitOil(shot.initiator, 0);
                    getEnergyPastArmor(0.22F, shot);
                    debuggunnery("Engine Module: Oil Radiator 3 Pierced..");
                }
                return;
            }
            if(s.startsWith("xxoil4"))
            {
                if(getEnergyPastArmor(0.25F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                {
                    this.FM.AS.hitOil(shot.initiator, 1);
                    getEnergyPastArmor(0.22F, shot);
                    debuggunnery("Engine Module: Oil Radiator 4 Pierced..");
                }
                return;
            }
            if(s.startsWith("xxspar"))
            {
                if(s.startsWith("xxsparli") && chunkDamageVisible("WingLIn") > 2 && getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F)
                {
                    debuggunnery("*** WingLIn Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLIn_D2", shot.initiator);
                }
                if(s.startsWith("xxsparri") && chunkDamageVisible("WingRIn") > 2 && getEnergyPastArmor(19.6F * World.Rnd().nextFloat(1.0F, 3F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F)
                {
                    debuggunnery("*** WingRIn Spars Damaged..");
                    nextDMGLevels(1, 2, "WingRIn_D2", shot.initiator);
                }
                if(s.startsWith("xxsparlm") && chunkDamageVisible("WingLMid") > 2 && getEnergyPastArmor(16.8F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F)
                {
                    Aircraft.debugprintln(this, "*** WingLMid Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLMid_D2", shot.initiator);
                }
                if(s.startsWith("xxsparrm") && chunkDamageVisible("WingRMid") > 2 && getEnergyPastArmor(16.8F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F)
                {
                    Aircraft.debugprintln(this, "*** WingRMid Spars Damaged..");
                    nextDMGLevels(1, 2, "WingRMid_D2", shot.initiator);
                }
                if(s.startsWith("xxsparlo") && chunkDamageVisible("WingLOut") > 2 && getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F)
                {
                    debuggunnery("*** WingLOut Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLOut_D2", shot.initiator);
                }
                if(s.startsWith("xxsparro") && chunkDamageVisible("WingROut") > 2 && getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F && World.Rnd().nextFloat() < 0.125F)
                {
                    debuggunnery("*** WingROut Spars Damaged..");
                    nextDMGLevels(1, 2, "WingROut_D2", shot.initiator);
                }
                if(s.startsWith("xxspark1") && chunkDamageVisible("Keel1") > 1 && getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)
                {
                    debuggunnery("*** Keel1 Spars Damaged..");
                    nextDMGLevels(1, 2, "Keel1_D2" + chunkDamageVisible("Keel1"), shot.initiator);
                }
                if(s.startsWith("xxsparsl") && chunkDamageVisible("StabL") > 1 && getEnergyPastArmor(11.2F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)
                {
                    debuggunnery("*** StabL Spars Damaged..");
                    nextDMGLevels(1, 2, "StabL_D2", shot.initiator);
                }
                if(s.startsWith("xxsparsr") && chunkDamageVisible("StabR") > 1 && getEnergyPastArmor(11.2F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)
                {
                    debuggunnery("*** StabR Spars Damaged..");
                    nextDMGLevels(1, 2, "StabR_D2", shot.initiator);
                }
                if(s.startsWith("xxspart") && chunkDamageVisible("Tail1") > 1 && getEnergyPastArmor(16.6F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)
                {
                    debuggunnery("*** Tail1 Spars Damaged..");
                    nextDMGLevels(1, 2, "Tail1_D2" + chunkDamageVisible("Taill1"), shot.initiator);
                }
                return;
            }
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
            }
            if(s.endsWith("mag"))
            {
                this.FM.EI.engines[0].setMagnetoKnockOut(shot.initiator, 1);
                Aircraft.debugprintln(this, "*** Engine Module: Magneto #1 Destroyed..");
                getEnergyPastArmor(25F, shot);
            }
            return;
        }
        if(s.startsWith("xxmgun"))
        {
            if(s.endsWith("1"))
            {
                debuggunnery("Cowling Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 0);
            }
            if(s.endsWith("2"))
            {
                debuggunnery("Cowling Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 1);
            }
            if(s.endsWith("3"))
            {
                debuggunnery("Cowling Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 2);
            }
            if(s.endsWith("4"))
            {
                debuggunnery("Cowling Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 3);
            }
            if(s.endsWith("5"))
            {
                debuggunnery("Cowling Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 4);
            }
            if(s.endsWith("6"))
            {
                debuggunnery("Cowling Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 5);
            }
            if(s.endsWith("7"))
            {
                debuggunnery("Cowling Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 6);
            }
            if(s.endsWith("8"))
            {
                debuggunnery("Cowling Gun: Disabled..");
                this.FM.AS.setJamBullets(0, 7);
            }
            return;
        }
        if(s.startsWith("xxtank"))
        {
            int k = s.charAt(6) - 48;
            switch(k)
            {
            case 1:
                doHitMeATank(shot, 1);
                doHitMeATank(shot, 2);
                break;

            case 2:
                doHitMeATank(shot, 1);
                break;

            case 3:
                doHitMeATank(shot, 2);
                break;

            case 4:
                doHitMeATank(shot, 0);
                break;

            case 5:
                doHitMeATank(shot, 3);
                break;
            }
            return;
        }
        if(s.startsWith("xcf"))
        {
            if(chunkDamageVisible("CF") < 2)
                hitChunk("CF", shot);
            if(World.Rnd().nextFloat() < 0.32F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
            if(World.Rnd().nextFloat() < 0.32F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
            if(World.Rnd().nextFloat() < 0.32F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
            if(World.Rnd().nextFloat() < 0.32F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
        } else
        if(s.startsWith("xcn"))
        {
            if(chunkDamageVisible("Nose") < 2)
                hitChunk("Nose", shot);
        } else
        if(s.startsWith("xtail"))
        {
            if(chunkDamageVisible("Tail1") < 2)
                hitChunk("Tail1", shot);
        } else
        if(s.startsWith("xkeel1"))
        {
            if(chunkDamageVisible("Keel1") < 2)
                hitChunk("Keel1", shot);
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
        if(s.startsWith("xwinglmid"))
        {
            if(chunkDamageVisible("WingLMid") < 2)
                hitChunk("WingLMid", shot);
        } else
        if(s.startsWith("xwingrmid"))
        {
            if(chunkDamageVisible("WingRMid") < 2)
                hitChunk("WingRMid", shot);
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
        if(s.startsWith("xflap01"))
        {
            if(chunkDamageVisible("Flap01") < 2)
                hitChunk("Flap01", shot);
        } else
        if(s.startsWith("xflap02"))
        {
            if(chunkDamageVisible("Flap02") < 2)
                hitChunk("Flap02", shot);
        } else
        if(s.startsWith("xslatl"))
        {
            if(chunkDamageVisible("SlatL") < 2)
                hitChunk("SlatL", shot);
        } else
        if(s.startsWith("xslatr"))
        {
            if(chunkDamageVisible("SlatR") < 2)
                hitChunk("SlatR", shot);
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
        if(s.startsWith("xengine3"))
        {
            if(chunkDamageVisible("Engine3") < 2)
                hitChunk("Engine3", shot);
        } else
        if(s.startsWith("xengine4"))
        {
            if(chunkDamageVisible("Engine4") < 2)
                hitChunk("Engine4", shot);
        } else
        if(s.startsWith("xgear"))
            gearDamageFX(s);
        else
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

    private final void doHitMeATank(Shot shot, int i)
    {
        if(getEnergyPastArmor(0.2F, shot) > 0.0F)
            if(shot.power < 14100F)
            {
                if(this.FM.AS.astateTankStates[i] == 0)
                {
                    this.FM.AS.hitTank(shot.initiator, i, 1);
                    this.FM.AS.doSetTankState(shot.initiator, i, 1);
                }
                if(this.FM.AS.astateTankStates[i] > 0 && (World.Rnd().nextFloat() < 0.02F || shot.powerType == 3 && World.Rnd().nextFloat() < 0.25F))
                    this.FM.AS.hitTank(shot.initiator, i, 2);
            } else
            {
                this.FM.AS.hitTank(shot.initiator, i, World.Rnd().nextInt(0, (int)(shot.power / 56000F)));
            }
    }

    private void gearDamageFX(String s)
    {
        if(s.startsWith("xgearl"))
        {
            if(this.FM.isPlayers())
                HUD.log("Left Gear:  Hydraulic system Failed");
            kl = World.Rnd().nextFloat();
            kr = World.Rnd().nextFloat() * kl;
        } else
        if(s.startsWith("xgearr"))
        {
            if(this.FM.isPlayers())
                HUD.log("Right Gear:  Hydraulic system Failed");
            kr = World.Rnd().nextFloat();
            kl = World.Rnd().nextFloat() * kr;
        }
        this.FM.CT.GearControl = 0.4F;
        this.FM.Gears.setHydroOperable(false);
    }

    public boolean typeBomberToggleAutomation()
    {
        return false;
    }

    public void typeBomberAdjDistanceReset()
    {
        fSightCurForwardAngle = 0.0F;
    }

    public void typeBomberAdjDistancePlus()
    {
        fSightCurForwardAngle += 0.4F;
        if(fSightCurForwardAngle > 75F)
            fSightCurForwardAngle = 75F;
    }

    public void typeBomberAdjDistanceMinus()
    {
        fSightCurForwardAngle -= 0.4F;
        if(fSightCurForwardAngle < -16F)
            fSightCurForwardAngle = -16F;
    }

    public void typeBomberAdjSideslipReset()
    {
        fSightCurSideslip = 0.0F;
    }

    public void typeBomberAdjSideslipPlus()
    {
        fSightCurSideslip += 0.5D;
        if(fSightCurSideslip > 10F)
            fSightCurSideslip = 10F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip " + fSightCurSideslip);
    }

    public void typeBomberAdjSideslipMinus()
    {
        fSightCurSideslip -= 0.5D;
        if(fSightCurSideslip < -10F)
            fSightCurSideslip = -10F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip " + fSightCurSideslip);
    }

    public void typeBomberAdjAltitudeReset()
    {
        fSightCurAltitude = 1000F;
    }

    public void typeBomberAdjAltitudePlus()
    {
        fSightCurAltitude += 10F;
        if(fSightCurAltitude > 6000F)
            fSightCurAltitude = 6000F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] {
            new Integer((int)fSightCurAltitude)
        });
    }

    public void typeBomberAdjAltitudeMinus()
    {
        fSightCurAltitude -= 10F;
        if(fSightCurAltitude < 500F)
            fSightCurAltitude = 500F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] {
            new Integer((int)fSightCurAltitude)
        });
    }

    public void typeBomberAdjSpeedReset()
    {
        fSightCurSpeed = 150F;
    }

    public void typeBomberAdjSpeedPlus()
    {
        fSightCurSpeed += 5F;
        if(fSightCurSpeed > 650F)
            fSightCurSpeed = 650F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] {
            new Integer((int)fSightCurSpeed)
        });
    }

    public void typeBomberAdjSpeedMinus()
    {
        fSightCurSpeed -= 5F;
        if(fSightCurSpeed < 150F)
            fSightCurSpeed = 150F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] {
            new Integer((int)fSightCurSpeed)
        });
    }

    public void typeBomberUpdate(float f)
    {
        double d = ((double)fSightCurSpeed / 3.6D) * Math.sqrt((double)fSightCurAltitude * (2F / 9.81F));
        d -= (double)(fSightCurAltitude * fSightCurAltitude) * 1.419E-005D;
        fSightSetForwardAngle = (float)Math.atan(d / (double)fSightCurAltitude);
    }

    public void typeBomberReplicateToNet(NetMsgGuaranted netmsgguaranted)
        throws IOException
    {
        netmsgguaranted.writeFloat(fSightCurForwardAngle);
        netmsgguaranted.writeFloat(fSightCurSideslip);
        netmsgguaranted.writeFloat(fSightCurAltitude);
        netmsgguaranted.writeFloat(fSightCurSpeed);
    }

    public void typeBomberReplicateFromNet(NetMsgInput netmsginput)
        throws IOException
    {
        fSightCurForwardAngle = netmsginput.readFloat();
        fSightCurSideslip = netmsginput.readFloat();
        fSightCurAltitude = netmsginput.readFloat();
        fSightCurSpeed = netmsginput.readFloat();
    }

    boolean bGunUp;
    public long btme;
    public float fGunPos;
    private static float kl = 1.0F;
    private static float kr = 1.0F;
    public float fSightCurAltitude;
    public float fSightCurSpeed;
    public float fSightCurForwardAngle;
    public float fSightSetForwardAngle;
    public float fSightCurSideslip;

    static 
    {
        Class class1 = P_108xyz.class;
        Property.set(class1, "originCountry", PaintScheme.countryItaly);
    }
}
