package com.rolling_stones.rollingstonesandroid.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.rolling_stones.rollingstonesandroid.R;


public class SettingsItem extends LinearLayout {

    private static final int DEFAULT_TITLE_COLOR = Color.parseColor("#000000");
    private static final int DEFAULT_SUBTITLE_COLOR = Color.parseColor("#7e7c7c");

    private TextView mTextViewSubtitle;
    private TextView mTextViewTitle;
    private Switch mSwitch;

    public SettingsItem(Context context) {
        super(context);
        inflate(context, null);
    }

    public SettingsItem(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, attrs);
    }

    public SettingsItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, attrs);
    }

    private void inflate(Context context, @Nullable AttributeSet attrs) {
        inflate(context, R.layout.settings_item, this);

        init();
        set(context, attrs);
    }

    private void init() {
        mTextViewTitle = (TextView) findViewById(R.id.title);
        mTextViewSubtitle = (TextView) findViewById(R.id.subtitle);
        mSwitch = (Switch) findViewById(R.id.switch_item);
    }

    private void set(Context context, @Nullable AttributeSet attrs) {
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SettingsItem, 0, 0);

        final String titleText = a.getString(R.styleable.SettingsItem_titleText);
        int titleColor = a.getColor(R.styleable.SettingsItem_titleColor, DEFAULT_TITLE_COLOR);
        float titleSize = a.getDimension(R.styleable.SettingsItem_titleSize,
                getResources().getDimension(R.dimen.text_size_16));

        int subtitleVisibility = a.getInt(R.styleable.SettingsItem_subtitleVisibility, 2);
        final String subtitleText = a.getString(R.styleable.SettingsItem_subtitleText);
        int subtitleColor = a.getColor(R.styleable.SettingsItem_subtitleColor, DEFAULT_SUBTITLE_COLOR);
        float subtitleSize = a.getDimension(R.styleable.SettingsItem_subtitleSize,
                getResources().getDimension(R.dimen.text_size_14));

        int switchVisibility = a.getInt(R.styleable.SettingsItem_switchVisibility, 2);

        a.recycle();

        mTextViewTitle.setText(titleText);
        mTextViewTitle.setTextColor(titleColor);
        mTextViewTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);

        mTextViewSubtitle.setVisibility(subtitleVisibility == 0 ? VISIBLE :
                (subtitleVisibility == 1 ? INVISIBLE : GONE));
        mTextViewSubtitle.setText(subtitleText);
        mTextViewSubtitle.setTextColor(subtitleColor);
        mTextViewSubtitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, subtitleSize);

        mSwitch.setVisibility(switchVisibility == 0 ? VISIBLE :
                (switchVisibility == 1 ? INVISIBLE : GONE));
    }

    public void setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        mSwitch.setOnCheckedChangeListener(listener);
    }

    @SuppressWarnings("unused")
    public void setSwitchVisibility(int visibility) {
        mSwitch.setVisibility(visibility);
    }

    public void setSwitchChecked(boolean isChecked) {
        mSwitch.setChecked(isChecked);
    }

    @SuppressWarnings("unused")
    public void setSubtitleVisibility(int visibility) {
        mTextViewSubtitle.setVisibility(visibility);
    }

    @SuppressWarnings("unused")
    public void setSubtitleText(@StringRes int text) {
        mTextViewSubtitle.setText(text);
    }

    public void setSubtitleText(String text) {
        mTextViewSubtitle.setText(text);
    }

    @SuppressWarnings("unused")
    public void setTitleText(@StringRes int text) {
        mTextViewTitle.setText(text);
    }

    @SuppressWarnings("unused")
    public void setTitleText(String text) {
        mTextViewTitle.setText(text);
    }

    public boolean isSwitchChecked() {
        return mSwitch.isChecked();
    }
}
