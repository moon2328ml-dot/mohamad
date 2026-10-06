package com.nevo.life;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private FrameLayout root;
    private WebView mainWeb;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.rgb(6,61,64));
        getWindow().setNavigationBarColor(Color.rgb(247,251,250));

        root = new FrameLayout(this);
        mainWeb = new WebView(this);
        mainWeb.setBackgroundColor(Color.rgb(247,251,250));
        mainWeb.setWebViewClient(new WebViewClient());
        WebSettings s = mainWeb.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        mainWeb.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        mainWeb.addJavascriptInterface(new NevoBridge(this), "NEVoAndroid");
        root.addView(mainWeb, new FrameLayout.LayoutParams(-1, -1));
        setContentView(root);
        mainWeb.loadUrl("file:///android_asset/index.html");
    }

    private static String safeFileName(String name, String extension) {
        String cleaned = name == null ? "NEVo-report" : name.replaceAll("[^\\p{L}\\p{N}._-]", "_");
        if (!cleaned.toLowerCase().endsWith(extension)) cleaned += extension;
        return cleaned;
    }

    private void toast(String message) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show());
    }

    private class NevoBridge {
        private final Context context;
        NevoBridge(Context context) { this.context = context; }

        @JavascriptInterface
        public void saveTextFile(String fileName, String content, String mimeType) {
            try {
                byte[] data = (content == null ? "" : content).getBytes(StandardCharsets.UTF_8);
                saveBytes(safeFileName(fileName, ".doc"), data, mimeType == null ? "application/msword" : mimeType);
                toast("گزارش متنی در پوشهٔ دانلود / NEVo ذخیره شد");
            } catch (Exception e) {
                toast("ذخیرهٔ گزارش متنی انجام نشد");
            }
        }

        @JavascriptInterface
        public void savePdf(String title, String html) {
            runOnUiThread(() -> renderPdf(title, html));
        }
    }

    private void renderPdf(String title, String html) {
        final WebView pdfWeb = new WebView(this);
        pdfWeb.setBackgroundColor(Color.WHITE);
        WebSettings settings = pdfWeb.getSettings();
        settings.setJavaScriptEnabled(false);
        settings.setDefaultTextEncodingName("UTF-8");
        pdfWeb.setVisibility(WebView.INVISIBLE);
        root.addView(pdfWeb, new FrameLayout.LayoutParams(2, 2));
        pdfWeb.setWebViewClient(new WebViewClient() {
            private boolean completed = false;
            @Override public void onPageFinished(WebView view, String url) {
                if (completed) return;
                completed = true;
                view.postDelayed(() -> writePdf(pdfWeb, title), 180);
            }
        });
        pdfWeb.loadDataWithBaseURL("file:///android_asset/", html, "text/html", "UTF-8", null);
    }

    private void writePdf(WebView pdfWeb, String title) {
        try {
            File temp = new File(getCacheDir(), "nevo-report-" + System.currentTimeMillis() + ".pdf");
            ParcelFileDescriptor descriptor = ParcelFileDescriptor.open(temp,
                    ParcelFileDescriptor.MODE_CREATE | ParcelFileDescriptor.MODE_READ_WRITE | ParcelFileDescriptor.MODE_TRUNCATE);
            PrintAttributes attributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setResolution(new PrintAttributes.Resolution("nevo", "NEVo", 300, 300))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build();
            PrintDocumentAdapter adapter = pdfWeb.createPrintDocumentAdapter(safeFileName(title, ""));
            adapter.onLayout(null, attributes, attributes, new PrintDocumentAdapter.LayoutResultCallback() {
                @Override public void onLayoutFinished(PrintDocumentInfo info, boolean changed) {
                    adapter.onWrite(new PageRange[]{PageRange.ALL_PAGES}, descriptor, new CancellationSignal(), new PrintDocumentAdapter.WriteResultCallback() {
                        @Override public void onWriteFinished(PageRange[] pages) {
                            closeQuietly(descriptor);
                            copyPdfToDownloads(temp, safeFileName(title, ".pdf"));
                            cleanupPdfWeb(pdfWeb, temp);
                        }
                        @Override public void onWriteFailed(CharSequence error) {
                            closeQuietly(descriptor);
                            cleanupPdfWeb(pdfWeb, temp);
                            toast("ساخت فایل PDF انجام نشد");
                        }
                    });
                }
                @Override public void onLayoutFailed(CharSequence error) {
                    closeQuietly(descriptor);
                    cleanupPdfWeb(pdfWeb, temp);
                    toast("گزارش برای PDF آماده نشد");
                }
            }, null);
        } catch (Exception e) {
            cleanupPdfWeb(pdfWeb, null);
            toast("ساخت فایل PDF انجام نشد");
        }
    }

    private void copyPdfToDownloads(File temp, String fileName) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/NEVo");
                values.put(MediaStore.Downloads.IS_PENDING, 1);
                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri == null) throw new IllegalStateException("download uri unavailable");
                try (OutputStream out = getContentResolver().openOutputStream(uri); FileInputStream in = new FileInputStream(temp)) {
                    byte[] buffer = new byte[8192]; int count;
                    while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
                }
                ContentValues done = new ContentValues();
                done.put(MediaStore.Downloads.IS_PENDING, 0);
                getContentResolver().update(uri, done, null, null);
            } else {
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "NEVo");
                if (!dir.exists() && !dir.mkdirs()) throw new IllegalStateException("download directory unavailable");
                File target = new File(dir, fileName);
                try (FileInputStream in = new FileInputStream(temp); FileOutputStream out = new FileOutputStream(target)) {
                    byte[] buffer = new byte[8192]; int count;
                    while ((count = in.read(buffer)) != -1) out.write(buffer, 0, count);
                }
            }
            toast("فایل PDF در پوشهٔ دانلود / NEVo ذخیره شد");
        } catch (Exception e) {
            toast("ذخیرهٔ فایل PDF انجام نشد");
        }
    }

    private void saveBytes(String fileName, byte[] data, String mimeType) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/NEVo");
            values.put(MediaStore.Downloads.IS_PENDING, 1);
            Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            if (uri == null) throw new IllegalStateException("download uri unavailable");
            try (OutputStream out = getContentResolver().openOutputStream(uri)) { out.write(data); }
            ContentValues done = new ContentValues();
            done.put(MediaStore.Downloads.IS_PENDING, 0);
            getContentResolver().update(uri, done, null, null);
        } else {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "NEVo");
            if (!dir.exists()) dir.mkdirs();
            try (FileOutputStream out = new FileOutputStream(new File(dir, fileName))) { out.write(data); }
        }
    }

    private void cleanupPdfWeb(WebView pdfWeb, File temp) {
        runOnUiThread(() -> {
            root.removeView(pdfWeb);
            pdfWeb.destroy();
            if (temp != null) temp.delete();
        });
    }

    private void closeQuietly(ParcelFileDescriptor descriptor) {
        try { descriptor.close(); } catch (Exception ignored) { }
    }

    @SuppressWarnings("deprecation")
    @Override public void onBackPressed() {
        if (mainWeb != null) {
            mainWeb.evaluateJavascript("(typeof window.nevoBack === 'function' ? window.nevoBack() : false)", value -> {
                if (!"true".equals(value)) MainActivity.super.onBackPressed();
            });
            return;
        }
        super.onBackPressed();
    }
}
