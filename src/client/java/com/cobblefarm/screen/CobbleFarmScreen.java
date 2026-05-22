package com.cobblefarm.screen;

import com.cobblefarm.CobbleFarm;
import com.cobblefarm.item.FarmBallItem;
import com.cobblefarm.network.CobbleFarmNetworking;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class CobbleFarmScreen extends HandledScreen<CobbleFarmScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of(CobbleFarm.MOD_ID, "textures/gui/cobblefarm.png");

    private static final int GUI_WIDTH  = 176;
    private static final int GUI_HEIGHT = 166;

    private static final int PROGRESS_X     = 71;
    private static final int PROGRESS_Y     = 55;
    private static final int PROGRESS_W     = 97;
    private static final int PROGRESS_H     = 5;

    private ButtonWidget pauseButton;
    private LivingEntity cachedEntity;
    private ItemStack lastStack = ItemStack.EMPTY;

    public CobbleFarmScreen(CobbleFarmScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth  = GUI_WIDTH;
        this.backgroundHeight = GUI_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        int btnX = x + 110;
        int btnY = y + 4;
        pauseButton = ButtonWidget.builder(getPauseText(), btn -> sendPauseToggle())
                .dimensions(btnX, btnY, 60, 20)
                .build();
        this.addDrawableChild(pauseButton);
    }

    private void updateCachedEntity() {
        ItemStack currentStack = handler.getSlot(0).getStack();
        if (ItemStack.areItemsAndComponentsEqual(currentStack, lastStack)) {
            return;
        }
        lastStack = currentStack.copy();

        if (currentStack.isEmpty() || !FarmBallItem.hasPokemon(currentStack)) {
            cachedEntity = null;
            return;
        }

        try {
            String species = FarmBallItem.getSpecies(currentStack);
            boolean shiny = FarmBallItem.isShiny(currentStack);
            int level = FarmBallItem.getLevel(currentStack);
            String form = FarmBallItem.getForm(currentStack);
            
            StringBuilder props = new StringBuilder(species);
            props.append(" level=").append(level);
            if (shiny) props.append(" shiny=yes");
            if (form != null && !form.isEmpty() && !form.equals("normal")) {
                props.append(" form=").append(form);
            }

            Pokemon pokemon = PokemonProperties.Companion.parse(props.toString()).create();
            com.cobblemon.mod.common.entity.pokemon.PokemonEntity entity = com.cobblemon.mod.common.CobblemonEntities.POKEMON.create(MinecraftClient.getInstance().world);
            if (entity != null) {
                entity.setPokemon(pokemon);
            }
            cachedEntity = entity;
        } catch (Exception e) {
            CobbleFarm.LOGGER.error("Failed to parse and spawn Pokemon for GUI rendering", e);
            cachedEntity = null;
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        float progress = handler.getProgress();
        if (!handler.isPaused() && progress > 0) {
            int filledW = (int) (PROGRESS_W * progress);
            context.fill(x + PROGRESS_X, y + PROGRESS_Y, 
                         x + PROGRESS_X + filledW, y + PROGRESS_Y + PROGRESS_H, 0xFF3DE031);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);

        if (pauseButton != null) {
            pauseButton.setMessage(getPauseText());
        }

        String status = handler.isPaused() ? "§cPaused" : "§aActive";
        context.drawText(this.textRenderer, "Status: " + status,
                x + 110, y + 26, 0x555555, false);

        updateCachedEntity();
        if (cachedEntity != null) {
            // Drive animations in GUI
            cachedEntity.age = (int) (MinecraftClient.getInstance().world.getTime());
            
            // Render entity at X=8, Y=15, W=52, H=63. Center X = 34, Bottom Y = 78
            // Match Tiny MobFarm Remastered logic: X=8, Y=15, Width=52, Height=63
            int x1 = x + 8;
            int y1 = y + 15;
            int x2 = x1 + 52;
            int y2 = y1 + 63;
            int size = 28; 
            InventoryScreen.drawEntity(context, x1, y1, x2, y2, size, 0.0625F, (float)mouseX, (float)mouseY, cachedEntity);
        }
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // We only render text here. Let's position it accurately.
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 0x404040, false);
        context.drawText(this.textRenderer, Text.translatable("container.inventory"),
                this.playerInventoryTitleX, this.playerInventoryTitleY, 0x404040, false);
    }

    private void sendPauseToggle() {
        var be = handler.getBlockEntity();
        if (be != null) {
            ClientPlayNetworking.send(
                new CobbleFarmNetworking.PauseTogglePayload(be.getPos())
            );
        }
    }

    private Text getPauseText() {
        return handler.isPaused()
                ? Text.translatable("gui.cobblefarm.farm.resume")
                : Text.translatable("gui.cobblefarm.farm.pause");
    }
}
