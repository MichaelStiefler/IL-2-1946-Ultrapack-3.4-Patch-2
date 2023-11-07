package com.maddox.il2.fm;

import java.util.ArrayList;
import java.util.Arrays;

import com.maddox.JGP.Vector3d;
import com.maddox.il2.ai.BulletEmitter;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.GunGeneric;
import com.maddox.il2.objects.weapons.BombGun;
import com.maddox.il2.objects.weapons.FuelTank;
import com.maddox.il2.objects.weapons.Pylon;
import com.maddox.il2.objects.weapons.PylonA5MPLN1;
import com.maddox.il2.objects.weapons.PylonA6MPLN1;
import com.maddox.il2.objects.weapons.PylonA6MPLN2;
import com.maddox.il2.objects.weapons.PylonB25PLN2;
import com.maddox.il2.objects.weapons.PylonB25RAIL;
import com.maddox.il2.objects.weapons.PylonB5NPLN0;
import com.maddox.il2.objects.weapons.PylonB5NPLN1;
import com.maddox.il2.objects.weapons.PylonB5NPLN2;
import com.maddox.il2.objects.weapons.PylonB5NPLN3;
import com.maddox.il2.objects.weapons.PylonB6NPLN1;
import com.maddox.il2.objects.weapons.PylonBEAUPLN1;
import com.maddox.il2.objects.weapons.PylonBEAUPLN2;
import com.maddox.il2.objects.weapons.PylonBEAUPLN3;
import com.maddox.il2.objects.weapons.PylonBEAUPLN4;
import com.maddox.il2.objects.weapons.PylonBF110R3;
import com.maddox.il2.objects.weapons.PylonBF110R4;
import com.maddox.il2.objects.weapons.PylonDer16TB3Fake;
import com.maddox.il2.objects.weapons.PylonETC250;
import com.maddox.il2.objects.weapons.PylonETC50;
import com.maddox.il2.objects.weapons.PylonETC501FW190;
import com.maddox.il2.objects.weapons.PylonETC50Bf109;
import com.maddox.il2.objects.weapons.PylonETC71;
import com.maddox.il2.objects.weapons.PylonETC900;
import com.maddox.il2.objects.weapons.PylonF4FPLN1;
import com.maddox.il2.objects.weapons.PylonF4FPLN2;
import com.maddox.il2.objects.weapons.PylonF4UPLN2;
import com.maddox.il2.objects.weapons.PylonF4UPLN3;
import com.maddox.il2.objects.weapons.PylonF6FPLN1;
import com.maddox.il2.objects.weapons.PylonF6FPLN2;
import com.maddox.il2.objects.weapons.PylonHS129BK37;
import com.maddox.il2.objects.weapons.PylonHS129BK75;
import com.maddox.il2.objects.weapons.PylonHS129MG17S;
import com.maddox.il2.objects.weapons.PylonHS129MK101;
import com.maddox.il2.objects.weapons.PylonHS129MK103;
import com.maddox.il2.objects.weapons.PylonHs129BombRackC250;
import com.maddox.il2.objects.weapons.PylonHs129BombRackC4x50;
import com.maddox.il2.objects.weapons.PylonHs129BombRackL;
import com.maddox.il2.objects.weapons.PylonHs129BombRackR;
import com.maddox.il2.objects.weapons.PylonKI43PLN1;
import com.maddox.il2.objects.weapons.PylonKI84PLN2;
import com.maddox.il2.objects.weapons.PylonKMB;
import com.maddox.il2.objects.weapons.PylonMG15120;
import com.maddox.il2.objects.weapons.PylonMG15120Internal;
import com.maddox.il2.objects.weapons.PylonMG15120x2;
import com.maddox.il2.objects.weapons.PylonMe262_R4M_Left;
import com.maddox.il2.objects.weapons.PylonMe262_R4M_Right;
import com.maddox.il2.objects.weapons.PylonMiG_3_BK;
import com.maddox.il2.objects.weapons.PylonMk103;
import com.maddox.il2.objects.weapons.PylonMk108;
import com.maddox.il2.objects.weapons.PylonN1K1PLN1;
import com.maddox.il2.objects.weapons.PylonP38GUNPOD;
import com.maddox.il2.objects.weapons.PylonP38RAIL3FL;
import com.maddox.il2.objects.weapons.PylonP38RAIL3FR;
import com.maddox.il2.objects.weapons.PylonP38RAIL3WL;
import com.maddox.il2.objects.weapons.PylonP38RAIL3WR;
import com.maddox.il2.objects.weapons.PylonP38RAIL5;
import com.maddox.il2.objects.weapons.PylonP39PLN1;
import com.maddox.il2.objects.weapons.PylonP51PLN2;
import com.maddox.il2.objects.weapons.PylonP63CGUNPOD;
import com.maddox.il2.objects.weapons.PylonP63CPLN2;
import com.maddox.il2.objects.weapons.PylonPE8_FAB100;
import com.maddox.il2.objects.weapons.PylonPE8_FAB250;
import com.maddox.il2.objects.weapons.PylonR5BombRackC;
import com.maddox.il2.objects.weapons.PylonR5BombRackL;
import com.maddox.il2.objects.weapons.PylonRO_4andHalfInch_3;
import com.maddox.il2.objects.weapons.PylonRO_82_1;
import com.maddox.il2.objects.weapons.PylonRO_82_3;
import com.maddox.il2.objects.weapons.PylonRO_WfrGr21;
import com.maddox.il2.objects.weapons.PylonRO_WfrGr21Dual;
import com.maddox.il2.objects.weapons.PylonS328;
import com.maddox.il2.objects.weapons.PylonSpitC;
import com.maddox.il2.objects.weapons.PylonSpitL;
import com.maddox.il2.objects.weapons.PylonSpitR;
import com.maddox.il2.objects.weapons.PylonSpitROCK;
import com.maddox.il2.objects.weapons.PylonTEMPESTPLN1;
import com.maddox.il2.objects.weapons.PylonTEMPESTPLN2;
import com.maddox.il2.objects.weapons.PylonTEMPESTPLN3;
import com.maddox.il2.objects.weapons.PylonTEMPESTPLN4;
import com.maddox.il2.objects.weapons.PylonVAP250;
import com.maddox.il2.objects.weapons.RocketBombGun;
import com.maddox.il2.objects.weapons.RocketGun;
import com.maddox.rts.Property;
import com.maddox.rts.SectFile;

