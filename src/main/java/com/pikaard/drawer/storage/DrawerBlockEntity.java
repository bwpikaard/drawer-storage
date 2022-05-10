package com.pikaard.drawer.storage;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.InventoryProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.WorldAccess;

import javax.annotation.Nullable;

public class DrawerBlockEntity extends BlockEntity implements BasicSidedInventory, InventoryProvider {
    private int MAX_SIZE = 2;
    public Item item = Items.AIR;
    private DefaultedList<ItemStack> stacks;

    public DrawerBlockEntity(BlockPos pos, BlockState state) {
        super(DrawerStorage.DRAWER_BLOCK_ENTITY, pos, state);
        this.stacks = DefaultedList.ofSize(size(), ItemStack.EMPTY);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
        this.stacks.clear();
        Inventories.readNbt(tag, this.stacks);
        this.item = Registry.ITEM.get(Identifier.tryParse(tag.getString("item")));
    }

    @Override
    public void writeNbt(NbtCompound tag) {
        Inventories.writeNbt(tag, this.stacks, true);
        tag.putString("item", Registry.ITEM.getId(item).toString());
        super.writeNbt(tag);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return stacks;
    }

    @Override
    public int size() {
        return MAX_SIZE;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        System.out.println("remove!");
        ItemStack stack = stacks.get(slot).split(amount);
        System.out.println(stack);
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
        return stack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStack stack = Inventories.removeStack(stacks, slot);
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
        return stack;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        stacks.set(slot, stack);
        this.markDirty();
        this.getWorld().updateListeners(this.getPos(), this.getCachedState(), this.getCachedState(), 3);
    }

    @Override
    public boolean isValid(int slot, ItemStack stack) {
        return true;
    }

    public ItemStack getMainItemStack() {
        return stacks.stream().filter(stack -> !stack.isEmpty()).findAny().orElse(ItemStack.EMPTY);
    }

    @Override
    public SidedInventory getInventory(BlockState state, WorldAccess world, BlockPos pos) {
        return this;
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        NbtCompound nbtCompound = new NbtCompound();
        Inventories.writeNbt(nbtCompound, this.stacks, true);
        return nbtCompound;
    }
}
