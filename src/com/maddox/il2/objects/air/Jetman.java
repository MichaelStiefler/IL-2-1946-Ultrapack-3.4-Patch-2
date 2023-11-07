package com.maddox.il2.objects.air;

import com.maddox.il2.ai.World;
import com.maddox.il2.engine.Actor;
import com.maddox.il2.engine.Config;
import com.maddox.il2.engine.Eff3DActor;
import com.maddox.il2.engine.Hook;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.Orient;
import com.maddox.il2.objects.ActorSimpleMesh;
import com.maddox.rts.CLASS;
import com.maddox.rts.ObjState;
import com.maddox.rts.Property;

public class Jetman extends HE_LERCHE3 implements TypeStormovik {

	public Jetman() {
		suka = new Loc();
		bToFire = false;
		this.initialized = false;
		headPos = new float[3];
		headOr = new float[3];
		flameLenFactor = new float[2];
	}
	
	public void destroy() {
		this.endFlames();
		super.destroy();
	}

	public void rareAction(float f, boolean flag) {
		super.rareAction(f, flag);
		if (!initialized)
			this.FM.Gears.H = 6.2776F;
	}

	public void doSetSootState(int i, int j) {
		for (int k = 0; k < 2; k++) {
			if (FM.AS.astateSootEffects[i][k] != null)
				Eff3DActor.finish(FM.AS.astateSootEffects[i][k]);
			FM.AS.astateSootEffects[i][k] = null;
		}
		this.endFlames();

		switch (j) {
		case 1:
			FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "ES_01"), null, 1.0F,
					"3DO/Effects/Aircraft/BlackSmallTSPD.eff", -1F);
			FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "ES_02"), null, 1.0F,
					"3DO/Effects/Aircraft/BlackSmallTSPD.eff", -1F);
			break;

		case 3:
			FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "EF_01"), null, 1.0F,
					"3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1F);
		case 2:
			FM.AS.astateSootEffects[i][0] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "EF_01"), null, 1.0F,
					"3DO/Effects/Aircraft/TurboJetman.eff", -1F);
			break;

		case 5:
			this.createFlames();
		case 4:
			FM.AS.astateSootEffects[i][1] = Eff3DActor.New(this, findHook("_Engine" + (i + 1) + "EF_01"), null, 1.0F,
					"3DO/Effects/Aircraft/BlackMediumTSPD.eff", -1F);
			break;

		default:
			break;
		}
	}

	private void createFlames() {
		this.exhaustFlames = new Actor[2];
		Hook theHook = null;
		for (int i = 0; i < 2; i++) {
			theHook = this.findHook("_Engine" + (i + 1) + "ES_01");
			this.exhaustFlames[i] = new ActorSimpleMesh("3DO/Effects/Aircraft/TurboJetmanFlame.sim");
			if (this.exhaustFlames[i] != null) {
				((ActorSimpleMesh)this.exhaustFlames[i]).mesh().setScale(1);
				this.exhaustFlames[i].pos.setBase(this, theHook, false);
				this.exhaustFlames[i].pos.changeHookToRel();
				this.exhaustFlames[i].pos.resetAsBase();
			}
		}
	}

	private void endFlames() {
		if (this.exhaustFlames != null) {
			for (int i = 0; i < 2; i++) {
				if (this.exhaustFlames[i] == null) {
					continue;
				}
				ObjState.destroy(this.exhaustFlames[i]);
			}
		}
	}

	private void updateFlameSize() {
		if (this.exhaustFlames != null) {
			for (int engineNum = 0; engineNum < 2; engineNum++) {
				if (this.exhaustFlames[engineNum] == null) {
					continue;
				}
				float newFlameLenFactor = (this.FM.EI.engines[engineNum].getPowerOutput() - 1.0F) * 10F;
				if (newFlameLenFactor < 0F)
					newFlameLenFactor = 0F;
				if (newFlameLenFactor > 1F)
					newFlameLenFactor = 1F;
				this.flameLenFactor[engineNum] = this.flameLenFactor[engineNum] * 0.95F + newFlameLenFactor * 0.05F;
				((ActorSimpleMesh)this.exhaustFlames[engineNum]).mesh().setScale(
						World.Rnd().nextFloat(0.5F * this.flameLenFactor[engineNum], 1.0F * this.flameLenFactor[engineNum]));
			}
		}
	}

	protected void moveElevator(float f) {
		hierMesh().chunkSetAngles("vVatorL_D0", 0.0F, 45.0F * f, 0.0F);
		hierMesh().chunkSetAngles("vVatorR_D0", 0.0F, -45.0F * f, 0.0F);
	}

	protected void moveAileron(float f) {
		hierMesh().chunkSetAngles("vAroneL_D0", 0.0F, -25.0F * f, 0.0F);
		hierMesh().chunkSetAngles("vAroneR_D0", 0.0F, -25.0F * f, 0.0F);
	}

	protected void moveFlap(float f) {
	}

	protected void moveAirBrake(float f) {
		hierMesh().chunkSetAngles("Flap01_D0", 0.0F, 65.0F * f, 0.0F);
		hierMesh().chunkSetAngles("Flap02_D0", 0.0F, -65.0F * f, 0.0F);
		hierMesh().chunkSetAngles("Flap03_D0", 0.0F, -65.0F * f, 0.0F);
		hierMesh().chunkSetAngles("Flap04_D0", 0.0F, 65.0F * f, 0.0F);
	}

	public void update(float f) {
		super.update(f);
		if (this.FM.AS.isMaster()) {
			if (Config.isUSE_RENDER()) {
				for (int engineNum = 0; engineNum < 2; engineNum++) {
					if ((this.FM.EI.engines[engineNum].getPowerOutput() > 0.8F)
							&& (this.FM.EI.engines[engineNum].getStage() == 6)) {
						if (this.FM.EI.engines[engineNum].getPowerOutput() > 1.00F) {
							this.FM.AS.setSootState(this, engineNum, 5);
							this.updateFlameSize();
						} else if (this.FM.EI.engines[engineNum].getPowerOutput() > 0.90F)
							this.FM.AS.setSootState(this, engineNum, 3);
						else
							this.FM.AS.setSootState(this, engineNum, 2);
					} else {
						this.FM.AS.setSootState(this, engineNum, 0);
					}
				}
			}
		}
	}

	public void movePilotsHead(float f, float f1) {
		if (Config.isUSE_RENDER() && (headTp < f1 || headTm > f1 || headYp < f || headYm > f)) {
			headTp = f1 + 0.0005F;
			headTm = f1 - 0.0005F;
			headYp = f + 0.0005F;
			headYm = f - 0.0005F;
			f *= 0.7F;
			f1 *= 0.7F;
			tmpOrLH.setYPR(0.0F, 0.0F, 0.0F);
			tmpOrLH.increment(0.0F, f, 0.0F);
			tmpOrLH.increment(f1, 0.0F, 0.0F);
			tmpOrLH.increment(0.0F, 0.0F, -0.2F * f1 + 0.05F * f);
			headOr[0] = tmpOrLH.getYaw();
			headOr[1] = tmpOrLH.getPitch();
			headOr[2] = tmpOrLH.getRoll();
			headPos[0] = 0.0005F * Math.abs(f);
			headPos[1] = -0.0001F * Math.abs(f);
			headPos[2] = 0.0F;
			hierMesh().chunkSetLocate("Head1x_D0", headPos, headOr);
		}
	}

	private float headPos[];
	private float headOr[];
	private static Orient tmpOrLH = new Orient();
	private float headYp;
	private float headTp;
	private float headYm;
	private float headTm;
	private float flameLenFactor[];
	private Actor exhaustFlames[];
	private boolean initialized;

	static {
		Class class1 = CLASS.THIS();
		new NetAircraft.SPAWN(class1);
		Property.set(class1, "iconFar_shortClassName", "Jetman");
		Property.set(class1, "meshName", "3DO/Plane/Jetman/hier.him");
		Property.set(class1, "PaintScheme", new PaintSchemeFMPar06());
		Property.set(class1, "originCountry", PaintScheme.countryGermany);
		Property.set(class1, "yearService", 1945F);
		Property.set(class1, "yearExpired", 1948F);
		Property.set(class1, "FlightModel", "FlightModels/Jetman.fmd");
		Property.set(class1, "cockpitClass", new Class[] { Cockpit_JET.class });
		Property.set(class1, "LOSElevation", 1.00705F);
		Aircraft.weaponTriggersRegister(class1, new int[] { 1, 1, 2, 3 });
		Aircraft.weaponHooksRegister(class1, new String[] { "_CANNON01", "_CANNON02", "_ExternalRock01", "_ExternalBomb01" });
	}
}
