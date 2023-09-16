package io.github.tiiime.demo.state.widget

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.FrameLayout
import io.github.tiiime.demo.state.IState
import io.github.tiiime.demo.state.StateContainer

class StateLayout : FrameLayout, StateContainer<StateLayout.State> {
    override var currentState: State? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    override fun getContainer(): ViewGroup = this


    interface State : IState<State>
}