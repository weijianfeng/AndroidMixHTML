package com.example.jsbridgedemo;

import com.github.lzyzsd.jsbridge.BridgeHandler;
import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.google.gson.Gson;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity implements OnClickListener {

	private final String TAG = "MainActivity";

	BridgeWebView webView;

	Button mGebButton;
	Button mSetButton;
	EditText mUsername;
	EditText mPassword;

	int RESULT_CODE = 0;

	ValueCallback<Uri> mUploadMessage;

	static class User {
		String name;
		String password;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		webView = (BridgeWebView) findViewById(R.id.webView);

		mGebButton = (Button) findViewById(R.id.bt_get);
		mSetButton = (Button) findViewById(R.id.bt_set);
		mUsername = (EditText) findViewById(R.id.edt_username);
		mPassword = (EditText) findViewById(R.id.edt_password);

		mGebButton.setOnClickListener(this);
		mSetButton.setOnClickListener(this);

		webView.setDefaultHandler(new NativeMethodHandler());

		webView.setWebChromeClient(new WebChromeClient() {

			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String AcceptType, String capture) {
				this.openFileChooser(uploadMsg);
			}

			@SuppressWarnings("unused")
			public void openFileChooser(ValueCallback<Uri> uploadMsg,
					String AcceptType) {
				this.openFileChooser(uploadMsg);
			}

			public void openFileChooser(ValueCallback<Uri> uploadMsg) {
				mUploadMessage = uploadMsg;
				pickFile();
			}
		});

		webView.loadUrl("file:///android_asset/demo.html");

		webView.registerHandler("submitFromWeb", new BridgeHandler() {

			@Override
			public void handler(String data, CallBackFunction function) {
				Log.i(TAG, "handler = submitFromWeb, data from web = " + data);
				User user = new Gson().fromJson(data, User.class);
				mUsername.setText(user.name);
				mPassword.setText(user.password);
				function.onCallBack("get js data!");
			}

		});

		webView.send("hello");

	}

	public void pickFile() {
		Log.i(TAG, "pickFile");
		Intent chooserIntent = new Intent(Intent.ACTION_GET_CONTENT);
		chooserIntent.setType("image/*");
		startActivityForResult(chooserIntent, RESULT_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == RESULT_CODE) {
			if (null == mUploadMessage) {
				return;
			}
			Uri result = intent == null || resultCode != RESULT_OK ? null
					: intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
	}

	@Override
	public void onClick(View v) {
		if (mGebButton.equals(v)) {
			webView.callHandler("getdataInJs", null, new CallBackFunction() {

				@Override
				public void onCallBack(String data) {
					// TODO Auto-generated method stub
					User user = new Gson().fromJson(data, User.class);
					mUsername.setText(user.name);
					mPassword.setText(user.password);

				}

			});
		} else if (mSetButton.equals(v)) {
			User user = new User();
			user.name = mUsername.getText().toString();
			user.password = mPassword.getText().toString();
			webView.callHandler("setdataInJs", new Gson().toJson(user),
					new CallBackFunction() {

						@Override
						public void onCallBack(String data) {
							// TODO Auto-generated method stub
							Log.i(TAG, "reponse data from js " + data);
						}

					});
		}

	}

	class NativeMethodHandler implements BridgeHandler {

		@Override
		public void handler(String data, CallBackFunction function) {
			// TODO Auto-generated method stub
			pickFile();
		}

	}
}
