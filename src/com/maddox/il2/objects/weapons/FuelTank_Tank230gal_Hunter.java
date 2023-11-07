package com.maddox.il2.objects.weapons;

import com.maddox.il2.objects.air.Hunter;
import com.maddox.rts.Property;

public class FuelTank_Tank230gal_Hunter extends FuelTank {

    public void visibilityAsBase(boolean flag) {
        super.visibilityAsBase(flag);
        this.drawing(false); // Hide mesh, we only need it for QMB!
    }

    public void start() {
        super.start();
        this.drawing(false); // Hide mesh, we only need it for QMB!
// System.out.println("230gal Droptank start(), Owner is " + this.getOwner().getClass().getName());
        if (!(this.getOwner() instanceof Hunter)) {
            return;
        }
        Hunter hunter = (Hunter) this.getOwner();
        if (hunter.FM.getOverload() < 0.0F) {
            return;
        }
        hunter.dropExternalTanks();
        this.destroy();
    }

    static {
        Class localClass = FuelTank_Tank230gal_Hunter.class;
        Property.set(localClass, "mesh", "3DO/Arms/230gal_hunter/mono.sim");
        Property.set(localClass, "kalibr", 0.46F);
        Property.set(localClass, "massa", 700.0F);
    }
}
