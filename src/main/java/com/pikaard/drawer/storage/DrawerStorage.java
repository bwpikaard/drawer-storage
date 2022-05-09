package com.pikaard.drawer.storage;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DrawerStorage implements ModInitializer {
    private static final ItemGroup DRAWER_STORAGE_ITEM_GROUP = FabricItemGroupBuilder.build(
            new Identifier("drawer-storage", "general"),
            () -> new ItemStack(Blocks.COBBLESTONE)
    );

    private static final DrawerBlock DRAWER_BLOCK = new DrawerBlock(FabricBlockSettings.of(Material.WOOD).strength(0.5f));

    @Override
    public void onInitialize() {
        Registry.register(Registry.BLOCK, new Identifier("drawer-storage", "drawer"), DRAWER_BLOCK);
        Registry.register(
                Registry.ITEM,
                new Identifier("drawer-storage", "drawer"),
                new BlockItem(DRAWER_BLOCK, new FabricItemSettings().group(DRAWER_STORAGE_ITEM_GROUP)));
    }
}
