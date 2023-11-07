package com.maddox.il2.objects.air;

import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.game.Main3D;
import com.maddox.il2.game.Mission;
import com.maddox.rts.Property;

public class TYPHOON1BLate extends TEMPEST {

    public void onAircraftLoaded() {
        this.FM.EI.engines[0].doSetKillControlAfterburner();
        if (Mission.curYear() >= fourBladeIntroduction) {
            this.hierMesh().chunkVisible(Props[0][0], false);
            this.hierMesh().chunkVisible(Propsb[0][0], true);
        }
        super.onAircraftLoaded();
    }

    protected void moveFan(float f) {
        int i = 0;
        boolean isPropB = Mission.curYear() >= fourBladeIntroduction;
        for (int j = 0; j < this.FM.EI.getNum(); j++) {
            if (this.oldProp[j] < 2) {
                i = Math.abs((int) (this.FM.EI.engines[j].getw() * 0.06F));
                if (i >= 1) i = 1;
                if (i != this.oldProp[j] && this.hierMesh().isChunkVisible(isPropB ? Propsb[j][this.oldProp[j]] : Props[j][this.oldProp[j]])) {
                    this.hierMesh().chunkVisible(isPropB ? Propsb[j][this.oldProp[j]] : Props[j][this.oldProp[j]], false);
                    this.oldProp[j] = i;
                    this.hierMesh().chunkVisible(isPropB ? Propsb[j][i] : Props[j][i], true);
                }

            }

            if (i == 0) this.propPos[j] = (this.propPos[j] + 57.3F * this.FM.EI.engines[j].getw() * f) % 360.0F;
            else {
                float f1 = 57.3F * this.FM.EI.engines[j].getw();
                f1 %= 2880.0F;
                f1 /= 2880.0F;
                if (f1 <= 0.5F) f1 *= 2.0F;
                else f1 = f1 * 2.0F - 2.0F;
                f1 *= 1200.0F;
                this.propPos[j] = (this.propPos[j] + f1 * f) % 360.0F;
            }
            this.hierMesh().chunkSetAngles(isPropB ? Propsb[j][0] : Props[j][0], 0.0F, -this.propPos[j], 0.0F);
        }
    }

    public void hitProp(int i, int j, Actor actor) {
//        System.out.println("hitProp(" + i + ", " + j + ", " + actor.getClass().getName());
        if (i > this.FM.EI.getNum() - 1 || this.oldProp[i] == 2) return;
        super.hitProp(i, j, actor);
//        if (this.hierMesh().isChunkVisible(Props[i][2]) && Mission.curYear() >= fourBladeIntroduction) {
//            this.hierMesh().chunkVisible(Props[i][2], false);
//            this.hierMesh().chunkVisible(Propsb[i][0], false);
//            this.hierMesh().chunkVisible(Propsb[i][1], false);
//            this.hierMesh().chunkVisible(Propsb[i][2], true);
//        }
        if (Mission.curYear() >= fourBladeIntroduction) {
            for (int k=0; k<3; k++) {
//                System.out.println("this.hierMesh().isChunkVisible(Props[" + i + "][" + k + "])=" + this.hierMesh().isChunkVisible(Props[i][k]));
                this.hierMesh().chunkVisible(Props[i][k], false);
            }
            this.hierMesh().chunkVisible(Propsb[i][0], false);
            this.hierMesh().chunkVisible(Propsb[i][1], false);
            this.hierMesh().chunkVisible(Propsb[i][2], true);
//            for (int k=0; k<3; k++) {
//                System.out.println("this.hierMesh().isChunkVisible(Props[" + i + "][" + k + "])=" + this.hierMesh().isChunkVisible(Props[i][k]));
//                System.out.println("this.hierMesh().isChunkVisible(Propsb[" + i + "][" + k + "])=" + this.hierMesh().isChunkVisible(Propsb[i][k]));
//            }
        }
    }

