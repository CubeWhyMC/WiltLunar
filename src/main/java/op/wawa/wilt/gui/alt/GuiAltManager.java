package op.wawa.wilt.gui.alt;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import op.wawa.wilt.interfaces.IMixinWorldRenderer;
import op.wawa.wilt.utils.FontUtils;
import op.wawa.wilt.utils.RenderUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiAltManager
extends GuiScreen {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private GuiButton login;
    private GuiButton remove;
    private GuiButton rename;
    private AltLoginThread loginThread;
    private int offset;
    public Alt selectedAlt = null;
    private String status = "\u00a7eWaiting...";

    public void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 0: {
                if (this.loginThread == null) {
                    mc.displayGuiScreen((GuiScreen)new GuiMainMenu());
                    break;
                }
                if (!this.loginThread.getStatus().equals("Logging in...") && !this.loginThread.getStatus().equals("Do not hit back! Logging in...")) {
                    mc.displayGuiScreen((GuiScreen)new GuiMainMenu());
                    break;
                }
                this.loginThread.setStatus("Do not hit back! Logging in...");
                break;
            }
            case 1: {
                String user = this.selectedAlt.getUsername();
                String pass = this.selectedAlt.getPassword();
                this.loginThread = new AltLoginThread(user, pass);
                this.loginThread.start();
                break;
            }
            case 2: {
                if (this.loginThread != null) {
                    this.loginThread = null;
                }
                AltManager.getAlts().remove(this.selectedAlt);
                this.status = "\u00a7cRemoved.";
                this.selectedAlt = null;
                break;
            }
            case 3: {
                mc.displayGuiScreen((GuiScreen)new GuiAddAlt(this));
                break;
            }
            case 4: {
                mc.displayGuiScreen((GuiScreen)new GuiAltLogin(this));
                break;
            }
            case 5: {
                if (AltManager.alts.size() == 0) {
                    this.status = "\u00a7cYou must have 1 alt!";
                    break;
                }
                Alt randomAlt = AltManager.alts.get(new Random().nextInt(AltManager.alts.size()));
                String user1 = randomAlt.getUsername();
                String pass1 = randomAlt.getPassword();
                this.loginThread = new AltLoginThread(user1, pass1);
                this.loginThread.start();
                break;
            }
            case 6: {
                mc.displayGuiScreen((GuiScreen)new GuiRenameAlt(this));
                break;
            }
            case 7: {
                Alt lastAlt = AltManager.lastAlt;
                if (lastAlt == null) {
                    if (this.loginThread == null) {
                        this.status = "?cThere is no last used alt!";
                        break;
                    }
                    this.loginThread.setStatus("?cThere is no last used alt!");
                    break;
                }
                String user2 = lastAlt.getUsername();
                String pass2 = lastAlt.getPassword();
                this.loginThread = new AltLoginThread(user2, pass2);
                this.loginThread.start();
            }
        }
    }

    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        if (Mouse.hasWheel()) {
            int wheel = Mouse.getDWheel();
            if (wheel < 0) {
                this.offset += 26;
                if (this.offset < 0) {
                    this.offset = 0;
                }
            } else if (wheel > 0) {
                this.offset -= 26;
                if (this.offset < 0) {
                    this.offset = 0;
                }
            }
        }
        this.drawDefaultBackground();
        GuiAltManager.mc.fontRendererObj.drawStringWithShadow(mc.getSession().getUsername(), 10.0f, 10.0f, -7829368);
        FontUtils.drawCenteredString(mc.fontRendererObj, "Account Manager - " + AltManager.getAlts().size() + " alts", (float)this.width / 2.0f, 10.0f, -1);
        FontUtils.drawCenteredString(mc.fontRendererObj, this.loginThread == null ? this.status : this.loginThread.getStatus(), (float)this.width / 2.0f, 20.0f, -1);
        GL11.glPushMatrix();
        this.prepareScissorBox(0.0f, 33.0f, this.width, this.height - 50);
        GL11.glEnable((int)3089);
        int y = 38;
        for (Alt alt : AltManager.getAlts()) {
            if (!this.isAltInArea(y)) continue;
            String name = alt.getMask().equals("") ? alt.getUsername() : alt.getMask();
            String pass = alt.getPassword().equals("") ? "\u00a7cCracked" : alt.getPassword().replaceAll(".", "*");
            String string = pass;
            if (alt == this.selectedAlt) {
                if (this.isMouseOverAlt(par1, par2, y - this.offset) && Mouse.isButtonDown((int)0)) {
                    RenderUtils.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2142943931);
                } else if (this.isMouseOverAlt(par1, par2, y - this.offset)) {
                    RenderUtils.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2142088622);
                } else {
                    RenderUtils.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2144259791);
                }
            } else if (this.isMouseOverAlt(par1, par2, y - this.offset) && Mouse.isButtonDown((int)0)) {
                RenderUtils.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2146101995);
            } else if (this.isMouseOverAlt(par1, par2, y - this.offset)) {
                RenderUtils.drawBorderedRect(52.0f, y - this.offset - 4, this.width - 52, y - this.offset + 20, 1.0f, -16777216, -2145180893);
            }
            FontUtils.drawCenteredString(mc.fontRendererObj, name, (float)this.width / 2.0f, y - this.offset, -1);
            FontUtils.drawCenteredString(mc.fontRendererObj, pass, (float)this.width / 2.0f, y - this.offset + 10, 0x555555);
            y += 26;
        }
        GL11.glDisable((int)3089);
        GL11.glPopMatrix();
        super.drawScreen(par1, par2, par3);
        if (this.selectedAlt == null) {
            this.login.enabled = false;
            this.remove.enabled = false;
            this.rename.enabled = false;
        } else {
            this.login.enabled = true;
            this.remove.enabled = true;
            this.rename.enabled = true;
        }
        if (Keyboard.isKeyDown((int)200)) {
            this.offset -= 26;
            if (this.offset < 0) {
                this.offset = 0;
            }
        } else if (Keyboard.isKeyDown((int)208)) {
            this.offset += 26;
            if (this.offset < 0) {
                this.offset = 0;
            }
        }
    }

    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 24, 75, 20, "Cancel"));
        this.login = new GuiButton(1, this.width / 2 - 154, this.height - 48, 70, 20, "Login");
        this.buttonList.add(this.login);
        this.remove = new GuiButton(2, this.width / 2 - 74, this.height - 24, 70, 20, "Remove");
        this.buttonList.add(this.remove);
        this.buttonList.add(new GuiButton(3, this.width / 2 + 4 + 76, this.height - 48, 75, 20, "Add"));
        this.buttonList.add(new GuiButton(4, this.width / 2 - 74, this.height - 48, 70, 20, "Direct Login"));
        this.buttonList.add(new GuiButton(5, this.width / 2 + 4, this.height - 48, 70, 20, "Random"));
        this.rename = new GuiButton(6, this.width / 2 + 4, this.height - 24, 70, 20, "Edit");
        this.buttonList.add(this.rename);
        this.rename = new GuiButton(7, this.width / 2 - 154, this.height - 24, 70, 20, "Last Alt");
        this.buttonList.add(this.rename);
        this.login.enabled = false;
        this.remove.enabled = false;
        this.rename.enabled = false;
    }

    private boolean isAltInArea(int y) {
        return y - this.offset <= this.height - 50;
    }

    private boolean isMouseOverAlt(int x, int y, int y1) {
        return x >= 52 && y >= y1 - 4 && x <= this.width - 52 && y <= y1 + 20 && x >= 0 && y >= 33 && x <= this.width && y <= this.height - 50;
    }

    protected void mouseClicked(int par1, int par2, int par3) {
        if (this.offset < 0) {
            this.offset = 0;
        }
        int y = 38 - this.offset;
        for (Alt alt : AltManager.getAlts()) {
            if (this.isMouseOverAlt(par1, par2, y)) {
                if (alt == this.selectedAlt) {
                    this.actionPerformed((GuiButton)this.buttonList.get(1));
                    return;
                }
                this.selectedAlt = alt;
            }
            y += 26;
        }
        super.mouseClicked(par1, par2, par3);
    }

    public void prepareScissorBox(float x, float y, float x2, float y2) {
        int factor = new ScaledResolution(mc).getScaleFactor();
        GL11.glScissor((int)((int)(x * (float)factor)), (int)((int)(((float)new ScaledResolution(mc).getScaledHeight() - y2) * (float)factor)), (int)((int)((x2 - x) * (float)factor)), (int)((int)((y2 - y) * (float)factor)));
    }

    public void renderBackground(int par1, int par2) {
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        OpenGlHelper.glBlendFunc((int)770, (int)771, (int)1, (int)0);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glDisable((int)3008);
        this.drawDefaultBackground();
        Tessellator var3 = Tessellator.getInstance();
        WorldRenderer worldRenderer = var3.getWorldRenderer();
        ((IMixinWorldRenderer)worldRenderer).startDrawingQuads();
        ((IMixinWorldRenderer)worldRenderer).addVertexWithUV(0.0, par2, -90.0, 0.0, 1.0);
        ((IMixinWorldRenderer)worldRenderer).addVertexWithUV(par1, par2, -90.0, 1.0, 1.0);
        ((IMixinWorldRenderer)worldRenderer).addVertexWithUV(par1, 0.0, -90.0, 1.0, 0.0);
        ((IMixinWorldRenderer)worldRenderer).addVertexWithUV(0.0, 0.0, -90.0, 0.0, 0.0);
        var3.draw();
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2929);
        GL11.glEnable((int)3008);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}

