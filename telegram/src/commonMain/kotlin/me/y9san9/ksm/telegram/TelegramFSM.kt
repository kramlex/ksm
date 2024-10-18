package me.y9san9.ksm.telegram

import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.types.update.MessageUpdate
import kotlinx.coroutines.flow.Flow
import me.y9san9.ksm.telegram.plugin.TelegramFSMBase
import me.y9san9.ksm.telegram.plugin.installTelegramFSM
import me.y9san9.pipeline.context.*
import me.y9san9.pipeline.proceed

public class TelegramFSM(public val context: PipelineContext) {
    public suspend fun run(bot: TelegramBot, messageUpdates: Flow<MessageUpdate>) {
        val subject = buildPipelineContext {
            this[TelegramFSMBase.Subject.Bot] = bot
            this[TelegramFSMBase.Subject.UpdateFlow] = messageUpdates
        }
        proceed(subject)
    }

    public suspend fun proceed(subject: PipelineContext): PipelineContext {
        val pipeline = context.require(TelegramFSMBase.Config.Pipeline)
        return pipeline.proceed(context, subject)
    }

    public class Builder(context: PipelineContext) {
        public val context: MutablePipelineContext = mutablePipelineContextOf(context)

        public constructor() : this(PipelineContext.Empty) {
            installTelegramFSM()
        }

        public fun build(): TelegramFSM {
            return TelegramFSM(context.toPipelineContext())
        }
    }
}

public inline fun buildTelegramFSM(
    from: TelegramFSM? = null,
    block: TelegramFSM.Builder.() -> Unit = {}
): TelegramFSM {
    val builder = when (from) {
        null -> TelegramFSM.Builder()
        else -> TelegramFSM.Builder(from.context)
    }
    builder.block()
    return builder.build()
}

public inline fun TelegramFSM?.build(block: TelegramFSM.Builder.() -> Unit): TelegramFSM {
    return buildTelegramFSM(from = this, block = block)
}
