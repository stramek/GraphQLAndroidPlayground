package pl.marcin.graphqlresearch

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

class NodeItemCallback : DiffUtil.ItemCallback<OrganizationRepositoryPullRequestsQuery.Node>() {

    override fun areItemsTheSame(
        oldItem: OrganizationRepositoryPullRequestsQuery.Node,
        newItem: OrganizationRepositoryPullRequestsQuery.Node
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: OrganizationRepositoryPullRequestsQuery.Node,
        newItem: OrganizationRepositoryPullRequestsQuery.Node
    ): Boolean {
        return oldItem == newItem
    }
}
