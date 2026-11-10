package com.coolet.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final int PICK_MEDIA = 101;
    // Serial application-wide I/O survives Activity recreation and preserves write ordering.
    private static final ExecutorService IO = Executors.newSingleThreadExecutor();
    private final List<MediaLibraryStore.Item> items = new ArrayList<>();
    private final List<MediaLibraryStore.Item> visible = new ArrayList<>();
    private MediaLibraryStore store;
    private Button add;
    private EditText search;
    private TextView status;
    private MediaAdapter adapter;
    private boolean hebrew;
    private boolean busy = true;
    private int mediaFilter = 0; // 0 all, 1 photos, 2 videos

    private String t(String en, String he) { return hebrew ? he : en; }
    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        String language = getResources().getConfiguration().getLocales().get(0).getLanguage();
        hebrew = language.equals("he") || language.equals("iw");
        store = new MediaLibraryStore(getApplicationContext());

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(12), dp(16), 0);
        root.setBackgroundColor(Color.WHITE);
        root.setLayoutDirection(hebrew ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);
        // Keep controls outside system bars when targeting Android 15 edge-to-edge.
        root.setOnApplyWindowInsetsListener((v, insets) -> {
            v.setPadding(dp(16) + insets.getSystemWindowInsetLeft(),
                    dp(12) + insets.getSystemWindowInsetTop(),
                    dp(16) + insets.getSystemWindowInsetRight(),
                    insets.getSystemWindowInsetBottom());
            return insets;
        });
        TextView title = new TextView(this);
        title.setText("Coolet"); title.setTextSize(30); title.setTextColor(Color.rgb(92, 45, 155));
        root.addView(title);
        TextView privacy = new TextView(this);
        privacy.setText(t("Your private photo and video library. Originals stay in place. Keep the original files to keep access.",
                "ספריית התמונות והסרטונים הפרטית שלך. המקור נשאר במקומו. יש לשמור את קובצי המקור כדי להמשיך לצפות בהם."));
        privacy.setTextSize(14); privacy.setTextColor(Color.DKGRAY);
        root.addView(privacy);

        add = new Button(this);
        add.setText(t("Add photos and videos", "הוספת תמונות וסרטונים"));
        add.setEnabled(false);
        add.setOnClickListener(v -> pickMedia());
        root.addView(add, new LinearLayout.LayoutParams(-1, dp(56)));
        search = new EditText(this);
        search.setSingleLine(true);
        search.setHint(t("Search file names", "חיפוש לפי שם קובץ"));
        search.setContentDescription(search.getHint());
        search.setSaveEnabled(false);
        root.addView(search, new LinearLayout.LayoutParams(-1, dp(52)));
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { filter(); }
            public void afterTextChanged(Editable e) {}
        });

        if (state != null) mediaFilter = state.getInt("mediaFilter", 0);
        if (mediaFilter < 0 || mediaFilter > 2) mediaFilter = 0;
        RadioGroup types = new RadioGroup(this);
        types.setOrientation(RadioGroup.HORIZONTAL);
        String[] labels = {t("All", "הכול"), t("Photos", "תמונות"), t("Videos", "סרטונים")};
        for (int i = 0; i < labels.length; i++) {
            RadioButton button = new RadioButton(this);
            button.setId(View.generateViewId());
            button.setText(labels[i]);
            button.setMinHeight(dp(48));
            types.addView(button, new RadioGroup.LayoutParams(0, -2, 1));
            if (i == mediaFilter) types.check(button.getId());
        }
        types.setOnCheckedChangeListener((group, checkedId) -> {
            for (int i = 0; i < group.getChildCount(); i++) {
                if (group.getChildAt(i).getId() == checkedId) mediaFilter = i;
            }
            filter();
        });
        root.addView(types);

        status = new TextView(this);
        status.setText(t("Loading library…", "טוען את הספרייה…"));
        status.setPadding(0, dp(8), 0, dp(8));
        status.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);
        root.addView(status);
        ListView list = new ListView(this);
        adapter = new MediaAdapter();
        list.setAdapter(adapter);
        list.setOnItemClickListener((parent, view, position, id) -> open(visible.get(position)));
        root.addView(list, new LinearLayout.LayoutParams(-1, 0, 1));
        setContentView(root);
        root.requestApplyInsets();
        if (state != null) search.setText(state.getString("query", ""));
        load();
    }

    @Override protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putString("query", search.getText().toString());
        state.putInt("mediaFilter", mediaFilter);
    }

    private void load() {
        IO.execute(() -> {
            try {
                List<MediaLibraryStore.Item> loaded = store.read();
                runOnUiThread(() -> {
                    if (isDestroyed()) return;
                    items.clear(); items.addAll(loaded);
                    busy = false; add.setEnabled(true); filter();
                });
            } catch (Exception error) {
                runOnUiThread(() -> {
                    if (isDestroyed()) return;
                    status.setText(t("Could not read the saved library. Close and reopen Coolet to retry. Your files are unchanged.",
                            "לא ניתן לקרוא את הספרייה השמורה. סגור ופתח שוב את קולט כדי לנסות מחדש. קובצי המקור לא השתנו."));
                });
            }
        });
    }

    private void pickMedia() {
        if (busy) return;
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        try { startActivityForResult(intent, PICK_MEDIA); }
        catch (android.content.ActivityNotFoundException error) {
            message(t("No document picker is available.", "לא נמצא בורר קבצים במכשיר."));
        }
    }

    @Override protected void onActivityResult(int request, int result, Intent data) {
        super.onActivityResult(request, result, data);
        if (request != PICK_MEDIA || result != RESULT_OK || data == null) return;
        Set<Uri> selected = new java.util.LinkedHashSet<>();
        ClipData clips = data.getClipData();
        if (clips != null) {
            for (int i = 0; i < clips.getItemCount(); i++) {
                Uri uri = clips.getItemAt(i).getUri();
                if (uri != null) selected.add(uri);
            }
        } else if (data.getData() != null) selected.add(data.getData());
        if (selected.isEmpty()) return;
        busy = true; add.setEnabled(false);
        status.setText(t("Adding selected media…", "מוסיף את המדיה שנבחרה…"));
        IO.execute(() -> importMedia(selected));
    }

    private void importMedia(Set<Uri> selected) {
        List<Uri> newlyGranted = new ArrayList<>();
        try {
            List<MediaLibraryStore.Item> saved = store.read();
            Set<String> known = new HashSet<>();
            for (MediaLibraryStore.Item item : saved) known.add(item.uri.toString());
            Set<String> existingGrants = new HashSet<>();
            for (android.content.UriPermission grant : getContentResolver().getPersistedUriPermissions()) {
                if (grant.isReadPermission()) existingGrants.add(grant.getUri().toString());
            }
            int added = 0, skipped = 0, failed = 0;
            for (Uri uri : selected) {
                if (known.contains(uri.toString())) { skipped++; continue; }
                try {
                    if (!"content".equals(uri.getScheme())) throw new IllegalArgumentException();
                    String mime = getContentResolver().getType(uri);
                    if (mime == null || !(mime.startsWith("image/") || mime.startsWith("video/"))) {
                        throw new IllegalArgumentException();
                    }
                    String name = t("Untitled media", "מדיה ללא שם");
                    try (Cursor cursor = getContentResolver().query(uri,
                            new String[]{OpenableColumns.DISPLAY_NAME}, null, null, null)) {
                        if (cursor != null && cursor.moveToFirst() && !cursor.isNull(0)) name = cursor.getString(0);
                    }
                    // Refuse a temporary-only grant, rather than promise access after restart.
                    getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    if (!existingGrants.contains(uri.toString())) newlyGranted.add(uri);
                    saved.add(new MediaLibraryStore.Item(uri, name, mime));
                    known.add(uri.toString()); added++;
                } catch (Exception error) { failed++; }
            }
            store.write(saved);
            String summary = t("Added: ", "נוספו: ") + added
                    + t(" · Already in library: ", " · כבר בספרייה: ") + skipped
                    + t(" · Could not add: ", " · לא נוספו: ") + failed;
            runOnUiThread(() -> {
                if (isDestroyed()) return;
                items.clear(); items.addAll(saved); busy = false; add.setEnabled(true);
                filter();
                new AlertDialog.Builder(this).setMessage(summary)
                        .setPositiveButton(t("OK", "אישור"), null).show();
            });
        } catch (Exception error) {
            for (Uri uri : newlyGranted) {
                try { getContentResolver().releasePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION); }
                catch (Exception ignored) {}
            }
            runOnUiThread(() -> {
                if (isDestroyed()) return;
                busy = false; add.setEnabled(true); filter();
                message(t("Could not save the library. Please try again.", "שמירת הספרייה נכשלה. נסה שוב."));
            });
        }
    }

    private void filter() {
        String query = search.getText().toString().trim().toLowerCase(Locale.ROOT);
        visible.clear();
        for (MediaLibraryStore.Item item : items) {
            boolean matchesType = mediaFilter == 0
                    || (mediaFilter == 1 && item.mime.startsWith("image/"))
                    || (mediaFilter == 2 && item.mime.startsWith("video/"));
            if (matchesType && item.name.toLowerCase(Locale.ROOT).contains(query)) visible.add(item);
        }
        if (adapter != null) adapter.notifyDataSetChanged();
        if (status == null || busy) return;
        if (items.isEmpty()) status.setText(t("No media yet. Tap Add photos and videos.",
                "הספרייה ריקה. לחץ על הוספת תמונות וסרטונים."));
        else if (visible.isEmpty()) status.setText(t("No media matches these filters.", "לא נמצאה מדיה שמתאימה לסינון ולחיפוש."));
        else status.setText(t("Shown: ", "מוצגים: ") + visible.size() + t(" of ", " מתוך ") + items.size());
    }

    private void open(MediaLibraryStore.Item item) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(item.uri, item.mime);
            intent.setClipData(ClipData.newRawUri(item.name, item.uri));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, t("Open media with", "פתיחת מדיה באמצעות")));
        } catch (Exception error) {
            message(t("Could not open this media. Check that the original exists and a viewer is installed.",
                    "לא ניתן לפתוח את המדיה. בדוק שהמקור קיים ושמותקנת אפליקציה לצפייה."));
        }
    }

    private void message(String text) { Toast.makeText(this, text, Toast.LENGTH_LONG).show(); }

    private class MediaAdapter extends BaseAdapter {
        public int getCount() { return visible.size(); }
        public Object getItem(int position) { return visible.get(position); }
        public long getItemId(int position) { return position; }
        public View getView(int position, View convert, ViewGroup parent) {
            TextView row = convert instanceof TextView ? (TextView) convert : new TextView(MainActivity.this);
            MediaLibraryStore.Item item = visible.get(position);
            row.setText((item.mime.startsWith("video/") ? t("Video", "סרטון") : t("Photo", "תמונה"))
                    + "\n" + item.name);
            row.setTextSize(17); row.setTextColor(Color.BLACK);
            row.setPadding(dp(12), dp(14), dp(12), dp(14));
            row.setMinHeight(dp(72)); row.setGravity(Gravity.CENTER_VERTICAL);
            return row;
        }
    }
}