public class Mass {

    public Mass() {
        this.bFuelTanksLoaded = false;
        this.nitro = 0.0F;
        this.maxNitro = 1.0F;
        this.pylonCoeff = 0.0F;
        this.pylonCoeffB = 0.0F;
        this.pylonCoeffR = 0.0F;
        this.fuelCoeff = 0.0F;
    }

    public void load(SectFile sectfile, FlightModelMain flightmodelmain) {
        String s = "Mass";
        String s1 = "Critical Mass in " + sectfile.toString();
        this.FM = flightmodelmain;
        float f = sectfile.get(s, "Empty", 0.0F);
        if (f == 0.0F) throw new RuntimeException(s1);
        this.massEmpty = f;
        f = sectfile.get(s, "Oil", -1F);
        if (f == -1F) throw new RuntimeException(s1);
        this.massEmpty += f;
        f = sectfile.get("Aircraft", "Crew", 0.0F);
        if (f == 0.0F) throw new RuntimeException(s1);
        this.massEmpty += f * 90F;
        this.referenceWeight = this.massEmpty;
        f = sectfile.get(s, "TakeOff", 0.0F);
        if (f == 0.0F) throw new RuntimeException(s1);
        this.maxWeight = f;
        f = sectfile.get(s, "Fuel", 0.0F);
        if (f == 0.0F) throw new RuntimeException(s1);
        else {
            this.maxFuel = f;
            this.mass = this.massEmpty;
            this.fuel = this.maxFuel;
            f = sectfile.get(s, "Nitro", 0.0F);
            this.maxNitro = this.nitro = f;
            this.referenceWeight += this.fuel + this.maxNitro;
        }
        this.loadMassFactors(sectfile);
    }

    public void onFuelTanksChanged() {
        this.bFuelTanksLoaded = true;
        this.fuelTanks = new FuelTank[0];
    }

    public boolean requestFuel(float f) {
        // TODO: By SAS~Storebror: Mass Factors +++
        // this.mass = this.massEmpty + this.fuel + this.nitro + this.parasiteMass;
        this.mass = this.massEmpty * this.getMassFactor() + (this.fuel + this.nitro) * this.getFuelFactor() + this.parasiteMass * this.getParasiteFactor();
        // TODO: By SAS~Storebror: Mass Factors ---
        if (!this.bFuelTanksLoaded) {
            this.fuelTanks = this.FM.CT.getFuelTanks();
            this.bFuelTanksLoaded = true;
        }
        if (this.fuelTanks.length != 0) {
            this.fuelCoeff = 1.0F;
            float f1 = 0.0F;
            for (int i = 0; i < this.fuelTanks.length; i++)
                f1 += this.fuelTanks[i].getFuel(f / this.fuelTanks.length);

            if (f1 > 0.0F) return true;
        } else this.fuelCoeff = 0.0F;
        this.fuel -= f;
        if (this.fuel < 0.0F) {
            this.fuel = 0.0F;
            return false;
        } else return true;
    }

