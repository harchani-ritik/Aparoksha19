package org.aparoksha.app19.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.LinearSnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*
import org.aparoksha.app19.R
import org.aparoksha.app19.utils.AppDB
import org.aparoksha.app19.adapters.BookmarksAdapter
import org.aparoksha.app19.adapters.UpcomingAdapter


class HomeFragment : Fragment() {
    private lateinit var appDB: AppDB
    lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appDB = AppDB.getInstance(context!!)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity!!.title = "My Aparoksha"

        val layoutManager = LinearLayoutManager(context)
        layoutManager.isAutoMeasureEnabled = true

        bookmarksRecyclerView.layoutManager = LinearLayoutManager(context,
            LinearLayoutManager.HORIZONTAL, false)
        bookmarksRecyclerView.isNestedScrollingEnabled = false

        bookmarksRecyclerView.setHasFixedSize(true)
        bookmarksRecyclerView.setItemViewCacheSize(20)
        bookmarksRecyclerView.isDrawingCacheEnabled = true
        bookmarksRecyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(bookmarksRecyclerView)

        upcomingRecyclerView.layoutManager = LinearLayoutManager(context)
        upcomingRecyclerView.isNestedScrollingEnabled = false
        upcomingRecyclerView.setItemViewCacheSize(20)
        upcomingRecyclerView.isDrawingCacheEnabled = true
        upcomingRecyclerView.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH

        appDB = AppDB.getInstance(context!!)
        val upcomingAdapter = UpcomingAdapter(context!!)
        val bookmarksAdapter = BookmarksAdapter(context!!) {
            if (appDB.getBookmarkedEvents().isEmpty()) {
                bookmarks_TV.visibility = View.GONE
                bookmarksRecyclerView.visibility = View.GONE
            }
        }
        upcomingRecyclerView.adapter = upcomingAdapter
        bookmarksRecyclerView.adapter = bookmarksAdapter

        var events = appDB.getAllEvents()
            .filter { ((it.timestamp - 5 * 60 * 60 - 30 * 60) > System.currentTimeMillis() / 1000L)
                .and(it.timestamp != 2L) }
            .sortedBy { it.timestamp }
        if(events.size > 10){
            events = events.subList(0, 10)
        }
        upcomingAdapter.addEvents(events)

        if (appDB.getBookmarkedEvents().isEmpty()) {
            bookmarks_TV.visibility = View.GONE
            bookmarksRecyclerView.visibility = View.GONE
        } else {
            bookmarksRecyclerView.visibility = View.VISIBLE
            appDB.getBookmarkedEvents().let { bookmarksAdapter.addEvents(it.sortedBy { it.timestamp }) }
        }

        auth=FirebaseAuth.getInstance();
        Picasso.with(activity!!.applicationContext)
            .load(auth.currentUser!!.photoUrl)
            .resize(50, 50)
            .centerCrop()
            .into(userIm)
        username.text = auth.currentUser!!.displayName
        userid.text = auth.currentUser!!.email

    }

}
