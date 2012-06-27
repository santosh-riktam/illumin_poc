package com.illumin.androidpoc;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Illumin_usActivity extends Activity implements OnItemClickListener {
	private static final String TAG = "Illumin_usActivity";
	private final int PICK_FILE = 0;
	private ListView mListView;
	private String[] mListItemStrings = new String[] { "Select file to upload" };

	private String mPostUrlString = "<>";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		mListView = (ListView) findViewById(R.id.listView1);
		mListView.setAdapter(new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, mListItemStrings));
		mListView.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		switch (arg2) {
		case 0:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("video/*");
			startActivityForResult(Intent.createChooser(intent, "Pick an app"),
					PICK_FILE);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK) {

			try {
				File file = new File(new URI(data.getDataString()));
				Log.d(TAG, "chose uri of file " + file.getAbsolutePath());

			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}

			switch (requestCode) {
			case PICK_FILE:

				Uri uri = data.getData();
				Cursor cursor = getContentResolver()
						.query(uri,
								new String[] { android.provider.MediaStore.Video.VideoColumns.DATA },
								null, null, null);
				if (cursor != null) {
					cursor.moveToFirst();
					String filePath = cursor.getString(0);
					cursor.close();
					HttpPost postRequest = new HttpPost(mPostUrlString);
					MultipartEntity multipartEntity = new MultipartEntity();
					multipartEntity.addPart("file", new FileBody(new File(
							filePath)));
					postRequest.setEntity(multipartEntity);
					
					try {
						new DefaultHttpClient().execute(postRequest);
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					Toast.makeText(
							this,
							"Please choose valid video from gallery "
									+ data.getDataString(), 0).show();
				}
				break;

			default:
				break;
			}
		}
	}
}