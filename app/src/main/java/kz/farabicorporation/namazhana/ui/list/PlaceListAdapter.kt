package kz.farabicorporation.namazhana.ui.list

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import kz.farabicorporation.namazhana.common.binding.BindingBasicAsyncRvAdapter
import kz.farabicorporation.namazhana.common.extensions.onSafeClick
import kz.farabicorporation.namazhana.data.models.PlaceMini
import kz.farabicorporation.namazhana.data.models.getColor
import kz.farabicorporation.namazhana.data.models.getText
import kz.farabicorporation.namazhana.data.models.toWorkTime
import kz.farabicorporation.namazhana.databinding.ItemPlaceListBinding
import kz.farabicorporation.namazhana.ui.adapters.MainPlaceMiniAdapter
import kz.farabicorporation.namazhana.ui.getDistance
import kz.farabicorporation.namazhana.ui.loadWithPlaceType

class PlaceListAdapter(
    private val onClick: (PlaceMini) -> Unit
): BindingBasicAsyncRvAdapter<PlaceMini, ItemPlaceListBinding>(
    MainPlaceMiniAdapter.Callback(), ItemPlaceListBinding::inflate
) {

    @SuppressLint("SetTextI18n")
    override fun bind(item: PlaceMini, binding: ItemPlaceListBinding, position: Int) = with(binding) {
        tvDistance.isVisible = item.distance != null
        tvDistance.text = item.distance?.getDistance(root.context)
        val color = item.type.getColor(root.context)
        tvDistance.setTextColor(color)
        TextViewCompat.setCompoundDrawableTintList(tvDistance, ColorStateList.valueOf(color))
        tvName.text = item.name
        tvTime.text = (item.startWorkTime to item.endWorkTime).toWorkTime(root.context)
        tvPlaceType.text = item.type.getText(root.context)
        tvPlaceType.setTextColor(color)
        ivPhoto.loadWithPlaceType(item.previewImage, item.type)
        root.onSafeClick { onClick(item) }
    }

}