package com.example.xyzreader.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.xyzreader.R;
import com.example.xyzreader.data.ArticleLoader;
//import com.example.xyzreader.data.GlideApp;
import com.example.xyzreader.data.ItemsContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by micha on 10/30/2018.
 */

public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {

    // Tag for logging
    private static final String TAG = GridAdapter.class.getSimpleName();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss");
    // Use default locale format
    private SimpleDateFormat outputFormat = new SimpleDateFormat();
    // Most time functions can only handle 1902 - 2037
    private GregorianCalendar START_OF_EPOCH = new GregorianCalendar(2,1,1);

    // Member variables
    private Cursor mCursor;
    private Context mContext;

    // Interface that receives the ID of the article
    final private ArticleClickHandler mArticleClickHandler;

    // Constructor of the article click handler
    public interface ArticleClickHandler {
        void onArticleClick(long articleId);
    }

    public GridAdapter(Cursor cursor, ArticleClickHandler clickHandler, Context context) {
        mCursor = cursor;
        mArticleClickHandler = clickHandler;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_article, parent, false);
        final ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    private Date parsePublishedDate() {
        try {
            String date = mCursor.getString(ArticleLoader.Query.PUBLISHED_DATE);
            return dateFormat.parse(date);
        } catch (ParseException ex) {
            Log.e(TAG, ex.getMessage());
            Log.i(TAG, "passing today's date");
            return new Date();
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.titleView.setText(mCursor.getString(ArticleLoader.Query.TITLE));
        Date publishedDate = parsePublishedDate();
        if (!publishedDate.before(START_OF_EPOCH.getTime())) {

            holder.subtitleView.setText(Html.fromHtml(
                    DateUtils.getRelativeTimeSpanString(
                            publishedDate.getTime(),
                            System.currentTimeMillis(), DateUtils.HOUR_IN_MILLIS,
                            DateUtils.FORMAT_ABBREV_ALL).toString()
                            + "<br/>" + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)));
        } else {
            holder.subtitleView.setText(Html.fromHtml(
                    outputFormat.format(publishedDate)
                            + "<br/>" + " by "
                            + mCursor.getString(ArticleLoader.Query.AUTHOR)));
        }

        GlideApp.with(mContext)
                .load(mCursor.getString(ArticleLoader.Query.THUMB_URL))
                .articleBackground()
                .into(holder.thumbnailView);

//        holder.thumbnailView.setImageUrl(
//                mCursor.getString(ArticleLoader.Query.THUMB_URL),
//                ImageLoaderHelper.getInstance(mContext).getImageLoader());
//        holder.thumbnailView.setAspectRatio(mCursor.getFloat(ArticleLoader.Query.ASPECT_RATIO));
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView thumbnailView;
        public TextView titleView;
        public TextView subtitleView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail_imageview);
            titleView = (TextView) view.findViewById(R.id.article_title);
            subtitleView = (TextView) view.findViewById(R.id.article_subtitle);

            // Pass the context to the click listener
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            // Create logic to open the article detail intent

            // Get the current position
            int adapterPosition = getAdapterPosition();

            // Move the cursor to position and get the ID
            mCursor.moveToPosition(adapterPosition);
            long itemId = mCursor.getLong(ArticleLoader.Query._ID);

            // Pass it to the click handler interface
            mArticleClickHandler.onArticleClick(itemId);
        }
    }

}
