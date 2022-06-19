package industries.zk.personalvoiceassistant;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AboutActivity extends AppCompatActivity {

    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTitle("COMMANDS");
        listView = (ListView) findViewById(R.id.list_view);
        String[] abc = {"Open <AppName you want to open>","About You","What is Your Name","What the time now","What the date today",
        "search <query which you want to search>",
        "locate <name of location>",
        "turn on wifi",
        "turn off wifi",
        "turn on bluetooth",
        "turn off bluetooth"};
        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,abc);
        listView.setAdapter(adapter);
    }
}
