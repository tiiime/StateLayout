package io.github.tiiime.demo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.lifecycle.coroutineScope
import androidx.recyclerview.widget.RecyclerView
import io.github.tiiime.demo.state.VisibilityChangeState
import io.github.tiiime.demo.state.widget.EmptyLayout
import io.github.tiiime.demo.statelayout.R
import io.github.tiiime.demo.statelayout.databinding.ActivityMainBinding
import io.github.tiiime.demo.statelayout.databinding.LayoutItemTextBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), EmptyLayout.ErrorState.Callback {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.content.adapter = SimpleAdapter()

        // 初始化 UI，设置为 loading 状态
        binding.stateContainer.setState(EmptyLayout.LoadingState)

        lifecycle.coroutineScope.launch(context = Dispatchers.Main) {
            delay(1000)
            // 展示下空视图
            binding.stateContainer.setState(EmptyLayout.EmptyState)
            delay(1000)
            binding.stateContainer.setState(EmptyLayout.LoadingState)
            delay(1000)
            // 数据加载失败，展示错误，提供重试按钮
            binding.stateContainer.setState(EmptyLayout.ErrorState(this@MainActivity))
        }
    }

    override fun retryClick() {
        lifecycle.coroutineScope.launch(Dispatchers.Main) {
            binding.stateContainer.setState(EmptyLayout.LoadingState)
            delay(3000)
            binding.stateContainer.setState(ContentState)
        }
    }

    private object ContentState : VisibilityChangeState<EmptyLayout.State>(R.id.content),
        EmptyLayout.State

    private class SimpleAdapter : RecyclerView.Adapter<VH>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(
            LayoutItemTextBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )

        override fun getItemCount(): Int = 100

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.binding.text.text = "haha: $position"
        }

    }

    private class VH(val binding: LayoutItemTextBinding) : RecyclerView.ViewHolder(binding.root)
}