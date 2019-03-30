package net.asaken1021.musicplayer;

import android.content.Context;
import android.net.Uri;
import android.support.v4.media.session.MediaControllerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;
import io.realm.RealmModel;

public class MusicListArrayAdapter extends ArrayAdapter<MusicListViewItem> {
    private List<MusicListViewItem> items;
    private MusicListItemListener listener;

    public MusicListArrayAdapter(Context context, int layoutResourceId, List<MusicListViewItem> objects) {
        super(context, layoutResourceId, objects);

        items = objects;

        listener = (MusicListItemListener) context;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public MusicListViewItem getItem(int position) {
        return items.get(position);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final ViewHolder viewHolder;

        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.music_list_view_item, null);
            viewHolder = new ViewHolder(view);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        final MusicListViewItem item = getItem(position);

        viewHolder.musicCoverImageView.setImageBitmap(item.musicCoverBitmapImage);
        viewHolder.musicTitleTextView.setText(item.musicTitleText);
        viewHolder.musicArtistTextView.setText(item.musicArtistText);
        viewHolder.musicLengthTextView.setText(item.musicLengthText);
        viewHolder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.playListViewItem(position);
            }
        });

        return view;
    }

    public static class ViewHolder {
        ImageView musicCoverImageView;
        TextView musicTitleTextView;
        TextView musicArtistTextView;
        TextView musicLengthTextView;
        LinearLayout itemLayout;

        public ViewHolder(View v) {
            musicCoverImageView = (ImageView) v.findViewById(R.id.item_musicCoverImageView);
            musicTitleTextView = (TextView) v.findViewById(R.id.item_musicTitleTextView);
            musicArtistTextView = (TextView) v.findViewById(R.id.item_musicArtistTextView);
            musicLengthTextView = (TextView) v.findViewById(R.id.item_musicLengthTextView);
            itemLayout = (LinearLayout) v.findViewById(R.id.musicItemLinearLayout);
        }
    }
}