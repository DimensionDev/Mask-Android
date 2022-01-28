/*
 *  Mask-Android
 *
 *  Copyright (C) DimensionDev and Contributors
 * 
 *  This file is part of Mask-Android.
 * 
 *  Mask-Android is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  Mask-Android is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with Mask-Android. If not, see <http://www.gnu.org/licenses/>.
 */
package com.dimension.maskbook

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import androidx.viewpager2.widget.ViewPager2
import com.dimension.maskbook.component.ChangeColor
import com.dimension.maskbook.util.setSettings
import com.rd.PageIndicatorView

class IntroActivity : AppCompatActivity() {

    data class SceneData(
        val img: Int,
        val color: Int,
        val title: String,
        val desc: String
    )

    private val scenes by lazy {
        listOf(
            SceneData(
                R.drawable.ic_intro_logo,
                Color.parseColor("#BED2FF"),
                "Welcome to Mask Network",
                "Post on Social Media Sites without being monitored"
            ),
            SceneData(
                R.drawable.ic_intro_chat,
                Color.parseColor("#DBFFFE"),
                "Encrypt for Friends",
                "Share your secret with anyone as your wish"
            ),
            SceneData(
                R.drawable.ic_intro_locker,
                Color.parseColor("#D2E7FF"),
                "Keep Data Carefully",
                "No one but you will own your data Be careful and backup your data regularly"
            )
        )
    }

    private var currentScene = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        findViewById<Button>(R.id.next_button).setOnClickListener {
            toScene(++currentScene)
        }
        findViewById<Button>(R.id.start_using_button).setOnClickListener {
            complete()
        }
        findViewById<PageIndicatorView>(R.id.page_indicator).count = scenes.count()
        findViewById<ViewPager2>(R.id.view_pager).apply {
            adapter = IntroAdapter(scenes)
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    toScene(position)
                }
            })
        }
        toScene(currentScene)
    }

    private fun complete() {
        setSettings("is_intro_shown", true)
        startActivity(Intent(this, GeckoViewActivity::class.java))
        finish()
    }

    private fun toScene(index: Int) {
        if (index >= scenes.size) {
            return
        }
        val data = scenes[index]
        TransitionManager.beginDelayedTransition(
            findViewById(R.id.root),
            TransitionSet()
                .addTransition(
                    ChangeColor()
                        .addTarget(findViewById<View>(R.id.intro_background))
                )
                .addTransition(
                    Fade()
                        .addTarget(findViewById<Button>(R.id.next_button))
                        .addTarget(findViewById<Button>(R.id.start_using_button))
                )
        )
        findViewById<View>(R.id.intro_background).setBackgroundColor(data.color)
//        intro_img.setImageResource(data.img)
//        intro_title.text = data.title
//        intro_desc.text = data.desc
        findViewById<ViewPager2>(R.id.view_pager).apply {
            if (currentItem != index) {
                currentItem = index
            }
        }
        findViewById<PageIndicatorView>(R.id.page_indicator).selection = index
        if (index == scenes.count() - 1) {
            findViewById<View>(R.id.next_button).isVisible = false
            findViewById<View>(R.id.start_using_button).isVisible = true
        } else {
            findViewById<View>(R.id.next_button).isVisible = true
            findViewById<View>(R.id.start_using_button).isVisible = false
        }
    }

    override fun onBackPressed() {
        if (currentScene > 0) {
            toScene(--currentScene)
        } else {
            super.onBackPressed()
        }
    }
}

class IntroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

class IntroAdapter(private val scenes: List<IntroActivity.SceneData>) :
    RecyclerView.Adapter<IntroViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroViewHolder {
        return IntroViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_intro,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return scenes.count()
    }

    override fun onBindViewHolder(holder: IntroViewHolder, position: Int) {
        val data = scenes[position]
        holder.itemView.apply {
            findViewById<ImageView>(R.id.intro_img).setImageResource(data.img)
            findViewById<TextView>(R.id.intro_title).text = data.title
            findViewById<TextView>(R.id.intro_desc).text = data.desc
        }
    }
}
