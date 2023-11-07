package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Maneuver;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.Polares;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.HUD;
import com.maddox.rts.Property;
import com.maddox.rts.Time;
import com.maddox.sas1946.il2.util.Reflection;

public class Li_P13 extends Scheme2
    implements TypeFighter, TypeBNZFighter, TypeX4Carrier
{

    public Li_P13()
    {
        bCockpitNVentilated = false;
        bCartAttached = false;
        bHasEngine = true;
        flame = null;
        flame1 = null;
        flame2 = null;
        flame3 = null;
        flame4 = null;
        flame5 = null;
        flame6 = null;
        flame7 = null;
        dust = null;
        dust2 = null;
        trail = null;
        sprite = null;
        turboexhaust = null;
        bToFire = false;
        tX4Prev = 0L;
        deltaAzimuth = 0.0F;
        deltaTangage = 0.0F;
    }

    public void setOnGround(Point3d point3d, Orient orient, Vector3d vector3d) {
        super.setOnGround(point3d, orient, vector3d);
        bCartAttached = true;
        hierMesh().chunkVisible("GearL1_D0", true);
        hierMesh().chunkVisible("GearR1_D0", true);
        hierMesh().chunkVisible("Cart_D0", true);
        FM.setCapableOfTaxiing(true);
        FM.CT.bHasBrakeControl = true;
    }

    public void destroy()
    {
        if(Actor.isValid(flame))
            flame.destroy();
        if(Actor.isValid(flame1))
            flame.destroy();
        if(Actor.isValid(flame2))
            flame.destroy();
        if(Actor.isValid(flame3))
            flame.destroy();
        if(Actor.isValid(flame4))
            flame.destroy();
        if(Actor.isValid(flame5))
            flame.destroy();
        if(Actor.isValid(flame6))
            flame.destroy();
        if(Actor.isValid(flame7))
            flame.destroy();
        if(Actor.isValid(dust))
            dust.destroy();
        if(Actor.isValid(dust2))
            dust2.destroy();
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
        hierMesh().chunkVisible("GearL1_D0", false);
        hierMesh().chunkVisible("GearR1_D0", false);
        hierMesh().chunkVisible("Cart_D0", false);
        FM.setCapableOfTaxiing(false);
        FM.CT.bHasBrakeControl = false;
        this.vMinFromFmd = this.FM.Vmin;
        this.vMinFlapsFromFmd = this.FM.VminFLAPS;
        if(Config.isUSE_RENDER())
        {
            flame = Eff3DActor.New(this, findHook("_Engine1EF_01"), null, 1.0F, "3DO/Effects/Aircraft/TurboHWK109F.eff", -1F);
            flame1 = Eff3DActor.New(this, findHook("_Engine2EF_01"), null, 1.0F, "3DO/Effects/Aircraft/TurboJRD1100T.eff", -1F);
            flame2 = Eff3DActor.New(this, findHook("_Engine2EF_02"), null, 1.0F, "3DO/Effects/Aircraft/TurboJRD1100T.eff", -1F);
            flame3 = Eff3DActor.New(this, findHook("_Engine2EF_03"), null, 1.0F, "3DO/Effects/Aircraft/TurboJRD1100T.eff", -1F);
            flame4 = Eff3DActor.New(this, findHook("_Engine2EF_04"), null, 1.0F, "3DO/Effects/Aircraft/TurboJRD1100T.eff", -1F);
            flame5 = Eff3DActor.New(this, findHook("_Engine2EF_05"), null, 1.0F, "3DO/Effects/Aircraft/TurboJRD1100T.eff", -1F);
            flame6 = Eff3DActor.New(this, findHook("_Engine2EF_06"), null, 1.0F, "3DO/Effects/Aircraft/TurboJRD1100T.eff", -1F);
            flame7 = Eff3DActor.New(this, findHook("_Engine2EF_07"), null, 1.0F, "3DO/Effects/Aircraft/TurboJRD1100T.eff", -1F);
            dust = Eff3DActor.New(this, findHook("_Engine1EF_01"), null, 1.0F, "3DO/Effects/Aircraft/TurboHWK109D.eff", -1F);
            dust2 = Eff3DActor.New(this, findHook("_Engine2EF_01"), null, 1.0F, "3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1F);
            trail = Eff3DActor.New(this, findHook("_Engine1EF_01"), null, 1.0F, "3DO/Effects/Aircraft/TurboHWK109T.eff", -1F);
            sprite = Eff3DActor.New(this, findHook("_Engine1EF_01"), null, 1.0F, "3DO/Effects/Aircraft/TurboHWK109S.eff", -1F);
            Eff3DActor.setIntesity(flame, 0.0F);
            Eff3DActor.setIntesity(flame1, 0.0F);
            Eff3DActor.setIntesity(flame2, 0.0F);
            Eff3DActor.setIntesity(flame3, 0.0F);
            Eff3DActor.setIntesity(flame4, 0.0F);
            Eff3DActor.setIntesity(flame5, 0.0F);
            Eff3DActor.setIntesity(flame6, 0.0F);
            Eff3DActor.setIntesity(flame7, 0.0F);
            Eff3DActor.setIntesity(dust, 0.0F);
            Eff3DActor.setIntesity(dust2, 0.0F);
            Eff3DActor.setIntesity(trail, 0.0F);
            Eff3DActor.setIntesity(sprite, 0.0F);
        }
        if(thisWeaponsName.startsWith("X4"))
        {
            Polares polares = (Polares)Reflection.getValue(FM, "Wing");
            polares.CxMin_0 = 0.022F;
        }
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }
    
    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
            hierMesh.chunkVisible("Pilon1_D0", thisWeaponsName.startsWith("X4"));
            hierMesh.chunkVisible("Pilon2_D0", thisWeaponsName.startsWith("X4"));
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
            } else
            if(s.startsWith("xxcontrols"))
            {
                int i = s.charAt(10) - 48;
                switch(i)
                {
                case 1:
                    if(getEnergyPastArmor(2.2F / (float)Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                        FM.AS.setControlsDamage(shot.initiator, 2);
                    break;

                case 2:
                    if(getEnergyPastArmor(2.2F / (float)Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F)
                    {
                        if(World.Rnd().nextFloat() < 0.25F)
                            FM.AS.setControlsDamage(shot.initiator, 2);
                        if(World.Rnd().nextFloat() < 0.25F)
                        {
                            FM.AS.setControlsDamage(shot.initiator, 1);
                            FM.AS.setControlsDamage(shot.initiator, 0);
                        }
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
            } else
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
            } else
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
            } else
            if(s.startsWith("xxeng"))
            {
                int j = s.charAt(8) - 48;
                switch(j)
                {
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
            } else
            if(s.startsWith("xxtank"))
            {
                int k = s.charAt(6) - 48;
                switch(k)
                {
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
            } else
            if(s.startsWith("xxammo"))
            {
                int l = s.charAt(6) - 48;
                if(World.Rnd().nextFloat() < 0.1F)
                    if(l == 0)
                        FM.AS.setJamBullets(1, 1);
                    else
                        FM.AS.setJamBullets(1, 0);
            } else
            {
                if(s.startsWith("xxgunl") && getEnergyPastArmor(World.Rnd().nextFloat(2.0F, 35.6F), shot) > 0.0F)
                    FM.AS.setJamBullets(1, 0);
                if(s.startsWith("xxgunr") && getEnergyPastArmor(World.Rnd().nextFloat(2.0F, 35.6F), shot) > 0.0F)
                    FM.AS.setJamBullets(1, 1);
                if(s.startsWith("xxeqpt"));
            }
        } else
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
            if(!s.endsWith("a") && !s.endsWith("a2"))
            {
                if(!s.endsWith("b") && !s.endsWith("b2"))
                {
                    i1 = s.charAt(5) - 49;
                } else
                {
                    byte0 = 2;
                    i1 = s.charAt(6) - 49;
                }
            } else
            {
                byte0 = 1;
                i1 = s.charAt(6) - 49;
            }
            hitFlesh(i1, shot, byte0);
        }
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(flag && bCockpitNVentilated)
            FM.AS.hitPilot(this, 0, 1);
        if((FM instanceof RealFlightModel) && ((RealFlightModel)FM).isRealMode() || !flag || !(FM instanceof Pilot))
            return;
        Pilot pilot = (Pilot)FM;
        if(pilot.get_maneuver() == 63 && pilot.target != null)
        {
            Point3d point3d = new Point3d(pilot.target.Loc);
            point3d.sub(FM.Loc);
            FM.Or.transformInv(point3d);
            if((point3d.x > 4000D && point3d.x < 5500D || point3d.x > 100D && point3d.x < 5000D && World.Rnd().nextFloat() < 0.33F) && Time.current() > tX4Prev + 10000L)
            {
                bToFire = true;
                tX4Prev = Time.current();
            }
        }
    }

    public void doMurderPilot(int i)
    {
        if(i == 0)
        {
            hierMesh().chunkVisible("Pilot1_D0", false);
            hierMesh().chunkVisible("Head1_D0", false);
            hierMesh().chunkVisible("Pilot1_D1", true);
            hierMesh().chunkVisible("HMask1_D0", false);
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
        if ((!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) && (this.FM instanceof Maneuver)) {
            if (this.FM.Gears.onGround() || this.FM.Gears.nearGround()) {
                this.FM.CT.forceGear(1F);
                return;
            }
        }
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
            Aircraft.xyz[1] = -0.706F;
            Aircraft.ypr[0] = 4.5F * f;
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
    }

    protected void moveAirBrake(float f)
    {
        hierMesh().chunkSetAngles("Brake01_D0", 0.0F, -30F * f, 0.0F);
        hierMesh().chunkSetAngles("Brake02_D0", 0.0F, -30F * f, 0.0F);
        hierMesh().chunkSetAngles("VatorL_D0", 0.0F, 30F * f, 0.0F);
        hierMesh().chunkSetAngles("VatorR_D0", 0.0F, 30F * f, 0.0F);
    }

    protected void moveFan(float f)
    {
        if(!Config.isUSE_RENDER())
            return;
        if(isNetMirror())
        {
            if(FM.EI.engines[0].getStage() == 6)
            {
                Eff3DActor.setIntesity(flame, 1.0F);
                Eff3DActor.setIntesity(dust, 1.0F);
                Eff3DActor.setIntesity(trail, 1.0F);
                Eff3DActor.setIntesity(sprite, 1.0F);
            } else
            {
                Eff3DActor.setIntesity(flame, 0.0F);
                Eff3DActor.setIntesity(dust, 0.0F);
                Eff3DActor.setIntesity(trail, 0.0F);
                Eff3DActor.setIntesity(sprite, 0.0F);
            }
            if(FM.EI.engines[1].getStage() == 6)
            {
                Eff3DActor.setIntesity(dust2, 1.0F);
                Eff3DActor.setIntesity(flame1, 1.0F);
                Eff3DActor.setIntesity(flame2, 1.0F);
                Eff3DActor.setIntesity(flame3, 1.0F);
                Eff3DActor.setIntesity(flame4, 1.0F);
                Eff3DActor.setIntesity(flame5, 1.0F);
                Eff3DActor.setIntesity(flame6, 1.0F);
                Eff3DActor.setIntesity(flame7, 1.0F);
            } else
            {
                Eff3DActor.setIntesity(dust2, 0.0F);
                Eff3DActor.setIntesity(flame1, 0.0F);
                Eff3DActor.setIntesity(flame2, 0.0F);
                Eff3DActor.setIntesity(flame3, 0.0F);
                Eff3DActor.setIntesity(flame4, 0.0F);
                Eff3DActor.setIntesity(flame5, 0.0F);
                Eff3DActor.setIntesity(flame6, 0.0F);
                Eff3DActor.setIntesity(flame7, 0.0F);
            }
        } else
        if(bHasEngine && FM.CT.getPower() > 0.0F && FM.EI.engines[0].getStage() == 6)
        {
            Eff3DActor.setIntesity(flame, 1.0F);
            Eff3DActor.setIntesity(dust, 1.0F);
            Eff3DActor.setIntesity(trail, 1.0F);
            Eff3DActor.setIntesity(sprite, 1.0F);
        } else
        {
            Eff3DActor.setIntesity(flame, 0.0F);
            Eff3DActor.setIntesity(dust, 0.0F);
            Eff3DActor.setIntesity(trail, 0.0F);
            Eff3DActor.setIntesity(sprite, 0.0F);
        }
        if(bHasEngine && FM.CT.getPower() > 0.15F && FM.EI.engines[1].getThrustOutput() > 0.15F && FM.EI.engines[1].getStage() == 6)
        {
            Eff3DActor.setIntesity(dust2, 1.0F);
            Eff3DActor.setIntesity(flame1, 1.0F);
            Eff3DActor.setIntesity(flame2, 1.0F);
            Eff3DActor.setIntesity(flame3, 1.0F);
            Eff3DActor.setIntesity(flame4, 1.0F);
            Eff3DActor.setIntesity(flame5, 1.0F);
            Eff3DActor.setIntesity(flame6, 1.0F);
            Eff3DActor.setIntesity(flame7, 1.0F);
        } else
        {
            Eff3DActor.setIntesity(dust2, 0.0F);
            Eff3DActor.setIntesity(flame1, 0.0F);
            Eff3DActor.setIntesity(flame2, 0.0F);
            Eff3DActor.setIntesity(flame3, 0.0F);
            Eff3DActor.setIntesity(flame4, 0.0F);
            Eff3DActor.setIntesity(flame5, 0.0F);
            Eff3DActor.setIntesity(flame6, 0.0F);
            Eff3DActor.setIntesity(flame7, 0.0F);
        }
        if(FM.Gears.onGround() && FM.CT.getGear() > 0.9F && FM.getSpeed() > 5F)
        {
            if(FM.Gears.lgear)
                hierMesh().chunkSetAngles("GearL6_D0", World.Rnd().nextFloat(-3F, 3F), -75F + World.Rnd().nextFloat(-3F, 3F), World.Rnd().nextFloat(-3F, 3F));
            if(FM.Gears.rgear)
                hierMesh().chunkSetAngles("GearR6_D0", World.Rnd().nextFloat(-3F, 3F), 75F + World.Rnd().nextFloat(-3F, 3F), World.Rnd().nextFloat(-3F, 3F));
        }
        if(FM.EI.engines[1].getThrustOutput() > 0.4F && FM.EI.engines[1].getStage() == 6)
        {
            if(FM.EI.engines[1].getThrustOutput() > 0.65F)
                FM.AS.setSootState(this, 1, 5);
            else
                FM.AS.setSootState(this, 1, 4);
        } else
        {
            FM.AS.setSootState(this, 1, 0);
        }
    }

    protected boolean cutFM(int i, int j, Actor actor)
    {
        switch(i)
        {
        case 3:
        case 19:
            bHasEngine = false;
            FM.AS.setEngineDies(this, 0);
            return cut(Aircraft.partNames()[i]);

        case 4:
        case 33:
        case 34:
        case 35:
            FM.AS.setEngineDies(this, 1);
            return cut(Aircraft.partNames()[i]);

//        case 5:
//        case 36:
//        case 37:
//        case 38:
//            FM.AS.setEngineDies(this, 2);
//            return cut(Aircraft.partNames()[i]);
//
//        case 6:
//        case 7:
//        case 8:
//        case 9:
//        case 10:
//        case 11:
//        case 12:
//        case 13:
//        case 14:
//        case 15:
//        case 16:
//        case 17:
//        case 18:
//        case 20:
//        case 21:
//        case 22:
//        case 23:
//        case 24:
//        case 25:
//        case 26:
//        case 27:
//        case 28:
//        case 29:
//        case 30:
//        case 31:
//        case 32:
        default:
            return super.cutFM(i, j, actor);
        }
    }

    public void update(float f)
    {
        super.update(f);
        if(isNetMirror())
            return;
        FM.setCapableOfBMP(true, this);
        FM.setCapableOfACM(true);
        bPowR = (this == World.getPlayerAircraft() && FM instanceof RealFlightModel && ((RealFlightModel)FM).isRealMode());
        float lPowR = bPowR ? 0.4120879F : 0.77F;
        if((double)FM.getAltitude() - Engine.land().HQ(FM.Loc.x, FM.Loc.y) > 5D && FM.M.fuel > 0.0F)
        {
            if(FM.EI.engines[0].getControlThrottle() > lPowR * 1.1F && FM.EI.engines[0].getStage() == 0)
            {
                FM.EI.engines[0].setStage(this, 6);
                if(bPowR)
                    HUD.log("EngineI" + (FM.EI.engines[0].getStage() == 6 ? 49 : '0'));
            }
            if(FM.CT.PowerControl < lPowR * 0.9F && FM.EI.engines[0].getStage() > 0)
            {
                FM.EI.engines[0].setEngineStops(this);
                if(bPowR)
                    HUD.log("EngineI" + (FM.EI.engines[0].getStage() == 6 ? 49 : '0'));
            }
        }
        if(FM.EI.engines[1].getControlThrottle() > 0.0F && FM.EI.engines[1].getStage() == 0 && FM.M.nitro > 0.0F)
            FM.EI.engines[1].setStage(this, 6);
        if(FM.EI.engines[1].getControlThrottle() < 0.0F && FM.EI.engines[1].getStage() == 6)
            FM.EI.engines[1].setStage(this, 0);
        if(bCartAttached)
        {
            moveGear(FM.CT.getGear());
            if((FM instanceof RealFlightModel) && ((RealFlightModel)FM).isRealMode())
                FM.CT.bHasBrakeControl = false;
            else
                FM.CT.bHasBrakeControl = true;
        }

        if ((!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) && (this.FM instanceof Maneuver)) {
            Maneuver maneuver = (Maneuver) this.FM;
            if ((maneuver.get_maneuver() == Maneuver.TAKEOFF) && (this.FM.Gears.onGround() || this.FM.Gears.nearGround())) {
                this.FM.Vmin = this.vMinFromFmd * 0.8F;
                this.FM.VminFLAPS = this.vMinFlapsFromFmd * 0.8F;
            }
            else {
                if (this.bCartAttached && !this.FM.Gears.onGround() && !this.FM.Gears.nearGround()) FM.CT.setGear(0F);
            }
        }
        
        if (this.FM.Vmin < this.vMinFromFmd * 0.99F) this.FM.Vmin = this.FM.Vmin * 0.99F + this.vMinFromFmd * 0.01F;
        if (this.FM.VminFLAPS < this.vMinFlapsFromFmd * 0.99F) this.FM.VminFLAPS = this.FM.VminFLAPS * 0.99F + this.vMinFlapsFromFmd * 0.01F;
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

    private boolean bHasEngine;
    private boolean bCockpitNVentilated;
    public boolean bCartAttached;
    private Eff3DActor flame;
    private Eff3DActor flame1;
    private Eff3DActor flame2;
    private Eff3DActor flame3;
    private Eff3DActor flame4;
    private Eff3DActor flame5;
    private Eff3DActor flame6;
    private Eff3DActor flame7;
    private Eff3DActor dust;
    private Eff3DActor dust2;
    private Eff3DActor trail;
    private Eff3DActor sprite;
    private Eff3DActor turboexhaust;
    private boolean bPowR;
    public boolean bToFire;
    private long tX4Prev;
    private float deltaAzimuth;
    private float deltaTangage;
    private float vMinFromFmd;
    private float vMinFlapsFromFmd;

    static 
    {
        Class class1 = Li_P13.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "P13");
        Property.set(class1, "meshName", "3DO/Plane/Li-P13/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
        Property.set(class1, "yearService", 1944F);
        Property.set(class1, "yearExpired", 1946F);
        Property.set(class1, "FlightModel", "FlightModels/Li-P13a.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            CockpitLi_P13.class
        });
        Property.set(class1, "LOSElevation", 0.87325F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            1, 1, 2, 2, 2, 2
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04"
        });
    }
}
