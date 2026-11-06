package com.coolet.app;

import android.content.Context;
import android.net.Uri;
import android.util.AtomicFile;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Private device-local catalog. Original media is never moved or deleted. */
final class MediaLibraryStore {
    static final class Item {
        final Uri uri;
        final String name;
        final String mime;
        Item(Uri uri, String name, String mime) {
            this.uri = uri; this.name = name; this.mime = mime;
        }
    }

    private final AtomicFile file;
    MediaLibraryStore(Context context) {
        file = new AtomicFile(new File(context.getFilesDir(), "media-library.json"));
    }

    List<Item> read() throws Exception {
        List<Item> items = new ArrayList<>();
        byte[] bytes;
        try { bytes = file.readFully(); }
        catch (java.io.FileNotFoundException missing) { return items; }
        JSONArray json = new JSONArray(new String(bytes, StandardCharsets.UTF_8));
        for (int i = 0; i < json.length(); i++) {
            JSONObject row = json.getJSONObject(i);
            Uri uri = Uri.parse(row.getString("uri"));
            String mime = row.getString("mime");
            if (!"content".equals(uri.getScheme()) ||
                    !(mime.startsWith("image/") || mime.startsWith("video/"))) {
                throw new IllegalStateException("Invalid catalog entry");
            }
            items.add(new Item(uri, row.getString("name"), mime));
        }
        return items;
    }

    void write(List<Item> items) throws Exception {
        JSONArray json = new JSONArray();
        for (Item item : items) {
            JSONObject row = new JSONObject();
            row.put("uri", item.uri.toString());
            row.put("name", item.name);
            row.put("mime", item.mime);
            json.put(row);
        }
        FileOutputStream stream = null;
        try {
            stream = file.startWrite();
            stream.write(json.toString().getBytes(StandardCharsets.UTF_8));
            file.finishWrite(stream);
        } catch (Exception error) {
            if (stream != null) file.failWrite(stream);
            throw error;
        }
    }
}
