package com.maddox.il2.objects.air;

import com.maddox.rts.Property;

public class R_XIIITerh extends R_XIIIxyz implements TypeSeaPlane, TypeSailPlane {
//    public void update(float f) {
//        super.update(f);
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 2; j++) {
//                if (this.FM.Gears.clpGearEff[i][j] != null) {
//                    R_XIIITerh.tmpp.set(this.FM.Gears.clpGearEff[i][j].pos.getAbsPoint());
//                    R_XIIITerh.tmpp.z = 0.01D;
//                    this.FM.Gears.clpGearEff[i][j].pos.setAbs(R_XIIITerh.tmpp);
//                    this.FM.Gears.clpGearEff[i][j].pos.reset();
//                }
//            }
//
//        }
//
//    }
    
//    public void update(float f) {
//        super.update(f);
//        HUD.training("RPM:" + this.FM.EI.engines[0].getRPM());
//    }

    protected void moveRudder(float f) {
        this.hierMesh().chunkSetAngles("Rudder1_D0", 0.0F, -20F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Rudder1L_D0", -20F * f, 0.0F, 0.0F);
        this.hierMesh().chunkSetAngles("Rudder1R_D0", -20F * f, 0.0F, 0.0F);
        this.hierMesh().chunkSetAngles("Gear11L_D0", 20F * f, 0.0F, 0.0F);
        this.hierMesh().chunkSetAngles("Gear11R_D0", 20F * f, 0.0F, 0.0F);
        this.hierMesh().chunkSetAngles("Gear11L_wireL", -20F * f, 0.0F, 0.0F);
        this.hierMesh().chunkSetAngles("Gear11L_wireR", -20F * f, 0.0F, 0.0F);
        this.hierMesh().chunkSetAngles("Gear11R_wireL", -20F * f, 0.0F, 0.0F);
        this.hierMesh().chunkSetAngles("Gear11R_wireR", -20F * f, 0.0F, 0.0F);
    }

//    private static Point3d tmpp = new Point3d();

    static {
        Class class1 = R_XIIITerh.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "R.XIII");
        Property.set(class1, "meshName", "3DO/Plane/R-XIIITerh(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar00());
        Property.set(class1, "meshName_pl", "3DO/Plane/R-XIIITerh/hier.him");
        Property.set(class1, "PaintScheme_pl", new PaintSchemeFCSPar01());
        Property.set(class1, "yearService", 1934F);
        Property.set(class1, "yearExpired", 1939.5F);
        Property.set(class1, "FlightModel", "FlightModels/R-XIIIterh.fmd");
//        Property.set(class1, "FlightModel", "FlightModels/R-XIIIterh.fmd:R_XIII_TERH_FM");
        Property.set(class1, "cockpitClass", new Class[] { CockpitR_XIIITerh.class, CockpitR_XIIITerh_TGunner.class });
        Aircraft.weaponTriggersRegister(class1, new int[] { 10 });
        Aircraft.weaponHooksRegister(class1, new String[] { "_MGUN01" });
    }
}
