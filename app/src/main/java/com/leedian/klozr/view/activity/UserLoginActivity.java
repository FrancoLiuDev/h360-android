package com.leedian.klozr.view.activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hannesdorfmann.mosby.mvp.viewstate.ViewState;
import com.leedian.klozr.AppManager;
import com.leedian.klozr.AppResource;
import com.leedian.klozr.R;
import com.leedian.klozr.base.baseView.BaseActivity;
import com.leedian.klozr.presenter.presenterInterface.UserLoginPresenter;
import com.leedian.klozr.utils.Validator;
import com.leedian.klozr.utils.viewUtils.ActionBarTop;
import com.leedian.klozr.view.navigator.AppNavigator;
import com.leedian.klozr.view.viewInterface.ViewUserLoginMvp;
import com.leedian.klozr.view.viewStates.ViewUserLoginState;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.leedian.klozr.utils.Validator.ipFilter;

/**
 * UserLoginActivity
 *
 * @author Franco
 */
public class UserLoginActivity
        extends BaseActivity<ViewUserLoginMvp, UserLoginPresenter>
        implements ViewUserLoginMvp,
                   ActionBarTop.ActionButtonEvent
{
    /**
     * Top navigation bar
     */
    protected ActionBarTop actionBar;

    /**
     * IP Address Edit Text box
     */
    @Bind(R.id.editText_address) EditText addressEditText;

    /**
     * Name Edit Text box
     */
    @Bind(R.id.editText_name) EditText nameEditText;

    /**
     * Password Edit Text box
     */
    @Bind(R.id.editText_password) EditText passwordEditText;

    /**
     * ImageView login button
     */
    @Bind(R.id.submit_button) ImageView submit_button;

    /**
     * layout container for logo image
     */
    @Bind(R.id.logo_layout) RelativeLayout logoImageLayout;

    /**
     * layout container for logo image
     */
    @Bind(R.id.login_input_layout) RelativeLayout login_input_layout;

    /**
     * Address edit box error image
     */
    @Bind(R.id.img_address_error) ImageView addressErrorImage;

    /**
     * Name edit box error image
     */
    @Bind(R.id.img_name_error) ImageView nameErrorImage;

    /**
     * Password edit box error image
     */
    @Bind(R.id.img_password_error) ImageView passwordErrorImage;

    View.OnFocusChangeListener textFocusListener = new View.OnFocusChangeListener() {
        @Override public void onFocusChange(View v, boolean hasFocus) {

            if (hasFocus && v.equals(addressEditText)) {
                addressErrorImage.setVisibility(View.INVISIBLE);
            }
            if (hasFocus && v.equals(nameEditText)) {

                nameErrorImage.setVisibility(View.INVISIBLE);
            }
            if (hasFocus && v.equals(passwordEditText)) {

                passwordErrorImage.setVisibility(View.INVISIBLE);
            }
        }
    };

    private AppNavigator viewNavigator = new AppNavigator(this);

    @Override protected int getContentResourceId() {

        return R.layout.activity_user_login;
    }

    @Override protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        configKeyboard();
        initView();
    }

    /**
     * handle when software keyboard is shown on the screen
     **/
    @Override protected void onKeyBoardShown() {

        runOnUiThread(new Runnable() {
            public void run() {

                logoImageLayout.setVisibility(View.GONE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) login_input_layout
                        .getLayoutParams();

                params.setMargins(params.leftMargin, 120, params.rightMargin, params.bottomMargin);
                login_input_layout.setLayoutParams(params);
            }
        });
    }

    /**
     * handle when software keyboard is hide
     **/
    @Override protected void onKeyBoardHide() {

        runOnUiThread(new Runnable() {
            public void run() {

                logoImageLayout.setVisibility(View.VISIBLE);
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) login_input_layout
                        .getLayoutParams();

                params.setMargins(params.leftMargin, 0, params.rightMargin, params.bottomMargin);
                login_input_layout.setLayoutParams(params);
            }
        });
    }

    /**
     * activity  onResume
     **/
    @Override protected void onResume() {

        super.onResume();
        //registerKeyboardCallBack();
        nameEditText.clearFocus();
        passwordEditText.clearFocus();
    }

    /**
     * Config Software Keyboard event listener for activity
     **/
    private void configKeyboard() {

        initKeyboardConfig(R.id.activity_klozr_login_view);
    }

    /**
     * Activity  initView
     **/
    @Override public void initView() {

        actionBar = new ActionBarTop.Builder(this, R.layout.actionbar_menu_bar)
                .setEventListener(this).setDebugMode(false)
                .setBackgroundColor(Color.parseColor("#00000000")).build();

        actionBar.setButtonImage(R.id.btn_left, R.drawable.icon_back);
        addressEditText.setText(AppManager.getPrefAddress());
        addressEditText.setFilters(ipFilter());
        addressEditText.setOnFocusChangeListener(textFocusListener);
        nameEditText.setOnFocusChangeListener(textFocusListener);
        passwordEditText.setOnFocusChangeListener(textFocusListener);
    }

    @NonNull @Override public UserLoginPresenter createPresenter() {

        return new com.leedian.klozr.presenter.presenterImp.UserLoginPresenter();
    }

    /**
     * event when login button clicked
     **/
    @OnClick(R.id.submit_button) public void onLoginClicked() {

        String str_address = addressEditText.getText().toString();
        if (!Validator.isMatchIpAddress(str_address)) {
            displayErrorResponse(AppResource.getString(R.string.invalid_input_ip_addr));
            showFailedError("address");
        }
        AppManager.setPrefAddress(str_address);
        presenter.doUserLogin(nameEditText.getText().toString(), passwordEditText.getText()
                                                                                 .toString());
    }

    /**
     * event when top action bar button clicked
     *
     * @param view sender object
     **/
    @Override public void onActionButtonClicked(View view) {

        if (view.getId() == R.id.btn_left) {
            this.finish();
        }
    }

    /**
     * create view state for mvp view
     **/
    @NonNull @Override public ViewState<ViewUserLoginMvp> createViewState() {

        return new ViewUserLoginState();
    }

    /**
     * create view new state for mvp view
     **/
    @Override public void onNewViewStateInstance() {

    }

    /**
     * navigate to content list view from login view
     **/
    @Override public void showLoginForm() {

    }

    @Override public void showFailedError(String type) {

        if (type.equals("address")) {
            addressErrorImage.setVisibility(View.VISIBLE);
        }
        if (type.equals("name")) {
            nameEditText.setText("");
            nameErrorImage.setVisibility(View.VISIBLE);
        }
        if (type.equals("password")) {
            passwordEditText.setText("");
            passwordErrorImage.setVisibility(View.VISIBLE);
        }
    }

    @Override public void navigateToListView() {

        viewNavigator.navigateOViewList();
    }
}
