import hashlib
import os
import json
from pathlib import Path
import re
import subprocess
import time
import xml.etree.ElementTree as ET

OUT = Path("runtime-evidence")
OUT.mkdir(exist_ok=True)
APK = Path(os.environ.get("COOLET_TEST_APK", "dist/Coolet-Preview-0.2.3.apk"))
SOURCE_BUILD = "COOLET_TEST_APK" in os.environ
PACKAGE = "com.coolet.app.preview"
ACTIVITY = PACKAGE + "/com.coolet.app.MainActivity"
report = []

def adb(*args):
    result = subprocess.run(["adb", *args], capture_output=True, check=True)
    return result.stdout.decode("utf-8", errors="replace")

def capture(name):
    adb("shell", "uiautomator", "dump", "/sdcard/coolet-window.xml")
    xml = adb("shell", "cat", "/sdcard/coolet-window.xml")
    (OUT / (name + ".xml")).write_text(xml)
    image = subprocess.check_output(["adb", "exec-out", "screencap", "-p"])
    (OUT / (name + ".png")).write_bytes(image)
    return ET.fromstring(xml)

def find_text(root, text):
    exact = next((n for n in root.iter("node") if text.casefold() == n.attrib.get("text", "").casefold()), None)
    if exact is not None:
        return exact
    return next((n for n in root.iter("node") if text.casefold() in n.attrib.get("text", "").casefold()), None)

