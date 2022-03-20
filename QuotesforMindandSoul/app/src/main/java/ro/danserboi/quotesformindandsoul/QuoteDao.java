package ro.danserboi.quotesformindandsoul;

import androidx.room.Dao;
// import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface QuoteDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertQuote(Quote quote);

    // @Insert()
    // void insertQuoteList(List<Quote> quoteList);

    @Query("SELECT * FROM quotes_table WHERE words LIKE :words")
    List<Quote> getQuote(String words);

    @Query("SELECT * FROM quotes_table WHERE category LIKE :category")
    List<Quote> getQuotesByCategory(String category);

    @Query("SELECT * FROM quotes_table WHERE isBookmarked LIKE :isBookmarked")
    List<Quote> getQuotesByBookmark(Boolean isBookmarked);

    @Query("SELECT * FROM quotes_table")
    List<Quote> getAllQuotes();

    @Update
    void updateQuote(Quote quote);

    // @Delete
    // int deleteQuote(Quote quote);
}
