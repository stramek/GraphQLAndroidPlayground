package pl.marcin.graphqlresearch

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class PullRequestsAdapter
    : ListAdapter<OrganizationRepositoryPullRequestsQuery.Node, PullRequestsViewHolder>(NodeItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            = PullRequestsViewHolder(parent)

    override fun onBindViewHolder(holder: PullRequestsViewHolder, position: Int)
            = holder.bind(getItem(position))

    fun clear() = submitList(emptyList())
}
