package com.pikaard.drawer.storage;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class DrawerBlockEntityRenderer<T extends BlockEntity> implements BlockEntityRenderer<DrawerBlockEntity> {
    private static ItemStack stack = new ItemStack(Items.JUKEBOX, 1);

    public DrawerBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {}

    @Override
    public void render(DrawerBlockEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (!blockEntity.isRemoved() && !blockEntity.getStack(0).isEmpty()) {
            matrices.push();

            BlockState state = blockEntity.getWorld().getBlockState(blockEntity.getPos());
            Direction direction = state.get(FacingBlock.FACING);

            if (direction == Direction.NORTH) {
                matrices.translate(0.5, 0.314, 0);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
            } else if (direction == Direction.SOUTH) {
                matrices.translate(0.5, 0.314, 1);
            } else if (direction == Direction.WEST) {
                matrices.translate(0, 0.314, 0.5);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(270));
            } else if (direction == Direction.EAST) {
                matrices.translate(1, 0.314, 0.5);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(90));
            }
            matrices.scale(1, 1, 0.1f);

            int lightAbove = WorldRenderer.getLightmapCoordinates(blockEntity.getWorld(), blockEntity.getPos().up());
            MinecraftClient.getInstance().getItemRenderer().renderItem(blockEntity.getStack(0), ModelTransformation.Mode.GROUND, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);

            matrices.pop();
        }
    }
}