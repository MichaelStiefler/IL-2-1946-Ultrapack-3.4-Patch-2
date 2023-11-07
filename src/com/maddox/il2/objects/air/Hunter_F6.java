package com.maddox.il2.objects.air;

import com.maddox.il2.objects.weapons.GuidedMissileUtils;
import com.maddox.rts.Property;
import com.maddox.rts.Time;

public class Hunter_F6 extends Hunter implements TypeGuidedMissileCarrier, TypeCountermeasure, TypeThreatDetector {
    private GuidedMissileUtils guidedMissileUtils = new GuidedMissileUtils(this);

    public Hunter_F6() {
        this.bToFire = false;
        this.guidedMissileUtils = new GuidedMissileUtils(this);
    }

    private boolean hasChaff          = false; // Aircraft is equipped with Chaffs yes/no
    private boolean hasFlare          = false; // Aircraft is equipped with Flares yes/no
    private long    lastChaffDeployed = 0L;             // Last Time when Chaffs have been deployed
    private long    lastFlareDeployed = 0L;             // Last Time when Flares have been deployed

    public long getChaffDeployed() {
        if (this.hasChaff) {
            return this.lastChaffDeployed;
        }
        return 0L;
    }

    public long getFlareDeployed() {
        if (this.hasFlare) {
            return this.lastFlareDeployed;
        }
        return 0L;
    }

    private long lastCommonThreatActive        = 0L;             // Last Time when a common threat was reported
    private long intervalCommonThreat          = 1000L; // Interval (milliseconds) at which common threats should be dealt with (i.e. duration of warning sound / light)
    private long lastRadarLockThreatActive     = 0L;             // Last Time when a radar lock threat was reported
    private long intervalRadarLockThreat       = 1000L; // Interval (milliseconds) at which radar lock threats should be dealt with (i.e. duration of warning sound / light)
    private long lastMissileLaunchThreatActive = 0L;             // Last Time when a missile launch threat was reported
    private long intervalMissileLaunchThreat   = 1000L; // Interval (milliseconds) at which missile launch threats should be dealt with (i.e. duration of warning sound / light)

    public void setCommonThreatActive() {
        long curTime = Time.current();
        if ((curTime - this.lastCommonThreatActive) > this.intervalCommonThreat) {
            this.lastCommonThreatActive = curTime;
            this.doDealCommonThreat();
        }
    }

    public void setRadarLockThreatActive() {
        long curTime = Time.current();
        if ((curTime - this.lastRadarLockThreatActive) > this.intervalRadarLockThreat) {
            this.lastRadarLockThreatActive = curTime;
            this.doDealRadarLockThreat();
        }
    }

    public void setMissileLaunchThreatActive() {
        long curTime = Time.current();
        if ((curTime - this.lastMissileLaunchThreatActive) > this.intervalMissileLaunchThreat) {
            this.lastMissileLaunchThreatActive = curTime;
            this.doDealMissileLaunchThreat();
        }
    }

    private void doDealCommonThreat() { // Must be filled with life for A/Cs capable of dealing with common Threats
    }

    private void doDealRadarLockThreat() { // Must be filled with life for A/Cs capable of dealing with radar lock Threats
    }

    private void doDealMissileLaunchThreat() {// Must be filled with life for A/Cs capable of dealing with missile launch Threats

    }

    public GuidedMissileUtils getGuidedMissileUtils() {
        return this.guidedMissileUtils;
    }

    public void onAircraftLoaded() {
        super.onAircraftLoaded();
        this.guidedMissileUtils.onAircraftLoaded();
    }

    public void update(float f) {
        super.update(f);
        this.guidedMissileUtils.update();
    }

    public boolean bToFire;

