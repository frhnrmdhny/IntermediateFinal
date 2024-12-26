package com.dicoding.picodiploma.loginwithanimation.ui.story

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.load.DataSource
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityStoriesDetailBinding
import com.dicoding.picodiploma.loginwithanimation.ui.ViewModelFactory
import kotlinx.coroutines.launch

class StoriesDetail : AppCompatActivity() {

    private lateinit var binding: ActivityStoriesDetailBinding
    private lateinit var storiesViewModel: StoriesDetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoriesDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        storiesViewModel = ViewModelProvider(
            this,
            ViewModelFactory(application)
        )[StoriesDetailViewModel::class.java]

        val storyId = intent.getStringExtra("story_id")
        if (storyId.isNullOrEmpty()) {
            Toast.makeText(this, "Story ID is missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        binding.progressBar.visibility = View.VISIBLE

        lifecycleScope.launch {
            storiesViewModel.getDetailStory(storyId)
        }

        storiesViewModel.story.observe(this) { story ->
            binding.progressBar.visibility = View.GONE

            Glide.with(this)
                .load(story.photoUrl)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean,
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>,
                        dataSource: DataSource,
                        isFirstResource: Boolean,
                    ): Boolean {
                        supportStartPostponedEnterTransition()
                        return false
                    }
                })
                .into(binding.storyImage)

            binding.storyName.text = story.name
            binding.storyDescription.text = story.description
        }


    }


}