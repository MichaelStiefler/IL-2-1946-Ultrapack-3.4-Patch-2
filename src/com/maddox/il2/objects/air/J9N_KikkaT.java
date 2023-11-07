package com.maddox.il2.objects.air;

import com.maddox.il2.engine.Config;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.Property;

public class J9N_KikkaT extends Kikka123
{
    public void update (float f) {
        super.update(f);
        if ((!this.FM.isPlayers() || !(this.FM instanceof RealFlightModel) || !((RealFlightModel)this.FM).isRealMode()) && this.FM.Gears.onGround()) {
            if (this.FM.getSpeedKMH() < 20F) {
                this.FM.CT.cockpitDoorControl = 1.0F;
            } else {
                this.FM.CT.cockpitDoorControl = 0.0F;
            }
        }
        if (!this.FM.AS.isMaster() || !Config.isUSE_RENDER()) return;
        if (this.FM.isPlayers() && (this.FM.getSpeedKMH() > 240F)) {
            HotKeyCmd.getByRecordedId(348).enable(false);
        } else {
            HotKeyCmd.getByRecordedId(348).enable(true);
        }
    }
    
    static 
    {
        Class class1 = J9N_KikkaT.class;
        new NetAircraft.SPAWN(class1);
        Property.set(class1, "iconFar_shortClassName", "Kikka");
        Property.set(class1, "meshName", "3DO/Plane/J9N-KikkaT(Multi1)/hier.him");
        Property.set(class1, "PaintScheme", new PaintSchemeBMPar03());
        Property.set(class1, "yearService", 1944.1F);
        Property.set(class1, "yearExpired", 1945.5F);
        Property.set(class1, "FlightModel", "FlightModels/J9N1K.fmd");
        Property.set(class1, "cockpitClass", new Class[] {
            Cockpit_KikkaF.class, Cockpit_KikkaR.class
        });
        Property.set(class1, "LOSElevation", 0.74185F);
        Aircraft.weaponTriggersRegister(class1, new int[] {
            0, 0, 1, 1, 3, 3, 3, 3
        });
        Aircraft.weaponHooksRegister(class1, new String[] {
            "_CANNON01", "_CANNON02", "_CANNON03", "_CANNON04", "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04"
        });
    }
}
