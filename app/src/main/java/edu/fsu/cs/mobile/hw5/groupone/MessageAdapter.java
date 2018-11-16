package edu.fsu.cs.mobile.hw5.groupone;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MessageAdapter extends FirestoreRecyclerAdapter<Message, MessageAdapter.MessageHolder> {

    public MessageAdapter(@NonNull FirestoreRecyclerOptions<Message> options){
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageHolder holder, int position, @NonNull Message model) {
        holder.title.setText(model.getNumber());
        holder.description.setText(model.getMessage());
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_card,
                viewGroup, false);
        return new MessageHolder(v);
    }

    public void deleteItem(int position){
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    class MessageHolder extends RecyclerView.ViewHolder {
        TextView title;//number
        TextView description; //message

        public  MessageHolder(View itemView){
            super(itemView);
            title=itemView.findViewById(R.id.text_title);
            description=itemView.findViewById(R.id.text_description);
        }


    }


}
