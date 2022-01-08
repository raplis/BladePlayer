package v.blade.player;

import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.core.content.ContextCompat;

import v.blade.BladeApplication;
import v.blade.library.Song;

public class MediaSessionCallback extends MediaSessionCompat.Callback
{
    protected final MediaBrowserService service;

    protected MediaSessionCallback(MediaBrowserService service)
    {
        this.service = service;
    }

    protected void updatePlaybackState(boolean isPlaying)
    {
        PlaybackStateCompat.Builder stateBuilder = new PlaybackStateCompat.Builder().setActions(PlaybackStateCompat.ACTION_PREPARE
                | (isPlaying ? PlaybackStateCompat.ACTION_PAUSE : PlaybackStateCompat.ACTION_PLAY)
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
                | PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID | PlaybackStateCompat.ACTION_SEEK_TO
                | PlaybackStateCompat.ACTION_SET_REPEAT_MODE | PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE);
        stateBuilder.setState(isPlaying ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED,
                service.current == null ? 0 : service.current.getCurrentPosition(), 1);
        service.mediaSession.setPlaybackState(stateBuilder.build());
    }

    @Override
    public void onPlay()
    {
        super.onPlay();

        /* Either we were paused and we resume play, or we play from playlist and we have to
         *   call playSong()
         */
        if(service.current != null && service.current.isPaused())
        {
            service.notification.update(true);
            updatePlaybackState(true);
            service.current.play();
        }
        else
        {
            if(service.current != null) service.current.pause();

            if(service.playlist == null || service.playlist.size() <= service.index) return;
            Song song = service.playlist.get(service.index);
            if(song == null) return;

            //Start service if not started (i.e. this is the first time the user clicks)
            service.startIfNotStarted();
            service.notification.update(true);
            updatePlaybackState(true);

            service.current = song.getBestSource().source.getPlayer();
            BladeApplication.obtainExecutorService().execute(() ->
            {
                service.current.playSong(song);
                ContextCompat.getMainExecutor(service).execute(() ->
                        service.notification.update(true));
            });

        }
    }

    @Override
    public void onPause()
    {
        super.onPause();

        service.notification.update(false);
        updatePlaybackState(false);
        service.current.pause();
    }

    @Override
    public void onSeekTo(long pos)
    {
        super.onSeekTo(pos);

        if(service.current != null)
        {
            service.current.seekTo(pos);
            updatePlaybackState(!service.current.isPaused());
        }
    }

    @Override
    public void onSkipToNext()
    {
        super.onSkipToNext();

        service.notifyPlaybackEnd();
    }

    @Override
    public void onSkipToPrevious()
    {
        super.onSkipToPrevious();

        if(service.index == 0) service.index = service.playlist.size() - 1;
        else service.setIndex(service.index - 1);
        onPlay();
    }


}
