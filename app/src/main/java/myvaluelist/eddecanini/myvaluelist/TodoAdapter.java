package myvaluelist.eddecanini.myvaluelist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoAdapter extends ArrayAdapter<String> {

    Context context;
    int resource;
    List<String> objects;

    public TodoAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
        }

        ((TextView) convertView.findViewById(R.id.tv_item)).setText(this.objects.get(position));
        convertView.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            this.objects.remove(position);
            notifyDataSetChanged();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String uid = FirebaseAuth.getInstance().getUid();

            Map<String, Object> userMap = new HashMap<>();
            userMap.put("todoList", this.objects);
            db.collection("users").document(uid).set(userMap);
        });

        return convertView;
    }
}
