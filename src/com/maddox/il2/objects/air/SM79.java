package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Engine;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Mat;
import com.maddox.il2.fm.Controls;
import com.maddox.il2.fm.Pitot;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.rts.Property;

public abstract class SM79 extends Scheme6 {

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.hierMesh().chunkSetAngles("GearR7_D0", 0.0F, 1.0F, 0.0F);
        this.hierMesh().chunkSetAngles("GearL7_D0", 0.0F, 1.0F, 0.0F);
        this.hierMesh().chunkSetAngles("GearR6_D0", 0.0F, -1F, 0.0F);
        this.hierMesh().chunkSetAngles("GearL6_D0", 0.0F, -1F, 0.0F);
        if (this.thisWeaponsName.startsWith("12") || this.thisWeaponsName.startsWith("6")) for (int i = 1; i <= 12; i++) {
            this.numBombsOld = 12;
            if (this.thisWeaponsName.startsWith("6")) this.numBombsOld = 6;
        }
        if (this.thisWeaponsName.startsWith("5")) for (int j = 1; j <= 5; j++) {
            this.numBombsOld = 5;
        }
        if (this.thisWeaponsName.startsWith("2")) {
            this.numBombsOld = 2;
        }
        if (this.thisWeaponsName.startsWith("1x")) {
            this.numBombsOld = 0;
        }
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        for (int i = 1; i <= 12; i++) hierMesh.chunkVisible("BombRack" + i + "_D0", thisWeaponsName.startsWith("12") || thisWeaponsName.startsWith("6"));
        for (int j = 1; j <= 5; j++) hierMesh.chunkVisible("BombRack250_" + j + "_D0", thisWeaponsName.startsWith("5"));
        hierMesh.chunkVisible("BombRack500_1_D0", thisWeaponsName.startsWith("2"));
        hierMesh.chunkVisible("BombRack500_2_D0", thisWeaponsName.startsWith("2"));
        hierMesh.chunkVisible("Torpedo_Support_D0", thisWeaponsName.startsWith("1x"));
    }
    
    public SM79() {
        this.fSightCurAltitude = 300F;
        this.fSightCurSpeed = 50F;
        this.fSightCurForwardAngle = 0.0F;
        this.fSightSetForwardAngle = 0.0F;
        this.fSightCurSideslip = 0.0F;
        this.bPitUnfocused = true;
        this.bPilot1Killed = false;
        this.bPilot2Killed = false;
        this.bPilot3Killed = false;
        this.bPilot4Killed = false;
        this.bPilot5Killed = false;
        this.bayDoorAngle = 0.0F;
        this.wasInTorpedoAttack = false;
        this.numEvasive = 0;
        this.timeEvasive = 0;
        this.timeTorpedoDrop = 0;
        this.bDynamoOperational = true;
        this.dynamoOrient = 0.0F;
        this.bDynamoRotary = false;
    }

    public void rareAction(float f, boolean flag) {
        if (this.hierMesh().isChunkVisible("Prop2_D1") && this.hierMesh().isChunkVisible("Prop3_D1") && (this.hierMesh().isChunkVisible("Prop1_D0") || this.hierMesh().isChunkVisible("PropRot1_D0"))) {
            this.mydebuggunnery("!!!!!!!!!!!!!!!!!!! HIT PROP 1 !!!!!!!!!!!!!!!!!!!!!");
            this.hitProp(0, 0, Engine.actorLand());
        }
        if (this.hierMesh().isChunkVisible("Prop2_D1") && this.hierMesh().isChunkVisible("Prop1_D1") && (this.hierMesh().isChunkVisible("Prop3_D0") || this.hierMesh().isChunkVisible("PropRot3_D0"))) {
            this.mydebuggunnery("!!!!!!!!!!!!!!!!!!! HIT PROP 3 !!!!!!!!!!!!!!!!!!!!!");
            this.hitProp(2, 0, Engine.actorLand());
        }
        super.rareAction(f, flag);
        if (flag) {
            if (this.FM.AS.astateEngineStates[0] > 3 && World.Rnd().nextFloat() < 0.39F) this.FM.AS.hitTank(this, 0, 1);
            if (this.FM.AS.astateEngineStates[1] > 3 && World.Rnd().nextFloat() < 0.39F) this.FM.AS.hitTank(this, 1, 1);
            if (this.FM.AS.astateEngineStates[2] > 3 && World.Rnd().nextFloat() < 0.39F) this.FM.AS.hitTank(this, 2, 1);
            if (this.FM.AS.astateTankStates[0] > 4 && World.Rnd().nextFloat() < 0.1F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[0] + "0", 0, this);
            if (this.FM.AS.astateTankStates[1] > 4 && World.Rnd().nextFloat() < 0.1F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[1] + "0", 0, this);
            if (this.FM.AS.astateTankStates[2] > 4 && World.Rnd().nextFloat() < 0.1F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[2] + "0", 0, this);
            if (this.FM.AS.astateTankStates[3] > 4 && World.Rnd().nextFloat() < 0.1F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[3] + "0", 0, this);
        }
        Actor actor = War.GetNearestEnemy(this, 16, 6000F);
        Aircraft aircraft = War.getNearestEnemy(this, 5000F);
        if ((actor != null && !(actor instanceof BridgeSegment) || aircraft != null) && this.FM.CT.getCockpitDoor() < 0.01F) this.FM.AS.setCockpitDoor(this, 1);
        this.drawBombs();
        for (int i = 1; i <= 5; i++)
            if (this.FM.getAltitude() < 3000F) this.hierMesh().chunkVisible("hmask" + i + "_d0", false);
            else this.hierMesh().chunkVisible("hmask" + i + "_d0", this.hierMesh().isChunkVisible("Pilot" + i + "_D0"));

    }

    protected void drawBombs() {
        if (!this.bPitUnfocused) return;
        if (this.thisWeaponsName.endsWith("drop")) {
            int i = 0;
            if (this.FM.CT.Weapons[3] != null) {
                for (int j = 0; j < this.FM.CT.Weapons[3].length; j++)
                    if (this.FM.CT.Weapons[3][j] != null) i += this.FM.CT.Weapons[3][j].countBullets();

                if (this.thisWeaponsName.startsWith("12x1")) {
                    for (int k = 2; k < i + 1; k++)
                        this.hierMesh().chunkVisible("Bomb100Kg" + k + "_D0", true);

                    for (int l = i + 1; l <= 12; l++)
                        this.hierMesh().chunkVisible("Bomb100Kg" + l + "_D0", false);

                }
                if (this.thisWeaponsName.startsWith("12x5")) {
                    for (int i1 = 2; i1 < i + 1; i1++)
                        this.hierMesh().chunkVisible("Bomb50Kg" + i1 + "_D0", true);

                    for (int j1 = i + 1; j1 <= 12; j1++)
                        this.hierMesh().chunkVisible("Bomb50Kg" + j1 + "_D0", false);

                }
                if (this.thisWeaponsName.startsWith("6")) {
                    for (int k1 = 2; k1 < i + 1; k1++)
                        this.hierMesh().chunkVisible("Bomb100Kg" + k1 + "_D0", true);

                    for (int l1 = i + 1; l1 <= 6; l1++)
                        this.hierMesh().chunkVisible("Bomb100Kg" + l1 + "_D0", false);

                }
                if (this.thisWeaponsName.startsWith("5")) {
                    for (int i2 = 2; i2 < i + 1; i2++)
                        this.hierMesh().chunkVisible("Bomb250Kg" + i2 + "_D0", true);

                    for (int j2 = i + 1; j2 <= 5; j2++)
                        this.hierMesh().chunkVisible("Bomb250Kg" + j2 + "_D0", false);

                }
                if (this.thisWeaponsName.startsWith("2")) if (i == 2) this.hierMesh().chunkVisible("Bomb500Kg2_D0", true);
                else this.hierMesh().chunkVisible("Bomb500Kg2_D0", false);
            }
        }
    }

    protected void moveFan(float f) {
        if (this.bDynamoOperational) {
            this.pk = Math.abs((int) (this.FM.Vwld.length() / 14D));
            if (this.pk >= 1) this.pk = 1;
        }
        if (this.bDynamoRotary != (this.pk == 1)) {
            this.bDynamoRotary = this.pk == 1;
            this.hierMesh().chunkVisible("Dina_D0", !this.bDynamoRotary);
            this.hierMesh().chunkVisible("PropDina_D0", this.bDynamoRotary);
        }
        this.dynamoOrient = this.bDynamoRotary ? (this.dynamoOrient - 17.987F) % 360F : (float) (this.dynamoOrient - this.FM.Vwld.length() * 1.5444015264511108D) % 360F;
        this.hierMesh().chunkSetAngles("Dina_D0", 0.0F, this.dynamoOrient, 0.0F);
        super.moveFan(f);
    }

    public void doWoundPilot(int i, float f) {
        switch (i) {
            case 2:
                this.FM.turret[0].setHealth(f);
                break;

            case 3:
                this.FM.turret[1].setHealth(f);
                break;

            case 4:
                this.FM.turret[2].setHealth(f);
                this.FM.turret[3].setHealth(f);
                break;
        }
    }

    public void doMurderPilot(int i) {
        switch (i) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("hmask1_d0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                this.bPilot1Killed = true;
                break;

            case 1:
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("hmask2_d0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                this.bPilot2Killed = true;
                break;

            case 2:
                this.hierMesh().chunkVisible("Pilot3_D0", false);
                this.hierMesh().chunkVisible("hmask3_d0", false);
                this.hierMesh().chunkVisible("Pilot3_D1", true);
                this.bPilot3Killed = true;
                break;

            case 3:
                this.hierMesh().chunkVisible("Pilot4_D0", false);
                this.hierMesh().chunkVisible("hmask4_d0", false);
                this.hierMesh().chunkVisible("Pilot4_D1", true);
                this.bPilot4Killed = true;
                break;

            case 4:
                this.hierMesh().chunkVisible("Pilot5_D0", false);
                this.hierMesh().chunkVisible("hmask5_d0", false);
                this.hierMesh().chunkVisible("Pilot5_D1", true);
                this.bPilot5Killed = true;
                break;
        }
    }

    public void update(float f) {
        for (int i = 0; i < 3; i++)
            if (this.FM.EI.engines[i].getControlProp() < 0.5F) this.FM.EI.engines[i].setControlProp(0.0F);
            else this.FM.EI.engines[i].setControlProp(1.0F);

        if (this.bPitUnfocused && Config.isUSE_RENDER()) {
            Mat mat = this.hierMesh().material(this.hierMesh().materialFind("InteriorFake1"));
            if (mat != null) this.hierMesh().materialReplace("InteriorFake", mat);
        }
        float f1 = 0.0F;
        f1 = Pitot.Indicator((float) this.FM.Loc.z, this.FM.getSpeedKMH());
        f1 = f1 * 0.05F + this.oldSpeed * 0.95F;
        Controls controls = this.FM.CT;
        float f2 = (210F - f1) / 70F;
        if (f2 > 1.0F) f2 = 1.0F;
        else if (f2 < 0.0F) f2 = 0.0F;
        f2 *= f2;
        this.oldSpeed = f1;
        float f4 = -35F * f2;
        this.hierMesh().chunkSetAngles("Flap_RearL_D0", 0.0F, f4, 0.0F);
        this.hierMesh().chunkSetAngles("Flap_RearR_D0", 0.0F, f4, 0.0F);
        f4 = -3F * f2;
        this.hierMesh().chunkSetAngles("Flap_FrontL_D0", 0.0F, f4, 0.0F);
        this.hierMesh().chunkSetAngles("Flap_FrontR_D0", 0.0F, f4, 0.0F);
        controls.forceFlaps(f2);
        controls.bHasFlapsControl = false;
        float f3 = controls.getAileron();
        float f5 = -(f3 * 30F + f2 * 17F);
        this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, f5, 0.0F);
        f5 = -(f3 * 30F - f2 * 17F);
        this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, f5, 0.0F);
        super.update(f);
    }

    protected void moveAileron(float f) {
        Controls controls = this.FM.CT;
        float f1 = controls.getFlap();
        float f2 = -(f * 30F + f1 * 17F);
        this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, f2, 0.0F);
        f2 = -(f * 30F - f1 * 17F);
        this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, f2, 0.0F);
    }

    public void moveCockpitDoor(float f) {
        boolean flag = this.isChunkAnyDamageVisible("Tail1_D");
        if (f < 0.99F) {
            if (!this.hierMesh().isChunkVisible("Tur2_DoorR_D0")) {
                this.hierMesh().chunkVisible("Tur2_DoorR_D0", true);
                this.hierMesh().chunkVisible("Tur2_DoorR_open_D0", false);
                this.hierMesh().chunkVisible("Tur2_DoorL_D0", true);
                this.hierMesh().chunkVisible("Tur2_DoorL_open_D0", false);
                this.hierMesh().chunkVisible("Tur1_DoorR_D0", true);
                this.hierMesh().chunkVisible("Tur1_DoorR_open_D0", false);
                this.hierMesh().chunkVisible("Tur1_DoorL_D0", true);
                this.hierMesh().chunkVisible("Tur1_DoorL_open_D0", false);
            }
            float f1 = 13.8F * f;
            this.hierMesh().chunkSetAngles("Tur2_Door1_D0", 0.0F, -f1, 0.0F);
            f1 = 8.8F * f;
            this.hierMesh().chunkSetAngles("Tur2_Door2_D0", 0.0F, -f1, 0.0F);
            f1 = 3.1F * f;
            this.hierMesh().chunkSetAngles("Tur2_Door3_D0", 0.0F, -f1, 0.0F);
            f1 = 14F * f;
            this.hierMesh().chunkSetAngles("Tur2_DoorL_D0", 0.0F, -f1, 0.0F);
            this.hierMesh().chunkSetAngles("Tur2_DoorR_D0", 0.0F, f1, 0.0F);
            this.hierMesh().chunkSetAngles("Tur1_DoorL_D0", 0.0F, f1, 0.0F);
            this.hierMesh().chunkSetAngles("Tur1_DoorR_D0", 0.0F, -f1, 0.0F);
        } else if (this.hierMesh().isChunkVisible("Tur2_DoorR_D0")) {
            this.hierMesh().chunkVisible("Tur2_DoorR_D0", false);
            this.hierMesh().chunkVisible("Tur2_DoorR_open_D0", true);
            this.hierMesh().chunkVisible("Tur2_DoorL_D0", false);
            this.hierMesh().chunkVisible("Tur2_DoorL_open_D0", true);
            this.hierMesh().chunkVisible("Tur1_DoorR_D0", false);
            this.hierMesh().chunkVisible("Tur1_DoorR_open_D0", flag);
            this.hierMesh().chunkVisible("Tur1_DoorL_D0", false);
            this.hierMesh().chunkVisible("Tur1_DoorL_open_D0", flag);
        }
        if (Config.isUSE_RENDER()) {
            if (Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null) Main3D.cur3D().cockpits[0].onDoorMoved(f);
            this.setDoorSnd(f);
        }
    }

    public void hitDaSilk() {
        if (this.FM.CT.getCockpitDoor() < 0.01F) this.FM.AS.setCockpitDoor(this, 1);
        super.hitDaSilk();
    }

    protected void mydebuggunnery(String s) {
    }

    protected void setControlDamage(Shot shot, int i) {
        if (World.Rnd().nextFloat() < 0.002F && this.getEnergyPastArmor(4F, shot) > 0.0F) {
            this.FM.AS.setControlsDamage(shot.initiator, i);
            this.mydebuggunnery(i + " Controls Out... //0 = AILERON, 1 = ELEVATOR, 2 = RUDDER");
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        this.mydebuggunnery("HitBone called! " + s);
        this.mydebuggunnery("IN: " + shot.power);
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) if (s.endsWith("p1")) {
                if (Aircraft.v1.z > 0.5D) this.getEnergyPastArmor(4D / Aircraft.v1.z, shot);
                else if (Aircraft.v1.x > 0.93969261646270752D) this.getEnergyPastArmor(8D / Aircraft.v1.x * World.Rnd().nextFloat(1.0F, 1.2F), shot);
            } else if (s.endsWith("p2")) this.getEnergyPastArmor(4D / Math.abs(Aircraft.v1.z), shot);
            else if (s.endsWith("p3")) this.getEnergyPastArmor(7D / Math.abs(Aircraft.v1.x) * World.Rnd().nextFloat(1.0F, 1.2F), shot);
            else if (s.endsWith("p4")) {
                if (Aircraft.v1.x > 0.70710676908493042D) this.getEnergyPastArmor(7D / Aircraft.v1.x * World.Rnd().nextFloat(1.0F, 1.2F), shot);
                else if (Aircraft.v1.x > -0.70710676908493042D) this.getEnergyPastArmor(5F, shot);
            } else if (s.endsWith("a1") || s.endsWith("a3") || s.endsWith("a4")) if (Aircraft.v1.x > 0.70710676908493042D) this.getEnergyPastArmor(0.8D / Aircraft.v1.x * World.Rnd().nextFloat(1.0F, 1.2F), shot);
            else this.getEnergyPastArmor(0.6F, shot);
            if (s.startsWith("xxspar")) {
                this.getEnergyPastArmor(4F, shot);
                if ((s.endsWith("cf1") || s.endsWith("cf2")) && World.Rnd().nextFloat() < 0.1F && this.chunkDamageVisible("CF") > 2
                        && this.getEnergyPastArmor(15.9F / (float) Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F) {
                    this.mydebuggunnery("*** CF Spars Broken in Half..");
                    this.msgCollision(this, "Tail1_D0", "Tail1_D0");
                    this.msgCollision(this, "WingLIn_D0", "WingLIn_D0");
                    this.msgCollision(this, "WingRIn_D0", "WingRIn_D0");
                }
                if ((s.endsWith("t1") || s.endsWith("t2")) && World.Rnd().nextFloat() < 0.1F && this.chunkDamageVisible("Tail1") > 2
                        && this.getEnergyPastArmor(15.9F / (float) Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F) {
                    this.mydebuggunnery("*** Tail1 Spars Broken in Half..");
                    this.msgCollision(this, "Tail1_D0", "Tail1_D0");
                }
                if ((s.endsWith("li1") || s.endsWith("li2")) && World.Rnd().nextFloat() < 1.0D - 0.92D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingLIn") > 2
                        && this.getEnergyPastArmor(13.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.mydebuggunnery("*** WingLIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLIn_D2", shot.initiator);
                }
                if ((s.endsWith("ri1") || s.endsWith("ri2")) && World.Rnd().nextFloat() < 1.0D - 0.92D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingRIn") > 2
                        && this.getEnergyPastArmor(10.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.mydebuggunnery("*** WingRIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRIn_D2", shot.initiator);
                }
                if ((s.endsWith("lm1") || s.endsWith("lm2")) && World.Rnd().nextFloat() < 1.0D - 0.86D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingLMid") > 2
                        && this.getEnergyPastArmor(10.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.mydebuggunnery("*** WingLMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLMid_D2", shot.initiator);
                }
                if ((s.endsWith("rm1") || s.endsWith("rm2")) && World.Rnd().nextFloat() < 1.0D - 0.86D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingRMid") > 2
                        && this.getEnergyPastArmor(13.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.mydebuggunnery("*** WingRMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRMid_D2", shot.initiator);
                }
                if ((s.endsWith("lo1") || s.endsWith("lo2")) && World.Rnd().nextFloat() < 1.0D - 0.79D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingLOut") > 2
                        && this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.mydebuggunnery("*** WingLOut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLOut_D2", shot.initiator);
                }
                if ((s.endsWith("ro1") || s.endsWith("ro2")) && World.Rnd().nextFloat() < 1.0D - 0.79D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingROut") > 2
                        && this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.mydebuggunnery("*** WingROut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingROut_D2", shot.initiator);
                }
                if (s.endsWith("e1") && (point3d.y > 2.79D || point3d.y < 2.32D) && this.getEnergyPastArmor(17F, shot) > 0.0F) {
                    this.mydebuggunnery("*** Engine1 Suspension Broken in Half..");
                    this.nextDMGLevels(3, 2, "Engine1_D0", shot.initiator);
                }
                if (s.endsWith("e2") && (point3d.y < -2.79D || point3d.y > -2.32D) && this.getEnergyPastArmor(17F, shot) > 0.0F) {
                    this.mydebuggunnery("*** Engine2 Suspension Broken in Half..");
                    this.nextDMGLevels(3, 2, "Engine2_D0", shot.initiator);
                }
                if (s.endsWith("e3") && (point3d.y < -2.79D || point3d.y > -2.32D) && this.getEnergyPastArmor(17F, shot) > 0.0F) {
                    this.mydebuggunnery("*** Engine3 Suspension Broken in Half..");
                    this.nextDMGLevels(3, 2, "Engine3_D0", shot.initiator);
                }
                if ((s.endsWith("k1") || s.endsWith("k2")) && World.Rnd().nextFloat() < 1.0D - 0.79D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingROut") > 2 && this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.mydebuggunnery("*** Keel spars damaged..");
                    this.nextDMGLevels(1, 2, "Keel1_D0", shot.initiator);
                }
                if ((s.endsWith("sr1") || s.endsWith("sr2")) && World.Rnd().nextFloat() < 1.0D - 0.79D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingROut") > 2
                        && this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.mydebuggunnery("*** Right Stab spars damaged..");
                    this.nextDMGLevels(1, 2, "StabR_D0", shot.initiator);
                }
                if ((s.endsWith("sl1") || s.endsWith("sl2")) && World.Rnd().nextFloat() < 1.0D - 0.79D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingROut") > 2
                        && this.getEnergyPastArmor(8.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.mydebuggunnery("*** Left Stab spars damaged..");
                    this.nextDMGLevels(1, 2, "StabL_D0", shot.initiator);
                }
            }
            if (s.startsWith("xxbomb") && World.Rnd().nextFloat() < 0.01F && this.FM.CT.Weapons[3] != null && this.FM.CT.Weapons[3][0].haveBullets()) {
                this.mydebuggunnery("*** Bomb Payload Detonates..");
                this.FM.AS.hitTank(shot.initiator, 0, 100);
                this.FM.AS.hitTank(shot.initiator, 1, 100);
                this.FM.AS.hitTank(shot.initiator, 2, 100);
                this.FM.AS.hitTank(shot.initiator, 3, 100);
                this.msgCollision(this, "CF_D0", "CF_D0");
            }
            if (s.startsWith("xxprop")) {
                byte byte0 = 0;
                if (s.endsWith("2")) byte0 = 1;
                if (s.endsWith("3")) byte0 = 2;
                if (this.getEnergyPastArmor(2.0F, shot) > 0.0F && World.Rnd().nextFloat() < 0.35F) {
                    this.FM.AS.setEngineSpecificDamage(shot.initiator, byte0, 3);
                    this.mydebuggunnery("*** Engine" + (byte0 + 1) + " Governor Failed..");
                }
            }
            if (s.startsWith("xxeng")) {
                byte byte1 = 0;
                if (s.startsWith("xxeng2")) byte1 = 1;
                if (s.startsWith("xxeng3")) byte1 = 2;
                if (s.endsWith("case")) {
                    if (this.getEnergyPastArmor(0.11F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < shot.power / 200000F) {
                            this.FM.AS.setEngineStuck(shot.initiator, byte1);
                            this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Crank Case Hit - Engine Stucks..");
                        }
                        if (World.Rnd().nextFloat() < shot.power / 50000F) {
                            this.FM.AS.hitEngine(shot.initiator, byte1, 2);
                            this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Crank Case Hit - Engine Damaged..");
                        }
                    }
                } else if (s.endsWith("cyls")) {
                    this.mydebuggunnery("*** Engine" + (byte1 + 1) + " RATIO " + this.FM.EI.engines[byte1].getCylindersRatio());
                    if (this.getEnergyPastArmor(1.4F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[byte1].getCylindersRatio() * 0.6F) {
                        this.FM.EI.engines[byte1].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 5000F)));
                        this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Cylinders Hit, " + this.FM.EI.engines[byte1].getCylindersOperable() + "/" + this.FM.EI.engines[byte1].getCylinders() + " Left..");
                        if (this.FM.AS.astateEngineStates[byte1] < 1) {
                            this.FM.AS.hitEngine(shot.initiator, byte1, 1);
                            this.FM.AS.doSetEngineState(shot.initiator, byte1, 1);
                        }
                        if (World.Rnd().nextFloat() < shot.power / 960000F) {
                            this.FM.AS.hitEngine(shot.initiator, byte1, 3);
                            this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Cylinders Hit - Engine Fires..");
                        }
                        this.mydebuggunnery("*** Engine" + (byte1 + 1) + " state " + this.FM.AS.astateEngineStates[byte1]);
                        this.getEnergyPastArmor(25F, shot);
                    }
                } else if (s.endsWith("supc") && this.getEnergyPastArmor(0.3F, shot) > 0.0F && World.Rnd().nextFloat() < 0.79F) {
                    this.FM.AS.setEngineSpecificDamage(shot.initiator, byte1, 0);
                    this.mydebuggunnery("*** Engine" + (byte1 + 1) + " Supercharger Out..");
                }
                if (s.endsWith("oil1") || s.endsWith("oil2") || s.endsWith("oil3")) {
                    if (this.getEnergyPastArmor(0.65F, shot) > 0.0F) this.FM.AS.hitOil(shot.initiator, byte1);
                    this.getEnergyPastArmor(0.42F, shot);
                }
                this.mydebuggunnery("*** Engine" + (byte1 + 1) + " state = " + this.FM.AS.astateEngineStates[byte1]);
            }
            if (s.startsWith("xxtank")) {
                int i = s.charAt(6) - 49;
                if (this.getEnergyPastArmor(1.3F, shot) > 0.0F) if (shot.power < 14100F) {
                    if (this.FM.AS.astateTankStates[i] < 1) {
                        this.mydebuggunnery("deterministic damage !!! ");
                        this.FM.AS.hitTank(shot.initiator, i, 1);
                    }
                    if (this.FM.AS.astateTankStates[i] < 4 && World.Rnd().nextFloat() < 0.1F) {
                        this.mydebuggunnery("random damage !!! ");
                        this.FM.AS.hitTank(shot.initiator, i, 1);
                    }
                    if (shot.powerType == 3 && this.FM.AS.astateTankStates[i] > 0 && World.Rnd().nextFloat() < 0.07F) {
                        this.mydebuggunnery("API round !!! ");
                        this.FM.AS.hitTank(shot.initiator, i, 10);
                    }
                } else {
                    this.mydebuggunnery("big shot !!! ");
                    this.FM.AS.hitTank(shot.initiator, i, World.Rnd().nextInt(0, (int) (shot.power / 40000F)));
                }
                this.mydebuggunnery("*** Tank " + (i + 1) + " state = " + this.FM.AS.astateTankStates[i]);
            }
            if (s.startsWith("xxlock")) {
                this.mydebuggunnery("Lock Construction: Hit..");
                if (s.startsWith("xxlockr") && (s.startsWith("xxlockr1") || s.startsWith("xxlockr2")) && this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    this.mydebuggunnery("Lock Construction: Rudder1 Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "Rudder1_D" + this.chunkDamageVisible("Rudder1"), shot.initiator);
                }
                if (s.startsWith("xxlockvl") && this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    this.mydebuggunnery("Lock Construction: VatorL Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "VatorL_D" + this.chunkDamageVisible("VatorL"), shot.initiator);
                }
                if (s.startsWith("xxlockvr") && this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    this.mydebuggunnery("Lock Construction: VatorR Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "VatorR_D" + this.chunkDamageVisible("VatorR"), shot.initiator);
                }
                if (s.startsWith("xxlockal") && this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    this.mydebuggunnery("Lock Construction: AroneL Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "AroneL_D" + this.chunkDamageVisible("AroneL"), shot.initiator);
                }
                if (s.startsWith("xxlockar") && this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F) {
                    this.debuggunnery("Lock Construction: AroneR Lock Shot Off..");
                    this.nextDMGLevels(3, 2, "AroneR_D" + this.chunkDamageVisible("AroneR"), shot.initiator);
                }
            }
        }
        if (s.startsWith("x12")) if (s.startsWith("x12,7_01")) {
            if (this.getEnergyPastArmor(5F, shot) > 0.0F && World.Rnd().nextFloat() < 0.75F) {
                this.FM.AS.setJamBullets(0, 0);
                this.getEnergyPastArmor(11.98F, shot);
            }
        } else if (s.startsWith("x12,7_00")) {
            if (this.getEnergyPastArmor(4.85F, shot) > 0.0F && World.Rnd().nextFloat() < 0.75F) {
                this.FM.AS.setJamBullets(0, 2);
                this.getEnergyPastArmor(11.98F, shot);
            }
        } else if (s.startsWith("x12,7_02")) {
            if (this.getEnergyPastArmor(4.85F, shot) > 0.0F && World.Rnd().nextFloat() < 0.75F) {
                this.FM.AS.setJamBullets(0, 1);
                this.getEnergyPastArmor(11.98F, shot);
            }
        } else if (s.startsWith("x12,7_03")) {
            if (this.getEnergyPastArmor(4.85F, shot) > 0.0F && World.Rnd().nextFloat() < 0.75F) {
                this.FM.AS.setJamBullets(0, 4);
                this.getEnergyPastArmor(11.98F, shot);
            }
        } else if (s.startsWith("x12,7_04") && this.getEnergyPastArmor(4.85F, shot) > 0.0F && World.Rnd().nextFloat() < 0.75F) {
            this.FM.AS.setJamBullets(0, 3);
            this.getEnergyPastArmor(11.98F, shot);
        }
        if (s.startsWith("xcf")) {
            this.setControlDamage(shot, 0);
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("CF") < 3) this.hitChunk("CF", shot);
            if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
            if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 2);
            if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
            if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
            if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
            if (World.Rnd().nextFloat() > 0.8F) this.getEnergyPastArmor(5F, shot);
        } else if (s.startsWith("xtail")) {
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Tail1") < 3) this.hitChunk("Tail1", shot);
            if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
            if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
            if (World.Rnd().nextFloat() > 0.8F) this.getEnergyPastArmor(3F, shot);
        } else if (s.startsWith("xkeel")) {
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Keel1") < 2) this.hitChunk("Keel1", shot);
        } else if (s.startsWith("xrudder")) {
            this.setControlDamage(shot, 2);
            this.hitChunk("Rudder1", shot);
        } else if (s.startsWith("xstabl")) this.hitChunk("StabL", shot);
        else if (s.startsWith("xstabr")) this.hitChunk("StabR", shot);
        else if (s.startsWith("xvatorl")) this.hitChunk("VatorL", shot);
        else if (s.startsWith("xvatorr")) this.hitChunk("VatorR", shot);
        else if (s.startsWith("xwinglin")) {
            this.setControlDamage(shot, 0);
            if (this.chunkDamageVisible("WingLIn") < 3) this.hitChunk("WingLIn", shot);
            if (World.Rnd().nextFloat() > 0.7F) this.getEnergyPastArmor(5F, shot);
        } else if (s.startsWith("xwingrin")) {
            this.setControlDamage(shot, 0);
            if (this.chunkDamageVisible("WingRIn") < 3) this.hitChunk("WingRIn", shot);
            if (World.Rnd().nextFloat() > 0.7F) this.getEnergyPastArmor(5F, shot);
        } else if (s.startsWith("xwinglmid")) {
            this.setControlDamage(shot, 0);
            if (this.chunkDamageVisible("WingLMid") < 3) this.hitChunk("WingLMid", shot);
        } else if (s.startsWith("xwingrmid")) {
            this.setControlDamage(shot, 0);
            if (this.chunkDamageVisible("WingRMid") < 3) this.hitChunk("WingRMid", shot);
        } else if (s.startsWith("xwinglout")) {
            if (this.chunkDamageVisible("WingLOut") < 3) this.hitChunk("WingLOut", shot);
        } else if (s.startsWith("xwingrout")) {
            if (this.chunkDamageVisible("WingROut") < 3) this.hitChunk("WingROut", shot);
        } else if (s.startsWith("xaronel")) this.hitChunk("AroneL", shot);
        else if (s.startsWith("xaroner")) this.hitChunk("AroneR", shot);
        else if (s.startsWith("xengine1")) {
            if (this.chunkDamageVisible("Engine1") < 2) this.hitChunk("Engine1", shot);
        } else if (s.startsWith("xengine2")) {
            if (this.chunkDamageVisible("Engine2") < 2) this.hitChunk("Engine2", shot);
        } else if (s.startsWith("xengine3")) {
            if (this.chunkDamageVisible("Engine3") < 2) this.hitChunk("Engine3", shot);
        } else if (s.startsWith("xgear")) {
            if (World.Rnd().nextFloat() < 0.1F) {
                this.mydebuggunnery("*** Gear Hydro Failed..");
                this.FM.Gears.setHydroOperable(false);
            }
        } else if (s.startsWith("xturret")) {
            if (s.startsWith("xturret1")) this.FM.AS.setJamBullets(10, 0);
            if (s.startsWith("xturret2")) this.FM.AS.setJamBullets(11, 0);
            if (s.startsWith("xturret3")) this.FM.AS.setJamBullets(12, 0);
            if (s.startsWith("xturret4")) this.FM.AS.setJamBullets(13, 0);
            if (s.startsWith("xturret5")) this.FM.AS.setJamBullets(14, 0);
        } else if (s.startsWith("xpilot") || s.startsWith("xhead") || s.startsWith("xpilox") || s.startsWith("xheax")) {
            byte byte2 = 0;
            int j;
            if (s.endsWith("a")) {
                byte2 = 1;
                j = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte2 = 2;
                j = s.charAt(6) - 49;
            } else j = s.charAt(5) - 49;
            this.mydebuggunnery("call HitFlesh:  " + j + " " + byte2 + " " + shot.power);
            this.hitFlesh(j, shot, byte2);
        }
        this.mydebuggunnery("out:  " + shot.power);
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        if (i == 19) this.FM.Gears.hitCentreGear();
        return super.cutFM(i, j, actor);
    }

    public static void moveGear(HierMesh hiermesh, float f) {
        float f1 = Math.max(-f * 800F, -70F);
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, -81F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, -81F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL4_D0", 0.0F, 45F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR4_D0", 0.0F, 45F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL5_D0", 0.0F, 53F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR5_D0", 0.0F, 53F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR7_D0", 0.0F, 1.2F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearL7_D0", 0.0F, 1.2F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearR6_D0", 0.0F, -1.2F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearL6_D0", 0.0F, -1.2F * f1, 0.0F);
    }

    protected void moveGear(float f) {
        moveGear(this.hierMesh(), f);
    }

    protected void moveBayDoor(float f) {
        float f1 = f * 4F;
        this.hierMesh().chunkSetAngles("DoorL_D0", 0.0F, 23F * f1, 0.0F);
        this.hierMesh().chunkSetAngles("DoorR_D0", 0.0F, -23F * f1, 0.0F);
        f1 = -(f * 3.5F);
        this.hierMesh().chunkSetAngles("Gambali_D0", 0.0F, f1, 0.0F);
        this.bayDoorAngle = f;
    }

    public float    fSightCurAltitude;
    public float    fSightCurSpeed;
    public float    fSightCurForwardAngle;
    public float    fSightSetForwardAngle;
    public float    fSightCurSideslip;
    public boolean  bPitUnfocused;
    public boolean  bPilot1Killed;
    public boolean  bPilot2Killed;
    public boolean  bPilot3Killed;
    public boolean  bPilot4Killed;
    public boolean  bPilot5Killed;
    public int      numBombsOld;
    public float    bayDoorAngle;
    boolean         wasInTorpedoAttack;
    int             numEvasive;
    int             timeEvasive;
    int             timeTorpedoDrop;
    private boolean bDynamoOperational;
    private float   dynamoOrient;
    private float   oldSpeed;
    private boolean bDynamoRotary;
    private int     pk;

    static {
        Class class1 = SM79.class;
        Property.set(class1, "originCountry", PaintScheme.countryItaly);
    }
}
