package com.rolling_stones.rollingstonesandroid.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rolling_stones.rollingstonesandroid.R;
import com.rolling_stones.rollingstonesandroid.api.ApiHelper;
import com.rolling_stones.rollingstonesandroid.application.MyApplication;
import com.rolling_stones.rollingstonesandroid.models.UserBase;
import com.rolling_stones.rollingstonesandroid.utils.IconUtils;

import retrofit2.Call;
import retrofit2.Response;

/**
 * This activity starts when logged user account is deleted;
 *
 * @author Deputat
 */
public class AccountDeletedActivity extends BaseActivity implements View.OnClickListener {

    private TextView mTextViewFirstAndLastName;
    private Button mButtonRestoreAccount;
    private ImageView mImageViewIcon;

    public static Intent getIntent(@NonNull final Context packageContext,
                                   @Nullable final Bundle bundle) {
        final Intent intent = new Intent(packageContext, AccountDeletedActivity.class);

        if (bundle != null) {
            intent.putExtras(bundle);
        }

        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_account_deleted;
    }

    @Override
    protected void initUI() {
        mTextViewFirstAndLastName = findViewWithId(R.id.tv_first_last_name);
        mButtonRestoreAccount = findViewWithId(R.id.button_restore);
        mImageViewIcon = findViewWithId(R.id.iv_icon);
    }

    @Override
    protected void setUI(Bundle savedInstanceState) {
        setDisplayHomeAsUpEnabled(true);
        mButtonRestoreAccount.setOnClickListener(this);
        setTitle(R.string.title_activity_account_deleted);

        ApiHelper.getUserById(getSharedPrefHelper().getLoggedUserId(), new MyCallback<UserBase>() {
            @Override
            public void onResponse(Call<UserBase> call, Response<UserBase> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mTextViewFirstAndLastName.setText(response.body().getFirstAndLastName());
                    IconUtils.setUserIcon(response.body(), mImageViewIcon);
                }

                super.onResponse(call, response);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_logout:
                MyApplication.getApplication().logout();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        recoverUserAccount();
    }

    /**
     * The method recovers user account and launch @{@link MainActivity};
     */
    private void recoverUserAccount() {
        ApiHelper.recoverUser(getSharedPrefHelper().getLoggedUserId(), new MyCallback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    startActivity(MainActivity.getIntent(AccountDeletedActivity.this, null));
                    finish();
                }

                super.onResponse(call, response);
            }
        });
    }
}
