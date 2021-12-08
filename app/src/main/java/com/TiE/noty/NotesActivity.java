package com.TiE.noty;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.TiE.noty.databinding.ActivityNotesBinding;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotesActivity extends AppCompatActivity {
ActivityNotesBinding binding;

    FirebaseAuth auth;

    StaggeredGridLayoutManager staggeredGridLayoutManager;
    FirebaseUser currentUser;
    FirebaseFirestore firebaseFirestore;
    FirestoreRecyclerAdapter<firebasemodel,NoteViewHolder> noteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNotesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth=FirebaseAuth.getInstance();

        currentUser=auth.getCurrentUser();
        firebaseFirestore=FirebaseFirestore.getInstance();

        getSupportActionBar().setTitle("All List");


        binding.createnotefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NotesActivity.this,CreateNoteActivity.class));

            }
        });


        Query query=firebaseFirestore.collection("notes").document(auth.getUid()).collection("myNotes")
                .orderBy("title",Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<firebasemodel> allusernotes=new FirestoreRecyclerOptions.Builder<firebasemodel>().setQuery(query, firebasemodel.class)
                .build();

        noteAdapter=new FirestoreRecyclerAdapter<firebasemodel, NoteViewHolder>(allusernotes) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder holder, int i, @NonNull firebasemodel model) {

                firebasemodel  firebasemodel= noteAdapter.getItem(i);
                holder.notetitle.setText(firebasemodel.getTitle());
                holder.notecontent.setText(firebasemodel.getContent());
                holder.notedate.setText(firebasemodel.getDate());
                holder.notetime.setText(firebasemodel.getTime());


              String   docId=noteAdapter.getSnapshots().getSnapshot(i).getId();


                int colorcode=getRandomColor();
                holder.mnote.setBackgroundColor(holder.itemView.getResources().getColor(colorcode,null));


                holder.mnote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(v.getContext(),NoteDetailsActivity.class);
                        intent.putExtra("title",firebasemodel.getTitle());
                        intent.putExtra("date",firebasemodel.getDate());
                        intent.putExtra("time",firebasemodel.getTime());
                        intent.putExtra("content",firebasemodel.getContent());
                        intent.putExtra("noteId",docId);
                        v.getContext().startActivity(intent);

                    }
                });



                holder.PopButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu=new PopupMenu(v.getContext(),v);
                        popupMenu.setGravity(Gravity.END);
                        popupMenu.getMenu().add("Update").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                Intent intent=new Intent(v.getContext(),EditNoteActivity.class);
                                intent.putExtra("title",firebasemodel.getTitle());
                                intent.putExtra("content",firebasemodel.getContent());
                                intent.putExtra("date",firebasemodel.getDate());
                                intent.putExtra("time",firebasemodel.getTime());
                                intent.putExtra("noteId",docId);
                                v.getContext().startActivity(intent);

                                return false;
                            }
                        });

                        popupMenu.getMenu().add("Delete").setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
//
                                DocumentReference documentReference=firebaseFirestore.collection("notes").document(auth.getUid())
                                        .collection("myNotes").document(docId);

                                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getApplicationContext(), "Note deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "Failed to Delete", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                return false;
                            }
                        });
                        popupMenu.show();
                    }
                });
            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_layout,parent,false);
                return new NoteViewHolder(view);
            }
        };

        binding.recyclerView.setHasFixedSize(true);
        staggeredGridLayoutManager=new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(staggeredGridLayoutManager);

        binding.recyclerView.setAdapter(noteAdapter);

    }

    public class NoteViewHolder extends RecyclerView.ViewHolder{
        private TextView notetitle;
        private TextView notecontent;
        private TextView notedate;
        private TextView notetime;
        LinearLayout mnote;
        ImageView PopButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            notetitle=itemView.findViewById(R.id.noteTitle);
            notecontent=itemView.findViewById(R.id.notecontent);
            notedate=itemView.findViewById(R.id.notedate);
            notetime=itemView.findViewById(R.id.notetime);
            mnote=itemView.findViewById(R.id.note);
            PopButton=itemView.findViewById(R.id.menuPopButton);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                auth.signOut();
                startActivity(new Intent(NotesActivity.this,LoginActivity.class));
                finish();
                break;
            case R.id.PrivacyPolicy:
                startActivity(new Intent(NotesActivity.this,PrivacyPolicyActivity.class));
                break;
            case R.id.TermsandConditions:
                startActivity(new Intent(NotesActivity.this,TermsandConditionsActivity.class));
                break;
            case R.id.Profile:
                startActivity(new Intent(NotesActivity.this,ProfileActivity.class));
                break;


        }

        return true;

    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter==null){
            noteAdapter.stopListening();
        }
    }

    private int getRandomColor(){
        List<Integer> list=new ArrayList<>();
        list.add(R.color.gray);
        list.add(R.color.green);
        list.add(R.color.lightgreen);
        list.add(R.color.skyblue);
        list.add(R.color.pink);
        list.add(R.color.color1);
        list.add(R.color.color2);
        list.add(R.color.color3);
        list.add(R.color.color4);
        list.add(R.color.color5);
        list.add(R.color.color6);
        list.add(R.color.color7);

        Random random=new Random();
        int number=random.nextInt(list.size());
        return list.get(number);

    }
    int counter=0;
    @Override
    public void onBackPressed() {
        counter++;
        Toast.makeText(getApplicationContext(), "Press Back Again to Exit", Toast.LENGTH_SHORT).show();
        if (counter==2){

            super.onBackPressed();
        }

    }

    }
