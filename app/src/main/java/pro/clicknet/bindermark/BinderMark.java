package pro.clicknet.bindermark;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

public class BinderMark extends Activity {

    private static final int DEFAULT_SIZE = 1;
    private static final boolean DEFAULT_NATIVE_METHOD = false;

    private int mSize;
    private EditText mSizeText;

    private boolean mNativeMethod;
    private Switch mNativeMethodSwitch;

    private Button mPerformButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSize = DEFAULT_SIZE;
        mSizeText = (EditText) findViewById(R.id.text_size);
        mSizeText.setText(String.valueOf(mSize));

        mNativeMethod = DEFAULT_NATIVE_METHOD;
        mNativeMethodSwitch = (Switch) findViewById(R.id.switch_native_method);
        mNativeMethodSwitch.setChecked(mNativeMethod);

        mPerformButton = (Button) findViewById(R.id.button_perform_test);
        mPerformButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    mSize = Integer.parseInt(mSizeText.getText().toString());

                    if (mSize < 1 || mSize > 512) {
                        throw new NumberFormatException("Incorrect size");
                    }
                } catch (NumberFormatException exc) {
                    Toast.makeText(BinderMark.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
                }

                mNativeMethod = mNativeMethodSwitch.isChecked();

                perform(mSize, mNativeMethod);
            }

        });

        setContentView(R.layout.bindermark);
    }

    private void perform(int size, boolean nativeMethod) {
        return;
    }

}
