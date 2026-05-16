# Mobile Automation Framework

A production-grade cross-platform mobile test automation framework built with **Appium 3.x**, **Java 21**, and **TestNG**, supporting Android and iOS execution on local devices, simulators, and real cloud devices via BrowserStack.

---

## Table of Contents

- [Framework Overview](#framework-overview)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Design Patterns & Principles](#design-patterns--principles)
- [Key Features](#key-features)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Cloud Execution](#cloud-execution)
- [CI/CD Pipeline](#cicd-pipeline)
- [Reporting](#reporting)
- [Design Decisions & Scalability Notes](#design-decisions--scalability-notes)
- [Known Issues & Debugging Log](#known-issues--debugging-log)

---

## Framework Overview

```
✅ Cross-platform          Android + iOS from single codebase
✅ Thread-safe parallel    Android and iOS running simultaneously
✅ Cloud execution         BrowserStack real device integration
✅ Config-driven           Zero hardcoded values
✅ Auto retry              Flaky test recovery with full history
✅ Screenshot on failure   Visual evidence attached to reports
✅ Allure reporting        Professional HTML reports
✅ CI/CD pipeline          Jenkins automated execution
✅ Test data layer         JSON-driven immutable data objects
✅ Page Object Model       Fluent API, platform-agnostic design
```

---

## Architecture

The framework is organised into 5 layers:

```
┌─────────────────────────────────────────┐
│         LAYER 5: CI/CD                  │  Jenkins Pipeline
├─────────────────────────────────────────┤
│         LAYER 4: REPORTING              │  Allure + Screenshots + Retry
├─────────────────────────────────────────┤
│         LAYER 3: TEST LAYER             │  TestNG + Test Classes
├─────────────────────────────────────────┤
│         LAYER 2: PAGE OBJECTS           │  Screens + WaitUtility + Gestures
├─────────────────────────────────────────┤
│         LAYER 1: CORE FRAMEWORK         │  Driver + Config + Base
└─────────────────────────────────────────┘
```

### Layer 1 — Core Framework

| Class | Responsibility |
|---|---|
| `ConfigManager` | Singleton. Loads `config.properties` from classpath. System property priority over file values. |
| `DriverFactory` | Interface contract — every factory must implement `createDriver()` |
| `AndroidDriverFactory` | Creates UiAutomator2 driver with capabilities from ConfigManager |
| `IOSDriverFactory` | Creates XCUITest driver with capabilities from ConfigManager |
| `BrowserStackAndroidFactory` | Creates Android driver routed to BrowserStack cloud |
| `BrowserStackIOSFactory` | Creates iOS driver routed to BrowserStack cloud |
| `DriverManager` | ThreadLocal driver lifecycle — create, serve, destroy. Platform stored in ThreadLocal for parallel safety. |
| `BaseTest` | `@BeforeMethod` and `@AfterMethod` lifecycle. Platform priority: testng.xml → System property → config file |

### Layer 2 — Page Objects

| Class | Responsibility |
|---|---|
| `BasePage` | `protected` driver from DriverManager. `isAndroid()` / `isIOS()` helpers. Context switching stubs. |
| `WaitUtility` | Static explicit waits. Timeout read from config. `waitForElementVisible`, `waitForElementClickable` |
| `GestureUtility` | Static gesture methods. `swipeUp`, `swipeDown`, `scrollToElement` |
| `MenuPage` | App home screen navigation. Handles compatibility screen dialog. |
| `LoginPage` | Full login flow. `loginSuccessfully()` → `HomePage`. `loginExpectingFailure()` → `LoginPage` |
| `HomePage` | Post-login landing screen |

### Layer 4 — Quality

| Class | Responsibility |
|---|---|
| `TestListener` | Implements `ITestListener` + `IConfigurationListener`. Screenshots on failure. Retry registration. |
| `RetryAnalyzer` | `IRetryAnalyzer`. Max retries from config. Same instance reused per test — correct increment behaviour. |
| `TestDataManager` | Singleton. Jackson-powered JSON reader. Immutable `User` objects via `@JsonCreator`. |

---

## Tech Stack

| Tool | Version | Purpose |
|---|---|---|
| Java | 21 | Language |
| Appium Server | 3.3.0 | Mobile automation server |
| Appium Java Client | 9.2.2 | Java bindings |
| Selenium | 4.23.0 | WebDriver base |
| TestNG | 7.9.0 | Test runner + parallel execution |
| Gradle | 9.3.0 | Build tool |
| Allure | 2.25.0 | Reporting |
| AspectJ | 1.9.20 | Allure instrumentation |
| Jackson | 2.17.1 | JSON test data parsing |
| Logback | 1.5.6 | Logging |
| Jenkins | LTS | CI/CD |
| BrowserStack | App Automate | Cloud real device execution |

---

## Project Structure

```
mobile-automation-framework/
│
├── src/main/java/com/nishan/mobile/
│   ├── config/
│   │   └── ConfigManager.java
│   ├── core/
│   │   ├── BaseTest.java
│   │   ├── DriverManager.java
│   │   └── driver/
│   │       ├── DriverFactory.java
│   │       ├── AndroidDriverFactory.java
│   │       ├── IOSDriverFactory.java
│   │       ├── BrowserStackAndroidFactory.java
│   │       └── BrowserStackIOSFactory.java
│   ├── data/
│   │   ├── User.java
│   │   └── TestDataManager.java
│   ├── listeners/
│   │   └── TestListener.java
│   ├── pages/
│   │   ├── BasePage.java
│   │   ├── MenuPage.java
│   │   ├── LoginPage.java
│   │   └── HomePage.java
│   ├── retry/
│   │   └── RetryAnalyzer.java
│   └── utils/
│       ├── WaitUtility.java
│       └── GestureUtility.java
│
├── src/main/resources/
│   ├── config.properties
│   ├── apps/
│   │   ├── TheApp.apk
│   │   └── TheApp.app.zip
│   └── testdata/
│       └── users.json
│
├── src/test/java/tests/
│   ├── LoginTest.java
│   └── SmokeTest.java
│
├── Jenkinsfile-Android
├── Jenkinsfile-iOS
├── testng.xml
├── build.gradle
└── README.md
```

---

## Design Patterns & Principles

### ThreadLocal Driver Management

Platform identity and driver session are both stored in `ThreadLocal` — never in `System.setProperty` or static fields:

```java
private static final ThreadLocal<AppiumDriver> driverThread = new ThreadLocal<>();
private static final ThreadLocal<String> platformThread    = new ThreadLocal<>();
```

`System.setProperty` is JVM-global and causes race conditions in parallel execution. ThreadLocal gives each thread complete isolation. This was identified and fixed after observing parallel test failures where platform values crossed between threads.

### Factory Pattern with Map Registry

Adding a new platform requires zero changes to existing code:

```java
private static final Map<String, Supplier<DriverFactory>> LOCAL_REGISTRY = Map.of(
    "android", AndroidDriverFactory::new,
    "ios",     IOSDriverFactory::new
);
```

This applies the Open/Closed Principle — open for extension, closed for modification.

### Singleton ConfigManager with Holder Pattern

Thread-safe lazy initialisation without `synchronized` overhead:

```java
private static class ConfigManagerHolder {
    private static final ConfigManager INSTANCE = new ConfigManager();
}
```

JVM class loading guarantees single initialisation. ConfigManager's `get()` applies `.trim()` to all values — preventing `NumberFormatException` from accidental whitespace in properties files (a real bug caught during development).

### Immutable Test Data Objects

`User` model uses `@JsonCreator` with `final` fields — no setters:

```java
public User(@JsonProperty("username") String username,
            @JsonProperty("password") String password,
            @JsonProperty("type")     String type) {
```

After JSON deserialisation, no thread can modify test data. Safe for parallel use.

---

## Key Features

### Parallel Execution

```xml
<suite name="Mobile Automation Suite"
       parallel="tests"
       thread-count="2">
    <test name="Android Tests">
        <parameter name="platform" value="android"/>
    </test>
    <test name="iOS Tests">
        <parameter name="platform" value="ios"/>
    </test>
</suite>
```

Android and iOS sessions launch simultaneously. ThreadLocal guarantees zero interference between threads.

### Platform-Agnostic Page Objects

Single page class handles both platforms via inline branching:

```java
private By loginButton() {
    return isAndroid()
        ? AppiumBy.accessibilityId("loginBtn")
        : AppiumBy.accessibilityId("loginBtn-ios");
}
```

No separate Android/iOS page classes needed unless flows genuinely differ entirely.

### Fluent Page API

```java
new MenuPage()
    .handleCompatibilityScreen()
    .navigateToLoginScreen()
    .enterUsername(user.getUsername())
    .enterPassword(user.getPassword())
    .loginSuccessfully()
    .verifyHomeScreenVisible();
```

Reads like a test case. Navigating to a new screen returns the next page object.

### Retry Analyzer

```java
private final int maxRetry = Integer.parseInt(
    ConfigManager.getInstance().get("max.retry"));
```

TestNG reuses the same `RetryAnalyzer` instance across retries of the same test — `retryCount` increments correctly and stops at `maxRetry`. Retry count is config-driven, not hardcoded.

---

## Configuration

### config.properties

```properties
# Execution
platform=android
execution.env=local
implicit.wait=10
explicit.wait=10
max.retry=2

# Appium Server (local)
appium.url=http://127.0.0.1:4723
appium.newCommandTimeout=120

# Android Local
android.deviceName=Pixel_10_Pro
android.platformVersion=17
android.automationName=UiAutomator2
android.app=apps/TheApp.apk
android.appPackage=com.appiumpro.the_app
android.appActivity=com.appiumpro.the_app.MainActivity
android.noReset=false
android.serverLaunchTimeout=60000
android.serverInstallTimeout=60000

# iOS Local
ios.deviceName=iPhone 16
ios.platformVersion=26.4
ios.automationName=XCUITest
ios.app=apps/TheApp.app.zip
ios.bundleId=com.appiumpro.the_app

# BrowserStack Cloud
bs.server.url=https://hub.browserstack.com/wd/hub
bs.username=
bs.accesskey=
bs.android.deviceName=.*
bs.android.platformVersion=13.0
bs.android.app=bs://YOUR_APP_ID
bs.ios.deviceName=iPhone.*
bs.ios.platformVersion=16
bs.ios.app=
bs.ios.bundleId=com.appiumpro.the_app
```

### Configuration Priority Chain

```
1. testng.xml parameter   ← suite-level execution
2. -D system property     ← CI/CD override
3. config.properties      ← local default
```

### Switching Environments

```bash
# Local Android
./gradlew test -Dplatform=android -Dexecution.env=local

# Local iOS
./gradlew test -Dplatform=ios -Dexecution.env=local

# BrowserStack Cloud
./gradlew test \
  -Dplatform=android \
  -Dexecution.env=cloud \
  "-Dbs.username=YOUR_USERNAME" \
  "-Dbs.accesskey=YOUR_KEY"
```

---

## Running Tests

### Prerequisites

```bash
# Install Appium
npm install -g appium
appium driver install uiautomator2
appium driver install xcuitest

# Install Allure CLI
brew install allure

# Start Appium server
appium --address 127.0.0.1 --port 4723
```

### Local Android

```bash
# Start emulator first
emulator -avd Pixel_10_Pro

# Run tests
./gradlew test -Dplatform=android
```

### Local iOS

```bash
# Boot simulator
xcrun simctl boot "iPhone 16"

# Run tests
./gradlew test -Dplatform=ios
```

### Parallel (Both Platforms)

```bash
./gradlew test
```

testng.xml is configured for `parallel="tests"` with `thread-count="2"` — both platforms run simultaneously.

### View Allure Report

```bash
allure serve allure-results
```

---

## Cloud Execution

### BrowserStack Setup

**1. Upload app:**
```bash
curl -u "USERNAME:ACCESSKEY" \
-X POST "https://api-cloud.browserstack.com/app-automate/upload" \
-F "file=@/path/to/TheApp.apk"
```

Response returns `bs://APP_ID` — add to `bs.android.app` in config.

**2. Run on cloud:**
```bash
./gradlew test \
  -Dplatform=android \
  -Dexecution.env=cloud \
  "-Dbs.username=YOUR_USERNAME" \
  "-Dbs.accesskey=YOUR_ACCESSKEY"
```

**3. View sessions via API:**
```bash
curl -u "USERNAME:ACCESSKEY" \
https://api-cloud.browserstack.com/app-automate/builds.json
```

### Verified Cloud Results

```
Project:  Mobile Automation Framework
Build:    Android Build
Device:   Google Pixel 8 Pro
OS:       Android 14.0
Sessions: 10 sessions verified
Status:   All done
```

### Device Selection Strategy

Credentials are passed via `-D` flags to avoid hardcoding in config. `bs.android.deviceName=.*` selects any available Android device running the specified minimum OS version — random allocation across BrowserStack's real device pool.

---

## CI/CD Pipeline

Two separate Jenkinsfiles — one per platform:

```
Jenkinsfile-Android   ← Android pipeline
Jenkinsfile-iOS       ← iOS pipeline
```

### Android Pipeline Stages

```
Checkout           ← pulls from GitHub (private repo)
Start Emulator     ← boots AVD headlessly, waits for full boot
Start Appium       ← starts server, polls /status for readiness
Run Tests          ← ./gradlew test -Dplatform=android
Generate Report    ← allure generate allure-results
Post: always       ← Allure Jenkins plugin publishes report
Post: cleanup      ← kills Appium + emulator
```

### Jenkins Credentials

BrowserStack credentials stored in Jenkins Credential Store — never in code or config files:

```groovy
environment {
    LT_USERNAME   = credentials('lt-username')
    LT_ACCESS_KEY = credentials('lt-accesskey')
}
```

---

## Reporting

Allure report generated after every run:

```bash
allure serve allure-results
```

### Report Contents

- Test pass/fail status per platform
- Screenshot attached on every failure
- Retry attempt history — each attempt with its own screenshot
- Test execution timeline
- Build history trends

### Screenshot on Failure

```java
@Override
public void onTestFailure(ITestResult result) {
    byte[] screenShot = DriverManager.getDriver()
        .getScreenshotAs(OutputType.BYTES);
    Allure.addAttachment(
        "Screenshot of failure - Attempt " +
        result.getMethod().getCurrentInvocationCount(),
        "image/png",
        new ByteArrayInputStream(screenShot),
        "png");
}
```

Each retry attempt captures its own screenshot. Full visibility into every failure.

---

## Design Decisions & Scalability Notes

### Configuration — Single File

Configuration is maintained in a single `config.properties` for simplicity as a portfolio project. In a production environment with multiple teams, this would be split into platform-specific files — `android.properties`, `ios.properties` — to enable clean ownership, reduce merge conflicts, and support Open/Closed scaling.

### Page Objects — No Interface Per Screen

Creating an interface for every page was evaluated and rejected for this codebase. Most flows are identical across platforms — only locators differ. Platform branching is handled inline with `isAndroid()`. Interfaces are introduced only when entire flows diverge completely between platforms.

### Appium Session Timeout — UiAutomator2 Socket Hang

During Jenkins CI runs, `SessionNotCreatedException` was observed on first session creation. Root cause identified via Appium logs:

```
Waiting up to 60000ms for UiAutomator2 to be online...
socket hang up   ← at startup
socket hang up   ← recovers after ~2 seconds
```

Fix: increased `uiautomator2ServerLaunchTimeout` to 60 seconds so Appium waits through the socket hang up rather than failing.

### BrowserStack Authentication

`bstack:options` credential passing was evaluated. URL-based Basic Auth was found more reliable with Appium Java Client 9.2.2:

```java
new URL("https://USERNAME:ACCESSKEY@hub.browserstack.com/wd/hub")
```

In production, credentials should be injected via environment variables or a secrets manager — never stored in config files or code.

### Retry Analyzer — @BeforeMethod Not Retried

TestNG's `IRetryAnalyzer` applies only to `@Test` methods — not `@BeforeMethod`. When setUp fails, the test is skipped rather than retried. This is a TestNG design constraint, not a framework bug. The first session creation occasionally fails due to Appium server initialisation timing — addressed with the UiAutomator2 timeout fix above.

---

## Known Issues & Debugging Log

| Issue | Root Cause | Fix Applied |
|---|---|---|
| `NumberFormatException` in WaitUtility | Trailing space in `explicit.wait=10 ` | Added `.trim()` to `ConfigManager.get()` |
| Parallel tests not running simultaneously | Platform stored in `System.setProperty` — JVM-global | Moved platform to `ThreadLocal<String>` in DriverManager |
| `adb: device offline` on first run | Emulator visually up but ADB not ready | Added `adb shell getprop sys.boot_completed` check |
| `android.deviceName` not found | Emulator AVD name is `Pixel_10_Pro` not `Pixel 10 Pro` | Run `emulator -list-avds` to confirm exact name |
| UiAutomator2 session timeout on Jenkins | Socket hang up during UiAutomator2 server startup | Set `uiautomator2ServerLaunchTimeout=60000` |
| BrowserStack sessions not visible in dashboard | Sessions filed under `Mobile Automation Framework` project, dashboard showing `Demo Project` | Use API endpoint: `api-cloud.browserstack.com/app-automate/builds.json` |
| `ClosedChannelException` on BrowserStack | Deprecated `AndroidDriver(URL, options)` constructor issue | Switched to URL-based Basic Auth for cloud connection |
| setUp appearing as separate Allure entry | Allure AspectJ intercepts `@BeforeMethod` failures independently of `ITestListener` | Implemented `IConfigurationListener` to separate configuration failures |

---

## Author

**Nishan MS** — QA Engineer  
Framework built for portfolio demonstration and professional development.  
GitHub: [msaninishan/mobile-automation-framework](https://github.com/msaninishan/mobile-automation-framework)
