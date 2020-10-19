package com.chentian.xiangkan.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RSSItemDao {
    @Query("SELECT * FROM RSSItem")
    fun getAll(): MutableList<RSSItem>

    @Query("SELECT * FROM RSSItem ORDER BY pubDate DESC")
    fun getAllOrderByPubDate(): MutableList<RSSItem>

    @Insert
    fun insertAll(rssItems: MutableList<RSSItem>)

    @Insert
    fun insertItem(vararg rssItem: RSSItem)

    @Update
    fun updateItems(vararg rssItem: RSSItem)
}