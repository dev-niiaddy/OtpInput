[![](https://jitpack.io/v/dev-niiaddy/OtpInput.svg)](https://jitpack.io/#dev-niiaddy/OtpInput/Tag)

Easy to use and highly customizable OTP Input.

<img src="https://i.ibb.co/mv9313B/Screenshot-1600877653.png" alt="Otp Input Android Screenshot" border="0" height="500">

## Installation
Step 1. Add the JitPack repository to your build file
```gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
Step 2. Add the dependency
```gradle
dependencies {
        implementation 'com.github.dev-niiaddy:OtpInput:1.0.0'
}
```

## Usage

### In Xml
```xml
<com.godwinaddy.otpinput.OtpInput
        android:id="@+id/otpInput"
        android:layout_width="0dp"
        android:layout_height="80dp"
        android:textStyle="normal"
        android:textSize="14sp"
        app:inputType="number"
        android:textColor="@android:color/white"
        app:inputBackground="@color/colorPrimary"
        app:inputCount="4"
        app:inputRadius="10dp"
        app:inputSpacing="10dp"/>
```

### Kotlin Code
```kotlin
    val otpInput = findViewById<OtpInput>(R.id.otpInput)

    // listener for all input fields completed
    otpInput.onInputFinishedListener { otpText ->
        Log.d("Input Finished", otpText)
    }
    
    // listener for otpInput Text Changed with completed status
    otpInput.inputChangedListener { inputComplete, otpText ->
        Log.d("Input Finished", otpText)
    }

    // function to focus the OtnInput and show keyboard
    otpInput.focusOtpInput()

    //get entered otp text input
    otpInput.otpText
```

## XML Attributes
<table>
<thead>
  <tr>
    <th>XML Attribute</th>
    <th>Format</th>
    <th>Description</th>
  </tr>
</thead>
<tbody>
  <tr>
    <td>android:textStyle</td>
    <td>bold | normal | italic</td>
    <td>Sets text size for input fields</td>
  </tr>
  <tr>
    <td>android:textSize</td>
    <td>dimension</td>
    <td>Text size of text in input fields</td>
  </tr>
  <tr>
      <td>android:textColor</td>
      <td>color</td>
      <td>Sets input field(s) text color</td>
    </tr>
  <tr>
    <td>app:inputBackground</td>
    <td>color</td>
    <td>Sets the background color for input fields</td>
  </tr>
  <tr>
    <td>app:inputCount</td>
    <td>integer</td>
    <td>Sets the number of fields to create for otp</td>
  </tr>
  <tr>
    <td>app:inputRadius</td>
    <td>dimension</td>
    <td>Sets input field radius. For achieving curved corners</td>
  </tr>
  <tr>
    <td>app:inputSpacing</td>
    <td>dimension</td>
    <td>Space to create in between input fields</td>
  </tr>
  <tr>
    <td>app:inputType</td>
    <td>number | numberPassword</td>
    <td>Specify Input Type to use.</td>
  </tr>
</tbody>
</table>