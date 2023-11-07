package com.maddox.il2.objects.air;

import java.io.IOException;
import java.util.ArrayList;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.World;
import com.maddox.il2.ai.air.Pilot;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Orientation;
import com.maddox.il2.fm.FMMath;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Selector;
import com.maddox.il2.objects.bridges.BridgeSegment;
import com.maddox.il2.objects.electronics.RadarLiSN2;
import com.maddox.il2.objects.humans.Soldier;
import com.maddox.il2.objects.sounds.Voice;
import com.maddox.il2.objects.weapons.RocketGunX4;
import com.maddox.il2.objects.weapons.RocketGunX4R;
import com.maddox.il2.objects.weapons.RocketGunX4homing;
import com.maddox.il2.objects.weapons.RocketGunX7;
import com.maddox.il2.objects.weapons.RocketGunX7homing;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class GO_229A3NJ extends GO_229 implements TypeFighterAceMaker, TypeFighter, TypeBNZFighter, TypeX4Carrier, TypeStormovik, TypeRadarLiSN2Carrier {

    public GO_229A3NJ() {
        this.tX4Prev = 0L;
        this.deltaAzimuth = 0.0F;
        this.deltaTangage = 0.0F;
//        this.X4check = false;
        this.missilesList = new ArrayList();
        this.homing = false;
        this.X7 = false;
        this.IR = false;
        this.k14Mode = 0;
        this.k14WingspanType = 0;
        this.k14Distance = 200F;
        this.k14Auto = 0;
        this.k14IFFmode = 1;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.missilesList.clear();
        this.createMissilesList();
    }

    public void createMissilesList() {
        for (int i = 0; i < this.FM.CT.Weapons.length; i++) {
            if (this.FM.CT.Weapons[i] != null) {
                for (int j = 0; j < this.FM.CT.Weapons[i].length; j++) {
                    if (this.FM.CT.Weapons[i][j] instanceof RocketGunX4) {
                        if ((this.FM.CT.Weapons[i][j] instanceof RocketGunX4homing) || (this.FM.CT.Weapons[i][j] instanceof RocketGunX7homing)) {
                            this.homing = true;
                        }
                        if (this.FM.CT.Weapons[i][j] instanceof RocketGunX7) {
                            this.X7 = true;
                        }
                        if (this.FM.CT.Weapons[i][j] instanceof RocketGunX4R) {
                            this.IR = true;
                        }
                        this.missilesList.add(this.FM.CT.Weapons[i][j]);
                    }
                }

            }
        }

    }

    public void launchMsl() {
        if (this.missilesList.isEmpty()) {
            return;
        }
         ((RocketGunX4) this.missilesList.remove(0)).shots(1);
         this.tX4Prev = Time.current();
         Voice.speakAttackByRockets(this);
    }

    public void update(float f) {
        super.update(f);
        if (this == World.getPlayerAircraft()) {
            this.typeFighterAceMakerRangeFinder();
        }
//        if (((this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) || !(this.FM instanceof Pilot)) {
//            return;
//        }
//        System.out.println("get_maneuver=" + ((Pilot)this.FM).get_maneuver());
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (((this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) || !flag || !(this.FM instanceof Pilot)) {
            return;
        }
        if (this.missilesList.isEmpty()) {
            return;
        }
        if (Time.current() > (this.tX4Prev + (!this.homing && !this.IR ? 500L : 10000L))) {
            Pilot pilot = (Pilot) this.FM;
            if (((pilot.get_maneuver() == 27) || (pilot.get_maneuver() == 62) || (pilot.get_maneuver() == 63)) && (pilot.target != null)) {
                Point3d point3d = new Point3d(pilot.target.Loc);
                point3d.sub(this.FM.Loc);
                this.FM.Or.transformInv(point3d);
                if (this.homing && !this.X7) {
                    if (pilot.get_maneuver() != 63 ? (point3d.x > (this.FM.CT.Weapons[0][1].countBullets() <= 20 ? 350D : 800D)) && (point3d.x < (3500D + (500D * this.FM.Skill))) : ((point3d.x > 7000D) && (point3d.x < 10000D)) || ((point3d.x > 350D) && (point3d.x < 9000D) && (World.Rnd().nextFloat() < 0.33F))) {
                        double d = Math.pow(point3d.x / 5000D, 2D) * 5000D;
                        if ((point3d.y < d) && (point3d.y > -d) && (point3d.z < d) && (point3d.z > -d) && ((pilot.get_maneuver() != 63) || (point3d.x < 7000D) || (((point3d.y * 2D) < point3d.x) && ((point3d.y * 2D) > -point3d.x) && ((point3d.z * 2D) < point3d.x) && ((point3d.z * 2D) > -point3d.x)))) {
                            Orientation orientation2 = new Orientation();
                            Orientation orientation3 = new Orientation();
                            this.FM.getOrient(orientation2);
                            pilot.target.getOrient(orientation3);
                            float f3 = Math.abs(orientation2.getAzimut() - orientation3.getAzimut()) % 360F;
                            f3 = f3 <= 180F ? f3 : 360F - f3;
                            f3 = f3 <= 90F ? f3 : 180F - f3;
                            float f4 = Math.abs(orientation2.getTangage() - orientation3.getTangage()) % 360F;
                            f4 = f4 <= 180F ? f4 : 360F - f4;
                            f4 = f4 <= 90F ? f4 : 180F - f4;
                            double d2 = (point3d.x * (5 - this.FM.Skill)) / (pilot.target.getSpeed() + 1.0F);
                            if ((f3 < d2) && (f4 < d2)) {
                                this.launchMsl();
                            }
                        }
                    }
                } else if ((pilot.get_maneuver() != 63 ? (point3d.x > (this.FM.CT.Weapons[0][1].countBullets() <= 20 ? 350D : 800D)) && (point3d.x < (2000D + (1000D * this.FM.Skill))) : ((point3d.x > 4000D) && (point3d.x < 5500D)) || ((point3d.x > 100D) && (point3d.x < 5000D) && (World.Rnd().nextFloat() < 0.33F))) && (((point3d.y * 1.5D) < point3d.x) && ((point3d.y * 1.5D) > -point3d.x) && ((point3d.z * 2D) < point3d.x) && ((point3d.z * 2D) > -point3d.x))) {
                    Orientation orientation = new Orientation();
                    Orientation orientation1 = new Orientation();
                    this.FM.getOrient(orientation);
                    pilot.target.getOrient(orientation1);
                    float f1 = Math.abs(orientation.getAzimut() - orientation1.getAzimut()) % 360F;
                    f1 = f1 <= 180F ? f1 : 360F - f1;
                    f1 = f1 <= 90F ? f1 : 180F - f1;
                    float f2 = Math.abs(orientation.getTangage() - orientation1.getTangage()) % 360F;
                    f2 = f2 <= 180F ? f2 : 360F - f2;
                    f2 = f2 <= 90F ? f2 : 180F - f2;
                    double d1 = (point3d.x * (4.5D - this.FM.Skill)) / (pilot.target.getSpeed() + 1.0F);
                    if ((f1 < d1) && (f2 < d1)) {
                        double d3 = FMMath.RAD2DEG(Math.atan(Math.sqrt(Math.pow(Math.tan(FMMath.DEG2RAD(f1)), 2D) + Math.pow(Math.tan(FMMath.DEG2RAD(f2)), 2D))));
                        Vector3d vector3d = pilot.target.getAccel();
                        double d4 = Math.sqrt(Math.pow(vector3d.x, 2D) + Math.pow(vector3d.y, 2D) + Math.pow(vector3d.z, 2D));
                        double d5 = (90D - d3) / (9 + this.FM.Skill);
                        if (d4 < d5) {
                            this.launchMsl();
                        }
                    }
                }
            } else if ((pilot.get_maneuver() == 43) && (pilot.target_ground != null) && (this.X7 || (!this.IR && !this.homing)) && ((!(pilot.target_ground instanceof Soldier) && !(pilot.target_ground instanceof BridgeSegment)) || !this.homing)) {
                Point3d point3d1 = new Point3d();
                pilot.target_ground.pos.getAbs(point3d1);
                point3d1.sub(this.FM.Loc);
                this.FM.Or.transformInv(point3d1);
                if ((point3d1.x > 400D) && (point3d1.x < ((this.homing ? 4250D : this.IR ? 2250D : 1250D) + (250D * this.FM.Skill)))) {
                    if (!this.homing || !this.IR) {
                        point3d1.x /= 2 - (this.FM.Skill / 3);
                    }
                    if (this.homing ? ((point3d1.y * 5D) < point3d1.x) && ((point3d1.y * 5D) > -point3d1.x) && ((point3d1.z * 5D) < point3d1.x) && ((point3d1.z * 5D) > -point3d1.x) : (point3d1.y < point3d1.x) && (point3d1.y > -point3d1.x) && ((point3d1.z * 1.5D) < point3d1.x) && ((point3d1.z * 2D) > -point3d1.x)) {
                        this.launchMsl();
                    }
                }
            }
        }
    }

    public void typeX4CAdjAttitudePlus() {
        this.deltaTangage = 1.0F;
        this.radarGain += 10;
        if (this.radarGain > 100) {
            this.radarGain = 100;
        }
    }

    public void typeX4CAdjAttitudeMinus() {
        this.deltaTangage = -1F;
        this.radarGain -= 10;
        if (this.radarGain < 0) {
            this.radarGain = 0;
        }
    }

    public void typeX4CAdjSidePlus() {
        this.deltaAzimuth = 1.0F;
//        if (this.X4check) {
//            this.X4check = false;
//        } else if ((this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) {
//            this.typeFighterAceMakerAdjSideslipPlus();
//        }
        this.typeFighterAceMakerAdjSideslipPlus();
        this.radarMode++;
        if (this.radarMode > RadarLiSN2.RADAR_MODE_SHORT) {
            this.radarMode = RadarLiSN2.RADAR_MODE_NORMAL;
        }
        this.typeFighterAceMakerAdjSideslipPlus();
    }

    public void typeX4CAdjSideMinus() {
        this.deltaAzimuth = -1F;
//        if (this.X4check) {
//            this.X4check = false;
//        } else if ((this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) {
//            this.typeFighterAceMakerAdjSideslipMinus();
//        }
        this.typeFighterAceMakerAdjSideslipMinus();
        this.radarMode--;
        if (this.radarMode < RadarLiSN2.RADAR_MODE_NORMAL) {
            this.radarMode = RadarLiSN2.RADAR_MODE_SHORT;
        }
        this.typeFighterAceMakerAdjSideslipMinus();
    }

    public void typeX4CResetControls() {
        this.deltaAzimuth = this.deltaTangage = 0.0F;
//        this.tX4Prev = Time.current();
//        this.X4check = true;
        this.radarGain = 50;
    }

    public float typeX4CgetdeltaAzimuth() {
        return this.deltaAzimuth;
    }

    public float typeX4CgetdeltaTangage() {
        return this.deltaTangage;
    }

    public boolean typeFighterAceMakerToggleAutomation() {
        this.k14Auto++;
        if (this.k14Auto > 2) {
            this.k14Auto = 0;
        }
        this.k14Mode = this.k14Auto;
        HUD.log(AircraftHotKeys.hudLogWeaponId, "K14AceMakerMode" + this.k14Auto);
        return true;
    }

    public void typeFighterAceMakerAdjDistanceReset() {
    }

    public void typeFighterAceMakerAdjDistancePlus() {
        this.k14IFFmode++;
        if (this.k14IFFmode > 2) {
            this.k14IFFmode = 0;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "IFF " + k14IFFmodeStr[this.k14IFFmode]);
    }

    public void typeFighterAceMakerAdjDistanceMinus() {
        this.k14IFFmode--;
        if (this.k14IFFmode < 0) {
            this.k14IFFmode = 2;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "IFF " + k14IFFmodeStr[this.k14IFFmode]);
    }

    public void typeFighterAceMakerAdjSideslipReset() {
    }

    public void typeFighterAceMakerAdjSideslipPlus() {
        this.k14WingspanType--;
        if (this.k14WingspanType < 0) {
            this.k14WingspanType = 0;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "AskaniaWing" + this.k14WingspanType);
    }

    public void typeFighterAceMakerAdjSideslipMinus() {
        this.k14WingspanType++;
        if (this.k14WingspanType > 10) {
            this.k14WingspanType = 10;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "AskaniaWing" + this.k14WingspanType);
    }

    public void typeFighterAceMakerRangeFinder() {
        if (this.k14Auto == 0) {
            return;
        }
        if (this.k14IFFmode == 2) {
            if ((trgt = Selector.look(true, false, Main3D.cur3D()._camera3D[Main3D.cur3D().getRenderIndx()], this.getArmy(), -1, this, false)) != null) {
                this.k14Mode = this.k14Auto;
                this.k14Distance = (float) this.pos.getAbsPoint().distance(trgt.pos.getAbsPoint());
                if (this.k14Distance > 800F) {
                    this.k14Distance = 800F;
                } else if (this.k14Distance < 100F) {
                    this.k14Distance = 100F;
                }
            } else {
                this.k14Mode = 0;
            }
        } else if ((trgt = Selector.look(true, false, Main3D.cur3D()._camera3D[Main3D.cur3D().getRenderIndx()], -1, -1, this, false)) != null) {
            if ((this.k14IFFmode == 1) && (trgt.getArmy() == this.getOwner().getArmy())) {
                this.k14Mode = 0;
            } else {
                this.k14Mode = this.k14Auto;
                this.k14Distance = (float) this.pos.getAbsPoint().distance(trgt.pos.getAbsPoint());
                if (this.k14Distance > 800F) {
                    this.k14Distance = 800F;
                } else if (this.k14Distance < 100F) {
                    this.k14Distance = 100F;
                }
            }
        } else {
            this.k14Mode = this.k14Auto;
        }
    }

    public void typeFighterAceMakerReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
        netmsgguaranted.writeByte(this.k14Mode);
        netmsgguaranted.writeByte(this.k14WingspanType);
        netmsgguaranted.writeFloat(this.k14Distance);
    }

    public void typeFighterAceMakerReplicateFromNet(NetMsgInput netmsginput) throws IOException {
        this.k14Mode = netmsginput.readByte();
        this.k14WingspanType = netmsginput.readByte();
        this.k14Distance = netmsginput.readFloat();
    }

    // +++ RadarLiSN2Carrier +++
    public void setCurPilot(int theCurPilot) {
        System.out.println("### Attempt to set Pilot Index on single crew plane!!! ###");
    }

    public int getCurPilot() {
        return 1;
    }

    public int getRadarGain() {
        return this.radarGain;
    }

    public int getRadarMode() {
        return this.radarMode;
    }

    private int           radarGain       = 50;
    private int           radarMode       = RadarLiSN2.RADAR_MODE_NORMAL;;
    // --- RadarLiSN2Carrier ---

    private long          tX4Prev;
    private float         deltaAzimuth;
    private float         deltaTangage;
//    private boolean       X4check;
    private ArrayList     missilesList;
    private boolean       homing;
    private boolean       X7;
    private boolean       IR;
    public int            k14Mode;
    public int            k14WingspanType;
    public float          k14Distance;
    public int            k14Auto;
    public int            k14IFFmode;
    private static String k14IFFmodeStr[] = { "NO check", "check FRIEND", "check FOE" };
    private static Actor  trgt            = null;

    static {
        Class class1 = GO_229A3NJ.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Go-229");
        Property.set(class1, "meshName", "3do/Plane/Go-229A3NJ/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1946.5F);
        Property.set(class1, "yearExpired", 1999F);
        Property.set(class1, "FlightModel", "FlightModels/Ho-229A3NJ.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitGO_229A3NJ.class });
//        Property.set(class1, "LOSElevation", 0.51305F);
        Property.set(class1, "LOSElevation", 1.2158F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 1, 1, 9, 9, 9, 9, 2, 2, 2, 2, 2, 2, 2, 2, 9, 9, 9, 9, 2, 2, 2, 2 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalRock01", "_ExternalRock01", "_ExternalRock02", "_ExternalRock02", "_ExternalRock03", "_ExternalRock03", "_ExternalRock04", "_ExternalRock04", "_ExternalDev1", "_ExternalDev2", "_ExternalDev3", "_ExternalDev4", "_ExternalRock05", "_ExternalRock05", "_ExternalRock06", "_ExternalRock06" });
    }
}
