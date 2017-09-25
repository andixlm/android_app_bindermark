package pro.clicknet.bindermark;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import pro.clicknet.bindermark.backend.BMBackend;
import pro.clicknet.bindermark.backend.BMResult;

public class BinderMark extends AppCompatActivity {

    public static final int MINIMUM_SIZE = 1;
    public static final int MAXIMUM_SIZE = 512;
    public static final int DEFAULT_SIZE = MINIMUM_SIZE;

    public static final int MINIMUM_TRANSACTIONS_AMOUNT = 1;
    public static final int MAXIMUM_TRANSACTIONS_AMOUNT = 1000000;
    public static final int DEFAULT_TRANSACTIONS_AMOUNT = 1000;

    public static final boolean DEFAULT_NATIVE_METHOD = false;

    private int mSize;
    private EditText mSizeText;

    private int mTransactionsAmount;
    private int mFaultsAmount;
    private EditText mTransactionsAmountText;

    private boolean mNativeMethod;
    private Switch mNativeMethodSwitch;

    private long mResult;
    private long mDeviation;
    private TextView mResultText;

    private Button mCreateBackendButton;
    private Button mPerformButton;
    private Button mDestroyBackendButton;

    private BMBackend mBackend;
    private boolean mServicesBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bindermark);

        mSize = DEFAULT_SIZE;
        mSizeText = (EditText) findViewById(R.id.text_size);
        mSizeText.setText(String.valueOf(mSize));
        mSizeText.addTextChangedListener(mTextChangedListener);

        mTransactionsAmount = DEFAULT_TRANSACTIONS_AMOUNT;
        mFaultsAmount = 0;
        mTransactionsAmountText = (EditText) findViewById(R.id.text_transactions_amount);
        mTransactionsAmountText.setText(String.valueOf(mTransactionsAmount));
        mTransactionsAmountText.addTextChangedListener(mTextChangedListener);

        mNativeMethod = DEFAULT_NATIVE_METHOD;
        mNativeMethodSwitch = (Switch) findViewById(R.id.switch_native_method);
        mNativeMethodSwitch.setOnCheckedChangeListener(mNativeSwitchOnCheckedChangeListener);
        mNativeMethodSwitch.setChecked(mNativeMethod);

        mResultText = (TextView) findViewById(R.id.text_result);

        mCreateBackendButton = (Button) findViewById(R.id.button_create_backend);
        mCreateBackendButton.setEnabled(true);
        mCreateBackendButton.setOnClickListener(mCreateButtonOnClickListener);

        mPerformButton = (Button) findViewById(R.id.button_perform_test);
        mPerformButton.setEnabled(false);
        mPerformButton.setOnClickListener(mPerformButtonOnClickListener);

        mDestroyBackendButton = (Button) findViewById(R.id.button_destroy_backend);
        mDestroyBackendButton.setEnabled(false);
        mDestroyBackendButton.setOnClickListener(mDestroyButtonOnClickListener);

        mBackend = new BMBackend(this);
        mBackend.setOnCreateListener(mBackendOnCreateListener);
        mBackend.setOnCompleteListener(mBackendOnCompleteListener);

        mServicesBound = false;
    }

    private void onServicesBoundChange(boolean servicesBound) {
        mServicesBound = servicesBound;

        mCreateBackendButton.setEnabled(!mServicesBound);
        mPerformButton.setEnabled(mServicesBound);
        mDestroyBackendButton.setEnabled(mServicesBound);

        mResultText.setText(getString(R.string.text_result_default_value));
    }

    /* Listeners */

    private TextWatcher mTextChangedListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            mBackend.destroy();
            onServicesBoundChange(false);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }

    };

    private CompoundButton.OnCheckedChangeListener mNativeSwitchOnCheckedChangeListener =
            new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mNativeMethod = isChecked;

                    mBackend.destroy();
                    onServicesBoundChange(false);
                }

            };

    private BMBackend.OnCreateListener mBackendOnCreateListener =
            new BMBackend.OnCreateListener() {

                @Override
                public void onCreate() {
                    mPerformButton.setEnabled(true);
                }

            };

    private BMBackend.OnCompleteListener mBackendOnCompleteListener =
            new BMBackend.OnCompleteListener() {

                @Override
                public void onComplete(BMResult result) {
                    mResult = result.getResult();
                    mDeviation = result.getDeviation();
                    mFaultsAmount = result.getFaultsAmount();
                }

            };

    private View.OnClickListener mCreateButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            try {
                mSize = Integer.parseInt(mSizeText.getText().toString());

                if (mSize < MINIMUM_SIZE || mSize > MAXIMUM_SIZE) {
                    throw new NumberFormatException("Size is out of allowed bounds");
                }

                mTransactionsAmount = Integer.parseInt(mTransactionsAmountText.getText().toString());

                if (mTransactionsAmount < MINIMUM_TRANSACTIONS_AMOUNT ||
                        mTransactionsAmount > MAXIMUM_TRANSACTIONS_AMOUNT) {
                    throw new NumberFormatException("Transactions amount is out of allowed bounds");
                }
            } catch (NumberFormatException exc) {
                Toast.makeText(BinderMark.this, exc.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            mBackend.setSize(mSize);
            mBackend.setTransactionsAmount(mTransactionsAmount);
            mBackend.setNativeMethod(mNativeMethod);

            mBackend.create();
            onServicesBoundChange(true);
        }

    };

    private View.OnClickListener mPerformButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {

            new AsyncTask<Void, Integer, Void>() {

                @Override
                protected void onPreExecute() {
                    mPerformButton.setEnabled(false);
                    mDestroyBackendButton.setEnabled(false);
                    mResultText.setText(R.string.text_result_default_value);
                }

                @Override
                protected Void doInBackground(Void... params) {
                    mBackend.perform();

                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    mPerformButton.setEnabled(true);
                    mDestroyBackendButton.setEnabled(true);

                    mResultText.setText(
                            String.format(Locale.getDefault(), "Results:\n\t" +
                                            "Size: %d\n\t" +
                                            "Transactions amount: %d\n\t" +
                                            "Faults amount: %d\n\t" +
                                            "Native method: %s\n\t" +
                                            "Average (ns): %d\n\t" +
                                            "Deviation (ns): %d\n\t",
                                    mSize, mTransactionsAmount, mFaultsAmount,
                                    String.valueOf(mNativeMethod), mResult, mDeviation
                            )
                    );
                }

            }.execute();
        }

    };

    private View.OnClickListener mDestroyButtonOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            mBackend.destroy();
            onServicesBoundChange(false);
        }

    };

}
