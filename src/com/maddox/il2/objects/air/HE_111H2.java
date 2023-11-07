package com.maddox.il2.objects.air;

import java.io.IOException;

import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.ActorNet;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.rts.NetMsgGuaranted;
import com.maddox.rts.NetMsgInput;
import com.maddox.rts.NetObj;
import com.maddox.rts.Property;

public class HE_111H2 extends HE_111 implements TypeDockable {

    public boolean typeDockableIsDocked() {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return false;
        }
        return true;
    }

    public Actor typeDockableGetDrone() {
        if (!this.thisWeaponsName.equals("buzzbomb")) {
            return null;
        }
        return this.drones[0];
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
                    this.typeDockableRequestDetach(this.drones[i], i, true);
                }
            }
        }
    }

    public void typeDockableRequestAttach(Actor actor) {
        if (!this.thisWeaponsName.equals("buzzbomb") || !(actor instanceof Aircraft)) {
            return;
        }
        Aircraft aircraft = (Aircraft) actor;
        if ((aircraft.FM.AS.isMaster()) && (aircraft.FM.Gears.onGround()) && (aircraft.FM.getSpeedKMH() < 10.0F) && (this.FM.getSpeedKMH() < 10.0F)) {
            for (int i = 0; i < this.drones.length; i++) {
                if (!Actor.isValid(this.drones[i])) {
                    HookNamed hooknamed = new HookNamed(this, "_Dockport" + i);
                    Loc loc = new Loc();
                    Loc loc1 = new Loc();
                    this.pos.getAbs(loc1);
                    hooknamed.computePos(this, loc1, loc);
                    actor.pos.getAbs(loc1);
                    if (loc.getPoint().distance(loc1.getPoint()) < 5.0D) {
                        if (this.FM.AS.isMaster()) {
                            this.typeDockableRequestAttach(actor, i, true);
                            return;
                        }
                        this.FM.AS.netToMaster(32, i, 0, actor);
                        return;
                    }
                }
            }
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

    public void typeDockableRequestAttach(Actor actor, int i, boolean flag) {
        if (!this.thisWeaponsName.equals("buzzbomb") || (i < 0) || (i > 1)) {
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

    public void typeDockableDoDetachFromDrone(int i) {
        if (!this.thisWeaponsName.equals("buzzbomb") || !Actor.isValid(this.drones[i])) {
            return;
        }
        this.drones[i].pos.setBase((Actor) null, (Hook) null, true);
        ((TypeDockable) this.drones[i]).typeDockableDoDetachFromQueen(i);
        this.drones[i] = null;
    }

    public void typeDockableDoAttachToQueen(Actor actor, int i) {
    }

    public void typeDockableDoDetachFromQueen(int i) {
    }

    public void typeDockableReplicateToNet(NetMsgGuaranted netmsgguaranted) throws IOException {
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

    public void typeDockableReplicateFromNet(NetMsgInput netmsginput) throws IOException {
        for (int i = 0; i < this.drones.length; i++) {
            if (netmsginput.readByte() == 1) {
                NetObj netobj = netmsginput.readNetObj();
                if (netobj != null) {
                    this.typeDockableDoAttachToDrone((Actor) netobj.superObj(), i);
                }
            }
        }
    }

    public void update(float f) {
        super.update(f);
        if (!this.thisWeaponsName.equals("buzzbomb") && !((this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) && (this.FM.AP.way.curr().Action == 3) && this.typeDockableIsDocked() && (Math.abs(this.FM.Or.getKren()) < 10.0F)) {
            this.typeDockableAttemptDetach();
        }
    }

    protected void moveBayDoor(float f) {
        if (this.thisWeaponsName.equals("buzzbomb")) {
            return;
        }
        this.hierMesh().chunkSetAngles("Bay1_D0", 0.0F, 74F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay2_D0", 0.0F, -94F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay3_D0", 0.0F, 74F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay4_D0", 0.0F, -94F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay5_D0", 0.0F, 74F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay6_D0", 0.0F, -94F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay7_D0", 0.0F, 74F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay8_D0", 0.0F, -94F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay9_D0", 0.0F, -74F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay10_D0", 0.0F, 94F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay11_D0", 0.0F, -74F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay12_D0", 0.0F, 94F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay13_D0", 0.0F, -74F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay14_D0", 0.0F, 94F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay15_D0", 0.0F, -74F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Bay16_D0", 0.0F, 94F * f, 0.0F);
    }

    private Actor[] drones = { null };

    static {
        Class class1 = HE_111H2.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "He-111");
        Property.set(class1, "meshName", "3do/plane/He-111H-2/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar02());
        Property.set(class1, "yearService", 1939.5F);
        Property.set(class1, "yearExpired", 1948F);
        Property.set(class1, "FlightModel", "FlightModels/He-111H-2.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitHE_111H2.class, CockpitHE_111H2_Bombardier.class, CockpitHE_111H2_NGunner.class, CockpitHE_111H2_TGunner.class, CockpitHE_111H2_BGunner.class, CockpitHE_111H2_LGunner.class, CockpitHE_111H2_RGunner.class });
        Aircraft.weaponTriggersRegister(class1, new int[] { 10, 11, 12, 13, 14, 3, 3, 3, 3, 3, 3, 3, 3 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_MGUN05", "_BombSpawn01", "_BombSpawn02", "_BombSpawn03", "_BombSpawn04", "_BombSpawn05", "_BombSpawn06", "_BombSpawn07", "_BombSpawn08" });
    }
}
