package srparasites_traps.features.biomass_factory;

import cofh.core.block.TileCore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import srparasites_traps.capability.DeadBloodTank;
import srparasites_traps.config.ForgeConfigHandler;
import srparasites_traps.util.InventoryHelper;
import srparasites_traps.util.NBTHelper;
import srparasites_traps.util.StateManager;
import srparasites_traps.util.UpdateLimiter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BiomassFactoryTileEntity extends TileCore implements ICapabilityProvider, ITickable {
    public DeadBloodTank biomassStorage;
    public final static int DEFAULT_BIOMASS_VALUE_MB = 10;
    public StateManager<BiomassFactoryState> state = new StateManager<>(BiomassFactoryState.IDLE, this::onSwitchState);

    private final UpdateLimiter updateLimiter = new UpdateLimiter(ForgeConfigHandler.biomassFactory.DEFAULT_CONSUME_DELAY);
    private int workingSoundCooldown = 0;
    private final Map<Item, Integer> allowedItems = new HashMap<>();

    private void onSwitchState(BiomassFactoryState oldState, BiomassFactoryState newState) {
        IBlockState blockState = this.world.getBlockState(this.pos);

        switch (newState) {
            case IDLE:
                this.world.setBlockState(this.pos, blockState.withProperty(BiomassFactoryBlock.ACTIVE, false));
                this.workingSoundCooldown = 0;
                break;
            case ACTIVE:
                this.world.setBlockState(this.pos, blockState.withProperty(BiomassFactoryBlock.ACTIVE, true));
                break;
        }
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
        return oldState.getBlock() != newSate.getBlock();
    }

    public final ItemStackHandler inputInventory = new ItemStackHandler(6) {
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
        this.biomassStorage = new DeadBloodTank(ForgeConfigHandler.biomassFactory.DEFAULT_MAX_BIOMASS);
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
        compound.setInteger("State", this.state.getState().ordinal());
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
        this.state.setState(BiomassFactoryState.values()[NBTHelper.getIntegerOrElse(compound, "State", BiomassFactoryState.IDLE::ordinal)]);
    }

    @Override
    public void update() {
        if (updateLimiter.tickDown()) return;

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

        switch (state.getState()) {
            case IDLE:
                if (canWork()) this.state.switchState(this.world.getTotalWorldTime());
                break;
            case ACTIVE:
                if (!canWork()) {
                    this.state.switchState(this.world.getTotalWorldTime());
                    return;
                }

                if (workingSoundCooldown == 0) {
                    workingSoundCooldown = 20;
                    this.world.playSound(null, this.pos, SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 1.0F, 1.0F);
                } else workingSoundCooldown--;

                for (int i = 0; i < inputInventory.getSlots(); i++) {
                    ItemStack stack = inputInventory.getStackInSlot(i);
                    if (stack.isEmpty()) continue;
                    Integer value = getRecycleValueOf(stack.getItem()).orElse(DEFAULT_BIOMASS_VALUE_MB);
                    this.biomassStorage.fill(value);
                    inputInventory.extractItem(i, 1, false);
                    this.markDirty();
                    updateLimiter.reset();
                    break;
                }

                break;
        }
    }

    public boolean canWork() {
        return !InventoryHelper.isInventoryEmpty(inputInventory) && !this.biomassStorage.isFull();
    }

    @Override
    public void sendGuiNetworkData(Container container, IContainerListener player) {
        super.sendGuiNetworkData(container, player);
        player.sendWindowProperty(container, 0, this.biomassStorage.getFluidAmount());
    }

    @Override
    public void receiveGuiNetworkData(int id, int data) {
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
