package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public abstract class FW_187 extends Scheme2
    implements TypeFighter, TypeBNZFighter, TypeStormovik
{

    public FW_187()
    {
        kangle0 = 0.0F;
        kangle1 = 0.0F;
        slpos = 0.0F;
    }

    protected void moveAileron(float f)
    {
        hierMesh().chunkSetAngles("AroneL_D0", 0.0F, 30F * f, 0.0F);
        hierMesh().chunkSetAngles("AroneR_D0", 0.0F, 30F * f, 0.0F);
    }

    public void moveCockpitDoor(float f)
    {
        hierMesh().chunkSetAngles("Blister1_D0", 0.0F, 0.0F, 70F * f);
        hierMesh().chunkSetAngles("Blister2_D0", 0.0F, 0.0F, 90F * f);
    }

    public void update(float f)
    {
        if(FM.getSpeed() > 5F)
        {
            slpos = 0.7F * slpos + 0.13F * (FM.getAOA() > 6.6F ? 0.07F : 0.0F);
            resetYPRmodifier();
            Aircraft.xyz[0] = slpos;
            hierMesh().chunkSetLocate("SlatL_D0", Aircraft.xyz, Aircraft.ypr);
            hierMesh().chunkSetLocate("SlatR_D0", Aircraft.xyz, Aircraft.ypr);
        }
        hierMesh().chunkSetAngles("WaterL_D0", 0.0F, 15F - 30F * kangle0, 0.0F);
        kangle0 = 0.95F * kangle0 + 0.05F * FM.EI.engines[0].getControlRadiator();
        hierMesh().chunkSetAngles("WaterR_D0", 0.0F, 15F - 30F * kangle1, 0.0F);
        kangle1 = 0.95F * kangle1 + 0.05F * FM.EI.engines[1].getControlRadiator();
        super.update(f);
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xx"))
        {
            if(s.startsWith("xxarmor"))
            {
                debuggunnery("Armor: Hit..");
                if(s.endsWith("p1"))
                {
                    if(Math.abs(point3d.y) > 0.231D)
                        getEnergyPastArmor(8.585D / (Math.abs(Aircraft.v1.y) + 0.0001D), shot);
                    else
                        getEnergyPastArmor(1.0F, shot);
                } else
                if(s.endsWith("p2"))
                    getEnergyPastArmor(World.Rnd().nextFloat(1.96F, 3.4839F), shot);
                else
                if(s.endsWith("p3"))
                {
                    if(point3d.z < 0.08D)
                    {
                        getEnergyPastArmor(8.585D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                    } else
                    {
                        if(point3d.z < 0.09D)
                        {
                            debuggunnery("Armor: Bullet Went Through a Pilofacturing Hole..");
                            return;
                        }
                        if(point3d.y > 0.175D && point3d.y < 0.287D && point3d.z < 0.177D)
                        {
                            debuggunnery("Armor: Bullet Went Through a Pilofacturing Hole..");
                            return;
                        }
                        if(point3d.y > -0.334D && point3d.y < -0.177D && point3d.z < 0.204D)
                        {
                            debuggunnery("Armor: Bullet Went Through a Pilofacturing Hole..");
                            return;
                        }
                        if(point3d.z > 0.288D && Math.abs(point3d.y) < 0.077D)
                            getEnergyPastArmor((double)World.Rnd().nextFloat(8.5F, 12.46F) / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                        else
                            getEnergyPastArmor(10.51D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                    }
                } else
                if(s.endsWith("p4"))
                {
                    getEnergyPastArmor(World.Rnd().nextFloat(20F, 30F), shot);
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 2);
                    debuggunnery("Armor: Armor Glass: Hit..");
                    if(shot.power <= 0.0F)
                    {
                        debuggunnery("Armor: Armor Glass: Bullet Stopped..");
                        if(World.Rnd().nextFloat() < 0.96F)
                            doRicochetBack(shot);
                    }
                } else
                if(s.endsWith("p5"))
                    getEnergyPastArmor(5.51D / (Math.abs(Aircraft.v1.z) + 0.0001D), shot);
                else
                if(s.endsWith("p6"))
                {
                    if(point3d.z > 0.448D)
                    {
                        if(point3d.z > 0.609D && Math.abs(point3d.y) > 0.251D)
                        {
                            debuggunnery("Armor: Bullet Went Through a Pilofacturing Hole..");
                            return;
                        }
                        getEnergyPastArmor(10.604999542236328D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                    } else
                    if(Math.abs(point3d.y) > 0.264D)
                    {
                        if(point3d.z > 0.021D)
                        {
                            getEnergyPastArmor(8.51D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                        } else
                        {
                            debuggunnery("Armor: Bullet Went Through a Pilofacturing Hole..");
                            return;
                        }
                    } else
                    {
                        if(point3d.z < -0.352D && Math.abs(point3d.y) < 0.04D)
                        {
                            debuggunnery("Armor: Bullet Went Through a Pilofacturing Hole..");
                            return;
                        }
                        getEnergyPastArmor(8.06D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                    }
                } else
                if(s.endsWith("p7"))
                    getEnergyPastArmor(6.06D / (Math.abs(Aircraft.v1.z) + 0.0001D), shot);
                else
                if(s.endsWith("p8"))
                {
                    if(point3d.y > 0.112D && point3d.z < -0.319D || point3d.y < -0.065D && point3d.z > 0.038D && point3d.z < 0.204D)
                    {
                        debuggunnery("Armor: Bullet Went Through a Pilofacturing Hole..");
                        return;
                    }
                    getEnergyPastArmor(8.06D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                } else
                if(s.endsWith("p9"))
                {
                    if(point3d.z > 0.611D && point3d.z < 0.674D && Math.abs(point3d.y) < 0.0415D)
                    {
                        debuggunnery("Armor: Bullet Went Through a Pilofacturing Hole..");
                        return;
                    }
                    getEnergyPastArmor(8.06D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                }
                return;
            }
            if(s.startsWith("xxcontrols"))
            {
                debuggunnery("Controls: Hit..");
                if(World.Rnd().nextFloat() < 0.99F)
                    return;
                int i = s.charAt(10) - 48;
                if(s.endsWith("10"))
                    i = 10;
                if(s.endsWith("11"))
                    i = 11;
                switch(i)
                {
                default:
                    break;

                case 1:
                    if(getEnergyPastArmor(2.2F, shot) > 0.0F)
                    {
                        debuggunnery("Controls: Control Column: Hit, Controls Destroyed..");
                        FM.AS.setControlsDamage(shot.initiator, 2);
                        FM.AS.setControlsDamage(shot.initiator, 1);
                        FM.AS.setControlsDamage(shot.initiator, 0);
                    }
                    break;

                case 2:
                    if(getEnergyPastArmor(World.Rnd().nextFloat(0.02F, 2.351F), shot) > 0.0F)
                    {
                        FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                        FM.AS.setEngineSpecificDamage(shot.initiator, 0, 6);
                        FM.AS.setEngineSpecificDamage(shot.initiator, 1, 1);
                        FM.AS.setEngineSpecificDamage(shot.initiator, 1, 6);
                        debuggunnery("Controls: Throttle Quadrant: Hit, Engine Controls Disabled..");
                    }
                    break;

                case 3:
                    if(getEnergyPastArmor(3.5F, shot) <= 0.0F)
                        break;
                    if(World.Rnd().nextFloat() < 0.25F)
                    {
                        FM.AS.setControlsDamage(shot.initiator, 0);
                        debuggunnery("Controls: Aileron Controls: Fuselage Line Destroyed..");
                    }
                    if(World.Rnd().nextFloat() < 0.25F)
                    {
                        FM.AS.setControlsDamage(shot.initiator, 1);
                        debuggunnery("Controls: Elevator Controls: Fuselage Line Destroyed..");
                    }
                    if(World.Rnd().nextFloat() < 0.25F)
                    {
                        FM.AS.setControlsDamage(shot.initiator, 2);
                        debuggunnery("Controls: Rudder Controls: Fuselage Line Destroyed..");
                    }
                    break;

                case 4:
                case 5:
                    if(getEnergyPastArmor(0.002F, shot) > 0.0F)
                    {
                        FM.AS.setControlsDamage(shot.initiator, 1);
                        debuggunnery("Controls: Elevator Controls: Disabled / Strings Broken..");
                    }
                    break;

                case 6:
                case 7:
                case 10:
                case 11:
                    if(getEnergyPastArmor(0.12F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F)
                    {
                        FM.AS.setControlsDamage(shot.initiator, 0);
                        debuggunnery("Controls: Aileron Controls: Disabled..");
                    }
                    break;

                case 8:
                case 9:
                    if(getEnergyPastArmor(6.8F, shot) > 0.0F)
                    {
                        FM.AS.setControlsDamage(shot.initiator, 0);
                        debuggunnery("Controls: Aileron Controls: Crank Destroyed..");
                    }
                    break;
                }
                return;
            }
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
                return;
            }
            if(s.startsWith("xxwj"))
            {
                if(getEnergyPastArmor(World.Rnd().nextFloat(12.5F, 55.96F), shot) > 0.0F)
                    if(s.endsWith("l"))
                    {
                        debuggunnery("Spar Construction: WingL Console Lock Destroyed..");
                        nextDMGLevels(4, 2, "WingLIn_D" + chunkDamageVisible("WingLIn"), shot.initiator);
                    } else
                    {
                        debuggunnery("Spar Construction: WingR Console Lock Destroyed..");
                        nextDMGLevels(4, 2, "WingRIn_D" + chunkDamageVisible("WingRIn"), shot.initiator);
                    }
                return;
            }
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
                return;
            }
            if(s.startsWith("xxeng"))
            {
                int k = s.charAt(5) - 49;
                debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Hit..");
                if(s.endsWith("prop"))
                {
                    if(getEnergyPastArmor(0.1F, shot) > 0.0F && World.Rnd().nextFloat() < 0.8F)
                        if(World.Rnd().nextFloat() < 0.5F)
                        {
                            FM.AS.setEngineSpecificDamage(shot.initiator, k, 3);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Prop Governor Hit, Disabled..");
                        } else
                        {
                            FM.AS.setEngineSpecificDamage(shot.initiator, k, 4);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Prop Governor Hit, Damaged..");
                        }
                } else
                if(s.endsWith("gear"))
                {
                    if(getEnergyPastArmor(4.6F, shot) > 0.0F)
                        if(World.Rnd().nextFloat() < 0.5F)
                        {
                            FM.EI.engines[k].setEngineStuck(shot.initiator);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Bullet Jams Reductor Gear..");
                        } else
                        {
                            FM.AS.setEngineSpecificDamage(shot.initiator, k, 3);
                            FM.AS.setEngineSpecificDamage(shot.initiator, k, 4);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Reductor Gear Damaged, Prop Governor Failed..");
                        }
                } else
                if(s.endsWith("supc"))
                {
                    if(getEnergyPastArmor(0.1F, shot) > 0.0F)
                    {
                        FM.AS.setEngineSpecificDamage(shot.initiator, k, 0);
                        debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Supercharger Disabled..");
                    }
                } else
                if(s.endsWith("feed"))
                {
                    if(getEnergyPastArmor(3.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.5F && FM.EI.engines[k].getPowerOutput() > 0.7F)
                    {
                        FM.AS.hitEngine(shot.initiator, k, 100);
                        debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Pressurized Fuel Line Pierced, Fuel Flamed..");
                    }
                } else
                if(s.endsWith("fuel"))
                {
                    if(getEnergyPastArmor(1.1F, shot) > 0.0F)
                    {
                        FM.EI.engines[k].setEngineStops(shot.initiator);
                        debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Fuel Line Stalled, Engine Stalled..");
                    }
                } else
                if(s.endsWith("case"))
                {
                    if(getEnergyPastArmor(2.1F, shot) > 0.0F)
                    {
                        if(World.Rnd().nextFloat() < shot.power / 175000F)
                        {
                            FM.AS.setEngineStuck(shot.initiator, k);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Bullet Jams Crank Ball Bearing..");
                        }
                        if(World.Rnd().nextFloat() < shot.power / 50000F)
                        {
                            FM.AS.hitEngine(shot.initiator, k, 2);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Crank Case Hit, Readyness Reduced to " + FM.EI.engines[k].getReadyness() + "..");
                        }
                        FM.EI.engines[k].setReadyness(shot.initiator, FM.EI.engines[k].getReadyness() - World.Rnd().nextFloat(0.0F, shot.power / 48000F));
                        debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Crank Case Hit, Readyness Reduced to " + FM.EI.engines[k].getReadyness() + "..");
                    }
                    getEnergyPastArmor(World.Rnd().nextFloat(22.5F, 33.6F), shot);
                } else
                if(s.endsWith("cyl1") || s.endsWith("cyl2"))
                {
                    if(getEnergyPastArmor(0.1F, shot) > 0.0F && World.Rnd().nextFloat() < FM.EI.engines[k].getCylindersRatio() * 1.75F)
                    {
                        FM.EI.engines[k].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int)(shot.power / 4800F)));
                        debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Cylinders Hit, " + FM.EI.engines[k].getCylindersOperable() + "/" + FM.EI.engines[k].getCylinders() + " Left..");
                        if(World.Rnd().nextFloat() < shot.power / 24000F)
                        {
                            FM.AS.hitEngine(shot.initiator, k, 3);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Cylinders Hit, Engine Fires..");
                        }
                        if(World.Rnd().nextFloat() < 0.01F)
                        {
                            FM.AS.setEngineStuck(shot.initiator, k);
                            debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Bullet Jams Piston Head..");
                        }
                        getEnergyPastArmor(22.5F, shot);
                    }
                } else
                if(s.endsWith("mag1") || s.endsWith("mag2"))
                {
                    int j2 = s.charAt(9) - 49;
                    FM.EI.engines[k].setMagnetoKnockOut(shot.initiator, j2);
                    debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Magneto " + j2 + " Destroyed..");
                } else
                if(s.endsWith("oil1"))
                {
                    FM.AS.hitOil(shot.initiator, k);
                    debuggunnery("Engine Module (" + (k == 0 ? "Left" : "Right") + "): Oil Radiator Hit..");
                }
                return;
            }
            if(s.startsWith("xxoil"))
            {
                if(getEnergyPastArmor(World.Rnd().nextFloat(1.2F, 2.345F), shot) > 0.0F)
                {
                    int l = s.charAt(5) - 49;
                    FM.AS.hitOil(shot.initiator, l);
                    getEnergyPastArmor(0.22F, shot);
                    debuggunnery("Engine Module (" + (l == 0 ? "Left" : "Right") + "): Oil Tank Pierced..");
                }
                return;
            }
            if(s.startsWith("xxw"))
            {
                if(getEnergyPastArmor(World.Rnd().nextFloat(0.1F, 0.75F), shot) > 0.0F)
                {
                    int i1 = s.charAt(3) - 49;
                    if(FM.AS.astateEngineStates[i1] == 0)
                    {
                        debuggunnery("Engine Module (" + (i1 == 0 ? "Left" : "Right") + "): Water Radiator Pierced..");
                        FM.AS.hitEngine(shot.initiator, i1, 2);
                        FM.AS.doSetEngineState(shot.initiator, i1, 2);
                    }
                    getEnergyPastArmor(2.22F, shot);
                }
                return;
            }
            if(s.startsWith("xxtank"))
            {
                int j1 = s.charAt(6) - 49;
                if(getEnergyPastArmor(World.Rnd().nextFloat(0.1F, 2.23F), shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                {
                    if(FM.AS.astateTankStates[j1] == 0)
                    {
                        debuggunnery("Fuel Tank " + (j1 + 1) + ": Pierced..");
                        FM.AS.hitTank(shot.initiator, j1, 1);
                        FM.AS.doSetTankState(shot.initiator, j1, 1);
                    } else
                    if(FM.AS.astateTankStates[j1] == 1)
                    {
                        debuggunnery("Fuel Tank " + (j1 + 1) + ": Pierced..");
                        FM.AS.hitTank(shot.initiator, j1, 1);
                        FM.AS.doSetTankState(shot.initiator, j1, 2);
                    }
                    if(shot.powerType == 3 && World.Rnd().nextFloat() < 0.5F)
                    {
                        FM.AS.hitTank(shot.initiator, 2, 2);
                        debuggunnery("Fuel Tank " + (j1 + 1) + ": Hit..");
                    }
                }
                return;
            }
            if(s.startsWith("xxhyd"))
            {
                if(getEnergyPastArmor(World.Rnd().nextFloat(0.25F, 12.39F), shot) > 0.0F)
                {
                    debuggunnery("Hydro System: Disabled..");
                    FM.AS.setInternalDamage(shot.initiator, 0);
                }
                return;
            }
            if(s.startsWith("xxmgun"))
            {
                if(s.endsWith("01"))
                {
                    debuggunnery("Cowling Gun: Disabled..");
                    FM.AS.setJamBullets(0, 0);
                }
                if(s.endsWith("02"))
                {
                    debuggunnery("Cowling Gun: Disabled..");
                    FM.AS.setJamBullets(0, 1);
                }
                if(s.endsWith("03"))
                {
                    debuggunnery("Cowling Gun: Disabled..");
                    FM.AS.setJamBullets(0, 2);
                }
                if(s.endsWith("04"))
                {
                    debuggunnery("Cowling Gun: Disabled..");
                    FM.AS.setJamBullets(0, 3);
                }
                getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 12.96F), shot);
            }
            if(s.startsWith("xxcannon"))
            {
                if(s.endsWith("01"))
                {
                    debuggunnery("Cowling Cannon: Disabled..");
                    FM.AS.setJamBullets(1, 0);
                }
                if(s.endsWith("02"))
                {
                    debuggunnery("Cowling Cannon: Disabled..");
                    FM.AS.setJamBullets(1, 1);
                }
                getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 24.6F), shot);
            }
            if(s.startsWith("xxmgff"))
            {
                if(s.endsWith("01"))
                {
                    debuggunnery("Cowling Cannon (MGFF): Disabled..");
                    FM.AS.setJamBullets(0, 0);
                }
                if(s.endsWith("02"))
                {
                    debuggunnery("Cowling Cannon (MGFF): Disabled..");
                    FM.AS.setJamBullets(0, 1);
                }
                getEnergyPastArmor(World.Rnd().nextFloat(3.3F, 24.6F), shot);
            }
            if(s.startsWith("xxmk"))
            {
                if(s.endsWith("01"))
                {
                    debuggunnery("Cowling Cannon (Mk 108): Disabled..");
                    FM.AS.setJamBullets(1, 0);
                }
                if(s.endsWith("02"))
                {
                    debuggunnery("Cowling Cannon (Mk 108): Disabled..");
                    FM.AS.setJamBullets(1, 1);
                }
                getEnergyPastArmor(World.Rnd().nextFloat(24.5F, 96.87F), shot);
            }
            return;
        }
        if(s.startsWith("xcf") || s.startsWith("xcockpit") || s.startsWith("xnose"))
        {
            if(chunkDamageVisible("CF") < 3)
                hitChunk("CF", shot);
            if(s.startsWith("xcockpit"))
            {
                if(point3d.x > 1.857D && point3d.z > 0.416D)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 2);
                if(World.Rnd().nextFloat() < 0.12F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 0x40);
                if(World.Rnd().nextFloat() < 0.12F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 4);
                if(World.Rnd().nextFloat() < 0.12F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 8);
                if(World.Rnd().nextFloat() < 0.12F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 0x10);
                if(World.Rnd().nextFloat() < 0.12F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 0x20);
                if(World.Rnd().nextFloat() < 0.12F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 1);
            }
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
                FM.AS.setInternalDamage(shot.initiator, 0);
            }
            if(s.endsWith("2"))
            {
                if(World.Rnd().nextFloat() < 0.1F && getEnergyPastArmor(World.Rnd().nextFloat(6.8F, 29.35F), shot) > 0.0F)
                {
                    debuggunnery("Undercarriage: Stuck..");
                    FM.AS.setInternalDamage(shot.initiator, 3);
                }
                String s1 = "" + s.charAt(5);
                hitChunk("Gear" + s1.toUpperCase() + "2", shot);
            }
        } else
        if(s.startsWith("xturret"))
        {
            if(getEnergyPastArmor(0.25F, shot) > 0.0F)
            {
                debuggunnery("Armament System: Turret Machine Gun(s): Disabled..");
                FM.AS.setJamBullets(10, 0);
                FM.AS.setJamBullets(10, 1);
                getEnergyPastArmor(World.Rnd().nextFloat(0.1F, 26.35F), shot);
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

    public static void moveGear(HierMesh hiermesh, float f)
    {
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, 77F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.1F, 0.0F, -50.5F), 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.1F, 0.0F, -50.5F), 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, 77F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR3_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.1F, 0.0F, -50.5F), 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(f, 0.0F, 0.1F, 0.0F, -50.5F), 0.0F);
    }

    protected void moveGear(float f)
    {
        moveGear(hierMesh(), f);
    }

    public void moveSteering(float f)
    {
        hierMesh().chunkSetAngles("GearC2_D0", f, 0.0F, 0.0F);
    }

    public void doKillPilot(int i)
    {
        switch(i)
        {
        case 1:
            FM.turret[0].bIsOperable = false;
            break;
        }
    }

    public void doMurderPilot(int i)
    {
        switch(i)
        {
        default:
            break;

        case 0:
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            hierMesh().chunkVisible("HMask1_D0", false);
            if(!FM.AS.bIsAboutToBailout)
                hierMesh().chunkVisible("Gore1_D0", true);
            break;

        case 1:
            hierMesh().chunkVisible("Pilot2_D0", false);
            hierMesh().chunkVisible("Pilot2_D1", true);
            hierMesh().chunkVisible("HMask2_D0", false);
            if(!FM.AS.bIsAboutToBailout)
                hierMesh().chunkVisible("Gore2_D0", true);
            break;
        }
    }

    protected boolean cutFM(int i, int j, Actor actor)
    {
        switch(i)
        {
        default:
            break;

        case 33:
            hitProp(0, j, actor);
            break;

        case 36:
            hitProp(1, j, actor);
            break;

        case 12:
        case 18:
        case 19:
            hierMesh().chunkVisible("Wire_D0", false);
            break;

        case 34:
            FM.AS.hitEngine(this, 0, 2);
            if(World.Rnd().nextFloat() < 0.66F)
                FM.AS.hitEngine(this, 0, 2);
            break;

        case 37:
            FM.AS.hitEngine(this, 1, 2);
            if(World.Rnd().nextFloat() < 0.66F)
                FM.AS.hitEngine(this, 1, 2);
            break;
        }
        return super.cutFM(i, j, actor);
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(flag)
        {
            if(FM.AS.astateEngineStates[0] > 3 && World.Rnd().nextFloat() < 0.06F)
                FM.AS.hitTank(this, 0, 1);
            if(FM.AS.astateEngineStates[1] > 3 && World.Rnd().nextFloat() < 0.06F)
                FM.AS.hitTank(this, 1, 1);
            if(FM.AS.astateTankStates[0] > 5 && World.Rnd().nextFloat() < 0.02F)
                nextDMGLevel(FM.AS.astateEffectChunks[0] + "0", 0, this);
            if(FM.AS.astateTankStates[1] > 5 && World.Rnd().nextFloat() < 0.02F)
                nextDMGLevel(FM.AS.astateEffectChunks[1] + "0", 0, this);
            if(FM.AS.astateTankStates[2] > 5 && World.Rnd().nextFloat() < 0.02F)
                nextDMGLevel(FM.AS.astateEffectChunks[2] + "0", 0, this);
            if(FM.AS.astateTankStates[3] > 5 && World.Rnd().nextFloat() < 0.02F)
                nextDMGLevel(FM.AS.astateEffectChunks[3] + "0", 0, this);
            if(FM.AS.astateTankStates[0] > 4 && World.Rnd().nextFloat() < 0.08F)
                FM.AS.hitTank(this, 1, 1);
            if(FM.AS.astateTankStates[1] > 4 && World.Rnd().nextFloat() < 0.08F)
                FM.AS.hitTank(this, 0, 1);
            if(FM.AS.astateTankStates[2] > 4 && World.Rnd().nextFloat() < 0.08F)
                FM.AS.hitTank(this, 3, 1);
            if(FM.AS.astateTankStates[3] > 4 && World.Rnd().nextFloat() < 0.08F)
                FM.AS.hitTank(this, 2, 1);
        }
        for(int i = 1; i < 3; i++)
            if(FM.getAltitude() < 3000F)
                hierMesh().chunkVisible("HMask" + i + "_D0", false);
            else
                hierMesh().chunkVisible("HMask" + i + "_D0", hierMesh().isChunkVisible("Pilot" + i + "_D0"));

    }

    public boolean turretAngles(int i, float af[])
    {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        switch(i)
        {
        case 0:
            if(f < -37F)
            {
                f = -37F;
                flag = false;
            }
            if(f > 37F)
            {
                f = 37F;
                flag = false;
            }
            if(f1 < -19F)
            {
                f1 = -19F;
                flag = false;
            }
            if(f1 > 27F)
            {
                f1 = 27F;
                flag = false;
            }
            if(Math.abs(f) > 17.8F && Math.abs(f) < 25F && f1 < -12F)
                flag = false;
            break;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    protected void moveFlap(float f)
    {
        float f1 = -45F * f;
        hierMesh().chunkSetAngles("Flap1_D0", 0.0F, f1, 0.0F);
        hierMesh().chunkSetAngles("Flap2_D0", 0.0F, f1, 0.0F);
    }

    private float kangle0;
    private float kangle1;
    private float slpos;

    static 
    {
        Class class1 = FW_187.class;
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
    }
}
