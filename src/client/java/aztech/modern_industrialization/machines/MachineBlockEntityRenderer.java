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
package aztech.modern_industrialization.machines;

import aztech.modern_industrialization.client.model.MachineBakedModel;
import aztech.modern_industrialization.compat.sodium.SodiumCompat;
import aztech.modern_industrialization.machines.models.MachineCasing;
import aztech.modern_industrialization.machines.models.MachineModelClientData;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.mesh.MutableQuadView;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Renders an overlay if the machine is active.
 */
public class MachineBlockEntityRenderer<T extends MachineBlockEntity> implements BlockEntityRenderer<T> {
    private final BlockModelShaper blockModels;
    private BlockState lastBlockState = null;
    private MachineBakedModel model = null;
    private final IdentityHashMap<MachineCasing, Object[]> quadCache = new IdentityHashMap<>();
    private static final Object NO_QUAD = new Object();

    public MachineBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.blockModels = ctx.getBlockRenderDispatcher().getBlockModelShaper();
    }

    @Nullable
    private BakedQuad getCachedQuad(MachineModelClientData data, Direction d) {
        var facing = data.frontDirection;
        int cachedQuadIndex = facing.ordinal() * 6 + d.ordinal();
        var casing = data.casing;
        var cachedQuads = quadCache.computeIfAbsent(casing, c -> new Object[36]);

        if (cachedQuads[cachedQuadIndex] == null) {
            Renderer renderer = RendererAccess.INSTANCE.getRenderer();
            QuadEmitter emitter = renderer.meshBuilder().getEmitter();

            TextureAtlasSprite sprite = MachineBakedModel.getSprite(model.getSprites(casing), d, facing, true);
            if (sprite != null) {
                emitter.material(model.cutoutMaterial);
                emitter.square(d, 0, 0, 1, 1, -2e-4f); // non-active face is -1e-6f, so we override it.
                emitter.cullFace(d);
                emitter.spriteBake(0, sprite, MutableQuadView.BAKE_LOCK_UV);
                emitter.spriteColor(0, -1, -1, -1, -1);
                cachedQuads[cachedQuadIndex] = emitter.toBakedQuad(0, sprite, false);
            } else {
                cachedQuads[cachedQuadIndex] = NO_QUAD;
            }
        }

        var quad = cachedQuads[cachedQuadIndex];
        return quad == NO_QUAD ? null : (BakedQuad) quad;
    }

    private static final Field FORWARDING_BAKED_MODEL_WRAPPED;
    static {
        try {
            FORWARDING_BAKED_MODEL_WRAPPED = ForwardingBakedModel.class.getDeclaredField("wrapped");
            FORWARDING_BAKED_MODEL_WRAPPED.setAccessible(true);
        } catch (ReflectiveOperationException reflectiveException) {
            throw new RuntimeException("Failed to find ForwardingBakedModel field \"wrapped\"", reflectiveException);
        }
    }

    private MachineBakedModel getMachineModel(BlockState state) {
        var model = blockModels.getBlockModel(state);

        while (true) {
            if (model instanceof MachineBakedModel mbm) {
                return mbm;
            } else if (model instanceof ForwardingBakedModel fbm) {
                // When mods like Continuity are used, it's possible that the machine model is behind some delegate.
                try {
                    model = (BakedModel) FORWARDING_BAKED_MODEL_WRAPPED.get(fbm);
                } catch (ReflectiveOperationException reflectiveException) {
                    throw new RuntimeException("Failed to read machine model", reflectiveException);
                }
            } else {
                throw new RuntimeException("Couldn't find for model for machine %s, wrong model is present: %s".formatted(state, model));
            }
        }
    }

    @Override
    public void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vcp, int light, int overlay) {
        BlockState state = entity.getBlockState();
        if (lastBlockState == null) {
            lastBlockState = state;
            model = getMachineModel(state);
        } else if (lastBlockState != state) {
            // Sanity check.
            throw new IllegalStateException("Tried to use the same machine BER with two block states: " + state + " and " + lastBlockState);
        }

        MachineModelClientData data = entity.getModelData();
        if (data.isActive) {
            VertexConsumer vc = vcp.getBuffer(RenderType.cutout());

            for (Direction d : Direction.values()) {
                BakedQuad quad = getCachedQuad(data, d);
                if (quad != null) {
                    int faceLight = LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockState(), entity.getBlockPos().relative(d));
                    vc.putBulkData(matrices.last(), quad, 1.0f, 1.0f, 1.0f, faceLight, OverlayTexture.NO_OVERLAY);

                    SodiumCompat.markSpriteActive(quad.getSprite());
                }
            }
        }
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
