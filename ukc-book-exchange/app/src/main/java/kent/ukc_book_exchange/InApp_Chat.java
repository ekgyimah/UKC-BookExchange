package kent.ukc_book_exchange;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.Configuration;
import co.chatsdk.core.session.NM;
import co.chatsdk.core.types.ConnectionType;
import co.chatsdk.firebase.FirebaseModule;
import co.chatsdk.firebase.FirebasePaths;
import co.chatsdk.firebase.file_storage.FirebaseFileStorageModule;
import co.chatsdk.firebase.wrappers.UserWrapper;
import co.chatsdk.ui.manager.InterfaceManager;
import co.chatsdk.ui.manager.UserInterfaceModule;

/**
 * Created by P-Yawlui on 13/02/2018.
 */

public class InApp_Chat extends AppCompatActivity{

    String userID;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_chat);

        databaseReference = FirebaseDatabase.getInstance().getReference("User/");

        final Context context = getApplicationContext();

// Create a new configuration
        Configuration.Builder builder = new Configuration.Builder(context);


// Initialize the Chat SDK
        ChatSDK.initialize(builder.build());
        UserInterfaceModule.activate(context);

// Activate the Firebase module
        FirebaseModule.activate();

// File storage is needed for profile image upload and image messages
        FirebaseFileStorageModule.activate();

        InterfaceManager.shared().a.startLoginActivity(context, true);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        UserWrapper wrapper = UserWrapper.initWithEntityId(ds.getKey());
                        wrapper.metaOn();
                        wrapper.onlineOn();
                        User user = wrapper.getModel();
                        NM.contact().addContact(user, ConnectionType.Contact);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }


    }
