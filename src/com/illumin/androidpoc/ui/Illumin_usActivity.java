package com.illumin.androidpoc.ui;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.illumin.androidpoc.R;
import com.illumin.androidpoc.R.id;
import com.illumin.androidpoc.R.layout;
import com.illumin.androidpoc.util.CustomMultiPartEntity;
import com.illumin.androidpoc.util.CustomMultiPartEntity.ProgressListener;

public class Illumin_usActivity extends Activity implements OnItemClickListener {
	private static final String TAG = "Illumin_usActivity";
	private static final int REQ_RECORD = 0;
	private final static int REQ_PICK_FILE = 0;
	private ListView mListView;
	private String[] mListItemStrings = new String[] { "Select file to upload", "Record video" };

	public static String POST_URL = "http://test.teamkollab.com/illuminous/server.php";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mListView = (ListView) findViewById(R.id.listView1);
		mListView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListItemStrings));
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch (arg2) {
		case 0:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("video/*");
			startActivityForResult(Intent.createChooser(intent, "Pick an app"), REQ_PICK_FILE);
			break;
		case 1:
			Intent recordeIntent = new Intent(this, VideoRecordingActivity.class);
			startActivityForResult(recordeIntent, REQ_RECORD);
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			//			try {
			//				File file = new File(new URI(data.getDataString()));
			//				Log.d(TAG, "chose uri of file " + file.getAbsolutePath());
			//
			//			} catch (URISyntaxException e) {
			//				e.printStackTrace();
			//			} catch (IllegalArgumentException e) {
			//				e.printStackTrace();
			//			}

			switch (requestCode) {
			case REQ_PICK_FILE:

				Uri uri = data.getData();
				Cursor cursor = getContentResolver().query(uri, new String[] { android.provider.MediaStore.Video.VideoColumns.DATA }, null, null, null);
				if (cursor != null) {
					cursor.moveToFirst();
					String filePath = cursor.getString(0);
					cursor.close();
					new UploadTask(this).execute(filePath);
				} else {
					Toast.makeText(this, "Please choose valid video from gallery " + data.getDataString(), 0).show();
				}
				break;

			default:
				break;
			}
		}
	}

	static class UploadTask extends AsyncTask<String, Integer, Boolean> {
		private WeakReference<Activity> activityReference;
		private ProgressDialog progressDialog;
		private long totalSize;

		public UploadTask(Activity activity) {
			activityReference = new WeakReference<Activity>(activity);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (activityReference.get() != null) {
				progressDialog = new ProgressDialog(activityReference.get());
				progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressDialog.setMessage("Uploading ...");
				progressDialog.setCancelable(false);
				progressDialog.show();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			progressDialog.setProgress((int) (progress[0]));
		}

		@Override
		protected Boolean doInBackground(String... params) {
			String filePath = params[0];
			HttpClient httpClient = new DefaultHttpClient();

			HttpGet getRequest = new HttpGet("http://illuminus-android.appspot.com/upload/");
			try {
				HttpResponse httpResponse = httpClient.execute(getRequest);
				String responseString = EntityUtils.toString(httpResponse.getEntity());
				String postUrlString = responseString.substring(responseString.indexOf("http://illuminus-android"), responseString.indexOf("\" method=\"POST\""));

				HttpPost postRequest = new HttpPost(postUrlString);

				CustomMultiPartEntity multipartEntity = new CustomMultiPartEntity(new ProgressListener() {
					@Override
					public void transferred(long num) {
						publishProgress((int) ((num / (float) totalSize) * 100));
					}
				});
				multipartEntity.addPart(new FormBodyPart("title", new StringBody("mobile")));
				multipartEntity.addPart("file", new FileBody(new File(filePath)));
				totalSize = multipartEntity.getContentLength();
				postRequest.setEntity(multipartEntity);
				httpClient.execute(postRequest);
				return true;
			} catch (ClientProtocolException e) {
				e.printStackTrace();

			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			progressDialog.dismiss();
			if (activityReference.get() != null) {
				if (result)
					Toast.makeText(activityReference.get(), "Upload successful !", 0).show();
				else
					Toast.makeText(activityReference.get(), "Upload Failed", 0).show();
			}
		}
	}
}