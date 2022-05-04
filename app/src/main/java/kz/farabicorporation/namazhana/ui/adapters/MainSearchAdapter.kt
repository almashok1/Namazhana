package kz.farabicorporation.namazhana.ui.adapters

import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.recyclerview.widget.DiffUtil
import kz.farabicorporation.namazhana.common.R
import kz.farabicorporation.namazhana.common.binding.BindingBasicAsyncRvAdapter
import kz.farabicorporation.namazhana.common.extensions.getColorCompat
import kz.farabicorporation.namazhana.common.extensions.onSafeClick
import kz.farabicorporation.namazhana.data.models.PlaceMini
import kz.farabicorporation.namazhana.databinding.ItemMainSearchBinding

class MainSearchAdapter(
    private val onClick: (PlaceMini) -> Unit
) : BindingBasicAsyncRvAdapter<Pair<IntRange, PlaceMini>, ItemMainSearchBinding>(
    Callback(), ItemMainSearchBinding::inflate
) {

    class Callback : DiffUtil.ItemCallback<Pair<IntRange, PlaceMini>>() {
        override fun areItemsTheSame(oldItem: Pair<IntRange, PlaceMini>, newItem: Pair<IntRange, PlaceMini>): Boolean {
            return oldItem.second.id == newItem.second.id
        }

        override fun areContentsTheSame(oldItem: Pair<IntRange, PlaceMini>, newItem: Pair<IntRange, PlaceMini>): Boolean {
            return oldItem.second.name == newItem.second.name
        }
    }

    override fun bind(item: Pair<IntRange, PlaceMini>, binding: ItemMainSearchBinding, position: Int) {
        val name = item.second.name
        val start = item.first.first
        val end = item.first.last
        binding.root.text = buildSpannedString {
            color(binding.root.context.getColorCompat(R.color.text_secondary)) {
                append(name.substring(0, start))
            }
            color(binding.root.context.getColorCompat(R.color.main_primary_one)) {
                append(name.substring(start, minOf(end + 1, item.second.name.length)))
            }
            if (end + 1 < name.length) color(binding.root.context.getColorCompat(R.color.text_secondary)) {
                append(name.substring(end + 1))
            }
        }

        binding.root.onSafeClick { onClick(item.second) }
    }
}