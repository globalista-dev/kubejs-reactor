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
package aztech.modern_industrialization;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class MITags {
    public static final TagKey<Item> WRENCHES = item("wrenches");

    public static final TagKey<Item> BARRELS = miItem("barrels");
    public static final TagKey<Item> TANKS = miItem("tanks");
    public static final TagKey<Item> FLUID_PIPES = miItem("fluid_pipes");
    public static final TagKey<Item> ITEM_PIPES = miItem("item_pipes");
    public static final TagKey<Item> ME_WIRES = miItem("me_wires");

    public static TagKey<Item> item(String path) {
        return TagKey.create(Registry.ITEM.key(), new ResourceLocation("c", path));
    }

    public static TagKey<Item> miItem(String path) {
        return TagKey.create(Registry.ITEM.key(), new MIIdentifier(path));
    }
}
