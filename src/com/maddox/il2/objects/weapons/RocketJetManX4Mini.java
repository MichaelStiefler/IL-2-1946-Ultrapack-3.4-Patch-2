package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.rts.Property;
import com.maddox.rts.Spawn;

public class RocketJetManX4Mini extends RocketX4 {

	static {
		Class class1 = RocketJetManX4Mini.class;
		Property.set(class1, "mesh", "3do/arms/JetManX4Mini/mono.sim");
		Property.set(class1, "sprite", "3DO/Effects/Tracers/GuidedRocket/Black.eff");
		Property.set(class1, "flame", "3do/effects/rocket/mono.sim");
		Property.set(class1, "smoke", "3DO/Effects/Tracers/GuidedRocket/White.eff");
		Property.set(class1, "emitColor", new Color3f(1.0F, 1.0F, 0.5F));
		Property.set(class1, "emitLen", 50F);
		Property.set(class1, "emitMax", 1.0F);
		Property.set(class1, "sound", "weapon.rocket_132");
		Property.set(class1, "radius", 40F);
		Property.set(class1, "timeLife", 30F);
		Property.set(class1, "timeFire", 33F);
		Property.set(class1, "force", 15712F);
		Property.set(class1, "power", 2.0F);
		Property.set(class1, "powerType", 0);
		Property.set(class1, "kalibr", 0.22F);
		Property.set(class1, "massa", 60F);
		Property.set(class1, "massaEnd", 45F);
		Spawn.add(class1, new RocketX4.SPAWN(class1));
	}

}
