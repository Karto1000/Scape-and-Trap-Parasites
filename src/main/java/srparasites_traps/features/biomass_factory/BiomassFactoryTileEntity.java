package srparasites_traps.features.biomass_factory;

import cofh.core.block.TileCore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import srparasites_traps.capability.DeadBloodTank;
import srparasites_traps.config.ForgeConfigHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BiomassFactoryTileEntity extends TileCore implements ICapabilityProvider, ITickable {
    public DeadBloodTank biomassStorage;
    public final static int DEFAULT_BIOMASS_VALUE_MB = 10;
    public int consumeDelayTicks = ForgeConfigHandler.biomassFactory.DEFAULT_BIOMASS_FACTORY_CONSUME_DELAY;

    private int currentConsumeCooldown = 0;
    private final Map<Item, Integer> allowedItems = new HashMap<>();

    public final ItemStackHandler inputInventory = new ItemStackHandler(21) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (stack.isEmpty()) return false;
            Item item = stack.getItem();
            return allowedItems.get(item) != null;
        }
    };

    public final ItemStackHandler fluidFillInventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack);
            return handler != null;
        }
    };

    public final ItemStackHandler fluidOutputInventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    };

    public void dropInventory() {
        for (int slot = 0; slot < inputInventory.getSlots(); slot++) {
            ItemStack stack = inputInventory.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack));
                inputInventory.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }

        ItemStack fluidInputStack = fluidFillInventory.getStackInSlot(0);
        if (!fluidInputStack.isEmpty()) {
            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, fluidInputStack));
            fluidFillInventory.setStackInSlot(0, ItemStack.EMPTY);
        }

        ItemStack fluidOutputStack = fluidOutputInventory.getStackInSlot(0);
        if (!fluidOutputStack.isEmpty()) {
            world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, fluidOutputStack));
            fluidOutputInventory.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    private Optional<Integer> getRecycleValueOf(Item item) {
        Integer value = allowedItems.get(item);
        return Optional.ofNullable(value);
    }

    public BiomassFactoryTileEntity() {
        this.biomassStorage = new DeadBloodTank(ForgeConfigHandler.biomassFactory.DEFAULT_BIOMASS_FACTORY_MAX_BIOMASS);
        this.biomassStorage.setTileEntity(this);

        // Add all infested blocks to the allowed items list
        for (Block block : SRPBlocks.SRP_BLOCKS) {
            ResourceLocation registryName = block.getRegistryName();

            if (registryName == null) continue;
            if (!registryName.toString().contains("infested")) continue;

            allowedItems.put(Item.getItemFromBlock(block), DEFAULT_BIOMASS_VALUE_MB);
        }

        // Add some special blocks
        allowedItems.put(Item.getItemFromBlock(SRPBlocks.ParasiteRubble), DEFAULT_BIOMASS_VALUE_MB);
        allowedItems.put(Item.getItemFromBlock(SRPBlocks.ParasiteRubbleDense), DEFAULT_BIOMASS_VALUE_MB);
        allowedItems.put(Item.getItemFromBlock(SRPBlocks.ParasiteStain), DEFAULT_BIOMASS_VALUE_MB);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("BiomassStorage", this.biomassStorage.writeToNBT(new NBTTagCompound()));
        compound.setTag("inventory", this.inputInventory.serializeNBT());
        compound.setTag("fluidFillInventory", this.fluidFillInventory.serializeNBT());
        compound.setTag("fluidOutputInventory", this.fluidOutputInventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("BiomassStorage"))
            this.biomassStorage.readFromNBT(compound.getCompoundTag("BiomassStorage"));
        if (compound.hasKey("inventory")) this.inputInventory.deserializeNBT(compound.getCompoundTag("inventory"));
        if (compound.hasKey("fluidFillInventory"))
            this.fluidFillInventory.deserializeNBT(compound.getCompoundTag("fluidFillInventory"));
        if (compound.hasKey("fluidOutputInventory"))
            this.fluidOutputInventory.deserializeNBT(compound.getCompoundTag("fluidOutputInventory"));
    }

    @Override
    public void update() {
        ItemStack fluidInputStack = fluidFillInventory.getStackInSlot(0);
        ItemStack fluidOutputStack = fluidOutputInventory.getStackInSlot(0);
        if (!fluidInputStack.isEmpty() && fluidOutputStack.isEmpty()) {
            IFluidHandlerItem handler = FluidUtil.getFluidHandler(fluidInputStack);
            if (handler == null) return;

            int amount = handler.fill(this.biomassStorage.getFluid(), true);
            this.biomassStorage.drain(amount, true);

            ItemStack newItem = handler.getContainer();
            fluidFillInventory.setStackInSlot(0, ItemStack.EMPTY);
            fluidOutputInventory.setStackInSlot(0, newItem);
        }

        if (currentConsumeCooldown > 0) {
            currentConsumeCooldown--;
            return;
        }

        if (this.biomassStorage.isFull()) return;

        for (int i = 0; i < inputInventory.getSlots(); i++) {
            ItemStack stack = inputInventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            Integer value = getRecycleValueOf(stack.getItem()).orElse(DEFAULT_BIOMASS_VALUE_MB);
            this.biomassStorage.fill(value);
            inputInventory.extractItem(i, 1, false);
            this.markDirty();
            currentConsumeCooldown = consumeDelayTicks;
            break;
        }
    }

    @Override
    public void sendGuiNetworkData(Container container, IContainerListener player) {
        super.sendGuiNetworkData(container, player);
        player.sendWindowProperty(container, 0, this.biomassStorage.getFluidAmount());
    }

    @Override
    public void receiveGuiNetworkData(int id, int data) {
        super.receiveGuiNetworkData(id, data);
        switch (id) {
            case 0:
                if (data == 0) this.biomassStorage.setFluid(null);
                else this.biomassStorage.set(data);
                break;
        }
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(this.biomassStorage);
        }

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inputInventory);
        }

        return super.getCapability(capability, facing);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return true;
        }

        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }

        return false;
    }
}
