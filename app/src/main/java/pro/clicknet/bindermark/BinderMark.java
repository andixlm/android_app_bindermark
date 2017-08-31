package pro.clicknet.bindermark;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

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

        mPerformButton = (Button) findViewById(R.id.button_perform_test);

        setContentView(R.layout.bindermark);
    }

}
