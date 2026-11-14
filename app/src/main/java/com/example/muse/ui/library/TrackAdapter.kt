package com.example.muse.ui.library

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.muse.R
import com.example.muse.core.model.Track
import com.example.muse.databinding.ItemTrackBinding

class TrackAdapter(
    private val onTrackClick: (Track, Int) -> Unit
) : ListAdapter<Track, TrackAdapter.TrackViewHolder>(Diff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemTrackBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TrackViewHolder(binding, onTrackClick)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class TrackViewHolder(
        private val binding: ItemTrackBinding,
        private val onTrackClick: (Track, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.trackTitle.text = track.title
            binding.trackArtist.text = track.artist
            binding.trackAlbum.text = track.album

            val model: Any? = track.artworkUri?.takeIf { it.isNotBlank() }
            Glide.with(binding.artwork)
                .load(model)
                .placeholder(R.drawable.placeholder_artwork)
                .error(R.drawable.placeholder_artwork)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .centerCrop()
                .into(binding.artwork)

            // НЕ вызываем File.exists()/length() на главном потоке.
            // Статус источника вычисляется на основе copyStatus и originalAvailable.
            // Комбинация COMPLETED / COMPLETED_UNVERIFIED считается "копия есть",
            // но фактическая проверка файла выполняется в Worker.
            val hasOriginal = track.originalAvailable
            val copyAvailable = track.copyStatus == Track.CopyStatus.COMPLETED ||
                    track.copyStatus == Track.CopyStatus.COMPLETED_UNVERIFIED

            val statusText = when {
                !hasOriginal && copyAvailable -> "Копия MUSE"
                !hasOriginal && !copyAvailable -> "Недоступен"
                hasOriginal && copyAvailable -> "Оригинал + копия"
                else -> null
            }

            if (statusText != null) {
                binding.sourceStatus.text = statusText
                binding.sourceStatus.visibility = View.VISIBLE
                val colorRes = if (!hasOriginal && !copyAvailable) R.color.muse_error
                else R.color.muse_on_dark_muted
                binding.sourceStatus.setTextColor(
                    ContextCompat.getColor(binding.root.context, colorRes)
                )
            } else {
                binding.sourceStatus.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onTrackClick(track, pos)
                }
            }
        }
    }

    private object Diff : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean =
            oldItem == newItem
    }
}