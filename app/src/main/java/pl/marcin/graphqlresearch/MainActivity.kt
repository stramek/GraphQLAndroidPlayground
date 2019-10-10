package pl.marcin.graphqlresearch

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.rx2.Rx2Apollo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.lang.Exception

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {

    lateinit var adapter: PullRequestsAdapter
    private val compositeDisposable = CompositeDisposable()
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureList()
        loadList()
        createRxRequest()
    }

    override fun onRefresh() = loadList()

    private fun loadList() {
        //createRequest()
        //createRxRequest()
        createCoroutinesRequest()
    }

    private fun configureList() {
        swipeRefresh.setOnRefreshListener(this)
        recyclerview.layoutManager = LinearLayoutManager(applicationContext)
        adapter = PullRequestsAdapter()
        recyclerview.adapter = adapter
    }

    private fun createQuery(): OrganizationRepositoryPullRequestsQuery {
        return OrganizationRepositoryPullRequestsQuery.builder()
            .organizationName("facebook")
            .repositoryName("react")
            .pullRequestsCount(30)
            .build()
    }

    private fun createRequest() {
        val apolloQuery = apolloClient.query(createQuery())
        val callback = object : ApolloCall.Callback<OrganizationRepositoryPullRequestsQuery.Data>() {
            override fun onFailure(e: ApolloException) { handleError(e) }

            override fun onResponse(response: Response<OrganizationRepositoryPullRequestsQuery.Data>) {
                handleResponse(response)
            }
        }
        apolloQuery.enqueue(callback)
    }

    private fun createRxRequest() {
        val apolloQuery = apolloClient.query(createQuery())
        compositeDisposable += Rx2Apollo.from(apolloQuery)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeBy(
                onNext = { response -> handleResponse(response) },
                onError = { error -> handleError(error) }
            )
    }

    private fun createCoroutinesRequest() {
        val apolloQuery = apolloClient.query(createQuery())
        job = CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apolloQuery.toDeferred().await()
                withContext(Dispatchers.Main) {
                    handleResponse(response)
                }
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private fun handleResponse(response: Response<OrganizationRepositoryPullRequestsQuery.Data>) {
        val list = response.data()?.organization()?.repository?.pullRequests?.nodes ?: emptyList()
        runOnUiThread { // required by createRequest() method
            adapter.submitList(list)
            Toast.makeText(applicationContext, "Received ${list.size} new items!", Toast.LENGTH_SHORT).show()
            swipeRefresh.isRefreshing = false
        }
    }

    private fun handleError(e: Throwable) {
        Log.e("MainActivity", "REQUEST FAILED", e)
        runOnUiThread { // required by createRequest() method
            adapter.clear()
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
            swipeRefresh.isRefreshing = false
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        job?.cancel()
        super.onDestroy()
    }

    companion object {
        private const val baseUrl = "https://api.github.com/graphql"

        private val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        private val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "bearer ${BuildConfig.GITHUB_API_KEY}")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .build()

        val apolloClient: ApolloClient = ApolloClient.builder()
                .serverUrl(baseUrl)
                .okHttpClient(okHttpClient)
                .build()
    }
}
