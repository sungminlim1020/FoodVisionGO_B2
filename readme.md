# 🍽️ FoodVisionGO
**AI 이미지 기반 음식 분석 앱 (Android Studio / Kotlin)**

FoodVisionGO는 사용자가 갤러리 또는 카메라로 찍은 음식 사진을 기반으로  
**OpenAI GPT API를 활용하여 음식 분석, 영양 정보 설명, 칼로리 조언** 등을 제공하는 Android 앱입니다.

---

## 📌 주요 기능

### ✅ 1. 사진 선택
- 갤러리에서 음식 사진 선택
- 선택한 이미지를 화면에 실시간으로 표시

### ✅ 2. 카메라 촬영(선택 기능)
- 앱 내 카메라로 음식 촬영 가능
- 촬영 즉시 분석 화면으로 이동

### ✅ 3. AI 음식 분석
- 선택한 이미지의 URI 정보를 기반으로
- OpenAI GPT API에 요청하여 음식 분석 수행
- 음식 추측, 칼로리 정보, 건강 조언 등을 한국어로 제공

### ✅ 4. 오류 처리
- 이미지 미선택 시 경고
- API 호출 실패 시 오류 메시지 표시
- 인터넷 문제/AI 응답 문제 대응

---

## 📱 앱 실행 흐름 (Flow)

1. **이미지 선택**
2. 이미지 UI에 표시
3. **AI 분석 버튼 클릭**
4. GPT API 요청
5. 분석 결과를 TextView에 표시

---

## 🛠️ 기술 스택

- **Android Studio** (Koala or Hedgehog)
- **Kotlin**
- **XML 기반 ViewBinding**
- **OkHttp3** (HTTP 통신)
- **OpenAI GPT-4o-mini API**
- **Camera / Storage Permission**

---

## 📂 프로젝트 구조

app/
└─ src/
└─ main/
├─ java/com/example/foodvisiongo/MainActivity.kt
├─ res/layout/activity_main.xml
├─ res/drawable/
├─ AndroidManifest.xml
└─ build.gradle.kts

yaml
코드 복사

---

아래는 FoodVisionGO 앱의 실제 실행 화면 캡처와 동작 영상입니다.
(https://youtube.com/shorts/Ki_Id4zfBXY?feature=share)


### 📱 메인 화면
![메인 화면](https://raw.githubusercontent.com/sungminlim1020/FoodVisionGO_B2/master/screenshots/main.jpg)

### 📷 사진 선택 및 분석
![사진 선택 및 분석](https://raw.githubusercontent.com/sungminlim1020/FoodVisionGO_B2/master/screenshots/camera.jpg)

### 🤖 AI 분석 결과
![AI 분석 결과](https://raw.githubusercontent.com/sungminlim1020/FoodVisionGO_B2/master/screenshots/result.jpg)

---
## 🔑 OpenAI API 키 설정

`MainActivity.kt` 안에 직접 입력하는 방식:

```kotlin
private fun callOpenAI() {
    val apiKey = "여기에 본인의 OpenAI API Key 입력" 


📸 카메라 / 갤러리 권한
AndroidManifest.xml:

xml
코드 복사
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
🚀 빌드 방법
Android Studio에서 프로젝트 열기

Gradle Sync 자동 실행

OpenAI API Key 입력

휴대폰 연결 후 실행

	

🙌 개발자
평택대학교 스마트콘텐츠학과

개발자: 임성민
