package org.androidanalyzer.gui;

import org.androidanalyzer.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String title = getResources().getString(R.string.about_title);
		setContentView(R.layout.about_layout);
		setTitle(title);
		TextView mtelUrl = (TextView)findViewById(R.id.aa_url);
		String mtelLink = "<a href="+getString(R.string.aa_url_link)+">"+getString(R.string.aa_url_text)+"</a>";
		mtelUrl.setText(Html.fromHtml(mtelLink), TextView.BufferType.SPANNABLE);
		mtelUrl.setMovementMethod(LinkMovementMethod.getInstance());
		TextView bzUrl = (TextView)findViewById(R.id.af_url);
		String sumcLink = "<a href="+getString(R.string.af_url_link)+">"+getString(R.string.af_url_text)+"</a>";
		bzUrl.setText(Html.fromHtml(sumcLink), TextView.BufferType.SPANNABLE);
		bzUrl.setMovementMethod(LinkMovementMethod.getInstance());
		TextView contactName = (TextView)findViewById(R.id.about_vendor_name);
		String authorMail = "<a href="+getString(R.string.about_vendor_mail)+">"+getString(R.string.about_vendor_name)+"</a>";
		contactName.setText(Html.fromHtml(authorMail), TextView.BufferType.SPANNABLE);
		contactName.setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	

}
