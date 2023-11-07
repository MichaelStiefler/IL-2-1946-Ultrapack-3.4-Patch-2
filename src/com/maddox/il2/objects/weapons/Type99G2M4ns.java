package com.maddox.il2.objects.weapons;

import com.maddox.JGP.Color3f;
import com.maddox.il2.engine.BulletProperties;
import com.maddox.il2.engine.GunProperties;

public class Type99G2M4ns extends MGunAircraftGeneric
{
    public GunProperties createProperties()
    {
        GunProperties gunproperties = super.createProperties();
        gunproperties.bCannon = false;
        gunproperties.bUseHookAsRel = true;
        gunproperties.fireMesh = "3DO/Effects/GunFire/20mm/mono.sim";
        gunproperties.fire = null;
        gunproperties.sprite = "3DO/Effects/GunFire/20mm/GunFlare.eff";
        gunproperties.smoke = "effects/smokes/MachineGun.eff";
        gunproperties.shells = null;
        gunproperties.sound = "weapon.mgun_20_700";
        gunproperties.emitColor = new Color3f(1.0F, 1.0F, 0.0F);
        gunproperties.emitI = 10F;
        gunproperties.emitR = 3F;
        gunproperties.emitTime = 0.03F;
        gunproperties.aimMinDist = 10F;
        gunproperties.aimMaxDist = 3500F;
        gunproperties.weaponType = 3;
        gunproperties.maxDeltaAngle = 0.15F;
        gunproperties.shotFreq = 8.333F;
        gunproperties.traceFreq = 2;
        gunproperties.bullets = 250;
        gunproperties.bulletsCluster = 1;
        gunproperties.bullet = (new BulletProperties[] {
            new BulletProperties(), new BulletProperties(), new BulletProperties(), new BulletProperties()
        });
        gunproperties.bullet[0].massa = 0.1283F;
        gunproperties.bullet[0].kalibr = 0.0004F;
        gunproperties.bullet[0].speed = 750F;
        gunproperties.bullet[0].power = 0.004F;
        gunproperties.bullet[0].powerType = 0;
        gunproperties.bullet[0].powerRadius = 0.5F;
        gunproperties.bullet[0].traceMesh = "3do/effects/tracers/20mmOrange/mono.sim";
        gunproperties.bullet[0].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[0].traceColor = 0;
        gunproperties.bullet[0].timeLife = 3F;
        gunproperties.bullet[1].massa = 0.13F;
        gunproperties.bullet[1].kalibr = 0.00038F;
        gunproperties.bullet[1].speed = 750F;
        gunproperties.bullet[1].power = 0.003672F;
        gunproperties.bullet[1].powerType = 0;
        gunproperties.bullet[1].powerRadius = 0.3F;
        gunproperties.bullet[1].traceMesh = "3do/effects/tracers/20mmOrange/mono.sim";
        gunproperties.bullet[1].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[1].traceColor = 0xd200ffff;
        gunproperties.bullet[1].timeLife = 3F;
        gunproperties.bullet[2].massa = 0.1313F;
        gunproperties.bullet[2].kalibr = 0.0004F;
        gunproperties.bullet[2].speed = 750F;
        gunproperties.bullet[2].power = 0.0005F;
        gunproperties.bullet[2].powerType = 0;
        gunproperties.bullet[2].powerRadius = 0.01F;
        gunproperties.bullet[2].traceMesh = "3do/effects/tracers/20mmOrange/mono.sim";
        gunproperties.bullet[2].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[2].traceColor = 0;
        gunproperties.bullet[2].timeLife = 3F;
        gunproperties.bullet[3].massa = 0.1275F;
        gunproperties.bullet[3].kalibr = 0.00038F;
        gunproperties.bullet[3].speed = 750F;
        gunproperties.bullet[3].power = 0.003372F;
        gunproperties.bullet[3].powerType = 0;
        gunproperties.bullet[3].powerRadius = 0.3F;
        gunproperties.bullet[3].traceMesh = "3do/effects/tracers/20mmOrange/mono.sim";
        gunproperties.bullet[3].traceTrail = "effects/Smokes/SmokeBlack_BuletteTrail.eff";
        gunproperties.bullet[3].traceColor = 0xd200ffff;
        gunproperties.bullet[3].timeLife = 3F;
        return gunproperties;
    }
}
