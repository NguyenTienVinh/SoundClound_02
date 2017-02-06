package com.framgia.soundcloud_2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.framgia.soundcloud_2.R;
import com.framgia.soundcloud_2.data.model.Category;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tri on 06/02/2017.
 */
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> mCategoryList;
    private LayoutInflater mLayoutInflater;
    private ItemClickListener mClickListener;

    public CategoryAdapter(Context context, List<Category> list, ItemClickListener clickListener) {
        mCategoryList = list;
        mLayoutInflater = LayoutInflater.from(context);
        mClickListener = clickListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
            mLayoutInflater.inflate(R.layout.item_songs_category, parent, false);
        return new CategoryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        holder.bindData(mCategoryList.get(position));
    }

    @Override
    public int getItemCount() {
        return mCategoryList != null ? mCategoryList.size() : 0;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onClick(View view, int position);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {
        @BindView(R.id.text_category_title)
        TextView mAudioTitle;
        @BindView(R.id.image_category_icon)
        ImageView mImageView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void bindData(Category category) {
            if (category == null) return;
            mAudioTitle.setText(category.getCategoryTitle());
            mImageView.setImageResource(category.getCategoryImage());
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onClick(view, getAdapterPosition());
        }
    }
}
