package ghostl.com.examplemvp.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import ghostl.com.examplemvp.R;
import ghostl.com.examplemvp.base.App;
import ghostl.com.examplemvp.base.ServerAPI;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class MainActivity extends AppCompatActivity {
    public static final String DEFAULT_NAME = "Chuck Norris";

    private ArrayAdapter<ServerAPI.Item> adapter;
    private Subscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter = new ArrayAdapter<>(this, R.layout.item));
        requestItems(DEFAULT_NAME);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }

    public void requestItems(String name){
        unsubscribe();
        subscription = App.getServerAPI()
                .getItems(name.split("\\s+")[0], name.split("\\s+")[1])
                .delay(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<ServerAPI.Response>() {
                    @Override
                    public void call(ServerAPI.Response response) {
                        onItemsNext(response.items);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        onItemsError(throwable);
                    }
                });
    }

    private void onItemsNext(ServerAPI.Item[] items) {
        adapter.clear();
        adapter.addAll(items);
    }


    private void unsubscribe() {
        if(subscription != null){
            subscription.unsubscribe();
            subscription = null;
        }
    }
    public void onItemsError(Throwable throwable) {
        Toast.makeText(this, throwable.getMessage(), Toast.LENGTH_LONG).show();
    }
}
