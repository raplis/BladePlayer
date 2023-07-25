package v.blade.sources.local;


import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContentResolverCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import v.blade.BladeApplication;
import v.blade.R;
import v.blade.databinding.SettingsFragmentLocalBinding;
import v.blade.library.Library;
import v.blade.library.Playlist;
import v.blade.library.Song;
import v.blade.sources.Source;
import v.blade.ui.ExploreFragment;

public class Local extends Source
{
    public static final int NAME_RESOURCE = R.string.local;
//    public static final int DESCRIPTION_RESOURCE = R.string.local_desc;
    public static final int IMAGE_RESOURCE = R.drawable.ic_local;

    private static final int LOCAL_IMAGE_LEVEL = 1;

    public Local()
    {
        super();
        this.name = BladeApplication.appContext.getString(NAME_RESOURCE);
        this.player = new LocalPlayer(this);
    }

    @Override
    public void initSource()
    {
        if(!checkPermission()) return;

        super.initSource();
    }

    @Override
    public int getImageResource()
    {
        return IMAGE_RESOURCE;
    }

    @Override
    public void synchronizeLibrary()
    {
        //Obtain library
        ContentResolver contentResolver = BladeApplication.appContext.getContentResolver();
        Cursor musicCursor = ContentResolverCompat.query(contentResolver,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,
                null, null);
        if(musicCursor != null && musicCursor.moveToFirst())
        {
            int titleColumn = musicCursor.getColumnIndex(MediaStore.MediaColumns.TITLE);
            int idColumn = musicCursor.getColumnIndex(MediaStore.MediaColumns._ID);
            int artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
            int albumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM);
            int trackNumberColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TRACK);
            int albumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ID);
            int displayColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int durationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

            do
            {
                String title = musicCursor.getString(titleColumn);
                String display = musicCursor.getString(displayColumn);
                String duration = musicCursor.getString(durationColumn);
                String data = musicCursor.getString(dataColumn);
                Log.i("Local", "Title : " + title + " | Display : " + display + " | Duration : " + duration + " | Data : " + data);
                if(display.endsWith("m4a") || display.endsWith("wav") || display.endsWith("ogg")) continue;
                if(duration == null || duration.equals("0")) continue;
                //MediaStore only allows one artist String
                //We will split this string on ',' for songs with multiple artists
                //NOTE : we could split on ' & ', but
                // for example this allows 'Lomepal & Stwo' but splits 'Bigflo & Oli', so no
                //TODO : maybe find a better solution ?
                String artist = musicCursor.getString(artistColumn);
                String[] artists = artist.split(", ");
                //if(artists.length == 1) artists = artist.split(" & ");
                String album = musicCursor.getString(albumColumn);
                long id = musicCursor.getLong(idColumn);
                int track_number = musicCursor.getInt(trackNumberColumn);

                //Load album art if it exists
                long albumId = musicCursor.getLong(albumIdColumn);
                String pathUri = "content://media/external/audio/albumart/" + albumId;
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId);
                try{
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                    {
                        contentResolver.loadThumbnail(contentUri, new Size(64, 64), null);
                    }
                }
                catch(IOException e)
                {
                    pathUri = null;
                }
                if(display.endsWith("flac")) {
                    pathUri = null;
//                    LOCAL_IMAGE_LEVEL = 3;
                }
                Log.i("Local","Title : " + title + " | Album : " + album + " | Artist : " + artist + " | Track : " + track_number + " | ID : " + id + " | Album ID : " + albumId + " | Path : " + pathUri);
                Library.addSong(data, album, artists, this, id, artists, pathUri, track_number, new String[artists.length], new String[artists.length], pathUri, LOCAL_IMAGE_LEVEL);
            }
            while(musicCursor.moveToNext());

            musicCursor.close();
        }
        //Obtain playlists

    }

    @Override
    public Fragment getSettingsFragment()
    {
        return new SettingsFragment(this);
    }

    @Override
    public JsonObject saveToJSON()
    {
        JsonObject jsonObject = new JsonObject();
        Gson gson = new Gson();

        jsonObject.add("class", gson.toJsonTree(Local.class.getName(), String.class));

        return jsonObject;
    }

    @Override
    public void restoreFromJSON(JsonObject jsonObject)
    {

    }

    @Override
    public void explore(ExploreFragment view)
    {

    }

    @Override
    public void exploreSearch(String query, ExploreFragment view)
    {

    }

    @Override
    public void addSongToPlaylist(Song song, Playlist playlist, Runnable callback, Runnable failureCallback)
    {
        failureCallback.run();
    }

    @Override
    public void createPlaylist(String name, BladeApplication.Callback<Playlist> callback, Runnable failureCallback)
    {
        failureCallback.run();
    }

    @Override
    public void removePlaylist(Playlist playlist, Runnable callback, Runnable failureCallback)
    {
        failureCallback.run();
    }

    @Override
    public void addToLibrary(Song song, Runnable callback, Runnable failureCallback)
    {
        failureCallback.run();
    }

    @Override
    public void removeFromLibrary(Song song, Runnable callback, Runnable failureCallback)
    {
        failureCallback.run();
    }

    @Override
    public void removeFromPlaylist(Song song, Playlist playlist, Runnable callback, Runnable failureCallback)
    {
        failureCallback.run();
    }

    private boolean checkPermission()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            {
                return ContextCompat.checkSelfPermission(BladeApplication.appContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            }
            else
            {
                //Android 11 : Scoped Storage and stuff
                //We only request READ_EXTERNAL_STORAGE, and will only use the MediaStore API i guess
                return ContextCompat.checkSelfPermission(BladeApplication.appContext, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            }
        }
        return true;
    }

    public static class SettingsFragment extends Fragment
    {
        private static final int EXT_PERM_REQUEST_CODE = 0x42;

        private final Local local;

        private SettingsFragment(Local local)
        {
            super(R.layout.settings_fragment_local);
            this.local = local;
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            v.blade.databinding.SettingsFragmentLocalBinding binding = SettingsFragmentLocalBinding.inflate(inflater, container, false);

            binding.settingsLocalGrantPermission.setOnClickListener(view ->
            {
                if(checkAndAskPermission())
                {
                    Toast.makeText(requireContext(), getString(R.string.permission_already_granted), Toast.LENGTH_SHORT).show();
                }
                else if(local.checkPermission())
                {
                    Toast.makeText(requireContext(), getString(R.string.permission_granted), Toast.LENGTH_SHORT).show();
                }
                local.status = SourceStatus.STATUS_READY;
            });

            binding.notificationPolicyAccess.setOnClickListener(view ->
            {
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().getPackageName());
                } else
                {
                    intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                    intent.putExtra("app_package", requireActivity().getPackageName());
                    intent.putExtra("app_uid", requireActivity().getApplicationInfo().uid);
                }
                startActivity(intent);
            });


            return binding.getRoot();
        }

        private boolean checkAndAskPermission()
        {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
                {
                    if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        // Request permission
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXT_PERM_REQUEST_CODE);
                        return false;
                    }
                }
                else
                {
                    //Android 11 : Scoped Storage and stuff
                    //We only request READ_EXTERNAL_STORAGE, and will only use the MediaStore API i guess
                    if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        // Request permission
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, EXT_PERM_REQUEST_CODE);
                        return false;
                    }
                }
            }

            return true;
        }
    }
}
