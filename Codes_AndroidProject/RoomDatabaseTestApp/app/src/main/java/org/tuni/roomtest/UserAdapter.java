package org.tuni.roomtest;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class UserAdapter extends ListAdapter<User, UserViewHolder> {

    public UserAdapter(@NonNull DiffUtil.ItemCallback<User> diffCallback) {
        super(diffCallback);
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return UserViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        User currentUser = (User) getItem(position);
        holder.bind(currentUser);
    }

    static class UserDiff extends DiffUtil.ItemCallback<User> {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem == newItem;
        }
        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.getFirstName().equals(newItem.getFirstName()) && oldItem.getLastName().equals(newItem.getLastName());
        }
    }
}
