package com.viettel.texteditor;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
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

import com.google.android.material.chip.Chip;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Chip boldButton;
    private Chip italicButton;
    private Chip underlineButton;
    private MyEditText editor;
    private TextView sizeText;
    private ImageButton addSizeButton;
    private ImageButton subSizeButton;
    private TextView colorButton;
    private int selectionStart;
    private int selectionEnd;
    private int start;
    private int end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        setTextStyle(boldButton, new StyleSpan(Typeface.BOLD));
        setTextStyle(italicButton, new StyleSpan(Typeface.ITALIC));
        setTextStyle(underlineButton, new UnderlineSpan());
        setTextSize(addSizeButton, true);
        setTextSize(subSizeButton, false);
        setColorText(colorButton);


    }

    private void setColorText(TextView button) {
        button.setOnClickListener(v -> {
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
                    Log.d("SELECT", "Start: " + selectionStart + "\tEnd: " + selectionEnd);
                }

                start = Math.min(selectionStart, selectionEnd);
                end = Math.max(selectionStart, selectionEnd);

                // Split text into 3 parts
                SpannableStringBuilder beginningString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(0, start));
                SpannableStringBuilder selectedString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(start, end));
                SpannableStringBuilder endingString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(end, Objects.requireNonNull(editor.getText()).length()));

                // Set style for selected text
                selectedString.setSpan(new ForegroundColorSpan(color), 0, selectedString.length(), 0);

                // Combine 3 parts into a text
                beginningString.insert(beginningString.length(), selectedString);
                beginningString.insert(beginningString.length(), endingString);

                //Show formatted text
                editor.setText(beginningString);
                editor.setSelection(end);
                if (color == Color.BLACK) {
                    colorButton.setText("#FFFFFF");
                    colorButton.setTextColor(Color.BLACK);
                } else {
                    colorButton.setText(colorString);
                    colorButton.setTextColor(color);
                }
            });

            builder.setNegativeButton("Cancel", null);


            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void setTextSize(ImageButton button, boolean isAdd) {
        button.setOnClickListener(v -> {
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
                Log.d("SELECT", "Start: " + selectionStart + "\tEnd: " + selectionEnd);
            }

            start = Math.min(selectionStart, selectionEnd);
            end = Math.max(selectionStart, selectionEnd);

            // Split text into 3 parts
            SpannableStringBuilder beginningString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(0, start));
            SpannableStringBuilder selectedString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(start, end));
            SpannableStringBuilder endingString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(end, Objects.requireNonNull(editor.getText()).length()));

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

    private void setTextStyle(Chip button, Object styleSpan) {
        button.setOnClickListener(v -> {
            // Set the beginning and end position of the text selected
            if(editor.hasSelection()) {
                selectionStart = editor.getSelectionStart();
                selectionEnd = editor.getSelectionEnd();
                Log.d("SELECT", "Start: " + selectionStart + "\tEnd: " + selectionEnd);
            }

            start = Math.min(selectionStart, selectionEnd);
            end = Math.max(selectionStart, selectionEnd);

            // Split text into 3 parts
            SpannableStringBuilder beginningString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(0, start));
            SpannableStringBuilder selectedString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(start, end));
            SpannableStringBuilder endingString = new SpannableStringBuilder(Objects.requireNonNull(editor.getText()).subSequence(end, Objects.requireNonNull(editor.getText()).length()));

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
}