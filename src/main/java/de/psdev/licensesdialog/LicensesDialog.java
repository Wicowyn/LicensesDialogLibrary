/*
 * Copyright 2013 Philip Schiffer
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package de.psdev.licensesdialog;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class LicensesDialog {
    public static final Notice LICENSES_DIALOG_NOTICE = new Notice("LicensesDialog", "http://psdev.de/LicensesDialog", "Copyright 2013 Philip Schiffer",
            new ApacheSoftwareLicense20());

    private final Context mContext;
    private final String mTitleText;
    private String mHeaderText;
    private final String mLicensesText;
    private final String mCloseText;

    //
    private DialogInterface.OnDismissListener mOnDismissListener;

    public LicensesDialog(final Context context, final int rawNoticesResourceId, final boolean showFullLicenseText, boolean includeOwnLicense) {
        mContext = context;
        mHeaderText=null;
        // Load defaults
        final String style = context.getString(R.string.notices_default_style);
        mTitleText = context.getString(R.string.notices_title);
        try {
            final Resources resources = context.getResources();
            if ("raw".equals(resources.getResourceTypeName(rawNoticesResourceId))) {
                final Notices notices = NoticesXmlParser.parse(resources.openRawResource(rawNoticesResourceId));
                if (includeOwnLicense) {
                    final List<Notice> noticeList = notices.getNotices();
                    noticeList.add(LICENSES_DIALOG_NOTICE);
                }
                mLicensesText = NoticesHtmlBuilder.create(mContext).setShowFullLicenseText(showFullLicenseText).setNotices(notices).setStyle(style).build();
            } else {
                throw new IllegalStateException("not a raw resource");
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        mCloseText = context.getString(R.string.notices_close);
    }
    
    public LicensesDialog(final Context context, final int rawNoticesResourceId, final boolean showFullLicenseText, boolean includeOwnLicense, String headerText){
    	this(context, rawNoticesResourceId, showFullLicenseText, includeOwnLicense);
    	
    	mHeaderText=headerText;
    }

    public LicensesDialog(final Context context, final String titleText, final String licensesText, final String closeText) {
        mContext = context;
        mHeaderText=null;
        mTitleText = titleText;
        mLicensesText = licensesText;
        mCloseText = closeText;
    }
    
    public LicensesDialog(final Context context, final String titleText, final String licensesText, final String closeText, String headerText){
    	this(context, titleText, licensesText, closeText);
    	
    	mHeaderText=headerText;
    }

    public LicensesDialog setOnDismissListener(final DialogInterface.OnDismissListener onDismissListener) {
        mOnDismissListener = onDismissListener;
        return this;
    }

    public Dialog create() {
        //Get resources
    	View view=LayoutInflater.from(mContext).inflate(R.layout.dialog, null);
    	
    	TextView header=(TextView) view.findViewById(R.id.dialog_content);
    	if(TextUtils.isEmpty(mHeaderText)){
    		header.setVisibility(View.GONE);
    	}
    	else{
    		header.setVisibility(View.VISIBLE);
    		header.setText(mHeaderText);
    	}
    	
        final WebView webView = (WebView) view.findViewById(R.id.dialog_webview);
        webView.loadDataWithBaseURL(null, mLicensesText, "text/html", "utf-8", null);
        
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext)
                .setTitle(mTitleText)
                .setView(view)
                .setPositiveButton(mCloseText, new Dialog.OnClickListener() {
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        dialogInterface.dismiss();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface dialog) {
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss(dialog);
                }
            }
        });
        return dialog;
    }

    public void show() {
        create().show();
    }

    //


}
