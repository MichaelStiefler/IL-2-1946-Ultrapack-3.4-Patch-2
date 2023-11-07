package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.fm.AircraftState;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.weapons.BombIJN_RATO;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class Kikka123 extends Scheme2
    implements TypeStormovik, TypeBNZFighter, TypeRocketBoost
{
    public Kikka123()
    {
        this.booster = new BombIJN_RATO[2];
        this.boosterEffects = new Eff3DActor[2];
        this.boostState = AircraftState._AS_BOOST_NOBOOST;
        this.boosterFireOutTime = -1L;
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        Kikka123.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
        this.hierMesh().chunkVisible("BoosterH1", false);
        this.hierMesh().chunkVisible("BoosterH2", false);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        _WeaponSlot[] weaponSlotsRegistered = Aircraft.getWeaponSlotsRegistered(aircraftClass, thisWeaponsName);
        hierMesh.chunkVisible("Pylon_Single_D0", weaponSlotsRegistered[6] != null || weaponSlotsRegistered[7] != null);
        hierMesh.chunkVisible("Pylon_Twin_D0", weaponSlotsRegistered[4] != null);
        hierMesh.chunkVisible("barrelL_D0", weaponSlotsRegistered[0] != null);
        hierMesh.chunkVisible("barrelR_D0", weaponSlotsRegistered[1] != null);
        hierMesh.chunkVisible("BoosterH1", thisWeaponsName.endsWith("_boost"));
        hierMesh.chunkVisible("BoosterH2", thisWeaponsName.endsWith("_boost"));
//        hierMesh.chunkVisible("Pylon_D0", thisWeaponsName.startsWith("2x"));
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
                this.booster[i] = new BombIJN_RATO();
                this.booster[i].pos.setBase(this, this.findHook("_BoosterH" + (i + 1)), false);
                this.booster[i].pos.resetAsBase();
                this.booster[i].drawing(true);
            } catch (Exception exception) {
                this.debugprintln("Structure corrupt - can't hang RATO...");
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
        if (this.thisWeaponsName.endsWith("_boost")) this.FM.AS.setBoostState(this, this.boostState | AircraftState._AS_BOOST_EXISTS);
    }

    public void destroy() {
        this.doCutBoosters();
        super.destroy();
    }

    public void moveCockpitDoor(float f)
    {
        if (this.FM.crew == 1) {
            resetYPRmodifier();
            Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.6F);
            hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        } else {
            hierMesh().chunkSetAngles("Blister1_D0", 0F, 0F, -90F * f);
            hierMesh().chunkSetAngles("Blister2_D0", 0F, 0F, -90F * f);
        }
        if(Config.isUSE_RENDER())
        {
            if(Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null)
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            setDoorSnd(f);
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
            if(!this.FM.AS.bIsAboutToBailout)
            {
                if(hierMesh().isChunkVisible("Blister1_D0"))
                    hierMesh().chunkVisible("Gore1_D0", true);
                hierMesh().chunkVisible("Gore2_D0", true);
            }
            break;

        case 1:
            if (this.FM.crew < 2) break;
            hierMesh().chunkVisible("Pilot2_D0", false);
            hierMesh().chunkVisible("Head2_D0", false);
            hierMesh().chunkVisible("HMask2_D0", false);
            hierMesh().chunkVisible("Pilot2_D1", true);
            if(!this.FM.AS.bIsAboutToBailout)
            {
                if(hierMesh().isChunkVisible("Blister2_D0"))
                    hierMesh().chunkVisible("Gore1_D0", true);
                hierMesh().chunkVisible("Gore2_D0", true);
            }
            break;

        default:
            return;
        }
    }

    protected void moveAileron(float f)
    {
        hierMesh().chunkSetAngles("AroneL_D0", 0.0F, 30F * f, 0.0F);
        hierMesh().chunkSetAngles("AroneR_D0", 0.0F, -30F * f, 0.0F);
    }

    protected void moveFlap(float f)
    {
        hierMesh().chunkSetAngles("Flap01_D0", 0.0F, -45F * f, 0.0F);
        hierMesh().chunkSetAngles("Flap02_D0", 0.0F, -45F * f, 0.0F);
    }

    public static void moveGear(HierMesh hiermesh, float f)
    {
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, 111F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC21_D0", 0.0F, 0.0F, 0.0F);
        hiermesh.chunkSetAngles("GearC5_D0", 0.0F, 90F * f, 0.0F);
        hiermesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(f, 0.01F, 0.11F, 0.0F, 90F), 0.0F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, 87F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, 87F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, 88F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, 88F * f, 0.0F);
        float f1 = Math.max(-f * 1500F, -90F);
        hiermesh.chunkSetAngles("GearL6_D0", 0.0F, f1, 0.0F);
        hiermesh.chunkSetAngles("GearR6_D0", 0.0F, f1, 0.0F);
    }

    protected void moveGear(float f)
    {
        moveGear(hierMesh(), f);
    }

    public void moveWheelSink()
    {
        resetYPRmodifier();
        float f = FM.Gears.gWheelSinking[2];
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.0F, 0.19F, 0.0F, 0.19F);
        hierMesh().chunkSetLocate("GearC22_D0", Aircraft.xyz, Aircraft.ypr);
        f = Aircraft.cvt(f, 0.0F, 19F, 0.0F, 30F);
        hierMesh().chunkSetAngles("GearC7_D0", 0.0F, f, 0.0F);
        hierMesh().chunkSetAngles("GearC8_D0", 0.0F, 2.0F * f, 0.0F);
    }

    protected void moveRudder(float f)
    {
        hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -30F * f, 0.0F);
        if(FM.CT.getGear() > 0.75F)
            hierMesh().chunkSetAngles("GearC21_D0", 0.0F, 40F * f, 0.0F);
    }

    protected void hitBone(String s, Shot shot, Point3d point3d)
    {
        if(s.startsWith("xcf"))
        {
            if(chunkDamageVisible("CF") < 3)
                hitChunk("CF", shot);
            if(point3d.x > 1.7D)
            {
                if(World.Rnd().nextFloat() < 0.07F)
                    FM.AS.setJamBullets(0, 0);
                if(World.Rnd().nextFloat() < 0.07F)
                    FM.AS.setJamBullets(0, 1);
                if(World.Rnd().nextFloat() < 0.12F)
                    FM.AS.setJamBullets(1, 0);
                if(World.Rnd().nextFloat() < 0.12F)
                    FM.AS.setJamBullets(1, 1);
            }
            if(point3d.x > -0.999D && point3d.x < 0.535D && point3d.z > -0.224D)
            {
                if(World.Rnd().nextFloat() < 0.1F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 0x10);
                if(World.Rnd().nextFloat() < 0.1F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 0x20);
                if(World.Rnd().nextFloat() < 0.1F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 4);
                if(World.Rnd().nextFloat() < 0.1F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 8);
                if(World.Rnd().nextFloat() < 0.1F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 0x40);
                if(World.Rnd().nextFloat() < 0.1F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 1);
                if(World.Rnd().nextFloat() < 0.1F)
                    FM.AS.setCockpitState(shot.initiator, FM.AS.astateCockpitState | 2);
            }
            if(point3d.x > 0.8D && point3d.x < 1.58D && World.Rnd().nextFloat() < 0.25F && (shot.powerType == 3 && getEnergyPastArmor(0.4F, shot) > 0.0F || shot.powerType == 0))
                FM.AS.hitTank(shot.initiator, 0, World.Rnd().nextInt(1, (int)(shot.power / 4000F)));
            if(point3d.x > -2.485D && point3d.x < -1.6D && World.Rnd().nextFloat() < 0.25F && (shot.powerType == 3 && getEnergyPastArmor(0.4F, shot) > 0.0F || shot.powerType == 0))
                FM.AS.hitTank(shot.initiator, 1, World.Rnd().nextInt(1, (int)(shot.power / 4000F)));
        } else
        if(s.startsWith("xtail"))
        {
            if(chunkDamageVisible("Tail1") < 3)
                hitChunk("Tail1", shot);
        } else
        if(s.startsWith("xkeel"))
            hitChunk("Keel1", shot);
        else
        if(s.startsWith("xstabl"))
            hitChunk("StabL", shot);
        else
        if(s.startsWith("xstabr"))
            hitChunk("StabR", shot);
        else
        if(s.startsWith("xwing"))
        {
            if(s.endsWith("lin") && chunkDamageVisible("WingLIn") < 3)
                hitChunk("WingLIn", shot);
            if(s.endsWith("rin") && chunkDamageVisible("WingRIn") < 3)
                hitChunk("WingRIn", shot);
            if(s.endsWith("lmid") && chunkDamageVisible("WingLMid") < 3)
                hitChunk("WingLMid", shot);
            if(s.endsWith("rmid") && chunkDamageVisible("WingRMid") < 3)
                hitChunk("WingRMid", shot);
            if(s.endsWith("lout") && chunkDamageVisible("WingLOut") < 3)
                hitChunk("WingLOut", shot);
            if(s.endsWith("rout") && chunkDamageVisible("WingROut") < 3)
                hitChunk("WingROut", shot);
        } else
        if(s.startsWith("xengine"))
        {
            int i = s.charAt(7) - 49;
            if(point3d.x > 0.0D && point3d.x < 0.697D)
                FM.EI.engines[i].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, 6));
            if(World.Rnd().nextFloat(0.009F, 0.1357F) < shot.mass)
                FM.AS.hitEngine(shot.initiator, i, 5);
        } else
        if(s.startsWith("xpilot") || s.startsWith("xhead"))
        {
            byte byte0 = 0;
            int j;
            if(s.endsWith("a"))
            {
                byte0 = 1;
                j = s.charAt(6) - 49;
            } else
            if(s.endsWith("b"))
            {
                byte0 = 2;
                j = s.charAt(6) - 49;
            } else
            {
                j = s.charAt(5) - 49;
            }
            hitFlesh(j, shot, byte0);
        }
    }

    protected boolean cutFM(int i, int j, Actor actor)
    {
        switch (i) {
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
                this.FM.AS.setBoostState(this, AircraftState._AS_BOOST_NOBOOST);
                this.FM.AS.setGliderBoostOff();
        }
        
        switch(i)
        {
        case 33:
            return super.cutFM(34, j, actor);

        case 36:
            return super.cutFM(37, j, actor);

        case 11:
            cutFM(17, j, actor);
            FM.cut(17, j, actor);
            cutFM(18, j, actor);
            FM.cut(18, j, actor);
            return super.cutFM(i, j, actor);
        }
        return super.cutFM(i, j, actor);
    }

    public void rareAction(float f, boolean flag)
    {
        super.rareAction(f, flag);
        if(flag && World.Rnd().nextFloat() < 0.2F)
        {
            if(FM.AS.astateEngineStates[0] > 3 && World.Rnd().nextFloat() < 0.12F)
            {
                FM.AS.explodeEngine(this, 0);
                msgCollision(this, "WingLIn_D0", "WingLIn_D0");
                if(World.Rnd().nextBoolean())
                    FM.AS.hitTank(this, 0, World.Rnd().nextInt(1, 8));
                else
                    FM.AS.hitTank(this, 1, World.Rnd().nextInt(1, 8));
            }
            if(FM.AS.astateEngineStates[1] > 3 && World.Rnd().nextFloat() < 0.12F)
            {
                FM.AS.explodeEngine(this, 1);
                msgCollision(this, "WingRIn_D0", "WingRIn_D0");
                if(World.Rnd().nextBoolean())
                    FM.AS.hitTank(this, 0, World.Rnd().nextInt(1, 8));
                else
                    FM.AS.hitTank(this, 1, World.Rnd().nextInt(1, 8));
            }
        }
        if(FM.getAltitude() < 3000F) {
            hierMesh().chunkVisible("HMask1_D0", false);
            if (this.FM.crew > 1) hierMesh().chunkVisible("HMask2_D0", false);
        } else {
            hierMesh().chunkVisible("HMask1_D0", hierMesh().isChunkVisible("Head1_D0"));
            if (this.FM.crew > 1) hierMesh().chunkVisible("HMask2_D0", hierMesh().isChunkVisible("Head2_D0"));
        }
    }

    public void update(float f)
    {
        if(FM.AS.isMaster() && Config.isUSE_RENDER())
        {
            if(FM.EI.engines[0].getPowerOutput() > 0.8F && FM.EI.engines[0].getStage() == 6)
            {
                if(FM.EI.engines[0].getPowerOutput() > 0.95F)
                    FM.AS.setSootState(this, 0, 3);
                else
                    FM.AS.setSootState(this, 0, 2);
            } else
            {
                FM.AS.setSootState(this, 0, 0);
            }
            if(FM.EI.engines[1].getPowerOutput() > 0.8F && FM.EI.engines[1].getStage() == 6)
            {
                if(FM.EI.engines[1].getPowerOutput() > 0.95F)
                    FM.AS.setSootState(this, 1, 3);
                else
                    FM.AS.setSootState(this, 1, 2);
            } else
            {
                FM.AS.setSootState(this, 1, 0);
            }
        }
        super.update(f);
        this.boostUpdate();
    }

    private BombIJN_RATO      booster[];
    private Eff3DActor                    boosterEffects[];
    private int           boostState;
    protected long        boosterFireOutTime;

    static 
    {
        Class class1 = Kikka123.class;
        Property.set(class1, "originCountry", PaintScheme.countryJapan);
    }
}
