package com.framgia.soundcloud_2.categorysong;

import android.support.annotation.NonNull;

import com.framgia.soundcloud_2.data.model.Category;

import java.util.ArrayList;
import java.util.List;

import static com.framgia.soundcloud_2.utils.Constant.ResourceImage.IMAGE_CATEGORY;

public class CategoryPresenter implements CategoryContract.Presenter {
    private CategoryContract.View mCategoryView;

    public CategoryPresenter(@NonNull CategoryContract.View categoryView) {
        mCategoryView = categoryView;
        mCategoryView.setPresenter(this);
    }

    @Override
    public void start() {
        mCategoryView.start();
    }

    @Override
    public void getCategory(String[] categoryNames, String[] categoryParams) {
        if (categoryNames == null || categoryParams == null) return;
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < categoryNames.length; i++) {
            Category category = new Category();
            category.setCategoryTitle(categoryNames[i]);
            category.setCategoryParam(categoryParams[i]);
            category.setCategoryImage(IMAGE_CATEGORY[i]);
            categories.add(category);
        }
        mCategoryView.showCategory(categories);
    }
}
