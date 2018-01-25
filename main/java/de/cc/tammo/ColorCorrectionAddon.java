package de.cc.tammo;

import net.labymod.api.LabyModAddon;
import net.labymod.gui.elements.ColorPicker;
import net.labymod.settings.elements.*;
import net.labymod.utils.Consumer;
import net.labymod.utils.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.List;

public class ColorCorrectionAddon extends LabyModAddon{

    private boolean enabled = true;

    private Color color = new Color(255, 255, 255, 0);

    private int alpha = 25;


    @Override
    public void onEnable() {
        System.out.println("-----------------------------------------------------");
        this.getApi().registerForgeListener(this);
    }

    @Override
    public void onDisable() {}

    @Override
    public void loadConfig() {
        this.enabled = !this.getConfig().has("enabled") || this.getConfig().get("enabled").getAsBoolean();
        this.alpha = this.getConfig().has("alpha") ? this.getConfig().get("alpha").getAsInt() : 25;

        final int red = this.getConfig().has("red") ? this.getConfig().get("red").getAsInt() : 255;
        final int green = this.getConfig().has("green") ? this.getConfig().get("green").getAsInt() : 255;
        final int blue = this.getConfig().has("blue") ? this.getConfig().get("blue").getAsInt() : 255;
        this.color = new Color(red, green, blue, 255);
    }

    @Override
    protected void fillSettings(final List<SettingsElement> list) {
        final BooleanElement enabled = new BooleanElement("Enabled", new ControlElement.IconData(Material.EMERALD), new Consumer<Boolean>() {

            @Override
            public void accept(final Boolean enabled) {
                ColorCorrectionAddon.this.enabled = enabled;

                ColorCorrectionAddon.this.getConfig().addProperty("enabled", ColorCorrectionAddon.this.enabled);
                ColorCorrectionAddon.this.saveConfig();
            }

        }, this.enabled);

        enabled.setDescriptionText("Enable the Color Correction mod");

        list.add(enabled);

        final SliderElement sliderElement = new SliderElement("Transparency", new ControlElement.IconData(Material.ANVIL), this.alpha);
        sliderElement.setRange(0, 50);
        sliderElement.setDescriptionText("Transparency of color");
        sliderElement.addCallback(new Consumer<Integer>() {

            @Override
            public void accept(final Integer alpha) {
                ColorCorrectionAddon.this.alpha = alpha;

                ColorCorrectionAddon.this.getConfig().addProperty("alpha", ColorCorrectionAddon.this.alpha);
                ColorCorrectionAddon.this.saveConfig();
            }

        });

        list.add(sliderElement);

        final ColorPickerCheckBoxBulkElement bulkElement = new ColorPickerCheckBoxBulkElement("Color");
        final ColorPicker colorPicker = new ColorPicker("Color", this.color, new ColorPicker.DefaultColorCallback() {

            @Override
            public final Color getDefaultColor() {
                return ColorCorrectionAddon.this.color;
            }

        }, 0, 0, 0, 0);

        colorPicker.setUpdateListener(new Consumer<Color>() {

            @Override
            public void accept(final Color color) {
                ColorCorrectionAddon.this.color = color;

                ColorCorrectionAddon.this.getConfig().addProperty("red", color.getRed());
                ColorCorrectionAddon.this.getConfig().addProperty("green", color.getGreen());
                ColorCorrectionAddon.this.getConfig().addProperty("blue", color.getBlue());
                ColorCorrectionAddon.this.saveConfig();
            }

        });

        colorPicker.setHasAdvanced(true);

        bulkElement.setDescriptionText("Color for Color Correction");
        bulkElement.addColorPicker(colorPicker);

        list.add(bulkElement);
    }

    @SubscribeEvent
    public void render(final RenderGameOverlayEvent event) {
        if (!this.enabled) {
            return;
        }
        Gui.drawRect(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, this.getColor());
    }

    private int getColor() {
        return new Color(this.color.getRed(), this.color.getGreen(), this.color.getBlue(), this.getAlpha()).getRGB();
    }

    private int getAlpha() {
        return (int) (((float) (this.alpha) / 100) * 255);
    }

}
