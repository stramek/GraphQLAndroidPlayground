package pl.marcin.graphqlresearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer

abstract class BaseViewHolder<in T>(parent: ViewGroup, @LayoutRes layoutResource: Int) :
    RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(layoutResource, parent, false)
    ), LayoutContainer {

    override val containerView: View? = itemView

    abstract fun bind(item: T)
}
