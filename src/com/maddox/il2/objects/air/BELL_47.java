package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.il2.game.HUD;
import com.maddox.rts.Property;

public class BELL_47 extends Scheme1 implements TypeScout, TypeTransport {

    public BELL_47() {
        this.suka = new Loc();
        this.pictVBrake = 0.0F;
        this.pictAileron = 0.0F;
        this.pictVator = 0.0F;
        this.pictRudder = 0.0F;
        this.pictBlister = 0.0F;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("30Cal_MGR", !thisWeaponsName.equals("none"));
        hierMesh.chunkVisible("30Cal_MGL", !thisWeaponsName.equals("none"));
    }
    
    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        for (int i = 1; i < 3; i++)
            if (this.FM.getAltitude() < 3000F) this.hierMesh().chunkVisible("HMask" + i + "_D0", false);
            else this.hierMesh().chunkVisible("HMask" + i + "_D0", this.hierMesh().isChunkVisible("Pilot" + i + "_D0"));

    }

    protected void moveAileron(float f) {
    }

    protected void moveElevator(float f) {
    }

    protected void moveFlap(float f) {
    }

    protected void moveRudder(float f) {
    }

    public static void moveGear(HierMesh hiermesh, float f) {
    }

    protected void moveGear(float f) {
        moveGear(this.hierMesh(), f);
    }

    public void moveSteering(float f) {
    }

    public void moveWheelSink() {
        this.resetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.5F, 0.0F, 0.5F);
        this.hierMesh().chunkSetLocate("GearL3_D0", Aircraft.xyz, Aircraft.ypr);
        float f = Aircraft.cvt(this.FM.Gears.gWheelSinking[0], 0.0F, 0.5F, 0.0F, 5F);
        this.hierMesh().chunkSetAngles("GearL2_D0", 0.0F, floatindex(f, gearL2), 0.0F);
        this.hierMesh().chunkSetAngles("GearL4_D0", 0.0F, floatindex(f, gearL4), 0.0F);
        this.hierMesh().chunkSetAngles("GearL5_D0", 0.0F, floatindex(f, gearL5), 0.0F);
        Aircraft.xyz[2] = Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.5F, 0.0F, 0.5F);
        this.hierMesh().chunkSetLocate("GearR3_D0", Aircraft.xyz, Aircraft.ypr);
        f = Aircraft.cvt(this.FM.Gears.gWheelSinking[1], 0.0F, 0.5F, 0.0F, 5F);
        this.hierMesh().chunkSetAngles("GearR2_D0", 0.0F, -floatindex(f, gearL2), 0.0F);
        this.hierMesh().chunkSetAngles("GearR4_D0", 0.0F, -floatindex(f, gearL4), 0.0F);
        this.hierMesh().chunkSetAngles("GearR5_D0", 0.0F, -floatindex(f, gearL5), 0.0F);
    }

    public void msgShot(Shot shot) {
        this.setShot(shot);
        if (shot.chunkName.startsWith("WingLMid") && World.Rnd().nextFloat(0.0F, 0.121F) < shot.mass) this.FM.AS.hitTank(shot.initiator, 0, (int) (1.0F + shot.mass * 18.95F * 2.0F));
        if (shot.chunkName.startsWith("WingRMid") && World.Rnd().nextFloat(0.0F, 0.121F) < shot.mass) this.FM.AS.hitTank(shot.initiator, 1, (int) (1.0F + shot.mass * 18.95F * 2.0F));
        if (shot.chunkName.startsWith("Engine")) {
            if (World.Rnd().nextFloat(0.0F, 1.0F) < shot.mass) this.FM.AS.hitEngine(shot.initiator, 0, 1);
            if (Aircraft.v1.z > 0.0D && World.Rnd().nextFloat() < 0.12F) {
                this.FM.AS.setEngineDies(shot.initiator, 0);
                if (shot.mass > 0.1F) this.FM.AS.hitEngine(shot.initiator, 0, 5);
            }
            if (Aircraft.v1.x < 0.1D && World.Rnd().nextFloat() < 0.57F) this.FM.AS.hitOil(shot.initiator, 0);
        }
        if (shot.chunkName.startsWith("Pilot1")) {
            this.killPilot(shot.initiator, 0);
            this.FM.setCapableOfBMP(false, shot.initiator);
            if (Aircraft.Pd.z > 0.5D && shot.initiator == World.getPlayerAircraft() && World.cur().isArcade()) HUD.logCenter("H E A D S H O T");
        } else if (shot.chunkName.startsWith("Pilot2")) {
            this.killPilot(shot.initiator, 1);
            if (Aircraft.Pd.z > 0.5D && shot.initiator == World.getPlayerAircraft() && World.cur().isArcade()) HUD.logCenter("H E A D S H O T");
        } else {
            if (shot.chunkName.startsWith("Turret")) this.FM.turret[0].bIsOperable = false;
            if (this.FM.AS.astateEngineStates[0] == 4 && World.Rnd().nextInt(0, 99) < 33) this.FM.setCapableOfBMP(false, shot.initiator);
            super.msgShot(shot);
        }
    }

    protected boolean cutFM(int i, int j, Actor actor) {
        switch (i) {
            case 34:
                return super.cutFM(35, j, actor);

            case 37:
                return super.cutFM(38, j, actor);
        }
        return super.cutFM(i, j, actor);
    }

    public void doKillPilot(int i) {
        if (i == 1) this.FM.turret[0].bIsOperable = false;
    }

    public void doMurderPilot(int i) {
        switch (i) {
            default:
                break;

            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                this.hierMesh().chunkVisible("HMask1_D0", false);
                if (!this.FM.AS.bIsAboutToBailout) this.hierMesh().chunkVisible("Gore1_D0", true);
                break;

            case 1:
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                this.hierMesh().chunkVisible("HMask2_D0", false);
                if (!this.FM.AS.bIsAboutToBailout) this.hierMesh().chunkVisible("Gore2_D0", true);
                break;
        }
    }

    protected void moveFan(float f) {
        int i = 0;
        int j = 0;
        int k = 1;
        if (this.oldProp[j] < 2) {
            i = Math.abs((int) (this.FM.EI.engines[0].getw() * 0.06F));
            if (i >= 1) i = 1;
            if (i != this.oldProp[j] && this.hierMesh().isChunkVisible(Props[j][this.oldProp[j]])) {
                this.hierMesh().chunkVisible(Props[0][this.oldProp[j]], false);
                this.oldProp[j] = i;
                this.hierMesh().chunkVisible(Props[j][i], true);
            }
        }
        if (i == 0) this.propPos[j] = (this.propPos[j] + 57.3F * this.FM.EI.engines[0].getw() * f) % 360F;
        else {
            float f1 = 57.3F * this.FM.EI.engines[0].getw();
            f1 %= 2880F;
            f1 /= 2880F;
            if (f1 <= 0.5F) f1 *= 2.0F;
            else f1 = f1 * 2.0F - 2.0F;
            f1 *= 1200F;
            this.propPos[j] = (this.propPos[j] + f1 * f) % 360F;
        }
        this.hierMesh().chunkSetAngles(Props[j][0], 0.0F, -this.propPos[j], 0.0F);
        if (this.oldProp[k] < 2) {
            i = Math.abs((int) (this.FM.EI.engines[0].getw() * 0.1F));
            if (i >= 1) i = 1;
            if (i != this.oldProp[k] && this.hierMesh().isChunkVisible(Props[k][this.oldProp[k]])) {
                this.hierMesh().chunkVisible(Props[k][this.oldProp[k]], false);
                this.oldProp[k] = i;
                this.hierMesh().chunkVisible(Props[k][i], true);
            }
        }
        if (i == 0) this.propPos[k] = (this.propPos[k] + 57.3F * (this.FM.EI.engines[0].getw() * 6F) * f) % 360F;
        else {
            float f2 = 57.3F * (this.FM.EI.engines[0].getw() * 6F);
            f2 %= 2880F;
            f2 /= 2880F;
            if (f2 <= 0.5F) f2 *= 2.0F;
            else f2 = f2 * 2.0F - 2.0F;
            f2 *= 1200F;
            this.propPos[k] = (this.propPos[k] + f2 * f) % 360F;
        }
        this.hierMesh().chunkSetAngles(Props[k][0], 0.0F, -this.propPos[k], 0.0F);
    }

    private void stability() {
        double d = 0.0D;
        if (this.FM.getSpeedKMH() > this.FM.VmaxFLAPS) d = (this.FM.getSpeedKMH() - this.FM.VmaxFLAPS) / 10F;
        Point3d point3d = new Point3d(0.0D, 0.0D, 0.0D);
        point3d.x = 0.0D - (this.FM.Or.getTangage() / 10F - this.FM.CT.getElevator() * 2.5D);
        point3d.y = 0.0D - (this.FM.Or.getKren() / 10F - this.FM.CT.getAileron() * 2.5D) + d / 3D;
        point3d.z = 2D;
        this.FM.EI.engines[0].setPropPos(point3d);
        this.FM.producedAF.x += 7000D * (-this.FM.CT.getElevator() * this.FM.EI.engines[0].getPowerOutput());
        this.FM.producedAF.y += 6000D * (-this.FM.CT.getAileron() * this.FM.EI.engines[0].getPowerOutput());
    }

    public void update(float f) {
        this.stability();
        boolean flag = false;
        super.update(f);
        float f1 = this.FM.CT.getAirBrake();
        f1 = this.FM.CT.getAileron();
        if (Math.abs(this.pictAileron - f1) > 0.01F) {
            this.pictAileron = f1;
            flag = true;
        }
        f1 = this.FM.CT.getRudder();
        if (Math.abs(this.pictRudder - f1) > 0.01F) {
            this.pictRudder = f1;
            flag = true;
        }
        f1 = this.FM.CT.getElevator();
        if (Math.abs(this.pictVator - f1) > 0.01F) {
            this.pictVator = f1;
            flag = true;
        }
        if (flag) {
            for (int i = 0; i < 9; i++) {
                float f3 = -60F * this.pictVBrake * (fcA[i] * this.pictAileron + fcE[i] * this.pictVator + fcR[i] * this.pictRudder);
                this.hierMesh().chunkSetAngles("Flap0" + (i + 1) + "B_D0", f3, 0.0F, 0.0F);
            }

            this.hierMesh().chunkSetAngles("AroneL_D0", 0.0F, 45F * this.pictAileron, 0.0F);
            this.hierMesh().chunkSetAngles("AroneR_D0", 0.0F, 45F * this.pictAileron, 0.0F);
            this.hierMesh().chunkSetAngles("Rudder1_D0", 20F * this.pictRudder, 0.0F, 0.0F);
            this.hierMesh().chunkSetAngles("VatorL_D0", -20F * this.pictVator, 0.0F, 0.0F);
            this.hierMesh().chunkSetAngles("VatorR_D0", 20F * this.pictVator, 0.0F, 0.0F);
        }
        if (this.FM.AS.astateBailoutStep > 1) {
            if (this.pictBlister < 1.0F) this.pictBlister += 3F * f;
            this.hierMesh().chunkSetAngles("Blister1_D0", -110F * this.pictBlister, 0.0F, 0.0F);
        }
        float f2 = this.FM.EI.getPowerOutput() * Aircraft.cvt(this.FM.getSpeedKMH(), 0.0F, 600F, 2.0F, 0.0F);
        if (this.FM.CT.getAirBrake() > 0.5F) {
            if (this.FM.Or.getTangage() > 5F) {
                this.FM.getW().scale(Aircraft.cvt(this.FM.Or.getTangage(), 45F, 90F, 1.0F, 0.1F));
                float f4 = this.FM.Or.getTangage();
                if (Math.abs(this.FM.Or.getKren()) > 90F) f4 = 90F + (90F - f4);
                float f5 = f4 - 90F;
                this.FM.CT.trimElevator = Aircraft.cvt(f5, -20F, 20F, 0.5F, -0.5F);
                f5 = this.FM.Or.getKren();
                if (Math.abs(f5) > 90F) if (f5 > 0.0F) f5 = 180F - f5;
                else f5 = -180F - f5;
                this.FM.CT.trimAileron = Aircraft.cvt(f5, -20F, 20F, 0.5F, -0.5F);
                this.FM.CT.trimRudder = Aircraft.cvt(f5, -15F, 15F, 0.04F, -0.04F);
            }
        } else {
            this.FM.CT.trimAileron = 0.0F;
            this.FM.CT.trimElevator = 0.0F;
            this.FM.CT.trimRudder = 0.0F;
        }
        ((FlightModelMain) this.FM).Or.increment(f2 * (((FlightModelMain) this.FM).CT.getRudder() + this.FM.CT.getTrimRudderControl()), f2 * (this.FM.CT.getElevator() + this.FM.CT.getTrimElevatorControl()),
                f2 * (this.FM.CT.getAileron() + this.FM.CT.getTrimAileronControl()));
    }

    public static boolean         bChangedPit = false;
    public Loc                    suka;
    private float                 pictVBrake;
    private float                 pictAileron;
    private float                 pictVator;
    private float                 pictRudder;
    private float                 pictBlister;
    private static final float    fcA[]       = { 0.0F, 0.04F, 0.1F, 0.04F, 0.02F, -0.02F, -0.04F, -0.1F, -0.04F };
    private static final float    fcE[]       = { 0.98F, 0.48F, 0.1F, -0.48F, -0.7F, -0.7F, -0.48F, 0.1F, 0.48F };
    private static final float    fcR[]       = { 0.02F, 0.48F, 0.8F, 0.48F, 0.28F, -0.28F, -0.48F, -0.8F, -0.48F };
    private static final float    gearL2[]    = { 0.0F, 1.0F, 2.0F, 2.9F, 3.2F, 3.35F };
    private static final float    gearL4[]    = { 0.0F, 7.5F, 15F, 22F, 29F, 35.5F };
    private static final float    gearL5[]    = { 0.0F, 1.5F, 4F, 7.5F, 10F, 11.5F };
//    protected int                 oldProp[]   = { 0, 0, 0, 0, 0, 0 };
    protected static final String Props[][]   = { { "Prop1_D0", "PropRot1_D0", "Prop1_D1" }, { "Prop2_D0", "PropRot2_D0", "Prop2_D1" }, { "Prop3_D0", "PropRot3_D0", "Prop3_D1" }, { "Prop4_D0", "PropRot4_D0", "Prop4_D1" },
            { "Prop5_D0", "PropRot5_D0", "Prop5_D1" }, { "Prop6_D0", "PropRot6_D0", "Prop6_D1" } };

    static {
        Class class1 = BELL_47.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Bell47");
        Property.set(class1, "meshName", "3do/plane/Bell47/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar02());
        Property.set(class1, "yearService", 1935F);
        Property.set(class1, "yearExpired", 1960F);
        Property.set(class1, "FlightModel", "FlightModels/Bell47.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitBELL_47.class });
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 0, 0 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04" });
    }
}
