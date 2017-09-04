package pro.clicknet.bindermark;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import pro.clicknet.bindermark.backend.BMBackend;
import pro.clicknet.bindermarkcommon.BMResponse;

public class BinderMark extends Activity {

    public static final int MINIMUM_SIZE = 1;
    public static final int MAXIMUM_SIZE  = 512;
    public static final int DEFAULT_SIZE = MINIMUM_SIZE;

    public static final boolean DEFAULT_NATIVE_METHOD = false;

    private int mSize;
    private EditText mSizeText;

    private boolean mNativeMethod;
    private Switch mNativeMethodSwitch;

    private TextView mResultText;

    private Button mPerformButton;

    private BMBackend mBackend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bindermark);

        mSize = DEFAULT_SIZE;
        mSizeText = (EditText) findViewById(R.id.text_size);
        mSizeText.setText(String.valueOf(mSize));

        mNativeMethod = DEFAULT_NATIVE_METHOD;
        mNativeMethodSwitch = (Switch) findViewById(R.id.switch_native_method);
        mNativeMethodSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mNativeMethod = isChecked;
            }

        });
        mNativeMethodSwitch.setChecked(mNativeMethod);

        mResultText = (TextView) findViewById(R.id.text_result);

        mPerformButton = (Button) findViewById(R.id.button_perform_test);
        mPerformButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    mSize = Integer.parseInt(mSizeText.getText().toString());

                    if (mSize < MINIMUM_SIZE || mSize > MAXIMUM_SIZE) {
                        throw new NumberFormatException("Size is out of allowed bounds");
                    }
                } catch (NumberFormatException exc) {
                    Toast.makeText(BinderMark.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
                }

                perform(mSize, mNativeMethod);
            }

        });

        mBackend = new BMBackend(this);
        mBackend.setOnCompleteListener(new BMBackend.OnCompleteListener() {

            @Override
            public void onComplete(BMResponse response) {
                mResultText.setText(String.valueOf(
                        (response == null) ? "null" : response.getReceiptTime()
                ));
            }

        });
    }

    private void perform(int size, boolean nativeMethod) {
        mBackend.setSize(size);
        mBackend.setNativeMethod(nativeMethod);

        mBackend.perform();
    }

}
