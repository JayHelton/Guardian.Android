/*
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.guardian.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.auth0.android.guardian.sdk.Guardian;
import com.auth0.android.guardian.sdk.ParcelableNotification;
import com.auth0.android.guardian.sdk.networking.Callback;

public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = NotificationActivity.class.getName();

    private static final String GUARDIAN = "GUARDIAN";
    private static final String ENROLLMENT = "ENROLLMENT";
    private static final String NOTIFICATION = "NOTIFICATION";

    private TextView labelText;
    private TextView userText;
    private TextView browserText;
    private TextView osText;
    private TextView locationText;
    private TextView dateText;

    private Guardian guardian;
    private ParcelableEnrollment enrollment;
    private ParcelableNotification notification;

    static Intent getStartIntent(@NonNull Context context,
                                 @NonNull Guardian guardian,
                                 @NonNull ParcelableNotification notification,
                                 @NonNull ParcelableEnrollment enrollment) {
        if (!enrollment.getId().equals(notification.getEnrollmentId())) {
            final String message = String.format("Notification doesn't match enrollment (%s != %s)",
                    notification.getEnrollmentId(), enrollment.getId());
            throw new IllegalArgumentException(message);
        }

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.putExtra(GUARDIAN, guardian);
        intent.putExtra(ENROLLMENT, enrollment);
        intent.putExtra(NOTIFICATION, notification);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Intent intent = getIntent();
        guardian = intent.getParcelableExtra(GUARDIAN);
        enrollment = intent.getParcelableExtra(ENROLLMENT);
        notification = intent.getParcelableExtra(NOTIFICATION);

        setupUI();

        updateUI();
    }

    private void setupUI() {
        labelText = (TextView) findViewById(R.id.labelText);
        userText = (TextView) findViewById(R.id.userText);
        browserText = (TextView) findViewById(R.id.browserText);
        osText = (TextView) findViewById(R.id.osText);
        locationText = (TextView) findViewById(R.id.locationText);
        dateText = (TextView) findViewById(R.id.dateText);

        Button rejectButton = (Button) findViewById(R.id.rejectButton);
        assert rejectButton != null;
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectRequested();
            }
        });

        Button allowButton = (Button) findViewById(R.id.allowButton);
        assert allowButton != null;
        allowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowRequested();
            }
        });
    }

    private void updateUI() {
        labelText.setText(enrollment.getLabel());
        userText.setText(enrollment.getUser());
        browserText.setText(
                String.format("%s, %s",
                        notification.getBrowserName(),
                        notification.getBrowserVersion()));
        osText.setText(
                String.format("%s, %s",
                        notification.getOsName(),
                        notification.getOsVersion()));
        locationText.setText(notification.getLocation());
        dateText.setText(notification.getDate().toString());
    }

    private void rejectRequested() {
        guardian
                .reject(notification, enrollment)
                .start(new DialogCallback<>(this,
                        R.string.progress_title_please_wait,
                        R.string.progress_message_reject,
                        new Callback<Void>() {
                            @Override
                            public void onSuccess(Void response) {
                                finish();
                            }

                            @Override
                            public void onFailure(Throwable exception) {

                            }
                        }));
    }

    private void allowRequested() {
        guardian
                .allow(notification, enrollment)
                .start(new DialogCallback<>(this,
                        R.string.progress_title_please_wait,
                        R.string.progress_message_allow,
                        new Callback<Void>() {
                            @Override
                            public void onSuccess(Void response) {
                                finish();
                            }

                            @Override
                            public void onFailure(Throwable exception) {

                            }
                        }));
    }
}
