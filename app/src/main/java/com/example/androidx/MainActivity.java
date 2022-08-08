package com.example.androidx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    ImageView imageViewLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageViewLogin = findViewById(R.id.imageView);
        checkBiometricSupported();

        Executor executor = ContextCompat.getMainExecutor(MainActivity.this);
        androidx.biometric.BiometricPrompt biometricPrompt = new androidx.biometric.BiometricPrompt(MainActivity.this, executor, new androidx.biometric.BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(MainActivity.this, "Auth error: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Auth Succeeded", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "Auth Failed", Toast.LENGTH_SHORT).show();
            }
        });

        imageViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                BiometricPrompt.PromptInfo.Builder promptInfo = dialogMetric();
                promptInfo.setNegativeButtonText("Cancel");
                biometricPrompt.authenticate(promptInfo.build());

            }
        });
    }

//    private BiometricPrompt getPrompt() {
//        Executor executor = ContextCompat.getMainExecutor(this);
//        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
//            @Override
//            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
//                super.onAuthenticationError(errorCode, errString);
//                Toast.makeText(MainActivity.this, "Auth error: " + errString, Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
//                super.onAuthenticationSucceeded(result);
//                Toast.makeText(MainActivity.this, "Auth Succeeded", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onAuthenticationFailed() {
//                super.onAuthenticationFailed();
//                Toast.makeText(MainActivity.this, "Auth Failed", Toast.LENGTH_SHORT).show();
//            }
//        };
//        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, callback);
//        return biometricPrompt;
//
//    }

    BiometricPrompt.PromptInfo.Builder dialogMetric() {
        return new BiometricPrompt.PromptInfo.Builder().setTitle("Biometric login").setSubtitle("Login using your biometric credential");
    }

    public void checkBiometricSupported() {
        BiometricManager manager = BiometricManager.from(this);

        String info = "";

        switch (manager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK | BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                info = "App can authenticate using biometrics.";
                enableButton(true);

                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                info = "No biometric features available on this device.";
                enableButton(false);

                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                info = "Biometric features are currently unavailable.";
                enableButton(false);

                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                enableButton(false, true);

                // Prompts the user to create credentials that your app accepts.
                info = "Need register at least one finger print";

                break;
            default:
                info = "Unknown case";
                break;
        }

        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();

    }

    void enableButton(boolean b) {
        imageViewLogin.setEnabled(b);
    }

    void enableButton(boolean b, boolean enroll) {
        enableButton(b);
        if (!enroll) return;
        Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
        enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL);
        startActivity(enrollIntent);

    }
}