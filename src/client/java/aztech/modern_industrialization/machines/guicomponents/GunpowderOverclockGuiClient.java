/*
 * MIT License
 *
 * Copyright (c) 2020 Azercoco & Technici4n
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package aztech.modern_industrialization.machines.guicomponents;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.machines.gui.ClientComponentRenderer;
import aztech.modern_industrialization.machines.gui.GuiComponentClient;
import aztech.modern_industrialization.machines.gui.MachineScreen;
import aztech.modern_industrialization.util.RenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public class GunpowderOverclockGuiClient implements GuiComponentClient {
    final GunpowderOverclockGui.Parameters params;
    int remTick;

    public GunpowderOverclockGuiClient(FriendlyByteBuf buf) {
        this.params = new GunpowderOverclockGui.Parameters(buf.readInt(), buf.readInt());
        readCurrentData(buf);
    }

    @Override
    public void readCurrentData(FriendlyByteBuf buf) {
        remTick = buf.readInt();
    }

    @Override
    public ClientComponentRenderer createRenderer(MachineScreen machineScreen) {
        return new Renderer();
    }

    public class Renderer implements ClientComponentRenderer {

        @Override
        public void renderBackground(net.minecraft.client.gui.GuiComponent helper, PoseStack matrices, int x, int y) {
            if (remTick > 0) {
                RenderSystem.setShaderTexture(0, MachineScreen.SLOT_ATLAS);
                int px = x + params.renderX;
                int py = y + params.renderY;
                helper.blit(matrices, px, py, 0, 58, 20, 20);
            }
        }

        @Override
        public void renderTooltip(MachineScreen screen, PoseStack matrices, int x, int y, int cursorX, int cursorY) {
            if (remTick > 0) {
                if (RenderHelper.isPointWithinRectangle(params.renderX, params.renderY, 20, 20, cursorX - x, cursorY - y)) {
                    List<Component> tooltip = new ArrayList<>();

                    int seconds = remTick / 20;
                    int hours = seconds / 3600;
                    int minutes = (seconds % 3600) / 60;

                    String time = String.format("%d", seconds);

                    if (hours > 0) {
                        time = String.format("%d:%02d:%02d", hours, minutes, seconds % 60);
                    } else if (minutes > 0) {
                        time = String.format("%d:%02d", minutes, seconds % 60);
                    }

                    tooltip.add(MIText.GunpowderTime.text(time));
                    screen.renderComponentTooltip(matrices, tooltip, cursorX, cursorY);
                }
            }
        }
    }
}
