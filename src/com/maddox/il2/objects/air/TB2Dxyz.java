package com.maddox.il2.objects.air;

import java.security.SecureRandom;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.fm.Controls;
import com.maddox.il2.game.Main3D;
import com.maddox.rts.Property;

public abstract class TB2Dxyz extends Scheme1 implements TypeStormovik {

    public TB2Dxyz() {
        this.arrestor = 0.0F;
    }

    public void onAircraftLoaded() {
        // Initialize Prop position with random values.
        // We are not using the ingame random number generator here,
        // because it generates the same values on each mission start.
        long lTime = System.currentTimeMillis();
        SecureRandom secRandom = new SecureRandom();
        secRandom.setSeed(lTime);
        this.propPos[0] = this.propPos[1] = secRandom.nextFloat() * 90F;//World.cur().rnd.nextFloat(0F, 90F);
        this.hierMesh().chunkSetAngles(Aircraft.Props[0][0], 0.0F, this.propPos[0], 0.0F);
        this.hierMesh().chunkSetAngles(Aircraft.Props[1][0], 0.0F, -(this.propPos[1] - 20F), 0.0F);
    }

    public void moveCockpitDoor(float f) {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.0F, 0.99F, 0.0F, -0.65F);
        this.hierMesh().chunkSetLocate("Slide_D0", Aircraft.xyz, Aircraft.ypr);
        if (Config.isUSE_RENDER()) {
            if ((Main3D.cur3D().cockpits != null) && (Main3D.cur3D().cockpits[0] != null)) {
                Main3D.cur3D().cockpits[0].onDoorMoved(f);
            }
            this.setDoorSnd(f);
        }
    }

    public void doMurderPilot(int i) {
        switch (i) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                break;
        }
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (this.FM.getAltitude() < 3000F) {
            this.hierMesh().chunkVisible("HMask1_D0", false);
        } else {
            this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
        }
    }

    public void moveArrestorHook(float f) {
        this.hierMesh().chunkSetAngles("Hook1_D0", 0.0F, -73.5F * this.arrestor, 0.0F);
    }

    // New 4.12 Style Gear Animation with separate values for all three legs
    public static void moveGear(HierMesh hiermesh, float leftGear, float rightGear, float noseGear) {
        hiermesh.chunkSetAngles("GearL2_D0", 0.0F, Aircraft.cvt(leftGear, 0.2F, 0.99F, 0F, -91.5F), 0.0F);
        TB2Dxyz.doResetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(leftGear, 0.2F, 0.25F, 0F, -0.01F);
        Aircraft.xyz[0] = Aircraft.cvt(leftGear, 0.25F, 0.5F, 0F, -0.06F);
        Aircraft.ypr[1] = Aircraft.cvt(leftGear, 0.2F, 0.99F, 0F, -95.5F);
        hiermesh.chunkSetLocate("GearL4_D0", Aircraft.xyz, Aircraft.ypr);
        if (leftGear < 0.5F) {
            hiermesh.chunkSetAngles("GearL3_D0", 0.0F, Aircraft.cvt(leftGear, 0.01F, 0.2F, 0F, -90F), 0.0F);
        } else {
            hiermesh.chunkSetAngles("GearL3_D0", 0.0F, Aircraft.cvt(leftGear, 0.5F, 0.99F, -90F, 0F), 0.0F);
        }

        hiermesh.chunkSetAngles("GearR2_D0", 0.0F, Aircraft.cvt(rightGear, 0.2F, 0.99F, 0F, 91.5F), 0.0F);
        TB2Dxyz.doResetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(rightGear, 0.2F, 0.25F, 0F, -0.01F);
        Aircraft.xyz[0] = Aircraft.cvt(rightGear, 0.25F, 0.5F, 0F, 0.06F);
        Aircraft.ypr[1] = Aircraft.cvt(rightGear, 0.2F, 0.99F, 0F, 95.5F);
        hiermesh.chunkSetLocate("GearR4_D0", Aircraft.xyz, Aircraft.ypr);
        if (rightGear < 0.5F) {
            hiermesh.chunkSetAngles("GearR3_D0", 0.0F, Aircraft.cvt(rightGear, 0.01F, 0.2F, 0F, 90F), 0.0F);
        } else {
            hiermesh.chunkSetAngles("GearR3_D0", 0.0F, Aircraft.cvt(rightGear, 0.5F, 0.99F, 90F, 0F), 0.0F);
        }
        hiermesh.chunkSetAngles("LanLight_D0", 0.0F, 0.0F, Aircraft.cvt(rightGear, 0.01F, 0.99F, 0F, 92.5F));

        hiermesh.chunkSetAngles("GearC6_D0", 0.0F, Aircraft.cvt(noseGear, 0.2F, 0.99F, 0F, 120F), 0.0F);
        TB2Dxyz.doResetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(noseGear, 0.2F, 0.99F, 0F, -0.2F);
        Aircraft.ypr[1] = Aircraft.cvt(noseGear, 0.2F, 0.99F, 0F, 120F);
        hiermesh.chunkSetLocate("GearC2_D0", Aircraft.xyz, Aircraft.ypr);
        if (noseGear < 0.5F) {
            hiermesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(noseGear, 0.01F, 0.2F, 0F, -90F), 0.0F);
            hiermesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(noseGear, 0.01F, 0.2F, 0F, -90F), 0.0F);
        } else {
            hiermesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(noseGear, 0.5F, 0.99F, -90F, 0F), 0.0F);
            hiermesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(noseGear, 0.5F, 0.99F, -90F, 0F), 0.0F);
        }
        if (noseGear < 0.99F) {
            hiermesh.chunkSetAngles("GearC2b2_D0", 0F, 0F, 0F);
        }
        TB2Dxyz.suspension(hiermesh, Aircraft.cvt(leftGear, 0.3F, 0.99F, 0.07F, 0F), Aircraft.cvt(rightGear, 0.3F, 0.99F, 0.07F, 0F), Aircraft.cvt(noseGear, 0.3F, 0.99F, 0.3F, 0F));
    }

    protected void moveGear(float leftGearPos, float rightGearPos, float noseGearPos) {
        TB2Dxyz.moveGear(this.hierMesh(), leftGearPos, rightGearPos, noseGearPos);
    }

    // Old Style Gear Animation Fallback Methods
    public static void moveGear(HierMesh hiermesh, float gearPos) {
        TB2Dxyz.moveGear(hiermesh, gearPos, gearPos, gearPos);
    }

    protected void moveGear(float gearPos) {
        TB2Dxyz.moveGear(this.hierMesh(), gearPos);
    }

    public void moveWheelSink() {
        if (this.FM.CT.GearControl > 0.5F) {
            TB2Dxyz.suspension(this.hierMesh(), this.FM.Gears.gWheelSinking[0], this.FM.Gears.gWheelSinking[1], this.FM.Gears.gWheelSinking[2]);
        }
    }

    private static void doResetYPRmodifier() {
        Aircraft.xyz[0] = Aircraft.xyz[1] = Aircraft.xyz[2] = Aircraft.ypr[0] = Aircraft.ypr[1] = Aircraft.ypr[2] = 0F;
    }

    private static void suspension(HierMesh hiermesh, float leftGearDeflection, float rightGearDeflection, float noseGearDeflection) {
        // +++ Nose Gear Suspension +++

        // Shock Strut Movement
        TB2Dxyz.doResetYPRmodifier(); // Reset Aircraft.xyz (Position) and ypr (Orientation) Values
        Aircraft.xyz[2] = Aircraft.cvt(noseGearDeflection, 0F, 0.3F, 0F, 0.3F); // Convert Nose Gear Deflection value 0~1 to Shock Strut Coords
        hiermesh.chunkSetLocate("GearC2b_D0", Aircraft.xyz, Aircraft.ypr); // Apply Shock Strut Movement

        // Torsion Link Rotation
        double strutLen = 0.4D; // Shock Strut Length when Deflection is 0.
        double torsionLen = 0.28D; // Length of Tension Links

        // Calculate the Tension Links angle depending on the Deflection of the Shock Strut
        // This is simple Math for isosceles triangles
        float alpha = (float) Math.toDegrees((Math.PI - Math.acos(1D - (Math.pow(strutLen - Aircraft.xyz[2], 2) / (2D * Math.pow(torsionLen, 2))))) / 2D);
        // Apply Torsion Link angles accordingly, taking the link's base angles from 3D model into account
        hiermesh.chunkSetAngles("GearC2c_D0", 0F, 45F - alpha, 0F);
        hiermesh.chunkSetAngles("GearC2d_D0", 0F, alpha - 45F, 0F);
        // ----------------------------

        // +++ Left Main Gear Suspension +++

        // Helper Parameters because the Main Gear's angled arrangement
        double mainGearAngle = Math.toRadians(10D); // Gear is angled by 10 degrees
        float mainGearAngleCos = (float) Math.cos(mainGearAngle); // Vertical Movement Factor for Shock Strut
        float mainGearAngleSin = (float) Math.sin(mainGearAngle); // Horizontal Movement Factor for Shock Strut

        // Shock Strut Movement
        TB2Dxyz.doResetYPRmodifier(); // Reset Aircraft.xyz (Position) and ypr (Orientation) Values
        // Convert Left Gear Deflection value 0~1 to Shock Strut Coords
        Aircraft.xyz[2] = Aircraft.cvt(leftGearDeflection, 0F, 0.27F, 0.07F, -0.2F) * mainGearAngleCos;
        Aircraft.xyz[1] = Aircraft.cvt(leftGearDeflection, 0F, 0.27F, -0.07F, 0.2F) * mainGearAngleSin;
        hiermesh.chunkSetLocate("GearL2b_D0", Aircraft.xyz, Aircraft.ypr); // Apply Shock Strut Movement

        // Torsion Link Rotation
        strutLen = 0.26D; // Shock Strut Length when Deflection is 0.
        torsionLen = 0.17D; // Length of Tension Links

        // Calculate the Tension Links angle depending on the Deflection of the Shock Strut
        // This is simple Math for isosceles triangles
        float beta = (float) Math.toDegrees((Math.PI - Math.acos(1D - (Math.pow(strutLen + Aircraft.xyz[2], 2) / (2D * Math.pow(torsionLen, 2))))) / 2D);
        // Apply Torsion Link angles accordingly, taking the link's base angles from 3D model into account
        hiermesh.chunkSetAngles("GearL2c_D0", 0F, 0F, beta - 40F);
        hiermesh.chunkSetAngles("GearL2d_D0", 0F, 0F, 38F - beta);
        // ----------------------------

        // +++ Right Main Gear Suspension +++

        // Shock Strut Movement
        TB2Dxyz.doResetYPRmodifier(); // Reset Aircraft.xyz (Position) and ypr (Orientation) Values
        // Convert Right Gear Deflection value 0~1 to Shock Strut Coords
        Aircraft.xyz[2] = Aircraft.cvt(rightGearDeflection, 0F, 0.27F, -0.07F, 0.2F) * mainGearAngleCos;
        Aircraft.xyz[1] = Aircraft.cvt(rightGearDeflection, 0F, 0.27F, -0.07F, 0.2F) * mainGearAngleSin;
        hiermesh.chunkSetLocate("GearR2b_D0", Aircraft.xyz, Aircraft.ypr); // Apply Shock Strut Movement

        // Torsion Link Rotation
        // Calculate the Tension Links angle depending on the Deflection of the Shock Strut
        // This is simple Math for isosceles triangles
        float gamma = (float) Math.toDegrees((Math.PI - Math.acos(1D - (Math.pow(strutLen - Aircraft.xyz[2], 2) / (2D * Math.pow(torsionLen, 2))))) / 2D);
        // Apply Torsion Link angles accordingly, taking the link's base angles from 3D model into account
        hiermesh.chunkSetAngles("GearR2c_D0", 0F, 0F, 40F - gamma);
        hiermesh.chunkSetAngles("GearR2d_D0", 0F, 0F, gamma - 38F);
    }

    protected void moveElevator(float f) {
        this.hierMesh().chunkSetAngles("VatorL_D0", 0.0F, 30F * f, 0.0F);
        this.hierMesh().chunkSetAngles("VatorR_D0", 0.0F, 30F * f, 0.0F);
    }

    protected void moveRudder(float f) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -31F * f, 0.0F);
        if (this.FM.CT.getGear() > 0.99F) {
            this.hierMesh().chunkSetAngles("GearC2b2_D0", -40F * f, 0F, 0F);
        }
    }

    protected void moveFlap(float f) {
        this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, -45F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Flap02_D0", 0.0F, -45F * f, 0.0F);
    }

    protected void moveWingFold(HierMesh hiermesh, float f) {
        hiermesh.chunkSetAngles("WingLMid_D0", 0.0F, 155F * f, 0.0F);
        hiermesh.chunkSetAngles("WingRMid_D0", 0.0F, -155F * f, 0.0F);
    }

    public void moveWingFold(float f) {
        this.moveWingFold(this.hierMesh(), f);
        this.updateHooks();
    }

    private void updateHooks() {
        String[] weaponHookArray = Aircraft.getWeaponHooksRegistered(this.getClass());
        for (int i = 0; i < weaponHookArray.length; i++) {
            try {
                BulletEmitter be = this.getBulletEmitterByHookName(weaponHookArray[i]);
                if (!be.haveBullets()) {
                    continue;
                }
                int bullets = ((this == World.getPlayerAircraft()) && !World.cur().diffCur.Limited_Ammo) ? -1 : be.countBullets();
                be.loadBullets(0);
                be.loadBullets(bullets);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void moveFan(float f) {
        int i = 0;
        for (int j = 0; j < 2; j++) {
            if (this.oldProp[j] < 2) {
                i = Math.abs((int) (this.FM.EI.engines[0].getw() * 0.06F));
                if (i >= 1) {
                    i = 1;
                }
                if ((i != this.oldProp[j]) && this.hierMesh().isChunkVisible(Aircraft.Props[j][this.oldProp[j]])) {
                    this.hierMesh().chunkVisible(Aircraft.Props[j][this.oldProp[j]], false);
                    this.oldProp[j] = i;
                    this.hierMesh().chunkVisible(Aircraft.Props[j][i], true);
                }
            }
            if (i == 0) {
                this.propPos[j] = (this.propPos[j] + (57.3F * this.FM.EI.engines[0].getw() * f)) % 360F;
            } else {
                float f1 = 57.3F * this.FM.EI.engines[0].getw();
                f1 %= 2880F;
                f1 /= 2880F;
                if (f1 <= 0.5F) {
                    f1 *= 2.0F;
                } else {
                    f1 = (f1 * 2.0F) - 2.0F;
                }
                f1 *= 1200F;
                this.propPos[j] = (this.propPos[j] + (f1 * f)) % 360F;
            }
            if (j == 0) {
                this.hierMesh().chunkSetAngles(Aircraft.Props[j][i], 0.0F, this.propPos[j], 0.0F);
            } else {
                this.hierMesh().chunkSetAngles(Aircraft.Props[j][i], 0.0F, -(this.propPos[j] - 20F), 0.0F);
            }
        }
    }

    public void hitProp(int i, int j, Actor actor) {
        if ((i > 1) || (this.oldProp[i] == 2)) {
            return;
        }

        if ((this.isChunkAnyDamageVisible("Prop" + (i + 1)) || this.isChunkAnyDamageVisible("PropRot" + (i + 1)))) {
            this.hierMesh().chunkVisible(Aircraft.Props[i + 1][0], false);
            this.hierMesh().chunkVisible(Aircraft.Props[i + 1][1], false);
            this.hierMesh().chunkVisible(Aircraft.Props[i + 1][2], true);
        }
        super.hitProp(i, j, actor);
    }

    public void update(float f) {
        super.update(f);
        if (this == World.getPlayerAircraft()) {
            World.cur().diffCur.Torque_N_Gyro_Effects = false;
        }
        if (this.FM.CT.getArrestor() > 0.001F) {
            if (this.FM.Gears.arrestorVAngle != 0.0F) {
                float f1 = Aircraft.cvt(this.FM.Gears.arrestorVAngle, -73.5F, 3.5F, 1.0F, 0.0F);
                this.arrestor = (0.8F * this.arrestor) + (0.2F * f1);
                this.moveArrestorHook(this.arrestor);
            } else {
                float f2 = -1.224F * this.FM.Gears.arrestorVSink;
                if ((f2 < 0.0F) && (this.FM.getSpeedKMH() > 60F)) {
                    Eff3DActor.New(this, this.FM.Gears.arrestorHook, null, 1.0F, "3DO/Effects/Fireworks/04_Sparks.eff", 0.1F);
                }
                if (f2 > 0.2F) {
                    f2 = 0.2F;
                }
                if (f2 > 0.0F) {
                    this.arrestor = (0.7F * this.arrestor) + (0.3F * (this.arrestor + f2));
                } else {
                    this.arrestor = (0.3F * this.arrestor) + (0.7F * (this.arrestor + f2));
                }
                if (this.arrestor < 0.0F) {
                    this.arrestor = 0.0F;
                } else if (this.arrestor > this.FM.CT.getArrestor()) {
                    this.arrestor = this.FM.CT.getArrestor();
                }
                this.moveArrestorHook(this.arrestor);
            }
        }
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        if (s.startsWith(".")) {
            s = s.substring(1); // Strip trailing dot
        }
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) {
                if (s.startsWith("xxarmorp")) {
                    int i = s.charAt(8) - 48;
                    switch (i) {
                        case 1:
                            this.getEnergyPastArmor(22.76D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                            if (shot.power <= 0.0F) {
                                this.doRicochetBack(shot);
                            }
                            break;

                        case 3:
                            this.getEnergyPastArmor(9.366F, shot);
                            break;

                        case 5:
                            this.getEnergyPastArmor(12.7D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot);
                            break;
                    }
                }
            } else if (s.startsWith("xxcontrols")) {
                int j = s.charAt(10) - 48;
                switch (j) {
                    case 1:
                    case 2:
                        if (this.getEnergyPastArmor(0.25F / ((float) Math.sqrt((Aircraft.v1.y * Aircraft.v1.y) + (Aircraft.v1.z * Aircraft.v1.z)) + 0.0001F), shot) > 0.0F) {
                            if (World.Rnd().nextFloat() < 0.05F) {
                                this.FM.AS.setControlsDamage(shot.initiator, 1);
                            }
                            if (World.Rnd().nextFloat() < 0.75F) {
                                this.FM.AS.setControlsDamage(shot.initiator, 2);
                            }
                        }
                        break;

                    case 3:
                        if (this.getEnergyPastArmor(3.2F, shot) > 0.0F) {
                            this.FM.AS.setControlsDamage(shot.initiator, 2);
                            this.FM.AS.setControlsDamage(shot.initiator, 1);
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                        }
                        break;

                    case 4:
                        if (this.getEnergyPastArmor(0.1F, shot) > 0.0F) {
                            this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 6);
                        }
                        break;

                    case 5:
                    case 7:
                        if (this.getEnergyPastArmor(0.1F, shot) > 0.0F) {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                        }
                        break;

                    case 6:
                    case 8:
                        if ((this.getEnergyPastArmor(4D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                        }
                        break;
                }
            } else if (s.startsWith("xxspar")) {
                if (s.startsWith("xxsparli") && (this.chunkDamageVisible("WingLIn") > 2) && (this.getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)) {
                    this.nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparri") && (this.chunkDamageVisible("WingRIn") > 2) && (this.getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)) {
                    this.nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if (s.startsWith("xxsparlo") && (this.chunkDamageVisible("WingLMid") > 2) && (this.getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)) {
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if (s.startsWith("xxsparro") && (this.chunkDamageVisible("WingRMid") > 2) && (this.getEnergyPastArmor(6.96D / (Math.abs(Aircraft.v1.x) + 0.0001D), shot) > 0.0F)) {
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if (s.startsWith("xxspart") && (this.chunkDamageVisible("Tail1") > 2) && (this.getEnergyPastArmor(3.86F / (float) Math.sqrt((Aircraft.v1.y * Aircraft.v1.y) + (Aircraft.v1.z * Aircraft.v1.z)), shot) > 0.0F) && (World.Rnd().nextFloat() < 0.25F)) {
                    this.nextDMGLevels(1, 2, "Tail1_D3", shot.initiator);
                }
            } else {
                if (s.startsWith("xxlock")) {
                    if (s.startsWith("xxlockr") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                        this.nextDMGLevels(3, 2, "Rudder1_D" + this.chunkDamageVisible("Rudder1"), shot.initiator);
                    }
                    if (s.startsWith("xxlockvl") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                        this.nextDMGLevels(3, 2, "VatorL_D" + this.chunkDamageVisible("VatorL"), shot.initiator);
                    }
                    if (s.startsWith("xxlockvr") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                        this.nextDMGLevels(3, 2, "VatorR_D" + this.chunkDamageVisible("VatorR"), shot.initiator);
                    }
                    if (s.startsWith("xxlockalL") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                        this.nextDMGLevels(3, 2, "AroneL_D" + this.chunkDamageVisible("AroneL"), shot.initiator);
                    }
                    if (s.startsWith("xxlockalR") && (this.getEnergyPastArmor(5.5F * World.Rnd().nextFloat(1.0F, 1.5F), shot) > 0.0F)) {
                        this.nextDMGLevels(3, 2, "AroneR_D" + this.chunkDamageVisible("AroneR"), shot.initiator);
                    }
                }
                if (s.startsWith("xxeng")) {
                    if ((s.endsWith("prop") || s.endsWith("pipe")) && (this.getEnergyPastArmor(0.2F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.5F)) {
                        this.FM.EI.engines[0].setKillPropAngleDevice(shot.initiator);
                    }
                    if (s.endsWith("case") || s.endsWith("gear")) {
                        if (this.getEnergyPastArmor(0.2F, shot) > 0.0F) {
                            if (World.Rnd().nextFloat() < (shot.power / 140000F)) {
                                this.FM.AS.setEngineStuck(shot.initiator, 0);
                            }
                            if (World.Rnd().nextFloat() < (shot.power / 85000F)) {
                                this.FM.AS.hitEngine(shot.initiator, 0, 2);
                            }
                        } else if (World.Rnd().nextFloat() < 0.02F) {
                            this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, 1);
                        } else {
                            this.FM.EI.engines[0].setReadyness(shot.initiator, this.FM.EI.engines[0].getReadyness() - World.Rnd().nextFloat(0.0F, shot.power / 48000F));
                        }
                        this.getEnergyPastArmor(12F, shot);
                    }
                    if (s.endsWith("cyl1") || s.endsWith("cyl2")) {
                        if ((this.getEnergyPastArmor(World.Rnd().nextFloat(2.0F, 4F), shot) > 0.0F) && (World.Rnd().nextFloat() < (this.FM.EI.engines[0].getCylindersRatio() * 0.75F))) {
                            this.FM.EI.engines[0].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 19000F)));
                            if (World.Rnd().nextFloat() < (shot.power / 48000F)) {
                                this.FM.AS.hitEngine(shot.initiator, 0, 2);
                            }
                        }
                        this.getEnergyPastArmor(25F, shot);
                    }
                    if (s.endsWith("supc")) {
                        if (this.getEnergyPastArmor(0.05F, shot) > 0.0F) {
                            this.FM.EI.engines[0].setKillCompressor(shot.initiator);
                        }
                        this.getEnergyPastArmor(2.0F, shot);
                    }
                    if (s.startsWith("xxeng1oil")) {
                        this.FM.AS.hitOil(shot.initiator, 0);
                    }
                } else if (s.startsWith("xxtank")) {
                    int k = s.charAt(6) - 49;
                    if ((this.getEnergyPastArmor(0.19F, shot) > 0.0F) && (World.Rnd().nextFloat() < 0.1F)) {
                        if (this.FM.AS.astateTankStates[k] == 0) {
                            this.FM.AS.hitTank(shot.initiator, k, 1);
                            this.FM.AS.doSetTankState(shot.initiator, k, 1);
                        } else if (this.FM.AS.astateTankStates[k] == 1) {
                            this.FM.AS.hitTank(shot.initiator, k, 1);
                            this.FM.AS.doSetTankState(shot.initiator, k, 2);
                        }
                        if ((shot.powerType == 3) && (World.Rnd().nextFloat() < 0.25F)) {
                            this.FM.AS.hitTank(shot.initiator, k, 2);
                        }
                    }
                } else if (s.startsWith("xxmgun")) {
                    if (s.endsWith("01")) {
                        this.FM.AS.setJamBullets(0, 0);
                    }
                    if (s.endsWith("02")) {
                        this.FM.AS.setJamBullets(1, 0);
                    }
                    this.getEnergyPastArmor(World.Rnd().nextFloat(0.3F, 12.6F), shot);
                } else if (s.startsWith("xxcannon")) {
                    if (s.endsWith("02")) {
                        this.FM.AS.setJamBullets(3, 0);
                    }
                    if (s.endsWith("03")) {
                        this.FM.AS.setJamBullets(4, 0);
                    }
                    this.getEnergyPastArmor(World.Rnd().nextFloat(0.3F, 12.6F), shot);
                }
            }
        } else if (s.startsWith("xcf")) {
            this.setControlDamage(shot, 0);
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("CF") < 3) {
                this.hitChunk("CF", shot);
            }
        } else if (s.startsWith("")) {
            if (this.chunkDamageVisible("Engine1") < 2) {
                this.hitChunk("Engine1", shot);
            }
        } else if (s.startsWith("xtail")) {
            this.setControlDamage(shot, 1);
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Tail1") < 3) {
                this.hitChunk("Tail1", shot);
            }
        } else if (s.startsWith("xkeel")) {
            this.hitChunk("Keel1", shot);
        } else if (s.startsWith("xrudder")) {
            this.setControlDamage(shot, 2);
            if (this.chunkDamageVisible("Rudder1") < 1) {
                this.hitChunk("Rudder1", shot);
            }
        } else if (s.startsWith("xstab")) {
            if (s.startsWith("xstabl")) {
                this.hitChunk("StabL", shot);
            }
            if (s.startsWith("xstabr")) {
                this.hitChunk("StabR", shot);
            }
        } else if (s.startsWith("xvator")) {
            if (s.startsWith("xvatorl") && (this.chunkDamageVisible("VatorL") < 1)) {
                this.hitChunk("VatorL", shot);
            }
            if (s.startsWith("xvatorr") && (this.chunkDamageVisible("VatorR") < 1)) {
                this.hitChunk("VatorR", shot);
            }
        } else if (s.startsWith("xwing")) {
            if (s.startsWith("xwinglin") && (this.chunkDamageVisible("WingLIn") < 3)) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingLIn", shot);
            }
            if (s.startsWith("xwingrin") && (this.chunkDamageVisible("WingRIn") < 3)) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingRIn", shot);
            }
            if (s.startsWith("xwinglmid") && (this.chunkDamageVisible("WingLMid") < 3)) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingLMid", shot);
            }
            if (s.startsWith("xwingrmid") && (this.chunkDamageVisible("WingRMid") < 3)) {
                this.setControlDamage(shot, 0);
                this.hitChunk("WingRMid", shot);
            }
            if (s.startsWith("xwinglout1") && (this.chunkDamageVisible("WingLOut") < 3)) {
                this.hitChunk("WingLOut", shot);
            }
            if (s.startsWith("xwingrout1") && (this.chunkDamageVisible("WingROut") < 3)) {
                this.hitChunk("WingROut", shot);
            }
        } else if (s.startsWith("xarone")) {
            if (s.startsWith("xaronel") && (this.chunkDamageVisible("AroneL") < 1)) {
                this.hitChunk("AroneL", shot);
            }
            if (s.startsWith("xaroner") && (this.chunkDamageVisible("AroneR") < 1)) {
                this.hitChunk("AroneR", shot);
            }
        } else if (s.startsWith("xflap")) {
            if (s.startsWith("xflap01") && (this.chunkDamageVisible("Flap01") < 1)) {
                this.hitChunk("Flap01", shot);
            }
            if (s.startsWith("xflap02") && (this.chunkDamageVisible("Flap02") < 1)) {
                this.hitChunk("Flap02", shot);
            }
        } else if (s.startsWith("xgear")) {
            if ((World.Rnd().nextFloat() < 0.1F) && (this.getEnergyPastArmor(World.Rnd().nextFloat(1.2F, 3.435F), shot) > 0.0F)) {
                this.FM.AS.setInternalDamage(shot.initiator, 3);
            }
        } else if (s.startsWith("xoil") || s.startsWith("Oil")) {
            if (World.Rnd().nextFloat() < 0.12F) {
                this.FM.AS.hitOil(shot.initiator, 0);
            }
        } else if (!s.startsWith("xblister") && (s.startsWith("xpilot") || s.startsWith("xhead"))) {
            byte byte0 = 0;
            int l;
            if (s.endsWith("a")) {
                byte0 = 1;
                l = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte0 = 2;
                l = s.charAt(6) - 49;
            } else {
                l = s.charAt(5) - 49;
            }
            this.hitFlesh(l, shot, byte0);
        }
    }

    protected void setControlDamage(Shot shot, int i) {
        if ((World.Rnd().nextFloat() < 0.002F) && (this.getEnergyPastArmor(4F, shot) > 0.0F)) {
            this.FM.AS.setControlsDamage(shot.initiator, i);
        }
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 33:
                return super.cutFM(34, j, actor);

            case 36:
                return super.cutFM(37, j, actor);
        }
        return super.cutFM(i, j, actor);
    }

    private float         arrestor;
    public static boolean gearsSeparated;

    static {
        try {
            Controls.class.getMethod("getGearC", null);
            TB2Dxyz.gearsSeparated = true;
        } catch (Exception e) {
            TB2Dxyz.gearsSeparated = false;
        }

        Class class1 = TB2Dxyz.class;
        Property.set(class1, "originCountry", PaintScheme.countryUSA);
    }
}
