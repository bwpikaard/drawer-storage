package com.pikaard.drawer.storage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class DrawerStorage implements ModInitializer {
    private static final ItemGroup DRAWER_STORAGE_ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier("drawer-storage", "general"),
            () -> new ItemStack(Blocks.COBBLESTONE)
    );

    private static final DrawerBlock DRAWER_BLOCK = new DrawerBlock(FabricBlockSettings.of(Material.WOOD).strength(3.5f));
    public static BlockEntityType<DrawerBlockEntity> DRAWER_BLOCK_ENTITY;

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("drawer-storage", "drawer"), DRAWER_BLOCK);
        Registry.register(
                Registry.ITEM,
                new Identifier("drawer-storage", "drawer"),
                new BlockItem(DRAWER_BLOCK, new FabricItemSettings().group(DRAWER_STORAGE_ITEM_GROUP)));

        DRAWER_BLOCK_ENTITY = Registry.register(
                Registry.BLOCK_ENTITY_TYPE,
                "drawer-storage:drawer_entity",
                FabricBlockEntityTypeBuilder.create(DrawerBlockEntity::new, DRAWER_BLOCK).build(null)
        );

        UseBlockCallback.EVENT.register((PlayerEntity player, World world, Hand hand, BlockHitResult hitResult) ->{
            if (!world.isClient && player.isSneaking() && world.getBlockEntity(hitResult.getBlockPos()) instanceof DrawerBlockEntity) {
                return world.getBlockState(hitResult.getBlockPos()).onUse(world, player, hand, hitResult);
            }

            return ActionResult.PASS;
        });
    }
}
