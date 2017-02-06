package com.framgia.soundcloud_2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.soundcloud_2.R;
import com.framgia.soundcloud_2.data.model.Track;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tri on 02/02/2017.
 */
public class SongsOfflineAdapter
    extends RecyclerView.Adapter<SongsOfflineAdapter.SongsOfflineViewHolder> {
    private List<Track> mListTracks;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private ItemClickListener mClickListener;

    public SongsOfflineAdapter(Context context, List<Track> list, ItemClickListener clickListener) {
        mContext = context;
        mListTracks = list;
        mLayoutInflater = LayoutInflater.from(context);
        mClickListener = clickListener;
    }

    @Override
    public SongsOfflineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
            mLayoutInflater.inflate(R.layout.item_songs_offline, parent, false);
        return new SongsOfflineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SongsOfflineViewHolder holder, int position) {
        holder.bindData(mListTracks.get(position));
    }

    @Override
    public int getItemCount() {
        return mListTracks != null ? mListTracks.size() : 0;
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public class SongsOfflineViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        @BindView(R.id.text_songs_offline_title)
        TextView mAudioTitle;
        @BindView(R.id.image_songs_icon)
        ImageView mImageView;

        public SongsOfflineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindData(Track track) {
            if (track == null || track.getTitle() == null) return;
            mAudioTitle.setText(track.getTitle());
            Picasso.with(mContext).load(track.getArtworkUrl()).into(mImageView);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onClick(view, getAdapterPosition());
        }
    }
}
