package srparasites_traps.features.augments;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import srparasites_traps.network.RemoveEntityFromTargetingAugment;
import srparasites_traps.network.SRParasitesTrapsNetwork;
import srparasites_traps.network.UpdateTargetingMode;
import srparasites_traps.registry.ModItems;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TargetingAugmentGui extends GuiScreen {
    private static final int GUI_WIDTH = 256;
    private static final int GUI_HEIGHT = 183;
    private static final int ROW_HEIGHT = 24;
    private static final int LIST_X = 8;
    private static final int LIST_Y = 58;
    private static final int LIST_HEIGHT = 120;
    private static final int LIST_WIDTH = GUI_WIDTH - 16;
    private static final int DELETE_BUTTON_SIZE = 12;
    private static final ResourceLocation TEXTURE = new ResourceLocation("srparasites_traps:textures/gui/targeting_augment_gui.png");
    private final Map<String, Entity> entityCache = new HashMap<>();
    private final EntityPlayer player;
    private final List<String> visibleEntityIds = new ArrayList<>();

    private GuiTextField textField;
    private int scrollOffset = 0;
    private int guiLeft;
    private int guiTop;
    private TargetingMode targetingMode = TargetingMode.WHITELIST;
    private GuiButton toggleModeButton;

    public TargetingAugmentGui(EntityPlayer player) {
        super();
        this.player = player;
    }

    public enum TargetingMode {
        WHITELIST,
        BLACKLIST;

        @Override
        public String toString() {
            if (this == WHITELIST) return "Whitelist";
            else return "Blacklist";
        }
    }

    @Override
    public void initGui() {
        super.initGui();

        ItemStack stack = this.player.getHeldItemMainhand();
        if (stack.getItem() == ModItems.TARGETING_AUGMENT) this.targetingMode = TargetingAugment.getTargetingMode(stack);

        this.guiLeft = this.width / 2 - GUI_WIDTH / 2;
        this.guiTop = this.height / 2 - GUI_HEIGHT / 2;
        this.textField = new GuiTextField(
                0, this.fontRenderer,
                this.guiLeft + 8,
                this.guiTop + 32,
                GUI_WIDTH - 16,
                this.fontRenderer.FONT_HEIGHT * 2
        );
        this.toggleModeButton = new GuiButton(0, this.guiLeft + GUI_WIDTH - 108, this.guiTop + 4, 100, 20, this.targetingMode.toString()) {
            @Override
            public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
                if (!super.mousePressed(mc, mouseX, mouseY)) return false;

                targetingMode = targetingMode == TargetingMode.WHITELIST ? TargetingMode.BLACKLIST : TargetingMode.WHITELIST;
                this.displayString = targetingMode.toString();
                SRParasitesTrapsNetwork.CHANNEL.sendToServer(new UpdateTargetingMode(targetingMode));
                return true;
            }
        };
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int wheel = Mouse.getEventDWheel();

        if (wheel > 0) scrollOffset--;
        else if (wheel < 0) scrollOffset++;

        int maxVisibleRows = LIST_HEIGHT / ROW_HEIGHT;
        int maxScroll = Math.max(0, visibleEntityIds.size() - maxVisibleRows);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
        this.clampScrollOffset();
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.textField.mouseClicked(mouseX, mouseY, mouseButton);
        this.toggleModeButton.mousePressed(this.mc, mouseX, mouseY);

        if (mouseButton != 0) return;

        int maxVisibleRows = this.getMaxVisibleRows();
        int end = Math.min(this.visibleEntityIds.size(), this.scrollOffset + maxVisibleRows);

        for (int i = this.scrollOffset; i < end; i++) {
            int visibleIndex = i - this.scrollOffset;
            int rowX = this.guiLeft + LIST_X;
            int rowY = this.guiTop + LIST_Y + visibleIndex * ROW_HEIGHT;

            int deleteX = rowX;
            int deleteY = rowY + 3;

            boolean clickedDelete = mouseX >= deleteX
                    && mouseX < deleteX + DELETE_BUTTON_SIZE
                    && mouseY >= deleteY
                    && mouseY < deleteY + DELETE_BUTTON_SIZE;

            if (clickedDelete) {
                String entityId = this.visibleEntityIds.get(i);

                SRParasitesTrapsNetwork.CHANNEL.sendToServer(new RemoveEntityFromTargetingAugment(entityId));

                return;
            }
        }
    }

    private void clampScrollOffset() {
        int maxVisibleRows = this.getMaxVisibleRows();
        int maxScroll = Math.max(0, this.visibleEntityIds.size() - maxVisibleRows);
        this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, maxScroll));
    }

    private int getMaxVisibleRows() {
        return LIST_HEIGHT / ROW_HEIGHT;
    }

    private void rebuildVisibleEntityList() {
        this.visibleEntityIds.clear();

        ItemStack stack = this.player.getHeldItemMainhand();
        if (stack.getItem() != ModItems.TARGETING_AUGMENT) return;

        String filter = this.textField.getText().trim().toLowerCase();
        String[] entityList = TargetingAugment.getEntityList(stack);

        for (String entityId : entityList) {
            Entity entity = this.getCachedEntity(entityId);
            if (entity == null) continue;

            String entityName = entity.getName();

            boolean matchesFilter = filter.isEmpty()
                    || entityName.toLowerCase().contains(filter)
                    || entityId.toLowerCase().contains(filter);

            if (!matchesFilter) continue;
            this.visibleEntityIds.add(entityId);
        }

        this.visibleEntityIds.sort(String::compareToIgnoreCase);
        this.clampScrollOffset();
    }

    private Entity getCachedEntity(String entityId) {
        if (this.entityCache.containsKey(entityId)) return this.entityCache.get(entityId);

        Entity entity = EntityList.createEntityByIDFromName(new ResourceLocation(entityId), this.mc.world);
        if (entity == null) return null;

        this.entityCache.put(entityId, entity);
        return entity;
    }

    private void drawScrollbar() {
        int maxVisibleRows = LIST_HEIGHT / ROW_HEIGHT;
        if (visibleEntityIds.size() <= maxVisibleRows) return;

        int scrollbarX = guiLeft + GUI_WIDTH - 10;
        int scrollbarY = guiTop + LIST_Y;
        int scrollbarHeight = LIST_HEIGHT;

        drawRect(scrollbarX, scrollbarY, scrollbarX + 3, scrollbarY + scrollbarHeight, 0xFFAAAAAA);

        int maxScroll = visibleEntityIds.size() - maxVisibleRows;
        int handleHeight = Math.max(12, scrollbarHeight * maxVisibleRows / visibleEntityIds.size());
        int handleY = scrollbarY + (scrollbarHeight - handleHeight) * scrollOffset / maxScroll;

        drawRect(scrollbarX, handleY, scrollbarX + 3, handleY + handleHeight, 0xFF333333);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.textField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();

        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, GUI_WIDTH, GUI_HEIGHT);
        this.toggleModeButton.drawButton(this.mc, mouseX, mouseY, partialTicks);
        this.textField.drawTextBox();
        this.drawString(this.fontRenderer, "Targeting List", this.guiLeft + 8, this.guiTop + 8, 0xFFFFFF);

        this.rebuildVisibleEntityList();

        int maxVisibleRows = LIST_HEIGHT / ROW_HEIGHT;
        int end = Math.min(visibleEntityIds.size(), scrollOffset + maxVisibleRows);

        for (int i = this.scrollOffset; i < end; i++) {
            int visibleIndex = i - this.scrollOffset;
            int rowX = this.guiLeft + LIST_X;
            int rowY = this.guiTop + LIST_Y + visibleIndex * ROW_HEIGHT;

            String entityId = this.visibleEntityIds.get(i);
            Entity entity = this.getCachedEntity(entityId);

            if (entity == null) continue;

            int deleteX = rowX;
            int deleteY = rowY + 3;

            boolean hoveringDelete = mouseX >= deleteX
                    && mouseX < deleteX + DELETE_BUTTON_SIZE
                    && mouseY >= deleteY
                    && mouseY < deleteY + DELETE_BUTTON_SIZE;

            drawRect(
                    deleteX,
                    deleteY,
                    deleteX + DELETE_BUTTON_SIZE,
                    deleteY + DELETE_BUTTON_SIZE,
                    hoveringDelete ? 0xFFFF5555 : 0xFFAA0000
            );

            this.drawCenteredString(
                    this.fontRenderer,
                    "x",
                    deleteX + DELETE_BUTTON_SIZE / 2,
                    deleteY + 3,
                    0xFFFFFF
            );

            String entityName = entity.getName();
            int nameX = rowX + DELETE_BUTTON_SIZE + 6;
            int nameY = rowY + 6;
            int maxNameWidth = LIST_WIDTH - DELETE_BUTTON_SIZE - 18;

            if (this.fontRenderer.getStringWidth(entityName) > maxNameWidth) {
                entityName = this.fontRenderer.trimStringToWidth(entityName, maxNameWidth - this.fontRenderer.getStringWidth("...")) + "...";
            }

            this.drawString(
                    this.fontRenderer,
                    entityName,
                    nameX,
                    nameY,
                    0xFFFFFF
            );
        }

        this.drawScrollbar();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
