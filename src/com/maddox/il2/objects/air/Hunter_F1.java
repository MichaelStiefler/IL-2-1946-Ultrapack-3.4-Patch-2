package com.maddox.il2.objects.air;

import com.maddox.il2.ai.World;
import com.maddox.il2.fm.RealFlightModel;
import com.maddox.il2.game.HUD;
import com.maddox.rts.Property;

public class Hunter_F1 extends Hunter {

    public void engineSurge(float f) {
        if (this.FM.AS.isMaster()) {
            if (this.curthrl == -1F) {
                this.curthrl = this.oldthrl = this.FM.EI.engines[0].getControlThrottle();
            } else {
                this.curthrl = this.FM.EI.engines[0].getControlThrottle();
                if (this.curthrl < 1.05F) {
                    if ((((this.curthrl - this.oldthrl) / f) > 10.0F) && (this.FM.EI.engines[0].getRPM() < 3200.0F) && (this.FM.EI.engines[0].getStage() == 6) && (World.Rnd().nextFloat() < 0.40F)) {
                        if (this.FM.actor == World.getPlayerAircraft()) {
                            HUD.log("Compressor Stall!");
                        }
                        super.playSound("weapon.MGunMk108s", true);
                        this.engineSurgeDamage += 0.01D * (this.FM.EI.engines[0].getRPM() / 1000F);
                        this.FM.EI.engines[0].doSetReadyness(this.FM.EI.engines[0].getReadyness() - this.engineSurgeDamage);
                        if ((World.Rnd().nextFloat() < 0.05F) && (this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) {
                            this.FM.AS.hitEngine(this, 0, 100);
                        }
                        if ((World.Rnd().nextFloat() < 0.05F) && (this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) {
                            this.FM.EI.engines[0].setEngineDies(this);
                        }
                    }
                    if ((((this.curthrl - this.oldthrl) / f) < -10.0F) && (((this.curthrl - this.oldthrl) / f) > -100.0F) && (this.FM.EI.engines[0].getRPM() < 3200.0F) && (this.FM.EI.engines[0].getStage() == 6)) {
                        super.playSound("weapon.MGunMk108s", true);
                        this.engineSurgeDamage += 0.001D * (this.FM.EI.engines[0].getRPM() / 1000F);
                        this.FM.EI.engines[0].doSetReadyness(this.FM.EI.engines[0].getReadyness() - this.engineSurgeDamage);
                        if ((World.Rnd().nextFloat() < 0.40F) && (this.FM instanceof RealFlightModel) && ((RealFlightModel) this.FM).isRealMode()) {
                            if (this.FM.actor == World.getPlayerAircraft()) {
                                HUD.log("Engine Flameout!");
                            }
                            this.FM.EI.engines[0].setEngineStops(this);
                        } else {
                            if (this.FM.actor == World.getPlayerAircraft()) {
                                HUD.log("Compressor Stall!");
                            }
                        }
                    }
                }
                this.oldthrl = this.curthrl;
            }
        }
    }

    private float oldthrl;
    private float curthrl;
    private float engineSurgeDamage;

    static {
        Class airClass = Hunter_F1.class;
        new NetAircraft.SPAWN(airClass);
        Property.set(airClass, "iconFar_shortClassName", "Hunter");
        Property.set(airClass, "meshName", "3DO/Plane/Hunter(Multi1)/hier_F1.him");
        Property.set(airClass, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(airClass, "yearService", 1949.9F);
        Property.set(airClass, "yearExpired", 1960.3F);
        Property.set(airClass, "FlightModel", "FlightModels/HunterF1.fmd");
        Property.set(airClass, "cockpitClass", new Class[] { CockpitHunter.class });
        Property.set(airClass, "LOSElevation", 0.725F);
        Aircraft.weaponTriggersRegister(airClass, new int[] { 0, 0, 0, 0 });
        Aircraft.weaponHooksRegister(airClass, new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04" });
    }
}
