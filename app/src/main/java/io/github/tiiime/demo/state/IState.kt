package io.github.tiiime.demo.state

import android.view.View
import androidx.annotation.IdRes

/**
 * LIMIT 泛型用于限制 StateContainer setState 方法只接受首先范围内的 State
 * 避免意料之外的 IState 设置给 StateContainer，出现异常
 *
 * 提供 enter 和 exit 两个生命周期回调，用于设置 view 状态
 */
interface IState<LIMIT> where LIMIT : IState<LIMIT> {
    fun enter(stateContainer: StateContainer<LIMIT>)
    fun exit(stateContainer: StateContainer<LIMIT>)
}

/**
 * 简化 visibility 状态切换实现
 * 类似，可以实现 shimmer loading 等复杂效果
 */
open class VisibilityChangeState<LimitState>(@IdRes private val containerId: Int) :
    IState<LimitState> where LimitState : IState<LimitState> {
    override fun enter(stateContainer: StateContainer<LimitState>) {
        stateContainer.getContainer().findViewById<View>(containerId)?.visibility = View.VISIBLE
    }

    override fun exit(stateContainer: StateContainer<LimitState>) {
        stateContainer.getContainer().findViewById<View>(containerId)?.visibility = View.GONE
    }
}