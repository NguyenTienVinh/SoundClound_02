package com.framgia.soundcloud_2.categorysong;

import com.framgia.soundcloud_2.BasePresenter;
import com.framgia.soundcloud_2.BaseView;
import com.framgia.soundcloud_2.data.model.Category;

import java.util.List;

public interface CategoryContract {
    interface View extends BaseView<Presenter> {
        void showCategory(List<Category> list);
    }

    interface Presenter extends BasePresenter {
        void getCategory(String[] categoryName, String[] categoryParam);
    }
}
