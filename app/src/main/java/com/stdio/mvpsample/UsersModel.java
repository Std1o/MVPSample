package com.stdio.mvpsample;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.AsyncTask;

import com.stdio.mvpsample.database.DbHelper;
import com.stdio.mvpsample.database.UserTable;
import com.stdio.mvpsample.models.User;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UsersModel {

    private final DbHelper dbHelper;

    public UsersModel(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void loadUsers(LoadUserCallback callback) {
        Observable.fromCallable(() -> {
            List<User> users = new LinkedList<>();
            Cursor cursor = dbHelper.getReadableDatabase().query(UserTable.TABLE, null, null, null, null, null, null);
            while (cursor.moveToNext()) {
                User user = new User();
                user.setId(cursor.getLong(cursor.getColumnIndex(UserTable.COLUMN.ID)));
                user.setName(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN.NAME)));
                user.setEmail(cursor.getString(cursor.getColumnIndex(UserTable.COLUMN.EMAIL)));
                users.add(user);
            }
            cursor.close();
            return users;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if (callback != null) {
                        callback.onLoad(result);
                    }
                });
    }

    public void addUser(ContentValues contentValues, CompleteCallback callback) {
        Observable.fromCallable(() -> {
            dbHelper.getWritableDatabase().insert(UserTable.TABLE, null, contentValues);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return dbHelper;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if (callback != null) {
                        callback.onComplete();
                    }
                });
    }

    public void clearUsers(CompleteCallback completeCallback) {
        Observable.fromCallable(() -> {
            dbHelper.getWritableDatabase().delete(UserTable.TABLE, null, null);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                    if (completeCallback != null) {
                        completeCallback.onComplete();
                    }
                });
    }


    interface LoadUserCallback {
        void onLoad(List<User> users);
    }

    interface CompleteCallback {
        void onComplete();
    }
}
