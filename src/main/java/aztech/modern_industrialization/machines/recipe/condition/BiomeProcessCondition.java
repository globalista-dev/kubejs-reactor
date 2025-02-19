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
package aztech.modern_industrialization.machines.recipe.condition;

import aztech.modern_industrialization.MIText;
import aztech.modern_industrialization.machines.recipe.MachineRecipe;
import com.google.gson.JsonObject;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;

public class BiomeProcessCondition implements MachineProcessCondition {
    public static final BiomeProcessCondition.Serde SERIALIZER = new BiomeProcessCondition.Serde();
    private final ResourceKey<Biome> biome;

    public BiomeProcessCondition(ResourceLocation biome) {
        this.biome = ResourceKey.create(Registry.BIOME_REGISTRY, biome);
    }

    @Override
    public boolean canProcessRecipe(Context context, MachineRecipe recipe) {
        var entityBiome = context.getLevel().getBiome(context.getBlockEntity().getBlockPos());
        return entityBiome.is(biome);
    }

    @Override
    public void appendDescription(List<Component> list) {
        var loc = biome.location();
        var biomeComponent = Component.translatable("biome.%s.%s".formatted(loc.getNamespace(), loc.getPath()));
        list.add(MIText.RequiresBiome.text(biomeComponent));
    }

    @Override
    public Serializer<?> getSerializer() {
        return SERIALIZER;
    }

    private static class Serde implements Serializer<BiomeProcessCondition> {
        @Override
        public BiomeProcessCondition fromJson(JsonObject json) {
            return new BiomeProcessCondition(new ResourceLocation(GsonHelper.getAsString(json, "biome")));
        }

        @Override
        public JsonObject toJson(BiomeProcessCondition condition, boolean syncToClient) {
            var obj = new JsonObject();
            obj.addProperty("biome", condition.biome.location().toString());
            return obj;
        }
    }
}
