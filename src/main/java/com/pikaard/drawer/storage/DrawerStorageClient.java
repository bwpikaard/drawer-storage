package com.pikaard.drawer.storage;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;

public class DrawerStorageClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(DrawerStorage.DRAWER_BLOCK_ENTITY, DrawerBlockEntityRenderer::new);
    }
}
