package br.com.gerencianet.gnsdksample;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.gerencianet.gnsdk.config.Config;
import br.com.gerencianet.gnsdk.interfaces.IGnListener;
import br.com.gerencianet.gnsdk.lib.Endpoints;
import br.com.gerencianet.gnsdk.models.*;
import br.com.gerencianet.gnsdk.models.Error;


public class MainActivity extends ActionBarActivity implements IGnListener {

    private Config config;
    private CreditCard creditCard;
    private Endpoints gnClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        config = new Config();
        config.setClientId("");
        config.setClientSecret("");

        gnClient = new Endpoints(config, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void pay(View view) {
        EditText cardNumber = (EditText) findViewById(R.id.card_number);
        EditText cardCvv = (EditText) findViewById(R.id.card_cvv);
        EditText cardMonth = (EditText) findViewById(R.id.card_month);
        EditText cardYear = (EditText) findViewById(R.id.card_year);

        String number = cardNumber.getText().toString();
        String cvv = cardCvv.getText().toString();
        String month = cardMonth.getText().toString();
        String year = cardYear.getText().toString();

        if(number.matches("") || cvv.matches("")
           || month.matches("") || year.matches("")) {
            Toast.makeText(this, "You did not enter all the form fields", Toast.LENGTH_SHORT).show();
            return;
        }

        creditCard = new CreditCard();
        creditCard.setCvv(cvv);
        creditCard.setNumber(number);
        creditCard.setExpirationMonth(month);
        creditCard.setExpirationYear(year);

        if(((RadioButton)findViewById(R.id.radio_visa)).isChecked()) {
            creditCard.setBrand("visa");
        } else {
            creditCard.setBrand("mastercard");
        }

        gnClient.getPaymentToken(creditCard);
    }

    @Override
    public void onPaymentDataFetched(PaymentData paymentData) {
        ArrayList<Installment> installments = (ArrayList<Installment>) paymentData.getInstallments();
        for(Installment installment : installments) {
            Log.e("Response: ", installment.getValue().toString());
        }
    }

    @Override
    public void onPaymentTokenFetched(PaymentToken paymentToken) {
        View view = findViewById(R.id.response_layout);
        view.setVisibility(View.VISIBLE);

        TextView textView = (TextView) findViewById(R.id.response_text);
        textView.setText("Payment token: " + paymentToken.getHash());
    }

    @Override
    public void onError(Error error) {
        View view = findViewById(R.id.response_layout);
        view.setVisibility(View.VISIBLE);

        String errorMsg = "Code: " + error.getCode() +
                " Message: " + error.getMessage();

        TextView textView = (TextView) findViewById(R.id.response_text);
        textView.setText(errorMsg);
    }
}
