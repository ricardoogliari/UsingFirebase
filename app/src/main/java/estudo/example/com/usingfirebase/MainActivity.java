package estudo.example.com.usingfirebase;

import android.content.DialogInterface;
import android.os.PersistableBundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<Task> tasks = new ArrayList<Task>();

    private ListView listView;
    private int key;

    private DatabaseReference myRef;
    private FirebaseDatabase refCurrent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                showOptions(position);
                return true;
            }
        });

        refCurrent = FirebaseDatabase.getInstance();
        myRef = refCurrent.getReference("tarefas");
        Query query = myRef.orderByChild("termino");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tasks.clear();
                key = 0;
                for (DataSnapshot snap : dataSnapshot.getChildren()){
                    if (Integer.parseInt(snap.getKey()) > key){
                        key = Integer.parseInt(snap.getKey());
                    }
                    Task task = snap.getValue(Task.class);
                    tasks.add(task);
                }
                listView.setAdapter(new ArrayAdapter<Task>(MainActivity.this, android.R.layout.simple_list_item_1, tasks));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showOptions(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("O que deseja fazer com a tarefa " + tasks.get(position).nome);
        builder.setPositiveButton("Editar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tasks.get(position).termino = System.currentTimeMillis();

                DatabaseReference refItem = refCurrent.getReference("tarefas/" + (position + 1));
                refItem.setValue(tasks.get(position));
            }
        });
        builder.setNegativeButton("Remover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DatabaseReference refItem = refCurrent.getReference("tarefas/" + (position + 1));
                refItem.removeValue();
            }
        });
        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Task task = new Task();
        task.nome = "Outra tarefa";
        task.iniciada = false;
        task.local = "Home";
        task.termino = 22334455;
        task.descricao = "Somente outra tarefa";
        myRef.child("" + ++key).setValue(task);
        return true;
    }
}