    public boolean requestNitro(float f) {
        // TODO: By SAS~Storebror: Mass Factors +++
        // this.mass = this.massEmpty + this.fuel + this.nitro + this.parasiteMass;
        this.mass = this.massEmpty * this.getMassFactor() + (this.fuel + this.nitro) * this.getFuelFactor() + this.parasiteMass * this.getParasiteFactor();
        // TODO: By SAS~Storebror: Mass Factors ---
        this.nitro -= f;
        if (this.nitro < 0.0F) {
            this.nitro = 0.0F;
            return false;
        } else return true;
    }

    public float getFullMass() {
        return this.mass;
    }

    public void computeParasiteMass(BulletEmitter abulletemitter[][]) {
        this.parasiteMass = 0.0F;
        this.parasiteJx = 0.0F;
        for (int i = 0; i < abulletemitter.length; i++) {
            if (abulletemitter[i] == null || abulletemitter[i].length <= 0) continue;
            for (int j = 0; j < abulletemitter[i].length; j++) {
                if (abulletemitter[i][j] instanceof GunGeneric) {
                    int k = abulletemitter[i][j].countBullets();
                    float f = abulletemitter[i][j].bulletMassa() * (k < 0 ? 50 : k) * 3F;
                    float f3 = (float) ((Actor) abulletemitter[i][j]).pos.getRelPoint().z;
                    float f6 = (float) ((Actor) abulletemitter[i][j]).pos.getRelPoint().y;
                    this.parasiteJx += (f3 * f3 + f6 * f6) * f;
                    this.parasiteMass += f;
                }
                if (abulletemitter[i][j] instanceof BombGun || abulletemitter[i][j] instanceof RocketGun || abulletemitter[i][j] instanceof RocketBombGun) {
                    int l = abulletemitter[i][j].countBullets();
                    float f1 = abulletemitter[i][j].bulletMassa() * (l < 0 ? 1 : l);
                    float f4 = 0.0F;
                    float f7 = 2.0F;
                    if (l > 0) if ((abulletemitter[i][j] instanceof BombGun || abulletemitter[i][j] instanceof RocketBombGun) && abulletemitter[i][j].getHookName().startsWith("_External")) this.pylonCoeffB = 1.5F;
                    else this.pylonCoeffR = 1.0F;
                    this.pylonCoeff = this.pylonCoeffB + this.pylonCoeffR;
                    this.parasiteJx += (f4 * f4 + f7 * f7) * f1;
                    this.parasiteMass += f1;
                }
                if (!(abulletemitter[i][j] instanceof Pylon)) continue;
                float f2;
                if (abulletemitter[i][j] instanceof PylonRO_82_1 || abulletemitter[i][j] instanceof PylonRO_82_3 || abulletemitter[i][j] instanceof PylonPE8_FAB100 || abulletemitter[i][j] instanceof PylonPE8_FAB250
                        || abulletemitter[i][j] instanceof PylonMG15120Internal)
                    f2 = 0.0F;
                else if (abulletemitter[i][j] instanceof PylonB25RAIL) f2 = 5F;
                else if (abulletemitter[i][j] instanceof PylonBEAUPLN2) f2 = 108F;
                else if (abulletemitter[i][j] instanceof PylonBEAUPLN3) f2 = 108F;
                else if (abulletemitter[i][j] instanceof PylonF4FPLN1) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonF6FPLN2) f2 = 5F;
                else if (abulletemitter[i][j] instanceof PylonP38RAIL3FL) f2 = 36.3F;
                else if (abulletemitter[i][j] instanceof PylonP38RAIL3FR) f2 = 36.3F;
                else if (abulletemitter[i][j] instanceof PylonP38RAIL3WL) f2 = 36.3F;
                else if (abulletemitter[i][j] instanceof PylonP38RAIL3WR) f2 = 36.3F;
                else if (abulletemitter[i][j] instanceof PylonP38RAIL5) f2 = 50F;
                else if (abulletemitter[i][j] instanceof PylonRO_4andHalfInch_3) f2 = 36.3F;
                else if (abulletemitter[i][j] instanceof PylonRO_WfrGr21) f2 = 37F;
                else if (abulletemitter[i][j] instanceof PylonRO_WfrGr21Dual) f2 = 70F;
                else if (abulletemitter[i][j] instanceof PylonSpitL) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonSpitR) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonSpitROCK) f2 = 5F;
                else if (abulletemitter[i][j] instanceof PylonTEMPESTPLN3) f2 = 108F;
                else if (abulletemitter[i][j] instanceof PylonTEMPESTPLN4) f2 = 108F;
                else if (abulletemitter[i][j] instanceof PylonA5MPLN1) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonA6MPLN2) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonETC71) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonF4FPLN2) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonS328) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonHs129BombRackL) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonHs129BombRackR) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonR5BombRackC) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonR5BombRackL) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonR5BombRackL) f2 = 10F;
                else if (abulletemitter[i][j] instanceof PylonETC50) f2 = 40F;
                else if (abulletemitter[i][j] instanceof PylonETC50Bf109) f2 = 40F;
                else if (abulletemitter[i][j] instanceof PylonETC501FW190) f2 = 60F;
                else if (abulletemitter[i][j] instanceof PylonP51PLN2) f2 = 9F;
                else if (abulletemitter[i][j] instanceof PylonP63CPLN2) f2 = 7F;
                else if (abulletemitter[i][j] instanceof PylonSpitC) f2 = 23F;
                else if (abulletemitter[i][j] instanceof PylonA6MPLN1) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonB25PLN2) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonBEAUPLN1) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonETC250) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonETC900) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonF6FPLN1) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonKI43PLN1) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonKI84PLN2) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonKMB) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonMe262_R4M_Left) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonMe262_R4M_Right) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonN1K1PLN1) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonP39PLN1) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonTEMPESTPLN1) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonTEMPESTPLN2) f2 = 30F;
                else if (abulletemitter[i][j] instanceof PylonHs129BombRackC250) f2 = 50F;
                else if (abulletemitter[i][j] instanceof PylonHs129BombRackC4x50) f2 = 50F;
                else if (abulletemitter[i][j] instanceof PylonB5NPLN1) f2 = 70F;
                else if (abulletemitter[i][j] instanceof PylonB5NPLN2) f2 = 70F;
                else if (abulletemitter[i][j] instanceof PylonB5NPLN3) f2 = 70F;
                else if (abulletemitter[i][j] instanceof PylonB6NPLN1) f2 = 70F;
                else if (abulletemitter[i][j] instanceof PylonF4UPLN2) f2 = 70F;
                else if (abulletemitter[i][j] instanceof PylonF4UPLN3) f2 = 70F;
                else if (abulletemitter[i][j] instanceof PylonB5NPLN0) f2 = 150F;
                else if (abulletemitter[i][j] instanceof PylonBEAUPLN4) f2 = 150F;
                else if (abulletemitter[i][j] instanceof PylonDer16TB3Fake) f2 = 150F;
                else if (abulletemitter[i][j] instanceof PylonVAP250) f2 = 150F;
                else if (abulletemitter[i][j] instanceof PylonBF110R3) f2 = 150F;
                else if (abulletemitter[i][j] instanceof PylonBF110R4) f2 = 350F;
                else if (abulletemitter[i][j] instanceof PylonMG15120x2) f2 = 100F;
                else if (abulletemitter[i][j] instanceof PylonHS129BK37) f2 = 350F;
                else if (abulletemitter[i][j] instanceof PylonHS129MG17S) f2 = 80F;
                else if (abulletemitter[i][j] instanceof PylonHS129MK101) f2 = 250F;
                else if (abulletemitter[i][j] instanceof PylonHS129MK103) f2 = 196F;
                else if (abulletemitter[i][j] instanceof PylonHS129BK75) {
                    if (this.FM.isPlayers() && ((RealFlightModel) this.FM).isRealMode()) f2 = 905F;
                    else f2 = 300F;
                } else if (abulletemitter[i][j] instanceof PylonMG15120) f2 = 61F;
                else if (abulletemitter[i][j] instanceof PylonMiG_3_BK) f2 = 35F;
                else if (abulletemitter[i][j] instanceof PylonMk103) f2 = 140F;
                else if (abulletemitter[i][j] instanceof PylonMk108) f2 = 90F;
                else if (abulletemitter[i][j] instanceof PylonP38GUNPOD) f2 = 90F;
                else if (abulletemitter[i][j] instanceof PylonP63CGUNPOD) f2 = 45F;

                // +++ TODO: By SAS~Storebror: Parasite Pylon Mass from Pylon Properties +++
