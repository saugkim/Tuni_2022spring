package org.tuni.roomtest;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.Serializable;

public class UserViewHolder extends RecyclerView.ViewHolder {

    public static final String TAG = "ZZ UserViewHolder: ";
    public static String SELECTED_USER = "org.tuni.roomtest.selected_user";
    public static String SELECTED_USER_ID = "org.tuni.roomtest.selected_user_id";
    private final TextView userTextViewFirst, userTextViewLast;
    User currentUser;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        userTextViewFirst = itemView.findViewById(R.id.textViewFirstname);
        userTextViewLast = itemView.findViewById(R.id.textViewLastname);

        itemView.setOnClickListener(view -> {
            Intent intent = new Intent(itemView.getContext(), UpdateUserActivity.class);
            Log.d(TAG, "onClickEvent current user: id " + currentUser.getId() + ", name " + currentUser.getFirstName());
            intent.putExtra(SELECTED_USER_ID, currentUser.getId());
            intent.putExtra(SELECTED_USER, (Serializable) currentUser);
            itemView.getContext().startActivity(intent);
        });
    }

    public void bind(User user) {
        currentUser = user;

        userTextViewFirst.setText(user.getFirstName());
        userTextViewLast.setText(user.getLastName());
    }

    static UserViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_user, parent, false);
        return new UserViewHolder(view);
    }
}
