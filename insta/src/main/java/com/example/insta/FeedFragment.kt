package com.example.insta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class FeedFragment : Fragment() {
    // override 한 후에
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        원하는 fragment return
        return inflater.inflate(R.layout.feed_fragment, container,false)
    }
}