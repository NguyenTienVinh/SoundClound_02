package com.framgia.soundcloud_2.categorysong;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.framgia.soundcloud_2.R;
import com.framgia.soundcloud_2.adapter.CategoryAdapter;
import com.framgia.soundcloud_2.data.model.Category;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CategorySongsFragment extends Fragment
    implements CategoryContract.View, CategoryAdapter.ItemClickListener {
    @BindView(R.id.recycle_category)
    RecyclerView mRecyclerViewCategory;
    private CategoryContract.Presenter mCategoryPresenter;
    private CategoryAdapter mCategoryAdapter;
    private List<Category> mCategories = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_songs, container, false);
        setPresenter(new CategoryPresenter(this));
        ButterKnife.bind(this, view);
        mCategoryPresenter.start();
        return view;
    }

    @Override
    public void setPresenter(CategoryContract.Presenter presenter) {
        mCategoryPresenter = presenter;
    }

    @Override
    public void start() {
        String[] categoriesName, categoryParams;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewCategory.setLayoutManager(linearLayoutManager);
        mCategoryAdapter = new CategoryAdapter(getActivity(), mCategories, this);
        mRecyclerViewCategory.setAdapter(mCategoryAdapter);
        categoriesName = getResources().getStringArray(R.array.category_name);
        categoryParams = getResources().getStringArray(R.array.category_param);
        mCategoryPresenter.getCategory(categoriesName, categoryParams);
    }

    @Override
    public void onClick(View view, int position) {
        // TODO: click item
    }

    @Override
    public void showCategory(List<Category> list) {
        if (list == null) return;
        mCategories.addAll(list);
        mCategoryAdapter.notifyDataSetChanged();
    }
}
