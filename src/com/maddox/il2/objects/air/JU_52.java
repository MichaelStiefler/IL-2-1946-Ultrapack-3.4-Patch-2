package com.maddox.il2.objects.air;

import com.maddox.il2.ai.Shot;
import com.maddox.il2.ai.World;
import com.maddox.il2.engine.HierMesh;
import com.maddox.rts.Property;

public abstract class JU_52 extends Scheme6 implements TypeTransport {

    public JU_52() {
        this.bDynamoOperational = true;
        this.dynamoOrient = 0.0F;
        this.bDynamoRotary = false;
    }

    public static void prepareWeapons(Class aircraftClass, HierMesh hierMesh, String thisWeaponsName) {
        int numPara = 0;
        int numCargo = 0;
        int paraIndex = thisWeaponsName.indexOf("xPara");
        int cargoIndex = thisWeaponsName.indexOf("xCargo");
        int agentIndex = thisWeaponsName.indexOf("xAgent");
        int type = 0;
        if (paraIndex != -1) {
            type = 1;
            try {
                numPara = Integer.parseInt(thisWeaponsName.substring(0, paraIndex));
            } catch (Exception e) {}
        }
        if (agentIndex != -1) {
            type = 1;
            try {
                numPara = Integer.parseInt(thisWeaponsName.substring(0, agentIndex));
            } catch (Exception e) {}
        }
        if (cargoIndex != -1) {
            type = 2;
            try {
                numCargo = Integer.parseInt(thisWeaponsName.substring(0, cargoIndex));
            } catch (Exception e) {}
        }
        
        if (hierMesh.chunkFindCheck("CFS_D0") > -1) hierMesh.chunkVisible("CFS_D0", numPara > 0);
        
        renderLoad(hierMesh, thisWeaponsName, type, type==1?numPara:numCargo);
    }
    
    public static void renderLoad(HierMesh hierMesh, String thisWeaponsName, int type, int number) {
        boolean odds = type == 1 && number > 6;
        if (type == 1 && number < 7) {
            number *= 2;
        }
        switch (type) {
            case 1:
                hierMesh.chunkVisible("Pilot4_D0", number>0);
                hierMesh.chunkVisible("Pilot5_D0", odds && number>1);
                hierMesh.chunkVisible("Pilot6_D0", number>2);
                hierMesh.chunkVisible("Pilot7_D0", odds && number>3);
                hierMesh.chunkVisible("Pilot8_D0", number>4);
                hierMesh.chunkVisible("Pilot9_D0", odds && number>5);
                hierMesh.chunkVisible("Pilot10_D0", number>6);
                hierMesh.chunkVisible("Pilot11_D0", odds && number>7);
                hierMesh.chunkVisible("Pilot12_D0", number>8);
                hierMesh.chunkVisible("Pilot13_D0", odds && number>9);
                hierMesh.chunkVisible("Pilot14_D0", number>10);
                hierMesh.chunkVisible("Pilot15_D0", odds && number>11);
                hierMesh.chunkVisible("Helm4_D0", number>0);
                hierMesh.chunkVisible("Helm5_D0", odds && number>1);
                hierMesh.chunkVisible("Helm6_D0", number>2);
                hierMesh.chunkVisible("Helm7_D0", odds && number>3);
                hierMesh.chunkVisible("Helm8_D0", number>4);
                hierMesh.chunkVisible("Helm9_D0", odds && number>5);
                hierMesh.chunkVisible("Helm10_D0", number>6);
                hierMesh.chunkVisible("Helm11_D0", odds && number>7);
                hierMesh.chunkVisible("Helm12_D0", number>8);
                hierMesh.chunkVisible("Helm13_D0", odds && number>9);
                hierMesh.chunkVisible("Helm14_D0", number>10);
                hierMesh.chunkVisible("Helm15_D0", odds && number>11);
                for (int i=1; i<6; i++)
                    hierMesh.chunkVisible("CargoA_0" + i, false);
                break;
            case 2:
                for (int i=4; i<16; i++) {
                    hierMesh.chunkVisible("Pilot" + i + "_D0", false);
                    hierMesh.chunkVisible("Helm" + i + "_D0", false);
                }
                hierMesh.chunkVisible("CargoA_01", number>0);
                hierMesh.chunkVisible("CargoA_02", number>1);
                hierMesh.chunkVisible("CargoA_03", number>2);
                hierMesh.chunkVisible("CargoA_04", number>3);
                hierMesh.chunkVisible("CargoA_05", number>4);
                break;
            default:
                for (int i=4; i<16; i++) {
                    hierMesh.chunkVisible("Pilot" + i + "_D0", false);
                    hierMesh.chunkVisible("Helm" + i + "_D0", false);
                }
                for (int i=1; i<6; i++)
                    hierMesh.chunkVisible("CargoA_0" + i, false);
                break;
        }
    }
    
