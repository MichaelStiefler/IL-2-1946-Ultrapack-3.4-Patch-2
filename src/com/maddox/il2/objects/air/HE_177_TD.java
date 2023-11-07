package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.JGP.Geom;
import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.War;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;

public abstract class HE_177_TD extends Scheme2 implements TypeBomber, TypeTransport {

    public HE_177_TD() {
        this.isVentralGunActive = false;
        this.minElev = 0.0F;
        this.f_tmp = 0.0F;
        this.B1_Distance = 500F;
        this.B1_Speed = 0.0F;
        this.HeadTangage = 0.0F;
        this.HeadTangage1 = 0.0F;
        this.HeadYaw = 0.0F;
        this.HeadYaw1 = 0.0F;
        this.ReviY = 0.0F;
        this.ReviX = 0.0F;
        this.ReviYaw = 0.0F;
        this.bAirEnemy = false;
        this.iRust = 0;
    }

    public static void moveGear(HierMesh hiermesh, float f, float f1, float f2) {
        float f3 = Aircraft.cvt(f, 0.01F, 0.175F, 0.0F, -90F);
        float f4 = Aircraft.cvt(f1, 0.01F, 0.175F, 0.0F, -90F);
        if (f > 0.8F) f3 = Aircraft.cvt(f, 0.8F, 1.0F, -90F, 0.0F);
        if (f1 > 0.8F) f4 = Aircraft.cvt(f1, 0.8F, 1.0F, -90F, 0.0F);
        hiermesh.chunkSetAngles("GearL1_11_D0", 0.0F, f3, 0.0F);
        hiermesh.chunkSetAngles("GearL2_11_D0", 0.0F, f3, 0.0F);
        hiermesh.chunkSetAngles("GearR1_11_D0", 0.0F, f4, 0.0F);
        hiermesh.chunkSetAngles("GearR2_11_D0", 0.0F, f4, 0.0F);
        hiermesh.chunkSetAngles("GearC4_D0", 0.0F, Aircraft.cvt(f2, 0.01F, 0.175F, 0.0F, -150F), 0.0F);
        hiermesh.chunkSetAngles("GearC5_D0", 0.0F, Aircraft.cvt(f2, 0.01F, 0.175F, 0.0F, -130F), 0.0F);
        hiermesh.chunkSetAngles("GearC6_D0", 0.0F, Aircraft.cvt(f2, 0.01F, 0.175F, 0.0F, -150F), 0.0F);
        hiermesh.chunkSetAngles("GearC7_D0", 0.0F, Aircraft.cvt(f2, 0.01F, 0.175F, 0.0F, -130F), 0.0F);
        hiermesh.chunkSetAngles("GearL1_12_D0", 0.0F, -90F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL2_12_D0", 0.0F, -90F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL1_10_D0", 0.0F, -90F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL2_10_D0", 0.0F, -90F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL1_7_D0", 0.0F, 0.0F, -144F * f);
        hiermesh.chunkSetAngles("GearL2_7_D0", 0.0F, 0.0F, 144F * f);
        hiermesh.chunkSetAngles("GearL1_2_D0", 0.0F, -90F * f, 0.0F);
        hiermesh.chunkSetAngles("GearL2_2_D0", 0.0F, -90F * f, 0.0F);
        hiermesh.chunkSetAngles("GearR1_12_D0", 0.0F, -90F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearR2_12_D0", 0.0F, -90F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearR1_10_D0", 0.0F, -90F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearR2_10_D0", 0.0F, -90F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearR1_7_D0", 0.0F, 0.0F, -144F * f1);
        hiermesh.chunkSetAngles("GearR2_7_D0", 0.0F, 0.0F, -144F * f1);
        hiermesh.chunkSetAngles("GearR1_2_D0", 0.0F, -90F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearR2_2_D0", 0.0F, -90F * f1, 0.0F);
        hiermesh.chunkSetAngles("GearC2_D0", 0.0F, -90F * f2, 0.0F);
    }

    // ************************************************************************************************
    // Gear code for backward compatibility, older base game versions don'f1 indepently move their gears
    public static void moveGear(HierMesh hiermesh, float f) {
        moveGear(hiermesh, f, f, f); // re-route old style function calls to new code
    }

    protected void moveGear(float f) {
        moveGear(this.hierMesh(), f);
    }
    // ************************************************************************************************

    public void moveWheelSink() {
        this.resetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0] - 0.055F, 0.0F, 0.1625F, 0.0F, 0.1625F);
        this.hierMesh().chunkSetLocate("GearL1_3_D0", Aircraft.xyz, Aircraft.ypr);
        this.hierMesh().chunkSetLocate("GearL2_3_D0", Aircraft.xyz, Aircraft.ypr);
        Aircraft.xyz[2] = Aircraft.cvt(this.FM.Gears.gWheelSinking[1] - 0.055F, 0.0F, 0.1625F, 0.0F, 0.1625F);
        this.hierMesh().chunkSetLocate("GearR1_3_D0", Aircraft.xyz, Aircraft.ypr);
        this.hierMesh().chunkSetLocate("GearR2_3_D0", Aircraft.xyz, Aircraft.ypr);
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(this.FM.Gears.gWheelSinking[2] - 0.055F, 0.0F, 0.1625F, -0.02F, 0.02F);
        this.hierMesh().chunkSetLocate("GearC3_D0", Aircraft.xyz, Aircraft.ypr);
    }

    protected void moveGear(float f, float f1, float f2) {
        moveGear(this.hierMesh(), f, f1, f2);
    }

    public void moveSteering(float f) {
        this.hierMesh().chunkSetAngles("GearC3_D0", -Aircraft.cvt(f, -65F, 65F, 65F, -65F), 0.0F, 0.0F);
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (flag) {
            if (this.FM.AS.astateEngineStates[0] > 3 && World.Rnd().nextFloat() < 0.09F) this.FM.AS.hitTank(this, 1, 1);
            if (this.FM.AS.astateEngineStates[0] > 3 && World.Rnd().nextFloat() < 0.09F) this.FM.AS.hitTank(this, 6, 1);
            if (this.FM.AS.astateEngineStates[1] > 3 && World.Rnd().nextFloat() < 0.09F) this.FM.AS.hitTank(this, 2, 1);
            if (this.FM.AS.astateEngineStates[1] > 3 && World.Rnd().nextFloat() < 0.09F) this.FM.AS.hitTank(this, 7, 1);
            if (this.FM.AS.astateTankStates[0] > 5 && World.Rnd().nextFloat() < 0.02F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[0] + "0", 0, this);
            if (this.FM.AS.astateTankStates[1] > 5 && World.Rnd().nextFloat() < 0.02F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[1] + "0", 0, this);
            if (this.FM.AS.astateTankStates[2] > 5 && World.Rnd().nextFloat() < 0.02F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[2] + "0", 0, this);
            if (this.FM.AS.astateTankStates[3] > 5 && World.Rnd().nextFloat() < 0.02F) this.nextDMGLevel(this.FM.AS.astateEffectChunks[3] + "0", 0, this);
        }
        for (int i = 1; i < 7; i++)
            if (i < 3 || i == 4) {
                if (this.FM.getAltitude() < 3000F) this.hierMesh().chunkVisible("HMask" + i + "_D0", false);
                else this.hierMesh().chunkVisible("HMask" + i + "_D0", this.hierMesh().isChunkVisible("Pilot" + i + "_FAK"));
            } else if (this.FM.getAltitude() < 3000F) this.hierMesh().chunkVisible("HMask" + i + "_D0", false);
            else this.hierMesh().chunkVisible("HMask" + i + "_D0", this.hierMesh().isChunkVisible("Pilot" + i + "_D0"));

        Actor actor = War.GetNearestEnemy(this, 16, 6000F);
        Aircraft aircraft = War.getNearestEnemy(this, 5900F);
        if (actor != null && !(actor instanceof BridgeSegment) || aircraft != null) this.bAirEnemy = true;
        else this.bAirEnemy = false;
    }

