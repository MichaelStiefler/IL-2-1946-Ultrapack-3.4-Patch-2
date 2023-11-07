package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;

public abstract class KI_108abc extends Scheme2
    implements TypeFighter, TypeBNZFighter
{
    public float getEyeLevelCorrection()
    {
        return -0.05F;
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(FM.getAltitude() < 3000F)
            hierMesh().chunkVisible("HMask1_D0", false);
        else
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Pilot1_D0"));
    }

    protected void moveFlap(float f)
    {
        hierMesh().chunkSetAngles("Flap01_D0", 0.0F, 50F * f, 0.0F);
        hierMesh().chunkSetAngles("Flap02_D0", 0.0F, 50F * f, 0.0F);
    }

    public void update(float f)
    {
        super.update(f);
        if(!this.FM.isPlayers() && this.FM.Gears.onGround())
            if(this.FM.EI.engines[1].getRPM() < 100F)
                this.FM.CT.cockpitDoorControl = 1.0F;
            else
                this.FM.CT.cockpitDoorControl = 0.0F;
        float f1 = 25F * this.FM.EI.engines[0].getControlRadiator();
        if(Math.abs(flapps[0] - f1) > 0.01F)
        {
            flapps[0] = f1;
            for(int i = 1; i <= 10; i++)
            {
                String s = "Radiator1_" + i + "_D0";
                if(i <= 5)
                    hierMesh().chunkSetAngles(s, 0.0F, -f1, 0.0F);
                else
                    hierMesh().chunkSetAngles(s, 0.0F, f1, 0.0F);
            }

        }
        f1 = 25F * this.FM.EI.engines[1].getControlRadiator();
        if(Math.abs(flapps[1] - f1) > 0.01F)
        {
            flapps[1] = f1;
            for(int j = 1; j <= 10; j++)
            {
                String s1 = "Radiator2_" + j + "_D0";
                if(j > 5)
                    hierMesh().chunkSetAngles(s1, 0.0F, -f1, 0.0F);
                else
                    hierMesh().chunkSetAngles(s1, 0.0F, f1, 0.0F);
            }

        }
        super.update(f);
    }

    public static void moveGear(HierMesh hiermesh, float f)
    {
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, -45.5F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, -45.5F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL7_D0", 0.0F, -54F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR7_D0", 0.0F, -54F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL9_D0", 0.0F, -60F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR9_D0", 0.0F, -60F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL6_D0", 0.0F, 105F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR6_D0", 0.0F, 105F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, 0.0F, -40F * f);
        hiermesh.chunkSetAngles("GearC3_D0", 0.0F, 35F * f, 0.0F);
        float f1 = Aircraft.cvt(f, 0.0F, 0.2F, 0.0F, 99F);
        float f2 = Aircraft.cvt(f, 0.0F, 0.2F, 0.0F, 99F);
        if(f1 < 0.05F)
            f1 = 0.0F;
        if(f2 < 0.05F)
            f2 = 0.0F;
        hiermesh.chunkSetAngles("GearL10_d0", 0.0F, f1, 0.0F);
        hiermesh.chunkSetAngles("GearL11_d0", 0.0F, -f1, 0.0F);
        hiermesh.chunkSetAngles("GearR10_d0", 0.0F, -f2, 0.0F);
        hiermesh.chunkSetAngles("GearR11_d0", 0.0F, f2, 0.0F);
        Aircraft.xyz[0] = 0.0F;
        Aircraft.ypr[0] = 0.0F;
        Aircraft.xyz[1] = 0.0F;
        Aircraft.ypr[1] = 0.0F;
        Aircraft.xyz[2] = 0.0F;
        Aircraft.ypr[2] = 0.0F;
        Aircraft.xyz[1] = f * -0.2F;
        hiermesh.chunkSetLocate("GearR8_D0", Aircraft.xyz, Aircraft.ypr);
        Aircraft.xyz[1] = f * -0.2F;
        hiermesh.chunkSetLocate("GearL8_D0", Aircraft.xyz, Aircraft.ypr);
    }

    protected void moveGear(float f)
    {
        moveGear(hierMesh(), f);
    }

    protected void moveAileron(float f)
    {
        if(f > 0.0F)
        {
            hierMesh().chunkSetAngles("AroneL_D0", 0.0F, 24F * f, 0.0F);
            hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -16F * f, 0.0F);
        } else
        {
            hierMesh().chunkSetAngles("AroneL_D0", 0.0F, 16F * f, 0.0F);
            hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -24F * f, 0.0F);
        }
    }

    protected void moveRudder(float f)
    {
        hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -27F * f, 0.0F);
    }

    protected void moveElevator(float f)
    {
        if(f > 0.0F)
        {
            hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -25F * f, 0.0F);
            hierMesh().chunkSetAngles("VatorR_D0", 0.0F, -25F * f, 0.0F);
        } else
        {
            hierMesh().chunkSetAngles("VatorL_D0", 0.0F, -18F * f, 0.0F);
            hierMesh().chunkSetAngles("VatorR_D0", 0.0F, -18F * f, 0.0F);
        }
    }

    public void moveWheelSink()
    {
        suspL = 0.9F * suspL + 0.1F * this.FM.Gears.gWheelSinking[0];
        suspR = 0.9F * suspR + 0.1F * this.FM.Gears.gWheelSinking[1];
        if(suspL > 0.035F)
            suspL = 0.035F;
        if(suspR > 0.035F)
            suspR = 0.035F;
        if(suspL < 0.0F)
            suspL = 0.0F;
        if(suspR < 0.0F)
            suspR = 0.0F;
        Aircraft.xyz[0] = 0.0F;
        Aircraft.ypr[0] = 0.0F;
        Aircraft.xyz[1] = 0.0F;
        Aircraft.ypr[1] = 0.0F;
        Aircraft.xyz[2] = 0.0F;
        Aircraft.ypr[2] = 0.0F;
        float f = -793.5F;
        Aircraft.xyz[1] = suspL * 3.5F;
        hierMesh().chunkSetLocate("GearL2_D0", Aircraft.xyz, Aircraft.ypr);
        hierMesh().chunkSetAngles("GearL4_D0", 0.0F, -suspL * f, 0.0F);
        hierMesh().chunkSetAngles("GearL5_D0", 0.0F, suspL * f, 0.0F);
        Aircraft.xyz[1] = suspR * 3.5F;
        hierMesh().chunkSetLocate("GearR2_D0", Aircraft.xyz, Aircraft.ypr);
        hierMesh().chunkSetAngles("GearR4_D0", 0.0F, -suspR * f, 0.0F);
        hierMesh().chunkSetAngles("GearR5_D0", 0.0F, suspR * f, 0.0F);
    }

    public void moveSteering(float f)
    {
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xx"))
        {
            if(s.startsWith("xxarmor"))
            {
                debuggunnery("Armor: Hit..");
                if(s.endsWith("2"))
                    getEnergyPastArmor(World.Rnd().nextFloat(1.96F, 3.4839F), shot);
                else
                if(s.endsWith("3"))
                {
                    if(point3d.z < 0.08D)
                        getEnergyPastArmor(8.585D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                    else
                    if(point3d.z < 0.09D)
                        debuggunnery("Armor: Bullet Went Through a Pilofacturing Hole..");
                    else
                    if(point3d.y > 0.175D && point3d.y < 0.287D && point3d.z < 0.177D)
                        debuggunnery("Armor: Bullet Went Through a Pilofacturing Hole..");
                    else
                    if(point3d.y > -0.334D && point3d.y < -0.177D && point3d.z < 0.204D)
                        debuggunnery("Armor: Bullet Went Through a Pilofacturing Hole..");
                    else
                    if(point3d.z > 0.288D && Math.abs(point3d.y) < 0.077D)
                        getEnergyPastArmor((double)World.Rnd().nextFloat(8.5F, 12.46F) / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                    else
                        getEnergyPastArmor(10.51D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                } else
                if(s.endsWith("1"))
                {
                    getEnergyPastArmor(World.Rnd().nextFloat(20F, 30F), shot);
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
                    debuggunnery("Armor: Armor Glass: Hit..");
                    if(shot.power <= 0.0F)
                    {
                        debuggunnery("Armor: Armor Glass: Bullet Stopped..");
                        if(World.Rnd().nextFloat() < 0.96F)
                            doRicochetBack(shot);
                    }
                }
            } else
            if(s.startsWith("xxcontrol"))
            {
                debuggunnery("Controls: Hit..");
                if(World.Rnd().nextFloat() >= 0.99F)
                {
                    int i = (new Integer(s.substring(9))).intValue();
                    switch(i)
                    {
                    case 2:
                    case 3:
                        if(getEnergyPastArmor(3.5F, shot) > 0.0F)
                        {
                            this.FM.AS.setControlsDamage(shot.initiator, 2);
                            debuggunnery("Controls: Rudder Controls: Fuselage Line Destroyed..");
                        }
                        break;

                    case 0:
                    case 1:
                        if(getEnergyPastArmor(0.002F, shot) > 0.0F)
                        {
                            this.FM.AS.setControlsDamage(shot.initiator, 1);
                            debuggunnery("Controls: Elevator Controls: Disabled / Strings Broken..");
                        }
                        break;

                    case 5:
                    case 6:
                    case 7:
                    case 8:
                        if(getEnergyPastArmor(0.12F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F)
                        {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                            debuggunnery("Controls: Aileron Controls: Disabled..");
                        }
                        break;
                    }
                }
            } else
            if(s.startsWith("xxspar"))
            {
                debuggunnery("Spar Construction: Hit..");
                if(s.startsWith("xxspart") && chunkDamageVisible("Tail1") > 2 && getEnergyPastArmor(3.5F / (float)Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F && World.Rnd().nextFloat() < 0.75F)
                {
                    debuggunnery("Spar Construction: Tail1 Spars Broken in Half..");
                    nextDMGLevels(1, 2, "Tail1_D3", shot.initiator);
                }
                if(s.startsWith("xxsparli") && chunkDamageVisible("WingLIn") > 2 && getEnergyPastArmor(19.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingLIn Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if(s.startsWith("xxsparri") && chunkDamageVisible("WingRIn") > 2 && getEnergyPastArmor(19.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingRIn Spars Damaged..");
                    nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if(s.startsWith("xxsparlm") && chunkDamageVisible("WingLMid") > 2 && getEnergyPastArmor(17.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingLMid Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if(s.startsWith("xxsparrm") && chunkDamageVisible("WingRMid") > 2 && getEnergyPastArmor(17.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingRMid Spars Damaged..");
                    nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if(s.startsWith("xxsparlo") && chunkDamageVisible("WingLOut") > 2 && getEnergyPastArmor(13.2F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingLOut Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if(s.startsWith("xxsparro") && chunkDamageVisible("WingROut") > 2 && getEnergyPastArmor(13.2F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingROut Spars Damaged..");
                    nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
            } else
            if(s.startsWith("xxlock"))
            {
                debuggunnery("Lock Construction: Hit..");
                if(s.startsWith("xxlockr"))
                {
                    int j = s.charAt(6) - 48;
                    if(getEnergyPastArmor(6.56F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F)
                        if(j < 3)
                        {
                            debuggunnery("Lock Construction: Rudder1 Lock Shot Off..");
                            nextDMGLevels(3, 2, "Rudder1_D" + chunkDamageVisible("Rudder1"), shot.initiator);
                        } else
                        {
                            debuggunnery("Lock Construction: Rudder2 Lock Shot Off..");
                            nextDMGLevels(3, 2, "Rudder2_D" + chunkDamageVisible("Rudder2"), shot.initiator);
                        }
                }
                if(s.startsWith("xxlockvl") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: VatorL Lock Shot Off..");
                    nextDMGLevels(3, 2, "VatorL_D" + chunkDamageVisible("VatorL"), shot.initiator);
                }
                if(s.startsWith("xxlockvr") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: VatorR Lock Shot Off..");
                    nextDMGLevels(3, 2, "VatorR_D" + chunkDamageVisible("VatorR"), shot.initiator);
                }
            } else
            if(s.startsWith("xxeng"))
            {
                int k = s.charAt(5) - 49;
                debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Hit..");
                if(s.endsWith("prop"))
                {
                    if(getEnergyPastArmor(2.0F, shot) > 0.0F && World.Rnd().nextFloat() < 0.8F)
                        if(World.Rnd().nextFloat() < 0.5F)
                        {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, k, 3);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Prop Governor Hit, Disabled..");
                        } else
                        {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, k, 4);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Prop Governor Hit, Damaged..");
                        }
                } else
                if(s.endsWith("eqpt"))
                {
                    if(getEnergyPastArmor(4.6F, shot) > 0.0F)
                        if(World.Rnd().nextFloat() < 0.5F)
                        {
                            this.FM.EI.engines[k].setEngineStuck(shot.initiator);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Bullet Jams Reductor Gear..");
                        } else
                        {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, k, 3);
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, k, 4);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Reductor Gear Damaged, Prop Governor Failed..");
                        }
                } else
                if(s.endsWith("supc"))
                {
                    if(getEnergyPastArmor(2.0F, shot) > 0.0F)
                    {
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, k, 0);
                        debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Supercharger Disabled..");
                    }
                } else
                if(s.endsWith("feed"))
                {
                    if(getEnergyPastArmor(3.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F && this.FM.EI.engines[k].getPowerOutput() > 0.7F)
                    {
                        this.FM.AS.hitEngine(shot.initiator, k, 100);
                        debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Pressurized Fuel Line Pierced, Fuel Flamed..");
                    }
                } else
                if(s.endsWith("fuel"))
                {
                    if(getEnergyPastArmor(1.1F, shot) > 0.0F)
                    {
                        this.FM.EI.engines[k].setEngineStops(shot.initiator);
                        debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Fuel Line Stalled, Engine Stalled..");
                    }
                } else
                if(s.endsWith("case"))
                {
                    if(getEnergyPastArmor(3.1F, shot) > 0.0F)
                    {
                        if(World.Rnd().nextFloat() < shot.power / 175000F)
                        {
                            this.FM.AS.setEngineStuck(shot.initiator, k);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Bullet Jams Crank Ball Bearing..");
                        }
                        if(World.Rnd().nextFloat() < shot.power / 50000F)
                        {
                            this.FM.AS.hitEngine(shot.initiator, k, 2);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Crank Case Hit, Readyness Reduced to " + this.FM.EI.engines[k].getReadyness() + "..");
                        }
                        this.FM.EI.engines[k].setReadyness(shot.initiator, this.FM.EI.engines[k].getReadyness() - World.Rnd().nextFloat(0.0F, shot.power / 48000F));
                        debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Crank Case Hit, Readyness Reduced to " + this.FM.EI.engines[k].getReadyness() + "..");
                    }
                    getEnergyPastArmor(World.Rnd().nextFloat(22.5F, 33.6F), shot);
                } else
                if(s.endsWith("cyls"))
                {
                    if(getEnergyPastArmor(2.1F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[k].getCylindersRatio() * 1.0F)
                    {
                        this.FM.EI.engines[k].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int)(shot.power / 4800F)));
                        debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Cylinders Hit, " + this.FM.EI.engines[k].getCylindersOperable() + "/" + this.FM.EI.engines[k].getCylinders() + " Left..");
                        if(World.Rnd().nextFloat() < shot.power / 24000F)
                        {
                            this.FM.AS.hitEngine(shot.initiator, k, 3);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Cylinders Hit, Engine Fires..");
                        }
                        if(World.Rnd().nextFloat() < 0.01F)
                        {
                            this.FM.AS.setEngineStuck(shot.initiator, k);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Bullet Jams Piston Head..");
                        }
                        getEnergyPastArmor(22.5F, shot);
                    }
                } else
                if(s.endsWith("mag1") || s.endsWith("mag2"))
                {
                    if(World.Rnd().nextFloat() < 0.5F)
                    {
                        int j2 = s.charAt(9) - 49;
                        this.FM.EI.engines[k].setMagnetoKnockOut(shot.initiator, j2);
                        debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Magneto " + j2 + " Destroyed..");
                    }
                } else
                if(s.endsWith("oil"))
                {
                    this.FM.AS.hitOil(shot.initiator, k);
                    debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Oil Radiator Hit..");
                }
            } else
            if(s.startsWith("xxoil"))
            {
                if(getEnergyPastArmor(World.Rnd().nextFloat(1.2F, 2.345F), shot) > 0.0F)
                {
                    int l = s.charAt(5) - 49;
                    this.FM.AS.hitOil(shot.initiator, l);
                    getEnergyPastArmor(0.22F, shot);
                    debuggunnery("Engine Module (" + (l == 0 ? "Left" : "Right") + "): Oil Tank Pierced..");
                }
            } else
            if(s.startsWith("xxw"))
            {
                if(getEnergyPastArmor(World.Rnd().nextFloat(0.1F, 0.75F), shot) > 0.0F)
                {
                    int i1 = s.charAt(3) - 49;
                    if(this.FM.AS.astateEngineStates[i1] == 0)
                    {
                        debuggunnery("Engine Module (" + (i1 == 0 ? "Left" : "Right") + "): Water Radiator Pierced..");
                        this.FM.AS.hitEngine(shot.initiator, i1, 2);
                        this.FM.AS.doSetEngineState(shot.initiator, i1, 2);
                    }
                    getEnergyPastArmor(2.22F, shot);
                }
            } else
            if(s.startsWith("xxtank"))
            {
                int j1 = s.charAt(6) - 49;
                if(getEnergyPastArmor(World.Rnd().nextFloat(1.0F, 2.23F), shot) > 0.0F && World.Rnd().nextFloat() < 0.1F && j1 < this.FM.AS.astateTankStates.length)
                {
                    if(this.FM.AS.astateTankStates[j1] == 0)
                    {
                        debuggunnery("Fuel Tank " + (j1 + 1) + ": Pierced..");
                        this.FM.AS.hitTank(shot.initiator, j1, 1);
                        this.FM.AS.doSetTankState(shot.initiator, j1, 1);
                    } else
                    if(this.FM.AS.astateTankStates[j1] == 1)
                    {
                        debuggunnery("Fuel Tank " + (j1 + 1) + ": Pierced..");
                        this.FM.AS.hitTank(shot.initiator, j1, 1);
                        this.FM.AS.doSetTankState(shot.initiator, j1, 2);
                    }
                    if(shot.powerType == 3 && World.Rnd().nextFloat() < 0.2F)
                    {
                        this.FM.AS.hitTank(shot.initiator, 2, 2);
                        debuggunnery("Fuel Tank " + (j1 + 1) + ": Hit..");
                    }
                }
            } else
            if(s.startsWith("xxhyd"))
            {
                if(getEnergyPastArmor(World.Rnd().nextFloat(0.25F, 12.39F), shot) > 0.0F)
                {
                    debuggunnery("Hydro System: Disabled..");
                    this.FM.AS.setInternalDamage(shot.initiator, 0);
                }
            } else
            if(s.startsWith("xxpanel"))
            {
                if(World.Rnd().nextFloat() < 0.12F)
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
                if(World.Rnd().nextFloat() < 0.12F)
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
                if(World.Rnd().nextFloat() < 0.12F)
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
            } else
            {
                if(s.startsWith("xxbgun"))
                {
                    debuggunnery("Belly gun disabled");
                    this.FM.AS.setJamBullets(0, 0);
                    getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 12.96F), shot);
                }
                if(s.startsWith("xxmaingun"))
                {
                    debuggunnery("main nose gun disabled..");
                    this.FM.AS.setJamBullets(1, 0);
                    getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 24.6F), shot);
                }
            }
            if(s.equals("xxglasstop"))
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
            else
            if(s.equals("xxglassfront"))
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
            else
            if(s.equals("xxpanel"))
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
            else
            if(s.equals("xxrevi"))
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
        } else
        if(s.startsWith("xcf") || s.startsWith("xcockpit"))
        {
            if(chunkDamageVisible("CF") < 3)
                hitChunk("CF", shot);
        } else
        if(s.startsWith("xengine"))
        {
            int k1 = s.charAt(7) - 48;
            if(chunkDamageVisible("Engine" + k1) < 2)
                hitChunk("Engine" + k1, shot);
        } else
        if(s.startsWith("xtail"))
        {
            if(chunkDamageVisible("Tail1") < 3)
                hitChunk("Tail1", shot);
        } else
        if(s.startsWith("xkeel"))
        {
            int l1 = s.charAt(5) - 48;
            if(chunkDamageVisible("Keel" + l1) < 3)
                hitChunk("Keel" + l1, shot);
            if(hierMesh().isChunkVisible("keel1_d2"))
                hierMesh().chunkVisible("Wire_D0", false);
        } else
        if(s.startsWith("xrudder"))
        {
            int i2 = s.charAt(7) - 48;
            if(chunkDamageVisible("Rudder" + i2) < 1)
                hitChunk("Rudder" + i2, shot);
        } else
        if(s.startsWith("xstab"))
        {
            if(s.startsWith("xstabl") && chunkDamageVisible("StabL") < 3)
                hitChunk("StabL", shot);
            if(s.startsWith("xstabr") && chunkDamageVisible("StabR") < 3)
                hitChunk("StabR", shot);
        } else
        if(s.startsWith("xvator"))
        {
            if(s.startsWith("xvatorl") && chunkDamageVisible("VatorL") < 1)
                hitChunk("VatorL", shot);
            if(s.startsWith("xvatorr") && chunkDamageVisible("VatorR") < 1)
                hitChunk("VatorR", shot);
        } else
        if(s.startsWith("xwing"))
        {
            if(s.startsWith("xwinglin") && chunkDamageVisible("WingLIn") < 3)
                hitChunk("WingLIn", shot);
            if(s.startsWith("xwingrin") && chunkDamageVisible("WingRIn") < 3)
                hitChunk("WingRIn", shot);
            if(s.startsWith("xwinglmid") && chunkDamageVisible("WingLMid") < 3)
                hitChunk("WingLMid", shot);
            if(s.startsWith("xwingrmid") && chunkDamageVisible("WingRMid") < 3)
                hitChunk("WingRMid", shot);
            if(s.startsWith("xwinglout") && chunkDamageVisible("WingLOut") < 3)
                hitChunk("WingLOut", shot);
            if(s.startsWith("xwingrout") && chunkDamageVisible("WingROut") < 3)
                hitChunk("WingROut", shot);
        } else
        if(s.startsWith("xarone"))
        {
            if(s.startsWith("xaronel"))
                hitChunk("AroneL", shot);
            if(s.startsWith("xaroner"))
                hitChunk("AroneR", shot);
        } else
        if(s.startsWith("xgear"))
        {
            if(s.endsWith("1") && World.Rnd().nextFloat() < 0.05F)
            {
                debuggunnery("Hydro System: Disabled..");
                this.FM.AS.setInternalDamage(shot.initiator, 0);
            }
            if(s.endsWith("2") && World.Rnd().nextFloat() < 0.1F && getEnergyPastArmor(World.Rnd().nextFloat(6.8F, 29.35F), shot) > 0.0F)
            {
                debuggunnery("Undercarriage: Stuck..");
                this.FM.AS.setInternalDamage(shot.initiator, 3);
            }
        } else
        if(s.startsWith("xpilot") || s.startsWith("xhead"))
        {
            byte byte0 = 0;
            int k2;
            if(s.endsWith("a"))
            {
                byte0 = 1;
                k2 = s.charAt(6) - 49;
            } else
            if(s.endsWith("b"))
            {
                byte0 = 2;
                k2 = s.charAt(6) - 49;
            } else
            {
                k2 = s.charAt(5) - 49;
            }
            hitFlesh(k2, shot, byte0);
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
        }
    }

    public void moveCockpitDoor(float f)
    {
        resetYPRmodifier();
        hierMesh().chunkSetAngles("Blister1_D0", 0.0F, 90F * f, 0.0F);
        if(Config.isUSE_RENDER())
        {
            if(Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null)
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            setDoorSnd(f);
        }
    }

    protected boolean cutFM(int i, int j, Actor actor)
    {
        switch(i)
        {
        case 10:
            doWreck("GearR6_D0");
            this.FM.Gears.hitRightGear();
            break;

        case 9:
            doWreck("GearL6_D0");
            this.FM.Gears.hitLeftGear();
            break;
        }
        return super.cutFM(i, j, actor);
    }

    private void doWreck(String s)
    {
    }

    float suspR;
    float suspL;
    private float flapps[] = {
        0.0F, 0.0F
    };

    static 
    {
        Class class1 = KI_108abc.class;
        Property.set(class1, "originCountry", PaintScheme.countryJapan);
    }
}
