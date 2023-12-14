package com.movies.syncflix.common.coremvi

import com.movies.syncflix.common.coremvi.actions.Action
import com.movies.syncflix.common.coremvi.actions.AsyncAction
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlin.reflect.KClass

/**
 * List with all transformations of the screen events.
 *
 * @param eventStream source stream with screen events
 */
open class EventTransformerList<INPUT_ACTION : AsyncAction, OUTPUT_ACTION : Action>(
    val eventStream: Flow<INPUT_ACTION>
) : MutableList<Flow<OUTPUT_ACTION>> by ArrayList() {

    /**
     * Unary operator overload to add Flows directly to the list
     */
    operator fun Flow<OUTPUT_ACTION>.unaryPlus() {
        add(this)
    }

    /**
     * Add several transformations to the list
     */
    fun addAll(vararg transformations: Flow<OUTPUT_ACTION>) {
        addAll(transformations.toList())
    }


    /**
     * Reaction on event of type [T].
     *
     * You should use it when the event [T] shouldn't be passed through data stream,
     * and we need to simply react on the event (for example, to open a screen, log something, or to send analytics).
     *
     * @param action функция реакции.
     */
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    inline infix fun <reified T : AsyncAction> Flow<T>.react(
        noinline action: (T) -> Unit
    ): Flow<T> {
        return flatMapMerge {
            action(it)
            emptyFlow()
        }
    }

    /**
     * Reaction on event of type [T].
     *
     * You should use it when the event [T] shouldn't be passed through data stream,
     * and we need to simply react on the event (for example, to open a screen, log something, or to send analytics).
     *
     * @param action функция реакции.
     */
    inline fun <reified T : AsyncAction> react(
        noinline action: (T) -> Unit
    ): Flow<T> {
        return eventStream.filterIsInstance<T>().react(action)
    }

    /**
     * Reaction on event of type [T].
     *
     * You should use it when the event [T] shouldn't be passed through data stream,
     * and we need to simply react on the event (for example, to open a screen, log something, or to send analytics).
     *
     * @param action функция реакции.
     */
    inline infix fun <reified T> KClass<T>.react(
        noinline action: (T) -> Unit
    ): Flow<T> where T : INPUT_ACTION {
        return eventStream.filterIsInstance<T>().react(action)
    }

    /**
     * Maps events of type [T] in another type, successor of type [E].
     *
     * Used when we need to map event to another event,
     * For example, when the PhotoButtonClick is appeared, we need to emit OpenSelectPhotoDialog.
     *
     * @param mapper mapper function.
     */
    inline infix fun <reified T : AsyncAction> Flow<T>.eventToEvent(
        noinline mapper: suspend (T) -> OUTPUT_ACTION
    ): Flow<OUTPUT_ACTION> {
        return map(mapper)
    }

    /**
     * Maps events of type [T] in another type, successor of type [E].
     *
     * Used when we need to map event to another event,
     * For example, when the PhotoButtonClick is appeared, we need to emit OpenSelectPhotoDialog.
     *
     * @param mapper mapper function.
     */
    inline fun <reified T : AsyncAction> eventToEvent(
        noinline mapper: suspend (T) -> OUTPUT_ACTION
    ): Flow<OUTPUT_ACTION> {
        return eventStream.filterIsInstance<T>() eventToEvent mapper
    }

    /**
     * Maps events of type [T] in another type, successor of type [E].
     *
     * Used when we need to map event to another event,
     * For example, when the PhotoButtonClick is appeared, we need to emit OpenSelectPhotoDialog.
     *
     * @param mapper mapper function.
     */
    inline infix fun <reified T : AsyncAction> KClass<T>.eventToEvent(
        noinline mapper: suspend (T) -> OUTPUT_ACTION
    ): Flow<OUTPUT_ACTION> {
        return eventStream.filterIsInstance<T>().eventToEvent(mapper)
    }

    /**
     * Maps events of type [T] to [Flow]<[E]>.
     *
     * Can be used when we need to transform event to a stream (like [flatMap]),
     * For example, when we should load data from data-layer on ButtonClicked event.
     *
     * @param mapper mapper function.
     */
    @OptIn(FlowPreview::class)
    infix fun <T : AsyncAction> Flow<T>.eventToStream(
        mapper: (T) -> Flow<OUTPUT_ACTION>
    ): Flow<OUTPUT_ACTION> {
        return this.flatMapMerge { mapper(it) }
    }

    /**
     * Maps events of type [T] to [Flow]<[E]>.
     *
     * Can be used when we need to transform event to a stream (like [flatMap]),
     * For example, when we should load data from data-layer on ButtonClicked event.
     *
     * @param mapper mapper function.
     */
    inline fun <reified T : AsyncAction> eventToStream(
        noinline mapper: (T) -> Flow<OUTPUT_ACTION>
    ): Flow<OUTPUT_ACTION> {
        return eventStream.filterIsInstance<T>().eventToStream(mapper)
    }

    /**
     * Maps events of type [T] to [Flow]<[E]>.
     *
     * Can be used when we need to transform event to a stream (like [flatMap]),
     * For example, when we should load data from data-layer on ButtonClicked event.
     *
     * @param mapper mapper function.
     */
    inline infix fun <reified T : AsyncAction> KClass<T>.eventToStream(
        noinline mapper: (T) -> Flow<OUTPUT_ACTION>
    ): Flow<OUTPUT_ACTION> {
        return eventStream.filterIsInstance<T>().eventToStream(mapper)
    }

    /**
     * Maps [Flow]<[T]> to [Flow]<[E]>.
     *
     * Can be used when the [eventToStream] is not enough, and we should modify source [Flow].
     * For example, when we need to add debounce and distinctUntilChanged on TextChanged event before sending it to network.
     * @param mapper mapper function.
     */
    infix fun <T : AsyncAction> Flow<T>.streamToStream(
        mapper: (Flow<T>) -> Flow<OUTPUT_ACTION>
    ): Flow<OUTPUT_ACTION> {
        return mapper(this)
    }

    /**
     * Maps [Flow]<[T]> to [Flow]<[E]>.
     *
     * Can be used when the [eventToStream] is not enough, and we should modify source [Flow].
     * For example, when we need to add debounce and distinctUntilChanged on TextChanged event before sending it to network.
     *
     * @param mapper mapper function.
     */
    inline fun <reified T : AsyncAction> streamToStream(
        noinline mapper: (Flow<T>) -> Flow<OUTPUT_ACTION>
    ): Flow<OUTPUT_ACTION> {
        return eventStream.filterIsInstance<T>().streamToStream(mapper)
    }

    /**
     * Maps [Flow]<[T]> to [Flow]<[E]>.
     *
     * Can be used when the [eventToStream] is not enough, and we should modify source [Flow].
     * For example, when we need to add debounce and distinctUntilChanged on TextChanged event before sending it to network.
     *
     * @param mapper mapper function.
     */
    inline infix fun <reified T : AsyncAction> KClass<T>.streamToStream(
        noinline mapper: (Flow<T>) -> Flow<OUTPUT_ACTION>
    ): Flow<OUTPUT_ACTION> {
        return eventStream.filterIsInstance<T>().streamToStream(mapper)
    }

    /**
     * Filters events by a given [filterCondition].
     */
    inline infix fun <reified T : AsyncAction> KClass<T>.filter(noinline filterCondition: (T) -> Boolean): Flow<T> {
        return eventStream.filterIsInstance<T>().filter(filterCondition)
    }

    /**
     * Roadmap
     * 1. Decompose events filtered by type, to process them in another middleware.
     */
}