package com.stdio.mvpsample;

import com.stdio.mvpsample.models.User;
import com.stdio.mvpsample.models.UserData;

import java.util.List;

public interface UsersContractView {
    UserData getUserData();
    void showUsers(List<User> users);
    void showToast(int resId);
    void showProgress();
    void hideProgress();
}