    public void moveCockpitDoor(float f) {
        this.resetYPRmodifier();
        Aircraft.xyz[1] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.625F);
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.01F, 0.99F, 0.0F, 0.01F);
        if (f < 0.5F) Aircraft.ypr[2] = Aircraft.cvt(f, 0.01F, 0.5F, 0.0F, 3.0F);
        else Aircraft.ypr[2] = Aircraft.cvt(f, 0.5F, 0.99F, 3.0F, 1.0F);
        this.hierMesh().chunkSetLocate("Blister1_D0", Aircraft.xyz, Aircraft.ypr);
        this.resetYPRmodifier();
        Aircraft.xyz[2] = Aircraft.cvt(f, 0.6F, 0.99F, 0.0F, 0.13F);
        Aircraft.ypr[2] = Aircraft.cvt(f, 0.6F, 0.99F, 0.0F, -5F);
        this.hierMesh().chunkSetLocate("Pilot1_D0", Aircraft.xyz, Aircraft.ypr);
        if (Config.isUSE_RENDER()) {
            if (Main3D.cur3D().cockpits != null && Main3D.cur3D().cockpits[0] != null) Main3D.cur3D().cockpits[0].onDoorMoved(f);
            this.setDoorSnd(f);
        }
    }

    public void rareAction(float f, boolean flag) {
        super.rareAction(f, flag);
        if (this.FM.getAltitude() < 3000F && this.FM.CT.getCockpitDoor() > 0.8F) this.hierMesh().chunkVisible("HMask1_D0", false);
        else this.hierMesh().chunkVisible("HMask1_D0", this.hierMesh().isChunkVisible("Pilot1_D0"));
//        if (Mission.curYear() >= fourBladeIntroduction && this.hierMesh().isChunkVisible(Props[0][0])) {
//            this.hierMesh().chunkVisible(Props[0][0], false);
//            this.hierMesh().chunkVisible(Propsb[0][0], true);
//        }
    }
    
    public void update(float f) {
        super.update(f);
        if (Mission.curYear() >= fourBladeIntroduction) {
            for (int i=0; i<3; i++) {
                if (this.hierMesh().isChunkVisible(Props[0][i])) {
                    this.hierMesh().chunkVisible(Props[0][i], false);
                    this.hierMesh().chunkVisible(Propsb[0][i], true);
                }
            }
            for (int i=0; i<2; i++) {
                if (this.hierMesh().isChunkVisible(Propsb[0][i])) {
                    for (int j=i + 1; j<3; j++) this.hierMesh().chunkVisible(Propsb[0][j], false);
                }
            }
        }
    }


    public static final String Propsb[][]            = { { "Prop1b_D0", "PropRot1b_D0", "Prop1b_D1" }, { "Prop2b_D0", "PropRot2b_D0", "Prop2b_D1" }, { "Prop3b_D0", "PropRot3b_D0", "Prop3b_D1" }, { "Prop4b_D0", "PropRot4b_D0", "Prop4b_D1" },
            { "Prop5b_D0", "PropRot5b_D0", "Prop5b_D1" }, { "Prop6b_D0", "PropRot6b_D0", "Prop6b_D1" } };
    private static final int   fourBladeIntroduction = 1943;

    static {
        Class class1 = TYPHOON1BLate.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Typhoon");
        Property.set(class1, "meshName", "3DO/Plane/TyphoonMkIBLate_(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeFMPar05());
        Property.set(class1, "meshName_gb", "3DO/Plane/TyphoonMkIBLate_(GB)/hier.him");
        Property.set(class1, "PaintScheme_gb", new PaintSchemeFMPar05());
        Property.set(class1, "yearService", 1942F);
        Property.set(class1, "yearExpired", 1946.5F);
        Property.set(class1, "FlightModel", "FlightModels/Typhoon1BLate.fmd");
        Property.set(class1, "cockpitClass", new Class[] { CockpitTemp5.class });
        Property.set(class1, "LOSElevation", 0.93655F);
        Aircraft.weaponTriggersRegister(class1, new int[] { 1, 1, 1, 1, 9, 9, 9, 9, 3, 3, 9, 9, 2, 2, 2, 2, 2, 2, 2, 2 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalDev01", "_ExternalDev02", "_ExternalDev05", "_ExternalDev06", "_ExternalBomb01", "_ExternalBomb02", "_ExternalDev07",
                "_ExternalDev08", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06", "_ExternalRock09", "_ExternalRock10" });
    }
}
