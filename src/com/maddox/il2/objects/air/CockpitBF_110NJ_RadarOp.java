package com.maddox.il2.objects.air;

import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.objects.electronics.RadarLiSN2Equipment;
import com.maddox.rts.HotKeyCmd;
import com.maddox.rts.Property;

public class CockpitBF_110NJ_RadarOp extends CockpitPilot {
    class Interpolater extends InterpolateRef {
        public boolean tick() {
            CockpitBF_110NJ_RadarOp.this.fm = World.getPlayerFM();
            if (CockpitBF_110NJ_RadarOp.this.fm == null) {
                return true;
            }
            if (CockpitBF_110NJ_RadarOp.this.bNeedSetUp) {
                CockpitBF_110NJ_RadarOp.this.reflectPlaneMats();
                CockpitBF_110NJ_RadarOp.this.bNeedSetUp = false;
            }
            return true;
        }
    }

    protected boolean doFocusEnter() {
        if (super.doFocusEnter()) {
            this.aircraft().hierMesh().chunkVisible("Interior_D0", false);
            if (!(this.aircraft() instanceof BF_110H4)) {
                this.aircraft().hierMesh().chunkVisible("Turret1B_D0", false);
            }
            return true;
        } else {
            return false;
        }
    }

    protected void doFocusLeave() {
        this.aircraft().hierMesh().chunkVisible("Interior_D0", true);
        if (!(this.aircraft() instanceof BF_110H4)) {
            this.aircraft().hierMesh().chunkVisible("Turret1B_D0", true);
        }
        super.doFocusLeave();
    }

    public CockpitBF_110NJ_RadarOp() {
        super("3DO/Cockpit/Bf-110G-Radar/hier.him", "he111");
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
        if (this.aircraft() instanceof BF_110H4) {
            this.mesh.chunkVisible("TurretA", false);
            this.mesh.chunkVisible("TurretB", false);
            this.mesh.chunkVisible("PatronsL", false);
            this.mesh.chunkVisible("PatronsR", false);
            this.mesh.chunkVisible("Hylse1", false);
        }
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

        // +++ RadarLiSN2 +++
        this.cockpitRadarLiSN2.updateRadar();
        // --- RadarLiSN2 ---
    }
    
    public boolean isEnableFocusing() {
        if (this.aircraft().thisWeaponsName.startsWith("SN") || Property.intValue(this.aircraft().getClass(), "hasRadar", 0) != 0) {
            return super.isEnableFocusing();
        }
        HotKeyCmd.exec("aircraftView", "cockpitSwitch2"); // Switch to Gunner Cockpit if not Night Fighter
        return false;
    }

    // +++ RadarLiSN2 +++
    private RadarLiSN2Equipment cockpitRadarLiSN2 = new RadarLiSN2Equipment(this, 28, 70F, 5F, 0.2F, 0.012F, 0.011637F, 0.073296F);
    // --- RadarLiSN2 ---

    private boolean             bNeedSetUp;

    static {
        Property.set(CockpitBF_110NJ_RadarOp.class, "astatePilotIndx", 1);
    }
}
