package com.pikaard.drawer.storage;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class DrawerBlock extends BlockWithEntity {
    public DrawerBlock(Settings settings) {
        super(settings);
        setDefaultState(this.stateManager.getDefaultState().with(Properties.FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(Properties.FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return (BlockState) this.getDefaultState().with(Properties.FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DrawerBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        //With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    @Override
    public ActionResult onUse(BlockState blockState, World world, BlockPos blockPos, PlayerEntity player, Hand hand, BlockHitResult blockHitResult) {
        System.out.println(((DrawerBlockEntity) world.getBlockEntity(blockPos)).getItems());
        if (world.isClient && !player.isSneaking()) return ActionResult.SUCCESS;
        DrawerBlockEntity blockEntity = (DrawerBlockEntity) world.getBlockEntity(blockPos);

        if (player.isSneaking()) {
            if (!blockEntity.getStack(1).isEmpty()) {
                // Give the player the stack in the inventory
                player.getInventory().offerOrDrop(blockEntity.getStack(1));
                // Remove the stack from the inventory
                blockEntity.removeStack(1);
                world.playSound(null, blockPos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0f, 1.0f);
                blockEntity.updateListeners();
            } else if (!blockEntity.getStack(0).isEmpty()) {
                player.getInventory().offerOrDrop(blockEntity.getStack(0));
                blockEntity.removeStack(0);
                world.playSound(null, blockPos, SoundEvents.ENTITY_ITEM_FRAME_REMOVE_ITEM, SoundCategory.BLOCKS, 1.0f, 1.0f);
                blockEntity.updateListeners();
            } else {
                player.sendMessage(new LiteralText("Empty!"), false);
            }
        } else {
            // INSERT
            if (!player.getStackInHand(hand).isEmpty()) {
                // Check what is the first open slot and put an item from the player's hand there
                if (blockEntity.getStack(0).isEmpty()) {
                    // Put the stack the player is holding into the inventory
                    blockEntity.setStack(0, player.getStackInHand(hand).copy());
                    // Remove the stack from the player's hand
                    player.getStackInHand(hand).setCount(0);
                    world.playSound(null, blockPos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    blockEntity.updateListeners();
                } else if (blockEntity.getStack(1).isEmpty()) {
                    blockEntity.setStack(1, player.getStackInHand(hand).copy());
                    player.getStackInHand(hand).setCount(0);
                    world.playSound(null, blockPos, SoundEvents.ENTITY_ITEM_FRAME_ADD_ITEM, SoundCategory.BLOCKS, 1.0f, 1.0f);
                    blockEntity.updateListeners();
                } else {
                    // If the inventory is full we'll print it's contents
                    System.out.println("The first slot holds "
                            + blockEntity.getStack(0) + " and the second slot holds " + blockEntity.getStack(1));
                }
            }
        }

        return ActionResult.SUCCESS;
    }


    //This method will drop all items onto the ground when the block is broken
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof DrawerBlockEntity) {
                ItemScatterer.spawn(world, pos, (DrawerBlockEntity) blockEntity);
                // update comparators
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
}
