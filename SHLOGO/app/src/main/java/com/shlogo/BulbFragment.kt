package com.shlogo


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment


class BulbFragment: Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bulb, container, false)
    }
    companion object {
        fun newInstance() =
            BulbFragment().apply {}
    }

    //fun invalidate() {
    //    myView.post(Runnable { myView.invalidate() })
    //}
}