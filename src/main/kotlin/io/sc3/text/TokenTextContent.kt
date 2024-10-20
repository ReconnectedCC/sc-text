package io.sc3.text

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.text.KeybindTextContent
import net.minecraft.text.StringVisitable
import net.minecraft.text.Style
import net.minecraft.text.TextContent
import java.util.*
import java.util.function.Function


class TokenTextContent(val token: String) : TextContent {
  override fun toString() = "token{***}"
  val CODEC: MapCodec<TokenTextContent> =
    RecordCodecBuilder.mapCodec { i ->
      i.group(
        Codec.STRING.fieldOf("token").forGetter(TokenTextContent::token)
      ).apply(i,  ::TokenTextContent)
    }

  override fun <T> visit(visitor: StringVisitable.StyledVisitor<T>, style: Style): Optional<T> {
    // For styled visitors (e.g. rendering in-game), render the token as normal
    return visitor.accept(style, token)
  }

  override fun <T> visit(visitor: StringVisitable.Visitor<T>): Optional<T> {
    // For unstyled visitors (e.g. logs), censor the token
    return visitor.accept("***")
  }

  override fun getType(): TextContent.Type<*> {
    return TextContent.Type(CODEC, "token");
  }
}
