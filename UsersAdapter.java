package com.example.clone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clone.ChatActivity;
import com.example.clone.Models.Users;
import com.example.clone.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    ArrayList<Users> list;
    Context context;

    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.show_user,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Users users = list.get(position);
        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.avatar).into(holder.image);//if user doesn't upload an image we use the default image

        //setting the user name
        holder.userName.setText(users.getUsername());



        //setting message to be ordered by last message received
        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(FirebaseAuth.getInstance().getUid() + users.getUserId())
                .orderByChild("timestamp")
                .limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChildren())
                        {
                            for (DataSnapshot snapshot1 : snapshot.getChildren())
                            {
                                holder.lastMessage.setText(snapshot1.child("message").getValue().toString());
                            }
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



        //when clicking to a saved user it directs you to their personal chat
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);

                //get user data all this is enabled as we made getters and setters in user model
                intent.putExtra("UserId",users.getUserId());
                intent.putExtra("ProfilePic",users.getProfilePic());
                intent.putExtra("userName", users.getUsername());

                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();//depends on how many users are there
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        //set variable for the show user
        ImageView image;
        TextView userName,
                 lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //initialize variables

            image = itemView.findViewById(R.id.profilePic);
            userName = itemView.findViewById(R.id.userNameList);
            lastMessage = itemView.findViewById(R.id.lastMessage);
        }
    }
}
