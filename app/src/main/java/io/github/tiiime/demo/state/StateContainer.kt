package io.github.tiiime.demo.state

import android.view.ViewGroup

/**
 * 提供一个状态切换容器抽象。常用于需要 Loading/Empty/Error 等状态切换的 UI 业务。
 *
 * StateContainer 的实现，需要指定 IState 范围，限制接受外部设置的 IState
 * 避免传入不支持的 State， 执行与预期的行为
 */
interface StateContainer<LimitState : IState<LimitState>> {
    var currentState: LimitState?

    fun setState(newState: LimitState) {
        if (newState == currentState) {
            return
        }

        val lastState = currentState
        currentState = newState
        lastState?.exit(this)
        newState.enter(this)
    }

    fun getContainer(): ViewGroup
}

