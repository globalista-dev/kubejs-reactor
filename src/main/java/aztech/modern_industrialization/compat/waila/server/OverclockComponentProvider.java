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
package aztech.modern_industrialization.compat.waila.server;

import aztech.modern_industrialization.compat.waila.holder.CrafterComponentHolder;
import aztech.modern_industrialization.machines.MachineBlockEntity;
import aztech.modern_industrialization.machines.components.CrafterComponent;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;

public class OverclockComponentProvider implements IDataProvider<MachineBlockEntity> {
    @Override
    public void appendData(IDataWriter data, IServerAccessor<MachineBlockEntity> accessor, IPluginConfig config) {
        if (accessor.getTarget() instanceof CrafterComponentHolder crafterComponentHolder) {
            CrafterComponent crafterComponent = crafterComponentHolder.getCrafterComponent();
            if (crafterComponent.hasActiveRecipe() && crafterComponent.getMaxEfficiencyTicks() > 0) {
                data.raw().putInt("efficiencyTicks", crafterComponent.getEfficiencyTicks());
                data.raw().putInt("maxEfficiencyTicks", crafterComponent.getMaxEfficiencyTicks());
                data.raw().putLong("baseRecipeEu", crafterComponent.getBaseRecipeEu());
                data.raw().putLong("currentRecipeEu", crafterComponent.getCurrentRecipeEu());
            }
        }
    }
}
