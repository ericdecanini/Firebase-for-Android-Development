package myvaluelist.eddecanini.myvaluelist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    String LOG_TAG = MainActivity.class.getSimpleName();

    private static int RC_SIGN_IN = 1;

    FirebaseFirestore db;
    String uid;
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build()
    );

    ListView listTodo;
    EditText etAddItem;
    Button btnAddItem;

    TodoAdapter todoAdapter;
    ArrayList<String> todoItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        referenceViews();
        initFirebase();
    }

    private void referenceViews() {
        listTodo = findViewById(R.id.list_todo);
        etAddItem = findViewById(R.id.et_add_item);
        btnAddItem = findViewById(R.id.btn_add_item);

        initList();

        // This button inserts the list item
        btnAddItem.setOnClickListener(v -> insertItem(etAddItem.getText().toString()));
    }

    private void insertItem(String newItem) {
        etAddItem.setText("");
        todoItems.add(newItem);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("todoList", todoItems);
        db.collection("users").document(uid).set(userMap);
    }

    private void initList() {
        todoAdapter = new TodoAdapter(this, R.layout.list_item_todo, todoItems);
        listTodo.setAdapter(todoAdapter);
    }

    private void addListListener() {
        // Downloads and updates to-do list
        db.collection("users").document(uid).addSnapshotListener((documentSnapshot, e) -> {
            ArrayList<String> newTodoItems = (ArrayList<String>) documentSnapshot.get("todoList");
            todoItems.clear();
            todoItems.addAll(newTodoItems);
            todoAdapter.notifyDataSetChanged();
        });
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        uid = FirebaseAuth.getInstance().getUid();
        Log.v(LOG_TAG, "Uid: " + uid);

        addListListener();

        // Open Authentication Screen if needed
        if (uid == null)
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(), RC_SIGN_IN
            );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Respond to Authentication Callback
        if (requestCode == RC_SIGN_IN && resultCode == RESULT_OK) {
            uid = FirebaseAuth.getInstance().getUid();
            Log.v(LOG_TAG, "New uid: " + uid);
        }
    }
}
