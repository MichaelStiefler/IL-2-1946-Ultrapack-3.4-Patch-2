package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class RocketGunJetManX4Mini extends RocketGun {

	public RocketGunJetManX4Mini() {
	}

	public void setRocketTimeLife(float f) {
		timeLife = 30F;
	}

	static {
		Class class1 = RocketGunJetManX4Mini.class;
		Property.set(class1, "bulletClass", (Object)RocketJetManX4Mini.class);
		Property.set(class1, "bullets", 1);
		Property.set(class1, "shotFreq", 0.25F);
		Property.set(class1, "sound", "weapon.rocketgun_132");
	}
}
