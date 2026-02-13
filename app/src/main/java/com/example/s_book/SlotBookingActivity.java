package com.example.s_book;



import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SlotBookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot_booking);

        // Get the data passed from MainActivity
        String vendorName = getIntent().getStringExtra("VENDOR_NAME");
        long vendorId = getIntent().getLongExtra("VENDOR_ID", -1);

        TextView title = findViewById(R.id.slotTitle);
        title.setText("Book Slots for: " + vendorName);

        // Next Step: Fetch slots from http://10.0.2.2:8010/api/slots/vendor/{vendorId}
    }
}