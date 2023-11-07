package com.maddox.il2.objects.weapons;

import com.maddox.rts.Property;

public class BombGunJetManNuke extends BombGun {

    public void selectFuzeAutomatically(boolean flag)
    {
    }

	static {
		Class class1 = BombGunJetManNuke.class;
		Property.set(class1, "bulletClass", (Object)BombJetManNuke.class);
		Property.set(class1, "bullets", 1);
		Property.set(class1, "shotFreq", 3F);
		Property.set(class1, "external", 1);
		Property.set(class1, "sound", "weapon.bombgun");
	}
}
