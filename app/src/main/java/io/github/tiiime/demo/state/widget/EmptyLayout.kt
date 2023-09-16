package io.github.tiiime.demo.state.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import io.github.tiiime.demo.state.IState
import io.github.tiiime.demo.state.StateContainer
import io.github.tiiime.demo.state.VisibilityChangeState
import io.github.tiiime.demo.statelayout.R

class EmptyLayout : FrameLayout, StateContainer<EmptyLayout.State> {
    override var currentState: State? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    constructor(
        context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_empty, this, true)
    }

    override fun getContainer(): EmptyLayout = this

    /**
     * 所有加入 EmptyLayout 的 state 都要实现这个接口
     *
     * 查看实现，可以确认有多少使用 EmptyLayout 的状态
     */
    interface State : IState<State>

    /**
     * 展示 Loading 状态
     */
    object LoadingState : VisibilityChangeState<State>(R.id.loading), State

    /**
     * 展示空视图
     */
    object EmptyState : VisibilityChangeState<State>(R.id.empty_hint), State

    /**
     * 展示 Error，提供重试回调
     */
    class ErrorState(private val callback: Callback) :
        VisibilityChangeState<State>(R.id.error_retry), State {
        interface Callback {
            fun retryClick()
        }

        override fun enter(stateContainer: StateContainer<State>) {
            super.enter(stateContainer)
            stateContainer.getContainer().findViewById<View>(R.id.error_retry).setOnClickListener {
                callback.retryClick()
            }
        }
    }
}

