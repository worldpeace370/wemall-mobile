package com.inuoer.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
/**
 * 
 * Downloads task = new Downloads(this)
 * task.execute(new URL("http://www.baidu.com"));
 * @author heqing
 *
 */
public class Downloads extends AsyncTask<URL, Integer, String> {
	ProgressDialog pdialog;
	int hasRead = 0;
	Context mContext;
	private TextView show;

	public Downloads(Context ctx) {
		mContext = ctx;
	}

	@Override
	protected String doInBackground(URL... params) {
		StringBuilder sb = new StringBuilder();
		try {
			URLConnection conn = params[0].openConnection();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
				hasRead++;
				publishProgress(hasRead);
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		show.setText(result);
		pdialog.dismiss();
	}

	@Override
	protected void onPreExecute() {
		pdialog = new ProgressDialog(mContext);
		pdialog.setTitle("任务正在执行中");
		pdialog.setMessage("任务正在下载中,敬请等待...");
		pdialog.setCancelable(false);
		pdialog.setMax(202);
		pdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pdialog.setIndeterminate(false);
		pdialog.show();
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		show.setText("已经读取了【" + values[0] + "】行!");
		pdialog.setProgress(values[0]);
	}
}
