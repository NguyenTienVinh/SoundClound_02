package com.framgia.soundcloud_2.data.model;

/**
 * Created by tri on 02/02/2017.
 */
public class Category {
    private int mCategoryImage;
    private String mCategoryTitle;
    private String mCategoryParam;

    public int getCategoryImage() {
        return mCategoryImage;
    }

    public void setCategoryImage(int categoryImage) {
        mCategoryImage = categoryImage;
    }

    public String getCategoryTitle() {
        return mCategoryTitle;
    }

    public void setCategoryTitle(String categoryTitle) {
        mCategoryTitle = categoryTitle;
    }

    public String getCategoryParam() {
        return mCategoryParam;
    }

    public void setCategoryParam(String categoryParam) {
        mCategoryParam = categoryParam;
    }
}
