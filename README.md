# StateLayout

提供业务需求中常见状态切换 Widget 容器实现抽象。可用于需要 Loading/Empty/Error 等状态切换的 UI 业务。

以一个常见 case 为例，进入页面，展示 loading 状态，数据加载失败后，展示异常 UI，并提供刷新 UI。用户点击刷新后，触发数据重新加载。

### IState
提供 IState，将 UI 状态切换抽象为两个 `enter` 和 `exit` 回调时机。  
Loading/Error/Content 等状态实现自己的 State，
在自己的生命周期内设置 UI 状态(也可以设置点击事件等对外回调)。

```kotlin
interface IState<LIMIT> where LIMIT : IState<LIMIT> {
    fun enter(stateContainer: StateContainer<LIMIT>)
    fun exit(stateContainer: StateContainer<LIMIT>)
}
```

State 实现可以使用 stateContainer.getContainer() 获取 ViewGroup。   
类似 VisibilityChangeState，去操作 ViewGroup 内容。
```kotlin
open class VisibilityChangeState<T>(@IdRes private val containerId: Int) :
    IState<T> where T : IState<T> {
    override fun enter(stateContainer: StateContainer<T>) {
        stateContainer.getContainer().findViewById<View>(containerId)?.visibility = View.VISIBLE
    }

    override fun exit(stateContainer: StateContainer<T>) {
        stateContainer.getContainer().findViewById<View>(containerId)?.visibility = View.GONE
    }
}
```

实现好的通用 IState 逻辑，可以方便的给其他 StateContainer 复用，参考 [LoadingState](./app/src/main/java/io/github/tiiime/demo/state/widget/EmptyLayout.kt#L42) 复用 VisibilityChangeState 的方式。


### StateContainer
容器 Layout 需要实现 StateContainer，里面维护了一个 `currentState`，并提供 `StateContainer#setState` 方法供外部使用。      
需要注意，实现 `StateContainer<LimitState : IState<LimitState>>` 需要指定 IState，限制自己 `setState` 方法接收的 IState 范围。避免用户传入预期外 State。

```kotlin   
interface StateContainer<LimitState : IState<LimitState>> {

    fun setState(newState: LimitState) 
    
    ...
}

class EmptyLayout : FrameLayout, StateContainer<EmptyLayout.State> {
    ...
    
    // 默认实现
    // override fun setState(newState: EmptyLayout.State) {
    //     super.setState(newState)
    // }

    interface State : IState<State>
}
```

### EmptyLayout
EmptyLayout.State 限制了 EmptyLayout#setState 参数范围，保证逻辑安全。  
LoadingState 等 State 复用了 VisibilityChangeState 实现。

```kotlin
// 

class EmptyLayout : FrameLayout, StateContainer<EmptyLayout.State> {
    ...

    /**
    * EmptyLayout.State 首先限制了 EmptyLayout#setState 方法接收的参数类型
    * 所有加入 EmptyLayout 的 state 都要实现这个接口
    * 使用方在实现 State 时，就要考虑好如何使用 Layout 
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
```

实现 StateContainer 参考 [EmptyLayout](./app/src/main/java/io/github/tiiime/demo/state/widget/EmptyLayout.kt)。  

使用 EmptyLayout 参考 [MainActivity](./app/src/main/java/io/github/tiiime/demo/MainActivity.kt)。