package com.maddox.il2.objects.air;

import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.objects.air.electronics.RadarFuG200Equipment;
import com.maddox.il2.objects.electronics.RadarLiSN2Equipment;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.Property;

public class CockpitMe_410_Radar extends CockpitPilot {
    class Interpolater extends InterpolateRef {
        public boolean tick() {
            CockpitMe_410_Radar.this.fm = World.getPlayerFM();
            if (CockpitMe_410_Radar.this.fm == null) {
                return true;
            }
            if (CockpitMe_410_Radar.this.bNeedSetUp) {
                CockpitMe_410_Radar.this.reflectPlaneMats();
                CockpitMe_410_Radar.this.bNeedSetUp = false;
            }
            return true;
        }
    }

    protected boolean doFocusEnter() {
        if (super.doFocusEnter()) {
            ((TypeRadarLiSN2Carrier) this.aircraft()).setCurPilot(2);
            return true;
        } else {
            return false;
        }
    }

    protected void doFocusLeave() {
        ((TypeRadarLiSN2Carrier) this.aircraft()).setCurPilot(1);
        super.doFocusLeave();
    }

    public CockpitMe_410_Radar() {
        super("3DO/Cockpit/Me410fam/410radar.him", "he111");
        this.bNeedSetUp = true;
    }

    public void destroy() {
        super.destroy();
    }

    protected void reflectPlaneMats() {
        HierMesh hiermesh = this.aircraft().hierMesh();
        com.maddox.il2.engine.Mat mat = hiermesh.material(hiermesh.materialFind("Gloss1D0o"));
        this.mesh.materialReplace("Gloss1D0o", mat);
        mat = hiermesh.material(hiermesh.materialFind("Gloss2D0o"));
        this.mesh.materialReplace("Gloss2D0o", mat);
        mat = hiermesh.material(hiermesh.materialFind("Pilot1"));
        this.mesh.materialReplace("Pilot1", mat);
    }

    public void reflectWorldToInstruments(float f) {
        if (this.fm != null) {
            if (this.bNeedSetUp) {
                this.reflectPlaneMats();
                this.bNeedSetUp = false;
            }
            this.mesh.chunkVisible("Head1_D0", this.aircraft().hierMesh().isChunkVisible("Pilot1_D0"));
            this.mesh.chunkVisible("Head1_D1", this.aircraft().hierMesh().isChunkVisible("Pilot1_D1"));
        }

        // +++ RadarLiSN2 / FuG200 +++
        if ((this.aircraft() instanceof ME_410DNJ) || this.aircraft().thisWeaponsName.toLowerCase().startsWith("nj")) {
            this.cockpitRadarLiSN2.updateRadar();
        } else if (this.aircraft().thisWeaponsName.toLowerCase().startsWith("sea")) {
            this.cockpitRadarFuG200.updateRadar();
        }
        // --- RadarLiSN2 / FuG200 ---
    }

    // +++ RadarLiSN2 / FuG200 +++
    private RadarLiSN2Equipment cockpitRadarLiSN2 = null;
    private RadarFuG200Equipment cockpitRadarFuG200 = null;
    // --- RadarLiSN2 / FuG200 ---

    public boolean isEnableFocusing() {
        if ((this.aircraft() instanceof ME_410DNJ) || this.aircraft().thisWeaponsName.toLowerCase().startsWith("nj")) {
            if (this.cockpitRadarLiSN2 == null) this.cockpitRadarLiSN2 = new RadarLiSN2Equipment(this, 28, 70F, 5F, 0.2F, 0.012F, 0.011637F, 0.073296F);
            return super.isEnableFocusing();
        } else if (this.aircraft().thisWeaponsName.toLowerCase().startsWith("sea")) {
            if (this.cockpitRadarFuG200 == null) this.cockpitRadarFuG200 = new RadarFuG200Equipment(this, 40, 1500.0F, 5.0F, 0.2F, 0.012F, 0.011637F, 0.073296F);
            return super.isEnableFocusing();
        }
        HotKeyCmd.exec("aircraftView", "cockpitSwitch3"); // Switch to Gunner Cockpit if not Night Fighter
        return false;
    }

    private boolean bNeedSetUp;

    static {
        Property.set(CockpitMe_410_Radar.class, "astatePilotIndx", 1);
    }
}