    protected void renderLoad()
    {
        int type=0;
        if (this.thisWeaponsName.indexOf("xPara") != -1) type = 1;
        if (this.thisWeaponsName.indexOf("xAgent") != -1) type = 1;
        if (this.thisWeaponsName.indexOf("xCargo") != -1) type = 2;
        if (type == 0) return;
        int bullets = 0;
        if(this.FM.CT.Weapons[3] != null)
        {
            for(int j = 0; j < this.FM.CT.Weapons[3].length; j++)
                if(this.FM.CT.Weapons[3][j] != null)
                    bullets += this.FM.CT.Weapons[3][j].countBullets();
        }
        renderLoad(this.hierMesh(), this.thisWeaponsName, type, bullets);
    }

    public void rareAction(float f, boolean flag)
    {
        renderLoad();
    }

    public void onAircraftLoaded()
    {
        super.onAircraftLoaded();
        JU_52.prepareWeapons(this.getClass(), this.hierMesh(), this.thisWeaponsName);
    }

    public void doWoundPilot(int i, float f) {
        switch (i) {
            default:
                break;

            case 2:
                if (this.FM.turret.length > 0) this.FM.turret[0].setHealth(f);
                break;
        }
    }

    public void doMurderPilot(int i) {
        switch (i) {
            case 0:
                this.hierMesh().chunkVisible("Pilot1_D0", false);
                this.hierMesh().chunkVisible("Head1_D0", false);
                this.hierMesh().chunkVisible("Pilot1_D1", true);
                break;

            case 1:
                this.hierMesh().chunkVisible("Pilot2_D0", false);
                this.hierMesh().chunkVisible("Pilot2_D1", true);
                break;

            case 2:
                this.hierMesh().chunkVisible("Pilot3_D0", false);
                this.hierMesh().chunkVisible("Pilot3_D1", true);
                break;
        }
    }

    protected void moveFan(float f) {
        if (this.bDynamoOperational) {
            this.pk = Math.abs((int) (this.FM.Vwld.length() / 14D));
            if (this.pk >= 1) this.pk = 1;
        }
        if (this.bDynamoRotary != (this.pk == 1)) {
            this.bDynamoRotary = this.pk == 1;
            this.hierMesh().chunkVisible("Cart_D0", !this.bDynamoRotary);
            this.hierMesh().chunkVisible("CartRot_D0", this.bDynamoRotary);
        }
        this.dynamoOrient = this.bDynamoRotary ? (this.dynamoOrient - 17.987F) % 360F : (float) (this.dynamoOrient - this.FM.Vwld.length() * 1.5444015264511108D) % 360F;
        this.hierMesh().chunkSetAngles("Cart_D0", 0.0F, this.dynamoOrient, 0.0F);
        super.moveFan(f);
    }

    protected void moveFlap(float f) {
        this.hierMesh().chunkSetAngles("Flap01_D0", 0.0F, -45F * f, 0.0F);
        this.hierMesh().chunkSetAngles("Flap02_D0", 0.0F, -45F * f, 0.0F);
    }

    public void msgShot(Shot shot) {
        this.setShot(shot);
        if (shot.chunkName.startsWith("Engine1") && World.Rnd().nextFloat(0.0F, 0.5F) < shot.mass) this.FM.AS.hitEngine(shot.initiator, 0, 1);
        if (shot.chunkName.startsWith("Engine2") && World.Rnd().nextFloat(0.0F, 0.5F) < shot.mass) this.FM.AS.hitEngine(shot.initiator, 1, 1);
        if (shot.chunkName.startsWith("Engine3") && World.Rnd().nextFloat(0.0F, 0.5F) < shot.mass) this.FM.AS.hitEngine(shot.initiator, 2, 1);
        if (shot.chunkName.startsWith("Turret")) this.FM.turret[0].bIsOperable = false;
        if (shot.chunkName.startsWith("Tail1") && Pd.z > 0.5D && Pd.x > -6D && Pd.x < -4.95D && World.Rnd().nextFloat() < 0.5F) this.FM.AS.hitPilot(shot.initiator, 2, (int) (shot.mass * 1000F * 0.5F));
        if (shot.chunkName.startsWith("CF") && v1.x < -0.2D && Pd.x > 2.6D && Pd.z > 0.735D && World.Rnd().nextFloat() < 0.178F) this.FM.AS.hitPilot(shot.initiator, Pd.y <= 0.0D ? 1 : 0, (int) (shot.mass * 900F));
        if (shot.chunkName.startsWith("WingLIn") && Math.abs(Pd.y) < 2.1D) this.FM.AS.hitTank(shot.initiator, 0, World.Rnd().nextInt(0, (int) (shot.mass * 30F)));
        if (shot.chunkName.startsWith("WingRIn") && Math.abs(Pd.y) < 2.1D) this.FM.AS.hitTank(shot.initiator, 0, World.Rnd().nextInt(1, (int) (shot.mass * 30F)));
        super.msgShot(shot);
    }

    public static void moveGear(HierMesh hiermesh, float f) {
    }

    protected void moveGear(float f) {
        moveGear(this.hierMesh(), f);
    }

    private boolean bDynamoOperational;
    private float   dynamoOrient;
    private boolean bDynamoRotary;
    private int     pk;

    static {
        Class class1 = JU_52.class;
        Property.set(class1, "originCountry", PaintScheme.countryGermany);
    }
}
