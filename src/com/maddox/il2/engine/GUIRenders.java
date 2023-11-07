package com.maddox.il2.engine;

import com.maddox.JGP.Color4f;
import com.maddox.gwindow.GWindow;
import com.maddox.opengl.GLContext;
import com.maddox.opengl.gl;
import com.maddox.rts.Time;

public class GUIRenders extends GWindow
{
    public class WinRenders extends Renders
    {

        protected void pushRenders()
        {
            parentRender = Renders.currentRender;
            parentCamera = Renders.currentCamera;
            parentLightEnv = Renders.currentLightEnv;
            Renders.currentRenders = this;
        }

        protected void popRenders(boolean flag)
        {
            Renders.currentRender = parentRender;
            Renders.currentCamera = parentCamera;
            Renders.currentLightEnv = parentLightEnv;
            Renders.currentRenders = parentRender.renders();
            parentRender = null;
            Renders.currentCamera.activate(1.0F, RendersMain.width(), RendersMain.height(), rootViewX, rootViewY, rootViewDX, rootViewDY);
            if(!flag)
                Renders.currentLightEnv.activate();
        }

        protected void activateCamera()
        {
            Renders.currentCamera.activate(1.0F, RendersMain.width(), RendersMain.height(), wViewX, wViewY, wViewDX, wViewDY, wClipX, wClipY, wClipDX, wClipDY);
        }

        public void doPreRender()
        {
            pushRenders();
            int j = renderSet.size();
            for(int i = 0; i < j; i++)
            {
                Renders.currentRender = (Render)renderArray[i];
                if(Renders.currentRender == null)
                    break;
                Renders.currentCamera = Renders.currentRender.getCamera();
                if(Renders.currentRender.isShow() && Renders.currentCamera != null && RenderContext.bPreRenderEnable)
                {
                    computeClip();
                    if(!bWClipped)
                    {
                        activateCamera();
                        Renders.currentRender.preRender();
                    }
                }
            }

            popRenders(true);
        }

        public void doRender()
        {
            pushRenders();
            int j = renderSet.size();
            for(int i = 0; i < j; i++)
            {
                Renders.currentRender = (Render)renderArray[i];
                if(Renders.currentRender == null)
                    break;
                Renders.currentCamera = Renders.currentRender.getCamera();
                if(Renders.currentRender.isShow() && Renders.currentCamera != null)
                {
                    computeClip();
                    if(!bWClipped)
                    {
                        _clearViewPort();
                        activateCamera();
                        Renders.currentLightEnv = Renders.currentRender.getLightEnv();
                        if(Renders.currentLightEnv == null)
                            Renders.currentLightEnv = Engine.cur.lightEnv;
                        Renders.currentLightEnv.activate();
                        Render.prepareStates();
                        Renders.currentRender.render();
                        Render.flush();
                    }
                    if(Time.isPaused())
                        Renders.currentCamera.pos.updateCurrent();
                }
            }

            popRenders(false);
        }

        public void resized()
        {
            if(!Config.cur.windowSaveAspect || (width() * 3) / 4 == height())
            {
                aspectView[0] = 0.0F;
                aspectView[1] = 0.0F;
                aspectView[2] = 1.0F;
                aspectView[3] = 1.0F;
            } else
            if((width() * 3) / 4 > height())
            {
                float f = (float)(((double)width() - ((double)height() * 4D) / 3D) / 2D / (double)width());
                aspectView[0] = f;
                aspectView[2] = 1.0F - 2.0F * f;
                aspectView[1] = 0.0F;
                aspectView[3] = 1.0F;
            } else
            {
                aspectView[0] = 0.0F;
                aspectView[2] = 1.0F;
                float f1 = (float)(((double)height() - ((double)width() * 3D) / 4D) / 2D / (double)height());
                aspectView[1] = f1;
                aspectView[3] = 1.0F - 2.0F * f1;
            }
            int j = renderSet.size();
            for(int i = 0; i < j; i++)
            {
                Render render1 = (Render)renderArray[i];
                render1.contextResize(width(), height());
            }

        }

