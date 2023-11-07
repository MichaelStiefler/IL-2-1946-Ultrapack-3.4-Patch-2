package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorNet;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.AircraftHotKeys;
import com.maddox.il2.game.HUD;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.NetObj;
import com.maddox.rts.Property;
import com.maddox.sas1946.il2.util.CommonTools;

public class AR_234C2 extends AR_234C implements TypeDockable {

    public AR_234C2() {
        this.bSightAutomation = false;
        this.bSightBombDump = false;
        this.fSightCurDistance = 0.0F;
        this.fSightCurForwardAngle = 0.0F;
        this.fSightCurSideslip = 0.0F;
        this.fSightCurAltitude = 850F;
        this.fSightCurSpeed = 150F;
        this.fSightCurReadyness = 0.0F;
        this.bayDoor = 0.0F;
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        hierMesh.chunkVisible("Wire_D0", !thisWeaponsName.equals("buzzbomb"));
        hierMesh.chunkVisible("DFLoop_D0", !thisWeaponsName.equals("buzzbomb"));
        hierMesh.chunkVisible("DFLoop_D0_Buzzbomb", thisWeaponsName.equals("buzzbomb"));
        hierMesh.chunkVisible("Elevator1", thisWeaponsName.equals("buzzbomb"));
        hierMesh.chunkVisible("Elevator2", thisWeaponsName.equals("buzzbomb"));
        hierMesh.chunkVisible("Launcher", thisWeaponsName.equals("buzzbomb"));
    }
    
    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        AR_234C2.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public boolean typeDiveBomberToggleAutomation() {
        return false;
    }

    public void typeDiveBomberAdjAltitudeReset() {
    }

    public void typeDiveBomberAdjAltitudePlus() {
    }

    public void typeDiveBomberAdjAltitudeMinus() {
    }

    public void typeDiveBomberAdjVelocityReset() {
    }

    public void typeDiveBomberAdjVelocityPlus() {
    }

    public void typeDiveBomberAdjVelocityMinus() {
    }

    public void typeDiveBomberAdjDiveAngleReset() {
    }

    public void typeDiveBomberAdjDiveAnglePlus() {
    }

    public void typeDiveBomberAdjDiveAngleMinus() {
    }

