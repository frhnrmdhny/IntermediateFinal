package com.dicoding.picodiploma.loginwithanimation.utils

import com.dicoding.picodiploma.loginwithanimation.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyListStoryItems(): List<ListStoryItem> {
        val items = mutableListOf<ListStoryItem>()
        for (i in 1..10) {
            val item = ListStoryItem(
                id = "story-$i",
                name = "Story $i",
                description = "Description $i",
                photoUrl = "https://example.com/story-$i.jpg",
                createdAt = "2024-12-15T00:00:00Z",
                lat = null,
                lon = null
            )
            items.add(item)
        }
        return items
    }
}