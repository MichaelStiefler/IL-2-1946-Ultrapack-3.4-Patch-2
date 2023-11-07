package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.rts.Property;

public class J8M1 extends Scheme1
    implements TypeFighter, TypeBNZFighter
{

    public J8M1()
    {
        bCockpitNVentilated = false;
        bCartAttached = true;
        flame = null;
        dust = null;
        trail = null;
        sprite = null;
        turboexhaust = null;
        dynamoOrient = 0.0F;
        bDynamoRotary = false;
        pk = 0;
    }

    public void destroy()
    {
        if(Actor.isValid(flame))
            flame.destroy();
        if(Actor.isValid(dust))
            dust.destroy();
        if(Actor.isValid(trail))
            trail.destroy();
        if(Actor.isValid(sprite))
            sprite.destroy();
        if(Actor.isValid(turboexhaust))
            turboexhaust.destroy();
        super.destroy();
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        if(Config.isUSE_RENDER())
        {
            flame = Eff3DActor.New(this, findHook("_Engine1EF_01"), null, 1.0F, "3DO/Effects/Aircraft/TurboHWK109F.eff", -1F);
            dust = Eff3DActor.New(this, findHook("_Engine1EF_01"), null, 1.0F, "3DO/Effects/Aircraft/TurboHWK109D.eff", -1F);
            trail = Eff3DActor.New(this, findHook("_Engine1EF_01"), null, 1.0F, "3DO/Effects/Aircraft/TurboHWK109T.eff", -1F);
            sprite = Eff3DActor.New(this, findHook("_Engine1EF_01"), null, 1.0F, "3DO/Effects/Aircraft/TurboHWK109S.eff", -1F);
            turboexhaust = Eff3DActor.New(this, findHook("_Engine1ES_02"), null, 1.0F, "3DO/Effects/Aircraft/WhiteOxySmallGND.eff", -1F);
            Eff3DActor.setIntesity(flame, 0.0F);
            Eff3DActor.setIntesity(dust, 0.0F);
            Eff3DActor.setIntesity(trail, 0.0F);
            Eff3DActor.setIntesity(sprite, 0.0F);
            Eff3DActor.setIntesity(turboexhaust, 1.0F);
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xx"))
        {
            if(s.startsWith("xxarmor"))
            {
                if(s.endsWith("1"))
                {
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 2);
                    if(getEnergyPastArmor((double)World.Rnd().nextFloat(30F, 90F) / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) < 0.0F)
                        doRicochet(shot);
                } else
                if(s.endsWith("2"))
                    getEnergyPastArmor(13.13D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                return;
            }
            if(s.startsWith("xxcontrols"))
            {
                int i = s.charAt(10) - 48;
                switch(i)
                {
                default:
                    break;

                case 1:
                    if(getEnergyPastArmor(2.2F / (float)Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                        FM.AS.setControlsDamage(shot.initiator, 2);
                    break;

                case 2:
                    if(getEnergyPastArmor(2.2F / (float)Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) <= 0.0F)
                        break;
                    if(World.Rnd().nextFloat() < 0.25F)
                        FM.AS.setControlsDamage(shot.initiator, 2);
                    if(World.Rnd().nextFloat() < 0.25F)
                    {
                        FM.AS.setControlsDamage(shot.initiator, 1);
                        FM.AS.setControlsDamage(shot.initiator, 0);
                    }
                    break;

                case 3:
                case 4:
                    if(getEnergyPastArmor(2.2F, shot) > 0.0F && World.Rnd().nextFloat() < 0.1F)
                    {
                        FM.AS.setControlsDamage(shot.initiator, 1);
                        FM.AS.setControlsDamage(shot.initiator, 0);
                    }
                    break;
                }
                return;
            }
            if(s.startsWith("xxspar"))
            {
                if(s.startsWith("xxsparli") && chunkDamageVisible("WingLIn") > 2 && (double)World.Rnd().nextFloat() < 1.0D - 0.92D * Math.abs(Aircraft.v1.x) && getEnergyPastArmor(17.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)
                {
                    debuggunnery("*** WingLIn Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if(s.startsWith("xxsparri") && chunkDamageVisible("WingRIn") > 2 && (double)World.Rnd().nextFloat() < 1.0D - 0.92D * Math.abs(Aircraft.v1.x) && getEnergyPastArmor(17.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)
                {
                    debuggunnery("*** WingRIn Spars Damaged..");
                    nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if(s.startsWith("xxsparlm") && chunkDamageVisible("WingLMid") > 2 && (double)World.Rnd().nextFloat() < 1.0D - 0.92D * Math.abs(Aircraft.v1.x) && getEnergyPastArmor(17.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)
                {
                    debuggunnery("*** WingLMid Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if(s.startsWith("xxsparrm") && chunkDamageVisible("WingRMid") > 2 && (double)World.Rnd().nextFloat() < 1.0D - 0.92D * Math.abs(Aircraft.v1.x) && getEnergyPastArmor(17.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)
                {
                    debuggunnery("*** WingRMid Spars Damaged..");
                    nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if(s.startsWith("xxsparlo") && chunkDamageVisible("WingLOut") > 2 && (double)World.Rnd().nextFloat() < 1.0D - 0.92D * Math.abs(Aircraft.v1.x) && getEnergyPastArmor(17.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)
                {
                    debuggunnery("*** WingLOut Spars Damaged..");
                    nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if(s.startsWith("xxsparro") && chunkDamageVisible("WingROut") > 2 && (double)World.Rnd().nextFloat() < 1.0D - 0.92D * Math.abs(Aircraft.v1.x) && getEnergyPastArmor(17.8D / (1.0001000165939331D - Math.abs(Aircraft.v1.y)), shot) > 0.0F)
                {
                    debuggunnery("*** WingROut Spars Damaged..");
                    nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                if(s.startsWith("xxspark") && chunkDamageVisible("Keel1") > 2 && getEnergyPastArmor(3.5F * World.Rnd().nextFloat(1.0F, 1.2F), shot) > 0.0F)
                {
                    Aircraft.debugprintln(this, "*** Keel Spars Damaged..");
                    nextDMGLevels(1, 2, "Keel1_D3", shot.initiator);
                }
                return;
            }
            if(s.startsWith("xxlock"))
            {
                if(s.startsWith("xxlockal") && getEnergyPastArmor(4.35F, shot) > 0.0F)
                {
                    debuggunnery("*** AroneL Lock Damaged..");
                    nextDMGLevels(1, 2, "AroneL_D0", shot.initiator);
                }
                if(s.startsWith("xxlockar") && getEnergyPastArmor(4.35F, shot) > 0.0F)
                {
                    debuggunnery("*** AroneR Lock Damaged..");
                    nextDMGLevels(1, 2, "AroneR_D0", shot.initiator);
                }
                if(s.startsWith("xxlockfl") && getEnergyPastArmor(4.35F, shot) > 0.0F)
                {
                    debuggunnery("*** VatorL Lock Damaged..");
                    nextDMGLevels(1, 2, "VatorL_D0", shot.initiator);
                }
                if(s.startsWith("xxlockfr") && getEnergyPastArmor(4.35F, shot) > 0.0F)
                {
                    debuggunnery("*** VatorR Lock Damaged..");
                    nextDMGLevels(1, 2, "VatorR_D0", shot.initiator);
                }
                if(s.startsWith("xxlockr") && getEnergyPastArmor(4.32F, shot) > 0.0F)
                {
                    debuggunnery("*** Rudder1 Lock Damaged..");
                    nextDMGLevels(1, 2, "Rudder1_D0", shot.initiator);
                }
                return;
            }
            if(s.startsWith("xxeng"))
            {
                int j = s.charAt(8) - 48;
                switch(j)
                {
                default:
                    break;

                case 1:
                    if(World.Rnd().nextFloat() < 0.01F)
                        FM.AS.hitEngine(shot.initiator, 0, 100);
                    if(Aircraft.Pd.x < -2.7D)
                        FM.EI.engines[0].setReadyness(shot.initiator, FM.EI.engines[0].getReadyness() - World.Rnd().nextFloat(0.01F, 0.35F));
                    break;

                case 2:
                    if(getEnergyPastArmor(4.96F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                        FM.AS.hitEngine(shot.initiator, 0, 100);
                    break;

                case 3:
                    getEnergyPastArmor(5.808F, shot);
                    break;
                }
                return;
            }
            if(s.startsWith("xxtank"))
            {
                int k = s.charAt(6) - 48;
                switch(k)
                {
                default:
                    break;

                case 1:
                    if(getEnergyPastArmor(World.Rnd().nextFloat(1.0F, 7.9F), shot) > 0.0F)
                        FM.AS.hitTank(shot.initiator, 3, 1);
                    break;

                case 2:
                case 3:
                    if(getEnergyPastArmor(World.Rnd().nextFloat(1.0F, 7.9F), shot) > 0.0F)
                    {
                        FM.AS.hitTank(shot.initiator, 2, 1);
                        bCockpitNVentilated = true;
                    }
                    // fall through

                case 4:
                    if(getEnergyPastArmor(World.Rnd().nextFloat(1.0F, 7.9F), shot) > 0.0F)
                        FM.AS.hitTank(shot.initiator, 0, World.Rnd().nextInt(1, 4));
                    break;

                case 5:
                    if(getEnergyPastArmor(World.Rnd().nextFloat(1.0F, 7.9F), shot) > 0.0F)
                        FM.AS.hitTank(shot.initiator, 1, World.Rnd().nextInt(1, 4));
                    break;
                }
                return;
            }
            if(s.startsWith("xxammo"))
            {
                int l = s.charAt(6) - 48;
                if(World.Rnd().nextFloat() < 0.1F)
                    if(l == 0)
                        FM.AS.setJamBullets(1, 1);
                    else
                        FM.AS.setJamBullets(1, 0);
                return;
            }
            if(s.startsWith("xxgunl") && getEnergyPastArmor(World.Rnd().nextFloat(2.0F, 35.6F), shot) > 0.0F)
                FM.AS.setJamBullets(1, 0);
            if(s.startsWith("xxgunr") && getEnergyPastArmor(World.Rnd().nextFloat(2.0F, 35.6F), shot) > 0.0F)
                FM.AS.setJamBullets(1, 1);
            if(s.startsWith("xxeqpt"));
            return;
        }
        if(s.startsWith("xcf"))
        {
            if(Aircraft.Pd.x > 2.01D && getEnergyPastArmor(11.11F / ((float)Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z) + 0.0001F), shot) <= 0.0F)
            {
                doRicochet(shot);
                return;
            }
            if(Aircraft.Pd.x > 0.8D && Aircraft.Pd.x < 2D)
                if(Aircraft.Pd.z > 0.425D)
                {
                    if(World.Rnd().nextFloat() < 0.5F)
                        FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 1);
                } else
                if(Aircraft.Pd.y > 0.0D)
                {
                    if(World.Rnd().nextFloat() < 0.5F)
                        FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 4);
                    else
                        FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 8);
                } else
                if(World.Rnd().nextFloat() < 0.5F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 0x10);
                else
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 0x20);
            if(chunkDamageVisible("CF") < 3)
                hitChunk("CF", shot);
        } else
        if(s.startsWith("xtail"))
        {
            if(chunkDamageVisible("Tail1") < 3)
                hitChunk("Tail1", shot);
        } else
        if(s.startsWith("xkeel"))
        {
            if(chunkDamageVisible("Keel1") < 2)
                hitChunk("Keel1", shot);
        } else
        if(s.startsWith("xrudder"))
        {
            if(chunkDamageVisible("Rudder1") < 1)
                hitChunk("Rudder1", shot);
        } else
        if(s.startsWith("xwinglin"))
        {
            if(chunkDamageVisible("WingLIn") < 3)
                hitChunk("WingLIn", shot);
        } else
        if(s.startsWith("xwingrin"))
        {
            if(chunkDamageVisible("WingRIn") < 3)
                hitChunk("WingRIn", shot);
        } else
        if(s.startsWith("xwinglmid"))
        {
            if(chunkDamageVisible("WingLMid") < 3)
                hitChunk("WingLMid", shot);
        } else
        if(s.startsWith("xwingrmid"))
        {
            if(chunkDamageVisible("WingRMid") < 3)
                hitChunk("WingRMid", shot);
        } else
        if(s.startsWith("xwinglout"))
        {
            if(chunkDamageVisible("WingLOut") < 3)
                hitChunk("WingLOut", shot);
        } else
        if(s.startsWith("xwingrout"))
        {
            if(chunkDamageVisible("WingROut") < 3)
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
        if(s.startsWith("xflapl"))
        {
            if(chunkDamageVisible("VatorL") < 1)
                hitChunk("VatorL", shot);
        } else
        if(s.startsWith("xflapr"))
        {
            if(chunkDamageVisible("VatorR") < 1)
                hitChunk("VatorR", shot);
        } else
        if(s.startsWith("xpilot") || s.startsWith("xhead"))
        {
            byte byte0 = 0;
            int i1;
            if(s.endsWith("a") || s.endsWith("a2"))
            {
                byte0 = 1;
                i1 = s.charAt(6) - 49;
            } else
            if(s.endsWith("b") || s.endsWith("b2"))
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
        if(flag && bCockpitNVentilated)
            FM.AS.hitPilot(this, 0, 1);
        if(Config.isUSE_RENDER())
        {
            if(oldVwld < 20F && FM.getSpeed() > 20F)
            {
                Eff3DActor.finish(turboexhaust);
                turboexhaust = Eff3DActor.New(this, findHook("_Engine1ES_02"), null, 1.0F, "3DO/Effects/Aircraft/WhiteOxySmallTSPD.eff", -1F);
            }
            if(oldVwld > 20F && FM.getSpeed() < 20F)
            {
                Eff3DActor.finish(turboexhaust);
                turboexhaust = Eff3DActor.New(this, findHook("_Engine1ES_02"), null, 1.0F, "3DO/Effects/Aircraft/WhiteOxySmallGND.eff", -1F);
            }
            oldVwld = FM.getSpeed();
        }
    }

    public void doMurderPilot(int i)
    {
        if(i != 0)
        {
            return;
        } else
        {
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            hierMesh().chunkVisible("HMask1_D0", false);
            return;
        }
    }

    public static void moveGear(HierMesh hiermesh, float f)
    {
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, -45F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, -45F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, -45F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, -45F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL6_D0", 0.0F, -45F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, -15F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC3_D0", 0.0F, 0.0F, 0.0F);
    }

    protected void moveGear(float f)
    {
        HierMesh hiermesh = hierMesh();
        if(bCartAttached)
        {
            if(f < 1.0F)
            {
                hierMesh().chunkVisible("GearL1_D0", false);
                hierMesh().chunkVisible("GearR1_D0", false);
                if(hierMesh().isChunkVisible("Cart_D0"))
                {
                    hierMesh().chunkVisible("CartDrop_D0", true);
                    cut("CartDrop");
                }
                hierMesh().chunkVisible("Cart_D0", false);
                bCartAttached = false;
                FM.setCapableOfTaxiing(false);
                FM.CT.bHasBrakeControl = false;
            }
        } else
        {
            resetYPRmodifier();
            Aircraft.xyz[1] = -0.3F + 0.1125F * f;
            Aircraft.ypr[1] = 88F;
            hiermesh.chunkSetLocate("Cart_D0", Aircraft.xyz, Aircraft.ypr);
        }
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, -45F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL3_D0", 0.0F, -45F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, -45F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, -45F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL6_D0", 0.0F, -45F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, -15F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC3_D0", 0.0F, 0.0F, 0.0F);
    }

    public void moveWheelSink()
    {
        if(!bCartAttached && FM.CT.getGear() > 0.99F)
        {
            float f = Aircraft.cvt(FM.Gears.gWheelSinking[0], 0.0F, 0.066F, -45F, 0.0F);
            hierMesh().chunkSetAngles("GearL2_D0", 0.0F, f, 0.0F);
            hierMesh().chunkSetAngles("GearL3_D0", 0.0F, f, 0.0F);
            hierMesh().chunkSetAngles("GearL4_D0", 0.0F, f, 0.0F);
            hierMesh().chunkSetAngles("GearL5_D0", 0.0F, f, 0.0F);
            hierMesh().chunkSetAngles("GearL6_D0", 0.0F, f, 0.0F);
        }
    }

    public void moveSteering(float f)
    {
        hierMesh().chunkSetAngles("GearC3_D0", 0.0F, -f, 0.0F);
    }

    protected void moveElevator(float f)
    {
        reflectControls();
    }

    protected void moveAileron(float f)
    {
        reflectControls();
    }

    private void reflectControls()
    {
        HierMesh hiermesh = hierMesh();
        float f = -20F * FM.CT.getAileron();
        float f1 = 20F * FM.CT.getElevator();
        hiermesh.chunkSetAngles("AroneL_D0", 0.0F, f + f1, 0.0F);
        hiermesh.chunkSetAngles("AroneR_D0", 0.0F, f - f1, 0.0F);
        hiermesh.chunkSetAngles("VatorL_D0", 0.0F, 0.5F * f1, 0.0F);
        hiermesh.chunkSetAngles("VatorR_D0", 0.0F, 0.5F * f1, 0.0F);
    }

    protected void moveFlap(float f)
    {
        float f1 = -50F * f;
        hierMesh().chunkSetAngles("Brake01_D0", 0.0F, f1, 0.0F);
        hierMesh().chunkSetAngles("Brake02_D0", 0.0F, f1, 0.0F);
    }

    protected void moveFan(float f)
    {
        pk = Math.abs((int)(FM.Vwld.length() / 14D));
        if(pk >= 1)
            pk = 1;
        if(bDynamoRotary != (pk == 1))
        {
            bDynamoRotary = pk == 1;
            hierMesh().chunkVisible("Prop1_D0", !bDynamoRotary);
            hierMesh().chunkVisible("PropRot1_D0", bDynamoRotary);
        }
        dynamoOrient = bDynamoRotary ? (dynamoOrient - 17.987F) % 360F : (float)((double)dynamoOrient - FM.Vwld.length() * 1.5444015264511108D) % 360F;
        hierMesh().chunkSetAngles("Prop1_D0", 0.0F, dynamoOrient, 0.0F);
    }

    public void update(float f)
    {
        super.update(f);
        if(bCartAttached)
        {
            moveGear(FM.CT.getGear());
            if((FM instanceof RealFlightModel) && ((RealFlightModel)FM).isRealMode())
                FM.CT.bHasBrakeControl = false;
            else
                FM.CT.bHasBrakeControl = true;
        }
        if(FM.AS.isMaster())
        {
            if(Config.isUSE_RENDER())
                if(FM.EI.engines[0].getw() > 0.0F && FM.EI.engines[0].getStage() == 6)
                    FM.AS.setSootState(this, 0, 1);
                else
                    FM.AS.setSootState(this, 0, 0);
            if(oldThtl < 0.35F)
                FM.EI.setThrottle(0.0F);
            else
            if(oldThtl < 0.65F)
                FM.EI.setThrottle(0.35F);
            else
            if(oldThtl < 1.0F)
                FM.EI.setThrottle(0.65F);
            else
                FM.EI.setThrottle(1.0F);
            if(oldThtl != FM.CT.PowerControl)
            {
                oldThtl = FM.CT.PowerControl;
                if((FM instanceof RealFlightModel) && ((RealFlightModel)FM).isRealMode())
                    HUD.log(AircraftHotKeys.hudLogPowerId, "Power", new Object[] {
                        new Integer(Math.round(oldThtl * 100F))
                    });
            }
            if(oldThtl == 0.0F)
            {
                if(!FM.Gears.onGround())
                {
                    if((FM instanceof RealFlightModel) && ((RealFlightModel)FM).isRealMode() && FM.EI.engines[0].getStage() == 6)
                        HUD.log("EngineI0");
                    FM.EI.engines[0].setEngineStops(this);
                }
            } else
            {
                if((FM instanceof RealFlightModel) && ((RealFlightModel)FM).isRealMode() && FM.EI.engines[0].getStage() == 0 && FM.M.fuel > 0.0F)
                    HUD.log("EngineI1");
                FM.EI.engines[0].setStage(this, 6);
            }
        }
    }

    public void doSetSootState(int i, int j)
    {
        switch(j)
        {
        case 0:
            Eff3DActor.setIntesity(flame, 0.0F);
            Eff3DActor.setIntesity(dust, 0.0F);
            Eff3DActor.setIntesity(trail, 0.0F);
            Eff3DActor.setIntesity(sprite, 0.0F);
            break;

        case 1:
            Eff3DActor.setIntesity(flame, 1.0F);
            Eff3DActor.setIntesity(dust, 1.0F);
            Eff3DActor.setIntesity(trail, 1.0F);
            Eff3DActor.setIntesity(sprite, 1.0F);
            break;
        }
    }

    private boolean bCockpitNVentilated;
    public boolean bCartAttached;
    private Eff3DActor flame;
    private Eff3DActor dust;
    private Eff3DActor trail;
    private Eff3DActor sprite;
    private Eff3DActor turboexhaust;
    private float oldThtl;
    private float oldVwld;
    private float dynamoOrient;
    private boolean bDynamoRotary;
    private int pk;

    static 
    {
        Class class1 = J8M1.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "J8M");
        Property.set(class1, "meshName", "3DO/Plane/Me-163B-1a/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
        Property.set(class1, "yearService", 1944F);
        Property.set(class1, "yearExpired", 1946F);
        Property.set(class1, "FlightModel", "FlightModels/J8M1.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitJ8M.class
        });
        Property.set(class1, "LOSElevation", 0.87325F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            1, 1
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02"
        });
    }
}
