package edu.umed.cvalderrama.contactsapplication.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import edu.umed.cvalderrama.contactsapplication.R;
import edu.umed.cvalderrama.contactsapplication.bean.Contact;

/**
 * Custom Adapter for listing contacts
 */
public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private Context context;
    private List<Contact> contacts;
    private int position;

    /**
     * @return The current Item Position
     */
    public int getPosition() {
        return position;
    }

    /**
     * Set the current Item position
     *
     * @param position The position
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Inner class that acts as a ViewHolder to re-use views
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        // View items
        public TextView firstNameView;
        public TextView lastNameView;
        public ImageView photoView;

        /**
         * Create a new instance of the ViewHolder
         *
         * @param itemView The itemLayout
         */
        public ViewHolder(View itemView) {
            super(itemView);

            firstNameView = (TextView) itemView.findViewById(R.id.firstNameViewID);
            lastNameView = (TextView) itemView.findViewById(R.id.lastNameViewID);
            photoView = (ImageView) itemView.findViewById(R.id.contactViewID);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.add(Menu.NONE, R.id.menu_edit_contact, Menu.NONE, R.string.menu_edit);
            contextMenu.add(Menu.NONE, R.id.menu_delete_contact, Menu.NONE, R.string.menu_delete);
        }
    }

    /**
     * Create a new instance of the adapter
     *
     * @param context The Android Context
     * @param contacts The list of Contacts
     */
    public ContactsAdapter(Context context, List<Contact> contacts) {
        this.context = context;
        this.contacts = contacts;
    }

    /**
     * Create the ViewHolder
     *
     * @param parent The parent ViewGroup
     * @param viewType The type of view
     * @return
     */
    @Override
    public ContactsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_row,
                parent, false);
        return new ViewHolder(v);
    }

    /**
     * Called when a list item must be binded on the layout
     *
     * @param holder The ViewHolder with cached Views
     * @param position The position of the view list
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemView.setTag(contacts.get(position));
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                setPosition(holder.getAdapterPosition());
                return false;
            }
        });

        Contact contact = contacts.get(position);

        holder.firstNameView.setText(contact.getFirstName());
        holder.lastNameView.setText(contact.getLastName());

        if (contact.getPhotoUri() != null) {
            Glide.with(context)
                    .load(contact.getPhotoUri())
                    .placeholder(getProgressBarIndeterminate())
                    .into(holder.photoView);
        } else {
            holder.photoView.setImageResource(R.drawable.contact_profile_placeholder);
        }
    }

    /**
     * @return The total number of items
     */
    @Override
    public int getItemCount() {
        return contacts.size();
    }

    /**
     * Get a Loader Drawable for pictures
     *
     * @return A Loader Drawable
     */
    private Drawable getProgressBarIndeterminate() {
        final int[] attrs = {android.R.attr.indeterminateDrawable};
        final int attrs_indeterminateDrawable_index = 0;
        TypedArray a = context.obtainStyledAttributes(android.R.style.Widget_ProgressBar, attrs);

        try {
            return a.getDrawable(attrs_indeterminateDrawable_index);
        } finally {
            a.recycle();
        }
    }
}
