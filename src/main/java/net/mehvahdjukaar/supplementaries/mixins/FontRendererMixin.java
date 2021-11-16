package net.mehvahdjukaar.supplementaries.mixins;

import net.mehvahdjukaar.supplementaries.api.IAntiqueTextProvider;
import net.mehvahdjukaar.supplementaries.common.Textures;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

//@Mixin(targets = {"net/minecraft/client/gui/FontRenderer$CharacterRenderer"})
@Mixin(Font.class)
public abstract class FontRendererMixin implements IAntiqueTextProvider {

    private boolean antique = false;

    @Override
    public boolean hasAntiqueInk() {
        return antique;
    }

    @Override
    public void setAntiqueInk(boolean hasInk) {
        antique = hasInk;
    }

    @Final
    @Shadow
    private Function<ResourceLocation, Font> fonts;

    @Inject(method = "getFontSet", at = @At("HEAD"), cancellable = true)
    private void accept(ResourceLocation resourceLocation, CallbackInfoReturnable<Font> cir) {
        if (antique) {
            cir.setReturnValue(this.fonts.apply(Textures.ANTIQUABLE_FONT));
        }
    }

}