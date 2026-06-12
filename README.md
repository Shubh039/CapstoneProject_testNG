# 🏦 Parabank Test Automation Framework

<div align="center">

![Java](https://img.shields.io/badge/Java-11-orange?style=for-the-badge&logo=java)
![Selenium](https://img.shields.io/badge/Selenium-4.25.0-green?style=for-the-badge&logo=selenium)
![TestNG](https://img.shields.io/badge/TestNG-7.9.0-red?style=for-the-badge)
![Maven](https://img.shields.io/badge/Maven-3.8.6-blue?style=for-the-badge&logo=apachemaven)
![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-darkred?style=for-the-badge&logo=jenkins)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue?style=for-the-badge&logo=docker)
![ExtentReports](https://img.shields.io/badge/ExtentReports-5.1.1-purple?style=for-the-badge)

**A comprehensive, industry-level Selenium Test Automation Framework built for the Parabank Demo Banking Application**

[Features](#-features) • [Tech Stack](#-tech-stack) • [Project Structure](#-project-structure) • [Test Modules](#-test-modules) • [Defects Found](#-defects-found) • [Setup](#-setup--installation) • [Reports](#-reports)

</div>

---

## 📌 Project Overview

This capstone project demonstrates a **Hybrid Automation Framework** combining the **Page Object Model (POM)** and **Data-Driven Testing** patterns to automate end-to-end banking workflows on the [Parabank Demo Application](https://parabank.parasoft.com).

The framework was designed to reflect **real-world SDET practices** including:
- Automated defect detection and documentation
- CI/CD pipeline integration with Jenkins and Docker
- Professional HTML reporting with screenshots
- Structured logging with Log4j2
- Bug tracking and test case management in Jira

---

## ✨ Features

| Feature | Description |
|---|---|
| 🏗️ **Hybrid Framework** | Combines POM + Data-Driven Testing |
| 📊 **Data-Driven Testing** | Excel-based test data with Apache POI |
| 🔗 **Data Chaining** | Registration writes credentials → Login reads them |
| 🐛 **Defect Detection** | 7 real bugs found and documented |
| 📸 **Auto Screenshots** | Captured on failure + embedded in reports |
| 📋 **ExtentReports** | Beautiful HTML reports with charts |
| 📝 **Log4j2 Logging** | Console + file logging with structured format |
| 🔄 **CI/CD Pipeline** | Jenkins + Docker automated pipeline |
| 🧵 **Thread Safe** | ThreadLocal WebDriver for parallel execution |
| 🌐 **Cross Browser** | Chrome, Firefox, Edge support |
| 💉 **Dependency Injection** | WebDriver injected via constructor |
| 🎯 **Smoke & Regression** | Separate TestNG suites for different test levels |

---

## 🛠️ Tech Stack

```
Language        →  Java 11
Test Framework  →  TestNG 7.9.0
Browser Auto    →  Selenium WebDriver 4.25.0
Driver Mgmt     →  WebDriverManager 5.8.0
Build Tool      →  Maven 3.8.6
Test Data       →  Apache POI 5.2.3 (Excel)
Reporting       →  ExtentReports 5.1.1
Logging         →  Log4j2 2.23.1
CI/CD           →  Jenkins
Containerization→  Docker
Bug Tracking    →  Jira
IDE             →  Eclipse
```

---

## 📁 Project Structure

```
ParabankAutomation/
│
├── 📄 Dockerfile                          → Docker image for automation
├── 📄 Jenkinsfile                         → CI/CD pipeline definition
├── 📄 pom.xml                             → Maven dependencies
├── 📄 testng.xml                          → Master test suite
├── 📄 testng-negative.xml                 → Negative/defect test suite
├── 📄 testng_smoke.xml                    → Smoke test suite
├── 📄 testng_regression.xml               → Regression test suite
│
├── 📁 src/test/java/
│   ├── 📁 base/
│   │   └── BaseTest.java                  → WebDriver setup, teardown, screenshots
│   │
│   ├── 📁 pages/                          → Page Object Model classes
│   │   ├── RegistrationPage.java
│   │   ├── LoginPage.java
│   │   ├── AccountsOverviewPage.java
│   │   ├── OpenAccount.java
│   │   ├── TransferFundsPage.java
│   │   ├── BillPaymentPage.java
│   │   └── RequestLoanPage.java
│   │
│   ├── 📁 testcases/                      → Test classes
│   │   ├── RegistrationTest.java
│   │   ├── LoginTest.java
│   │   ├── OpenAccountTest.java
│   │   ├── TransferFundsTest.java
│   │   ├── BillPaymentTest.java
│   │   ├── RequestLoanTest.java
│   │   └── NegativeTest.java              → All defect test cases
│   │
│   └── 📁 utilities/                      → Utility/helper classes
│       ├── ConfigReader.java              → Reads config.properties
│       ├── ExcelUtils.java                → Read/Write Excel data
│       ├── ScreenShotUtils.java           → File + Base64 screenshots
│       ├── LoggerUtil.java                → Log4j2 logger factory
│       ├── ExtentReportManager.java       → ExtentReports lifecycle
│       └── TestListener.java              → TestNG ITestListener
│
├── 📁 src/test/resources/
│   ├── 📁 config/
│   │   └── config.properties              → Browser, URL, paths
│   ├── 📁 testdata/
│   │   └── TestData.xlsx                  → Excel test data
│   └── log4j2.xml                         → Log4j2 configuration
│
├── 📁 reports/                            → ExtentReports HTML output
├── 📁 screenshots/                        → Failure screenshots
└── 📁 logs/                               → automation.log file
```

---

## 🧪 Test Modules

### ✅ Positive Test Cases (testng.xml)

| Module | Test | Description |
|---|---|---|
| **Registration** | TC-REG-01 | Register new user with valid Excel data |
| **Login** | TC-LOG-01 | Valid login with credentials from Excel |
| **Open Account** | TC-OAC-01 | Open CHECKING account + balance verification |
| **Transfer Funds** | TC-TRF-01 | Valid transfer + before/after balance check |
| **Bill Payment** | TC-BILL-01 | Valid payment + transaction activity check |
| **Request Loan** | TC-LOAN-01 | Valid loan application + new account verification |
| **Request Loan** | TC-LOAN-02 | Excessive loan amount correctly denied |

### ❌ Negative Test Cases (testng-negative.xml)

| Bug ID | Module | Scenario |
|---|---|---|
| PB-L01/02/03 | Login | Invalid credentials / Empty fields / Wrong password |
| PB-001 | Transfer Funds | Negative amount transfer |
| PB-002 | Bill Payment | Negative payment amount |
| PB-003 | Transfer Funds | Same source and destination account |
| PB-004 | Bill Payment | Mismatched account numbers |
| PB-005 | Transfer Funds | Zero amount transfer |
| PB-006 | Request Loan | Negative down payment |
| PB-007 | Request Loan | Balance goes negative from down payment |
| PB-008 | Request Loan | Letters in amount field cause server error |
| PB-009 | Request Loan | Large loan amount denied without clear limit |

---

## 🐛 Defects Found

> Real defects discovered by the automation framework in the Parabank application

### 🔴 Critical Defects

| Bug ID | Module | Description | Impact |
|---|---|---|---|
| **PB-001** | Transfer Funds | System accepts **negative transfer amount** (-$100) and completes the transfer | Money flow reversed — potential fraud |
| **PB-002** | Bill Payment | System accepts **negative payment amount** (-$100) and completes the payment | Reverses payment direction |
| **PB-007** | Request Loan | Account **balance goes negative** after loan with down payment exceeding available funds | Banking integrity violation |

### 🟠 High Severity Defects

| Bug ID | Module | Description |
|---|---|---|
| **PB-003** | Transfer Funds | System allows transfer **from and to the same account** |
| **PB-005** | Transfer Funds | System allows **$0.00 transfer** — creates meaningless transaction records |
| **PB-006** | Request Loan | System accepts **negative down payment** value |

### 🟡 Medium Severity Defects

| Bug ID | Module | Description |
|---|---|---|
| **PB-008** | Request Loan | Letters in amount field cause **server error** instead of client-side validation |
| **PB-009** | Request Loan | Large loan denial message doesn't communicate **maximum loan limit** to user |

---

## ⚙️ Setup & Installation

### Prerequisites

Make sure you have these installed:

```
✅ Java JDK 11
✅ Maven 3.8.6+
✅ Eclipse IDE
✅ Google Chrome (latest)
✅ Docker Desktop
✅ Jenkins
✅ Git
```

### Step 1 — Clone the Repository

```bash
git clone https://github.com/Shubh039/CapstoneProject_testNG.git
cd CapstoneProject_testNG/ParabankAutomation
```

### Step 2 — Configure `config.properties`

Open `src/test/resources/config/config.properties`:

```properties
browser=chrome
url=http://localhost:8080/parabank/index.htm
explicitWait=15
screenshotPath=screenshots/
excelPath=src/test/resources/testdata/TestData.xlsx
```

### Step 3 — Start Parabank using Docker

```bash
docker pull parasoft/parabank
docker run -d --name parabank -p 8080:8080 parasoft/parabank
```

Wait 30 seconds, then open `http://localhost:8080/parabank` to verify it's running.

### Step 4 — Install Maven Dependencies

```bash
mvn clean install -DskipTests
```

### Step 5 — Run the Tests

```bash
# Run all positive tests
mvn test -Dsurefire.suiteXmlFiles=testng.xml

# Run negative/defect tests
mvn test -Dsurefire.suiteXmlFiles=testng-negative.xml

# Run smoke tests only
mvn test -Dsurefire.suiteXmlFiles=testng_smoke.xml

# Run full regression suite
mvn test -Dsurefire.suiteXmlFiles=testng_regression.xml
```

---

## 📊 Reports

After running tests, open the HTML report:

```
reports/AutomationReport.html
```

Open this file in any browser to see:

- ✅ Pass / ❌ Fail / ⚠️ Warning counts
- 📸 Screenshots embedded for failed tests
- 🐛 Bug documentation with screenshots for defects
- 📈 Test execution timeline
- 🖥️ System information (browser, OS, Java version)

**Log file** is available at:
```
logs/automation.log
```

---

## 🔄 CI/CD Pipeline

### Jenkins Pipeline Stages

```
┌─────────────────────────────────────────────────────┐
│                  Jenkins Pipeline                   │
├──────────┬──────────┬──────────┬──────────┬─────────┤
│ Checkout │  Start   │  Build   │   Run    │ Archive │
│   Code   │ Parabank │  Docker  │  Tests   │ Reports │
│          │  Docker  │  Image   │          │         │
└──────────┴──────────┴──────────┴──────────┴─────────┘
                                                  │
                                            Post Always:
                                            Stop Parabank
                                            Publish Results
```

### Running via Jenkins

1. Open Jenkins at `http://localhost:8080`
2. Navigate to `CapstoneProject_testNG` job
3. Click **Build Now**
4. View results in **Console Output**
5. Download reports from **Build Artifacts**

### Running via Docker

```bash
# Build automation image
docker build -t parabank-automation .

# Run tests inside Docker
docker run --rm \
  -e headless=true \
  --add-host=host.docker.internal:host-gateway \
  -v $(pwd)/reports:/app/reports \
  -v $(pwd)/screenshots:/app/screenshots \
  parabank-automation
```

---

## 🧠 Key Concepts Demonstrated

```
✅ Page Object Model (POM)
✅ Data-Driven Testing with Excel
✅ Data Chaining across test modules
✅ ThreadLocal WebDriver (parallel test safety)
✅ Explicit Waits (WebDriverWait)
✅ Hard Assert vs SoftAssert
✅ TestNG Annotations (@Test, @BeforeMethod, @AfterMethod)
✅ TestNG Groups (smoke, regression)
✅ @DataProvider for parameterized tests
✅ ITestListener for automatic reporting
✅ ExtentReports with embedded screenshots
✅ Log4j2 with dual appenders (console + file)
✅ Dynamic XPath for runtime element location
✅ Select class for dropdown handling
✅ AJAX/JavaScript button handling
✅ Currency string parsing to double
✅ Before/After balance comparison
✅ Defect documentation pattern
✅ CI/CD with Jenkins Pipeline
✅ Docker containerization
```

---

## 📐 Framework Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    Test Classes                         │
│  RegistrationTest  LoginTest  TransferTest  NegativeTest│
└─────────────────────┬───────────────────────────────────┘
                      │ extends
┌─────────────────────▼───────────────────────────────────┐
│                    BaseTest                             │
│         ThreadLocal WebDriver + Setup/Teardown          │
└──────┬──────────────┬──────────────────┬────────────────┘
       │              │                  │
┌──────▼──────┐ ┌─────▼──────┐ ┌────────▼───────┐
│ Page Classes│ │  Utilities │ │  TestListener  │
│ (POM Layer) │ │ ConfigReader│ │ ExtentReports  │
│ LoginPage   │ │ ExcelUtils │ │ Auto reporting │
│ TransferPage│ │ ScreenShot │ │ for every test │
└─────────────┘ └────────────┘ └────────────────┘
                      │
              ┌───────▼────────┐
              │  Test Data     │
              │  TestData.xlsx │
              │  config.props  │
              └────────────────┘
```

---

## 📋 Test Suite Summary

| Suite | File | Tests | Purpose |
|---|---|---|---|
| **Master** | `testng.xml` | All positive | Full positive test run |
| **Negative** | `testng-negative.xml` | 10 defect tests | Bug documentation |
| **Smoke** | `testng_smoke.xml` | 6 critical | Quick sanity check |
| **Regression** | `testng_regression.xml` | All tests | Full regression |

---

## 👨‍💻 Author

**Shubhank Sharma**

> *SDET Capstone Project — Automation Testing Framework for BFSI Applications using Selenium (Java)*

---

<div align="center">

⭐ **If you found this project helpful, please give it a star!** ⭐

</div>
