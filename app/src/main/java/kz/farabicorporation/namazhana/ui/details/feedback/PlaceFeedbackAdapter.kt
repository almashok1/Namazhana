package kz.farabicorporation.namazhana.ui.details.feedback

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import kz.farabicorporation.namazhana.common.binding.BindingBasicRvAdapter
import kz.farabicorporation.namazhana.common.extensions.beginChangeBoundsTransition
import kz.farabicorporation.namazhana.common.extensions.onSafeClick
import kz.farabicorporation.namazhana.common.extensions.toReadableFullFormat
import kz.farabicorporation.namazhana.data.models.Feedback
import kz.farabicorporation.namazhana.data.models.PlaceType
import kz.farabicorporation.namazhana.data.models.getColor
import kz.farabicorporation.namazhana.databinding.ItemFeedbackBinding

class PlaceFeedbackAdapter(
    private val type: PlaceType
) : BindingBasicRvAdapter<Feedback, ItemFeedbackBinding>(
    ItemFeedbackBinding::inflate
) {

    @SuppressLint("SetTextI18n")
    override fun bind(item: Feedback, pos: Int, binding: ItemFeedbackBinding) = with(binding) {
        val color = type.getColor(root.context)
        tvInitial.setTextColor(color)
        tvInitial.text = item.lastName.firstOrNull()?.toString() ?: item.firstName.firstOrNull()?.toString()
        tvDate.text = item.createdAt.toReadableFullFormat()
        tvName.text = item.firstName + " " + item.lastName
        tvText.text = item.message
        tvText.post {
            val isEllipsized = tvText.layout.text?.toString() ?: "" != item.message ?: ""
            tvReadMore.isVisible = isEllipsized
            tvReadMore.onSafeClick {
                tvText.maxLines = Int.MAX_VALUE
                tvText.ellipsize = null
                tvReadMore.isVisible = false
                root.beginChangeBoundsTransition()
            }
        }
        Unit
    }

}