package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.AircraftState;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.objects.weapons.BombStarthilfe109500;
import com.maddox.rts.MsgAction;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public abstract class AR_234C extends Scheme2
    implements TypeBomber, TypeRocketBoost
{
    
    public AR_234C() {
        this.booster = new BombStarthilfe109500[2];
        this.boosterEffects = new Eff3DActor[2];
        this.boostState = AircraftState._AS_BOOST_NOBOOST;
        this.boosterFireOutTime = -1L;
    }
    
    public void setBoostState(int state) {
        if (this.boostState == state) return; // Nothing to do here, we are in the requested state already.
        if (((state ^ this.boostState) & AircraftState._AS_BOOST_EXISTS) != 0) { // The existence of boosters has changed
            if ((state & AircraftState._AS_BOOST_EXISTS) != 0) { // Boosters exist now
                this.doAttachBoosters();
            } else { // Boosters don't exist anymore
                this.doCutBoosters();
            }
        }
        if (((state ^ this.boostState) & AircraftState._AS_BOOST_ACTIVE) != 0) { // The boosters activity state changed
            if ((state & AircraftState._AS_BOOST_ACTIVE) != 0) { // Boosters are active now
                this.doFireBoosters();
            } else { // Boosters aren't active
                this.doShutoffBoosters();
            }
        }
        this.boostState = state;
    }

    public int getBoostState() {
        return this.boostState;
    }

    public void doAttachBoosters() {
        for (int i = 0; i < 2; i++) {
            try {
                this.booster[i] = new BombStarthilfe109500();
                this.booster[i].pos.setBase(this, this.findHook("_BoosterH" + (i + 1)), false);
                this.booster[i].pos.resetAsBase();
                this.booster[i].drawing(true);
            } catch (Exception exception) {
                this.debugprintln("Structure corrupt - can't hang Starthilferakete..");
            }
        }
    }
    
    public void doCutBoosters() {
      for (int i = 0; i < 2; i++) {
          if (this.booster[i] != null) {
              this.booster[i].start();
              this.booster[i] = null;
          }
      }
      this.stopBoosterSound();
    }

    public void doFireBoosters() {
        for (int i=0; i<2; i++) {
            this.boosterEffects[i] = Eff3DActor.New(this, this.findHook("_Booster" + (i+1)), null, 1.0F, "3DO/Effects/Tracers/HydrogenRocket/rocket.eff", 30F);
        }
        this.startBoosterSound();
    }

    public void doShutoffBoosters() {
        for (int i=0; i<2; i++) {
            Eff3DActor.finish(this.boosterEffects[i]); // No null checks etc. required here, it's done internally already.
        }
        this.stopBoosterSound();
    }

    public void startBoosterSound() {
        for (int i = 0; i < 2; i++) {
            if (this.booster[i] != null) {
                this.booster[i].startSound();
            }
        }
    }

    public void stopBoosterSound() {
        for (int i = 0; i < 2; i++) {
            if (this.booster[i] != null) {
                this.booster[i].stopSound();
            }
        }
    }
    
    private void boostUpdate() {
        if (!(this.FM instanceof Pilot)) return;
        if ((this.boostState & AircraftState._AS_BOOST_EXISTS) == 0) return;
            // TODO: Changed Booster cutoff reasons from absolute altitude to altitude above ground
            if (this.FM.getAltitude() - World.land().HQ_Air(this.FM.Loc.x, this.FM.Loc.y) > 300F && this.boosterFireOutTime == -1L && this.FM.Loc.z != 0.0D && World.Rnd().nextFloat() < 0.05F) {
                this.FM.AS.setBoostState(this, AircraftState._AS_BOOST_NOBOOST);
                this.FM.AS.setGliderBoostOff();
            }
            if (this.boosterFireOutTime == -1L && this.FM.Gears.onGround() && this.FM.EI.getPowerOutput() > 0.8F && this.FM.EI.engines[0].getStage() == 6 && this.FM.EI.engines[1].getStage() == 6 && this.FM.getSpeedKMH() > 20F) {
                this.boosterFireOutTime = Time.current() + 30000L;
                this.FM.AS.setBoostState(this, this.boostState | AircraftState._AS_BOOST_ACTIVE);
                this.FM.AS.setGliderBoostOn();
            }
            if (this.boosterFireOutTime > 0L) {
                if (Time.current() < this.boosterFireOutTime) {
                    this.FM.producedAF.x += 20000D;
                } else { // Stop sound
                    this.FM.AS.setBoostState(this, this.boostState & ~AircraftState._AS_BOOST_ACTIVE);
                }
                if (Time.current() > this.boosterFireOutTime + 10000L) { // cut boosters 10 seconds after burnout regardless altitude if not done so before.
                    this.FM.AS.setBoostState(this, AircraftState._AS_BOOST_NOBOOST);
                    this.FM.AS.setGliderBoostOff();
                }
            }
    }
    
    public void setOnGround(Point3d point3d, Orient orient, Vector3d vector3d) {
        super.setOnGround(point3d, orient, vector3d);
        if (!this.isNetMaster()) return; // FIXME: Maybe FM.AS.isMaster() works better? Idea is to deal with "setOnGround" in single player missions only, or in Dogfight missions on server side only.
        this.FM.AS.setBoostState(this, this.boostState | AircraftState._AS_BOOST_EXISTS);
    }

    public void destroy() {
        this.doCutBoosters();
        super.destroy();
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
            this.FM.AS.setBoostState(this, AircraftState._AS_BOOST_NOBOOST);
            this.FM.AS.setGliderBoostOff();
            break;
        }
        return super.cutFM(i, j, actor);
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

    public static void moveGear(HierMesh hiermesh, float f, float f1, float f2)
    {
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, Aircraft.cvt(f2, 0.2F, 0.65F, 0.0F, -92F), 0.0F);
        hiermesh.chunkSetAngles("GearC3_D0", 0.0F, 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(f2, 0.2F, 0.65F, 0.0F, -62.5F), 0.0F);
        if(f2 < 0.525F)
        {
            hiermesh.chunkSetAngles("GearC6_D0", 0.0F, Aircraft.cvt(f2, 0.2F, 0.525F, 0.0F, -46F), 0.0F);
            hiermesh.chunkSetAngles("GearC7_D0", 0.0F, Aircraft.cvt(f2, 0.2F, 0.525F, 0.0F, -0.25F), 0.0F);
        } else
        {
            hiermesh.chunkSetAngles("GearC6_D0", 0.0F, Aircraft.cvt(f2, 0.525F, 0.65F, -46F, -73.5F), 0.0F);
            hiermesh.chunkSetAngles("GearC7_D0", 0.0F, Aircraft.cvt(f2, 0.525F, 0.65F, -0.25F, -7.5F), 0.0F);
        }
        Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = 0.0F;
        Aircraft.xyz[1] = Aircraft.cvt(f2, 0.2F, 0.65F, 0.0F, -0.2935F);
        hiermesh.chunkSetLocate("GearC8_D0", Aircraft.xyz, Aircraft.ypr);
        hiermesh.chunkSetAngles("GearC9_D0", 0.0F, Aircraft.cvt(f2, 0.07F, 0.32F, 0.0F, -98F), 0.0F);
        hiermesh.chunkSetAngles("GearC10_D0", 0.0F, Aircraft.cvt(f2, 0.07F, 0.32F, 0.0F, -98F), 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(f, 0.3F, 0.8F, 0.0F, -90F), 0.0F);
        hiermesh.chunkSetAngles("GearL6_D0", 0.0F, floatindex(Aircraft.cvt(f, 0.3F, 0.8F, 0.0F, 3F), gear6), 0.0F);
        Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = 0.0F;
        Aircraft.xyz[1] = floatindex(Aircraft.cvt(f, 0.3F, 0.8F, 0.0F, 3F), gear7);
        hiermesh.chunkSetLocate("GearL7_D0", Aircraft.xyz, Aircraft.ypr);
        if(f < 0.6F)
        {
            hiermesh.chunkSetAngles("GearL8_D0", 0.0F, Aircraft.cvt(f, 0.3F, 0.6F, 0.0F, -76.5F), 0.0F);
            hiermesh.chunkSetAngles("GearL9_D0", 0.0F, Aircraft.cvt(f, 0.1F, 0.3F, 0.0F, -44F), 0.0F);
        } else
        {
            hiermesh.chunkSetAngles("GearL8_D0", 0.0F, Aircraft.cvt(f, 0.6F, 0.8F, -76.5F, -62F), 0.0F);
            hiermesh.chunkSetAngles("GearL9_D0", 0.0F, Aircraft.cvt(f, 0.6F, 0.8F, -44F, 0.0F), 0.0F);
        }
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(f1, 0.3F, 0.8F, 0.0F, -90F), 0.0F);
        hiermesh.chunkSetAngles("GearR6_D0", 0.0F, floatindex(Aircraft.cvt(f1, 0.3F, 0.8F, 0.0F, 3F), gear6), 0.0F);
        Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = 0.0F;
        Aircraft.xyz[1] = floatindex(Aircraft.cvt(f1, 0.3F, 0.8F, 0.0F, 3F), gear7);
        hiermesh.chunkSetLocate("GearR7_D0", Aircraft.xyz, Aircraft.ypr);
        if(f1 < 0.6F)
        {
            hiermesh.chunkSetAngles("GearR8_D0", 0.0F, Aircraft.cvt(f1, 0.3F, 0.6F, 0.0F, -76.5F), 0.0F);
            hiermesh.chunkSetAngles("GearR9_D0", 0.0F, Aircraft.cvt(f1, 0.1F, 0.3F, 0.0F, -44F), 0.0F);
        } else
        {
            hiermesh.chunkSetAngles("GearR8_D0", 0.0F, Aircraft.cvt(f1, 0.6F, 0.8F, -76.5F, -62F), 0.0F);
            hiermesh.chunkSetAngles("GearR9_D0", 0.0F, Aircraft.cvt(f1, 0.6F, 0.8F, -44F, 0.0F), 0.0F);
        }
    }

    protected void moveGear(float f, float f1, float f2)
    {
        moveGear(hierMesh(), f, f1, f2);
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        moveGear(hiermesh, f, f, f);
    }

    protected void moveGear(float f) {
        moveGear(this.hierMesh(), f, f, f);
    }

    protected void moveFlap(float f)
    {
        float f1 = -35F * f;
        hierMesh().chunkSetAngles("Flap01_D0", 0.0F, f1, 0.0F);
        hierMesh().chunkSetAngles("Flap02_D0", 0.0F, f1, 0.0F);
        hierMesh().chunkSetAngles("Flap03_D0", 0.0F, f1, 0.0F);
        hierMesh().chunkSetAngles("Flap04_D0", 0.0F, f1, 0.0F);
    }

    public void moveWheelSink()
    {
        resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.32F, 0.0F, -0.3274F);
        hierMesh().chunkSetLocate("GearL3_D0", Aircraft.xyz, Aircraft.ypr);
        hierMesh().chunkSetAngles("GearL4_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.32F, 0.0F, -46F), 0.0F);
        hierMesh().chunkSetAngles("GearL5_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.32F, 0.0F, -46F), 0.0F);
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.32F, 0.0F, -0.3274F);
        hierMesh().chunkSetLocate("GearR3_D0", Aircraft.xyz, Aircraft.ypr);
        hierMesh().chunkSetAngles("GearR4_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.32F, 0.0F, -46F), 0.0F);
        hierMesh().chunkSetAngles("GearR5_D0", 0.0F, Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.32F, 0.0F, -46F), 0.0F);
    }

    public void moveSteering(float f)
    {
        if(this.FM.CT.getGear() > 0.8F)
            hierMesh().chunkSetAngles("GearC3_D0", 0.0F, -f, 0.0F);
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xx"))
        {
            if(s.startsWith("xxarmor"))
            {
                debuggunnery("Armor: Hit..");
                if(s.startsWith("xxarmorp"))
                    getEnergyPastArmor(15.15D / (Math.abs(Aircraft.v1.x) + 0.00001D), shot);
                return;
            }
            if(s.startsWith("xxcannon"))
            {
                if(s.endsWith("01"))
                {
                    debuggunnery("Armament System: Left Rear Cannon: Disabled..");
                    this.FM.AS.setJamBullets(0, 0);
                }
                if(s.endsWith("02"))
                {
                    debuggunnery("Armament System: Right Rear Cannon: Disabled..");
                    this.FM.AS.setJamBullets(0, 1);
                }
                getEnergyPastArmor(World.Rnd().nextFloat(6.98F, 24.35F), shot);
                return;
            }
            if(s.startsWith("xxcontrols"))
            {
                int i = s.charAt(10) - 48;
                if(s.length() == 12)
                    i = 10 + (s.charAt(11) - 48);
                switch(i)
                {
                default:
                    break;

                case 1:
                    if(getEnergyPastArmor(3.2F, shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** Control Column: Hit, Controls Destroyed..");
                        this.FM.AS.setControlsDamage(shot.initiator, 2);
                        this.FM.AS.setControlsDamage(shot.initiator, 1);
                        this.FM.AS.setControlsDamage(shot.initiator, 0);
                    }
                    break;

                case 2:
                    if(getEnergyPastArmor(0.1F, shot) <= 0.0F)
                        break;
                    Aircraft.debugprintln(this, "*** Throttle Quadrant: Hit, Engine Controls Disabled..");
                    this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
                    if(World.Rnd().nextFloat() < 0.5F)
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                    else
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 1, 1);
                    break;

                case 3:
                    if(getEnergyPastArmor(1.0F, shot) > 0.0F)
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                    break;

                case 7:
                    if(getEnergyPastArmor(1.0F, shot) > 0.0F)
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                    break;

                case 4:
                case 6:
                case 8:
                case 10:
                    if(getEnergyPastArmor(2.1F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                    {
                        Aircraft.debugprintln(this, "*** Aileron Controls: Disabled..");
                        this.FM.AS.setControlsDamage(shot.initiator, 0);
                    }
                    break;

                case 5:
                case 9:
                    if(getEnergyPastArmor(4.1F, shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** Aileron Controls Crank: Disabled..");
                        this.FM.AS.setControlsDamage(shot.initiator, 0);
                    }
                    break;

                case 11:
                case 12:
                    if(getEnergyPastArmor(0.3F, shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** Rudder Controls: Disabled / Strings Broken..");
                        this.FM.AS.setControlsDamage(shot.initiator, 2);
                    }
                    break;
                }
                return;
            }
            if(s.startsWith("xxEng"))
            {
                int j = s.charAt(5) - 49;
                if(point3d.x > 0.0D)
                {
                    if(getEnergyPastArmor(0.1F, shot) > 0.0F)
                    {
                        Aircraft.debugprintln(this, "*** Engine Module(s): Supercharger Disabled..");
                        this.FM.AS.setEngineSpecificDamage(shot.initiator, j, 0);
                    }
                } else
                {
                    if(getEnergyPastArmor(3.2F, shot) > 0.0F)
                        this.FM.EI.engines[j].setCyliderKnockOut(shot.initiator, 1);
                    if(World.Rnd().nextFloat(0.009F, 0.1357F) < shot.mass && this.FM.EI.engines[j].getStage() == 6)
                        this.FM.AS.hitEngine(shot.initiator, j, 1);
                    getEnergyPastArmor(14.296F, shot);
                }
                return;
            }
            if(s.startsWith("xxLock"))
            {
                debuggunnery("Lock Construction: Hit..");
                if(s.startsWith("xxLockR") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: Rudder1 Lock Shot Off..");
                    nextDMGLevels(3, 2, "Rudder1_D" + chunkDamageVisible("Rudder1"), shot.initiator);
                }
                if(s.startsWith("xxLockVL") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: VatorL Lock Shot Off..");
                    nextDMGLevels(3, 2, "VatorL_D" + chunkDamageVisible("VatorL"), shot.initiator);
                }
                if(s.startsWith("xxLockVR") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: VatorR Lock Shot Off..");
                    nextDMGLevels(3, 2, "VatorR_D" + chunkDamageVisible("VatorR"), shot.initiator);
                }
                if(s.startsWith("xxLockAL") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: AroneL Lock Shot Off..");
                    nextDMGLevels(3, 2, "AroneL_D" + chunkDamageVisible("AroneL"), shot.initiator);
                }
                if(s.startsWith("xxLockAR") && getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)
                {
                    debuggunnery("Lock Construction: AroneR Lock Shot Off..");
                    nextDMGLevels(3, 2, "AroneR_D" + chunkDamageVisible("AroneR"), shot.initiator);
                }
            }
            if(s.startsWith("xxSpar"))
            {
                debuggunnery("Spar Construction: Hit..");
                if(s.startsWith("xxSparLI") && chunkDamageVisible("WingLIn") > 2 && (double)World.Rnd().nextFloat() > Math.abs(Aircraft.v1.x) + 0.12D && getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingLIn Spar Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if(s.startsWith("xxSparrI") && chunkDamageVisible("WingRIn") > 2 && (double)World.Rnd().nextFloat() > Math.abs(Aircraft.v1.x) + 0.12D && getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingRIn Spar Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if(s.startsWith("xxSparlm") && chunkDamageVisible("WingLMid") > 2 && (double)World.Rnd().nextFloat() > Math.abs(Aircraft.v1.x) + 0.12D && getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingLMid Spar Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if(s.startsWith("xxSparrm") && chunkDamageVisible("WingRMid") > 2 && (double)World.Rnd().nextFloat() > Math.abs(Aircraft.v1.x) + 0.12D && getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingRMid Spar Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if(s.startsWith("xxSparlo") && chunkDamageVisible("WingLOut") > 2 && (double)World.Rnd().nextFloat() > Math.abs(Aircraft.v1.x) + 0.12D && getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingLOut Spar Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if(s.startsWith("xxSparro") && chunkDamageVisible("WingROut") > 2 && (double)World.Rnd().nextFloat() > Math.abs(Aircraft.v1.x) + 0.12D && getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)
                {
                    debuggunnery("Spar Construction: WingROut Spar Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                if(s.startsWith("xxSpart") && chunkDamageVisible("Tail1") > 2 && getEnergyPastArmor(3.86F / (float)Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F && World.Rnd().nextFloat() < 0.25F)
                {
                    debuggunnery("Spar Construction: Tail1 Ribs Hit, Breaking in Half..");
                    nextDMGLevels(1, 2, "Tail1_D3", shot.initiator);
                }
            }
            if(s.startsWith("xxTank"))
            {
                int k = s.charAt(6) - 49;
                if(getEnergyPastArmor(0.1F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F && shot.powerType == 3)
                    this.FM.AS.hitTank(shot.initiator, k, 2);
                return;
            } else
            {
                return;
            }
        }
        if(s.startsWith("xcf"))
        {
            if(chunkDamageVisible("CF") < 3)
                hitChunk("CF", shot);
            if(point3d.x > 0.5D)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
            else
            if(point3d.y > 0.0D)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
            else
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
            if(World.Rnd().nextFloat() < 0.05F)
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
        } else
        if(s.startsWith("xtail"))
        {
            if(chunkDamageVisible("Tail1") < 3)
                hitChunk("Tail1", shot);
        } else
        if(s.startsWith("xkeel"))
            hitChunk("Keel1", shot);
        else
        if(s.startsWith("xrudder"))
        {
            if(chunkDamageVisible("Rudder1") < 1)
                hitChunk("Rudder1", shot);
        } else
        if(s.startsWith("xstab"))
        {
            if(s.startsWith("xstabl"))
                hitChunk("StabL", shot);
            if(s.startsWith("xstabr"))
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
            if(s.startsWith("xaronel") && chunkDamageVisible("AroneL") < 1)
                hitChunk("AroneL", shot);
            if(s.startsWith("xaroner") && chunkDamageVisible("AroneR") < 1)
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
        {
            if(s.endsWith("1") && World.Rnd().nextFloat() < 0.05F)
            {
                debuggunnery("Hydro System: Disabled..");
                this.FM.AS.setInternalDamage(shot.initiator, 0);
            }
        } else
        if(s.startsWith("xpilot") || s.startsWith("xhead"))
        {
            byte byte0 = 0;
            int l;
            if(s.endsWith("a"))
            {
                byte0 = 1;
                l = s.charAt(6) - 49;
            } else
            if(s.endsWith("b"))
            {
                byte0 = 2;
                l = s.charAt(6) - 49;
            } else
            {
                l = s.charAt(5) - 49;
            }
            hitFlesh(l, shot, byte0);
        }
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(flag && World.Rnd().nextFloat() < 0.04F)
        {
            if(this.FM.AS.astateEngineStates[0] > 3 && World.Rnd().nextFloat() < 0.12F)
            {
                this.FM.AS.explodeEngine(this, 0);
                msgCollision(this, "WingLIn_D0", "WingLIn_D0");
                if(World.Rnd().nextBoolean())
                    this.FM.AS.hitTank(this, 0, World.Rnd().nextInt(1, 8));
                else
                    this.FM.AS.hitTank(this, 1, World.Rnd().nextInt(1, 8));
            }
            if(this.FM.AS.astateEngineStates[1] > 3 && World.Rnd().nextFloat() < 0.12F)
            {
                this.FM.AS.explodeEngine(this, 1);
                msgCollision(this, "WingLIn_D0", "WingLIn_D0");
                if(World.Rnd().nextBoolean())
                    this.FM.AS.hitTank(this, 0, World.Rnd().nextInt(1, 8));
                else
                    this.FM.AS.hitTank(this, 1, World.Rnd().nextInt(1, 8));
            }
            if(this.FM.AS.astateEngineStates[2] > 3 && World.Rnd().nextFloat() < 0.12F)
            {
                this.FM.AS.explodeEngine(this, 2);
                msgCollision(this, "WingRIn_D0", "WingRIn_D0");
                if(World.Rnd().nextBoolean())
                    this.FM.AS.hitTank(this, 0, World.Rnd().nextInt(1, 8));
                else
                    this.FM.AS.hitTank(this, 1, World.Rnd().nextInt(1, 8));
            }
            if(this.FM.AS.astateEngineStates[3] > 3 && World.Rnd().nextFloat() < 0.12F)
            {
                this.FM.AS.explodeEngine(this, 3);
                msgCollision(this, "WingRIn_D0", "WingRIn_D0");
                if(World.Rnd().nextBoolean())
                    this.FM.AS.hitTank(this, 0, World.Rnd().nextInt(1, 8));
                else
                    this.FM.AS.hitTank(this, 1, World.Rnd().nextInt(1, 8));
            }
        }
        if(this.FM.getAltitude() < 3000F)
            hierMesh().chunkVisible("HMask1_D0", false);
        else
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Head1_D0"));
    }

    public void doEjectCatapult()
    {
        new MsgAction(false, this) {

            public void doAction(Object obj)
            {
                Aircraft aircraft = (Aircraft)obj;
                if(!Actor.isValid(aircraft))
                {
                    return;
                } else
                {
                    Loc loc = new Loc();
                    Loc loc1 = new Loc();
                    Vector3d vector3d = new Vector3d(0.0D, 0.0D, 10D);
                    HookNamed hooknamed = new HookNamed(aircraft, "_ExternalSeat01");
                    aircraft.pos.getAbs(loc1);
                    hooknamed.computePos(aircraft, loc1, loc);
                    loc.transform(vector3d);
                    vector3d.x += aircraft.FM.Vwld.x;
                    vector3d.y += aircraft.FM.Vwld.y;
                    vector3d.z += aircraft.FM.Vwld.z;
                    new EjectionSeat(3, loc, vector3d, aircraft);
                    return;
                }
            }

        }
;
        hierMesh().chunkVisible("Seat_D0", false);
    }

    public void update(float f)
    {
        if(this.FM.AS.isMaster())
        {
            for(int i = 0; i < 2; i++)
                if(curctl[i] == -1F)
                {
                    curctl[i] = oldctl[i] = this.FM.EI.engines[i].getControlThrottle();
                } else
                {
                    curctl[i] = this.FM.EI.engines[i].getControlThrottle();
                    if((curctl[i] - oldctl[i]) / f > 3F && this.FM.EI.engines[i].getRPM() < 2400F && this.FM.EI.engines[i].getStage() == 6 && World.Rnd().nextFloat() < 0.25F)
                        this.FM.AS.hitEngine(this, i, 100);
                    if((curctl[i] - oldctl[i]) / f < -3F && this.FM.EI.engines[i].getRPM() < 2400F && this.FM.EI.engines[i].getStage() == 6)
                    {
                        if(World.Rnd().nextFloat() < 0.25F && (this.FM instanceof RealFlightModel) && ((RealFlightModel)this.FM).isRealMode())
                            this.FM.EI.engines[i].setEngineStops(this);
                        if(World.Rnd().nextFloat() < 0.75F && (this.FM instanceof RealFlightModel) && ((RealFlightModel)this.FM).isRealMode())
                            this.FM.EI.engines[i].setKillCompressor(this);
                    }
                    oldctl[i] = curctl[i];
                }

            if(Config.isUSE_RENDER())
            {
                if(this.FM.EI.engines[0].getPowerOutput() > 0.8F && this.FM.EI.engines[0].getStage() == 6)
                {
                    if(this.FM.EI.engines[0].getPowerOutput() > 0.95F)
                        this.FM.AS.setSootState(this, 0, 3);
                    else
                        this.FM.AS.setSootState(this, 0, 2);
                } else
                {
                    this.FM.AS.setSootState(this, 0, 0);
                }
                if(this.FM.EI.engines[1].getPowerOutput() > 0.8F && this.FM.EI.engines[1].getStage() == 6)
                {
                    if(this.FM.EI.engines[1].getPowerOutput() > 0.95F)
                        this.FM.AS.setSootState(this, 1, 3);
                    else
                        this.FM.AS.setSootState(this, 1, 2);
                } else
                {
                    this.FM.AS.setSootState(this, 1, 0);
                }
                if(this.FM.EI.engines[2].getPowerOutput() > 0.8F && this.FM.EI.engines[2].getStage() == 6)
                {
                    if(this.FM.EI.engines[2].getPowerOutput() > 0.95F)
                        this.FM.AS.setSootState(this, 2, 3);
                    else
                        this.FM.AS.setSootState(this, 2, 2);
                } else
                {
                    this.FM.AS.setSootState(this, 2, 0);
                }
                if(this.FM.EI.engines[3].getPowerOutput() > 0.8F && this.FM.EI.engines[3].getStage() == 6)
                {
                    if(this.FM.EI.engines[3].getPowerOutput() > 0.95F)
                        this.FM.AS.setSootState(this, 3, 3);
                    else
                        this.FM.AS.setSootState(this, 3, 2);
                } else
                {
                    this.FM.AS.setSootState(this, 3, 0);
                }
            }
        }
        super.update(f);
        this.boostUpdate();
    }

    public abstract void typeBomberReplicateFromNet(NetMsgInput netmsginput)
        throws IOException;

    public abstract void typeBomberReplicateToNet(NetMsgGuaranted netmsgguaranted)
        throws IOException;

    public abstract void typeBomberUpdate(float f);

    public abstract void typeBomberAdjSpeedMinus();

    public abstract void typeBomberAdjSpeedPlus();

    public abstract void typeBomberAdjSpeedReset();

    public abstract void typeBomberAdjAltitudeMinus();

    public abstract void typeBomberAdjAltitudePlus();

    public abstract void typeBomberAdjAltitudeReset();

    public abstract void typeBomberAdjSideslipMinus();

    public abstract void typeBomberAdjSideslipPlus();

    public abstract void typeBomberAdjSideslipReset();

    public abstract void typeBomberAdjDistanceMinus();

    public abstract void typeBomberAdjDistancePlus();

    public abstract void typeBomberAdjDistanceReset();

    public abstract boolean typeBomberToggleAutomation();

    private float oldctl[] = {
        -1F, -1F
    };
    private float curctl[] = {
        -1F, -1F
    };
    private static final float gear6[] = {
        0.0F, -3F, -3.5F, -1F, 7F
    };
    private static final float gear7[] = {
        0.0F, -0.09835F, -0.21265F, -0.3185F, -0.3917F
    };

    private BombStarthilfe109500      booster[];
    private Eff3DActor                    boosterEffects[];
    private int           boostState;
    protected long        boosterFireOutTime;

    static 
    {
        Class class1 = AR_234C.class;
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
    }
}