def click_text(text):
    root = capture("before-tap")
    node = find_text(root, text)
    assert node is not None, "Missing UI text: " + text
    x1, y1, x2, y2 = map(int, re.findall(r"\d+", node.attrib["bounds"]))
    adb("shell", "input", "tap", str((x1+x2)//2), str((y1+y2)//2))
    time.sleep(0.8)

def launch():
    result = adb("shell", "am", "start", "-W", "-n", ACTIVITY)
    assert "Status: ok" in result, result
    time.sleep(1)
    return capture("launched")


def tap_node(node):
    assert node is not None, "Missing target node"
    x1, y1, x2, y2 = map(int, re.findall(r"\d+", node.attrib["bounds"]))
    adb("shell", "input", "tap", str((x1+x2)//2), str((y1+y2)//2))
    time.sleep(1)

def import_file(name):
    click_text("Add photos and videos")
    root = capture("import-picker")
    if find_text(root, name) is None:
        menu = next((n for n in root.iter("node") if n.attrib.get("content-desc") == "Show roots"), None)
        if menu is not None:
            tap_node(menu)
            click_text("Downloads")
        root = capture("downloads")
    click_text(name)
    for attempt in range(6):
        root = capture("import-result")
        if find_text(root, "Added:") is not None:
            break
        time.sleep(1)
    summary = find_text(root, "Added:")
    assert summary is not None, "Import summary missing"
    result = summary.attrib["text"]
    click_text("OK")
    return result

def edit_search(value, previous=""):
    root = capture("before-search")
    field = next(n for n in root.iter("node") if n.attrib.get("content-desc") == "Search names and descriptions")
    tap_node(field)
    if previous:
        adb("shell", "input", "keyevent", "123")
        for _ in previous:
            adb("shell", "input", "keyevent", "112")
    if value:
        adb("shell", "input", "text", value)
    adb("shell", "input", "keyevent", "4")
    time.sleep(1)

try:
    digest = hashlib.sha256(APK.read_bytes()).hexdigest()
    if not SOURCE_BUILD:
        assert digest == "df7b7c2f83e0c7b4ef778eed20455905b4ff84cf6ea13a0bd898cfe168916b46"
    report.append(("Current source test APK SHA256: " if SOURCE_BUILD else "Distributed APK SHA256: ") + digest)
    result = adb("install", str(APK))
    assert "Success" in result, result
    report.append("Signed APK installed on Android 15 emulator")
    root = launch()
    assert find_text(root, "Coolet Preview") is not None
    assert find_text(root, "0.2.3") is not None
    assert find_text(root, "No media yet") is not None
    report.append("App launched; visible version 0.2.3; empty library displayed")
    for text in ("Photos", "Videos", "All"):
        click_text(text)
        root = capture("filter-" + text.lower())
        node = find_text(root, text)
        assert node is not None and node.attrib.get("checked") == "true"
    report.append("All / Photos / Videos controls respond and show checked state")
    click_text("Add photos and videos")
    root = capture("picker")
    assert any("documentsui" in n.attrib.get("package", "") for n in root.iter("node")), "Document picker did not appear"
    adb("shell", "input", "keyevent", "4")
    time.sleep(1)
    root = capture("picker-cancelled")
    assert find_text(root, "Coolet Preview") is not None
    report.append("Document picker opened; cancellation returned to app")

    import base64
    photo = Path("/tmp/coolet-smoke.png")
    video = Path("/tmp/coolet-smoke.mp4")
    photo.write_bytes(base64.b64decode("iVBORw0KGgoAAAANSUhEUgAAAEAAAABACAIAAAAlC+aJAAAAZUlEQVR4nO3PQQ3AIADAQEANIvCvAjETweOypKegnXef8WdLB7xqQGtAa0BrQGtAa0BrQGtAa0BrQGtAa0BrQGtAa0BrQGtAa0BrQGtAa0BrQGtAa0BrQGtAa0BrQGtAa0BrQPsAkpgBwOL2HUcAAAAASUVORK5CYII="))
    video.write_bytes(base64.b64decode("AAAAIGZ0eXBpc29tAAACAGlzb21pc28yYXZjMW1wNDEAAAN1bW9vdgAAAGxtdmhkAAAAAAAAAAAAAAAAAAAD6AAAA+gAAQAAAQAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAAAqB0cmFrAAAAXHRraGQAAAADAAAAAAAAAAAAAAABAAAAAAAAA+gAAAAAAAAAAAAAAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAABAAAAAAAAAAAAAAAAAABAAAAAAEAAAABAAAAAAAAkZWR0cwAAABxlbHN0AAAAAAAAAAEAAAPoAAAQAAABAAAAAAIYbWRpYQAAACBtZGhkAAAAAAAAAAAAAAAAAAAoAAAAKABVxAAAAAAALWhkbHIAAAAAAAAAAHZpZGUAAAAAAAAAAAAAAABWaWRlb0hhbmRsZXIAAAABw21pbmYAAAAUdm1oZAAAAAEAAAAAAAAAAAAAACRkaW5mAAAAHGRyZWYAAAAAAAAAAQAAAAx1cmwgAAAAAQAAAYNzdGJsAAAAv3N0c2QAAAAAAAAAAQAAAK9hdmMxAAAAAAAAAAEAAAAAAAAAAAAAAAAAAAAAAEAAQABIAAAASAAAAAAAAAABFUxhdmM2MS4xOS4xMDEgbGlieDI2NAAAAAAAAAAAAAAAGP//AAAANWF2Y0MBZAAK/+EAGGdkAAqs2UQmwEQAAAMABAAAAwAoPEiWWAEABmjr48siwP34+AAAAAAQcGFzcAAAAAEAAAABAAAAFGJ0cnQAAAAAAAAYeAAAAAAAAAAYc3R0cwAAAAAAAAABAAAABQAACAAAAAAUc3RzcwAAAAAAAAABAAAAAQAAADhjdHRzAAAAAAAAAAUAAAABAAAQAAAAAAEAACgAAAAAAQAAEAAAAAABAAAAAAAAAAEAAAgAAAAAHHN0c2MAAAAAAAAAAQAAAAEAAAAFAAAAAQAAAChzdHN6AAAAAAAAAAAAAAAFAAAC3QAAAA4AAAAMAAAADAAAAAwAAAAUc3RjbwAAAAAAAAABAAADpQAAAGF1ZHRhAAAAWW1ldGEAAAAAAAAAIWhkbHIAAAAAAAAAAG1kaXJhcHBsAAAAAAAAAAAAAAAALGlsc3QAAAAkqXRvbwAAABxkYXRhAAAAAQAAAABMYXZmNjEuNy4xMDMAAAAIZnJlZQAAAxdtZGF0AAACrQYF//+p3EXpvebZSLeWLNgg2SPu73gyNjQgLSBjb3JlIDE2NCByMzEwOCAzMWUxOWY5IC0gSC4yNjQvTVBFRy00IEFWQyBjb2RlYyAtIENvcHlsZWZ0IDIwMDMtMjAyMyAtIGh0dHA6Ly93d3cudmlkZW9sYW4ub3JnL3gyNjQuaHRtbCAtIG9wdGlvbnM6IGNhYmFjPTEgcmVmPTMgZGVibG9jaz0xOjA6MCBhbmFseXNlPTB4MzoweDExMyBtZT1oZXggc3VibWU9NyBwc3k9MSBwc3lfcmQ9MS4wMDowLjAwIG1peGVkX3JlZj0xIG1lX3JhbmdlPTE2IGNocm9tYV9tZT0xIHRyZWxsaXM9MSA4eDhkY3Q9MSBjcW09MCBkZWFkem9uZT0yMSwxMSBmYXN0X3Bza2lwPTEgY2hyb21hX3FwX29mZnNldD0tMiB0aHJlYWRzPTIgbG9va2FoZWFkX3RocmVhZHM9MSBzbGljZWRfdGhyZWFkcz0wIG5yPTAgZGVjaW1hdGU9MSBpbnRlcmxhY2VkPTAgYmx1cmF5X2NvbXBhdD0wIGNvbnN0cmFpbmVkX2ludHJhPTAgYmZyYW1lcz0zIGJfcHlyYW1pZD0yIGJfYWRhcHQ9MSBiX2JpYXM9MCBkaXJlY3Q9MSB3ZWlnaHRiPTEgb3Blbl9nb3A9MCB3ZWlnaHRwPTIga2V5aW50PTI1MCBrZXlpbnRfbWluPTUgc2NlbmVjdXQ9NDAgaW50cmFfcmVmcmVzaD0wIHJjX2xvb2thaGVhZD00MCByYz1jcmYgbWJ0cmVlPTEgY3JmPTIzLjAgcWNvbXA9MC42MCBxcG1pbj0wIHFwbWF4PTY5IHFwc3RlcD00IGlwX3JhdGlvPTEuNDAgYXE9MToxLjAwAIAAAAAoZYiEABL//ujJ/MsteeZ1GomWWg+VpxzCPR0j/rkHZkvIIcFZB4uJwQAAAApBmiRsQ//+qZ00AAAACEGeQniCHwLHAAAACAGeYXRD/wTEAAAACAGeY2pD/wTF"))
    adb("push", str(photo), "/sdcard/Download/coolet-smoke.png")
    adb("push", str(video), "/sdcard/Download/coolet-smoke.mp4")
    assert "Added: 1" in import_file("coolet-smoke.png")
    assert "Added: 1" in import_file("coolet-smoke.mp4")
    root = capture("mixed-library")
    assert find_text(root, "Shown: 2 of 2") is not None
    report.append("Imported PNG image and MP4 video through Android document picker")
    click_text("Photos")
    root = capture("photos-only")
    assert find_text(root, "Shown: 1 of 2") is not None
    assert find_text(root, "coolet-smoke.png") is not None
    assert find_text(root, "coolet-smoke.mp4") is None
    click_text("Videos")
    root = capture("videos-only")
    assert find_text(root, "Shown: 1 of 2") is not None
    assert find_text(root, "coolet-smoke.mp4") is not None
    assert find_text(root, "coolet-smoke.png") is None
    click_text("All")
    report.append("Media filters return correct photo/video contents and counts")
    summary = import_file("coolet-smoke.png")
    assert "Added: 0" in summary and "Already in library: 1" in summary, summary
    report.append("Repeated URI import skipped without additional catalog entry")
    click_text("Description")
    root = capture("description-editor")
    field = next(n for n in root.iter("node") if n.attrib.get("class") == "android.widget.EditText")
    tap_node(field)
    adb("shell", "input", "text", "field-test")
    click_text("Save")
    time.sleep(1)
    root = capture("description-saved")
    assert find_text(root, "field-test") is not None
    edit_search("field-test")
    root = capture("description-search")
    assert find_text(root, "Shown: 1 of 2") is not None
    assert find_text(root, "coolet-smoke.png") is not None
    report.append("Manual description saved and found through description search")
    if SOURCE_BUILD:
        edit_search("test%sfield", "field-test")
        root = capture("reordered-keywords")
        assert find_text(root, "Shown: 1 of 2") is not None
        assert find_text(root, "coolet-smoke.png") is not None
        edit_search("png%sfield", "test field")
        root = capture("cross-field-keywords")
        assert find_text(root, "Shown: 1 of 2") is not None
        click_text("Videos")
        root = capture("search-and-type-filter")
        assert find_text(root, "No media matches") is not None
        click_text("All")
        edit_search("missing%sfield", "png field")
        root = capture("all-terms-required")
        assert find_text(root, "No media matches") is not None
        edit_search("", "missing field")
        report.append("Reordered and cross-field keywords, missing term exclusion and video filter passed")
    else:
        edit_search("", "field-test")
    adb("shell", "am", "force-stop", PACKAGE)
    root = launch()
    assert find_text(root, "Shown: 2 of 2") is not None
    assert find_text(root, "field-test") is not None
    report.append("Imported catalog and manual description persisted after force-stop")
    for media, label in (("coolet-smoke.png", "image"), ("coolet-smoke.mp4", "video")):
        click_text("Photos" if label == "image" else "Videos")
        click_text(media)
        root = capture(label + "-open")
        if find_text(root, "Got it") is not None:
            click_text("Got it")
            root = capture(label + "-fullscreen-confirmed")
        packages = {n.attrib.get("package") for n in root.iter("node")}
        if "com.google.android.apps.photos" not in packages:
            assert packages.intersection({"android", "com.android.intentresolver"}), "Neither chooser nor viewer appeared"
            click_text("Photos")
            root = capture(label + "-viewer-selected")
            if find_text(root, "Just once") is not None:
                click_text("Just once")
            root = capture(label + "-viewer")
        assert any(n.attrib.get("package") == "com.google.android.apps.photos" for n in root.iter("node")), "Photos viewer did not open"
        if label == "video":
            time.sleep(2)
            capture("video-after-play")
            (OUT / "video-media-metrics.txt").write_text(adb("shell", "dumpsys", "media.metrics"))
            (OUT / "video-media-session.txt").write_text(adb("shell", "dumpsys", "media_session"))
        report.append(label + " opened in Photos; screenshot evidence preserved")
        adb("shell", "input", "keyevent", "4")
        time.sleep(1)
    adb("shell", "am", "force-stop", PACKAGE)

    launch()
    report.append("App relaunched after force-stop")
    result = adb("install", "-r", str(APK))
    assert "Success" in result, result
    launch()
    report.append("Reinstallation of exact same signed APK succeeded")
    (OUT / "result.json").write_text(json.dumps({"passed": True, "checks": report,
        "limits": ["Video playback metrics/screenshots require inspection; Hebrew input/layout, large batches, permission revocation and physical phone validation remain outside this run"]}, indent=2))
    print("\n".join(report))
finally:
    (OUT / "logcat.txt").write_text(adb("logcat", "-d", "-t", "1000"))
    (OUT / "completed-checks.json").write_text(json.dumps(report, indent=2))