    static {
        Class airClass = Hunter_F6.class;
        new NetAircraft.SPAWN(airClass);
        Property.set(airClass, "iconFar_shortClassName", "Hunter");
        Property.set(airClass, "meshName", "3DO/Plane/Hunter(Multi1)/hier_F6.him");
        Property.set(airClass, "PaintScheme", new PaintSchemeFMPar06());
        Property.set(airClass, "yearService", 1949.9F);
        Property.set(airClass, "yearExpired", 1960.3F);
        Property.set(airClass, "FlightModel", "FlightModels/HunterF6.fmd");
        Property.set(airClass, "cockpitClass", new Class[] { CockpitHunter.class });
        Property.set(airClass, "LOSElevation", 0.725F);
        Aircraft.weaponTriggersRegister(airClass, new int[] { 0, 0, 0, 0, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 9, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 });
        Aircraft.weaponHooksRegister(airClass,
                new String[] { "_MGUN01", "_MGUN02", "_MGUN03", "_MGUN04", "_ExternalDev01", "_ExternalDev02", "_ExternalDev03", "_ExternalDev04", "_ExternalDev05", "_ExternalDev06", "_ExternalDev07", "_ExternalDev08", "_ExternalDev09", "_ExternalDev10", "_ExternalDev11", "_ExternalDev12", "_ExternalDev13", "_ExternalDev14", "_ExternalDev15", "_ExternalDev16", "_ExternalDev17", "_ExternalDev18", "_ExternalDev19", "_ExternalDev20", "_ExternalDev21", "_ExternalDev22", "_ExternalDev23", "_ExternalDev24", "_ExternalDev25", "_ExternalDev26", "_ExternalDev27", "_ExternalDev28", "_ExternalDev29", "_ExternalDev30", "_ExternalDev31", "_ExternalDev32", "_ExternalDev33", "_ExternalDev34", "_ExternalDev35", "_ExternalDev36", "_ExternalDev37", "_ExternalDev38", "_ExternalDev39", "_ExternalDev40", "_ExternalDev41", "_ExternalDev42", "_ExternalDev43", "_ExternalDev44", "_ExternalDev45", "_ExternalDev46", "_ExternalRock01", "_ExternalRock02", "_ExternalRock03", "_ExternalRock04", "_ExternalRock05", "_ExternalRock06",
                        "_ExternalRock07", "_ExternalRock08", "_ExternalRock09", "_ExternalRock10", "_ExternalRock11", "_ExternalRock12", "_ExternalRock13", "_ExternalRock14", "_ExternalRock15", "_ExternalRock16", "_ExternalRock17", "_ExternalRock18", "_ExternalRock19", "_ExternalRock20", "_ExternalRock21", "_ExternalRock22", "_ExternalRock23", "_ExternalRock24", "_ExternalRock25", "_ExternalRock26", "_ExternalRock27", "_ExternalRock28", "_ExternalRock29", "_ExternalRock30", "_ExternalRock31", "_ExternalRock32", "_ExternalRock33", "_ExternalRock34", "_ExternalRock35", "_ExternalRock36", "_ExternalRock37", "_ExternalRock38", "_ExternalRock39", "_ExternalRock40", "_ExternalRock41", "_ExternalRock42", "_ExternalRock43", "_ExternalRock44", "_ExternalRock45", "_ExternalRock46", "_ExternalRock47", "_ExternalRock48", "_ExternalRock49", "_ExternalRock50", "_ExternalRock51", "_ExternalRock52", "_ExternalRock53", "_ExternalRock54", "_ExternalRock55", "_ExternalRock56", "_ExternalRock57", "_ExternalRock58",
                        "_ExternalRock59", "_ExternalRock60", "_ExternalRock61", "_ExternalRock62", "_ExternalRock63", "_ExternalRock64", "_ExternalRock65", "_ExternalRock66", "_ExternalRock67", "_ExternalRock68", "_ExternalRock69", "_ExternalRock70", "_ExternalRock71", "_ExternalRock72", "_ExternalRock73", "_ExternalRock74", "_ExternalRock75", "_ExternalRock76", "_ExternalRock77", "_ExternalRock78", "_ExternalRock79", "_ExternalRock80", "_ExternalRock81", "_ExternalRock82", "_ExternalRock83", "_ExternalRock84", "_ExternalRock85", "_ExternalRock86", "_ExternalRock87", "_ExternalRock88", "_ExternalRock89", "_ExternalRock89", "_ExternalRock90", "_ExternalRock90", "_ExternalRock91", "_ExternalRock91", "_ExternalRock92", "_ExternalRock92", "_ExternalRock93", "_ExternalRock94", "_ExternalRock95", "_ExternalRock96", "_ExternalRock97", "_ExternalRock98", "_ExternalRock99", "_ExternalRock100", "_ExternalRock101", "_ExternalRock102", "_ExternalRock103", "_ExternalRock104", "_ExternalRock105", "_ExternalRock106",
                        "_ExternalBomb01", "_ExternalBomb02", "_ExternalBomb03", "_ExternalBomb04", "_ExternalBomb05", "_ExternalBomb06", "_ExternalBomb07", "_ExternalBomb08", "_ExternalBomb09", "_ExternalBomb10", "_ExternalBomb11", "_ExternalBomb12" });
    }
}
