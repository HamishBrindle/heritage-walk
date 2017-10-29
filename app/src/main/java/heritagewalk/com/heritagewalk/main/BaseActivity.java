package heritagewalk.com.heritagewalk.main;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import heritagewalk.com.heritagewalk.R;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void buildDialog(String header, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(header);
        builder.setMessage(content);
        builder.setNegativeButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }


}