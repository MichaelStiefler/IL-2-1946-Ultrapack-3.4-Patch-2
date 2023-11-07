package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;

public abstract class KI_98 extends Scheme1
    implements TypeFighter
{

    public KI_98()
    {
        flapps = 1.0F;
    }

    protected void moveAileron(float f)
    {
        if(f > 0.0F)
        {
            hierMesh().chunkSetAngles("AroneL_D0", 0.0F, 15F * f, 0.0F);
            hierMesh().chunkSetAngles("AroneR_D0", 0.0F, 20F * f, 0.0F);
        } else
        {
            hierMesh().chunkSetAngles("AroneL_D0", 0.0F, 20F * f, 0.0F);
            hierMesh().chunkSetAngles("AroneR_D0", 0.0F, 15F * f, 0.0F);
        }
    }

    protected void moveElevator(float f)
    {
        if(f > 0.0F)
            hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -30F * f, 0.0F);
        else
            hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -25F * f, 0.0F);
    }

    protected void moveRudder(float f)
    {
        hierMesh().chunkSetAngles("rudder1_D0", 0.0F, -20F * f, 0.0F);
        hierMesh().chunkSetAngles("rudder2_D0", 0.0F, -20F * f, 0.0F);
        if(FM.CT.getGear() > 0.98F)
            hierMesh().chunkSetAngles("GearC33_D0", 0.0F, 30F * f, 0.0F);
        super.moveRudder(f);
    }

    public static void moveGear(HierMesh hiermesh, float f)
    {
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, Aircraft.cvt(f, 0.02F, 0.92F, 0.0F, -101F), 0.0F);
        hiermesh.chunkSetAngles("GearC6_D0", 0.0F, Aircraft.cvt(f, 0.02F, 0.92F, 0.0F, -25F), 0.0F);
        hiermesh.chunkSetAngles("GearC7_D0", 0.0F, Aircraft.cvt(f, 0.02F, 0.92F, 0.0F, 175F), 0.0F);
        hiermesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.05F, 0.0F, -70F), 0.0F);
        hiermesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.05F, 0.0F, 70F), 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(f, 0.02F, 0.93F, 0.0F, -97.5F), 0.0F);
        Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0.0F;
        Aircraft.xyz[0] = Aircraft.cvt(f, 0.02F, 0.89F, 0.0F, 0.12F);
        hiermesh.chunkSetLocate("GearL9_D0", Aircraft.xyz, Aircraft.ypr);
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, 0.0F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.07F, 0.0F, -65F), 0.0F);
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.07F, 0.0F, 65F), 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(f, 0.02F, 0.92F, 0.0F, -97.5F), 0.0F);
        Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0.0F;
        Aircraft.xyz[0] = Aircraft.cvt(f, 0.41F, 0.99F, 0.0F, 0.12F);
        hiermesh.chunkSetLocate("GearR9_D0", Aircraft.xyz, Aircraft.ypr);
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, 0.0F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.07F, 0.0F, 65F), 0.0F);
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.07F, 0.0F, -65F), 0.0F);
        Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0.0F;
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.02F, 0.61F, 0.0F, 0.09F);
        hiermesh.chunkSetLocate("gearpL_D0", Aircraft.xyz, Aircraft.ypr);
        Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0.0F;
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.41F, 0.99F, 0.0F, 0.09F);
        hiermesh.chunkSetLocate("gearpR_D0", Aircraft.xyz, Aircraft.ypr);
    }

    protected void moveGear(float f)
    {
        moveGear(hierMesh(), f);
    }

    public void moveWheelSink()
    {
        resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(FM.Gears.gWheelSinking[2], 0.0F, 0.231F, 0.0F, 0.231F);
        hierMesh().chunkSetLocate("GearC3_D0", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
        Aircraft.ypr[1] = Aircraft.cvt(FM.Gears.gWheelSinking[2], 0.0F, 0.231F, 0.0F, 50F);
        hierMesh().chunkSetLocate("GearC8_D0", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
        Aircraft.ypr[1] = Aircraft.cvt(FM.Gears.gWheelSinking[2], 0.0F, 0.231F, 0.0F, -50F);
        hierMesh().chunkSetLocate("GearC9_D0", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(FM.Gears.gWheelSinking[0], 0.0F, 0.231F, 0.0F, 0.231F);
        hierMesh().chunkSetLocate("GearL3_D0", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
        Aircraft.ypr[1] = Aircraft.cvt(FM.Gears.gWheelSinking[0], 0.0F, 0.231F, 0.0F, 50F);
        hierMesh().chunkSetLocate("GearL6_D0", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
        Aircraft.ypr[1] = Aircraft.cvt(FM.Gears.gWheelSinking[0], 0.0F, 0.231F, 0.0F, -50F);
        hierMesh().chunkSetLocate("GearL7_D0", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(FM.Gears.gWheelSinking[1], 0.0F, 0.231F, 0.0F, 0.231F);
        hierMesh().chunkSetLocate("GearR3_D0", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
        Aircraft.ypr[1] = Aircraft.cvt(FM.Gears.gWheelSinking[1], 0.0F, 0.231F, 0.0F, 50F);
        hierMesh().chunkSetLocate("GearR6_D0", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
        Aircraft.ypr[1] = Aircraft.cvt(FM.Gears.gWheelSinking[1], 0.0F, 0.231F, 0.0F, -50F);
        hierMesh().chunkSetLocate("GearR7_D0", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
    }

    public void moveCockpitDoor(float f)
    {
        resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.5F);
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.01F, 0.5F, 0.0F, 0.02F);
        Aircraft.ypr[2] = Aircraft.cvt(f, 0.01F, 0.1F, 0.0F, 0.0F);
        hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        resetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.0F);
        Aircraft.ypr[2] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, -1F);
        hierMesh().chunkSetLocate("Pilot1_D0", Aircraft.xyz, Aircraft.ypr);
        if(Config.isUSE_RENDER())
        {
            if(Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null)
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            setDoorSnd(f);
        }
    }

    protected void moveFlap(float f)
    {
        float f1 = 35F * f;
        hierMesh().chunkSetAngles("Flap01_D0", 0.0F, f1, 0.0F);
        hierMesh().chunkSetAngles("Flap02_D0", 0.0F, f1, 0.0F);
        hierMesh().chunkSetAngles("Flap03_D0", 0.0F, f1, 0.0F);
        hierMesh().chunkSetAngles("Flap04_D0", 0.0F, f1, 0.0F);
    }

    public void update(float f)
    {
        super.update(f);
        float f1 = FM.EI.engines[0].getControlRadiator();
        if(Math.abs(flapps - f1) > 0.01F)
        {
            flapps = f1;
            for(int i = 1; i < 9; i++)
                hierMesh().chunkSetAngles("Cowflap" + i + "_D0", 0.0F, 40F * (1.0F - f1), 0.0F);

        }
    }

    public void doMurderPilot(int i)
    {
        switch(i)
        {
        case 0:
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            hierMesh().chunkVisible("HMask1_D0", false);
            break;
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xx"))
        {
            if(s.startsWith("xxarmor"))
            {
                if(s.endsWith("p1"))
                {
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 2);
                    if(getEnergyPastArmor((double)World.Rnd().nextFloat(32.5F, 65F) / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) < 0.0F)
                        doRicochetBack(shot);
                } else
                if(s.endsWith("p2"))
                    getEnergyPastArmor(13.13D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                else
                if(s.endsWith("p3"))
                    getEnergyPastArmor((double)World.Rnd().nextFloat(8.7F, 9.81F) / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                if(s.endsWith("p4"))
                    getEnergyPastArmor((double)World.Rnd().nextFloat(8.7F, 9.81F) / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                return;
            }
            if(s.startsWith("xxcanon0"))
            {
                int i = s.charAt(8) - 49;
                if(getEnergyPastArmor(6.29F, shot) > 0.0F)
                {
                    debuggunnery("Armament: Cannon (" + i + ") Disabled..");
                    FM.AS.setJamBullets(1, i);
                    getEnergyPastArmor(World.Rnd().nextFloat(0.5F, 23.325F), shot);
                }
                return;
            }
            if(s.startsWith("xxcontrols"))
            {
                debuggunnery("Controls: Hit..");
                int j = s.charAt(10) - 48;
                switch(j)
                {
                default:
                    break;

                case 1:
                case 2:
                case 3:
                case 4:
                    if(getEnergyPastArmor(0.99F, shot) > 0.0F && World.Rnd().nextFloat() < 0.175F)
                    {
                        debuggunnery("Controls: Ailerones Controls: Out..");
                        FM.AS.setControlsDamage(shot.initiator, 0);
                    }
                    break;

                case 5:
                case 6:
                    if(getEnergyPastArmor(0.22F, shot) > 0.0F && World.Rnd().nextFloat() < 0.275F)
                    {
                        debuggunnery("Controls: Rudder Controls: Disabled / Strings Broken..");
                        FM.AS.setControlsDamage(shot.initiator, 2);
                    }
                    break;

                case 7:
                    if(getEnergyPastArmor(4.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.175F)
                    {
                        debuggunnery("Controls: Elevator Controls: Disabled..");
                        FM.AS.setControlsDamage(shot.initiator, 1);
                    }
                    break;

                case 8:
                    if(getEnergyPastArmor(3.2F, shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** Control Column: Hit, Controls Destroyed..");
                        FM.AS.setControlsDamage(shot.initiator, 2);
                        FM.AS.setControlsDamage(shot.initiator, 1);
                        FM.AS.setControlsDamage(shot.initiator, 0);
                    }
                    break;

                case 9:
                    if(getEnergyPastArmor(0.1F, shot) > 0.0F)
                    {
                        FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 8);
                        FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                        FM.AS.setEngineSpecificDamage(shot.initiator, 0, 6);
                        Aircraft.debugprintln(this, "*** Throttle Quadrant: Hit, Engine Controls Disabled..");
                    }
                    break;
                }
                return;
            }
            if(s.startsWith("xxeng1"))
            {
                if(s.endsWith("case"))
                {
                    if(getEnergyPastArmor(0.2F, shot) > 0.0F)
                    {
                        if(World.Rnd().nextFloat() < shot.power / 140000F)
                        {
                            FM.AS.setEngineStuck(shot.initiator, 0);
                            Aircraft.debugprintln(this, "*** Engine Crank Case Hit - Engine Stucks..");
                        }
                        if(World.Rnd().nextFloat() < shot.power / 85000F)
                        {
                            FM.AS.hitEngine(shot.initiator, 0, 2);
                            Aircraft.debugprintln(this, "*** Engine Crank Case Hit - Engine Damaged..");
                        }
                    } else
                    if(World.Rnd().nextFloat() < 0.01F)
                    {
                        FM.EI.engines[0].setCyliderKnockOut(shot.initiator, 1);
                    } else
                    {
                        FM.EI.engines[0].setReadyness(shot.initiator, FM.EI.engines[0].getReadyness() - 0.002F);
                        Aircraft.debugprintln(this, "*** Engine Crank Case Hit - Readyness Reduced to " + FM.EI.engines[0].getReadyness() + "..");
                    }
                    getEnergyPastArmor(12F, shot);
                }
                if(s.endsWith("cyls"))
                {
                    if(getEnergyPastArmor(5.85F, shot) > 0.0F && World.Rnd().nextFloat() < FM.EI.engines[0].getCylindersRatio() * 0.75F)
                    {
                        FM.EI.engines[0].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int)(shot.power / 19000F)));
                        Aircraft.debugprintln(this, "*** Engine Cylinders Hit, " + FM.EI.engines[0].getCylindersOperable() + "/" + FM.EI.engines[0].getCylinders() + " Left..");
                        if(World.Rnd().nextFloat() < shot.power / 48000F)
                        {
                            FM.AS.hitEngine(shot.initiator, 0, 2);
                            Aircraft.debugprintln(this, "*** Engine Cylinders Hit - Engine Fires..");
                        }
                    }
                    getEnergyPastArmor(25F, shot);
                }
                if(s.endsWith("gear"))
                {
                    if(getEnergyPastArmor(0.1F, shot) > 0.0F)
                    {
                        if(World.Rnd().nextFloat(0.0F, 26700F) < shot.power)
                            FM.AS.setEngineSpecificDamage(shot.initiator, 0, 4);
                        if(World.Rnd().nextFloat(0.0F, 26700F) < shot.power)
                            FM.AS.setEngineSpecificDamage(shot.initiator, 0, 0);
                        if(World.Rnd().nextFloat(0.0F, 26700F) < shot.power)
                            FM.AS.setEngineSpecificDamage(shot.initiator, 0, 6);
                        if(World.Rnd().nextFloat(0.0F, 26700F) < shot.power)
                            FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                    }
                    getEnergyPastArmor(2.0F, shot);
                }
                if(s.endsWith("prop") && getEnergyPastArmor(0.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F)
                    FM.EI.engines[0].setKillPropAngleDevice(shot.initiator);
                return;
            }
            if(s.startsWith("xxlock"))
            {
                debuggunnery("Lock Construction: Hit..");
                if(s.startsWith("xxlockrl") && getEnergyPastArmor(2.5F, shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: Rudder1 Lock Shot Off..");
                    nextDMGLevels(3, 2, "Rudder1_D" + chunkDamageVisible("Rudder1"), shot.initiator);
                }
                if(s.startsWith("xxlockrr") && getEnergyPastArmor(2.5F, shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: Rudder2 Lock Shot Off..");
                    nextDMGLevels(3, 2, "Rudder2_D" + chunkDamageVisible("Rudder2"), shot.initiator);
                }
                if(s.startsWith("xxlockvl") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: VatorL Lock Shot Off..");
                    nextDMGLevels(3, 2, "VatorL_D" + chunkDamageVisible("VatorL"), shot.initiator);
                }
                if(s.startsWith("xxlockal") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: AroneL Lock Shot Off..");
                    nextDMGLevels(3, 2, "AroneL_D" + chunkDamageVisible("AroneL"), shot.initiator);
                }
                if(s.startsWith("xxlockar") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: AroneR Lock Shot Off..");
                    nextDMGLevels(3, 2, "AroneR_D" + chunkDamageVisible("AroneR"), shot.initiator);
                }
                return;
            }
            if(s.startsWith("xxmgun0"))
            {
                int k = s.charAt(7) - 49;
                if(getEnergyPastArmor(0.75F, shot) > 0.0F)
                {
                    debuggunnery("Armament: Machine Gun (" + k + ") Disabled..");
                    FM.AS.setJamBullets(0, k);
                    getEnergyPastArmor(World.Rnd().nextFloat(0.5F, 23.325F), shot);
                }
                return;
            }
            if(s.startsWith("xxoil"))
            {
                if(getEnergyPastArmor(0.25F, shot) > 0.0F && World.Rnd().nextFloat() < 0.125F)
                {
                    FM.AS.hitOil(shot.initiator, 0);
                    getEnergyPastArmor(0.22F, shot);
                    debuggunnery("Engine Module: Oil Tank Pierced..");
                }
                return;
            }
            if(s.startsWith("xxspar"))
            {
                debuggunnery("Spar Construction: Hit..");
                if(s.startsWith("xxsparli") && chunkDamageVisible("WingLIn") > 2 && getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingLIn Spar Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if(s.startsWith("xxsparri") && chunkDamageVisible("WingRIn") > 2 && getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingRIn Spar Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if(s.startsWith("xxsparlm") && chunkDamageVisible("WingLMid") > 2 && getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingLMid Spar Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if(s.startsWith("xxsparrm") && chunkDamageVisible("WingRMid") > 2 && getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingRMid Spar Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if(s.startsWith("xxsparlo") && chunkDamageVisible("WingLOut") > 2 && getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingLOut Spar Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if(s.startsWith("xxsparro") && chunkDamageVisible("WingROut") > 2 && getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingROut Spar Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                if(s.startsWith("xxspartl") && chunkDamageVisible("Tail1") > 2 && getEnergyPastArmor(3.86F / (float)Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                {
                    debuggunnery("Spar Construction: Tail1 Ribs Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "Tail1_D3", shot.initiator);
                }
                if(s.startsWith("xxspartr") && chunkDamageVisible("Tail2") > 2 && getEnergyPastArmor(3.86F / (float)Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                {
                    debuggunnery("Spar Construction: Tail2 Ribs Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "Tail2_D3", shot.initiator);
                }
                return;
            }
            if(s.startsWith("xxtank"))
            {
                int l = s.charAt(6) - 49;
                if(getEnergyPastArmor(0.8F, shot) > 0.0F && World.Rnd().nextFloat() < 0.45F)
                {
                    if(FM.AS.astateTankStates[l] == 0)
                    {
                        debuggunnery("Fuel Tank (" + l + "): Pierced..");
                        FM.AS.hitTank(shot.initiator, l, 1);
                        FM.AS.doSetTankState(shot.initiator, l, 1);
                    }
                    if(World.Rnd().nextFloat() < 0.008F || shot.powerType == 3 && World.Rnd().nextFloat() < 0.6F)
                    {
                        FM.AS.hitTank(shot.initiator, l, 1);
                        debuggunnery("Fuel Tank (" + l + "): Hit..");
                    }
                }
                return;
            } else
            {
                return;
            }
        }
        if(s.startsWith("xcf") || s.startsWith("xcock"))
        {
            hitChunk("CF", shot);
            return;
        }
        if(s.startsWith("xnose"))
        {
            if(chunkDamageVisible("Nose") < 2 && getEnergyPastArmor(2.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 5000F) < shot.power)
                hitChunk("Nose", shot);
        } else
        if(s.startsWith("xeng"))
        {
            if(chunkDamageVisible("Engine1") < 2 && getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 3000F) < shot.power)
                hitChunk("Engine1", shot);
        } else
        if(s.startsWith("xtail"))
        {
            if(s.startsWith("xtail1") && getEnergyPastArmor(2.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 5000F) < shot.power)
                hitChunk("Tail1", shot);
            if(s.startsWith("xtail2") && getEnergyPastArmor(2.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 5000F) < shot.power)
                hitChunk("Tail2", shot);
        } else
        if(s.startsWith("xkeel"))
        {
            if(s.startsWith("xkeel1") && getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 3000F) < shot.power)
                hitChunk("Keel1", shot);
            if(s.startsWith("xkeel2") && getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 3000F) < shot.power)
                hitChunk("Keel2", shot);
        } else
        if(s.startsWith("xrudder"))
        {
            if(s.startsWith("xrudder1") && chunkDamageVisible("Rudder1") < 1 && getEnergyPastArmor(2.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 3000F) < shot.power && getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 3000F) < shot.power)
                hitChunk("Rudder1", shot);
            if(s.startsWith("xrudder2") && chunkDamageVisible("Rudder2") < 1 && getEnergyPastArmor(2.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 3000F) < shot.power && getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 3000F) < shot.power)
                hitChunk("Rudder2", shot);
        } else
        if(s.startsWith("xstab"))
        {
            if(getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 3000F) < shot.power)
                hitChunk("StabL", shot);
        } else
        if(s.startsWith("xvator"))
        {
            if(s.startsWith("xvatorl") && chunkDamageVisible("VatorL") < 1 && getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 3000F) < shot.power)
                hitChunk("VatorL", shot);
        } else
        if(s.startsWith("xwing"))
        {
            if(s.startsWith("xwinglin") && getEnergyPastArmor(2.5F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 5000F) < shot.power)
                hitChunk("WingLIn", shot);
            if(s.startsWith("xwingrin") && getEnergyPastArmor(2.5F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 5000F) < shot.power)
                hitChunk("WingRIn", shot);
            if(s.startsWith("xwinglmid") && getEnergyPastArmor(2.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 4000F) < shot.power)
                hitChunk("WingLMid", shot);
            if(s.startsWith("xwingrmid") && getEnergyPastArmor(2.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 4000F) < shot.power)
                hitChunk("WingRMid", shot);
            if(s.startsWith("xwinglout") && getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 3000F) < shot.power)
                hitChunk("WingLOut", shot);
            if(s.startsWith("xwingrout") && getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat(0.0F, 3000F) < shot.power)
                hitChunk("WingROut", shot);
        } else
        if(s.startsWith("xarone"))
        {
            if(s.startsWith("xaronel") && chunkDamageVisible("AroneL") < 1)
                hitChunk("AroneL", shot);
            if(s.startsWith("xaroner") && chunkDamageVisible("AroneR") < 1)
                hitChunk("AroneR", shot);
        } else
        if(s.startsWith("xgear"))
        {
            if(World.Rnd().nextFloat() < 0.05F)
            {
                debuggunnery("Hydro System: Disabled..");
                FM.AS.setInternalDamage(shot.initiator, 0);
            }
            if(World.Rnd().nextFloat() < 0.1F && getEnergyPastArmor(World.Rnd().nextFloat(1.2F, 3.435F), shot) > 0.0F)
            {
                debuggunnery("Undercarriage: Stuck..");
                FM.AS.setInternalDamage(shot.initiator, 3);
            }
        } else
        if(s.startsWith("xpilot") || s.startsWith("xhead"))
        {
            byte byte0 = 0;
            int i1;
            if(s.endsWith("a"))
            {
                byte0 = 1;
                i1 = s.charAt(6) - 49;
            } else
            if(s.endsWith("b"))
            {
                byte0 = 2;
                i1 = s.charAt(6) - 49;
            } else
            {
                i1 = s.charAt(5) - 49;
            }
            hitFlesh(i1, shot, byte0);
        }
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(FM.getAltitude() < 3000F)
            hierMesh().chunkVisible("HMask1_D0", false);
        else
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Head1_D0"));
    }

    private float flapps;

}
