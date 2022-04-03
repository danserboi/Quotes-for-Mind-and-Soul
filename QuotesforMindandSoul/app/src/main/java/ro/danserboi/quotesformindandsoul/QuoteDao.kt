package ro.danserboi.quotesformindandsoul

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.IGNORE
import androidx.room.Query
import androidx.room.Update

@Dao
interface QuoteDao {
    @Insert(onConflict = IGNORE)
    fun insertQuote(quote: Quote)

    @Query("SELECT * FROM quotes_table WHERE words LIKE :words")
    fun getQuote(words: String): List<Quote>

    @Query("SELECT * FROM quotes_table WHERE category LIKE :category")
    fun getQuotesByCategory(category: String): List<Quote>

    @Query("SELECT * FROM quotes_table WHERE isBookmarked LIKE :isBookmarked")
    fun getQuotesByBookmark(isBookmarked: Boolean): List<Quote>

    @get:Query("SELECT * FROM quotes_table")
    val allQuotes: List<Quote>

    @Update
    fun updateQuote(quote: Quote)
}