    public void typeDiveBomberReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
    }

    public void typeDiveBomberReplicateFromNet(NetMsgInput netmsginput) throws IOException {
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
        if (this.fSightCurForwardAngle > 85F) {
            this.fSightCurForwardAngle = 85F;
        }
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) this.fSightCurForwardAngle) });
        if (this.bSightAutomation) {
            this.typeBomberToggleAutomation();
        }
    }

    public void typeBomberAdjDistanceMinus() {
        this.fSightCurForwardAngle--;
        if (this.fSightCurForwardAngle < 0.0F) {
            this.fSightCurForwardAngle = 0.0F;
        }
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightElevation", new Object[] { new Integer((int) this.fSightCurForwardAngle) });
        if (this.bSightAutomation) {
            this.typeBomberToggleAutomation();
        }
    }

    public void typeBomberAdjSideslipReset() {
        this.fSightCurSideslip = 0.0F;
    }

    public void typeBomberAdjSideslipPlus() {
        this.fSightCurSideslip += 0.1F;
        if (this.fSightCurSideslip > 3F) {
            this.fSightCurSideslip = 3F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Integer((int) (this.fSightCurSideslip * 10F)) });
    }

    public void typeBomberAdjSideslipMinus() {
        this.fSightCurSideslip -= 0.1F;
        if (this.fSightCurSideslip < -3F) {
            this.fSightCurSideslip = -3F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSlip", new Object[] { new Integer((int) (this.fSightCurSideslip * 10F)) });
    }

    public void typeBomberAdjAltitudeReset() {
        this.fSightCurAltitude = 850F;
    }

    public void typeBomberAdjAltitudePlus() {
        this.fSightCurAltitude += 10F;
        if (this.fSightCurAltitude > 6000F) {
            this.fSightCurAltitude = 6000F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) this.fSightCurAltitude) });
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
    }

    public void typeBomberAdjAltitudeMinus() {
        this.fSightCurAltitude -= 10F;
        if (this.fSightCurAltitude < 850F) {
            this.fSightCurAltitude = 850F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightAltitude", new Object[] { new Integer((int) this.fSightCurAltitude) });
        this.fSightCurDistance = this.fSightCurAltitude * (float) Math.tan(Math.toRadians(this.fSightCurForwardAngle));
    }

    public void typeBomberAdjSpeedReset() {
        this.fSightCurSpeed = 250F;
    }

    public void typeBomberAdjSpeedPlus() {
        this.fSightCurSpeed += 10F;
        if (this.fSightCurSpeed > 900F) {
            this.fSightCurSpeed = 900F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberAdjSpeedMinus() {
        this.fSightCurSpeed -= 10F;
        if (this.fSightCurSpeed < 150F) {
            this.fSightCurSpeed = 150F;
        }
        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightSpeed", new Object[] { new Integer((int) this.fSightCurSpeed) });
    }

    public void typeBomberUpdate(float f) {
        if (Math.abs(this.FM.Or.getKren()) > 4.5D) {
            this.fSightCurReadyness -= 0.0666666F * f;
            if (this.fSightCurReadyness < 0.0F) {
                this.fSightCurReadyness = 0.0F;
            }
        }
        if (this.fSightCurReadyness < 1.0F) {
            this.fSightCurReadyness += 0.0333333F * f;
        } else if (this.bSightAutomation) {
            this.fSightCurDistance -= (this.fSightCurSpeed / 3.6F) * f;
            if (this.fSightCurDistance < 0.0F) {
                this.fSightCurDistance = 0.0F;
                this.typeBomberToggleAutomation();
            }
            this.fSightCurForwardAngle = (float) Math.toDegrees(Math.atan(this.fSightCurDistance / this.fSightCurAltitude));
            if (this.fSightCurDistance < ((this.fSightCurSpeed / 3.6F) * Math.sqrt(this.fSightCurAltitude * (2F / 9.81F)))) {
                this.bSightBombDump = true;
            }
            if (this.bSightBombDump) {
                if (this.FM.isTick(3, 0)) {
                    if ((this.FM.CT.Weapons[3] != null) && (this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1] != null) && this.FM.CT.Weapons[3][this.FM.CT.Weapons[3].length - 1].haveBullets()) {
                        this.FM.CT.WeaponControl[3] = true;
                        HUD.log(AircraftHotKeys.hudLogWeaponId, "BombsightBombdrop");
                    }
                } else {
                    this.FM.CT.WeaponControl[3] = false;
                }
            }
        }
    }
    
    public void update(float f) {
        super.update(f);
        if (!(this.FM instanceof RealFlightModel) || !((RealFlightModel) this.FM).isRealMode()) {
            if (this.typeDockableIsDocked()) {
                if (this.FM.AP.way.Cur() < this.FM.AP.way.size() - 1 && this.FM.AP.way.get(this.FM.AP.way.Cur() + 1).Action == 3) {
                    this.FM.CT.BayDoorControl = 1.0F;
                }
                if (this.FM.AP.way.curr().Action == 3 && Math.abs(this.FM.Or.getKren()) < 10F)
                    this.typeDockableAttemptDetach();
            }
        }
        if (this.typeDockableIsDocked()) {
            if (this.bayDoor > 0.99F) {
                if (this.drones[0] instanceof FI_103_V1) {
                    ((FI_103_V1) this.drones[0]).startEngine();
                } else if (this.drones[0] instanceof FI_103RIV) {
                    ((FI_103RIV) this.drones[0]).startEngine();
                }
            } else {
                if (this.drones[0] instanceof FI_103_V1) {
                    ((FI_103_V1) this.drones[0]).stopEngine();
                } else if (this.drones[0] instanceof FI_103RIV) {
                    ((FI_103RIV) this.drones[0]).stopEngine();
                }
            }
        }
        if ((!this.FM.CT.bHasBayDoors || !this.FM.CT.bHasBayDoorControl) && Actor.isValid(this.drones[0])) {
            this.FM.CT.bHasBayDoors = this.FM.CT.bHasBayDoorControl = true;
        } else if (this.FM.CT.bHasBayDoors && this.FM.CT.bHasBayDoorControl && !Actor.isValid(this.drones[0])) {
            this.FM.CT.BayDoorControl = 0F;
            this.FM.CT.bHasBayDoors = this.FM.CT.bHasBayDoorControl = false;
        }
        float fBay = this.FM.CT.getBayDoor();
        if (Math.abs(this.bayDoor - fBay) > 0.005F) {
            this.bayDoor = this.bayDoor + (0.005F * (fBay > this.bayDoor ? 1.0F : -1.0F));
            this.moveDockport(this.bayDoor);
        }
    }

    protected void moveDockport(float f) {
        if (Actor.isValid(this.drones[0])) {
            float angle = CommonTools.smoothCvt(f, 0F, 1F, 0F, -70F);
            this.resetYPRmodifier();
            Aircraft.xyz[2] = (float)Math.sin(Math.toRadians(angle)) * -0.25F;
            Aircraft.xyz[0] = ((float)Math.cos(Math.toRadians(angle)) - 1F) * 0.2F;
            Aircraft.ypr[1] = angle;
            this.hierMesh().chunkSetLocate("Elevator1", Aircraft.xyz, Aircraft.ypr);
            this.hierMesh().chunkSetAngles("Elevator2", 0.0F, angle, 0.0F);
            angle = CommonTools.smoothCvt(f, 0F, 1F, 0F, -68F);
            this.hierMesh().chunkSetAngles("Dockport", 0.0F, angle, 0.0F);
            HookNamed hooknamed = new HookNamed(this, "_Dockport0");
            Loc loc = new Loc();
            Loc loc1 = new Loc();
            this.pos.getAbs(loc1);
            hooknamed.computePos(this, loc1, loc);
            this.drones[0].pos.setAbs(loc);
            this.drones[0].pos.setBase(this, (Hook) null, true);
            this.drones[0].pos.resetAsBase();
        } else {
            float angle = CommonTools.smoothCvt(f, 0F, 1F, 0F, -70F);
            this.resetYPRmodifier();
            Aircraft.xyz[2] = (float)Math.sin(Math.toRadians(angle)) * -0.25F;
            Aircraft.xyz[0] = ((float)Math.cos(Math.toRadians(angle)) - 1F) * 0.2F;
            Aircraft.ypr[1] = angle;
            this.hierMesh().chunkSetLocate("Elevator1", Aircraft.xyz, Aircraft.ypr);
            this.hierMesh().chunkSetAngles("Dockport", 0.0F, angle, 0.0F);
            this.resetYPRmodifier();
            angle = CommonTools.smoothCvt(f, 0F, 1F, 5.5F, -70F);
            Aircraft.xyz[2] = CommonTools.smoothCvt(f, 0F, 0.1F, -0.02F, 0F);
            Aircraft.ypr[1] = angle;
            this.hierMesh().chunkSetLocate("Elevator2", Aircraft.xyz, Aircraft.ypr);
        }
    }

    public void typeDockableAttemptAttach() {
    }

    public void typeDockableAttemptDetach() {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return;
        }
        if (this.FM.AS.isMaster()) {
            for (int i = 0; i < this.drones.length; i++) {
                if (Actor.isValid(this.drones[i])) {
                    if (this.bayDoor >= 0.99F) this.typeDockableRequestDetach(this.drones[i], i, true);
                }
            }

        }
    }

    public void typeDockableDoAttachToDrone(Actor actor, int i) {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return;
        }
        if (!Actor.isValid(this.drones[i])) {
            HookNamed hooknamed = new HookNamed(this, "_Dockport" + i);
            Loc loc = new Loc();
            Loc loc1 = new Loc();
            this.pos.getAbs(loc1);
            hooknamed.computePos(this, loc1, loc);
            actor.pos.setAbs(loc);
            actor.pos.setBase(this, (Hook) null, true);
            actor.pos.resetAsBase();
            this.drones[i] = actor;
            ((TypeDockable) this.drones[i]).typeDockableDoAttachToQueen(this, i);
        }
    }

    public void typeDockableDoAttachToQueen(Actor actor, int i) {
    }

    public void typeDockableDoDetachFromDrone(int i) {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return;
        }
        if (!Actor.isValid(this.drones[i])) {
            return;
        } else {
            this.drones[i].pos.setBase((Actor) null, (Hook) null, true);
            ((TypeDockable) this.drones[i]).typeDockableDoDetachFromQueen(i);
            this.drones[i] = null;
            return;
        }
    }

    public void typeDockableDoDetachFromQueen(int i) {
    }

    public Actor typeDockableGetDrone() {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return null;
        }
        return this.drones[0];
    }

    public boolean typeDockableIsDocked() {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return false;
        }
        return true;
    }

    public void typeDockableReplicateFromNet(NetMsgInput netmsginput) throws IOException {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return;
        }
        for (int i = 0; i < this.drones.length; i++) {
            if (netmsginput.readByte() == 1) {
                NetObj netobj = netmsginput.readNetObj();
                if (netobj != null) {
                    this.typeDockableDoAttachToDrone((Actor) netobj.superObj(), i);
                }
            }
        }
    }

    public void typeDockableReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return;
        }
        for (int i = 0; i < this.drones.length; i++) {
            if (Actor.isValid(this.drones[i])) {
                netmsgguaranted.writeByte(1);
                ActorNet actornet = this.drones[i].net;
                if (actornet.countNoMirrors() == 0) {
                    netmsgguaranted.writeNetObj(actornet);
                } else {
                    netmsgguaranted.writeNetObj((NetObj) null);
                }
            } else {
                netmsgguaranted.writeByte(0);
            }
        }
    }

    public void typeDockableRequestAttach(Actor actor) {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return;
        }
        if (!(actor instanceof Aircraft)) {
            return;
        }
        Aircraft aircraft = (Aircraft) actor;
        if (aircraft.FM.AS.isMaster() && aircraft.FM.Gears.onGround() && (aircraft.FM.getSpeedKMH() < 10F) && (this.FM.getSpeedKMH() < 10F)) {
            for (int i = 0; i < this.drones.length; i++) {
                if (!Actor.isValid(this.drones[i])) {
                    HookNamed hooknamed = new HookNamed(this, "_Dockport" + i);
                    Loc loc = new Loc();
                    Loc loc1 = new Loc();
                    this.pos.getAbs(loc1);
                    hooknamed.computePos(this, loc1, loc);
                    actor.pos.getAbs(loc1);
                    if (loc.getPoint().distance(loc1.getPoint()) < 5D) {
                        if (this.FM.AS.isMaster()) {
                            this.typeDockableRequestAttach(actor, i, true);
                            return;
                        } else {
                            this.FM.AS.netToMaster(32, i, 0, actor);
                            return;
                        }
                    }
                }
            }

        }
    }

    public void typeDockableRequestAttach(Actor actor, int i, boolean flag) {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return;
        }
        if ((i < 0) || (i > 1)) {
            return;
        }
        if (flag) {
            if (this.FM.AS.isMaster()) {
                this.FM.AS.netToMirrors(34, i, 1, actor);
                this.typeDockableDoAttachToDrone(actor, i);
            } else {
                this.FM.AS.netToMaster(34, i, 1, actor);
            }
        } else if (this.FM.AS.isMaster()) {
            if (!Actor.isValid(this.drones[i])) {
                this.FM.AS.netToMirrors(34, i, 1, actor);
                this.typeDockableDoAttachToDrone(actor, i);
            }
        } else {
            this.FM.AS.netToMaster(34, i, 0, actor);
        }
    }

    public void typeDockableRequestDetach(Actor actor) {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return;
        }
        for (int i = 0; i < this.drones.length; i++) {
            if (actor == this.drones[i]) {
                Aircraft aircraft = (Aircraft) actor;
                if (aircraft.FM.AS.isMaster()) {
                    if (this.FM.AS.isMaster()) {
                        this.typeDockableRequestDetach(actor, i, true);
                    } else {
                        this.FM.AS.netToMaster(33, i, 1, actor);
                    }
                }
            }
        }
    }

    public void typeDockableRequestDetach(Actor actor, int i, boolean flag) {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return;
        }
        if (flag) {
            if (this.FM.AS.isMaster()) {
                this.FM.AS.netToMirrors(35, i, 1, actor);
                this.typeDockableDoDetachFromDrone(i);
            } else {
                this.FM.AS.netToMaster(35, i, 1, actor);
            }
        }
    }

    public void typeBomberReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
        netmsgguaranted.writeByte((this.bSightAutomation ? 1 : 0) | (this.bSightBombDump ? 2 : 0));
        netmsgguaranted.writeFloat(this.fSightCurDistance);
        netmsgguaranted.writeByte((int) this.fSightCurForwardAngle);
        netmsgguaranted.writeByte((int) ((this.fSightCurSideslip + 3F) * 33.33333F));
        netmsgguaranted.writeFloat(this.fSightCurAltitude);
        netmsgguaranted.writeFloat(this.fSightCurSpeed);
        netmsgguaranted.writeByte((int) (this.fSightCurReadyness * 200F));
    }

    public void typeBomberReplicateFromNet(NetMsgInput netmsginput) throws IOException {
        int i = netmsginput.readUnsignedByte();
        this.bSightAutomation = (i & 1) != 0;
        this.bSightBombDump = (i & 2) != 0;
        this.fSightCurDistance = netmsginput.readFloat();
        this.fSightCurForwardAngle = netmsginput.readUnsignedByte();
        this.fSightCurSideslip = -3F + (netmsginput.readUnsignedByte() / 33.33333F);
        this.fSightCurAltitude = netmsginput.readFloat();
        this.fSightCurSpeed = netmsginput.readFloat();
        this.fSightCurReadyness = netmsginput.readUnsignedByte() / 200F;
    }

    private boolean bSightAutomation;
    private boolean bSightBombDump;
    private float   fSightCurDistance;
    public float    fSightCurForwardAngle;
    public float    fSightCurSideslip;
    public float    fSightCurAltitude;
    public float    fSightCurSpeed;
    public float    fSightCurReadyness;
    private Actor   drones[] = { null };
    private float   bayDoor;

    static {
        Class class1 = AR_234C2.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Ar 234");
        Property.set(class1, "meshName", "3DO/Plane/Ar-234C/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar05());
        Property.set(class1, "yearService", 1944F);
        Property.set(class1, "yearExpired", 1948.8F);
        Property.set(class1, "FlightModel", "FlightModels/Ar-234C.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitAR_234C2.class, CockpitAR_234C2_Bombardier.class });
        Property.set(class1, "LOSElevation", 1.14075F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 0, 0, 3, 3, 3, 9, 9 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_CANNON01", "_CANNON02", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalDev01", "_ExternalDev02" });
    }

}
