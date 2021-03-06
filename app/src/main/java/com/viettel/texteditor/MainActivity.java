package com.viettel.texteditor;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.button.MaterialButton;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private MaterialButton boldButton;
    private MaterialButton italicButton;
    private MaterialButton underlineButton;
    private MyEditText editor;
    private TextView sizeText;
    private ImageButton addSizeButton;
    private ImageButton subSizeButton;
    private ImageButton colorButton;
    private int selectionStart;
    private int selectionEnd;
    private int start;
    private int end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setTextStyle(boldButton, StyleState.BOLD);
        setTextStyle(italicButton, StyleState.ITALIC);
        setTextStyle(underlineButton, StyleState.UNDERLINE);
        setTextSize(addSizeButton, true);
        setTextSize(subSizeButton, false);
        setColorText(colorButton);



    }

    private void setColorText(ImageButton button) {
        button.setOnClickListener(v -> {
            if (Objects.requireNonNull(editor.getText()).length() == 0) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            final View colorView = getLayoutInflater().inflate(R.layout.color_choose_layout, null);

            builder.setView(colorView);
            builder.setIcon(R.drawable.ic_color);
            builder.setTitle("Color");

            builder.setCancelable(false);
            builder.setPositiveButton("OK", (dialog, which) -> {
                EditText colorEdit = colorView.findViewById(R.id.color_text);
                String colorString = colorEdit.getText().toString();
                int color;

                try {
                    color = Color.parseColor(colorString);
                } catch (IllegalArgumentException  e) {
                    Toast.makeText(getApplicationContext(), "Wrong color code", Toast.LENGTH_SHORT).show();
                    color = Color.BLACK;
                }

                // Set the beginning and end position of the text selected
                if(editor.hasSelection()) {
                    selectionStart = editor.getSelectionStart();
                    selectionEnd = editor.getSelectionEnd();
                }
                Log.d("SELECT", "Start: " + selectionStart + "\tEnd: " + selectionEnd);

                start = Math.min(selectionStart, selectionEnd);
                end = Math.max(selectionStart, selectionEnd);

                // Split text into 3 parts
                SpannableStringBuilder beginningString;
                if (start == 0) {
                    beginningString = new SpannableStringBuilder("");
                } else {
                    beginningString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(0, start));
                }
                SpannableStringBuilder selectedString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(start, end));

                SpannableStringBuilder endingString;
                if (end == editor.getText().length()) {
                    endingString = new SpannableStringBuilder("");
                } else {
                    endingString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(end, Objects.requireNonNull(editor.getText()).length()));
                }

                // Set style for selected text
                selectedString.setSpan(new ForegroundColorSpan(color), 0, selectedString.length(), 0);

                // Combine 3 parts into a text
                beginningString.insert(beginningString.length(), selectedString);
                beginningString.insert(beginningString.length(), endingString);

                //Show formatted text
                editor.setText(beginningString);
                editor.setSelection(end);
                Drawable unwrappedColorDrawable = colorButton.getBackground();
                Drawable wrappedColorDrawable = DrawableCompat.wrap(unwrappedColorDrawable);
                DrawableCompat.setTint(wrappedColorDrawable, color);
            });

            builder.setNegativeButton("Cancel", null);


            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void setTextSize(ImageButton button, boolean isAdd) {
        button.setOnClickListener(v -> {
            if (Objects.requireNonNull(editor.getText()).length() == 0) {
                return;
            }

            String sizeString = sizeText.getText().toString();
            try {
                Integer.parseInt(sizeString);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Wrong size text", Toast.LENGTH_SHORT).show();
            }

            int size = Integer.parseInt(sizeString);
            if (isAdd) {
                size++;
            } else {
                size--;
            }
            // Set the beginning and end position of the text selected
            if(editor.hasSelection()) {
                selectionStart = editor.getSelectionStart();
                selectionEnd = editor.getSelectionEnd();
            }
            Log.d("SELECT", "Start: " + selectionStart + "\tEnd: " + selectionEnd);
            start = Math.min(selectionStart, selectionEnd);
            end = Math.max(selectionStart, selectionEnd);

            // Split text into 3 parts
            SpannableStringBuilder beginningString;
            if (start == 0) {
                beginningString = new SpannableStringBuilder("");
            } else {
                beginningString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(0, start));
            }
            SpannableStringBuilder selectedString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(start, end));

            SpannableStringBuilder endingString;
            if (end == editor.getText().length()) {
                endingString = new SpannableStringBuilder("");
            } else {
                endingString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(end, Objects.requireNonNull(editor.getText()).length()));
            }

            // Set style for selected text
            selectedString.setSpan(new AbsoluteSizeSpan(sp2px(size, getApplicationContext())), 0, selectedString.length(), 0);

            // Combine 3 parts into a text
            beginningString.insert(beginningString.length(), selectedString);
            beginningString.insert(beginningString.length(), endingString);

            //Show formatted text
            editor.setText(beginningString);
            editor.setSelection(end);
            sizeText.setText(String.valueOf(size));
        });
    }

    private void setTextStyle(MaterialButton button, Object styleSpan) {
        button.setOnClickListener(v -> {
            if (Objects.requireNonNull(editor.getText()).length() == 0) {
                return;
            }
            // Set the beginning and end position of the text selected
            if (editor.hasSelection()) {
                selectionStart = editor.getSelectionStart();
                selectionEnd = editor.getSelectionEnd();
            }

            Log.d("SELECT", "Start: " + selectionStart + "\tEnd: " + selectionEnd);

            start = Math.min(selectionStart, selectionEnd);
            end = Math.max(selectionStart, selectionEnd);

            // Split text into 3 parts
            SpannableStringBuilder beginningString;
            if (start == 0) {
                beginningString = new SpannableStringBuilder("");
            } else {
                beginningString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(0, start));
            }
            SpannableStringBuilder selectedString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(start, end));

            SpannableStringBuilder endingString;
            if (end == editor.getText().length()) {
                endingString = new SpannableStringBuilder("");
            } else {
                endingString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(end, Objects.requireNonNull(editor.getText()).length()));
            }
            // Set style for selected text
            if (button.isChecked()) {
                selectedString.setSpan(styleSpan, 0, selectedString.length(), 0);
            } else {
                selectedString.removeSpan(styleSpan);
            }

            // Combine 3 parts into a text
            beginningString.insert(beginningString.length(), selectedString);
            beginningString.insert(beginningString.length(), endingString);

            //Show formatted text
            editor.setText(beginningString);
            editor.setSelection(end);
        });
    }

    public void init() {
        boldButton = findViewById(R.id.action_bold);
        italicButton = findViewById(R.id.action_italic);
        underlineButton = findViewById(R.id.action_underline);
        addSizeButton = findViewById(R.id.action_size_add);
        subSizeButton = findViewById(R.id.action_size_sub);
        colorButton = findViewById(R.id.action_color);
        editor = findViewById(R.id.editor);
        sizeText = findViewById(R.id.text_size);
    }

    public static int sp2px(float sp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    private static class StyleState {
        static final StyleSpan BOLD = new StyleSpan(Typeface.BOLD);
        static final StyleSpan ITALIC = new StyleSpan(Typeface.ITALIC);
        static final UnderlineSpan UNDERLINE = new UnderlineSpan();
    }
}