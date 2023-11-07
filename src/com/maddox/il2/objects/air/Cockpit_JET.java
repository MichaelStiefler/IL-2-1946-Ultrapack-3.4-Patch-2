package com.maddox.il2.objects.air;

import com.maddox.JGP.Point3d;
import com.maddox.JGP.Tuple3d;
import com.maddox.JGP.Vector3d;
import com.maddox.JGP.Vector3f;
import com.maddox.il2.ai.WayPoint;
import com.maddox.il2.engine.HookNamed;
import com.maddox.il2.engine.InterpolateRef;
import com.maddox.il2.engine.Loc;
import com.maddox.il2.engine.hotkey.HookPilot;
import com.maddox.il2.fm.FlightModelMain;
import com.maddox.rts.HotKeyEnv;
import com.maddox.rts.Time;
import com.maddox.sound.ReverbFXRoom;

public class Cockpit_JET extends CockpitPilot {
	class Interpolater extends InterpolateRef {

		public boolean tick() {
			if (fm != null && cockpitDimControl && setNew.dimPosition > 0.0F)
				setNew.dimPosition = setOld.dimPosition - 0.05F;
			return true;
		}

		Interpolater() {
		}
	}

	private class Variables {

		float dimPosition;

		private Variables() {
		}

		Variables(Variables variables) {
			this();
		}
	}

	protected boolean doFocusEnter() {
		if (super.doFocusEnter()) {
			HookPilot hookpilot = HookPilot.current;
			hookpilot.doAim(false);
			return true;
		} else {
			return false;
		}
	}

	protected void doFocusLeave() {
		if (isFocused()) {
			leave();
			super.doFocusLeave();
		}
	}

	private void enter() {
		HookPilot hookpilot = HookPilot.current;
		if (hookpilot.isPadlock())
			hookpilot.stopPadlock();
		hookpilot.doAim(true);
		hookpilot.setSimpleUse(true);
		hookpilot.setSimpleAimOrient(aAim, tAim, kAim);
		HotKeyEnv.enable("PanView", false);
		HotKeyEnv.enable("SnapView", false);
		bEntered = true;
	}

	private void leave() {
		if (bEntered) {
			HookPilot hookpilot = HookPilot.current;
			hookpilot.doAim(false);
			hookpilot.setSimpleAimOrient(0.0F, 0.0F, 0.0F);
			hookpilot.setSimpleUse(false);
			boolean bool = HotKeyEnv.isEnabled("aircraftView");
			HotKeyEnv.enable("PanView", bool);
			HotKeyEnv.enable("SnapView", bool);
			bEntered = false;
		}
	}

	public void destroy() {
		super.destroy();
		leave();
	}

	public void doToggleAim(boolean bool) {
		if (isFocused() && isToggleAim() != bool)
			if (bool)
				enter();
			else
				leave();
	}

	public Cockpit_JET() {
		super("3DO/Cockpit/JetmanPit/hier.him", "he111");
		setOld = new Variables(null);
		setNew = new Variables(null);
		w = new Vector3f();
		bEntered = false;
		tmpP = new Point3d();
		tmpV = new Vector3d();
		try {
			Loc loc = new Loc();
			HookNamed hooknamed = new HookNamed(super.mesh, "CAMERAAIM");
			hooknamed.computePos(this, super.pos.getAbs(), loc);
			aAim = loc.getOrient().getAzimut();
			tAim = loc.getOrient().getTangage();
			kAim = loc.getOrient().getKren();
		} catch (Exception exception) {
			System.out.println(exception.getMessage());
			exception.printStackTrace();
		}
		interpPut(new Interpolater(), null, Time.current(), null);
		if (super.acoustics != null)
			super.acoustics.globFX = new ReverbFXRoom(0.45F);
	}

	public void toggleDim() {
		super.cockpitDimControl = !super.cockpitDimControl;
	}

	public void reflectWorldToInstruments(float f1) {
	}

	protected float waypointAzimuth() {
		WayPoint waypoint = ((FlightModelMain)(super.fm)).AP.way.curr();
		if (waypoint == null)
			return 0.0F;
		waypoint.getP(tmpP);
		tmpV.sub(tmpP, ((FlightModelMain)(super.fm)).Loc);
		float f;
		for (f = (float)(57.295779513082323D * Math.atan2(-((Tuple3d)(tmpV)).y, ((Tuple3d)(tmpV)).x)); f <= -180F; f += 180F)
			;
		for (; f > 180F; f -= 180F)
			;
		return f;
	}

	public void reflectCockpitState() {
		retoggleLight();
	}

	public void toggleLight() {
		super.cockpitLightControl = !super.cockpitLightControl;
		if (super.cockpitLightControl)
			setNightMats(true);
		else
			setNightMats(false);
	}

	private void retoggleLight() {
		if (super.cockpitLightControl) {
			setNightMats(false);
			setNightMats(true);
		} else {
			setNightMats(true);
			setNightMats(false);
		}
	}

	private Variables setOld;
	private Variables setNew;
	public Vector3f w;
	private float aAim;
	private float tAim;
	private float kAim;
	private boolean bEntered;
	private Point3d tmpP;
	private Vector3d tmpV;

}
