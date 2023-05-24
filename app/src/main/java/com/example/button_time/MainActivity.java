package com.example.button_time;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public List<String> getButtonClickTimes() {
        List<String> data = new ArrayList<>();
        File file = new File(getExternalFilesDir(null), "button_click_times.csv");
        try {
            FileReader fileReader = new FileReader(file);
            CSVReader csvReader = new CSVReaderBuilder(fileReader).withSkipLines(1).build();
            String[] row;
            while ((row = csvReader.readNext()) != null) {
                data.add(row[0]);
            }
            csvReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
        return data;
    }

    public void saveButtonClickTimes(View view) {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        // Create CSV file if it doesn't exist
        File file = new File(getExternalFilesDir(null), "button_click_times.csv");
        boolean fileExists = file.exists();
        try {
            FileWriter writer = new FileWriter(file, true);
            CSVWriter csvWriter = new CSVWriter(writer);
            if (!fileExists) {
                csvWriter.writeNext(new String[]{"Timestamp"});
            }
            csvWriter.writeNext(new String[]{timeStamp});
            csvWriter.close();
            Toast.makeText(this, "Button click time saved to CSV file", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void calculateEnergyAndPower(View view) {
        List<String> buttonClickTimes = getButtonClickTimes();
        if (buttonClickTimes.size() < 2) {
            Toast.makeText(this, "Not enough data to calculate energy and power", Toast.LENGTH_SHORT).show();
            return;
        }
        long totalTimeMillis = 0;
        for (int i = 1; i < buttonClickTimes.size(); i++) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date prevTime = format.parse(buttonClickTimes.get(i - 1));
                Date currTime = format.parse(buttonClickTimes.get(i));
                totalTimeMillis += currTime.getTime() - prevTime.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        double totalTimeSeconds = totalTimeMillis / 1000.0;
        double energy = totalTimeSeconds / 3600.0; // Energy in watt-hours
        double power = energy / (totalTimeSeconds / 3600.0); // Power in watts
        String result = String.format("Energy: %.2f Wh, Power: %.2f W", energy, power);
        Toast.makeText(this, result, Toast.LENGTH_LONG).show();

        // Your energy and power calculation code here
    }


}