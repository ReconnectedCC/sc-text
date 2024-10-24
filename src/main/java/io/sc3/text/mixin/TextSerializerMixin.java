package io.sc3.text.mixin;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import io.sc3.text.TokenTextContent;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.Util;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Text.Serialization.class)
public class TextSerializerMixin {
  @Unique
  private static final ThreadLocal<String> token = ThreadLocal.withInitial(() -> null);

  @Inject(
    method = "toJson",
    at = @At(
      value = "HEAD"
    ),
    cancellable = true,
    locals = LocalCapture.CAPTURE_FAILHARD
  )
  private static void serialize(
    Text text, RegistryWrapper.WrapperLookup registries, CallbackInfoReturnable<JsonElement> cir
  ) {
    if (text.getContent() instanceof TokenTextContent tokenTextContent) {
      JsonObject elem = TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, text).getOrThrow(JsonParseException::new).getAsJsonObject();
      elem.addProperty("text", "<token>");
      elem.addProperty("token", tokenTextContent.getToken());
      cir.setReturnValue(elem);
    }
  }
  @Inject(
    method = "fromJson(Lcom/google/gson/JsonElement;Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/text/MutableText;",
    at = @At(
      value = "TAIL"
    ),
    locals = LocalCapture.CAPTURE_FAILHARD
  )
  private static void startDeserializingText(
    JsonElement json, RegistryWrapper.WrapperLookup registries, CallbackInfoReturnable<MutableText> cir
  ) {
    if (json.getAsJsonObject().has("token")) {
      String tokenn = JsonHelper.getString(json.getAsJsonObject(), "token");
      token.set(tokenn);
    }
  }
  @Inject(
    method = "fromJson(Lcom/google/gson/JsonElement;Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/text/MutableText;",
    at = @At("HEAD")
  )
  private static void clearLocalToken(CallbackInfoReturnable<MutableText> cir) {
    token.set(null);
  }
  @Inject(
    method = "fromJson(Lcom/google/gson/JsonElement;Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/text/MutableText;",
    at = @At("HEAD"),
    cancellable = true
  )
  private static void something(CallbackInfoReturnable<MutableText> cir) {
    if (token.get() != null) {
      cir.setReturnValue(MutableText.of(new TokenTextContent(token.get())));
    }
  }
  /*
  @Inject(
    method = "serialize(Lnet/minecraft/text/Text;Ljava/lang/reflect/Type;Lcom/google/gson/JsonSerializationContext;)Lcom/google/gson/JsonElement;",
    at = @At(
      value = "INVOKE",
      target = "Ljava/lang/IllegalArgumentException;<init>(Ljava/lang/String;)V"
    ),
    cancellable = true,
    locals = LocalCapture.CAPTURE_FAILHARD
  )
  private void serialize(
    Text text,
    Type type,
    JsonSerializationContext jsonSerializationContext,
    CallbackInfoReturnable<JsonElement> cir,
    JsonObject jsonObject
  ) {
    if (text.getContent() instanceof TokenTextContent tokenTextContent) {
      jsonObject.addProperty("text", "<token>");
      jsonObject.addProperty("token", tokenTextContent.getToken());
      cir.setReturnValue(jsonObject);
    }
  }

  @Inject(
    method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/MutableText;",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/util/JsonHelper;getString(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/String;",
      ordinal = 0
    ),
    locals = LocalCapture.CAPTURE_FAILHARD
  )
  private void startDeserializingText(
    JsonElement jsonElement,
    Type type,
    JsonDeserializationContext jsonDeserializationContext,
    CallbackInfoReturnable<MutableText> cir,
    JsonObject jsonObject
  ) {
    if (jsonObject.has("token")) {
      String token = JsonHelper.getString(jsonObject, "token");
      this.token.set(token);
    }
  }

  @Redirect(
    method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/MutableText;",
    at = @At(
      value = "INVOKE",
      target = "Lnet/minecraft/text/Text;literal(Ljava/lang/String;)Lnet/minecraft/text/MutableText;",
      ordinal = 1
    )
  )
  private MutableText applyTokenTextContent(String text) {
    if (token.get() != null) {
      return MutableText.of(new TokenTextContent(token.get()));
    } else {
      return MutableText.of(new LiteralTextContent(text));
    }
  }

  @Inject(
    method = "deserialize(Lcom/google/gson/JsonElement;Ljava/lang/reflect/Type;Lcom/google/gson/JsonDeserializationContext;)Lnet/minecraft/text/MutableText;",
    at = @At("HEAD")
  )
  private void clearLocalToken(CallbackInfoReturnable<MutableText> cir) {
    token.set(null);
  }*/
}
