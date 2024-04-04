package com.example.przepysznik;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<UserComment> commentList;

    public CommentAdapter(List<UserComment> commentList) {
        this.commentList = commentList;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        UserComment comment = commentList.get(position);
        holder.commentTextView.setText(comment.getComment());
        holder.commentTimeTextView.setText(comment.getTime());
        holder.commentNickTextView.setText(comment.getNick());
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView commentTextView;
        TextView commentTimeTextView;
        TextView commentNickTextView;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentTextView = itemView.findViewById(R.id.commentTextView);
            commentTimeTextView = itemView.findViewById(R.id.commentTimeTextView);
            commentNickTextView = itemView.findViewById(R.id.commentNickTextView);
        }
    }
}
