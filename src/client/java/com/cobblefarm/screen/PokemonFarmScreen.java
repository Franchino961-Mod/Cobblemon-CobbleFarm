package com.cobblefarm.screen;

import com.cobblefarm.CobbleFarm;
import com.cobblefarm.network.CobbleFarmNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class PokemonFarmScreen extends HandledScreen<PokemonFarmScreenHandler> {

    private static final Identifier TEXTURE = Identifier.of(CobbleFarm.MOD_ID, "textures/gui/pokemon_farm.png");

    private static final int GUI_WIDTH  = 176;
    private static final int GUI_HEIGHT = 166;

    private static final int PROGRESS_X     = 79;
    private static final int PROGRESS_Y     = 58;
    private static final int PROGRESS_W     = 22;
    private static final int PROGRESS_H     = 16;

    private ButtonWidget pauseButton;

    public PokemonFarmScreen(PokemonFarmScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth  = GUI_WIDTH;
        this.backgroundHeight = GUI_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        int btnX = x + 105;
        int btnY = y + 54;
        pauseButton = ButtonWidget.builder(getPauseText(), btn -> sendPauseToggle())
                .dimensions(btnX, btnY, 60, 20)
                .build();
        this.addDrawableChild(pauseButton);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        float progress = handler.getProgress();
        if (!handler.isPaused()) {
            int filledW = (int) (PROGRESS_W * progress);
            context.drawTexture(TEXTURE, x + PROGRESS_X, y + PROGRESS_Y,
                    176, 0, filledW, PROGRESS_H);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);

        if (pauseButton != null) {
            pauseButton.setMessage(getPauseText());
        }

        String bufferText = handler.getBufferCount() + " / " + handler.getBufferCapacity();
        context.drawText(this.textRenderer, "Buffer: " + bufferText,
                x + 8, y + 78, 0x555555, false);

        String status = handler.isPaused() ? "§cPaused" : "§aActive";
        context.drawText(this.textRenderer, "Status: " + status,
                x + 8, y + 90, 0x555555, false);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
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