        private void _clearViewPort()
        {
            if(Renders.currentRender.isClearColor() || Renders.currentRender.isClearDepth() || Renders.currentRender.isClearStencil())
            {
                Render.clearStates();
                gl.Disable(3553);
                boolean flag = (RenderContext.texGetFlags() & 0x20) != 0;
                if(flag)
                    gl.PolygonMode(1032, 6914);
                gl.Viewport(0, 0, RendersMain.width(), RendersMain.height());
                gl.MatrixMode(5889);
                gl.LoadIdentity();
                gl.Ortho(0.0D, RendersMain.width(), 0.0D, RendersMain.height(), 0.0D, 1.0D);
                gl.MatrixMode(5888);
                gl.LoadIdentity();
                if(Renders.currentRender.isClearColor())
                {
                    Color4f color4f = Renders.currentRender.getClearColor();
                    gl.Color4f(color4f.x, color4f.y, color4f.z, color4f.w);
                    if(color4f.w == 0.0F)
                    {
                        gl.Enable(3042);
                        gl.BlendFunc(0, 1);
                    } else
                    if(color4f.w != 1.0F)
                    {
                        gl.Enable(3042);
                        gl.BlendFunc(770, 771);
                    }
                } else
                {
                    gl.Color4f(0.0F, 0.0F, 0.0F, 0.0F);
                    gl.Enable(3042);
                    gl.BlendFunc(0, 1);
                }
                float f;
                if(Renders.currentRender.isClearDepth())
                {
                    f = -Renders.currentRender.getClearDepth();
                    gl.DepthFunc(519);
                    gl.Enable(2929);
                    gl.DepthMask(true);
                } else
                {
                    f = -1E-006F;
                    gl.Disable(2929);
                    gl.DepthMask(false);
                }
                if(Renders.currentRender.bClearStencil && Config.cur.windowStencilBits != 0)
                {
                    gl.Enable(2960);
                    gl.StencilFunc(519, 0, -1);
                    gl.StencilOp(0, 0, 0);
                }
                gl.Begin(7);
                gl.Vertex3f(wClipX, wClipY, f);
                gl.Vertex3f(wClipX + wClipDX, wClipY, f);
                gl.Vertex3f(wClipX + wClipDX, wClipY + wClipDY, f);
                gl.Vertex3f(wClipX, wClipY + wClipDY, f);
                gl.End();
                if(Renders.currentRender.bClearStencil && Config.cur.windowStencilBits != 0)
                {
                    gl.StencilOp(7680, 7680, 7680);
                    gl.Disable(2960);
                }
                if(flag)
                    gl.PolygonMode(1032, 6913);
            }
        }

        public boolean isShow()
        {
            return isVisible();
        }

        public void setShow(boolean flag)
        {
            if(flag)
                showWindow();
            else
                hideWindow();
        }

        public int frame()
        {
            return RendersMain.frame();
        }

        public int width()
        {
            return (int)win.dx;
        }

        public int height()
        {
            return (int)win.dy;
        }

        public GLContext glContext()
        {
            return RendersMain.glContext();
        }

        public void setGlContext(GLContext glcontext)
        {
        }

        public void msgGLContext(int i)
        {
        }

        public boolean isTickPainting()
        {
            return RendersMain.isTickPainting();
        }

        public void setTickPainting(boolean flag)
        {
        }

        public void paint()
        {
        }

        public void paint(Render render1)
        {
        }

        public Render parentRender;
        public Camera parentCamera;
        public LightEnv parentLightEnv;

        public WinRenders()
        {
        }
    }


