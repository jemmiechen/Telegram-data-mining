package com.jemmiechen.dvrconverter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int REQUEST_OPEN_LOG = 100;
    private static final int REQUEST_CREATE_LGD = 101;

    private static final byte[] LGD_HEADER = new byte[] {
            0x03, 0x0E, 0x00, (byte) 0xFF, 0x01, 0x7F, (byte) 0xA0, 0x7A,
            0x43, 0x57, (byte) 0xA9, 0x6D, 0x41, (byte) 0xB8, (byte) 0xFD, 0x6A,
            (byte) 0xBF, 0x1B, 0x00, (byte) 0xFF, 0x02, 0x16, 0x6E, 0x6D,
            0x41, 0x16, 0x6E, 0x6D, 0x41, 0x01, 0x33, 0x33,
            0x6F, 0x41, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00
    };

    private Uri inputUri;
    private String inputName = "";
    private TextView fileView;
    private TextView statusView;
    private Button convertButton;
    private ScrollView mainView;
    private LinearLayout progressView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainView = buildMainView();
        progressView = buildProgressView();
        setContentView(mainView);
    }

    private ScrollView buildMainView() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(22), dp(20), dp(22));
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setBackgroundColor(0xfff8fafc);

        TextView title = new TextView(this);
        title.setText("\n\nTelegram Data Mining for ATO Gateway Computer (TIC)");
        title.setTextSize(18);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(0xff0f172a);
        title.setTypeface(null, 1);
        root.addView(title, matchWrap());

        TextView subtitle = new TextView(this);
        subtitle.setText("LOG to LGD Converter");
        subtitle.setTextSize(26);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setTextColor(0xff0f766e);
        subtitle.setTypeface(null, 1);
        root.addView(subtitle, matchWrap());

        fileView = new TextView(this);
        fileView.setText("尚未選擇 .log 檔案");
        fileView.setTextSize(16);
        fileView.setTextColor(0xff334155);
        fileView.setPadding(0, dp(22), 0, dp(12));
        root.addView(fileView, matchWrap());

        Button chooseButton = new Button(this);
        chooseButton.setText("選擇 .log 檔案");
        chooseButton.setOnClickListener(v -> openLogFile());
        root.addView(chooseButton, matchWrap());

        convertButton = new Button(this);
        convertButton.setText("轉檔並儲存 .lgd");
        convertButton.setEnabled(false);
        convertButton.setOnClickListener(v -> createOutputFile());
        root.addView(convertButton, matchWrap());

        statusView = new TextView(this);
        statusView.setText("輸入範例：S1155.log\n輸出建議檔名：1155.lgd");
        statusView.setTextSize(15);
        statusView.setTextColor(0xff475569);
        statusView.setPadding(0, dp(18), 0, 0);
        root.addView(statusView, matchWrap());

        TextView copyright = new TextView(this);
        copyright.setText("This program was written by Jimmy Chen.\nAll rights reserved.\nhttp://khcity.weebly.com");
        copyright.setTextSize(14);
        copyright.setTextColor(0xff64748b);
        copyright.setGravity(Gravity.CENTER);
        copyright.setPadding(0, dp(28), 0, 0);
        root.addView(copyright, matchWrap());

        ScrollView scrollView = new ScrollView(this);
        scrollView.addView(root);
        return scrollView;
    }

    private LinearLayout buildProgressView() {
        LinearLayout view = new LinearLayout(this);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(24), dp(24), dp(24), dp(24));
        view.setBackgroundColor(0xff0f766e);

        TextView title = new TextView(this);
        title.setText("LOG to LGD Converter");
        title.setTextSize(26);
        title.setTypeface(null, 1);
        title.setTextColor(0xffffffff);
        title.setGravity(Gravity.CENTER);
        view.addView(title, matchWrap());

        ProgressBar progress = new ProgressBar(this);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(dp(72), dp(72));
        progressParams.setMargins(0, dp(28), 0, dp(28));
        progressParams.gravity = Gravity.CENTER_HORIZONTAL;
        view.addView(progress, progressParams);

        TextView wait = new TextView(this);
        wait.setText("正在轉檔，請稍後幾秒鐘...");
        wait.setTextSize(22);
        wait.setTypeface(null, 1);
        wait.setTextColor(0xffffffff);
        wait.setGravity(Gravity.CENTER);
        view.addView(wait, matchWrap());

        TextView detail = new TextView(this);
        detail.setText("檔案較大時需要數秒，請不要關閉程式。");
        detail.setTextSize(15);
        detail.setTextColor(0xffccfbf1);
        detail.setGravity(Gravity.CENTER);
        detail.setPadding(0, dp(10), 0, 0);
        view.addView(detail, matchWrap());

        return view;
    }

    private void openLogFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, REQUEST_OPEN_LOG);
    }

    private void createOutputFile() {
        if (inputUri == null) {
            Toast.makeText(this, "請先選擇 .log 檔案", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/octet-stream");
        intent.putExtra(Intent.EXTRA_TITLE, outputNameFor(inputName));
        startActivityForResult(intent, REQUEST_CREATE_LGD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            return;
        }

        if (requestCode == REQUEST_OPEN_LOG) {
            inputUri = data.getData();
            inputName = displayName(inputUri);
            fileView.setText(String.format(Locale.US, "已選擇：%s\n將輸出：%s", inputName, outputNameFor(inputName)));
            statusView.setText("準備轉檔。");
            convertButton.setEnabled(true);
            return;
        }

        if (requestCode == REQUEST_CREATE_LGD) {
            convertTo(data.getData());
        }
    }

    private void convertTo(Uri outputUri) {
        convertButton.setEnabled(false);
        setContentView(progressView);

        new Thread(() -> {
            try (InputStream input = getContentResolver().openInputStream(inputUri);
                 OutputStream output = getContentResolver().openOutputStream(outputUri, "wt")) {
                if (input == null || output == null) {
                    throw new IOException("無法開啟輸入或輸出檔案");
                }

                long records = convert(input, output);
                String message = String.format(Locale.US, "轉檔完成：%s -> %s\n共 %d 筆 record", inputName, outputNameFor(inputName), records);
                runOnUiThread(() -> {
                    setContentView(mainView);
                    statusView.setText(message);
                    convertButton.setEnabled(true);
                    Toast.makeText(this, "轉檔完成", Toast.LENGTH_LONG).show();
                });
            } catch (Exception ex) {
                runOnUiThread(() -> {
                    setContentView(mainView);
                    statusView.setText("轉檔失敗：" + ex.getMessage());
                    convertButton.setEnabled(true);
                    Toast.makeText(this, "轉檔失敗", Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private long convert(InputStream input, OutputStream output) throws IOException {
        byte[] record = new byte[45];
        long records = 0;

        output.write(LGD_HEADER);
        while (true) {
            int read = readFully(input, record);
            if (read == 0) {
                break;
            }
            if (read < record.length) {
                break;
            }

            output.write(record[0]);
            output.write(0x00);
            output.write(record, 1, 41);
            records++;
        }
        output.flush();
        return records;
    }

    private int readFully(InputStream input, byte[] buffer) throws IOException {
        int offset = 0;
        while (offset < buffer.length) {
            int read = input.read(buffer, offset, buffer.length - offset);
            if (read < 0) {
                break;
            }
            offset += read;
        }
        return offset;
    }

    private String displayName(Uri uri) {
        try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (index >= 0) {
                    return cursor.getString(index);
                }
            }
        } catch (Exception ignored) {
            // Fallback below.
        }
        String fallback = uri.getLastPathSegment();
        return fallback == null ? "input.log" : fallback;
    }

    private String outputNameFor(String sourceName) {
        String name = sourceName == null || sourceName.isEmpty() ? "output.log" : sourceName;
        if (name.startsWith("S") || name.startsWith("s")) {
            name = name.substring(1);
        }
        if (name.toLowerCase(Locale.US).endsWith(".log")) {
            name = name.substring(0, name.length() - 4);
        }
        return name + ".lgd";
    }

    private LinearLayout.LayoutParams matchWrap() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dp(8), 0, dp(8));
        return params;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
