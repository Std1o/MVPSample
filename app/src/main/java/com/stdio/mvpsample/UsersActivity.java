package com.stdio.mvpsample;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stdio.mvpsample.database.DbHelper;
import com.stdio.mvpsample.databinding.ActivityUsersBinding;
import com.stdio.mvpsample.models.User;
import com.stdio.mvpsample.models.UserData;

import java.util.List;

public class UsersActivity extends AppCompatActivity implements UsersContractView{

    private UserAdapter userAdapter;

    private ActivityUsersBinding binding;
    private ProgressDialog progressDialog;

    private UsersPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }

    private void init() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        userAdapter = new UserAdapter();

        RecyclerView userList = findViewById(R.id.list);
        userList.setLayoutManager(layoutManager);
        userList.setAdapter(userAdapter);


        DbHelper dbHelper = new DbHelper(this);
        UsersModel usersModel = new UsersModel(dbHelper);
        presenter = new UsersPresenter(usersModel);
        presenter.attachView(this);
        presenter.viewIsReady();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add:
                presenter.add();
                break;
            case R.id.clear:
                presenter.clear();
                break;
        }
    }

    @Override
    public UserData getUserData() {
        UserData userData = new UserData();
        System.out.println("ddd"+binding.name.getText());
        userData.setName(binding.name.getText().toString());
        userData.setEmail(binding.email.getText().toString());
        return userData;
    }

    @Override
    public void showUsers(List<User> users) {
        userAdapter.setData(users);
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress() {
        progressDialog = ProgressDialog.show(this, "", getString(R.string.please_wait));
    }

    @Override
    public void hideProgress() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