    protected void computeRendersClip()
    {
        Render.current().getViewPort(view);
        rootViewX = view[0];
        rootViewY = view[1];
        rootViewDX = view[2];
        rootViewDY = view[3];
        float f = root.C.org.x;
        float f1 = root.C.org.y;
        float f2 = win.dx;
        float f3 = win.dy;
        float f4 = f - root.C.clip.x;
        bRendersClipped = true;
        if(f4 < 0.0F)
        {
            f2 += f4;
            if(f2 <= 0.0F)
                return;
            f = root.C.clip.x;
            f4 = 0.0F;
        }
        f4 = (f2 + f4) - root.C.clip.dx;
        if(f4 > 0.0F)
        {
            f2 -= f4;
            if(f2 <= 0.0F)
                return;
        }
        f4 = f1 - root.C.clip.y;
        if(f4 < 0.0F)
        {
            f3 += f4;
            if(f3 <= 0.0F)
                return;
            f1 = root.C.clip.y;
            f4 = 0.0F;
        }
        f4 = (f3 + f4) - root.C.clip.dy;
        if(f4 > 0.0F)
        {
            f3 -= f4;
            if(f3 <= 0.0F)
                return;
        }
        bRendersClipped = false;
        curX = rootViewX + Math.round(root.C.org.x);
        curY = (rootViewY + rootViewDY) - Math.round(root.C.org.y) - Math.round(win.dy);
        curDX = Math.round(win.dx);
        curDY = Math.round(win.dy);
        clipX = rootViewX + Math.round(f);
        clipY = (rootViewY + rootViewDY) - Math.round(f1) - Math.round(f3);
        clipDX = Math.round(f2);
        clipDY = Math.round(f3);
    }

    protected void computeClip()
    {
        Render.current().getViewPort(view);
        wViewX = view[0] + curX;
        wViewY = view[1] + curY;
        wViewDX = view[2];
        wViewDY = view[3];
        int i = wViewX;
        int j = wViewY;
        int k = wViewDX;
        int l = wViewDY;
        int i1 = i - clipX;
        bWClipped = true;
        if(i1 < 0)
        {
            k += i1;
            if(k <= 0)
                return;
            i = clipX;
            i1 = 0;
        }
        i1 = (k + i1) - clipDX;
        if(i1 > 0)
        {
            k -= i1;
            if(k <= 0)
                return;
        }
        i1 = j - clipY;
        if((float)i1 < 0.0F)
        {
            l += i1;
            if(l <= 0)
                return;
            j = clipY;
            i1 = 0;
        }
        i1 = (l + i1) - clipDY;
        if(i1 > 0)
        {
            l -= i1;
            if(l <= 0)
                return;
        }
        bWClipped = false;
        wClipX = i;
        wClipY = j;
        wClipDX = k;
        wClipDY = l;
    }

    public void preRender()
    {
        computeRendersClip();
        if(bRendersClipped)
        {
            return;
        } else
        {
            renders.doPreRender();
            return;
        }
    }

    public void render()
    {
        if(bRendersClipped)
        {
            return;
        } else
        {
            renders.doRender();
            return;
        }
    }

    public void resized()
    {
        super.resized();
        renders.resized();
    }

    public GUIRenders(GWindow gwindow, float f, float f1, float f2, float f3, boolean flag)
    {
        super(gwindow, f, f1, f2, f3, flag);
        view = new int[4];
        renders = new WinRenders();
    }

    public GUIRenders(GWindow gwindow)
    {
        super(gwindow);
        view = new int[4];
        renders = new WinRenders();
    }

    public GUIRenders()
    {
        view = new int[4];
        renders = new WinRenders();
    }

    public WinRenders renders;
    protected int rootViewX;
    protected int rootViewY;
    protected int rootViewDX;
    protected int rootViewDY;
    protected int curX;
    protected int curY;
    protected int curDX;
    protected int curDY;
    protected int clipX;
    protected int clipY;
    protected int clipDX;
    protected int clipDY;
    protected boolean bRendersClipped;
    private int view[];
    protected int wViewX;
    protected int wViewY;
    protected int wViewDX;
    protected int wViewDY;
    protected int wClipX;
    protected int wClipY;
    protected int wClipDX;
    protected int wClipDY;
    protected boolean bWClipped;
}