//				else f2 = 150F; // To hell with one static parasite mass value for all unknown pylons, let alone such a massive something...
                else f2 = Property.floatValue(abulletemitter[i][j].getClass(), "mass", 50F);
                // --- TODO: By SAS~Storebror: Parasite Pylon Mass from Pylon Properties ---

                float f5 = (float) ((Actor) abulletemitter[i][j]).pos.getRelPoint().z;
                float f8 = (float) ((Actor) abulletemitter[i][j]).pos.getRelPoint().y;
                this.parasiteJx += (f5 * f5 + f8 * f8) * f2;
                this.parasiteMass += f2;
            }

        }

        this.pylonCoeffB = 0.0F;
        this.pylonCoeffR = 0.0F;
        // TODO: By SAS~Storebror: Mass Factors +++
        this.mass = this.massEmpty * this.getMassFactor() + (this.fuel + this.nitro) * this.getFuelFactor() + this.parasiteMass * this.getParasiteFactor();
        // TODO: By SAS~Storebror: Mass Factors ---
    }

    public void computeFullJ(Vector3d vector3d, Vector3d vector3d1) {
        // TODO: By SAS~Storebror: Mass Factors +++
        // vector3d.scale(this.massEmpty, vector3d1);
     // vector3d.x += this.parasiteJx;
        vector3d.scale(this.massEmpty * this.getMassFactor(), vector3d1);
        vector3d.x += this.parasiteJx * this.getParasiteFactor();
        // TODO: By SAS~Storebror: Mass Factors ---
    }
    
    // TODO: By SAS~Storebror: Mass Factors +++
    private float parseFloat(String floatString, float defaultValue) {
        try
        {
            return Float.parseFloat(floatString);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public void loadMassFactors(SectFile sectfile) {
        int sectionIndex = sectfile.sectionIndex("MassFactors");
        if (sectionIndex == -1)
            return;
        ArrayList massFactorList = new ArrayList();
        ArrayList massFactorAltitudeList = new ArrayList();
        ArrayList parasiteFactorList = new ArrayList();
        ArrayList parasiteFactorAltitudeList = new ArrayList();
        ArrayList fuelFactorList = new ArrayList();
        ArrayList fuelFactorAltitudeList = new ArrayList();
        ArrayList varNames = new ArrayList(Arrays.asList(new String[] { "MassFactor", "ParasiteFactor", "FuelFactor" }));
        int vars = sectfile.vars(sectionIndex);
        for (int var = 0; var < vars; var++) {
            try {
                String varName = sectfile.var(sectionIndex, var);
                if (varNames.contains(varName)) {
                    float factor = parseFloat(sectfile.value(sectionIndex, var), Float.NaN);
                    float alt = Float.NaN;
                    if (!Float.isNaN(factor)) {
                        if (var + 1 < vars) {
                            String var2Name = sectfile.var(sectionIndex, var + 1);
                            if (var2Name.equalsIgnoreCase("Altitude")) {
                                alt = parseFloat(sectfile.value(sectionIndex, ++var), Float.NaN);
                            }
                        }
                    }
                    switch (varNames.indexOf(varName)) {
                        case 0:
                            if (Float.isNaN(alt)) massFactor = factor;
                            else {
                                if (massFactor == 1F) {
                                    massFactorAltitudeList.add(new Float(alt));
                                    massFactorList.add(new Float(factor));
                                }
                            }
                            break;
                        case 1:
                            if (Float.isNaN(alt)) parasiteFactor = factor;
                            else {
                                if (parasiteFactor == 1F) {
                                    parasiteFactorAltitudeList.add(new Float(alt));
                                    parasiteFactorList.add(new Float(factor));
                                }
                            }
                            break;
                        case 2:
                            if (Float.isNaN(alt)) fuelFactor = factor;
                            else {
                                if (fuelFactor == 1F) {
                                    fuelFactorAltitudeList.add(new Float(alt));
                                    fuelFactorList.add(new Float(factor));
                                }
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if (massFactorList.size() == 1) {
            massFactor = ((Float)massFactorList.get(0)).floatValue();
        } else if (massFactorList.size() > 1) {
            massFactorAltitudes = new float[massFactorList.size()];
            massFactors = new float[massFactorList.size()];
            for (int index = 0; index < massFactorList.size(); index++) {
                massFactorAltitudes[index] = ((Float)massFactorAltitudeList.get(index)).floatValue();
                massFactors[index] = ((Float)massFactorList.get(index)).floatValue();
                FlightModelMain.sort(massFactorAltitudes, massFactors);
            }
        }

        if (parasiteFactorList.size() == 1) {
            parasiteFactor = ((Float)parasiteFactorList.get(0)).floatValue();
        } else if (parasiteFactorList.size() > 1) {
            parasiteFactorAltitudes = new float[parasiteFactorList.size()];
            parasiteFactors = new float[parasiteFactorList.size()];
            for (int index = 0; index < parasiteFactorList.size(); index++) {
                parasiteFactorAltitudes[index] = ((Float)parasiteFactorAltitudeList.get(index)).floatValue();
                parasiteFactors[index] = ((Float)parasiteFactorList.get(index)).floatValue();
                FlightModelMain.sort(parasiteFactorAltitudes, parasiteFactors);
            }
        }

        if (fuelFactorList.size() == 1) {
            fuelFactor = ((Float)fuelFactorList.get(0)).floatValue();
        } else if (fuelFactorList.size() > 1) {
            fuelFactorAltitudes = new float[fuelFactorList.size()];
            fuelFactors = new float[fuelFactorList.size()];
            for (int index = 0; index < fuelFactorList.size(); index++) {
                fuelFactorAltitudes[index] = ((Float)fuelFactorAltitudeList.get(index)).floatValue();
                fuelFactors[index] = ((Float)fuelFactorList.get(index)).floatValue();
                FlightModelMain.sort(fuelFactorAltitudes, fuelFactors);
            }
        }
        
        if (MASSFACTOR_DEBUG) {
            if (massFactors == null) {
                System.out.println("MassFactor=" + massFactor);
            } else {
                for (int i=0; i<massFactors.length; i++) {
                    System.out.println("MassFactor for " + massFactorAltitudes[i] + "m altitude = " + massFactors[i]);
                }
            }
            if (parasiteFactors == null) {
                System.out.println("ParasiteFactor=" + parasiteFactor);
            } else {
                for (int i=0; i<parasiteFactors.length; i++) {
                    System.out.println("ParasiteFactor for " + parasiteFactorAltitudes[i] + "m altitude = " + parasiteFactors[i]);
                }
            }
            if (fuelFactors == null) {
                System.out.println("FuelFactor=" + fuelFactor);
            } else {
                for (int i=0; i<fuelFactors.length; i++) {
                    System.out.println("FuelFactor for " + fuelFactorAltitudes[i] + "m altitude = " + fuelFactors[i]);
                }
            }
        }
    }
    
    public float getMassFactor() {
        if (this.massFactors == null) return this.massFactor;
        return FlightModelMain.interpolate(this.massFactorAltitudes, this.massFactors, this.FM.getAltitude());
    }
    
    public float getFuelFactor() {
        if (this.fuelFactors == null) return this.fuelFactor;
        return FlightModelMain.interpolate(this.fuelFactorAltitudes, this.fuelFactors, this.FM.getAltitude());
    }
    
    public float getParasiteFactor() {
        if (this.parasiteFactors == null) return this.parasiteFactor;
        return FlightModelMain.interpolate(this.parasiteFactorAltitudes, this.parasiteFactors, this.FM.getAltitude());
    }
    
    private float massFactor = 1F;
    private float[] massFactors = null;
    private float[] massFactorAltitudes = null;
    private float parasiteFactor = 1F;
    private float[] parasiteFactors = null;
    private float[] parasiteFactorAltitudes = null;
    private float fuelFactor = 1F;
    private float[] fuelFactors = null;
    private float[] fuelFactorAltitudes = null;
    private static final boolean MASSFACTOR_DEBUG = false;
    // TODO: By SAS~Storebror: Mass Factors ---

    private FlightModelMain FM;
    public float            massEmpty;
    public float            mass;
    public float            maxWeight;
    private float           parasiteMass;
    private float           parasiteJx;
    public float            fuel;
    public float            maxFuel;
    private FuelTank        fuelTanks[];
    private boolean         bFuelTanksLoaded;
    public float            nitro;
    public float            maxNitro;
    public float            referenceWeight;
    public float            pylonCoeff;
    private float           pylonCoeffB;
    private float           pylonCoeffR;
    public float            fuelCoeff;
}
