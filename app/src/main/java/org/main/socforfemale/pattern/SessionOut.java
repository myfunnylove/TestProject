package org.main.socforfemale.pattern;

import android.content.Context;
import android.content.Intent;

import org.main.socforfemale.base.BaseActivity;
import org.main.socforfemale.resources.utils.Const;
import org.main.socforfemale.resources.utils.Prefs;
import org.main.socforfemale.ui.activity.LoginActivity;

/**
 * Created by Sarvar on 28.08.2017.
 */

public class SessionOut {

    private final BaseActivity context;

    private final int errorCode;

    public BaseActivity getContext() {
        return context;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public static class Builder{

        private final BaseActivity context;
        private int errorCode = -1;

        public Builder(BaseActivity context) {
            this.context = context;
        }

        public BaseActivity getContext() {
            return context;
        }

        int getErrorCode() {
            return errorCode;
        }

        public Builder setErrorCode(int errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public SessionOut build(){

            return new SessionOut(this);
        }
    }

    private SessionOut(Builder builder){
        context = builder.getContext();
        errorCode = builder.getErrorCode();

    }

    public void out(){

        Prefs.INSTANCE.Builder()
                .clearUser();

        context.startActivityForResult(new Intent(context, LoginActivity.class), Const.INSTANCE.getSESSION_OUT());

    }

}
