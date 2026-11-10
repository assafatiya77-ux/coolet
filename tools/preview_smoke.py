import hashlib
import json
from pathlib import Path
import re
import subprocess
import time
import xml.etree.ElementTree as ET

OUT = Path("runtime-evidence")
OUT.mkdir(exist_ok=True)
APK = Path("dist/Coolet-Preview-0.2.3.apk")
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

try:
    assert hashlib.sha256(APK.read_bytes()).hexdigest() == "df7b7c2f83e0c7b4ef778eed20455905b4ff84cf6ea13a0bd898cfe168916b46"
    report.append("Exact distributed APK SHA256 verified")
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
    adb("shell", "am", "force-stop", PACKAGE)
    launch()
    report.append("App relaunched after force-stop")
    result = adb("install", "-r", str(APK))
    assert "Success" in result, result
    launch()
    report.append("Reinstallation of exact same signed APK succeeded")
    (OUT / "result.json").write_text(json.dumps({"passed": True, "checks": report,
        "limits": ["No media import, description persistence, playback or physical phone validation in this smoke check"]}, indent=2))
    print("\n".join(report))
finally:
    (OUT / "logcat.txt").write_text(adb("logcat", "-d", "-t", "1000"))
    (OUT / "completed-checks.json").write_text(json.dumps(report, indent=2))
