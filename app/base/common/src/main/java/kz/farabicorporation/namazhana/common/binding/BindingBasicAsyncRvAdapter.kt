package kz.farabicorporation.namazhana.common.binding

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class BindingBasicAsyncRvAdapter<M: Any, V: ViewBinding>(
    diffUtil: DiffUtil.ItemCallback<M>,
    private val inflate: (inflater: LayoutInflater, parent: ViewGroup?, attachToParent: Boolean) -> V
) : RecyclerView.Adapter<BindingBasicAsyncRvAdapter.ViewHolder<M, V>>() {

    private val differ: AsyncListDiffer<M> = AsyncListDiffer(this, diffUtil)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<M, V> {
        val view = inflate.invoke(LayoutInflater.from(parent.context), parent, false)
        setViewParams(parent, view)
        return ViewHolder(view, ::bind)
    }

    open fun setViewParams(parent: ViewGroup, view: V) {}

    override fun onBindViewHolder(viewHolder: ViewHolder<M, V>, position: Int) {
        viewHolder.bind(differ.currentList[position], position)
    }

    override fun getItemCount(): Int = differ.currentList.size

    open fun submitList(list: List<M>?, callback: (() -> Unit)? = null) {
        differ.submitList(list, callback)
    }

    open fun bind(item: M, binding: V, position: Int) {}

    class ViewHolder<M: Any, V: ViewBinding>(
        private val binding: V,
        private val bind: (item: M, binding: V, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: M, position: Int) {
            bind.invoke(item, binding, position)
        }
    }
}