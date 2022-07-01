package codes.ztereohype.autotechno.mixin;

import codes.ztereohype.autotechno.AutoTechno;
import codes.ztereohype.autotechno.chat.Event;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChatHud.class)
public abstract class MixinChatHud {
    @Inject(method = "addMessage(Lnet/minecraft/text/Text;IIZ)V", at = @At("HEAD"))
    private void sendRespect(Text message, int messageId, int timestamp, boolean bl, CallbackInfo ci) {
        Event event = AutoTechno.detector.scanForEvent(message.getString());

        System.out.println(event);

        if (event != null) {
            String technoMessage = AutoTechno.messageRandomiser.getRandomMessage(event);
            System.out.println(technoMessage);
            AutoTechno.client.sendMessage(technoMessage);
        }
    }
}
