package pl.marcin.graphqlresearch

import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_pull_request.*

class PullRequestsViewHolder(itemView: ViewGroup)
    : BaseViewHolder<OrganizationRepositoryPullRequestsQuery.Node>(itemView, R.layout.item_pull_request) {

    override fun bind(item: OrganizationRepositoryPullRequestsQuery.Node) {
        authorName.text = item.author?.login
        prValue.text = item.title
    }
}
