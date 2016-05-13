package org.projects.shoppinglist;

import com.firebase.client.Firebase;

/**
 * Created by KennethBovbjerg on 12-05-2016.
 */
public class ShoppingList extends android.app.Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);
    }
}