//	protected void moveFlap(float f) {
//		float f1 = -12F * f;
//		float f2 = -20F * f;
//		this.hierMesh().chunkSetAngles("Flap01E_D0", 0.0F, f1, 0.0F);
//		this.hierMesh().chunkSetAngles("Flap02E_D0", 0.0F, f1, 0.0F);
//		this.hierMesh().chunkSetAngles("Flap03E_D0", 0.0F, f1, 0.0F);
//		this.hierMesh().chunkSetAngles("Flap04E_D0", 0.0F, f1, 0.0F);
//		this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, f2, 0.0F);
//		this.hierMesh().chunkSetAngles("Flap02_D0", 0.0F, f2, 0.0F);
//		this.hierMesh().chunkSetAngles("Flap03_D0", 0.0F, f2, 0.0F);
//		this.hierMesh().chunkSetAngles("Flap04_D0", 0.0F, f2, 0.0F);
//	}

    private void moveFlapExt(float f, boolean extender) {
        String meshExt = extender ? "E" : "";
        float yprExt = extender ? 0.6F : 1F;
        if (f <= 0.2F) {
            xyz[0] = Aircraft.cvt(f, 0.0F, 0.2F, 0.0F, 0.7F);
            xyz[2] = -f * 0.18F;
        } else {
            xyz[0] = 0.7F;
            xyz[2] = -0.036F;
        }
        xyz[1] = 0F;

        ypr[0] = 0F;
        ypr[1] = -45.0F * f * yprExt;
        ypr[2] = 0F;

        this.hierMesh().chunkSetLocate("Flap02_D0" + meshExt, xyz, ypr);
        this.hierMesh().chunkSetLocate("Flap03_D0" + meshExt, xyz, ypr);

        if (f <= 0.2F) {
            ypr[0] = f * -13.5F * yprExt;
            ypr[2] = f * 2.0F * yprExt;
        } else {
            ypr[0] = -2.7F * yprExt;
            ypr[2] = 0.4F * yprExt;
        }

        xyz[0] *= 0.8F;
        xyz[2] *= 0.5F;
        this.hierMesh().chunkSetLocate("Flap01_D0" + meshExt, xyz, ypr);
        ypr[0] *= -1F;
        ypr[2] *= -1F;
        this.hierMesh().chunkSetLocate("Flap04_D0" + meshExt, xyz, ypr);

    }

    protected void moveFlap(float f) {
        this.moveFlapExt(f, false);
        this.moveFlapExt(f, true);
    }

    public void update(float f) {
        float f1 = 0.0F;
        if (this.isVentralGunActive) f1 = 180F;
        this.hierMesh().chunkSetAngles("Pilot4_D0", f1, 0.0F, 0.0F);
        float f2 = this.FM.EI.engines[0].getControlRadiator();
        if (Math.abs(this.radiators[0] - f2) > 0.01F) {
            this.radiators[0] = f2;
            for (int i = 1; i < 20; i++) {
                String s = "RADL" + i + "_D0";
                this.hierMesh().chunkSetAngles(s, 0.0F, -30F * f2, 0.0F);
            }

        }
        f2 = this.FM.EI.engines[1].getControlRadiator();
        if (Math.abs(this.radiators[1] - f2) > 0.01F) {
            this.radiators[1] = f2;
            for (int j = 1; j < 20; j++) {
                String s1 = "RADR" + j + "_D0";
                this.hierMesh().chunkSetAngles(s1, 0.0F, -30F * f2, 0.0F);
            }

        }
        float f3 = this.FM.turret[3].tu[0];
        this.hierMesh().chunkSetAngles("Dome_D0", 0.0F, -f3, 0.0F);
        this.rotateInnerWheels();
        super.update(f);
    }

    private void rotateInnerWheels() {
        this.hierMesh().chunkSetAngles("GearL1_1_D0", 0.0F, -this.FM.Gears.gWheelAngles[0], 0.0F);
        this.hierMesh().chunkSetAngles("GearR1_1_D0", 0.0F, -this.FM.Gears.gWheelAngles[1], 0.0F);
    }

    public boolean turretAngles(int i, float af[]) {
        boolean flag = super.turretAngles(i, af);
        float f = -af[0];
        float f1 = af[1];
        switch (i) {
            default:
                break;

            case 0:
                if (f < -40F) {
                    f = -40F;
                    flag = false;
                }
                if (f > 40F) {
                    f = 40F;
                    flag = false;
                }
                if (f1 < -20F) {
                    f1 = -20F;
                    flag = false;
                }
                if (f1 > 60F) {
                    f1 = 60F;
                    flag = false;
                }
                if (f1 > 20F) {
                    if (f > 0.0F) {
                        float f3 = 40F * (float) Math.cos(Geom.DEG2RAD(2.25F * (f1 - 20F)));
                        if (f > f3) {
                            f = f3;
                            flag = false;
                        }
                    } else {
                        float f4 = -40F * (float) Math.cos(Geom.DEG2RAD(2.25F * (f1 - 20F)));
                        if (f < f4) {
                            f = f4;
                            flag = false;
                        }
                    }
                } else if (f > 0.0F) {
                    float f5 = 40F * (float) Math.cos(Geom.DEG2RAD(2.25F * (f1 - 20F)));
                    if (f > f5) {
                        f = f5;
                        flag = false;
                    }
                } else {
                    float f6 = -40F * (float) Math.cos(Geom.DEG2RAD(2.25F * (f1 - 20F)));
                    if (f < f6) {
                        f = f6;
                        flag = false;
                    }
                }
                break;

            case 1:
                if (Math.abs(f) > 90F && this.FM.turret[2].target != null && this.FM.CT.getBayDoor() < 0.05F) {
                    this.isVentralGunActive = true;
                    f = 0.0F;
                    f1 = 0.0F;
                    flag = false;
                    break;
                }
                this.isVentralGunActive = false;
                if (f >= 0.0F) {
                    if (f1 >= -1.7F) {
                        if (f > 15F) {
                            f = 15F;
                            flag = false;
                        }
                        if (f1 > 7F) {
                            f1 = 7F;
                            flag = false;
                        }
                        break;
                    }
                    if (f1 < -1.7F && f1 > -14.39F) {
                        if (f > Aircraft.cvt(f1, -14.39F, -1.7F, 5.5F, 15F)) {
                            f = Aircraft.cvt(f1, -14.39F, -1.7F, 5.5F, 15F);
                            flag = false;
                        }
                        break;
                    }
                    if (f > 5.5F) {
                        f = 5.5F;
                        flag = false;
                    }
                    if (f1 < -40F) {
                        f1 = -40F;
                        flag = false;
                    }
                    break;
                }
                if (f1 >= -20.29F) {
                    if (f < -15F) {
                        f = -15F;
                        flag = false;
                    }
                    if (f1 > 7F) {
                        f1 = 7F;
                        flag = false;
                    }
                    break;
                }
                if (f1 > -22.9F && f1 < -20.29F) {
                    if (f < Aircraft.cvt(f1, -22.9F, -20.29F, -6.5F, -15F)) {
                        f = Aircraft.cvt(f1, -22.9F, -20.29F, -6.5F, -15F);
                        flag = false;
                    }
                    break;
                }
                if (f < -6.5F) {
                    f = -6.5F;
                    flag = false;
                }
                if (f1 < -40F) {
                    f1 = -40F;
                    flag = false;
                }
                break;

            case 2:
                if (!this.isVentralGunActive) {
                    f = 0.0F;
                    f1 = -3F;
                    flag = false;
                    break;
                }
                if (this.FM.CT.BayDoorControl > 0.05F) {
                    f = 0.0F;
                    f1 = -3F;
                    flag = false;
                    this.isVentralGunActive = false;
                    break;
                }
                if (f >= 0.0F) {
                    if (f1 >= -35F) {
                        if (f1 >= -3F && f < 40.03915F) {
                            if (f > 40.03915F) {
                                f = 40.03915F;
                                flag = false;
                            }
                            if (f1 > Aircraft.cvt(f, 0.0F, 40.03915F, -3F, -2.311216F)) {
                                f1 = Aircraft.cvt(f, 0.0F, 40.03915F, -3F, -2.311216F);
                                flag = false;
                            }
                            break;
                        }
                        if (f1 > -2.311216F) {
                            f1 = -2.311216F;
                            flag = false;
                        }
                        if (f > Aircraft.cvt(f1, -35F, -2.311216F, 59F, 40.03915F)) {
                            f = Aircraft.cvt(f1, -35F, -2.311216F, 59F, 40.03915F);
                            flag = false;
                        }
                        break;
                    }
                    if (f1 >= -51.67F) {
                        if (f > Aircraft.cvt(f1, -51.67F, -35F, 71.48F, 59F)) {
                            f = Aircraft.cvt(f1, -51.67F, -35F, 71.48F, 59F);
                            flag = false;
                        }
                        break;
                    }
                    if (f > 71.48F) {
                        f = 71.48F;
                        flag = false;
                    }
                    if (f1 < Aircraft.cvt(f, 0.0F, 71.48F, -80F, -51.67F)) {
                        f1 = Aircraft.cvt(f, 0.0F, 71.48F, -80F, -51.67F);
                        flag = false;
                    }
                    break;
                }
                if (f1 >= -35F) {
                    if (f1 >= -3F && f > -40.03915F) {
                        if (f < -40.03915F) {
                            f = -40.03915F;
                            flag = false;
                        }
                        if (f1 > Aircraft.cvt(f, -40.03915F, 0.0F, -2.311216F, -3F)) {
                            f1 = Aircraft.cvt(f, -40.03915F, 0.0F, -2.311216F, -3F);
                            flag = false;
                        }
                        break;
                    }
                    if (f1 > -2.311216F) {
                        f1 = -2.311216F;
                        flag = false;
                    }
                    if (f < Aircraft.cvt(f1, -35F, -2.311216F, -59F, -40.03915F)) {
                        f = Aircraft.cvt(f1, -35F, -2.311216F, -59F, -40.03915F);
                        flag = false;
                    }
                    break;
                }
                if (f1 >= -43.29834F) {
                    if (f < Aircraft.cvt(f1, -43.29834F, -35F, -42.30127F, -59F)) {
                        f = Aircraft.cvt(f1, -43.29834F, -35F, -42.30127F, -59F);
                        flag = false;
                    }
                    break;
                }
                if (f1 < -56.89526F) {
                    if (f < -57.896F) {
                        f = -57.896F;
                        flag = false;
                    }
                    if (f1 < Aircraft.cvt(f, -57.896F, 0.0F, -56.89526F, -80F)) {
                        f1 = Aircraft.cvt(f, -57.896F, 0.0F, -56.89526F, -80F);
                        flag = false;
                    }
                    break;
                }
                if (f < Aircraft.cvt(f1, -56.89526F, -43.29834F, -57.896F, -42.30127F)) {
                    f = Aircraft.cvt(f1, -56.89526F, -43.29834F, -57.896F, -42.30127F);
                    flag = false;
                }
                break;

            case 3:
                this.minElev = 0.0F;
                this.f_tmp = 0.0F;
                if (f >= -5F && f <= 5D) {
                    if (f > -1F && f < 1.5F && f1 < 11F) flag = false;
                    if (f1 < 3F) this.minElev = 3F;
                } else if (f > 5F && f <= 8F) {
                    this.f_tmp = Aircraft.cvt(f, 5F, 8F, 3F, 0.0F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f >= -8F && f < -5F) {
                    this.f_tmp = Aircraft.cvt(f, -8F, -5F, 0.0F, 3F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f <= -169F || f >= 172D) {
                    if (f1 < 4F) this.minElev = 4F;
                } else if (f >= 169F && f < 172F) {
                    this.f_tmp = Aircraft.cvt(f, 169F, 172F, 0.0F, 4F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f > -169F && f <= -166F) {
                    this.f_tmp = Aircraft.cvt(f, -169F, -166F, 4F, 0.0F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f >= 77F && f <= 85F) {
                    if (f1 < 1.5F) this.minElev = 1.5F;
                } else if (f >= 74F && f < 77F) {
                    this.f_tmp = Aircraft.cvt(f, 74F, 77F, 0.0F, 1.5F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f > 85F && f <= 88F) {
                    this.f_tmp = Aircraft.cvt(f, 85F, 88F, 1.5F, 0.0F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f >= -85F && f <= -77F) {
                    if (f1 < 1.5F) this.minElev = 1.5F;
                } else if (f >= -88F && f < -85F) {
                    this.f_tmp = Aircraft.cvt(f, -88F, -85F, 0.0F, 1.5F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f > -77F && f <= -74F) {
                    this.f_tmp = Aircraft.cvt(f, -77F, -74F, 1.5F, 0.0F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                }
                if (f1 > 87F) {
                    f1 = 87F;
                    flag = false;
                    break;
                }
                if (f1 < this.minElev) {
                    f1 = this.minElev;
                    flag = false;
                }
                break;

            case 4:
                this.minElev = -10F;
                this.f_tmp = 0.0F;
                if (f < -177F || f > 175F) this.minElev = 5F;
                else if (f >= -177F && f < -172F) {
                    this.f_tmp = Aircraft.cvt(f, -177F, -172F, 5F, 1.0F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f > 170F && f <= 175F) {
                    this.f_tmp = Aircraft.cvt(f, 170F, 175F, 1.0F, 5F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f < -98.5F || f > 98.5F) this.minElev = 1.0F;
                else if (f >= -98.5F && f < -93.5F) {
                    this.f_tmp = Aircraft.cvt(f, -98.5F, -93.5F, 0.0F, -10F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f > 93.5F && f <= 98.5F) {
                    this.f_tmp = Aircraft.cvt(f, 93.5F, 98.5F, -10F, 0.0F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f >= -8F && f <= 8F) this.minElev = 0.0F;
                else if (f >= -32F && f < -8F) {
                    this.f_tmp = Aircraft.cvt(f, -32F, -8F, -4.5F, 0.0F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f > 8F && f <= 32F) {
                    this.f_tmp = Aircraft.cvt(f, 8F, 32F, 0.0F, -4.5F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f >= -35F && f < -32F) {
                    this.f_tmp = Aircraft.cvt(f, -35F, -32F, -10F, -4.5F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                } else if (f > 32F && f <= 35F) {
                    this.f_tmp = Aircraft.cvt(f, 32F, 35F, -4.5F, -10F);
                    if (f1 < this.f_tmp) this.minElev = this.f_tmp;
                }
                if (f < 2.5F && f > -2F && f1 < 21.5F) flag = false;
                if (f < -180F) f = -180F;
                if (f > 180F) f = 180F;
                if (f1 > 80F) {
                    f1 = 80F;
                    flag = false;
                    break;
                }
                if (f1 < this.minElev) {
                    f1 = this.minElev;
                    flag = false;
                }
                break;

            case 5:
                if (f < -22.5F) {
                    f = -22.5F;
                    flag = false;
                }
                if (f > 22.5F) {
                    f = 22.5F;
                    flag = false;
                }
                if (f1 < -20F) {
                    f1 = -20F;
                    flag = false;
                }
                if (f1 > 40F) {
                    f1 = 40F;
                    flag = false;
                }
                break;
        }
        af[0] = -f;
        af[1] = f1;
        return flag;
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            default:
                break;

            case 33:
                this.hitProp(0, j, actor);
                break;

            case 36:
                this.hitProp(1, j, actor);
                break;

            case 35:
                this.FM.AS.hitEngine(this, 0, 3);
                if (World.Rnd().nextInt(0, 99) < 66) this.FM.AS.hitEngine(this, 0, 1);
                break;

            case 38:
                this.FM.AS.hitEngine(this, 1, 3);
                if (World.Rnd().nextInt(0, 99) < 66) this.FM.AS.hitEngine(this, 1, 1);
                break;

            case 11:
                this.hierMesh().chunkVisible("Wire_D0", false);
                break;

            case 19:
                this.hierMesh().chunkVisible("Wire_D0", false);
                this.killPilot(this, 5);
                break;

            case 13:
                this.killPilot(this, 0);
                this.killPilot(this, 1);
                return false;
        }
        return super.cutFM(i, j, actor);
    }

    protected void hitBone(String s, Shot shot, Point3d point3d) {
        if (s.startsWith("xx")) {
            if (s.startsWith("xxarmor")) if (s.endsWith("p1")) {
                if (Aircraft.v1.z > 0.5D) this.getEnergyPastArmor(5D / Aircraft.v1.z, shot);
                else if (Aircraft.v1.x > 0.93969261646270752D) this.getEnergyPastArmor(10D / Aircraft.v1.x * World.Rnd().nextFloat(1.0F, 1.2F), shot);
            } else if (s.endsWith("p2")) this.getEnergyPastArmor(5D / Math.abs(Aircraft.v1.z), shot);
            else if (s.endsWith("p3a") || s.endsWith("p3b")) this.getEnergyPastArmor(8D / Math.abs(Aircraft.v1.x) * World.Rnd().nextFloat(1.0F, 1.2F), shot);
            else if (s.endsWith("p4")) {
                if (Aircraft.v1.x > 0.70710676908493042D) this.getEnergyPastArmor(8D / Aircraft.v1.x * World.Rnd().nextFloat(1.0F, 1.2F), shot);
                else if (Aircraft.v1.x > -0.70710676908493042D) this.getEnergyPastArmor(6F, shot);
            } else if (s.endsWith("o1") || s.endsWith("o2")) if (Aircraft.v1.x > 0.70710676908493042D) this.getEnergyPastArmor(8D / Aircraft.v1.x * World.Rnd().nextFloat(1.0F, 1.2F), shot);
            else this.getEnergyPastArmor(5F, shot);
            if (s.startsWith("xxcontrols")) {
                int i = s.charAt(10) - 48;
                switch (i) {
                    default:
                        break;

                    case 1:
                    case 2:
                        if (this.getEnergyPastArmor(1.0F, shot) > 0.0F) {
                            if (World.Rnd().nextFloat() < 0.12F) {
                                this.FM.AS.setControlsDamage(shot.initiator, 1);
                                this.debuggunnery("Evelator Controls Out..");
                            }
                            if (World.Rnd().nextFloat() < 0.12F) {
                                this.FM.AS.setControlsDamage(shot.initiator, 2);
                                this.debuggunnery("Rudder Controls Out..");
                            }
                        }
                        break;

                    case 3:
                    case 4:
                        if (this.getEnergyPastArmor(1.0F, shot) > 0.0F && World.Rnd().nextFloat() < 0.25F) {
                            this.FM.AS.setControlsDamage(shot.initiator, 0);
                            this.debuggunnery("Ailerons Controls Out..");
                        }
                        break;

                    case 5:
                        if (this.getEnergyPastArmor(0.1F, shot) <= 0.0F) break;
                        if (World.Rnd().nextFloat() < 0.75F) {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 1);
                            this.debuggunnery("*** Engine1 Throttle Controls Out..");
                        }
                        if (World.Rnd().nextFloat() < 0.45F) {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 0, 6);
                            this.debuggunnery("*** Engine1 Prop Controls Out..");
                        }
                        break;

                    case 6:
                        if (this.getEnergyPastArmor(0.1F, shot) <= 0.0F) break;
                        if (World.Rnd().nextFloat() < 0.75F) {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 1, 1);
                            this.debuggunnery("*** Engine2 Throttle Controls Out..");
                        }
                        if (World.Rnd().nextFloat() < 0.45F) {
                            this.FM.AS.setEngineSpecificDamage(shot.initiator, 1, 6);
                            this.debuggunnery("*** Engine2 Prop Controls Out..");
                        }
                        break;
                }
            }
            if (s.startsWith("xxspar")) {
                if ((s.endsWith("cf1") || s.endsWith("cf2")) && World.Rnd().nextFloat() < 0.1F && this.chunkDamageVisible("CF") > 2
                        && this.getEnergyPastArmor(19.9F / (float) Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F) {
                    this.debuggunnery("*** CF Spars Broken in Half..");
                    this.msgCollision(this, "Tail1_D0", "Tail1_D0");
                    this.msgCollision(this, "WingLIn_D0", "WingLIn_D0");
                    this.msgCollision(this, "WingRIn_D0", "WingRIn_D0");
                }
                if ((s.endsWith("ta1") || s.endsWith("ta2")) && World.Rnd().nextFloat() < 0.1F && this.chunkDamageVisible("Tail1") > 2
                        && this.getEnergyPastArmor(19.9F / (float) Math.sqrt(Aircraft.v1.y * Aircraft.v1.y + Aircraft.v1.z * Aircraft.v1.z), shot) > 0.0F) {
                    this.debuggunnery("*** Tail1 Spars Broken in Half..");
                    this.msgCollision(this, "Tail1_D0", "Tail1_D0");
                }
                if ((s.endsWith("li1") || s.endsWith("li2")) && World.Rnd().nextFloat() < 1.0D - 0.92D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingLIn") > 2
                        && this.getEnergyPastArmor(17.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.debuggunnery("*** WingLIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLIn_D3", shot.initiator);
                }
                if ((s.endsWith("ri1") || s.endsWith("ri2")) && World.Rnd().nextFloat() < 1.0D - 0.92D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingRIn") > 2
                        && this.getEnergyPastArmor(17.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.debuggunnery("*** WingRIn Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRIn_D3", shot.initiator);
                }
                if ((s.endsWith("lm1") || s.endsWith("lm2")) && World.Rnd().nextFloat() < 1.0D - 0.86D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingLMid") > 2
                        && this.getEnergyPastArmor(13.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.debuggunnery("*** WingLMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLMid_D3", shot.initiator);
                }
                if ((s.endsWith("rm1") || s.endsWith("rm2")) && World.Rnd().nextFloat() < 1.0D - 0.86D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingRMid") > 2
                        && this.getEnergyPastArmor(13.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.debuggunnery("*** WingRMid Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingRMid_D3", shot.initiator);
                }
                if ((s.endsWith("lo1") || s.endsWith("lo2")) && World.Rnd().nextFloat() < 1.0D - 0.79D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingLOut") > 2
                        && this.getEnergyPastArmor(10.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.debuggunnery("*** WingLOut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingLOut_D3", shot.initiator);
                }
                if ((s.endsWith("ro1") || s.endsWith("ro2")) && World.Rnd().nextFloat() < 1.0D - 0.79D * Math.abs(Aircraft.v1.x) && this.chunkDamageVisible("WingROut") > 2
                        && this.getEnergyPastArmor(10.5F * World.Rnd().nextFloat(1.0F, 2.0F), shot) > 0.0F) {
                    this.debuggunnery("*** WingROut Spars Damaged..");
                    this.nextDMGLevels(1, 2, "WingROut_D3", shot.initiator);
                }
                if (s.endsWith("e1") && (point3d.y > 2.79D || point3d.y < 2.32D) && this.getEnergyPastArmor(18F, shot) > 0.0F) {
                    this.debuggunnery("*** Engine1 Suspension Broken in Half..");
                    this.nextDMGLevels(3, 2, "Engine1_D0", shot.initiator);
                }
                if (s.endsWith("e2") && (point3d.y < -2.79D || point3d.y > -2.32D) && this.getEnergyPastArmor(18F, shot) > 0.0F) {
                    this.debuggunnery("*** Engine2 Suspension Broken in Half..");
                    this.nextDMGLevels(3, 2, "Engine2_D0", shot.initiator);
                }
            }
            if (s.startsWith("xxbomb") && World.Rnd().nextFloat() < 0.01F && this.FM.CT.Weapons[3] != null && this.FM.CT.Weapons[3][0].haveBullets()) {
                this.debuggunnery("*** Bomb Payload Detonates..");
                if ((this.iRust == 0 || this.iRust == 1 && (s.startsWith("xxbomb2") || s.startsWith("xxbomb3")) || this.iRust == 2 && s.startsWith("xxbomb3")) && this.getEnergyPastArmor(3F, shot) > 0.0F) {
                    this.FM.AS.hitTank(shot.initiator, 0, 100);
                    this.FM.AS.hitTank(shot.initiator, 1, 100);
                    this.FM.AS.hitTank(shot.initiator, 2, 100);
                    this.FM.AS.hitTank(shot.initiator, 3, 100);
                    this.FM.AS.hitTank(shot.initiator, 4, 100);
                    this.FM.AS.hitTank(shot.initiator, 5, 100);
                    this.FM.AS.hitTank(shot.initiator, 6, 100);
                    this.FM.AS.hitTank(shot.initiator, 7, 100);
                    this.msgCollision(this, "CF_D0", "CF_D0");
                }
            }
            if (s.startsWith("xxprop")) {
                int j = 0;
                if (s.endsWith("2")) j = 1;
                if (this.getEnergyPastArmor(2.0F, shot) > 0.0F && World.Rnd().nextFloat() < 0.35F) {
                    this.FM.AS.setEngineSpecificDamage(shot.initiator, j, 3);
                    this.debuggunnery("*** Engine" + (j + 1) + " Governor Failed..");
                }
            }
            if (s.startsWith("xxengine")) {
                int k = 0;
                if (s.startsWith("xxengine2")) k = 1;
                if (s.endsWith("base")) {
                    if (this.getEnergyPastArmor(0.1F, shot) > 0.0F) {
                        if (World.Rnd().nextFloat() < shot.power / 200000F) {
                            this.FM.AS.setEngineStuck(shot.initiator, k);
                            this.debuggunnery("*** Engine" + (k + 1) + " Crank Case Hit - Engine Stucks..");
                        }
                        if (World.Rnd().nextFloat() < shot.power / 50000F) {
                            this.FM.AS.hitEngine(shot.initiator, k, 2);
                            this.debuggunnery("*** Engine" + (k + 1) + " Crank Case Hit - Engine Damaged..");
                        }
                    }
                } else if (s.endsWith("cyl")) {
                    if (this.getEnergyPastArmor(1.45F, shot) > 0.0F && World.Rnd().nextFloat() < this.FM.EI.engines[k].getCylindersRatio() * 0.5F) {
                        this.FM.EI.engines[k].setCyliderKnockOut(shot.initiator, World.Rnd().nextInt(1, (int) (shot.power / 4800F)));
                        this.debuggunnery("*** Engine" + (k + 1) + " Cylinders Hit, " + this.FM.EI.engines[k].getCylindersOperable() + "/" + this.FM.EI.engines[k].getCylinders() + " Left..");
                        if (this.FM.AS.astateEngineStates[k] < 1) {
                            this.FM.AS.hitEngine(shot.initiator, k, 1);
                            this.FM.AS.doSetEngineState(shot.initiator, k, 1);
                        }
                        if (World.Rnd().nextFloat() < shot.power / 960000F) {
                            this.FM.AS.hitEngine(shot.initiator, k, 3);
                            this.debuggunnery("*** Engine" + (k + 1) + " Cylinders Hit - Engine Fires..");
                        }
                        this.getEnergyPastArmor(25F, shot);
                    }
                } else if (s.endsWith("sup") && this.getEnergyPastArmor(0.05F, shot) > 0.0F && World.Rnd().nextFloat() < 0.89F) {
                    this.FM.AS.setEngineSpecificDamage(shot.initiator, k, 0);
                    this.debuggunnery("*** Engine" + (k + 1) + " Supercharger Out..");
                }
            }
            if (s.startsWith("xxoil")) {
                int l = 0;
                if (s.endsWith("2")) l = 1;
                if (this.getEnergyPastArmor(0.21F, shot) > 0.0F) {
                    this.FM.AS.hitOil(shot.initiator, l);
                    this.getEnergyPastArmor(0.42F, shot);
                }
            }
            if (s.startsWith("xxtank")) {
                int i1 = s.charAt(6) - 49;
                if (this.getEnergyPastArmor(1.5F, shot) > 0.0F) if (shot.power < 14100F) {
                    if (this.FM.AS.astateTankStates[i1] < 1) this.FM.AS.hitTank(shot.initiator, i1, 1);
                    if (this.FM.AS.astateTankStates[i1] < 4 && World.Rnd().nextFloat() < 0.1F) this.FM.AS.hitTank(shot.initiator, i1, 1);
                    if (shot.powerType == 3 && this.FM.AS.astateTankStates[i1] > 2 && World.Rnd().nextFloat() < 0.12F) this.FM.AS.hitTank(shot.initiator, i1, 10);
                } else this.FM.AS.hitTank(shot.initiator, i1, World.Rnd().nextInt(0, (int) (shot.power / 20000F)));
            }
            return;
        }
        if (s.startsWith("xoil")) {
            if (s.equals("xoil1")) {
                this.FM.AS.hitOil(shot.initiator, 0);
                s = "xengine1";
            }
            if (s.equals("xoil2")) {
                this.FM.AS.hitOil(shot.initiator, 1);
                s = "xengine2";
            }
        }
        if (s.startsWith("xcf") && this.chunkDamageVisible("CF") < 3) this.hitChunk("CF", shot);
        if (s.startsWith("xcf1")) {
            if (shot.power > 200000F) {
                this.FM.AS.hitPilot(shot.initiator, 0, World.Rnd().nextInt(3, 192));
                this.FM.AS.hitPilot(shot.initiator, 1, World.Rnd().nextInt(3, 192));
            }
            if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x40);
            if (point3d.x > 4.505000114440918D) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 1);
            else if (point3d.y > 0.0D) {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 4);
                if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 8);
            } else {
                this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x10);
                if (World.Rnd().nextFloat() < 0.1F) this.FM.AS.setCockpitState(shot.initiator, this.FM.AS.astateCockpitState | 0x20);
            }
        } else if (s.startsWith("xtail")) {
            if (this.chunkDamageVisible("Tail1") < 3) this.hitChunk("Tail1", shot);
        } else if (s.startsWith("xkeel")) {
            if (this.chunkDamageVisible("Keel1") < 2) this.hitChunk("Keel1", shot);
        } else if (s.startsWith("xrudder")) this.hitChunk("Rudder1", shot);
        else if (s.startsWith("xstabl")) this.hitChunk("StabL", shot);
        else if (s.startsWith("xstabr")) this.hitChunk("StabR", shot);
        else if (s.startsWith("xvatorl")) this.hitChunk("VatorL", shot);
        else if (s.startsWith("xvatorr")) this.hitChunk("VatorR", shot);
        else if (s.startsWith("xwinglin")) {
            if (this.chunkDamageVisible("WingLIn") < 3) this.hitChunk("WingLIn", shot);
        } else if (s.startsWith("xwingrin")) {
            if (this.chunkDamageVisible("WingRIn") < 3) this.hitChunk("WingRIn", shot);
        } else if (s.startsWith("xwinglmid")) {
            if (this.chunkDamageVisible("WingLMid") < 3) this.hitChunk("WingLMid", shot);
        } else if (s.startsWith("xwingrmid")) {
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
        } else if (s.startsWith("xgear")) {
            if (World.Rnd().nextFloat() < 0.1F) {
                this.debuggunnery("*** Gear Hydro Failed..");
                this.FM.Gears.setHydroOperable(false);
            }
        } else if (s.startsWith("xturret")) {
            if (s.startsWith("xturret1")) this.FM.AS.setJamBullets(10, 0);
            if (s.startsWith("xturret2")) this.FM.AS.setJamBullets(11, 0);
            if (s.startsWith("xturret3")) this.FM.AS.setJamBullets(12, 0);
            if (s.startsWith("xturret4")) this.FM.AS.setJamBullets(13, 0);
            if (s.startsWith("xturret5")) this.FM.AS.setJamBullets(14, 0);
        } else if (s.startsWith("xpilot") || s.startsWith("xhead")) {
            byte byte0 = 0;
            int j1;
            if (s.endsWith("a")) {
                byte0 = 1;
                j1 = s.charAt(6) - 49;
            } else if (s.endsWith("b")) {
                byte0 = 2;
                j1 = s.charAt(6) - 49;
            } else j1 = s.charAt(5) - 49;
            this.hitFlesh(j1, shot, byte0);
        }
    }

    public void doWoundPilot(int i, float f) {
        switch (i) {
            case 1:
                this.FM.turret[0].setHealth(f);
                break;

            case 2:
                this.FM.turret[3].setHealth(f);
                break;

            case 3:
                this.FM.turret[1].setHealth(f);
                this.FM.turret[2].setHealth(f);
                break;

            case 4:
                this.FM.turret[4].setHealth(f);
                break;

            case 5:
                this.FM.turret[5].setHealth(f);
                break;
        }
    }

    public void doMurderPilot(int i) {
        this.hierMesh().chunkVisible("Pilot" + (i + 1) + "_D0", false);
        this.hierMesh().chunkVisible("HMask" + (i + 1) + "_D0", false);
        this.hierMesh().chunkVisible("Pilot" + (i + 1) + "_D1", true);
        if (i <= 1 || i == 3) {
            if (i == 0) this.hierMesh().chunkVisible("Head1_D0", false);
            this.hierMesh().chunkVisible("Pilot" + (i + 1) + "_FAK", false);
            this.hierMesh().chunkVisible("Pilot" + (i + 1) + "_FAL", true);
        }
    }

    public void doRemoveBodyFromPlane(int i) {
        super.doRemoveBodyFromPlane(i);
        switch (i) {
            case 1:
                this.hierMesh().chunkVisible("Pilot1_FAK", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                break;

            case 2:
                this.hierMesh().chunkVisible("Pilot2_FAK", false);
                break;

            case 4:
                this.hierMesh().chunkVisible("Pilot4_FAK", false);
                break;
        }
    }

    public double Check_ASIN_ACOS(double d) {
        if (d > 1.0D) d = 1.0D;
        else if (d < -1D) d = -1D;
        return d;
    }

    public void CalculateRevi(float f, float f1) {
        float f2 = this.B1_Speed / 3.6F * (this.B1_Distance / 750F);
        float f3 = 3.675947F;
        float f4 = f;
        float f5 = 1.0F;
        if (f4 < 0.0F) f5 = -1F;
        float f6 = f1;
        float f7 = Geom.DEG2RAD(f4);
        float f8 = Geom.DEG2RAD(f6);
        double d = this.B1_Distance * Math.cos(f8);
        double d1 = d * Math.sin(f7);
        double d2 = d * Math.cos(f7);
        double d3 = this.B1_Distance * Math.sin(f8);
        double d4 = 0.0D;
        double d5 = 0.0D;
        double d6 = 0.0D;
        float f9 = 0.103622F;
        if (f4 > -90F && f4 < 90F) d4 = d2 + f3 + f2;
        else if (Math.abs(d2) < Math.abs(f3 + f2)) d4 = d2 + f3 + f2;
        else d4 = Math.abs(d2) - (f3 + f2);
        d5 = Math.sqrt(d4 * d4 + d1 * d1);
        d6 = Math.sqrt(d5 * d5 + d3 * d3);
        this.HeadTangage = Geom.RAD2DEG((float) Math.atan((f3 + f2) * (float) Math.cos(Geom.DEG2RAD(f4)) * (float) Math.sin(Geom.DEG2RAD(f6)) / d6));
        this.HeadTangage1 = f6 - this.HeadTangage;
        this.HeadYaw1 = Geom.RAD2DEG((float) Math.atan((f3 + f2) * (float) Math.sin(Geom.DEG2RAD(f4)) / d));
        this.HeadYaw = Geom.RAD2DEG((float) Math.atan((f3 + f2) * (float) Math.sin(Geom.DEG2RAD(f4)) / this.B1_Distance));
        if (f5 < 0.0F) {
            this.HeadYaw1 = -Math.abs(this.HeadYaw1);
            this.HeadYaw = -Math.abs(this.HeadYaw);
        }
        this.ReviX = f9 - f9 * (float) (d6 / this.B1_Distance);
        this.ReviY = (f3 + f2) * f9 / this.B1_Distance;
        this.ReviYaw = f4 - this.HeadYaw1;
    }

    public float getB1_HeadTangage() {
        return this.HeadTangage;
    }

    public float getB1_HeadTangage1() {
        return this.HeadTangage1;
    }

    public float getB1_HeadYaw() {
        return this.HeadYaw;
    }

    public float getB1_HeadYaw1() {
        return this.HeadYaw1;
    }

    public float getB1_ReviX() {
        return this.ReviX;
    }

    public float getB1_ReviY() {
        return this.ReviY;
    }

    public float getB1_ReviYaw() {
        return this.ReviYaw;
    }

    public boolean typeBomberToggleAutomation() {
        this.bSightAutomation = !this.bSightAutomation;
        this.bSightBombDump = false;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAutomation" + (this.bSightAutomation ? "ON" : "OFF"));
        return this.bSightAutomation;
    }

    public void typeBomberAdjDistanceReset() {
        this.fSightCurDistance = 0.0F;
        this.fSightCurForwardAngle = 0.0F;
    }

    public void typeBomberAdjDistancePlus() {
        this.fSightCurForwardAngle++;
        if (this.fSightCurForwardAngle > 85F) this.fSightCurForwardAngle = 85F;
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) this.fSightCurForwardAngle) });
        if (this.bSightAutomation) this.typeBomberToggleAutomation();
    }

    public void typeBomberAdjDistanceMinus() {
        this.fSightCurForwardAngle--;
        if (this.fSightCurForwardAngle < 0.0F) this.fSightCurForwardAngle = 0.0F;
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) this.fSightCurForwardAngle) });
        if (this.bSightAutomation) this.typeBomberToggleAutomation();
    }

    public void typeBomberAdjSideslipReset() {
        this.fSightCurSideslip = 0.0F;
    }

    public void typeBomberAdjSideslipPlus() {
        this.fSightCurSideslip += 0.05F;
        if (this.fSightCurSideslip > 3F) this.fSightCurSideslip = 3F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Float(this.fSightCurSideslip * 10F) });
    }

    public void typeBomberAdjSideslipMinus() {
        this.fSightCurSideslip -= 0.05F;
        if (this.fSightCurSideslip < -3F) this.fSightCurSideslip = -3F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Float(this.fSightCurSideslip * 10F) });
    }

    public void typeBomberAdjAltitudeReset() {
        this.fSightCurAltitude = 850F;
    }

    public void typeBomberAdjAltitudePlus() {
        this.fSightCurAltitude += 10F;
        if (this.fSightCurAltitude > 10000F) this.fSightCurAltitude = 10000F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) this.fSightCurAltitude) });
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
    }

    public void typeBomberAdjAltitudeMinus() {
        this.fSightCurAltitude -= 10F;
        if (this.fSightCurAltitude < 850F) this.fSightCurAltitude = 850F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) this.fSightCurAltitude) });
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
    }

    public void typeBomberAdjSpeedReset() {
        this.fSightCurSpeed = 150F;
    }

    public void typeBomberAdjSpeedPlus() {
        this.fSightCurSpeed += 10F;
        if (this.fSightCurSpeed > 600F) this.fSightCurSpeed = 600F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberAdjSpeedMinus() {
        this.fSightCurSpeed -= 10F;
        if (this.fSightCurSpeed < 150F) this.fSightCurSpeed = 150F;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberUpdate(float f) {
        if (Math.abs(this.FM.Or.getKren()) > 4.5D) {
            this.fSightCurReadyness -= 0.0666666F * f;
            if (this.fSightCurReadyness < 0.0F) this.fSightCurReadyness = 0.0F;
        }
        if (this.fSightCurReadyness < 1.0F) this.fSightCurReadyness += 0.0333333F * f;
        else if (this.bSightAutomation) {
            this.fSightCurDistance -= this.fSightCurSpeed / 3.6F * f;
            if (this.fSightCurDistance < 0.0F) {
                this.fSightCurDistance = 0.0F;
                this.typeBomberToggleAutomation();
            }
            this.fSightCurForwardAngle = (float) Math.toDegrees(Math.atan(this.fSightCurDistance / this.fSightCurAltitude));
            this.calibDistance = this.fSightCurSpeed / 3.6F * Aircraft.floatindex(Aircraft.cvt(this.fSightCurAltitude, 0.0F, 7000F, 0.0F, 7F), calibrationScale);
            if (this.fSightCurDistance < this.calibDistance + this.fSightCurSpeed / 3.6F * Math.sqrt(this.fSightCurAltitude * (2F / 9.81F))) this.bSightBombDump = true;
            if (this.bSightBombDump) if (this.FM.isTick(3, 0)) {
                if (this.FM.CT.Weapons[3] != null && this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1] != null && this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1].haveBullets()) {
                    this.FM.CT.WeaponControl[3] = true;
                    HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightBombdrop");
                }
            } else this.FM.CT.WeaponControl[3] = false;
        }
    }

    public void typeBomberReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
        netmsgguaranted.writeByte((this.bSightAutomation ? 1 : 0) | (this.bSightBombDump ? 2 : 0));
        netmsgguaranted.writeFloat(this.fSightCurDistance);
        netmsgguaranted.writeByte((int) this.fSightCurForwardAngle);
        netmsgguaranted.writeByte((int) ((this.fSightCurSideslip + 3F) * 33.33333F));
        netmsgguaranted.writeFloat(this.fSightCurAltitude);
        netmsgguaranted.writeByte((int) (this.fSightCurSpeed / 2.5F));
        netmsgguaranted.writeByte((int) (this.fSightCurReadyness * 200F));
    }

    public void typeBomberReplicateFromNet(NetMsgInput netmsginput) throws IOException {
        int i = netmsginput.readUnsignedByte();
        this.bSightAutomation = (i & 1) != 0;
        this.bSightBombDump = (i & 2) != 0;
        this.fSightCurDistance = netmsginput.readFloat();
        this.fSightCurForwardAngle = netmsginput.readUnsignedByte();
        this.fSightCurSideslip = -3F + netmsginput.readUnsignedByte() / 33.33333F;
        this.fSightCurAltitude = netmsginput.readFloat();
        this.fSightCurSpeed = netmsginput.readUnsignedByte() * 2.5F;
        this.fSightCurReadyness = netmsginput.readUnsignedByte() / 200F;
    }

    public static boolean checkTurretVisibilityPlaneBlocking(int turretIndex) {
        return false;
    }

    public float       fSightCurDistance;
    public float       fSightCurForwardAngle;
    public float       fSightCurSideslip;
    public float       fSightCurAltitude;
    public float       fSightCurSpeed;
    public float       fSightCurReadyness;
    public boolean     bSightAutomation;
    private boolean    bSightBombDump;
    private float      calibDistance;
    static final float calibrationScale[] = { 0.0F, 0.2F, 0.4F, 0.66F, 0.86F, 1.05F, 1.2F, 1.6F };
    public boolean     bElectroSound;

    protected boolean  isGuidingBomb;
    private boolean    isVentralGunActive;
    private float      radiators[]        = { 0.0F, 0.0F };
    private float      minElev;
    private float      f_tmp;
    public float       B1_Distance;
    public float       B1_Speed;
    protected float    HeadTangage;
    protected float    HeadTangage1;
    protected float    HeadYaw;
    protected float    HeadYaw1;
    protected float    ReviY;
    protected float    ReviX;
    protected float    ReviYaw;
    public boolean     bAirEnemy;
    public int         iRust;

    static {
        Class class1 = HE_177_TD.class;
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
    }
}
