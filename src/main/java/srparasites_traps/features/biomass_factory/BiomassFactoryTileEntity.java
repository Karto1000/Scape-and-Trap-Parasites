package srparasites_traps.features.biomass_factory;

import cofh.core.block.TileCore;
import com.dhanantry.scapeandrunparasites.init.SRPBlocks;
import net.minecraft.block.Block;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import srparasites_traps.capability.BiomassTank;
import srparasites_traps.config.ForgeConfigHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BiomassFactoryTileEntity extends TileCore implements ICapabilityProvider, ITickable {
    public BiomassTank biomassStorage;
    public final static int DEFAULT_BIOMASS_VALUE_MB = 10;
    public int consumeDelayTicks = ForgeConfigHandler.biomassFactory.DEFAULT_BIOMASS_FACTORY_CONSUME_DELAY;

    private int currentConsumeCooldown = 0;
    private final Map<Item, Integer> allowedItems = new HashMap<>();

    public final ItemStackHandler inventory = new ItemStackHandler(21) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (stack.isEmpty()) return false;
            Item item = stack.getItem();
            return allowedItems.get(item) != null;
        }
    };

    private Optional<Integer> getRecycleValueOf(Item item) {
        Integer value = allowedItems.get(item);
        return Optional.ofNullable(value);
    }

    public BiomassFactoryTileEntity() {
        this.biomassStorage = new BiomassTank(ForgeConfigHandler.biomassFactory.DEFAULT_BIOMASS_FACTORY_MAX_BIOMASS);
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
        compound.setTag("inventory", this.inventory.serializeNBT());
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("BiomassStorage"))
            this.biomassStorage.readFromNBT(compound.getCompoundTag("BiomassStorage"));
        if (compound.hasKey("inventory")) this.inventory.deserializeNBT(compound.getCompoundTag("inventory"));
    }

    @Override
    public void update() {
        if (currentConsumeCooldown > 0) {
            currentConsumeCooldown--;
            return;
        }

        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            Integer value = getRecycleValueOf(stack.getItem()).orElse(DEFAULT_BIOMASS_VALUE_MB);
            this.biomassStorage.fillBiomass(value);
            inventory.extractItem(i, 1, false);
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
                System.out.println(data);
                if (data == 0) this.biomassStorage.setFluid(null);
                else {
                    FluidStack currentFluid = this.biomassStorage.getFluid();
                    if (currentFluid != null) {
                        currentFluid.amount = data;
                        this.biomassStorage.setFluid(currentFluid);
                    } else {
                        this.biomassStorage.setFluid(new FluidStack(this.biomassStorage.getAcceptedFluid(), data));
                    }
                }
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
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(this.inventory);
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
